package com.v20charactermanager.ui.compendium

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.v20charactermanager.domain.definition.*
import com.v20charactermanager.domain.repository.RuleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class CompendiumItem(
    val id: String,
    val nameIt: String,
    val nameEn: String,
    val descriptionIt: String,
    val descriptionEn: String,
    val category: String,
    val subCategoryIt: String? = null,
    val subCategoryEn: String? = null,
    val cost: Int? = null,
    val extraInfoIt: String? = null,
    val extraInfoEn: String? = null
)

enum class CompendiumCategory(val nameIt: String, val nameEn: String) {
    CLAN("Clan", "Clan"),
    DISCIPLINE("Disciplina", "Discipline"),
    BACKGROUND("Background", "Background"),
    MERIT("Vantaggio", "Merit"),
    FLAW("Difetto", "Flaw"),
    NATURE("Natura", "Nature"),
    DEMEANOR("Comportamento", "Demeanor")
}

data class CompendiumUiState(
    val categories: List<CompendiumCategory> = CompendiumCategory.entries,
    val selectedCategory: CompendiumCategory = CompendiumCategory.CLAN,
    val searchQuery: String = "",
    val items: List<CompendiumItem> = emptyList(),
    val filteredItems: List<CompendiumItem> = emptyList(),
    val selectedItem: CompendiumItem? = null
)

