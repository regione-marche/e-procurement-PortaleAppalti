<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>
<%@ taglib prefix="wpsa" uri="apsadmin-core.tld" %>
<h1><s:text name="title.generalSettings" /></h1>
<h2><s:text name="title.entityAdmin.manage" /></h2>
<p><s:text name="note.entities.intro" /></p>

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

<p>
	<a href="<s:url namespace="/do/BaseAdmin" action="reloadEntitiesReferences" />"><s:text name="note.entityAdmin.entityManagers.reload" /></a>
</p>

<table class="generic" summary="<s:text name="note.entityAdmin.entityManagers.list.summary" />">
<caption><s:text name="title.entityAdmin.entityManagers.list" /></caption>
<tr>
	<th><s:text name="label.description" /></th>
	<th class="icon"><abbr title="<s:text name="label.references.long" />"><s:text name="label.references.short" /></abbr></th>
</tr>
<s:iterator value="entityManagers" var="entityManager" status="counter">
	<s:set var="entityAnchor" value="%{'entityCounter'+#counter.count}" />
	<tr>
		<td>
			<a id="<s:property value="#entityAnchor" />" href="<s:url namespace="/do/Entity" action="initViewEntityTypes" ><s:param name="entityManagerName" value="#entityManager" /></s:url>"><s:text name="%{#entityManager}.name" /></a>
		</td>
		
		<td class="icon">
<s:if test="getEntityManagerStatus(#entityManager) == 1">
			<a href="
					<s:url action="viewManagers" namespace="/do/Entity" anchor="%{#entityAnchor}" /> 
				" title="<s:text name="label.references.status.wip" />"><img src="<wp:resourceURL />administration/img/icons/generic-status-wip.png" alt="<s:text name="label.references.status.wip" />" /></a>
</s:if>
<s:elseif test="getEntityManagerStatus(#entityManager) == 2">
			<a href="
					<s:url action="reloadEntityManagerReferences" namespace="/do/Entity" anchor="%{#entityAnchor}">
						<s:param name="entityManagerName" value="#entityManager" />
					</s:url>
				" title="<s:text name="label.references.status.ko" />"><img src="<wp:resourceURL />administration/img/icons/generic-status-ko.png" alt="<s:text name="label.references.status.ko" />" /></a>	
</s:elseif>
<s:elseif test="getEntityManagerStatus(#entityManager) == 0">
			<a href="
					<s:url action="reloadEntityManagerReferences" namespace="/do/Entity" anchor="%{#entityAnchor}">
						<s:param name="entityManagerName" value="#entityManager" />
					</s:url>
				" title="<s:text name="label.references.status.ok" />"><img src="<wp:resourceURL />administration/img/icons/generic-status-ok.png" alt="<s:text name="label.references.status.ok" />" /></a>
</s:elseif>
		</td>
		
		<%-- 
		- -
		<a href="<s:url action="search" namespace="/do/Entity" ><s:param name="entityManagerName" value="#entityManager" /></s:url>">**RICERCA ELEMENTI**</a>
		--%>
	</tr>
</s:iterator>
</table>