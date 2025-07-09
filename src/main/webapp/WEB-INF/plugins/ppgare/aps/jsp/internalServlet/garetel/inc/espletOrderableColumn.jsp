<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<s:set var="identifier">${param.identifier}</s:set>
<s:if test="%{orderField == null || !orderField.identifier.equals(#identifier)}">
    <c:set var="order_image" value="order_arrow_off.png"/>
    <c:set var="order_value" value="ASC" />
    <c:set var="order_tooltip" value="LABEL_ORDER_ASC" />
</s:if>
<s:elseif test='"ASC".equals(orderField.orderable.name())'>
    <c:set var="order_image" value="order_arrow_asc.png"/>
    <c:set var="order_value" value="DESC" />
    <c:set var="order_tooltip" value="LABEL_ORDER_ASC" />
</s:elseif>
<s:else>
    <c:set var="order_image" value="order_arrow_desc.png"/>
    <c:set var="order_value" value="ASC" />
    <c:set var="order_tooltip" value="LABEL_ORDER_DESC" />
</s:else>

<a href="<wp:action path="/ExtStr2/do/FrontEnd/GareTel/${param.action}.action"/>&amp;orderField.orderable=${order_value}&amp;codice=${param.codice}&amp;ext=${param.ext}&amp;orderField.identifier=${param.identifier}&amp;codiceLotto=${param.codiceLotto}">
    <img class="orderLink" src="<wp:imgURL/>/${order_image}" alt="<wp:i18n key='${order_tooltip}' />" title="<wp:i18n key='${order_tooltip}' />"/>
</a>