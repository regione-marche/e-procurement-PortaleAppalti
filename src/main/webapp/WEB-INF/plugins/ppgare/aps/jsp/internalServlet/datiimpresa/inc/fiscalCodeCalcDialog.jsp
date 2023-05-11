<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<script src="<wp:resourceURL/>static/js/jquery-ui-1.12.1.min.js"></script>
<wp:headInfo type="CSS" info="jquery/jquery-ui/jquery-ui.css" />

<style>

    .ui-widget {
        font-family: inherit;
    }

    .dialogCFTitle { }
    .dialogCFHeader { }
    .dialogCFContainer {
        width: 100%;
        border-radius: .3em;
    }

    .tab { }

    .tab button {
        background-color: Transparent;
        border: none;
    }


    .tabcontent {
        display: none;
        padding: .5em;
        padding-top: 0em;
    }
    .cfDialogTable {
        width: 100%;
    }
    .cfDialogTable label {
        text-transform: uppercase;
    }
    .cfDialogTable input, .cfDialogTable select { }

    .cfContainer { }


</style>

<div id="container" style="display: none; " class="source-popup portgare-view">

    <h2><wp:i18n key="LABEL_TITLE_GENERA_CF" /></h2>
    <div id="listaVociContainer" class="cfContainer">

        <jsp:include page="cfDialogInc/errori.jsp" />

        <fieldset>

            <div class="fieldset-row" style="padding-left:.5em;">
                <div class="label">
                    <label for="dialogIsItalian"> <wp:i18n key="LABEL_ITALIANO" />? :</label>
                </div>
                <div class="element">
                    <s:select list="maps['sino']" name="dialogIsItalian" id="dialogIsItalian" value="1"
                                        headerKey="" headerValue="" emptyOption="false" />
                </div>
            </div>
        
            <jsp:include page="cfDialogInc/tabItaliani.jsp" />
            <jsp:include page="cfDialogInc/tabEsteri.jsp" />
        </fieldset>
    </div>
</div>

<script type="module">
    //Gli script per il calcolo del codice fiscale sono dei MODULI, quindi, non posso semplicemente includerli
    import { CodiceFiscale } from '<wp:resourceURL/>static/js/CodiceFiscaleJS/codice-fiscale.js';
    import { COMUNI } from '<wp:resourceURL/>static/js/CodiceFiscaleJS/lista-comuni.js';

    window.CodiceFiscale = CodiceFiscale;
    window.COMUNI = COMUNI;
</script>

<script>

//Regex per estrarsi i valori dalla textfield del comune
const comuneFieldRegex = new RegExp("\\s*(.+)\\s*\\((\\w+)\\)\\s*\\-\\s*(\\w+)\\s*");
//Lista di possibili errori ritornabili dalla libreria che genera il codice fiscale
const ERRORS = new Map([
    ["LABEL_ERRORE_CF_NON_VALIDO", "<wp:i18n key='LABEL_ERRORE_CF_NON_VALIDO' />"]
    , ["LABEL_ERRORE_DATA_NON_VALIDA", "<wp:i18n key='LABEL_ERRORE_DATA_NON_VALIDA' />"]
    , ["LABEL_ERRORE_COMUNE_NOME_NON_ESISTE", "<wp:i18n key='LABEL_ERRORE_COMUNE_NOME_NON_ESISTE' />"]
    , ["LABEL_ERRORE_COMUNE_NOME_E_PROV_NON_ESISTE", "<wp:i18n key='LABEL_ERRORE_COMUNE_NOME_E_PROV_NON_ESISTE' />"]
    , ["LABEL_ERRORE_COMUNE_MULTIPLO", "<wp:i18n key='LABEL_ERRORE_COMUNE_MULTIPLO' />"]
]);

//Sostituisce i {0}, {1} .... {n} in valori. E' una spece di String.format, ma, con numero di valori da sostituire indefiniti
function messageTemplateCompleter(_array, startFrom){
    var s = _array[startFrom];
    for (var i = startFrom; i < _array.length - 1; i++) {       
         var reg = new RegExp("\\{" + i + "\\}", "gm");             
         s = s.replace(reg, _array[i + 1]);
    }
    return s;
}


