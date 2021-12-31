<%@ taglib prefix="wp"  uri="aps-core.tld" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="s"   uri="/struts-tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<wp:headInfo type="CSS" info="jquery/jquery-ui/jquery-ui.css" />
<link href="<wp:resourceURL/>static/css/parsley.css" rel="stylesheet"></link>

<script type="text/javascript" src="<wp:resourceURL/>static/js/jquery-ui-1.12.1.min.js"></script>
<script type="text/javascript" src="<wp:resourceURL/>static/js/jquery.dataTables.min.js"></script>

<c:set var="maxDimensioneDescrizione" value="20" />
<s:set name="totaleOffertaPrezziUnitari" value="0" />
<c:if test="${empty totaleOffertaPrezziUnitari}">
	<s:set name="totaleOffertaPrezziUnitari" value="0" />
</c:if>

<c:set var="vociDettaglioEditabile" value="true" />


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-list">

	<h2><wp:i18n key='TITLE_PAGE_ASTA_RILANCIO'/></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/field_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
	
	<div class="balloon errors" id="noJs">
		<div class="balloon-content balloon-info">
			<wp:i18n key='BALLOON_JAVASCRIPT_REQUIRED'/>
		</div>
	</div>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_PREZZI_UNITARI_OFFERTA_ASTA"/>
	</jsp:include>
	
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/skip_form_buttons.jsp" />

	<form action="<wp:action path="/ExtStr2/do/FrontEnd/Aste/rilancioPrezziUnitari.action"/>" method="post" id="formViewPrezziUnitari">
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
		
		<fieldset>
			<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_ULTIMA_OFFERTA_PREZZI_UNITARI" /></legend>
		
			<c:if test="${vociNonSoggette.size() > 0}">
				<div class="fieldset-row first-row last-row special">
					<div class="label">
						<label><wp:i18n key="LABEL_VISUALIZZA_VOCI" /> : </label>
					</div>
					<div class="element">
						<input type="radio" value="${VOCI_SOGGETTE_RIBASSO}" name="discriminante" 
							<s:if test="%{discriminante == VOCI_SOGGETTE_RIBASSO}">checked="checked"</s:if> /> <wp:i18n key="LABEL_VOCI_SOGGETTE_RIBASSO" />
						&nbsp;&nbsp;&nbsp;&nbsp;
						<input type="radio" value="${VOCI_NON_SOGGETTE_RIBASSO}" name="discriminante" 
							<s:if test="%{discriminante == VOCI_NON_SOGGETTE_RIBASSO}">checked="checked"</s:if> /> <wp:i18n key="LABEL_VOCI_NON_SOGGETTE_RIBASSO" />
					</div>
				</div>
			</c:if>
			
			<c:set var="vociDettaglio" scope="request" value="${prezziUnitari}"/>
			<wp:i18n key="LABEL_RIEPILOGO_LAVORAZIONI_OFFERTA_PREZZI_UNITARI" var="valueVociDettaglioSummary" />
			<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/aste/inc/iteratorPrezziUnitari.jsp" >
				<jsp:param name="vociDettaglioSummary" value="${valueVociDettaglioSummary}" />
				<jsp:param name="vociDettaglioColonneVisibili" value="voce,descrizione,unitaMisura,quantita,astePrezzoUnitario,asteImportoUnitario" />
				<jsp:param name="vociDettaglioTitoliColonne" value="Voce,Descrizione,udm,Qta,Prezzo unitario,Importo" />
				<jsp:param name="vociDettaglioTipiColonne" value="7,7,7,5,2,2" />								
				<jsp:param name="vociDettaglioColonneEditabili" value="astePrezzoUnitario" />
				<jsp:param name="vociDettaglioColQta" value="quantita" />
				<jsp:param name="vociDettaglioColPrz" value="astePrezzoUnitario" />
				<jsp:param name="vociDettaglioColImp" value="asteImportoUnitario" />			
				<jsp:param name="vociDettaglioEditabile" value="${vociDettaglioEditabile}" />
				<jsp:param name="discriminante" value="${discriminante}" />
			</jsp:include>
			
		</fieldset>

		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/skip_main_content_from_buttons.jsp" />
		
		<div class="azioni">
			<s:if test="%{discriminante != VOCI_NON_SOGGETTE_RIBASSO}">
				<input type="button" value="<wp:i18n key='BUTTON_EDIT' />" title="<wp:i18n key='BUTTON_EDIT' />" class="button" id="modifica" />
			</s:if>
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_next.jsp" />
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_cancel.jsp" />				
			<input type="hidden" id="azioneMain" value="Azione"/>
			<input type="hidden" name="codice" value="${param.codice}" />
			<input type="hidden" name="codiceLotto" value="${param.codiceLotto}" />
		</div>		
	</form>
</div>


<%-- ********************************************************************** --%>
<%--  script di gestione della pagina                                       --%>
<%-- ********************************************************************** --%>

<script type="text/javascript">
$(document).ready(function() {
	// se js abilitato rimuovo l'avviso che per essere usabile la pagina
	// serve js abilitato
	$('#noJs').remove();
	
	// gestisci il radio button
	$('[name="discriminante"]').on("click", function(){
 		$("#azioneMain").attr("name", "method:undo");
 		$("#formViewPrezziUnitari").submit();
	});
});	
</script>


<%-- ********************************************************************** --%>
<%--  dialog modale di inserimento dati                                     --%>
<%-- ********************************************************************** --%>
<%--<c:if test="${vociDettaglioEditabile}" > --%>	

	<c:set var="url"><wp:action path="/ExtStr2/do/FrontEnd/Aste/rilancioPrezziUnitari.action"/></c:set>
  		
	<c:set var="vociDettaglio" scope="request" value="${prezziUnitari}"/>
	<wp:i18n key="LABEL_RIEPILOGO_LAVORAZIONI_OFFERTA_PREZZI_UNITARI" var="valueVociDettaglioSummary" />
	<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/aste/inc/dialogPrezziUnitari.jsp" >
		<jsp:param name="vociDettaglioAction" value="${url}" />
		<jsp:param name="vociDettaglioSummary" value="${valueVociDettaglioSummary}" />
		<jsp:param name="vociDettaglioColonneVisibili" value="voce,descrizione,unitaMisura,quantita,astePrezzoUnitario,asteImportoUnitario" />
		<jsp:param name="vociDettaglioTitoliColonne" value="Voce,Descrizione,udm,Qta,Prezzo unitario,Importo" />
		<jsp:param name="vociDettaglioTipiColonne" value="7,7,7,5,2,2" />								
		<jsp:param name="vociDettaglioColonneEditabili" value="astePrezzoUnitario" />
		<jsp:param name="vociDettaglioColQta" value="quantita" />
		<jsp:param name="vociDettaglioColPrz" value="astePrezzoUnitario" />
		<jsp:param name="vociDettaglioColImp" value="asteImportoUnitario" />
		<jsp:param name="discriminante" value="${discriminante}" />					
		<jsp:param name="hiddenInput" value='<input type="hidden" name="codice" value="${param.codice}" />' />
		<jsp:param name="hiddenInput" value='<input type="hidden" name="codiceLotto" value="${param.codiceLotto}" />' />
	</jsp:include>
			
<%--</c:if> --%>

	
