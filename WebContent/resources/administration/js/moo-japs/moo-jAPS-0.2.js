/*
 
	Title: moo-jAPS
	version 0.2.3 - 11/2009 Wood refactoring, Taboo refactoring. 
	version 0.2.1 - 28/04/2009 fixed cookie path
	version 0.2.1 - fixed HoofEd when loading buttons files, changed buttons files structure now simpler, added HoofEd buttons separator
	version 0.2 - added Taboo focus when switching
	version 0.2 - added HoofEd a simple textarea editor/tagger, added documentation
	
	
*/

/*
	Class: Taboo

	Classe che gestisce blocchi di informazioni in stile TAB

	Info:
	
		Version - 0.2
		Date - 24 Nov 2009

	ChangeLog:

		11/2009 - refactoring delle funzioni interne, richiamo dei tab. Pulizia e miglioramento performance in generale. 
	
	Parameters:
		tabs - {String} classe css dei contenitori dei tab
		tabTogglers - {String} classe css degli elementi che attivano e disattivano la visualizzazione dei tab
		startTabIndex - {String|Number} indice dell'array dei tab, quale tab mostrare in fase di istanziazione
		startTab - {String|El} id (html) del tab da mostrare in fase di istanziazione
		activeTabClass - {String} clases css applicata al toggler attivo (default: activetab)
		hideClass - {String} classe css che da applicare quando il tab si trova in stato *nascosto* (default: noscreen) 	
		showClass - {String} classe css che da applicare quando il tab si trova in stato *visibile* (default: showClass)
		anchorTab - {String} suffisso per il link, all'interno del tab, che prendrà il focus

		Esempio di struttura con due tab: 
		
		Taboo prevede che vi siano dei gli elementi *<a>* che puntino ai contenitori dei tab.
		Le ancore devono avere come attributo *href* l'id del tab.
		Gli attivatori (tabTogglers) devono avere una classe comune. Anche i contenitori dei tab devono avere una classe in comune.
		
		(start code)
		
		
		<a href="#idTab1" class="classeAttivatoreTab">Tab Uno</a>
		<a href="#idTab2" class="classeAttivatoreTab">Tab Due</a>
		
		<div id="idTab1" class="classeContenitoreTab" >
			Contenuto del tab numero uno.
		</div>
		
		<div id="idTab2" class="classeContenitoreTab">
			Questo il contenuto del tab numero due.
		</div>
		
		
		(end)
*/

