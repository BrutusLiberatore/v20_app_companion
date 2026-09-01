package com.v20charactermanager.data.repository

import com.v20charactermanager.data.local.entity.*
import com.v20charactermanager.domain.model.*

fun ChronicleEntity.toDomain() = Chronicle(
    id = id, name = name, description = description,
    storytellerName = storytellerName, userRole = ChronicleUserRole.valueOf(userRole),
    createdAt = createdAt, updatedAt = updatedAt
)

fun Chronicle.toEntity() = ChronicleEntity(
    id = id, name = name, description = description,
    storytellerName = storytellerName, userRole = userRole.name,
    createdAt = createdAt, updatedAt = updatedAt
)

fun ChronicleMemberEntity.toDomain() = ChronicleMember(
    id = id, chronicleId = chronicleId, characterId = characterId,
    role = ChronicleMemberRole.valueOf(role), createdAt = createdAt
)

fun SessionEntity.toDomain() = Session(
    id = id, chronicleId = chronicleId, number = number, title = title,
    date = date, notes = notes, createdAt = createdAt, updatedAt = updatedAt
)

fun Session.toEntity() = SessionEntity(
    id = id, chronicleId = chronicleId, number = number, title = title,
    date = date, notes = notes, createdAt = createdAt, updatedAt = updatedAt
)

fun ChronicleNoteEntity.toDomain() = ChronicleNote(
    id = id, chronicleId = chronicleId, text = text,
    createdAt = createdAt, updatedAt = updatedAt
)

fun ChronicleNote.toEntity() = ChronicleNoteEntity(
    id = id, chronicleId = chronicleId, text = text,
    createdAt = createdAt, updatedAt = updatedAt
)

fun ChronicleCharacterNoteEntity.toDomain() = ChronicleCharacterNote(
    id = id, chronicleId = chronicleId, characterId = characterId,
    text = text, visibility = NoteVisibility.valueOf(visibility),
    createdAt = createdAt, updatedAt = updatedAt
)

fun ChronicleCharacterNote.toEntity() = ChronicleCharacterNoteEntity(
    id = id, chronicleId = chronicleId, characterId = characterId,
    text = text, visibility = visibility.name,
    createdAt = createdAt, updatedAt = updatedAt
)

fun NpcEntity.toDomain() = NpcEntry(
    id = id, chronicleId = chronicleId, name = name,
    portraitAssetId = portraitAssetId, creatureType = CreatureType.valueOf(creatureType),
    clanId = clanId, sectId = sectId, role = role, description = description,
    personality = personality, motivation = motivation, narratorNotes = narratorNotes,
    imagePath = imagePath,
    status = NpcStatus.valueOf(status), type = NpcType.valueOf(type),
    createdAt = createdAt, updatedAt = updatedAt
)

fun NpcEntry.toEntity() = NpcEntity(
    id = id, chronicleId = chronicleId, name = name,
    portraitAssetId = portraitAssetId, creatureType = creatureType.name,
    clanId = clanId, sectId = sectId, role = role, description = description,
    personality = personality, motivation = motivation, narratorNotes = narratorNotes,
    imagePath = imagePath,
    status = status.name, type = type.name,
    createdAt = createdAt, updatedAt = updatedAt
)

fun LocationEntity.toDomain() = ChronicleLocation(
    id = id, chronicleId = chronicleId, name = name, typeId = typeId,
    description = description, districtOrArea = districtOrArea,
    controllerEntityId = controllerEntityId, factionId = factionId,
    linkedNpcIds = linkedNpcIds.split(",").filter { it.isNotEmpty() },
    linkedPlotIds = linkedPlotIds.split(",").filter { it.isNotEmpty() },
    mediaAssetIds = mediaAssetIds.split(",").filter { it.isNotEmpty() },
    narratorNotes = narratorNotes, imagePath = imagePath,
    status = LocationStatus.valueOf(status),
    createdAt = createdAt, updatedAt = updatedAt
)

fun ChronicleLocation.toEntity() = LocationEntity(
    id = id, chronicleId = chronicleId, name = name, typeId = typeId,
    description = description, districtOrArea = districtOrArea,
    controllerEntityId = controllerEntityId, factionId = factionId,
    linkedNpcIds = linkedNpcIds.joinToString(","), linkedPlotIds = linkedPlotIds.joinToString(","),
    mediaAssetIds = mediaAssetIds.joinToString(","), narratorNotes = narratorNotes,
    imagePath = imagePath,
    status = status.name, createdAt = createdAt, updatedAt = updatedAt
)

