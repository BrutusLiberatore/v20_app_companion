# V20 Character Manager — Cronologia Modifiche

## Ultimo aggiornamento: 07/10/2026

---

## Stato Build: ✅ SUCCESS (v48) — CI GitHub Actions verde (build + 350 test, 1053 stringhe)

---

## Processo release — nome APK (post v47)

- **Ogni APK caricato su GitHub Releases va rinominato in `V20-Companion-App.apk`** prima dell'upload (build → copia/rinomina → `gh release upload <tag> <path>\V20-Companion-App.apk`)
- Asset e note della release `v1.10.2` già aggiornati con il nuovo nome; README allineato (sezione Installation e Building)

---

## Tutorial guidato del tavolo + fix WiFi Direct (v48)

**Release**: `v1.10.3` (versionCode 11003)

- **Tutorial spotlight a 7 passi per il Narratore** (`ui/tutorial/TableTutorial.kt`): al primo ingresso in una stanza evidenzia i **pulsanti reali** (banner IP/WiFi Direct, giocatori e punti, presentare file, condivisione selettiva, combattimento, dadi, registro tiri e cronaca) con scheda descrittiva accanto all'elemento forato; overlay a livello radice in MainActivity, completamento salvato in `v20_table_tutorial`, saltabile con "Salta"/tiro indietro, riprovabile da Impostazioni («Rivedi tutorial tavolo»); fuori dalla stanza le pagine compaiono centrate senza foratura
- **Fix WiFi Direct — errori espliciti**: permesso negato → errore dedicato + pulsante "Apri impostazioni"; pre-check Wi-Fi e posizione (SDK < 33) prima della ricerca; lista vuota con checklist completa
- **Fix WiFi Direct — scansione ripetuta**: 4 cicli da 4,5s (~18s totali) con stop automatico su trova/errore; errori del manager sincronizzati nella UI (prima restavano nel log)
- **Stato P2P visibile nel master**: sotto l'IP, "WiFi Direct attivo" (verde) oppure "solo sulla stessa rete (motivo)" (arancione); fallback a LAN **non più silenzioso** — pre-check Wi-Fi e motivo del fallimento del gruppo mostrati al momento della creazione stanza
- **APK**: rilasciato come `V20-Companion-App.apk`
- **350 test, 1053 stringhe EN/IT**

---

## Backup completo con ripristino (v45)

