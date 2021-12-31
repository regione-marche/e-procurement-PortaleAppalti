<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="wpsa" uri="apsadmin-core.tld" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>

<h1><s:text name="ppcommon.title.customSection" /></h1>
<h2><s:text name="ppcommon.customParams.subtitle" /></h2>

<s:form action="saveInterface" >
	<s:if test="hasFieldErrors()">
		<div class="message message_error">	
			<h3><s:text name="message.title.FieldErrors" /></h3>
			<ul>
				<s:iterator value="fieldErrors">
					<s:iterator value="value">
						<li><s:property escape="false" /></li>
					</s:iterator>
				</s:iterator>
			</ul>
		</div>
	</s:if>
	<s:if test="hasActionErrors()">
		<div class="message message_error">	
			<h3><s:text name="message.title.ActionErrors" /></h3>
			<ul>
				<s:iterator value="actionErrors">
					<li><s:property escape="false" /></li>
				</s:iterator>
			</ul>
		</div>
	</s:if>

<p>
	<s:text name="ppcommon.customParams.text.1" /><br/>
	<s:text name="ppcommon.customParams.text.2" />
</p>

<table class="generic" summary="<s:text name="ppcommon.systemParams.summary" />">
	<caption><s:text name="ppcommon.systemParams.summary" /></caption>
	<tr>
		<th><s:text name="ppcommon.customParams.column.id" /></th>
		<th><s:text name="ppcommon.customParams.column.configuration" /></th>
		<th><s:text name="ppcommon.customParams.column.value" /></th>
	</tr>
<s:iterator value="customConfigs" id="config" status="stat">
	<tr>
		<td>
			<s:property value="#config.objectId"/>.<em><s:property value="#config.attrib"/></em>
			<s:hidden value="%{#config.objectId}" name="objectId" id="objectId%{#stat.count}"/>
			<s:hidden value="%{#config.attrib}" name="attrib" id="attrib%{#stat.count}"/>
		</td>
		<td>
			<s:if test='%{"VIS".equals(#config.feature)}'><s:text name="ppcommon.customParams.feature.visible" /></s:if>
			<s:if test='%{"MAN".equals(#config.feature)}'><s:text name="ppcommon.customParams.feature.mandatory" /></s:if>
			<s:if test='%{"ACT".equals(#config.feature)}'><s:text name="ppcommon.customParams.feature.active" /></s:if>
			<s:hidden value="%{#config.feature}" name="feature" id="feature%{#stat.count}"/>
		</td>
		<td>
			<s:select name="configValue" value="%{#config.configValue ? 1: 0}" list="#{'1': getText('ppcommon.option.yes'), '0': getText('ppcommon.option.no')}" id="configValue%{#stat.count}"></s:select>
		</td>
	</tr>
</s:iterator>
</table>

<p>
	<wpsf:submit value="%{getText('ppcommon.customParams.button.save')}" cssClass="button" />
	<s:reset value="%{getText('ppcommon.customParams.button.undo')}" cssClass="button" />
</p>
	
</s:form>