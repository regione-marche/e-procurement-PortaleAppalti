<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wpsa" uri="apsadmin-core.tld" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>

<h1><s:text name="title.contentManagement" /></h1>

<s:form action="search" >

<p class="noscreen">
	<input type="hidden" name="lastGroupBy" />
	<input type="hidden" name="lastOrder" />
</p>

<p><label for="text"><s:text name="label.search.for"/>&#32;<s:text name="label.description"/>:</label><br />
<wpsf:textfield name="text" id="text" cssClass="text" /></p>

<fieldset><legend class="accordion_toggler"><s:text name="title.searchFilters" /></legend>
<div class="accordion_element">

<p><label for="contentIdToken"><s:text name="label.code"/>:</label><br />
<wpsf:textfield name="contentIdToken" id="contentIdToken" cssClass="text" /></p>

<p><label for="contentType"><s:text name="label.type"/>:</label><br />
<wpsf:select name="contentType" id="contentType" 
	list="contentTypes" listKey="code" listValue="descr" 
	headerKey="" headerValue="%{getText('label.all')}" cssClass="text"></wpsf:select>
</p>

<p><label for="state"><s:text name="label.state"/>:</label><br />
<wpsf:select name="state" id="state" list="avalaibleStatus" headerKey="" headerValue="%{getText('label.all')}" cssClass="text" listKey="key" listValue="%{getText(value)}" />	
</p>
<p>
	<input type="radio" name="onLineState" id="approved" <s:if test="('yes' == onLineState)">checked="checked"</s:if> value="yes" /><label for="approved"><s:text name="name.isApprovedContent"/></label>&#32;<input type="radio" name="onLineState" id="notApproved" <s:if test="('no' == onLineState)">checked="checked"</s:if> value="no" /><label for="notApproved"><s:text name="name.isNotApprovedContent"/></label>&#32;<input type="radio" name="onLineState" id="bothApproved" <s:if test="('yes' != onLineState) && ('no' != onLineState)">checked="checked"</s:if> value="" /><label for="bothApproved"><s:text name="name.isApprovedOrNotContent" /></label>
</p>
</div>
</fieldset>
<fieldset><legend class="accordion_toggler"><s:text name="title.searchResultOptions" /></legend>
<div class="accordion_element">
<p>
	<wpsf:checkbox name="viewCode" id="viewCode"></wpsf:checkbox><label for="viewCode"><s:text name="label.code"/></label><br />
	<wpsf:checkbox name="viewTypeDescr" id="viewTypeDescr"></wpsf:checkbox><label for="viewTypeDescr"><s:text name="name.contentType"/></label><br />
	<wpsf:checkbox name="viewStatus" id="viewStatus"></wpsf:checkbox><label for="viewStatus"><s:text name="name.contentStatus"/></label><br />
	<wpsf:checkbox name="viewGroup" id="viewGroup"></wpsf:checkbox><label for="viewGroup"><s:text name="label.group"/></label><br />
	<wpsf:checkbox name="viewCreationDate" id="viewCreationDate"></wpsf:checkbox><label for="viewCreationDate"><s:text name="label.creationDate"/></label>
</p>
</div>
</fieldset>
<p><wpsf:submit value="%{getText('label.search')}" cssClass="button" /></p>

</s:form>

<s:form action="search" >
<p class="noscreen">
	<wpsf:hidden name="text" id="POBA"/>
	<wpsf:hidden name="contentType" />
	<wpsf:hidden name="state" />
	<wpsf:hidden name="onLineState" />
	<wpsf:hidden name="viewTypeDescr" />
	<wpsf:hidden name="viewGroup" />
	<wpsf:hidden name="viewCode" />
	<wpsf:hidden name="viewStatus" />
	<wpsf:hidden name="viewCreationDate" />
	<wpsf:hidden name="lastGroupBy" />
	<wpsf:hidden name="lastOrder" />
	<wpsf:hidden name="contentIdToken" />
</p>

<s:if test="hasActionErrors()">
<div class="message message_error">
<h2><s:text name="message.title.ActionErrors" /></h2>	
	<ul>
	<s:iterator value="ActionErrors">
		<li><s:property escape="false" /></li>
	</s:iterator>
	</ul>
</div>
</s:if>
<s:if test="hasActionMessages()">
<div class="message message_confirm">
<h2><s:text name="messages.confirm" /></h2>	
<ul>
	<s:iterator value="actionMessages">
		<li><s:property escape="false" /></li>
	</s:iterator>
</ul>
</div>
</s:if>

<wpsa:subset source="contents" count="10" objectName="groupContent" advanced="true" offset="5">
<s:set name="group" value="#groupContent" />

<div class="pager">
	<s:include value="/WEB-INF/apsadmin/jsp/common/inc/pagerInfo.jsp" />
	<s:include value="/WEB-INF/apsadmin/jsp/common/inc/pager_formBlock.jsp" />
</div>