var Taboo_class = {
	Implements: [Events, Options],
	options: {
		tabs: "",						//classe dei contenitori dei tab
		tabTogglers: "",				//classe css degli elementi che attivano e disattivano la visualizzazione dei tab
		startTabIndex: "",				//quale tab mostrare per primo (vince su startTab)
		startTab: "",					//come sopra però arriva un ID
		activeTabClass: "activetab",	//class css applicata al toggler attivo
		hideClass: "noscreen",			//classe css che viene applicata in stato "nascosto"
		showClass: "showClass",			//classe css che viene applicata in stato "visibile"
		anchorTab: "_quickmenu"			//classe css per i backLink 
		/*
		 * 		tabs: "tab",
		 *		tabTogglers: "tab-toggle"
		 *		startTabIndex: 1,
		 */
	},
/*
	Function: initialize
	
	Esegue le operazioni necessarie ad istanziare un Taboo
	
	- Setta le opzioni passate al costruttore
	- Organizza i tab
	- Apre il tab d'inizio 
	 
	Parameters:
	
		options - {Obj} options
*/
	initialize: function(options){
		this.setOptions(options);	//setto le opzioni entranti
		this.setupTaboo();	//setto i tab
		this.startupTab();
    },
/*
	Function: setupTaboo

	Cerca e prepara i tab al corretto funzionamento 
	
	- Cerca le accoppiate tab/toggler
	- Scarta i tab e i toggler disaccoppiati
	- Setta gli eventi
*/
    
    setupTaboo: function() {
		var togglers = $$("."+this.options.tabTogglers);
		var tabs = $$("."+this.options.tabs);
		var avaiable = [];
		for (var i = 0; i < togglers.length; i++){
			if ($chk(tabs[i]) && $defined(tabs[1]) && $type(tabs[1]) == "element"  ) {
				avaiable.push([ togglers[i] , tabs[i] ]);
			}
		};
		this.options.avaiableTabPairs = avaiable;
		for (var i = 0; i < avaiable.length; i++){
			this.setupTab(avaiable[i][0],avaiable[i][1]);
		};
	},
		setupTab: function(togg, tab) {
			tab.removeClass(this.options.showClass);
			tab.addClass(this.options.hideClass);
			
			tab.addEvent("makeTaboo", function(event, tab){
				//console.log("we sto lanciando makeTaboo");
				this.makeTaboo(tab);
			}.bindWithEvent(this,tab));
			
			tab.store("toggler",togg);
			
			togg.addEvent("click", function(event, tab, taboo, togg){
				event.preventDefault();
				//var tab = arguments[1];
				//var taboo = arguments[2];
				//var togg = arguments[3];
				arguments[1].fireEvent("makeTaboo");
			}.bindWithEvent(tab,[tab,this,togg]));
		},
/*
	Function: startupTab
	
	Apre il tab corretto all'avvio
	
	Si preoccupa di aprire il primo tab verificando i parametri di istanza, nell'ordine:
	
	- startTabIndex
	- startTab
	- ancora del documento (document.location.hash)
	- altrimenti il primo in ordine di comparizione
	
*/
	startupTab: function() {
		var done = false;
		
		if ($defined(this.options.startTabIndex) && this.options.startTabIndex != '') {
			done = this.makeTaboo(this.options.startTabIndex.toInt());
		}
		else if($defined(this.options.startTab) && this.options.startTab != '') {
			done = this.makeTaboo(this.options.startTab);	 
		}
		else if (document.location.hash != '') {
			done = this.makeTaboo(document.location.hash.replace('#',''));
		}
		else { 
			done = this.makeTaboo(0);
		}
		
		if (!done) {this.makeTaboo(0);}
	},
/*
	Function: makeTaboo
	
	Apre un tab
	
	Il tab in ingresso può essere sotto forma di stringa, numero o Element.
	
	- verifica il dato input
	- chiude tutti gli altri tab
	- apre il tab ingresso
	- seleziona il toggler corretto
	- restituisce true
	
	Parameters:
	
		tab - {String|Number|El} tab da aprire
	
	
	Returns:
		
		Boolean - *true* se ha trovato il tab passato, altrimenti false
	
*/	
	makeTaboo: function(tab) {
		switch($type(tab)) {
			case "string":
				if (tab != "NaN") {
				    tab = $(tab);
				}
				break;
			case "number":
		  		  tab = this.options.avaiableTabPairs[tab][1];
				break;
			case "element":
				break;
			default: 
				tab = null;
		}
		if ($defined(tab)) {
			for (var i = 0; i < this.options.avaiableTabPairs.length; i++){
				this.options.avaiableTabPairs[i][1].removeClass(this.options.showClass);
				this.options.avaiableTabPairs[i][1].addClass(this.options.hideClass);
			}
			tab.removeClass(this.options.hideClass);
			tab.addClass(this.options.showClass);
			
			var toggler = tab.retrieve("toggler");
			this.makeTabooTogglers(toggler);
			
			try {
				toggler.blur();
				$(tab.get('id')+this.options.anchorTab).focus();
				return true;
			} catch (e) {	}
			
		}
		return false;
 	},
/*
	Function: makeTabooTogglers
	
	Seleziona il toggler
	
	Deseleziona tutti gli altri toggler e attiva quello corrente.
	
	Parameters:
	
		toggler - {String|El} toggler da attivare
	
*/	
	makeTabooTogglers: function(toggler) {
		if ($type(toggler) == "string") {toggler = $(toggler);}
		for (var i = 0; i < this.options.avaiableTabPairs.length; i++){
			this.options.avaiableTabPairs[i][0].removeClass(this.options.activeTabClass);
		}
		toggler.addClass(this.options.activeTabClass); 
	}
};
var Taboo = new Class(Taboo_class);


/**************************************************/
/**************************************************/
/**************************************************/
/**************************************************/
/**************************************************/
/**************************************************/
/**************************************************/


