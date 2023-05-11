<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<%--
	inserire il parametro "elencoFasi" nella pagina chiamante come segue
	   
	<s:set var="elencoFasi" value="%{ [lista da passare questo iteratore] }" /> 
	<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/aste/inc/iteratorFasi.jsp" />		
--%>


<div class="table-container">	
	<table id="tableClassifica" summary='<wp:i18n key="LABEL_FASI_TABELLA_SUMMARY" />' class="info-table">
		<thead>
			<tr>
				<th scope="col"><wp:i18n key="LABEL_FASE" /></th>
				<th scope="col"><wp:i18n key="LABEL_DATA_ORA_APERTURA" /></th>
				<th scope="col"><wp:i18n key="LABEL_DURATA_MINIMA" /></th>
				<th scope="col"><wp:i18n key="LABEL_DURATA_MASSIMA" /></th>
				<th scope="col"><wp:i18n key="LABEL_TEMPO_BASE" /></th>
				<th scope="col"><wp:i18n key="LABEL_DATA_ORA_CHIUSURA" /></th>
			</tr>
		</thead>
		<tbody>
			<s:iterator var="item" value="elencoFasi" status="stat">
				<tr>
					<td align="center">
						<s:property value="#item.numeroFase" /> 
					</td>
					<td align="center">
						<s:date name="#item.dataOraApertura" format="dd/MM/yyyy HH:mm" />							
					</td>
					<td align="center">
						<s:property value="#item.durataMinima" /> 
					</td>
					<td align="center">
						<s:property value="#item.durataMassima" /> 
					</td>
					<td align="center">
						<s:property value="#item.tempoBase" />
					</td>
					<td align="center">
						<s:date name="#item.dataOraChiusura" format="dd/MM/yyyy HH:mm" /> 
					</td>
				</tr>
			</s:iterator>
		</tbody>
	</table>
</div>
