<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<script type="text/javascript" src='<wp:resourceURL/>static/js/jquery.dataTables.min.js'></script>
<script type="text/javascript">

	$(document).ready(function() {
	
		$.extend($.fn.dataTable.defaults, {
			"paging": false,
		    "ordering": false,
		    "info": false,
		    "searching" : false
		});
		
		$('#tableBandi').dataTable({
			scrollX: true,
			scrollY: "25em",
			scrollCollapse: true
		});
	});
</script>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2><wp:i18n key="TITLE_PAGE_TABINF_INDICIZZAZIONE" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_INDICIZZAZIONE_DPCM"/>
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />

	<div class="detail-section first-detail-section">		
		<h3 class="detail-section-title"><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span>
			<s:property value="tipo" /><s:if test="%{!'Avviso'.equals(tipo)}"> di gara</s:if>
		</h3>
	
		<div class="detail-row">
			<label><wp:i18n key="LABEL_TITOLO" /> :</label>
			<s:property value="titolo" />
		</div>
	</div>

	<div class="table-container">
		<table id="tableIndicizzazione" summary="<wp:i18n key="LABEL_TABINF_INDICIZZAZIONE_TABELLA" />" class="indicizzazione-table">
			<thead>
				<tr>
					<th colspan="18" scope="colgroup"><wp:i18n key="LABEL_TABINF_INDICIZZAZIONE_TABELLA" /></th>
				</tr>
				<tr>
					<th scope="col" abbr="Tipo"><wp:i18n key="LABEL_TABINF_INDICIZZAZIONE_TIPO" /></th>
					<th scope="col" abbr="Contratto"><wp:i18n key="LABEL_TABINF_INDICIZZAZIONE_CONTRATTO" /></th>
					<th scope="col" abbr="DenominazioneEnte" style="min-width: 30em;"><wp:i18n key="LABEL_TABINF_INDICIZZAZIONE_DENOMINAZIONE_ENTE" /></th>
					<th scope="col" abbr="TipoEnte"><wp:i18n key="LABEL_TABINF_INDICIZZAZIONE_TIPO_ENTE" /></th>
					<th scope="col" abbr="Provincia"><wp:i18n key="LABEL_TABINF_INDICIZZAZIONE_PROVINCIA" /></th>
					<th scope="col" abbr="Comune"><wp:i18n key="LABEL_TABINF_INDICIZZAZIONE_COMUNE" /></th>
					<th scope="col" abbr="Indirizzo" style="min-width: 20em;"><wp:i18n key="LABEL_TABINF_INDICIZZAZIONE_INDIRIZZO" /></th>
					<th scope="col" abbr="SenzaImporto"><wp:i18n key="LABEL_TABINF_INDICIZZAZIONE_SENZA_IMPORTO" /></th>
					<th scope="col" abbr="a base asta"><wp:i18n key="LABEL_TABINF_INDICIZZAZIONE_BASE_ASTA" /></th>
					<th scope="col" abbr="di aggiudicazione"><wp:i18n key="LABEL_TABINF_INDICIZZAZIONE_IMPORTO_AGGIUDICAZIONE" /></th>
					<th scope="col" abbr="DtPubblicazione"><wp:i18n key="LABEL_TABINF_INDICIZZAZIONE_DATA_PUBBLICAZIONE" /></th>
					<th scope="col" abbr="DtScadenzaBando"><wp:i18n key="LABEL_TABINF_INDICIZZAZIONE_DATA_SCAD" /></th>
					<th scope="col" abbr="DtScadenzaPubblEsito"><wp:i18n key="LABEL_TABINF_INDICIZZAZIONE_DATA_SCAD_PUBB_ESITO" /></th>
					<th scope="col" abbr="RequisitiQualificazione"><wp:i18n key="LABEL_TABINF_INDICIZZAZIONE_REQUISITI" /></th>
					<th scope="col" abbr="CPV"><wp:i18n key="LABEL_TABINF_INDICIZZAZIONE_CPV" /></th>
					<th scope="col" abbr="SCP"><wp:i18n key="LABEL_TABINF_INDICIZZAZIONE_SCP" /></th>
					<th scope="col" abbr="URL"><wp:i18n key="LABEL_TABINF_INDICIZZAZIONE_URL_PUBB_SCP" /></th>
					<th scope="col" abbr="CIG"><wp:i18n key="LABEL_TABINF_INDICIZZAZIONE_CIG" /></th>
				</tr>
			</thead>
			<tbody>
				<s:iterator var="riga" value="lotti">
					<tr>
						<td><s:property value="tipo" /></td>
						<td><s:property value="contratto" /></td>
						<td><s:property value="amministrazione" /></td>
						<td>
							<s:property value="tipoAmministrazione" />
							<c:if test="${! empty riga.descTipoAmministrazione}"> - <s:property value="descTipoAmministrazione" /></c:if>
						</td>
						<td><s:property value="provinciaSedeGara" /></td>
						<td><s:property value="comuneSedeGara" /></td>
						<td><s:property value="indirizzoSedeGara" /></td>
						<td><s:if test="senzaImporto">SI</s:if><s:else>NO</s:else></td>
						<td class="money-content">
							<s:if test="%{importoBaseAsta != null}">
								<s:text name="format.money"><s:param value="importoBaseAsta"/></s:text>
							</s:if>
						</td>
						<td class="money-content">
							<s:if test="%{importoAggiudicazione != null}">
								<s:text name="format.money"><s:param value="importoAggiudicazione"/></s:text>
							</s:if>
						</td>
						<td class="date-content"><s:date name="pubblicazione" format="dd/MM/yyyy" /></td>
						<td class="date-content"><s:date name="scadenzaBando" format="dd/MM/yyyy" /></td>
						<td class="date-content"><s:date name="scadenzaPubblEsito" format="dd/MM/yyyy" /></td>
						<td><s:property value="reqQualificazione" /></td>
						<td><s:property value="cpv" /></td>
						<td><s:property value="scp" /></td>
						<td><s:property value="urlPubblicazione" /></td>
						<td><s:property value="cig" /></td>
					</tr>
				</s:iterator>
			</tbody>
		</table>
	</div>

	<div class="detail-section last-detail-section">
		<h3 class="detail-section-title"><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_DOCUMENTATION" /></h3>
		
		<div class="detail-row">
			<s:set var="numeroDocumentiAllegati" value="0" />
			<s:if test="%{documenti.length > 0}">
				<s:if test="%{documenti.length > 1}">
					<span class="important"><wp:i18n key="LABEL_LOTTO" /> <s:property value="%{#status.index+1}" /></span>
				</s:if>	
				<c:set var="dataPubblicazione"><s:date name="dataPubblicazione" format="dd/MM/yyyy" /></c:set>
				<s:set var="elencoDocumentiAllegati" value="%{documenti}"/>
				<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/bandi/inc/iteratorDocAllegati.jsp">
					<jsp:param name="path" value="downloadDocumentoPubblico"/>
					<jsp:param name="dataPubblicazione" value="${dataPubblicazione}" />
				</jsp:include>
			</s:if>
			<s:if test="%{#numeroDocumentiAllegati == 0}">
				<wp:i18n key="LABEL_NO_DOCUMENT" />
			</s:if>
		</div>
	</div>

	<c:choose>
		<c:when test="${param.tipo eq 'Bando'}">
			<c:if test="${sessionScope.fromPage eq 'listAllInCorso'}">
				<c:set var="pageBack" value="ppgare_bandi_lista"/>
			</c:if>
			<c:if test="${sessionScope.fromPage eq 'listAllScaduti'}">
				<c:set var="pageBack" value="ppgare_bandi_scaduti_lista"/>
			</c:if>
			<c:set var="namespace" value="Bandi" />
		</c:when>
		<c:when test="${param.tipo eq 'Esito'}">
			<c:if test="${sessionScope.fromPage eq 'listAllInCorso'}">
				<c:set var="pageBack" value="ppgare_esiti_lista"/>
			</c:if>
			<c:set var="namespace" value="Esiti" />
		</c:when>
		<c:when test="${param.tipo eq 'Avviso'}">
			<c:if test="${sessionScope.fromPage eq 'listAllInCorso'}">
				<c:set var="pageBack" value="ppgare_avvisi_lista"/>
			</c:if>
			<c:if test="${sessionScope.fromPage eq 'listAllScaduti'}">
				<c:set var="pageBack" value="ppgare_avvisi_scaduti_lista"/>
			</c:if>
			<c:set var="namespace" value="Avvisi" />
		</c:when>
	</c:choose>

	<c:choose>
		<c:when test="${sessionScope.fromSearch}">
			<div class="back-link">
				<a href='<wp:action path="/ExtStr2/do/FrontEnd/${namespace}/${sessionScope.fromPage}.action" />&amp;last=1&amp;${tokenHrefParams}'>
					<wp:i18n key="LINK_BACK_TO_SEARCH" />
				</a>
			</div>
		</c:when>
		<c:otherwise>
			<c:if test="${! empty sessionScope.fromPage}">
				<div class="back-link">
					<a href='<wp:url page="${pageBack}"/>?${tokenHrefParams}'>
						<wp:i18n key="LINK_BACK_TO_LIST" />
					</a>
				</div>
			</c:if>
		</c:otherwise>
	</c:choose>
</div>