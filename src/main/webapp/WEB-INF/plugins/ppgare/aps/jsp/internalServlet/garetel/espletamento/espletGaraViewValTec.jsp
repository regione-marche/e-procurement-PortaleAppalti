<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2>
		<s:if test="%{isConcorsoPrimoGrado() && isWith2Phase()}">
			<wp:i18n key="LABEL_VALUTAZIONE_TECNICA_PRIMO_GRADO" />
		</s:if>
		<s:else>
			<wp:i18n key="LABEL_VALUTAZIONE_TECNICA" />
		</s:else>
	</h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_GARA_TEL_VALUTAZIONE_TECNICA"/>
	</jsp:include>
	
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_messages.jsp" />

 	<div class="detail-row">
		<c:set var="showPunteggioTecnico" value="false" />
		<c:set var="showPunteggioRiparametrato" value="false" />
		<c:set var="showSoccorso" value="false" />
		<c:set var="showAmmissione" value="${faseGara >= 6}" />
		<s:iterator var="item" value="elencoOperatori" status="stat">
			<s:if test="%{faseGara >= 6 && #item.codiceModAgg == 6}" >
				<c:set var="showPunteggioTecnico" value="true" />
			</s:if>
			<s:if test="%{faseGara >= 6 && #item.codiceModAgg == 6}" >
				<c:set var="showPunteggioTecnico" value="true" />
				<s:if test="%{#item.riparametrazioneTecnica == 1 || #item.riparametrazioneTecnica == 2}" >
					<c:set var="showPunteggioRiparametrato" value="true" />
				</s:if>
			</s:if>
			<s:if test="%{#item.codiceAmmissione == 10 && faseGara <= 5}" >
				<c:set var="showSoccorso" value="true" />
			</s:if>
		</s:iterator>
		
		<div class="table-container">
			<table id="tableOperatori" summary="Tabella operatori" class="info-table">
				<thead>
					<tr>
						<th scope="col">
							<wp:i18n key="LABEL_NUMERO_PLICO" />
							<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/garetel/inc/espletOrderableColumn.jsp">
								<jsp:param name="identifier" value="PLICO" />
								<jsp:param name="action" value="espletGaraViewValTec" />
							</jsp:include>
						</th>
						<s:if test="%{lotto != null}">
							<th scope="col"><wp:i18n key="LABEL_CODICE_LOTTO" /></th>
						</s:if>
						<s:if test="%{!hideFiscalCode}">
							<th scope="col"><wp:i18n key="LABEL_CODICE_FISCALE" /></th>
						</s:if>
						<th scope="col"><wp:i18n key="LABEL_RAGIONE_SOCIALE" /></th>
						<th scope="col"><wp:i18n key="LABEL_BUSTA_TECNICA" /></th>
						<c:if test="${showPunteggioTecnico}" >
							<th scope="col">
							    <wp:i18n key="LABEL_PUNTEGGIO_TECNICO" />
                                <jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/garetel/inc/espletOrderableColumn.jsp">
                                    <jsp:param name="identifier" value="PUNTEGGIO" />
									<jsp:param name="action" value="espletGaraViewValTec" />
                                </jsp:include>
                            </th>
						</c:if>
						<c:if test="${showPunteggioRiparametrato}" >
							<th scope="col">
							    <wp:i18n key="LABEL_PUNTEGGIO_RIPARAMETRATO" />
                                <jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/garetel/inc/espletOrderableColumn.jsp">
                                    <jsp:param name="identifier" value="RIPARAMETRATO" />
									<jsp:param name="action" value="espletGaraViewValTec" />
                                </jsp:include>
							</th>
						</c:if>
						<c:if test="${showSoccorso}" >
							<th scope="col"><wp:i18n key="LABEL_SOCCORSO_ISTRUTTORIO_IN_CORSO" /></th>
						</c:if>
						<c:if test="${showAmmissione}" >
							<th scope="col"><wp:i18n key="LABEL_AMMISSIONE" /></th>
						</c:if>
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
							<s:if test="%{lotto != null}">
								<td>
									<s:property value="%{codiceLotto}" />
								</td>
							</s:if>
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
							<c:if test="${showPunteggioTecnico}" >
								<td class="money-content">
									<s:text name="format.money"><s:param value="#item.punteggioTecnico"/></s:text>
								</td>
							</c:if>
							<c:if test="${showPunteggioRiparametrato}" >
								<td class="money-content">
									<s:text name="format.money"><s:param value="#item.punteggioTecnicoRiparametrato"/></s:text>
								</td>
							</c:if>
							<c:if test="${showSoccorso}" >
								<td>
									<!-- soccorso istruttorio -->
									<s:if test="%{#item.codiceAmmissione == 10}">
										<wp:i18n key="LABEL_YES" />
									</s:if>
								</td>
							</c:if>
							<c:if test="${showAmmissione}" >
								<td>
									<s:property value="#item.ammissione" />
								</td>
							</c:if>
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
									<c:set var="href"><wp:action path="/ExtStr2/do/FrontEnd/GareTel/espletGaraViewValTecOperatore.action"/>&amp;codice=${param.codice}&amp;codiceLotto=${param.codiceLotto}&amp;codiceOper=<s:property value="%{#item.codiceOperatore}"/></c:set>
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
 	
	<s:if test="%{faseGara == 5}" >
		<div class="azioni">
			<form action="<wp:action path="/ExtStr2/do/FrontEnd/GareTel/espletGaraViewValTec.action"/>" method="post" class="azione">
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
				
				<div>
					<wp:i18n key="BUTTON_REFRESH" var="valueRefreshButton" />
					<wp:i18n key="TITLE_REFRESH" var="titleRefreshButton" />
					<s:submit value="%{#attr.valueRefreshButton}" title="%{#attr.titleRefreshButton}" cssClass="button" />
					<input type="hidden" name="codice" value="${param.codice}" />
					<input type="hidden" name="codiceLotto" value="${param.codiceLotto}" />
				</div>
			</form>
		</div>
	</s:if>
</div>

<div class="back-link">
	<s:if test="%{lottiDistinti}" >
		<c:set var="href" value="/ExtStr2/do/FrontEnd/GareTel/espletGaraViewValTecLotti.action" />
		<a href="<wp:action path="${href}" />&amp;codice=${param.codice}">
			<wp:i18n key="LINK_BACK" />
		</a>
	</s:if>
	<s:else>
		<c:set var="href" value="/ExtStr2/do/FrontEnd/GareTel/espletGaraFasi.action" />
		<a href="<wp:action path="${href}" />&amp;codice=${param.codice}&amp;ext=${param.ext}">
			<wp:i18n key="LINK_BACK" />
		</a>
	</s:else>
</div>



