<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2><wp:i18n key="TITLE_PAGE_GARETEL_APERTURA_DOC_AMMINISTRATIVA" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_GARA_TEL_APERTURA_DOC_AMM"/>
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_messages.jsp" />

	<div class="detail-row">
		<c:set var="showSoccorso" value="false" />
		<s:iterator var="item" value="elencoOperatori" status="stat">
			<s:if test="%{#item.codiceAmmissione == 10 && faseGara < 5}" >
				<c:set var="showSoccorso" value="true" />
			</s:if>
		</s:iterator>
	
		<div class="table-container">
			<table id="tableOperatori" summary="Tabella operatori" class="info-table">
				<thead>
					<tr>
						<th scope="col"><wp:i18n key="LABEL_NUMERO_PLICO" /></th>
						<s:if test="%{!hideFiscalCode}">
						    <th scope="col"><wp:i18n key="LABEL_CODICE_FISCALE" /></th>
						</s:if>
						<th scope="col"><wp:i18n key="LABEL_RAGIONE_SOCIALE" /></th>
						<th scope="col"><wp:i18n key="LABEL_BUSTA_AMMINISTRATIVA" /></th>
						<c:if test="${showSoccorso}" >
							<th scope="col"><wp:i18n key="LABEL_SOCCORSO_ISTRUTTORIO_IN_CORSO" /></th>
						</c:if>
						<s:if test="%{faseGara >= 5}" >
							<th scope="col"><wp:i18n key="LABEL_AMMISSIONE" /></th>
						</s:if>
						<%--
						<c:if test="${proceduraInversa}" >
							<th scope="col">Esito procedura inversa</th> 
						</c:if>
						--%>
						<th scope="col" class="action" style="min-width: 4em;"><wp:i18n key="ACTIONS" /></th>
					</tr>
				</thead>
				<tbody>
					<s:iterator var="item" value="elencoOperatori" status="stat">
						<tr>
							<td>
								<s:property value="#item.numeroPlico" /> 
							</td>
							<s:if test="%{!hideFiscalCode}">
                                <td>
                                    <s:property value="#item.codiceFiscale" />
                                </td>
							</s:if>
							<td>
								<s:property value="#item.ragioneSociale" />
							</td>
							<td>
								<s:if test="%{#item.statoBusta == 1}" >
									<wp:i18n key="LABEL_CHIUSA" />
								</s:if>
								<s:elseif test="%{#item.statoBusta == 2}" >
									<wp:i18n key="LABEL_APERTA" />
								</s:elseif>
								<s:else>
									<wp:i18n key="LABEL_NON_PRESENTATA" />
								</s:else>
							</td>
							<c:if test="${showSoccorso}" >
								<td>
									<!-- soccorso istruttorio -->
									<s:if test="%{#item.codiceAmmissione == 10}">
										<wp:i18n key="LABEL_YES" />
									</s:if>
								</td>
							</c:if>
							<s:if test="%{faseGara >= 5}" >
								<td>
									<s:property value="#item.ammissione" />
								</td>
							</s:if>
							<%--
							<c:if test="${proceduraInversa}" >
								<td>
									<s:if test="%{#item.esitoProceduraInversa != null}">
										<s:if test="%{#item.esitoProceduraInversa}" >Idonea</s:if><s:else>Non idonea</s:else>
									</s:if>
								</td>
							</c:if>
							--%>
							<td class="azioni">
								<s:if test="%{#item.statoBusta == 2}" >
								<c:set var="href"><wp:action path="/ExtStr2/do/FrontEnd/GareTel/espletGaraViewDocAmmOperatore.action"/>&amp;codice=${param.codice}&amp;codiceOper=<s:property value="%{#item.codiceOperatore}"/></c:set>
								<c:choose>
									<c:when test="${skin == 'highcontrast' || skin == 'text'}">
										<a href='${href}' title='<wp:i18n key="LABEL_VISUALIZZA_DETTAGLIO_PLICO" />'>
											<wp:i18n key="LABEL_VISUALIZZA_DETTAGLIO_PLICO" />
										</a>
								 	</c:when>
								 	<c:otherwise>
								 		<a href='${href}' title='<wp:i18n key="LABEL_VISUALIZZA_DETTAGLIO_PLICO" />' class="bkg detail">
								 		</a>
								 	</c:otherwise>
								</c:choose>
								</s:if>
							</td>
						</tr>
					</s:iterator>
				</tbody>
			</table>
		</div>
	</div>	
	
	<s:if test="%{faseGara == 2 || proceduraInversa}" >
		<div class="azioni">
			<form action="<wp:action path="/ExtStr2/do/FrontEnd/GareTel/espletGaraViewDocAmm.action"/>" method="post" class="azione">
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
				
				<div>
					<wp:i18n key="BUTTON_REFRESH" var="valueRefreshButton" />
					<wp:i18n key="TITLE_REFRESH" var="titleRefreshButton" />
					<s:submit value="%{#attr.valueRefreshButton}" title="%{#attr.titleRefreshButton}" cssClass="button" />
					<input type="hidden" name="codice" value="${param.codice}" />
				</div>
			</form>
		</div>
	</s:if>
</div>

<div class="back-link">
	<a href="<wp:action path="/ExtStr2/do/FrontEnd/GareTel/espletGaraFasi.action" />&amp;codice=${codice}&amp;ext=${param.ext}">
		<wp:i18n key="LINK_BACK" />
	</a>
</div>
