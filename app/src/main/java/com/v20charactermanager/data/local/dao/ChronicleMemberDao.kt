package com.v20charactermanager.data.local.dao

import androidx.room.*
import com.v20charactermanager.data.local.entity.ChronicleMemberEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChronicleMemberDao {
    @Query("SELECT * FROM chronicle_members WHERE chronicleId = :chronicleId")
    fun getMembersByChronicle(chronicleId: String): Flow<List<ChronicleMemberEntity>>

    @Query("SELECT * FROM chronicle_members WHERE chronicleId = :chronicleId AND role = :role")
    fun getMembersByRole(chronicleId: String, role: String): Flow<List<ChronicleMemberEntity>>

    @Query("SELECT * FROM chronicle_members WHERE characterId = :characterId")
    fun getChroniclesForCharacter(characterId: String): Flow<List<ChronicleMemberEntity>>

    @Query("SELECT * FROM chronicle_members WHERE chronicleId = :chronicleId AND characterId = :characterId")
    suspend fun getMember(chronicleId: String, characterId: String): ChronicleMemberEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: ChronicleMemberEntity)

    @Update
    suspend fun updateMember(member: ChronicleMemberEntity)

    @Query("DELETE FROM chronicle_members WHERE id = :id")
    suspend fun deleteMember(id: String)

    @Query("DELETE FROM chronicle_members WHERE chronicleId = :chronicleId AND characterId = :characterId")
    suspend fun removeCharacterFromChronicle(chronicleId: String, characterId: String)
}
