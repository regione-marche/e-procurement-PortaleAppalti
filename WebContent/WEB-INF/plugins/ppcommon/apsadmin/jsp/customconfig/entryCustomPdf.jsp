<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wpsa" uri="apsadmin-core.tld" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>

<h1><s:text name="ppcommon.title.customSection" /></h1>
<h2><s:text name="ppcommon.pdf.add.subtitle" /></h2>


<s:form action="saveCustomPdf" >
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
	<label for="entityCode"><s:text name="ppcommon.pdf.column.entityCode" />:</label><br />
	<wpsf:textfield name="entityCode" cssClass="text" size="20" maxlength="20" />
</p>

	<table class="generic" summary="<s:text name="ppcommon.pdf.summary" />">
		<caption>Report</caption>
		<tr>
			<th></th>
			<th><s:text name="ppcommon.pdf.column.reportOperation" /></th>
			<th><s:text name="ppcommon.pdf.column.reportName" /></th>
			<th><s:text name="ppcommon.pdf.column.reportDescription" /></th>
		</tr>
		<s:iterator id="report" value="customReports">
		<tr>
			<td><input type="radio" name="idReport" value="<s:property value='#report.reportId'/>" <s:if test="idReport == #report.reportId">checked="checked"</s:if> ></input></td>
			<td>
				<s:iterator value="operations">
					<s:if test="%{key == #report.reportOperation}"><s:property value="%{getText(value)}" /></s:if>
				</s:iterator>
			</td>
			<td><s:property value="#report.reportName" /></td>
			<td><s:property value="#report.reportDescription" /></td>
		</tr>
		</s:iterator>
	</table>

<p>
	<wpsf:submit value="%{getText('label.save')}" cssClass="button" />
</p>
	
</s:form>