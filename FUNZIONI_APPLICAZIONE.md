# V20 Companion — Guida alle funzioni dell'applicazione

Aggiornato alla versione **v34** dell'app.

App Android per gestire le partite di **Vampire: The Masquerade 20th Anniversary (V20)**.
Può essere usata dal **Giocatore** (per il proprio personaggio) e dal **Narratore** (per gestire la cronaca e la tavola).
Tutti i dati restano salvati sul telefono. L'interfaccia è disponibile in **italiano** e **inglese**, con possibilità di cambiare lingua dall'app.

---

## 1. Schermata iniziale (Home)

- Elenco di tutti i personaggi salvati, con azioni rapide: **modifica**, **duplica**, **elimina**.
- **Crea personaggio**: avvia la creazione guidata.
- **Tiro rapido dei dadi**: tiro veloce senza entrare nel dettaglio.
- Sezione **Strumenti**: Cronache, Compendio, Tiri di dadi, Impostazioni.
- **Tracker di combattimento**: pulsante scudo con l'inseguitore di iniziativa locale (vedi sezione 17).
- Se la lista è vuota, mostra un invito a creare il primo personaggio.

## 2. Creazione del personaggio (5 passi)

1. **Identità**: nome, clan, generazione, concetto, natura, indole, sire e dati di base.
2. **Attributi**: ripartizione dei tre gruppi (Fisico, Sociale, Mentale).
3. **Abilità**: ripartizione delle abilità (Talenti, Competenze, Conoscenze).
4. **Vantaggi**: Discipline, Background, Virtù, Meriti e Difetti.
5. **Finale**: riepilogo, controlli e salvataggio.

Funzioni di supporto:

- **Preset rapidi** per attributi (7/5/3) e abilità (13/9/5), con varianti Combattimento e Furtività.
- **Nome casuale** per suggerire un nome al volo.
- Contatore punti per **Meriti** e **Difetti**.
- **Validazione**: elenca gli errori da sistemare prima del salvataggio.
- **Avviso di punti**: se i punti assegnati non corrispondono al totale standard, chiede se continuare comunque.
- **Suggerimenti di creazione** con le cifre consigliate dal manuale.
- **Regole della casa della cronaca**: avviando la creazione dal tab Personaggi di una cronaca (pulsante "Crea personaggio"), vengono applicati costi e punti freebie, totali di ripartizione e clan esclusi della cronaca; al salvataggio il PG viene collegato automaticamente alla cronaca.

## 3. Scheda del personaggio

Schermata principale del personaggio, con otto sezioni:

- **Panoramica**: identità, statistiche rapide, umanità e **pannello stato rapido**.
- **Attributi**: valori con dettaglio e calcolo del pool di tiro.
- **Abilità**: abilità apprese con il loro rango.
- **Vantaggi**: discipline, background e virtù (con aggiunta e rimozione).
- **Dettagli**: salute per livelli con danno (contuso, letale, gravato) e tratti derivati.
- **Pregi & Difetti**: meriti e difetti con contatore punti.
- **Equipaggiamento**: oggetti con peso, danno, costo e note.
- **Note**: note libere del personaggio.

Inoltre:

- **Dashboard** con azioni rapide: *Tira i dadi*, *Modalità sessione*, *Modifica personaggio*, stato della **salute**.
- **Salute** con le tre caselle di danno: contuso (`/`), letale (`X`), gravato (`*`).
- **Esperienza**: punti guadagnati, spesi e disponibili, con accesso alla spesa.
- **Sezioni riordinabili**: dall'icona di riordino in alto è possibile riassegnare l'ordine delle sezioni della scheda (su/giù), con ripristino all'ordine standard; l'ordine è salvato nell'app.
- **Pannello stato rapido** (sangue, volontà, salute) interattivo: caselle sangue con −/+, punti volontà con −/+, 7 livelli di salute da toccare per ciclare il danno (contuso → letale → gravato → nessuno); le modifiche di stato sono **salvate subito** senza premere Salva e, al tavolo, sono sincronizzate in tempo reale (vedi sezione 12). In sola lettura (foglio condiviso) i controlli sono nascosti.
- **Salvataggio automatico** al primo cambiamento, con messaggio di conferma.

