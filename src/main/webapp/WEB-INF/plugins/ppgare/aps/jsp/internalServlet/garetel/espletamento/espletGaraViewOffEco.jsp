<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<c:set var="codiceGara" value="${param.codice}" />
<c:if test="${lottiDistinti}">
	<c:set var="codiceGara" value="${param.codiceLotto}" />
</c:if>


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2><wp:i18n key="TITLE_PAGE_GARETEL_OFFERTA_ECONOMICA" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_GARA_TEL_OFFERTA_ECONOMICA"/>
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_messages.jsp" />

	<div class="detail-row">
		<c:set var="showImportoOfferto" value="false" />
		<c:set var="showRibassoOfferto" value="false" />
		<c:set var="showRialzoOfferto" value="false" />
		<c:set var="showPunteggioEconomico" value="false" />
		<c:set var="showPunteggioRiparametrato" value="false" />
		<c:set var="showSoccorso" value="false" />
		<c:set var="showAmmissione" value="${faseGara > 6}" />
		<c:set var="oepv" value="false" />
		<s:iterator var="item" value="elencoOperatori" status="stat">
			<s:if test="%{#item.codiceModAgg == 17}" >
				<c:set var="showRialzoOfferto" value="true" />
			</s:if>
			<s:elseif test="%{#item.codiceModAgg != 6 && 
						     (#item.codiceModAgg == 5 || #item.codiceModAgg == 14 || #item.tipoRibasso == 2)}">
				<c:set var="showImportoOfferto" value="true" />
			</s:elseif>
			<s:elseif test="%{#item.codiceModAgg != 6 && #item.tipoRibasso == 1}">
				<c:set var="showRibassoOfferto" value="true" />
			</s:elseif>
			<s:elseif test="%{faseGara > 6 && #item.codiceModAgg == 6}">
				<c:set var="showPunteggioEconomico" value="true" />
				<s:if test="%{#item.riparametrazioneEconomica == 1 || #item.riparametrazioneEconomica == 2}" >
					<c:set var="showPunteggioRiparametrato" value="true" />
				</s:if>
			</s:elseif>
			<s:if test="%{#item.codiceAmmissione == 10}" >
				<c:set var="showSoccorso" value="true" />
			</s:if>
			
			<%-- codiceModAgg = 6 allora e' una OEPV --%>
			<s:if test="%{#item.codiceModAgg == 6}" >
				<c:set var="oepv" value="true" />
			</s:if>
		</s:iterator>

		<%-- in caso di QFORM (TORN.OFFTEL = 3) ribasso e importo vanno visualizzati sempre 
		 	 a meno che in BO appalti non venga configurato diversamente --%>
		<c:if test="${tipoOffertaTelematica == 3}" >
			<c:set var="showRibassoOfferto" value="true" />
		</c:if>
		
		<%-- verifica la configurazione "altri dati" in BO ("nascondi importo", "nascondi ribasso", "nascondi entrambi") --%>
		<c:if test="${nascondiValoriEspletamento == 1 || nascondiValoriEspletamento == 3}">
			<c:set var="showImportoOfferto" value="false"/>
		</c:if>
		<c:if test="${nascondiValoriEspletamento == 2 || nascondiValoriEspletamento == 3}">
			<c:set var="showRibassoOfferto" value="false" />
		</c:if>
		
		<%-- se TORN.ITERGA = 8 l'importo non e' visibile --%>
		<c:if test="${dettaglioGara.datiGeneraliGara.iterGara == 8}" >
			<c:set var="showImportoOfferto" value="false" />
			<c:set var="showRibassoOfferto" value="false" />
			<c:set var="showRialzoOfferto" value="false" />
			<c:set var="showPunteggioEconomico" value="false" />
			<c:set var="showPunteggioRiparametrato" value="false" />
		</c:if>
<!--
oepv=${oepv}<br/>
tipoOffertaTelematica=${tipoOffertaTelematica}<br/>
showImportoOfferto=${showImportoOfferto}<br/>
showRibassoOfferto=${showRibassoOfferto}<br/>
showPunteggioEconomico=${showPunteggioEconomico}<br/>
showAmmissione=${showAmmissione}<br/>
-->

		<div class="table-container">
			<table id="tableOperatori" summary="Tabella operatori" class="info-table">
				<thead>
					<tr>
						<th scope="col"><wp:i18n key="LABEL_NUMERO_PLICO" /></th>
						<th scope="col"><wp:i18n key="LABEL_CODICE_LOTTO" /></th>
						<th scope="col"><wp:i18n key="LABEL_CODICE_FISCALE" /></th>
						<th scope="col"><wp:i18n key="LABEL_RAGIONE_SOCIALE" /></th>
						<th scope="col"><wp:i18n key="LABEL_BUSTA_ECONOMICA" /></th>
						<c:if test="${showImportoOfferto}" >
							<th scope="col"><wp:i18n key="LABEL_IMPORTO_OFFERTO" /></th>
						</c:if>
						<c:if test="${showRibassoOfferto}" >
							<th scope="col"><wp:i18n key="LABEL_RIBASSO_OFFERTO" /></th> 
						</c:if>
						<c:if test="${showRialzoOfferto}" >
							<th scope="col"><wp:i18n key="LABEL_RIALZO_OFFERTO" /></th> 
						</c:if>
						<c:if test="${showPunteggioEconomico}" >
							<th scope="col"><wp:i18n key="LABEL_PUNTEGGIO_ECONOMICO" /></th>
						</c:if>
						<c:if test="${showPunteggioRiparametrato}" >
							<th scope="col"><wp:i18n key="LABEL_PUNTEGGIO_RIPARAMETRATO" /></th> 
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
							<td>
								<s:property value="%{lotto}" />
							</td>
							<td>
								<s:property value="#item.codiceFiscale" />
							</td>
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
							<c:if test="${showImportoOfferto}" >
								<td class="money-content">
									<s:if test="%{#item.importoOfferto != null}" >
										<s:text name="format.money"><s:param value="#item.importoOfferto"/></s:text>
									</s:if>
								</td>
							</c:if>
							
 							<s:set var="ribassoRialzo" value="#item.ribassoOfferto" />
 							
							<c:if test="${showRibassoOfferto}" >
								<td class="money-content">
									<s:if test="%{#ribassoRialzo != null}" >
											<s:text name="format.money5dec"><s:param value="#ribassoRialzo"/></s:text>
									</s:if>
								</td> 
							</c:if>
							<c:if test="${showRialzoOfferto}" >
								<td class="money-content">
									<s:if test="%{#ribassoRialzo != null && #ribassoRialzo != 0}" >
										<s:text name="format.money5dec"><s:param value="#item.ribassoOfferto"/></s:text>
									</s:if>
								</td> 
							</c:if>
							<c:if test="${showPunteggioEconomico}" >
								<td class="money-content">
									<s:if test="%{#item.punteggioEconomico != null}" >
										<s:text name="format.money"><s:param value="#item.punteggioEconomico"/></s:text>
									</s:if>
								</td> 
							</c:if>
							<c:if test="${showPunteggioRiparametrato}" >
								<td class="money-content">
									<s:property value="#item.punteggioEconomicoRiparametrato" />
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
								<c:set var="href"><wp:action path="/ExtStr2/do/FrontEnd/GareTel/espletGaraViewOffEcoOperatore.action"/>&amp;codice=${param.codice}&amp;codiceLotto=${param.codiceLotto}&amp;codiceOper=<s:property value="%{#item.codiceOperatore}"/>&amp;${tokenHrefParams}</c:set>
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
	
	<s:if test="%{faseGara == 6}" >
		<c:if test="${dettaglioGara.datiGeneraliGara.iterGara != 8}" >
			<div class="azioni">
				<div class="azioni">
					<form action="<wp:action path="/ExtStr2/do/FrontEnd/GareTel/espletGaraViewOffEco.action"/>" method="post" class="azione">
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
			</div>
		</c:if>
	</s:if>	
</div>

<div class="back-link">
	<s:if test="%{lottiDistinti}" >
		<c:set var="href" value="/ExtStr2/do/FrontEnd/GareTel/espletGaraViewOffEcoLotti.action" />
		<a href="<wp:action path="${href}" />&amp;codice=${param.codice}&amp;${tokenHrefParams}">
			<wp:i18n key="LINK_BACK" />
		</a>
	</s:if>
	<s:else>
		<c:set var="href" value="/ExtStr2/do/FrontEnd/GareTel/espletGaraFasi.action" />
		<a href="<wp:action path="${href}" />&amp;codice=${param.codice}&amp;ext=${param.ext}&amp;${tokenHrefParams}">
			<wp:i18n key="LINK_BACK" />
		</a>
	</s:else>
</div>
