<%@ taglib prefix="wp"   uri="aps-core.tld" %>
<%@ taglib prefix="wpsa" uri="apsadmin-core.tld" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>
<%@ taglib prefix="s"    uri="/struts-tags" %>

<h2><wp:i18n key="jpuserreg_REGISTRATION"/></h2>

<form action="<wp:action path="/ExtStr2/do/jpuserreg/UserReg/register.action"/>" method="post" >
	
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/field_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
	
	<p>
		<label for="privacyPolicyAgreement"><wp:i18n key="jpuserreg_PRIVACY_AGREEMENT"/> <abbr title="(<wp:i18n key="jpuserreg_REQUIRED"/>)">*</abbr></label><br />
		<wpsf:checkbox name="privacyPolicyAgreement" id="privacyPolicyAgreement" />
	</p>
	<p>
		<label for="username"><wp:i18n key="jpuserreg_USERNAME"/> <abbr title="(<wp:i18n key="jpuserreg_REQUIRED"/>)">*</abbr></label><br />
		<wpsf:textfield name="username" id="username" maxlength="20" />
	</p>
	<p>
		<label for="name"><wp:i18n key="jpuserreg_NAME"/> <abbr title="(<wp:i18n key="jpuserreg_REQUIRED"/>)">*</abbr></label><br />
		<wpsf:textfield name="%{userRegConfig.profileNameAttr}" value="%{name}" id="name" />
	</p>
	<p>
		<label for="surname"><wp:i18n key="jpuserreg_SURNAME"/> <abbr title="(<wp:i18n key="jpuserreg_REQUIRED"/>)">*</abbr></label><br />
		<wpsf:textfield name="%{userRegConfig.profileSurnameAttr}" value="%{surname}" id="surname" />
	</p>
	<p>
		<label for="eMail"><wp:i18n key="jpuserreg_EMAIL"/> <abbr title="(<wp:i18n key="jpuserreg_REQUIRED"/>)">*</abbr></label><br />
		<wpsf:textfield name="%{userRegConfig.profileEMailAttr}" value="%{email}" id="eMail" />
	</p>
	<p>
		<label for="eMailConfirm"><wp:i18n key="jpuserreg_EMAIL_CONFIRM"/> <abbr title="(<wp:i18n key="jpuserreg_REQUIRED"/>)">*</abbr></label><br />
		<wpsf:textfield name="emailConfirm" id="eMailConfirm" />
	</p>
	<p>
		<label for="language"><wp:i18n key="jpuserreg_LANG"/> <abbr title="(<wp:i18n key="jpuserreg_REQUIRED"/>)">*</abbr></label><br />
		<select name="<s:property value="%{userRegConfig.profileLangAttr}"/>" id="language" tabindex="<wpsa:counter />">
			<s:iterator id="lang" value="langs">
				<option value="<s:property value="#lang.code"/>" <s:if test="%{language==#lang.code}">selected="selected" </s:if>><s:property value="#lang.code"/> &ndash; <s:property value="#lang.descr"/></option> 
			</s:iterator>
		</select>
	</p>
	<div class="azioni">
		<input type="submit" value="<wp:i18n key="jpuserreg_SAVE"/>" />
	</div>
</form>