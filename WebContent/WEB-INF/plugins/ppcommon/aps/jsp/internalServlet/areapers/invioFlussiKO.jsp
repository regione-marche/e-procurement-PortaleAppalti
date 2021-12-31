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
		
		$('#tableFlussiKO').dataTable({
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

	<h2><wp:i18n key="TITLE_PAGE_FLUSSI_KO" /></h2>
	
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_AREA_PERSONALE_ADMIN_FLUSSI" />
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_messages.jsp" />
	
	<s:if test="%{rielaborazioneOK}">
		<p><wp:i18n key="LABEL_FLUSSI_KO_COMUNICAZIONE" /> <s:property value="%{tipoComunicazione}"/> <wp:i18n key="LABEL_FLUSSI_KO_CON_ID" /> <s:property value="%{idComunicazione}"/> <wp:i18n key="LABEL_FLUSSI_KO_RIPRISTINATA" />.</p>
	</s:if>
	
	<form action="<wp:action path="/ExtStr2/do/FrontEnd/AreaPers/searchInvioFlussiKO.action" />" method="post">
		<!-- FILTRI -->
		<fieldset>
			<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_SEARCH_CRITERIA" /></legend>

			<div class="fieldset-row first-row">
				<div class="label">
					<label for="model.utente"><wp:i18n key="USERNAME" />: </label>
				</div>
				<div class="element">
					<s:textfield name="model.utente" id="model.utente" cssClass="text" value="%{#session.formSearchFlussiKO.utente}" 
											maxlength="20"/>
				</div>
			</div>	
			<div class="fieldset-row">
				<div class="label">
					<label for="model.mittente"><wp:i18n key="LABEL_RAGIONE_SOCIALE" />: </label>
				</div>
				<div class="element">
					<s:textfield name="model.mittente" id="model.mittente" cssClass="text" value="%{#session.formSearchFlussiKO.mittente}" 
											maxlength="160"/>
				</div>
			</div>	
			<div class="fieldset-row">
				<div class="label">
					<label for="model.tipoComunicazione"><wp:i18n key="LABEL_FLUSSI_KO_TIPO_COMUNICAZIONE" /> : </label>
				</div>
				<div class="element">
					<s:textfield name="model.tipoComunicazione" id="model.tipoComunicazione" value="%{#session.formSearchFlussiKO.tipoComunicazione}" 
											maxlength="5"/>
				</div>
			</div>
			<div class="fieldset-row last-row">
				<div class="label">
					<label for="model.riferimentoProcedura"><wp:i18n key="LABEL_RIFERIMENTO_PROCEDURA" /> : </label>
				</div>
				<div class="element">
					<s:textfield name="model.riferimentoProcedura" id="model.riferimentoProcedura" value="%{#session.formSearchFlussiKO.riferimentoProcedura}" maxlength="20"/>
				</div>
			</div>
			
			<div class="azioni">
				<wp:i18n key="BUTTON_SEARCH" var="valueSearchButton" />
				<s:submit value="%{#attr.valueSearchButton}" cssClass="button"/>
			</div>
		</fieldset>
	</form>
	
	<!-- LISTA RISULTATI + FORM RIELABORAZIONE RICHIESTA -->
	<s:if test="%{listaFlussiKO.size > 0}">	
		<div class="list-summary">
			<wp:i18n key="SEARCH_RESULTS_INTRO" /> <s:property value="%{listaFlussiKO.size}"/> <wp:i18n key="SEARCH_RESULTS_OUTRO" />.
		</div>
	
		<form action="<wp:action path="/ExtStr2/do/FrontEnd/AreaPers/rielaboraFlusso.action&amp;last=1" />" method="post">
			<div class="table-container">
					<table id="tableFlussiKO" summary="<wp:i18n key="LABEL_FLUSSI_KO_TABELLA_SUMMARY" />" class="info-table">
						<thead>
							<tr>
								<th scope="col"></th>
								<th scope="col"><wp:i18n key="LABEL_FLUSSI_KO_COMUNICAZIONE" /></th>
								<th scope="col"><wp:i18n key="LABEL_FLUSSI_KO_DATA_INSERIMENTO" /></th>
								<th scope="col"><wp:i18n key="USERNAME" /></th>
								<th scope="col"><wp:i18n key="LABEL_RAGIONE_SOCIALE" /></th>
								<th scope="col"><wp:i18n key="LABEL_FLUSSI_KO_TIPO_COMUNICAZIONE" /></th>
								<th scope="col"><wp:i18n key="LABEL_RIFERIMENTO_PROCEDURA" /></th>
							</tr>
						</thead>
						<tbody>
							<s:iterator var="riga" value="listaFlussiKO" status="status">
								<tr>									
									<td>																				
										<input type="radio" name="idComunicazione" id='idComunicazione_${status.index}' value='<s:property value="%{idComunicazione}"/>' 
												aria-label='<s:property value="idComunicazione"/>' />								
									</td>								
									<td><s:property value="idComunicazione"/></td>
									<td>
										<s:date name="dataInserimento"  format="dd/MM/yyyy hh:mm:ss"/>
									</td>
									<td><s:property value="utente" /></td>
									<td><s:property value="mittente" /></td>
									<td><s:property value="tipoComunicazione" /></td>
									<td><s:property value="riferimentoProcedura" /></td>
								</tr>
							</s:iterator>
						</tbody>
					</table>
				</div> 	
				<div class="azioni">
					<wp:i18n key="BUTTON_FLUSSI_KO_RIELABORA" var="valueSubmitButton" />
					<s:submit value="%{#attr.valueSubmitButton}" cssClass="button"/>
				</div> 
		</form>
	</s:if>
	<s:else>
		<div class="list-summary">
			<wp:i18n key="SEARCH_NOTHING_FOUND" />
		</div>
	</s:else>
	
	
</div>