class CompendiumViewModel(
    private val ruleRepository: RuleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CompendiumUiState())
    val uiState: StateFlow<CompendiumUiState> = _uiState.asStateFlow()

    init {
        loadAllItems()
    }

    private fun loadAllItems() {
        val items = mutableListOf<CompendiumItem>()

        ruleRepository.getAllClanDefinitions().forEach { clan ->
            items.add(
                CompendiumItem(
                    id = clan.id,
                    nameIt = clan.nameIt,
                    nameEn = clan.nameEn,
                    descriptionIt = clan.weaknessIt,
                    descriptionEn = clan.weaknessEn,
                    category = CompendiumCategory.CLAN.name,
                    extraInfoIt = "Setta: ${clan.sect}\nDiscipline: ${clan.clanDisciplines.joinToString { it.nameIt }}",
                    extraInfoEn = "Sect: ${clan.sect}\nDisciplines: ${clan.clanDisciplines.joinToString { it.nameEn }}"
                )
            )
        }

        ruleRepository.getAllDisciplineDefinitions().forEach { discipline ->
            val (descIt, descEn) = getDisciplineDescription(discipline)
            items.add(
                CompendiumItem(
                    id = discipline.id,
                    nameIt = discipline.nameIt,
                    nameEn = discipline.nameEn,
                    descriptionIt = descIt,
                    descriptionEn = descEn,
                    category = CompendiumCategory.DISCIPLINE.name
                )
            )
        }

        ruleRepository.getAllBackgroundDefinitions().forEach { background ->
            val (descIt, descEn) = getBackgroundDescription(background)
            items.add(
                CompendiumItem(
                    id = background.id,
                    nameIt = background.nameIt,
                    nameEn = background.nameEn,
                    descriptionIt = descIt,
                    descriptionEn = descEn,
                    category = CompendiumCategory.BACKGROUND.name
                )
            )
        }

        ruleRepository.getAllMeritDefinitions().forEach { merit ->
            items.add(
                CompendiumItem(
                    id = merit.id,
                    nameIt = merit.nameIt,
                    nameEn = merit.nameEn,
                    descriptionIt = merit.descriptionIt,
                    descriptionEn = merit.descriptionEn,
                    category = CompendiumCategory.MERIT.name,
                    subCategoryIt = merit.category.nameIt,
                    subCategoryEn = merit.category.nameEn,
                    cost = merit.cost
                )
            )
        }

        ruleRepository.getAllFlawDefinitions().forEach { flaw ->
            items.add(
                CompendiumItem(
                    id = flaw.id,
                    nameIt = flaw.nameIt,
                    nameEn = flaw.nameEn,
                    descriptionIt = flaw.descriptionIt,
                    descriptionEn = flaw.descriptionEn,
                    category = CompendiumCategory.FLAW.name,
                    subCategoryIt = flaw.category.nameIt,
                    subCategoryEn = flaw.category.nameEn,
                    cost = flaw.cost
                )
            )
        }

        ruleRepository.getAllNatureDefinitions().forEach { nature ->
            val (descIt, descEn) = getNatureDescription(nature)
            items.add(
                CompendiumItem(
                    id = nature.id,
                    nameIt = nature.nameIt,
                    nameEn = nature.nameEn,
                    descriptionIt = descIt,
                    descriptionEn = descEn,
                    category = CompendiumCategory.NATURE.name
                )
            )
        }

        ruleRepository.getAllDemeanorDefinitions().forEach { demeanor ->
            val (descIt, descEn) = getDemeanorDescription(demeanor)
            items.add(
                CompendiumItem(
                    id = demeanor.id,
                    nameIt = demeanor.nameIt,
                    nameEn = demeanor.nameEn,
                    descriptionIt = descIt,
                    descriptionEn = descEn,
                    category = CompendiumCategory.DEMEANOR.name
                )
            )
        }

        _uiState.value = _uiState.value.copy(
            items = items,
            filteredItems = items.filter { it.category == CompendiumCategory.CLAN.name }
        )
    }

    fun selectCategory(category: CompendiumCategory) {
        _uiState.value = _uiState.value.copy(
            selectedCategory = category,
            searchQuery = ""
        )
        filterItems()
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        filterItems()
    }

    private fun filterItems() {
        val state = _uiState.value
        val categoryItems = state.items.filter { it.category == state.selectedCategory.name }
        val filtered = if (state.searchQuery.isBlank()) {
            categoryItems
        } else {
            categoryItems.filter { item ->
                item.nameIt.contains(state.searchQuery, ignoreCase = true) ||
                        item.nameEn.contains(state.searchQuery, ignoreCase = true) ||
                        item.descriptionIt.contains(state.searchQuery, ignoreCase = true) ||
                        item.descriptionEn.contains(state.searchQuery, ignoreCase = true)
            }
        }
        _uiState.value = state.copy(filteredItems = filtered)
    }

    fun selectItem(item: CompendiumItem) {
        _uiState.value = _uiState.value.copy(selectedItem = item)
    }

    fun clearSelection() {
        _uiState.value = _uiState.value.copy(selectedItem = null)
    }

    private fun getDisciplineDescription(discipline: DisciplineId): Pair<String, String> = when (discipline) {
        DisciplineId.ANIMALISM -> "Permette di comunicare con gli animali, comandarli e persino legare la propria anima a quella di una bestia. A livelli elevati, consente di assumere forme animalesche temporanee." to "Allows communication with animals, commanding them, and even binding one's soul to that of a beast. At higher levels, it permits temporary assumption of animal forms."
        DisciplineId.AUSPEX -> "Concede poteri di percezione soprannaturale: vista fluente, consapevolezza dei disastri, retrocognizione e la capacità di penetrare le illusioni e le menzogne." to "Grants supernatural perception powers:Aura Reading, Spirit's Touch, Psychometry, and the ability to pierce illusions and lies."
        DisciplineId.CELERITY -> "Concede velocità e riflessi sovrumani. Il vampiro può colpire e muoversi con una rapidità che supera ogni capacità mortale, fino a sembrare un'ombra." to "Grants supernatural speed and reflexes. The vampire can strike and move with rapidity that exceeds any mortal capacity, appearing as little more than a blur."
        DisciplineId.CHIMESTRY -> "Permette di creare illusioni che ingannano i sensi, dalla semplice distorsione sensoriale fino a realistiche figure illusorie e barriere di nebbia." to "Allows the creation of illusions that deceive the senses, from simple sensory distortions to realistic illusory figures and walls of fog."
        DisciplineId.DEMENTATION -> "Concede il potere di toccare la follia nell'animo altrui, causando paranoia, isteria e terrore. Può anche indurre euforia temporanea." to "Grants the power to touch the madness within others' souls, causing paranoia, hysteria, and terror. It can also induce temporary euphoria."
        DisciplineId.DOMINATE -> "Permette di imporre la propria volontà ad altri vampiri e mortali, cancellando ricordi, imponendo comandi e controllando le azioni." to "Allows the imposition of one's will upon other vampires and mortals, erasing memories, issuing commands, and controlling actions."
        DisciplineId.FORTITUDE -> "Concede una resistenza sovrumana ai danni fisici. Il vampiro può sopravvivere a ferite che ucciderebbero normalmente, resistere al fuoco e alla luce solare." to "Grants supernatural resilience to physical damage. The vampire can survive wounds that would normally be lethal, resist fire and sunlight."
        DisciplineId.NECROMANCY -> "Permette di comunicare con i morti, evocare spiriti, aprire portali verso il Regno della Morte e compiere rituali che violano la legge naturale." to "Allows communication with the dead, summoning spirits, opening portals to the Land of Death, and performing rituals that violate natural law."
        DisciplineId.OBFUSCATE -> "Concede il potere di nascondersi, rendersi invisibili e confondere le menti altrui. Il vampiro può passare inosservato anche in piena luce." to "Grants the power to hide, become invisible, and confuse others' minds. The vampire can go unnoticed even in broad daylight."
        DisciplineId.OBTENEBRATION -> "Permette di manipolare le tenebre come sostanza fisica, creando artigli d'ombra, armi, barriere e inghiottendo i nemici nell'oscurità." to "Allows manipulation of darkness as physical matter, creating shadow claws, weapons, barriers, and swallowing enemies into darkness."
        DisciplineId.POTENCE -> "Concede forza sovrumana. Il vampiro può devastare con un singolo colpo, sfondare muri e sollevare pesi impossibili per un mortale." to "Grants supernatural strength. The vampire can devastate with a single blow, smash through walls, and lift weights impossible for a mortal."
        DisciplineId.PRESENCE -> "Concede il potere di influenzare le emozioni altrui: amore, odio, terrore, adorazione. Può affascinare o terrorizzare intere folla." to "Grants the power to influence others' emotions: love, hatred, terror, adoration. It can charm or terrorize entire crowds."
        DisciplineId.PROTEAN -> "Permette di mutare il proprio corpo, assumendo forme animali, fondendosi con la terra, o generando artigli e denti d'acciaio." to "Allows the transformation of one's body, assuming animal forms, merging with the earth, or growing steel-like claws and fangs."
        DisciplineId.QUIETUS -> "Concede il potere di azzerare il proprio rapporto con il sangue, fermando il flusso vitale. Può anche uccidere con un morso silenzioso." to "Grants the power to zero out one's blood relationship, stopping the vital flow. It can also kill with a silent bite."
        DisciplineId.RESILIENCE -> "Concede una resistenza fisica superiore alla norma. Il vampiro guarisce più rapidamente e sopporta danni che abbatterebbero altri." to "Grants physical resilience beyond the norm. The vampire heals more quickly and endures damage that would fell others."
        DisciplineId.SERPENTIS -> "Permette di trasformare gli occhi in serpenti, immobilizzare con lo sguardo, generare veleno e persino rigenerare il corpo." to "Allows transforming eyes into serpents, immobilizing with a gaze, generating venom, and even regenerating the body."
        DisciplineId.THAUMATURGY -> "La magia del sangue: permette di manipolare il flusso vitale, creare fuoco, controllare il clima e compiere prodigi through rituali complessi." to "Blood magic: allows manipulation of the vital flow, creating fire, controlling weather, and performing wonders through complex rituals."
        DisciplineId.VICISSITUDE -> "Permette di manipolare la carne propria e altrui, deformando, scolpendo e trasformando i corpi in maniera orrorifica o artistica." to "Allows the manipulation of one's own flesh and that of others, deforming, sculpting, and transforming bodies in horrific or artistic ways."
    }

    private fun getBackgroundDescription(background: BackgroundId): Pair<String, String> = when (background) {
        BackgroundId.ALLIES -> "Alleati umani che aiutano il vampiro in cambio di favori. Possono essere amici, familiari o figure politiche. Livelli più alti significano alleati più potenti." to "Human allies who aid the vampire in exchange for favors. They can be friends, family, or political figures. Higher levels mean more powerful allies."
        BackgroundId.CONTACTS -> "Reti di informatori che forniscono notizie, informazioni e servizi. I contatti possono essere mortali o altri vampiri che condividono informazioni." to "Networks of informants who provide news, information, and services. Contacts can be mortals or other vampires who share information."
        BackgroundId.FAME -> "La reputazione del vampiro nel mondo della Notte. Una fama elevata apre porte ma attira anche attenzioni indesiderate." to "The vampire's reputation in the world of the Night. High fame opens doors but also attracts unwanted attention."
        BackgroundId.GENERATION -> "La vicinanza genealogica ai Patriarchi fondatori. Una generazione più bassa significa più potenza nel sangue e un Blood Pool maggiore." to "Proximity to the founding Patriarchs. A lower generation means more blood potency and a larger Blood Pool."
        BackgroundId.HERD -> "Un gruppo di mortali dal quale il vampiro si nutre regolarmente. Un gregge fedele garantisce nutrimento sicuro e discreto." to "A group of mortals from whom the vampire feeds regularly. A loyal herd ensures safe and discreet sustenance."
        BackgroundId.INFLUENCE -> "Potere politico e sociale nel mondo mortale. L'influenza permette di manipolare eventi, controllare organizzazioni e influenzare decisioni." to "Political and social power in the mortal world. Influence allows manipulation of events, control of organizations, and swaying of decisions."
        BackgroundId.MENTOR -> "Un vampiro più antico che guida e protegge il personaggio. Il mentore offre insegnamenti, connessioni e protezione, ma attende fedeltà." to "An older vampire who guides and protects the character. The mentor offers teachings, connections, and protection, but expects loyalty."
        BackgroundId.RESOURCES -> "Risorse finanziarie e materiali del vampiro. Possedere risorse significa avere accesso a beni, armi, veicoli e sistemi di supporto." to "The vampire's financial and material resources. Possessing resources means access to goods, weapons, vehicles, and support systems."
        BackgroundId.RETAINERS -> "Servitori fedeli: umanoidi ghouls, infermieri, autisti, o creature che obbediscono al vampiro. I seguaci eseguono compiti e proteggono il padrone." to "Faithful servants: human ghouls, nurses, drivers, or creatures who obey the vampire. Retainers carry out tasks and protect their master."
        BackgroundId.STATUS -> "Il rango e la posizione del vampiro nella gerarchia del suo clan o setta. Uno status elevato conferisce autorità e privilegi." to "The vampire's rank and position within the hierarchy of their clan or sect. High status confers authority and privileges."
    }

    private fun getNatureDescription(nature: NatureId): Pair<String, String> = when (nature) {
        NatureId.ALTRUIST -> "Si dedica al bene degli altri, mettendo i bisogni della comunità prima dei propri. Ottieni 1 punto Forza di Volontà quando compi un atto di sacrificio personale per il bene comune." to "Dedicates themselves to the good of others, putting community needs before their own. Gain 1 Willpower point when committing a personal sacrifice for the common good."
        NatureId.ARCHITECT -> "Crea per il gusto di creare: opere d'arte, strutture, organizzazioni. Vuole lasciare un segno duraturo nel mondo." to "Creates for the sake of creation: art, structures, organizations. Wants to leave a lasting mark on the world."
        NatureId.AUTOCRAT -> "Vuole avere il controllo totale su tutto e tutti. Non accetta di non essere al comando." to "Wants total control over everything and everyone. Does not accept not being in charge."
        NatureId.CHILD -> "È semplice, ingenuo e dipendente. Cerca protezione e cura dagli altri." to "Is simple, naive, and dependent. Seeks protection and care from others."
        NatureId.BON_VIVANT -> "Il motteggiatore, il buffone. Usa l'umorismo per coprire la propria natura oscura e per sopravvivere all'eternità." to "The jester, the fool. Uses humor to mask their dark nature and survive eternity."
        NatureId.BRAVO -> "Il dominatore. Vuole controllare gli altri attraverso la forza, l'intimidazione o la manipolazione diretta." to "The bully. Wants to control others through force, intimidation, or direct manipulation."
        NatureId.CONFORMIST -> "Vive seguendo le aspettative della società, accettando le regole e le norme stabilite." to "Lives by following society's expectations, accepting established rules and norms."
        NatureId.CONNIVER -> "Sfrutta gli altri per ottenere ciò che vuole, usando l'astuzia e l'inganno." to "Exploits others to get what they want, using cunning and deception."
        NatureId.COMPETITOR -> "Cerca costantemente di superare gli altri. La competizione è il suo modo di vivere." to "Constantly seeks to outdo others. Competition is their way of life."
        NatureId.CURMUDGEON -> "Il burbero: vede il lato negativo di tutto. È cinico e diffidente verso tutti." to "The curmudgeon: sees the negative side of everything. Is cynical and distrustful of everyone."
        NatureId.DAREDEVIL -> "Cerca l'adrenalina e il rischio. Vive per il pericolo e l'eccitazione." to "Seeks adrenaline and risk. Lives for danger and excitement."
        NatureId.DEVIANT -> "Si ribella contro le norme sociali, cercando di destabilizzare l'ordine costituito." to "Rebels against social norms, seeking to destabilize the established order."
        NatureId.ENTHUSIAST -> "Si entusiasma per tutto con passione. Vive ogni esperienza con intensità." to "Gets enthusiastic about everything with passion. Lives every experience with intensity."
        NatureId.FANATIC -> "È devoto a una causa con assoluta dedizione. Nulla lo fermerà dalla sua missione." to "Is devoted to a cause with absolute dedication. Nothing will stop them from their mission."
        NatureId.GALLANT -> "Si comporta con grazia e fascino. Cerca di essere ammirato e desiderato." to "Behaves with grace and charm. Seeks to be admired and desired."
        NatureId.JUDGE -> "Giudica gli altri con severità, cercando di mantenere lo standard morale alto." to "Judges others harshly, seeking to maintain high moral standards."
        NatureId.LONER -> "Preferisce la solitudine. Non si fida degli altri e lavora meglio da solo." to "Prefers solitude. Does not trust others and works better alone."
        NatureId.MARTYR -> "Si sacrifica per gli altri, trovando soddisfazione nel donare la propria vita." to "Sacrifices for others, finding satisfaction in giving of themselves."
        NatureId.MONSTER -> "Accetta la propria natura mostruosa e la abbraccia senza rimpianti." to "Accepts their monstrous nature and embraces it without regret."
        NatureId.PEDAGOGUE -> "Insegna e guida gli altri, trovando scopo nell'istruzione e nella saggezza." to "Teaches and guides others, finding purpose in instruction and wisdom."
        NatureId.PENITENT -> "Cerca di espiare i propri peccati attraverso azioni positive." to "Seeks to atone for their sins through positive actions."
        NatureId.PERFECTIONIST -> "Cerca la perfezione in tutto ciò che fa. Non si accontenta mai del mediocre." to "Seeks perfection in everything they do. Never settles for mediocrity."
        NatureId.PLOTTER -> "Pianifica ogni mossa con cura, anticipando le azioni degli altri." to "Plans every move carefully, anticipating the actions of others."
        NatureId.REBEL -> "Si ribella contro qualsiasi autorità o sistema. Non accetta regole imposte dall'esterno." to "Rebels against any authority or system. Does not accept externally imposed rules."
        NatureId.ROGUE -> "Vive per il piacere e l'avventura, ignorando le conseguenze delle proprie azioni." to "Lives for pleasure and adventure, ignoring the consequences of their actions."
        NatureId.SURVIVOR -> "La sopravvivenza è tutto. Fa qualsiasi cosa per restare in vita." to "Survival is everything. Will do whatever it takes to stay alive."
        NatureId.TRADITIONALIST -> "Rispetta le tradizioni e le antiche usanze. Il passato è la guida per il futuro." to "Respects traditions and ancient customs. The past is the guide for the future."
        NatureId.VISIONARY -> "Cerca di creare un futuro migliore, immaginando possibilità che altri non vedono." to "Seeks to create a better future, imagining possibilities that others do not see."
        NatureId.MASOCHIST -> "Cerca attivamente il dolore e la sofferenza. Trova soddisfazione nel sacrificio e nell'auto-distruzione." to "Actively seeks pain and suffering. Finds satisfaction in sacrifice and self-destruction."
    }

    private fun getDemeanorDescription(demeanor: DemeanorId): Pair<String, String> = when (demeanor) {
        DemeanorId.ALTRUIST -> "Si presenta come generoso e premuroso verso gli altri. Nasconde i propri desideri dietro una maschera di altruismo." to "Appears generous and caring toward others. Hides their desires behind a mask of altruism."
        DemeanorId.ARCHITECT -> "Si mostra come un costruttore metodico e paziente. Presenta un'immagine di stabilità e lungimiranza." to "Appears as a methodical and patient builder. Presents an image of stability and foresight."
        DemeanorId.AUTOCRAT -> "Si presenta come autoritario e dominante. Mostra che è lui a comandare." to "Appears authoritarian and dominant. Shows that they are the one in charge."
        DemeanorId.CHILD -> "Si presenta come semplice, ingenuo e dipendente. Cerca protezione e cura." to "Appears simple, naive, and dependent. Seeks protection and care."
        DemeanorId.BON_VIVANT -> "Si presenta come il giullare, sempre con una battuta pronta. Nasconde la propria intelligenza dietro la farsa." to "Appears as the jester, always with a witty remark. Masks their intelligence behind buffoonery."
        DemeanorId.BRAVO -> "Si presenta come dominante e spaventoso. Usa l'intimidazione per ottenere ciò che vuole." to "Appears dominant and fearsome. Uses intimidation to get what they want."
        DemeanorId.CONFORMIST -> "Si conforma alle aspettative della società, seguendo le regole e le norme stabilite." to "Conforms to society's expectations, following established rules and norms."
        DemeanorId.CONNIVER -> "Si presenta come affascinante e persuasivo. Nasconde le proprie intenzioni dietro un sorriso." to "Appears charming and persuasive. Masks their intentions behind a smile."
        DemeanorId.COMPETITOR -> "Si presenta come ambizioso e determinato. Mostra il proprio valore attraverso i risultati." to "Appears ambitious and determined. Shows their worth through results."
        DemeanorId.CURMUDGEON -> "Si presenta come burbero e scontroso. Mostra diffidenza verso tutti." to "Appears grumpy and cantankerous. Shows distrust toward everyone."
        DemeanorId.DAREDEVIL -> "Si presenta come avventuroso e temerario. Mostra coraggio e spavalderia." to "Appears adventurous and daring. Shows courage and bravado."
        DemeanorId.DEVIANT -> "Si presenta come diverso e originale. Mostra una visione alternativa del mondo." to "Appears different and original. Shows an alternative worldview."
        DemeanorId.ENTHUSIAST -> "Si presenta come entusiasta e passionale. Trasmette energia e ottimismo." to "Appears enthusiastic and passionate. Transmits energy and optimism."
        DemeanorId.FANATIC -> "Si presenta come devoto e convinto. Mostra una fede incrollabile nella propria causa." to "Appears devout and convinced. Shows unwavering faith in their cause."
        DemeanorId.GALLANT -> "Si presenta come galante e cortese. Mostra grazia e fascino in ogni situazione." to "Appears gallant and courteous. Shows grace and charm in every situation."
        DemeanorId.JUDGE -> "Si presenta come severo e giusto. Mostra integrità e imparzialità." to "Appears stern and fair. Shows integrity and impartiality."
        DemeanorId.LONER -> "Si presenta come solitario e indipendente. Mostra autarchia e riservatezza." to "Appears solitary and independent. Shows self-sufficiency and reserve."
        DemeanorId.MARTYR -> "Si presenta come disposto al sacrificio. Mostra devozione e umiltà." to "Appears willing to sacrifice. Shows devotion and humility."
        DemeanorId.MONSTER -> "Si presenta come spietato e crudele. Mostra indifferenza alla sofferenza altrui." to "Appears ruthless and cruel. Shows indifference to the suffering of others."
        DemeanorId.PEDAGOGUE -> "Si presenta come saggio e colto. Mostra desiderio di insegnare e guidare." to "Appears wise and learned. Shows a desire to teach and guide."
        DemeanorId.PENITENT -> "Si presenta come pentito e umile. Mostra rimorso e desiderio di redenzione." to "Appears penitent and humble. Shows remorse and a desire for redemption."
        DemeanorId.PERFECTIONIST -> "Si presenta come preciso e meticoloso. Mostra attenzione ai dettagli." to "Appears precise and meticulous. Shows attention to detail."
        DemeanorId.PLOTTER -> "Si presenta come calcolatore e patient. Mostra una mente strategica." to "Appears calculating and patient. Shows a strategic mind."
        DemeanorId.REBEL -> "Si presenta come ribelle, sfidando l'autorità e le convenzioni sociali." to "Appears as a rebel, defying authority and social conventions."
        DemeanorId.ROGUE -> "Si presenta come sfacciato e carismatico. Mostra sicurezza e spavalderia." to "Appears brash and charismatic. Shows confidence and bravado."
        DemeanorId.SURVIVOR -> "Si presenta come resiliente e determinato. Mostra forza interiore e testardaggine." to "Appears resilient and determined. Shows inner strength and stubbornness."
        DemeanorId.TRADITIONALIST -> "Si presenta come rispettoso delle tradizioni. Mostra reverenza per il passato." to "Appears respectful of traditions. Shows reverence for the past."
        DemeanorId.VISIONARY -> "Si presenta come sognatore e idealista. Mostra una visione del futuro." to "Appears as a dreamer and idealist. Shows a vision of the future."
        DemeanorId.MASOCHIST -> "Si presenta come calcolatore e spietato. Nasconde la propria crudeltà dietro una facciata di compostezza." to "Appears calculating and ruthless. Masks their cruelty behind a composed facade."
    }
}

class CompendiumViewModelFactory(
    private val ruleRepository: RuleRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CompendiumViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CompendiumViewModel(ruleRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
