<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<s:set var="sessionId" value="%{'dettAnagrImpresa'}"/>
<s:set var="helper" value="%{#session[#sessionId]}"/>


<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/send_blockUI.jsp"/>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<s:url id="urlPdf" namespace="/do/FrontEnd/DatiImpr" action="createPdf" />

<div class="portgare-view">

	<h2><wp:i18n key="TITLE_PAGE_EDIT_DATI_OE" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/field_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />

	<jsp:include page="stepsAggiornamento.jsp" />

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
			<jsp:param name="keyMsg" value="BALLOON_AGG_IMPRESA_RIEPILOGO"/>
	</jsp:include>

	<form action="<wp:action path="/ExtStr2/do/FrontEnd/DatiImpr/processPageRiepilogo.action" />" method="post">
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
		
		<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/datiimpresa/datiRiepilogoSection.jsp">
			<jsp:param name="sessionIdObj" value="dettAnagrImpresa" />
		</jsp:include>

		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/skip_main_content_from_buttons.jsp" />
		
		<s:if test="%{#helper.mailVariataDopoInvio}">
			<div class="note">
				<wp:i18n key="LABEL_MESSAGE_WARNING" />: <wp:i18n key="LABEL_REGISTRA_OE_MAIL_VARIATA_DOPO_INVIO_1" /> <strong><s:property value="%{#helper.mailUtenteImpostata}"/></strong> 
				<wp:i18n key="LABEL_REGISTRA_OE_MAIL_VARIATA_DOPO_INVIO_2" />
			</div>
		</s:if>
		<s:elseif test="%{#helper.mailVariata}">
			<div class="note">
				<wp:i18n key="LABEL_MESSAGE_WARNING" />: <wp:i18n key="LABEL_REGISTRA_OE_MAIL_VARIATA_1" /> <strong><s:property value="%{#helper.mailUtenteImpostata}"/></strong>. 
				<wp:i18n key="LABEL_REGISTRA_OE_MAIL_VARIATA_2" />
			</div>
		</s:elseif>

		<div class="azioni">
			<input type="hidden" name="ext" value="${param.ext}" />
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_previous.jsp" />
			<%-- 
			input type="button" id="createPdf" value="Stampa anagrafica operatore economico" title="Scarica il PDF con il dettaglio completo dei dati dell'operatore economico" onclick="document.location.href='${urlPdf}'" class="button" style="visibility: hidden;"/ 
			--%>
			<s:if test="!sendBlocked">
				<wp:i18n key="BUTTON_SEND" var="valueSendButton" />
				<wp:i18n key="TITLE_EDIT_DATI_OE_SEND" var="titleSendButton" />
				<s:submit value="%{#attr.valueSendButton}" title="%{#attr.titleSendButton}"  cssClass="button block-ui" method="send"></s:submit>
			</s:if>
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_cancel.jsp" />
		</div>
	</form>
</div>