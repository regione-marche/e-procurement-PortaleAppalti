<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>

<h1><s:text name="title.generalSettings" /></h1>
<h2><s:text name="title.generalSettings.settings" /></h2>

<s:form action="updateSystemParams">
	
	<fieldset>		
		<legend class="accordion_toggler">
			<s:text name="sysconfig.legend.misc" />
		</legend>		
		<div class="accordion_element">
			<p>
				<span class="important"><s:text name="sysconfig.requirePageCode" />:</span><br />
				<s:set name="paramName" value="'requirePageCode'" />
				<s:include value="/WEB-INF/apsadmin/jsp/admin/booleanParamBlock.jsp" />
			</p>
			<p>
				<span class="important"><s:text name="sysconfig.chooseYourEditor" />:</span><br />
				<input type="radio" id="hypertextEditor_none" name="hypertextEditor" value="none" <s:if test="systemParams['hypertextEditor'] == 'none'">checked="checked"</s:if> /><label for="hypertextEditor_none"><s:text name="label.none" /></label><br />
				<input type="radio" id="hypertextEditor_fckeditor" name="hypertextEditor" value="fckeditor" <s:if test="systemParams['hypertextEditor'] == 'fckeditor'">checked="checked"</s:if> /><label for="hypertextEditor_fckeditor"><s:text name="name.editor.fckeditor" /></label><br />
				<input type="radio" id="hypertextEditor_hoofed" name="hypertextEditor" value="hoofed" <s:if test="systemParams['hypertextEditor'] == 'hoofed'">checked="checked"</s:if> /><label for="hypertextEditor_hoofed"><s:text name="name.editor.hoofed" /></label>
			</p>
			<p>
				<span class="important"><s:text name="sysconfig.URLstyle" />:</span><br />
				<s:set name="paramName" value="'urlStyle'" />
				<input type="radio" id="urlStyle_classic" name="urlStyle" value="classic" <s:if test="systemParams['urlStyle'] == 'classic'">checked="checked"</s:if> /><label for="urlStyle_classic"><s:text name="URLstyle.classic" /></label><br />
				<input type="radio" id="urlStyle_breadcrumbs" name="urlStyle" value="breadcrumbs" <s:if test="systemParams['urlStyle'] == 'breadcrumbs'">checked="checked"</s:if> /><label for="urlStyle_breadcrumbs"><s:text name="URLstyle.breadcrumbs" /></label><br />
			</p>
		</div>
	</fieldset>

	<fieldset>
		<legend class="accordion_toggler">
			<s:text name="sysconfig.legend.systemPages" />
		</legend>
		<div class="accordion_element">
			<p>
				<label for="homePageCode"><s:text name="sysconfig.homePageCode" />:</label><br />
				<s:set name="paramName" value="'homePageCode'" />
				<s:include value="/WEB-INF/apsadmin/jsp/admin/selectPageParamBlock.jsp" />
			</p>
			<p>
				<label for="notFoundPageCode"><s:text name="sysconfig.notFoundPageCode" />:</label><br />
				<s:set name="paramName" value="'notFoundPageCode'" />
				<s:include value="/WEB-INF/apsadmin/jsp/admin/selectPageParamBlock.jsp" />
			</p>
			<p>
				<label for="errorPageCode"><s:text name="sysconfig.errorPageCode" />:</label><br />
				<s:set name="paramName" value="'errorPageCode'" />
				<s:include value="/WEB-INF/apsadmin/jsp/admin/selectPageParamBlock.jsp" />
			</p>
			<p>
				<label for="loginPageCode"><s:text name="sysconfig.loginPageCode" />:</label><br />
				<s:set name="paramName" value="'loginPageCode'" />
				<s:include value="/WEB-INF/apsadmin/jsp/admin/selectPageParamBlock.jsp" />
			</p>
		</div>
	</fieldset>

	<fieldset><legend class="accordion_toggler"><s:text name="sysconfig.legend.privacyModule" /></legend>
		<div class="accordion_element">
			<p>
				<span class="important"><s:text name="label.active" />:</span><br />
				<s:set name="paramName" value="'extendedPrivacyModuleEnabled'" />
				<s:include value="/WEB-INF/apsadmin/jsp/admin/booleanParamBlock.jsp" />
			</p>
			<p>
				<label for="maxMonthsSinceLastAccess"><s:text name="sysconfig.maxMonthsSinceLastAccess" />:</label><br />
				<s:set name="paramName" value="'maxMonthsSinceLastAccess'" />
				<s:include value="/WEB-INF/apsadmin/jsp/admin/textParamBlock.jsp" />
			</p>
			<p>
				<label for="maxMonthsSinceLastPasswordChange"><s:text name="sysconfig.maxMonthsSinceLastPasswordChange" />:</label><br />
				<s:set name="paramName" value="'maxMonthsSinceLastPasswordChange'" />
				<s:include value="/WEB-INF/apsadmin/jsp/admin/textParamBlock.jsp" />
			</p>
			<p>
				<label for="maxLoginAttemptsWrongPassword"><s:text name="sysconfig.maxLoginAttemptsWrongPassword" />:</label><br />
				<s:set name="paramName" value="'maxLoginAttemptsWrongPassword'" />
				<s:include value="/WEB-INF/apsadmin/jsp/admin/textParamBlock.jsp" />
			</p>
			<p>
				<label for="maxLoginAttemptsSameIp"><s:text name="sysconfig.maxLoginAttemptsSameIp" />:</label><br />
				<s:set name="paramName" value="'maxLoginAttemptsSameIp'" />
				<s:include value="/WEB-INF/apsadmin/jsp/admin/textParamBlock.jsp" />
			</p>
			<p>
				<label for="inhibitionIpTimeInMinutes"><s:text name="sysconfig.inhibitionIpTimeInMinutes" />:</label><br />
				<s:set name="paramName" value="'inhibitionIpTimeInMinutes'" />
				<s:include value="/WEB-INF/apsadmin/jsp/admin/textParamBlock.jsp" />
			</p>
		</div>
	</fieldset>

	<p><wpsf:submit value="%{getText('label.confirm')}" cssClass="button" /></p>
</s:form>