fun FactionEntity.toDomain() = Faction(
    id = id, chronicleId = chronicleId, name = name, typeId = typeId,
    sectId = sectId, description = description, leaderEntityId = leaderEntityId,
    memberIds = memberIds.split(",").filter { it.isNotEmpty() },
    objectives = objectives.split("||").filter { it.isNotEmpty() },
    allyFactionIds = allyFactionIds.split(",").filter { it.isNotEmpty() },
    enemyFactionIds = enemyFactionIds.split(",").filter { it.isNotEmpty() },
    locationIds = locationIds.split(",").filter { it.isNotEmpty() },
    narratorNotes = narratorNotes, imagePath = imagePath,
    status = FactionStatus.valueOf(status),
    createdAt = createdAt, updatedAt = updatedAt
)

fun Faction.toEntity() = FactionEntity(
    id = id, chronicleId = chronicleId, name = name, typeId = typeId,
    sectId = sectId, description = description, leaderEntityId = leaderEntityId,
    memberIds = memberIds.joinToString(","), objectives = objectives.joinToString("||"),
    allyFactionIds = allyFactionIds.joinToString(","), enemyFactionIds = enemyFactionIds.joinToString(","),
    locationIds = locationIds.joinToString(","), narratorNotes = narratorNotes,
    imagePath = imagePath,
    status = status.name, createdAt = createdAt, updatedAt = updatedAt
)

fun RelationshipEntity.toDomain() = Relationship(
    id = id, chronicleId = chronicleId,
    fromEntityId = fromEntityId, fromEntityType = fromEntityType,
    toEntityId = toEntityId, toEntityType = toEntityType,
    typeId = typeId, direction = RelationshipDirection.valueOf(direction),
    description = description, strength = strength,
    visibility = Visibility.valueOf(visibility), secret = secret,
    status = RelationshipStatus.valueOf(status), notes = notes,
    createdAt = createdAt, updatedAt = updatedAt
)

fun Relationship.toEntity() = RelationshipEntity(
    id = id, chronicleId = chronicleId,
    fromEntityId = fromEntityId, fromEntityType = fromEntityType,
    toEntityId = toEntityId, toEntityType = toEntityType,
    typeId = typeId, direction = direction.name,
    description = description, strength = strength,
    visibility = visibility.name, secret = secret,
    status = status.name, notes = notes,
    createdAt = createdAt, updatedAt = updatedAt
)

fun PlotArcEntity.toDomain() = PlotArc(
    id = id, chronicleId = chronicleId, title = title, summary = summary,
    type = PlotType.valueOf(type), status = PlotStatus.valueOf(status),
    themeIds = themeIds.split(",").filter { it.isNotEmpty() },
    characterIds = characterIds.split(",").filter { it.isNotEmpty() },
    npcIds = npcIds.split(",").filter { it.isNotEmpty() },
    locationIds = locationIds.split(",").filter { it.isNotEmpty() },
    startingSituation = startingSituation,
    possibleDevelopments = possibleDevelopments.split("||").filter { it.isNotEmpty() },
    possibleClimax = possibleClimax, resolutionNotes = resolutionNotes,
    createdAt = createdAt, updatedAt = updatedAt
)

fun PlotArc.toEntity() = PlotArcEntity(
    id = id, chronicleId = chronicleId, title = title, summary = summary,
    type = type.name, status = status.name,
    themeIds = themeIds.joinToString(","), characterIds = characterIds.joinToString(","),
    npcIds = npcIds.joinToString(","), locationIds = locationIds.joinToString(","),
    startingSituation = startingSituation,
    possibleDevelopments = possibleDevelopments.joinToString("||"),
    possibleClimax = possibleClimax, resolutionNotes = resolutionNotes,
    createdAt = createdAt, updatedAt = updatedAt
)

fun SceneEntity.toDomain() = ChronicleScene(
    id = id, chronicleId = chronicleId, storyId = storyId, sessionId = sessionId,
    title = title, locationId = locationId,
    participantIds = participantIds.split(",").filter { it.isNotEmpty() },
    hook = hook, objective = objective, conflict = conflict, mood = mood,
    description = description,
    clueIds = clueIds.split(",").filter { it.isNotEmpty() },
    secretIds = secretIds.split(",").filter { it.isNotEmpty() },
    possibleComplications = possibleComplications.split("||").filter { it.isNotEmpty() },
    mediaAssetIds = mediaAssetIds.split(",").filter { it.isNotEmpty() },
    outcome = outcome, status = SceneStatus.valueOf(status),
    createdAt = createdAt, updatedAt = updatedAt
)

