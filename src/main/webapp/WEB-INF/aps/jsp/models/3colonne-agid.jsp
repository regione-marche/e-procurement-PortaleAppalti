<%@ taglib prefix="wp"  uri="aps-core.tld"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="es"  uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>

<wp:contentNegotiation mimeType="text/html" charset="utf-8"/>
<wp:currentPage param="title" var="currentViewTitle" />
<es:checkCustomization var="fixed_header" objectId="LAYOUT" attribute="TESTATAFISSA" feature="ACT" />

<!DOCTYPE html>

<html lang='<wp:info key="currentLang" />'>

	<head prefix="og: http://ogp.me/ns#; dcterms: http://purl.org/dc/terms/#">
		<!--- - - - -->
		<meta name="viewport" content="width=device-width, user-scalable=no" />
		<!--- - - - -->
		<link rel="profile" href="http://dublincore.org/documents/dcq-html" />
		
		<title><wp:i18n key="MAIN_TITLE" />|${currentViewTitle}</title>
		
		<meta http-equiv="X-UA-Compatible" content="IE=Edge" /> <%-- per forzare ultima versione di Internet Explorer --%>

		<link rel="shortcut icon" href="<wp:imgURL/>favicon.ico" />
		
		<%-- imposta font --%>
		<c:set var="font" value=""/>
		<c:if test="${! empty param.font}">
			<c:set var="font" value="${param.font}"/>
		</c:if>
		<c:if test="${empty font}">
			<c:set var="font" value="${cookie.font.value}"/>
		</c:if>

		<%-- imposta skin --%>
		<c:set var="skin" value=""/>
		<c:if test="${! empty param.skin}">
			<c:set var="skin" value="${param.skin}"/>
		</c:if>
		<c:if test="${empty skin}">
			<c:set var="skin" value="${cookie.skin.value}"/>
		</c:if>
		
		<%-- imposta layout --%>
		<c:set var="layout" value="${param.layout}" />
		<c:if test="${empty layout}">
			<c:set var="layout" value="${cookie.layout.value}" />
		</c:if>
						
		<es:getAppParam name="layoutStyle" var="setCss"/>
		<c:if test="${! empty layout}">
			<c:set var="setCss" value="${layout}"/>
		</c:if>		
		
		<link type="text/css" rel="stylesheet" href="<wp:cssURL/>normalize.css" />
		<c:choose>
			<c:when test="${skin == 'highcontrast'}">
				<link type="text/css" rel="stylesheet" href="<wp:cssURL/>portale-text.css" />
				<link type="text/css" rel="stylesheet" href="<wp:cssURL/>highcontrast/portale.css" />
			</c:when>
			<c:when test="${skin == 'text'}">
				<link type="text/css" rel="stylesheet" href="<wp:cssURL/>portale-text.css" />
			</c:when>
			<c:otherwise>
				<link type="text/css" rel="stylesheet" href="<wp:cssURL/>portale-agid.css" />
				<c:if test="${setCss eq '01' || setCss eq 'jcity-gov' || setCss eq 'appalti-contratti' || setCss eq 'appalti-contratti-v1'}">
				<link type="text/css" rel="stylesheet" href="<wp:cssURL/>banner.css" />
				</c:if>
				<link type="text/css" rel="stylesheet" href="<wp:cssURL/>${setCss}/portale.css" />
			</c:otherwise>
		</c:choose>
		<c:if test="${font == 'big'}">
			<link type="text/css" rel="stylesheet" href="<wp:cssURL/>portale-bigfont.css" />
		</c:if>
		<c:if test="${font == 'verybig'}">
			<link type="text/css" rel="stylesheet" href="<wp:cssURL/>portale-verybigfont.css" />
		</c:if>
		<wp:outputHeadInfo type="CSS">
			<link rel="stylesheet" type="text/css" href="<wp:cssURL /><wp:printHeadInfo />" />
		</wp:outputHeadInfo>
	
		<!-- gestione mediante linguaggio dublin core dei metadati descrittivi per l'indicizzazione dei contenuti come previsto dal DPCM 26/04/2011 -->
		<%-- https://www.madebymagnitude.com/blog/resolving-html5-dublin-core-microdata-validation-issues/ --%>
		<wp:outputHeadInfo type="DC.Description">
			<meta property="dcterms:description" content="<wp:printHeadInfo />" />
		</wp:outputHeadInfo>
		<wp:outputHeadInfo type="RSS.Bandi">
			<link rel="alternate" type="application/rss+xml" title="Bandi di gara" href="<wp:printHeadInfo />"/>
		</wp:outputHeadInfo>
		<wp:outputHeadInfo type="RSS.BandiScaduti">
			<link rel="alternate" type="application/rss+xml" title="Bandi di gara scaduti" href="<wp:printHeadInfo />"/>
		</wp:outputHeadInfo>

		<c:if test="${setCss == 'appalti-contratti' || setCss == 'appalti-contratti-v1' || fixed_header}">
				<script src="<wp:resourceURL/>static/js/ScrollHeader.js"></script>
		</c:if>
				
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/jquery.jsp" />
		<%-- inclusione dei js che filtrano i caratteri inseribili in campi di input e textarea --%>
		<script src="<wp:resourceURL/>static/js/jquery.alphanum.js"></script>
		<script src="<wp:resourceURL/>static/js/jquery.character.js"></script>
		<script src="<wp:resourceURL/>static/js/resizeBalloon.js"></script>
	</head>

	<body>
		<%-- link trappola per bot --%>
		<div style="display: none;">
			<a href="<wp:info key="systemParam" paramName="applicationBaseURL"/>do/funopen.action">fun</a>
		</div>

		<jsp:include page="/WEB-INF/aps/jsp/models/inc/disclamer-note.jsp" />

		<div class="noscreen">
			<p>[ <a href="#mainarea" id="pagestart"><wp:i18n key="SKIP_TO_MAIN_CONTENT"/></a> ]</p>
			<dl>
				<dt><wp:i18n key="LABEL_ACCESS_KEYS"/>:</dt>
				<jsp:include page="inc/common-access-keys.jsp" />
				<dd><a href="#menu2" accesskey="3"><wp:i18n key="SKIP_TO_OTHER_FEATURES"/></a> [3]</dd>
			</dl>
		</div>

		<div class="viewport-container">
			<div id="ext-container" class="smooth-transition page-container">
				<jsp:include page="inc/header-agid.jsp">
					<jsp:param name="layoutPage" value="three-columns" />
				</jsp:include>
				
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/allowCookies.jsp" />
				
				<div class="container three-columns">
					<div class="columns">
						<div class="row">
							<nav>
								<div class="col-3 responsive-hide">
									<div class="responsive column menu">
										<hr class="noscreen"/>
										<h2 class="noscreen information"><wp:i18n key="SECTION_MENU"/>:</h2>
										<p class="noscreen">[ <a id="menu1" href="#mainarea"><wp:i18n key="SKIP_TO_MAIN_CONTENT"/></a> ]</p>
