<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="es"
	uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld"%>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<es:checkCustomization var="visAderenti" objectId="GARE" attribute="ADERENTI" feature="VIS" />

<div class="portgare-view">

	<h2><wp:i18n key="TITLE_PAGE_LISTA_LOTTI" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_LOTTI_BANDO"/>
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />

	<s:iterator var="lotto" value="lotti" status="status">
		<div class="detail-section first-detail-section last-detail-section">
			<h3 class="detail-section-title"><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span>
				<s:if test='%{dettaglioEsito.datiGenerali.tipologia eq "1"}'>
					<wp:i18n key="LABEL_LOTTO" /> <s:property value="%{codiceInterno}" /> <!-- <s:property value="%{#status.index+1}" /> -->
				</s:if>
				<s:else>
					<s:if test="#status.index == 0">
						<wp:i18n key="LABEL_LISTA_LOTTI_DATI_PROCEDURA" />
					</s:if>
					<s:else>
						<wp:i18n key="LABEL_LOTTO" /> <s:property value="%{codiceInterno}" /> <!-- <s:property value="%{#status.index}" /> -->
					</s:else>
				</s:else>
			</h3>
			
			<c:if test="${visAderenti}">
				<s:if test="%{soggettiAderenti.length > 0}" >
					<div class="detail-row">
						<label><wp:i18n key="LABEL_SOGGETTI_ADERENTI" /> : </label>
			
						<div class="detail-subrow">
			 				<ul class="list">
								<s:iterator var="soggetto" value="%{soggettiAderenti}" status="stat">
									<li class='<s:if test="%{#stat.first}">first</s:if> <s:if test="%{#stat.last}">last</s:if>'>
										<s:property value="%{#soggetto.codiceFiscale}" /> - <s:property value="#soggetto.denominazione" />
			 						</li>	 
								</s:iterator>
			 				</ul>
						</div>					
					</div>
				</s:if>
			</c:if>
			
			<div class="detail-row">
				<label><wp:i18n key="LABEL_TITOLO" /> : </label>
				<s:property value="oggetto" /> 
				<c:if test="${! empty cig}">- <wp:i18n key="LABEL_CIG" />: <s:property value="cig" /></c:if>
				<c:if test="${! empty cup}">- <wp:i18n key="LABEL_CUP" />: <s:property value="cup" /></c:if>
			</div>

			<div class="detail-row">
				<label><wp:i18n key="LABEL_IMPORTO_BASE_GARA" /> : </label>
				<s:if test="%{importoBaseAsta != null && importoBaseAsta != 0}">
					<s:text name="format.money"><s:param value="importoBaseAsta"/></s:text> &euro;
				</s:if>
			</div>

			<s:if test='%{dettaglioEsito.datiGenerali.tipologia == 1 || dettaglioEsito.datiGenerali.tipologia == 3}'>
				<div class="detail-row">
					<label><wp:i18n key="LABEL_STATO_GARA" /> : </label>
					<s:if test="%{stato != null}">
						<s:property value="stato" /> <s:if test="%{esito != null}"> - <s:property value="esito" /></s:if>
					</s:if>
				</div>
			</s:if>

			<s:if test='%{!(dettaglioEsito.datiGenerali.tipologia == 3 && #status.index == 0)}'>
				<s:if test="%{aggiudicataria.length > 0}">
					<s:if test="%{aggiudicataria.length == 1}">
						<div class="detail-row">	
							<label><wp:i18n key="LABEL_DETTAGLIO_ESITO_AGGIUDICATARIA" /> : </label>
							<s:property value="aggiudicataria[0]"/>
						</div>
					</s:if>
				</s:if>
				<s:if test="%{!dettaglioEsito.datiGenerali.accordoQuadro}">
					<div class="detail-row">
						<label><wp:i18n key="LABEL_DETTAGLIO_ESITO_IMPORTO_AGGIUDICAZIONE" /> : </label>
						<s:if test="%{importoAggiudicazione != null && importoAggiudicazione != 0}">
							<s:text name="format.money"><s:param value="importoAggiudicazione"/></s:text> &euro;
						</s:if>
					</div>
				</s:if> 
			</s:if>

			<s:if test='%{dettaglioEsito.datiGenerali.tipologia != 3 || #status.index == 0}'>
				<div class="detail-row">
					<label><wp:i18n key="LABEL_DETTAGLIO_ESITO_DATA_AGGIUDICAZIONE" /> : </label>
					<s:date name="dataAggiudicazione" format="dd/MM/yyyy" />
				</div>
			</s:if>
			
			<s:if test='%{!(dettaglioEsito.datiGenerali.tipologia == 3 && #status.index == 0)}'>
				<s:if test="%{aggiudicataria.length > 0}">
					<s:if test="%{aggiudicataria.length > 1}">
						<div class="detail-row">	
							<div class="detail-subrow">
								<div class="list">
									<h4 class="detail-section-title"><wp:i18n key="LABEL_DETTAGLIO_ESITO_AGGIUDICATARIE" /></h4>
									<ul>
										<s:iterator var="aggiudicataria" value="aggiudicataria" status="status">
											<li><s:property value="aggiudicataria[#status.index]"/></li>
										</s:iterator>
									</ul>
								</div>
							</div>
						</div>
					</s:if>
				</s:if> 
			</s:if>
		
		</div>
	</s:iterator>

	<div class="back-link">
		<a href="<wp:action path="/ExtStr2/do/FrontEnd/Esiti/view.action" />&amp;codice=${param.codice}&amp;ext=${param.ext}">
			<wp:i18n key="LINK_BACK_TO_ESITO" />
		</a>
	</div>
</div>