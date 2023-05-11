<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<es:checkCustomization var="visConsensoPrivacy" objectId="REG-IMPRESA" attribute="PRIVACY" feature="VIS" />
<es:checkCustomization var="bloccaLoginUgualeCodiceFiscale" objectId="REG-IMPRESA" attribute="LOGINCF" feature="ACT" />
<es:checkCustomization var="registrazioneManuale" objectId="REG-IMPRESA" attribute="VERIFICAMANUALE" feature="ACT" />
<es:checkCustomization var="consensoExtra" objectId="REG-IMPRESA" attribute="CONSENSO-EXTRA" feature="VIS" />


<s:set var="codiceFiscale" value="%{#session.dettRegistrImpresa.datiPrincipaliImpresa.codiceFiscale}" />
<s:set var="ragioneSociale" value="%{#session.dettRegistrImpresa.datiPrincipaliImpresa.ragioneSociale}" />

<c:set var="bloccaLogin" value="${0}"/>

<c:set var="titoloFieldsetUtenza" value="LABEL_REGISTRA_OE_UTENZA"/>
<c:set var="codiceBalloon" value="BALLOON_REG_IMPRESA_UTENZA"/>
<c:if test="${bloccaLoginUgualeCodiceFiscale}">
	<c:set var="titoloFieldsetUtenza" value="LABEL_REGISTRA_OE_UTENZA_LOGINCF"/>
	<c:set var="codiceBalloon" value="BALLOON_REG_IMPRESA_UTENZA_LOGINCF"/>
</c:if>

<s:set var="helper" value="%{#session.dettRegistrImpresa}" />


<div class="portgare-view">

	<h2><wp:i18n key="TITLE_PAGE_REGISTRA_OE" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/field_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />

	<jsp:include page="stepsRegistrazione.jsp" />

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="${codiceBalloon}"/>
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/skip_form_buttons.jsp" />

	<form action="<wp:action path="/ExtStr2/do/FrontEnd/RegistrImpr/processPageUtenza.action" />" method="post">
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
		
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/mandatory_fields_message.jsp" />
		
		<fieldset>
			<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="${titoloFieldsetUtenza}" /></legend>
	
			<c:choose>
				<c:when test="${bloccaLoginUgualeCodiceFiscale || (bloccaLogin == 1) || ssoPresente}">
					<div class="fieldset-row">
						<div class="label">
							<label for="usernamereg"><wp:i18n key="USERNAME" /> : </label>
						</div>
						<div class="element">
							<div class="contents-group">
								<s:property value="%{#codiceFiscale}" /> 
								<s:hidden name="username" id="usernamereg" value="%{#codiceFiscale}"/>
								<s:hidden name="usernameConfirm" id="usernameConfirm" value="%{#codiceFiscale}"/>
								<%--
								<div class="note">
									Attenzione: questo portale assegna <strong>in automatico</strong> il nome utente uguale al codice fiscale impresa.
								</div>
								 --%>
								<%--
								<c:if test="${ssoPresente}">
									<div class="note">
										<wp:i18n key="LABEL_REGISTRA_OE_USERNAME_NOTA_SSO" />
									</div>
								</c:if>
								--%>
							</div>
						</div>
					</div>
				</c:when>
				<c:otherwise>
					<div class="fieldset-row">
						<div class="label">
							<label for="usernamereg"><wp:i18n key="USERNAME" /> : <span class="required-field">*</span></label>
						</div>
						<div class="element">
							<div class="contents-group">
								<s:textfield name="username" id="usernamereg" maxlength="20" size="20" aria-required="true" />
								<div class="note">
									<wp:i18n key="LABEL_REGISTRA_OE_USERNAME_NOTA_1" /><br/>
									<strong><wp:i18n key="LABEL_REGISTRA_OE_USERNAME_NOTA_2" /></strong>
								</div>
								<c:if test="${ssoPresente}">
									<div class="note">
										<wp:i18n key="LABEL_REGISTRA_OE_USERNAME_NOTA_SSO" />
									</div>
								</c:if>
							</div>
						</div>
					</div>

					<div class="fieldset-row">
						<div class="label">
							<label for="usernameConfirm"><wp:i18n key="LABEL_USERNAME_CONFIRM" /> : <span class="required-field">*</span></label>
						</div>
						<div class="element">
							<div class="contents-group">
								<s:textfield name="usernameConfirm" id="usernameConfirm" maxlength="20" size="20" aria-required="true" />
							</div>
							<div class="note"><wp:i18n key="LABEL_REGISTRA_OE_USERNAME_CONFIRM_NOTA" /></div>
						</div>
					</div>
				</c:otherwise>
			</c:choose>
	
		</fieldset>
					
		<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/regimpresa/inc/accettazioneConsensi.jsp" />

		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/skip_main_content_from_buttons.jsp" />

		<div class="azioni">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_previous.jsp" />
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_next.jsp" />
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_cancel.jsp" />
		</div>
	</form>
</div>