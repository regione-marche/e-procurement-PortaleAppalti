<%@ taglib prefix="wp"   uri="aps-core.tld" %>
<%@ taglib prefix="c"    uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wpsa" uri="apsadmin-core.tld"  %>
<%@ taglib prefix="s"    uri="/struts-tags" %>
<%@ taglib prefix="es" 	 uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>

<script src='<wp:resourceURL/>static/js/jquery.dataTables.min.js'></script>
<es:checkCustomization var="withAdvancedUI" objectId="UI-ADVANCED" attribute="DATATABLE" feature="ACT" />

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-list">

	<h2>${sessionScope.titoloReport}</h2>

	<div class="list-summary">
		<wp:i18n key="SEARCH_RESULTS_INTRO" /> <s:property value="datiRisultato.totRecord"/> <wp:i18n key="SEARCH_RESULTS_OUTRO" />.
	</div>
 
	<s:if test="%{type == 'Griglia'}">

<c:if test="${withAdvancedUI}" >
	<script>
	
		$(document).ready(function() {
		
			$.extend($.fn.dataTable.defaults, {
				"paging": false,
			    "ordering": false,
			    "info": false,
			    "searching" : false
			});
			
			$('#tableReport').dataTable({
				scrollX: true,
				scrollY: "25em",
				scrollCollapse: true
			});
		});
	</script>
</c:if>
	<div class="table-container">
		<table id="tableReport" summary="Tabella report" class="info-table" >
			<thead>
				<tr>
					<s:iterator var="def" value="datiRisultato.defColonnaRisultatoArray">
						<th scope="col"><s:property value="#def.titolo" /></th>
					</s:iterator>
				</tr>
			</thead>
			<tbody>
				<s:iterator var="record" value="datiRisultato.recordArray">
					<tr>
						<s:iterator var="campo" value="#record.campoArray" status="stat">
							<td <s:if test="%{#stat.first}">scope="row"</s:if>><s:property value="#campo"/></td>
						</s:iterator>
					</tr>
				</s:iterator>
			</tbody>
		</table>
	</div>
	</s:if>
	<s:else>
		<s:iterator var="record" value="datiRisultato.recordArray">
			<ul class="list">
				<s:iterator var="campo" value="#record.campoArray" status="stat">
					<li class='<s:if test="%{#stat.first}">first</s:if> <s:if test="%{#stat.last}">last</s:if>'>
						<label><s:property value="datiRisultato.defColonnaRisultatoArray[#stat.index].titolo"/> : </label>
						<s:property value="#campo"/>
					</li>
				</s:iterator>
			</ul>
		</s:iterator>
	</s:else>
</div>