<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="wpsa" uri="apsadmin-core.tld" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>

<h1><s:text name="title.eMailManagement" /></h1>
<h2><s:text name="title.eMailManagement.sendersConfig" /></h2>

<p>
	<a href="<s:url action="newSender" />" ><s:text name="label.senders.new" /></a>
</p>

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

<s:if test="%{senderCodes.size()==0}">
	<p>
		<s:text name="label.senders.none" />
	</p>
</s:if>
<s:else>
<table class="generic" summary="<s:text name="note.sendersConfig.summary" />">
	<caption><s:text name="note.sendersConfig.caption" /></caption>
	<tr>
		<th><s:text name="code" /></th>
		<th><s:text name="mail" /></th>
		<th class="icon"><abbr title="<s:text name="label.remove" />">&ndash;</abbr></th>
	</tr>
<s:iterator value="%{config.senders.entrySet()}" id="sender">
	<tr>
		<td><s:property value="#sender.key"/></td>
		<td>
			<a href="<s:url action="editSender" ><s:param name="code" value="#sender.key" /></s:url>" title="<s:text name="label.edit" />: <s:property value="#sender.value" />">
				<s:property value="#sender.value" />
			</a>
		</td>
		<td class="icon">
			<a href="<s:url action="trashSender" ><s:param name="code" value="#sender.key" /></s:url>" >
				<img src="<wp:resourceURL/>administration/img/icons/delete.png" alt="<s:text name="label.remove" />: <s:property value="#sender.value" />" title="<s:text name="label.remove" />: <s:property value="#sender.value" />" />
			</a>
		</td>
	</tr>
</s:iterator>
</table>
</s:else>