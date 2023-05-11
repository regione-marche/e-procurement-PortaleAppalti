<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>

<h1><a href="<s:url action="viewTree" namespace="/do/Page" />" title="<s:text name="note.goToSomewhere" />: <s:text name="title.pageManagement" />"><s:text name="title.pageManagement" /></a></h1>
<h2><s:text name="title.configPage" /></h2>

<s:include value="/WEB-INF/apsadmin/jsp/portal/include/pageInfo.jsp" />
<s:include value="/WEB-INF/apsadmin/jsp/portal/include/frameInfo.jsp" />
<h3>Showlet: <s:property value="%{getTitle(showlet.type.code, showlet.type.titles)}" /></h3>
<s:form action="saveConfigSimpleParameter">
	<wpsf:hidden name="pageCode" />
	<wpsf:hidden name="frame" />
	<wpsf:hidden name="showletTypeCode" value="%{showlet.type.code}" />

<s:iterator value="showlet.type.typeParameters" id="showletParam" >

<s:property value="#showletParam.name" /> - <wpsf:textfield name="%{#showletParam.name}" value="%{showlet.config[#showletParam.name]}" /> - <s:property value="#showletParam.descr" />
<br />
</s:iterator>

<wpsf:submit cssClass="button" />

</s:form>