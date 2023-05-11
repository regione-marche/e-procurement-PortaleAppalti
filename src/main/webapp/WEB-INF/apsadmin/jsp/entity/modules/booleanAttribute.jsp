<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>

<s:if test="#lang.default">
<ul class="noBullet">

<li><wpsf:radio name="%{#attributeTracer.getFormFieldName(#attribute)}" id="true_%{#attributeTracer.getFormFieldName(#attribute)}" value="true" checked="%{#attribute.value == true}" cssClass="radio" /><label for="true_<s:property value="#attributeTracer.getFormFieldName(#attribute)" />" class="normal"><s:text name="label.yes"/></label></li>
<li><wpsf:radio name="%{#attributeTracer.getFormFieldName(#attribute)}" id="false_%{#attributeTracer.getFormFieldName(#attribute)}" value="false" checked="%{#attribute.value == false}" cssClass="radio"  /><label for="false_<s:property value="#attributeTracer.getFormFieldName(#attribute)" />" class="normal"><s:text name="label.no"/></label></li>
</ul>
</s:if>
<s:else>
<p>
	<s:text name="note.editContent.doThisInTheDefaultLanguage.must" />.
</p>
</s:else>