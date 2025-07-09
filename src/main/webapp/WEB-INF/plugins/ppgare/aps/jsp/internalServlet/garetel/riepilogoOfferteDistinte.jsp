<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<c:set var="isInvioOfferta" value="${operazione == inviaOfferta}"/>
<c:set var="isPartecipazione" value="${operazione == presentaPartecipazione}"/>

<c:set var="titolo"><wp:i18n key='TITLE_PAGE_GARETEL_RIEPILOGO_OFFERTE_DISTINTE'/></c:set>
<c:set var="codiceBalloon" value="BALLOON_RIEPILOGO_OFFERTA"/>


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2>${titolo} [<s:property value="%{dettGara.datiGeneraliGara.codice}" />]</h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="${codiceBalloon}"/>
	</jsp:include>

	<s:url id="urlDownloadBusta" namespace="/do/FrontEnd/GareTel" action="downloadBusta" />

	<fieldset>
		<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key='LABEL_DATI_OPERATORE_ECONOMICO'/></legend>
		
		<div class="fieldset-row first-row">
			<div class="label">
				<label><wp:i18n key="LABEL_GARETEL_OGGETTO" /> : </label>
			</div>
			<div class="element">
				<s:property value="%{dettGara.datiGeneraliGara.oggetto}" />
			</div>
		</div>
	
		<div class="fieldset-row">
			<div class="label">
				<label><wp:i18n key="LABEL_OPERATORE_ECONOMICO" /> : </label>
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
				<s:else><wp:i18n key="LABEL_NOT_DEFINED" /></s:else>
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
		
		<c:if test="${isInvioOfferta}">
			<div class="fieldset-row last-row">
				<div class="label">
					<label><wp:i18n key="LABEL_CODICE_CNEL" /> : </label>
				</div>
				<div class="element">
					<s:property value="%{codiceCNEL}" />
				</div>
			</div>
		</c:if>
			
	</fieldset>

	<fieldset>
		<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_DOCUMENTAZIONE_ALLEGATA" /></legend>
		
		<s:if test="%{hasFileRiepilogoAllegati}">

			<div class="fieldset-row">
				<div class="label">
					<label><wp:i18n key="LABEL_FILE_RIEPILOGO_ALLEGATI" /> : </label>
				</div>
				<div class="element">
					<b>
					<s:if test="%{ !riepilogoAllegatiFirmato }">
						<s:url id="urlDownloadFileRiepilogo" namespace="/do/FrontEnd/GareTel" action="downloadFileRiepilogoAllegatiAction" />
						<s:a href="%{#urlDownloadFileRiepilogo}?id=%{idBustaRiepilogo}" 
							title="%{#attr.urlDownloadFileRiepilogo}">
							<wp:i18n key="LABEL_SCARICA_ALLEGATO" />
						</s:a>
					</s:if>
					<s:else>
						<form action="<wp:action path="/ExtStr2/do/FrontEnd/GareTel/downloadFileRiepilogoAllegatiAction.action" />" method="post" >
							<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
							<a href="javascript:;" onclick="parentNode.submit();" 
								title="<s:property value='%{#attr.urlDownloadFileRiepilogo}'/>" >
	    						<wp:i18n key="LABEL_SCARICA_ALLEGATO" />
	    					</a>
	    					<input type="hidden" name="id" value="${idBustaRiepilogo}" />
						</form>
					</s:else>
					</b>
				</div>
			</div>
		</s:if>
		
		<%-- BUSTA AMMINISTRATIVA : LIVELLO DI GARA --%>
		
		<div class="fieldset-row first-row">
			<div class="label">
				<label><wp:i18n key="LABEL_BUSTA_AMMINISTRATIVA" /> : </label>
			</div>
			<div class="element">
				<s:if test="%{!bustaAmministrativaCifrata}">
					<s:if test="%{idBustaAmm != null}">
						<wp:i18n key="TITLE_DOWNLOAD_DOCUMENTI_BUSTA_AMMINISTRATIVA" var="titleDownloadBustaAmm" />
						<c:choose>
							<c:when test="${skin == 'highcontrast' || skin == 'text'}">
								<s:a href="%{#urlDownloadBusta}?idBusta=%{idBustaAmm}&amp;tipoBusta=%{BUSTA_AMMINISTRATIVA}&amp;codice=%{codice}" 
										title="%{#attr.titleDownloadBustaAmm}">
									 <wp:i18n key="LABEL_DOWNLOAD_DOCUMENTI" />
								</s:a>
							</c:when>
							<c:otherwise>
								<s:a href="%{#urlDownloadBusta}?idBusta=%{idBustaAmm}&amp;tipoBusta=%{BUSTA_AMMINISTRATIVA}&amp;codice=%{codice}" 
										title="%{#attr.titleDownloadBustaAmm}" cssClass="bkg zip">
									<wp:i18n key="LABEL_DOWNLOAD_DOCUMENTI" />
								</s:a>
							</c:otherwise>
						</c:choose>
					</s:if>
					<s:else>
						<wp:i18n key="LABEL_NO_DOCUMENTS_ATTACHED" />
					</s:else>
				</s:if>
				<s:else>
					<!-- INFO STATICHE -->
					<%--
					<s:if test="%{bustaRiepilogativa.bustaAmministrativa.questionarioPresente}">
						<div class="fieldset-row">
							<div class="label">
								<label><wp:i18n key="LABEL_QUESTIONARIO_COMPLETATO" /> : </label>
							</div>
							<div class="element">
								<s:if test="%{bustaRiepilogativa.busteEconomicheLotti.get(#lotto).questionarioCompletato}">
									<wp:i18n key="LABEL_YES" />
								</s:if>
								<s:else>
									<wp:i18n key="LABEL_NO" />
								</s:else>
							</div>
						</div>
					</s:if>
					--%>
					<s:if test="%{bustaRiepilogativa.bustaAmministrativa.documentiInseriti.size() > 0}">
						<ul class="list">
							<s:iterator value="%{bustaRiepilogativa.bustaAmministrativa.documentiInseriti}" var="documento" status="stat">
								<%-- in caso di gestione dei questionari l'allegato "questionario.json" non va visualizzato --%>
								<s:if test="%{ !'questionario.json'.equals(#documento.nomeFile) }" >
									
									<li class='<s:if test="%{#stat.first}">first</s:if> <s:if test="%{#stat.last}">last</s:if>'>
										<c:choose>
											<c:when test="${skin == 'highcontrast' || skin == 'text'}">
												<s:if test="%{#documento.required}">
													 &#8226;&nbsp;<span  title="<s:property value="%{#documento.nomeFile}"/> (<s:property value="%{#documento.dimensione}" /> KB), SHA1 &nbsp;<s:property value="%{#documento.sha1}"/>">&nbsp;<s:property value="%{#documento.descrizione}"/></span>
												</s:if>
												<s:else>
													 &#8226;&nbsp;<span title="<s:property value="%{#documento.nomeFile}"/> (<s:property value="%{#documento.dimensione}" /> KB, SHA1 &nbsp;<s:property value="%{#documento.sha1}"/>)">&nbsp;<s:property value="%{#documento.descrizione}"/></span>
												</s:else>
											</c:when>
											<c:otherwise>
												<s:if test="%{#documento.required}">
													 &#8226;&nbsp;<span title="<s:property value="%{#documento.nomeFile}"/> (<s:property value="%{#documento.dimensione}" /> KB), SHA1 &nbsp;<s:property value="%{#documento.sha1}"/>">&nbsp;<s:property value="%{#documento.descrizione}"/></span>
												</s:if>
												<s:else>
													 &#8226;&nbsp;<span title="<s:property value="%{#documento.nomeFile}"/> (<s:property value="%{#documento.dimensione}" /> KB), SHA1 &nbsp;<s:property value="%{#documento.sha1}"/>">&nbsp;<s:property value="%{#documento.descrizione}"/></span>
												</s:else>
											</c:otherwise>
										</c:choose>
										&nbsp;(<s:property value="%{#documento.nomeFile}"/> )
									</li>
								</s:if>
							</s:iterator>
						</ul>
					</s:if>
					<s:else>
						<wp:i18n key="LABEL_NO_DOCUMENTS_ATTACHED" />
					</s:else>
				</s:else>

				<!-- RETTIFICA NON PREVISTA -->
			</div>
		</div>
		
		<%-- BUSTA TECNICA E BUSTA ECONOMICA --%>
		
		<s:set var="idxListaBusteTec" value="0"/>
		<s:set var="idxListaBusteEco" value="0"/>
		<s:iterator value="%{bustaRiepilogativa.listaCompletaLotti}" var="lotto" status="stat">
		
			<!-- OEPV costoFisso=1 ==> non c'e' offerta economica -->
			<s:set var="costoFisso" value="0"/>
			<s:iterator var="item" value="%{dettGara.lotto}"  >
				<s:if test="%{#item.codiceLotto eq #lotto}" >
					<s:set var="costoFisso" value="%{#item.costoFisso}"/>
				</s:if>
			</s:iterator>
			
			<s:set var="lottoTecPresente" value="%{bustaRiepilogativa.busteTecnicheLotti.get(#lotto) != null}"/>
			<s:set var="lottoEcoPresente" value="%{bustaRiepilogativa.busteEconomicheLotti.get(#lotto) != null}"/>
			
			<s:if test="%{#lottoTecPresente || #lottoEcoPresente}">
			<fieldset>	
				<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_LOTTO" /> <s:property value="%{bustaRiepilogativa.listaCodiciInterniLotti.get(#lotto)}" /></legend>
				 <div class="fieldset-row first-row">
					<div class="label">
						<label><wp:i18n key="LABEL_GARETEL_OGGETTO" /> : </label>
					</div>
					<div class="element">
						<label>
							<s:if test="%{#lottoEcoPresente}">
								<s:property value="%{bustaRiepilogativa.busteEconomicheLotti.get(#lotto).oggetto}"/>
							</s:if>
							<s:elseif test="%{#lottoTecPresente}">
								<s:property value="%{bustaRiepilogativa.busteTecnicheLotti.get(#lotto).oggetto}"/>
							</s:elseif>
						</label>
					</div>
				</div>
				
				<%-- LOTTO BUSTA TECNICA --%>
					
				<s:if test="%{#lottoTecPresente}">
					<div class="fieldset-row">
						<div class="label">
							<label><wp:i18n key="LABEL_BUSTA_TECNICA" /> : </label>
						</div>
						<div class="element">
							<s:if test="%{!bustaTecnicaCifrata}">
								<s:if test="%{listaIdBusteTec != null && !listaIdBusteTec.isEmpty()}">
									<wp:i18n key="TITLE_DOWNLOAD_DOCUMENTI_BUSTA_TECNICA" var="titleDownloadBustaTec" />
									<c:choose>
										<c:when test="${skin == 'highcontrast' || skin == 'text'}">
											<s:a href="%{#urlDownloadBusta}?idBusta=%{listaIdBusteTec.get(#stat.index)}&amp;tipoBusta=%{BUSTA_TECNICA}&amp;codice=%{codice}" 
													title="%{#attr.titleDownloadBustaTec}">
												<wp:i18n key="LABEL_DOWNLOAD_DOCUMENTI" />
											</s:a>
										</c:when>
										<c:otherwise>
											<s:a href="%{#urlDownloadBusta}?idBusta=%{listaIdBusteTec.get(#stat.index)}&amp;tipoBusta=%{BUSTA_TECNICA}&amp;codice=%{codice}" 
													title="%{#attr.titleDownloadBustaTec}" cssClass="bkg zip">
												<wp:i18n key="LABEL_DOWNLOAD_DOCUMENTI" />
											</s:a>
										</c:otherwise>
									</c:choose>
									<s:set var="idxListaBusteTec" value="%{#idxListaBusteTec + 1}"/>
								</s:if>
								<s:else>
									<wp:i18n key="LABEL_NO_DOCUMENTS_ATTACHED" />
								</s:else>
							</s:if>
							<s:else>
								<%--
								<s:if test="%{bustaRiepilogativa.busteTecnicheLotti.get(#lotto).questionarioPresente}">
									<div class="fieldset-row">
										<div class="label">
											<label><wp:i18n key="LABEL_QUESTIONARIO_COMPLETATO" /> : </label>
										</div>
										<div class="element">
											<s:if test="%{bustaRiepilogativa.busteEconomicheLotti.get(#lotto).questionarioCompletato}">
												<wp:i18n key="LABEL_YES" />
											</s:if>
											<s:else>
												<wp:i18n key="LABEL_NO" />
											</s:else>
										</div>
									</div>
								</s:if>
								--%>
								<s:if test="%{bustaRiepilogativa.busteTecnicheLotti.get(#lotto).documentiInseriti.size() > 0}">
									<ul class="list">
										<s:iterator value="bustaRiepilogativa.busteTecnicheLotti.get(#lotto).documentiInseriti" var="documento" status="stat">
											<%-- in caso di gestione dei questionari l'allegato "questionario.json" non va visualizzato --%>
											<s:if test="%{ !'questionario.json'.equals(#documento.nomeFile) }" >
											
												<li class='<s:if test="%{#stat.first}">first</s:if> <s:if test="%{#stat.last}">last</s:if>'>
													<c:choose>
														<c:when test="${skin == 'highcontrast' || skin == 'text'}">
															<s:if test="%{#documento.required}">
																 &#8226;&nbsp;<span title="<s:property value="%{#documento.nomeFile}"/> (<s:property value="%{#documento.dimensione}" /> KB), SHA1 &nbsp;<s:property value="%{#documento.sha1}"/>"><s:property value="%{#documento.descrizione}"/></span>
															</s:if>
															<s:else>
																 &#8226;&nbsp;<span title="<s:property value="%{#documento.nomeFile}"/> (<s:property value="%{#documento.dimensione}" /> KB), SHA1 &nbsp;<s:property value="%{#documento.sha1}"/>"><s:property value="%{#documento.descrizione}"/></span>
															</s:else>
														</c:when>
														<c:otherwise>
															<s:if test="%{#documento.required}">
																 &#8226;&nbsp;<span title="<s:property value="%{#documento.nomeFile}"/> (<s:property value="%{#documento.dimensione}" /> KB),SHA1 &nbsp;<s:property value="%{#documento.sha1}"/>"><s:property value="%{#documento.descrizione}"/></span>
															</s:if>
															<s:else>
																 &#8226;&nbsp;<span title="<s:property value="%{#documento.nomeFile}"/> (<s:property value="%{#documento.dimensione}" /> KB), SHA1 &nbsp;<s:property value="%{#documento.sha1}"/>"><s:property value="%{#documento.descrizione}"/></span>
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
									<wp:i18n key="LABEL_NO_DOCUMENTS_ATTACHED" />
								</s:else>
							</s:else>
							
							<s:if test="%{!abilitaRettifica && lottoTecnicaRettifica.get(#lotto) != null}">
								<s:set var="richiestaRettifica" value="lottoTecnicaRettifica.get(#lotto)"/>
								<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/garetel/inc/infoRichiestaRettifica.jsp"/>
							</s:if>
							
						</div>
					</div>
				</s:if>

				<%-- LOTTO BUSTA ECONOMICA --%>
				
				<s:if test="%{#lottoEcoPresente && #costoFisso != 1}" >
					<div class="fieldset-row">
						<div class="label">
							<label><wp:i18n key="LABEL_BUSTA_ECONOMICA" /> : </label>
						</div>
						<div class="element">
							<s:if test="%{!bustaEconomicaCifrata}">
								<s:if test="%{listaIdBusteEco != null}">
									<wp:i18n key="TITLE_DOWNLOAD_DOCUMENTI_BUSTA_ECONOMICA" var="titleDownloadBustaEco" />
									<c:choose>
										<c:when test="${skin == 'highcontrast' || skin == 'text'}">
											<s:a href="%{#urlDownloadBusta}?idBusta=%{listaIdBusteEco.get(#stat.index)}&amp;tipoBusta=%{BUSTA_ECONOMICA}&amp;codice=%{codice}" 
													title="%{#attr.titleDownloadBustaEco}">
												<wp:i18n key="LABEL_DOWNLOAD_DOCUMENTI" />
											</s:a>
										</c:when>
										<c:otherwise>
											<s:a href="%{#urlDownloadBusta}?idBusta=%{listaIdBusteEco.get(#stat.index)}&amp;tipoBusta=%{BUSTA_ECONOMICA}&amp;codice=%{codice}" 
													title="%{#attr.titleDownloadBustaEco}" cssClass="bkg zip">
												<wp:i18n key="LABEL_DOWNLOAD_DOCUMENTI" />
											</s:a>
										</c:otherwise>
									</c:choose>
									<s:set var="idxListaBusteEco" value="%{#idxListaBusteEco + 1}"/>
								</s:if>
								<s:else>
									<wp:i18n key="LABEL_NO_DOCUMENTS_ATTACHED" />
								</s:else>
							</s:if>
							<s:else>
								<%--
								<s:if test="%{bustaRiepilogativa.busteEconomicheLotti.get(#lotto).questionarioPresente}">
									<div class="fieldset-row">
										<div class="label">
											<label><wp:i18n key="LABEL_QUESTIONARIO_COMPLETATO" /> : </label>
										</div>
										<div class="element">
											<s:if test="%{bustaRiepilogativa.busteEconomicheLotti.get(#lotto).questionarioCompletato}">
												<wp:i18n key="LABEL_YES" />
											</s:if>
											<s:else>
												<wp:i18n key="LABEL_NO" />
											</s:else>
										</div>
									</div>
								</s:if>
								--%>
								<s:if test="%{bustaRiepilogativa.busteEconomicheLotti.get(#lotto).documentiInseriti.size() > 0}">
									<ul class="list">
										<s:iterator value="bustaRiepilogativa.busteEconomicheLotti.get(#lotto).documentiInseriti" var="documento" status="stat">
											<%-- in caso di gestione dei questionari l'allegato "questionario.json" non va visualizzato --%>
											<s:if test="%{ !'questionario.json'.equals(#documento.nomeFile) }" >
											
												<li class='<s:if test="%{#stat.first}">first</s:if> <s:if test="%{#stat.last}">last</s:if>'>
													<c:choose>
														<c:when test="${skin == 'highcontrast' || skin == 'text'}">
															<s:if test="%{#documento.required}">
																 &#8226;&nbsp;<span title="<s:property value="%{#documento.nomeFile}"/> (<s:property value="%{#documento.dimensione}" /> KB), SHA1 &nbsp;<s:property value="%{#documento.sha1}"/>"><s:property value="%{#documento.descrizione}"/></span>
															</s:if>
															<s:else>
																 &#8226;&nbsp;<span title="<s:property value="%{#documento.nomeFile}"/> (<s:property value="%{#documento.dimensione}" /> KB), SHA1 &nbsp;<s:property value="%{#documento.sha1}"/>"><s:property value="%{#documento.descrizione}"/></span>
															</s:else>
														</c:when>
														<c:otherwise>
															<s:if test="%{#documento.required}">
																 &#8226;&nbsp;<span title="<s:property value="%{#documento.nomeFile}"/> (<s:property value="%{#documento.dimensione}" /> KB), SHA1 &nbsp;<s:property value="%{#documento.sha1}"/>"><s:property value="%{#documento.descrizione}"/></span>
															</s:if>
															<s:else>
																 &#8226;&nbsp;<span title="<s:property value="%{#documento.nomeFile}"/> (<s:property value="%{#documento.dimensione}" /> KB), SHA1 &nbsp;<s:property value="%{#documento.sha1}"/>"><s:property value="%{#documento.descrizione}"/></span>
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
									<wp:i18n key="LABEL_NO_DOCUMENTS_ATTACHED" />
								</s:else>
							</s:else>
							
							<s:if test="%{!abilitaRettifica && lottoEconomicaRettifica.get(#lotto) != null}">
								<s:set var="richiestaRettifica" value="lottoEconomicaRettifica.get(#lotto)"/>
								<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/garetel/inc/infoRichiestaRettifica.jsp"/>
							</s:if>
							
						</div>
					</div>
				</s:if>
			</fieldset>
			</s:if>
			
		</s:iterator>

	</fieldset>

	<div class="azioni">
		<s:if test="%{abilitaRettifica}">
			<form action="<wp:action path="/ExtStr2/do/FrontEnd/GareTel/confirmRettificaOfferteDistinte.action" />" method="post" class="azione">
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
				
				<div>
					<input type="hidden" name="ext" value="${param.ext}" />
					<s:hidden name="codice" value="%{codice}" />
					<s:hidden name="operazione" value="%{operazione}" />
					<s:hidden name="progressivoOfferta" value="%{progressivoOfferta}" />
					<s:hidden name="fromListaOfferte" value="%{fromListaOfferte}" />
					<wp:i18n key="LABEL_ANNULLA_RIPRESENTA_OFFERTA" var="valueButtonAnnullaRipresenta" />
					<s:submit value="%{#attr.valueButtonAnnullaRipresenta}" title="%{#attr.valueButtonAnnullaRipresenta}" cssClass="button"></s:submit>
				</div>
			</form>
		</s:if>
	</div>
</div>

<div class="back-link">
	<a href="<wp:action path="/ExtStr2/do/FrontEnd/Bandi/view.action" />&amp;codice=${codice}&amp;ext=${param.ext}">
		<wp:i18n key="LINK_BACK_TO_PROCEDURE" />
	</a>
</div>