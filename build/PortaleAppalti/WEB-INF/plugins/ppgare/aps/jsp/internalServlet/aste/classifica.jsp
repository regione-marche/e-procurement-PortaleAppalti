<%@ taglib prefix="wp" uri="aps-core.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<script type="text/javascript" src='<wp:resourceURL/>static/js/jquery.treeview.js'></script>

<s:set var="dettGara" value="%{#session['dettGara']}" />
<s:set var="gara" value="%{#dettGara.datiGeneraliGara}" />
<c:set var="simboloOfferta"><s:if test="%{dettaglioAsta.tipoOfferta == 1}">%</s:if><s:else>&euro;</s:else></c:set>

<%-- numero dei rilanci effettuati --%>
<c:set var="numRilanci" value="0"/>
<s:iterator var="item" value="%{rilanci}">
	<c:set var="numRilanci" value="${numRilanci + 1}" />
</s:iterator>

<%-- codice del lotto --%>
<s:set var="codLotto">${param.codiceLotto}</s:set>
<c:set var="codiceInternoLotto" value=""/>  
<s:iterator var="lotto" value="%{#dettGara.lotto}" status="status">
	<s:if test="#lotto.codiceLotto == #codLotto">
		<c:set var="codiceInternoLotto"><s:property value="#lotto.codiceInterno" /></c:set> 
	</s:if> 
