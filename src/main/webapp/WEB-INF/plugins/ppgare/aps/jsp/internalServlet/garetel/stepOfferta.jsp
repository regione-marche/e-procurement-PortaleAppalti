<%@ taglib prefix="wp"  uri="aps-core.tld" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="s"   uri="/struts-tags" %>

<!-- OBSOLETO <s:set var="helper" value="%{#session['offertaEconomica']}" /> -->
<!-- OBSOLETO <s:set var="riepilogoBuste" value="%{#session.riepilogoBuste}" /> -->
<s:set var="bustaEconomica" value="%{#session['dettaglioOffertaGara'].bustaEconomica}" />
<s:set var="helper" value="%{#bustaEconomica.helper}" />
<s:set var="codiceLotto" value="%{#bustaEconomica.codiceLotto}" />
<c:set var="chelper" scope="request" value="${sessionScope['dettaglioOffertaGara'].bustaEconomica.helper}"/>
<s:set var="riepilogoBuste" value="%{#session['dettaglioOffertaGara'].bustaRiepilogo.helper}" />
<s:set var="codiceTitolo" value="%{#codiceLotto}" />
<%--
session['dettaglioOffertaGara']=<s:property value="%{#session['dettaglioOffertaGara']}" /><br/>
session['dettaglioOffertaGara'].bustaEconomica=<s:property value="%{#session['dettaglioOffertaGara'].bustaEconomica}" /><br/>
helper=<s:property value="%{#helper}" /><br/>
codice=<s:property value="%{#helper.codice}" /><br/>
 --%>

<s:set name="gara" value="%{#helper.gara}" />
<s:set name="cig" value="%{#gara.cig}"/>
<s:set name="cup" value="%{#gara.cup}"/>
<s:set name="importoNonSoggettoRibasso" value="%{#gara.importoNonSoggettoRibasso != null ? #gara.importoNonSoggettoRibasso : 0}"/>
<s:set name="importoSicurezza" value="%{#gara.importoSicurezza != null ? #gara.importoSicurezza : 0}"/>
<s:set name="importoEsclusoRibasso" value="%{#importoNonSoggettoRibasso + #importoSicurezza}"/>
<s:set name="oneriProgettazione" value="%{#gara.importoOneriProgettazione}" />
<s:set name="oneriSoggettiARibasso" value="%{#gara.oneriSoggettiRibasso}" />

<%-- Condizioni di visibilita dei campi  --%>
<s:set name="numDecimaliRibasso" value="%{#helper.numDecimaliRibasso}"/>
<s:set name="numDecimaliManodopera" value="5"/>
<s:set name="importoOneriProgettazioneVisible" value="%{#helper.importoOneriProgettazioneVisible}"/>
<s:set name="importoOffertoVisible" value="%{#helper.importoOffertoVisible}"/>
<s:set name="importoOffertoEditable" value="%{#helper.importoOffertoEditable}"/>
<s:set name="importoOffertoMandatory" value="%{#helper.importoOffertoMandatory}"/>
<s:set name="percentualeRibassoEditable" value="%{#helper.percentualeRibassoEditable}"/>
<s:set name="percentualeRibassoMandatory" value="%{#helper.percentualeRibassoMandatory}"/>
<s:set name="percentualeAumentoVisible" value="%{#helper.percentualeAumentoVisible}"/>
<s:set name="percentualeAumentoEditable" value="%{#helper.percentualeAumentoEditable}"/>
<s:set name="importoOffertoPerPermutaVisible" value="%{#helper.importoOffertoPerPermutaVisible}"/>
<s:set name="importoOffertoCanoneAssistenzaVisible" value="%{#helper.importoOffertoPerCanoneAssistenzaVisible}"/>
<s:set name="costiSicurezzaVisible" value="%{#helper.costiSicurezzaVisible}"/>
<s:set name="costiManodoperaVisible" value="%{#helper.costiManodoperaVisible}"/>
<s:set name="percentualeManodoperaVisible" value="%{#helper.percentualeManodoperaVisible}"/>