- **Backup completo in un unico file `.v20backup`** (ZIP): `backup.json` con tutti i dati strutturati (personaggi, cronache con tutte le entità figlie, media, audio, regole della casa) più i file binari (ritratti, immagini originali/thumbnail, tracce audio) con mappa dei percorsi rimappata all'estrazione
- **Esporta** dalla schermata Import/Export (SAF, `application/zip`) e **Ripristina** con dialog di conferma "sostituisci tutto": estrazione file → `clearAllTables` → reinserimento in ordine (personaggi → cronache → figli → media → audio → regole casa)
- **Copia automatica giornaliera** in `Download/V20Companion` (MediaStore su API 29+, cartella specifica dell'app sotto API 29), cadenza 24h controllata all'avvio, saltata se non ci sono dati, marcata solo a successo
- Validazione dedicata: file non valido → `BackupFormatException`, `schemaVersion` futuro → `BackupVersionException`, entrambi con stringhe proprie EN/IT
- **350 test (+8 BackupEngineTest), 1031 stringhe**

---

## Fix CI — gradlew eseguibile (v44)

**Commit**: `368d828`

- Permesso eseguibile `gradlew` (da 100644 a 100755) per l'esecuzione sui runner Linux
- `.gitattributes` per mantenere `gradlew` con fine riga LF anche su checkout Windows
- `actions/setup-java` aggiornato a v5 (v4 deprecato)

## CI GitHub Actions (v43)

**Commit**: `6b0365e`

- Workflow `.github/workflows/ci.yml`: build debug + test unitari a ogni push e pull request
- JDK 21 (Temurin), cache Gradle, annullamento run supersoste (`concurrency`)
- Controlli rapidi prima del build: parità stringhe EN/IT e assenza di caratteri di sostituzione U+FFFD nei sorgenti
- Report HTML (test/lint) caricati come artifact in caso di errore

## Onboarding al primo avvio + tavolo in orizzontale (v42)

**Commit**: `957ba9e`

- **Selettore lingua al primo avvio** — English/Italiano prima di tutto, flag dedicato `language_chosen` (non alias della locale), applicazione via `AppCompatDelegate.setApplicationLocales`
- **Tutorial interattivo a 9 passi** — overlay paginato con punti indicatore, completamento salvato in `v20_tutorial`, riprovabile da Impostazioni ("Rivedi il tutorial")
- **Tavolo live in landscape** — layout split: pannello strumenti a sinistra, tavolo a destra; banner condivisione e feed tiri spostati nel pannello
- **Chip "Tavolo" nella QuickActionBar** della prima pagina cronaca con dialog Crea/Unisciti — risolve l'assenza della barra alta nel layout largo
- **Dadi animati più grandi in orizzontale** — griglia fino a 6 colonne e raggio 0.42 (vs 0.34)
- **Reveal cinematografico riadattato in landscape** — nome + etichetta su una sola riga, verdetto a 34sp, meno padding → più spazio ai dadi
- Fix: chip "Tavolo Live" nascosto quando si è già sulla schermata del tavolo
- **334 test, 1019 stringhe**

## Estetica gotica e adattamento landscape/tablet (v41)

**Commit**: `2e519ef`

- Palette colori tokenizzati (tema scuro V20) e ritratti/icone clan — 14 simboli Dark Pack Paradox in `drawable-nodpi`, logo e note di licenza in Impostazioni
- Avatar con ritratto del PG o simbolo del clan; icone clan al tavolo live via protocollo `clanId` con fallback lato client
- Servizio keep-alive del tavolo (`TableKeepAliveService`)
- Regole casa collegate a dadi/XP/creazione con start sangue/volontà correnti (`HouseRulesEngines`)
- Layout adaptivo: Home con pannello laterale strumenti, elenchi Compendium/Cronache/Seleziona PG a 2 colonne, media 2/3/4 colonne, Impostazioni 6 card su 2 colonne
- Barra alta compatta `V20TopBar` (40dp, inset status bar e cutout) su tutte le 26 schermate; tasto "Tavolo" diretto nel pannello Home
- **334 test, 995 stringhe**

## Timer di turno nel tracker di combattimento (v40)

**Commit**: `c87b2ae`

- Countdown sincronizzato Off/15/30/60/120s con pausa/ripresa e auto-avanzamento del turno
- Beep + vibrazione alla scadenza, rebase dell'orologio tra dispositivi
- Stessa UI su tavolo master/giocatore e tracker standalone
- **334 test, 987 stringhe**

## Pannello stato rapido unificato (v39)

**Commit**: `f39daff`

- Sangue/volontà/salute condivisi su sessione, scheda e tavolo live, con modifica interattiva
- Sync `StatUpdate` salvata in locale; menu master "Stato dei giocatori" per correggere ogni PG
- `EditCharacterViewModel` con persist immediato; dominio `applyStatUpdate` puro
- **316 test, 981 stringhe**

## Tracker di combattimento + reveal cinematografico (v38)

**Commit**: `3ccdac4`

- Tracker di combattimento con iniziativa V20 (1d10 + Des + Prontezza), round e menu live + standalone locale
- Reveal cinematografico dei dadi (menu tavolo, impostazioni, schermo locale; flash rosso su boccia)
- **302 test, 977 stringhe**

## WiFi Direct e varianti scena (v36–v37)

- **v36** `ff66eb9` — connessione WiFi Direct al tavolo (P2P senza router) con permessi runtime e fallback LAN; varianti scena con selezione predefinita (DB v13)
- **v37** `ada25f7` — README riscritto: sezione tavolo live con i tre modi di connessione, dati aggiornati, credito dadi 3D CC BY-SA

## Strumenti Narratore e contenuti al tavolo (v31–v35)

- **v31** `0bf9112` — dadi 3D nel tavolo live, condivisione file mirata, localizzazione EN/IT completa, rimozione codice morto, documento funzioni
- **v32** `1952139` — dadi 3D a texture reale (CC BY-SA eddex), log tiri con tiri privati e tiri da scheda, modificatori/roll request (sezione 34), feed riducibile a icona
- **v33** `e26d513` — regole casa applicate a dadi/XP/creazione (costi XP, decine esplosive, freebie cronaca, contenuti ammessi), sezioni scheda riordinabili, invio scheda volontario al Narratore
- **v34** `549628f` — azione RIVELA handout al tavolo live (Addendum 63): indizi/segreti ai giocatori con evento "indizio rivelato"
- **v35** `604eb6f` — documento funzioni aggiornato: 8 sezioni scheda con riordino, quick action bar e quick NPC del Narratore, mixer audio al tavolo

## Tavolo virtuale, layout adattivo e creazione PG (v26–v30)

- **v26** `4639f25` — mixer audio con preset, browser cronache profondo, fix crash tavolo
- **v27** `0c7f040` — layout adattivo Compact/Medium/Expanded con NavigationRail e ContextInspector
- **v28** `69aed72` — correzioni Character Creator secondo la spec V20
- **v29** `aac199f` — validazione creazione (gap #3), fix "Avanti", tasto Chiudi Tavolo per il master
- **v30** `0509b06` — fix crash creazione, pack tavolo/sedie al tavolo live, ritratti in rete, schede PG cliccabili (master: tutte, giocatori: solo la propria)

## Rete del tavolo live — serie di fix TCP (v15–v24)

- **v15** `72da62f` — fix race condition sul join TCP + diagnostica
- **v16** `72cbfdb` — rilevamento IP via `ConnectivityManager` (specifica WiFi)
- **v17** `16556b2` — banner IP spostato sopra l'area del tavolo
- **v18** `ecba60e` — fix join TCP: serializer esplicito, stato connessione, gestione errori di parsing lato server
- **v19** `44c2e65` — fix serializzazione TCP: `@Serializable` su classi sigillate, timeout server, stato connessione
- **v20** `f3980fd` — visibilità dei giocatori sul master, fix crash `presentFile`, UI aggiornata su `onConnected`, broadcast su IO thread, `ConcurrentHashMap`
- **v21** `df2c27e` — fix caricamento asset cronaca per la presentazione file del master
- **v22** `1e37952` — modalità presentazione riscritta: solo asset ID, niente byte file via TCP
- **v23** `28a0d58` — condivisione file in tempo reale via Base64
- **v24** `6e8406d` — PDF con prima pagina renderizzata, video riprodotto inline a schermo intero

## Virtual Table Custom Assets + WiFi Direct (v14)

### Virtual Table Redesign
- **Custom medieval assets** — tavolo rotondo (`assets_tavolo.png`) e sedie (`assets_sedia.png`) dal design medievale
- **Circular seat positioning** — sedie posizionate equidistantemente attorno al tavolo usando calcolo trigonometrico (angolo + raggio)
- **ChairWithPlayer** — composable che mostra la sedia ruotata verso il centro con avatar del giocatore sopra
- **SeatData** — data class con angolo, rotazione, dimensioni sedia/avatar, portraitUri
- **buildCircularSeats()** — calcola posizioni circolari per 8 posti, master al top (-90°)
- **onGloballyPositioned** — il container Box misura le dimensioni parent e calcola offset pixel per ogni sedia
- **Rotation** — ogni sedia ruota `angleDeg + 90°` per affacciarsi verso il centro del tavolo

### WiFi Direct (senza mDNS)
- **WifiDirectManager** — gestione WiFi P2P: creazione gruppo, peer discovery, connessione diretta
- **LiveRoomServer** — bind `0.0.0.0`, accetta connessioni TCP dirette (senza mDNS)
- **LiveRoomClient** — connessione TCP diretta con IP del master
- **Fallback** — se WiFi Direct non disponibile, fallback a connessione diretta per IP
- **jmDNS rimosso** — dipendenza rimossa da `build.gradle.kts`

### Permissions
- Aggiunti: `CHANGE_WIFI_STATE`, `CHANGE_NETWORK_STATE`, `ACCESS_FINE_LOCATION`, `ACCESS_COARSE_LOCATION`, `NEARBY_WIFI_DEVICES`

### UI
- QuickActionBar: icona 🎲 per "Tavolo Live"
- Stringhe: "Tavolo Live" IT/EN

### File Risorse
- `res/drawable/assets_tavolo.png` — tavolo medievale rotondo
- `res/drawable/assets_sedia.png` — sedia medievale
- Permessi INTERNET + WIFI_STATE_STATE aggiunti

### Crash Handler
- **CrashHandler** — utility per logging crash su file con timestamp
- **CrashLogScreen** — visualizzazione log crash con espandi/copia/cancella
- **CrashDialog** in `MainActivity` — mostra crash al riavvio con pulsante "COPIA LOG"
- Log crash copiabili negli Impostazioni
- Route `crash_logs` accessibile da Impostazioni

### Bug Fix
- **NavGraph indentation fix** — rotte `session_recap`, `live_room`, `crash_logs` spostate a livello top-level del NavHost (da 12 a 8 spazi di indentazione)

## Moduli completati

### LinkedTextEditor & @Autocomplete
- **LinkedTextEditor** composable — campo di testo con supporto `@` autocomplete per collegare elementi della cronaca
- 12 categorie supportate: PG/PC, NPC, LUOGHI/LOC, MAPPE/MAP, SEGRETI/SECRET, INDIZI/CLUE, NOTE/NOTA, SESSIONI/SESSION, FAZIONI/FACTION, EVENTI/EVENT, SCENE/SCENA, OGGETTI/ITEM
- Supporto bilingue IT/EN — ogni funziona accetta keyword italiane e inglesi
- Menu popup con filtro live durante la digitazione
- Inserimento formato `[TYPE:ID:Name]` nei campi di testo
- **LinkedTextDisplay** — rendering dei link come chip cliccabili in modalità lettura
- Funzioni `parseLinks()` e `stripLinks()` per parsing e pulizia testo
- `toLinkableItems()` converte lo stato della cronaca in elementi collegabili

### NPC Full Sheet Support
- Campo `characterId` aggiunto al modello `NpcEntry` per link opzionale a scheda personaggio completa
- **DB Migration 7→8** — `ALTER TABLE npcs ADD COLUMN characterId TEXT`
- **NpcDetailSheet** aggiornato con:
  - Pulsante "Apri Scheda Personaggio" quando NPC è collegato a un Character
  - Pulsante "Crea Scheda Personaggio Completa" per promuovere NPC a personaggio completo
  - Campi descrizione e note ora usano `LinkedTextEditor` con supporto `@` autocomplete
  - Visualizzazione link cliccabili in modalità lettura
- `NpcEntry` e `NpcEntity` aggiornati con campo `characterId`
- Mapper `ChronicleMappers.kt` aggiornati per `characterId`

### Bug Fix
- **Willpower format string** — `sheet_willpower` con `%1$d/%2$d` ora usa `sheet_willpower_label` come etichetta separata
- Campi Willpower ora mostrano correttamente "Willpower"/"Volontà" con valori separati

---

## Moduli completati (precedenti)

### Character System
- Character CRUD (create, read, update, delete)
- Portrait system (gallery/camera, internal storage)
- Character randomizer (random name, attributes, abilities)
- Character duplication
- Import/Export (.v20 format)
- Bilingual IT/EN (~600+ strings)

### Character Creation
- 5-step builder (Identity, Attributes, Abilities, Advantages, Final Touches)
- Attribute presets (7/5/3: Combat, Stealth)
- Ability presets (13/9/5: Combat, Social, Knowledge)
- Camarilla/Anarch/Independent (3 disc/5 bg/7 virtue)
- Sabbat (4 disc/5 bg/5 virtue)
- Caitiff/Pander (10 XP new discipline, 6× level)

### Character Sheet
- Tabbed UI (Info, Attributes, Abilities, Advantages, Morality, Resources, Equipment, Notes)
- Clickable item popups (equipment, merits, flaws, disciplines, backgrounds)
- V20 Green Edition theme (green palette, dark backgrounds, ivory text)
- Custom 8 icons (health, blood pool, humanity, attributes, abilities, merits, equipment, notes)
- V20 Gothic Button Design System (V20BloodButton, V20IvoryButton, V20ControlButton, etc.)

### Session Management
- Session screen with health/blood/Willpower management
- Quick dice roller from sheet
- Session notes

### Dice System
- Manual dice pool input
- Difficulty settings
- Optional exploding tens (configurable)
- Dice statistics

### XP System
- XP spending UI
- Cost rules (standard, Caitiff/Pander)
- Session-based XP awards

### Compendium
- Clans, Disciplines, Backgrounds, Merits/Flaws
- Search and filtering
- Detail views

### Settings
- Language toggle (IT/EN)
- Theme settings

---

## Chronicle System (v5 → v6)

### Chronicle CRUD
- Create/read/update/delete chronicles
- Chronicle list screen
- Chronicle detail screen with 10 tabs

### Chronicle Members
- Add/remove player characters
- Role assignment (Player/NPC)

### Chronicle Entities (10 types)
| Entity | Create | Edit | Delete | Description |
|--------|--------|------|--------|-------------|
| NPCs | ✅ | ✅ | ✅ | Name, role, creature type, description, personality, motivation |
| Locations | ✅ | ✅ | ✅ | Name, type, description, narrator notes |
| Factions | ✅ | ✅ | ✅ | Name, description, narrator notes |
| Plot Arcs | ✅ | ✅ | ✅ | Title, summary, type (Main/Subplot/Personal), starting situation |
| Scenes | ✅ | ✅ | ✅ | Title, notes |
| Secrets | ✅ | ✅ | ✅ | Title, content, visibility (GM_ONLY/PUBLIC) |
| Clues | ✅ | ✅ | ✅ | Title, content, status |
| Events | ✅ | ✅ | ✅ | Title, description, in-game time, type |
| Boons | ✅ | ✅ | ✅ | Creditor, debtor, description |
| Relationships | ✅ | — | ✅ | From/to entity, type, description |

### Chronicle Image System
- **ChronicleImageManager**: Save/delete images from internal storage
- **ChronicleImageRow**: Reusable composable with picker + preview
- Image support in: NPCs, Locations, Factions, Events
- Cards show 48px thumbnails
- Edit dialogs have image picker + remove option

---

## Visual Board System (v7) — COMPLETATO

### Media Library (✅)
- MediaLibraryScreen con griglia 2 colonne
- Filtri per categoria (ALL, MAPS, NPC, LOCATIONS, CLUES, DOCUMENTS, OTHER)
- Import immagini da galleria
- Click apre Image Viewer
- Long press per eliminare
- Thumbnail con overlay titolo/tipo
- Empty state con icona e messaggio

### Image Viewer (✅)
- Pinch-to-zoom con `rememberTransformableState`
- Pan/drag support
- Double tap per toggle zoom (1x ↔ 2.5x)
- Top bar con titolo, back, layers, draw toggle, presentazione
- Bottom bar con dimensioni e zoom percentage
- Layers panel slide-in da destra con layer attivo evidenziato
- Background nero per full immersion
- Modalità disegno blocca pan/zoom durante editing

### Annotation Canvas (✅ — Fase 6)
- **AnnotationCanvas**: Compose Canvas con rendering annotazioni non-distruttive
- **9 strumenti di disegno**: Pen, Highlighter, Line, Arrow, Circle, Rectangle, Text, Pin, Eraser
- **AnnotationToolbar**: Selezione strumenti, color picker (10 preset), slider spessore (1-20)
- **Undo/Redo**: Stack operazioni in MediaViewModel, elimina/riinserisci da DB
- **Preview live**: Durante il disegno l'annotazione è visibile in tempo reale
- **Layer attivo**: Selezione layer nel pannello laterale, tutte le nuove annotazioni vanno sul layer attivo
- **Clear Layer**: Elimina tutte le annotazioni del layer attivo
- **Save revisione**: Snapshot delle annotazioni attuali in ImageRevision
- Coordinate normalizzate (0-1) per cross-platform
- Tutte le annotazioni visibili solo sui layer visibili
- ~30 nuove stringhe EN+IT

### MediaViewModel (✅)
- Gestione completa CRUD per MediaAsset, ImageDocument, ImageLayer, ImageAnnotation, ImageRevision
- Undo/Redo stack con eliminazione e reinserimento DB
- Toggle modalità disegno
- Selezione layer attivo
- Clear annotazioni layer attivo
- Restore revisione (elimina annotazioni attuali)
- Toggle visibilità layer
- Caricamento documenti, layers, annotazioni, revisioni

---

## Version History System (v8) — COMPLETATO

### VersionHistoryScreen (✅ — Fase 7)
- Lista di tutte le revisioni ordinate per numero decrescente
- Card per ogni revisione con: numero, descrizione, data, sessione linkata
- Bottone "Ripristina" con conferma AlertDialog
- Empty state con icona History
- Navigate da ImageViewerScreen tramite icona History nella top bar

### Presentation Mode (✅ — Fase 8)
- Toggle dalla top bar (icona Play/Presentation)
- Nasconde tutta la UI (top bar, bottom bar, layers panel, toolbar)
- Mostra SOLO annotazioni su layer visibili con visibility=PUBLIC
- Disegno e zoom disabilitati durante presentazione
- Badge "PRESENTAZIONE" in alto al centro (semi-trasparente)
- Uscita con singolo tocco ovunque
- Import con salvataggio in internal storage
- Create image document + default layer all'import
- Select annotation tool
- Save revision

### Modelli di dominio (5)
| File | Descrizione |
|------|-------------|
| MediaAsset.kt | Asset media (maps, portraits, documents, etc.) |
| ImageDocument.kt | Documento immagine con zoom defaults |
| ImageLayer.kt | Layer con visibilità e ordine |
| ImageAnnotation.kt | Annotazioni (pen, shapes, text, pins) |
| ImageRevision.kt | Versioni storiche |

### Room Entities (5) + DAOs (5)
| Entity | Table | DAO |
|--------|-------|-----|
| MediaAssetEntity | media_assets | MediaAssetDao |
| ImageDocumentEntity | image_documents | ImageDocumentDao |
| ImageLayerEntity | image_layers | ImageLayerDao |
| ImageAnnotationEntity | image_annotations | ImageAnnotationDao |
| ImageRevisionEntity | image_revisions | ImageRevisionDao |

### Database Migration 5→6
- 5 nuove tabelle con foreign keys e indici

### Repository + Mappers
- MediaRepository.kt (interface)
- MediaRepositoryImpl.kt (implementation)
- MediaMappers.kt (domain↔entity con JSON serialization per geometry/style)

### Navigation
- Routes: MEDIA_LIBRARY, IMAGE_VIEWER
- ChronicleDetailScreen ha tab "Media Library" che apre la schermata
- MediaViewModel collegato in NavGraph

### Da implementare (prossimi passi)
- ~~Annotation system~~ ✅ COMPLETATO
- ~~Undo/Redo system~~ ✅ COMPLETATO (trough revision history)
- ~~Version History UI~~ ✅ COMPLETATO
- ~~Presentation Mode~~ ✅ COMPLETATO
- Session/Scene integration (auto-log eventi, riepilogo fine sessione)
- Pin interattivi collegati a entità cronaca
- Export .v20chronicle (archive format)
- Layout adattivo (Compact/Medium/Expanded)
- House Rules configurabili
- Roll dal character sheet

---

## Database Schema (v11)

```
characters
chronicles
chronicle_members
sessions
chronicle_notes
chronicle_character_notes
npcs (imagePath)
locations (imagePath)
factions (imagePath)
relationships
plot_arcs
scenes
secrets
clues
events (imagePath)
boons
media_assets
image_documents
image_layers
image_annotations
image_revisions
quick_notes
session_events
audio_tracks
audio_presets
```

---

## Colori V20 Green Edition

| Nome | Valore | Uso |
|------|--------|-----|
| V20Green | #3A8A5A | Primary, accents |
| V20GreenBright | #5ABF7A | Highlights |
| V20DarkBg | #0A0A0A | Background |
| V20Surface | #141414 | Cards |
| V20Surface2 | #1E1E1E | Tab bar |
| V20Ink | #E8E8E8 | Text primary |
| V20InkDim | #9A9A6A | Secondary text |
| V20InkFaint | #5A5A4A | Muted text |
| V20GoldBright | #D4AF37 | Gold accent |
| V20BloodRed | #8B0000 | Blood points |
| V20Error | #FF4444 | Delete/errors |

---

## Struttura Cartelle

```
app/src/main/java/com/v20charactermanager/
├── data/
│   ├── di/AppContainer.kt
│   ├── datastore/SettingsDataStore.kt
│   ├── local/
│   │   ├── V20Database.kt (v6)
│   │   ├── PortraitManager.kt
│   │   ├── ChronicleImageManager.kt
│   │   ├── converter/Converters.kt
│   │   ├── dao/ (20+ DAOs)
│   │   └── entity/ (20+ entities)
│   └── repository/
│       ├── CharacterRepositoryImpl.kt
│       ├── ChronicleRepositoryImpl.kt
│       ├── ChronicleMappers.kt
│       ├── MediaRepositoryImpl.kt
│       ├── MediaMappers.kt
│       ├── RuleRepositoryImpl.kt
│       └── SettingsRepositoryImpl.kt
├── domain/
│   ├── model/ (25+ models)
│   └── repository/ (5 interfaces)
├── ui/
│   ├── chronicle/
│   │   ├── ChronicleViewModel.kt
│   │   ├── ChronicleListScreen.kt
│   │   ├── ChronicleDetailScreen.kt
│   │   ├── MediaViewModel.kt
│   │   ├── MediaLibraryScreen.kt
│   │   ├── ImageViewerScreen.kt
│   │   ├── AnnotationCanvas.kt
│   │   ├── AnnotationToolbar.kt
│   │   └── VersionHistoryScreen.kt
│   ├── compendium/
│   ├── components/
│   │   ├── V20Buttons.kt
│   │   └── PortraitPicker.kt
│   ├── creation/
│   ├── dice/
│   ├── home/HomeScreen.kt
│   ├── io/
│   ├── navigation/NavGraph.kt
│   ├── session/
│   ├── settings/
│   ├── sheet/
│   ├── theme/ (Color.kt, Theme.kt)
│   └── xp/
└── V20CharacterManager.kt (Application)
```

---

### v9 — Session Manager Backend + Mobile-First UI (Session Manager / Storyteller Mode)

**Commit range**: `68feb8a` → `4275f81`

#### Database Migration 6→7
- Extended `Session` entity with 12 new fields: status, realStartDateTime, realEndDateTime, inGameDate, activeSceneId, plannedSceneIds, participantCharacterIds, preparationNotes, liveNotes, recap, xpAwarded, unresolvedThreadIds
- Extended `Scene` entity with 3 new fields: order, npcIds, notes
- New tables: `quick_notes`, `session_events`
- Full migration SQL for existing data preservation
- New domain models: `QuickNote`, `SessionEvent`, `NoteScope`, `SessionEventType`
- New DAOs: `QuickNoteDao`, `SessionEventDao`
- Updated `SessionDao`: active session query, session number max
- Updated `SceneDao`: scenes by session, ordered scenes, active scene query
- Extended `ChronicleRepository` + `ChronicleRepositoryImpl` with QuickNote and SessionEvent CRUD

#### Session Lifecycle
- `startSession()` — sets ACTIVE status + realStartDateTime + auto-logs SESSION_STARTED event
- `endSession()` — sets COMPLETED status + realEndDateTime + recap + auto-logs SESSION_ENDED event
- `setActiveScene()` — sets activeSceneId + auto-logs SCENE_CHANGED event
- `updateSessionLiveNotes()` / `updateSessionPrepNotes()` — in-session notes editing
- Active session tracked in `ChronicleDetailUiState.activeSession`

#### Mobile-First UI Components (Quest Portal reference)
- **StorytellerLiveScreen** — Live Dashboard with session header, active scene card, PG list, PNG list, quick actions
- **ActiveSceneCard** — Compact scene display with Open/Change buttons
- **CharacterLiveCard** — Name, Clan, Gen, Blood +/-, Willpower +/-, Health summary
- **NpcLiveCard** — Name, Role, chevron indicator
- **QuickActionBar** — Dice, Quick Note, Event chips
- **SceneDeckSheet** — Bottom Sheet for scene selection with check/play/unplayed icons
- **ChronicleBottomNavigation** — 5-tab bottom nav: Sessione, Persone, Trame, Visual, Altro
- **ChronicleStorytellerScreen** — Orchestrator with bottom nav switching between tabs
- **ChroniclePeopleTab** — PG + NPC lists with add dialog
- **ChroniclePlotsTab** — Plot arcs + notes + scenes
- **ChronicleMoreTab** — Locations, Factions, Sessions, Secrets, Clues

#### Localized Strings (EN + IT)
- 25+ new strings in both EN and IT for mobile-first UI components
- Session lifecycle, quick notes, scene deck, bottom navigation labels

#### Technical Details
- SessionStatus enum: PLANNED, ACTIVE, COMPLETED, ARCHIVED
- SessionEventType enum: SESSION_STARTED, SESSION_ENDED, SCENE_CHANGED, MANUAL_EVENT, etc.
- SceneStatus enum: PLANNED, ACTIVE, COMPLETED, ARCHIVED
- All UI follows mobile-first principle (Quest Portal reference): bottom nav, bottom sheets, quick actions, single workspace per tab

---

### v10 — Location Image Annotation + Auto-Save + High-Res Image Storage

**Commit**: `1264874`

#### ChronicleImageManager Upgrade
- Resolution increased from 512px to 1920px max for annotation-quality images
- Automatic EXIF rotation correction (phone photos no longer appear rotated)
- Internal thumbnail generation (256px) for fast list views
- Proper cleanup: `deleteAllForEntity()` removes both full and thumbnail copies
- JPEG quality upgraded from 85 to 92

#### Location Image Attachment
- `LocationImageScreen`: attach photo from gallery → save internal copy to app storage
- Original image is NEVER modified; app works on its own internal copy
- Two layers created automatically: "Mappa" (GM_ONLY) + "Annotazioni" (PUBLIC)
- Location→MediaAsset linked via `linkedEntityIds`

#### Annotation Auto-Save
- `saveAnnotationImmediate()` in MediaViewModel: every annotation is persisted to Room DB on stroke completion
- No explicit "Save" required for individual annotations — first edit = permanent save
- Undo/Redo still available, backed by Room inserts/deletes

#### Navigation
- New route: `chronicle/{chronicleId}/location/{locationId}/image`
- Locations list in "Altro" tab now shows image icon → tap to open annotation view
- Full canvas tools available: pen, highlighter, line, arrow, circle, rectangle, text, pin, eraser

#### Dependency Added
- `androidx.exifinterface:exifinterface:1.3.7` for EXIF rotation handling

---

### v11 — Bug Fixes (9 GM Zone Issues) + Image Rename + PIN Tool

**Commit**: `663c835`

#### Critical Bug Fixes
- **BUG 1**: MEDIA tab navigation side-effect — changed from direct call to `LaunchedEffect(Unit)` to prevent infinite recomposition loops
- **BUG 2**: `saveAnnotationImmediate()` — changed from `updateAnnotation` (non-existent record) to `insertAnnotation` (creates new record)
- **BUG 3**: `saveRevision()` — now serializes actual annotations/layers via `Json.encodeToString()` instead of hardcoded `"[]"`
- **BUG 4**: `restoreRevision()` — now deserializes and re-inserts annotations/layers from the revision snapshot

#### Medium Bug Fixes
- **BUG 5**: Scene selection — now passes selected `sceneId` through callback to `viewModel.setActiveScene()` instead of ignoring the selection
- **BUG 8**: LocationImageScreen — now shows all visible layer annotations (consistent with ImageViewerScreen) instead of only active layer
- **BUG 9**: PIN tool — now creates proper `ImageAnnotation` with `AnnotationType.PIN` and `AnnotationGeometry(position:)` instead of empty `{ }` lambda

#### Image Rename
- `MediaViewModel.renameAsset()` — update asset title, persisted to Room DB immediately
- `MediaViewModel.updateAssetDescription()` — update asset description
- `MediaLibraryScreen` — pencil icon on each image card → rename dialog
- Long-press still shows delete dialog
- EN+IT strings: `media_rename`

---

## Build Info

- **Platform**: Android (Kotlin, Jetpack Compose)
- **compileSdk**: 34
- **Kotlin**: 2.0.0
- **AGP**: 8.5.0
- **Room**: 2.6.1
- **Java**: 21
- **CI**: GitHub Actions (`.github/workflows/ci.yml`) — assembleDebug + test + parità stringhe EN/IT + scan U+FFFD
- **APK size**: ~40 MB