## 4. Sessione di gioco (modalità sessione)

Pannello da usare durante la partita per aggiornare il personaggio al volo:

- **Pannello stato rapido unificato**: serbatoio di sangue (prelievo e ricarica), volontà (spesa e recupero) e salute (7 livelli, tocca per cambiare tipo di danno) in un unico pannello — lo stesso usato nella scheda e al tavolo live.
- **Esperienza**: guadagnare e spendere punti.
- **Note di sessione** libere.

## 5. Esperienza (spesa XP)

Schermata dedicata alla spesa dei punti esperienza:

- Mostra i punti disponibili.
- Categorie: Tutti, Fisico, Sociale, Mentale, Talenti, Competenze, Conoscenze, Discipline, Background, Virtù.
- Ogni acquisto mostra la riduzione di punti applicata.
- Se i punti non bastano, compare un avviso.

## 6. Tiri di dadi

- **Pool di dadi** e **difficoltà** del tiro.
- Opzioni: dado extra, attivazione della **volontà**, modificatore di tiri, modificatore di difficoltà, **decine esplosive**, motivo del modificatore.
- I valori predefiniti di difficoltà e decine esplosive seguono le **Regole della casa** quando configurate (vedi sezione 15).
- Risultato con **successi**, **falle**, **botch** e riepilogo (successi e 1).
- **Reveal cinematografico**: quando attivo, il risultato appare a schermo intero con dado 3D, nome del tiro, verdict in grande e flash rosso su boccia (oro su successo totale); chiude da solo dopo ~3 secondi o con un tocco. Modalità: disattivato, **solo momenti critici** (boccia o successo totale, predefinito), ogni tiro (vedi Impostazioni, sezione 15).

## 7. Compendio

- Raccolta di regoli e contenuti di gioco (clan, discipline, meriti, ecc.).
- **Ricerca** con cancellazione dei risultati.
- Voce dettagliata con **categoria**, **costo in punti** e **descrizione**.

## 8. Cronache

Elenco delle cronache (le avventure del gruppo). Ogni cronaca ha:

- **Membri**: PG e giocatori della cronaca.
- **Sessioni**: sessioni pianificate, in corso e completate.
- **Note** e **Note personaggi**: appunti liberi, con editor che supporta **collegamenti interni** ed **etichette (tag)**.
- **NPC**: personaggi non giocanti con ruolo, descrizione e note.
- **Luoghi**: luoghi della cronaca, con immagine e mappa.
- **Fazioni**, **Trame**, **Segreti**, **Eventi**, **Indizi**: elementi di racconto gestibili con aggiunta, modifica ed eliminazione.
- **Varianti di scena**: ogni scena può avere alternative (es. "Elysium — durante l'attacco") con nome, note e asset propri; una variante può essere impostata come **predefinita** (radiobutton), e compare come sottotitolo della scena nel deck.
- **Regole della casa**: testo delle regole personalizzate della cronaca.
- **Ricerca nella cronaca**: cerca in tutte le aree, con filtri per PG/NPC e per tipo (luoghi, trame, segreti, indizi, fazioni, eventi, note).
- **Media**: libreria dei file della cronaca (vedi sezione 10).
- **Schermata del Narratore** (vedi sezione 11).
- **Tavolo live** (vedi sezione 12).

Le sessioni hanno tre stati: **pianificata**, **attiva**, **completata**.

## 9. Riepilogo di sessione

Per ogni sessione completata si può aprire il riepilogo:

- Partecipanti e PNG presenti.
- Scene e cronologia degli eventi.
- Note, **tiri di dadi** avvenuti e **media presentati**.
- **XP assegnati** e tempo di gioco trascorso.
- Azione **Clona e riprendi**: crea una nuova sessione pianificata copiando quella passata.

## 10. Media, documenti e mappe

**Libreria media** della cronaca:

- Categorie: Tutti, Mappe, NPC, Luoghi, Indizi, Documenti, Altro.
- Import di **immagini**, **documenti** e **video**.
- Tipo di file: mappa, ritratto, documento, foto, altro.
- Ogni file può essere segnato come riservato al Narratore.
- Ritratti dei personaggi aggiornabili.

