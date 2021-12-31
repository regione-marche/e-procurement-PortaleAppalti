<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>

<s:set var="helper" value="%{#session['dettRegistrImpresa']}" />

<s:set var="bloccoEdit">${param.noEdit}</s:set>
<c:if test="${param.noEdit}">
	<s:set var="classBlocco" value="%{'no-editable'}" />
</c:if>

<c:set var="registrazioneDitta" value="false"/>
<c:if test="${param.registrazione}">
	<c:set var="registrazioneDitta" value="${param.registrazione}"/>
</c:if>

<es:checkCustomization var="visDataNullaOstaAntimafiaCCIAA" objectId="IMPRESA-DATIULT-CCIAA" attribute="DATANULLAOSTAANTIMAFIA" feature="VIS" />
<es:checkCustomization var="obblDataNullaOstaAntimafiaCCIAA" objectId="IMPRESA-DATIULT-CCIAA" attribute="DATANULLAOSTAANTIMAFIA" feature="MAN" />
<es:checkCustomization var="visSezAbilitPreventiva" objectId="IMPRESA-DATIULT-SEZ" attribute="ABILITAZIONEPREVENTIVA" feature="VIS" />
<es:checkCustomization var="obblDataScadenzaAbilitPreventiva" objectId="IMPRESA-DATIULT-ABILITPREVENTIVA" attribute="DATASCADENZA" feature="MAN" />
<es:checkCustomization var="visZoneAttivita" objectId="IMPRESA-DATIULT" attribute="ZONEATTIVITA" feature="VIS" />
<es:checkCustomization var="obblZoneAttivita" objectId="IMPRESA-DATIULT" attribute="ZONEATTIVITA" feature="MAN" />
<es:checkCustomization var="obblSoggettoNormativeDURC" objectId="IMPRESA-DATIULT" attribute="SOGGOBBLIGODURC" feature="MAN" />
<es:checkCustomization var="obblAssunzioniObbligate" objectId="IMPRESA-DATIULT" attribute="SOGGASSUNZIONIOBBLIGATORIE" feature="MAN" />
<es:checkCustomization var="obblDipendentiTriennio" objectId="IMPRESA-DATIULT" attribute="DIPENDENTITRIENNIO" feature="MAN" />
<%-- <c:set var="obblClasseDimensione" value="true" /> --%>
<es:checkCustomization var="obblClasseDimensione" objectId="IMPRESA-DATIULT" attribute="CLASSEDIMENSIONE" feature="MAN" />
<es:checkCustomization var="obblSettoreAttivitaEconomica" objectId="IMPRESA-DATIULT" attribute="SETTOREATTIVITA" feature="MAN" />
<es:checkCustomization var="visIscrizioneElenchi189" objectId="IMPRESA-DATIULT" attribute="ISCRELENCHIDL189-2016" feature="VIS" />
<es:checkCustomization var="obblIscrizioneElenchi189" objectId="IMPRESA-DATIULT" attribute="ISCRELENCHIDL189-2016" feature="MAN" />
<es:checkCustomization var="visRating" objectId="IMPRESA-DATIULT" attribute="RATINGLEGALITA" feature="VIS" />
<es:checkCustomization var="obblRating" objectId="IMPRESA-DATIULT" attribute="RATINGLEGALITA" feature="MAN" />
<es:checkCustomization var="obblOggettoSociale" objectId="IMPRESA-DATIULT-CCIAA" attribute="OGGETTOSOCIALE" feature="MAN" />
<es:checkCustomization var="cciaVisible" objectId="IMPRESA-DATIULT" attribute="CCIAA" feature="VIS" />
<es:checkCustomization var="previdenzaVisible" objectId="IMPRESA-DATIULT" attribute="PREVIDENZA" feature="VIS" />
<es:checkCustomization var="soaVisible" objectId="IMPRESA-DATIULT" attribute="SOA" feature="VIS" />
<es:checkCustomization var="isoVisible" objectId="IMPRESA-DATIULT" attribute="ISO" feature="VIS" />
<es:checkCustomization var="whitelistVisible" objectId="IMPRESA-DATIULT" attribute="WHITELIST" feature="VIS" />
<es:checkCustomization var="altridatiVisible" objectId="IMPRESA-DATIULT" attribute="ALTRIDATI" feature="VIS" />



<wp:i18n key="OPT_CHOOSE_ORGANISMO" var="headerValueOrganismo" />

<c:if test="${!cciaVisible}">
	<input type="hidden" name="iscrittoCCIAA" value="0" aria-required="true"/>
</c:if>
<c:if test="${!whitelistVisible}">
	<input type="hidden" name="iscrittoWhitelistAntimafia" value="0" aria-required="true"/>
</c:if>
<c:if test="${!previdenzaVisible}">
	<input type="hidden" name="obblSoggettoNormativeDURC" value="false" aria-required="true"/>
</c:if>
<c:if test="${!altridatiVisible}">
	<input type="hidden" name="visSezAbilitPreventiva" value="false" aria-required="true"/>
	<input type="hidden" name="codiceBICCCDedicato" value="" aria-required="true"/>
	<input type="hidden" name="strNumDipendenti" value="" aria-required="true"/>
</c:if>

<s:set var="impresa" value="%{#session['dettAnagrImpresa']}" />
<s:if test="%{#session['dettAnagrImpresa'] == null}" >
	<s:set var="impresa" value="%{#session['dettRegistrImpresa']}" />
</s:if>

<s:set var="tipoImpresa" value="%{#impresa.datiPrincipaliImpresa.tipoImpresa}" />
<input type="hidden" name="tipoImpresa" value="<s:property value='%{#tipoImpresa}'/>" aria-required="true"/>


