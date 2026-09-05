package com.v20charactermanager.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.v20charactermanager.data.local.converter.Converters
import com.v20charactermanager.data.local.dao.*
import com.v20charactermanager.data.local.entity.*

@Database(
    entities = [
        CharacterEntity::class,
        ChronicleEntity::class,
        ChronicleMemberEntity::class,
        SessionEntity::class,
        ChronicleNoteEntity::class,
        ChronicleCharacterNoteEntity::class,
        NpcEntity::class,
        LocationEntity::class,
        FactionEntity::class,
        RelationshipEntity::class,
        PlotArcEntity::class,
        SceneEntity::class,
        SecretEntity::class,
        ClueEntity::class,
        EventEntity::class,
        BoonEntity::class,
        MediaAssetEntity::class,
        ImageDocumentEntity::class,
        ImageLayerEntity::class,
        ImageAnnotationEntity::class,
        ImageRevisionEntity::class,
        QuickNoteEntity::class,
        SessionEventEntity::class,
        AudioTrackEntity::class,
        AudioPresetEntity::class,
        HouseRuleEntity::class
    ],
    version = 12,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class V20Database : RoomDatabase() {
    abstract fun characterDao(): CharacterDao
    abstract fun chronicleDao(): ChronicleDao
    abstract fun chronicleMemberDao(): ChronicleMemberDao
    abstract fun sessionDao(): SessionDao
    abstract fun chronicleNoteDao(): ChronicleNoteDao
    abstract fun chronicleCharacterNoteDao(): ChronicleCharacterNoteDao
    abstract fun npcDao(): NpcDao
    abstract fun locationDao(): LocationDao
    abstract fun factionDao(): FactionDao
    abstract fun relationshipDao(): RelationshipDao
    abstract fun plotArcDao(): PlotArcDao
    abstract fun sceneDao(): SceneDao
    abstract fun secretDao(): SecretDao
    abstract fun clueDao(): ClueDao
    abstract fun eventDao(): EventDao
    abstract fun boonDao(): BoonDao
    abstract fun mediaAssetDao(): MediaAssetDao
    abstract fun imageDocumentDao(): ImageDocumentDao
    abstract fun imageLayerDao(): ImageLayerDao
    abstract fun imageAnnotationDao(): ImageAnnotationDao
    abstract fun imageRevisionDao(): ImageRevisionDao
    abstract fun quickNoteDao(): QuickNoteDao
    abstract fun sessionEventDao(): SessionEventDao
    abstract fun audioTrackDao(): AudioTrackDao
    abstract fun audioPresetDao(): AudioPresetDao
    abstract fun houseRuleDao(): HouseRuleDao

    companion object {
        @Volatile
        private var INSTANCE: V20Database? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Add importMetadataJson column to characters table
                db.execSQL("ALTER TABLE characters ADD COLUMN importMetadataJson TEXT")

                // Create chronicles table
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS chronicles (
                        id TEXT NOT NULL,
                        name TEXT NOT NULL,
                        description TEXT NOT NULL,
                        storytellerName TEXT NOT NULL,
                        userRole TEXT NOT NULL,
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL,
                        PRIMARY KEY(id)
                    )
                    """
                )

                // Create chronicle_members table with foreign keys
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS chronicle_members (
                        id TEXT NOT NULL,
                        chronicleId TEXT NOT NULL,
                        characterId TEXT NOT NULL,
                        role TEXT NOT NULL,
                        createdAt INTEGER NOT NULL,
                        PRIMARY KEY(id),
                        FOREIGN KEY(chronicleId) REFERENCES chronicles(id) ON DELETE CASCADE,
                        FOREIGN KEY(characterId) REFERENCES characters(id) ON DELETE CASCADE
                    )
                    """
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_chronicle_members_chronicleId ON chronicle_members(chronicleId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_chronicle_members_characterId ON chronicle_members(characterId)")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_chronicle_members_chronicleId_characterId ON chronicle_members(chronicleId, characterId)")

                // Create sessions table with foreign key
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS sessions (
                        id TEXT NOT NULL,
                        chronicleId TEXT NOT NULL,
                        number INTEGER NOT NULL,
                        title TEXT NOT NULL,
                        date INTEGER NOT NULL,
                        notes TEXT NOT NULL,
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL,
                        PRIMARY KEY(id),
                        FOREIGN KEY(chronicleId) REFERENCES chronicles(id) ON DELETE CASCADE
                    )
                    """
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_sessions_chronicleId ON sessions(chronicleId)")

                // Create chronicle_notes table with foreign key
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS chronicle_notes (
                        id TEXT NOT NULL,
                        chronicleId TEXT NOT NULL,
                        text TEXT NOT NULL,
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL,
                        PRIMARY KEY(id),
                        FOREIGN KEY(chronicleId) REFERENCES chronicles(id) ON DELETE CASCADE
                    )
                    """
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_chronicle_notes_chronicleId ON chronicle_notes(chronicleId)")

                // Create chronicle_character_notes table with foreign keys
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS chronicle_character_notes (
                        id TEXT NOT NULL,
                        chronicleId TEXT NOT NULL,
                        characterId TEXT NOT NULL,
                        text TEXT NOT NULL,
                        visibility TEXT NOT NULL,
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL,
                        PRIMARY KEY(id),
                        FOREIGN KEY(chronicleId) REFERENCES chronicles(id) ON DELETE CASCADE,
                        FOREIGN KEY(characterId) REFERENCES characters(id) ON DELETE CASCADE
                    )
                    """
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_chronicle_character_notes_chronicleId ON chronicle_character_notes(chronicleId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_chronicle_character_notes_characterId ON chronicle_character_notes(characterId)")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE characters ADD COLUMN narrativeJson TEXT NOT NULL DEFAULT '{}'")
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE characters ADD COLUMN portraitUri TEXT")
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS npcs (
                        id TEXT NOT NULL,
                        chronicleId TEXT NOT NULL,
                        name TEXT NOT NULL,
                        portraitAssetId TEXT,
                        creatureType TEXT NOT NULL DEFAULT 'MORTAL',
                        clanId TEXT,
                        sectId TEXT,
                        role TEXT NOT NULL DEFAULT '',
                        description TEXT NOT NULL DEFAULT '',
                        personality TEXT NOT NULL DEFAULT '',
                        motivation TEXT NOT NULL DEFAULT '',
                        narratorNotes TEXT NOT NULL DEFAULT '',
                        imagePath TEXT,
                        status TEXT NOT NULL DEFAULT 'ACTIVE',
                        type TEXT NOT NULL DEFAULT 'QUICK',
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL,
                        PRIMARY KEY(id),
                        FOREIGN KEY(chronicleId) REFERENCES chronicles(id) ON DELETE CASCADE
                    )
                """)
                db.execSQL("CREATE INDEX IF NOT EXISTS index_npcs_chronicleId ON npcs(chronicleId)")

                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS locations (
                        id TEXT NOT NULL,
                        chronicleId TEXT NOT NULL,
                        name TEXT NOT NULL,
                        typeId TEXT NOT NULL DEFAULT 'Generic Location',
                        description TEXT NOT NULL DEFAULT '',
                        districtOrArea TEXT NOT NULL DEFAULT '',
                        controllerEntityId TEXT,
                        factionId TEXT,
                        linkedNpcIds TEXT NOT NULL DEFAULT '',
                        linkedPlotIds TEXT NOT NULL DEFAULT '',
                        mediaAssetIds TEXT NOT NULL DEFAULT '',
                        narratorNotes TEXT NOT NULL DEFAULT '',
                        imagePath TEXT,
                        status TEXT NOT NULL DEFAULT 'ACTIVE',
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL,
                        PRIMARY KEY(id),
                        FOREIGN KEY(chronicleId) REFERENCES chronicles(id) ON DELETE CASCADE
                    )
                """)
                db.execSQL("CREATE INDEX IF NOT EXISTS index_locations_chronicleId ON locations(chronicleId)")

                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS factions (
                        id TEXT NOT NULL,
                        chronicleId TEXT NOT NULL,
                        name TEXT NOT NULL,
                        typeId TEXT,
                        sectId TEXT,
                        description TEXT NOT NULL DEFAULT '',
                        leaderEntityId TEXT,
                        memberIds TEXT NOT NULL DEFAULT '',
                        objectives TEXT NOT NULL DEFAULT '',
                        allyFactionIds TEXT NOT NULL DEFAULT '',
                        enemyFactionIds TEXT NOT NULL DEFAULT '',
                        locationIds TEXT NOT NULL DEFAULT '',
                        narratorNotes TEXT NOT NULL DEFAULT '',
                        imagePath TEXT,
                        status TEXT NOT NULL DEFAULT 'ACTIVE',
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL,
                        PRIMARY KEY(id),
                        FOREIGN KEY(chronicleId) REFERENCES chronicles(id) ON DELETE CASCADE
                    )
                """)
                db.execSQL("CREATE INDEX IF NOT EXISTS index_factions_chronicleId ON factions(chronicleId)")

                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS relationships (
                        id TEXT NOT NULL,
                        chronicleId TEXT NOT NULL,
                        fromEntityId TEXT NOT NULL,
                        fromEntityType TEXT NOT NULL,
                        toEntityId TEXT NOT NULL,
                        toEntityType TEXT NOT NULL,
                        typeId TEXT NOT NULL DEFAULT '',
                        direction TEXT NOT NULL DEFAULT 'DIRECTED',
                        description TEXT NOT NULL DEFAULT '',
                        strength INTEGER,
                        visibility TEXT NOT NULL DEFAULT 'PUBLIC',
                        secret INTEGER NOT NULL DEFAULT 0,
                        status TEXT NOT NULL DEFAULT 'ACTIVE',
                        notes TEXT NOT NULL DEFAULT '',
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL,
                        PRIMARY KEY(id),
                        FOREIGN KEY(chronicleId) REFERENCES chronicles(id) ON DELETE CASCADE
                    )
                """)
                db.execSQL("CREATE INDEX IF NOT EXISTS index_relationships_chronicleId ON relationships(chronicleId)")

                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS plot_arcs (
                        id TEXT NOT NULL,
                        chronicleId TEXT NOT NULL,
                        title TEXT NOT NULL,
                        summary TEXT NOT NULL DEFAULT '',
                        type TEXT NOT NULL DEFAULT 'MAIN',
                        status TEXT NOT NULL DEFAULT 'PLANNED',
                        themeIds TEXT NOT NULL DEFAULT '',
                        characterIds TEXT NOT NULL DEFAULT '',
                        npcIds TEXT NOT NULL DEFAULT '',
                        locationIds TEXT NOT NULL DEFAULT '',
                        startingSituation TEXT NOT NULL DEFAULT '',
                        possibleDevelopments TEXT NOT NULL DEFAULT '',
                        possibleClimax TEXT NOT NULL DEFAULT '',
                        resolutionNotes TEXT NOT NULL DEFAULT '',
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL,
                        PRIMARY KEY(id),
                        FOREIGN KEY(chronicleId) REFERENCES chronicles(id) ON DELETE CASCADE
                    )
                """)
                db.execSQL("CREATE INDEX IF NOT EXISTS index_plot_arcs_chronicleId ON plot_arcs(chronicleId)")

                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS scenes (
                        id TEXT NOT NULL,
                        chronicleId TEXT NOT NULL,
                        storyId TEXT,
                        sessionId TEXT,
                        title TEXT NOT NULL,
                        locationId TEXT,
                        participantIds TEXT NOT NULL DEFAULT '',
                        hook TEXT,
                        objective TEXT,
                        conflict TEXT,
                        mood TEXT,
                        description TEXT,
                        clueIds TEXT NOT NULL DEFAULT '',
                        secretIds TEXT NOT NULL DEFAULT '',
                        possibleComplications TEXT NOT NULL DEFAULT '',
                        mediaAssetIds TEXT NOT NULL DEFAULT '',
                        outcome TEXT,
                        status TEXT NOT NULL DEFAULT 'PLANNED',
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL,
                        PRIMARY KEY(id),
                        FOREIGN KEY(chronicleId) REFERENCES chronicles(id) ON DELETE CASCADE
                    )
                """)
                db.execSQL("CREATE INDEX IF NOT EXISTS index_scenes_chronicleId ON scenes(chronicleId)")

                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS secrets (
                        id TEXT NOT NULL,
                        chronicleId TEXT NOT NULL,
                        title TEXT NOT NULL,
                        content TEXT NOT NULL DEFAULT '',
                        linkedEntityIds TEXT NOT NULL DEFAULT '',
                        visibility TEXT NOT NULL DEFAULT 'GM_ONLY',
                        status TEXT NOT NULL DEFAULT 'HIDDEN',
                        revealedAtEventId TEXT,
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL,
                        PRIMARY KEY(id),
                        FOREIGN KEY(chronicleId) REFERENCES chronicles(id) ON DELETE CASCADE
                    )
                """)
                db.execSQL("CREATE INDEX IF NOT EXISTS index_secrets_chronicleId ON secrets(chronicleId)")

                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS clues (
                        id TEXT NOT NULL,
                        chronicleId TEXT NOT NULL,
                        title TEXT NOT NULL,
                        content TEXT,
                        mediaAssetId TEXT,
                        linkedSecretIds TEXT NOT NULL DEFAULT '',
                        status TEXT NOT NULL DEFAULT 'UNKNOWN',
                        discoveredAtEventId TEXT,
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL,
                        PRIMARY KEY(id),
                        FOREIGN KEY(chronicleId) REFERENCES chronicles(id) ON DELETE CASCADE
                    )
                """)
                db.execSQL("CREATE INDEX IF NOT EXISTS index_clues_chronicleId ON clues(chronicleId)")

                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS events (
                        id TEXT NOT NULL,
                        chronicleId TEXT NOT NULL,
                        sessionId TEXT,
                        sceneId TEXT,
                        timestamp INTEGER NOT NULL,
                        inGameTime TEXT,
                        typeId TEXT NOT NULL DEFAULT 'GENERAL',
                        title TEXT NOT NULL,
                        description TEXT,
                        involvedEntityIds TEXT NOT NULL DEFAULT '',
                        consequenceNotes TEXT NOT NULL DEFAULT '',
                        visibility TEXT NOT NULL DEFAULT 'GM_ONLY',
                        imagePath TEXT,
                        mediaAssetIds TEXT NOT NULL DEFAULT '',
                        createdAt INTEGER NOT NULL,
                        PRIMARY KEY(id),
                        FOREIGN KEY(chronicleId) REFERENCES chronicles(id) ON DELETE CASCADE
                    )
                """)
                db.execSQL("CREATE INDEX IF NOT EXISTS index_events_chronicleId ON events(chronicleId)")

                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS boons (
                        id TEXT NOT NULL,
                        chronicleId TEXT NOT NULL,
                        creditorEntityId TEXT NOT NULL,
                        debtorEntityId TEXT NOT NULL,
                        typeId TEXT,
                        description TEXT NOT NULL DEFAULT '',
                        status TEXT NOT NULL DEFAULT 'OPEN',
                        witnessedBy TEXT NOT NULL DEFAULT '',
                        visibility TEXT NOT NULL DEFAULT 'GM_ONLY',
                        narratorNotes TEXT NOT NULL DEFAULT '',
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL,
                        PRIMARY KEY(id),
                        FOREIGN KEY(chronicleId) REFERENCES chronicles(id) ON DELETE CASCADE
                    )
                """)
                db.execSQL("CREATE INDEX IF NOT EXISTS index_boons_chronicleId ON boons(chronicleId)")
            }
        }

        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS media_assets (
                        id TEXT NOT NULL,
                        chronicleId TEXT NOT NULL,
                        type TEXT NOT NULL DEFAULT 'OTHER',
                        title TEXT NOT NULL,
                        description TEXT NOT NULL DEFAULT '',
                        originalFilePath TEXT NOT NULL,
                        thumbnailFilePath TEXT,
                        width INTEGER NOT NULL DEFAULT 0,
                        height INTEGER NOT NULL DEFAULT 0,
                        tags TEXT NOT NULL DEFAULT '',
                        linkedEntityIds TEXT NOT NULL DEFAULT '',
                        visibility TEXT NOT NULL DEFAULT 'GM_ONLY',
                        createdAt INTEGER NOT NULL,
                        modifiedAt INTEGER NOT NULL,
                        PRIMARY KEY(id),
                        FOREIGN KEY(chronicleId) REFERENCES chronicles(id) ON DELETE CASCADE
                    )
                """)
                db.execSQL("CREATE INDEX IF NOT EXISTS index_media_assets_chronicleId ON media_assets(chronicleId)")

                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS image_documents (
                        id TEXT NOT NULL,
                        mediaAssetId TEXT NOT NULL,
                        currentRevisionId TEXT,
                        zoomDefaults TEXT NOT NULL DEFAULT '',
                        createdAt INTEGER NOT NULL,
                        modifiedAt INTEGER NOT NULL,
                        PRIMARY KEY(id),
                        FOREIGN KEY(mediaAssetId) REFERENCES media_assets(id) ON DELETE CASCADE
                    )
                """)
                db.execSQL("CREATE INDEX IF NOT EXISTS index_image_documents_mediaAssetId ON image_documents(mediaAssetId)")

                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS image_layers (
                        id TEXT NOT NULL,
                        imageDocumentId TEXT NOT NULL,
                        name TEXT NOT NULL,
                        visible INTEGER NOT NULL DEFAULT 1,
                        visibility TEXT NOT NULL DEFAULT 'GM_ONLY',
                        locked INTEGER NOT NULL DEFAULT 0,
                        `order` INTEGER NOT NULL DEFAULT 0,
                        createdAt INTEGER NOT NULL,
                        PRIMARY KEY(id),
                        FOREIGN KEY(imageDocumentId) REFERENCES image_documents(id) ON DELETE CASCADE
                    )
                """)
                db.execSQL("CREATE INDEX IF NOT EXISTS index_image_layers_imageDocumentId ON image_layers(imageDocumentId)")

                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS image_annotations (
                        id TEXT NOT NULL,
                        layerId TEXT NOT NULL,
                        imageDocumentId TEXT NOT NULL,
                        type TEXT NOT NULL,
                        geometryJson TEXT NOT NULL DEFAULT '{}',
                        styleJson TEXT NOT NULL DEFAULT '{}',
                        text TEXT,
                        pinType TEXT,
                        linkedEntityId TEXT,
                        linkedEntityType TEXT,
                        visibility TEXT NOT NULL DEFAULT 'GM_ONLY',
                        createdAt INTEGER NOT NULL,
                        modifiedAt INTEGER NOT NULL,
                        PRIMARY KEY(id),
                        FOREIGN KEY(imageDocumentId) REFERENCES image_documents(id) ON DELETE CASCADE
                    )
                """)
                db.execSQL("CREATE INDEX IF NOT EXISTS index_image_annotations_imageDocumentId ON image_annotations(imageDocumentId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_image_annotations_layerId ON image_annotations(layerId)")

                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS image_revisions (
                        id TEXT NOT NULL,
                        imageDocumentId TEXT NOT NULL,
                        mediaAssetId TEXT NOT NULL,
                        revisionNumber INTEGER NOT NULL,
                        createdAt INTEGER NOT NULL,
                        sessionId TEXT,
                        description TEXT,
                        annotationSnapshot TEXT NOT NULL DEFAULT '[]',
                        layerSnapshot TEXT NOT NULL DEFAULT '[]',
                        PRIMARY KEY(id),
                        FOREIGN KEY(imageDocumentId) REFERENCES image_documents(id) ON DELETE CASCADE
                    )
                """)
                db.execSQL("CREATE INDEX IF NOT EXISTS index_image_revisions_imageDocumentId ON image_revisions(imageDocumentId)")
            }
        }

        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE sessions ADD COLUMN status TEXT NOT NULL DEFAULT 'PLANNED'")
                db.execSQL("ALTER TABLE sessions ADD COLUMN realStartDateTime INTEGER")
                db.execSQL("ALTER TABLE sessions ADD COLUMN realEndDateTime INTEGER")
                db.execSQL("ALTER TABLE sessions ADD COLUMN inGameDate TEXT")
                db.execSQL("ALTER TABLE sessions ADD COLUMN activeSceneId TEXT")
                db.execSQL("ALTER TABLE sessions ADD COLUMN plannedSceneIds TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE sessions ADD COLUMN participantCharacterIds TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE sessions ADD COLUMN preparationNotes TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE sessions ADD COLUMN liveNotes TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE sessions ADD COLUMN recap TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE sessions ADD COLUMN xpAwarded INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE sessions ADD COLUMN unresolvedThreadIds TEXT NOT NULL DEFAULT ''")

                db.execSQL("ALTER TABLE scenes ADD COLUMN `order` INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE scenes ADD COLUMN npcIds TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE scenes ADD COLUMN notes TEXT NOT NULL DEFAULT ''")

                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS quick_notes (
                        id TEXT NOT NULL,
                        chronicleId TEXT NOT NULL,
                        scopeType TEXT NOT NULL DEFAULT 'QUICK',
                        scopeId TEXT,
                        text TEXT NOT NULL,
                        visibility TEXT NOT NULL DEFAULT 'GM_ONLY',
                        createdAt INTEGER NOT NULL,
                        modifiedAt INTEGER NOT NULL,
                        PRIMARY KEY(id),
                        FOREIGN KEY(chronicleId) REFERENCES chronicles(id) ON DELETE CASCADE
                    )
                """)
                db.execSQL("CREATE INDEX IF NOT EXISTS index_quick_notes_chronicleId ON quick_notes(chronicleId)")

                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS session_events (
                        id TEXT NOT NULL,
                        chronicleId TEXT NOT NULL,
                        sessionId TEXT,
                        sceneId TEXT,
                        timestamp INTEGER NOT NULL,
                        type TEXT NOT NULL,
                        title TEXT NOT NULL,
                        description TEXT,
                        entityRefs TEXT NOT NULL DEFAULT '',
                        visibility TEXT NOT NULL DEFAULT 'GM_ONLY',
                        metadata TEXT,
                        origin TEXT NOT NULL DEFAULT 'MANUAL',
                        createdAt INTEGER NOT NULL,
                        PRIMARY KEY(id),
                        FOREIGN KEY(chronicleId) REFERENCES chronicles(id) ON DELETE CASCADE
                    )
                """)
                db.execSQL("CREATE INDEX IF NOT EXISTS index_session_events_chronicleId ON session_events(chronicleId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_session_events_sessionId ON session_events(sessionId)")
            }
        }

        val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE npcs ADD COLUMN characterId TEXT")
            }
        }

        val MIGRATION_8_9 = object : Migration(8, 9) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE INDEX IF NOT EXISTS index_image_annotations_layerId ON image_annotations(layerId)")
            }
        }

        val MIGRATION_9_10 = object : Migration(9, 10) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS audio_tracks (
                        id TEXT NOT NULL,
                        chronicleId TEXT NOT NULL,
                        title TEXT NOT NULL,
                        filePath TEXT NOT NULL,
                        category TEXT NOT NULL DEFAULT 'CUSTOM',
                        isLooping INTEGER NOT NULL DEFAULT 1,
                        volume REAL NOT NULL DEFAULT 0.7,
                        isActive INTEGER NOT NULL DEFAULT 0,
                        createdAt INTEGER NOT NULL,
                        PRIMARY KEY(id),
                        FOREIGN KEY(chronicleId) REFERENCES chronicles(id) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                """.trimIndent())
                db.execSQL("CREATE INDEX IF NOT EXISTS index_audio_tracks_chronicleId ON audio_tracks(chronicleId)")
            }
        }

        val MIGRATION_10_11 = object : Migration(10, 11) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS audio_presets (
                        id TEXT NOT NULL,
                        chronicleId TEXT NOT NULL,
                        name TEXT NOT NULL,
                        tracksJson TEXT NOT NULL DEFAULT '[]',
                        createdAt INTEGER NOT NULL,
                        PRIMARY KEY(id),
                        FOREIGN KEY(chronicleId) REFERENCES chronicles(id) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                """.trimIndent())
                db.execSQL("CREATE INDEX IF NOT EXISTS index_audio_presets_chronicleId ON audio_presets(chronicleId)")
            }
        }

        val MIGRATION_11_12 = object : Migration(11, 12) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS house_rules (
                        chronicleId TEXT NOT NULL,
                        rulesJson TEXT NOT NULL,
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL,
                        PRIMARY KEY(chronicleId)
                    )
                """.trimIndent())
            }
        }

        fun getDatabase(context: Context): V20Database {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    V20Database::class.java,
                    "v20_database"
                )
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9, MIGRATION_9_10, MIGRATION_10_11, MIGRATION_11_12)
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
