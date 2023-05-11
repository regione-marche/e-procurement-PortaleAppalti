<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>

<es:checkCustomization var="visNotificaLettura" objectId="GARE-ESPLETAMENTO" attribute="LETTURADOC" feature="VIS" />
<s:set var="imgRead"><wp:resourceURL/>static/img/read.png</s:set>


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2><wp:i18n key="TITLE_PAGE_GARETEL_OFFERTA_ECONOMICA" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_GARA_TEL_OFFERTA_ECONOMICA_OP"/>
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_messages.jsp" />

	<div class="fieldset-row first-row">
		<div class="label">
			<label><wp:i18n key="LABEL_NUMERO_PLICO" /> : </label>
		</div>
		<div class="element">
			${operatoreEconomico.numeroPlico}
		</div>
 	</div> 	
 	
 	<s:if test="%{!operatoreEconomico.rti}">
	 	<div class="fieldset-row">
			<div class="label">
				<label><wp:i18n key="LABEL_CODICE_FISCALE" />: </label>
			</div>
			<div class="element">
				${operatoreEconomico.codiceFiscale}
			</div>
	 	</div>
	 </s:if>
 	
 	<div class="fieldset-row">
		<div class="label">
			<label>
				<s:if test="%{!operatoreEconomico.rti}"><wp:i18n key="LABEL_RAGIONE_SOCIALE" />:</s:if>
				<s:else><wp:i18n key="LABEL_DENOMINAZIONE_RTI" /></s:else>
			</label>						
		</div>
		<div class="element">
			${operatoreEconomico.ragioneSociale}
		</div>
 	</div>
 	
 	<%-- solo se l'operatore partecipa in RTI --%>
 	<s:if test="%{operatoreEconomico.rti}">
	 	<div class="fieldset-row">
			<div class="label">
				<label><wp:i18n key="LABEL_RAGGRUPPAMENTO_COMPOSTO_DA" />: </label>
			</div>
			
			<div class="element">
				<div class="table-container">	
					<table id="tableOperatori" summary="Tabella operatori" class="info-table">
						<thead>
							<tr>
								<th scope="col"><wp:i18n key="LABEL_CODICE_FISCALE" /></th>
								<th scope="col"><wp:i18n key="LABEL_RAGIONE_SOCIALE" /></th>
								<th scope="col"><wp:i18n key="LABEL_MANDATARIA" /></th>
							</tr>
						</thead>
						<tbody>						
							<s:iterator var="item" value="operatoreEconomico.componentiRTI" status="stat">
								<tr>
									<td>				
										<s:if test="%{#item.partitaIva != null}" >					
											<s:property value="#item.partitaIva" />
										</s:if>
										<s:else>
											<s:property value="#item.codiceFiscale" />
										</s:else>
									</td>
									<td>								
										<s:property value="#item.ragioneSociale" />
									</td>							
									<td>
										<s:if test="%{#item.mandataria == 1}"><wp:i18n key="LABEL_YES" /></s:if><s:else><wp:i18n key="LABEL_NO" /></s:else>
									</td>
								</tr>								
							</s:iterator> 							
						</tbody>
					</table>			
				</div>
			</div>
	 	</div>
	</s:if> 
 
 	<%-- documenti --%>
 	<div class="fieldset-row">
		<div class="label">
			<label><wp:i18n key="LABEL_DOCUMENTI_ALLEGATI" /> : </label>
		</div>
		
		<div class="element">
			<ul class="list">
				<s:iterator var="item" value="operatoreEconomico.documenti" status="stat">		
					<li class='<s:if test="%{#stat.index == 0}">first</s:if> <s:if test="%{#stat.index == operatoreEconomico.documenti.size-1}">last</s:if>'>
						<s:property value="#item.descrizione" /> (<s:property value="#item.filename" />)&nbsp;
						<s:if test="%{#attr.visNotificaLettura && #item.dataLettura != null}" >
							<c:choose>
								<c:when test="${skin == 'highcontrast' || skin == 'text'}">
									<wp:i18n key="LABEL_DOCUMENTO_LETTO" />
								</c:when>
								<c:otherwise>
									<img src="${imgRead}" title="<wp:i18n key="LABEL_DOCUMENTO_LETTO" />" />
								</c:otherwise>
							</c:choose>
						</s:if>
					</li>
				</s:iterator>
			</ul>
		</div>
	</div>	
	
	
	<s:set var="showRialzoOfferto" value="%{operatoreEconomico.codiceModAgg == 17}" />
	<s:set var="showImportoOfferto" value="%{operatoreEconomico.codiceModAgg != 6 &&
	                 						(operatoreEconomico.codiceModAgg == 5 || operatoreEconomico.codiceModAgg == 14 || operatoreEconomico.tipoRibasso == 2)}" />
	<s:set var="showRibassoOfferto" value="%{operatoreEconomico.codiceModAgg != 6 && operatoreEconomico.tipoRibasso == 1}" />
	<s:set var="showPunteggio" value="%{operatoreEconomico.codiceModAgg == 6}" />
	<s:set var="showPunteggioRiparametrato" value="%{operatoreEconomico.riparametrazioneEconomica == 1 || 
	 	              								 operatoreEconomico.riparametrazioneEconomica == 2}" />
	<s:set var="oepv" value="%{operatoreEconomico.codiceModAgg == 6}" />
	
	<%-- in caso di QFORM (TORN.OFFTEL = 3) ribasso e importo vanno visualizzati sempre 
		 a meno che in BO appalti non venga configurato diversamente --%>
	<s:if test="%{tipoOffertaTelematica == 3}">
		<s:set var="showRibassoOfferto" value="true" />
	</s:if>
	
	<%-- verifica la configurazione "altri dati" in BO ("nascondi importo", "nascondi ribasso", "nascondi entrambi") --%>
	<c:if test="${nascondiValoriEspletamento == 1 || nascondiValoriEspletamento == 3}">
		<s:set var="showImportoOfferto" value="false"/>
	</c:if>
	<c:if test="${nascondiValoriEspletamento == 2 || nascondiValoriEspletamento == 3}">
		<s:set var="showRibassoOfferto" value="false" />
	</c:if>
	
	<%-- se TORN.ITERGA = 8 l'importo non e' visibile --%>
	<c:if test="${dettaglioGara.datiGeneraliGara.iterGara == 8}" >
		<s:set var="showImportoOfferto" value="false" />
		<s:set var="showRialzoOfferto" value="false" />
		<s:set var="showRibassoOfferto" value="false" />
		<s:set var="showPunteggio" value="false" />
		<s:set var="showPunteggioRiparametrato" value="false" />
	</c:if>
	
