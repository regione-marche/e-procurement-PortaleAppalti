<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wpsa" uri="apsadmin-core.tld" %>

<h1><s:text name="ppcommon.title.customSection" /></h1>
<h2><s:text name="ppcommon.customParams.subtitle" /></h2>

<div class="message">
<p><s:text name="ppcommon.update.success" /></p>
</div>

<a href="<s:url action="editInterface" namespace="/do/ppcommon/CustomConfig" />" tabindex="<wpsa:counter />"><s:text name="ppcommon.customParams.link.backToEdit" /></a>