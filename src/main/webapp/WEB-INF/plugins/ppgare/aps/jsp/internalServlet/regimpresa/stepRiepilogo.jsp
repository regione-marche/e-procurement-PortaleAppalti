<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>

<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/send_blockUI.jsp" />

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<s:url id="urlPdf" namespace="/do/FrontEnd/DatiImpr" action="createPdfFromRegImpr" />
<es:checkCustomization var="visConsensoPrivacy" objectId="REG-IMPRESA" attribute="PRIVACY" feature="VIS" />
<es:checkCustomization var="bloccaLoginUgualeCodiceFiscale" objectId="REG-IMPRESA" attribute="LOGINCF" feature="ACT" />

<div class="portgare-view">

	<h2><wp:i18n key="TITLE_PAGE_REGISTRA_OE" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/field_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />

	<jsp:include page="stepsRegistrazione.jsp" />

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_REG_IMPRESA_RIEPILOGO"/>
	</jsp:include>

	<form action="<wp:action path="/ExtStr2/do/FrontEnd/RegistrImpr/processPageRiepilogo.action" />" method="post">
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
		
		<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/datiimpresa/datiRiepilogoSection.jsp">
			<jsp:param name="sessionIdObj" value="dettRegistrImpresa" />
		</jsp:include>

		<fieldset>
			<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_REGISTRA_OE_CREDENZIALI" /><c:if test="${visConsensoPrivacy}"> - <wp:i18n key="LABEL_PRIVACY" /></c:if></legend>
	
			<div class="fieldset-row first-row">
				<div class="label">
				<c:choose>
					<c:when test="${bloccaLoginUgualeCodiceFiscale}">
						<label><wp:i18n key="USERNAME" /> : </label>
					</c:when>
					<c:otherwise>
						<label><wp:i18n key="USERNAME" /> : </label>
					</c:otherwise>
				</c:choose>
				</div>
				<div class="element">
					<s:property value="%{#session.dettRegistrImpresa.username}"/>
					<%--
					<c:if test="${bloccaLoginUgualeCodiceFiscale}">
					- <s:property value="%{#session.dettRegistrImpresa.datiPrincipaliImpresa.ragioneSociale}"/>
					</c:if>
					--%>
				</div>
			</div>

			<div class="fieldset-row row<c:if test="${!visConsensoPrivacy}"> lastrow</c:if>">
				<div class="label">
					<label><wp:i18n key="LABEL_REGISTRA_OE_RICHIESTA_DA" /> : </label>
				</div>
				<div class="element">
					<s:property value="%{#session.dettRegistrImpresa.soggettoRichiedente}"/>
				</div>
			</div>

			<c:if test="${visConsensoPrivacy}">
				<div class="fieldset-row last-row">
					<div class="label">
						<label><wp:i18n key="LABEL_PRIVACY" /> : </label>
					</div>
					<div class="element">
						<s:if test="%{#session.dettRegistrImpresa.privacy == 1}"><wp:i18n key="LABEL_YES" /></s:if>
						<s:else><wp:i18n key="LABEL_NO" /></s:else>
					</div>
				</div>
			</c:if>
		</fieldset>
	
		<s:set var="sessionId" value="%{'dettRegistrImpresa'}"/>
		<s:if test="%{#session[#sessionId].mailVariataDopoInvio}">
			<div class="note">
				<wp:i18n key="LABEL_MESSAGE_WARNING" />: <wp:i18n key="LABEL_REGISTRA_OE_MAIL_VARIATA_DOPO_INVIO_1" /> <strong><s:property value="%{#session[#sessionId].mailUtenteImpostata}"/></strong> 
				<wp:i18n key="LABEL_REGISTRA_OE_MAIL_VARIATA_DOPO_INVIO_2" />
			</div>
		</s:if>
		<s:elseif test="%{#session[#sessionId].mailVariata}">
			<div class="note">
				<wp:i18n key="LABEL_MESSAGE_WARNING" />: <wp:i18n key="LABEL_REGISTRA_OE_MAIL_VARIATA_1" /> <strong><s:property value="%{#session[#sessionId].mailUtenteImpostata}"/></strong>. 
				<wp:i18n key="LABEL_REGISTRA_OE_MAIL_VARIATA_2" />
			</div>
		</s:elseif>
		
		<div class="azioni">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_previous.jsp" />
			<wp:i18n key="BUTTON_SEND" var="valueSendButton" />
			<wp:i18n key="TITLE_REGISTRA_OE_INVIA_REGISTRAZIONE" var="titleSendButton" />
			<s:submit value="%{#attr.valueSendButton}" title="%{#attr.titleSendButton}"  cssClass="button block-ui" method="send"></s:submit>
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_cancel.jsp" />
		</div>
	</form>
</div>