# V20 — Dati Clan per Character Creator

**Scopo:** catalogo operativo per il tool di creazione PG. La struttura distingue regole V20 Core, suggerimenti di creazione, effetti meccanici del Clan e profili configurabili del progetto.

## 1. Regola fondamentale di modellazione

In V20 il Clan **non è una classe** e normalmente non assegna bonus automatici ad Attributi o Abilità. I paragrafi “Character Creation” dei Clan indicano tendenze e priorità consigliate; la meccanica generale resta 7/5/3 per gli Attributi e 13/9/5 per le Abilità. Il Clan interviene soprattutto su Discipline di Clan, Debolezza, eventuali scelte obbligatorie e rare eccezioni meccaniche (es. Nosferatu). Setta e Clan devono restare due dati distinti.

## 2. Profilo V20 Core verificato

| Voce | Valore |
|---|---:|
| Attributi | 7 / 5 / 3 (+1 base a ciascuno, salvo eccezioni) |
| Abilità | 13 / 9 / 5; max 3 nella fase |
| Discipline | 3 |
| Background | 5 |
| Virtù | 7 (+1 base a ciascuna Virtù attiva) |
| Punti Liberi | 15 |
| Pregi/Difetti opzionali | max 7 punti |
| Costo PL Attributo | 5 |
| Costo PL Abilità | 2 |
| Costo PL Disciplina | 7 |
| Costo PL Background | 1 |
| Costo PL Virtù | 2 |
| Costo PL Umanità/Sentiero | **2** |
| Costo PL Volontà | 1 |

**Opzione Core “More Inhuman Vampires”:** a discrezione del Narratore, 4 pallini Discipline iniziali **al posto dei pallini Background iniziali** (Background iniziali = 0). Questa opzione non va fusa con il profilo Sabbat personalizzato del progetto.

## 3. Dati divisi per Clan

### Assamiti (`assamite`)

- **Setta tipica/predefinita:** independent
- **Discipline di Clan:** celerity, obfuscate, quietus
- **Discipline firma:** quietus
- **Bonus automatici di creazione:** nessuno
- **Indicazioni di creazione (non bonus):** `{"attributes": ["physical", "social"], "abilities": ["talents", "skills"], "backgrounds": "Usually fewer extensive Backgrounds; Discipline specialization is common.", "morality": "Humanity requires attention; Clan-specific Paths may be appropriate only when allowed by the chronicle."}`
- **Debolezza — struttura:** `{"type": "kindred_blood_curse", "kindredBloodDamage": {"damageType": "lethal", "automaticLevelsPerBloodPoint": 1, "soakable": false}, "diablerie": {"damageType": "aggravated", "automaticLevelsPerVictimPermanentWillpower": 1, "benefitsIfSurvives": false}}`

### Brujah (`brujah`)

- **Setta tipica/predefinita:** camarilla
- **Discipline di Clan:** celerity, potence, presence
- **Discipline firma:** nessuna
- **Bonus automatici di creazione:** nessuno
- **Indicazioni di creazione (non bonus):** `{"attributes": ["physical"], "abilities": ["skills", "talents"], "backgrounds": ["contacts", "allies", "herd"], "note": "Mental and Social commonly follow Physical; Knowledges remain viable."}`
- **Debolezza — struttura:** `{"type": "frenzy", "resistOrRideFrenzyDifficultyModifier": 2, "canSpendWillpowerToAvoidFrenzy": false, "canSpendWillpowerToEndFrenzy": true}`

### Seguaci di Set (`followers_of_set`)

- **Setta tipica/predefinita:** independent
- **Discipline di Clan:** obfuscate, presence, serpentis
- **Discipline firma:** serpentis
- **Bonus automatici di creazione:** nessuno
- **Indicazioni di creazione (non bonus):** `{"attributes": ["social", "mental"], "abilities": ["knowledges", "talents"], "backgrounds": ["allies", "contacts", "influence", "resources", "retainers"]}`
- **Debolezza — struttura:** `{"type": "light_sensitivity", "sunlightAdditionalHealthLevels": 2, "brightLightDicePenalty": 1}`

