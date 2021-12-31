<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wpsa" uri="apsadmin-core.tld" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h2 id="manage" class="noscreen"><s:text name="note.userbar.intro" />:</h2>

<h3 class="styled"><s:text name="menu.componentsAdmin" /></h3>
<ul class="menu vertical">
	
	<wp:ifauthorized permission="viewUsers">
		<li class="openmenu"><a href="#" rel="fagiano_utenze" id="menu_utenze" class="subMenuToggler"><s:text name="menu.accountAdmin" /></a>
			<ul class="menuToggler" id="fagiano_utenze">
				<li><a href="<s:url action="list" namespace="/do/User" />"><s:text name="menu.accountAdmin.users" /></a></li>
				<wp:ifauthorized permission="superuser">
					<li><a href="<s:url action="list" namespace="/do/Role" />"><s:text name="menu.accountAdmin.roles" /></a></li>
				</wp:ifauthorized>
			</ul>
		</li>
		<wp:ifauthorized permission="superuser">
			<li><a href="<s:url action="list" namespace="/do/Group" />" id="menu_gruppi"><s:text name="menu.accountAdmin.groups" /></a></li>
		</wp:ifauthorized>
	</wp:ifauthorized>
	
	<wp:ifauthorized permission="manageCategories">
		<li><a href="<s:url action="viewTree" namespace="/do/Category" />" id="menu_categorie"><s:text name="menu.categoryAdmin" /></a></li>
	</wp:ifauthorized>
	
	<wp:ifauthorized permission="manageLanguages">
	<li class="openmenu"><a href="#" rel="fagiano_lingue" class="subMenuToggler" id="menu_lingue"><s:text name="menu.languageAdmin" /></a>
	<ul class="menuToggler" id="fagiano_lingue">
		<li><a href="<s:url action="list" namespace="/do/Lang" />"><s:text name="menu.languageAdmin.languages" /></a></li>
		<li><a href="<s:url action="list" namespace="/do/LocaleString" />"><s:text name="menu.languageAdmin.labels" /></a></li>
	</ul>
	</li>
	</wp:ifauthorized>

	<wp:ifauthorized permission="superuser">
	<wpsa:pluginsSubMenu objectName="subMenus" />
	<li <s:if test="#subMenus.size > 0">class="openmenu"</s:if>><a href="#" <s:if test="#subMenus.size > 0">rel="fagiano_plugins" class="subMenuToggler"</s:if> id="menu_plugins"><s:text name="menu.plugins" /></a>
		<ul class="menuToggler" id="fagiano_plugins">		
	<s:iterator value="#subMenus" id="subMenu">
	<wpsa:include value="%{#subMenu.subMenuFilePath}"></wpsa:include>
	</s:iterator>
		</ul>
	</li>
	</wp:ifauthorized>
	
</ul>

<wp:ifauthorized permission="managePages">
<h3 class="styled"><s:text name="menu.portalAdmin" /></h3>
<ul class="menu vertical">
	<li><a href="<s:url action="viewTree" namespace="/do/Page" />" id="menu_pagine"><s:text name="menu.pageAdmin" /></a></li>
	<li><a href="<s:url action="viewShowlets" namespace="/do/Portal/Showlets" />" id="menu_servizi"><s:text name="menu.showletAdmin" /></a></li>
</ul>
</wp:ifauthorized>

<wp:ifauthorized permission="editContents" var="isEditContents" />
<wp:ifauthorized permission="manageResources" var="isManageResources" />

<c:if test="${isEditContents || isManageResources}">
<h3 class="styled"><s:text name="jacms.menu.cmsAdmin" /></h3>
<ul class="menu vertical">
	<wp:ifauthorized permission="editContents">
		<li class="openmenu"><a href="#" rel="fagiano_contenuti" class="subMenuToggler" id="menu_contenuti"><s:text name="jacms.menu.contentAdmin" /></a>
			<ul class="menuToggler" id="fagiano_contenuti">
				<li><a href="<s:url action="new" namespace="/do/jacms/Content" />"><s:text name="jacms.menu.contentAdmin.new" /></a></li>
				<li><a href="<s:url action="list" namespace="/do/jacms/Content" />"><s:text name="jacms.menu.contentAdmin.list" /></a></li>
				<wp:ifauthorized permission="superuser">
				<li><a href="<s:url action="list" namespace="/do/jacms/ContentModel" />"><s:text name="jacms.menu.contentModelAdmin" /></a></li>
				<li><a href="<s:url action="initViewEntityTypes" namespace="/do/Entity"><s:param name="entityManagerName">jacmsContentManager</s:param></s:url>"><s:text name="jacms.menu.contentTypeAdmin" /></a></li>				
				<li><a href="<s:url action="openIndexProspect" namespace="/do/jacms/Content/Admin" />"><s:text name="menu.reload.contents" /></a></li>
				</wp:ifauthorized>						
			</ul>
		</li>
	</wp:ifauthorized>
	<wp:ifauthorized permission="manageResources">
		<li class="openmenu"><a href="#" rel="fagiano_risorse" class="subMenuToggler" id="menu_risorse"><s:text name="jacms.menu.resourceAdmin" /></a>
			<ul class="menuToggler" id="fagiano_risorse">
				<li><a href="<s:url action="list" namespace="/do/jacms/Resource"><s:param name="resourceTypeCode" >Image</s:param></s:url>"><s:text name="jacms.menu.imageAdmin" /></a></li>
				<li><a href="<s:url action="list" namespace="/do/jacms/Resource"><s:param name="resourceTypeCode" >Attach</s:param></s:url>"><s:text name="jacms.menu.attachAdmin" /></a></li>
			</ul>
		</li>
	</wp:ifauthorized>
	
</ul>
</c:if>

<wp:ifauthorized permission="viewUsers">
<h3 class="styled"><s:text name="menu.generalSettings" /></h3>
<ul class="menu vertical">
	<wp:ifauthorized permission="superuser">
	<li><a href="<s:url namespace="/do/BaseAdmin" action="configSystemParams" />" id="menu_configura"><s:text name="menu.configure" /></a></li>
	<li><a href="<s:url namespace="/do/BaseAdmin" action="reloadConfig" />" id="menu_ricarica_configurazione"><s:text name="menu.reload.config" /></a></li>	
	<li><a href="<s:url namespace="/do/Entity" action="viewManagers" />" id="menu_entita"><s:text name="menu.entityAdmin" /></a></li>	
	</wp:ifauthorized>
	<li><a href="<s:url action="searchEvents" namespace="/do/ppcommon/CustomConfig" />" tabindex="<wpsa:counter />"><s:text name="ppcommon.function.searchEvents" /></a></li>
</ul>
</wp:ifauthorized>
