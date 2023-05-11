<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s"  uri="/struts-tags"%>
<%@ taglib prefix="wp" uri="aps-core.tld"%>


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp">
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">
	
	<h2><wp:i18n key="TITLE_PAGE_ALBO_FORNITORI" /></h2>
	
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_ALBO_FORNITORI" />
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />

<%-- 
dettComunicazione=${dettComunicazione}<br/>
dettComunicazione.stato=${dettComunicazione.stato}<br/>
urlAlboFornitori=${urlAlboFornitori}<br/>
--%>

	<div class="azioni">
	
		<s:if test="%{funzionNonAccessibile}">
			<wp:i18n key="LABEL_RICHIESTA_ISCRIZIONE_ALBO_NON_ACCESSIBILE" />
		</s:if>
		<s:else>
			<c:if test="${dettComunicazione == null || 
						  (dettComunicazione != null && dettComunicazione.stato != '5' && dettComunicazione.stato != '6' && dettComunicazione.stato != '7')}"> 
				<form action='<wp:action path="/ExtStr2/do/FrontEnd/Bandi/richiestaIscrizioneAlboFornitori.action" />' method="post" class="azione">
					<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
					<div>
						<wp:i18n key="BUTTON_RICHIEDI_ISCRIZIONE" var="valueRichiediButton" />
						<s:submit value="%{#attr.valueRichiediButton}" cssClass="button" />
					</div>
				</form>
			</c:if>
		
			<c:if test="${dettComunicazione != null && dettComunicazione.stato == '5'}">
				<wp:i18n key="LABEL_RICHIESTA_ISCRIZIONE_ALBO_INVIATA" />
			</c:if>
			
			<c:if test="${dettComunicazione != null && dettComunicazione.stato == '7'}">
				<wp:i18n key="LABEL_RICHIESTA_ISCRIZIONE_ALBO_ERRORE" />
			</c:if>
				
			<c:if test="${dettComunicazione != null && dettComunicazione.stato == '6'}">
				<%--
				<a href="${urlAlboFornitori}" title="<wp:i18n key="BUTTON_ACCEDI_ALBO_FORNITORI" />" class='bkg button'>
					<wp:i18n key="BUTTON_ACCEDI_ALBO_FORNITORI" />
				</a>
				--%>
				<form action='<wp:action path="/ExtStr2/do/FrontEnd/Bandi/accediAlboFornitori.action" />' method="post" class="azione">
					<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
					<div>
						<wp:i18n key="BUTTON_ACCEDI_ALBO_FORNITORI" var="valueAccediAlboButton" />
						<s:submit value="%{#attr.valueAccediAlboButton}" cssClass="button" />
					</div>
				</form>
			</c:if>
		</s:else>
		
	</div>
	
</div>