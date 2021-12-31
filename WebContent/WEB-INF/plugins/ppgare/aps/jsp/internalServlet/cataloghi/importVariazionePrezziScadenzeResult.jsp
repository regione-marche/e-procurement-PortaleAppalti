<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<script type="text/javascript" src='<wp:resourceURL/>static/js/jquery.treeview.js'></script>
<script type="text/javascript" src="<wp:resourceURL/>static/js/jquery-ui-1.12.1.min.js"></script>
<wp:headInfo type="CSS" info="jquery/jquery-ui/jquery-ui.css" />

<%--
<script type="text/javascript">
<!--//--><![CDATA[//><!--
	$(document).ready(function() {

		// tutti gli alberi compressi
		$(".filetree").treeview({collapsed: true});
		
	});
//--><!]]></script>
 --%>
<wp:headInfo type="CSS" info="jquery/treeview/jquery.treeview.css" />


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">
	
	<h2><wp:i18n key="TITLE_PAGE_CATALOGHI_IMPORTA_VARIAZIONE_PREZZI" /></h2>
	
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/warning_javascript.jsp" />	
  
 	<div>
		<wp:i18n key="LABEL_STATO_IMPORTAZIONE" /> <span id="progressval" value="" /> 
	</div>
	<div id="progressbar" ></div>
	
	<%--
	<s:textarea id="txtlog" name="txtlog" value="" rows="15" cols="80" />				
 	--%> 	
		
	<div id="erroriImport">		
		<p>
			<wp:i18n key="LABEL_PRODOTTI_PROCESSATI" />: <span id="inputRows" value="0" />
		</p>
		<p>
			<wp:i18n key="LABEL_PRODOTTI_AGGIORNATI_CORRETTAMENTE" />: <span id="successRows" value="0" />
		</p>
		<p>
			<wp:i18n key="LABEL_PRODOTTI_SCARTATI_PER_NO_VARIAZIONI" />: <span id="skippedRows" value="0" />
		</p>
		<p>
			<wp:i18n key="LABEL_PRODOTTI_NON_AGGIORNATI_PER_DATI_INVALIDI" />: <span id="errorRows" value="0" /> 
		</p>
	</div>
	<div id="erroriImport" class="errors">
		<ul id="prodottiErrati" class="filetree">
		</ul>	
	</div>
 	
	<div class="azioni" id="azioni" >
		<form action="<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/openPageImportVariazionePrezziScadenze.action" />" method="post" class="azione">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			<div>			
				<wp:i18n key="BUTTON_RICARICA" var="valueRicaricaButton" />
				<wp:i18n key="TITLE_BACK_TO_UPLOAD_XLS_PREZZI" var="titleRicaricaButton" />
				<s:submit value="%{#attr.valueRicaricaButton}" title="%{#attr.titleRicaricaButton}" cssClass="button" />
				<input type="hidden" name="catalogo" value='<s:property value="catalogo"/>'/>
				<input type="hidden" name="ext" value='${param.ext}'/>
				<input type="hidden" id="allegatoFileName" value="${allegatoFileName}" />
			</div>
		</form>
	</div>
	<div class="back-link">
		<a href='<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/openGestioneProdotti.action" />&amp;catalogo=<s:property value="%{catalogo}"/>&amp;ext=${param.ext}&amp;${tokenHrefParams}'>
			<wp:i18n key="LINK_BACK_TO_GESTIONE_PRODOTTI" />
		</a>
	</div>
</div>


<!-- 
***************************************************************************
* SCRIPT GESTIONE ASINCRONA  
*************************************************************************** 
-->
<s:url id="actionUrl" namespace="/do/FrontEnd/Cataloghi" action="importVariazionePrezziScadenzeAsync" />

