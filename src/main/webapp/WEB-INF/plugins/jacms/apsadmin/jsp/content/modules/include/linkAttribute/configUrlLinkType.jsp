<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>
<%@ taglib prefix="wpsa" uri="apsadmin-core.tld" %>
<s:include value="linkAttributeConfigIntro.jsp"></s:include>
<h3><s:text name="title.configureLinkAttribute" />&#32;(<s:text name="title.step2of2" />)</h3>
<s:include value="linkAttributeConfigReminder.jsp"></s:include>
<p><s:text name="note.typeValidURL" /></p>

<s:form>
<s:if test="hasFieldErrors()">
<ul>
	<s:iterator value="fieldErrors">
		<s:iterator value="value">
            <li><s:property escape="false" /></li>
		</s:iterator>
	</s:iterator>
</ul>
</s:if>
<p>
	<label for="url">URL:</label><br />
	<wpsf:textfield name="url" id="url" cssClass="text"></wpsf:textfield>
</p>
<p>
	<wpsf:submit action="joinUrlLink" value="%{getText('label.confirm')}" cssClass="button" />
</p>

</s:form>

<s:include value="/WEB-INF/plugins/jacms/apsadmin/jsp/content/modules/include/backToContentButton.jsp" />