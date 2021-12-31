<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>

<h1><s:text name="title.languageAdmin" /></h1>
<h2><s:text name="title.languageAdmin.languages" /></h2>

<s:form action="add">

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
	<label for="langCode"><s:text name="name.chooseALanguage" />:</label><br />
	<select name="langCode" id="langCode" class="monospace text">
		<s:iterator id="lang" value="assignableLangs">
		<option value="<s:property value="#lang.code"/>"><s:property value="#lang.code"/> &ndash; <s:property value="#lang.descr"/></option> 
		</s:iterator>
	</select>
</p>
<p><wpsf:submit value="%{getText('label.add')}" cssClass="button" /></p>
</s:form>

<table class="generic" summary="<s:text name="note.generalSettings.lang.summary" />">
<caption><s:text name="note.generalSettings.lang.definedLangs" /></caption>
<tr>
	<th><s:text name="label.code" /></th>
	<th><s:text name="label.description" /></th>
	<th class="icon"><abbr title="<s:text name="label.remove" />">&ndash;</abbr></th>	
</tr>
<s:iterator id="lang" value="langs">
<tr>
	<td class="monospace">
		<s:if test="#lang.default">
		(<em><s:text name="label.default" /></em>)
		</s:if>	
		<s:property value="#lang.code" />
	</td>
	<td><s:property value="#lang.descr" /></td>
	
	<td class="icon"><a href="<s:url action="remove"><s:param name="langCode" value="#lang.code"/></s:url>" title="<s:text name="label.remove" />: <s:property value="#lang.descr" />"><img src="<wp:resourceURL />administration/img/icons/delete.png" alt="<s:text name="label.alt.clear" />" /></a></td>	
</tr>
</s:iterator>
</table>
