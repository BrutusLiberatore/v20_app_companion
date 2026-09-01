# V20 Character Manager

A companion application for Vampire: The Masquerade 20th Anniversary Edition, designed for both players and storytellers. Built as a native Android application with offline-first architecture, it manages character sheets, chronicle data, and provides a dedicated storyteller workspace with live session tools.

---

## Architecture

The application follows a layered architecture with clear separation of concerns:

```
UI Layer (Jetpack Compose)
    |
Domain Layer (Models, Repository Interfaces, Engine)
    |
Data Layer (Room DAOs, Entity Mappers, File Managers)
```

- **Domain layer** contains pure Kotlin models with no Android dependencies, making them testable and portable
- **Data layer** uses Room for persistence with manual dependency injection via `AppContainer`
- **UI layer** is built entirely with Jetpack Compose, no XML layouts

All user edits persist immediately to the Room database. There is no "save" button for character data -- every change writes through to storage on first modification.

---

## Features

### Character Management

Full V20 character sheet support across seven tabs: Identity, Attributes, Abilities, Advantages, Morality, Resources/Equipment, and Notes. Includes:

- 5-step character creation wizard (Identity, Attributes, Abilities, Advantages, Final Touches)
- Attribute presets (7/5/3 distribution: Combat, Stealth, Social)
- Ability presets (13/9/5 distribution)
- Sect-aware creation paths (Camarilla, Anarch, Independent, Sabbat, Caitiff/Pander)
- Character randomizer (random name, attribute allocation, ability allocation)
- Portrait system with gallery/camera import and internal storage
- Character duplication
- Import/Export in `.v20` JSON format with duplicate detection

### Chronicle System

A dedicated chronicle management system separate from individual character files:

- Chronicle CRUD with name, setting, and theme
- Character membership with role assignment (Player Character, Storyteller, Retired)
- Session management with numbered sessions, status tracking (Planned, Active, Completed), and lifecycle events
- Plot arcs, secrets, clues, events, factions, locations
- Relationships and boons tracking

### Storyteller Workspace

A mobile-first storyteller interface organized around a five-tab bottom navigation:

**Live** -- Active session dashboard with real-time controls:
- Session status display with start/end controls
- Active scene card with participant tracking
- Character quick cards with inline Blood Pool and Willpower +/- controls
- Quick action bar for dice, notes, and event logging
- Scene deck bottom sheet for scene switching

**People** -- Character and NPC management:
- Player character list with quick-view cards
- NPC creation with optional PG linking (promote NPC to full character sheet)
- NPC detail sheet with editable role, description, and narrator notes

**Plots** -- Narrative management:
- Plot arc cards
- Full note CRUD with `@` autocomplete cross-referencing (see LinkedTextEditor below)
- Scene list with hooks

**Visual** -- Media library (navigates to full Media Library screen):
- Image import with automatic thumbnail generation
- PDF/document import with built-in viewer
- Category filtering (Maps, NPC, Locations, Clues, Documents)

**More** -- Utility section:
- Dice roller
- Locations with image attachment
- Factions, Sessions (full CRUD), Secrets, Clues

### LinkedTextEditor

A custom composable that adds `@` autocomplete to any note field. Typing `@` followed by a category keyword triggers a popup menu showing matching chronicle entities:

| Italiano | English | Category |
|----------|---------|----------|
| `@PG` | `@PC` | Player Characters |
| `@NPC` | `@NPC` | NPCs |
| `@LUOGHI` | `@LOC` | Locations |
| `@MAPPE` | `@MAP` | Maps |
| `@SEGRETI` | `@SECRET` | Secrets |
| `@INDIZI` | `@CLUE` | Clues |
| `@NOTE` | `@NOTE` | Notes |
| `@SESSIONI` | `@SESSION` | Sessions |
| `@FAZIONI` | `@FACTION` | Factions |
| `@EVENTI` | `@EVENT` | Events |
| `@SCENE` | `@SCENE` | Scenes |
| `@OGGETTI` | `@ITEM` | Items |

Selecting an item inserts a `[TYPE:ID:Name]` reference that renders as a clickable chip in read mode. The system supports both Italian and English keywords.

### Visual Board & Annotation System

A layered image annotation system for maps, location plans, and visual references:

- **Canvas tools**: Pen, highlighter, line, arrow, circle, rectangle, text, pin, eraser
- **Layer system**: Create, rename, delete, toggle visibility, reorder layers
- **Pin types**: Location, NPC, Event, Secret, Clue -- each linkable to chronicle entities
- **Revision history**: Snapshot-based versioning with restore capability
- **Presentation mode**: Fullscreen display with GM tools hidden, public layers only
- **Image management**: Rename, category filtering, internal storage with 1920px max resolution

### Dice Engine

V20 tabletop dice roller supporting:
- Standard rolls with custom dice count and difficulty
- Botch detection
- Willpower and blood expenditure dice
- Specialty and automatic success rules

### Equipment Library

Import/export system for equipment libraries in a structured JSON format, mergeable into character equipment lists.

### Bilingual Support

Full Italian and English localization (~600+ strings). Language can be switched at runtime from Settings.

---

## Technical Details

| Component | Technology |
|-----------|-----------|
| Language | Kotlin 2.0.0 |
| UI | Jetpack Compose with Material 3 |
| Database | Room (currently v8, 7 migrations) |
| DI | Manual via `AppContainer` |
| Images | Coil for async loading, `BitmapFactory` for import |
| PDF | Android `PdfRenderer` (built-in, no external dependencies) |
| Serialization | `kotlinx.serialization` |
| Min SDK | 26 (Android 8.0) |
| Target SDK | 34 |
| Build | Gradle 8.5.0, AGP 8.5.0 |

### Database

Room database with 15+ entities covering characters, chronicles, sessions, scenes, NPCs, locations, factions, relationships, plot arcs, secrets, clues, events, media assets, annotations, layers, revisions, quick notes, and session events.

Migrations are versioned and tested. A `fallbackToDestructiveMigration` is configured as a safety net.

---

## Building

Prerequisites:
- JDK 21
- Android SDK with compileSdk 34
- Gradle 8.5+

```bash
cd V20CharacterManager
./gradlew assembleDebug
```

The debug APK will be at `app/build/outputs/apk/debug/app-debug.apk`.

---

## Testing

```bash
./gradlew test
```

Test coverage includes character CRUD, import/export engine, dice engine, database migrations, and repository operations.

---

## Project Structure

```
app/src/main/java/com/v20charactermanager/
  data/
    di/AppContainer.kt              -- Manual dependency injection
    local/
      V20Database.kt                -- Room database + migrations
      ChronicleImageManager.kt      -- Image storage and thumbnails
      dao/                          -- 15+ Room DAOs
      entity/                       -- 15+ Room entities
    repository/                     -- Repository implementations + mappers
  domain/
    engine/                         -- Import/export engines
    model/                          -- Pure Kotlin domain models
    repository/                     -- Repository interfaces
  ui/
    chronicle/                      -- Chronicle + Storyteller screens
    compendium/                     -- V20 rules reference
    creation/                       -- Character creation wizard
    io/                             -- Import/export UI
    navigation/NavGraph.kt          -- Single navigation graph
    settings/                       -- App settings
    sheet/                          -- Character sheet tabs
    components/                     -- Shared UI components
  util/                             -- Helpers (locale, etc.)
```

---

## License

This application is a fan-made companion tool for Vampire: The Masquerade. Vampire: The Masquerade and all related properties are trademarks of Paradox Interactive AB.
