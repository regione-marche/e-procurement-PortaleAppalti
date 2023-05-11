<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix = "fn" uri = "http://java.sun.com/jsp/jstl/functions" %>

<c:set var="pathProcedura" value="/ExtStr2/do/FrontEnd/AreaPers/view.action"/>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">
	
	<h2><wp:i18n key="TITLE_PAGE_DISABILITA_ACCESSO_CON" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
	<s:if test="! hasActionErrors()">	
		<p><wp:i18n key="LABEL_DISABILITA_ACCESSO_CON_SUCCESS" /></p>
	</s:if>

	<div class="back-link">
		<a href="ppcommon_area_personale.wp">
			<wp:i18n key="LINK_BACK_TO_AREAPERSONALE" />
		</a>
	</div>
</div>