<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags"%>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>

<script type="text/javascript" src='<wp:resourceURL/>static/js/jquery.dataTables.min.js'></script>
<script type="text/javascript">

	$(document).ready(function() {
	
		$.extend($.fn.dataTable.defaults, {
			"paging": false,
		    "ordering": false,
		    "info": false,
		    "searching" : false
		});
		
		$('#tableOperatoriEconomiciKO').dataTable({
			scrollX: true,
			scrollY: "25em",
			scrollCollapse: true
		});
	});
</script>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp">
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2><wp:i18n key="TITLE_PAGE_OP_KO" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_AREA_PERSONALE_ADMIN_OP_ECON" />
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/field_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_messages.jsp" />
	
	<form action="<wp:action path="/ExtStr2/do/FrontEnd/AreaPers/searchOperatoriEconomiciKO.action" />" method="post">
		<!-- FILTRI -->
		<fieldset>
			<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_SEARCH_CRITERIA" /></legend>

			<div class="fieldset-row first-row">
				<div class="label">
					<label for="model.utente"><wp:i18n key="USERNAME" />: </label>
				</div>
				<div class="element">
					<s:textfield name="model.utente" id="model.utente" cssClass="text" value="%{#session.formSearchOpEconomiciKO.utente}" />
				</div>
			</div>	
			<div class="fieldset-row">
				<div class="label">
					<label for="model.email"><wp:i18n key="jpuserreg_EMAIL" /> : </label>
				</div>
				<div class="element">
					<s:textfield name="model.email" id="model.email" value="%{#session.formSearchOpEconomiciKO.email}" />
				</div>
			</div>
			<div class="fieldset-row last-row">
				<div class="label">
					<label><wp:i18n key="LABEL_OP_KO_DATA_REGISTRAZIONE" />: </label>
				</div>
				<div class="element">
					<label for="model.dataRegistrazioneDa"><wp:i18n key="LABEL_DA_DATA" />: </label>
					<s:textfield name="model.dataRegistrazioneDa" id="model.dataRegistrazioneDa" cssClass="text" value="%{#session.formSearchOpEconomiciKO.dataRegistrazioneDa}" maxlength="10" size="10" />
					<label for="model.dataRegistrazioneA"><wp:i18n key="LABEL_A_DATA" />: </label>
					<s:textfield name="model.dataRegistrazioneA" id="model.dataRegistrazioneA" cssClass="text" value="%{#session.formSearchOpEconomiciKO.dataRegistrazioneA}" maxlength="10" size="10" />
					(<wp:i18n key="LABEL_FORMATO_DATA" />)
				</div>
			</div>
			<div class="azioni">
				<wp:i18n key="BUTTON_SEARCH" var="valueSearchButton" />
				<s:submit value="%{#attr.valueSearchButton}" cssClass="button"/>
			</div>
		</fieldset>
	</form>
	
	<!-- LISTA RISULTATI + FORM RIATTIVAZIONE -->
	<s:if test="%{listaOperatoriKO.size > 0}">	
		<div class="list-summary">
			<wp:i18n key="SEARCH_RESULTS_INTRO" /> <s:property value="%{listaOperatoriKO.size}"/> <wp:i18n key="SEARCH_RESULTS_OUTRO" />.
		</div>
	
		<form action="<wp:action path="/ExtStr2/do/FrontEnd/AreaPers/openAttivaOperatoreEconomico.action" />&amp;last=1" method="post">
			<div class="table-container">
					<table id="tableOperatoriEconomiciKO" summary="<wp:i18n key="LABEL_OP_KO_TABELLA_SUMMARY" />" class="info-table">
						<thead>
							<tr>
								<th scope="col"></th>
								<th scope="col"><wp:i18n key="USERNAME" /></th>
								<th scope="col"><wp:i18n key="jpuserreg_EMAIL" /></th>
								<th scope="col"><wp:i18n key="LABEL_OP_KO_DATA_REGISTRAZIONE" /></th>
							</tr>
						</thead>
						<tbody>
							<s:iterator var="riga" value="listaOperatoriKO" status="status">
								<tr>
									<td>
										<input type="radio" name="username"
												id='operatoreSelezionato<s:property value="%{#status.index}"/>' value='<s:property value="%{utente}"/>'
												aria-label='<s:property value="utente"/>' />
										<%-- <s:hidden name="utenteSelezionato" id="utenteSelezionato" value="%{utente}"/> --%>
									</td>
									<td><s:property value="utente"/></td>
									<td><s:property value="email" /></td>
									<td>
										<s:date name="dataRegistrazione" format="dd/MM/yyyy" />
									</td>
								</tr>
							</s:iterator>
						</tbody>
					</table>
				</div> 	
				<div class="azioni">
					<wp:i18n key="BUTTON_OP_KO_REINVIA" var="valueSubmitButton" />
					<s:submit value="%{#attr.valueSubmitButton}" cssClass="button"/>
				</div> 
		</form>
	</s:if>
	<s:else>
		<div class="list-summary">
			<wp:i18n key="SEARCH_NOTHING_FOUND" />.
		</div>
	</s:else>
	
</div>