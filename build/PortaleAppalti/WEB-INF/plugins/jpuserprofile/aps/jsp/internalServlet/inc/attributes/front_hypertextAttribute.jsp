<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>
<wpsf:textarea cols="50" rows="3" id="%{attribute_id}" 
	name="%{#attributeTracer.getFormFieldName(#attribute)}" value="%{#attribute.textMap[#lang.code]}" cssClass="text" />