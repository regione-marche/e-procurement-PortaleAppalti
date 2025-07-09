<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>

<s:set var="sessionId">${param.sessionIdObj}</s:set>
<s:set var="helper" value="%{#session[#sessionId]}"/>
<c:set var="chelper" value="${sessionScope[param.sessionIdObj]}"/>

<es:checkCustomization var="obblIscrizione" objectId="IMPRESA-DATIANAGR-SEZ" attribute="ISCRIZIONEALBOPROF" feature="MAN" />
<c:set var="obblIscrizione" value="${obblIscrizione  && (chelper.datiPrincipaliImpresa.naturaGiuridica == 7)}" />

<wp:i18n key="OPT_CHOOSE_PROVINCIA" var="headerValueProvincia" />


<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/warning_javascript.jsp" />

<s:if test="%{delete}">
	<p class="question">
		<wp:i18n key="LABEL_QUESTION_CONFIRM_DEL_SOGGETTO_1" />
		<s:set var="sog" value=""/>
		<s:if test="%{tipoSoggettoDelete == 1}">
			<s:set var="sog" value="%{#helper.legaliRappresentantiImpresa.get(idDelete)}"/>
		</s:if>
		<s:elseif test="%{tipoSoggettoDelete == 2}">
			<s:set var="sog" value="%{#helper.direttoriTecniciImpresa.get(idDelete)}"/>
		</s:elseif>
		<s:elseif test="%{tipoSoggettoDelete == 3}">
			<s:set var="sog" value="%{#helper.altreCaricheImpresa.get(idDelete)}"/>
		</s:elseif>
		<s:else>
			<s:set var="sog" value="%{#helper.collaboratoriImpresa.get(idDelete)}"/>
		</s:else>
		<s:property value="%{#sog.cognome}"/> <s:property value="%{#sog.nome}"/>
			<wp:i18n key="LABEL_QUESTION_CONFIRM_DEL_SOGGETTO_2" />
		<s:iterator value="maps['tipiSoggetto']">
			<s:if test="%{key == #sog.soggettoQualifica}"><s:property value="%{value}"/></s:if>
		</s:iterator>
		<s:iterator value="maps['tipiAltraCarica']">
			<s:if test="%{key == #sog.soggettoQualifica}"><s:property value="%{value}"/></s:if>
		</s:iterator>
		<s:iterator value="maps['tipiCollaborazione']">
			<s:if test="%{key == #sog.soggettoQualifica}"><s:property value="%{value}"/></s:if>
		</s:iterator>
		?
	</p>
	<div class="azioni">
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_confirm_delete.jsp" />
	</div>
