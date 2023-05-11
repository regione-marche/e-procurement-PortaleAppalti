<%@ taglib prefix="wp" uri="aps-core.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<script	src='<wp:resourceURL/>static/js/jquery.dataTables.min.js'></script>


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp">
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2><wp:i18n key="TITLE_PAGE_190_PROSPETTI_ANNUALI" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_PROSPETTO_ANNI_ANTICORRUZIONE" />
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />

	<ul>
		<s:iterator var="anno" value="listaAnni" status="index">
			<li>
				<a href="<wp:action path="/ExtStr2/do/FrontEnd/AmmTrasp/openSearchAnticorruzione.action"/>&amp;model.anno=<s:property value="%{anno}"/>&amp;${tokenHrefParams}">
					<wp:i18n key="LABEL_ANNO" /> <s:property value="%{anno}" />
				</a>
			</li>
		</s:iterator>
	</ul>
</div>