<s:set name="totaleRibassoVisible" value="%{#helper.gara.tipoRibasso == 3 &&
											(#helper.gara.modalitaAggiudicazione == 5 || #helper.gara.modalitaAggiudicazione == 14)}"/>
<s:set name="totaleRibasso" value="0" />
<s:iterator value="%{#helper.vociDettaglio}" status="stat">
	<s:set name="totaleRibasso" value="%{#totaleRibasso + ribassoPesato}" />
</s:iterator>

<%-- Note per importo offerto --%>
<s:set name="comprensivoNonSoggettiARibasso" value="%{#helper.comprensivoNonSoggettiARibasso}"/>
<s:set name="comprensivoOneriSicurezza" value="%{#helper.comprensivoOneriSicurezza}"/>
<s:set name="nettoOneriSicurezza" value="%{#helper.nettoOneriSicurezza}"/>
<s:set name="comprensivoOneriProgettazione" value="%{#helper.comprensivoOneriProgettazione}"/>
<s:set name="nonSuperioreAImportoBaseAsta" value="%{#helper.nonSuperioreAImportoBaseAsta}"/>
<s:set name="valoreMassimoImportoOfferto" value="%{#helper.valoreMassimoImportoOfferto}"/>

<%--
Offerta OEPV se:
	- procedura di gara telematica (GARTEL=1)
	- gara con upload documenti ed inserimento importi (OFFTEL=1)
	- gara/lotto OEPV (CODMODAGG=6)
	- esistenza di almeno un criterio di valutazione (almeno un'occorrenza
	  ottenuta da GOEV INNER JOIN G1CRIDEF con FORMATO<>100 e TIPPAR=2)
--%>
<s:set name="OEPV" value="%{#helper.criteriValutazioneVisibili}"/>


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2><wp:i18n key="TITLE_PAGE_GARETEL_OFFERTA_ECONOMICA" /> [${codiceTitolo}]</h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/field_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />

	<jsp:include page="stepsOffertaTelematica.jsp" />

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_WIZ_OFFERTA_OFFERTA_TEL"/>
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/skip_form_buttons.jsp" />

	<form action="<wp:action path="/ExtStr2/do/FrontEnd/GareTel/processPageOffTelOfferta.action" />" method="post" 
		  id="formOffertaEconomica" name="formOffertaEconomica">
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />

		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/mandatory_fields_message.jsp" />
		
		<fieldset>
			<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_GARETEL_DATI_DELLA_GARA" /></legend>
			
			<div class="fieldset-row first-row">
				<div class="label">
					<label><wp:i18n key="LABEL_TITOLO" /> :</label>
				</div>
				<div class="element">
					<s:property value="%{#gara.oggetto}" /> 
					<c:if test="${! empty gara.cig}">- <wp:i18n key="LABEL_CIG" /> : <s:property value="%{#gara.cig}" /></c:if>
					<c:if test="${! empty gara.cup}">- <wp:i18n key="LABEL_CUP" /> : <s:property value="%{#gara.cup}" /></c:if>
				</div>
			</div>
			
			<s:if test="{!#riepilogoBuste.listaCodiciInterniLotti.isEmpty()}">
				<div class="fieldset-row">
					<div class="label">
						<label><wp:i18n key="LABEL_LOTTO" /> :</label>
					</div>
					<div class="element">
						<s:property value="%{#riepilogoBuste.listaCodiciInterniLotti.get(#helper.codice)}" /> - <s:property value="%{#riepilogoBuste.busteEconomicheLotti.get(#helper.codice).oggetto}" />
					</div>
				</div>
			</s:if>
			
			<div class="fieldset-row <s:if test="%{!((#importoNonSoggettoRibasso > 0)|| (#importoSicurezza > 0) || #importoOneriProgettazioneVisible)}">last-row</s:if>">
				<div class="label">
					<label><wp:i18n key="LABEL_IMPORTO_BASE_GARA" /> :</label>
				</div>
				<div class="element">
					<s:if test="%{#gara.importo != null}">
						<s:text name="format.money"><s:param value="%{#gara.importo}"/></s:text> &euro;
					</s:if>
				</div>
			</div>

			<s:if test="%{#importoNonSoggettoRibasso > 0}">
			<div class="fieldset-row <s:if test="%{!((#importoSicurezza > 0)|| #importoOneriProgettazioneVisible)}">last-row</s:if>">
				<div class="label">
					<label><wp:i18n key="LABEL_DI_CUI_NON_SOGGETTO_RIBASSO" /> :</label>
				</div>
				<div class="element">
						<s:text name="format.money"><s:param value="%{#importoNonSoggettoRibasso}"/></s:text> &euro;
				</div>
			</div>
			</s:if>
	
			<s:if test="%{#importoSicurezza > 0}">
				<div class="fieldset-row <s:if test="%{!(#importoOneriProgettazioneVisible)}">last-row</s:if>">
					<div class="label">
						<label><wp:i18n key="LABEL_DI_CUI_SICUREZZA" /> :</label>
					</div>
					<div class="element">
						<s:text name="format.money"><s:param value="%{#importoSicurezza}"/></s:text> &euro;
					</div>
				</div>
			</s:if>
			
			<s:if test="%{#importoOneriProgettazioneVisible}">
				<div class="fieldset-row last-row">
					<div class="label">
						<label><wp:i18n key="LABEL_DI_CUI_ONERI_PROGETTAZIONE" /> : </label>
					</div>
					<div class="element">
						<s:text name="format.money"><s:param value="%{#oneriProgettazione}"/></s:text> &euro; 
						<s:if test="%{!#oneriSoggettiARibasso}">
						&nbsp;(<wp:i18n key="LABEL_NON_SOGGETTI_RIBASSO" />)
						</s:if>
					</div>
				</div>
			</s:if>

		</fieldset>		
		<fieldset>
			<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="TITLE_PAGE_GARETEL_OFFERTA_ECONOMICA" /></legend>
			
			<%-- CRITERI DI VALUTAZIONE PER OEPV 								--%>
			<%-- In caso di OEPV "importo offerto", "Aumento" e "Ribasso"		--%>
			<%-- vanno nascosti e vanno visualizzati i criteri di valutazione	--%>
			<s:if test="%{OEPV}" >						
				<c:set var="criteriValutazione" scope="request" value="${chelper.listaCriteriValutazione}"/>							
				<c:set var="criteriValutazioneEditabile" scope="request" value="${chelper.criterioValutazioneEditabile}" />
				<c:set var="criteriValutazioneValore" scope="request" value="${criterioValutazione}"/>
				<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/garetel/inc/criteriValutazioneOEPV.jsp">
					<jsp:param name="tipoBusta" value="${BUSTA_ECONOMICA}" />
				</jsp:include>
				<s:hidden id="importoOfferto" name="importoOfferto" value="%{#helper.importoOffertoNotazioneStandard}"></s:hidden>
		 		<s:hidden id="ribasso" name="ribasso"  value="%{ribasso}"></s:hidden>
		 		<s:hidden id="aumento" name="aumento"  value="%{aumento}"></s:hidden>
			</s:if>
			<s:else>
				<s:if test="%{#importoOffertoVisible}">
					<div class="fieldset-row first-row">
						<div class="label">
							<label><wp:i18n key="LABEL_IMPORTO_OFFERTO" /> : 
								<s:if test="%{#importoOffertoMandatory}"><span class="required-field">*</span></s:if> 
							</label>
						</div>
						<div class="element">
							<div class="contents-group">
								<s:if test="%{#importoOffertoEditable}">
									<s:textfield name="importoOfferto" maxlength="16" size="20" aria-required="true" aria-label='<wp:i18n key="LABEL_IMPORTO_OFFERTO" />'/> &euro; (<wp:i18n key="LABEL_INDICARE_MAX" /> 5 <wp:i18n key="LABEL_DECIMALI" />)
								</s:if>
								<s:else>
									<s:text name="format.money5dec"><s:param value="%{#helper.importoOfferto}"/></s:text> &euro;
									<s:hidden id="importoOfferto" name="importoOfferto" value="%{#helper.importoOffertoNotazioneStandard}"></s:hidden>
								</s:else>
							</div>
							<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/garetel/inc/notePrezziUnitari.jsp"></jsp:include>
						</div>
					</div>
				</s:if>
	
				<%-- Attenzione 
				    1) Se la modalita di aggiudicazione = 17 si da evidenza prima all'aumento e poi al ribasso 
				    2) Se la modalita di aggiudicazione <> 17 si da evidenza prima al ribasso e poi all'aumento
				--%>
				<s:set name="primaAumento" value="%{#helper.gara.modalitaAggiudicazione == 17}"/>
				
				<s:if test="%{#primaAumento}">
					<s:if test="%{#percentualeAumentoVisible}">
						<div class="fieldset-row">
							<div class="label">
								<label for="aumento"><wp:i18n key="LABEL_AUMENTO_PERCENTUALE" /> : </label>
							</div>
							<div class="element">
								<div class="contents-group">
									<s:if test="%{#percentualeAumentoEditable}">
										<s:textfield id="aumento" name="aumento"  value="%{aumento}" maxlength="13" size="20" readonly="%{!#percentualeAumentoEditable}"/> (<wp:i18n key="LABEL_INDICARE_MAX" /> <s:property value="%{#numDecimaliRibasso}"/> <wp:i18n key="LABEL_DECIMALI" />)
									</s:if>
									<s:else>
										<s:hidden id="aumento" name="aumento"  value="%{aumento}"></s:hidden>
									</s:else>
								</div>
							</div>
						</div>
					</s:if>	
				</s:if>
				
				<s:if test="%{#percentualeRibassoEditable || #totaleRibassoVisible}">
					<div class="fieldset-row <s:if test="%{!#importoOffertoVisible}">first-row</s:if>">
						<div class="label">
							<label><wp:i18n key="LABEL_RIBASSO_PERCENTUALE" /> :<s:if test="%{#percentualeRibassoMandatory}"> 
									<span class="required-field">*</span>
								</s:if> </label>
						</div>
						<div class="element">
							<div class="contents-group">
								<s:if test="%{#percentualeRibassoEditable}">
									<s:textfield id="ribasso" name="ribasso" value="%{ribasso}" maxlength="13" size="20" readonly ="%{!#percentualeRibassoEditable}" aria-required="true" aria-label='<wp:i18n key="LABEL_RIBASSO_PERCENTUALE" />'/> (<wp:i18n key="LABEL_INDICARE_MAX" /> <s:property value="%{#numDecimaliRibasso}"/> <wp:i18n key="LABEL_DECIMALI" />)
								</s:if>
								<s:else>
									<s:if test="%{#totaleRibassoVisible}">
										<s:text name="format.numberdec"><s:param value="%{#totaleRibasso}"/></s:text>
										<s:hidden id="ribasso" name="ribasso" value="%{totaleRibasso}"></s:hidden>
									</s:if>
									<s:else>
										<s:hidden id="ribasso" name="ribasso" value="%{ribasso}"></s:hidden>
									</s:else>
								</s:else>
							</div>
						</div>
					</div>
				</s:if>

				<s:if test="%{!#primaAumento}">
					<s:if test="%{#percentualeAumentoVisible}">
						<div class="fieldset-row">
							<div class="label">
								<label for="aumento"><wp:i18n key="LABEL_AUMENTO_PERCENTUALE" /> : </label>
							</div>
							<div class="element">
								<div class="contents-group">
									<s:if test="%{#percentualeAumentoEditable}">
										<s:textfield id="aumento" name="aumento"  value="%{aumento}" maxlength="13" size="20" readonly="%{!#percentualeAumentoEditable}"/> (<wp:i18n key="LABEL_INDICARE_MAX" /> <s:property value="%{#numDecimaliRibasso}"/> <wp:i18n key="LABEL_DECIMALI" />)
									</s:if>
									<s:else>
										<s:hidden id="aumento" name="aumento"  value="%{aumento}"></s:hidden>
									</s:else>
								</div>
							</div>
						</div>
					</s:if>	
				</s:if>
			</s:else>

			<!-- 
			<div class="fieldset-row <s:if test="%{!#importoOffertoPerPermutaVisible && !#importoOffertoCanoneAssistenzaVisible}">last-row</s:if>">
			 -->
			<s:if test="%{#costiSicurezzaVisible}">
				<div class="fieldset-row">
					<div class="label">
						<label for="costiSicurezzaAziendali"><wp:i18n key="LABEL_COSTI_SICUREZZA_AZIENDALE" /> : <span class="required-field">*</span></label>
					</div>
					<div class="element">
						<div class="contents-group">
							<s:textfield name="costiSicurezzaAziendali" value="%{costiSicurezzaAziendali}" maxlength="16" size="20" aria-required="true" /> &euro;
						</div>
					</div>
				</div>
			</s:if>
			
			<%-- Costi manodopera (art.95 c.10 DLgs.50/2016) --%>
			<s:if test="%{#costiManodoperaVisible}">
				<div class="fieldset-row <s:if test="%{!#importoOffertoPerPermutaVisible && !#importoOffertoCanoneAssistenzaVisible}">last-row</s:if>">
					<s:if test="%{#percentualeManodoperaVisible}">
						<div class="label">
							<label for="percentualeManodopera"><wp:i18n key="LABEL_PERCENTUALE_COSTI_MANODOPERA" /> : <span class="required-field">*</span></label>
						</div>
						<div class="element">
							<div class="contents-group">
								<input type="text" name="percentualeManodopera" data-parsley-trigger="change" data-parsley-pattern="^\d{0,3}(\.\d{0,<s:property value='%{#numDecimaliManodopera}'/>})?$"
										value="${percentualeManodopera}" maxlength="13" size="20" aria-required="true" /> (<wp:i18n key="LABEL_INDICARE_MAX" /> <s:property value="%{#numDecimaliManodopera}"/> <wp:i18n key="LABEL_DECIMALI" />
							</div>
						</div>
					</s:if>
					<s:else>
						<div class="label">
							<label for="importoManodopera"><wp:i18n key="LABEL_COSTI_MANODOPERA" /> : <span class="required-field">*</span></label>
						</div>
						<div class="element">
							<div class="contents-group">
								<s:textfield name="importoManodopera" value="%{importoManodopera}" maxlength="16" size="20" aria-required="true" /> &euro;
							</div>
						</div>
					</s:else>
				</div>
			</s:if>
				
			<%-- PERMUTA --%>
			<s:if test="%{#importoOffertoPerPermutaVisible}">
				<div class="fieldset-row <s:if test="%{!#importoOffertoCanoneAssistenzaVisible}">last-row</s:if>">
					<div class="label">
						<label for="importoOffertoPerPermuta"><wp:i18n key="LABEL_IMPORTO_OFFERTO_PERMUTA" /> : </label>
					</div>
					<div class="element">
						<div class="contents-group">
							<s:textfield name="importoOffertoPerPermuta" value="%{importoOffertoPerPermuta}" maxlength="16" size="20"/> &euro;
						</div>
					</div>
				</div>
			</s:if>
			
			<%-- CANONE ASSISTENZA --%>
			<s:if test="%{#importoOffertoCanoneAssistenzaVisible}">
				<div class="fieldset-row last-row">
					<div class="label">
						<label for="importoOffertoCanoneAssistenza"><wp:i18n key="LABEL_IMPORTO_OFFERTO_CANONE_ASSISTENZA" /> : </label>
					</div>
					<div class="element">
						<div class="contents-group">
							<s:textfield name="importoOffertoCanoneAssistenza" value="%{importoOffertoCanoneAssistenza}" maxlength="16" size="20"/> &euro;
						</div>
					</div>
				</div>
			</s:if>
			
		</fieldset>

		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/skip_main_content_from_buttons.jsp" />

		<div class="azioni">		
			<input type="hidden" name="codice" value='<s:property value="%{#helper.codice}"/>'/>
			<s:hidden name="obblImportoOfferto"></s:hidden>
			<s:hidden name="costiManodoperaObbligatori"></s:hidden>
			<s:hidden name="percentualeManodoperaVisibile"></s:hidden>
			
			<s:if test="%{#helper.stepNavigazione.contains(STEP_PREZZI_UNITARI)}">
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_previous.jsp" />
			</s:if>
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_next.jsp" />
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_cancel.jsp" />
		</div>
	</form>
</div>


<script type="text/javascript">
  $('#formOffertaEconomica').parsley();
  window.Parsley.setLocale('it');
</script>

