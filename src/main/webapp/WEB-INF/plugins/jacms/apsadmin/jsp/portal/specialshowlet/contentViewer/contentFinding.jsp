<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wpsa" uri="apsadmin-core.tld" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>

<h1><a href="<s:url action="viewTree" namespace="/do/Page" />" title="<s:text name="note.goToSomewhere" />: <s:text name="title.pageManagement" />"><s:text name="title.pageManagement" /></a></h1>
<h2><s:text name="title.configPage" /></h2>
<h3><s:text name="title.configContentViewer.settings" /></h3>

<s:form action="searchContents" >

<p>
	<wpsf:hidden name="pageCode" />
	<wpsf:hidden name="frame" />
	<wpsf:hidden name="showletTypeCode" />
	<wpsf:hidden name="modelId" />
</p>

	<s:if test="hasFieldErrors()">
<div class="message message_error">
<h3><s:text name="message.title.FieldErrors" /></h3>	
	<ul>
	<s:iterator value="fieldErrors">
		<s:iterator value="value">
		<li><s:property escape="false" /></li>
		</s:iterator>
	</s:iterator>
	</ul>
</div>
	</s:if>
<fieldset><legend><s:text name="title.search" /></legend>
<p>
	<label for="text"><s:text name="label.description"/>:</label><br />
	<wpsf:textfield name="text" id="text" cssClass="text" />
</p>

<p>
	<label for="contentIdToken"><s:text name="label.code"/>:</label><br />
	<wpsf:textfield name="contentIdToken" id="contentIdToken" cssClass="text" />
</p>

<p>
	<label for="contentType"><s:text name="label.type"/>:</label><br />
	<wpsf:select name="contentType" id="contentType" 
		list="contentTypes" listKey="code" listValue="descr" 
		headerKey="" headerValue="%{getText('label.all')}" cssClass="text"></wpsf:select>
</p>

<p>
	<label for="state"><s:text name="label.state"/>:</label><br />
	<wpsf:select name="state" id="state" list="avalaibleStatus" 
		headerKey="" headerValue="%{getText('label.all')}" cssClass="text" listKey="key" listValue="%{getText(value)}" />
</p>

<p><wpsf:submit value="%{getText('label.search')}" cssClass="button" /></p>
</fieldset>

<wpsa:subset source="contents" count="10" objectName="groupContent" advanced="true" offset="5">
<s:set name="group" value="#groupContent" />

<div class="pager">
	<s:include value="/WEB-INF/apsadmin/jsp/common/inc/pagerInfo.jsp" />
	<s:include value="/WEB-INF/apsadmin/jsp/common/inc/pager_formBlock.jsp" />
</div>

<p class="noscreen">
	<wpsf:hidden name="lastGroupBy" />
	<wpsf:hidden name="lastOrder" />
</p>

<table class="generic" summary="<s:text name="note.page.contentViewer.summary" />">
<caption><s:text name="title.contentList" /></caption>
<tr>
	<th><a href="
	<s:url action="changeContentListOrder">
		<s:param name="text">
			<s:property value="#request.text"/>
		</s:param>
		<s:param name="contentIdToken">
			<s:property value="#request.contentIdToken"/>
		</s:param>
		<s:param name="contentType">
			<s:property value="#request.contentType"/>
		</s:param>
		<s:param name="state">
			<s:property value="#request.state"/>
		</s:param>
		<s:param name="pagerItem">
			<s:property value="#groupContent.currItem"/>
		</s:param>
		<s:param name="pageCode">
			<s:property value="#request.pageCode"/>
		</s:param>
		<s:param name="frame">
			<s:property value="#request.frame"/>
		</s:param>
		<s:param name="modelId">
			<s:property value="#request.modelId"/>
		</s:param>
		<s:param name="showletTypeCode"><s:property value="showletTypeCode" /></s:param>
		<s:param name="lastGroupBy"><s:property value="lastGroupBy"/></s:param>
		<s:param name="lastOrder"><s:property value="lastOrder"/></s:param>
		<s:param name="groupBy">descr</s:param>
	</s:url>
