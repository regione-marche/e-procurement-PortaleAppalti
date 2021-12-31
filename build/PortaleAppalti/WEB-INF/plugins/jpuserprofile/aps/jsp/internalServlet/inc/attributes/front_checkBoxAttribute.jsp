<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>
<wpsf:checkbox name="%{#attributeTracer.getFormFieldName(#attribute)}" id="%{attribute_id}" value="#checkedValue"/>
