<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<c:set var="titolo"><wp:i18n key="TITLE_PAGE_GARETEL_RIEPILOGO_OFFERTE_DISTINTE"/></c:set>
<c:set var="codiceBalloon" value="BALLOON_INVIO_BUSTE_TELEMATICHE_RIEPILOGO"/>
<s:set var="imgCheck"><wp:resourceURL />static/img/check.svg</s:set>


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2>${titolo}</h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/field_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />

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
				<s:property value="%{#session.dettaglioGara.datiGeneraliGara.oggetto}" />
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

		<div class="fieldset-row last-row">
			<div class="label">
				<label><wp:i18n key="LABEL_PARTECIPA_COME_MANDATARIA_RTI" /> : </label>
			</div>
			<div class="element">
				<s:if test="%{rti}"><wp:i18n key="LABEL_YES" />, <wp:i18n key="LABEL_PER_CONTO_DI" /> <s:property value="%{denominazioneRti}"/></s:if>
				<s:else><wp:i18n key="LABEL_NO" /></s:else>
			</div>
		</div>		
	</fieldset>

	<!--  BUSTA AMMINISTRASTIVA  -->
	<fieldset>
		<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_BUSTA_AMMINISTRATIVA" /></legend>
		
		<s:if test="%{#session.dettBustaAmministrativa.datiModificati}">
			<div class="balloon">
				<div class="balloon-content balloon-alert">
					<wp:i18n key="LABEL_NO_MODIFICHE_AI_DOCUMENTI" />.
				</div>
			</div>
		</s:if>
		
		<div class="fieldset-row first-row">
			<div class="label">
				<label><wp:i18n key="LABEL_DOCUMENTI_INSERITI" /> <s:if test="%{bustaRiepilogativa.bustaAmministrativa.documentiInseriti.size() > 0}">(<s:property value="%{bustaRiepilogativa.bustaAmministrativa.documentiInseriti.size()}"/>)</s:if> : </label>
			</div>
			<div class="element">
				<s:if test="%{bustaRiepilogativa.bustaAmministrativa.documentiInseriti.size() > 0}">
					<ul class="list">
						<s:iterator value="bustaRiepilogativa.bustaAmministrativa.documentiInseriti" var="documento" status="stat">
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
						</s:iterator>
					</ul>
				</s:if>
				<s:else>
					<%-- Nessun documento ancora inserito. --%>
					<wp:i18n key="LABEL_NO_DOCUMENT" />. 
				</s:else>
			</div>
		</div>

		<div class="fieldset-row last-row">
			<div class="label">
				<label>
					<wp:i18n key="LABEL_DOCUMENTI_OBBLIGATORI_MANCANTI" /> <s:if test="%{docObbligatoriMancantiAmministrativa.size() > 0}">(<s:property value="%{docObbligatoriMancantiAmministrativa.size()}"/>) : </s:if> 
					<a title='<wp:i18n key="TITLE_MODIFICA_BUSTA" />' class="bkg modify" href='<wp:action path="/ExtStr2/do/FrontEnd/GareTel/openPageDocumenti.action" />&amp;tipoBusta=${BUSTA_AMMINISTRATIVA}&amp;codiceGara=${codiceGara}&amp;codice=${lotto}&amp;operazione=${operazione}&amp;${tokenHrefParams}'>
					</a> 
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
		
	</fieldset>

	<!-- BUSTE TECNICHE  ED ECONOMICHE LOTTO -->
	
	<s:if test="%{#session.dettBustaTecnica.datiModificati}">
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
		
		<s:if test="%{bustaRiepilogativa.busteTecnicheLotti.get(#lotto) != null ||
		              bustaRiepilogativa.busteEconomicheLotti.get(#lotto) != null}">
			<fieldset>
				<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_LOTTO" /> <s:property value="%{#session.riepilogoBuste.listaCodiciInterniLotti.get(#lotto)}" /></legend>
				
				<div class="fieldset-row first-row">
					<div class="label">
						<label><wp:i18n key="LABEL_GARETEL_OGGETTO" /> : </label>
					</div>
					<div class="element">
						<label>
							<s:if test="%{#costoFisso != 1}" >
								<s:property value="%{bustaRiepilogativa.busteEconomicheLotti.get(#lotto).oggetto}"/>																
							</s:if>
							<s:else>
								<s:property value="%{bustaRiepilogativa.busteTecnicheLotti.get(#lotto).oggetto}"/>
							</s:else> 
						</label>
					</div>
				</div>
				
				<s:if test="%{bustaRiepilogativa.busteTecnicheLotti.get(#lotto) != null}">
					<fieldset>
						<legend><wp:i18n key="LABEL_BUSTA_TECNICA" /></legend>
						<div class="fieldset-row">
							<div class="label">
								<label><wp:i18n key="LABEL_DOCUMENTI_INSERITI" /> <s:if test="%{bustaRiepilogativa.busteTecnicheLotti.get(#lotto).documentiInseriti.size() > 0}">(<s:property value="%{bustaRiepilogativa.busteTecnicheLotti.get(#lotto).documentiInseriti.size()}"/>)</s:if> : </label>
							</div>
							<div class="element">
								<s:if test="%{bustaRiepilogativa.busteTecnicheLotti.get(#lotto).documentiInseriti.size() > 0}">
									<ul class="list">
										<s:iterator value="bustaRiepilogativa.busteTecnicheLotti.get(#lotto).documentiInseriti" var="documento" status="stat">
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
										</s:iterator>
									</ul>
								</s:if>
								<s:else>
									<wp:i18n key="LABEL_NO_DOCUMENT" />.
								</s:else>
							</div>
						</div>
						<div class="fieldset-row">
							<div class="label">
								<label>
									<wp:i18n key="LABEL_DOCUMENTI_OBBLIGATORI_MANCANTI" /> <s:if test="%{docObbligatoriMancantiTecnica.get(#lotto).size > 0}">(<s:property value="%{docObbligatoriMancantiTecnica.get(#lotto).size()}"/>)</s:if> : 
									<a title='<wp:i18n key="TITLE_MODIFICA_BUSTA" />' class="bkg modify" href='<wp:action path="/ExtStr2/do/FrontEnd/GareTel/openPageDocumenti.action" />&amp;tipoBusta=${BUSTA_TECNICA}&amp;codiceGara=${codiceGara}&amp;codice=${lotto}&amp;operazione=${operazione}&amp;${tokenHrefParams}'>&nbsp;
									</a>
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
					</fieldset>
				</s:if>
				
				<s:if test="%{bustaRiepilogativa.busteEconomicheLotti.get(#lotto) != null && #costoFisso != 1}">
					<fieldset>
						<legend><wp:i18n key="LABEL_BUSTA_ECONOMICA" /></legend>
						<div class="fieldset-row">
							<div class="label">
								<label><wp:i18n key="LABEL_DOCUMENTI_INSERITI" /> <s:if test="%{bustaRiepilogativa.busteEconomicheLotti.get(#lotto).documentiInseriti.size() > 0}">(<s:property value="%{bustaRiepilogativa.busteEconomicheLotti.get(#lotto).documentiInseriti.size()}"/>)</s:if> : </label>
							</div>
							<div class="element">
								<s:if test="%{bustaRiepilogativa.busteEconomicheLotti.get(#lotto).documentiInseriti.size() > 0}">
									<ul class="list">
										<s:iterator value="bustaRiepilogativa.busteEconomicheLotti.get(#lotto).documentiInseriti" var="documento" status="stat">
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
										</s:iterator>
									</ul>
								</s:if>
								<s:else>
									<wp:i18n key="LABEL_NO_DOCUMENT" />.
								</s:else>
							</div>
						</div>
						<div class="fieldset-row last-row">
							<div class="label">
								<label>
									<wp:i18n key="LABEL_DOCUMENTI_OBBLIGATORI_MANCANTI" /> <s:if test="%{docObbligatoriMancantiEconomica.get(#lotto).size() > 0}">(<s:property value="%{docObbligatoriMancantiEconomica.get(#lotto).size()}"/>)</s:if> : 
										<s:if test="%{#session.wizardOfferta.get(#lotto)}">
											<a title='<wp:i18n key="TITLE_MODIFICA_BUSTA" />' class="bkg modify" 
											   href='<wp:action path="/ExtStr2/do/FrontEnd/GareTel/initOffTel.action" />&amp;tipoBusta=3&amp;operazione=${operazione}&amp;codice=<s:property value="%{#lotto}"/>&amp;codiceGara=${codiceGara}&amp;${tokenHrefParams}'>
											</a>
										</s:if>
										<s:else>
											<a class="bkg modify" href='<wp:action path="/ExtStr2/do/FrontEnd/GareTel/openPageDocumenti.action" />&amp;tipoBusta=${BUSTA_ECONOMICA}&amp;codiceGara=${codiceGara}&amp;codice=${lotto}&amp;operazione=${operazione}&amp;${tokenHrefParams}'>
											</a>
										</s:else>
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
					</fieldset>
				</s:if>
			</fieldset>
		</s:if>
	</s:iterator>


	<div class="back-link">
		<a href="<wp:action path="/ExtStr2/do/FrontEnd/GareTel/openGestioneBusteDistinte.action" />&amp;codice=${codiceGara}&amp;operazione=${operazione}&amp;progressivoOfferta=${progressivoOfferta}&amp;ext=${param.ext}&amp;${tokenHrefParams}">
			<wp:i18n key="BUTTON_WIZARD_BACK_TO_MENU" />
		</a>
	</div>
</div>