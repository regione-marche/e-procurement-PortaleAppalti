<%@ taglib prefix="s" uri="/struts-tags" %>

<s:if test="#lang.default">
	<s:include value="/WEB-INF/apsadmin/jsp/entity/modules/textAttribute.jsp" />
</s:if>
<s:else>
	<s:if test="#attributeTracer.listElement">
		<s:include value="/WEB-INF/apsadmin/jsp/entity/modules/textAttribute.jsp" />
	</s:if>
	<s:else>
		<s:text name="note.editContent.doThisInTheDefaultLanguage.must" />.
	</s:else>
</s:else>