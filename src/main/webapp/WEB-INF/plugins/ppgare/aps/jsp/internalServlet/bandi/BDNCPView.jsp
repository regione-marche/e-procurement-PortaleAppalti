<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">
	<h2><wp:i18n key="TITLE_PAGE_DATI_APERTI_BDNCP" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_DATI_APERTI_BDNCP"/>
	</jsp:include>

	<ul class="list">
		<s:iterator var="lotto" value="lotti" status="status">
			<s:if test="%{cig != null}">
					<li class='first last'>
						<c:set var="codiceCig"><s:property value="%{cig}" /></c:set>
						<c:set var="href">${fn:replace(BDNCPUrl, '{0}', codiceCig)}</c:set>
							
						<a href='${href}' target="_blank" class="bkg-big go" title='<wp:i18n key="LABEL_APRI_BDNCP" />' >
							<wp:i18n key="LABEL_LOTTO" /> <s:property value="%{codiceInterno}" />
							&nbsp;-&nbsp;<wp:i18n key="LABEL_CIG" /> <s:property value="%{cig}" />
							&nbsp;-&nbsp;<s:property value="%{oggetto}" />
						</a>
					</li>
			</s:if>
		</s:iterator>
	</ul>
		
	<div class="back-link">
		<c:choose>
			<c:when test="${esito}">
				<c:set var="href"><wp:action path="/ExtStr2/do/FrontEnd/Esiti/view.action"/></c:set>
			</c:when>
			<c:otherwise>
				<c:set var="href"><wp:action path="/ExtStr2/do/FrontEnd/Bandi/view.action"/></c:set>
			</c:otherwise>
		</c:choose>
		<a href="${href}&amp;codice=${param.codice}&amp;ext=${param.ext}">
			<wp:i18n key="LINK_BACK_TO_PROCEDURE" />
		</a>
	</div>
</div>