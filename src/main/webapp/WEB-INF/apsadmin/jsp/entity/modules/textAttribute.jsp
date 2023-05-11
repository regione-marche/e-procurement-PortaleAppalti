<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>
<wpsf:textfield id="%{#attributeTracer.getFormFieldName(#attribute)}" 
	name="%{#attributeTracer.getFormFieldName(#attribute)}" value="%{#attribute.getTextForLang(#lang.code)}"
	maxlength="254" cssClass="text" />