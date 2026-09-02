# V20 Character Manager — Audio & Video Features

## Video Player

### Loop
- Video playback loops automatically by default (`REPEAT_MODE_ALL`)
- Toggle loop on/off via the repeat icon in the top bar
- Loop state is visual: gold icon = ON, gray = OFF

### Supported Formats
- MP4, WebM, 3GPP, AVI, MOV
- Playback via Media3 ExoPlayer
- Fullscreen with system controls

---

## Audio Mix Board

### Overview
The Audio Mix board allows Storytellers to play multiple overlapping audio tracks simultaneously, each with independent volume, loop, and play/pause controls. Inspired by Pocket Bard, RPG Soundboard, and Audio Forge.

### Features

#### Multi-Track Playback
- Import and play unlimited audio tracks per chronicle
- Multiple tracks play simultaneously (layered audio)
- Each track has independent controls

#### Per-Track Controls
| Control | Description |
|---------|-------------|
| **Play/Pause** | Toggle playback for individual tracks |
| **Volume Slider** | 0-100% independent volume per track |
| **Loop Toggle** | Enable/disable looping per track |
| **Stop** | Stop and release a track |
| **Delete** | Remove track from chronicle |

#### Audio Categories
| Category | Use Case |
|----------|----------|
| **Ambience** | Environmental sounds (rain, wind, tavern noise) |
| **Music** | Background music, combat themes |
| **SFX** | One-shot sound effects (door creak, thunder) |
| **Custom** | Any other audio |

#### Global Controls
- **Stop All** button stops all playing tracks at once
- Tracks persist in Room database (survive app restart)
- Volume and loop settings saved per track

### Supported Audio Formats
- MP3, WAV, OGG, FLAC, AAC, M4A
- Any format supported by Android MediaPlayer

### How to Use
1. Open a Chronicle → tap "Audio" tab
2. Tap `+` to import audio files
3. Choose category (Ambience/Music/SFX/Custom)
4. Tap play on any track to start playback
5. Adjust volume sliders to mix
6. Toggle loop for ambient/background tracks
7. Layer multiple tracks for rich soundscapes

### Example Scene Setup
```
Vampire Tavern Scene:
├── Ambience: "Tavern Chatter" (loop ON, volume 40%)
├── Ambience: "Crackling Fire" (loop ON, volume 30%)
├── Music: "Gothic Waltz" (loop ON, volume 25%)
└── SFX: "Thunder" (triggered on demand)
```

---

## Design Reference

### Pocket Bard (Research)
- Scene-based audio with ambiences, music, one-shots
- Intensity slider for dynamic mixing
- Three music states: Explore, Combat, Victory
- Seamless transitions between states
- Location-aware weather effects

### RPG Soundboard (Research)
- Layer up to 20 sounds per scene
- Independent volume per sound
- Loop control per sound
- Sound types: Ambient, SFX, Music
- Auto-save state
- Remote control from phone

### Audio Forge (Research)
- Layered playback (music + ambience + one-shots)
- One-shot soundboard for instant triggers
- Import MP3, WAV, OGG, FLAC
- Freesound integration
- Works offline

### Our Implementation
- Multi-track layered playback ✓
- Per-track volume/loop/play ✓
- Category system (Ambience/Music/SFX) ✓
- Room persistence ✓
- Import from device storage ✓
- Loop on video ✓
- Works offline ✓

---

## Technical Details

### Database
- New table: `audio_tracks` (Room, version 10)
- New table: `audio_presets` (Room, version 11)
- Fields: id, chronicleId, title, filePath, category, isLooping, volume, isActive, createdAt
- Migration 9→10 adds audio_tracks
- Migration 10→11 adds audio_presets

### File Storage
- Audio files: `context.filesDir/chronicle_audio/`
- Video files: `context.filesDir/chronicle_documents/`
- Thumbnails: `context.filesDir/chronicle_documents/thumb_<id>.jpg`

### Dependencies
- `androidx.media3:media3-exoplayer:1.4.1` — Video playback
- `androidx.media3:media3-ui:1.4.1` — Video player UI
- Android `MediaPlayer` — Multi-track audio playback

### Key Files
- `AudioTrack.kt` — Domain model
- `AudioTrackEntity.kt` — Room entity
- `AudioTrackDao.kt` — Database DAO
- `AudioRepositoryImpl.kt` — Repository
- `AudioViewModel.kt` — ViewModel with MediaPlayer management
- `AudioMixScreen.kt` — Compose UI
- `VideoPlayerScreen.kt` — ExoPlayer video with loop
- `ChronicleBottomNav.kt` — Bottom nav with AUDIO tab