### Gangrel (`gangrel`)

- **Setta tipica/predefinita:** camarilla
- **Discipline di Clan:** animalism, fortitude, protean
- **Discipline firma:** protean
- **Bonus automatici di creazione:** nessuno
- **Indicazioni di creazione (non bonus):** `{"attributes": ["physical"], "abilities": ["talents", "skills", "knowledges"], "backgrounds": "Often fewer Backgrounds; significant Resources, Influence and Retainers are uncommon in the core archetype."}`
- **Debolezza — struttura:** `{"type": "frenzy_animal_trait", "onEachFrenzy": "Gain a temporary animal physical or behavioral characteristic; Storyteller defines a meaningful effect.", "mayBecomePermanent": true}`

### Giovanni (`giovanni`)

- **Setta tipica/predefinita:** independent
- **Discipline di Clan:** dominate, necromancy, potence
- **Discipline firma:** necromancy
- **Bonus automatici di creazione:** nessuno
- **Indicazioni di creazione (non bonus):** `{"attributes": ["social", "mental", "physical"], "abilities": ["knowledges", "talents"], "backgrounds": "Characters often emphasize wealth/influence Backgrounds or concentrate on Disciplines rather than staying evenly distributed."}`
- **Debolezza — struttura:** `{"type": "painful_kiss", "appliesTo": "mortal_vessels", "healthDamagePerBloodPointTaken": 2}`
- **Setup Discipline speciali:** `{"necromancy": {"usesPaths": true, "usesRituals": true, "requiresPrimaryPath": true}}`

### Lasombra (`lasombra`)

- **Setta tipica/predefinita:** sabbat
- **Discipline di Clan:** dominate, obtenebration, potence
- **Discipline firma:** obtenebration
- **Bonus automatici di creazione:** nessuno
- **Indicazioni di creazione (non bonus):** `{"attributes": ["mental", "social"], "abilities": "No fixed category bonus; builds tend to be specialized.", "backgrounds": "Often diversified across several Backgrounds at modest starting ratings."}`
- **Debolezza — struttura:** `{"type": "no_reflection", "affectedSurfaces": ["mirrors", "water", "polished_reflective_surfaces", "rear_view_mirrors"], "note": "Do not automatically extend this V20 Core rule to all digital cameras/recordings unless the Chronicle RuleSet says so."}`

### Malkavian (`malkavian`)

- **Setta tipica/predefinita:** camarilla
- **Discipline di Clan:** auspex, dementation, obfuscate
- **Discipline firma:** dementation
- **Bonus automatici di creazione:** nessuno
- **Indicazioni di creazione (non bonus):** `{"attributes": ["mental"], "abilities": ["talents", "knowledges"], "backgrounds": "Highly variable; should follow concept."}`
- **Debolezza — struttura:** `{"type": "permanent_derangement", "incurableOriginalDerangement": true, "canSpendWillpowerToAmeliorateForScene": true, "canAcquireOtherDerangements": true}`
- **Scelte obbligatorie nel wizard:** `[{"id": "malkavian_derangement", "type": "derangement", "required": true, "lockedAsClanWeakness": true}]`

### Nosferatu (`nosferatu`)

- **Setta tipica/predefinita:** camarilla
- **Discipline di Clan:** animalism, obfuscate, potence
- **Discipline firma:** nessuna
- **Bonus automatici di creazione:** nessuno
- **Indicazioni di creazione (non bonus):** `{"attributes": ["physical", "mental"], "abilities": ["talents", "skills", "knowledges"], "backgrounds": ["allies", "contacts", "mentor", "influence"]}`
- **Debolezza — struttura:** `{"type": "appearance_zero", "attributeId": "appearance", "forcedValue": 0, "maxValue": 0, "canImprove": false}`
- **Effetti automatici in creazione:** `[{"operation": "set_and_lock", "traitId": "attribute.appearance", "value": 0, "refundCreationDots": false, "note": "Appearance is an exception to the normal rule that Attributes begin at 1."}]`