/*
   Class: Wood
   Classe che gestisce gli alberi e i menu

	Info:
	
		Version - 0.2
		Date - 18 Oct 2009

	ChangeLog:

		10/2009 - refactoring delle funzioni interne per lo storage dei rami. Pulizia in generale. 
 
   
   Parameters:
		rootId - {String} id (html) dell'elemento di riferimento, necessario se showTools è true
		menuToggler - {String} classe css degli elementi attivatori dell'albero/menu
		hideClass - {String} classe css utilizzata nei rami per lo stato *nascosto* (default: noscreen) 
		showClass - {String} classe css utilizzata nei rami per lo stato *visibile* (default: undoNoscreen)
		openClass - {String} classe utilizzata nei rami per lo stato *aperto* (default: openmenu)
		closedClass - {String} classe utilizzata nei rami per lo stato *chiuso* (default: closedMenu)
		showTools - {Boolean} decide se creare la toolbar. (opzionale)
		expandAllLabel - {String} etichetta per il link che *espande tutto* (default: +)
		collapseAllLabel - {String} etichetta per il link che "chiude tutto" (default: -)
		startIndex - {String} id (html) del ramo da aprire in fase di istanziazione  
		toolClass - {String} classe css utilizzata per gli strumenti (default: toolClass)
		type - {String} tipo di oggetto, tipi attualmente implentati *tree* e *menu*
		enableHistory - {Boolean} attiva o disattiva la funzionalità per la memoria del Wood menu
		cookieName - {String} nome del cookie dove salvare lo stato del Wood menu
		cookiePath -  {String} path del cookie, default "/"
		toolTextIntro - {String} Testo introduttivo per la toolbar
		toolexpandAllLabelTitle - {String} Titolo per *expandAll*
		toolcollapseLabelTitle - {String} titolo per *collapseAll*

		Wood è stato pensato per essere un menu oppure un albero.
		
		La struttura di esempio per il menu:
		E' possibile annidare all'infinito i rami del menu.
		(start code)
		
		<ul id="MyMenu">
			<li class="menuAperto">
				<a href="#menu_PrimaVoce" rel="menu_PrimaVoce" class="attivatoreDelMenu">Prima Voce</a>
				<ul id="menu_PrimaVoce">
					<li><a href="http://link1">Uno</a></li>
					<li><a href="http://link2">Due</a></li>
				</ul>
			</li>
			<li class="menuAperto">
				<a href="#menu_SecondaVoce" rel="menu_SecondaVoce" class="attivatoreDelMenu">Seconda Voce</a>
				<ul id="menu_SecondaVoce">
					<li><a href="http://link3">Tre</a></li>
					<li><a href="http://link4">Quattro</a></li>
				</ul>
			</li>
		</ul>
		
		(end)

		La struttura di esempio per il tree:
		E' possibile annidare all'infinito i rami del tree.
		(start code)
		
		<ul id="MyTree">
			<li>
				<input type="radio" name="" id="id_numero_1" value="" class="attivatoreMyTree" />
				<label for="id_numero_1">Primo Ramo</label>
				<ul class="treeToggler"> 
					<li>
						<input type="radio" name="" id="id_numero_2" value="" />
						<label for="id_numero_2">Primo Ramo del primo ramo</label>
					</li>
					<li>
						<input type="radio" name="" id="id_numero_3" value="" />
						<label for="id_numero_3">Secondo Ramo del primo ramo</label>
					</li>
					<li>
						<input type="radio" name="" id="id_numero_4" value="" class="attivatoreMyTree" />
						<label for="id_numero_4">Terzo Ramo del primo ramo</label>
						
						<ul class="treeToggler"> 
							<li>
								<input type="radio" name="" id="id_numero_5" value="" />
								<label for="id_numero_5">Sottovoce Uno</label>
							</li>
							<li>
								<input type="radio" name="" id="id_numero_6" value="" />
								<label for="id_numero_6">Sottovoce Due</label>
							</li>
						</ul>
					</li>		
				</ul>
			</li>
		</ul>
		
		(end)
   
*/

