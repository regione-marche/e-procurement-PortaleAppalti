<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<s:set var="helper" value="%{#session['dettOffertaAsta']}"/>


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2><wp:i18n key='TITLE_PAGE_ASTA_CONFERMA_OFFERTA_FINALE'/></h2>
	
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/field_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />

	<jsp:include page="stepsConfermaOfferta.jsp" />

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_WIZ_CONFERMA_ASTA_OPERATORE"/>
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/datiimpresa/datiRiepilogoSection.jsp">
		<jsp:param name="sessionIdObj" value="dettAnagrImpresa" />
	</jsp:include>
	
	<div class="azioni">
		<s:if test="%{not #helper.datiInviati}">
			<form action="<wp:url page="ppgare_impr_aggdati"/>" method="post" class="azione">
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
				
				<div>
					<input type="hidden" name="fromAction" value="openPageDatiOperatore" />
					<input type="hidden" name="fromModule" value="Aste" />
					<input type="hidden" name="fromUrl" value="<wp:url />" />
					<input type="hidden" name="fromFrame" value="${param.currentFrame}" />
					<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/datiimpresa/inc/button_edit_datiimpresa.jsp" />
				</div>
			</form>
		</s:if>
		<form action="<wp:action path="/ExtStr2/do/FrontEnd/Aste/processPageDatiOperatore.action" />" method="post" class="azione">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			
			<div>
				<input type="hidden" name="page" value="datioperatore"/>
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_next.jsp" />
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_cancel.jsp" />
			</div>
		</form>
	</div>
</div>