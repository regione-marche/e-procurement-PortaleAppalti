<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="es"   uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>

<es:checkCustomization var="withAdvancedUI" objectId="UI-ADVANCED" attribute="DATATABLE" feature="ACT" />

<c:if test="${withAdvancedUI}" >
	<%-- ******************************************************************************** --%>
	<script>
	<!--//--><![CDATA[//><!--
		
		
	// apertura della pagina...
	$(document).ready(function() {
		$.extend($.fn.dataTable.defaults, {
			"paging": false,
			"ordering": false,
			"info": false,
			"searching" : false
		});
		$('#tableViewFatture').DataTable();
	});	
	//--><!]]>
	</script>
	<%-- ******************************************************************************** --%>
</c:if>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp">
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2><wp:i18n key="TITLE_PAGE_EORDERS_LISTA_FATTURE" /> <s:property value="orderCode"/></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_EORDERS_LISTA_FATTURE" />
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />

	<div class="detail-section first-detail-section">
		<h3 class="detail-section-title">
			<span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_SDI_FATTURE_INVIATE" />
		</h3>

		<%-- <div>
		STRUTS
			model: <s:property value="model" />
			<br>model.empty: <s:property value="model.empty" />
			<br>model.numberOfElements: <s:property value="model.numberOfElements" />
		</div>
		<div>
		SPRING
			<br><c:out value="${model}"></c:out>
			<br><c:out value="${model.numberOfElements}"></c:out>
		</div> --%>
		<s:if test="model.numberOfElements != 0">
			<table id="tableViewFatture">
				<thead>
					<tr>
						<th><wp:i18n	key="LABEL_FATT_PROGINVIO" /></th>	
						<th><wp:i18n	key="LABEL_FATT_DATAINVIO" /></th>	
						<th><wp:i18n	key="LABEL_FATT_DATARICSDI" /></th>	
						<th><wp:i18n	key="LABEL_FATT_STATO" /></th>	
						<th><wp:i18n	key="LABEL_FATT_NOMEFILE" /></th>	
					</tr>
				</thead>
				<tbody>
					<s:iterator var="fattura" value="model.content" status="status">
						<tr>
							<td><s:property value="progInvio"/></td>
							<td>
								<s:if test="datainvio">
									<s:text name="format.date.withtime"><s:param value="datainvio"/></s:text>
								</s:if>
							</td>
							<td>
								<s:if test="dataricezionesdi">
									<s:text name="format.date.withtime"><s:param value="dataricezionesdi"/></s:text>
								</s:if>
							</td>
							<td><s:property value="stato"/></td>
							<td><s:property value="nomeFile"/></td>
						</tr>
					</s:iterator>
				</tbody>
				<tfoot>
					<tr>
						<th><wp:i18n	key="LABEL_FATT_PROGINVIO" /></th>	
						<th><wp:i18n	key="LABEL_FATT_DATAINVIO" /></th>	
						<th><wp:i18n	key="LABEL_FATT_DATARICSDI" /></th>	
						<th><wp:i18n	key="LABEL_FATT_STATO" /></th>	
						<th><wp:i18n	key="LABEL_FATT_NOMEFILE" /></th>
					</tr>
				</tfoot>	
			</table>
		</s:if>

	</div>
	<div class="list-summary">
		<wp:i18n key="SEARCH_RESULTS_INTRO" />
		<s:property value="model.totalElements" />
		<wp:i18n key="SEARCH_RESULTS_OUTRO" />.
	</div>
		
	<%-- <form action="<wp:action path="/ExtStr2/do/FrontEnd/EOrders/archivioFatture.action" />" method="post" >
		<div class="azioni">
			<input type="hidden" name="id" value="${id}" />
			<input type="hidden" name="orderCode" value="${orderCode}" />
			<input type="hidden" name="progInvio" value="${progInvio}" />
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			<s:submit value="RICARICA" cssClass="button"></s:submit>
		</div>
	</form> --%>
	<form action="<wp:action path="/ExtStr2/do/FrontEnd/EOrders/archivioFatture.action" />" method="post" >
		<div class="azioni">
			<input type="hidden" name="id" value="${id}" />
			<input type="hidden" name="orderCode" value="${orderCode}" />
			<input type="hidden" name="progInvio" value="${progInvio}" />
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_cancel.jsp" />
		</div>
	</form>
</div>