</s:if>
<s:else>
	<s:if test="%{#helper.legaliRappresentantiImpresa.size() > 0 
				|| #helper.direttoriTecniciImpresa.size() > 0 
				|| #helper.altreCaricheImpresa.size() > 0 
				|| #helper.collaboratoriImpresa.size() > 0}">

		<p class="noscreen">[ <a href="#" ><wp:i18n key="LABEL_SKIP_SOGGETTI" /></a> ]</p>

		<fieldset>
			<legend><wp:i18n key="LABEL_ELENCO_SOGGETTI" /></legend>
			
			<table class="wizard-table" summary="<wp:i18n key="ELENCO_SOGGETTI_TABELLA_SUMMARY" />">
				<tr>
					<th scope="col"><wp:i18n key="LABEL_QUALIFICA" /></th>
					<th scope="col"><wp:i18n key="LABEL_NOMINATIVO" /></th>
					<th scope="col"><wp:i18n key="LABEL_CODICE_FISCALE" /></th>
					<th scope="col"><wp:i18n key="LABEL_DATA_INIZIO_INCARICO" /></th>
					<th scope="col"><wp:i18n key="LABEL_DATA_FINE_INCARICO" /></th>
					<th scope="col"><wp:i18n key="ACTIONS" /></th>
				</tr>
				<s:set var="elencoSoggetti" value="%{#helper.legaliRappresentantiImpresa}"/>
				<s:include value="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/datiimpresa/inc/iteratorSoggetti.jsp" />
				<s:set var="elencoSoggetti" value="%{#helper.direttoriTecniciImpresa}"/>
				<s:include value="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/datiimpresa/inc/iteratorSoggetti.jsp" />
				<s:set var="elencoSoggetti" value="%{#helper.altreCaricheImpresa}"/>
				<s:include value="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/datiimpresa/inc/iteratorSoggetti.jsp" />
				<s:set var="elencoSoggetti" value="%{#helper.collaboratoriImpresa}"/>
				<s:include value="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/datiimpresa/inc/iteratorSoggetti.jsp" />
			</table>
		</fieldset>
	</s:if>
	<s:if test="%{esistente}">
		<s:set var="classBlocco" value="%{'no-editable'}" />
	</s:if>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/skip_form_buttons.jsp" />
	
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/mandatory_fields_message.jsp" />

	<fieldset>
		<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span>
			<c:choose>
				<c:when test="${empty id}">
					<wp:i18n key="LABEL_NUOVO_SOGGETTO" />
				</c:when>
				<c:otherwise>
					<wp:i18n key="LABEL_MODIFICA_SOGGETTO" />
				</c:otherwise>
			</c:choose>
		</legend>
		
		<s:hidden name="esistente" value="%{esistente}"></s:hidden>
		<s:hidden name="solaLettura" value="%{solaLettura}"></s:hidden>
		
		<div class="fieldset-row first-row">
			<div class="label">
				<label><wp:i18n key="LABEL_INCARICO" /> : </label>
			</div>
			<div class="element">
				<div class="contents-group">
					<label for="soggettoQualifica"><wp:i18n key="LABEL_QUALIFICA" /> : <span class="required-field">*</span></label>
					
					<%-- verifica se esiste una decodifica per soggettoQualifica --%>
					<c:set var="soggettoQualificaValue" value=""/>
					<s:iterator value="maps['tipiSoggetto']">
						<s:if test="%{key == soggettoQualifica}">
							<c:set var="soggettoQualificaValue"><s:property value="%{value}"/></c:set> 
						</s:if>
					</s:iterator>
					<s:iterator value="maps['tipiAltraCarica']">
						<s:if test="%{key == soggettoQualifica}">
							<c:set var="soggettoQualificaValue"><s:property value="%{value}"/></c:set>
						</s:if>
					</s:iterator>
					<s:iterator value="maps['tipiCollaborazione']">
						<s:if test="%{key == soggettoQualifica}">
							<c:set var="soggettoQualificaValue"><s:property value="%{value}"/></c:set>
						</s:if>
					</s:iterator>
			
					<c:choose>
						<c:when test="${empty id or empty soggettoQualificaValue}">
							<wp:i18n key="OPT_CHOOSE_QUALIFICA" var="headerValueQualifica" />
							<wp:i18n key="OPT_GROUP_ALTRE_CARICHE" var="labelAltreCariche" />
							<wp:i18n key="OPT_GROUP_COLLABORATORI" var="labelAltreCollaboratori" />
							
							<c:choose>
								<c:when test="${empty soggettoQualificaValue && not empty tipoSoggetto}">
									<%-- se la qualifica del soggetto e' vuota mostra solo la lista associata al soggetto --%>
									<c:if test="${tipoSoggetto == 1 or tipoSoggetto == 2}">
										<s:select list="maps['tipiSoggetto']" name="soggettoQualifica" id="soggettoQualifica" value="{soggettoQualifica}"
														headerKey="" headerValue="%{#attr.headerValueQualifica}" aria-required="true">
										</s:select>
									</c:if>
									<c:if test="${tipoSoggetto == 3}">
										<s:select list="maps['tipiAltraCarica']" name="soggettoQualifica" id="soggettoQualifica" value="{soggettoQualifica}"
														headerKey="" headerValue="%{#attr.headerValueQualifica}" aria-required="true">
										</s:select>
									</c:if>
									<c:if test="${tipoSoggetto == 4}">
										<s:select list="maps['tipiCollaborazione']" name="soggettoQualifica" id="soggettoQualifica" value="{soggettoQualifica}"
														headerKey="" headerValue="%{#attr.headerValueQualifica}" aria-required="true">
										</s:select>
									</c:if>
								</c:when>
								<c:otherwise>
									<%-- lista standard per la selezione della qualifica del soggetto con tutte le qualifiche --%>
									<s:select list="maps['tipiSoggetto']" name="soggettoQualifica" id="soggettoQualifica" value="{soggettoQualifica}"
													headerKey="" headerValue="%{#attr.headerValueQualifica}" aria-required="true">
										<s:optgroup label="%{#attr.labelAltreCariche}" list="maps['tipiAltraCarica']"/>
										<s:optgroup label="%{#attr.labelAltreCollaboratori}" list="maps['tipiCollaborazione']"/>
									</s:select>
								</c:otherwise>
							</c:choose>
							
						</c:when>
						<c:otherwise>
							<s:iterator value="maps['tipiSoggetto']">
								<s:if test="%{key == soggettoQualifica}">
									<s:textfield name="descTipoSoggetto" id="descTipoSoggetto" value="%{value}" 
															 size="60" readonly="true" cssClass="no-editable" />
								</s:if>
							</s:iterator>
							<s:iterator value="maps['tipiAltraCarica']">
								<s:if test="%{key == soggettoQualifica}">
									<s:textfield name="descTipoSoggetto" id="descTipoSoggetto" value="%{value}" 
															 size="60" readonly="true" cssClass="no-editable" />
								</s:if>
							</s:iterator>
							<s:iterator value="maps['tipiCollaborazione']">
								<s:if test="%{key == soggettoQualifica}">
									<s:textfield name="descTipoSoggetto" id="descTipoSoggetto" value="%{value}" 
															 size="60" readonly="true" cssClass="no-editable" />
								</s:if>
							</s:iterator>
							<s:hidden name="soggettoQualifica" value="%{soggettoQualifica}"></s:hidden>
						</c:otherwise>
					</c:choose>
				</div>
				<div class="contents-group">
					<label for="dataInizioIncarico"><wp:i18n key="LABEL_DATA_INIZIO_INCARICO" /> (<wp:i18n key="LABEL_FORMATO_DATA" />) : <span class="required-field">*</span></label>
					<s:set var="classBloccoCampo" value="" />
					
					<s:if test="%{readonlyDataInizioIncarico}">
						<s:set var="classBloccoCampo" value="%{'no-editable'}" />
					</s:if>
					<s:textfield name="dataInizioIncarico" id="dataInizioIncarico"  value="%{dataInizioIncarico}" 
											 size="10" maxlength="10" readonly="%{readonlyDataInizioIncarico}" cssClass="%{#classBloccoCampo}" 
											 aria-required="true" />
					<s:if test="%{esistente}">
						<s:set var="classBloccoCampo" value="" />
						<s:if test="%{readonlyDataFineIncarico}">
							<s:set var="classBloccoCampo" value="%{'no-editable'}" />
						</s:if>
						
						<label for="dataFineIncarico"><wp:i18n key="LABEL_DATA_FINE_INCARICO" /> (<wp:i18n key="LABEL_FORMATO_DATA" />) :</label>
						<s:textfield name="dataFineIncarico" id="dataFineIncarico"  value="%{dataFineIncarico}" 
												 size="10" maxlength="10" readonly="%{readonlyDataFineIncarico}" cssClass="%{#classBloccoCampo}" />
					</s:if>
					<s:else>
						<s:if test="%{visibleDataFineIncarico}">
							<label for="dataFineIncarico"><wp:i18n key="LABEL_DATA_FINE_INCARICO" /> (<wp:i18n key="LABEL_FORMATO_DATA" />) :</label>
							<s:textfield name="dataFineIncarico" id="dataFineIncarico" value="%{dataFineIncarico}" size="10" maxlength="10"></s:textfield>
						</s:if> 
						<s:else>
							<s:hidden name="dataFineIncarico" id="dataFineIncarico" value="%{dataFineIncarico}"></s:hidden>
						</s:else>
					</s:else>
				</div>
			</div>
		</div>

		<div class="fieldset-row">
			<div class="label">
				<label for="responsabileDichiarazioni"><wp:i18n key="LABEL_RESPONSABILE_DICHIARAZIONI" />? : <span class="required-field">*</span></label>
			</div>
			<div class="element">
				<s:set var="classBloccoCampo" value="" />
				<s:if test="%{readonlyResponsabileDichiarazioni}">
					<s:iterator value="maps['sino']">
							<s:if test="%{key == responsabileDichiarazioni}">
								<s:textfield name="responsabileDichiarazioni" id="responsabileDichiarazioni" value="%{value}" 
														 size="60" readonly="true" cssClass="no-editable" aria-required="true" />
							</s:if>
						</s:iterator>
				</s:if>
				<s:else>
					<s:select list="maps['sino']" name="responsabileDichiarazioni" id="responsabileDichiarazioni" 
										headerKey="" headerValue="" value="%{responsabileDichiarazioni}" aria-required="true">
					</s:select>
				</s:else>
			</div>
		</div>
					
		<div class="fieldset-row">
			<div class="label">
				<label><wp:i18n key="LABEL_NOMINATIVO" /> : </label>
			</div>
			<div class="element">
			
				<s:set var="classBloccoCampo" value="" />
				<s:if test="%{readonlyCognome}">
					<s:set var="classBloccoCampo" value="%{'no-editable'}" />
				</s:if>
				
				<div class="contents-group">
					<label for="cognome"><wp:i18n key="LABEL_COGNOME" /> : <span class="required-field">*</span></label>
					<s:set var="classBloccoCampo" value="" />
					<s:if test="%{readonlyCognome}">
						<s:set var="classBloccoCampo" value="%{'no-editable'}" />
					</s:if>
					<s:textfield name="cognome" id="cognome" value="%{cognome}" 
											 size="20" maxlength="80" readonly="%{readonlyCognome}" cssClass="%{#classBloccoCampo}" 
											 aria-required="true"/>
					<s:set var="classBloccoCampo" value="" />
					<s:if test="%{readonlyNome}">
						<s:set var="classBloccoCampo" value="%{'no-editable'}" />
					</s:if>
					<label for="nome"><wp:i18n key="LABEL_NOME" /> : <span class="required-field">*</span></label>
					<s:textfield name="nome" id="nome" value="%{nome}" 
											 size="20" maxlength="80" readonly="%{readonlyNome}" cssClass="%{#classBloccoCampo}" 
											 aria-required="true"/>
				</div>
				<div class="contents-group">
					<s:set var="classBloccoCampo" value="" />
					<s:if test="%{readonlyTitolo}">
						<s:set var="classBloccoCampo" value="%{'no-editable'}" />
					</s:if>
					<label for="titolo"><wp:i18n key="LABEL_TITOLO" /> : </label>
					<s:if test="%{readonlyTitolo}">
						<s:iterator value="maps['titoliSoggetto']">
							<s:if test="%{key == titolo}">
								<s:textfield name="titolo" id="titolo" value="%{value}" 
														 size="60" readonly="true" cssClass="no-editable" />
							</s:if>
						</s:iterator>
					</s:if>
					<s:else>
						<wp:i18n key="OPT_CHOOSE_TITOLO_SOGGETTO" var="headerValueTitolo" />
						<s:select list="maps['titoliSoggetto']" name="titolo" id="titolo" value="{titolo}"
								headerKey="" headerValue="%{#attr.headerValueTitolo}" ></s:select>
					</s:else>
				</div>
			</div>
		</div>

		<div class="fieldset-row">
			<div class="label">
				<label><wp:i18n key="LABEL_DATI_NASCITA" /> : </label>
			</div>
			<div class="element">
				<div class="contents-group">
					<s:set var="classBloccoCampo" value="" />
					<s:if test="%{readonlyDataNascita}">
						<s:set var="classBloccoCampo" value="%{'no-editable'}" />
					</s:if>
					<label for="dataNascita"><wp:i18n key="LABEL_DATI_NASCITA_NATO_IL" /> (<wp:i18n key="LABEL_FORMATO_DATA" />) : <span class="required-field">*</span></label>
					<s:textfield name="dataNascita" id="dataNascita"  value="%{dataNascita}" 
											 size="10" maxlength="10" readonly="%{readonlyDataNascita}" cssClass="%{#classBloccoCampo}" 
											 aria-required="true" />
				 	<s:set var="classBloccoCampo" value="" />
					<s:if test="%{readonlyComuneNascita}">
						<s:set var="classBloccoCampo" value="%{'no-editable'}" />
					</s:if>						 
					<label for="comuneNascita"> <wp:i18n key="LABEL_DATI_NASCITA_NATO_A" /> : <span class="required-field">*</span></label>
					<s:textfield name="comuneNascita" id="comuneNascita" value="%{comuneNascita}" 
											 size="20" maxlength="100" readonly="%{readonlyComuneNascita}" cssClass="%{#classBloccoCampo}" 
											 aria-required="true" />
				</div>
				<div class="contents-group">
					<s:set var="classBloccoCampo" value="" />
					<s:if test="%{readonlyProvinciaNascita}">
						<s:set var="classBloccoCampo" value="%{'no-editable'}" />
					</s:if>	
					<label for="provinciaNascita"> <wp:i18n key="LABEL_PROVINCIA_NASCITA" /> :</label>
					<s:if test="%{readonlyProvinciaNascita}">
						<s:iterator value="maps['province']">
							<s:if test="%{key == provinciaNascita}">
								<s:textfield name="provinciaNascita" id="provinciaNascita" value="%{value}" 
														 size="60" readonly="true" cssClass="no-editable" />
							</s:if>
						</s:iterator>
					</s:if>
					<s:else>
						<s:select list="maps['province']" name="provinciaNascita" id="provinciaNascita" value="%{provinciaNascita}" cssClass="provinciaNascita"
											headerKey="" headerValue="%{#attr.headerValueProvincia}" >
						</s:select>
					</s:else>
					
				</div>
				<div class="note"><wp:i18n key="LABEL_NOTA_NAZIONE_PER_ESTERI" /></div>
			</div>
		</div>

		<div class="fieldset-row">
			<div class="label">
				<label for="sesso"><wp:i18n key="LABEL_SESSO" /> : <span class="required-field">*</span></label>
			</div>
			<div class="element">
				<s:set var="classBloccoCampo" value="" />	
				<s:if test="%{readonlySesso}">
					<s:iterator value="maps['sesso']">
							<s:if test="%{key == sesso}">
								<s:textfield name="sesso" id="sesso" value="%{value}" 
														 size="60" readonly="true" cssClass="no-editable"  />
							</s:if>
						</s:iterator>
				</s:if>
				<s:else>
					<wp:i18n key="OPT_CHOOSE_SESSO" var="headerValueSesso" />
					<s:select list="maps['sesso']" name="sesso" id="sesso" value="%{sesso}"
									headerKey="" headerValue="%{#attr.headerValueSesso}" readonly="%{readonlySesso}" cssClass="%{#classBloccoCampo}" 
									aria-required="true" >
					</s:select>
				</s:else>
			</div>
		</div>
 
		<div class="fieldset-row">
			<div class="label">
				<label for="codiceFiscale"><wp:i18n key="LABEL_CODICE_FISCALE" /> : <span class="required-field">*</span></label>
			</div>
			<div class="element">
				<s:set var="classBloccoCampo" value="" />
				<s:if test="%{readonlyCodiceFiscale}">
					<s:set var="classBloccoCampo" value="%{'no-editable'}" />
				</s:if>
				<s:textfield name="codiceFiscale" id="codiceFiscale" value="%{codiceFiscale}" 
										 size="20" maxlength="16" readonly="%{readonlyCodiceFiscale}" cssClass="%{#classBloccoCampo}" 
										 aria-required="true" />
				<button type="button" id="genera" name="genera" title="<wp:i18n key='LABEL_GENERA' />" onclick="openCFGeneratorDialog()" class="button" ><wp:i18n key='LABEL_GENERA' /></button>
			</div>
		</div>

		<div class="fieldset-row">
			<div class="label">
				<label><wp:i18n key="LABEL_RESIDENZA" /> : </label>
			</div>
			<div class="element">
				<div class="contents-group">
					<s:set var="classBloccoCampo" value="" />
					<s:if test="%{readonlyIndirizzo}">
						<s:set var="classBloccoCampo" value="%{'no-editable'}" />
					</s:if>	
					<label for="indirizzo"><wp:i18n key="LABEL_INDIRIZZO" /> : <span class="required-field">*</span></label>
					<s:textfield name="indirizzo" id="indirizzo" value="%{indirizzo}" 
											 size="40" maxlength="100"  readonly="%{readonlyIndirizzo}" cssClass="%{#classBloccoCampo}"
											 aria-required="true" />
					<s:set var="classBloccoCampo" value="" />
					<s:if test="%{readonlyNumCivico}">
						<s:set var="classBloccoCampo" value="%{'no-editable'}" />
					</s:if>							 
					<label for="numCivico"><wp:i18n key="LABEL_NUM_CIVICO" /> : <span class="required-field">*</span></label>
					<s:textfield name="numCivico" id="numCivico" value="%{numCivico}" 
											 size="4" maxlength="10" readonly="%{readonlyNumCivico}" cssClass="%{#classBloccoCampo}" 
											 aria-required="true" />
				</div>
				<div class="contents-group">
					<s:set var="classBloccoCampo" value="" />
					<label for="nazione"><wp:i18n key="LABEL_NAZIONE" /> : <span class="required-field">*</span></label>
					<s:if test="%{readonlyNazione}">
						<s:iterator value="maps['nazioni']">
							<s:if test="%{key == nazione}">
								<s:textfield name="nazione" id="nazione" value="%{value}" 
														 size="60" readonly="true" cssClass="no-editable" aria-required="true" />
							</s:if>
						</s:iterator>
					</s:if>	
					<s:else>
						<wp:i18n key="OPT_CHOOSE_NAZIONE" var="headerValueNazione" />
						<s:select list="maps['nazioni']" name="nazione" id="nazione" value="{nazione}"
										headerKey="" headerValue="%{#attr.headerValueNazione}" aria-required="true">
						</s:select>
					</s:else>
				</div>
				<div class="contents-group">
					<s:set var="classBloccoCampo" value="" />
					<s:if test="%{readonlyCap}">
						<s:set var="classBloccoCampo" value="%{'no-editable'}" />
					</s:if>	
					<label for="cap" class="cap"><wp:i18n key="LABEL_CAP" /> : <span id="obbCap" class="required-field">*</span></label>
					<s:textfield name="cap" id="cap" value="%{cap}" 
											 size="5" maxlength="5"  readonly="%{readonlyCap}" cssClass="%{#classBloccoCampo} cap" 
											 aria-required="true" />
					<s:set var="classBloccoCampo" value="" />
					<s:if test="%{readonlyComune}">
						<s:set var="classBloccoCampo" value="%{'no-editable'}" />
					</s:if>	
					<label for="comune"><wp:i18n key="LABEL_COMUNE" /> : <span class="required-field">*</span></label>
					<s:textfield name="comune" id="comune" value="%{comune}" 
											 size="30" maxlength="100" readonly="%{readonlyComune}" cssClass="%{#classBloccoCampo}" 
											 aria-required="true" />
				</div>
				<div class="contents-group">
					<s:set var="classBloccoCampo" value="" />
					<label for="provincia" class="provincia"><wp:i18n key="LABEL_PROVINCIA" /> : <span id="obbProv" class="required-field">*</span></label>
					<s:if test="%{readonlyProvincia}">
						<s:iterator value="maps['province']">
							<s:if test="%{key == provincia}">
								<s:textfield name="provincia" id="provincia" value="%{value}"  
											size="60" readonly="true" cssClass="no-editable provincia" aria-required="true" />
							</s:if>
						</s:iterator>
					</s:if>	
					<s:else>
						<s:select list="maps['province']" name="provincia" id="provincia" value="%{provincia}" cssClass="provincia"
											headerKey="" headerValue="%{#attr.headerValueProvincia}" aria-required="true" >
						</s:select>
					</s:else>
				</div>
				<div class="note">
					<wp:i18n key="LABEL_NOTA_PROVINCIA_SOLO_PER_ITALIA" />
				</div>
			</div>
		</div>
				
		<div class="fieldset-row">
			<div class="label">
				<label><wp:i18n key="LABEL_ISCRIZIONE_ALBO_PROF" />: </label>
			</div>
			<div class="element">
				<div class="contents-group">
					<s:set var="classBloccoCampo" value="" />
					<label for="tipologiaAlboProf"><wp:i18n key="LABEL_TIPOLOGIA" /> :</label>
					<s:if test="%{readonlyTipologiaAlboProf}">
						<s:iterator value="maps['albiProfessionali']">
							<s:if test="%{key == tipologiaAlboProf}">
								<s:textfield name="tipologiaAlboProf" id="tipologiaAlboProf" value="%{value}" 
														 size="60" readonly="true" cssClass="no-editable" />
							</s:if>
						</s:iterator>
					</s:if>	
					<s:else>
						<wp:i18n key="OPT_CHOOSE_TIPOLOGIA" var="headerValueTipologiaAlboProf" />
						<s:select list="maps['albiProfessionali']" name="tipologiaAlboProf" id="tipologiaAlboProf" 
											value="%{tipologiaAlboProf}" headerKey="" headerValue="%{#attr.headerValueTipologiaAlboProf}" >
						</s:select>
					</s:else>
				</div>
				<div class="contents-group tipologiaAlboProf">
					<s:set var="classBloccoCampo" value="" />
					<s:if test="%{readonlyNumIscrizioneAlboProf}">
						<s:set var="classBloccoCampo" value="%{'no-editable'}" />
					</s:if>	
					<label for="numIscrizioneAlboProf"><wp:i18n key="LABEL_NUM_ISCRIZIONE" /> :</label>
					<s:textfield name="numIscrizioneAlboProf" id="numIscrizioneAlboProf" value="%{numIscrizioneAlboProf}" 
											 size="16" maxlength="50" readonly="%{readonlyNumIscrizioneAlboProf}" cssClass="%{#classBloccoCampo}" />
				</div>
				<div class="contents-group tipologiaAlboProf">
					<s:set var="classBloccoCampo" value="" />
					<s:if test="%{readonlyDataIscrizioneAlboProf}">
						<s:set var="classBloccoCampo" value="%{'no-editable'}" />
					</s:if>	
					<label for="dataIscrizioneAlboProf"><wp:i18n key="LABEL_DATA_ISCRIZIONE" /> (<wp:i18n key="LABEL_FORMATO_DATA" />):</label>
					<s:textfield name="dataIscrizioneAlboProf" id="dataIscrizioneAlboProf"  value="%{dataIscrizioneAlboProf}" 
											 size="10" maxlength="10" readonly="%{readonlyDataIscrizioneAlboProf}" cssClass="%{#classBloccoCampo}" />
				</div>
				<div class="contents-group tipologiaAlboProf">
					<s:set var="classBloccoCampo" value="" />
					<label for="provinciaIscrizioneAlboProf"><wp:i18n key="LABEL_PROVINCIA_ISCRIZIONE" /> :</label>
					<s:if test="%{readonlyProvinciaIscrizioneAlboProf}">
						<s:iterator value="maps['province']">
							<s:if test="%{key == provinciaIscrizioneAlboProf}">
								<s:textfield name="provinciaIscrizioneAlboProf" id="provinciaIscrizioneAlboProf" value="%{value}" 
														 size="60" readonly="true" cssClass="no-editable" />
							</s:if>
						</s:iterator>
					</s:if>	
					<s:else>
						<s:select list="maps['province']" name="provinciaIscrizioneAlboProf" id="provinciaIscrizioneAlboProf" 
											value="%{provinciaIscrizioneAlboProf}" headerKey="" headerValue="%{#attr.headerValueProvincia}" >
						</s:select>
					</s:else>
				</div>
			</div>
		</div>

		<div class="fieldset-row">
			<div class="label">
				<label><wp:i18n key="LABEL_ISCRIZIONE_CASSA_PREV" />: </label>
			</div>
			<div class="element">
				<div class="contents-group">
					<s:set var="classBloccoCampo" value="" />
					<label for="tipologiaCassaPrevidenza"><wp:i18n key="LABEL_TIPOLOGIA" /></label>
					<s:if test="%{readonlyTipologiaCassaPrevidenza}">
						<s:iterator value="maps['cassePrevidenza']">
							<s:if test="%{key == tipologiaCassaPrevidenza}">
								<s:textfield name="tipologiaCassaPrevidenza" id="tipologiaCassaPrevidenza" value="%{value}" 
														 size="60" readonly="true" cssClass="no-editable" />
							</s:if>
						</s:iterator>
					</s:if>	
					<s:else>
						<wp:i18n key="OPT_CHOOSE_TIPOLOGIA" var="headerValueTipologiaCassaPrevidenza" />
						<s:select list="maps['cassePrevidenza']" name="tipologiaCassaPrevidenza" id="tipologiaCassaPrevidenza" 
											value="%{tipologiaCassaPrevidenza}" headerKey="" headerValue="%{#attr.headerValueTipologiaCassaPrevidenza}" >
						</s:select>
					</s:else>
				</div>
				<div class="contents-group tipologiaCassaPrevidenza">
					<s:set var="classBloccoCampo" value="" />
					<s:if test="%{readonlyNumMatricolaCassaPrevidenza}">
						<s:set var="classBloccoCampo" value="%{'no-editable'}" />
					</s:if>	
					<label for="numMatricolaCassaPrevidenza"><wp:i18n key="LABEL_ISCRIZIONE_CASSA_PREV_MATRICOLA" /> :</label>
					<s:textfield name="numMatricolaCassaPrevidenza" id="numMatricolaCassaPrevidenza" 
											 value="%{numMatricolaCassaPrevidenza}" size="16" maxlength="16" readonly="%{readonlyNumMatricolaCassaPrevidenza}" cssClass="%{#classBloccoCampo}" />
				</div>
			</div>
		</div>

		<div class="fieldset-row last-row">
			<s:set var="classBloccoCampo" value="" />
			<s:if test="%{readonlyNote}">
				<s:set var="classBloccoCampo" value="%{'no-editable'}" />
			</s:if>	
			<div class="label">
				<label for="note"><wp:i18n key="LABEL_NOTE" />: </label>
			</div>
			<div class="element">
					<s:textarea name="note" id="note" value="%{note}" rows="5" cols="68" readonly="%{readonlyNote}" cssClass="%{#classBloccoCampo}" />
			</div>
		</div>

		<div class="azioni">
			<c:choose>
				<c:when test="${empty id}">
					<wp:i18n key="BUTTON_ADD" var="valueAddButton" />
					<wp:i18n key="LABEL_ADD_SOGGETTO" var="titleAddButton" />
					<s:submit value="%{#attr.valueAddButton}" title="%{#attr.titleAddButton}"  cssClass="button" method="insert"></s:submit>
				</c:when>
				<c:otherwise>
					<s:if test="%{!allFieldAsReadonly}">
						<wp:i18n key="BUTTON_REFRESH" var="valueRefreshButton" />
						<wp:i18n key="LABEL_REFRESH_SOGGETTO" var="titleRefreshButton" />
						<s:submit value="%{#attr.valueRefreshButton}" title="%{#attr.titleRefreshButton}" cssClass="button" method="save"></s:submit>
					</s:if>
					<wp:i18n key="BUTTON_NEW" var="valueNewButton" />
					<wp:i18n key="LABEL_START_NEW_SOGGETTO" var="titleNewButton" />
					<s:submit value="%{#attr.valueNewButton}" title="%{#attr.titleNewButton}" cssClass="button" method="add"></s:submit>
				</c:otherwise>
			</c:choose>
		</div>
	</fieldset>

	<input type="hidden" name="obblIscrizione" value="${obblIscrizione}" />	

