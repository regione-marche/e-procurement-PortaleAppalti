<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags"%>


<%-- ********************************************************************** --%>
<%--  script di gestione della pagina                                       --%>
<%-- ********************************************************************** --%>
<script type="text/javascript">
$(document).ready(function() {
	// se js abilitato rimuovo l'avviso che per essere usabile la pagina
	// serve js abilitato
	$('#noJs').remove();
	
	refreshForm($('[name="tipoOperazione"]:checked').val());
	
	// gestisci il radio button
	$('[name="tipoOperazione"]').on("click", function(){
		refreshForm($('[name="tipoOperazione"]:checked').val());
	});
	
	function refreshForm(operazione) {
		if(operazione == null) {
			operazione = "1";
		}
		//[name*=...] "name" che contiene la stringa "..."
		$('[name*="newDelegateUser"]').hide();
		$('[name*="editDelegateUser"]').hide();
		$('[name*="removeDelegateUser"]').hide();
		$('.olddelegate').hide();
		$('.newdelegate').hide();
		$('input:radio[name="tipoOperazione"]').filter('[value="'+ operazione +'"]').attr('checked', true);
		if(operazione == "1") {	
			$('[name*="newDelegateUser"]').show();
			$('.newdelegate').show();
		} else if(operazione == "2") {
			$('[name*="editDelegateUser"]').show();	
			$('.olddelegate').show();
			$('.newdelegate').show();
		} else if(operazione == "3") {	
			$('[name*="removeDelegateUser"]').show();
			$('.olddelegate').show();
		}
	}

});	
</script>


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp">
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2><wp:i18n key="TITLE_PAGE_DELEGATEUSER" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_AREA_PERSONALE_DELEGATEUSER" />
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/field_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_messages.jsp" />
	
	
	<form action="<wp:action path="/ExtStr2/do/FrontEnd/AreaPers/delegateUser.action" />" method="post">

		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/mandatory_fields_message.jsp" />

		<fieldset>
			<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_DELEGATEUSER" /></legend>

			<div class="fieldset-row first-row">
				<br/>
				<input type="radio" value="1" name="tipoOperazione" <c:if test="${tipoOperazione eq '1'}">checked="checked"</c:if> aria-label='<wp:i18n key="BUTTON_INSERT_DELEGATEUSER" />' /> <wp:i18n key="BUTTON_INSERT_DELEGATEUSER" />
				&nbsp;&nbsp;
				<input type="radio" value="2" name="tipoOperazione" <c:if test="${tipoOperazione eq '2'}">checked="checked"</c:if> aria-label='<wp:i18n key="BUTTON_UPDATE_DELEGATEUSER" />' /> <wp:i18n key="BUTTON_UPDATE_DELEGATEUSER" />
				&nbsp;&nbsp;
				<input type="radio" value="3" name="tipoOperazione" <c:if test="${tipoOperazione eq '3'}">checked="checked"</c:if> aria-label='<wp:i18n key="BUTTON_RESET_DELEGATEUSER" />' /> <wp:i18n key="BUTTON_RESET_DELEGATEUSER" />
				<br/>
			</div>
			<div class="fieldset-row">
				<div class="label">
					<label for="username"><wp:i18n key="USERNAME" />: <span class="required-field">*</span></label>
				</div>
				<div class="element">
					<s:textfield name="username" id="username" cssClass="text" aria-required="true" />
				</div>
			</div>	
			<div class="fieldset-row olddelegate">
				<div class="label">
					<label for="olddelegateuser"><wp:i18n key="OLDDELEGATEUSER" />: <span class="required-field">*</span></label>
				</div>
				<div class="element">
					<s:textfield name="olddelegateuser" id="olddelegateuser" cssClass="text" aria-required="true" />
				</div>
			</div>	
			<div class="fieldset-row newdelegate">
				<div class="label">
					<label for="newdelegateuser"><wp:i18n key="NEWDELEGATEUSER" />: <span class="required-field">*</span></label>
				</div>
				<div class="element">
					<s:textfield name="newdelegateuser" id="newdelegateuser" cssClass="text" aria-required="true" />
				</div>
			</div>	
			<div class="azioni">
				<wp:i18n key="BUTTON_INSERT_DELEGATEUSER" var="valueButtonInsert" />
				<wp:i18n key="BUTTON_UPDATE_DELEGATEUSER" var="valueButtonUpdate" />
				<wp:i18n key="BUTTON_RESET_DELEGATEUSER" var="valueButtonReset" />
				<s:submit value="%{#attr.valueButtonInsert}" method="newDelegateUser" cssClass="button"/>
				<s:submit value="%{#attr.valueButtonUpdate}" method="editDelegateUser" cssClass="button"/>
				<s:submit value="%{#attr.valueButtonReset}" method="removeDelegateUser" cssClass="button"/>
			</div>
		</fieldset>
	</form>
	
</div>


