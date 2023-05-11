<%@ taglib prefix="wp" uri="aps-core.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="action" value="${param.action}"/>
<%-- <c:set var="codice" value="${param.codice}"/> --%>


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp">
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2><wp:i18n key="LABEL_ASTA_ELETTRONICA" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_LOTTI_ASTA" />
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />

	<div class="detail-section first-detail-section">
		<h3 class="detail-section-title">
			<span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_LISTA_LOTTI" />
		</h3>

		<div class="detail-row">		
			<div class="table-container">
				<table id="tableLotti" summary="Tabella lotti" class="info-table">
					<thead>
						<tr>
							<th scope="col"><wp:i18n key="LABEL_LOTTO" /></th>
							<th scope="col"><wp:i18n key="LABEL_FASE_ASTA" /></th>
							<th scope="col"><wp:i18n key="LABEL_INFORMAZIONI" /></th>
						</tr>
					</thead>
					<tbody>										
						<s:iterator var="item" value="lotti" status="stat">
							<s:set var="lotto" value="#item.key" />
							<s:set var="asta" value="#item.value" />
							
							<s:if test="#lotto.codiceInterno != null && #lotto.codiceInterno != ''" >							
								<!-- action = { classifica | riepilogo } -->
								<c:set var="url" value="" scope="page"/>
								<c:set var="titolo" value=""/>								
								<c:set var="stato" value=""/>					
								<c:set var="descStato" value=""/>			
								
								<s:if test="#asta != null" >
									<c:if test="${action == 'classifica'}" >
										<s:if test="#asta.termineAsta > 0" >
											<c:set var="url" value="classifica" />
											<c:set var="titolo"><wp:i18n key="LABEL_CONFERMA_OFFERTA" /></c:set>
											<c:set var="stato"><wp:i18n key="LABEL_TERMINATA" /></c:set>
											<c:set var="descStato"><wp:i18n key="LABEL_ASTA_TERMINATA" /></c:set>
										</s:if>
										<s:elseif test="#asta.attiva" >
											<c:set var="url" value="classifica" />
											<c:set var="titolo"><wp:i18n key="LABEL_CLASSIFICA_ASTA" /></c:set>
											<c:set var="stato"><wp:i18n key="LABEL_ATTIVA" /></c:set>
											<c:set var="descStato"><wp:i18n key="LABEL_ASTA_APERTA" /></c:set>
										</s:elseif>
									</c:if>															
									<c:if test="${action == 'riepilogo' || urlPage == ''}">
										<c:set var="url" value="riepilogo" />
										<c:set var="titolo"><wp:i18n key="LABEL_RIEPILOGO_ASTA" /></c:set>
										<wp:i18n key="LABEL_RIEPILOGO" var="labelRiepilogo"/>
										<c:set var="stato">${fn:toLowerCase(labelRiepilogo)}</c:set>
										<c:set var="descStato"><wp:i18n key="LABEL_RIEPILOGO" /></c:set>
									</c:if>
								</s:if>

								<c:if test="${url != ''}">
									<c:set var="url"><wp:action path="/ExtStr2/do/FrontEnd/Aste/${url}.action"/>&amp;codice=${codice}&amp;codiceLotto=<s:property value="#lotto.codiceLotto"/>&amp;${tokenHrefParams}</c:set>
								</c:if>
								<c:if test="${url == ''}">
									<c:set var="url"><wp:action path="/ExtStr2/do/FrontEnd/Bandi/view.action"/>&amp;codice=${codice}&amp;ext=&amp;${tokenHrefParams}</c:set>
								</c:if>

								<tr>
									<td class="azioni">
										<a href="${url}" title="${descStato}" class="<s:property value="cssClass"/>">
											<s:property value="#lotto.codiceInterno" /> - <s:property value="#lotto.oggetto" />
										</a>
									</td>
									<td > 
										<s:if test="#asta.dataOraApertura != null">
											<wp:i18n key="LABEL_APERTURA" />: <s:date name="#asta.dataOraApertura" format="dd/MM/yyyy HH:mm" />
										</s:if>
										<s:if test="#asta.dataOraChiusura != null">
											<wp:i18n key="LABEL_CHIUSURA" />: <s:date name="#asta.dataOraChiusura" format="dd/MM/yyyy HH:mm" />
										</s:if>
									</td>
									<td >
										${descStato}
									</td>
								</tr>
 							</s:if>
						</s:iterator>
					</tbody>
				</table>
			</div>

		</div>
	</div>
	
	<div class="back-link">
		<a href="<wp:action path="/ExtStr2/do/FrontEnd/Bandi/view.action"/>&amp;codice=${codice}&amp;ext=&amp;${tokenHrefParams}">
			<wp:i18n key="LINK_BACK_TO_PROCEDURE" />
		</a>
	</div>
</div>