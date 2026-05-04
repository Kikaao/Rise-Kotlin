package com.rise.fitrpg.data.repository

import com.rise.fitrpg.data.database.ClassProgressDao
import com.rise.fitrpg.data.database.InventoryDao
import com.rise.fitrpg.data.database.PlayerProfileDao
import com.rise.fitrpg.data.database.UserDao
import com.rise.fitrpg.data.database.toDomain
import com.rise.fitrpg.data.database.toEntity
import com.rise.fitrpg.data.models.FitnessClass
import com.rise.fitrpg.data.models.ItemType
import com.rise.fitrpg.data.models.PlayerProfile
import com.rise.fitrpg.data.models.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

// ============================================================
// UserRepository
// Layer: Repository
// ============================================================
// Single source of truth for all user-related data.
// ViewModels call this — they never touch DAOs directly.
//
// Assembles the full User domain model from 3 tables:
//   users            → UserEntity       (core game state)
//   class_progress   → ClassProgressEntity (per-class XP/level)
//   inventory        → InventoryEntity  (items held)
//
// PlayerProfile is separate (optional physical data) and
// exposed via its own function.
// ============================================================

class UserRepository(
    private val userDao: UserDao,
    private val classProgressDao: ClassProgressDao,
    private val playerProfileDao: PlayerProfileDao,
    private val inventoryDao: InventoryDao
) {

    // ── READ ──────────────────────────────────────────────

    // Reactive stream of the full User domain model.
    // Combines 3 flows (user, class progress, inventory) into one.
    // The UI observes this — it updates automatically when any table changes.
    fun getUserFlow(userId: Int): Flow<User> {
        return combine(
            userDao.getUserFlow(userId).filterNotNull(),
            classProgressDao.getAllForUserFlow(userId),
            inventoryDao.getAllForUserFlow(userId)
        ) { userEntity, classProgressEntities, inventoryEntities ->

            val classMap = classProgressEntities
                .associate { FitnessClass.valueOf(it.fitnessClass) to it.toDomain() }

            val inventory = inventoryEntities
                .associate { ItemType.valueOf(it.itemType) to it.toDomain() }

            userEntity.toDomain(classMap, inventory)
        }
    }

    // One-shot suspend read — used when you need the user once, not as a stream.
    // e.g. before running a system calculation that only needs current state.
    suspend fun getUser(userId: Int): User? {
        val userEntity = userDao.getUser(userId) ?: return null

        val classMap = classProgressDao.getAllForUser(userId)
            .associate { FitnessClass.valueOf(it.fitnessClass) to it.toDomain() }

        val inventory = inventoryDao.getAllForUser(userId)
            .associate { ItemType.valueOf(it.itemType) to it.toDomain() }

        return userEntity.toDomain(classMap, inventory)
    }

    // ── CREATE ────────────────────────────────────────────

    // Inserts a brand new user with all 6 class progress rows.
    // Called once during onboarding — never called again for the same user.
    suspend fun createUser(user: User) {
        userDao.insertUser(user.toEntity())

        // Insert one ClassProgressEntity row per class — all start at 0 XP / level 0
        val classProgressEntities = user.classMap.values.map { it.toEntity(userId = user.id) }
        classProgressDao.insertAll(classProgressEntities)

        // Insert any starting inventory items (usually empty on account creation)
        user.inventory.values.forEach { item ->
            inventoryDao.insertOrUpdate(item.toEntity(userId = user.id))
        }
    }

    // ── UPDATE ────────────────────────────────────────────

    // Saves the full user state after any game event (workout logged, level up, class switch, etc.).
    // Updates user core, all class progress rows, and inventory in one call.
    suspend fun saveUser(user: User) {
        userDao.updateUser(user.toEntity())

        // Update all 6 class progress rows — always save all of them together
        // to avoid partial state where one class is out of sync.
        val classProgressEntities = classProgressDao.getAllForUser(user.id)
        val entityIdMap = classProgressEntities.associate {
            FitnessClass.valueOf(it.fitnessClass) to it.id
        }

        val updatedProgress = user.classMap.values.map { progress ->
            val existingId = entityIdMap[progress.fitnessClass] ?: 0
            progress.toEntity(userId = user.id, entityId = existingId)
        }
        classProgressDao.updateAll(updatedProgress)

        // Save inventory — upsert each item (insert if new, replace if existing)
        user.inventory.values.forEach { item ->
            inventoryDao.insertOrUpdate(item.toEntity(userId = user.id))
        }
    }

    // ── PLAYER PROFILE ────────────────────────────────────

    // Physical profile is optional — users can skip it during onboarding.
    // MuscleSystem uses it for score calculations; null = use default coefficients.
    fun getProfileFlow(userId: Int): Flow<PlayerProfile?> {
        return playerProfileDao.getProfileFlow(userId).map { it?.toDomain() }
    }

    suspend fun getProfile(userId: Int): PlayerProfile? {
        return playerProfileDao.getProfile(userId)?.toDomain()
    }

    suspend fun saveProfile(profile: PlayerProfile) {
        playerProfileDao.insertOrUpdate(profile.toEntity())
    }
}
