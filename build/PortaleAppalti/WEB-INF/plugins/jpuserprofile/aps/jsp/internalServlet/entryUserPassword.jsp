<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>

<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/js_validate_password.jsp" />
<script type="text/javascript">
	
	var msgOldPassword = '<wp:i18n key="MSG_INSERT_OLD_PASSWORD" />';
	var msgNewPassword = '<wp:i18n key="MSG_INSERT_NEW_PASSWORD" />';
	var msgRepeatNewPassword = '<wp:i18n key="MSG_REPEAT_NEW_PASSWORD" />';
	var msgNewPasswordsDontMatch = '<wp:i18n key="MSG_NEW_PASSWORD_DONT_MATCH" />';
	var isAdmin = ('${administrator}' == "true");
	var minLength = (isAdmin ? 14 : 8); 
	var maxLength = 20;
	
	$(document).ready(function() {
	
		// validate signup form on keyup and submit
		var validator = $("#change_password_form").validate({
			rules: {
				oldPassword: {
					required: true
				},
				password: {
					required: true,
					passwordPolicy: ["<c:out value="${sessionScope.currentUser}"/>", minLength, maxLength, isAdmin]
				},
				passwordConfirm: {
					required: true,
					equalTo: "#jpuserprofile_newPassword"
				}
			},
			messages: {
				oldPassword: {
					required: msgOldPassword
				},
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
				//label.remove();
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

<%--
jpuserprofile_EDITPASSWORD = Modifica Password
jpuserprofile_OLDPASSWORD = Vecchia password
jpuserprofile_NEWPASS = Nuova password
jpuserprofile_CONFIRM_NEWPASS = Conferma nuova password
jpuserprofile_SAVE_PASSWORD = Salva password
jpuserprofile_PLEASE_LOGIN_TO_EDIT_PASSWORD = E' necessario logarsi per cambiare la password
--%>

<c:set var="infoMsg" value="BALLOON_jpuserprofile_EDITPASSWORD"/>
<c:if test="${administrator}">
	<c:set var="infoMsg" value="BALLOON_jpadminprofile_EDITPASSWORD"/>
</c:if>


<div class="portgare-view">

	<h2><wp:i18n key="jpuserprofile_EDITPASSWORD" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="${infoMsg}"/>
	</jsp:include>

	<c:if test="${sessionScope.currentUser != null && !sessionScope.currentUser.isCredentialsNotExpired()}">
		<br/>
		<p>
			<h2><wp:i18n key="USER_EXPIRED_PASSWORD" /></h2>
		</p>
	</c:if>

	<c:choose>
		<c:when test="${sessionScope.currentUser != 'guest'}">
		
			<form id="change_password_form" action="<wp:action path="/ExtStr2/do/jpuserprofile/Front/CurrentUser/changePassword.action" />" method="post" enctype="multipart/form-data">

				<s:if test="hasFieldErrors()">
					<div class="errors">
						<h3><wp:i18n key="jpuserprofile_MESSAGE_TITLE_FIELDERRORS" /></h3>
						<ul>
							<s:iterator value="fieldErrors">
								<s:iterator value="value" status="stat">
									<li class='<s:if test="%{#stat.first}">first</s:if> <s:if test="%{#stat.last}">last</s:if>'>
										<s:property escape="false" />
									</li>
								</s:iterator>
							</s:iterator>
						</ul>
					</div>
				</s:if>
				<s:hidden name="username" />
				<table>
					<tr>
						<td class="label"><label for="jpuserprofile_oldPassword"><wp:i18n key="jpuserprofile_OLDPASSWORD" /></label></td>
						<td class="field"><s:password name="oldPassword" id="jpuserprofile_oldPassword" cssClass="text" value="" maxlength="50" /></td>
						<td class="status"> </td>
					</tr>
					<tr>
						<td class="label"><label for="jpuserprofile_newPassword"><wp:i18n key="jpuserprofile_NEWPASS" /></label></td>
						<td class="field"><s:password name="password" id="jpuserprofile_newPassword" cssClass="text" value="" maxlength="50" /></td>
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
						<td class="label"><label for="jpuserprofile_passwordConfirm"><wp:i18n key="jpuserprofile_CONFIRM_NEWPASS" /></label></td>
						<td class="field"><s:password name="passwordConfirm" id="jpuserprofile_passwordConfirm" cssClass="text" value="" maxlength="50" /></td>
						<td class="status"> </td>
					</tr>
				</table>
				<s:set var="savepassword_label"><wp:i18n key="jpuserprofile_SAVE_PASSWORD" /></s:set>
				<div class="azioni">
					<s:submit value="%{savepassword_label}" cssClass="button"/>
				</div>
			</form>
		</c:when>
		<c:otherwise>
			<p>
				<wp:i18n key="jpuserprofile_PLEASE_LOGIN_TO_EDIT_PASSWORD" />
			</p>
		</c:otherwise>
	</c:choose>
</div>