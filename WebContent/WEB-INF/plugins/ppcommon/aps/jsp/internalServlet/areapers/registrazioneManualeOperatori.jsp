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
		
		$('#tableRegistrazioni').dataTable({
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

	<h2><wp:i18n key="TITLE_PAGE_REG_MANUALE_OP" /></h2>
	
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_AREA_PERSONALE_REG_MANUALE_OP_ECON" />
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/field_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_messages.jsp" />
	
	<s:if test="%{rejectQuestion}">
	
		<%-- FORM DI RIFIUTO REGISTRAZIONE --%>
		<form action="<wp:action path="/ExtStr2/do/FrontEnd/AreaPers/rejectRegistrazioneOperatoreManuale.action" />" method="post">
			<fieldset>
				<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_REG_MANUALE_OP_INFO_MOTIVAZIONE" /></legend>
				<div class="fieldset-row">
					<div class="label">
						<label for="motivoRifiuto"><wp:i18n key="LABEL_MOTIVAZIONE_RIFIUTO" />:  <span class="required-field">*</span></label>
					</div>
					<div class="element">
						<s:textarea name="motivoRifiuto" id="motivoRifiuto" value="%{motivoRifiuto}" rows="5" cols="68" aria-required="true"/>
					</div>
				</div>
			</fieldset>
			<div class="azioni">
				<input type="hidden" name="rejectQuestion" value="${rejectQuestion}"/>
				<input type="hidden" name="idComunicazione" value="${idComunicazione}"/>
				<wp:i18n key="BUTTON_RIFIUTA" var="valueRejectButton" />
				<s:submit value="%{#attr.valueRejectButton}" title="%{#attr.valueRejectButton}" cssClass="button" method="rejectConfirm"></s:submit>
				<wp:i18n key="BUTTON_WIZARD_CANCEL" var="valueCancelButton" />
				<s:submit value="%{#attr.valueCancelButton}" title="%{#attr.valueCancelButton}" cssClass="button" method="rejectCancel"></s:submit>
			</div> 
		</form>
		
	</s:if>
	<s:else>
	
		<%-- LISTA DELLE REGISTRAZIONI IN ATTESA DI VALIDAZIONE --%>
		<s:if test="%{rielaborazioneOK}">
			<p><wp:i18n key="LABEL_REG_MANUALE_OP_COMUNICAZIONE" /> <s:property value="%{tipoComunicazione}"/> <wp:i18n key="LABEL_REG_MANUALE_OP_CON_ID" /> <s:property value="%{idComunicazione}"/> <wp:i18n key="LABEL_REG_MANUALE_OP_RIPRISTINATA" />.</p>
		</s:if>	
		
		<s:if test="%{listaRegistazioni.size > 0}">	
			<div class="list-summary">
				<wp:i18n key="SEARCH_RESULTS_INTRO" /> <s:property value="%{listaRegistazioni.size}"/> <wp:i18n key="SEARCH_RESULTS_OUTRO" />.
			</div>
		
			<form action="<wp:action path="/ExtStr2/do/FrontEnd/AreaPers/registrazioneOperatoreManuale.action" />" method="post">
				<div class="table-container">
					<table id="tableRegistrazioni" summary="<wp:i18n key="LABEL_REGISTRAZIONE_MANUALE_TABELLA_SUMMARY" />" class="info-table">
						<thead>
							<tr>
								<th scope="col"></th>
								<th scope="col"><wp:i18n key="LABEL_FLUSSI_KO_COMUNICAZIONE" /></th>
								<th scope="col"><wp:i18n key="LABEL_FLUSSI_KO_DATA_INSERIMENTO" /></th>
								<th scope="col"><wp:i18n key="USERNAME" /></th>
								<th scope="col"><wp:i18n key="LABEL_RAGIONE_SOCIALE" /></th>
							</tr>
						</thead>
						<tbody>
							<s:iterator var="riga" value="listaRegistazioni" status="status">
								<tr>									
									<td>																				
										<input type="radio" name="idComunicazione" id='idComunicazione_${status.index}' value='<s:property value="%{idComunicazione}"/>' 
												aria-label='<s:property value="idComunicazione"/>' />								
									</td>								
									<td><s:property value="idComunicazione"/></td>
									<td><s:date name="dataInserimento" format="dd/MM/yyyy hh:mm:ss"/></td>
									<td><s:property value="utente" /></td>
									<td><s:property value="mittente" /></td>
								</tr>
							</s:iterator>
						</tbody>
					</table>
				</div> 	
				<div class="azioni">
					<wp:i18n key="BUTTON_VISUALIZZA_DATI_REGISTRAZIONE" var="valueViewButton" />
					<wp:i18n key="BUTTON_APPROVA" var="valueAccectButton" />
					<wp:i18n key="BUTTON_RIFIUTA" var="valueRejectButton" />
					<s:submit value="%{#attr.valueViewButton}" title="%{#attr.valueViewButton}" cssClass="button" method="view"></s:submit>
					<s:submit value="%{#attr.valueAccectButton}" title="%{#attr.valueAccectButton}" cssClass="button" method="accept"></s:submit>
					<s:submit value="%{#attr.valueRejectButton}" title="%{#attr.valueRejectButton}" cssClass="button" method="reject"></s:submit>
				</div> 
			</form>
		</s:if>
		<s:else>
			<div class="list-summary">
				<wp:i18n key="SEARCH_NOTHING_FOUND" />
			</div>
		</s:else>
			
	</s:else>
	
</div>
