<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wpsa" uri="apsadmin-core.tld" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>
<wp:contentNegotiation mimeType="application/xhtml+xml" charset="utf-8"/>


<wp:ifauthorized permission="superuser">
<li class="openmenu"><a href="#" rel="ppcommon" id="ppcommon_menu" class="subMenuToggler" tabindex="<wpsa:counter />">Maggioli</a>
	<ul class="menuToggler" id="ppcommon">
		<li><a href="<s:url action="editAppParam" namespace="/do/ppcommon/CustomConfig" />" tabindex="<wpsa:counter />"><s:text name="ppcommon.function.systemParams" /></a></li>
		<li><a href="<s:url action="editInterface" namespace="/do/ppcommon/CustomConfig" />" tabindex="<wpsa:counter />"><s:text name="ppcommon.function.customParams" /></a></li>
		<li><a href="<s:url action="listCustomPdf" namespace="/do/ppcommon/CustomConfig" />" tabindex="<wpsa:counter />"><s:text name="ppcommon.function.pdf" /></a></li>
	</ul>
</li>
</wp:ifauthorized>
