<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>
<h1><a href="<s:url action="viewTree" namespace="/do/Page" />" title="<s:text name="note.goToSomewhere" />: <s:text name="title.pageManagement" />"><s:text name="title.pageManagement" /></a></h1>
<h2><s:text name="title.configPage" /></h2>
<s:include value="/WEB-INF/apsadmin/jsp/portal/include/pageInfo.jsp" />
<s:include value="/WEB-INF/apsadmin/jsp/portal/include/frameInfo.jsp" />

<s:set var="showletType" value="%{getShowletType(showletTypeCode)}"></s:set>
<h3>Showlet: <s:property value="%{getTitle(#showletType.code, #showletType.titles)}" /></h3>
<h4><s:text name="title.filterAdd" /></h4>

<s:form namespace="/do/jacms/Page/SpecialShowlet/ListViewer">
<p class="noscreen">
	<wpsf:hidden name="pageCode" />
	<wpsf:hidden name="frame" />
	<wpsf:hidden name="showletTypeCode" />
</p>

	<s:if test="hasFieldErrors()">
<div class="message message_error">
<h5><s:text name="message.title.FieldErrors" /></h5>	
	<ul>
	<s:iterator value="fieldErrors">
		<s:iterator value="value">
		<li><s:property escape="false" /></li>
		</s:iterator>
	</s:iterator>
	</ul>
</div>
	</s:if>

<p class="noscreen">
	<wpsf:hidden name="contentType" />
	<wpsf:hidden name="category" />
	<wpsf:hidden name="filters" />
	<wpsf:hidden name="modelId" />
	<wpsf:hidden name="maxElemForItem" />
</p>

<s:if test="filterTypeId < 0">

<p>
	<label for="filterKey"><s:text name="label.type"/>:</label><br />
	<wpsf:select name="filterKey" id="filterKey" list="filterTypes" listKey="key" listValue="value" cssClass="text" />
</p>
<p>
	<wpsf:submit action="setFilterType" value="%{getText('label.continue')}" cssClass="button" />
</p>

</s:if>
<s:else>
<p class="noscreen">
	<wpsf:hidden name="filterKey" />
	<wpsf:hidden name="filterTypeId" />
	<wpsf:hidden name="attributeFilter" value="%{filterTypeId>0 && filterTypeId<5}"/>
</p>

<s:set name="filterDescription" value="%{filterKey}" />
<s:if test="%{filterKey == 'created'}">
	<s:set name="filterDescription" value="%{getText('label.creationDate')}" />
</s:if>
<s:elseif test="%{filterKey == 'modified'}">
	<s:set name="filterDescription" value="%{getText('label.lastModifyDate')}" />			
</s:elseif>

<p><s:text name="note.filterTypes.intro" />: <span class="important"><s:property value="filterDescription" /></span><em> (
<s:if test="filterTypeId == 0">
	<s:text name="note.filterTypes.metadata" /> )</em></p>
</s:if>

<s:elseif test="filterTypeId==1">
<%-- INIZIO FILTRO PER ATTRIBUTO TIPO STRINGA --%>
	<s:text name="note.filterTypes.TextAttribute" /> )</em></p>
<p>
	<label for="filterOptionId"><s:text name="label.option"/>:</label><br />
	<wpsf:select id="filterOptionId" name="filterOptionId" headerKey="-1" headerValue="%{getText('label.none')}" list="#{1:getText('label.valueLikeOptionFilter'),2:getText('label.rangeOptionFilter')}" disabled="filterOptionId>-1" cssClass="text" />
	<s:if test="filterOptionId>-1"><wpsf:hidden name="filterOptionId" /></s:if>
	<s:else><wpsf:submit action="setFilterOption" value="%{getText('label.confirm')}" cssClass="button" /></s:else>	
</p>

<s:if test="filterOptionId==1">
<p>
	<label for="stringValue"><s:text name="label.filterValue" />:</label><br />
	<wpsf:textfield name="stringValue" id="stringValue" cssClass="text" />
