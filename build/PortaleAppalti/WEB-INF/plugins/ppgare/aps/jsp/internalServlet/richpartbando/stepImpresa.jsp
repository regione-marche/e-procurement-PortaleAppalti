<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<s:if test="%{#session.dettPartecipGara.tipoEvento == 1}">
	<c:set var="titolo"><wp:i18n key='TITLE_PAGE_RICHPART_PRESENTA_PARTECIPAZIONE'/></c:set>
	<c:set var="codiceBalloon" value="BALLOON_PART_GARA_IMPRESA"/>
</s:if>
<s:elseif test="%{#session.dettPartecipGara.tipoEvento == 2}">
	<c:set var="titolo"><wp:i18n key='TITLE_PAGE_RICHPART_PRESENTA_OFFERTA'/></c:set>
	<c:set var="codiceBalloon" value="BALLOON_INVIO_OFFERTA_IMPRESA"/>
</s:elseif>
<s:else>
	<c:set var="titolo"><wp:i18n key='TITLE_PAGE_RICHPART_PRESENTA_DOCUMENTAZIONE'/></c:set>
	<c:set var="codiceBalloon" value="BALLOON_INVIO_DOC_ART48_IMPRESA"/>
</s:else>	

<s:url id="urlPdf" namespace="/do/FrontEnd/DatiImpr" action="createPdfFromRichPart" />


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2>${titolo}</h2>
	
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/field_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_messages.jsp" />

	<jsp:include page="stepsPartecipazione.jsp" />

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="${codiceBalloon}"/>
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/datiimpresa/datiRiepilogoSection.jsp">
		<jsp:param name="sessionIdObj" value="dettPartecipGara" />
	</jsp:include>

	<div class="azioni">
		<form action="<wp:url page="ppgare_impr_aggdati"/>" method="post" class="azione">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			
			<div>
				<input type="hidden" name="fromUrl" value="<wp:url />" />
				<input type="hidden" name="fromModule" value="RichPartBando" />
				<input type="hidden" name="fromFrame" value="${param.currentFrame}" />
				<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/datiimpresa/inc/button_edit_datiimpresa.jsp" />
			</div>
		</form>
		<form action="<wp:action path="/ExtStr2/do/FrontEnd/RichPartBando/processPageImpresa.action" />" method="post" class="azione">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			
			<div>
				<%--
				<s:submit value="Avanti >" title="Vai al passo successivo" cssClass="button" method="next"></s:submit>
				 --%>
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_next.jsp" />
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_cancel.jsp" />
			</div>
		</form>
	</div>
</div>