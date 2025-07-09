<%@ taglib prefix="wp" uri="aps-core.tld" %>
	<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
		<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>

			<c:set var="layout" value="${param.layout}" />
			<c:if test="${empty layout}">
				<c:set var="layout" value="${cookie.layout.value}" />
			</c:if>

			<es:getAppParam name="layoutStyle" var="dir" />
			<c:if test="${! empty layout}">
				<c:set var="dir" value="${layout}" />
			</c:if>
			<c:set var="isNewLayout" value="${layout == 'appalti-contratti' || dir == 'appalti-contratti'}" />

			<header>
				<div id="htmlprebanner">
					<wp:i18n key="HTML_PRE_BANNER" />
				</div>
				<div id="header">
					<div id="header-top">
						<c:if test="${isNewLayout}">
							<div id="date-sub-menu">
								<h1 class="noscreen information">
									<wp:i18n key="SECTION_DATE_TIME" />:
								</h1>
								<wp:show frame="0" />
								<p class="noscreen">[ <a href="#pagestart">
										<wp:i18n key="BACK_TO_THE_TOP" />
									</a> ]</p>
							</div>
							<div id="new-area-riservata" tabindex="0" role="button">
								<jsp:include
									page="/WEB-INF/plugins/jpuserreg/aps/jsp/showlets/jpuserreg_loginUserReg.jsp" />
								<span>
									<c:choose>
										<c:when
											test="${sessionScope.currentUser != 'guest' || sessionScope.accountSSO != null}">
											<wp:i18n key="RESERVED_AREA" />
										</c:when>
										<c:otherwise>
											<wp:i18n key="auth_LOGIN_HEADER" />
										</c:otherwise>
									</c:choose>
								</span>
							</div>
						</c:if>
						<wp:show frame="9" />

					</div>
					<div id="header-main">
						<jsp:include page="logo.jsp"></jsp:include>
					</div>
					<div id="header-sub"></div>
				</div>
				<div id="htmlpostbanner">
					<wp:i18n key="HTML_POST_BANNER" />
				</div>
				<jsp:include page="menu-agid.jsp" />
				<div id="prebreadcrumbs">
					<wp:i18n key="HTML_PRE_BREADCRUMBS" />
				</div>
				<jsp:include page="breadcrumbs.jsp">
					<jsp:param name="layoutPage" value="${param.layoutPage}" />
				</jsp:include>
			</header>