<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wpsa" uri="apsadmin-core.tld" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>

<h1><a href="<s:url action="viewTree" namespace="/do/Page" />" title="<s:text name="note.goToSomewhere" />: <s:text name="title.pageManagement" />"><s:text name="title.pageManagement" /></a></h1>

<s:if test="getStrutsAction() == 1">
	<h2><s:text name="title.newPage" /></h2>
</s:if>
<s:elseif test="getStrutsAction() == 2">
	<h2><s:text name="title.editPage" /></h2>
</s:elseif>
<s:elseif test="getStrutsAction() == 3">
	<h2><s:text name="title.pastePage" /></h2>
	<%-- INDICAZIONE PAGINA DA COPIARE... --%>
</s:elseif>

<p><s:text name="note.youAreHere" />: 

<s:if test="getStrutsAction() == 2"><s:set value="%{getBreadCrumbsTargets(pageCode)}" name="breadCrumbsTargets" ></s:set></s:if>
<s:else><s:set value="%{getBreadCrumbsTargets(parentPageCode)}" name="breadCrumbsTargets" ></s:set></s:else>
<s:iterator value="#breadCrumbsTargets" id="target" status="rowstatus">
<s:if test="%{#rowstatus.index != 0}"> &raquo; </s:if>
<s:if test="%{#rowstatus.index == (#breadCrumbsTargets.size()-1) && strutsAction == 2}">
<s:property value="#target.titles[currentLang.code]" />
</s:if>
<s:else>
<a href="<s:url namespace="/do/Page" action="viewTree" ><s:param name="selectedNode"><s:property value="#target.code" /></s:param></s:url>" title="<s:text name="note.goToSomewhere" />: <s:property value="#target.titles[currentLang.code]" />"><s:property value="#target.titles[currentLang.code]" /></a>
</s:else>
</s:iterator>

</p>

<s:form action="save">
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
<p class="noscreen">
	<wpsf:hidden name="selectedNode" />
	<wpsf:hidden name="strutsAction" />
	<wpsf:hidden name="copyPageCode" />
	<wpsf:hidden name="parentPageCode" />
	<wpsf:hidden name="groupSelectLock" />
	<s:if test="getStrutsAction() == 2"><wpsf:hidden name="pageCode" /></s:if>
</p>
<h3><s:text name="title.pageInfo" /></h3>
<p>
	<label for="pageCode"><s:text name="name.pageCode" />:</label><br />
	<s:if test="getStrutsAction() != 2">
		<wpsf:textfield name="pageCode" id="pageCode" cssClass="text" />
	</s:if>
	<s:if test="getStrutsAction() == 2">
		<wpsf:textfield name="pageCode" id="pageCode" disabled="true" cssClass="text" />
	</s:if>	
</p>
<s:iterator value="langs">
<p>
	<label for="lang<s:property value="code" />"><span class="monospace">(<s:property value="code" />)</span> <s:text name="name.pageTitle" />:</label><br />
	<wpsf:textfield name="%{'lang'+code}" id="%{'lang'+code}" value="%{titles.get(code)}" cssClass="text" />
</p>
</s:iterator>

<s:if test="getStrutsAction() == 3">
<p class="noscreen">
	<wpsf:hidden name="group" />
	<wpsf:hidden name="model" />
	<wpsf:hidden name="defaultShowlet" />
	<wpsf:hidden name="showable" />
	<wpsf:hidden name="selectedNode" />
</p>
</s:if>

<s:if test="getStrutsAction() != 3">

<p>
<label for="group"><s:text name="name.pageGroup" />:</label><br />
<wpsf:select value="%{getGroup()}" name="group" id="group" 
	list="groups" listKey="name" listValue="descr" 
	headerKey="" headerValue="%{getText('label.select')}" disabled="%{groupSelectLock}" cssClass="text"></wpsf:select>
<s:if test="groupSelectLock"><wpsf:hidden name="group" /></s:if>
</p>

<p>
<label for="model"><s:text name="name.pageModel" />:</label><br />
<select name="model" id="model" class="text">
<s:iterator value="pageModels">
	<option <s:if test="model == code">selected="selected"</s:if> value="<s:property value="code" />"><s:property value="descr" /></option>
</s:iterator>
</select>
</p>

<p><wpsf:checkbox name="defaultShowlet" id="defaultShowlet" /> <label for="defaultShowlet"><s:text name="name.hasDefaultShowlets" /></label></p>
<p><wpsf:checkbox name="viewerPage" id="viewerPage" /> <label for="viewerPage"><s:text name="name.isViewerPage" /></label></p>
<p><wpsf:checkbox name="showable" id="showable" /> <label for="showable"><s:text name="name.isShowablePage" /></label></p>

</s:if>
<p><wpsf:submit value="%{getText('label.save')}" cssClass="button" /></p>
</s:form>