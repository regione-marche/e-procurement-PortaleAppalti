<%@ taglib prefix="wp" uri="aps-core.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="/struts-tags"%>


<c:set var="dettGara" value="${sessionScope['dettGara']}" />
<s:set var="gara" value="%{#session['dettGara'].datiGeneraliGara}" />
<c:set var="simboloOfferta"><s:if test="%{dettaglioAsta.tipoOfferta == 1}">%</s:if><s:else>&euro;</s:else></c:set>

<%-- numero dei rilanci effettuati --%>
<c:set var="numRilanci" value="0"/>
<s:iterator var="item" value="%{rilanci}">
	<c:set var="numRilanci" value="${numRilanci + 1}" />
</s:iterator>

<%-- codice del lotto --%>
<s:set var="codLotto">${param.codiceLotto}</s:set>
<c:set var="codiceInternoLotto" value=""/>  
<s:iterator var="lotto" value="%{#dettGara.lotto}">
	<s:if test="#lotto.codiceLotto == #codLotto">
		<c:set var="codiceInternoLotto"><s:property value="#lotto.codiceInterno" /></c:set>
	</s:if>	
</s:iterator>


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp">
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2><wp:i18n key='TITLE_PAGE_ASTA_RIEPILOGO'/></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_ASTA_RIEPILOGO" />
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />

	<c:if test="${numRilanci <= 0}">
		<wp:i18n key='LABEL_OFFERTA_INIZIALE_ASTA_NON_TROVATA'/> <s:property value="codice" />
		<s:if test="%{codiceLotto != null && codiceLotto != ''}" >
			- <wp:i18n key='LABEL_LOTTO'/> <s:property value="codiceLotto" />
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
			<label><wp:i18n key="LABEL_ULTIMO_VALORE_OFFERTO" /> : </label>
			<s:if test="%{dettaglioAsta.tipoOfferta == 1}">					
				<s:text name="format.money5dec"><s:param value="dettaglioAsta.ribassoUltimoRilancio"/></s:text>&nbsp;${simboloOfferta}
			</s:if>
			<s:else>
				<s:text name="format.money5dec"><s:param value="dettaglioAsta.importoUltimoRilancio"/></s:text>&nbsp;${simboloOfferta}
			</s:else>							
		</div>
		
<%-- 		
		<div class="detail-row">
			<label>Tipo classifica : </label>
			<s:iterator value="maps['tipiClassificaAsta']">
				<s:if test="%{key == dettaglioAsta.tipoClassifica}"><s:property value="%{value}"/></s:if>
			</s:iterator>
		</div>				
--%>		

		<div class="detail-row">			
			<s:set var="elencoClassifica" value="classifica" />	
			<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/aste/inc/iteratorClassifica.jsp" >
				<jsp:param name="tipoClassifica" value="${dettaglioAsta.tipoClassifica}" />
				<jsp:param name="tipoOfferta" value="${dettaglioAsta.tipoOfferta}" />
			</jsp:include>
		</div>	
		
		<div class="azioni">
			<form action="<wp:action path="/ExtStr2/do/FrontEnd/Aste/listRilanci.action"/>" method="post" class="azione">
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
				
				<div>
					<wp:i18n key="BUTTON_STORIA_RILANCI" var="valueStoriaRilanciButton" />
					<wp:i18n key="TITLE_STORIA_RILANCI" var="titleStoriaRilanciButton" /> 
					<s:submit value="%{#attr.valueStoriaRilanciButton}" title="%{#attr.titleStoriaRilanciButton}" cssClass="button" />
					<input type="hidden" name="codice" value="${param.codice}" />
					<input type="hidden" name="codiceLotto" value="${param.codiceLotto}" />
					<input type="hidden" name="fromPage" value="riepilogo" />
					<input type="hidden" name="fase" value="${dettaglioAsta.fase}" />
				</div>
			</form>
		</div>		
	</c:if>
	
	<div class="back-link">
		<a href="<wp:action path="/ExtStr2/do/FrontEnd/Bandi/view.action"/>&amp;codice=${codice}&amp;ext=&amp;${tokenHrefParams}">
			<wp:i18n key="LINK_BACK_TO_PROCEDURE" />
		</a>
	</div>
</div>
