package models

// ============================================================
// PlayerProfile.kt
// Layer: Models
// ============================================================
// Stores the physical profile of the player.
// Used by MuscleSystem to calculate muscle ranks relative to
// the player's own body — a 50kg woman benching 100kg is judged
// differently from a 95kg man benching the same weight.
//
// Kept separate from User — User is game state, PlayerProfile
// is physical reality. Different concerns, different lifecycle.
// ============================================================

// ─────────────────────────────────────────────────────
// GENDER
// Used to apply a coefficient that normalizes muscle scores
// across biological differences in strength potential.
// ─────────────────────────────────────────────────────

enum class Gender {
    MALE,
    FEMALE,
    OTHER   // Uses the average of MALE and FEMALE coefficients — TUNABLE
}

// ─────────────────────────────────────────────────────
// AGE BRACKET
// Coarse bucketing — we ask for exact age but convert to bracket
// for the coefficient lookup. Avoids over-engineering the formula
// while still rewarding older athletes appropriately.
// ─────────────────────────────────────────────────────

enum class AgeBracket(val label: String) {
    TEENS("13–19"),
    TWENTIES("20–29"),
    THIRTIES("30–39"),
    FORTIES("40–49"),
    FIFTIES_PLUS("50+")
}

// ─────────────────────────────────────────────────────
// PLAYER PROFILE
// Collected at sign-up. Never changes unless the player
// explicitly updates it in settings.
//
// heightCm and weightKg are stored as Double for precision.
// weightKg is the key field — it is the denominator in every
// muscle rank formula (score = liftedKg / weightKg × coefficients).
// ─────────────────────────────────────────────────────

data class PlayerProfile(
    val userId: Int,
    val gender: Gender,
    var ageYears: Int,                  // exact age — converted to AgeBracket internally
    var heightCm: Double,
    var weightKg: Double                // bodyweight — used as denominator in rank formula
) {
    // Derived — age bracket for coefficient lookup
    val ageBracket: AgeBracket
        get() = when {
            ageYears < 20 -> AgeBracket.TEENS
            ageYears < 30 -> AgeBracket.TWENTIES
            ageYears < 40 -> AgeBracket.THIRTIES
            ageYears < 50 -> AgeBracket.FORTIES
            else          -> AgeBracket.FIFTIES_PLUS
        }
}

// ─────────────────────────────────────────────────────
// PROFILE COEFFICIENTS
// Normalizes scores across gender and age so that rank reflects
// effort relative to the player's own physical context.
//
// Gender coefficient: women have a lower absolute strength ceiling,
// so a lower denominator makes their score higher for the same lift.
// MALE = 1.0 (baseline), FEMALE = 0.75 (TUNABLE), OTHER = 0.875 (average).
//
// Age coefficient: peak strength is in the 20s–30s. Older athletes
// get a slight bonus, teens get a slight penalty (still developing).
// All values TUNABLE — adjust during playtesting.
// ─────────────────────────────────────────────────────

object ProfileCoefficients {

    // Gender coefficients — TUNABLE
    // Lower value = same lift scores higher (rewards the gender with lower strength ceiling)
    fun genderCoefficient(gender: Gender): Double = when (gender) {
        Gender.MALE   -> 1.00    // TUNABLE — baseline
        Gender.FEMALE -> 0.75    // TUNABLE — normalized for biological strength difference
        Gender.OTHER  -> 0.875   // TUNABLE — average of MALE and FEMALE
    }

    // Gender coefficient: lower value = same lift scores higher
    // MALE = 1.0 baseline, FEMALE = 0.75
    //
    // Age coefficient: same logic, lower = rewarded more
    // Peak strength is 20s–30s so those sit at 1.0 baseline

    fun ageCoefficient(ageBracket: AgeBracket): Double = when (ageBracket) {
        AgeBracket.TEENS        -> 1.05   // TUNABLE mild penalty (still developing)
        AgeBracket.TWENTIES     -> 1.00   // TUNABLE peak baseline
        AgeBracket.THIRTIES     -> 1.00   // TUNABLE still peak
        AgeBracket.FORTIES      -> 0.95   // TUNABLE bonus for age
        AgeBracket.FIFTIES_PLUS -> 0.90   // TUNABLE bigger bonus for age
    }

// Used by MuscleSystem
    fun combined(profile: PlayerProfile): Double =
        genderCoefficient(profile.gender) * ageCoefficient(profile.ageBracket)
}
