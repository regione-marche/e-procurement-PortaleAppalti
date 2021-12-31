<%@ taglib prefix="wp" uri="aps-core.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<s:set var="helper" value="%{#session['nuovaComunicazione']}"/>

<s:if test="%{#helper.modello > 0}" >
	<c:set var="title"><wp:i18n key="TITLE_SOCCORSO_ISTRUTTORIO_NUOVO" /></c:set>
	<!-- <c:set var="balloon" value="BALLOON_WIZ_SOCCORSO_TESTO" /> -->
	<c:set var="balloon" value="BALLOON_WIZ_COMUNICAZIONE_TESTO" />
	<c:set var="hrefAzioni"><wp:action path="/ExtStr2/do/FrontEnd/Comunicazioni/processPageNuovoSoccorso.action" /></c:set>	
</s:if>
<s:else>
	<c:set var="title"><wp:i18n key="TITLE_COMUNICAZIONI_NUOVA" /></c:set>
	<c:set var="balloon" value="BALLOON_WIZ_COMUNICAZIONE_TESTO" />
	<c:set var="hrefAzioni"><wp:action path="/ExtStr2/do/FrontEnd/Comunicazioni/processPageNuovaComunicazione.action" /></c:set>
</s:else>



<script type="text/javascript">
$(document).ready(function() {
	// se js abilitato rimuovo l'avviso che per essere usabile la pagina
	// serve js abilitato
	$('#noJs').remove();
});
</script>


<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/send_blockUI.jsp" />

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp">
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2>${title}</h2>
	
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/field_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
	
	<jsp:include page="stepsComunicazione.jsp" />
	
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="${balloon}"/>
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/skip_form_buttons.jsp" />

	<form action="${hrefAzioni}" method="post" class="azione">
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
		
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/mandatory_fields_message.jsp" />

 		<s:hidden name="from" id="from" value="%{from}"/>
		<s:hidden name="id" id="id" value="%{id}"/> 
		<fieldset>
			<legend>
				<span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_COMUNICAZIONI_TESTO" />
			</legend>
			<div class="fieldset-row first-row">
				<div class="label">
					<label><wp:i18n key="LABEL_COMUNICAZIONI_MITTENTE" /> : </label>
				</div>
				<div class="element">
					<s:property value="%{#helper.mittente}" />
				</div>
			</div>
			<div class="fieldset-row ">
				<div class="label">
					<label for="oggetto"><wp:i18n key="LABEL_COMUNICAZIONI_OGGETTO" /> : <span class="required-field">*</span></label>
				</div>
				<div class="element">
					<s:textfield id="oggetto" name="oggetto" value="%{oggetto}" size="60" maxlength="300" aria-required="true" />
				</div>
			</div>
			<div class="fieldset-row last-row">
				<div class="label">
					<label for="testo"><wp:i18n key="LABEL_COMUNICAZIONI_TESTO" /> : <span class="required-field">*</span></label>
				</div>
				<div class="element">
					<s:textarea id="testo" name="testo" cols="75" rows="27" value="%{testo}" aria-required="true" />
				</div>
			</div>

			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/skip_main_content_from_buttons.jsp" />

		</fieldset>

		<div class="azioni">
			<input type="hidden" name="codice" value="<s:property value="%{codice}"/>"/>
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_next.jsp" />
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_cancel.jsp" />
		</div>	
	</form> 
</div>