</s:else>

<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/datiimpresa/inc/fiscalCodeCalcDialog.jsp" />


<%-- gestione CAP per nazione estera --%>
<script type="text/javascript">
//<![CDATA[
	$(document).ready(function() {

		nazioneChange();
		visualizzaTipologiaAlboProf();
		visualizzaTipologiaCassaPrevidenza();

		$("optgroup").removeAttr("aria-required")
	
		$('[id="nazione"]').change(function() {
			nazioneChange();
		});

	});

	function nazioneChange() {
 		var newval = $('[id="nazione"] option:selected').val();
 		var oldval = $('[id="nazione"]').data("value");
 		$('[id="nazione"]').data("value", newval); 
 		if(newval.toUpperCase() == "ITALIA") {
			$('#obbCap').show();
			$("#obbProv").show();
			$('.cap').show();
			$('.provincia').show();
			$('[id="codiceFiscale"]').prop('maxLength', 16);
		} else {
			$('#obbCap').hide();
			$("#obbProv").hide();
			$('#provincia').val("");
			$('.cap').hide();
			$('.provincia').hide();
			$('.provincia').hide();
			$('[id="codiceFiscale"]').prop('maxLength', 30);
		} 
		if(newval != oldval && oldval != null) {
			$('[id="cap"]').val("");
		}
	}

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
	
	// se il valore di "Iscrizione Albo professionale ?" = "", si annullano 
    // i valori dei campi rating legale 
	function visualizzaTipologiaAlboProf() {
		var v = $("#tipologiaAlboProf :selected").val();
		showElement($(".tipologiaAlboProf"), (v != ""));
	}
	
	$("select[id^='tipologiaAlboProf']").change(function() {
		var v = $("#tipologiaAlboProf :selected").val();
		
		visualizzaTipologiaAlboProf();
		
		// resetta i campi
		if(v == "") {
			$("#numIscrizioneAlboProf").val(null);
			$("#dataIscrizioneAlboProf").val(null);
			$("#provinciaIscrizioneAlboProf").val(null);
		}
	});

	// se il valore di "Iscrizione Cassa di Previdenza ?" = "", si annullano 
    // i valori dei campi rating legale 
	function visualizzaTipologiaCassaPrevidenza() {
		var v = $("#tipologiaCassaPrevidenza :selected").val();
		showElement($(".tipologiaCassaPrevidenza"), (v != ""));
	}
	
	$("select[id^='tipologiaCassaPrevidenza']").change(function() {
		var v = $("#tipologiaCassaPrevidenza :selected").val();
		
		visualizzaTipologiaCassaPrevidenza();
		
		// resetta i campi
		if(v == "") {
			$("#numMatricolaCassaPrevidenza").val(null);
		}
	});

//]]>
</script>