</p>
<p>
	<wpsf:checkbox name="like" id="like" cssClass="text" /> <label for="like"><s:text name="label.filterValue.isLike" /></label>
</p>
</s:if>

<s:if test="filterOptionId==2">
<p>
	<label for="stringStart"><s:text name="label.filterFrom" />:</label><br />
	<wpsf:textfield name="stringStart" id="stringStart" cssClass="text" />
</p>
<p>
	<label for="stringEnd"><s:text name="label.filterTo" />:</label><br />
	<wpsf:textfield name="stringEnd" id="stringEnd" cssClass="text" />
</p>
</s:if>

<%-- FINE FILTRO PER ATTRIBUTO TIPO STRINGA --%>
</s:elseif>

<s:elseif test="filterTypeId==2">
<%-- INIZIO FILTRO PER ATTRIBUTO TIPO NUMERO --%>
	<s:text name="note.filterTypes.NumberAttribute" /> )</em></p>
<p>
	<label for="filterOptionId"><s:text name="label.option"/>:</label><br />
	<wpsf:select name="filterOptionId" id="filterOptionId" headerKey="-1" headerValue="%{getText('label.none')}" list="#{1:getText('label.valueOptionFilter'),2:getText('label.rangeOptionFilter')}" disabled="filterOptionId>-1" cssClass="text" />
	<s:if test="filterOptionId>-1"><wpsf:hidden name="filterOptionId" /></s:if>
	<s:else><wpsf:submit action="setFilterOption" value="%{getText('label.confirm')}" cssClass="button" /></s:else>	
</p>

<s:if test="filterOptionId==1">
<p>
	<label for="numberValue"><s:text name="label.filterValue.exact" />:</label><br />
	<wpsf:textfield name="numberValue" id="numberValue" cssClass="text" />
</p>
</s:if>

<s:if test="filterOptionId==2">
<p>
	<label for="numberStart"><s:text name="label.filterFrom" />:</label><br />
	<wpsf:textfield name="numberStart" id="numberStart" cssClass="text" />
</p>
<p>
	<label for="numberEnd"><s:text name="label.filterTo" />:</label><br />
	<wpsf:textfield name="numberEnd" id="numberEnd" cssClass="text" />
</p>
</s:if>

<%-- FINE FILTRO PER ATTRIBUTO TIPO NUMERO --%>
</s:elseif>

<s:elseif test="filterTypeId==3">
<%-- INIZIO FILTRO PER ATTRIBUTO TIPO BULEANO --%>
	<s:text name="note.filterTypes.BooleanAttribute" /> )</em></p>
<p>
	<input type="radio" name="booleanValue" id="booleanValue_true" value="true" /><label for="booleanValue_true"><s:text name="label.yes" /></label><br />
	<input type="radio" name="booleanValue" id="booleanValue_false" value="false" /><label for="booleanValue_false"><s:text name="label.no" /></label><br />
	<input type="radio" name="booleanValue" id="booleanValue_none" checked="checked" value="" /><label for="booleanValue_none"><s:text name="label.all" /></label>		
</p>

<%-- FINE FILTRO PER ATTRIBUTO TIPO BULEANO --%>
</s:elseif>

<s:elseif test="filterTypeId==4">
<%-- INIZIO FILTRO PER ATTRIBUTO TIPO DATA --%>
	<s:text name="note.filterTypes.DateAttribute" /> )</em></p>
<p>
	<label for="filterOptionId"><s:text name="label.option"/>:</label><br />
	<wpsf:select name="filterOptionId" id="filterOptionId" headerKey="-1" headerValue="%{getText('label.none')}" list="#{1:getText('label.valueOptionFilter'),2:getText('label.rangeOptionFilter')}" disabled="filterOptionId>-1" cssClass="text" />
	<s:if test="filterOptionId>-1"><wpsf:hidden name="filterOptionId" /></s:if>
	<s:else><wpsf:submit action="setFilterOption" value="%{getText('label.confirm')}" cssClass="button" /></s:else>	
</p>

