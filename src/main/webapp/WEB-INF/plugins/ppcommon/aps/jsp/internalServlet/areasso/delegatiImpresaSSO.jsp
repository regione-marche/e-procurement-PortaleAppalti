<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags"%>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="hrefAddSSO" value="/ExtStr2/do/FrontEnd/AreaPers/addDelegatoSSO.action"/>
<c:set var="hrefModifySSO" value="/ExtStr2/do/FrontEnd/AreaPers/modifyDelegatoSSO.action"/>
<c:set var="hrefSaveSSO" value="/ExtStr2/do/FrontEnd/AreaPers/saveDelegatoSSO.action"/>
<c:set var="hrefDeleteSSO" value="/ExtStr2/do/FrontEnd/AreaPers/confirmDeleteDelegatoSSO.action"/>

<%-- per i tipi di profilo utente, mappa le label e i valori associati alle label --%>
<wp:i18n var="lblReadonly" key="LABEL_ACCESSO_SOLA_LETTURA"/>
<wp:i18n var="lblEdit" key="LABEL_ACCESSO_COMPILAZIONE"/>
<wp:i18n var="lblEditSend" key="LABEL_ACCESSO_CONTROLLO_COMPLETO"/>
<s:set name="valueReadonly"><%= com.agiletec.aps.system.services.user.DelegateUser.Accesso.READONLY.toString() %></s:set>
<s:set name="valueEdit"><%= com.agiletec.aps.system.services.user.DelegateUser.Accesso.EDIT.toString() %></s:set>
<s:set name="valueEditSend"><%= com.agiletec.aps.system.services.user.DelegateUser.Accesso.EDIT_SEND.toString() %></s:set>


 
<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp">
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>  
 
