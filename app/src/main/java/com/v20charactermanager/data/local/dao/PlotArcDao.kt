package com.v20charactermanager.data.local.dao

import androidx.room.*
import com.v20charactermanager.data.local.entity.PlotArcEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlotArcDao {
    @Query("SELECT * FROM plot_arcs WHERE chronicleId = :chronicleId ORDER BY createdAt ASC")
    fun getPlotArcsByChronicle(chronicleId: String): Flow<List<PlotArcEntity>>

    @Query("SELECT * FROM plot_arcs WHERE id = :id")
    suspend fun getPlotArcById(id: String): PlotArcEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlotArc(plotArc: PlotArcEntity)

    @Update
    suspend fun updatePlotArc(plotArc: PlotArcEntity)

    @Query("DELETE FROM plot_arcs WHERE id = :id")
    suspend fun deletePlotArc(id: String)

    @Query("DELETE FROM plot_arcs WHERE chronicleId = :chronicleId")
    suspend fun deleteAllPlotArcsByChronicle(chronicleId: String)
}