<s:if test="filterOptionId==1">
<p>
	<input type="radio" name="dateValueType" id="dateValueType_today" value="2" <s:if test="(2 == dateValueType)">checked="checked"</s:if> /> <label for="dateValueType_today"><s:text name="label.today" /></label><br />
	<input type="radio" name="dateValueType" id="dateValueType_chosen" value="3" <s:if test="(3 == dateValueType)">checked="checked"</s:if> /> <label for="dateValueType_chosen"><s:text name="label.chosenDate" /></label>, 
	<label for="dateValue_cal"><s:text name="label.filterValue.exact" />:</label> <wpsf:textfield name="dateValue" id="dateValue_cal" cssClass="text" />
</p>
</s:if>

<s:if test="filterOptionId==2">
<fieldset><legend><s:text name="label.filterFrom" /></legend>
<p>
	<input type="radio" name="dateStartType" id="dateStartType_none" value="1" <s:if test="(1 == dateStartType)">checked="checked"</s:if> /> <label for="dateStartType_none"><s:text name="label.none" /></label><br />
	<input type="radio" name="dateStartType" id="dateStartType_today" value="2" <s:if test="(2 == dateStartType)">checked="checked"</s:if> /> <label for="dateStartType_today"><s:text name="label.today" /></label><br />
	<input type="radio" name="dateStartType" id="dateStartType_chosen" value="3" <s:if test="(3 == dateStartType)">checked="checked"</s:if> /> <label for="dateStartType_chosen"><s:text name="label.chosenDate" /></label>, 
	<label for="dateStart_cal"><s:text name="label.filterValue.exact" />:</label> <wpsf:textfield name="dateStart" id="dateStart_cal" cssClass="text" />
</p>
</fieldset>
<fieldset><legend><s:text name="label.filterTo" /></legend>
<p>
	<input type="radio" name="dateEndType" id="dateEndType_none" value="1" <s:if test="(1 == dateEndType)">checked="checked"</s:if> /> <label for="dateEndType_none"><s:text name="label.none" /></label><br />
	<input type="radio" name="dateEndType" id="dateEndType_today" value="2" <s:if test="(2 == dateEndType)">checked="checked"</s:if> /> <label for="dateEndType_today"><s:text name="label.today" /></label><br />
	<input type="radio" name="dateEndType" id="dateEndType_chosen" value="3" <s:if test="(3 == dateEndType)">checked="checked"</s:if> /> <label for="dateEndType_chosen"><s:text name="label.chosenDate" /></label>, 
	<label for="dateEnd_cal"><s:text name="label.filterValue.exact" />:</label> <wpsf:textfield name="dateEnd" id="dateEnd_cal" cssClass="text" />
</p>
</fieldset>
</s:if>

<%-- FINE FILTRO PER ATTRIBUTO TIPO DATA --%>
</s:elseif>

<fieldset><legend><s:text name="label.order" /></legend>
<p>
	<input type="radio" name="order" id="order_none" checked="checked" value="" /><label for="order_none"><s:text name="label.none" /></label><br />
	<input type="radio" name="order" id="order_asc" value="ASC" <s:if test="('ASC' == order)">checked="checked"</s:if> /><label for="order_asc"><s:text name="label.order.ascendant" /></label><br />
	<input type="radio" name="order" id="order_desc" value="DESC" <s:if test="('DESC' == order)">checked="checked"</s:if> /><label for="order_desc"><s:text name="label.order.descendant" /></label>
</p>
</fieldset>

<s:set name="saveFilterActionName"><s:if test="filterTypeId == 0">saveFilter</s:if><s:elseif test="filterTypeId == 1">saveTextFilter</s:elseif><s:elseif test="filterTypeId == 2">saveNumberFilter</s:elseif><s:elseif test="filterTypeId == 3">saveBooleanFilter</s:elseif><s:elseif test="filterTypeId == 4">saveDateFilter</s:elseif></s:set>

<p class="buttons">
	<wpsf:submit action="%{#saveFilterActionName}" value="%{getText('label.save')}" cssClass="button" />
</p>

</s:else>

</s:form>