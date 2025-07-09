<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld"%>

<c:set var="garaPrivatistica" value="${dettaglioGara.datiGeneraliGara.garaPrivatistica}" />


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
		
		<div class="detail-section <s:if test="#status.first">first-detail-section</s:if> <s:if test="#status.last">last-detail-section</s:if>">
			<h3 class="detail-section-title"><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span>
				<s:if test='%{dettaglioGara.datiGeneraliGara.tipologia eq "1"}'>
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
				<label><wp:i18n key="LABEL_TITOLO" /> :</label>
				<s:property value="oggetto" />
				<c:if test="${garaPrivatistica == null}">
					<c:if test="${! empty cig}">- <wp:i18n key="LABEL_CIG" /> : <s:property value="cig" /></c:if>
					<c:if test="${! empty cup}">- <wp:i18n key="LABEL_CUP" /> : <s:property value="cup" /></c:if>
				</c:if>
			</div>

			<c:if test="${dettaglioGara.datiGeneraliGara.nascondiImportoBaseGara != 1}">
				<s:if test="%{dettaglioGara.datiGeneraliGara.iterGara != 7}">
					<div class="detail-row">
						<s:if test="%{importo != null || importo != 0}">
							<label><wp:i18n key="LABEL_IMPORTO_BASE_GARA" /> : </label>
							<s:text name="format.money"><s:param value="importo"/></s:text> &euro;
						</s:if>
					</div>
				</s:if>
			</c:if>
			<s:if test='%{dettaglioGara.datiGeneraliGara.tipologia eq "1"}'>
				<div class="detail-row">
					<label><wp:i18n key="LABEL_STATO_GARA" /> : </label>
					<s:if test="%{stato != null}">
						<s:property value="stato" /> <s:if test="%{esito != null}"> - <s:property value="esito" /></s:if>
					</s:if>
				</div>
			</s:if>

			<div class="detail-subrow">
				<s:set var="elencoCategorie" value="%{#lotto.categoria}"/>
				<s:include value="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/bandi/inc/iteratorCategorie.jsp" />
			</div>
		</div>
	</s:iterator>
	
	<div class="back-link">
		<a href="<wp:action path="/ExtStr2/do/FrontEnd/Bandi/view.action" />&amp;codice=${param.codice}&amp;ext=${param.ext}">
			<wp:i18n key="LINK_BACK_TO_PROCEDURE" />
		</a>
	</div>
</div>