//Autocomplete per il comune
$("#dialogComuneNascitaI").autocomplete({
    source: function (request, response) {  //Invede di passargli una lista, decido io che dati deve mostrare
        var values = Array();

        let provincia = $("#dialogProvinciaNascitaI").val();

        //element[0] = istat, element[1] = provincia, element[2] = comune

        window.COMUNI.every(element => {  //Trovare un modo per farlo fermare dono n risultati trovati
            //Se non è una nazione, se la provincia corrisponde e se il comune inizia per la stringa immessa
            //Aggiungo il comune all'array
            if (element[1] != "EE" && (provincia == '' || provincia == element[1]) && element[2].startsWith(request.term.toUpperCase())) {
                values.push(reverseNormalizationCitta(element[2]) + " (" + element[1] +") - " + element[0]);
                if (Object.keys(values).length == 10)    //Limito il numero di risultati
                    return false;
                }
            return true;    //Con every devo ritornare true, altrimenti finisce il loop
        });

        response(values);
    },
    minLength: 1    //Numero di caratteri minimi da digitare per triggerare l'autocomplete
    // , appendTo: $("#container")
    , select: function (event, ui) {
        if (ui.item) {
            //Se viene selezionato un elemento dell'autocomplete, seleziono anche la relativa provincia
            let match = comuneFieldRegex.exec(ui.item.label);
            if (match != undefined) {
                var activeTab = retrieveActiveTabContent();
                activeTab.find("select[name='dialogProvinciaNascita']").val(match[2]);
            }
        }
    }
    , open: function(event, ui) {
        //Necessario per avere le option dell'autocomplete in primo piano
        //Senza di questo non è possibile selezionare e neanche vedere le varie opzioni
        var dialog = $(this).closest('.ui-dialog');
        if(dialog.length > 0)
            $('.ui-autocomplete.ui-front').css("z-index", dialog.css("z-index") + 1);
    }
});


function initCSS() {
    //START - Inizializzo il contenuto del dialog
    $("#Italiano").show();
    $("#error").text("");
    $("#container").find(".errors").hide();

    let emptyOption = $("#dialogIsItalian").find("option[value='']");
    if (emptyOption.length > 0)
        emptyOption.remove()
    $("#dialogIsItalian").val("1");

    initFieldsValue();
    //END - Inizializzo il contenuto del dialog
}

function openCFGeneratorDialog() {
    var larghezzaSchermo = $(window).width();
    if (larghezzaSchermo > 960)
        var larghezzaDialog = 960;
    else
        var larghezzaDialog = larghezzaSchermo;

    initCSS();

    $("#container").dialog({
        dialogClass: "no-close",
        autoOpen: true,
        closeOnEscape: true,    //Chiude il dialog se si preme ESC
        width: larghezzaDialog - 100,
        /*height: altezzaDialog,*/
        show: { //Animazione quando si visualizza il dialog
            effect: "blind",
            duration: 500
        },
        hide: { //Animazione quando si chiude il dialog
            effect: "blind",
            duration: 500
        },
        modal: true,    //Blocca tutto c'ho che è sotto il dialog (a meno che qualcuno non abbia giocato con il z-index)
        resizable: false,
        focusCleanup: true,
        draggable: false,
        cache: false
        // , zIndex: 1000
        , open: function(){
            //Rimuovo le classi di default dei bottoni del jquery dialog e aggiungo la classe di default del template
            var dialog = $(this).closest('.ui-dialog');
            let buttonSet = dialog.find(".ui-dialog-buttonset");    //Contenitore dei bottoni el jquery-dialog
            if (buttonSet.length > 0) {
                let buttons = buttonSet.find("button");
                buttons.removeClass();  //Rimuovo tutte le classi dai bottoni
                buttons.addClass("button"); //Aggiungo la classe di default a tutti i bottoni
            }
        }
        , buttons : {

            //Calcola
            <wp:i18n key="LABEL_BUTTON_CALCOLA" />:function() {
                if (evaluateFiscalCode()) { //Se false, vuol dire che non sono riuscito a calcolare il CF, quindi, non chiudo il dialog
                    overwriteMainFrameFields(); //Aggiorno i fields dell'anagrafica in caso siano stati cambiati
                    $(this).dialog("close");
                    $(".tabcontent").hide(500);
                    $(".tablinks").removeClass("active");
                }
            }
            //Chiudi
            , <wp:i18n key="BUTTON_CLOSE" />:function() {
                $(this).dialog("close");
                $(".tabcontent").hide(500);
                $(".tablinks").removeClass("active");
            }
        }
    }).position({
        my: 'center'    //Posiziona il dialog al centro dello schermo
    });
    $(".ui-dialog-titlebar").hide();    //Rimuovo la barra sopra del dialog con la X per chiudere il dialog
}