<c:if test="${cciaVisible || previdenzaVisible|| soaVisible || isoVisible || whitelistVisible || visRating || visIscrizioneElenchi189}">
	<fieldset>
		<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_ISCRIZIONI_CERTIFICAZIONI" /></legend>

		<c:if test="${cciaVisible}">
			<div class="fieldset-row first-row">
				<div class="label">
					<label><wp:i18n key="LABEL_ISCRIZIONE_CCIAA" /> : </label>
				</div>
				<div class="element">
					<div class="contents-group">
						<label for="iscrittoCCIAA"><wp:i18n key="LABEL_ISCRITTO" /> :
							<span class="required-field">*</span> 
						</label>
						<s:set var="iscrittoCCIAAvalue" value="%{iscrittoCCIAA}" />
						<s:select list="maps['sino']" name="iscrittoCCIAA" id="iscrittoCCIAA" value="%{#iscrittoCCIAAvalue}"
										headerKey="" headerValue=""  
										 aria-required="true">
						</s:select>
					</div>			
					<div class="contents-group cciaa">
						<label for="numIscrizioneCCIAA"><wp:i18n key="LABEL_NUM_ISCRIZIONE_CCIAA" /> :
							<span class="required-field">*</span>
						</label>
						<s:textfield name="numIscrizioneCCIAA" id="numIscrizioneCCIAA" value="%{numIscrizioneCCIAA}" 
												 size="16" maxlength="16" aria-required="true" />
					</div>
					<div class="contents-group cciaa">
						<label for="dataIscrizioneCCIAA"><wp:i18n key="LABEL_DATA_ISCRIZIONE" /> (<wp:i18n key="LABEL_FORMATO_DATA" />) : 
							<span class="required-field">*</span>
						</label>
						<c:set var="lblDtaIscrCCIAA"><wp:i18n key="LABEL_DATA_ISCRIZIONE_CCIAA" /></c:set>
						<s:textfield name="dataIscrizioneCCIAA" id="dataIscrizioneCCIAA" value="%{dataIscrizioneCCIAA}" 
												 size="10" maxlength="10" aria-required="true" aria-label="${lblDtaIscrCCIAA}" />
					</div>
					<div class="contents-group cciaa">
						<label for="numRegistroDitteCCIAA"><wp:i18n key="LABEL_NUM_REGISTRO_DITTE_CCIAA" /> : 
							<span class="required-field">*</span>
						</label>
						<s:textfield name="numRegistroDitteCCIAA" id="numRegistroDitteCCIAA" value="%{numRegistroDitteCCIAA}" 
												 size="16" maxlength="16" aria-required="true" />
					</div>
					<div class="contents-group cciaa">
						<label for="dataDomandaIscrizioneCCIAA"><wp:i18n key="LABEL_DATA_DOMANDA_ISCRIZIONE_CCIAA" /> (<wp:i18n key="LABEL_FORMATO_DATA" />) : 
							<span class="required-field">*</span>
						</label>
						<s:textfield name="dataDomandaIscrizioneCCIAA" id="dataDomandaIscrizioneCCIAA" value="%{dataDomandaIscrizioneCCIAA}" 
												 size="10" maxlength="10" aria-required="true" />
					</div>
					<div class="contents-group cciaa">
						<label for="provinciaIscrizioneCCIAA"><wp:i18n key="LABEL_SEDE" /> : 
							<span class="required-field">*</span>
						</label>
						<wp:i18n key="OPT_CHOOSE_PROVINCIA" var="headerValueProvincia" />
						<s:select list="maps['province']" name="provinciaIscrizioneCCIAA" id="provinciaIscrizioneCCIAA" value="%{provinciaIscrizioneCCIAA}" 
											headerKey="" headerValue="%{#attr.headerValueProvincia}" aria-required="true" >
						</s:select>
					</div>
					<div class="contents-group cciaa">
						<input type="hidden" name="obblOggettoSociale" value="${obblOggettoSociale}" />
						<input type="hidden" name="liberoProfessionista" value="${liberoProfessionista}" />
						<div class="label">
							<label for="oggettoSociale"><wp:i18n key="LABEL_OGGETTO_SOCIALE" /> : <c:if test="${obblOggettoSociale && !liberoProfessionista}">
									<span class="required-field">*</span></c:if>
							</label>
						</div>
						<div class="element">
							<s:textarea name="oggettoSociale" id="oggettoSociale" value="%{oggettoSociale}" 
													rows="5" cols="68" />
						</div>
					</div>
					<c:if test="${visDataNullaOstaAntimafiaCCIAA}">
						<div class="contents-group cciaa">
							<label for="dataNullaOstaAntimafiaCCIAA"><wp:i18n key="LABEL_DATA_NULLA_OSTA_ANTIMAFIA_CCIAA" /> (<wp:i18n key="LABEL_FORMATO_DATA" />) : 
								<c:if test="${obblDataNullaOstaAntimafiaCCIAA}"><span class="required-field">*</span></c:if> 
							</label>
							<input type="text" name="dataNullaOstaAntimafiaCCIAA" id="dataNullaOstaAntimafiaCCIAA" value="${dataNullaOstaAntimafiaCCIAA}" 
													 size="10" maxlength="10" <c:if test="${obblDataNullaOstaAntimafiaCCIAA}"> aria-required="true" </c:if> />
						</div>
					</c:if>
					<input type="hidden" name="obblDataNullaOstaAntimafiaCCIAA" value="${obblDataNullaOstaAntimafiaCCIAA}"/>
					<input type="hidden" name="obblSezCCIAA" value="${obblSezCCIAA}"/>
				</div>
			</div>
		</c:if>
		
		<c:if test="${previdenzaVisible}">	
		
			<div class="fieldset-row">
				<div class="label">
					<label><wp:i18n key="LABEL_DURC" /> : </label>
				</div>
				<div class="element">
					<div class="contents-group">
						<label for="soggettoNormativeDURC"><wp:i18n key="LABEL_SOGGETTO_NORMATIVE_DURC" /> : 
						<c:if test="${obblSoggettoNormativeDURC}"><span class="required-field">*</span></c:if></label>
						<s:select list="maps['sino']" name="soggettoNormativeDURC" id="soggettoNormativeDURC" value="%{soggettoNormativeDURC}"
											headerKey="" headerValue="" aria-required="true">
						</s:select>
					</div>
					<div class="contents-group">
						<label for="settoreProduttivoDURC"><wp:i18n key="LABEL_SETTORE_PRODUTTIVO_DURC" /> :</label>
						<wp:i18n key="OPT_CHOOSE_SETTORE" var="headerValueSettoreProduttivoDURC" />
						<s:select list="maps['settoriProduttivi']" name="settoreProduttivoDURC" id="settoreProduttivoDURC" value="%{settoreProduttivoDURC}"
											headerKey="" headerValue="%{#attr.headerValueSettoreProduttivoDURC}" aria-required="true">
						</s:select>
					</div>
				</div>
			</div>

			<div class="fieldset-row">
				<div class="label">
					<label><wp:i18n key="LABEL_ISCRIZIONE_INPS" /> : </label>
				</div>
				<div class="element">
					<div class="contents-group">
						<label for="numIscrizioneINPS"><wp:i18n key="LABEL_MATRICOLA" /> :</label>
						<s:textfield name="numIscrizioneINPS" id="numIscrizioneINPS" value="%{numIscrizioneINPS}" 
												 size="10" maxlength="16" />
					</div>
					<div class="contents-group">
						<label for="posizContributivaIndividualeINPS"><wp:i18n key="LABEL_POSIZ_CONTRIBUTIVA" /> :</label>
						<s:textfield name="posizContributivaIndividualeINPS" id="posizContributivaIndividualeINPS" value="%{posizContributivaIndividualeINPS}" 
												 size="10" maxlength="10" />
					</div>
					<div class="contents-group">
						<label for="dataIscrizioneINPS"><wp:i18n key="LABEL_DATA_ISCRIZIONE" /> (<wp:i18n key="LABEL_FORMATO_DATA" />) :</label>
						<c:set var="lblDtaIscrINPS"><wp:i18n key="LABEL_DATA_ISCRIZIONE_INPS" /></c:set>
						<s:textfield name="dataIscrizioneINPS" id="dataIscrizioneINPS" value="%{dataIscrizioneINPS}" 
												 size="10" maxlength="10" aria-label="${lblDtaIscrINPS}" />
					</div>
					<div class="contents-group">
						<label for="localitaIscrizioneINPS"><wp:i18n key="LABEL_SEDE" /> :</label>
						<s:textfield name="localitaIscrizioneINPS" id="localitaIscrizioneINPS" value="%{localitaIscrizioneINPS}" 
												 size="40" maxlength="40" />
					</div>
				</div>
			</div>

			<div class="fieldset-row">
				<div class="label">
					<label><wp:i18n key="LABEL_ISCRIZIONE_CASSA_EDILE" /> : </label>
				</div>
				<div class="element">
					<div class="contents-group">
						<label for="numIscrizioneCassaEdile"><wp:i18n key="LABEL_CODICE_IMPRESA" /> :</label>
						<s:textfield name="numIscrizioneCassaEdile" id="numIscrizioneCassaEdile" value="%{numIscrizioneCassaEdile}" 
												 size="10" maxlength="16" />
					</div>
					<div class="contents-group">
						<label for="codiceCassaEdile"><wp:i18n key="LABEL_CODICE_CASSA_EDILE" /> :</label>
						<s:textfield name="codiceCassaEdile" id="codiceCassaEdile" value="%{codiceCassaEdile}" 
												 size="10" maxlength="4" />
					</div>
					<div class="contents-group">
						<label for="dataIscrizioneCassaEdile"><wp:i18n key="LABEL_DATA_ISCRIZIONE_CASSA_EDILE" /> (<wp:i18n key="LABEL_FORMATO_DATA" />) :</label>
						<s:textfield name="dataIscrizioneCassaEdile" id="dataIscrizioneCassaEdile" value="%{dataIscrizioneCassaEdile}" 
												 size="10" maxlength="10" />
					</div>
					<div class="contents-group">
						<label for="localitaIscrizioneCassaEdile"><wp:i18n key="LABEL_LOCALITA_ISCRIZIONE" /> :</label>
						<c:set var="lblLocIscrCassaEdile"><wp:i18n key="LABEL_LOCALITA_ISCRIZIONE_CASSA_EDILE" /></c:set>
						<s:textfield name="localitaIscrizioneCassaEdile" id="localitaIscrizioneCassaEdile" value="%{localitaIscrizioneCassaEdile}" 
												 size="40" maxlength="40" aria-label="${lblLocIscrCassaEdile}"/>
					</div>
				</div>
			</div>

			<div class="fieldset-row">
				<div class="label">
					<label for="altriIstitutiPrevidenziali"><wp:i18n key="LABEL_ALTRI_ISTITUTI_PREVIDENZIALI" /> : </label>
				</div>
				<div class="element">
						<s:textarea name="altriIstitutiPrevidenziali" id="altriIstitutiPrevidenziali" value="%{altriIstitutiPrevidenziali}" 
												rows="5" cols="68" />
				</div>
			</div>
			<div class="fieldset-row">
				<div class="label">
					<label><wp:i18n key="LABEL_ISCRIZIONE_INAIL" /> : </label>
				</div>
				<div class="element">
					<div class="contents-group">
						<label for="numIscrizioneINAIL"><wp:i18n key="LABEL_CODICE_DITTA" /> :</label>
						<s:textfield name="numIscrizioneINAIL" id="numIscrizioneINAIL" value="%{numIscrizioneINAIL}" 
												 size="10" maxlength="16" />
					</div>
					<div class="contents-group">
						<label for="posizAssicurativaINAIL"><wp:i18n key="LABEL_POSIZ_ASSICURATIVA" /> :</label>
						<s:textfield name="posizAssicurativaINAIL" id="posizAssicurativaINAIL" value="%{posizAssicurativaINAIL}" 
												 size="10" maxlength="9" />
					</div>
					<div class="contents-group">
						<label for="dataIscrizioneINAIL"><wp:i18n key="LABEL_DATA_ISCRIZIONE" /> (<wp:i18n key="LABEL_FORMATO_DATA" />) :</label>
						<c:set var="lblDtaIscrINAIL"><wp:i18n key="LABEL_DATA_ISCRIZIONE_INAIL" /></c:set>
						<s:textfield name="dataIscrizioneINAIL" id="dataIscrizioneINAIL" value="%{dataIscrizioneINAIL}" 
												 size="10" maxlength="10" aria-label="${lblDtaIscrINAIL}" />
					</div>
					<div class="contents-group">
						<label for="localitaIscrizioneINAIL"><wp:i18n key="LABEL_LOCALITA_ISCRIZIONE" /> :</label>
						<c:set var="lblLocIscrINAIL"><wp:i18n key="LABEL_LOCALITA_ISCRIZIONE_INAIL" /></c:set>
						<s:textfield name="localitaIscrizioneINAIL" id="localitaIscrizioneINAIL" value="%{localitaIscrizioneINAIL}" 
												 size="40" maxlength="40" aria-label="${lblLocIscrINAIL}"/>
					</div>
				</div>
			</div>
		</c:if>
		<c:if test="${soaVisible}">
			<div class="fieldset-row">
				<div class="label">
					<label><wp:i18n key="LABEL_ATTESTAZIONE_SOA" /> : </label>
				</div>
				<div class="element">
					<div class="contents-group">
						<label for="numIscrizioneSOA"><wp:i18n key="LABEL_NUM_ATTESTAZIONE" /> :</label>
						<s:textfield name="numIscrizioneSOA" id="numIscrizioneSOA" value="%{numIscrizioneSOA}" 
												 size="10" maxlength="20" />
					</div>
					<div class="contents-group">
						<label for="dataIscrizioneSOA"><wp:i18n key="LABEL_DATA_RILASCIO" /> (<wp:i18n key="LABEL_FORMATO_DATA" />) :</label>
						<s:textfield name="dataIscrizioneSOA" id="dataIscrizioneSOA" value="%{dataIscrizioneSOA}" 
												 size="10" maxlength="10" />
					</div>
					<s:hidden name="dataUltimaRichiestaIscrizioneSOA" id="dataUltimaRichiestaIscrizioneSOA" value="%{dataUltimaRichiestaIscrizioneSOA}"/>
					<div class="contents-group">
						<label for="dataScadenzaTriennaleSOA"><wp:i18n key="LABEL_DATA_SCADENZA_TRIENNALE" /> (<wp:i18n key="LABEL_FORMATO_DATA" />) :</label>
						<s:textfield name="dataScadenzaTriennaleSOA" id="dataScadenzaTriennaleSOA" value="%{dataScadenzaTriennaleSOA}" 
												 size="10" maxlength="10" />
					</div>
					
					<div class="contents-group">
						<label for="dataVerificaTriennaleSOA"><wp:i18n key="LABEL_DATA_VERIFICA_TRIENNALE" /> (<wp:i18n key="LABEL_FORMATO_DATA" />) :</label>
						<s:textfield name="dataVerificaTriennaleSOA" id="dataVerificaTriennaleSOA" value="%{dataVerificaTriennaleSOA}" 
												 size="10" maxlength="10" />
					</div>
					
					<s:if test="%{#tipoImpresa == 2 || #tipoImpresa == 11}">
						<!-- visibile solo per i consorzi -->
						<div class="contents-group">
							<label for="dataScadenzaIntermediaSOA"><wp:i18n key="LABEL_DATA_SCADENZA_INTERMEDIA" /> (<wp:i18n key="LABEL_FORMATO_DATA" />) :</label>
							<s:textfield name="dataScadenzaIntermediaSOA" id="dataScadenzaIntermediaSOA" value="%{dataScadenzaIntermediaSOA}" 
													 size="10" maxlength="10" />
						</div>
					</s:if>
					
					<div class="contents-group">
						<label for="dataScadenzaQuinquennaleSOA"><wp:i18n key="LABEL_DATA_SCADENZA_QUINQUENNALE" /> (<wp:i18n key="LABEL_FORMATO_DATA" />) :</label>
						<s:textfield name="dataScadenzaQuinquennaleSOA" id="dataScadenzaQuinquennaleSOA" value="%{dataScadenzaQuinquennaleSOA}" 
												 size="10" maxlength="10" />
					</div>
					
					<div class="contents-group">
						<label for="organismoCertificatoreSOA"><wp:i18n key="LABEL_ORGANISMO_CERTIFICATORE" /> :</label>
						<c:set var="lblOrgCertSOA"><wp:i18n key="LABEL_ORGANISMO_CERTIFICATORE_SOA" /></c:set>
						<s:select list="maps['certificatoriSOA']" name="organismoCertificatoreSOA" id="organismoCertificatoreSOA" value="%{organismoCertificatoreSOA}"
											headerKey="" headerValue="%{#attr.headerValueOrganismo}" aria-label="${lblOrgCertSOA}" >
						</s:select>
					</div>
				</div>
			</div>
		</c:if>
		<c:if test="${isoVisible}">
			<div class="fieldset-row">
				<div class="label">
					<label><wp:i18n key="LABEL_CERTIFICAZIONE_ISO" /> : </label>
				</div>
				<div class="element">
					<div class="contents-group">
						<label for="numIscrizioneISO"><wp:i18n key="LABEL_NUM_ISCRIZIONE" /> :</label>
						<s:textfield name="numIscrizioneISO" id="numIscrizioneISO" value="%{numIscrizioneISO}" 
												 size="16" maxlength="16" />
					</div>
					<div class="contents-group">
						<label for="dataScadenzaISO"><wp:i18n key="LABEL_DATA_SCADENZA_CERTIFICAZIONE" /> (<wp:i18n key="LABEL_FORMATO_DATA" />):</label>
						<s:textfield name="dataScadenzaISO" id="dataScadenzaISO"  value="%{dataScadenzaISO}" 
												 size="10" maxlength="10" />
					</div>
					<div class="contents-group">
						<label for="organismoCertificatoreISO"><wp:i18n key="LABEL_ORGANISMO_CERTIFICATORE" /> :</label>
						<c:set var="lblOrgCertISO"><wp:i18n key="LABEL_ORGANISMO_CERTIFICATORE_ISO" /></c:set>
						<s:select list="maps['certificatoriISO']" name="organismoCertificatoreISO" id="organismoCertificatoreISO" value="%{organismoCertificatoreISO}"
											headerKey="" headerValue="%{#attr.headerValueOrganismo}" aria-label="${lblOrgCertISO}" >
						</s:select>
					</div>
				</div>	
			</div>
		</c:if>
		<c:if test="${isoVisible}">	
			<div class="fieldset-row">
				<div class="label">
					<label for="altreCertificazioniAttestazioni"><wp:i18n key="LABEL_ALTRE_CERTIFICAZIONI_ATTESTAZIONI" />: </label>
				</div>
				<div class="element">
						<s:textarea name="altreCertificazioniAttestazioni" id="altreCertificazioniAttestazioni" value="%{altreCertificazioniAttestazioni}" 
												rows="5" cols="68" />
				</div>
			</div>
		</c:if>
		<c:if test="${whitelistVisible}">
			<div class="fieldset-row">
				<div class="label">
					<label><wp:i18n key="LABEL_ISCRIZIONE_WL_ANTIMAFIA" /> : </label>
				</div>
				<div class="element">
					<div class="contents-group">
						<label for="iscrittoWhitelistAntimafia"><wp:i18n key="LABEL_ISCRITTO" /> :
							<span class="required-field">*</span>
						</label>
						<s:select list="maps['sino']" name="iscrittoWhitelistAntimafia" id="iscrittoWhitelistAntimafia" value="%{iscrittoWhitelistAntimafia}"
									headerKey="" headerValue="" aria-required="true">
						</s:select>
					</div>
					<div class="contents-group iscrizioneWhitelist">
						<label for="sedePrefetturaWhitelistAntimafia"><wp:i18n key="LABEL_SEDE_PREFETTURA" /> :<span class="required-field">*</span>
						</label>
						<s:textfield name="sedePrefetturaWhitelistAntimafia" id="sedePrefetturaWhitelistAntimafia" value="%{sedePrefetturaWhitelistAntimafia}" 
									size="35" maxlength="50" aria-required="true"/>
					</div>
					<br/>
					<wp:i18n key="LABEL_SEZIONI_ISCRIZIONE" var="titleSezioniIscrizione" />
					<div class="iscrizioneWhitelist">
						<label for="sezioniIscrizioneWhitelistAntimafia"><wp:i18n key="LABEL_SEZIONI_ISCRIZIONE" /> :<span class="required-field">*</span>
						</label>
						<table class="light-table">
							<%--
							<thead>
								<tr><th scope="col" colspan="2">Sezioni d'iscrizione</th></tr>
							</thead>
							 --%>
							<tbody>
								<s:iterator value="maps['sezioniWhitelist']">
								
									<s:set var="sezioneIscrizioneValue" value="%{false}"/>
									<s:iterator value="sezioniIscrizioneWhitelistAntimafia" var="sezione" status="status">
										<s:if test="%{key == #sezione}">
											<s:set var="sezioneIscrizioneValue" value="%{true}"/>
										</s:if>
									</s:iterator>
								
									<tr>
										<td style="text-align: left;">
											<s:property value="%{key}"/> - <s:property value="%{value}"/>
										</td>
										<td style="text-align: left;">
											<s:checkbox name="sezioniIscrizioneWhitelistAntimafia" fieldValue="%{key}" 
													id="sezioniIscrizioneWhitelistAntimafia%{key}" value="%{sezioneIscrizioneValue}" 
													cssClass="sezioniIscrizioneWhitelistAntimafia" title="%{value}" 
													aria-required="true" > 
											</s:checkbox>
										</td>
									</tr>
								</s:iterator>
							</tbody>
						</table>
					</div>
					<div class="contents-group iscrizioneWhitelist">
						<label for="dataIscrizioneWhitelistAntimafia"><wp:i18n key="LABEL_DATA_ISCRIZIONE" /> (<wp:i18n key="LABEL_FORMATO_DATA" />) :<span class="required-field">*</span>
						</label>
						<s:textfield name="dataIscrizioneWhitelistAntimafia" id="dataIscrizioneWhitelistAntimafia" value="%{dataIscrizioneWhitelistAntimafia}" 
									 size="10" maxlength="10" aria-required="true"/>
					</div>
					<div class="contents-group iscrizioneWhitelist">
						<label for="dataScadenzaIscrizioneWhitelistAntimafia"><wp:i18n key="LABEL_DATA_SCADENZA" /> (<wp:i18n key="LABEL_FORMATO_DATA" />) :<span class="required-field">*</span>
						</label>
						<s:textfield name="dataScadenzaIscrizioneWhitelistAntimafia" id="dataScadenzaIscrizioneWhitelistAntimafia" value="%{dataScadenzaIscrizioneWhitelistAntimafia}" 
									 size="10" maxlength="10" aria-required="true" />
					</div>
					<div class="contents-group iscrizioneWhitelist">
						<label for="aggiornamentoWhitelistAntimafia"><wp:i18n key="LABEL_AGGIORNAMENTO_IN_CORSO" /> :
						</label>
						<s:select list="maps['sino']" name="aggiornamentoWhitelistAntimafia" id="aggiornamentoWhitelistAntimafia" value="%{aggiornamentoWhitelistAntimafia}"
									headerKey="" headerValue=""  >
						</s:select>
					</div>
				</div>
			</div>
		</c:if>

		<c:if test="${visIscrizioneElenchi189}">
			<div class="fieldset-row">
				<div class="label">
					<label><wp:i18n key="LABEL_ISCRIZIONE_ELENCHI_RICOSTRUZIONE" /> : </label>
				</div>
				<div class="element">
					<div class="contents-group">
						<label for="iscrittoAnagrafeAntimafiaEsecutori"><wp:i18n key="LABEL_ISCRITTO_ANAGRAFE_ANTIMAFIA" />? :
							<c:if test="${obblIscrizioneElenchi189}"><span class="required-field">*</span></c:if>
						</label>
						<s:select list="maps['sino']" name="iscrittoAnagrafeAntimafiaEsecutori" id="iscrittoAnagrafeAntimafiaEsecutori" value="%{iscrittoAnagrafeAntimafiaEsecutori}"
									headerKey="" headerValue="" aria-required="true">
						</s:select>
					</div>
					<div class="contents-group iscrittoAnagrafeAntimafiaEsecutori">
						<label for="dataScadenzaIscrizioneAnagrafeAntimafiaEsecutori"><wp:i18n key="LABEL_DATA_SCADENZA_ISCRIZIONE_ANAGRAFE" /> (<wp:i18n key="LABEL_FORMATO_DATA" />) :
							<span class="required-field">*</span>
						</label>
						<s:textfield name="dataScadenzaIscrizioneAnagrafeAntimafiaEsecutori" id="dataScadenzaIscrizioneAnagrafeAntimafiaEsecutori" value="%{dataScadenzaIscrizioneAnagrafeAntimafiaEsecutori}" 
									 size="10" maxlength="10" aria-required="true" />
					</div>
					<div class="contents-group iscrittoAnagrafeAntimafiaEsecutori">
						<label for="rinnovoIscrizioneInCorsoAnagrafeAntimafiaEsecutori"><wp:i18n key="LABEL_RINNOVO_ISCRIZIONE_ANAGRAFE" />? :
							<span class="required-field">*</span>
						</label>
						<s:select list="maps['sino']" name="rinnovoIscrizioneInCorsoAnagrafeAntimafiaEsecutori" id="rinnovoIscrizioneInCorsoAnagrafeAntimafiaEsecutori" value="%{rinnovoIscrizioneInCorsoAnagrafeAntimafiaEsecutori}"
									headerKey="" headerValue="" aria-required="true">
						</s:select>
					</div>
					<div class="contents-group">
						<label for="iscrittoElencoSpecialeProfessionisti"><wp:i18n key="LABEL_ISCRITTO_ELENCO_SPECIALE_PROF" />? :
							<c:if test="${obblIscrizioneElenchi189}"><span class="required-field">*</span></c:if>
						</label>
						<s:select list="maps['sino']" name="iscrittoElencoSpecialeProfessionisti" id="iscrittoElencoSpecialeProfessionisti" value="%{iscrittoElencoSpecialeProfessionisti}"
									headerKey="" headerValue="" aria-required="true">
						</s:select>
					</div>
				</div>
			</div>
		</c:if>
		
		<c:if test="${visRating}">
			<div class="fieldset-row">
				<div class="label">
					<label><wp:i18n key="LABEL_RATING_LEGALITA" /> : </label>
				</div>
				<div class="element">
					<div class="contents-group">
						<label for="possiedeRatingLegalita"><wp:i18n key="LABEL_POSSIEDE_RATING_LEGALITA" />? :
							<c:if test="${obblRating}"><span class="required-field">*</span></c:if>
						</label>
						<s:select list="maps['sino']" name="possiedeRatingLegalita" id="possiedeRatingLegalita" value="%{possiedeRatingLegalita}"
									headerKey="" headerValue="" aria-required="true">
						</s:select>
					</div>
					<div class="contents-group possiedeRatingLegalita">
						<label for="ratingLegalita"><wp:i18n key="LABEL_RATING" /> :
							<span class="required-field">*</span>
						</label>
						<s:select list="maps['ratingLegale']" name="ratingLegalita" id="ratingLegalita" value="%{ratingLegalita}"
									headerKey="" headerValue="" aria-required="true">
						</s:select>
					</div>
					<div class="contents-group possiedeRatingLegalita">
						<label for="dataScadenzaPossessoRatingLegalita"><wp:i18n key="LABEL_DATA_SCADENZA_POSSESSO_RATING" /> (<wp:i18n key="LABEL_FORMATO_DATA" />) :
							<span class="required-field">*</span>
						</label>
						<s:textfield name="dataScadenzaPossessoRatingLegalita" id="dataScadenzaPossessoRatingLegalita" value="%{dataScadenzaPossessoRatingLegalita}" 
									 size="10" maxlength="10" aria-required="true" />
					</div>
					<div class="contents-group possiedeRatingLegalita">
						<label for="aggiornamentoRatingLegalita"><wp:i18n key="LABEL_AGGIORNAMENTO_RATING" />? :
							<span class="required-field">*</span>
						</label>
						<s:select list="maps['sino']" name="aggiornamentoRatingLegalita" id="aggiornamentoRatingLegalita" value="%{aggiornamentoRatingLegalita}"
									headerKey="" headerValue="" aria-required="true">
						</s:select>
					</div>
				</div>
			</div>
		</c:if>
	</fieldset>