"><s:text name="label.description" /></a></th>
	<th><a href="
	<s:url action="changeContentListOrder">
		<s:param name="text">
			<s:property value="#request.text"/>
		</s:param>
		<s:param name="contentIdToken">
			<s:property value="#request.contentIdToken"/>
		</s:param>
		<s:param name="contentType">
			<s:property value="#request.contentType"/>
		</s:param>
		<s:param name="state">
			<s:property value="#request.state"/>
		</s:param>
		<s:param name="pagerItem">
			<s:property value="#groupContent.currItem"/>
		</s:param>
		<s:param name="pageCode">
			<s:property value="#request.pageCode"/>
		</s:param>
		<s:param name="frame">
			<s:property value="#request.frame"/>
		</s:param>
		<s:param name="modelId">
			<s:property value="#request.modelId"/>
		</s:param>
		<s:param name="showletTypeCode"><s:property value="showletTypeCode" /></s:param>
		<s:param name="lastGroupBy"><s:property value="lastGroupBy"/></s:param>
		<s:param name="lastOrder"><s:property value="lastOrder"/></s:param>
		<s:param name="groupBy">code</s:param>
	</s:url>
"><s:text name="label.code" /></a></th>
	<th><s:text name="label.group" /></th>
	<th><a href="
	<s:url action="changeContentListOrder">
		<s:param name="text">
			<s:property value="#request.text"/>
		</s:param>
		<s:param name="contentIdToken">
			<s:property value="#request.contentIdToken"/>
		</s:param>		
		<s:param name="contentType">
			<s:property value="#request.contentType"/>
		</s:param>
		<s:param name="state">
			<s:property value="#request.state"/>
		</s:param>
		<s:param name="pagerItem">
			<s:property value="#groupContent.currItem"/>
		</s:param>
		<s:param name="pageCode">
			<s:property value="#request.pageCode"/>
		</s:param>
		<s:param name="frame">
			<s:property value="#request.frame"/>
		</s:param>
		<s:param name="modelId">
			<s:property value="#request.modelId"/>
		</s:param>
		<s:param name="showletTypeCode"><s:property value="showletTypeCode" /></s:param>
		<s:param name="lastGroupBy"><s:property value="lastGroupBy"/></s:param>
		<s:param name="lastOrder"><s:property value="lastOrder"/></s:param>
		<s:param name="groupBy">created</s:param>
	</s:url>
"><s:text name="label.creationDate" /></a></th>
	<th><a href="
	<s:url action="changeContentListOrder">
		<s:param name="text">
			<s:property value="#request.text"/>
		</s:param>
		<s:param name="contentIdToken">
			<s:property value="#request.contentIdToken"/>
		</s:param>		
		<s:param name="contentType">
			<s:property value="#request.contentType"/>
		</s:param>
		<s:param name="state">
			<s:property value="#request.state"/>
		</s:param>
		<s:param name="pagerItem">
			<s:property value="#groupContent.currItem"/>
		</s:param>
		<s:param name="pageCode">
			<s:property value="#request.pageCode"/>
		</s:param>
		<s:param name="frame">
			<s:property value="#request.frame"/>
		</s:param>
		<s:param name="modelId">
			<s:property value="#request.modelId"/>
		</s:param>
		<s:param name="showletTypeCode"><s:property value="showletTypeCode" /></s:param>
		<s:param name="lastGroupBy"><s:property value="lastGroupBy"/></s:param>
		<s:param name="lastOrder"><s:property value="lastOrder"/></s:param>
		<s:param name="groupBy">lastModified</s:param>
	</s:url>
"><s:text name="label.lastEdit" /></a></th>
</tr>
<s:iterator id="contentId">
<s:set name="content" value="%{getContentVo(#contentId)}"></s:set>
<tr>
<td><input type="radio" name="contentId" id="contentId_<s:property value="#content.id"/>" value="<s:property value="#content.id"/>" />
<label for="contentId_<s:property value="#content.id"/>"><s:property value="#content.descr" /></label></td>
<td><span class="monospace"><s:property value="#content.id" /></span></td>
<td><s:property value="groupsMap[#content.mainGroupCode].descr" /></td>
<td><s:date name="#content.create" format="dd/MM/yyyy HH:mm" /></td>
<td><s:date name="#content.modify" format="dd/MM/yyyy HH:mm" /></td>
</tr>
</s:iterator>
</table>

<div class="pager">
	<s:include value="/WEB-INF/apsadmin/jsp/common/inc/pager_formBlock.jsp" />
</div>

</wpsa:subset>

<p>
	<wpsf:submit action="joinContent" value="%{getText('label.confirm')}" cssClass="button" />
</p>
</s:form>