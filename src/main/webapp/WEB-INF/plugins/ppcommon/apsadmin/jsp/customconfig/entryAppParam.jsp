<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="wpsa" uri="apsadmin-core.tld" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>

<h1><s:text name="ppcommon.title.customSection" /></h1>
<h2><s:text name="ppcommon.systemParams.subtitle" /></h2>

<s:form action="saveAppParam" >
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
	<s:text name="ppcommon.systemParams.text.1" /><br/>
	<s:text name="ppcommon.systemParams.text.2" />
	<s:text name="ppcommon.systemParams.text.3" />
	<s:text name="ppcommon.systemParams.text.4" />
</p>
<p>
	<b><s:text name="ppcommon.systemParams.text.5" /></b>
	<s:text name="ppcommon.systemParams.text.6" />
</p>

<s:set var="righeEditabili" value="%{false}"></s:set>


<s:select list="categories" name="category" id="categorie" value="category" headerKey="" headerValue="" />
<wpsf:submit value="%{getText('ppcommon.systemParams.button.filtra')}" cssClass="button" method="filtra"  />

<table class="generic" summary="<s:text name="ppcommon.systemParams.summary" />">
	<caption><s:text name="ppcommon.systemParams.summary" /></caption>
	<tr>
		<th></th>
		<th><s:text name="ppcommon.systemParams.column.key" /></th>
		<th><s:text name="ppcommon.systemParams.column.descr" /></th>
		<th><s:text name="ppcommon.systemParams.column.value" /></th>
	</tr>
<s:iterator value="appParams" id="apppar" status="stat">
	<tr>
		<td><s:if test="%{! #apppar.jndi}">
			<input type="checkbox" name="reset" value="${apppar.name}" />
			<s:set var="righeEditabili" value="%{true}"></s:set>
			</s:if>
		</td>
		<td>
			<s:property value="#apppar.name"/>
			<s:if test="%{! #apppar.jndi}">
			<s:hidden value="%{#apppar.name}" name="name" id="name%{#stat.count}"/>
			</s:if>
		</td>
		<td>
			<s:property value="#apppar.description"/>
		</td>
		<td>
			<s:if test='%{"B".equals(#apppar.type)}'>
				<s:select name="value" value="%{#apppar.value}" list="#{'true': getText('ppcommon.option.yes'), 'false': getText('ppcommon.option.no')}" id="value%{#stat.count}" disabled="%{#apppar.jndi}"></s:select>
			</s:if>
			<s:else>
				<s:textfield name="value" id="value%{#stat.count}" title="%{#apppar.value}" disabled="%{#apppar.jndi}"></s:textfield>
			</s:else>
			
		</td>
	</tr>
</s:iterator>
</table>

<s:if test="%{#righeEditabili}">
<p>
	<wpsf:submit value="%{getText('ppcommon.systemParams.button.save')}" cssClass="button" method="save"  />
	<wpsf:submit value="%{getText('ppcommon.systemParams.button.default')}" cssClass="button" method="setDefault" />
	<s:reset value="%{getText('ppcommon.systemParams.button.undo')}" cssClass="button" />
</p>
</s:if>
	
</s:form>