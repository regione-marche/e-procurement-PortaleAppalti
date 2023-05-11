<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<s:set var="buste" value="%{#session.dettaglioOffertaGara}"/>
<s:set var="partecipazione" value="%{#buste.bustaPartecipazione.helper}"/>
<!-- OBSOLETO <s:set var="riepilogoBuste" value="%{#session.riepilogoBuste}" /> -->
<s:set var="riepilogoBuste" value="%{#buste.bustaRiepilogo.helper}" />

<s:if test="%{tipoBusta == BUSTA_AMMINISTRATIVA}">
	<s:set var="documenti" value="%{#buste.bustaAmministrativa.helperDocumenti}"/>
	<c:set var="codiceBalloon" value="BALLOON_BUSTA_AMMINISTRATIVA"/>
	<c:set var="titolo"><wp:i18n key='TITLE_PAGE_GARETEL_DOCUMENTI_BUSTA_AMMINISTRATIVA'/></c:set>
</s:if>
<s:if test="%{tipoBusta == BUSTA_TECNICA}">
	<s:set var="documenti" value="%{#buste.bustaTecnica.helperDocumenti}"/>
	<c:set var="codiceBalloon" value="BALLOON_BUSTA_TECNICA"/>
	<c:set var="titolo"><wp:i18n key='TITLE_PAGE_GARETEL_DOCUMENTI_BUSTA_TECNICA'/></c:set>
</s:if>
<s:if test="%{tipoBusta == BUSTA_ECONOMICA}">
	<s:set var="documenti" value="%{#buste.bustaEconomica.helperDocumenti}"/>
	<c:set var="codiceBalloon" value="BALLOON_BUSTA_ECONOMICA"/>
	<c:set var="titolo"><wp:i18n key='TITLE_PAGE_GARETEL_DOCUMENTI_BUSTA_ECONOMICA'/></c:set>
</s:if>
<s:if test="%{tipoBusta == BUSTA_PRE_QUALIFICA}">
	<s:set var="documenti" value="%{#buste.bustaPrequalifica.helperDocumenti}"/>
	<c:set var="codiceBalloon" value="BALLOON_BUSTA_PREQUALIFICA"/>
	<c:set var="titolo"><wp:i18n key='TITLE_PAGE_GARETEL_DOCUMENTI_BUSTA_PREQUALIFICA'/></c:set>
</s:if>

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
<h2>${titolo} [${codice}]</h2>
<div class="portgare-view">

<%-- ******************************************************************************** --%>
<%-- ******************************************************************************** --%>

	<c:set var="token">${strutsTokenName}=${requestScope[strutsTokenName]}</c:set>

	<c:set var="queryParams">
	&codice=<s:property value="%{codice}"/>
	&codiceGara=<s:property value="%{codiceGara}" />
	&progressivoOfferta=<s:property value="%{#riepilogoBuste.progressivoOfferta}" />
	&tipoBusta=<s:property value="%{#documenti.tipoBusta}" />
	&operazione=<s:property value="%{#documenti.operazione}" />
	&ext=${param.ext}
	&${token}
	</c:set>
	
	<s:url id="urlGetUuid" namespace="/do/FrontEnd/GareTel" action="newUuidQC" />
	<s:url id="urlAttachmentUpload" namespace="/do/FrontEnd/GareTel" action="addQCDocument" />
	<s:url id="urlAttachmentDownload" namespace="/do/FrontEnd/GareTel" action="downloadQCDocument" />
	<s:url id="urlAttachmentDelete" namespace="/do/FrontEnd/GareTel" action="deleteQCDocument" />
	<s:url id="urlGeneratePDF" namespace="/do/FrontEnd/GareTel" action="generateQCPDFDataSummary">
		<s:param name="urlPage">${currentPageUrl}</s:param>
		<s:param name="currentFrame">${param.currentFrame}</s:param>
	</s:url>
	<s:url id="urlSaveForm" namespace="/do/FrontEnd/GareTel" action="saveQCForm" />
	<s:url id="urlLoadForm" namespace="/do/FrontEnd/GareTel" action="loadQCForm" />
	<c:set var="urlActionNext"><wp:action path="/ExtStr2/do/FrontEnd/GareTel/nextQC.action" />${queryParams}</c:set>
	<c:set var="urlActionCancel"><wp:action path="/ExtStr2/do/FrontEnd/GareTel/cancelQC.action" />${queryParams}</c:set>
	
	<%-- <s:if test="%{iddocdig != null}"> --%>
    <s:url action="redirectToDGUE" var="urlRedirect"></s:url>

    <c:set var="urlDGUEFirst"><s:property value="%{#urlRedirect}" escape="false"/>?codice=<s:property value="%{codice}"/>&amp;codiceGara=<s:property value="%{#riepilogoBuste.codiceGara}"/>&amp;iddocdig=<s:property value="%{iddocdig}"/></c:set>
    <c:set var="urlDGUESecond"><s:property value="%{dgueLinkSecondHref}" escape="false"/></c:set>
    <c:set var="urlDGUEThird"><s:property value="%{dgueLinkThirdHref}" escape="false"/></c:set>
	<%-- </s:if> --%>
	
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
				actionNext: `${urlActionNext}`.replace(/&amp;/g, '&'),
				actionCancel: `${urlActionCancel}`.replace(/&amp;/g, '&'),
				mdgueLink1: `${urlDGUEFirst}`.replace(/&amp;/g, '&'),
				mdgueLink2: `${urlDGUESecond}`.replace(/&amp;/g, '&'),
				mdgueLink3: `${urlDGUEThird}`.replace(/&amp;/g, '&'),
			}
		};
	</script>
	
	<wp:headInfo type="CSS" info="../js/qcompiler/styles.css" />
	<app-root></app-root>
	<script src="<wp:resourceURL/>static/js/qcompiler/runtime.js" defer></script>
	<script src="<wp:resourceURL/>static/js/qcompiler/polyfills.js" defer></script>
	<script src="<wp:resourceURL/>static/js/qcompiler/main.js" defer></script></body>
 	
 	<br/>
 	<br/>
 	<br/>
 	<hr/>
 	