var WoodClass = {
	
	Implements: [Events, Options],
			 
	options: { 
		rootId: "rootId", 			//id dell'elemento di riferimento, necessario se showTools è true
		menuToggler: "",			//classe degli elementi che "cliccano"
		hideClass: "noscreen",		//classe utilizzata nei rami per lo stato "nascosto" 
		showClass: "undoNoscreen",	//classe utilizzata nei rami per lo stato "visibile"
		openClass: "openmenu",		//classe utilizzata nei rami per lo stato "aperto"
		closedClass: "closedmenu",	//classe utilizzata nei rami per lo stato "chiuso"
		showTools: "false",			//boolean(true | false), opzionale
		expandAllLabel: "+",		//etichetta per il link che "espande tutto"
		collapseAllLabel: "-",		//etichetta per il link che "chiude tutto"
		startIndex: "",				//id del ramo da aprire 
		toolClass: "toolClass",		//classe css utilizzata per gli strumenti
		type: "",					//tipo di oggetto(tree | menu)
		enableHistory: false,		//enable coockie based history
		cookieName: 'jAPS2Full',	//cookie name
		cookiePath: "/",			//path del cookie
		toolTextIntro: 'You can expand the tree structure, or again, using the appropriate links.', //text before toolbar links
		toolexpandAllLabelTitle: 'Expand All Tree',	//title for expand  all
		toolcollapseLabelTitle: 'Collapse All Tree' //title for close all
	},	

	/*
		Function: initialize
		
		Esegue le operazioni necessarie ad istanziare un Wood. 
		
		- Setta le opzioni passate al costruttore
		- Istanzia i Togglers
		- Istanzia la ToolBar
		- Chiude tutto
		- Aggiorna i Cookie
		- Apre il ramo desiderato secondo l'opzione StartIndex
		
		Parameters:
		
			options - oggetto contenente le opzioni. Si veda Wood
	*/
	
	initialize: function(options){ //brodo primordiale: set delle opzioni di istanza...
		this.setOptions(options);
		this.setupTogglers();
		this.setupToolBar();
		this.collapseAll();
		this.updateBouquet();
		this.startIndex(); 
    },

	/*
		Function: setupToolBar
		
		Prepara la ToolBar se *showTools* è true.
		
		Crea i link per le azioni collapseAll e expandAll ed inietta un paragrafo prima dell Wood
		
	 */

	setupToolBar: function() {
		if ((this.options.showTools == "true") && (this.options.rootId != "")) { 
			
			//istanzio un nuovo elemendo DOM di tipo P e lo inietto prima della rootId
			var toolbar = new Element('p', {
				'class': this.options.toolClass
			}).injectBefore($(this.options.rootId));
			
			var toolbarIntro = new Element('p', {
				'class' : this.options.hideClass, 
				'text' : this.options.toolTextIntro
			}).injectBefore(toolbar);
			
			//istanzio un nuovo elemento Dom di tipo A
			var cLink = new Element('a');
			
			//applico stile css
			cLink.addClass(this.options.toolClass);
			
			//setto href a niente
			cLink.set('href','#');
			
			//setto l'etichetta del link
			cLink.set('html',this.options.collapseAllLabel)
			cLink.set('title',this.options.toolcollapseLabelTitle)
			
			//aggiungo l'evento click di cui blocco la propagazione
			cLink.addEvents({
				'click': function() {
					arguments[0].collapseAll();
					return false;
				}.pass(this)
			});
			
			//lo piazzo dentro alla toolbar
			cLink.injectInside(toolbar);
			
			//hack per lo spazio tra i due link
			toolbar.appendText(' ','bottom');
	
			//idem sopra, cambia solo l'evento
			var oLink = new Element('a');
			oLink.addClass(this.options.toolClass);
			oLink.set('href','#');
			oLink.set('html',this.options.expandAllLabel)
			oLink.set('title',this.options.toolexpandAllLabelTitle)
			oLink.addEvents({
				'click': function() {
					arguments[0].expandAll();
					return false;
				}.pass([this])
			});
			oLink.injectInside(toolbar);
		
		}

	},

	/*
		Function: startIndex
		
		Apre il ramode desiderato secondo il parametro *startIndex*.
		
		Se startIndex il Wood viene esploso sino in quel ramo.
		
	 */
	
	startIndex: function() {
		if($defined(this.options.startIndex) && (this.options.startIndex != "" || this.options.startIndex >= "0"))  {

			var parentEl;							//preparo il livello padre
			var previousEl;							//preparo il toggler
			var elementsToOpen = [];		//preparo la lista degli elementi da "aprire"
			var idToRead = this.options.startIndex;	//id da cui partire...
			
			switch (this.options.type) {			//controllo sul tipo... prendo gli elementi necessari
				case "tree": 
					parentEl = "ul";								//adattamento per tree
					previousEl = "input";							//adattamento per tree
					idToRead = $(idToRead);								//adattamento per tree
					elementsToOpen.include(idToRead);
				break;
				
				case "menu": 
					parentEl = "ul";								//adattamento per menu
					previousEl = "a";								//adattamento per menu
					idToRead = $(idToRead).getPrevious(previousEl);		//adattamento per menu
					elementsToOpen.include(idToRead);					
				break;
			}
			
			var test = false;										//init
			while(test == false) {					
				if ($defined($(idToRead).getParent(parentEl))) {		//se esiste un livello padre 
					elementsToOpen.include($(idToRead).getParent(parentEl).getPrevious(previousEl));	//prendo il toggler del livello padre
					idToRead = $(idToRead).getParent(parentEl);		//salto un gradino e mi posiziono sul livello padre corrente per proseguire la lettura 
				}
				else { test = true; }								//se non esiste un livello padre... esco dal while
			}
		
			this.makeWood(elementsToOpen,"open");		
		
		}
	},

	/*
		Function: updateBouquet
		
		Si preoccupa di sincronizzare il Wood con il relativo Cookie del browser e vice versa 
		
		Viene chiamato in causa sempre, ma attiva il Cookie solo se *enableHistory* è true
	*/

	updateBouquet: function() {
		if (this.options.enableHistory || this.options.enableHistory == "true") { 
			var CookieRead = Cookie.read(this.options.cookieName);

			if ($defined(CookieRead)) {
				this.options.bouquet = CookieRead.split(",");
				this.options.bouquet = this.options.bouquet.clean(); 		
				
				for (var i = this.options.bouquet.length - 1; i >= 0; i--){
					this.makeWood(this.options.bouquet[i],"open"); 
				};
				
				switch(this.options.type) {
					case "tree":
					    if (this.options.bouquet[this.options.bouquet.length-1] != null && this.options.bouquet[this.options.bouquet.length-1] != "" && $type(this.options.bouquet[this.options.bouquet.length-1]) == "element") {
							$(this.options.bouquet[this.options.bouquet.length-1]).checked="checked";
						}
						break;
					case "menu":
						break;
					default: 
						return null;
				}
			
			} else { this.options.bouquet = []; }
			
		}
	},

	/*
		Function: setupTogglers
		
		Ricerca tutti i Toggler e crea il meccanismo per apertura e chiusura del ramo.
		
		Prende tutti gli elementi con la classe *menuToggler* ed associa loro un eventu onclick e un evento custom chiamato *clickMenu*
		
	 */
	
	setupTogglers: function() {
		//console.log("setupTogglers -> "+this.options.type);
		this.options.menuToggler = $$("."+this.options.menuToggler);
		for (var i = this.options.menuToggler.length - 1; i >= 0; i--){
			var toggler = this.options.menuToggler[i];
			
			switch(this.options.type) {
				case 'tree':
					if(!$chk($(toggler).getNext("ul"))) { continue; }
					else {
						//add event
						toggler.addEvent('click', function(ev){
							//ev.preventDefault();
							var el = ev.target;
							el.fireEvent('clickMenu');
						});
					}
					break;
				case 'menu':
					if ( (toggler.rel == "") && ($(toggler.rel) != 'element')) { continue; } else {
						//add event
						toggler.addEvent('click', function(event, toggler){
							event.preventDefault(); 
							toggler.fireEvent('clickMenu');
						}.bindWithEvent(toggler, toggler));
					}
					break;
				default: 
					return false;
			}
			
			toggler.addEvent('clickMenu', function(){
				arguments[1].makeWood(arguments[0]);
			}.pass([toggler,this]));
		}
	}, 

	/*
		Function: makeWood
		
		Chiamato per aprire e chiudere i rami del Wood.
		
		Nel dettaglio:
		
		- Recupera i rami in ingresso
		- Verifica di che tipo sono
		- Forza l'apertura o la chiusura
		- Eventualmente salva lo storico
		
		Parameters:
		
			rami - {String | El | Array(String|El)} elementi su cui azionare makeWood
			action -  {String} valori accettati: *open* | *close* | *null*(default). Specifica cosa fare
			doHistory - {Boolean} Specifica se salvare l'azione nello storico oppure no
		
	 */
	
	makeWood: function (rami,action,doHistory) {
		
		var history = true;
		if ($defined(doHistory)) {
			history = doHistory;
		}		
		
		rami = $splat(rami);
		
		for (var i = rami.length - 1; i >= 0; i--) {
			var el = rami[i];
			
			if ($type(el) == "string") { el = $(el); }
			if ($type(el) != 'element') { continue; };
			
			var toggParent = el.getParent(); 
			var menu = null;
			switch (this.options.type) {
				case 'tree':
					menu = el.getNext("ul");
					break;
				case 'menu':
					menu = $(el.rel);
					break;
				default:
					return false;
			}
			
			switch(action) {
				case "close":
				    toggParent.removeClass(this.options.openClass);
					toggParent.addClass(this.options.closedClass);
					menu.addClass(this.options.hideClass);
					menu.removeClass(this.options.showClass);
					break;
				case "open":
				    toggParent.addClass(this.options.openClass);
					toggParent.removeClass(this.options.closedClass);
					menu.removeClass(this.options.hideClass);
					menu.addClass(this.options.showClass);
					break;
				default: 
					toggParent.toggleClass(this.options.openClass);
					toggParent.toggleClass(this.options.closedClass);
					menu.toggleClass(this.options.hideClass);
					menu.toggleClass(this.options.showClass);
			}
			
			if (history && (this.options.enableHistory == "true" || this.options.enableHistory)) {
					
					var key = false;
					switch(this.options.type) {
						case "tree":
						    key = el.get("id");
							break;
						case "menu": 
						    key = el.get("id");
							break;
						default: 
							key = false;
					}
					
					//console.log("cookie key ->"+key);
					//se il ramo è aperto procedo nel marcarlo come da salvare
					if (toggParent.hasClass(this.options.openClass)) { 
						//aggiorno l'indice dei rami aperti
						this.options.bouquet.include(key); 
					} else { //se il ramo è chiuso procedo a rimuoverlo dall'insieme di quelli aperti
						this.options.bouquet.erase(key); }
					//aggiorno il cookie
					this.updateWoodCookie();
			}
			//console.log(menu);
		}

	}, 

	/*
		Function: collapseAll
		
		Chiude tutti i rami di Wood
		
		Parameters:
		
			history - {Boolean} specifica storicizzare la situazione di Wood, defaule *false*
		
	 */	
	
	collapseAll: function(history) {
		var useHistory = false;
		if ($defined(history) && $type(history) == 'boolean') {
			userHistory = history;
		} 
		this.makeWood(this.options.menuToggler,"close",useHistory);
	},

	/*
		Function: expandAll
		
		Apre tutti i rami di Wood
		
		Parameters:
		
			history - {Boolean} specifica storicizzare la situazione di Wood, defaule *false*
	
	 */	
	
	expandAll: function(history) {
		var useHistory = false;
		if ($defined(history) && $type(history) == 'boolean') {
			userHistory = history;
		} 
		this.makeWood(this.options.menuToggler,"open",useHistory);
	},

	/*
		Function: updateWoodCookie
		
		Salva la situazione del Wood e la salva nel Cookie
	
	 */
	
	updateWoodCookie: function() {
		this.options.bouquet = this.options.bouquet.clean();
		Cookie.write(this.options.cookieName,this.options.bouquet, { path: this.options.cookiePath});
	}

};

