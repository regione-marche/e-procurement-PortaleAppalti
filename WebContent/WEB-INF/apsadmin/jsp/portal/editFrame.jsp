<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>
<%@ taglib prefix="wpsa" uri="apsadmin-core.tld" %>

<h1><a href="<s:url action="viewTree" namespace="/do/Page" />" title="<s:text name="note.goToSomewhere" />: <s:text name="title.pageManagement" />"><s:text name="title.pageManagement" /></a></h1>
<p class="noscreen"><a href="#editFrame"><s:text name="note.goToEditFrame" /></a></p>
<h2><s:text name="title.configPage" /></h2>

<s:include value="/WEB-INF/apsadmin/jsp/portal/include/pageInfo.jsp" />

<s:if test="showlet != null">

<s:include value="/WEB-INF/apsadmin/jsp/portal/include/frameInfo.jsp" />
<h3>Showlet: <s:property value="%{getTitle(showlet.type.code, showlet.type.titles)}" /></h3>

<p><s:text name="note.editFrame.noConfigNeeded" /></p>
</s:if>
<s:else>

<h2 id="editFrame"><s:text name="title.editFrame" />: <s:property value="frame" /></h2>

<%-- Error message handling --%>
	<s:if test="hasActionErrors()">
<div class="message message_error">
<h3><s:text name="message.title.ActionErrors" /></h3>	
	<ul>
	<s:iterator value="actionErrors">
		<li><s:property escape="false" /></li>
	</s:iterator>
	</ul>
</div>
	</s:if>

<p><s:text name="note.editFrame.chooseAShowlet" /></p>
<s:form action="joinShowlet">
	<p class="noscreen">
		<wpsf:hidden name="pageCode" />
		<wpsf:hidden name="frame" />
	</p>
	<p>
	<label for="showletCode"><s:text name="name.showlet" />:</label><br />
	<select name="showletTypeCode" tabindex="<wpsa:counter />" id="showletCode">
	<s:iterator var="showletFlavour" value="showletFlavours">
				
		<wpsa:set var="tmpShowletType">POBA</wpsa:set>

		<s:iterator var="showletType" value="#showletFlavour" >
					
			<s:if test="#showletType.groupCode != #tmpShowletType">
			
				<s:if test="#showletType.groupCode == 'stockShowletCode'">
					<wpsa:set var="optgroupLabel"><s:text name="title.showletManagement.showlets.stock" /></wpsa:set>
				</s:if>
				<s:elseif test="#showletType.groupCode == 'customShowletCode'">
					<wpsa:set var="optgroupLabel"><s:text name="title.showletManagement.showlets.custom" /></wpsa:set>
				</s:elseif>
				<s:elseif test="#showletType.groupCode == 'userShowletCode'">
					<wpsa:set var="optgroupLabel"><s:text name="title.showletManagement.showlets.user" /></wpsa:set>
				</s:elseif>
				<s:else>
					<wpsa:set var="pluginPropertyName" value="%{getText(#showletType.groupCode + '.name')}" />		
					<wpsa:set var="pluginPropertyCode" value="%{getText(#showletType.groupCode + '.code')}" />					
					<wpsa:set var="optgroupLabel">(<s:text name="#pluginPropertyCode" />) <s:text name="#pluginPropertyName" /></wpsa:set>
				</s:else>
						
			<optgroup label="<s:property value="#optgroupLabel" />">
			</s:if>
				<option value="<s:property value="#showletType.key" />"><s:property value="#showletType.value" /></option>
		
			<wpsa:set var="tmpShowletType"><s:property value="#showletType.groupCode" /></wpsa:set>
		
		</s:iterator>
			</optgroup>	
	</s:iterator>
	</select>
	</p>
	<p><wpsf:submit value="%{getText('label.confirm')}" cssClass="button" /></p>
</s:form>

</s:else>