7										<jsp:include page="/WEB-INF/aps/jsp/models/inc/frameLogin.jsp" />
										<wp:show frame="4"/>
										<wp:show frame="5"/>
										<p class="noscreen">[ <a href="#pagestart"><wp:i18n key="BACK_TO_THE_TOP" /></a> ]</p>
									</div>
								</div>
							</nav>
							<main class="col-6">
								<div class="responsive content">
									<hr class="noscreen"/>
									<h2 class="noscreen information"><wp:i18n key="SECTION_CONTENTS"/>:</h2>
									<p class="noscreen">[ <a id="mainarea" href="#menu1"><wp:i18n key="SKIP_TO_MENU" /></a> ]</p>
									<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/internalServlet/sessioniattive/inc/viewSessioniAttiveMessage.jsp" />
									<wp:show frame="6"/>
									<wp:show frame="7"/>
									<wp:show frame="8"/>
									<p class="noscreen">[ <a href="#pagestart"><wp:i18n key="BACK_TO_THE_TOP" /></a> ]</p>
								</div>
							</main>
							<nav>
								<div class="col-3 responsive-hide">
									<div class="responsive column menu ">
										<hr class="noscreen"/>
										<h2 class="noscreen information"><wp:i18n key="SECTION_OTHER_FEATURES" />:</h2>
										<p class="noscreen">[ <a id="menu2" href="#footerarea"><wp:i18n key="SKIP_TO_FOOTER"/></a> ]</p>
										<wp:show frame="10"/>
										<wp:show frame="11"/>
										<wp:show frame="12"/>
										<p class="noscreen">[ <a href="#pagestart"><wp:i18n key="BACK_TO_THE_TOP" /></a> ]</p>
									</div>
								</div>
							</nav>
						</div>
						
						<!-- end #main -->
					</div>
				</div>
				<jsp:include page="inc/footer-agid.jsp" >
				    <jsp:param name="setCss" value="${setCss}" />
				</jsp:include><!-- endF #ext-container -->
			</div>

			<c:if test="${skin == 'normal' || empty skin}">
				<div class="responsive-static-menu smooth-transition scrollable-menu responsive-show">
					<nav>
						<div class="col-3">
							<div class="responsive column menu">
								<jsp:include page="/WEB-INF/aps/jsp/models/inc/frameLogin.jsp" />
								<wp:show frame="4"/>
								<wp:show frame="5"/>
							</div>
						</div>
					</nav>
		
					<nav>
						<div class="col-3">
							<div class="responsive column menu ">
								<wp:show frame="10"/>
								<wp:show frame="11"/>
								<wp:show frame="12"/>
							</div>
						</div>
					</nav>

				</div>
			</c:if>
			
		</div>
	</body>
</html>