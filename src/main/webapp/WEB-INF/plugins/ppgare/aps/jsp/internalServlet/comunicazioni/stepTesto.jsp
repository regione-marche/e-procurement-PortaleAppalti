<%@ taglib prefix="wp" uri="aps-core.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<s:set var="helper" value="%{#session['nuovaComunicazione']}"/>

<s:set var="isWizardRettifica" value="%{rettifica != null || #helper.modelloRettifica}" />                                  		 
<s:set var="isRichiestaRettifica" value="%{#helper.modello == 10 || #helper.modello == 15}" />
<s:set var="isInvioRettifica" value="%{#isWizardRettifica && !#isRichiestaRettifica}"/>

<s:if test="%{#isWizardRettifica}" >
	<c:set var="title"><wp:i18n key="TITLE_RETTIFICA_OFFERTA" /></c:set>
	<c:set var="balloon" value="BALLOON_WIZ_RETTIFICA_OFFERTA" />
	<c:set var="hrefAzioni"><wp:action path="/ExtStr2/do/FrontEnd/Comunicazioni/processPageNuovaRettifica.action" /></c:set>
</s:if>
<s:elseif test="%{#helper.modello > 0}" >
	<c:set var="title"><wp:i18n key="TITLE_SOCCORSO_ISTRUTTORIO_NUOVO" /></c:set>
	<c:set var="balloon" value="BALLOON_WIZ_COMUNICAZIONE_TESTO" />
	<c:set var="hrefAzioni"><wp:action path="/ExtStr2/do/FrontEnd/Comunicazioni/processPageNuovoSoccorso.action" /></c:set>	
</s:elseif>
<s:else>
	<c:set var="title"><wp:i18n key="TITLE_COMUNICAZIONI_NUOVA" /></c:set>
	<c:set var="balloon" value="BALLOON_WIZ_COMUNICAZIONE_TESTO" />
	<c:set var="hrefAzioni"><wp:action path="/ExtStr2/do/FrontEnd/Comunicazioni/processPageNuovaComunicazione.action" /></c:set>
</s:else>

<s:set var="tipoComunicazioniCount" value="0"/>
<s:if test="%{#helper.entita eq 'APPA'}" >
	<s:iterator value="%{maps['tipologieComunicazioni']}">
		<s:set var="tipoComunicazioniCount" value="%{#tipoComunicazioniCount + 1}"/>
	</s:iterator>
</s:if>

<%--
helper.entita =<s:property value="%{#helper.entita}" /><br/>
tipoComunicazioniCount=<s:property value="%{#tipoComunicazioniCount}" /><br/>
id=<s:property value="%{id}" /><br/>
garalotti=<s:property value="%{garaLotti}" /><br/>
#garalotti=<s:property value="%{#garaLotti}" /><br/>
rettifica=<s:property value="%{rettifica}" /><br/>
#helper.modelloRettifica=<s:property value="%{#helper.modelloRettifica}" /><br/>
 --%>


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

	<form action="${hrefAzioni}" method="post" >
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
				
			<%-- SOLO PER NUOVA COMUNICAZIONE, SOCCORSO ISTRUTTORIO --%>
			<s:if test="%{! #isWizardRettifica}">
				<s:if test="%{#tipoComunicazioniCount > 0}">
					<div class="fieldset-row ">
						<div class="label">
							<label for="tipoRichiesta"><wp:i18n key="LABEL_COMUNICAZIONI_TIPO_RICHIESTA" /> : <span class="required-field">*</span></label>
						</div>
						<div class="element">
						<%--
							<s:if test="%{id == null}">
								
							</s:if>
							<s:else>
								<s:property value="%{#helper.tipoRichiesta}" />
							</s:else>
						--%>
							<wp:i18n key="OPT_CHOOSE_TIPOLOGIA" var="attr.headerValueTipoRichiesta" />
							<s:select name="tipoRichiesta" list="maps['tipologieComunicazioni']" 
										headerKey="" headerValue="%{#attr.headerValueTipoRichiesta}" 
										value="tipoRichiesta" >
							</s:select>
						</div>
					</div>
				</s:if>
			</s:if>

			<div class="fieldset-row ">
				<div class="label">
					<label for="oggetto"><wp:i18n key="LABEL_COMUNICAZIONI_OGGETTO" /> : <s:if test="%{!#isWizardRettifica}"><span class="required-field">*</span></s:if></label>
				</div>
				<div class="element">
					<s:if test="%{! #isWizardRettifica}">
						<s:textfield id="oggetto" name="oggetto" value="%{oggetto}" size="60" maxlength="300" aria-required="true" />
					</s:if>
					<s:else>
						<s:property value="%{oggetto}" />
						<input type="hidden" name="oggetto" value='<s:property value="%{oggetto}"/>'/>
					</s:else>
				</div>
			</div>
			
			<%-- SOLO PER RETTIFICA --%>
<%--			
isWizardRettifica=<s:property value="%{#isWizardRettifica}"/><br/>
isInvioRettifica=<s:property value="%{#isInvioRettifica}"/><br/>
#helper.modello=<s:property value="%{#helper.modello}"/><br/>
--%>			
			<s:if test="%{#isWizardRettifica}">
				<div class="fieldset-row ">
					<div class="label">
						<label for="tipoBusta"><wp:i18n key="LABEL_TIPO_BUSTA" /> : </label>
					</div>
					<div class="element">
						<s:if test="%{#isInvioRettifica}">
							<s:iterator value="maps['tipiBusta']">
								<s:if test="%{key == tipoBusta}">
									<s:property value="%{value}"/>
								</s:if>
							</s:iterator>
							<input type="hidden" name="tipoBusta" value="${tipoBusta}"/>
						</s:if>
						<s:else>
							<wp:i18n key="OPT_CHOOSE_TIPOLOGIA" var="attr.headerValueTipoBusta" />
							<s:select name="tipoBusta" list="maps['tipiBusta']" 
										headerKey="" headerValue="%{#attr.headerValueTipoBusta}" 
										value="%{tipoBusta}" >
							</s:select>
						</s:else>
					</div>
				</div>
				
				<s:if test="%{garaLotti}">				
					<div class="fieldset-row ">
						<div class="label">
							<label for="lotto"><wp:i18n key="LABEL_LOTTO" /> : </label>
						</div>
						<div class="element">
							<s:if test="%{#isInvioRettifica}">
								<s:property value="%{lotto}"/>
								<input type="hidden" name="lotto" value="${lotto}"/>
							</s:if>
							<s:else>
								<wp:i18n key="OPT_CHOOSE_LOTTO" var="attr.headerValueLotto" />
								<s:select name="lotto" list="lotti" 
										headerKey="" headerValue="%{#attr.headerValueLotto}" 
										value="%{lotto}" >
								</s:select>
							</s:else>
						</div>
					</div>
				</s:if>
			</s:if>
			
			<div class="fieldset-row last-row">
				<div class="label">
					<label for="testo"><wp:i18n key="LABEL_COMUNICAZIONI_TESTO" /> : <s:if test="%{!#isWizardRettifica}"><span class="required-field">*</span></s:if></label>
				</div>
				<div class="element">
					<s:if test="%{ !#isWizardRettifica }">
						<s:textarea id="testo" name="testo" cols="75" rows="27" value="%{testo}" aria-required="true" />
					</s:if>
					<s:else>
						<s:property value="%{#helper.testo}" />
						<input type="hidden" name="testo" value='<s:property value="%{#helper.testo}"/>'/>
					</s:else>
				</div>
			</div>

			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/skip_main_content_from_buttons.jsp" />

		</fieldset>

		<div class="azioni">
			<%-- <input type="hidden" name="codice" value="<s:property value="%{codice}"/>"/> --%>
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_next.jsp" />
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_cancel.jsp" />
		</div>	
	</form> 
</div>
