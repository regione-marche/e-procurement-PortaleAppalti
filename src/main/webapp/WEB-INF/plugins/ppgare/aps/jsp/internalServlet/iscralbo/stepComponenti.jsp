<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<s:if test="%{#session.dettIscrAlbo.tipologia == 2}">
	<c:set var="titolo"><wp:i18n key='TITLE_PAGE_ISCRALBO_MERCATO_ELETTRONICO'/></c:set>
	<s:if test="%{!#session.dettIscrAlbo.aggiornamentoIscrizione}">
		<s:if test="%{#session.dettIscrAlbo.rti}">
			<c:set var="codiceBalloon" value="BALLOON_ISCR_CATALOGO_DETTAGLIO_RTI"/>
		</s:if>
		<s:elseif test="%{#session.dettIscrAlbo.impresa.consorzio && !#session.dettIscrAlbo.Rti}">
			<c:set var="codiceBalloon" value="BALLOON_ISCR_CATALOGO_CONSORZIATE"/>
		</s:elseif>
	</s:if>
</s:if>
<s:else>
	<c:set var="titolo"><wp:i18n key='TITLE_PAGE_ISCRALBO_ELENCO_OE'/></c:set>
	<s:if test="%{!#session.dettIscrAlbo.aggiornamentoIscrizione}">
		<s:if test="%{#session.dettIscrAlbo.rti}">
			<c:set var="codiceBalloon" value="BALLOON_ISCR_ALBO_DETTAGLIO_RTI"/>
		</s:if>
		<s:elseif test="%{#session.dettIscrAlbo.impresa.consorzio && !#session.dettIscrAlbo.Rti}">
			<c:set var="codiceBalloon" value="BALLOON_ISCR_ALBO_CONSORZIATE"/>
		</s:elseif>
	</s:if>
</s:else>

<div class="portgare-view">

	<s:if test="%{!#session.dettIscrAlbo.aggiornamentoIscrizione}">
		<h2><wp:i18n key='TITLE_PAGE_ISCRALBO_RICHIESTA_ISCRIZIONE'/> <c:out value="${titolo}"/></h2>
	</s:if>
	<s:else>
		<h2><wp:i18n key='TITLE_PAGE_ISCRALBO_AGGIORNAMENTO_ISCRIZIONE'/> <c:out value="${titolo}"/></h2>
	</s:else>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/field_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_messages.jsp" />

	<jsp:include page="stepsIscrizione.jsp">
		<jsp:param name="sessionIdObj" value="dettIscrAlbo" />
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="${codiceBalloon}"/>
	</jsp:include>

	<form action="<wp:action path="/ExtStr2/do/FrontEnd/IscrAlbo/processPageIscrAlboComponenti.action" />" method="post">
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
		
		<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/richpartbando/datiComponentiSection.jsp">
			<jsp:param name="namespace" value="IscrAlbo" />
			<jsp:param name="sessionIdObj" value="dettIscrAlbo" />
		</jsp:include>

		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/skip_main_content_from_buttons.jsp" />

		<s:if test="%{!delete}">
			<div class="azioni">
				<s:if test="%{!confirmNoConsorziate}">
					<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_previous.jsp" />
					<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_next.jsp" />
				</s:if>
				<s:else>
					<s:hidden name="noConsorziate" value="1"/>
					<wp:i18n key="BUTTON_WIZARD_PREVIOUS_ADD_CONSORZIATE" var="valuePreviousButtonConsorziate" />
					<wp:i18n key="TITLE_WIZARD_PREVIOUS_ADD_CONSORZIATE" var="titlePreviousButtonConsorziate" />
					<wp:i18n key="BUTTON_WIZARD_NEXT_NO_CONSORZIATE" var="valueNextButtonConsorziate" />
					<wp:i18n key="TITLE_WIZARD_NEXT_NO_CONSORZIATE" var="titleNextButtonConsorziate" />
					<s:submit value="%{#attr.valuePreviousButtonConsorziate}" title="%{#attr.titlePreviousButtonConsorziate}"  cssClass="button" method="editConsorziate"></s:submit>
					<s:submit value="%{#attr.valueNextButtonConsorziate}" title="%{#attr.titleNextButtonConsorziate}" cssClass="button" method="next"></s:submit>
				</s:else>
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_cancel.jsp" />
				<s:hidden name="id" value="%{id}"/>
				<s:hidden name="idDelete" value="%{idDelete}"/>
				<input type="hidden" name="ext" value="${param.ext}" />
			</div>
		</s:if>
	</form>
</div>