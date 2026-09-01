package com.v20charactermanager.data.local.dao

import androidx.room.*
import com.v20charactermanager.data.local.entity.SecretEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SecretDao {
    @Query("SELECT * FROM secrets WHERE chronicleId = :chronicleId ORDER BY createdAt ASC")
    fun getSecretsByChronicle(chronicleId: String): Flow<List<SecretEntity>>

    @Query("SELECT * FROM secrets WHERE id = :id")
    suspend fun getSecretById(id: String): SecretEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSecret(secret: SecretEntity)

    @Update
    suspend fun updateSecret(secret: SecretEntity)

    @Query("DELETE FROM secrets WHERE id = :id")
    suspend fun deleteSecret(id: String)

    @Query("DELETE FROM secrets WHERE chronicleId = :chronicleId")
    suspend fun deleteAllSecretsByChronicle(chronicleId: String)
}
