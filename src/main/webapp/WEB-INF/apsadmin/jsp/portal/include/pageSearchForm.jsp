<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>

<s:form action="search" >

	<p>
		<label for="pageCodeToken"><s:text name="label.search.for"/>&#32;<s:text name="name.pageCode"/>:</label><br />
		<wpsf:textfield name="pageCodeToken" id="pageCodeToken" value="%{pageCodeToken}" cssClass="text" />
	</p>
	<p>
		<wpsf:submit value="%{getText('label.search')}" cssClass="button" />
	</p>
	
</s:form>

