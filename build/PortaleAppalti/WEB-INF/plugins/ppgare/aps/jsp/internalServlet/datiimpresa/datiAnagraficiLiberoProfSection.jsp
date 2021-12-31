<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>

<s:set var="bloccoEdit">${param.noEdit}</s:set>
	<c:if test="${param.noEdit}">
	<s:set var="classBlocco" value="%{'no-editable'}" />
</c:if>

<wp:i18n key="OPT_CHOOSE_PROVINCIA" var="headerValueProvincia" />

<es:checkCustomization var="obblIscrizione" objectId="IMPRESA-DATIANAGR-SEZ" attribute="ISCRIZIONEALBOPROF" feature="MAN" />

<fieldset>
	<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_EDIT_DATI_OE_STEP_ALTRI_DATI_ANAGRAFICI" /></legend>
	
	<div class="fieldset-row first-row">
		<div class="label">
			<label for="titolo"><wp:i18n key="LABEL_TITOLO" /> : </label>
		</div>
		<div class="element">
			<wp:i18n key="OPT_CHOOSE_TITOLO_SOGGETTO" var="headerValueTitolo" />
			<s:select list="maps['titoliSoggetto']" name="titolo" id="titolo" value="{titolo}"
								headerKey="" headerValue="%{#attr.headerValueTitolo}" ></s:select>
		</div>
	</div>
	<div class="fieldset-row">
		<div class="label">
			<label for="cognome"><wp:i18n key="LABEL_COGNOME" /> : <span class="required-field">*</span></label>
		</div>
		<div class="element">
			<s:textfield name="cognome" id="cognome" value="%{cognome}" 
										 size="16" maxlength="80" />
		</div>
	</div>
	<div class="fieldset-row">
		<div class="label">
			<label for="nome"><wp:i18n key="LABEL_NOME" /> : <span class="required-field">*</span></label>
		</div>
		<div class="element">
			<s:textfield name="nome" id="nome" value="%{nome}" 
										 size="16" maxlength="80" />
		</div>
	</div>
	<div class="fieldset-row">
		<div class="label">
			<label><wp:i18n key="LABEL_DATI_NASCITA" /> : </label>
		</div>
		<div class="element">
			<label for="dataNascita"><wp:i18n key="LABEL_DATI_NASCITA_NATO_IL" /> (<wp:i18n key="LABEL_FORMATO_DATA" />) : <span class="required-field">*</span></label>
			<s:textfield name="dataNascita" id="dataNascita"  value="%{dataNascita}" 
									 size="10" maxlength="10" />
			<label for="comuneNascita"> <wp:i18n key="LABEL_DATI_NASCITA_NATO_A" /> : <span class="required-field">*</span></label>
			<s:textfield name="comuneNascita" id="comuneNascita" value="%{comuneNascita}" 
									 size="20" maxlength="100" />
			<label for="provinciaNascita"> <wp:i18n key="LABEL_PROVINCIA" /> :</label>
			<s:select list="maps['province']" name="provinciaNascita" id="provinciaNascita" value="%{provinciaNascita}"
								headerKey="" headerValue="%{#attr.headerValueProvincia}" >
			</s:select>
		</div>
	</div>

	<div class="fieldset-row">
		<div class="label">
			<label for="sesso"><wp:i18n key="LABEL_SESSO" /> : <span class="required-field">*</span></label>
		</div>
		<div class="element">
			<wp:i18n key="OPT_CHOOSE_SESSO" var="headerValueSesso" />
			<s:select list="maps['sesso']" name="sesso" id="sesso" value="%{sesso}"
								headerKey="" headerValue="%{#attr.headerValueSesso}" >
			</s:select>
		</div>
	</div>
	
	<div class="fieldset-row">
		<div class="label">
			<label><wp:i18n key="LABEL_ISCRIZIONE_ALBO_PROF" /> : <c:if test="${obblIscrizione}"><span class="required-field">*</span></c:if></label>
			<input type="hidden" name="obblIscrizione" value="${obblIscrizione}"/>
		</div>
		<div class="element">
			<div class="contents-group">
				<label for="tipologiaAlboProf"><wp:i18n key="LABEL_TIPOLOGIA" /> :</label>
				<wp:i18n key="OPT_CHOOSE_TIPOLOGIA" var="headerValueTipologiaAlboProf" />
				<s:select list="maps['albiProfessionali']" name="tipologiaAlboProf" id="tipologiaAlboProf" value="%{tipologiaAlboProf}"
									headerKey="" headerValue="%{#attr.headerValueTipologiaAlboProf}" >
			</s:select>
			</div>
			<div class="contents-group">
				<label for="numIscrizioneAlboProf"><wp:i18n key="LABEL_NUM_ISCRIZIONE" /> :</label>
				<s:textfield name="numIscrizioneAlboProf" id="numIscrizioneAlboProf" value="%{numIscrizioneAlboProf}" 
										 size="16" maxlength="50" />
			</div>
			<div class="contents-group">
				<label for="dataIscrizioneAlboProf"><wp:i18n key="LABEL_DATA_ISCRIZIONE" /> (<wp:i18n key="LABEL_FORMATO_DATA" />) :</label>
				<s:textfield name="dataIscrizioneAlboProf" id="dataIscrizioneAlboProf" value="%{dataIscrizioneAlboProf}" 
										 size="10" maxlength="10" />
			</div>
			<div class="contents-group">
				<label for="provinciaIscrizioneAlboProf"><wp:i18n key="LABEL_PROVINCIA_ISCRIZIONE" /> :</label>
				<s:select list="maps['province']" name="provinciaIscrizioneAlboProf" id="provinciaIscrizioneAlboProf" value="%{provinciaIscrizioneAlboProf}"
									headerKey="" headerValue="%{#attr.headerValueProvincia}" >
				</s:select>
			</div>
		</div>
	</div>

	<div class="fieldset-row last-row">
		<div class="label">
			<label><wp:i18n key="LABEL_ISCRIZIONE_CASSA_PREV" /> : </label>
		</div>
		<div class="element">
			<div class="contents-group">
				<label for="tipologiaCassaPrevidenza"><wp:i18n key="LABEL_TIPOLOGIA" /> :</label>
				<wp:i18n key="OPT_CHOOSE_TIPOLOGIA" var="headerValueTipologiaCassaPrevidenza" />
				<s:select list="maps['cassePrevidenza']" name="tipologiaCassaPrevidenza" id="tipologiaCassaPrevidenza" value="%{tipologiaCassaPrevidenza}"
									headerKey="" headerValue="%{#attr.headerValueTipologiaCassaPrevidenza}" >
				</s:select>
			</div>
			<div class="contents-group">
				<label for="numMatricolaCassaPrevidenza"><wp:i18n key="LABEL_ISCRIZIONE_CASSA_PREV_MATRICOLA" /> :</label>
				<s:textfield name="numMatricolaCassaPrevidenza" id="numMatricolaCassaPrevidenza" value="%{numMatricolaCassaPrevidenza}" 
										 size="16" maxlength="16" />
			</div>
		</div>
	</div>	
</fieldset>