fun ChronicleScene.toEntity() = SceneEntity(
    id = id, chronicleId = chronicleId, storyId = storyId, sessionId = sessionId,
    title = title, locationId = locationId,
    participantIds = participantIds.joinToString(","),
    hook = hook, objective = objective, conflict = conflict, mood = mood,
    description = description,
    clueIds = clueIds.joinToString(","), secretIds = secretIds.joinToString(","),
    possibleComplications = possibleComplications.joinToString("||"),
    mediaAssetIds = mediaAssetIds.joinToString(","),
    outcome = outcome, status = status.name,
    createdAt = createdAt, updatedAt = updatedAt
)

fun SecretEntity.toDomain() = Secret(
    id = id, chronicleId = chronicleId, title = title, content = content,
    linkedEntityIds = linkedEntityIds.split(",").filter { it.isNotEmpty() },
    visibility = Visibility.valueOf(visibility), status = SecretStatus.valueOf(status),
    revealedAtEventId = revealedAtEventId, createdAt = createdAt, updatedAt = updatedAt
)

fun Secret.toEntity() = SecretEntity(
    id = id, chronicleId = chronicleId, title = title, content = content,
    linkedEntityIds = linkedEntityIds.joinToString(","),
    visibility = visibility.name, status = status.name,
    revealedAtEventId = revealedAtEventId, createdAt = createdAt, updatedAt = updatedAt
)

fun ClueEntity.toDomain() = Clue(
    id = id, chronicleId = chronicleId, title = title, content = content,
    mediaAssetId = mediaAssetId,
    linkedSecretIds = linkedSecretIds.split(",").filter { it.isNotEmpty() },
    status = ClueStatus.valueOf(status), discoveredAtEventId = discoveredAtEventId,
    createdAt = createdAt, updatedAt = updatedAt
)

fun Clue.toEntity() = ClueEntity(
    id = id, chronicleId = chronicleId, title = title, content = content,
    mediaAssetId = mediaAssetId,
    linkedSecretIds = linkedSecretIds.joinToString(","),
    status = status.name, discoveredAtEventId = discoveredAtEventId,
    createdAt = createdAt, updatedAt = updatedAt
)

fun EventEntity.toDomain() = ChronicleEvent(
    id = id, chronicleId = chronicleId, sessionId = sessionId, sceneId = sceneId,
    timestamp = timestamp, inGameTime = inGameTime, typeId = typeId,
    title = title, description = description,
    involvedEntityIds = involvedEntityIds.split(",").filter { it.isNotEmpty() },
    consequenceNotes = consequenceNotes.split("||").filter { it.isNotEmpty() },
    visibility = Visibility.valueOf(visibility),
    imagePath = imagePath,
    mediaAssetIds = mediaAssetIds.split(",").filter { it.isNotEmpty() },
    createdAt = createdAt
)

fun ChronicleEvent.toEntity() = EventEntity(
    id = id, chronicleId = chronicleId, sessionId = sessionId, sceneId = sceneId,
    timestamp = timestamp, inGameTime = inGameTime, typeId = typeId,
    title = title, description = description,
    involvedEntityIds = involvedEntityIds.joinToString(","),
    consequenceNotes = consequenceNotes.joinToString("||"),
    visibility = visibility.name,
    imagePath = imagePath,
    mediaAssetIds = mediaAssetIds.joinToString(","),
    createdAt = createdAt
)

fun BoonEntity.toDomain() = BoonRecord(
    id = id, chronicleId = chronicleId,
    creditorEntityId = creditorEntityId, debtorEntityId = debtorEntityId,
    typeId = typeId, description = description, status = BoonStatus.valueOf(status),
    witnessedBy = witnessedBy.split(",").filter { it.isNotEmpty() },
    visibility = Visibility.valueOf(visibility), narratorNotes = narratorNotes,
    createdAt = createdAt, updatedAt = updatedAt
)

fun BoonRecord.toEntity() = BoonEntity(
    id = id, chronicleId = chronicleId,
    creditorEntityId = creditorEntityId, debtorEntityId = debtorEntityId,
    typeId = typeId, description = description, status = status.name,
    witnessedBy = witnessedBy.joinToString(","),
    visibility = visibility.name, narratorNotes = narratorNotes,
    createdAt = createdAt, updatedAt = updatedAt
)
