<%@ taglib prefix="wp" uri="aps-core.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<c:set var="descrizioneHint"><wp:i18n key="LABEL_INSERIRE_DESCRIZIONE_PER_ALLEGARE_DOC" /></c:set>
<s:set var="helper" value="%{#session['nuovaComunicazione']}"/>
<s:set var="helperDocumenti" value="%{#session['nuovaComunicazione'].documenti}"/>

<s:set var="rettifica" value="%{#helper.modelloRettifica}"/>
<s:set var="isRichiestaRettifica" value="%{#helper.modello == 10 || #helper.modello == 15}" />
<s:set var="isInvioRettifica" value="%{#rettifica && !#isRichiestaRettifica}"/>

<s:set var="soccorsoIstruttorio" value="%{#helper.modello > 0 && !#rettifica}"/>

<s:if test="%{#rettifica}" >
	<c:set var="title"><wp:i18n key="TITLE_RETTIFICA_OFFERTA" /></c:set>
	<c:set var="balloon" value="BALLOON_WIZ_RETTIFICA_DOCUMENTI" />
	<s:set var="questionElimina" value="%{deleteAllegato || deleteAllegatoRichiesto}"/>
	<c:set var="hrefAzioni" value="/ExtStr2/do/FrontEnd/Comunicazioni/processPageDocumentiNuovaRettifica.action" />	
</s:if>
<s:elseif test="%{#helper.modello > 0}" >
	<c:set var="title"><wp:i18n key="TITLE_SOCCORSO_ISTRUTTORIO_NUOVO" /></c:set>
	<!-- <c:set var="balloon" value="BALLOON_WIZ_SOCCORSO_DOCUMENTI" /> -->
	<c:set var="balloon" value="BALLOON_WIZ_COMUNICAZIONE_DOCUMENTI" />
	<s:set var="questionElimina" value="%{deleteAllegato || deleteAllegatoRichiesto}"/>
	<c:set var="hrefAzioni" value="/ExtStr2/do/FrontEnd/Comunicazioni/processPageDocumentiNuovoSoccorso.action" />	
</s:elseif>
<s:else>
	<c:set var="title"><wp:i18n key="TITLE_COMUNICAZIONI_NUOVA" /></c:set>
	<c:set var="balloon" value="BALLOON_WIZ_COMUNICAZIONE_DOCUMENTI" />
	<s:set var="questionElimina" value="%{deleteAllegato}"/>
	<c:set var="hrefAzioni" value="/ExtStr2/do/FrontEnd/Comunicazioni/processPageDocumentiNuovaComunicazione.action" />
</s:else>


<%-- <script type="text/javascript" src='<wp:resourceURL/>static/js/ppgare/uploadFile.js'></script> --%>
<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/internalServlet/fileupload/uploadFile.jsp" />

<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/send_blockUI.jsp" />

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp">
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2>${title}</h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/field_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_messages.jsp" />
	
	<jsp:include page="stepsComunicazione.jsp" />
	
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="${balloon}"/>
	</jsp:include>


	<s:if test="%{#soccorsoIstruttorio}" >
		<%-- SOCCORSO ISTRUTTORIO --%>
		<jsp:include page="inc/documentiSoccorso.jsp"/>
	</s:if>
	<s:else>
		<%-- RICHIESTA RETTIFICA/RETTIFICA --%>
		<s:if test="%{! #rettifica}" >
			<jsp:include page="inc/documentiComunicazione.jsp"/>
		</s:if>
		<s:elseif test="%{#isInvioRettifica}" >
			<jsp:include page="inc/documentiComunicazione.jsp"/>
		</s:elseif>
	</s:else>
	
	
	<s:if test="%{!#questionElimina}">
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/skip_main_content_from_buttons.jsp" />
	
		<form action="<wp:action path="${hrefAzioni}" />" method="post">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			<div class="azioni">
				<input type="hidden" name="codice" value="<s:property value="%{codice}"/>"/>
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_previous.jsp" />
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_next.jsp" />
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_cancel.jsp" />
			</div>
		</form>	
	</s:if>
</div>