<div class="portgare-view">

	<h2><wp:i18n key="TITLE_PAGE_AREA_PERSONALE_SOGGETTI_ABILITATI" /></h2>
	
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_AREA_PERSONALE_SOGGETTI_ABILITATI" />
	</jsp:include>
	
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/field_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_messages.jsp" />
	
	<c:if test="${sessionScope.accountSSO != null}">
	</c:if>
	
	<s:if test="%{questionConfirmDelete}">
		<%-- richiesta cancellazione soggetto impresa --%>
		
		<p class="question">
			<wp:i18n key="LABEL_ELIMINA_SOGGETTO_IMPRESA" /> "<s:property value='%{delegatiImpresa.get(idDelete).delegate}'/>" (<s:property value='%{delegatiImpresa.get(idDelete).description}'/>)?
		</p>
		<div class="azioni">
			<form action="<wp:action path="/ExtStr2/do/FrontEnd/AreaPers/deleteDelegatoSSO.action" />" method="post" class="azione">
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
	
				<input type="hidden" name="ext" value="${param.ext}" />
				<s:hidden name="id" id="id" value="%{idDelete}" />
				<s:hidden name="idUtente" id="idUtente" value="%{delegatiImpresa.get(idDelete).delegate}" />

				<wp:i18n key="LABEL_YES" var="valueButtonYes" />
				<wp:i18n key="TITLE_CONFIRM_DEL_YES" var="titleButtonYes" />
				<s:submit value="%{#attr.valueButtonYes}" title="%{#attr.titleButtonYes}" cssClass="button"></s:submit>
			</form>
			<form action="<wp:action path="/ExtStr2/do/FrontEnd/AreaPers/cancelDeleteDelegatoSSO.action" />" method="post" class="azione">
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
	
				<input type="hidden" name="ext" value="${param.ext}" />
				<s:hidden name="id" id="id" value="%{idDelete}" />
				<s:hidden name="idUtente" id="idUtente" value="%{#delegatiImpresa.get(idDelete).delegate}" />
				
				<wp:i18n key="LABEL_NO" var="valueButtonNo" />
				<wp:i18n key="TITLE_CONFIRM_DEL_NO" var="titleButtonNo" />
				<s:submit value="%{#attr.valueButtonNo}" title="%{#attr.titleButtonNo}" cssClass="button"></s:submit>
			</form>
		</div>
		
	</s:if>
	<s:else>
		<%-- lista soggetti impresa --%>
	
		<fieldset>
			<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_SOGGETTI_ABILITATI" /></legend>
	
			<div class="table-container">
				<table class="wizard-table">
					<%-- <caption style="display:none;"><wp:i18n key="LABEL_SBLOCCA_ACCOUNT" /></caption> --%>
					<tr>
						<th scope="col"><wp:i18n key="LABEL_ID_UTENTE"/></th>
						<th scope="col"><wp:i18n key="LABEL_COGNOME_NOME"/></th>
						<th scope="col"><wp:i18n key="LABEL_PROFILO"/></th>
						<th scope="col"><wp:i18n key="LABEL_EMAIL"/></th>
						<th scope="col"><wp:i18n key="ACTIONS"/></th>
					</tr>
					<s:iterator value="delegatiImpresa" var="item" status="stat">
						<tr>
							<td>
								<s:property value="%{#item.delegate}"/>
							</td>
							<td>
								<s:property value="%{#item.description}"/>
							</td>
							<td>
								<s:if test="%{accesso[1] == #item.rolename}">${attr.lblReadonly}</s:if>
								<s:if test="%{accesso[2] == #item.rolename}">${attr.lblEdit}</s:if>
								<s:if test="%{accesso[3] == #item.rolename}">${attr.lblEditSend}</s:if>
							</td>
							<td>
								<s:property value="%{#item.email}"/>
							</td>
							<td class="azioni">
								<ul>
									<li>
										<c:set var="hrefModify">
											<wp:action path='${hrefModifySSO}'/>&amp;id=<s:property value="%{#stat.index}"/>&amp;idUtente=<s:property value="%{#item.delegate}"/>
										</c:set>
										<a href="${hrefModify}" title='<wp:i18n key="LABEL_MODIFICA_SOGGETTO_SSO"/>' class='bkg modify'>
										<c:choose>
											<c:when test="${skin == 'highcontrast' || skin == 'text'}">
													<wp:i18n key="LABEL_MODIFICA_SOGGETTO_SSO"/>
											</c:when>
											<c:otherwise>
													
											</c:otherwise>
										</c:choose>
										</a>
									</li>
									<li>
										<c:set var="hrefDelete">
											<wp:action path='${hrefDeleteSSO}'/>&amp;id=<s:property value="%{#stat.index}"/>&amp;idUtente=<s:property value="%{#item.delegate}"/>
										</c:set>
										<a href="${hrefDelete}" title='<wp:i18n key="LABEL_ELIMINA_SOGGETTO_SSO"/>' class='bkg delete'>
										<c:choose>
											<c:when test="${skin == 'highcontrast' || skin == 'text'}">
													<wp:i18n key="LABEL_ELIMINA_SOGGETTO_SSO"/>
											</c:when>
											<c:otherwise>
													
											</c:otherwise>
										</c:choose>
										</a>
									</li>
								</ul>
							</td>
						</tr>
					</s:iterator>
				</table>
			</div>
		</fieldset>
		
		<form action="<wp:action path="/ExtStr2/do/FrontEnd/AreaPers/delegatiImpresaSSO.action" />" method="post" >
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			
			<fieldset>
				<div class="fieldset-row first-row">
					<div class="label">
						<label for="idUtente"><wp:i18n key="LABEL_ID_UTENTE" /> : <span class="required-field">*</span></label>
					</div>
					<div class="element">
						<c:choose>
							<c:when test="${id != null && id >= 0}">
								<s:property value="%{idUtente}"/>
								<input type="hidden" name="idUtente" id="idUtente" value="<s:property value='%{idUtente}'/>"/>
							</c:when>
							<c:otherwise>
								<s:textfield name="idUtente" id="idUtente" value="%{idUtente}" size="40" maxlength="40" aria-required="true"/>
							</c:otherwise>
						</c:choose>
					</div>
				</div>
				
				<div class="fieldset-row">
					<div class="label">
						<label for="cognomeNome"><wp:i18n key="LABEL_COGNOME_NOME" /> : <span class="required-field">*</span></label>
					</div>
					<div class="element">
						<s:textfield name="cognomeNome" id="cognomeNome" value="%{cognomeNome}" size="70" maxlength="70" aria-required="true"/>
					</div>
				</div>
			
				<div class="fieldset-row">
					<div class="label">
						<label for="profilo"><wp:i18n key="LABEL_PROFILO" /> : <span class="required-field">*</span></label>
					</div>
					<div class="element">
						<select name="profilo" id="profilo" class="text">
							<option <c:if test="${profilo==valueReadonly}">selected="selected"</c:if> value="${valueReadonly}">${attr.lblReadonly}</option>
							<option <c:if test="${profilo==valueEdit}">selected="selected"</c:if> value="${valueEdit}">${attr.lblEdit}</option>
							<option <c:if test="${profilo==valueEditSend}">selected="selected"</c:if> value="${valueEditSend}">${attr.lblEditSend}</option>
						</select>
					</div>
				</div>
				
				<div class="fieldset-row">
					<div class="label">
						<label for="email"><wp:i18n key="LABEL_EMAIL" /> : </label>
					</div>
					<div class="element">
						<s:textfield name="email" id="email" value="%{email}" size="70" maxlength="70"/>
					</div>
				</div>
				
				<div class="azioni">
					<input type="hidden" name="id" id="id" value="${id}"/>
					<c:choose>
						<c:when test="${(id == null) || (id != null && id < 0)}">
							<wp:i18n key="BUTTON_ADD" var="lblInsert" />
							<s:submit value="%{#attr.lblInsert}" title="%{#attr.lblInsert}" cssClass="button" method="addSoggetto"></s:submit>
						</c:when>
						<c:otherwise>
							<c:if test="${true}">
								<wp:i18n key="BUTTON_REFRESH" var="lblRefresh" />
								<s:submit value="%{#attr.lblRefresh}" title="%{#attr.lblRefresh}" cssClass="button" method="saveSoggetto"></s:submit>
							</c:if>
							<wp:i18n key="BUTTON_NEW" var="lblNuovo" />
							<s:submit value="%{#attr.lblNuovo}" title="%{#attr.lblNuovo}" cssClass="button" method="clearSoggetto"></s:submit>
						</c:otherwise>
					</c:choose>
				</div>
			</fieldset>
		</form>
		
	</s:else>
</div>