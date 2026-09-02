package com.v20charactermanager.data.di

import android.content.Context
import com.v20charactermanager.data.datastore.SettingsDataStore
import com.v20charactermanager.data.local.V20Database
import com.v20charactermanager.data.repository.AudioRepositoryImpl
import com.v20charactermanager.data.repository.CharacterRepositoryImpl
import com.v20charactermanager.data.repository.ChronicleRepositoryImpl
import com.v20charactermanager.data.repository.MediaRepositoryImpl
import com.v20charactermanager.data.repository.RuleRepositoryImpl
import com.v20charactermanager.data.repository.SettingsRepositoryImpl
import com.v20charactermanager.domain.repository.CharacterRepository
import com.v20charactermanager.domain.repository.ChronicleRepository
import com.v20charactermanager.domain.repository.MediaRepository
import com.v20charactermanager.domain.repository.RuleRepository
import com.v20charactermanager.domain.repository.SettingsRepository

class AppContainer(private val context: Context) {

    private val database: V20Database by lazy {
        V20Database.getDatabase(context)
    }

    private val characterDao by lazy {
        database.characterDao()
    }

    private val chronicleDao by lazy {
        database.chronicleDao()
    }

    private val chronicleMemberDao by lazy {
        database.chronicleMemberDao()
    }

    private val sessionDao by lazy {
        database.sessionDao()
    }

    private val chronicleNoteDao by lazy {
        database.chronicleNoteDao()
    }

    private val chronicleCharacterNoteDao by lazy {
        database.chronicleCharacterNoteDao()
    }

    private val npcDao by lazy {
        database.npcDao()
    }

    private val locationDao by lazy {
        database.locationDao()
    }

    private val factionDao by lazy {
        database.factionDao()
    }

    private val relationshipDao by lazy {
        database.relationshipDao()
    }

    private val plotArcDao by lazy {
        database.plotArcDao()
    }

    private val sceneDao by lazy {
        database.sceneDao()
    }

    private val secretDao by lazy {
        database.secretDao()
    }

    private val clueDao by lazy {
        database.clueDao()
    }

    private val eventDao by lazy {
        database.eventDao()
    }

    private val boonDao by lazy {
        database.boonDao()
    }

    private val mediaAssetDao by lazy {
        database.mediaAssetDao()
    }

    private val imageDocumentDao by lazy {
        database.imageDocumentDao()
    }

    private val imageLayerDao by lazy {
        database.imageLayerDao()
    }

    private val imageAnnotationDao by lazy {
        database.imageAnnotationDao()
    }

    private val imageRevisionDao by lazy {
        database.imageRevisionDao()
    }

    private val quickNoteDao by lazy {
        database.quickNoteDao()
    }

    private val sessionEventDao by lazy {
        database.sessionEventDao()
    }

    private val audioTrackDao by lazy {
        database.audioTrackDao()
    }

    private val settingsDataStore by lazy {
        SettingsDataStore(context)
    }

    val characterRepository: CharacterRepository by lazy {
        CharacterRepositoryImpl(characterDao)
    }

    val chronicleRepository: ChronicleRepository by lazy {
        ChronicleRepositoryImpl(
            chronicleDao, chronicleMemberDao, sessionDao,
            chronicleNoteDao, chronicleCharacterNoteDao,
            npcDao, locationDao, factionDao, relationshipDao,
            plotArcDao, sceneDao, secretDao, clueDao, eventDao, boonDao,
            quickNoteDao, sessionEventDao
        )
    }

    val settingsRepository: SettingsRepository by lazy {
        SettingsRepositoryImpl(settingsDataStore)
    }

    val mediaRepository: MediaRepository by lazy {
        MediaRepositoryImpl(
            mediaAssetDao, imageDocumentDao, imageLayerDao,
            imageAnnotationDao, imageRevisionDao
        )
    }

    val audioRepository: AudioRepositoryImpl by lazy {
        AudioRepositoryImpl(audioTrackDao)
    }

    val ruleRepository: RuleRepository by lazy {
        RuleRepositoryImpl()
    }
}
