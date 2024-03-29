<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="wpsa" uri="apsadmin-core.tld" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>

<h3><s:text name="title.userManagement.userList" /></h3>
<s:form>
<s:if test="hasActionErrors()">
<div class="message message_error">
<h4><s:text name="message.title.ActionErrors" /></h4>
	<ul>
		<s:iterator value="actionErrors">
			<li><s:property escape="false" /></li>
		</s:iterator>
	</ul>
</div>	
</s:if>

<s:if test="hasFieldErrors()">
<div class="message message_error">
<h4><s:text name="message.title.ActionErrors" /></h4>
	<ul>
		<s:iterator value="fieldErrors">
			<li><s:property escape="false" /></li>
		</s:iterator>
	</ul>
</div>
</s:if>
<s:set name="removeIcon" id="removeIcon"><wp:resourceURL/>administration/img/icons/list-remove.png</s:set>
<ul>
<s:iterator id="user" value="authorizedUsers">
	<li>
		<wpsa:actionParam action="removeUser" var="actionName" >
			<wpsa:actionSubParam name="username" value="%{#user.username}" />
		</wpsa:actionParam>
		<wpsf:submit action="%{#actionName}" type="image" src="%{#removeIcon}" value="%{getText('label.remove')}" title="%{getText('label.remove')}" />: <s:property value="#user" />
	</li>
</s:iterator>
</ul>

<h4><s:text name="title.userManagement.searchUsers" /></h4>
<p class="noscreen">
<%-- //TODO attenzione che se l'azione precedente ha parametri, viene messo un '?' nell'id e la pagina non valida più --%>
	<wpsf:hidden name="authName" />
</p>
<p><label for="text"><s:text name="label.search.for"/>&#32;<s:text name="label.username"/>:</label><br />
<wpsf:textfield name="text" id="text" cssClass="text" /></p>
<p>
	<wpsf:submit action="search" value="%{getText('label.search')}" cssClass="button" />
</p>

<wpsa:subset source="users" count="10" objectName="groupUser" advanced="true" offset="5">
<s:set name="group" value="#groupUser" />

<s:include value="/WEB-INF/apsadmin/jsp/common/inc/pagerInfo.jsp" />
<s:include value="/WEB-INF/apsadmin/jsp/common/inc/pager_formBlock.jsp" />

<table class="generic" summary="<s:text name="note.configAuthority.summary" />">
<tr>
	<th><s:text name="label.username" /></th>
	<th><s:text name="label.date.registration" /></th>
	<th><s:text name="label.date.lastLogin" /></th>
	<th><s:text name="label.date.lastPasswordChange" /></th>
	<th class="icon"><abbr title="<s:text name="label.state" />">S</abbr></th>	
</tr>
<s:iterator id="user">

<s:if test="!#user.japsUser">
	<s:set name="statusIconImagePath" id="statusIconImagePath"><wp:resourceURL/>administration/img/icons/user-status-notjAPSUser.png</s:set>
	<s:set name="statusIconText" id="statusIconText"><s:text name="note.userStatus.notjAPSUser" /></s:set>
</s:if>
<s:elseif test="#user.disabled">
	<s:set name="statusIconImagePath" id="statusIconImagePath"><wp:resourceURL/>administration/img/icons/user-status-notActive.png</s:set>
	<s:set name="statusIconText" id="statusIconText"><s:text name="note.userStatus.notActive" /></s:set>	
</s:elseif>
<s:elseif test="!#user.accountNotExpired">
	<s:set name="statusIconImagePath" id="statusIconImagePath"><wp:resourceURL/>administration/img/icons/user-status-expiredAccount.png</s:set>
	<s:set name="statusIconText" id="statusIconText"><s:text name="note.userStatus.expiredAccount" /></s:set>	
</s:elseif>
<s:elseif test="!#user.credentialsNotExpired">
	<s:set name="statusIconImagePath" id="statusIconImagePath"><wp:resourceURL/>administration/img/icons/user-status-expiredPassword.png</s:set>
	<s:set name="statusIconText" id="statusIconText"><s:text name="note.userStatus.expiredPassword" /></s:set>	
</s:elseif>
<s:elseif test="!#user.disabled">
	<s:set name="statusIconImagePath" id="statusIconImagePath"><wp:resourceURL/>administration/img/icons/user-status-active.png</s:set>
	<s:set name="statusIconText" id="statusIconText"><s:text name="note.userStatus.active" /></s:set>
</s:elseif>

<tr>
	<td><input type="radio" name="username" id="<s:property value="#user.username" />" value="<s:property value="#user.username"/>" /><label for="<s:property value="#user.username" />"><s:property value="#user" /></label></td>
	<td class="centerText monospace"><s:if test="#user.japsUser"><s:date name="#user.creationDate" format="dd/MM/yyyy" /></s:if><s:else>&ndash;</s:else></td>
	<td class="centerText monospace"><s:if test="#user.japsUser"><s:date name="#user.lastAccess" format="dd/MM/yyyy" /></s:if><s:else>&ndash;</s:else></td>
	<td class="centerText monospace"><s:if test="#user.japsUser"><s:date name="#user.lastPasswordChange" format="dd/MM/yyyy" /></s:if><s:else>&ndash;</s:else></td>
	<td class="icon"><img src="<s:property value="#statusIconImagePath" />" alt="<s:property value="#statusIconText" />" title="<s:property value="#statusIconText" />" /></td>
</tr>
</s:iterator>
</table>

<s:include value="/WEB-INF/apsadmin/jsp/common/inc/pager_formBlock.jsp" />

</wpsa:subset>

<p><wpsf:submit action="addUser" value="%{getText('label.add')}" cssClass="button" /></p>

</s:form>