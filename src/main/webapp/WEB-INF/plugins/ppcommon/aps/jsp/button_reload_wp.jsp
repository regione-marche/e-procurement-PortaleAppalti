<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<wp:i18n key="BUTTON_RESET" var="lblReset" />
<c:set var="struts_url"><s:url includeParams="all" /></c:set>
<c:set var="splitted" value="${fn:split(struts_url, '/')}" />
<c:set var="wp" value="${splitted[fn:length(splitted)-1]}" />
<%-- <a id="linkReload" href="<wp:url page='${wp}' />?${tokenHrefParams}" class="button block-ui">Reset</a> --%>
<c:set var="label"><s:property value="%{#attr.lblReset}" /></c:set>
<input id="btnReload" type="button" class="button block-ui" value="${label}" />

<script>
    $("#btnReload").click(function() {
        window.location.href = "<wp:url page='${wp}' />"
    });
</script>