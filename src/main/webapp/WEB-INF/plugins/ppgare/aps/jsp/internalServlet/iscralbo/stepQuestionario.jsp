<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<%-- forza il refresh delle librerie js ogni giorno --%>
<%
	String file = application.getRealPath("/") + "WEB-INF/PA_VER.TXT";
	String webappVersion = org.apache.commons.io.FileUtils.readFileToString(new java.io.File(file), "ISO-8859-1");
%>
<c:set var="versione"><%=webappVersion%></c:set>


<s:if test="%{#session.dettIscrAlbo != null}">
	<s:set name="sessionIdObj" value="'dettIscrAlbo'" />
</s:if>
<s:else>
	<s:set name="sessionIdObj" value="'dettRinnAlbo'" />
</s:else>
<s:set name="helper" value="%{#session[#sessionIdObj]}" />
<s:set var="documenti" value="%{#helper.documenti}"/>


<s:if test="%{#helper.tipologia == 2}">
	<c:set var="titolo"><wp:i18n key='TITLE_PAGE_ISCRALBO_MERCATO_ELETTRONICO'/></c:set>
</s:if>
<s:else>
	<c:set var="titolo"><wp:i18n key='TITLE_PAGE_ISCRALBO_ELENCO_OE'/></c:set>
</s:else>

<%--
	Gestione blocco di attesa sulla pagina
	utilizzare le seguenti funzioni per visualizzare o nascondere il pannello di blocco/attesa
		showBlockUIMessage() 
		hideBlockUIMessage()
 --%>
<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/send_blockUI.jsp" />


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<s:if test="%{!#helper.aggiornamentoIscrizione && !#helper.rinnovoIscrizione}">
		<h2><wp:i18n key='TITLE_PAGE_ISCRALBO_RICHIESTA_ISCRIZIONE'/> <c:out value="${titolo}" /></h2>
	</s:if>
	<s:elseif test="%{#helper.rinnovoIscrizione}">
		<h2><wp:i18n key='TITLE_PAGE_ISCRALBO_RICHIESTA_ISCRIZIONE'/> <c:out value="${titolo}" /></h2>
	</s:elseif>
	<s:else>
		<h2><wp:i18n key='TITLE_PAGE_ISCRALBO_AGGIORNAMENTO_ISCRIZIONE'/> <c:out value="${titolo}" /></h2>
	</s:else>


	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/field_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_messages.jsp" />

	<jsp:include page="stepsIscrizione.jsp">
		<jsp:param name="sessionIdObj" value="${sessionIdObj}" />
	</jsp:include>

<%-- ******************************************************************************** --%>
<%-- ******************************************************************************** --%>

	<c:set var="token">${strutsTokenName}=${requestScope[strutsTokenName]}</c:set>

	<c:set var="queryParams">&codice=<s:property value="%{codice}"/>&ext=${param.ext}&${token}
	</c:set>
	
	<s:url id="urlGetUuid" namespace="/do/FrontEnd/IscrAlbo" action="newUuidQC" />
	<s:url id="urlAttachmentUpload" namespace="/do/FrontEnd/IscrAlbo" action="addQCDocument" />
	<s:url id="urlAttachmentDownload" namespace="/do/FrontEnd/IscrAlbo" action="downloadQCDocument" />
	<s:url id="urlAttachmentDelete" namespace="/do/FrontEnd/IscrAlbo" action="deleteQCDocument" />	

<s:if test="%{ !#helper.aggiornamentoIscrizione }">
	<%-- ISCRIZIONE --%>
	<s:url id="urlGeneratePDF" namespace="/do/FrontEnd/IscrAlbo" action="generateQCPDFDataSummaryIscr">
		<s:param name="urlPage">${currentPageUrl}</s:param>
		<s:param name="currentFrame">${param.currentFrame}</s:param>
	</s:url>
</s:if>
<s:else>
	<%-- AGGIORNAMENTO ISCRIZIONE --%>
	<s:url id="urlGeneratePDF" namespace="/do/FrontEnd/IscrAlbo" action="generateQCPDFDataSummaryAgg">
		<s:param name="urlPage">${currentPageUrl}</s:param>
		<s:param name="currentFrame">${param.currentFrame}</s:param>
	</s:url>
</s:else>

	<s:url id="urlSaveForm" namespace="/do/FrontEnd/IscrAlbo" action="saveQCForm" />
	<s:url id="urlLoadForm" namespace="/do/FrontEnd/IscrAlbo" action="loadQCForm" />	
	<c:set var="urlActionPrev"><wp:action path="/ExtStr2/do/FrontEnd/IscrAlbo/prevQC.action" />${queryParams}</c:set>	
	<c:set var="urlActionNext"><wp:action path="/ExtStr2/do/FrontEnd/IscrAlbo/nextQC.action" />${queryParams}</c:set>
	<c:set var="urlActionCancel"><wp:action path="/ExtStr2/do/FrontEnd/IscrAlbo/cancelQC.action" />${queryParams}</c:set>		

	
	<script type="text/javascript">
		function saveCompleted() {
			//...
		}

		function setFormLocked(locked) {
			if(locked) showBlockUIMessage();
			else hideBlockUIMessage();
		}
	</script>
		
	<script>
		// App Parameters
		window.APP_DATA = {
			modeDevLocal: false,
			modePreview: false,
			language: '<wp:info key="currentLang" />',
			idCom: '<s:property value="%{#documenti.idComunicazione}" />',
			methods: {
				setFormLocked: setFormLocked
			},
			urls: {
				assets: '<wp:resourceURL/>static/js/qcompiler/assets',
				getUuid: '<s:property value="%{#urlGetUuid}" />',
				attachmentUpload: '<s:property value="%{#urlAttachmentUpload}" />',
				attachmentDownload: '<s:property value="%{#urlAttachmentDownload}" />',
				attachmentDelete: '<s:property value="%{#urlAttachmentDelete}" />',
				generatePDF: '<s:property value="%{#urlGeneratePDF}" />',
				saveForm: '<s:property value="%{#urlSaveForm}" />',
				loadForm: '<s:property value="%{#urlLoadForm}" />',
				actionPrev: `${urlActionPrev}`.replace(/&amp;/g, '&'),
				actionNext: `${urlActionNext}`.replace(/&amp;/g, '&'),
				actionCancel: `${urlActionCancel}`.replace(/&amp;/g, '&'),
			}
		};
	</script>
	
	<wp:headInfo type="CSS" info="../js/qcompiler/styles.css" />
	<app-root></app-root>
	<script src="<wp:resourceURL/>static/js/qcompiler/runtime.js?v=${versione}" defer></script>
	<script src="<wp:resourceURL/>static/js/qcompiler/polyfills.js?v=${versione}" defer></script>
	<script src="<wp:resourceURL/>static/js/qcompiler/main.js?v=${versione}" defer></script></body>
 	
 	<br/>
 	<br/>
 	<br/>
 	<hr/>

</div> 	
<%-- ******************************************************************************** --%>
<%-- ******************************************************************************** --%>

