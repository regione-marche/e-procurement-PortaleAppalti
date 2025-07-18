<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<c:set var="titolo"><wp:i18n key="TITLE_PAGE_GARETEL_RIEPILOGO_OFFERTE_DISTINTE"/></c:set>
<c:set var="codiceBalloon" value="BALLOON_INVIO_BUSTE_TELEMATICHE_RIEPILOGO"/>
<s:set var="imgCheck"><wp:resourceURL />static/img/check.svg</s:set>

<s:set var="buste" value="%{#session.dettaglioOffertaGara}"/>
<%--<s:set var="bustaPrequalifica" value="%{#buste.bustaPrequalifica.helper}"/>--%>
<s:set var="bustaAmministrativa" value="%{#buste.bustaAmministrativa.helper}"/>
<s:set var="bustaTecnica" value="%{#buste.bustaTecnica.helper}"/>
<s:set var="bustaEconomica" value="%{#buste.bustaEconomica.helper}"/>
<s:set var="riepilogoBuste" value="%{#buste.bustaRiepilogo.helper}" />
<s:set var="partecipazione" value="%{#buste.bustaPartecipazione.helper}" />

<s:set var="dettaglioGara" value="%{#session.dettaglioGara}"/>
<!-- OBSOLETO <s:set var="riepilogoBuste" value="%{#session.riepilogoBuste}" /> -->
<s:set var="wizardOfferta" value="%{#session.wizardOfferta}"/>


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2>${titolo} [${codiceGara}]</h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/field_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
    <jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_messages.jsp" />

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="${codiceBalloon}"/>
	</jsp:include>

	<fieldset>
		<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_DATI_OPERATORE_ECONOMICO" /></legend>
		
		<div class="fieldset-row first-row">
			<div class="label">
				<label><wp:i18n key="LABEL_GARETEL_OGGETTO" /> : </label>
			</div>
			<div class="element">
				<s:property value="%{#dettaglioGara.datiGeneraliGara.oggetto}" />
			</div>
		</div>
	
		<div class="fieldset-row">
			<div class="label">
				<label><wp:i18n key="LABEL_DATI_OPERATORE_ECONOMICO" /> : </label>
			</div>
			<div class="element">
				<s:property value="%{datiImpresa.datiPrincipaliImpresa.ragioneSociale}" />
			</div>
		</div>
			
		<div class="fieldset-row">
			<div class="label">
				<label><wp:i18n key="LABEL_LEGALI_RAPPRESENTANTI" /> : </label>
			</div>
			<div class="element">
				<s:if test="%{datiImpresa.legaliRappresentantiImpresa.size() > 0}">
					<ul class="list">
						<s:iterator value="%{datiImpresa.legaliRappresentantiImpresa.iterator()}" var="legaleRappresentante" status="stat">
							<li class='<s:if test="%{#stat.first}">first</s:if> <s:if test="%{#stat.last}">last</s:if>'>
								<s:property value="%{#legaleRappresentante.cognome}"/> <s:property value="%{#legaleRappresentante.nome}"/> 
								<c:if test="${! empty legaleRappresentante.dataInizioIncarico}">
									<wp:i18n key="LABEL_DA_DATA" /> <s:property value="%{#legaleRappresentante.dataInizioIncarico}"/>
								</c:if> 
								<c:if test="${! empty legaleRappresentante.dataFineIncarico}">
									<wp:i18n key="LABEL_A_DATA" /> <s:property value="%{#legaleRappresentante.dataFineIncarico}"/>
								</c:if>
							</li>
						</s:iterator>
					</ul>
				</s:if>
				<s:else><p><wp:i18n key="LABEL_NOT_DEFINED" /></p></s:else>
			</div>
		</div>

		<div class="fieldset-row">
			<div class="label">
			<label><wp:i18n key="LABEL_DIRETTORI_TECNICI" /> : </label>
			</div>
			<div class="element">
				<s:if test="%{datiImpresa.direttoriTecniciImpresa.size() > 0}">
					<ul class="list">
						<s:iterator value="%{datiImpresa.direttoriTecniciImpresa.iterator()}" var="direttoreTecnico" status="stat">
							<li class='<s:if test="%{#stat.first}">first</s:if> <s:if test="%{#stat.last}">last</s:if>'>
								<s:property value="%{#direttoreTecnico.cognome}"/> <s:property value="%{#direttoreTecnico.nome}"/> 
								<c:if test="${! empty direttoreTecnico.dataInizioIncarico}">
									<wp:i18n key="LABEL_DA_DATA" /> <s:property value="%{#direttoreTecnico.dataInizioIncarico}"/>
								</c:if> 
								<c:if test="${! empty direttoreTecnico.dataFineIncarico}">
									<wp:i18n key="LABEL_A_DATA" /> <s:property value="%{#direttoreTecnico.dataFineIncarico}"/>
								</c:if>
							</li>
						</s:iterator>
					</ul>
				</s:if>
				<s:else><wp:i18n key="LABEL_NOT_DEFINED" /></s:else>
			</div>
		</div>

		<div class="fieldset-row">
			<div class="label">
				<label><wp:i18n key="LABEL_PARTECIPA_COME_MANDATARIA_RTI" /> : </label>
			</div>
			<div class="element">
				<s:if test="%{rti}"><wp:i18n key="LABEL_YES" />, <wp:i18n key="LABEL_PER_CONTO_DI" /> <s:property value="%{denominazioneRti}"/></s:if>
				<s:else><wp:i18n key="LABEL_NO" /></s:else>
			</div>
		</div>
		
		<!-- liste partecipanti RTI (mandanti/consorziate esecutrici)-->
		<s:if test="%{#partecipazione.rti}">
			<s:if test="%{#partecipazione.componenti != null && #partecipazione.componenti.size() > 0}">
				<div class="fieldset-row">
					<div class="label">
						<label>
							<s:if test="%{#partecipazione.impresa.consorzio}">
								<wp:i18n key="LABEL_CONSORZIATE_ESECUTRICI"/>
							</s:if>
							<s:else>
								<wp:i18n key="LABEL_MANDANTI"/>
							</s:else>
						</label>
					</div>
					<div class="element">
						<ul class="list">
							<s:iterator value="%{#partecipazione.componenti}" var="partecipante" status="status">
								<li class='<s:if test="%{#status.first}">first</s:if> <s:if test="%{#status.last}">last</s:if>'>
									<s:property value="%{#partecipante.ragioneSociale}" />
								</li>
							</s:iterator>
						</ul>
					</div>
				</div>
			</s:if>
		</s:if>
		
		<s:if test="%{#buste.invioOfferta}">
			<div class="fieldset-row last-row">
				<div class="label">
					<label><wp:i18n key='LABEL_CODICE_CNEL'/> : </label>
				</div>
				<div class="element">
					<s:property value="%{#partecipazione.codiceCNEL}" />
				</div>
			</div>
		</s:if>
	</fieldset>

	<!--  BUSTA AMMINISTRASTIVA  -->
	
	<s:if test="%{ !dettGara.datiGeneraliGara.noBustaAmministrativa }" >
		<fieldset>
			<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_BUSTA_AMMINISTRATIVA" /></legend>
			
			<s:if test="%{#bustaAmministrativa.datiModificati}">
				<div class="balloon">
					<div class="balloon-content balloon-alert">
						<wp:i18n key="LABEL_NO_MODIFICHE_AI_DOCUMENTI" />.
					</div>
				</div>
			</s:if>

			<s:set var="riepilogoAmm" value="%{bustaRiepilogativa.bustaAmministrativa}" />
			
			<s:set var="documentiInseritiSize" value="%{#riepilogoAmm.documentiInseriti.size()}" />
		 	<s:if test="%{#riepilogoAmm.questionarioPresente}">
		 		<s:set var="documentiInseritiSize" value="#documentiInseritiSize - 1" />
		 		
				<div class="fieldset-row first-row">
					<div class="label">
						<label><wp:i18n key="LABEL_QUESTIONARIO_COMPLETATO" /> : </label>
					</div>
					<div class="element">
						<s:if test="%{#riepilogoAmm.questionarioCompletato}">
							<wp:i18n key="LABEL_YES" />
						</s:if>
						<s:else>
							<wp:i18n key="LABEL_NO" />
						</s:else>
					</div>
				</div>
			</s:if>

			<div class="fieldset-row <s:if test='%{#riepilogoAmm.questionarioPresente}'>first-row</s:if>" >
				<div class="label">
					<label><wp:i18n key="LABEL_DOCUMENTI_INSERITI" /> <s:if test="%{#documentiInseritiSize > 0}">(<s:property value="%{#documentiInseritiSize}"/>)</s:if> : </label>
				</div>
				<div class="element">
					<s:if test="%{#riepilogoAmm.documentiInseriti.size() > 0}">
						<ul class="list">
							<s:iterator value="#riepilogoAmm.documentiInseriti" var="documento" status="stat">
								<%-- in caso di gestione dei questionari l'allegato "questionario.json" non va visualizzato --%>
								<s:if test="%{ !'questionario.json'.equals(#documento.nomeFile) }" >
								 
									<li class='<s:if test="%{#stat.first}">first</s:if> <s:if test="%{#stat.last}">last</s:if>'>
										<c:choose>
											<c:when test="${skin == 'highcontrast' || skin == 'text'}">
												<s:if test="%{#documento.required}">
													 &#8226;&nbsp;<span title="<s:property value="%{#documento.nomeFile}"/> (<s:property value="%{#documento.dimensione}" /> KB)"><s:property value="%{#documento.descrizione}"/></span>
												</s:if>
												<s:else>
													 &#8226;&nbsp;<span title="<s:property value="%{#documento.nomeFile}"/> (<s:property value="%{#documento.dimensione}" /> KB)"><s:property value="%{#documento.descrizione}"/></span>
												</s:else>
											</c:when>
											<c:otherwise>
												<s:if test="%{#documento.required}">
													 &#8226;&nbsp;<span title="<s:property value="%{#documento.nomeFile}"/> (<s:property value="%{#documento.dimensione}" /> KB)"><s:property value="%{#documento.descrizione}"/></span>
												</s:if>
												<s:else>
													 &#8226;&nbsp;<span title="<s:property value="%{#documento.nomeFile}"/> (<s:property value="%{#documento.dimensione}" /> KB)"><s:property value="%{#documento.descrizione}"/></span>
												</s:else>
											</c:otherwise>
										</c:choose>
										&nbsp;(<s:property value="%{#documento.nomeFile}"/>)
									</li>
								
								</s:if>
							</s:iterator>
						</ul>
					</s:if>
					<s:else>
						<%-- Nessun documento ancora inserito. --%>
						<wp:i18n key="LABEL_NO_DOCUMENT" />. 
					</s:else>
				</div>
			</div>
	
			<%-- in caso di QUESTIONARI non vanno visualizzati i documenti obbligatori --%>
			<s:if test="%{ !#riepilogoAmm.questionarioPresente }">
				<div class="fieldset-row last-row">
					<div class="label">
						<label>
							<wp:i18n key="LABEL_DOCUMENTI_OBBLIGATORI_MANCANTI" /> <s:if test="%{docObbligatoriMancantiAmministrativa.size() > 0}">(<s:property value="%{docObbligatoriMancantiAmministrativa.size()}"/>) : </s:if> 
						</label>
					</div>
					<div class="element">
						<s:if test="%{docObbligatoriMancantiAmministrativa.size() > 0}">
							<ul>
								<s:iterator value="docObbligatoriMancantiAmministrativa" var="documento" status="stat">
									<li><s:property value="%{#documento}"/></li>
								</s:iterator>
							</ul>
						</s:if>
						<s:else>
							<img class="resize-svg-16" src="${imgCheck}" title="<wp:i18n key='TITLE_BUSTA_PRONTA_PER_INVIO' />" alt="<wp:i18n key='TITLE_BUSTA_PRONTA_PER_INVIO' />" />
						</s:else>
					</div>
				</div>
			</s:if>
			<c:if test='${"1".equals(statoAmministrativa) || "".equals(statoAmministrativa)}'>
				<form action="<wp:action path='/ExtStr2/do/FrontEnd/GareTel/redirectSummaryEnv.action' />" method="post">
					<div class="azioni">
						<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
						<input type="submit" value="Compila" title='<wp:i18n key="TITLE_MODIFICA_BUSTA" />' class="button" name="method:doModify" />
						<c:if test='${"1".equals(statoAmministrativa)}'>
							<input type="submit" value="<wp:i18n key='LABEL_ELIMINA' />" title="<wp:i18n key='LABEL_ELIMINA' />" class="button" name="method:doReset" />
						</c:if>
						<input type="hidden" name="tipoBusta" value="${BUSTA_AMMINISTRATIVA}" />
						<input type="hidden" name="codiceGara" value="${codiceGara}" />
						<input type="hidden" name="codice" value="${lotto}" />
						<input type="hidden" name="operazione" value="${operazione}" />
						<input type="hidden" name="offertaTelematica" value="${false}" />
						<input type="hidden" name="backActionPath" value="/ExtStr2/do/FrontEnd/GareTel/openRiepilogoOfferteDistinte.action" />
					</div>
				</form>
			</c:if>
		</fieldset>
	</s:if>
	
	<!-- BUSTE TECNICHE ED ECONOMICHE LOTTO -->
		
	<s:if test="%{#bustaTecnica.datiModificati}">
		<div class="balloon">
			<div class="balloon-content balloon-alert">
				<wp:i18n key="LABEL_NO_MODIFICHE_AI_DOCUMENTI" />
			</div>
		</div>
	</s:if>
	<s:iterator value="%{bustaRiepilogativa.listaCompletaLotti}" var="lotto" status="stat">	

		<!-- OEPV costoFisso=1 ==> non c'e' offerta economica -->
		<s:set var="costoFisso" value="0"/>
		<s:iterator var="item" value="%{dettGara.lotto}"  >
			<s:if test="%{#item.codiceLotto eq #lotto}" >
				<s:set var="costoFisso" value="%{#item.costoFisso}"/>
			</s:if>
		</s:iterator>

		<s:set var="riepilogoLottoTec" value="%{bustaRiepilogativa.busteTecnicheLotti.get(#lotto)}" />
		<s:set var="riepilogoLottoEco" value="%{bustaRiepilogativa.busteEconomicheLotti.get(#lotto)}" />

		<s:if test="%{bustaRiepilogativa.busteTecnicheLotti.get(#lotto) != null ||
		              bustaRiepilogativa.busteEconomicheLotti.get(#lotto) != null}">
			<fieldset>
				<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_LOTTO" /> <s:property value="%{#riepilogoBuste.listaCodiciInterniLotti.get(#lotto)}" /></legend>

				<div class="fieldset-row first-row">
					<div class="label">
						<label><wp:i18n key="LABEL_GARETEL_OGGETTO" /> : </label>
					</div>
					<div class="element">
						<label>
							<s:if test="%{#costoFisso != 1}" >
								<s:property value="%{#riepilogoLottoEco.oggetto}"/>
							</s:if>
							<s:else>
								<s:property value="%{#riepilogoLottoTec.oggetto}"/>
							</s:else> 
						</label>
					</div>
				</div>
				
				<s:if test="%{#riepilogoLottoTec != null}">
					<fieldset>
						<legend><wp:i18n key="LABEL_BUSTA_TECNICA" /></legend>
						
						<s:set var="documentiInseritiSize" value="%{#riepilogoLottoTec.documentiInseriti.size()}" />		
						<s:if test="%{#riepilogoLottoTec.questionarioPresente}">
							<s:set var="documentiInseritiSize" value="#documentiInseritiSize - 1" />
							
							<div class="fieldset-row">
								<div class="label">
									<label><wp:i18n key="LABEL_QUESTIONARIO_COMPLETATO" /> : </label>
								</div>
								<div class="element">
									<s:if test="%{#riepilogoLottoTec.questionarioCompletato}">
										<wp:i18n key="LABEL_YES" />
									</s:if>
									<s:else>
										<wp:i18n key="LABEL_NO" />
									</s:else>
								</div>
							</div>
						</s:if>
						
						<div class="fieldset-row">
							<div class="label">
								<label><wp:i18n key="LABEL_DOCUMENTI_INSERITI" /> <s:if test="%{#documentiInseritiSize > 0}">(<s:property value="%{#documentiInseritiSize}"/>)</s:if> : </label>
							</div>
							<div class="element">
								<s:if test="%{#riepilogoLottoTec.documentiInseriti.size() > 0}">
									<ul class="list">
										<s:iterator value="#riepilogoLottoTec.documentiInseriti" var="documento" status="stat">
											<%-- in caso di gestione dei questionari l'allegato "questionario.json" non va visualizzato --%>
											<s:if test="%{ !'questionario.json'.equals(#documento.nomeFile) }" >
											
												<li class='<s:if test="%{#stat.first}">first</s:if> <s:if test="%{#stat.last}">last</s:if>'>
													<c:choose>
														<c:when test="${skin == 'highcontrast' || skin == 'text'}">
															<s:if test="%{#documento.required}">
																 &#8226;&nbsp;<span title="<s:property value="%{#documento.nomeFile}"/> (<s:property value="%{#documento.dimensione}" /> KB)"><s:property value="%{#documento.descrizione}"/></span>
															</s:if>
															<s:else>
																 &#8226;&nbsp;<span title="<s:property value="%{#documento.nomeFile}"/> (<s:property value="%{#documento.dimensione}" /> KB)"><s:property value="%{#documento.descrizione}"/></span>
															</s:else>
														</c:when>
														<c:otherwise>
															<s:if test="%{#documento.required}">
																 &#8226;&nbsp;<span title="<s:property value="%{#documento.nomeFile}"/> (<s:property value="%{#documento.dimensione}" /> KB)"><s:property value="%{#documento.descrizione}"/></span>
															</s:if>
															<s:else>
																 &#8226;&nbsp;<span title="<s:property value="%{#documento.nomeFile}"/> (<s:property value="%{#documento.dimensione}" /> KB)"><s:property value="%{#documento.descrizione}"/></span>
															</s:else>
														</c:otherwise>
													</c:choose>
													&nbsp;(<s:property value="%{#documento.nomeFile}"/>)
												</li>
												
											</s:if>
										</s:iterator>
									</ul>
								</s:if>
								<s:else>
									<wp:i18n key="LABEL_NO_DOCUMENT" />.
								</s:else>
							</div>
						</div>
						
						<%-- in caso di QUESTIONARI non vanno visualizzati i documenti obbligatori --%>
						<s:if test="%{ !#riepilogoLottoTec.questionarioPresente }">
							<div class="fieldset-row">
								<div class="label">
									<label>
										<wp:i18n key="LABEL_DOCUMENTI_OBBLIGATORI_MANCANTI" /> <s:if test="%{docObbligatoriMancantiTecnica.get(#lotto).size > 0}">(<s:property value="%{docObbligatoriMancantiTecnica.get(#lotto).size() - }"/>)</s:if> : 
									</label>
								</div>
								<div class="element">
									<s:if test="%{docObbligatoriMancantiTecnica.get(#lotto).size() > 0}">
										<ul class="list">
											<s:iterator value="docObbligatoriMancantiTecnica.get(#lotto)" var="documento" status="stat">
											
												<li><s:property value="%{#documento.descrizione}"/></li>
											
											</s:iterator>
										</ul>
									</s:if>
									<s:elseif test="%{!bustaRiepilogativa.primoAccessoTecnicheEffettuato.get(#lotto)}">
										<wp:i18n key="LABEL_NO_VISIONE_DOCUMENTAZIONE_NECESSARIA" />
									</s:elseif>
									<s:else>
										<img class="resize-svg-16" src="${imgCheck}" title="<wp:i18n key='TITLE_BUSTA_PRONTA_PER_INVIO' />" alt="<wp:i18n key='TITLE_BUSTA_PRONTA_PER_INVIO' />" />
									</s:else>
								</div>
							</div>
						</s:if>
						<c:if test='${"1".equals(statoTecnica[lotto]) || "".equals(statoTecnica[lotto])}'>
							<form action="<wp:action path='/ExtStr2/do/FrontEnd/GareTel/redirectSummaryEnv.action' />" method="post">
								<div class="azioni">
									<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
									<input type="submit" value="Compila" title='<wp:i18n key="TITLE_MODIFICA_BUSTA" />' class="button" name="method:doModify" />
									<c:if test='${"1".equals(statoTecnica[lotto])}'>
										<input type="submit" value="<wp:i18n key='LABEL_ELIMINA' />" title="<wp:i18n key='LABEL_ELIMINA' />" class="button" name="method:doReset" />
									</c:if>
									<input type="hidden" name="tipoBusta" value="${BUSTA_TECNICA}" />
									<input type="hidden" name="codiceGara" value="${codiceGara}" />
									<input type="hidden" name="codice" value="${lotto}" />
									<input type="hidden" name="operazione" value="${operazione}" />
									<input type="hidden" name="offertaTelematica" value="${false}" />
									<input type="hidden" name="backActionPath" value="/ExtStr2/do/FrontEnd/GareTel/openRiepilogoOfferteDistinte.action" />
								</div>
							</form>
						</c:if>
					</fieldset>
				</s:if>
				
				
				<s:if test="%{#riepilogoLottoEco != null && #costoFisso != 1}">
					<fieldset>
						<legend><wp:i18n key="LABEL_BUSTA_ECONOMICA" /></legend>
						
						<s:set var="documentiInseritiSize" value="%{#riepilogoLottoEco.documentiInseriti.size()}" />
						<s:if test="%{#riepilogoLottoEco.questionarioPresente}">
							<s:set var="documentiInseritiSize" value="#documentiInseritiSize - 1" />
							
							<div class="fieldset-row">
								<div class="label">
									<label><wp:i18n key="LABEL_QUESTIONARIO_COMPLETATO" /> : </label>
								</div>
								<div class="element">
									<s:if test="%{#riepilogoLottoEco.questionarioCompletato}">
										<wp:i18n key="LABEL_YES" />
									</s:if>
									<s:else>
										<wp:i18n key="LABEL_NO" />
									</s:else>
								</div>
							</div>
						</s:if>
						
						<div class="fieldset-row">
							<div class="label">
								<label><wp:i18n key="LABEL_DOCUMENTI_INSERITI" /> <s:if test="%{#documentiInseritiSize > 0}">(<s:property value="%{#documentiInseritiSize}"/>)</s:if> : </label>
							</div>
							<div class="element">
								<s:if test="%{#riepilogoLottoEco.documentiInseriti.size() > 0}">
									<ul class="list">
										<s:iterator value="#riepilogoLottoEco.documentiInseriti" var="documento" status="stat">
											<%-- in caso di gestione dei questionari l'allegato "questionario.json" non va visualizzato --%>
											<s:if test="%{ !'questionario.json'.equals(#documento.nomeFile) }" >
												
												<li class='<s:if test="%{#stat.first}">first</s:if> <s:if test="%{#stat.last}">last</s:if>'>
													<c:choose>
														<c:when test="${skin == 'highcontrast' || skin == 'text'}">
															<s:if test="%{#documento.required}">
																 &#8226;&nbsp;<span title="<s:property value="%{#documento.nomeFile}"/> (<s:property value="%{#documento.dimensione}" /> KB)"><s:property value="%{#documento.descrizione}"/></span>
															</s:if>
															<s:else>
																 &#8226;&nbsp;<span title="<s:property value="%{#documento.nomeFile}"/> (<s:property value="%{#documento.dimensione}" /> KB)"><s:property value="%{#documento.descrizione}"/></span>
															</s:else>
														</c:when>
														<c:otherwise>
															<s:if test="%{#documento.required}">
																 &#8226;&nbsp;<span title="<s:property value="%{#documento.nomeFile}"/> (<s:property value="%{#documento.dimensione}" /> KB)"><s:property value="%{#documento.descrizione}"/></span>
															</s:if>
															<s:else>
																 &#8226;&nbsp;<span title="<s:property value="%{#documento.nomeFile}"/> (<s:property value="%{#documento.dimensione}" /> KB)"><s:property value="%{#documento.descrizione}"/></span>
															</s:else>
														</c:otherwise>
													</c:choose>
													&nbsp;(<s:property value="%{#documento.nomeFile}"/>)
												</li>
												
											</s:if>
										</s:iterator>
									</ul>
								</s:if>
								<s:else>
									<wp:i18n key="LABEL_NO_DOCUMENT" />.
								</s:else>
							</div>
						</div>
						
						<%-- in caso di QUESTIONARI non vanno visualizzati i documenti obbligatori --%>
						<s:if test="%{ !#riepilogoLottoEco.questionarioPresente }">
							<div class="fieldset-row last-row">
								<div class="label">
									<label>
										<wp:i18n key="LABEL_DOCUMENTI_OBBLIGATORI_MANCANTI" /> <s:if test="%{docObbligatoriMancantiEconomica.get(#lotto).size() > 0}">(<s:property value="%{docObbligatoriMancantiEconomica.get(#lotto).size()}"/>)</s:if> : 
									</label>
								</div>
								<div class="element">
									<s:if test="%{docObbligatoriMancantiEconomica.get(#lotto).size() > 0}">
										<ul class="list">
											<s:iterator value="docObbligatoriMancantiEconomica.get(#lotto)" var="documento" status="stat">
												<s:if test="%{#documento.obbligatorio}">
													
													<li><s:property value="%{#documento.descrizione}"/></li>
													
												</s:if>
											</s:iterator>
										</ul>
									</s:if>
									<s:elseif test="%{!bustaRiepilogativa.primoAccessoEconomicheEffettuato.get(#lotto)}">
										<wp:i18n key="LABEL_NO_VISIONE_DOCUMENTAZIONE_NECESSARIA" />
									</s:elseif>
									<s:else>
										<img class="resize-svg-16" src="${imgCheck}" title="<wp:i18n key='TITLE_BUSTA_PRONTA_PER_INVIO' />" alt="<wp:i18n key='TITLE_BUSTA_PRONTA_PER_INVIO' />"/>
									</s:else>
								</div>
							</div>
						</s:if>
						<c:if test='${"1".equals(statoEconomica[lotto]) || "".equals(statoEconomica[lotto])}'>
							<form action="<wp:action path='/ExtStr2/do/FrontEnd/GareTel/redirectSummaryEnv.action' />" method="post">
								<div class="azioni">
									<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
									<input type="submit" value="Compila" title='<wp:i18n key="TITLE_MODIFICA_BUSTA" />' class="button" name="method:doModify" />
									<c:if test='${"1".equals(statoEconomica[lotto])}'>
										<input type="submit" value="<wp:i18n key='LABEL_ELIMINA' />" title="<wp:i18n key='LABEL_ELIMINA' />" class="button" name="method:doReset" />
									</c:if>
									<input type="hidden" name="tipoBusta" value="${BUSTA_ECONOMICA}" />
									<input type="hidden" name="codiceGara" value="${codiceGara}" />
									<input type="hidden" name="codice" value="${lotto}" />
									<input type="hidden" name="operazione" value="${operazione}" />
									<s:if test="%{#wizardOfferta.get(#lotto)}">
										<input type="hidden" name="offertaTelematica" value="${true}" />
									</s:if>
									<s:else>
										<input type="hidden" name="offertaTelematica" value="${false}" />
									</s:else>
									<input type="hidden" name="backActionPath" value="/ExtStr2/do/FrontEnd/GareTel/openRiepilogoOfferteDistinte.action" />
								</div>
							</form>
						</c:if>
					</fieldset>
				</s:if>
			</fieldset>
		</s:if>
	</s:iterator>


	<div class="back-link">
		<a href="<wp:action path="/ExtStr2/do/FrontEnd/GareTel/openGestioneBusteDistinte.action" />&amp;codice=${codiceGara}&amp;operazione=${operazione}&amp;progressivoOfferta=${progressivoOfferta}&amp;ext=${param.ext}">
			<wp:i18n key="BUTTON_WIZARD_BACK_TO_MENU" />
		</a>
	</div>
</div>