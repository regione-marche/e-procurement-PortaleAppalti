<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>

<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/js_validate_password.jsp" />

<script type="text/javascript">

	var msgNewPassword = '<wp:i18n key="MSG_INSERT_NEW_PASSWORD" />';
	var msgRepeatNewPassword = '<wp:i18n key="MSG_REPEAT_NEW_PASSWORD" />';
	var msgNewPasswordsDontMatch = '<wp:i18n key="MSG_NEW_PASSWORD_DONT_MATCH" />';

	$(document).ready(function() {

		// validate signup form on keyup and submit
		var validator = $("#reactivate_user_form").validate({
			rules: {
				password: {
					required: true,
					passwordPolicy: ["<c:out value="${username}"/>", 8, 20]
				},
				passwordConfirm: {
					required: true,
					equalTo: "#jpuserreg_password"
				}
			},
			messages: {
				password: {
					required: msgNewPassword
				},
				passwordConfirm: {
					required: msgRepeatNewPassword,
					equalTo: msgNewPasswordsDontMatch
				}
			},
			errorPlacement: function(error, element) {
				error.prependTo(element.parent().next());
			},
			submitHandler: function(form) {
				form.submit();
			},
			success: function(label) {
				label.html("&nbsp;").addClass("checked");
			},
			invalidHandler: function(event, validator) {
				return false;
			}
		});

	});
</script>

<wp:headInfo type="CSS" info="jquery/validation/jquery.validation.css" />

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2><wp:i18n key="jpuserreg_PASSWORD_RECOVERY" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_jpuserreg_PASSWORD_RECOVERY"/>
	</jsp:include>

	<form id="reactivate_user_form" class="change_password_form" action="<wp:action path="/ExtStr2/do/jpuserreg/UserReg/reactivate.action"/>" method="post" >
		
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/field_errors.jsp" />
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
		
		<s:hidden name="token" value="%{token}" />
		<s:hidden name="username" />
		
		<table>
			<tr>
				<td class="label"><label for="password"><wp:i18n key="jpuserreg_PASSWORD"/> <abbr title="(<wp:i18n key="jpuserreg_REQUIRED"/>)">*</abbr></label></td>
				<td class="field"><s:password name="password" id="jpuserreg_password" maxlength="50" cssClass="text" value="" /></td>
				<td class="status">
					<div class="password-meter">
						<div class="password-meter-message"> </div>
						<div class="password-meter-bg">
							<div class="password-meter-bar"></div>
						</div>
					</div>
				</td>
			</tr>
			<tr>
				<td class="label"><label for="passwordConfirm"><wp:i18n key="jpuserreg_PASSWORD_CONFIRM"/> <abbr title="(<wp:i18n key="jpuserreg_REQUIRED"/>)">*</abbr></label></td>
				<td class="field"><s:password name="passwordConfirm" id="jpuserreg_passwordConfirm" maxlength="50" cssClass="text" value="" /></td>
				<td class="status"> </td>	
			</tr>
		</table>

		<div class="azioni">
			<input type="submit" value="<wp:i18n key="jpuserreg_SAVE"/>" class="button"/>
		</div>	
	</form>
</div>