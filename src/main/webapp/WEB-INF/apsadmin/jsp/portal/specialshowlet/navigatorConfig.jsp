<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wpsa" uri="apsadmin-core.tld" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>
<h1><a href="<s:url action="viewTree" namespace="/do/Page" />" title="<s:text name="note.goToSomewhere" />: <s:text name="title.pageManagement" />"><s:text name="title.pageManagement" /></a></h1>
<h2><s:text name="title.configPage" /></h2>

<s:include value="/WEB-INF/apsadmin/jsp/portal/include/pageInfo.jsp" />
<s:include value="/WEB-INF/apsadmin/jsp/portal/include/frameInfo.jsp" />

<h3>Showlet: <s:property value="%{getTitle(showlet.type.code, showlet.type.titles)}" /></h3>

<s:url action="saveNavigatorConfig" namespace="do/Page/SpecialShowlet/Navigator" id="formAction" anchor="expressions" />
<s:form action="%{'/' + #formAction}">

	<s:if test="hasActionErrors()">
<div class="message message_error">
<h4><s:text name="message.title.ActionErrors" /></h4>	
	<ul>
	<s:iterator value="actionErrors">
		<li><s:property escape="false" /></li>
	</s:iterator>
	</ul>
</div>
	</s:if>
	<s:if test="hasFieldErrors()">
<div class="message message_error">
<h4><s:text name="message.title.FieldErrors" /></h4>	
	<ul>
	<s:iterator value="fieldErrors">
		<s:iterator value="value">
		<li><s:property/></li>
		</s:iterator>
	</s:iterator>
	</ul>
</div>
	</s:if>

<p class="noscreen">
	<wpsf:hidden name="pageCode" />
	<wpsf:hidden name="frame" />
	<wpsf:hidden name="showletTypeCode" value="%{showlet.type.code}" />
	<wpsf:hidden name="navSpec" />
</p>

<h4 id="expressions"><s:text name="Showlet.configNavigator.expressionList" /></h4>

<s:if test="expressions.size != 0">

<table class="generic" summary="<s:text name="note.page.navigator.summary" />">
<tr>
	<th><abbr title="<s:text name="label.number" />">N</abbr></th>
	<th><s:text name="Showlet.configNavigator.navSpec" /></th> 
	<th><s:text name="Showlet.configNavigator.operator" /></th>
	<th class="icon" colspan="3"><abbr title="<s:text name="label.actions" />">&ndash;</abbr></th> 
</tr>

<s:iterator value="expressions" id="expression" status="rowstatus">
<tr>
	<td><s:property value="#rowstatus.index + 1"/></td>
	<td>
		<s:if test="#expression.specId == 1"><s:text name="Showlet.configNavigator.currentPage" /></s:if>
		<s:elseif test="#expression.specId == 2"><s:text name="Showlet.configNavigator.parentCurrent" /></s:elseif>
		<s:elseif test="#expression.specId == 3"><s:text name="Showlet.configNavigator.parentFromCurrent" />: <s:property value="specSuperLevel" /></s:elseif>
		<s:elseif test="#expression.specId == 4"><s:text name="Showlet.configNavigator.parentFromRoot" />: <s:property value="specAbsLevel" /></s:elseif>
		<s:elseif test="#expression.specId == 5"><s:text name="Showlet.configNavigator.specifiedPage" />: <s:property value="specCode" /></s:elseif>
		<s:else>ERRORE</s:else>
	</td>
	<td>
		<s:if test="#expression.operatorId == -1"><s:text name="Showlet.configNavigator.none" /></s:if>
		<s:elseif test="#expression.operatorId == 1"><s:text name="Showlet.configNavigator.allChildren" /></s:elseif>
		<s:elseif test="#expression.operatorId == 2"><s:text name="Showlet.configNavigator.allNodes" /></s:elseif>
		<s:elseif test="#expression.operatorId == 3"><abbr title="<s:text name="Showlet.configNavigator.levelOfNodesTothisLevel" />"><s:text name="Showlet.configNavigator.nodesTothisLevel" /></abbr>: <s:property value="operatorSubtreeLevel" /></s:elseif>
		<s:else>ERRORE</s:else>
	</td>
	<td class="icon">
		<wpsa:actionParam action="moveExpression" var="actionName" >
			<wpsa:actionSubParam name="expressionIndex" value="%{#rowstatus.index}" />
			<wpsa:actionSubParam name="movement" value="UP" />
		</wpsa:actionParam>
		<s:set name="iconImagePath" id="iconImagePath"><wp:resourceURL/>administration/img/icons/go-up.png</s:set>
		<wpsf:submit action="%{#actionName}" type="image" src="%{#iconImagePath}" value="%{getText('label.moveUp')}" title="%{getText('label.moveUp')}" />
	</td>
	<td class="icon">	
		<wpsa:actionParam action="moveExpression" var="actionName" >
			<wpsa:actionSubParam name="expressionIndex" value="%{#rowstatus.index}" />
			<wpsa:actionSubParam name="movement" value="DOWN" />
		</wpsa:actionParam>
		<s:set name="iconImagePath" id="iconImagePath"><wp:resourceURL/>administration/img/icons/go-down.png</s:set>
		<wpsf:submit action="%{#actionName}" type="image" src="%{#iconImagePath}" value="%{getText('label.moveDown')}" title="%{getText('label.moveDown')}" />
	</td>
	<td class="icon">	
		<wpsa:actionParam action="removeExpression" var="actionName" >
			<wpsa:actionSubParam name="expressionIndex" value="%{#rowstatus.index}" />
		</wpsa:actionParam>
		<s:set name="iconImagePath" id="iconImagePath"><wp:resourceURL/>administration/img/icons/list-remove.png</s:set>
		<wpsf:submit action="%{#actionName}" type="image" src="%{#iconImagePath}" value="%{getText('label.remove')}" title="%{getText('label.remove')}" />
	</td>