<s:if test="%{getContents().size() > 0}">
	
	<table class="generic" id="contentListTable" summary="<s:text name="note.content.contentList.summary" />">
	<caption><s:text name="title.contentList" /></caption>
	<tr>
		<th class="icon_double"><abbr title="<s:text name="label.actions" />">&ndash;</abbr></th>
		<th><a href="
		<s:url action="changeOrder" anchor="contentListTable">
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
			<s:param name="onLineState">
				<s:property value="#request.onLineState"/>
			</s:param>
			<s:param name="viewTypeDescr">
				<s:property value="#request.viewTypeDescr"/>
			</s:param>
			<s:param name="viewGroup">
				<s:property value="#request.viewGroup"/>
			</s:param>
			<s:param name="viewCode">
				<s:property value="#request.viewCode"/>
			</s:param>
			<s:param name="viewStatus">
				<s:property value="#request.viewStatus"/>
			</s:param>
			<s:param name="viewCreationDate">
				<s:property value="#request.viewCreationDate"/>
			</s:param>
			<s:param name="pagerItem">
				<s:property value="#groupContent.currItem"/>
			</s:param>
			<s:param name="lastGroupBy"><s:property value="lastGroupBy"/></s:param>
			<s:param name="lastOrder"><s:property value="lastOrder"/></s:param>
			<s:param name="groupBy">descr</s:param>
		</s:url>
	"><s:text name="label.description" /></a></th>
	<s:if test="viewCode">
		<th><a href="
		<s:url action="changeOrder" anchor="contentListTable">
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
			<s:param name="onLineState">
				<s:property value="#request.onLineState"/>
			</s:param>
			<s:param name="viewTypeDescr">
				<s:property value="#request.viewTypeDescr"/>
			</s:param>
			<s:param name="viewGroup">
				<s:property value="#request.viewGroup"/>
			</s:param>
			<s:param name="viewCode">
				<s:property value="#request.viewCode"/>
			</s:param>
			<s:param name="viewStatus">
				<s:property value="#request.viewStatus"/>
			</s:param>
			<s:param name="viewCreationDate">
				<s:property value="#request.viewCreationDate"/>
			</s:param>
			<s:param name="pagerItem">
				<s:property value="#groupContent.currItem"/>
			</s:param>
			<s:param name="lastGroupBy"><s:property value="lastGroupBy"/></s:param>
			<s:param name="lastOrder"><s:property value="lastOrder"/></s:param>
			<s:param name="groupBy">code</s:param>
		</s:url>
	"><s:text name="label.code" /></a></th>
	</s:if>
		<s:if test="viewTypeDescr"><th><s:text name="label.type" /></th></s:if>
		<s:if test="viewStatus"><th><s:text name="label.state" /></th></s:if>
		<s:if test="viewGroup"><th><s:text name="label.group" /></th></s:if>
		<s:if test="viewCreationDate">
		<th><a href="
		<s:url action="changeOrder" anchor="contentListTable">
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
			<s:param name="onLineState">
				<s:property value="#request.onLineState"/>
			</s:param>
			<s:param name="viewTypeDescr">
				<s:property value="#request.viewTypeDescr"/>
			</s:param>
			<s:param name="viewGroup">
				<s:property value="#request.viewGroup"/>
			</s:param>
			<s:param name="viewCode">
				<s:property value="#request.viewCode"/>
			</s:param>
			<s:param name="viewStatus">
				<s:property value="#request.viewStatus"/>
			</s:param>
			<s:param name="viewCreationDate">
				<s:property value="#request.viewCreationDate"/>
			</s:param>
			<s:param name="pagerItem">
				<s:property value="#groupContent.currItem"/>
			</s:param>
			<s:param name="lastGroupBy"><s:property value="lastGroupBy"/></s:param>
			<s:param name="lastOrder"><s:property value="lastOrder"/></s:param>
			<s:param name="groupBy">created</s:param>
		</s:url>
	"><s:text name="label.creationDate" /></a></th>
	</s:if>
		<th><a href="
		<s:url action="changeOrder" anchor="contentListTable">
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
			<s:param name="onLineState">
				<s:property value="#request.onLineState"/>
			</s:param>
			<s:param name="viewTypeDescr">
				<s:property value="#request.viewTypeDescr"/>
			</s:param>
			<s:param name="viewGroup">
				<s:property value="#request.viewGroup"/>
			</s:param>
			<s:param name="viewCode">
				<s:property value="#request.viewCode"/>
			</s:param>
			<s:param name="viewStatus">
				<s:property value="#request.viewStatus"/>
			</s:param>
			<s:param name="viewCreationDate">
				<s:property value="#request.viewCreationDate"/>
			</s:param>
			<s:param name="pagerItem">
				<s:property value="#groupContent.currItem"/>
			</s:param>
			<s:param name="lastGroupBy"><s:property value="lastGroupBy"/></s:param>
			<s:param name="lastOrder"><s:property value="lastOrder"/></s:param>
			<s:param name="groupBy">lastModified</s:param>
		</s:url>
	"><s:text name="label.lastEdit" /></a></th>
		<th class="icon"><abbr title="<s:text name="name.onLine" />">P</abbr></th>
	</tr>
	<s:iterator var="contentId">
	<s:set name="content" value="%{getContentVo(#contentId)}"></s:set>
	<tr>
	<td class="icon_double">
	<a href="<s:url action="edit" namespace="/do/jacms/Content"><s:param name="contentId" value="#content.id" /></s:url>" ><img src="<wp:resourceURL/>administration/img/icons/edit-content.png" alt="<s:text name="label.edit" />: <s:property value="#content.id" /> - <s:property value="#content.descr" />" title="<s:text name="label.edit" />: <s:property value="#content.id" /> - <s:property value="#content.descr" />" /></a>
	<a href="<s:url action="copyPaste" namespace="/do/jacms/Content"><s:param name="contentId" value="#content.id" /><s:param name="copyPublicVersion" value="'false'" /></s:url>" ><img src="<wp:resourceURL/>administration/img/icons/edit-copy.png" alt="<s:text name="label.copyPaste" />: <s:property value="#content.id" /> - <s:property value="#content.descr" />" title="<s:text name="label.copyPaste" />: <s:property value="#content.id" /> - <s:property value="#content.descr" />" /></a>
	</td>
	<td><input type="checkbox" name="contentIds" id="content_<s:property value="#content.id" />" value="<s:property value="#content.id" />" /><label for="content_<s:property value="#content.id" />"><s:property value="#content.descr" /></label></td>
	<s:if test="viewCode"><td><span class="monospace"><s:property value="#content.id" /></span></td></s:if>
	<s:if test="viewTypeDescr"><td><s:property value="%{getSmallContentType(#content.typeCode).descr}" /></td></s:if>
	<s:if test="viewStatus"><td><s:property value="%{getText('name.contentStatus.' + #content.status)}" /></td></s:if>
	<s:if test="viewGroup"><td><s:property value="%{getGroup(#content.mainGroupCode).descr}" /></td></s:if>
	<s:if test="viewCreationDate"><td class="centerText"><span class="monospace"><s:date name="#content.create" format="dd/MM/yyyy HH:mm" /></span></td></s:if>
	<td class="icon"><span class="monospace"><s:date name="#content.modify" format="dd/MM/yyyy HH:mm" /></span></td>
	
	<s:if test="#content.onLine && #content.sync">
		<s:set name="iconImagePath" id="iconImagePath"><wp:resourceURL/>administration/img/icons/content-isonline.png</s:set>
		<s:set name="isOnlineStatus" value="%{getText('label.yes')}" />
	</s:if>
	<s:if test="#content.onLine && !(#content.sync)">
		<s:set name="iconImagePath" id="iconImagePath"><wp:resourceURL/>administration/img/icons/content-isnotsynched.png</s:set>
		<s:set name="isOnlineStatus" value="%{getText('label.yes') + ', ' + getText('note.notSynched')}" />
	</s:if>
	<s:if test="!(#content.onLine)">
		<s:set name="iconImagePath" id="iconImagePath"><wp:resourceURL/>administration/img/icons/content-isnotonline.png</s:set>
		<s:set name="isOnlineStatus" value="%{getText('label.no')}" />
	</s:if>
	
	<td class="centerText"><img src="<s:property value="iconImagePath" />" alt="<s:property value="isOnlineStatus" />" title="<s:property value="isOnlineStatus" />" /></td>
	</tr>
	</s:iterator>
	</table>
