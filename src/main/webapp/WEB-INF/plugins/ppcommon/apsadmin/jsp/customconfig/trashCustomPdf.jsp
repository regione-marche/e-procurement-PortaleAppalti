<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wpsa" uri="apsadmin-core.tld" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>

<h1><s:text name="ppcommon.title.customSection" /></h1>
<h2><s:text name="ppcommon.pdf.trash.subtitle" /></h2>

<s:form action="deleteCustomPdf" >
	<p class="noscreen"><wpsf:hidden name="idReport"/><wpsf:hidden name="entityCode"/></p>
	
	<p> 
		<s:text name="ppcommon.pdf.trashConfirm" /> "<s:property value="entityCode" />"? 
		<wpsf:submit value="%{getText('label.remove')}" cssClass="button" />
	</p>

	<p>
		<s:text name="ppcommon.pdf.trashConfirm.goBack" />&#32;<a href="<s:url action="listCustomPdf" />"><s:text name="ppcommon.function.pdf" /></a>
	</p>

</s:form>