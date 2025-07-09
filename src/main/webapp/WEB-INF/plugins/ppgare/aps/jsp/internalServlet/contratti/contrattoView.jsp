<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp">
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<es:getAppParam name="denominazioneStazioneAppaltanteUnica" var="stazAppUnica" scope = "page"/> 	

<div class="portgare-view">

	<h2><wp:i18n key="TITLE_PAGE_CONTRATTI_DETTAGLIO_ORDINE" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_DETTAGLIO_CONTRATTO" />
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />

	<s:if test="%{dettaglioContratto.dataUltimoAggiornamento != null}">					
		<div class="align-right important last-update-detail">
			<wp:i18n key="LABEL_LAST_UPDATE" />	<s:date name="dettaglioContratto.dataUltimoAggiornamento" format="dd/MM/yyyy" />
		</div>
	</s:if>
			
	<div class="detail-section first-detail-section">
		<h3 class="detail-section-title">
			<span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_STAZIONE_APPALTANTE" />
		</h3>

		<div class="detail-row">
			<label><wp:i18n key="LABEL_DENOMINAZIONE" /> : </label>
			<c:choose>
				<c:when test="${! empty stazAppUnica }">
					<s:set var="stazAppUnicaToStruts">${stazAppUnica}</s:set>
					<s:property value="stazAppUnicaToStruts" />
				</c:when>
				<c:otherwise>
					<s:iterator value="maps['stazioniAppaltanti']">
								<s:if test="%{key == dettaglioContratto.stazioneAppaltante}">
									<s:property value="%{value}" />
								</s:if>
					</s:iterator>
				</c:otherwise>
			</c:choose>
		</div>
	</div>

	<div class="detail-section">
		<h3 class="detail-section-title">
			<span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_DATI_GENERALI" />
		</h3>

		<div class="detail-row">
			<label><wp:i18n key="LABEL_TITOLO" /> :</label>
			<s:property value="dettaglioContratto.oggetto" />
		</div>

		<div class="detail-row">
			<label><wp:i18n key="LABEL_CIG" /> :</label>
			<s:property value="dettaglioContratto.cig" />
		</div>

		<div class="detail-row">
			<label><wp:i18n key="LABEL_IMPORTO" /> :</label>
			<s:if test="%{dettaglioContratto.importo != null}">
				<s:text name="format.money"><s:param value="dettaglioContratto.importo"/></s:text> &euro;
			</s:if>
		</div>

		<div class="detail-row">
			<label><wp:i18n key="LABEL_TIPO_ATTO" /> : </label>
			<s:property value="dettaglioContratto.tipoAtto" />
		</div>

		<div class="detail-row">
			<label><wp:i18n key="LABEL_DATA_ATTO" /> : </label>
			<s:date name="dettaglioContratto.dataAtto" format="dd/MM/yyyy" />
		</div>
		
		<div class="detail-row">
			<label><wp:i18n key="LABEL_NUM_REPERTORIO" /> : </label>
			<s:property value="dettaglioContratto.numeroRepertorio" />
		</div>

		<div class="detail-row">
			<label><wp:i18n key="LABEL_RIFERIMENTO_PROCEDURA" /> : </label>
				<s:property value="dettaglioContratto.codice" />
		</div>
	</div>

	<div class="detail-section">
		<h3 class="detail-section-title">
			<span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_ORDINE" />
		</h3>
		<ul class="list">
			<li>
				<%-- prepara il BACK LINK della pagina di visualizzazione della firma digitale --%>
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/backFromPageFirmaDigitale.jsp"/>
				
				<c:choose>
					<c:when test="${empty dettaglioContratto.dataLettura}">
						<c:set var="urlFileDownload"><wp:action path="/ExtStr2/do/FrontEnd/Contratti/download.action"/></c:set>
						<c:set var="url" value="${urlFileDownload}&amp;codice=${dettaglioContratto.codice}"/>
					</c:when>
					<c:otherwise>
						<c:set var="urlFileDownload"><wp:action path="/ExtStr2/do/FrontEnd/DocDig/downloadDocumentoRiservato.action"/></c:set>
						<c:set var="url" value="${urlFileDownload}&amp;id=${dettaglioContratto.idDocumento}"/>
					</c:otherwise>
				</c:choose>
			
				<form action='${url}' method="post">
   					<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
   					
   					<a href="javascript:;" onclick="parentNode.submit();" title="<wp:i18n key="LABEL_INFO_FIRMA_DIGITALE" />" class='bkg p7m'>
   						<wp:i18n key="LINK_VIEW_ORDER" />
   					</a>
   					
   					<c:if test="${not empty dettaglioContratto.dataLettura}">
  						<input type="hidden" name="codice" value="${param.codice}" />
  					</c:if>
				</form>
			</li>
		</ul>
	</div>
	
	<!-- ALLEGATI -->
	<s:if test="%{dettaglioContratto.allegati.length > 0}">
		<div class="detail-section">
			<h3 class="detail-section-title">
				<span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_ALLEGATI_ORDINE" />
			</h3>
		</div>
		<s:set var="elencoDocumentiAllegati" value="%{dettaglioContratto.allegati}" />
		<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/bandi/inc/iteratorDocAllegati.jsp">
			<jsp:param name="path" value="downloadDocumentoRiservato"/>
		</jsp:include>
	</s:if>

	<!-- COMUNICAZIONI -->
	<c:if test="${sessionScope.currentUser != 'guest'}">
		<jsp:include
			page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/comunicazioni/sommarioComunicazioni.jsp" >
				<jsp:param name="genere" value="4"/>
			</jsp:include>
			
	</c:if>

	<div class="back-link">
		<a href="<wp:action path="/ExtStr2/do/FrontEnd/Contratti/searchContratti.action"/>&amp;last=1">
			<wp:i18n key="LINK_BACK_TO_LIST" />
		</a>
	</div>
</div>