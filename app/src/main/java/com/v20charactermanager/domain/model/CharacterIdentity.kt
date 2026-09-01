package com.v20charactermanager.domain.model

import com.v20charactermanager.domain.definition.ClanId
import com.v20charactermanager.domain.definition.DemeanorId
import com.v20charactermanager.domain.definition.NatureId
import com.v20charactermanager.domain.definition.SectId
import kotlinx.serialization.Serializable

@Serializable
data class CharacterIdentity(
    val name: String = "",
    val player: String = "",
    val chronicle: String = "",
    val profile: String = "",
    val clan: ClanId = ClanId.BRUAH,
    val sect: SectId = SectId.CAMARILLA,
    val generation: Int = 13,
    val nature: NatureId = NatureId.REBEL,
    val demeanor: DemeanorId = DemeanorId.CONFORMIST,
    val sire: String = "",
    val haven: String = "",
    val concept: String = ""
)
