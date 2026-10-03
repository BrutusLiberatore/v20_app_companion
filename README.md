# V20 Character Manager

[![CI](https://github.com/BrutusLiberatore/v20_app_companion/actions/workflows/ci.yml/badge.svg)](https://github.com/BrutusLiberatore/v20_app_companion/actions/workflows/ci.yml)
[![Release](https://img.shields.io/github/v/release/BrutusLiberatore/v20_app_companion)](https://github.com/BrutusLiberatore/v20_app_companion/releases)
[![Android](https://img.shields.io/badge/Android-26%2B-3DDC84)](https://www.android.com)
[![Tests](https://img.shields.io/badge/tests-350-4C8BF5)](https://github.com/BrutusLiberatore/v20_app_companion/actions/workflows/ci.yml)
[![i18n](https://img.shields.io/badge/i18n-EN%20%7C%20IT-A970FF)](#features)

A native Android companion app for *Vampire: The Masquerade 20th Anniversary Edition*, for both players and the Storyteller. It manages characters, chronicles, and live tabletop sessions with an offline-first architecture: no account, no server, no data collection. The interface is fully bilingual (Italian and English).

First launch shows a language picker and a 9-step interactive tutorial (replayable from Settings). Full backups are exported to a single `.v20backup` file and copied automatically to `Download/V20Companion` once a day.

---

## Features

### Getting Started

- First-launch language picker (English/Italian) followed by a 9-step interactive tutorial, replayable anytime from Settings
- Adaptive layouts for phones and tablets (2/3/4-column grids, compact top bar, landscape split view at the live table)

### Characters

- Five-step creation wizard (Identity, Attributes, Abilities, Advantages, Final Touches) with V20 rules validation
- Thirteen clans with starting disciplines and backgrounds
- Sect-aware creation paths (Camarilla, Anarch, Independent, Sabbat, Caitiff/Pander)
- Attribute presets (7/5/3) and ability presets (13/9/5), plus a full randomizer
- Character sheet in eight reorderable sections: Overview, Attributes, Abilities, Advantages, Details, Merits & Flaws, Equipment, Notes
- Portrait import from gallery or camera, character duplication
- Import/export in `.v20` JSON format with duplicate detection

### Chronicle

- Chronicles with characters, roles, sessions, XP, factions, locations, relationships and boons
- Plot arcs, secrets, clues, events, and session lifecycle tracking
- Scenes with variants (default variant selectable per scene)
- Media library: images, PDF documents, video, with categories and tags
- Notes with `@` autocomplete cross-references to any chronicle entity (PCs, NPCs, locations, secrets, sessions, and so on)

### Storyteller Workspace

Six-tab mobile interface:

- **Live**: active session dashboard, scene deck, character quick cards with blood pool and willpower controls, quick action bar with Quick NPC and a Table chip (create/join)
- **People**: player characters and NPCs, NPC creation with optional link to a full sheet
- **Plots**: plot arcs, notes, scenes with hooks
- **Visual**: media library and annotation board
- **Audio**: multi-track audio mixer (ambience, music, SFX, custom) with presets
- **More**: dice roller, locations, factions, sessions, secrets, clues

### Live Table

The Storyteller creates a room; players join and pick a character. Three connection modes:

| Mode | Requirements |
|------|--------------|
| LAN discovery | Both devices on the same Wi-Fi network |
| WiFi Direct | No router needed; proximity and permission only |
| Manual | Enter IP and port (default port 39641) |

During a live session:

- Real-time stat sync (blood pool, willpower) between master and players
- Dice with a 3D renderer, roll log, private rolls, roll requests from the Storyteller, and modifiers
- Dice engine: dice pools, difficulty, attribute-ability pairing, botch detection, willpower and blood expenditure, specialties, automatic successes
- House rules applied to dice, XP, and creation (exploding tens, XP costs, chronicle freebies, content filters)
- Reveal handouts (clues and secrets) to selected players
- Targeted file sharing and full-screen asset presentation
- Voluntary character sheet push to the Storyteller, who can save it into the chronicle
- Scene deck and audio mixer usable from the table
- Landscape split layout (tools panel left, table right) with enlarged dice and a landscape-adapted cinematic reveal

### Visual Board

Layered annotation over maps and images: pen, highlighter, line, arrow, circle, rectangle, text, pin, and eraser tools; layer create/rename/delete/reorder; pins linked to chronicle entities (locations, NPCs, events, secrets, clues); snapshot revision history with restore; presentation mode that hides editor tools.

### Media Viewers

- PDF viewer with lazy page rendering, page slider, zoom, and fullscreen presentation
- Video player (ExoPlayer) with loop and fullscreen controls
- Audio mixer with multi-track playback, per-track volume and loop, presets, and import of MP3, WAV, OGG, FLAC, AAC, M4A

### Tools

- Equipment library import/export in structured JSON
- Full backup export/restore in a single `.v20backup` archive (data + portraits/media/audio files) with a daily automatic copy to `Download/V20Companion`
- Localisation: 1031 strings in Italian and English, switchable at runtime

---

## Installation

Download `app-debug.apk` from the [Releases](https://github.com/BrutusLiberatore/v20_app_companion/releases) page, open it, and allow installation from unknown sources when prompted.

The current build is signed with the debug key. It installs and updates normally from the same machine.

---

## Building

Prerequisites:

- JDK 17 or 21
- Android SDK with compileSdk 34
- Gradle 8.9 (wrapper included)

```bash
gradlew assembleDebug
```

The APK is written to `app/build/outputs/apk/debug/app-debug.apk`.

---

## Testing

```bash
gradlew test
```

The suite contains 350 unit tests covering character CRUD, import/export, the backup engine, the dice engine, migrations, repositories, live room messages, scene variants, the combat timer, XP calculators, and house-rule engines.

---

## Technology

| Component | Technology |
|-----------|-----------|
| Language | Kotlin 2.0.0 |
| UI | Jetpack Compose, Material 3 |
| Database | Room v13, 12 migrations, 27 entities |
| Dependency injection | Manual, via `AppContainer` |
| Images | Coil 2.6.0 |
| PDF | Android `PdfRenderer` |
| Video | Media3 ExoPlayer 1.4.1 |
| Networking (live table) | Raw TCP/UDP sockets, WiFi Direct |
| Serialization | `kotlinx.serialization` |
| Min SDK | 26 (Android 8.0) |
| Target SDK | 34 |
| Build | Gradle 8.9, AGP 8.5.0 |
| CI | GitHub Actions: debug build, unit tests, EN/IT string parity, U+FFFD scan |

Every character edit is written to the database on first modification; there is no separate save step. Migrations are versioned, with destructive migration configured only as a last-resort fallback.

---

## Project Structure

```
app/src/main/java/com/v20charactermanager/
  data/
    di/AppContainer.kt              -- Manual dependency injection
    local/
      V20Database.kt                -- Room database and migrations
      ChronicleImageManager.kt      -- Image storage and thumbnails
      dao/                          -- Room DAOs
      entity/                       -- Room entities
    network/
      LiveRoomServer.kt             -- TCP server for the live table (port 39641)
      LiveRoomClient.kt             -- TCP client
      TableDiscoveryManager.kt      -- LAN discovery (UDP broadcast)
      WifiDirectManager.kt          -- WiFi Direct group and peer discovery
    repository/                     -- Repository implementations and mappers
  domain/
    definition/                     -- AbilityId, AttributeId, RuleSet enums
    engine/                         -- DiceEngine, DicePoolBuilder, import/export engines
    model/                          -- Pure Kotlin domain models
    repository/                     -- Repository interfaces
  ui/
    chronicle/                      -- Chronicle and Storyteller screens
    compendium/                     -- V20 rules reference
    creation/                       -- Character creation wizard
    io/                             -- Import/export UI
    liveroom/                       -- Live table screens, dice renderer, discovery
    navigation/NavGraph.kt          -- Single navigation graph
    settings/                       -- App settings
    sheet/                          -- Character sheet sections
    components/                     -- Shared UI components
  util/                             -- Helpers (locale, and others)

app/src/test/                       -- JVM unit tests (350)
```

---

## Documentation

- `FUNZIONI_APPLICAZIONE.md` — function-by-function guide to the application (Italian)
- `V20_AGENT_MASTER_SPEC_v2.1.md` — master specification
- `V20_Agent_Addendum_Session_Manager_Adaptive_UX_VisualBoard_v1.0.md` — session manager and visual board addendum
- `V20_Clan_Data_Character_Creator_IT.md` — clan data and creation rules

---

## Credits

The 3D dice use the model set "Low Poly 3D Dice Set" by [eddex](https://eddex.itch.io/low-poly-3d-dice-set-game-assets), licensed under [CC BY-SA 4.0](https://creativecommons.org/licenses/by-sa/4.0/). Face textures are baked from the original UV texture.

---

## Disclaimer

This is an unofficial, fan-made companion tool. *Vampire: The Masquerade* and all related properties are trademarks of Paradox Interactive AB. This project is not affiliated with or endorsed by Paradox Interactive.
