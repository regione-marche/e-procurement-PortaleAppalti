<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix = "fn" uri = "http://java.sun.com/jsp/jstl/functions" %>

<c:set var="pathProcedura" value="/ExtStr2/do/FrontEnd/Contratti/dettaglioStipulaContratti.action"/>

<c:set var="title"><wp:i18n key="TITLE_PAGE_CONFERMA_INVIO_STIPULA" /></c:set>


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">
	
	<h2>${title}</h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
	
	<s:if test="! hasActionErrors()">
		<p><wp:i18n key="LABEL_CONFERMA_INVIO_STIPULA_SUCCESS" /></p>
	</s:if>

	<p>	
	<s:if test="%{codiceSistema == 'LAPISOPERA'}"> 
		${LABEL_RICHIESTA_CON_ID}
	</s:if>
	<s:else>
		<wp:i18n key="LABEL_RICHIESTA_INVIATA_IL" /> <s:date name="dataInvio" format="dd/MM/yyyy HH:mm:ss" /> <s:if test="dataProtocollo != null"><wp:i18n key="LABEL_RICHIESTA_PROTOCOLLATA_IL" /> <s:property value="dataProtocollo"/></s:if>
		<s:if test="%{presentiDatiProtocollazione}"> <wp:i18n key="LABEL_CON_ANNO" /> <s:property value="annoProtocollo"/> <wp:i18n key="LABEL_E_NUMERO" /> <s:property value="numeroProtocollo"/></s:if>
	</s:else>
	.
	</p>

	<s:if test="%{msgErrore != null || !mailSent}">
		<s:if test="%{msgErrore != null}">
			<p><wp:i18n key="LABEL_MESSAGE_WARNING" />: <s:property value="%{msgErrore}"/></p>
		</s:if>
		<s:elseif test="%{!mailSent}">
			<p><wp:i18n key="LABEL_MESSAGE_WARNING" />: <s:property value="%{msgErroreMail}"/></p>
		</s:elseif>
	</s:if>
	
	<div class="azioni">
		<form action='<wp:action path="${pathProcedura}" />' method="post">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			<input type="hidden" name="id" value='<s:property value="id"/>' />
			<input type="hidden" name="codice" value='<s:property value="codiceStipula"/>' />
			
			<div>
				<wp:i18n key="BUTTON_BACK_PREVIOUS_STEP" var="valueBackToStep" />
				<s:submit value="%{#attr.valueBackToStep}" title="%{#attr.valueBackToStep}" cssClass="button block-ui"/>
			</div>
		</form>
	</div>
</div>