<script type="text/javascript">
<!--//--><![CDATA[//><!--
     
	var actionUrl = '${actionUrl}';
	var catalogo = '${catalogo}';
	var allegatoFileName = $("#allegatoFileName").val();
	
	$(document).ready(function() {
	
		// rendi invisisibili pulsanti e risultati
		$("#azioni").hide();
		$("#erroriImport").hide();
			
		progressbar(0);
		writelog("<wp:i18n key='LABEL_LOG_IMPORT_INIZIO' />");
		
		importAsync();
		
		function importAsync() {
			return $.ajax({
				url: actionUrl,
			    type: 'POST',
			    dataType: 'json',
			    data: { allegatoFileName: allegatoFileName, catalogo: catalogo }
				})
			  	.done(function(data) {
			  		var json = JSON.parse(JSON.stringify(data));
			  		progressbar( 100*(eval(json.rowIndex)+1)/eval(json.rowCount) );
			  		writelog("..."+(eval(json.rowIndex)+1)+"/"+json.rowCount);
			  		if(eval(json.isRunning)) {
			  			importAsync();
		    			sleep(1000);
		    		} else {
		    			//writelog(JSON.stringify(data));
		    			if(eval(json.isCompleted)) {
		    				progressbar(100);
		    				writelog("<wp:i18n key='LABEL_LOG_IMPORT_COMPLETATO' />");
		    			} else {
		    				writelog("Import interrotto.");
		    			}
		    			mostraRisultati(json);
		    		}
				})
				.always(function(data) {
					//...
				})
				.fail(function(data) {
					writelog("ERROR: " + data.responseCode);  //+ " " + data.responseText );
					writelog("<wp:i18n key='LABEL_LOG_IMPORT_INTERROTTO' />");
				});
		}
		
		function progressbar(val) {
			val = val | 0; //Math.round(val);
			$("#progressbar").progressbar({value: val});
			$("#progressval").text(val+"%");
		}
		
		function writelog(msg) {
			//console.log(msg);
		}
		
		//
		// inserisci qui il codice di visualizzazione dei risultati
		//
		function mostraRisultati(jsonResponse) {
			// aggiorna i risultati
			$("#inputRows").text(jsonResponse.inputRows);
	  		$("#successRows").text(jsonResponse.successRows);
	  		$("#skippedRows").text(Object.keys(jsonResponse.skippedRows).length);
			
			var n = Object.keys( jsonResponse.errorRows ).length;
			if(n > 0) {
				var list = "";
				var i = 0;
			    for(var key in jsonResponse.errorRows) {
			    	list = list + 
			    		"<li class='" + (i==0 ? "first" : "") + " " + (i==n-1 ? "last" : "") + "'>" +
				    	"<wp:i18n key='LABEL_PRODOTTO_ALLA_RIGA' /> " + (eval(key)+1) +
				        "<ul id='errorRow"+ (eval(key)+1) +"'>";
		
				    var m = Object.keys( jsonResponse.errorRows[key] ).length;
				    var j = 0;
				    for(var value in jsonResponse.errorRows[key]) {	
				    	list = list + 
				    		"<li class='" + (j==0 ? "first" : "") + " " + (j==m-1 ? "last" : "") + "'>" +
				    		jsonResponse.errorRows[key][value] +  
				    		"</li>";
				    	j++;
				    }
				    list = list + 
				    	"</ul>" + 
				        "</li>";
				    i++;
			    }
			    $("#prodottiErrati").append(list);
		    	$("#errorRows").text(n);
		  		$("#erroriImport").show();
			}
			
			// visualizza pulsanti e risultati
			$("#azioni").show();
	
			// tutti gli alberi compressi
			$(".filetree").treeview({collapsed: true});
		}
	});
	
	
	function sleep(milliseconds) { 
	    let timeStart = new Date().getTime(); 
	    while (true) { 
	      let elapsedTime = new Date().getTime() - timeStart; 
	      if (elapsedTime > milliseconds) { 
	        break; 
	      } 
	    } 
	  }

//--><!]]></script>


