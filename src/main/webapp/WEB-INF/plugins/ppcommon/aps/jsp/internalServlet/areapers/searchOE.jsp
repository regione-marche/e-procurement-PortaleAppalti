<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags"%>


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp">
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2><wp:i18n key="TITLE_PAGE_SEARCHOE" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_AREA_PERSONALE_SEARCHOE" />
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/field_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_messages.jsp" />
	

	<form action="<wp:action path='/ExtStr2/do/FrontEnd/AreaPers/findSearchOE.action' />" method="post">

		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/mandatory_fields_message.jsp" />

		<fieldset>
			<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_SEARCH_CRITERIA" /></legend>

			<div class="fieldset-row">
				<div class="label">
					<label for="username"><wp:i18n key="USERNAME" />: </label>
				</div>
				<div class="element">
					<s:textfield name="username" id="username" cssClass="text" />
				</div>
			</div>	
			<div class="fieldset-row olddelegate">
				<div class="label">
					<label for="ragioneSociale"><wp:i18n key="LABEL_RAGIONE_SOCIALE" />: </label>
				</div>
				<div class="element">
					<s:textfield name="ragioneSociale" id="ragioneSociale" cssClass="text" />
				</div>
			</div>	
			<div class="fieldset-row newdelegate">
				<div class="label">
					<label for="email"><wp:i18n key="LABEL_EMAIL" />: </label>
				</div>
				<div class="element">
					<s:textfield name="email" id="email" cssClass="text" />
				</div>
			</div>	
			<div class="fieldset-row">
				<div class="label">
					<label for="attivo"><wp:i18n key="LABEL_ATTIVO" />: </label>
				</div>
				<div class="element">
					<s:select list="maps['sino']" 
						name="attivo" id="attivo" value="%{attivo}" headerKey="" headerValue="" />
				</div>
			</div>
			
			<div class="azioni">
				<wp:i18n key="BUTTON_SEARCH" var="valueButtonProcedi" />
				<s:submit value="%{#attr.valueButtonProcedi}" cssClass="button"/>
			</div>
		</fieldset>
	</form>
	
	<form action="<wp:action path='/ExtStr2/do/FrontEnd/AreaPers/createPdf.action' />" method="post">

		<!-- LISTA RISULTATI + FORM RIATTIVAZIONE -->
		<s:if test="%{listaOperatori.size > 0}">
			<div class="list-summary">
				<wp:i18n key="SEARCH_RESULTS_INTRO" /> <s:property value="%{listaOperatori.size}"/> <wp:i18n key="SEARCH_RESULTS_OUTRO" />.
			</div>
		
			<div class="table-container">
				<table id="tableOperatoriEconomiciKO" summary="<wp:i18n key="LABEL_OP_KO_TABELLA_SUMMARY" />" class="info-table">
					<thead>
						<tr>
							<th scope="col" />
							<th scope="col"><wp:i18n key="USERNAME" /></th>
							<th scope="col"><wp:i18n key="LABEL_RAGIONE_SOCIALE" /></th>
							<th scope="col"><wp:i18n key="LABEL_EMAIL" /></th>
							<th scope="col"><wp:i18n key="LABEL_ATTIVO" /></th>
						</tr>
					</thead>
					<tbody>
						<s:iterator var="riga" value="listaOperatori" status="status">
							<tr>
								<td>
									<input type="radio"
										name="username"
										id="username_${status.index}"
										value='<s:property value="username"/>'
										aria-label='<s:property value="username"/>' />
								</td>
								<td><s:property value="username"/></td>
								<td><s:property value="ragioneSociale"/></td>
								<td><s:property value="email" /></td>
								<td>
									<s:iterator value="maps['sino']">
										<s:if test="%{key == attivo}">
											<s:property value="%{value}"/>
										</s:if>
									</s:iterator>
								</td>
							</tr>
						</s:iterator>
					</tbody>
				</table>
			</div>

			<div class="azioni">
				<wp:i18n key="LABEL_DATI_OE_STAMPA_ANAGRAFICA" var="labelStampaAnagrafica" />
				<wp:i18n key='BUTTON_DATI_OE_STAMPA_ANAGRAFICA' var="btnStampa" />
				<div class="azione">
					<s:submit
						name="btnCreatePdf"
						value="%{#attr.btnStampa}"
						title="%{#attr.labelStampaAnagrafica}"
						cssClass="button" 
						method="createPdf" />
				</div>
			</div>
		</s:if>
		<s:else>
			<div class="list-summary">
				<wp:i18n key="SEARCH_NOTHING_FOUND" />.
			</div>
		</s:else>
	</form>

</div>