<%--
operatoreEconomico.codiceModAgg=<s:property value="%{operatoreEconomico.codiceModAgg}" /><br/>
tipoOffertaTelematica=<s:property value="%{tipoOffertaTelematica}" /><br/>
oepv=<s:if test="%{oepv}" >true</s:if><s:else>false</s:else><br/>
showRialzoOfferto=<s:if test="%{showRialzoOfferto}" >true</s:if><s:else>false</s:else><br/>
showImportoOfferto=<s:if test="%{showImportoOfferto}" >true</s:if><s:else>false</s:else><br/>
showRibassoOfferto=<s:if test="%{showRibassoOfferto}" >true</s:if><s:else>false</s:else><br/>
--%>

 	<s:set var="ribassoRialzo" value="operatoreEconomico.ribassoOfferto" />

	<s:if test="%{#showRialzoOfferto}" >
	 	<div class="fieldset-row">
			<div class="label">
				<label><wp:i18n key="LABEL_RIALZO_OFFERTO" /> : </label>
			</div>
			<div class="element">
				<s:if test="%{#ribassoRialzo != null && #ribassoRialzo != 0}" >
					<s:text name="format.money5dec"><s:param value="operatoreEconomico.ribassoOfferto"/></s:text>
				</s:if>
			</div>
	 	</div>
 	</s:if>
 	<s:elseif test="%{#showImportoOfferto || #showRibassoOfferto}" >
 	 	<s:if test="%{#showImportoOfferto}" >
		 	<div class="fieldset-row">
				<div class="label">
					<label><wp:i18n key="LABEL_IMPORTO_OFFERTO" /> : </label>
				</div>
				<div class="element">
					<s:if test="%{operatoreEconomico.importoOfferto != null}" >
						<s:text name="format.money"><s:param value="operatoreEconomico.importoOfferto"/></s:text>
					</s:if>
				</div>
		 	</div>
		</s:if>
		<s:if test="%{#showRibassoOfferto}">
			<div class="fieldset-row">
				<div class="label">
					<label><wp:i18n key="LABEL_RIBASSO_OFFERTO" /> : </label>
				</div>
				<div class="element">
					<s:if test="%{#ribassoRialzo != null}" >
							<s:text name="format.money5dec"><s:param value="#ribassoRialzo"/></s:text>
					</s:if>
				</div>
		 	</div>
		</s:if>
 	</s:elseif>
	<s:elseif test="%{#showPunteggio}">
	 	<div class="fieldset-row">
			<div class="label">
				<label><wp:i18n key="LABEL_PUNTEGGIO_ECONOMICO" /> : </label>
			</div>
			<div class="element">
				<s:if test="%{operatoreEconomico.punteggioEconomico != null}" >
					<s:text name="format.money"><s:param value="operatoreEconomico.punteggioEconomico"/></s:text>
				</s:if>
			</div>
	 	</div>
	 	
	 	<s:if test="%{#showPunteggioRiparametrato}" >
		 	<div class="fieldset-row">
				<div class="label">
					<label><wp:i18n key="LABEL_PUNTEGGIO_RIPARAMETRATO" /> : </label>
				</div>
				<div class="element">
					<s:if test="%{operatoreEconomico.punteggioEconomicoRiparametrato != null}" >
						<s:property value="operatoreEconomico.punteggioEconomicoRiparametrato" />
					</s:if>
				</div>
		 	</div>
	 	</s:if>	 
	</s:elseif>
	  	
	<s:if test="%{faseGara > 6}" >
	 	<div class="fieldset-row">
			<div class="label">
				<label><wp:i18n key="LABEL_AMMISSIONE" /> : </label>
			</div>
			<div class="element">
				${operatoreEconomico.ammissione}
			</div>
	 	</div>
	 </s:if>
 	
</div>

<div class="back-link">
	<a href="<wp:action path="/ExtStr2/do/FrontEnd/GareTel/espletGaraViewOffEco.action" />&amp;codice=${param.codice}&amp;codiceLotto=${param.codiceLotto}&amp;${tokenHrefParams}">
		<wp:i18n key="LINK_BACK" />
	</a>
</div>
