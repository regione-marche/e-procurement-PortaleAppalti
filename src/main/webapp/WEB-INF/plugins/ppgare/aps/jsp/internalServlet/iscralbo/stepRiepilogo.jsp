<%@ taglib prefix="wp" uri="aps-core.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/send_blockUI.jsp" />


<s:url id="urlPdf" namespace="/do/FrontEnd/IscrAlbo" action="createRiepilogoPdf">
	<s:param name="urlPage">${currentPageUrl}</s:param>
	<s:param name="currentFrame">${param.currentFrame}</s:param>
</s:url>
<%-- <c:set var="urlPdf">${urlPdf}&amp;${tokenHrefParams}</c:set> --%>

<s:if test="%{#session.dettIscrAlbo != null}">
	<s:set name="sessionIdObj" value="'dettIscrAlbo'" />
	<c:set var="nextAction" value="/ExtStr2/do/FrontEnd/IscrAlbo/endWizard.action" />
</s:if>
<s:else>
	<s:set name="sessionIdObj" value="'dettRinnAlbo'" />
	<c:set var="nextAction" value="/ExtStr2/do/FrontEnd/IscrAlbo/rinnovo.action" />
</s:else>

<s:set name="helper" value="%{#session[#sessionIdObj]}" />


<s:if test="%{!#helper.rinnovoIscrizione}">
	<s:if test="%{!#helper.aggiornamentoIscrizione}">
		<c:set var="codiceBalloon" value="BALLOON_ISCR_ALBO_RIEPILOGO" />
	</s:if>
	<s:else>
		<c:set var="codiceBalloon" value="BALLOON_AGG_ISCR_ALBO_RIEPILOGO" />
	</s:else>
</s:if>
<s:else>
	<c:set var="codiceBalloon" value="BALLOON_RIN_ISCR_ALBO_RIEPILOGO" />
</s:else>

<s:if test="%{#helper.tipologia == 2}">
	<c:set var="titolo"><wp:i18n key='TITLE_PAGE_ISCRALBO_MERCATO_ELETTRONICO'/></c:set>
</s:if>
<s:else>
	<c:set var="titolo"><wp:i18n key='TITLE_PAGE_ISCRALBO_ELENCO_OE'/></c:set>
</s:else>

<c:set var="warning0Documenti" value="false" />
<s:if test="%{!#helper.rinnovoIscrizione || #helper.aggiornamentoIscrizione }">
	<s:if test="%{(#helper.documenti.docRichiestiId.size() + #helper.documenti.docUlterioriDesc.size()) == 0}">
		<c:set var="warning0Documenti" value="true" />
	</s:if>
</s:if>
<s:else>
	<s:if test="%{(#helper.documenti.docRichiestiId.size() + #helper.documenti.docUlterioriDesc.size()) == 0}">
		<c:set var="warning0Documenti" value="true" />
	</s:if>
</s:else>


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp">
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<s:if test="%{!#helper.aggiornamentoIscrizione && !#helper.rinnovoIscrizione}">
		<h2><wp:i18n key='TITLE_PAGE_ISCRALBO_RICHIESTA_ISCRIZIONE'/> <c:out value="${titolo}" /></h2>
	</s:if>
	<s:elseif test="%{#helper.rinnovoIscrizione}">
		<h2><wp:i18n key='TITLE_PAGE_ISCRALBO_RICHIESTA_RINNOVO_ISCRIZIONE'/> <c:out value="${titolo}" /></h2>
	</s:elseif>
	<s:else>
		<h2><wp:i18n key='TITLE_PAGE_ISCRALBO_AGGIORNAMENTO_ISCRIZIONE'/> <c:out value="${titolo}" /></h2>
	</s:else>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/field_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_messages.jsp" />

	<jsp:include page="stepsIscrizione.jsp">
		<jsp:param value="${sessionIdObj}" name="sessionIdObj" />
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="${codiceBalloon}" />
	</jsp:include>

	<form action='<wp:action path="${nextAction}"/>' method="post">
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
		
		<fieldset>
			<legend>
				<span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_ISCRALBO_RIEPILOGO" />
			</legend>

