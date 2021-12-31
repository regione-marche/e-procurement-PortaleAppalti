<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>


<div class="portgare-view">

	<h2><wp:i18n key="LABEL_GRADUATORIA" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_GARA_TEL_GRADUATORIA"/>
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_messages.jsp" />

	<div class="detail-row">
		<c:set var="showImportoOfferto" value="false" />
		<c:set var="showRibassoOfferto" value="false" />
		<c:set var="showRialzoOfferto" value="false" />
		<c:set var="showPunteggio" value="false" />
		<c:set var="showEsitoInversa" value="${proceduraInversa}" />
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
				<c:set var="showPunteggio" value="true" />
			</s:elseif>
		</s:iterator>
		
		<div class="table-container">
			<table id="tableOperatori" summary="Tabella operatori" class="info-table">
				<thead>
					<tr>
						<th scope="col"><wp:i18n key="LABEL_NUMERO_PLICO" /></th>
						<th scope="col"><wp:i18n key="LABEL_CODICE_FISCALE" /></th>
						<th scope="col"><wp:i18n key="LABEL_RAGIONE_SOCIALE" /></th>
						<c:if test="${showImportoOfferto}" >
							<th scope="col"><wp:i18n key="LABEL_IMPORTO_OFFERTO" /></th>
						</c:if>
						<c:if test="${showRibassoOfferto}" >
							<th scope="col"><wp:i18n key="LABEL_RIBASSO_OFFERTO" /></th> 
						</c:if>
						<c:if test="${showRialzoOfferto}" >
							<th scope="col"><wp:i18n key="LABEL_RIALZO_OFFERTO" /></th> 
						</c:if>
						<c:if test="${showPunteggio}" >
							<th scope="col"><wp:i18n key="LABEL_PUNTEGGIO_TOTALE" /></th> 
						</c:if>
						<th scope="col"><wp:i18n key="LABEL_GRADUATORIA" /></th>
						<c:if test="${proceduraInversa}" >
							<th scope="col"><wp:i18n key="LABEL_ESITO_PROCEDURA_INVERSA" /></th> 
						</c:if>
					</tr>
				</thead>
				<tbody>
					<s:iterator var="item" value="elencoOperatori" status="stat">
						<tr>
							<td>
								<s:property value="#item.numeroPlico" />
							</td>
							<td>
								<s:property value="#item.codiceFiscale" />
							</td>
							<td>
								<s:property value="#item.ragioneSociale" />
							</td>
							<c:if test="${showImportoOfferto}" >
								<td class="money-content">
									<s:if test="%{#item.importoOfferto != null}" >
										<s:text name="format.money"><s:param value="#item.importoOfferto"/></s:text>
									</s:if>
								</td>
							</c:if>
							
							<%-- normalizza il valore del ribasso               --%> 							
							<%-- se il valore e' troppo basso non visualizzarlo --%>
							<%-- quindi =>  abs(ribasso-0.0000) > 1/10000 => OK --%>
							<s:set var="e" value="0.00001" />
							<s:set var="ribassoRialzo" value="#item.ribassoOfferto" />							
							<s:if test="%{#ribassoRialzo < 0}" ><s:set var="ribassoRialzo" value="-1*#ribassoRialzo" /></s:if>
							<s:if test="%{#ribassoRialzo < #e}" ><s:set var="ribassoRialzo" value="0" /></s:if>
									
							<c:if test="${showRibassoOfferto}" >
								<td class="money-content">
									<s:if test="%{#ribassoRialzo != null && #ribassoRialzo != 0}" >
										<s:text name="format.money5dec"><s:param value="-1 * #item.ribassoOfferto"/></s:text>
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
							
							<c:if test="${showPunteggio}" >
								<td class="money-content">
									<s:text name="format.money"><s:param value="#item.punteggioTotale"/></s:text>
								</td> 
							</c:if>	
							<td>
								<s:property value="#item.graduatoria" />
							</td>
							<c:if test="${proceduraInversa}" >
								<td>
									<s:if test="%{#item.esitoProceduraInversa != null}">
										<s:if test="%{#item.esitoProceduraInversa}" ><wp:i18n key="LABEL_IDONEA" /></s:if><s:else><wp:i18n key="LABEL_NON_IDONEA" /></s:else>
									</s:if>
								</td>
							</c:if>
						</tr>
					</s:iterator>
				</tbody>
			</table>
		</div>
	</div>	
	
	<s:if test="%{faseGara < 8}" >
		<div class="azioni">
			<form action="<wp:action path="/ExtStr2/do/FrontEnd/GareTel/espletGaraViewGraduatoria.action"/>" method="post" class="azione">
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
		<c:set var="href" value="/ExtStr2/do/FrontEnd/GareTel/espletGaraViewGraduatoriaLotti.action" />
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
