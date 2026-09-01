package com.v20charactermanager.data.local.dao

import androidx.room.*
import com.v20charactermanager.data.local.entity.BoonEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BoonDao {
    @Query("SELECT * FROM boons WHERE chronicleId = :chronicleId ORDER BY createdAt DESC")
    fun getBoonsByChronicle(chronicleId: String): Flow<List<BoonEntity>>

    @Query("SELECT * FROM boons WHERE id = :id")
    suspend fun getBoonById(id: String): BoonEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBoon(boon: BoonEntity)

    @Update
    suspend fun updateBoon(boon: BoonEntity)

    @Query("DELETE FROM boons WHERE id = :id")
    suspend fun deleteBoon(id: String)

    @Query("DELETE FROM boons WHERE chronicleId = :chronicleId")
    suspend fun deleteAllBoonsByChronicle(chronicleId: String)
}
