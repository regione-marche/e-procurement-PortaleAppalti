<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="wpsa" uri="apsadmin-core.tld" %>
<h1><s:text name="document.title.main" /></h1>
<p><s:text name="note.jAPS.intro" />.</p>

<wp:ifauthorized permission="editContents" var="hasEditContents" />
<wp:ifauthorized permission="manageResources" var="hasManageResources" />

<c:if test="${hasEditContents || hasManageResources}">
	<p><s:text name="note.maxiButtons.intro" />:</p>
</c:if>

<wp:ifauthorized permission="editContents">
<dl class="maxiButton">
	<dt class="name">
		<a href="<s:url action="new" namespace="/do/jacms/Content" />" title="<s:text name="jacms.menu.contentAdmin.new" />"><img src="<wp:resourceURL/>administration/img/icons/48x48/content-new.png" alt=" " /><br />
		<s:text name="jacms.menu.contentAdmin.new" />
		</a>	
	</dt>
	<dd class="noscreen">
		<p><s:text name="jacms.menu.contentAdmin.new.long" />.</p>
	</dd>
</dl>

<dl class="maxiButton">
	<dt class="name">
		<a href="<s:url action="list" namespace="/do/jacms/Content" />" title="<s:text name="jacms.menu.contentAdmin.list" />"><img src="<wp:resourceURL/>administration/img/icons/48x48/content-list.png" alt=" " /><br />
		<s:text name="jacms.menu.contentAdmin.list" />
		</a>	
	</dt>
	<dd class="noscreen">
		<p><s:text name="jacms.menu.contentAdmin.list.long" />.</p>
	</dd>
</dl>
</wp:ifauthorized>

<wp:ifauthorized permission="manageResources">
<dl class="maxiButton">
	<dt class="name">
		<a href="<s:url action="new" namespace="/do/jacms/Resource"><s:param name="resourceTypeCode" >Image</s:param></s:url>" title="<s:text name="label.new" />&#32;<s:text name="label.image" />"><img src="<wp:resourceURL/>administration/img/icons/48x48/image-new.png" alt=" " /><br />
		<s:text name="label.new" />&#32;<s:text name="label.image" />
		</a>
	</dt>
	<dd class="noscreen">
		<p><s:text name="jacms.menu.resourceAdmin.newImage.long" />.</p>
	</dd>
</dl>

<dl class="maxiButton">
	<dt class="name">
		<a href="<s:url action="new" namespace="/do/jacms/Resource"><s:param name="resourceTypeCode" >Attach</s:param></s:url>" title="<s:text name="label.new.male" />&#32;<s:text name="label.attach" />"><img src="<wp:resourceURL/>administration/img/icons/48x48/attach-new.png" alt=" " /><br />
		<s:text name="label.new.male" />&#32;<s:text name="label.attach" />
		</a>
	</dt>
	<dd class="noscreen">
		<p><s:text name="jacms.menu.resourceAdmin.newAttach.long" />.</p>
	</dd>
</dl>
</wp:ifauthorized>