### Ravnos (`ravnos`)

- **Setta tipica/predefinita:** independent
- **Discipline di Clan:** animalism, chimerstry, fortitude
- **Discipline firma:** chimerstry
- **Bonus automatici di creazione:** nessuno
- **Indicazioni di creazione (non bonus):** `{"attributes": ["physical", "social"], "abilities": ["talents", "skills"], "backgrounds": ["resources", "domain", "allies", "contacts"]}`
- **Debolezza — struttura:** `{"type": "vice_compulsion", "resistTrait": "self_control_or_instinct", "difficulty": 6}`
- **Scelte obbligatorie nel wizard:** `[{"id": "ravnos_vice", "type": "free_text_or_enum", "required": true, "examples": ["lying", "cruelty", "theft"]}]`

### Toreador (`toreador`)

- **Setta tipica/predefinita:** camarilla
- **Discipline di Clan:** auspex, celerity, presence
- **Discipline firma:** nessuna
- **Bonus automatici di creazione:** nessuno
- **Indicazioni di creazione (non bonus):** `{"attributes": ["social"], "abilities": "Ability priority follows the chosen artistic/social focus.", "backgrounds": ["allies", "contacts", "resources", "domain", "haven", "mentor", "retainers"], "morality": "Virtues, Humanity/Path and Willpower are useful investments because the Clan weakness can remove agency in dangerous scenes."}`
- **Debolezza — struttura:** `{"type": "aesthetic_entrancement", "trigger": "something genuinely remarkable/beautiful", "resistTrait": "self_control_or_instinct", "difficulty": 6, "onFailure": "entranced_for_scene_or_until_stimulus_ends", "rerollIfWounded": true}`

### Tremere (`tremere`)

- **Setta tipica/predefinita:** camarilla
- **Discipline di Clan:** auspex, dominate, thaumaturgy
- **Discipline firma:** thaumaturgy
- **Bonus automatici di creazione:** nessuno
- **Indicazioni di creazione (non bonus):** `{"attributes": ["mental"], "abilities": ["knowledges"], "backgrounds": ["mentor", "status", "retainers"], "morality": "High Courage/Willpower is common but not mandatory."}`
- **Debolezza — struttura:** `{"type": "blood_bond_susceptibility", "drinksToFullBond": 2, "firstDrinkCountsAsStage": 2}`
- **Setup Discipline speciali:** `{"thaumaturgy": {"usesPaths": true, "usesRituals": true, "requiresPrimaryPath": true, "onFirstDot": {"primaryPathDots": 1, "levelOneRituals": 1}, "onThaumaturgyIncrease": "Increase primary path by one until path cap; later allocation follows RuleSet."}}`

### Tzimisce (`tzimisce`)

- **Setta tipica/predefinita:** sabbat
- **Discipline di Clan:** animalism, auspex, vicissitude
- **Discipline firma:** vicissitude
- **Bonus automatici di creazione:** nessuno
- **Indicazioni di creazione (non bonus):** `{"attributes": ["physical", "mental"], "abilities": ["knowledges", "skills"], "backgrounds": ["mentor", "allies", "domain", "retainers"], "morality": "Paths of Enlightenment are common in the archetype but remain a separate morality-system choice."}`
- **Debolezza — struttura:** `{"type": "native_soil_dependency", "minimumNativeSoil": "at least two handfuls", "onNightWithoutSoil": "halve all dice pools cumulatively, minimum 1 die", "recovery": "rest a full day with native soil"}`
- **Scelte obbligatorie nel wizard:** `[{"id": "native_soil_origin", "type": "free_text", "required": true, "description": "Place important to the character's mortal life/Embrace from which the soil originates."}]`

### Ventrue (`ventrue`)

