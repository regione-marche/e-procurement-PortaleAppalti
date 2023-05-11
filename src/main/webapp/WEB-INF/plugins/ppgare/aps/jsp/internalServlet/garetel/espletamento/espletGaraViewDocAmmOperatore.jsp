<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>

<es:checkCustomization var="visNotificaLettura" objectId="GARE-ESPLETAMENTO" attribute="LETTURADOC" feature="VIS" />
<s:set var="imgRead"><wp:resourceURL/>static/img/read.png</s:set>


<c:set var="lottiCount" value="0" />
<s:iterator var="item" value="operatoreEconomico.lotti">
	<c:set var="lottiCount" value="${lottiCount+1}" />
</s:iterator>
<c:set var="lottiDistinti" value="${lottiCount > 0}" />

<!-- 
operatoreEconomico=<s:property value="operatoreEconomico" /><br/>
operatoreEconomico.lotti=<s:property value="operatoreEconomico.lotti" /><br/>
lottiCount=${lottiCount} <br/>
lottiDistinti=${lottiDistinti} <br/>
 -->

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2><wp:i18n key="TITLE_PAGE_GARETEL_APERTURA_DOC_AMMINISTRATIVA" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_GARA_TEL_APERTURA_DOC_AMM_OP"/>
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
				<label><wp:i18n key="LABEL_RAGGRUPPAMENTO_COMPOSTO_DA" /> : </label>
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
 
 	<%-- solo per gara a lotti --%>
 	<c:if test="${lottiDistinti}" >
	 	<div class="fieldset-row">
			<div class="label">
				<label><wp:i18n key="LABEL_LOTTI_A_CUI_PARTECIPA" /> : </label>
			</div>
			
			<div class="element">
				<div class="table-container">
					<table id="tableLotti" summary='<wp:i18n key="LABEL_TABELLA_LOTTI" />' class="info-table">
						<thead>
							<tr>
								<th scope="col"><wp:i18n key="LABEL_CODICE_LOTTO" /></th>
								<th scope="col"><wp:i18n key="LABEL_OGGETTO" /></th>
								<s:if test="%{faseGara >= 5}" >
									<th scope="col"><wp:i18n key="LABEL_AMMISSIONE" /></th>
								</s:if>
							</tr>
						</thead>
						<tbody>
							<s:iterator var="item" value="operatoreEconomico.lotti" status="stat">
								<tr>
									<td align="center">
										<s:property value="#item.codiceInterno" /> 
									</td>
									<td align="center">
										<s:property value="#item.oggetto" />
									</td>
									<s:if test="%{faseGara >= 5}" >
										<td align="center">
											<s:property value="#item.ammissione" />
										</td>
									</s:if>
								</tr>
							</s:iterator>
						</tbody>
					</table>
				</div>
			</div>
	 	</div>
	</c:if>
 	
	<s:if test="%{faseGara >= 5}" >
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
	<a href="<wp:action path="/ExtStr2/do/FrontEnd/GareTel/espletGaraViewDocAmm.action" />&amp;codice=${codice}&amp;${tokenHrefParams}">
		<wp:i18n key="LINK_BACK" />
	</a>
</div>
