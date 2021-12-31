<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wpsa" uri="apsadmin-core.tld" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>
<wp:contentNegotiation mimeType="application/xhtml+xml" charset="utf-8"/>


<wp:ifauthorized permission="superuser">
<li class="openmenu"><a href="#" rel="fagiano_jpmail" id="fagiano_menu_jpmail" class="subMenuToggler" tabindex="<wpsa:counter />"><s:text name="jpmail.admin.menu" /></a>
	<ul class="menuToggler" id="fagiano_jpmail">
		<li><a href="<s:url action="editSmtp" namespace="/do/jpmail/MailConfig" />" tabindex="<wpsa:counter />"><s:text name="jpmail.admin.menu.smtp" /></a></li>
		<li><a href="<s:url action="viewSenders" namespace="/do/jpmail/MailConfig" />" tabindex="<wpsa:counter />"><s:text name="jpmail.admin.menu.senders" /></a></li>
	</ul>
</li>
</wp:ifauthorized>
<%-- 
<wp:ifauthorized permission="superuser">
<li class="openmenu"><a href="#" rel="fagiano_jpmail" id="fagiano_menu_jpmail" class="subMenuToggler" tabindex="<wpsa:counter />">Gestione eMail</a>
	<ul class="menuToggler" id="fagiano_jpmail">
		<li><a href="<s:url action="editSmtp" namespace="/do/jpmail/MailConfig" />" tabindex="<wpsa:counter />">Smtp</a></li>
		<li><a href="<s:url action="viewSenders" namespace="/do/jpmail/MailConfig" />" tabindex="<wpsa:counter />">Mittenti</a></li>
	</ul>
</li>
</wp:ifauthorized>
 --%>