</tr>
</s:iterator>

</table>
</s:if>
<s:else>
<p><s:text name="note.noRuleSet" /></p>
</s:else>

<!-- // Lista ESPRESSIONI -->	


<h4><s:text name="Showlet.configNavigator.addExpression" /></h4>

<fieldset>
<legend><s:text name="Showlet.configNavigator.navSpec" /></legend>

<p>
	<input type="radio" name="specId" id="specId1" value="1" /> <label for="specId1"><s:text name="Showlet.configNavigator.currentPage" /></label>
</p>
<p>
	<input type="radio" name="specId" id="specId2" value="2" /> <label for="specId2"><s:text name="Showlet.configNavigator.parentCurrent" /></label>
</p>
<p>
	<input type="radio" name="specId" id="specId3" value="3" /> <label for="specId3"><s:text name="Showlet.configNavigator.parentFromCurrent" />:</label> <wpsf:select name="specSuperLevel" headerKey="-1" list="#{1:1,2:2,3:3,4:4,5:5,6:6,7:7,8:8,9:9,10:10}" />
</p>
<p>
	<input type="radio" name="specId" id="specId4" value="4" /> <label for="specId4"><s:text name="Showlet.configNavigator.parentFromRoot" />:</label> <wpsf:select name="specAbsLevel" headerKey="-1" list="#{1:1,2:2,3:3,4:4,5:5,6:6,7:7,8:8,9:9,10:10}" />
</p>
<p>
	<input type="radio" name="specId" id="specId5" value="5" /> <label for="specId5"><s:text name="Showlet.configNavigator.specifiedPage" />:</label> <select name="specCode">
	<s:iterator id="page" value="freePages">
		<option <s:if test="systemParams[#paramName] == #page.code">selected="selected"</s:if> 
			value="<s:property value="#page.code"/>"><s:if test="!#page.showable">[i] </s:if><s:property value="#page.titles[currentLang.code]"/></option>
	</s:iterator>
</select>
</p>
</fieldset>

<fieldset>
<legend><s:text name="Showlet.configNavigator.operator" /></legend>
<p>
	<label for="operatorId"><s:text name="Showlet.configNavigator.operatorType" />:</label>
	<select name="operatorId" id="operatorId">
		<option value="0"><s:text name="Showlet.configNavigator.none" /></option>
		<option value="1"><s:text name="Showlet.configNavigator.allChildren" /></option>
		<option value="2"><s:text name="Showlet.configNavigator.allNodes" /></option>
		<option value="3"><s:text name="Showlet.configNavigator.nodesTothisLevel" /></option>
	</select>
</p>
<p>
	<label for="operatorSubtreeLevel"><s:text name="Showlet.configNavigator.levelOfNodesTothisLevel" />:</label>
	<wpsf:select name="operatorSubtreeLevel" id="operatorSubtreeLevel" headerKey="-1" list="#{1:1,2:2,3:3,4:4,5:5,6:6,7:7,8:8,9:9,10:10}" />
</p>
</fieldset>

<p><wpsf:submit action="addExpression" value="%{getText('label.add')}" cssClass="button" /></p>

<!-- FINE AGGIUNGI NUOVA ESPRESSIONE -->
<p class="buttons"><wpsf:submit action="saveNavigatorConfig" value="%{getText('label.save')}" cssClass="button" /></p>

</s:form>