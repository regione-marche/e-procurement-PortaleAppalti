<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>

<es:checkCustomization var="visRSS" objectId="TABINF-INDICIZZAZIONE" attribute="RSS" feature="VIS" />
<es:getAppParam name="bandiGara.searchForm" var="searchFormPref"/> 	
<%-- controllo che se il prefisso per la ricerca e' vuoto viene inizializato a 'default' --%>
<c:if test="${empty searchFormPref }">
	<c:set var="searchFormPref" value="default"/>
</c:if>


<%-- 
NB: per le seguenti variabili, indicativamente si utilizza lo stesso valore di "fromPage" 
	che contiene il nome del metodo relativo alla chiamata presente nella action BandiFinderAction!!!
--%>
<c:set var="listAllInCorso" value="false"/>
<c:set var="listAllScaduti" value="false"/>
<c:set var="listAllRichiesteOfferta" value="false"/>
<c:set var="listAllRichiesteDocumenti" value="false"/>
<c:set var="listAllAsteInCorso" value="false"/>
<c:set var="listAllProcInAggiudicazione" value="false"/>
<c:set var="searchBandiConEsito" value="false"/>
<c:set var="listAllAcqRegimePrivScaduti" value="false"/>
<c:set var="listAllRichiesteOffertaAcqRegimePriv" value="false"/>
<c:set var="searchProcedureAcqRegimePriv" value="false"/>
<c:set var="listAllAcqRegimePrivInCorso" value="false"/>
<c:set var="listAllVendRegimePrivScaduti" value="false"/>
<c:set var="listAllRichiesteOffertaVendRegimePriv" value="false"/>
<c:set var="searchProcedureVendRegimePriv" value="false"/>
<c:set var="listAllVendRegimePrivInCorso" value="false"/> 
<c:choose>
	<c:when test="${sessionScope.fromPage eq 'listAllInCorso'}">
		<c:set var="codiceTitolo" value="TITLE_PAGE_LISTA_BANDI_IN_CORSO"/>
		<c:set var="codiceBalloon" value="BALLOON_LISTA_BANDI_IN_CORSO"/>
		<c:if test="${visRSS}">
			<wp:headInfo type="DC.Description" info="Bandi di gara" />
				<c:set var="rss" value="bandi.xml" />
			<wp:headInfo type="RSS.Bandi" var="rss" />
		</c:if>
		<s:set var="searchForm" value="%{#session.formListAllInCorsoBandi}" />
		<c:set var="listAllInCorso" value="true"/>
	</c:when>
	<c:when test="${sessionScope.fromPage eq 'listAllScaduti'}">
		<c:set var="codiceTitolo" value="TITLE_PAGE_LISTA_BANDI_SCADUTI"/>
		<c:set var="codiceBalloon" value="BALLOON_LISTA_BANDI_SCADUTI"/>
		<c:if test="${visRSS}">
			<wp:headInfo type="DC.Description" info="Bandi di gara scaduti" />
			<c:set var="rss" value="bandi_scaduti.xml" />
			<wp:headInfo type="RSS.BandiScaduti" var="rss" />
		</c:if>
		<s:set var="searchForm" value="%{#session.formListAllScadutiBandi}" />
		<c:set var="listAllScaduti" value="true"/>
	</c:when>
	<c:when test="${sessionScope.fromPage eq 'listAllRichiesteOfferta'}">
		<c:set var="codiceTitolo" value="TITLE_PAGE_LISTA_NEGOZIATE"/>
		<c:set var="codiceBalloon" value="BALLOON_LISTA_BANDI_RICH_INVIO_OFFERTA"/>
		<s:set var="searchForm" value="%{#session.formListAllRichiesteOffertaBandi}" />
		<c:set var="listAllRichiesteOfferta" value="true"/>
	</c:when>
	<c:when test="${sessionScope.fromPage eq 'listAllRichiesteDocumenti'}">
		<c:set var="codiceTitolo" value="TITLE_PAGE_LISTA_COMPROVA_REQUISITI"/>
		<c:set var="codiceBalloon" value="BALLOON_LISTA_BANDI_RICH_INVIO_DOCUMENTAZIONE"/>
		<s:set var="searchForm" value="%{#session.formListAllRichiesteDocumentiBandi}" />
		<c:set var="listAllRichiesteDocumenti" value="true"/>
	</c:when>
	<c:when test="${sessionScope.fromPage eq 'listAllAsteInCorso'}">
		<c:set var="codiceTitolo" value="TITLE_PAGE_LISTA_ASTE_IN_CORSO"/>
		<c:set var="codiceBalloon" value="BALLOON_LISTA_ASTE_IN_CORSO"/>
		<s:set var="searchForm" value="%{#session.formListAllAsteInCorsoBandi}" />		
		<c:set var="listAllAsteInCorso" value="true"/>
	</c:when>
	<c:when test="${sessionScope.fromPage eq 'searchProcedure'}">
		<c:set var="codiceTitolo" value="TITLE_PAGE_LISTA_PROC_AGGIUDICAZIONE_CONCLUSE"/>
		<c:set var="codiceBalloon" value="BALLOON_RICERCA_BANDI_PROC_AGG"/>
		<s:set var="searchForm" value="%{#session.formSearchBandiProcAgg}" />		
		<c:set var="listAllProcInAggiudicazione" value="true"/>
	</c:when>
	<c:when test="${sessionScope.fromPage eq 'searchBandiConEsito'}">
		<c:set var="codiceTitolo" value="TITLE_PAGE_RICERCA_BANDI"/>
		<c:set var="codiceBalloon" value="BALLOON_LISTA_BANDI_CON_ESITO"/>
		<s:set var="searchForm" value="%{#session.formSearchBandiConEsito}" />		
		<c:set var="searchBandiConEsito" value="true"/>
	</c:when>
		
	<c:when test="${sessionScope.fromPage eq 'listAllAcqRegimePrivInCorso'}">
		<c:set var="codiceTitolo" value="TITLE_PAGE_LISTA_BANDI_IN_CORSO_ACQ"/>
		<c:set var="codiceBalloon" value="BALLOON_LISTA_BANDI_IN_CORSO_ACQ"/>
		<s:set var="searchForm" value="%{#session.formListAllAcqInCorsoBandi}" />
		<c:set var="listAllAcqRegimePrivInCorso" value="true"/>
	</c:when>	
	<c:when test="${sessionScope.fromPage eq 'listAllAcqRegimePrivScaduti'}">
		<c:set var="codiceTitolo" value="TITLE_PAGE_LISTA_BANDI_SCADUTI_ACQ"/>
		<c:set var="codiceBalloon" value="BALLOON_LISTA_BANDI_SCADUTI_ACQ"/>
		<s:set var="searchForm" value="%{#session.formListAllAcqScadutiBandi}" />
		<c:set var="listAllAcqRegimePrivScaduti" value="true"/>
	</c:when>
	<c:when test="${sessionScope.fromPage eq 'listAllRichiesteOffertaAcqRegimePriv'}">
		<c:set var="codiceTitolo" value="TITLE_PAGE_LISTA_NEGOZIATE_ACQ"/>
		<c:set var="codiceBalloon" value="BALLOON_LISTA_BANDI_RICH_INVIO_OFFERTA_ACQ"/>
		<s:set var="searchForm" value="%{#session.formListAllAcqRichiesteOffertaBandi}" />
		<c:set var="listAllRichiesteOffertaAcqRegimePriv" value="true"/>
	</c:when>
	<c:when test="${sessionScope.fromPage eq 'searchProcedureAcqRegimePriv'}">
		<c:set var="codiceTitolo" value="TITLE_PAGE_LISTA_PROC_AGGIUDICAZIONE_CONCLUSE_ACQ"/>
		<c:set var="codiceBalloon" value="BALLOON_RICERCA_BANDI_PROC_AGG_ACQ"/>
		<s:set var="searchForm" value="%{#session.formSearchBandiProcAgg}" />
		<c:set var="searchProcedureAcqRegimePriv" value="true"/>
	</c:when>
	
	<c:when test="${sessionScope.fromPage eq 'listAllVendRegimePrivInCorso'}">
		<c:set var="codiceTitolo" value="TITLE_PAGE_LISTA_BANDI_IN_CORSO_VEN"/>
		<c:set var="codiceBalloon" value="BALLOON_LISTA_BANDI_IN_CORSO_VEN"/>
		<s:set var="searchForm" value="%{#session.formListAllVendInCorsoBandi}" />
		<c:set var="listAllVendRegimePrivInCorso" value="true"/>
	</c:when>		
	<c:when test="${sessionScope.fromPage eq 'listAllVendRegimePrivScaduti'}">
		<c:set var="codiceTitolo" value="TITLE_PAGE_LISTA_BANDI_SCADUTI_VEN"/>
		<c:set var="codiceBalloon" value="BALLOON_LISTA_BANDI_SCADUTI_VEN"/>
		<s:set var="searchForm" value="%{#session.formListAllVendScadutiBandi}" />
		<c:set var="listAllVendRegimePrivScaduti" value="true"/>
	</c:when>
	<c:when test="${sessionScope.fromPage eq 'listAllRichiesteOffertaVendRegimePriv'}">
		<c:set var="codiceTitolo" value="TITLE_PAGE_LISTA_NEGOZIATE_VEN"/>
		<c:set var="codiceBalloon" value="BALLOON_LISTA_BANDI_RICH_INVIO_OFFERTA_VEN"/>
		<s:set var="searchForm" value="%{#session.formListAllVendRichiesteOffertaBandi}" />
		<c:set var="listAllRichiesteOffertaVendRegimePriv" value="true"/>
	</c:when>
	<c:when test="${sessionScope.fromPage eq 'searchProcedureVendRegimePriv'}">
		<c:set var="codiceTitolo" value="TITLE_PAGE_LISTA_PROC_AGGIUDICAZIONE_CONCLUSE_VEN"/>
		<c:set var="codiceBalloon" value="BALLOON_RICERCA_BANDI_PROC_AGG_VEN"/>
		<s:set var="searchForm" value="%{#session.formSearchBandiProcAggVend}" />
		<c:set var="searchProcedureVendRegimePriv" value="true"/>
	</c:when>
