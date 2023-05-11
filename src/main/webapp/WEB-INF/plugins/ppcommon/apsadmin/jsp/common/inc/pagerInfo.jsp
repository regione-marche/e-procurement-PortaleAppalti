<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>
<p><s:text name="note.searchIntro" />&#32;<s:property value="totEvents" />&#32;<s:text name="note.searchOutro" />.<br />

<s:if test="%{risultati.size() > 0}">
	<s:text name="label.page" />: [<s:property value="model.currentPage" />/<s:property value="model.totalPages" />].</p>
</s:if>
<s:else>
	<s:text name="label.page" />: [<s:property value="0" />/<s:property value="0" />].</p>
</s:else>