</s:iterator>


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp">
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2><wp:i18n key="LABEL_ASTA_ELETTRONICA" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_ASTA_RIEPILOGO" />
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />

	<c:if test="${numRilanci <= 0}">
		<wp:i18n key='LABEL_OFFERTA_INIZIALE_ASTA_NON_TROVATA'/> <s:property value="codice" />
		<s:if test="%{codiceInternoLotto != ''}" >
			- <wp:i18n key='LABEL_LOTTO'/> <s:property value="codiceInternoLotto" />
		</s:if> 
	</c:if>
	<c:if test="${numRilanci > 0}">
		<div class="detail-section first-detail-section">
			<h3 class="detail-section-title">
				<span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_DATI_GENERALI" />
			</h3>
	
			<div class="detail-row">
				<label><wp:i18n key="LABEL_TITOLO" /> : </label>
				<s:property value="%{#gara.oggetto}" />
			</div>
			
			<div class="detail-row">
				<label><wp:i18n key="LABEL_TIPO_APPALTO" /> : </label>
				<s:iterator value="maps['tipiAppalto']">
					<s:if test="%{key == #gara.tipoAppalto}">
						<s:property value="%{value}" />
					</s:if>
				</s:iterator>
			</div>
			
			<div class="detail-row">
				<label><wp:i18n key="LABEL_RIFERIMENTO_PROCEDURA" /> : </label>
				<s:property value="codice" />
				<c:if test="${! empty codiceInternoLotto}" >
					- <wp:i18n key="LABEL_LOTTO" /> ${codiceInternoLotto}
				</c:if>
			</div>
		
			<div class="detail-row">
				<label><wp:i18n key="LABEL_FASE" /> : </label>
				<s:property value="dettaglioAsta.fase" />			
			</div>
			
			<div class="detail-row">
				<label><wp:i18n key="LABEL_DATA_ORA_APERTURA_ASTA" /> : </label>
				<s:date name="dettaglioAsta.dataOraApertura" format="dd/MM/yyyy HH:mm:ss" />
			</div>
			
			<div class="detail-row">
				<label><wp:i18n key="LABEL_DURATA_MINIMA" /> : </label>
				<s:property value="dettaglioAsta.durataMinima" />			
			</div>
			
			<div class="detail-row">
				<label><wp:i18n key="LABEL_DURATA_MASSIMA" /> : </label>
				<s:property value="dettaglioAsta.durataMassima" />			
			</div>
			
			<div class="detail-row">
				<label><wp:i18n key="LABEL_TEMPO_BASE" /> : </label>
				<s:property value="dettaglioAsta.tempoBase" />			
			</div>
			
			<div class="detail-row">
				<label><wp:i18n key="LABEL_DATA_ORA_CHIUSURA_ASTA" /> : </label>
				<s:date name="dettaglioAsta.dataOraChiusura" format="dd/MM/yyyy HH:mm:ss" />
	<%-- 			<s:iterator value="maps['terminiAsta']"> --%>
	<%-- 				<s:if test="%{key == dettaglioAsta.termineAsta}"><s:property value="%{value}"/></s:if> --%>
	<%-- 			</s:iterator> --%>
			</div>
	
			<div class="detail-row">
				<label><wp:i18n key="LABEL_TEMPO_MANCANTE_CHIUSURA" /> : </label>
				<strong><label id="tempoMancanteAllaChiusura" name="tempoMancanteAllaChiusura"></label></strong>
			</div>				
					
			<c:if test="${not empty dettaglioAsta.scartoRilancioMinimo}">				
				<div class="detail-row">
					<label><wp:i18n key="LABEL_SCARTO_MINIMO_RILANCIO_DA_ULTIMA_OFFERTA" /> : </label>
					<s:text name="format.money5dec"><s:param value="dettaglioAsta.scartoRilancioMinimo"/></s:text>&nbsp;${simboloOfferta}
				</div>
			</c:if>
			
			<c:if test="${not empty dettaglioAsta.scartoRilancioMassimo}">
				<div class="detail-row">
					<label><wp:i18n key="LABEL_SCARTO_MASSIMO_RILANCIO_DA_ULTIMA_OFFERTA" /> : </label>
					<s:text name="format.money5dec"><s:param value="dettaglioAsta.scartoRilancioMassimo"/></s:text>&nbsp;${simboloOfferta}
				</div>    
			</c:if>
			
			<div class="detail-row">
				<s:if test="%{dettaglioAsta.tipoOfferta == 1}">
					<label><wp:i18n key="LABEL_ULTIMO_RIBASSO_OFFERTO" /> : </label>
					<s:text name="format.money5dec"><s:param value="dettaglioAsta.ribassoUltimoRilancio"/></s:text>&nbsp;${simboloOfferta}			
				</s:if>
				<s:else>
					<label><wp:i18n key="LABEL_ULTIMO_IMPORTO_OFFERTO" /> : </label>
					<s:text name="format.money5dec"><s:param value="dettaglioAsta.importoUltimoRilancio"/></s:text>&nbsp;${simboloOfferta}
				</s:else>					
			</div>
			
	<!-- 		<div class="detail-row"> -->
	<!-- 			<label>Tipo classifica : </label> -->
	<%-- 			<s:iterator value="maps['tipiClassificaAsta']"> --%>
	<%-- 				<s:if test="%{key == dettaglioAsta.tipoClassifica}"><s:property value="%{value}"/></s:if> --%>
	<%-- 			</s:iterator> --%>
	<!-- 		</div>		 -->
	
			<div class="detail-row">			
				<s:set var="elencoClassifica" value="classifica" />		
				<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/aste/inc/iteratorClassifica.jsp" >
					<jsp:param name="tipoClassifica" value="${dettaglioAsta.tipoClassifica}" />
					<jsp:param name="tipoOfferta" value="${dettaglioAsta.tipoOfferta}" />
					<jsp:param name="maxNumeroDecimali" value="${maxNumeroDecimali}" />
				</jsp:include>
			</div>
			
			<c:if test="${numRilanci <= 1}">
				<div class="note">
					<label><wp:i18n key="LABEL_PRESENTE_SOLO_OFFERTA_SENZA_RILANCI" /> </label>
				</div>
			</c:if>
						
		</div>	
			
		<div class="azioni">
			<s:if test="%{dettaglioAsta.termineAsta <= 0}" >
				<form action="<wp:action path="/ExtStr2/do/FrontEnd/Aste/classifica.action"/>" method="post" class="azione">
					<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
					
					<div>
						<wp:i18n key="BUTTON_REFRESH" var="valueRefreshButton" />
						<wp:i18n key="TITLE_REFRESH_CLASSIFICA" var="titleRefreshButton" />
						<s:submit value="%{#attr.valueRefreshButton}" title="%{#attr.titleRefreshButton}" cssClass="button" />
						<input type="hidden" name="codice" value="${param.codice}" />
						<input type="hidden" name="codiceLotto" value="${param.codiceLotto}" />
					</div>
				</form>
			</s:if>
			<form action="<wp:action path="/ExtStr2/do/FrontEnd/Aste/listRilanci.action"/>" method="post" class="azione">
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
				
				<div>
					<wp:i18n key="BUTTON_STORIA_RILANCI" var="valueStoriaRilanciButton" />
					<wp:i18n key="TITLE_STORIA_RILANCI" var="titleStoriaRilanciButton" />
					<s:submit value="%{#attr.valueStoriaRilanciButton}" title="%{#attr.titleStoriaRilanciButton}" cssClass="button" />
					<input type="hidden" name="codice" value="${param.codice}" />
					<input type="hidden" name="codiceLotto" value="${param.codiceLotto}" />
					<input type="hidden" name="fromPage" value="classifica" />
				</div>
			</form>
	 		<s:if test="%{dettaglioAsta.termineAsta > 0 && abilitaConfermaOfferta}" > 
				<form action="<wp:action path="/ExtStr2/do/FrontEnd/Aste/confirmOfferta.action"/>" method="post" class="azione">
					<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
					
					<div>
						<wp:i18n key="BUTTON_CONFERMA_OFFERTA_FINALE" var="valueConfermaOffertaButton" />
						<s:submit value="%{#attr.valueConfermaOffertaButton}" title="%{#attr.valueConfermaOffertaButton}" cssClass="button" />
						<input type="hidden" name="codice" value="${param.codice}" />
						<input type="hidden" name="codiceLotto" value="${param.codiceLotto}" />
					</div>
				</form>
			</s:if>
			<s:else>
				<s:if test="%{dettaglioAsta.attiva && abilitaRilancio}" >	
					<c:set var="url"><wp:action path="/ExtStr2/do/FrontEnd/Aste/openRilancio.action"/></c:set>
					<c:if test="${prezziUnitari}">
						<c:set var="url"><wp:action path="/ExtStr2/do/FrontEnd/Aste/openRilancioPrezziUnitari.action"/></c:set>
					</c:if>
					
					<form action="${url}" method="post" class="azione">
						<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
						
						<div id="eseguiRilancio">
							<wp:i18n key="BUTTON_ESEGUI_RILANCIO" var="valueEseguiRilancioButton" />
							<s:submit value="%{#attr.valueEseguiRilancioButton}" title="%{#attr.valueEseguiRilancioButton}" cssClass="button" />
							<input type="hidden" name="codice" value="${param.codice}" />
							<input type="hidden" name="codiceLotto" value="${param.codiceLotto}" />
						</div>
					</form>
				</s:if>
			</s:else>
		</div>
	</c:if>

	<div class="back-link">
		<s:if test="%{lottiDistinti}">
			<c:set var="href"><wp:action path="/ExtStr2/do/FrontEnd/Aste/openAsta.action"/></c:set>			
		</s:if>
		<s:else>
			<c:set var="href"><wp:action path="/ExtStr2/do/FrontEnd/Bandi/view.action"/></c:set>
		</s:else>
		<a href="${href}&amp;codice=${codice}&amp;ext=&amp;${tokenHrefParams}">
			<wp:i18n key="LINK_BACK_TO_PROCEDURE" />
		</a>
	</div>