</c:choose>

<%-- calcola la visibilita' di alcuni campi di filtro in base alla form di ricerca --%>
<c:set var="proceduraTelematicaVisibile" value="${
				listAllInCorso || listAllScaduti || listAllRichiesteOfferta || listAllRichiesteDocumenti || listAllProcInAggiudicazione || searchBandiConEsito}"/>
<c:set var="statoVisibile" value="${listAllScaduti || listAllProcInAggiudicazione || searchBandiConEsito}"/>
<c:set var="esitoVisibile" value="${searchBandiConEsito}"/>
<c:set var="cigVisibile" value="${ 
		   not (listAllAcqRegimePrivInCorso || listAllAcqRegimePrivScaduti || listAllRichiesteOffertaAcqRegimePriv || searchProcedureAcqRegimePriv ||
				listAllVendRegimePrivInCorso || listAllVendRegimePrivScaduti || listAllRichiesteOffertaVendRegimePriv || searchProcedureVendRegimePriv)}"/>


<%--
listAllInCorso: ${listAllInCorso}<br/>
listAllScaduti: ${listAllScaduti}<br/>
listAllRichiesteOfferta: ${listAllRichiesteOfferta}<br/> 
listAllRichiesteDocumenti: ${listAllRichiesteDocumenti}<br/>
listAllAsteInCorso: ${listAllAsteInCorso}<br/>
listAllProcInAggiudicazione: ${listAllProcInAggiudicazione}<br/>
searchBandiConEsito: ${searchBandiConEsito}<br/>
searchFormPref: ${searchFormPref}<br/>
titolo : ${titolo}<br/>
codiceBalloon : ${codiceBalloon}<br/>

