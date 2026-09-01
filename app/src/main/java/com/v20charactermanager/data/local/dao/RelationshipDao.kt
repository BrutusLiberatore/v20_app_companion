package com.v20charactermanager.data.local.dao

import androidx.room.*
import com.v20charactermanager.data.local.entity.RelationshipEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RelationshipDao {
    @Query("SELECT * FROM relationships WHERE chronicleId = :chronicleId")
    fun getRelationshipsByChronicle(chronicleId: String): Flow<List<RelationshipEntity>>

    @Query("SELECT * FROM relationships WHERE id = :id")
    suspend fun getRelationshipById(id: String): RelationshipEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRelationship(relationship: RelationshipEntity)

    @Update
    suspend fun updateRelationship(relationship: RelationshipEntity)

    @Query("DELETE FROM relationships WHERE id = :id")
    suspend fun deleteRelationship(id: String)

    @Query("DELETE FROM relationships WHERE chronicleId = :chronicleId")
    suspend fun deleteAllRelationshipsByChronicle(chronicleId: String)
}