**Visualizzatore immagini**:

- **Pin e annotazioni** (note posizionate sull'immagine).
- **Livelli** (layer) con occhio per nascondere o mostrare ciascuno.
- **Storico versioni**: le revisioni salvate possono essere **ripristinate** (collegate alle sessioni).

**Lettore documenti** (PDF):

- Pagine avanti/indietro, zoom, indicatore pagina.
- **Modalità presentazione** a schermo intero (usata anche dal Narratore sul tavolo live).

**Video**: lettore con schermo intero.

**Mappa dei luoghi**: immagine del luogo con annotazioni posizionabili.

## 11. Schermata del Narratore (Storyteller)

Pannello di controllo durante la sessione:

- **Scena attiva**: apertura e cambio scena con titolo.
- **Avvio e fine sessione**.
- **PG in scena** e **PNG in scena** (con personaggi e ritratti).
- **Trame attive** della cronaca.
- **Barra azioni rapide**: **dadi**, **nota rapida**, **evento** da registrare al volo e **Quick NPC** (crea un PNG al volo con nome, ruolo e tipo).
- Indicatore **LIVE** quando la tavola è attiva.
- Pulsanti per creare o unirsi al **tavolo live**.

## 12. Tavolo live (partita in rete locale)

Permette a Giocatori e Narratore di giocare insieme sulla **stessa rete WiFi**:

**Connessione**

- **Crea tavolo**: il Narratore apre la sala e condivide il suo indirizzo IP.
- **Trova tavolo**: scansione automatica delle tavole attive sulla stessa WiFi (con avvio e arresto scansione).
- **Connessione manuale**: inserimento dell'IP del Narratore e della porta.
- **WiFi Direct (P2P)**: connessione diretta tra dispositivi **senza router** — il Narratore crea automaticamente il gruppo alla creazione del tavolo; da "Trova tavolo" i giocatori possono **cercare i dispositivi WiFi Direct** e unirsi con un tocco (serve il permesso di posizione/vicinanza). Alla creazione del tavolo l'app **chiede il permesso WiFi Direct**: se negato, il tavolo resta comunque aperto ma **solo sulla stessa rete (LAN)**.
- Avviso se il Narratore è su un emulatore (indirizzi 10.0.2.x non raggiungibili).

**Al tavolo**

- **Tavolo virtuale** con posti a sedere; i giocatori cliccano il proprio posto per entrare.
- **Stile del tavolo** (fusti e sedie) sincronizzato tra tutti.
- **Mixer audio al tavolo**: dal menu del Narratore si apre il mixer delle tracce (vedi sezione 13).
- **Ritratti** dei personaggi sui posti.
- **Tiri di dadi visibili a tutti** con il risultato in tempo reale.
- **Modificatori al tavolo**: ogni tiro (finestra dadi o tiro dalla scheda) supporta **modificatore dadi**, **volontà**, **dieci esplosive** e **motivo** del modificatore, oltre a pool e difficoltà.
- **Richiesta di tiro del Narratore**: dal pulsante "Richiedi tiro" il Narratore invia un tiro (pool, difficoltà, modificatori, motivo) a **un giocatore specifico o a tutti**; al giocatore compare una finestra "Tiro richiesto da…" con Tira/Annulla, e il tiro eseguito appare a tutti.
- **Log dei tiri**: cronologia completa di ogni tiro (ora, giocatore, caratteristiche, difficoltà, dadi, esito) leggibile dal **Narratore** con un tocco.
- **Tiri privati del Narratore**: opzione "Tiro privato" nella finestra dei dadi; solo il Narratore vede pool, dadi ed esito, gli altri vedono solo la notula "privato".
- **Tiri dalla scheda**: dal proprio foglio aperto al tavolo (icona dadi in alto) si selezionano **1, 2 o 3 caratteristiche** (Attributi/Abilità) e il pool è la loro **somma** (regole V20, difficoltà di default 6); il tiro è visibile a tutti sul tavolo.
- **Feed riducibile a icona**: i risultati recenti sul tavolo possono essere ridotti a una sola icona dadi e riaperti con un tocco.
- **Dadi 3D animati**: d10 in marmo a texture reale (volatina, atterraggio sul risultato, tinta rosso/verde per boccia/successo). Mesh e texture dal set gratuito "Low Poly 3D Dice Set" di **eddex** (itch.io), licenza **CC BY-SA 4.0**.
- **Reveal cinematografico al tavolo**: dal menu del Narratore (voce "Reveal cinematografico dei dadi") si sceglie quando mostrare il reveal a schermo intero — **disattivato**, **solo momenti critici** (predefinito) o **ogni tiro**; i giocatori possono cambiare la stessa preferenza da Impostazioni. I tiri privati non attivano mai il reveal per gli altri.
- **Stato rapido al tavolo**: il giocatore vede una card "Stato rapido" con il riepilogo (sangue · volontà · salute); toccandola si apre il pannello stato interattivo per modificare il proprio stato. Ogni modifica è **salvata in locale** e **sincronizzata a tutti in tempo reale** (protocollo StatUpdate).
- **Stato dei giocatori (Narratore)**: dal menu del Narratore (voce "Stato dei giocatori") si apre l'elenco dei PG condivisi con il pannello stato di ciascuno; il Narratore può **correggere** sangue, volontà e salute di qualunque giocatore e la modifica viene replicata a tutti e **salvata sul dispositivo del giocatore**. I giocatori senza scheda condivisa sono elencati con l'avviso "Nessun personaggio condiviso".
- **Sincronizzazione dei dati**: aggiornamenti delle statistiche del personaggio condivisi (salute, volontà, sangue) sia dalla sessione che dalla scheda al tavolo.
- **Foglio in sola lettura**: i giocatori possono vedere il foglio condiviso senza modificarlo.
- **Invio volontario della scheda**: dal proprio foglio aperto al tavolo il giocatore può premere l'icona invio per **inviare la scheda corrente al Narratore** (in aggiunta all'invio automatico alla connessione e alla richiesta del Narratore); arriva un'avviso "Scheda inviata" al giocatore e "Scheda ricevuta da…" al Narratore.
- **Salvataggio della scheda ricevuta**: il Narratore, aprendo il foglio condiviso di un giocatore, può premere **"Salva sul dispositivo"** per conservarlo nella propria libreria locale (e nella cronaca della tavola, se presente).
- **Presentazione dei file** dal Narratore (immagini, PDF, video) a schermo intero per tutti.
- **Rivela (handout)**: dal browser cronaca al tavolo il Narratore può premere **"Rivela"** su un **indizio** o un **segreto**; il contenuto appare a tutti i giocatori con un'avviso, l'indizio passa a "condiviso" e (se la sessione è attiva) viene registrato l'evento "Indizio rivelato" nel diario.
- **Condivisione di file** verso i giocatori: il file ricevuto viene salvato nella cronaca del giocatore.
- **Tracker di combattimento**: dal menu del Narratore (voce "Combattimento") si apre l'inseguitore di iniziativa e round; i giocatori premono **"Tira iniziativa"** (1d10 + Destrezza + Prontezza del loro personaggio), l'ordine si ricalcola e viene mostrato a tutti, il turno corrente è evidenziato (chi ci si trova vede "Tuo turno"); il Narratore aggiunge/rimuove combattenti, avanza i round e può disattivare il rilancio a ogni round (vedi sezione 17). Anche il **timer di turno** è sincronizzato: i giocatori vedono lo stesso countdown nella propria finestra combattimento (vedi sezione 17).
- Il Narratore può **chiudere la tavola**; tutti ricevono l'avviso.