//Sistema i nomi delle città
function reverseNormalizationCitta(valore) {
    //Devo utilizzare gli \u perchè altrimenti il rendering sulla combo mostra caratteri buffi
    //Questi due sono casi particolari, in quanto non utilizzano il normale carattere accentato 
    var toReturn = valore.toUpperCase() == "FE'NIS" ? "F\u00C9NIS".toUpperCase() : undefined;
    toReturn = valore.toUpperCase() == "SAINT-RHE'MY-EN-BOSSES" ? "SAINT-RH\u00C9MY-EN-BOSSES" : toReturn;
    if (toReturn == undefined)
        toReturn = valore.trim()
            .replace(new RegExp(/a\'/gi), '\u00E0')
            .replace(new RegExp(/[^d]e\'/gi), '\u00C8')
            .replace(new RegExp(/i\'/gi), '\u00CC')
            .replace(new RegExp(/o\'/gi), '\u00D2')
            .replace(new RegExp(/u\'/gi), '\u00D9')
        .toUpperCase()
    return toReturn;

}

//Prende i valori nello step dati anagrafici e li inserisce nel dialog (su entrambi i tab)
function initFieldsValue() {
    //Utilizzo i nomi e non gli ID, perchè i tab sono in div completamente diversi ed hanno field diversi
    //Utilizzare l'id avrebbe richiesto di duplicare molte righe
    let comNascita = $("#comuneNascita").val()
    $("input[name='dialogNome']").val($("#nome").val());
    $("input[name='dialogCognome']").val($("#cognome").val());
    $("input[name='dialogDataNascita']").val($("#dataNascita").val());
    $("input[name='dialogComuneNascita']").val(comNascita);
    $("select[name='dialogProvinciaNascita']").val($("#provinciaNascita").val());
    $("select[name='dialogSesso']").val($("#sesso").val());
    valorizeNazionalityCombobox();
    if (comNascita != '' && $("select[name='dialogNazione'] option[value='" + comNascita + "']").length > 0)
        $("select[name='dialogNazione']").val($("#comuneNascita").val());
}

//Valorizza la combo delle nazionalità
function valorizeNazionalityCombobox() {
    let nazioni = $("#dialogNazioneE");
    if (nazioni.find("option").length == 1) {   //Se c'è una solo OPTION vuol dire che c'è solo il valore nullo, quindi, devo aggiungere le nazioni
        $.each(window.COMUNI, function(index, comune) {
            if (comune[1] == "EE") {
                //Sistemo gli accenti
                let nazionalitaNormalizzata = reverseNormalizationCitta(comune[2]);
                nazioni.append($("<option>")
                    .prop('value', nazionalitaNormalizzata)
                    .text(nazionalitaNormalizzata));
                }
        });
    }
}

//Sovrascrive i dati dei tab quando li cambio, in modo da non dover ripetere i dati ogni volta che si cambia
function overwriteMainFrameFields() {
    var activeTab = retrieveActiveTabContent();
    $("#nome").val(activeTab.find("input[name='dialogNome']").val())
    $("#cognome").val(activeTab.find("input[name='dialogCognome']").val());
    $("#dataNascita").val(activeTab.find("input[name='dialogDataNascita']").val());

    let comNascita = activeTab.find("input[name='dialogComuneNascita']")  //CASO: Italiani
    if (comNascita.length > 0) {
        var comNascitaToWrite = comNascita.val();
        if (comuneFieldRegex.test(comNascitaToWrite))   //Controllo se è stato salvato il comune con l'autocomplete
            comNascitaToWrite = comuneFieldRegex.exec(comNascitaToWrite)[1].trim(); //Mi estraggo il nome del comune
        $("#comuneNascita").val(comNascitaToWrite);
        //Se non è presente il comune, vuol dire non è presente neanche la provincia (o non dovrebbe)
        let provNascita = activeTab.find("select[name='dialogProvinciaNascita']");
        if (provNascita.length > 0)
            $("#provinciaNascita").val(provNascita.val());
    } else {
        let nazione = activeTab.find("select[name='dialogNazione']"); //CASO: Esteri
        if (nazione.length > 0)
            $("#comuneNascita").val(nazione.val());
        $("#provinciaNascita").val(""); //Gli stranieri non devono valorizzare la provincia
    }
    $("#sesso").val(activeTab.find("select[name='dialogSesso']").val());
    return false;   //break
}

//Sovrascrivo i dati tra i tab, in modo da avere entrambi i tab con le stesse informazioni
function overwriteTabsFields() {
    var activeTab = retrieveActiveTabContent();
    $(".tabcontent").each(function() {
        if ($(this).css("display") == "none") {
            $(this).find("input[name='dialogNome']").val(activeTab.find("input[name='dialogNome']").val());
            $(this).find("input[name='dialogCognome']").val(activeTab.find("input[name='dialogCognome']").val());
            $(this).find("input[name='dialogDataNascita']").val(activeTab.find("input[name='dialogDataNascita']").val());
            let comNascitaOld = $(this).find("input[name='dialogComuneNascita']");
            let comNascitaNew = activeTab.find("input[name='dialogComuneNascita']");
            if (comNascitaOld.length > 0 && comNascitaNew.length > 0)
                comNascitaOld.val(comNascitaNew.val());

            let provNascitaOld = $(this).find("select[name='dialogProvinciaNascita']");
            let provNascitaNew = activeTab.find("select[name='dialogProvinciaNascita']");
            if (provNascitaOld.length > 0 && provNascitaNew.length > 0)
                provNascitaOld.val(provNascitaNew.val());
            $(this).find("select[name='dialogSesso']").val(activeTab.find("select[name='dialogSesso']").val());
        }
    });
}
function openTab(toShow) {
    overwriteTabsFields();
    //In caso sia cambiato tab sbianco e nascondo la sezione con gli errori
    $("#error").text("");
    $("#container").find(".errors").hide();

    //Nacondo il div corrente con un delay, creando una piccola animazione
    $(".tabcontent").hide(500, function() {
        //Disattivo temporaneamente i bottoni per evitare dei cambi di tab veloci che possono causare glitch
        //Mostro il nuovo div con un delay, creando una piccola animazione
        $("#" + toShow).show(500);
    });

}


//Controlla che tutti i campi obbligatori siano valorizzati (non controlla però che siano validi)
function checkFields() {
    var errore = ''

    let activeTab = retrieveActiveTabContent();
    let nome = activeTab.find("input[name='dialogNome']");
    let cognome = activeTab.find("input[name='dialogCognome']");
    let dataNascita = activeTab.find("input[name='dialogDataNascita']");
    let comune = activeTab.find("input[name='dialogComuneNascita']");
    let provincia = activeTab.find("select[name='dialogProvinciaNascita']");
    let sesso = activeTab.find("select[name='dialogSesso']");
    let nazione = activeTab.find("select[name='dialogNazione']");

    //Campi obbligatori su entrambi i tab
    if (nome.val() != ''
        && cognome.val() != ''
        && sesso.val() != ''
        && dataNascita.val() != '') {

        //Rimosso il controllo sul nome, perchè bisognerebbe fare una regex più completa (nomi non italiani)
        // let isValidName = RegExp("[a-zA-Z\\sàáÀÁèéÈÉìíÌÍòóÒÓùúÙÚ]+");
        // if (isValidName.test(nome) && isValidName.test(cognome)) {
            if (comune.length > 0) {
                //Campo obbligatorio tab italiani
                if (comune.val() == '')
                    errore = "<wp:i18n key='LABEL_REQUIRED_COMUNE' />";
            } else {
                //Campo obbligatorio specifico tab esteri
                if (nazione.val() == '')
                    errore = "<wp:i18n key='LABEL_REQUIRED_NAZIONE' />";
            }
        // } else  //Un nome 
            // errore = "<wp:i18n key='LABEL_ERRORE_DATI_NON_VALIDI' />"
    } else 
        errore = "<wp:i18n key='LABEL_REQUIRED_FIELD_GENERATE_CF' />";

    return errore;
}


//Recupera il contenuto del tab attualmente attivo
function retrieveActiveTabContent() {
    var activeTab;
    $(".tabcontent").each(function() {
        if ($(this).css("display") != "none") {
            activeTab = $(this);
            return false;
        }
    });
    return activeTab;
}

function evaluateFiscalCode() {

    let errore = checkFields(); //Controllo eventuali errori nei campi (controlli base) 
    if (errore == '') {
        let cf = getCF();
        if (cf != undefined) {  //Non ci sono stati errori, quindi, posso valorizzare la field sull'anagrafica
            $("#codiceFiscale").val(cf.toString());
        } else {
            errore = "ERRORE";
        }
    } else {
        //Mostra il messaggio di errore ritornato dal controllo dei campi
        $("#error").text(errore);
        $("#container").find(".errors").show(500);
    }

    return errore == '';
}

//Ritorna il codice fiscale e mostra il div di errore in caso di problemi
function getCF() {
    var cf;

    let activeTab = retrieveActiveTabContent(); //Recupero il tab attivo
    let nome = activeTab.find("input[name='dialogNome']");
    let cognome = activeTab.find("input[name='dialogCognome']");
    let dataNascita = activeTab.find("input[name='dialogDataNascita']");
    let comune = activeTab.find("input[name='dialogComuneNascita']");
    let provincia = activeTab.find("select[name='dialogProvinciaNascita']");
    let sesso = activeTab.find("select[name='dialogSesso']");
    let nazione = activeTab.find("select[name='dialogNazione']");


    let splitted = dataNascita.val().split("/");
    var dataNascitaConvertita = dataNascita;
    //La liberia che calcola il CF avvolte da problemi con il format dd/MM/yyyy,  meglio usare yyyy/MM/dd
    if (splitted.length == 3)
        dataNascitaConvertita = splitted[2] + "/" + splitted[1] + "/" + splitted[0];

    try {

        //CASO: Soggetto italiano 
        if (provincia.length > 0) {
            let istat = undefined;  //Presente solo se E' stato utilizzato l'autocomplete
            let comuneEstratto = comune.val()
            if (comune.length > 0) {
                //In caso si abbia usato l'autocomplete, sulla textfield del comune si avranno anche provincia ed istat
                //in questo caso mi estraggo comune ed istat con una regex
                if (comuneFieldRegex.test(comuneEstratto)) {
                    let match = comuneFieldRegex.exec(comuneEstratto);
                    comuneEstratto = match[1].trim();
                    istat = match[3];
                }
            }
            //CASO: Soggetto italiano con provincia
            //L'istat viene inserito solo tramite autocomplete nel campo comune
            //quindi lo inserisco solo in questo caso
            if (provincia.val() != '')
                cf = new window.CodiceFiscale({
                    name : nome.val(),
                    surname : cognome.val(),
                    gender : sesso.val(),
                    // birthday : dataNascita.val(),
                    birthday : dataNascitaConvertita,
                    birthplace : comuneEstratto,
                    birthplaceProvincia : provincia.val(),
                    istat: istat
                });
            else    //CASO: Soggetto italiano senza provincia
                cf = new window.CodiceFiscale({
                    name : nome.val(),
                    surname : cognome.val(),
                    gender : sesso.val(),
                    birthday : dataNascitaConvertita,
                    birthplace : comuneEstratto
                });
        } else  //CASO: Soggetto estero
            cf = new window.CodiceFiscale({
                name : nome.val(),
                surname : cognome.val(),
                gender : sesso.val(),
                birthday : dataNascitaConvertita,
                birthplace : nazione.val()
            });
    } catch(error) {
        cf = undefined;
        let splitted = error.message.split('|');
        var toPrint = error.message;
        //Per non creare n if, ho fatto un append dei parametri da sostituire nella stringa, separati da "|"
        //Le label devono avere {n} in modo che messageTemplateCompleter(...) possa sostituire il valore
        if (splitted.length > 1) {  //L'errore ha dei parametri
            splitted[0] = ERRORS.get(splitted[0]);  //Il valore 0 deve essere il messaggio nella tabella localstrings
            if (splitted[0] != undefined)
                toPrint = messageTemplateCompleter(splitted, 0);
 
        } else {    //L'errore non ha parametri
            toPrint = ERRORS.get(toPrint);
            if (toPrint == '' || toPrint == undefined)
                toPrint = error.message;
        }
        //Modifico il messaggio di errore nel div con gli errori e lo visualizzo
        $("#error").text(toPrint);
        $("#container").find(".errors").show(500);
    }

    return cf;
}

//Quando cambio provincia sbianco il comune
$( "#dialogProvinciaNascitaI" ).change(function() {
    let comune = $("input[name='dialogComuneNascita']");
    //Se non ho "comune (provincia) - istat", non sono in grado di capire se la provincia sia sbagliata o se fosse la provincia precedente quella errata
    if (comuneFieldRegex.test(comune.val())) {
        //CASO: Comune valorizzato tramite autocomplete
        //Controllo che la provincia nella field comune e quella appena selezionata coincidano
        //Se non coincidono sbianco il comune
        let match = comuneFieldRegex.exec(comune.val());
        let provincia = match[2].trim();
        if (provincia != $(this).val())
            comune.val("");
    }
});

$("#dialogIsItalian").change(function() {
    if ($(this).val().toLowerCase() == '1') {
        openTab("Italiano");
    } else {
        openTab("Estero");
    }
});


</script>