<%-- ******************************************************************************** --%>
<%-- ******************************************************************************** --%>


<c:if test="${1 == 0}">
	<br/>
	<br/>
	<br/>
	<br/>
	<br/>
	<br/>

	<s:set var="formDebug">
	{  "sysVariables": [    {      "values": "",      "name": "indirizzoImpresa"    },    {      "values": "",      "name": "ragioneSocialeImpresa"    },    {      "values": "",      "name": "tipoImpresa"    }  ],  "survey": {    "title": "primo questionario vvvvvv bbbbbb",    "subtitle": "primo sottotitolo",    "logoUrl": "logo url qua",    "nextComponentSequence": 13,    "structures": [      {        "structureCode": "struttura_1",        "title": "Struttura 1",        "hidden": null,        "sections": [          {            "sectionCode": "sezione_2",            "title": "Sezione 1",            "description": "descrizione sezione 1",            "cardinalityAllowed": false,            "cardinalityMax": null,            "questionsRequired": false,            "hidden": null,            "groups": [              {                "groupCode": "gruppo_3",                "title": "Gruppo domande 1",                "description": "descrizione gruppo 1",                "cardinalityAllowed": false,                "cardinalityMax": null,                "questionsRequired": false,                "hidden": null,                "questions": [                  {                    "questionCode": "quesito_11",                    "title": "Domanda breve",                    "description": "ccccccc",                    "valuesPlaceholder": "",                    "valuesDescription": "",                    "cardinalityAllowed": false,                    "cardinalityMax": -1,                    "required": false,                    "controlType": "text",                    "inputType": "",                    "dataType": "",                    "hidden": false,                    "options": "",                    "sysVariableName": null,                    "decimalPrecision": 2,                    "visibilityRules": []                  },                  {                    "questionCode": "quesito_12",                    "title": "Testo della domanda",                    "description": "",                    "valuesPlaceholder": "",                    "valuesDescription": "",                    "cardinalityAllowed": false,                    "cardinalityMax": -1,                    "required": false,                    "controlType": "dropdown",                    "inputType": "",                    "dataType": "",                    "hidden": false,                    "options": "",                    "sysVariableName": null,                    "decimalPrecision": 2,                    "visibilityRules": []                  }                ]              },              {                "groupCode": "gruppo_5",                "title": "Gruppo domande 2",                "description": null,                "cardinalityAllowed": false,                "cardinalityMax": null,                "questionsRequired": false,                "hidden": null,                "questions": [                  {                    "questionCode": "quesito_6",                    "title": "Numero",                    "description": "",                    "valuesPlaceholder": "",                    "valuesDescription": "",                    "cardinalityAllowed": false,                    "cardinalityMax": -1,                    "required": false,                    "controlType": "integer",                    "inputType": "",                    "dataType": "",                    "hidden": false,                    "options": "",                    "sysVariableName": null,                    "decimalPrecision": 2,                    "visibilityRules": []                  },                  {                    "questionCode": "quesito_7",                    "title": "Elemento multiplo",                    "description": "",                    "valuesPlaceholder": "",                    "valuesDescription": "",                    "cardinalityAllowed": true,                    "cardinalityMax": -1,                    "required": false,                    "controlType": "text",                    "inputType": "",                    "dataType": "",                    "hidden": false,                    "options": "",                    "sysVariableName": null,                    "decimalPrecision": 2,                    "visibilityRules": []                  }                ]              },              {                "groupCode": "gruppo_8",                "title": "Carica files qui",                "description": null,                "cardinalityAllowed": false,                "cardinalityMax": null,                "questionsRequired": false,                "hidden": null,                "questions": [                  {                    "questionCode": "quesito_9",                    "title": "File da caricare con cardinalita'",                    "description": "",                    "valuesPlaceholder": "",                    "valuesDescription": "",                    "cardinalityAllowed": true,                    "cardinalityMax": -1,                    "required": false,                    "controlType": "file",                    "inputType": "",                    "dataType": "",                    "hidden": false,                    "options": "",                    "sysVariableName": null,                    "decimalPrecision": 2,                    "visibilityRules": []                  },                  {                    "questionCode": "quesito_10",                    "title": "File senza cardinalita'",                    "description": "",                    "valuesPlaceholder": "",                    "valuesDescription": "",                    "cardinalityAllowed": false,                    "cardinalityMax": -1,                    "required": false,                    "controlType": "file",                    "inputType": "",                    "dataType": "",                    "hidden": false,                    "options": "",                    "sysVariableName": null,                    "decimalPrecision": 2,                    "visibilityRules": []                  }                ]              }            ]          }        ]      }    ]  }}
	</s:set>

	<div >

		<div>
			<form action="${urlActionNext}" method="post" >
			<!--
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
				<s:hidden name="codice" value="%{codice}" />
				<s:hidden name="codiceGara" value="%{codiceGara}" />
				<s:hidden name="progressivoOfferta" value="%{#riepilogoBuste.progressivoOfferta}" />
				<s:hidden name="tipoBusta" value="%{#documenti.tipoBusta}" />
				<s:hidden name="operazione" value="%{#documenti.operazione}" />
				<input type="hidden" name="ext" value="${param.ext}" />	
			-->
				<wp:i18n key="BUTTON_WIZARD_NEXT" var="valueNextButton" />
				<wp:i18n key="TITLE_WIZARD_NEXT" var="titleNextButton" />
				<s:submit value="%{#attr.valueNextButton}" title="%{#attr.titleNextButton}" cssClass="button" ></s:submit>				
			</form>
			
			<form action="${urlActionCancel}" method="post" >
			<!--
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
				<s:hidden name="codice" value="%{codice}" />
				<s:hidden name="codiceGara" value="%{codiceGara}" />
				<s:hidden name="progressivoOfferta" value="%{#riepilogoBuste.progressivoOfferta}" />
				<s:hidden name="tipoBusta" value="%{#documenti.tipoBusta}" />
				<s:hidden name="operazione" value="%{#documenti.operazione}" />
				<input type="hidden" name="ext" value="${param.ext}" />	
			-->
				<wp:i18n key="BUTTON_WIZARD_BACK_TO_MENU" var="valueBackToMenuButton" />
				<s:submit value="%{#attr.valueBackToMenuButton}" title="%{#attr.valueBackToMenuButton}" cssClass="button" ></s:submit>
			</form>
		</div>



		<br/>
		<br/>
		********************************************************************************
		<c:set var="imgPath"><wp:resourceURL/>static/css/img/</c:set>
		
		<div>
			TEST ELENCO DOCUMENTI - TEST DOWNLOAD DOC - TEST ELIMINA DOC<br/>
			<s:url id="urlDownloadDoc" namespace="/do/FrontEnd/GareTel" action="downloadQCDocument" />
			<s:url id="urlDeleteDoc" namespace="/do/FrontEnd/GareTel" action="deleteQCDocument" />
			
			<table id="tableDocumenti" class="wizard-table" >
				<thead/>
				<tbody>
					<s:iterator value="#documenti.docUlterioriDesc" var="descDocUlteriore" status="status">
						<s:if test="%{#documenti.docUlterioriVisibile.get(#status.index)}" >
							<tr>
								<td>
									<s:property value="%{#descDocUlteriore}"/>
								</td>
								<td>
								
								</td>
								<td class="azioni">
									<ul>
										<li>
											<s:property value="%{#documenti.docUlterioriFileName.get(#status.index)}"/>
											<span>(<s:property value="%{#documenti.docUlterioriSize.get(#status.index)}" /> KB)</span>
											
											<form action="<s:property value='%{#urlDownloadDoc}'/>" method="post" >											 
												<s:hidden name="idCom" value="%{#documenti.idComunicazione}" />
												<s:hidden name="uuid" value="%{#documenti.docUlterioriUuid.get(#status.index)}" />
												
												<a href="javascript:;" onclick="parentNode.submit();" cssClass="bkg download">
													DOWNLOAD
												</a>
											</form>
										</li>
										<li>
											<form action="<s:property value='%{#urlDeleteDoc}'/>" method="post" >												
												<s:hidden name="idCom" value="%{#documenti.idComunicazione}" />
												<s:hidden name="uuids" value="%{#documenti.docUlterioriUuid.get(#status.index)}" />
												<s:hidden name="form" value="%{#formDebug}" />
												
												<a href="javascript:;" onclick="parentNode.submit();" cssClass="bkg delete">
													ELIMINA
												</a>
											</form>
										</li>
									</ul>
								</td>
							</tr>
						</s:if>
					</s:iterator>
				</tbody>
			</table>
		</div>
	

		<br/>
		<br/>
		<div>
			TEST GENERA UUID<br/>
			<s:url id="urlNewUuid" namespace="/do/FrontEnd/GareTel" action="newUuidQC" />
			<form action="<s:property value='%{#urlNewUuid}'/>" method="post" >
				<s:submit value="genera UUID" cssClass="button" />
			</form>
		</div>

	
		<br/>
		<br/>
		<div>
			TEST ALLEGA DOCUMENTO<br/>
			<s:url id="urlUpload" namespace="/do/FrontEnd/GareTel" action="addQCDocument" />
			<form action="<s:property value='%{#urlUpload}'/>" method="post" enctype="multipart/form-data" >
				Descrizione : 
				<input type="text" name="description" id="description" />
				<input type="file" name="attachmentData" id="attachmentData" />
				<!-- il framwork automaticamente predisporrebbe "attachmentDataFileName" invece di "attachmentFileName" !!! -->
				<s:hidden name="attachmentFileName" value="TestUpload.pdf" />		<!-- nome del file in upload -->
				<s:hidden name="idCom" value="%{#documenti.idComunicazione}" />
				<s:hidden name="uuid" value="990101235912345" />					<!-- UUID recuperato con newUuidQC -->
				<s:hidden name="form" value="%{#formDebug}" />

				<s:submit value="Aggiungi allegato" cssClass="button"  />
			</form>
		</div>
	
	
		<br/>
		<br/>
		<div>
			TEST PDF 
			<br/>
			<s:url id="urlPdfUpload" namespace="/do/FrontEnd/GareTel" action="addQCDocument" />
			<form action="${urlPdfUpload}" method="post" enctype="multipart/form-data" >
				Descrizione : 
				<input type="text" name="description" id="description" />
				<input type="file" name="attachmentData" id="attachmentData" />
				<!-- il framwork automaticamente predisporrebbe "attachmentDataFileName" invece di "attachmentFileName" !!! -->
				<s:hidden name="attachmentFileName" value="TestUpload.pdf" />		<!-- nome del file in upload -->
				<s:hidden name="idCom" value="%{#documenti.idComunicazione}" />
				<s:hidden name="uuid" value="990101235912345" />					<!-- UUID recuperato con newUuidQC -->
				<s:hidden name="signed" value="true" />
				<s:hidden name="summary" value="true" />
				<s:hidden name="form" value="%{#formDebug}" />

				<s:submit value="Upload Pdf" cssClass="button"  />
			</form>
				
			<br/>
			
		
			
			<form action="${urlGeneratePDF}"  method="post">
				<s:hidden name="idCom" value="%{#documenti.idComunicazione}" />
				
				<input type="submit" id="createPdf" value='<wp:i18n key="BUTTON_WIZARD_GENERA_PDF" />' 
					   title='<wp:i18n key="TITLE_GARETEL_GENERA_PDF" />' class="button" />
			 </form>
		</div>
	
	
		<br/>
		<br/>
		<div>
			TEST SALVA QUESTIONARIO <br/>
			<s:url id="urlSave" namespace="/do/FrontEnd/GareTel" action="saveQCForm" />
			<form action="<s:property value='%{#urlSave}'/>" method="post"  >
				<s:hidden name="idCom" value="%{#documenti.idComunicazione}" />
				<s:hidden name="form" value="%{#formDebug}" />
				
				<s:submit value="save questionario" cssClass="button"  />
			</form>
		</div>
		
		
		<br/>
		<br/>
		<div>
			TEST CARICA QUESTIONARIO <br/>
			<s:url id="urlLoad" namespace="/do/FrontEnd/GareTel" action="loadQCForm" />
			<form action="<s:property value='%{#urlLoad}'/>" method="post"  >
				<s:hidden name="idCom" value="%{#documenti.idComunicazione}" />
				
				<s:submit value="load questionario" cssClass="button"  />
			</form>
		</div>
		
	</div>	
</c:if>	
