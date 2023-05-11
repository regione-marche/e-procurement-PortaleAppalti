<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="wpsa" uri="apsadmin-core.tld" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>

<s:set name="currentResource" value="#attribute.resources[#lang.code]"></s:set>
<s:set name="defaultResource" value="#attribute.resource"></s:set>

<s:if test="#lang.default">
<%-- Lingua di DEFAULT --%>

	<s:if test="#currentResource != null">
		<a class="noborder" href="<s:property value="#defaultResource.attachPath" />"><s:include value="/WEB-INF/apsadmin/jsp/entity/view/textAttribute.jsp" /></a>&#32;(<s:property value="%{#defaultResource.instance.fileLength}"/>)
	</s:if>
	<s:else>
		<s:text name="label.attribute.resources.null" />
	</s:else>

</s:if>
<s:else>
<%-- Lingua NON di DEFAULT --%>
	<s:if test="#defaultResource == null">
		<%-- Risorsa lingua di DEFAULT NON VALORIZZATA --%>
		<s:text name="label.attribute.resources.null" />
	</s:if>
	<s:else>
		<s:if test="#currentResource != null">
			<a class="noborder" href="<s:property value="#defaultResource.attachPath" />"><s:include value="/WEB-INF/apsadmin/jsp/entity/view/textAttribute.jsp" /></a>&#32;(<s:property value="%{#defaultResource.instance.fileLength}"/>)
		</s:if>
		<s:else>
			<a class="noborder" href="<s:property value="#defaultResource.attachPath" />"><s:text name="label.attribute.resources.attach.null" /></a>&#32;(<s:property value="%{#defaultResource.instance.fileLength}"/>)
		</s:else>
	</s:else>

</s:else>