</s:if>
<s:else>

</s:else>

<wp:ifauthorized permission="validateContents">
<fieldset><legend><s:text name="title.contentActions" /></legend>
<p class="noscreen"><s:text name="title.contentActionsIntro" /></p>
<p class="buttons">
	<s:set name="iconImagePath" id="iconImagePath"><wp:resourceURL/>administration/img/icons/32x32/approve.png</s:set>	
	<wpsf:submit action="approveContentGroup" type="image" src="%{#iconImagePath}" value="%{getText('label.approve')}" title="%{getText('note.button.approve')}" />
	<s:set name="iconImagePath" id="iconImagePath"><wp:resourceURL/>administration/img/icons/32x32/suspend.png</s:set>
	<wpsf:submit action="suspendContentGroup" type="image" src="%{#iconImagePath}" value="%{getText('label.suspend')}" title="%{getText('note.button.suspend')}" />
	<s:set name="iconImagePath" id="iconImagePath"><wp:resourceURL/>administration/img/icons/32x32/delete.png</s:set>
	<wpsf:submit action="trashContentGroup" type="image" src="%{#iconImagePath}" value="%{getText('label.remove')}" title="%{getText('note.button.delete')}" />
</p>
</fieldset>
</wp:ifauthorized>

<div class="pager">
	<s:include value="/WEB-INF/apsadmin/jsp/common/inc/pager_formBlock.jsp" />
</div>

</wpsa:subset>

</s:form>