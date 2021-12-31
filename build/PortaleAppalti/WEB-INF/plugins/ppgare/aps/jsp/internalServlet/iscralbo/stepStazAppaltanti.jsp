<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<s:if test="%{!#session.dettIscrAlbo.aggiornamentoIscrizione}">
	<c:set var="codiceBalloon" value="BALLOON_ISCR_ALBO_SA"/>
</s:if>
<s:else>
	<c:set var="codiceBalloon" value="BALLOON_AGG_ISCR_ALBO_SA"/>
</s:else>	

<s:if test="%{#session.dettIscrAlbo.tipologia == 2}">
	<c:set var="titolo"><wp:i18n key='TITLE_PAGE_ISCRALBO_CATALOGO'/></c:set>
</s:if>
<s:else>
	<c:set var="titolo"><wp:i18n key='TITLE_PAGE_ISCRALBO_ELENCO_OE' /></c:set>
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

	<jsp:include page="stepsIscrizione.jsp" />

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="${codiceBalloon}"/>
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/skip_form_buttons.jsp" />

	<form action="<wp:action path="/ExtStr2/do/FrontEnd/IscrAlbo/processPageSA.action" />" method="post">
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
		
		<fieldset>
			<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_STAZIONI_APPALTANTI" /></legend>

			<s:iterator value="maps['stazioniAppaltanti']" var="stazione" status="status">
				<div class="fieldset-row <s:if test="#status.first">first-row</s:if><s:if test="#status.last">last-row</s:if>">
					<div class="label">
						<label for="saSelezionata%{key}"><s:property value="value" /></label>
					</div>
					<div class="element">
						<s:checkbox name="saSelezionata" fieldValue="%{key}" id="saSelezionata%{key}" 
												value="%{checkSA[#status.index]}" ></s:checkbox>
					</div>
				</div>
			</s:iterator>
		</fieldset>	

		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/skip_main_content_from_buttons.jsp" />

		<div class="azioni">
			<input type="hidden" name="page" value="sa"/>
			<input type="hidden" name="ext" value="${param.ext}" />
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_previous.jsp" />
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_next.jsp" />
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_cancel.jsp" />
		</div>
	</form>
</div>