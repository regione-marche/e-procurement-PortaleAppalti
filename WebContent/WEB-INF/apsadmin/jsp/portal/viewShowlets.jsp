<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="wpsa" uri="apsadmin-core.tld" %>

<h1><s:text name="title.showletManagement" /></h1>

<s:set var="pluginTitleCheck" value="false" ></s:set>
<s:set var="showletFlavours" value="showletFlavours" ></s:set>

<s:iterator var="showletFlavour" value="#showletFlavours">
<s:set var="firstType" value="%{#showletFlavour.get(0)}"></s:set>

<s:if test="%{#firstType.groupCode == 'stockShowletCode'}">
	<h2><span lang="en"><s:text name="title.showletManagement.showlets.stock" /></span></h2>
</s:if>

<s:elseif test="%{#firstType.groupCode == 'customShowletCode'}">
	<h2><span lang="en"><s:text name="title.showletManagement.showlets.custom" /></span></h2>
</s:elseif>

<s:elseif test="%{#firstType.groupCode == 'userShowletCode'}">
	<h2><span lang="en"><s:text name="title.showletManagement.showlets.user" /></span></h2>
</s:elseif>

<s:else>
	<s:if test="!#pluginTitleCheck">
		<h2><span lang="en"><s:text name="title.showletManagement.showlets.plugin" /></span></h2>
	</s:if>
	<s:set var="pluginTitleCheck" value="true" ></s:set>

	<wpsa:set var="pluginPropertyName" value="%{getText(#firstType.groupCode + '.name')}" />	
	<wpsa:set var="pluginPropertyCode" value="%{getText(#firstType.groupCode + '.code')}" />		
	
	<h3>(<s:text name="#pluginPropertyCode" />) <s:text name="#pluginPropertyName" /></h3>
</s:else>

<ul>
<s:iterator var="showletType" value="#showletFlavour" >
	<li><s:property value="#showletType.value" /></li>
</s:iterator>
</ul>

</s:iterator>