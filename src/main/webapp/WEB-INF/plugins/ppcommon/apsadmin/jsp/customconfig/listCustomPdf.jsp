<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="wpsa" uri="apsadmin-core.tld" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>
<%@ taglib prefix="c"    uri="http://java.sun.com/jsp/jstl/core" %>

<h1><s:text name="ppcommon.title.customSection" /></h1>
<h2><s:text name="ppcommon.pdf.subtitle" /></h2>

	<s:if test="hasActionErrors()">
		<div class="message message_error">	
		<h2><s:text name="message.title.ActionErrors" /></h2>
			<ul>
				<s:iterator value="actionErrors">
					<li><s:property escape="false" /></li>
				</s:iterator>
			</ul>
		</div>
	</s:if>
	
	<p>
	<s:text name="ppcommon.pdf.text.1" /><br/>
	<s:text name="ppcommon.pdf.text.2" />
	</p>
	<br/>
	
	<p><s:text name="ppcommon.pdf.folder" /> : <s:property value="folder" /></p>
	
	<c:if test="${! empty folder}">

		<p><a href="<s:url action="newCustomPdf" namespace="/do/ppcommon/CustomConfig" />"><s:text name="ppcommon.pdf.link.newCustomPdf" /></a></p>
		
		<table class="generic" summary="<s:text name="ppcommon.pdf.summary" />">
			<tr>
				<th><s:text name="ppcommon.pdf.column.entityCode" /></th>
				<th><s:text name="ppcommon.pdf.column.reportOperation" /></th>
				<th><s:text name="ppcommon.pdf.column.reportName" /></th>
				<th><s:text name="ppcommon.pdf.column.reportDescription" /></th>
				<th class="icon"><abbr title="<s:text name="label.remove" />">&ndash;</abbr></th>	
			</tr>
			<s:iterator id="report" value="customReports">
			<tr>
				<td><s:property value="#report.entityCode" /></td>
				<td>
					<s:iterator value="operations">
						<s:if test="%{key == #report.reportOperation}"><s:property value="%{getText(value)}" /></s:if>
					</s:iterator>
				</td>
				<td><s:property value="#report.reportName" /></td>
				<td><s:property value="#report.reportDescription" /></td>
				<td class="icon"><a href="<s:url action="trashCustomPdf" namespace="/do/ppcommon/CustomConfig"><s:param name="entityCode" value="#report.entityCode"/><s:param name="idReport" value="#report.reportId"/></s:url>" title="<s:text name="label.remove" />: <s:property value="#user.username" />"><img src="<wp:resourceURL />administration/img/icons/delete.png" alt="<s:text name="label.alt.clear" />" /></a></td>
			</tr>
			</s:iterator>
		</table>

	</c:if>