</div>


<!-- 
////////////////////////////////////////////////////////////////////////////////
                       Script di gestione countdown  
////////////////////////////////////////////////////////////////////////////////
-->
<s:url id="reloadAstaUrl" namespace="/do/FrontEnd/aste" action="reloadAstaAsync" />
<c:set var="dataOraChiusura"><s:date name="dettaglioAsta.dataOraChiusura" format="yyyy-MM-dd HH:mm:ssZ" /></c:set>
<c:set var="chiusuraDD"><s:date name="dettaglioAsta.dataOraChiusura" format="dd" /></c:set>
<c:set var="chiusuraMM"><s:date name="dettaglioAsta.dataOraChiusura" format="MM" /></c:set>
<c:set var="chiusuraYY"><s:date name="dettaglioAsta.dataOraChiusura" format="yyyy" /></c:set>
<c:set var="chiusuraHH"><s:date name="dettaglioAsta.dataOraChiusura" format="HH" /></c:set>
<c:set var="chiusuraMI"><s:date name="dettaglioAsta.dataOraChiusura" format="mm" /></c:set>
<c:set var="chiusuraSS"><s:date name="dettaglioAsta.dataOraChiusura" format="ss" /></c:set>

<script type="text/javascript">
<!--//--><![CDATA[//><!--                         
////////////////////////////////////////////////////////////////////////////////
	
	// timer del termine scadenza asta...
	var actionUrl = '${reloadAstaUrl}';
	var codice = '${codice}';
	var codiceLotto = '${codiceLotto}';		
	var timerId = "#tempoMancanteAllaChiusura"; 	
	var dataChiusura = new Date('${chiusuraYY}', ('${chiusuraMM}'-1), '${chiusuraDD}',
								'${chiusuraHH}', '${chiusuraMI}', '${chiusuraSS}');
	var timerInteval = 1000; 		// in ms
	var reloadAstaTimer = 10000;  	// in ms
	var lastReload = Date.now();
			
	// document ready event... 
	$(document).ready(function() {
		
		// create a timer...
		var obj = function(interval){
	        return function(){	        	
	        	var currentTime = Date.now();

  	        	// gestione del countdown dell'asta... 
	            if(currentTime >= dataChiusura.getTime()) {
	            	// end timer execution...
	            	$(timerId).text("<wp:i18n key='LABEL_TERMINATA'/>"); // il " + formatDatetime(dataChiusura))
	            	$("#eseguiRilancio").hide();
	            } else {
	            	// schedule next timer execution...
	                setTimeout(obj, interval);
	                try{
	                	//updateTimer(dataChiusura.getTime(), currentTime);
	                	updateTimer(dataChiusura, currentTime);
	                } catch(e){	                
	                }	                
	            }
	            	        	
//	        	//  AJAX, info asta reload...
//	            if(Math.abs(currentTime-lastReload) > reloadAstaTimer) {
//	            	reloadInfoAstaAsync();	
//	            	lastReload = currentTime;
//	            }		            	            	        
	        };
	    }(timerInteval);	  
	    
	    // ...and schedule first timer execution...	    
	    setTimeout(obj, timerInteval);
	});	
	
	function updateTimer(endTime, currentTime){
		var x = Math.abs(endTime-currentTime); // ms
		x = Math.floor(x/1000);
		var ss = ("0" + (x % 60)).slice(-2);
		x = Math.floor(x/60);
		var nn = ("0" + (x % 60)).slice(-2);
		x = Math.floor(x/60);
		var hh = ("0" + (x % 24)).slice(-2);
		x = Math.floor(x/24);
		var dd = (x > 0 ? x + " giorni" : "");		
	    $(timerId).text(dd + " " + hh + ":" + nn + ":" + ss);
    }
	
	function formatDatetime(d) {
		return ("0" + d.getDate()).slice(-2) + "/" +
	    	   ("0" + (d.getMonth()+1)).slice(-2) + "/" +
	           (d.getFullYear()) + " " +
	    	   ("0" + d.getHours()).slice(-2) + ":" +
	           ("0" + d.getMinutes()).slice(-2) + ":" +
	           ("0" + d.getSeconds()).slice(-2);
	}	
	
//	// AJAX refresh...
//	function reloadInfoAstaAsync() {
//		return $.ajax({
//			url: actionUrl,
//		    type: 'POST',
//		    dataType: 'json',
//		    data: { codice: codice, codiceLotto: codiceLotto }
//			})
//		  	.done(function(data) {
//		  		var json = JSON.parse(JSON.stringify(data));
//		  		// json.asta
//		  		// json.classifica		  		
//		  		alert("Hey JSON data here!!!");
//		  		
//		  		// ricarica la nuova data di chiusura dell'asta...
//		  		//dataChiusura = new Date(json.asta.dataOraChiusura);
//		  		alert(json.asta.dataOraChiusura);
//			})
//			.always(function(data) {
//				//...	
//			})
//			.fail(function(data) {
//				writelog("ERROR: " + data.responseCode);  //+ " " + data.responseText );
//				writelog("Import interrotto.");
//			});
//	}

////////////////////////////////////////////////////////////////////////////////
//--><!]]></script>
