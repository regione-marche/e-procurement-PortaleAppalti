<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>

<p>
	<label for="attributeTypeCode"><s:text name="label.type" />:</label><br />
	<wpsf:select list="attributeTypes" name="attributeTypeCode" id="attributeTypeCode" listKey="type" listValue="type"></wpsf:select>
	<wpsf:submit value="%{getText('label.add')}" action="addAttribute" cssClass="button" />
</p>