- **Setta tipica/predefinita:** camarilla
- **Discipline di Clan:** dominate, fortitude, presence
- **Discipline firma:** nessuna
- **Bonus automatici di creazione:** nessuno
- **Indicazioni di creazione (non bonus):** `{"attributes": ["social", "mental"], "abilities": "Any category can be primary according to expertise.", "backgrounds": ["resources", "status", "herd", "domain", "haven"]}`
- **Debolezza — struttura:** `{"type": "feeding_restriction", "restrictionAppliesTo": "mortal_blood", "otherMortalOrAnimalBloodGrantsBloodPool": false, "vampiricBloodException": true, "choicePermanent": true}`
- **Scelte obbligatorie nel wizard:** `[{"id": "ventrue_feeding_restriction", "type": "free_text_or_tagged_rule", "required": true, "permanent": true}]`

### Caitiff / Senza Clan (`caitiff`)

- **Setta tipica/predefinita:** nessuna; selezionabile separatamente
- **Discipline di Clan:** nessuna lista di Clan
- **Discipline firma:** nessuna
- **Bonus automatici di creazione:** nessuno
- **Indicazioni di creazione (non bonus):** `{"attributes": "No clan preference.", "abilities": "No clan preference.", "backgrounds": "No core-clan preference; chronicle/supplement restrictions should be separate rule modules."}`
- **Debolezza — struttura:** `{"type": "clanless", "coreNote": "No inherited Clan Disciplines or Clan weakness in the V20 Core presentation; social consequences and supplemental rules should be modular."}`
- **Regole Discipline:** `{"initialSelection": "unrestricted_subject_to_storyteller", "newDisciplineXp": 10, "increaseMultiplierCurrentRating": 6}`

## 4. Regole di implementazione consigliate

1. `ClanDefinition` contiene identità, Discipline di Clan, Discipline firma, debolezza, suggerimenti e scelte richieste.
2. `CreationProfile` appartiene a Setta/cronaca e contiene i budget di punti.
3. `CharacterCreationValidator` applica solo effetti meccanici reali, non i suggerimenti narrativi.
4. Le scelte obbligatorie vanno richieste nello step Clan: `malkavian_derangement`, `ravnos_vice`, `ventrue_feeding_restriction`, `native_soil_origin`.
5. Nosferatu: `Appearance = 0`, bloccato; non va concesso alcun rimborso perché non è una spesa rimossa, è l’eccezione del Clan al valore base.
6. Tremere/Giovanni: il modello Disciplina deve supportare `paths` e `rituals`; Thaumaturgy richiede il percorso primario e assegna il relativo setup al primo pallino. Per Necromanzia il percorso primario cresce automaticamente con la Disciplina; di norma si parte dalla Via del Sepolcro, servono almeno 3 livelli nel percorso primario prima di un secondo percorso e il primario va padroneggiato prima di un terzo.
7. Caitiff/Pander: separare quantità di pallini iniziali (profilo Setta) da disponibilità delle Discipline (ClanStatus) e costo XP.

## 5. Correzioni da applicare alla specifica attuale del progetto

- Nel RuleSet V20 Core impostare `freebieCosts.humanityOrPath = 2`. Se si vuole mantenere il precedente valore 1, deve essere una house rule esplicita.
- Mantenere `creation.sabbat = 4 Discipline / 5 Virtù` come **profilo del progetto**, non come valore universale del Core.
- Aggiungere anche `v20.standard` e l’opzione `moreInhumanVampires` separata.
- Non dedurre `sectId` da `clanId`: il Clan può solo suggerire la Setta iniziale.

## 6. Fonti e perimetro

Riferimenti verificati: **Vampire: The Masquerade 20th Anniversary Edition Core**, in particolare capitoli Clan (pp. 48–73), creazione (pp. 79–86), XP (p. 124) e Discipline; più **V20_Addendum_Regole_Agent v1.1** per le regole configurabili specifiche del progetto. Le descrizioni sono parafrasate e strutturate per uso software, non riprodotte dal manuale.
