package com.v20charactermanager.data.local.dao

import androidx.room.*
import com.v20charactermanager.data.local.entity.HouseRuleEntity

@Dao
interface HouseRuleDao {
    @Query("SELECT * FROM house_rules WHERE chronicleId = :chronicleId")
    suspend fun getByChronicleId(chronicleId: String): HouseRuleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: HouseRuleEntity)

    @Delete
    suspend fun delete(entity: HouseRuleEntity)

    @Query("DELETE FROM house_rules WHERE chronicleId = :chronicleId")
    suspend fun deleteByChronicleId(chronicleId: String)
}
