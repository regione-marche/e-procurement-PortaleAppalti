<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags"%>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp">
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>


<div class="portgare-view">

	<h2><wp:i18n key="TITLE_PAGE_ACCESSO_SIMULTANEO" /></h2>

<%-- 
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_AREA_PERSONALE" />
	</jsp:include>
--%>
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_messages.jsp" />
	
	<div class="question">
		<wp:i18n key="LABEL_ACCESSO_SIMULTANEO_WARNING_1" /> <s:property value="%{sessioneSimultanea[1]}" /> <wp:i18n key="LABEL_ACCESSO_SIMULTANEO_WARNING_2" /> <s:property value="%{sessioneSimultanea[2]}" /> <wp:i18n key="LABEL_ACCESSO_SIMULTANEO_WARNING_3" /> <s:property value="%{sessioneSimultanea[0]}" />.<br/> 
		<wp:i18n key="LABEL_ACCESSO_SIMULTANEO_WARNING_4" />:
		<ol>
			<li><wp:i18n key="LABEL_ACCESSO_SIMULTANEO_OPTION_1" /></li>
			<li><wp:i18n key="LABEL_ACCESSO_SIMULTANEO_OPTION_2_1" />. <strong><wp:i18n key="LABEL_ACCESSO_SIMULTANEO_OPTION_2_2" /></strong></li>
		</ol>
		<wp:i18n key="LABEL_ACCESSO_SIMULTANEO_QUESTION" />
	</div>

	<div class="azioni">
		<form action="<wp:action path="/ExtStr2/do/FrontEnd/AreaPers/cancelAccessoSimultaneo.action" />" method="post" class="azione">
			<div>
				<wp:i18n key="BUTTON_ACCESSO_SIMULTANEO_RINUNCIA" var="valueButtonRinuncia" />
				<wp:i18n key="LABEL_ACCESSO_SIMULTANEO_OPTION_1" var="titleButtonRinuncia" />
				<s:submit value="%{#attr.valueButtonRinuncia}" title="%{#attr.titleButtonRinuncia}" cssClass="button block-ui"></s:submit>
			</div>
		</form>
		<form action="<wp:action path="/ExtStr2/do/FrontEnd/AreaPers/forceAccessoSimultaneo.action" />" method="post" class="azione">
			<div>
				<wp:i18n key="BUTTON_ACCESSO_SIMULTANEO_ACCEDI" var="valueButtonAccedi" />
				<wp:i18n key="LABEL_ACCESSO_SIMULTANEO_OPTION_2_1" var="titleButtonAccedi" />
				<s:submit value="%{#attr.valueButtonAccedi}" title="%{#attr.titleButtonAccedi}" cssClass="button block-ui"></s:submit>
			</div>
		</form>
	</div>

</div>
