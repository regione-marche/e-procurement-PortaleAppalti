<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>

<s:include value="/WEB-INF/plugins/jacms/apsadmin/jsp/content/modules/include/linkAttribute/linkAttributeConfigIntro.jsp"></s:include>
<h3><s:text name="title.configureLinkAttribute" />&#32;(<s:text name="title.step1of2" />)</h3>
<s:include value="/WEB-INF/plugins/jacms/apsadmin/jsp/content/modules/include/linkAttribute/linkAttributeConfigReminder.jsp"></s:include>
<p>
	<s:text name="note.chooseLinkType" />:
</p>

<s:form action="configLink">
<s:if test="hasFieldErrors()">
<ul>
	<s:iterator value="fieldErrors">
		<s:iterator value="value">
            <li><s:property escape="false" /></li>
		</s:iterator>
	</s:iterator>
</ul>
</s:if>
<ul>
<s:iterator id="typeId" value="linkDestinations">
<s:if test="#typeId != 4">
	
	<s:if test="#typeId == 1">
		<s:set name="iconImagePath" id="iconImagePath"><wp:resourceURL/>administration/img/icons/link-url.png</s:set>
		<s:set name="linkDestination" value="%{getText('note.URLLinkTo')}" />
	</s:if>
	
	<s:if test="#typeId == 2">
		<s:set name="iconImagePath" id="iconImagePath"><wp:resourceURL/>administration/img/icons/link-page.png</s:set>
		<s:set name="linkDestination" value="%{getText('note.pageLinkTo')}" />
	</s:if>
	
	<s:if test="#typeId == 3">
		<s:set name="iconImagePath" id="iconImagePath"><wp:resourceURL/>administration/img/icons/link-content.png</s:set>
		<s:set name="linkDestination" value="%{getText('note.contentLinkTo')}" />
	</s:if>
	
	<li><input type="radio" <s:if test="#typeId == symbolicLink.destType">checked="checked"</s:if> name="linkType" id="linkType_<s:property value="#typeId"/>" value="<s:property value="#typeId"/>" /><label for="linkType_<s:property value="#typeId"/>"><img src="<s:property value="iconImagePath" />" alt=" " /> <s:property value="linkDestination" /></label></li>
	
</s:if>	
</s:iterator>
</ul>

<p><wpsf:submit value="%{getText('label.continue')}" title="%{getText('label.continue')}" cssClass="button" /></p>

</s:form>



<s:include value="/WEB-INF/plugins/jacms/apsadmin/jsp/content/modules/include/backToContentButton.jsp" />