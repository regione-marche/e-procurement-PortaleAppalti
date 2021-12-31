<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2><wp:i18n key="TITLE_PAGE_REGISTRA_OE" /></h2>

	<p><wp:i18n key="LABEL_REGISTRAZIONE_OE_SUCCESS_1" /></p>
	<p><wp:i18n key="LABEL_REGISTRAZIONE_OE_SUCCESS_2" /> 
		<s:property value="%{mail}" />
		<c:if test="${empty sessionScope.accountSSO}"><wp:i18n key="LABEL_REGISTRAZIONE_OE_SUCCESS_3" /></c:if>.</p>
	<c:if test="${empty sessionScope.accountSSO}">
	<p><wp:i18n key="LABEL_MESSAGE_WARNING" />: <wp:i18n key="LABEL_REGISTRAZIONE_OE_SUCCESS_4" /></p>
	</c:if>
	<p><wp:i18n key="LABEL_GREETINGS" /></p>
</div>