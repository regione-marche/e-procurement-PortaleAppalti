<%@ taglib prefix="wp" uri="aps-core.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<%--
	inserire il parametro "elencoClassifica" nella pagina chiamante come segue
	   
	<s:set var="elencoClassifica" value="[lista]" /> 
	<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/aste/inc/iteratorClassifica.jsp" >
		<jsp:param name="tipoClassifica" value="..." />
	</jsp:include>		
--%>

<c:set var="tipoClassifica" value="${param.tipoClassifica}" />
<c:set var="simboloOfferta">&euro;</c:set>
<c:set var="oggettoOfferta"><wp:i18n key="LABEL_IMPORTO_OFFERTA" /></c:set>
<c:if test="${param.tipoOfferta == 1}" >
	<c:set var="simboloOfferta">%</c:set>
	<c:set var="oggettoOfferta"><wp:i18n key="LABEL_RIBASSO_PERCENTUALE" /></c:set>
</c:if>

<c:choose>
	<%-- CLASSIFICAZIONE GENERALE --%>
	<c:when test="${tipoClassifica == 3}" >
	
		<h3 class="detail-section-title">
			<span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_CLASSIFICA_CAPITALIZE" />
		</h3>
	
		<div class="detail-row">					
			<div class="table-container">	
				<table id="tableClassifica" summary='<wp:i18n key="LABEL_CLASSIFICA_TABELLA_SUMMARY" />' class="info-table">
					<thead>
						<tr>
							<th scope="col"><wp:i18n key="LABEL_ASTE_POSIZIONE" /></th>
							<th scope="col"><wp:i18n key="LABEL_ASTE_UTENTE" /></th>
							<th scope="col"><c:out value="${oggettoOfferta}"/></th>				
						</tr>
					</thead>
					<tbody>
						<s:iterator var="item" value="elencoClassifica" status="stat">
							<tr>
								<td align="center">
									<s:property value="#item.posizione" />&deg;
								</td>
								<td align="center">
<%-- 									<s:property value="#item.username" /> --%>
<%-- 									<s:property value="#item.mandataria" />--%>
									<s:property value="#item.ragioneSociale" />
								</td>
								<td class="money-content">
									<s:if test="%{#item.importo != null}" >
										<s:text name="format.money5dec"><s:param value="%{#item.importo}"/></s:text>&nbsp;${simboloOfferta}
									</s:if>
									<s:else>
										*****&nbsp;${simboloOfferta}
									</s:else>
								</td>
							</tr>
						</s:iterator>
					</tbody>
				</table>
			</div>
		</div>

	</c:when>
	
	<%-- CLASSIFICAZIONE PROPRIA E MIGLIORE OFFERTA IN ASSOLUTO --%>
	<c:when test="${tipoClassifica == 2}" >	
	
		<div class="detail-row">
			<label><wp:i18n key="LABEL_POSIZIONE_CLASSIFICA" /> : </label>
			<s:property value="#elencoClassifica[0].posizione" />&deg; <%-- classificato --%>				
		</div>
		<div class="detail-row">
			<label><wp:i18n key="LABEL_MIGLIOR_OFFERTA_PRESENTATA" /> : </label>
			<s:text name="format.money5dec"><s:param value="#elencoClassifica[1].importo"/></s:text>&nbsp;${simboloOfferta}
		</div>
		
	</c:when>
	
	<%-- CLASSIFICAZIONE SOLO PROPRIA --%>
	<c:otherwise>
	
		<div class="detail-row">
			<label><wp:i18n key="LABEL_POSIZIONE_CLASSIFICA" /> : </label>
			<s:property value="#elencoClassifica[0].posizione" />&deg;  <%-- classificato --%>
		</div>
		
	</c:otherwise>						
</c:choose> 

