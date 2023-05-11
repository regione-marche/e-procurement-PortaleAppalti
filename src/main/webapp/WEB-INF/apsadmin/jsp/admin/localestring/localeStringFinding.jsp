<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>
<%@ taglib prefix="wpsa" uri="apsadmin-core.tld" %>
<h1><s:text name="title.languageAdmin" /></h1>
<h2><s:text name="title.languageAdmin.labels" /></h2>

<s:form action="search" >

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

		<p>
			<table>
				<tr>
					<td>
						<label for="text"><s:text name="label.search.for"/>&#32;<s:text name="label.text"/>:</label><br />
						<wpsf:textfield name="text" id="text" value="%{text}" cssClass="text" />
					</td>
					<td>&#32;&#32;&#32;&#32;&#32;</td>
					<td>
						<label for="customized"><s:text name="label.customized"/>:</label><br />
						<input type="checkbox" name="customized" id="customized" class="text" value="1" <s:if test="%{customized == 1}">checked="checked"</s:if> />
					</td>
				</tr>
			</table>
		</p>
	
	<fieldset>
		<legend class="accordion_toggler"><s:text name="title.searchFilters" /></legend>
		<div class="accordion_element">
		<p>
			<label for="searchOption"><s:text name="label.type"/>:</label><br />
			<select name="searchOption" id="searchOption" class="text">
				<option <s:if test="searchOption=='all'">selected="selected"</s:if> value="all"><s:text name="label.all" /></option>
				<option <s:if test="searchOption=='labelkey'">selected="selected"</s:if> value="labelkey"><s:text name="label.key" /></option>
				
				<s:iterator value="systemLangs">
					<option <s:if test="searchOption==code">selected="selected"</s:if> value="<s:property value="code" />"><s:property value="descr" /></option>
				</s:iterator>
			</select>
		</p>
		</div>
	</fieldset>
	<p>
		<wpsf:submit value="%{getText('label.search')}" cssClass="button" />
	</p>
<p>
	<a href="<s:url action="new" namespace="/do/LocaleString" />"><s:text name="locale.addNewLabel" /></a>
</p>

<s:set name="currentLocaleStrings" value="localeStrings"/>
<wpsa:subset source="currentLocaleStrings" count="10" objectName="groupContent" advanced="true" offset="5">
	<s:set name="group" value="#groupContent" />
	<div class="pager">
		<s:include value="/WEB-INF/apsadmin/jsp/common/inc/pagerInfo.jsp" />
		<s:include value="/WEB-INF/apsadmin/jsp/common/inc/pager_formBlock.jsp" />
	</div>
	<p class="noscreen">
		<wpsf:hidden name="lastGroupBy" />
		<wpsf:hidden name="lastOrder" />
	</p>

<s:if test="%{#currentLocaleStrings.size > 0}" >
	<table class="generic" summary="<s:text name="note.generalSettings.locale.summary" />">
	<caption><s:text name="title.generalSettings.locale.installedLabels" /></caption>
		<tr>
			<th><s:text name="label.code" /></th>
			<s:iterator value="systemLangs" id="lang">
				<th><s:property value="#lang.descr" /></th>
			</s:iterator>
			<th class="icon"><abbr title="<s:text name="label.remove" />">&ndash;</abbr></th>	
		</tr>
	
		<s:iterator id="key">
			<s:set name="currentLabel" value="labels[#key]" />
			<tr>
				<td>
					<a href="<s:url action="edit" namespace="/do/LocaleString" />?key=<s:property value="#key" />" title="<s:text name="label.edit" /> &ldquo;<s:property value="#key" />&rdquo;">
						<s:property value="#key" />
					</a>
				</td>
			<s:iterator value="systemLangs" id="lang">
				<td> 
					<%--
					<s:if test="%{#currentLabel.getCustomized(#lang.code) != 0}">
						<strong><s:property value="#currentLabel[#lang.code]" /></strong>
					</s:if>
					<s:else>
						<s:property value="#currentLabel[#lang.code]" />
					</s:else>
					--%>
					<s:property value="#currentLabel[#lang.code]" />
				</td>
			</s:iterator>
				<td class="icon"><a href="<s:url action="delete" namespace="/do/LocaleString"><s:param name="key" value="#key" /></s:url>" title="<s:text name="label.remove" />: <s:property value="#key" />"><img src="<wp:resourceURL />administration/img/icons/delete.png" alt="<s:text name="label.alt.clear" />" /></a></td>			
			</tr>
		</s:iterator>
	</table>
</s:if>
	<div class="pager">
		<s:include value="/WEB-INF/apsadmin/jsp/common/inc/pager_formBlock.jsp" />
	</div>
</wpsa:subset>
</s:form>