var Wood = new Class(WoodClass);


/*
	Class: HoofEd
	Genera un piccolo editor da utilizzare per la formattazione del testo nelle textarea
	
	Parameters:
		buttonsFileName - stringa, suffisso con il quale indicare i file catalogo e delle lingue
		basePath - stringa, URI della cartella contentende i file di catalogo e della lingua
		textareaID - stringa, l'id (html) della textarea di riferimento
		toolClass - stringa, classe css da applicare all'HoofEd
		toolPosition - stringa, indica dove posizionare Hoofed rispetto <textareaID>. Accetta i valori: *before* ed *after* (default: before)
		buttons - array, array js dove dichiarare i bottoni (i bottoni devono essere presenti nel file di catalogo)
		lang - stringa, di 2 caratteri rappresenta la lingua dell'editor
		separatorClass - string, css class for the separator element
		separatorElement - string, element type for the separator
		separatorText - string, text separator	
	
	Per il funzionamento di HoofEd si pressupone che esistano due file: il file con il catalogo dei bottoni disponibili e il file contenente le etichette tradotte.
	
	Il file di catalogo si deve chiamare *<buttonsFileName>*.js
	
	Il file con delle traduzioni si deve chiamare *<buttonsFileName>_<lang>*.js

File di supporto: 
	Esempio di file catalogo *<buttonsFileName>*.js 
	(start code)
	
	var HoofEd_buttons = { //il nome dell'oggetto DEVE rimanere inviariato
		"bold" : ['<span class="bold">',"</span>"],
		"underline" : ['<span class="underline"','</span>'],
		"italic" : ['<span class="italic"','</span>']
	}
	(end)
	
	Esempio di file delle traduzioni in italiano
	(start code)
	var HoofEd_buttons_it = {
		"bold" : "Grassetto",
		"underline" : "Sottolineato",
		"italic" : "Corsivo"
	}
	(end) 
		
*/
var HoofEd = new Class({
	Implements: [Events, Options],
	options: {
		buttonsFileName: 'HoofEd_buttons',
		basePath: './hoofed',
		textareaID: "textarea",						//id della textarea
		toolElement: "p",
		toolClass: "HoofEd_tools",					
		toolPosition: "before",						//posizione dei tools: before | after
		buttons: [],								//oggetto contenente Codici!
		lang: 'it',
		separatorClass: "hoofed_separator",
		separatorElement: "span",
		separatorText : " | "
		 
	},	

	/*
		Function: initialize
		*Di servizio* chiamato al momento dell'istanziazione.
		
		Parameters:
			options - obj che racchiude i parametri di istanza: <textareaID> <toolClass> <toolPosition> <button>
	 */	
	initialize: function(options){ //brodo primordiale: set delle opzioni di istanza...
		this.setOptions(options);
		this.options.Map = new Hash();
		//this.options.Map.combine(this.options.buttons);
		this.createButtonMap();
		this.injectTools();

	},
	
	/*
		Function: injectTools
		*Di servizio* inietta la toolbar dove richiesto
		
	 */
	injectTools: function () {
		//istanzio un nuovo elemendo DOM di tipo P e lo inietto prima della rootId
		this.toolBar = new Element(this.options.toolElement, {
			'class': this.options.toolClass
		}).inject($(this.options.textareaID),this.options.toolPosition);
	
		//per ogni elemento della mappa, creo il bottone e lo inietto
		
		var mapLength = this.options.Map.getLength();

		var current = 0;
		$each(this.options.Map, function(value, key, index){
			current = current+1;
			var createdButton = this.createButton(key,value[0],value[1]);
			createdButton.inject(this.toolBar,'bottom');
			
			//hack per lo spazio tra i due link
			//this.toolBar.appendText(' | ','bottom');
			//span.appendText(" | ");
			
			if (current < mapLength) {
				var span = 	new Element(this.options.separatorElement, {
						"class" : this.options.separatorClass
				});
				span.appendText(this.options.separatorText);
				span.inject(this.toolBar, "bottom");
			}
			
		},this);
		
	},
	
	/*
		Function: createButtonMap
		*Di servizio* crea la mappa dei bottoni (etichette + tags)
		
		Carica dinamicamente il file con la configurazione dei bottoni/tag disponibili e carica dinamicamente il file delle etichette nella lingua selezionata
	
		Vengono richiamati i file con suffisso *<buttonsFileName>*.
		 
		Il file contentente il set di bottoni disponibili si chiama *<buttonsFileName>.js* mentre il file della lingua *<buttonsFileName>_<lang>.js*.
		
		Al caricamento di questi due file vengono richiamati i tag e le etichette, necessarie a comporre l'editor che si vuole istanziare, e caricate nella mappa principale dell'editor 
		
	*/
	createButtonMap: function() {
		//buttons = this.options.buttons;
		
		//var loadfile = new Asset.javascript(this.options.basePath+'/'+this.options.buttonsFileName+'.js');
		//loadfile = new Asset.javascript(this.options.basePath+'/'+this.options.buttonsFileName+'_'+this.options.lang+'.js');
		

		var HoofEd_buttons = '';
		var labels = '';
		
		var myJSONRemote = new Request.JSON({
			url: this.options.basePath+'/'+this.options.buttonsFileName+'.js',
			async: false,
			onComplete: function(r1, r2) {
			HoofEd_buttons = r2;
			}
		}).send();

		
		myJSONRemote = new Request.JSON({
			url: this.options.basePath+'/'+this.options.buttonsFileName+'_'+this.options.lang+'.js',
			async: false,
			onComplete: function(r1, r2) {
			labels = r2;
			}
		}).send();
		
		
		HoofEd_buttons = eval( '(' + HoofEd_buttons + ')' );
		labels = eval( '(' + labels + ')' );
		
		this.options.buttons.each(
			function(button) {
				var newB = eval('HoofEd_buttons.'+button);
				var newL = eval('labels.'+button);
				this.options.Map.include(newL,newB);
			},this);
				
	},
	
	/*
		Funtion: createButton
		Crea un nuovo bottone nella toolbar
		
		Parameters:
			lab - etichetta del nuovo bottone
			startCode - tag di apertura
			endCode - tag di chiusura
			
	 */
 	createButton: function(lab, startCode, endCode) {
		//nuovo bottone
		var b = new Element('a');
		
		//setto href a niente
		b.set('href','#');
		
		//setto l'etichetta del link
		b.set('html',lab);
		
		//just for testing
		// b.set('id','tool'+$random(1,9000));
		//setto il tag
		
		// inutile!
		//b.set('BBcode',lab);
		
		//aggiungo l'evento click di cui blocco la propagazione
		this.addEventButton(b);
		
		//rendo a cesare quel che è di cesare
		return b;
 	},
	
	/*
		Function: addEventButton
		*Di servizio* aggiunge gli eventi necessari ai bottoni della toolbar
		
		Parameters:
			el - elemento del dom che rappresenta il bottone
	*/
 	addEventButton: function(el) {
		el.addEvents({
			'click': function(el){
				//ad ogni click: prendo il tagcode corretto  
				this.encloseSelection(el.get('text'));
				return false; 
				}.pass(el, this)
			});
	},

	/*
		Function: encloseSelection
		Servizio che viene chiamato per applicare i tag
		
		- legge dalla mappa dei tag, gli startTag e gli endTag
		- legge la selezione nella textarea
		- applica il tab
	*/
	encloseSelection: function(key) {
		//prendo il tag dalla mappa...
		var tagCode = this.options.Map.get(key);
	
		//setto il marcatore di apertura (delimitatori + valore in posizione 0)
		var prefix = tagCode[0];
		
		//setto il marcatore di chiusura (delimitatori + valore in posizione 1)
		var suffix = tagCode[1];
		
		//prendo il riferimento la textarea...
		var textarea = $(this.options.textareaID);
		
		//codice cross browser... da qui in poi non ci sono personalizzazioni.
		textarea.focus();
		
		var start, end, sel, scrollPos, subst;
		if (typeof(document["selection"]) != "undefined") {
			sel = document.selection.createRange().text;
	 	} 
	 	else if (typeof(textarea["setSelectionRange"]) != "undefined") {
	 		start = textarea.selectionStart;
	 		end = textarea.selectionEnd;
	 		scrollPos = textarea.scrollTop;
	 		sel = textarea.value.substring(start, end);
	 	}
	 	
	 	if (sel.match(/ $/)) { // exclude ending space char, if any
	 		sel = sel.substring(0, sel.length - 1);
	 		suffix = suffix + " ";
	 	}
	 	
	 	subst = prefix + sel + suffix;
	 	if (typeof(document["selection"]) != "undefined") {
	 		var range = document.selection.createRange().text = subst;
	 		textarea.caretPos -= suffix.length;
	 	} 
	 	else if (typeof(textarea["setSelectionRange"]) != "undefined") {
	 		textarea.value = textarea.value.substring(0, start) + subst +
	 		textarea.value.substring(end);
	 		if (sel) {
	 			textarea.setSelectionRange(start + subst.length, start + subst.length);
		 	} else {
		 		textarea.setSelectionRange(start + prefix.length, start + prefix.length);
		 	}
		 	textarea.scrollTop = scrollPos;
	 	}
	}
});