## 13. Mixer audio

Strumento sonoro per l'atmosfera di partita:

- Import di tracce con categoria: **Ambiente**, **Musica**, **Effetti (SFX)**, **Personalizzate**.
- Riproduzione con **play**, **pausa**, **loop** e **ferma tutto**.
- **Preset**: salva le tracce attive con un nome, riattivali, rinominali o eliminiali (es. "Taverna gotica").
- Accessibile anche **dal tavolo live** (menu del Narratore) durante la partita.

## 14. Importazione ed esportazione

- **Personaggi**: file `.v20` (JSON) condivisibili con altri giocatori.
- Controlli all'importazione: file **valido**, **corrotto**, **versione non supportata**, **dati mancanti**.
- Se un personaggio con lo stesso nome esiste già: **sostituisci** o **crea una copia**.
- **Esporta JSON** e **Condivisione** del file.
- **Libreria di attrezzature**: import/export del file `v20-equipment-library` (formato JSON dedicato).
- Accesso anche dalle **Impostazioni**.

## 15. Impostazioni

- **Lingua**: Inglese / Italiano, applicata subito senza riavvio.
- **Tema**: aspetto dell'interfaccia.
- **Import/Export** dei personaggi.
- **Regole della casa** delle cronache, per cronaca:
  - Valori di **creazione** (abilità, discipline, background, virtù, punti freebie) e **costi freebie**.
  - **Valori iniziali** (sangue e volontà).
  - **Costi XP**: attributi, abilità, background, virtù, umanità, volontà e discipline (in clan, fuori clan, Caitiff) — applicati alla schermata **Esperienza**.
  - **Regole dei dadi**: difficoltà predefinita e **decine esplosive** (consentite, attive per default, a cascata) — applicate a tutti i tiri.
  - **Contenuti ammessi**: clan esclusi non appaiono nella tendina di creazione (e un clan escluso blocca il salvataggio).
