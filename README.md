# Rise — Fitness RPG

> Solo Levelling meets Duolingo. A gamified fitness tracking app where your real workouts level up a real character.

---

## What is Rise?

Rise is an Android app that turns your gym sessions into an RPG progression system. You log a workout, you earn XP, your character levels up. Miss your weekly goal and your streak breaks, just like Duolingo, but for the gym.

The idea came from a simple frustration: fitness apps track your data but give you nothing to work toward. Rise gives you classes to master, quests to complete, achievements to unlock, and a muscle rank system that reflects your actual strength relative to your own body, not a generic leaderboard.

**The six classes:** Champion (strength), Striker (power), Rogue (dexterity), Monk (flexibility), Adventurer (endurance/cardio), Paragon (all stats equally)

**The six stats:** Strength, Dexterity, Endurance, Flexibility, Power, Focus. Every exercise contributes to stats through fixed weights that always sum to 1.0. A bench press might be 0.8 strength and 0.2 power, log a set, earn XP, and that XP splits across your stats by those weights. Focus is the exception, it never grows from exercise, only from keeping your streak alive and completing quests. Stats are global and never reset when you switch class.

**The loop:** log workout, earn XP, level up your class, unlock achievements, complete weekly quests, watch your muscle ranks reflect reality

The full roadmap:

- **Phase 1 (done):** Core game logic in Kotlin, models, systems, test suite. Functionally complete but content is still thin in places. More exercises are coming, especially flexibility which has very few right now. New items beyond the streak freeze, more quest types, and tuning on things like XP scaling and class switch cooldowns will be added over time.
- **Phase 2 (current):** Android app, Jetpack Compose, Room DB, MVVM, AI-generated quests
- **Phase 3:** Spring Boot backend, PostgreSQL, JWT auth, AI coach
- **Phase 4:** Polish and Google Play launch

---

## Technical Overview

### Architecture

The project enforces a strict layered architecture. Each layer only communicates with the layer directly adjacent to it, no shortcuts.

```
Models  ->  Systems  ->  Repository  ->  ViewModel  ->  UI
```

Every file belongs to exactly one layer. Systems never call other systems. If two systems need the same value, it lives in `GameConstants.kt`, one source of truth, zero drift.

This rule was born from a real bug: `StreakSystem` and `QuestSystem` both had independent `when` blocks defining the weekly workout minimums per tier. One got updated, the other didn't. The fix was structural, not a patch.

### Key Design Decisions

**Sealed class results everywhere**
Every system function returns a typed result instead of a boolean or nullable. `StreakSystem.evaluateWeek()` returns `GoalMet`, `FreezeConsumed`, or `StreakBroken`. The caller is forced by the compiler to handle every case.

**Bodyweight XP**
A 100kg person doing pull-ups is moving more load than a 60kg person doing the same reps. `Exercise` has a `usesBodyweight` flag. When true, `XpSystem` pulls the player's weight from `PlayerProfile` and folds it into the difficulty calculation. Small detail, but it reflects the domain honestly.

**Muscle rank is relative, not absolute**
Score = `liftedKg / (bodyweightKg x genderCoefficient x ageCoefficient)`. A 60kg woman benching 60kg scores higher than a 90kg man benching the same weight, because relative to their own body the effort is proportionally greater. Age coefficients reward older athletes appropriately.

**The Phase 2 seam**
`QuestSystem` generates quests via an algorithm in Phase 1. In Phase 2, an AI API replaces `generateClassQuests()` and `generateBalanceQuests()` with zero changes to the `Quest` model or the progress tracking logic. The seam was designed in from the start.

### What AI Did and What I Did

This project used AI assistance. Here is the honest breakdown.

**AI generated, labeled in comments:**
- `ExerciseLibrary.kt` and `CardioExerciseLibrary.kt`, 240+ exercises with stat contributions, equipment types, and XP rates. Designing this by hand would have taken days and added no value to the architecture
- `InventoryModels.kt`, a placeholder model not yet fully designed
- Boilerplate parts of `Main.kt`

**AI assisted, decisions mine:**
- All system logic. The algorithms are AI assisted but every design decision, every bug catch, and every structural fix was deliberate
- The bodyweight XP feature was identified as a missing piece mid-session and designed from scratch
- The `GameConstants` consolidation came from catching a real drift bug during code review

**Designed and written independently:**
- The overall architecture and layer rules
- The game design, classes, stats, streak tiers, quest types, rank system
- Every audit pass that caught issues before they made it into the codebase

I used AI for the parts that are repetitive or time consuming, and kept full control over the parts that actually matter.

### Stack

- **Language:** Kotlin
- **Phase 1 IDE:** IntelliJ IDEA
- **Phase 2 (current):** Android Studio, Jetpack Compose, Room, MVVM
- **Phase 3 (planned):** Spring Boot, PostgreSQL, JWT

### Running the Tests

Open the project in IntelliJ IDEA, mark `src` as Sources Root, and run `Main.kt`. The test suite covers all 8 systems with around 60 labeled assertions printed to console.

---

*Built by Stamatis Kalaitzidis*
