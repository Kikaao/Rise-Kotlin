package com.rise.fitrpg

// ============================================================
// AppConstants.kt
// Layer: App-level (shared across ViewModels)
// ============================================================
// Constants that don't belong to any specific layer but are
// needed across the app. Kept separate from GameConstants
// (which is game design values) — this is app infrastructure.
// ============================================================

object AppConstants {

    // Single-user app — every install gets its own Room database,
    // so userId = 1 is unique per device. No collision between installs.
    // Phase 3: replaced by real user ID from backend auth (JWT).
    const val CURRENT_USER_ID = 1
}