</c:if>
<c:if test="${altridatiVisible}">
	<fieldset>
		<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_ALTRI_DATI" /></legend>

		<div class="fieldset-row first-row">
			<div class="label">
				<label><wp:i18n key="LABEL_CC_DEDICATO" /> : </label>
			</div>
			<div class="element">
				<div class="contents-group">
					<label for="codiceIBANCCDedicato"><wp:i18n key="LABEL_IBAN" /> :</label>
					<s:textfield name="codiceIBANCCDedicato" id="codiceIBANCCDedicato" value="%{codiceIBANCCDedicato}" 
											 size="50" maxlength="50" />
				</div>
				<div class="contents-group">
					<label for="codiceBICCCDedicato"><wp:i18n key="LABEL_BIC" /> :</label>
					<s:textfield name="codiceBICCCDedicato" id="codiceBICCCDedicato" value="%{codiceBICCCDedicato}" 
											 size="11" maxlength="11" />
				</div>			
				<div class="contents-group">
					<label for="soggettiAbilitatiCCDedicato"><wp:i18n key="LABEL_SOGGETTI_ABILITATI_CC_DEDICATO" />:</label><br/>
					<s:textarea name="soggettiAbilitatiCCDedicato" id="soggettiAbilitatiCCDedicato" value="%{soggettiAbilitatiCCDedicato}" 
											rows="5" cols="68" />
				</div>
			</div>
		</div>
			
		<div class="fieldset-row">
			<div class="label">
				<label><wp:i18n key="LABEL_SOCIO_UNICO" /> : </label>
			</div>
			<div class="element">
				<div class="contents-group">
					<label for="socioUnico"><wp:i18n key="LABEL_SOCIO_UNICO" />? :</label>
					<s:select list="maps['sino']" name="socioUnico" id="socioUnico" value="%{socioUnico}"
								headerKey="" headerValue="" >
					</s:select>	
				</div>
			</div>
		</div>
		
		<div class="fieldset-row">
			<div class="label">
				<label><wp:i18n key="LABEL_REGIME_FISCALE" /> : </label>
			</div>
			<div class="element">
				<div class="contents-group">
					<label for="regimeFiscale"><wp:i18n key="LABEL_REGIME_FISCALE" />? :</label>
					<s:select list="maps['tipiRegimeFiscale']" name="regimeFiscale" id="regimeFiscale" value="%{regimeFiscale}"
								headerKey="" headerValue="" >
					</s:select>	
				</div>
			</div>
		</div>

		<div class="fieldset-row">
			<div class="label">
				<label for="settoreAttivitaEconomica"><wp:i18n key="LABEL_SETTORE_ATTIVITA_ECONOMICA" /> : 
					<c:if test="${obblSettoreAttivitaEconomica}"><span class="required-field">*</span></c:if>
				</label>
			</div>
			<div class="element">
				<s:select list="maps['settoreAttivitaEconomica']" name="settoreAttivitaEconomica" id="settoreAttivitaEconomica" 
							headerKey="" headerValue="" value="%{settoreAttivitaEconomica}" 
							cssStyle="width: 100%;" aria-required="true" >
				</s:select>
			</div>
		</div>	

		<c:if test="${visSezAbilitPreventiva}">
			<div class="fieldset-row">
				<div class="label">
					<label><wp:i18n key="LABEL_ABILIT_PREVENTIVA" /> : </label>
				</div>
				<div class="element">
					<div class="contents-group">
						<label for="dataScadenzaAbilitPreventiva"><wp:i18n key="LABEL_DATA_SCADENZA" /> (<wp:i18n key="LABEL_FORMATO_DATA" />) : 
						<c:if test="${obblDataScadenzaAbilitPreventiva}"><span class="required-field">*</span></c:if></label>
						<s:textfield name="dataScadenzaAbilitPreventiva" id="dataScadenzaAbilitPreventiva" value="%{dataScadenzaAbilitPreventiva}" 
												 size="10" maxlength="10" aria-required="true" />
					</div>
					<div class="contents-group">
						<label for="rinnovoAbilitPreventiva"><wp:i18n key="LABEL_IN_FASE_RINNOVO" /> :</label>
						<s:select list="maps['sino']" name="rinnovoAbilitPreventiva" id="rinnovoAbilitPreventiva" value="%{rinnovoAbilitPreventiva}"
											headerKey="" headerValue="" >
						</s:select>
					</div>
					<div class="contents-group">
						<label for="dataRichRinnovoAbilitPreventiva"><wp:i18n key="LABEL_DATA_RICHIESTA_RINNOVO" /> (<wp:i18n key="LABEL_FORMATO_DATA" />) :</label>
						<s:textfield name="dataRichRinnovoAbilitPreventiva" id="dataRichRinnovoAbilitPreventiva" value="%{dataRichRinnovoAbilitPreventiva}" 
												 size="10" maxlength="10" />
					</div>
				</div>
			</div>
		</c:if>

		<c:if test="${visZoneAttivita}">
			<div class="fieldset-row">
				<div class="label">
					<label><wp:i18n key="LABEL_ZONE_ATTIVITA" /> : 
					<c:if test="${obblZoneAttivita}"><span class="required-field">*</span></c:if></label>
				</div>
				<div class="element">
					<c:set var="obblZoneAtt">${obblZoneAttivita}</c:set>
				
					<table class="light-table bordered-table">
						<tr>
							<td>Piemonte </td><td>
								<s:select list="maps['sino']" name="zoneAttivita" id="zoneAttivita1" 
									headerKey="" headerValue="" value="%{zoneAttivita[0]}" 
									aria-required="${obblZoneAtt}" aria-label="Piemonte"></s:select>
							</td>
							<td>Valle d'Aosta </td><td><s:select list="maps['sino']" name="zoneAttivita" id="zoneAttivita2" 
										headerKey="" headerValue="" value="%{zoneAttivita[1]}" 
										aria-required="${obblZoneAtt}" aria-label="Valle d'Aosta"></s:select>
							</td>
							<td>Liguria </td><td><s:select list="maps['sino']" name="zoneAttivita" id="zoneAttivita3" 
										headerKey="" headerValue="" value="%{zoneAttivita[2]}" 
										aria-required="${obblZoneAtt}" aria-label="Liguria"></s:select>
							</td>
						</tr>
						<tr>
							<td>Lombardia </td><td><s:select list="maps['sino']" name="zoneAttivita" id="zoneAttivita4" 
									headerKey="" headerValue="" value="%{zoneAttivita[3]}" 
									aria-required="${obblZoneAtt}" aria-label="Lombardia"></s:select>
							</td>
							<td>Friuli Venezia Giulia </td><td><s:select list="maps['sino']" name="zoneAttivita" id="zoneAttivita5" 
									headerKey="" headerValue="" value="%{zoneAttivita[4]}" 
									aria-required="${obblZoneAtt}" aria-label="Friuli Venezia Giulia"></s:select>
							</td>
							<td>Trentino Alto Adige </td><td><s:select list="maps['sino']" name="zoneAttivita" id="zoneAttivita6" 
									headerKey="" headerValue="" value="%{zoneAttivita[5]}" 
									aria-required="${obblZoneAtt}" aria-label="Trentino Alto Adige"></s:select>
							</td>
						</tr>
						<tr>
							<td>Veneto </td><td><s:select list="maps['sino']" name="zoneAttivita" id="zoneAttivita7" 
									headerKey="" headerValue="" value="%{zoneAttivita[6]}" 
									aria-required="${obblZoneAtt}" aria-label="Veneto"></s:select>
							</td>
							<td>Emilia Romagna </td><td><s:select list="maps['sino']" name="zoneAttivita" id="zoneAttivita8" 
									headerKey="" headerValue="" value="%{zoneAttivita[7]}" 
									aria-required="${obblZoneAtt}" aria-label="Emilia Romagna"></s:select>
							</td>
							<td>Toscana</td><td> <s:select list="maps['sino']" name="zoneAttivita" id="zoneAttivita9" 
									headerKey="" headerValue="" value="%{zoneAttivita[8]}" 
									aria-required="${obblZoneAtt}" aria-label="Toscana"></s:select>
							</td>
						</tr>
						<tr>
							<td>Umbria </td><td><s:select list="maps['sino']" name="zoneAttivita" id="zoneAttivita10" 
									headerKey="" headerValue="" value="%{zoneAttivita[9]}" 
									aria-required="${obblZoneAtt}" aria-label="Umbria"></s:select>
							</td>
							<td>Marche </td><td><s:select list="maps['sino']" name="zoneAttivita" id="zoneAttivita11" 
									headerKey="" headerValue="" value="%{zoneAttivita[10]}" 
									aria-required="${obblZoneAtt}" aria-label="Marche"></s:select>
							</td>
							<td>Abruzzo </td><td><s:select list="maps['sino']" name="zoneAttivita" id="zoneAttivita12" 
									headerKey="" headerValue="" value="%{zoneAttivita[11]}" 
									aria-required="${obblZoneAtt}" aria-label="Abruzzo"></s:select>
							</td>
						</tr>
						<tr>
							<td>Molise </td><td><s:select list="maps['sino']" name="zoneAttivita" id="zoneAttivita13" 
									headerKey="" headerValue="" value="%{zoneAttivita[12]}" 
									aria-required="${obblZoneAtt}" aria-label="Molise"></s:select>
							</td>
							<td>Lazio </td><td><s:select list="maps['sino']" name="zoneAttivita" id="zoneAttivita14" 
									headerKey="" headerValue="" value="%{zoneAttivita[13]}" 
									aria-required="${obblZoneAtt}" aria-label="Lazio"></s:select>
							</td>
							<td>Campania </td><td><s:select list="maps['sino']" name="zoneAttivita" id="zoneAttivita15" 
									headerKey="" headerValue="" value="%{zoneAttivita[14]}" 
									aria-required="${obblZoneAtt}" aria-label="Campania"></s:select>
							</td>
						</tr>
						<tr>
							<td>Basilicata </td><td><s:select list="maps['sino']" name="zoneAttivita" id="zoneAttivita16"
									headerKey="" headerValue="" value="%{zoneAttivita[15]}" 
									aria-required="${obblZoneAtt}" aria-label="Basilicata"></s:select>
							<td>Puglia </td><td><s:select list="maps['sino']" name="zoneAttivita" id="zoneAttivita17" 
									headerKey="" headerValue="" value="%{zoneAttivita[16]}" 
									aria-required="${obblZoneAtt}" aria-label="Puglia"></s:select>
							<td>Calabria </td><td><s:select list="maps['sino']" name="zoneAttivita" id="zoneAttivita18" 
									headerKey="" headerValue="" value="%{zoneAttivita[17]}" 
									aria-required="${obblZoneAtt}" aria-label="Calabria"></s:select>
						</tr>
						<tr>
							<td>Sardegna </td><td><s:select list="maps['sino']" name="zoneAttivita" id="zoneAttivita19" 
									headerKey="" headerValue="" value="%{zoneAttivita[18]}" 
									aria-required="${obblZoneAtt}" aria-label="Sardegna"></s:select>
							<td>Sicilia </td><td><s:select list="maps['sino']" name="zoneAttivita" id="zoneAttivita20" 
									headerKey="" headerValue="" value="%{zoneAttivita[19]}" 
									aria-required="${obblZoneAtt}" aria-label="Sicilia"></s:select>
							<td>
								<span id="allRegioni" style="display:none;">
									<c:choose>
										<c:when test="${skin == 'highcontrast' || skin == 'text'}">
											<a href="javascript:checkAll();" title="<wp:i18n key="LABEL_CHECK_REGIONI" />"><wp:i18n key="LABEL_CHECK_REGIONI" /></a> 
											<a href="javascript:uncheckAll();" title="<wp:i18n key="LABEL_UNCHECK_REGIONI" />"><wp:i18n key="LABEL_UNCHECK_REGIONI" /></a>
										</c:when>
										<c:otherwise>
											<a href="javascript:checkAll();" title="<wp:i18n key="LABEL_CHECK_REGIONI" />" class="bkg select"></a> 
											<a href="javascript:uncheckAll();" title="<wp:i18n key="LABEL_UNCHECK_REGIONI" />" class="bkg unselect"></a>
										</c:otherwise>
									</c:choose>
								</span> 
							</td>
						</tr>
					</table>
				</div>
			</div>

			<script type="text/javascript">
			<!--//--><![CDATA[//><!--
				document.getElementById("allRegioni").style.display = "";

				function checkAll() {
					for (var i = 1; i <= 20; i++)
						document.getElementById("zoneAttivita" + i).value = "1";
				}

				function uncheckAll() {
					for (var i = 1; i <= 20; i++)
						document.getElementById("zoneAttivita" + i).value = "0";
				}
				//--><!]]>
			</script>
		</c:if>
			
		<div class="fieldset-row">
			<div class="label">
				<label><wp:i18n key="LABEL_PERSONALE_DIPENDENTE" /> : </label>
			</div>
			<div class="element">
				<div class="contents-group">
					<label for="assunzioniObbligate"><wp:i18n key="LABEL_ASSUNZIONI_OBBLIGATE" /> : 
						<c:if test="${obblAssunzioniObbligate}"><span class="required-field">*</span></c:if>
					</label>
					<s:select list="maps['sino']" name="assunzioniObbligate" id="assunzioniObbligate" 
										headerKey="" headerValue="" value="%{assunzioniObbligate}" aria-required="true">
					</s:select>
					<div class="note">
					<wp:i18n key="LABEL_ASSUNZIONI_OBBLIGATE_NOTA" />
					</div>
				</div>
				<br/>
				<label><wp:i18n key="LABEL_DIPENDENTI_ULTIMO_TRIENNIO" /> : 
					<c:if test="${obblDipendentiTriennio}"><span class="required-field">*</span></c:if>
				</label>
				<wp:i18n key="LABEL_NUMERO_MEDIO_DIPENDENTI" var="titleNumeroMedioDipendenti"/>
				<table class="light-table bordered-table">
					<thead>
						<tr>
							<th scope="col"><wp:i18n key="LABEL_ANNO" /></th>
							<th scope="col"><wp:i18n key="LABEL_NUMERO_MEDIO_DIPENDENTI" /></th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td><s:property value="%{anni[0]}"/><s:hidden name="anni" id="anni0" value="%{anni[0]}" /></td>
							<td style="text-align: left;">
								<s:textfield name="strNumDipendenti" id="numDipendenti0" value="%{strNumDipendenti[0]}" 
														 size="10" maxlength="5" title="%{#attr.titleNumeroMedioDipendenti} %{anni[0]}"
														 aria-required="true" />
								<span id='warningDipendenti0' class="noscreen important"> (<wp:i18n key="LABEL_WARNING_ZERO_DIPENDENTI" />)</span>
							</td>
						</tr>
						<tr>
							<td><s:property value="%{anni[1]}"/><s:hidden name="anni" id="anni1" value="%{anni[1]}" /></td>
							<td style="text-align: left;">
								<s:textfield name="strNumDipendenti" id="numDipendenti1" value="%{strNumDipendenti[1]}" 
														 size="10" maxlength="5" title="%{#attr.titleNumeroMedioDipendenti} %{anni[1]}"/>
								<span id='warningDipendenti1' class="noscreen important"> (<wp:i18n key="LABEL_WARNING_ZERO_DIPENDENTI" />)</span>
							</td>
						</tr>
						<tr>
							<td><s:property value="%{anni[2]}"/><s:hidden name="anni" id="anni2" value="%{anni[2]}" /></td>
							<td style="text-align: left;">
								<s:textfield name="strNumDipendenti" id="numDipendenti2" value="%{strNumDipendenti[2]}" 
														 size="10" maxlength="5" title="%{#attr.titleNumeroMedioDipendenti} %{anni[2]}"/>
								<span id='warningDipendenti2' class="noscreen important"> (<wp:i18n key="LABEL_WARNING_ZERO_DIPENDENTI" />)</span>
							</td>
						</tr>
					</tbody>
				</table>
				<div class="contents-group">
					<label for="classeDimensioneDipendenti"><wp:i18n key="LABEL_CLASSE_DIMENSIONE_DIPENDENTI" /> : 
						<c:if test="${obblClasseDimensione}"><span class="required-field">*</span></c:if>
					</label>
					<s:select list="maps['classiDimensione']" name="classeDimensioneDipendenti" id="classeDimensioneDipendenti" 
								headerKey="" headerValue="" value="%{classeDimensioneDipendenti}" aria-required="true" >
					</s:select>
				</div>
			</div>
		</div>	
		
		<div class="fieldset-row last-row">
			<div class="label">
				<label for="ulterioriDichiarazioni"><wp:i18n key="LABEL_ULTERIORI_DICHIARAZIONI" /> : 	</label>
			</div>
			<div class="element">
				<div class="contents-group">
					<s:textarea name="ulterioriDichiarazioni" id="ulterioriDichiarazioni" value="%{ulterioriDichiarazioni}" rows="5" cols="68" />
				</div>
			</div>
		</div>
	</fieldset>
