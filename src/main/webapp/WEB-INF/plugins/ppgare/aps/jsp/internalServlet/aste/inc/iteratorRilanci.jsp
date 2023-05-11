<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<%--
	inserire il parametro "elencoRilanci" nella pagina chiamante come segue
	   
	<s:set var="elencoRilanci" value="%{ [lista da passare questo iteratore] }" /> 
	<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/aste/inc/iteratorRilanci.jsp" />		
--%>

<c:set var="tipoOfferta" value="${param.tipoOfferta}" />



<div class="table-container">
	<table id="tableRilanci" summary='<wp:i18n key="LABEL_RILANCI_TABELLA_SUMMARY" />' class="info-table">
		<thead>
			<tr>
				<th scope="col"><wp:i18n key="LABEL_DATA_ORA_RILANCIO" /></th>
				<th scope="col">
					<c:choose>
						<c:when test="${tipoOfferta == 1}">
							<wp:i18n key="LABEL_RIBASSO_PERCENTUALE" />
						</c:when>
						<c:otherwise>
							<wp:i18n key="LABEL_IMPORTO_OFFERTO" />
						</c:otherwise>
					</c:choose>
				</th>
			</tr>
		</thead>
		<tbody>
			<s:iterator var="riga" value="elencoRilanci" status="status">
				<tr>
					<td class="date-content">
						<s:if test="#riga.numeroRilancio < 0" >
							<wp:i18n key="LABEL_OFFERTA_INIZIALE" />
						</s:if>
						<s:else>
							<s:date name="#riga.dataRilancio" format="dd/MM/yyyy HH:mm:ss" />
						</s:else>
					</td>
					
					<td class="money-content">
						<c:choose>
							<c:when test="${tipoOfferta == 1}">
								<s:text name="format.money5dec"><s:param value="#riga.ribasso"/></s:text>&nbsp;%
							</c:when>
							<c:otherwise>
								<s:text name="format.money5dec"><s:param value="#riga.importo"/></s:text>&nbsp;&euro;
							</c:otherwise>
						</c:choose>
					</td>
				</tr>
			</s:iterator>
		</tbody>
	</table>
</div>