proceduraTelemanticaVisibile: ${proceduraTelematicaVisibile}<br/>
statoVisibile: ${statoVisibile}<br/>
esitoVisibile: ${esitoVisibile}<br/>
--%>

<%-- 
model.currentPage=${model.currentPage}<br/>
model.totalPages=${model.totalPages}<br/>
model.iDisplayLength=${model.iDisplayLength}<br/>
model.iDisplayStart=${model.iDisplayStart}<br/>
model.iTotalDisplayRecords=${model.iTotalDisplayRecords}<br/>
model.iTotalRecords=${model.iTotalRecords}<br/>
--%>

<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/send_blockUI.jsp" />

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-list">

	<h2><wp:i18n key="${codiceTitolo}"/></h2>

	<c:if test="${visRSS && (listAllInCorso || listAllScaduti)}"> 
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_rss.jsp" >
			<jsp:param name="rss" value="${rss}" />
		</jsp:include>
	</c:if>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
			<jsp:param name="keyMsg" value="${codiceBalloon}"/>
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />

	<%-- FORM DI RICERCA (default o personalizzata) --%>
	<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/bandi/inc/${searchFormPref}BandiGaraSearchForm.jsp" >
		<jsp:param name="searchForm" value="${searchForm}"/>
		<jsp:param name="proceduraTelematicaVisibile" value="${proceduraTelematicaVisibile}"/>
		<jsp:param name="statoVisibile" value="${statoVisibile}"/>
		<jsp:param name="esitoVisibile" value="${esitoVisibile}"/>
		<jsp:param name="cigVisibile" value="${cigVisibile}"/>
	</jsp:include>
	
	
	<!-- LISTA -->
	<c:if test="${listaBandi ne null}">
		<form action="<wp:action path="/ExtStr2/do/FrontEnd/Bandi/${sessionScope.fromPage}.action" />" method="post">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			<input type="hidden" name="last" value="1" />
			
			<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/bandi/inc/listaBandi.jsp" >
				<jsp:param name="cigVisibile" value="${cigVisibile}" />
			</jsp:include>
		</form>
	</c:if>		

</div>

<script>
<!--//--><![CDATA[//><!--
	
	function abilitaEsitiGara(obj) {
		var stato = $("select[id^='model.stato'] option:selected").val();
		var esito = $("select[id^='model.esito']");
		if(stato != "3") {										// 3=Conclusa
			$("#esitoGara").hide();	
			esito.val('');
		} else {
			$("#esitoGara").show();
		}
	}
    
	$("select[id^='model.stato']").change(function() {
		abilitaEsitiGara();
	});

// apertura della pagina...
$(document).ready(function() {
	abilitaEsitiGara();
});	

//--><!]]>
</script>