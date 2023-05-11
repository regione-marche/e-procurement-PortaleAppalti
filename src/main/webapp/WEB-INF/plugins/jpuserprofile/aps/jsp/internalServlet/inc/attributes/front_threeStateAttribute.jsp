<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%--
BOTH_YES_AND_NO = Indifferente
YES = Si
NO = No
 --%>
<ul class="noBullet">
	<li><wpsf:radio name="%{#attributeTracer.getFormFieldName(#attribute)}" id="none_%{attribute_id}" value="" checked="%{#attribute.booleanValue == null}" cssClass="radio" /><label for="none_<s:property value="%{attribute_id}" />" class="normal" ><wp:i18n key="BOTH_YES_AND_NO" /></label></li>
	<li><wpsf:radio name="%{#attributeTracer.getFormFieldName(#attribute)}" id="true_%{attribute_id}" value="true" checked="%{#attribute.booleanValue != null && #attribute.booleanValue == true}" cssClass="radio" /><label for="true_<s:property value="%{attribute_id}" />" class="normal" ><wp:i18n key="YES" /></label></li>
	<li><wpsf:radio name="%{#attributeTracer.getFormFieldName(#attribute)}" id="false_%{attribute_id}" value="false" checked="%{#attribute.booleanValue != null && #attribute.booleanValue == false}" cssClass="radio" /><label for="false_<s:property value="%{attribute_id}" />" class="normal"><wp:i18n key="NO" /></label></li>
</ul>