<%-- 
			<div class="fieldset-row first-row">
				<div class="label">
					<label>Stazioni appaltanti</label>
				</div>
				<div class="element">
					<s:if
						test="%{#helper.stazioniAppaltanti.size() > 0}">
						<s:iterator
							value="%{#helper.stazioniAppaltanti.iterator()}"
							var="stazioneAppaltante" status="stat">
							<s:iterator value="maps['stazioniAppaltanti']">
								<s:if test="%{key == #stazioneAppaltante}">
									<s:property value="%{value}" />
									<br />
								</s:if>
							</s:iterator>
						</s:iterator>
					</s:if>
					<s:else><wp:i18n key="LABEL_NOT_DEFINED" /></s:else>
				</div>
			</div>
 --%>
 
			<div class="fieldset-row first-row">
				<div class="label">
					<label><wp:i18n key="LABEL_ISCRALBO_TITOLO_BANDO_AVVISO"/></label>
				</div>
				<div class="element">
					<s:property value="%{#helper.descBando}" />
				</div>
			</div>

			<div class="fieldset-row">
				<div class="label">
					<label><wp:i18n key="LABEL_OPERATORE_ECONOMICO"/></label>
				</div>
				<div class="element">
					<s:property value="%{#helper.datiPrincipaliImpresa.ragioneSociale}" />
				</div>
			</div>

			<div class="fieldset-row">
				<div class="label">
					<label><wp:i18n key="LABEL_LEGALI_RAPPRESENTANTI"/></label>
				</div>
				<div class="element">
					<s:if test="%{#helper.impresa.legaliRappresentantiImpresa.size() > 0}">
						<ul class="list">
							<s:iterator
								value="%{#helper.impresa.legaliRappresentantiImpresa.iterator()}"
								var="legaleRappresentante" status="stat">
								<li
									class='<s:if test="%{#stat.first}">first</s:if> <s:if test="%{#stat.last}">last</s:if>'>
									<s:property value="%{#legaleRappresentante.cognome}" /> <s:property
										value="%{#legaleRappresentante.nome}" /> <c:if
										test="${! empty legaleRappresentante.dataInizioIncarico}">
									<wp:i18n key="LABEL_DA_DATA" /> <s:property
											value="%{#legaleRappresentante.dataInizioIncarico}" />
									</c:if> <c:if test="${! empty legaleRappresentante.dataFineIncarico}">
									<wp:i18n key="LABEL_A_DATA" /> <s:property
											value="%{#legaleRappresentante.dataFineIncarico}" />
									</c:if>
								</li>
							</s:iterator>
						</ul>
					</s:if>
					<s:else><wp:i18n key="LABEL_NOT_DEFINED"/></s:else>
				</div>
			</div>

			<div class="fieldset-row">
				<div class="label">
					<label><wp:i18n key="LABEL_DIRETTORI_TECNICI"/> </label>
				</div>
				<div class="element">
					<s:if test="%{#helper.impresa.direttoriTecniciImpresa.size() > 0}">
						<ul class="list">
							<s:iterator
								value="%{#helper.impresa.direttoriTecniciImpresa.iterator()}"
								var="direttoreTecnico" status="stat">
								<li
									class='<s:if test="%{#stat.first}">first</s:if> <s:if test="%{#stat.last}">last</s:if>'>
									<s:property value="%{#direttoreTecnico.cognome}" /> <s:property
										value="%{#direttoreTecnico.nome}" /> <c:if
										test="${! empty direttoreTecnico.dataInizioIncarico}">
									<wp:i18n key="LABEL_DA_DATA" /> <s:property value="%{#direttoreTecnico.dataInizioIncarico}" />
									</c:if> <c:if test="${! empty direttoreTecnico.dataFineIncarico}">
									<wp:i18n key="LABEL_A_DATA" /> <s:property value="%{#direttoreTecnico.dataFineIncarico}" />
									</c:if>
								</li>
							</s:iterator>
						</ul>
					</s:if>
					<s:else><wp:i18n key="LABEL_NOT_DEFINED"/></s:else>
				</div>
			</div>

			<s:if test="%{#helper.rti}">
				<div class="fieldset-row">
					<div class="label">
						<label><wp:i18n key="LABEL_FORMA_DI_PARTECIPAZIONE"/></label>
					</div>
					<div class="element">
						<wp:i18n key="LABEL_PARTECIPA_COME_MANDATARIA_RAGGRUPPAMENTO"/>
						<s:property value="%{#helper.denominazioneRTI}" />
					</div>
				</div>
			</s:if>
			
			<s:if test="%{#helper.rti}">
				<div class="fieldset-row">
					<div class="label">
						<label><wp:i18n key="LABEL_MANDANTI"/></label>
					</div>
					<div class="element">
						<s:if test="%{#helper.componentiRTI.size() > 0}">
							<ul class="list">
								<%-- 	<s:iterator value="%{#session.dettIscrAlbo.componenti.iterator()}" var="partecipante" status="stat"> --%>
								<s:iterator
									value="%{#helper.componentiRTI.iterator()}"
									var="partecipante" status="status">
									<c:if test="${status.index > 0}">
										<li
											class='<s:if test="%{#status.first}">first</s:if> <s:if test="%{#status.last}">last</s:if>'>
											<s:property value="%{#partecipante.ragioneSociale}" />
										</li>
									</c:if>
								</s:iterator>
							</ul>
						</s:if>
					</div>
				</div>
			</s:if>
			<s:elseif test="%{#helper.impresa.consorzio}">
				<div class="fieldset-row">
					<div class="label">
						<label><wp:i18n key="LABEL_CONSORZIATE_ESECUTRICI"/></label>
					</div>
					<div class="element">
						<s:if test="%{#helper.componenti.size() > 0}">
							<ul class="list">
								<s:iterator
									value="%{#helper.componenti.iterator()}"
									var="partecipante" status="stat">
									<li
										class='<s:if test="%{#stat.first}">first</s:if> <s:if test="%{#stat.last}">last</s:if>'>
										<s:property value="%{#partecipante.ragioneSociale}" />
									</li>
								</s:iterator>
							</ul>
						</s:if>
					</div>
				</div>
			</s:elseif>

			<s:if test="%{!#helper.rinnovoIscrizione}">
				<div class="fieldset-row">
					<div class="label">
						<label><wp:i18n key="LABEL_CATEGORIE_SELEZIONATE"/></label>
					</div>
					<div class="element">
						<s:property value="%{categorieSelezionate.size()}" />
					</div>
				</div>
			</s:if>
			<s:if test="%{!#helper.rinnovoIscrizione || #helper.aggiornamentoIscrizione }">
				<div class="fieldset-row last-row">
					<div class="label">
						<%-- devo contare solo gli allegati effettivamente visibili, quindi siamo costretti a fare doppio doppio ciclo for --%>
						<s:set name="totAllegati" value="0" />
						<s:iterator value="documentiRichiesti" var="doc">
							<s:iterator	value="#helper.documenti.docRichiestiId" var="idDoc" status="stat">
								<s:if test="%{#idDoc == #doc.id && #helper.documenti.docRichiestiVisibile.get(#stat.index)}">
									<s:set name="totAllegati" value="%{#totAllegati +1}" />
								</s:if>
							</s:iterator>
						</s:iterator>
						<s:iterator	value="#helper.documenti.docUlterioriDesc" var="doc" status="stat">
							<s:if test="%{#helper.documenti.docUlterioriVisibile.get(#stat.index)}">
									<s:set name="totAllegati" value="%{#totAllegati +1}" />
							</s:if>
						</s:iterator>
						<label><wp:i18n key="LABEL_DOCUMENTI_ALLEGATI"/> (<s:property
								value="%{#totAllegati}" />)
						</label>
					</div>
					<div class="element">
						<s:iterator value="documentiRichiesti" var="doc">
							<s:iterator	value="#helper.documenti.docRichiestiId" var="idDoc" status="stat">
								<s:if test="%{#idDoc == #doc.id && #helper.documenti.docRichiestiVisibile.get(#stat.index)}">
									<s:property value="%{#doc.nome}" /> (<s:property
										value="%{#helper.documenti.docRichiestiFileName.get(#stat.index)}" />)<br />
								</s:if>
							</s:iterator>
						</s:iterator>
						<s:iterator	value="#helper.documenti.docUlterioriDesc" var="doc" status="stat">
							<s:if test="%{#helper.documenti.docUlterioriVisibile.get(#stat.index)}">
								<s:property value="%{#doc}" /> (<s:property
									value="%{#helper.documenti.docUlterioriFileName.get(#stat.index)}" />)<br />
							</s:if>
						</s:iterator>
					</div>
				</div>
			</s:if>
			<s:else>
				<div class="fieldset-row last-row">
					<div class="label">
						<%-- devo contare solo gli allegati effettivamente visibili, quindi siamo costretti a fare doppio doppio ciclo for --%>
						<s:set name="totAllegati" value="0" />
						<s:iterator value="documentiRichiesti" var="doc">
							<s:iterator	value="#helper.documenti.docRichiestiId" var="idDoc" status="stat">
								<s:if test="%{#idDoc == #doc.id && #helper.documenti.docRichiestiVisibile.get(#stat.index)}">
									<s:set name="totAllegati" value="%{#totAllegati +1}" />
								</s:if>
							</s:iterator>
						</s:iterator>
						<s:iterator	value="#helper.documenti.docUlterioriDesc" var="doc" status="stat">
							<s:if test="%{#helper.documenti.docUlterioriVisibile.get(#stat.index)}">
									<s:set name="totAllegati" value="%{#totAllegati +1}" />
							</s:if>
						</s:iterator>
						<label><wp:i18n key="LABEL_DOCUMENTI_ALLEGATI"/> (<s:property
								value="%{#totAllegati}" />)
						</label>
					</div>
					<div class="element">
						<s:iterator value="documentiRichiesti" var="doc">
							<s:iterator value="#helper.documenti.docRichiestiId" var="idDoc" status="stat">
								<s:if test="%{#idDoc == #doc.id && #helper.documenti.docRichiestiVisibile.get(#stat.index)}">
									<s:property value="%{#doc.nome}" /> (<s:property
										value="%{#helper.documenti.docRichiestiFileName.get(#stat.index)}" />)<br />
								</s:if>
							</s:iterator>
						</s:iterator>
						<s:iterator	value="%{#helper.documenti.docUlterioriDesc}" var="doc" status="stat">
							<s:if test="%{#helper.documenti.docUlterioriVisibile.get(#stat.index)}">
								<s:property value="%{#doc}" /> (<s:property
									value="%{#helper.documenti.docUlterioriFileName.get(#stat.index)}" />)<br />
							</s:if>
						</s:iterator>
					</div>
				</div>
			</s:else>
		</fieldset>

		<s:if test="%{!#helper.aggiornamentoIscrizione && !#helper.rinnovoIscrizione }">
			<p id="linkCreatePdf">
				<wp:i18n key="BUTTON_WIZARD_PRINT_DATA" var="valueScaricaPdf" />
				<wp:i18n key="LABEL_ISCRALBO_SCARICA_PDF_RIEPILOGO" var="titleScaricaPdf" />
				<a href="${urlPdf}" title="${attr.titleScaricaPdf}" class="bkg download">
					<c:choose>
						<c:when test="${skin == 'highcontrast' || skin == 'text'}">
							${attr.titleScaricaPdf}
						</c:when>
						<c:otherwise>
						
						</c:otherwise>
					</c:choose>
				</a>
				<wp:i18n key="LABEL_ISCRALBO_SCARICA_PDF_RIEPILOGO"/>.
			</p>
		</s:if>
		
		<c:if test="${warning0Documenti}">
			<p>
				<span class="warnings"><strong><wp:i18n key="LABEL_MESSAGE_WARNING"/>: <wp:i18n key="LABEL_MESSAGE_NO_DOCUMENTS"/>.</span>
			</p>
			<div class="azioni">
				<input type="button" id="attivaSend" name="procedi" 
					class="button noscreen" style="margin-bottom: 1em;"
					value='<wp:i18n key="BUTTON_WIZARD_PROCEED_NO_ATTACH"/>' />
			</div>
		</c:if>
		
		<div class="azioni">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_previous.jsp" />
			<s:if test="%{!#helper.rinnovoIscrizione}">
				<wp:i18n key="BUTTON_WIZARD_PRINT_DATA" var="valuePrintDataButton" />
				<wp:i18n key="TITLE_WIZARD_PRINT_DATA" var="titlePrintDataButton" />
				<input type="button" id="createPdf" 
					value="${valuePrintDataButton}" title="${titlePrintDataButton}"
					onclick="document.location.href='${urlPdf}'" 
					class="button" style="display: none;" />
			</s:if>
			<s:if test="%{!#helper.aggiornamentoIscrizione && !#helper.rinnovoIscrizione}">
				<wp:i18n key="BUTTON_WIZARD_SALVA_BOZZA" var="valueSalvaBozzaButton" />
				<wp:i18n key="TITLE_WIZARD_SALVA_BOZZA" var="titleSalvaBozzaButton" />
				<s:submit 
					value="%{#attr.valueSalvaBozzaButton}" title="%{#attr.titleSalvaBozzaButton}"
					cssClass="button block-ui" method="save">
				</s:submit>
				<s:if test="!sendBlocked">
					<wp:i18n key="BUTTON_WIZARD_SEND_SUBSCRIPTION" var="valueSendSubscriptionButton" />
					<wp:i18n key="TITLE_WIZARD_SEND_SUBSCRIPTION" var="titleSendSubscriptionButton" />
					<s:submit 
						value="%{#attr.valueSendSubscriptionButton}" title="%{#attr.titleSendSubscriptionButton}"
						cssClass="button block-ui sendDomanda" method="send">
					</s:submit>
				</s:if>
			</s:if>
			<s:elseif test="%{#helper.rinnovoIscrizione}">
				<s:if test="!sendBlocked">
					<wp:i18n key="BUTTON_WIZARD_SEND_RENEWAL" var="valueSendRenewalButton" />
					<wp:i18n key="TITLE_WIZARD_SEND_RENEWAL" var="titleSendRenewalButton" />
					<s:submit
						value="%{#attr.valueSendRenewalButton}" title="%{#attr.titleSendRenewalButton}" 
						cssClass="button block-ui sendDomanda" method="rinnovo">
					</s:submit>
				</s:if>
			</s:elseif>
			<s:else>
				<s:if test="!sendBlocked">
					<wp:i18n key="BUTTON_WIZARD_SEND_UPDATE" var="valueSendUpdateButton" />
					<wp:i18n key="TITLE_WIZARD_SEND_UPDATE" var="titleSendUpdateButton" />
					<s:submit
						value="%{#attr.valueSendUpdateButton}" title="%{#attr.titleSendUpdateButton}" 
						cssClass="button block-ui sendDomanda" method="sendUpdate">
					</s:submit>
				</s:if>
			</s:else>
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_cancel.jsp" />
			<input type="hidden" name="ext" value="${param.ext}" />
		</div>
	</form>

	<script type="text/javascript">
	<!--//--><![CDATA[//><!--
	<s:if test="%{!#helper.aggiornamentoIscrizione && !#helper.rinnovoIscrizione}">
		document.getElementById("createPdf").style.display = "inline";
		document.getElementById("linkCreatePdf").style.display = "none";
	</s:if>
	
	<c:if test="${warning0Documenti}">
	<%-- nel caso di 0 documenti va spento il pulsante per l'invio, e quando cliccato il pulsante per proseguire ugualmente va ripresentato --%>	
		$("input.sendDomanda").addClass("noscreen");
		$("#attivaSend").removeClass("noscreen")
		$("#attivaSend").on("click", function() {$("input.sendDomanda").removeClass("noscreen"); $("#attivaSend").addClass("noscreen");});
	</c:if>
	//--><!]]></script>
</div>