</c:if>

<input type="hidden" name="obblSoggettoNormativeDURC" value="${obblSoggettoNormativeDURC}"/>
<input type="hidden" name="obblSettoreAttivitaEconomica" value="${obblSettoreAttivitaEconomica}"/>
<input type="hidden" name="obblDataScadenzaAbilitPreventiva" value="${obblDataScadenzaAbilitPreventiva}"/>
<input type="hidden" name="obblZoneAttivita" value="${obblZoneAttivita}"/>
<input type="hidden" name="obblAssunzioniObbligate" value="${obblAssunzioniObbligate}" />
<input type="hidden" name="obblDipendentiTriennio" value="${obblDipendentiTriennio}" />
<input type="hidden" name="obblClasseDimensione" value="${obblClasseDimensione}" />
<input type="hidden" name="visSezAbilitPreventiva" value="${visSezAbilitPreventiva}"/>
<input type="hidden" name="obblIscrittoAnagrafeAntimafiaEsecutori" value="${obblIscrizioneElenchi189}" />
<input type="hidden" name="obblPossiedeRatingLegalita" value="${obblRating}" />

<script type="text/javascript">
<!--//--><![CDATA[//><!--
	// funzione che attiva la visualizzazione di un warning quando si inseriscono 0 dipendenti e lo nasconde quando il valore non e' piu' 0
	function visualizzaWarningZeroDipendenti(id) {
		//determino l'indice dell'elemento (0, 1 o 2) per referenziare il warning corrispondente
		var pos = id.substring(id.length - 1);
		if (/^0+$/.test($("#"+id).val())) {
			$("#warningDipendenti" + pos).removeClass("noscreen");
		} else {
			$("#warningDipendenti" + pos).addClass("noscreen");
		}
	}
	// inizializza la pagina con l'eventuale messaggio a destra di ogni numero dipendenti per anno
	visualizzaWarningZeroDipendenti("numDipendenti0");
	visualizzaWarningZeroDipendenti("numDipendenti1");
	visualizzaWarningZeroDipendenti("numDipendenti2");
	
	// al cambio del valore attiva o nasconde la visualizzazione del messaggio
	$("input[id^='numDipendenti']").change(function() {
		visualizzaWarningZeroDipendenti($(this).attr('id'));
	});

	// visualizza/nasconde una sezione (display e WCAG 2.1)
	function showElement(element, value) {
		if(!value) {
			element.hide();
			element.attr('aria-hidden', 'true');	// WCAG 2.1 validation
		} else {
			element.show();
			element.attr('aria-hidden', 'false');	// WCAG 2.1 validation
		}
	}
	
	// se il valore di "Iscritto a Camera di Commercio?" = "NO", si annullano 
    // i valori dei campi CCIAA
	function visualizzaRequiredFieldCCIAA() {
		var iscritto = $("#iscrittoCCIAA :selected").val();
		showElement($(".cciaa"), (iscritto == "1"));
	}
    
	$("select[id^='iscrittoCCIAA']").change(function() {
		//var registrazione = '${registrazioneDitta}';
		//var tipoImpresa = parseInt('${tipoImpresa}');
		var iscritto = $("#iscrittoCCIAA :selected").val();

		visualizzaRequiredFieldCCIAA();
		
		// Nella sola registrazione, nel caso di libero professionista o 
		// società di professionisti (TIPIMP.IMPR <= 5) impostare ISCRCCIAA a "No" 
		// e resettare tutti i campi della sezione "Iscrizione alla Camera di Commercio". 
		//if(registrazione && (tipoImpresa <= 5) && (iscritto == "0")) {
		if(iscritto == "0") {
			document.getElementById("numIscrizioneCCIAA").value = null;
			document.getElementById("dataIscrizioneCCIAA").value = null;
			document.getElementById("numRegistroDitteCCIAA").value = null;
			document.getElementById("dataDomandaIscrizioneCCIAA").value = null;
			document.getElementById("provinciaIscrizioneCCIAA").value = null;
			document.getElementById("dataNullaOstaAntimafiaCCIAA").value = null;
			document.getElementById("oggettoSociale").value = null;
			
		}		
	});

	// se il valore di "Iscritto ?" = "NO", si annullano 
    // i valori dei campi iscrizione whitelist antimafia 
	function visualizzaRequiredFieldWhitelistAntimafia() {
		var iscritto = $("#iscrittoWhitelistAntimafia :selected").val();
		showElement($(".iscrizioneWhitelist"), (iscritto == "1"));	
	}
    
	$("select[id^='iscrittoWhitelistAntimafia']").change(function() {
		var iscritto = $("#iscrittoWhitelistAntimafia :selected").val();
		
		visualizzaRequiredFieldWhitelistAntimafia();
		
		// resetta i campi
		if(iscritto != "1") {
			$("#sedePrefetturaWhitelistAntimafia").val(null);
			$("#dataIscrizioneWhitelistAntimafia").val(null);
			$("#dataScadenzaIscrizioneWhitelistAntimafia").val(null);
			$(".sezioniIscrizioneWhitelistAntimafia").prop("checked", false);
			$("#aggiornamentoWhitelistAntimafia").val(null);
		}
	});

	// se il valore di "Iscritto ?" = "NO", si annullano 
    // i valori dei campi iscrizione anagrafe antimafia 
	function visualizzaRequiredFieldIscrittoAnagrafeAntimafia() {
		var iscritto = $("#iscrittoAnagrafeAntimafiaEsecutori :selected").val();
		showElement($(".iscrittoAnagrafeAntimafiaEsecutori"), (iscritto == "1"));
	}
	
	$("select[id^='iscrittoAnagrafeAntimafiaEsecutori']").change(function() {
		var iscritto = $("#iscrittoAnagrafeAntimafiaEsecutori :selected").val();
		
		visualizzaRequiredFieldIscrittoAnagrafeAntimafia();
		
		// resetta i campi
		if(iscritto != "1") {
			$("#dataScadenzaIscrizioneAnagrafeAntimafiaEsecutori").val(null);
			$("#rinnovoIscrizioneInCorsoAnagrafeAntimafiaEsecutori").val(null);
		}
	});
	
	// se il valore di "Iscritto ?" = "NO", si annullano 
    // i valori dei campi rating legale 
	function visualizzaRequiredFieldRatingLegale() {
		var iscritto = $("#possiedeRatingLegalita :selected").val();
		showElement($(".possiedeRatingLegalita"), (iscritto == "1"));
	}
	
	$("select[id^='possiedeRatingLegalita']").change(function() {
		var iscritto = $("#possiedeRatingLegalita :selected").val();
		
		visualizzaRequiredFieldRatingLegale();
		
		// resetta i campi
		if(iscritto != "1") {
			$("#ratingLegalita").val(null);
			$("#dataScadenzaPossessoRatingLegalita").val(null);
			$("#aggiornamentoRatingLegalita").val(null);
		}
	});
	
// apertura della pagina...
$(document).ready(function() {

	visualizzaRequiredFieldCCIAA();
	visualizzaRequiredFieldWhitelistAntimafia();
	visualizzaRequiredFieldIscrittoAnagrafeAntimafia();
	visualizzaRequiredFieldRatingLegale();
	
});	
//--><!]]>
</script>