- **Reveal cinematografico dei dadi**: quando appare il reveal a schermo intero — disattivato, solo momenti critici (predefinito) o ogni tiro (vedi sezione 6); stessa preferenza del menu del tavolo (sezione 12).
- **Log di errore**.

## 16. Log di errore

- Elenco dei crash rilevati dall'app.
- **Copia log**: copia il testo completo del crash per inviarlo allo sviluppatore.
- Dialogo di crash con titolo, istruzioni e pulsante per copiare.

## 17. Tracker di combattimento

- Accessibile dal **pulsante scudo** nella home (tracker locale) e dal **menu del Narratore** al tavolo live (voce "Combattimento").
- **Iniziativa V20**: 1d10 + Destrezza + Prontezza; chi ha il valore più alto agisce per primo, a parità vale l'ordine di inserimento.
- **Round**: il pulsante "Prossimo" passa al combattente successivo; superato l'ultimo si passa al round successivo.
- **Rilancio a ogni round**: di default (regole ufficiali) l'iniziativa va rilanciata a ogni round; il Narratore può disattivarlo dallo switch per congelare l'ordine.
- **Al tavolo live**:
  - Solo il Narratore avvia/termina il combattimento, aggiunge e rimuove combattenti (es. NPC) e avanza i round.
  - I giocatori premono "Tira iniziativa" per inviare il proprio valore (dalla scheda connessa) al Narratore.
  - L'ordine viene ricalcolato e mostrato a tutti; il turno corrente è evidenziato.
  - Il tracker viene inviato automaticamente ai giocatori che si collegano durante un combattimento.
- **Tracker locale**: stato solo in memoria (alla chiusura dell'app si azzera); non è collegato al tavolo live.
- **Timer di turno**: countdown per turno visibile a tutti (Off, 15, 30, 60, 120 secondi), con **pausa/ripresa** e interruttore **auto-avanza turno** (default attivo). Alla scadenza: suono + vibrazione e "TEMPO!" in rosso; con l'auto-avanzamento attivo il turno successivo parte automaticamente e il countdown si riavvia, altrimenti resta in rosso finché non si avanza manualmente. Il countdown è sincronizzato al tavolo (corretto automaticamente sugli orologi dei dispositivi) e la configurazione del timer sopravvive alla fine del combattimento.
- Limite: non gestisce azioni per round, Ferite, Celerità o turni multipli: è un inseguitore di iniziativa e round fedele alle regole base.

## 18. Note e limiti noti

- Le stringhe tecniche dei file di importazione (es. "formato non valido", errori interni) restano in inglese.
- Il tavolo live funziona solo se tutti i dispositivi sono sulla **stessa rete WiFi**.
- Un Narratore su emulatore (BlueStacks) non è raggiungibile dagli altri dispositivi: usare un telefono fisico.
- Le modifiche vengono salvate appena si cambia qualcosa.
