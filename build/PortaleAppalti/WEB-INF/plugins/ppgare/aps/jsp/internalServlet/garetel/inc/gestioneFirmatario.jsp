<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<wp:i18n key="OPT_CHOOSE_PROVINCIA" var="headerValueProvincia" />

<s:set name="key" value="%{getFirmatario(currentMandante)}"/>
 
<fieldset>
	<legend><wp:i18n key="LABEL_FIRMATARIO"/> <s:property value="%{currentMandante.ragioneSociale}"/></legend>

	<div class="fieldset-row first-row">
		<div class="label">
			<label for="ragioneSociale"><wp:i18n key="LABEL_RAGIONE_SOCIALE" /> : </label>
		</div>
		<div class="element"><s:property value="%{currentMandante.ragioneSociale}"/></div>
	</div>
	
	<div class="fieldset-row">
		<div class="label">
			<label for="tipologiaImpresa"><wp:i18n key="LABEL_TIPO_IMPRESA" /> : </label>
		</div>
		<div class="element"><s:property value="%{tipoImpresaCodifica}"/></div>
	</div>
	
	<div class="fieldset-row">
		<div class="label">
			<label for="codiceFiscale"><wp:i18n key="LABEL_CODICE_FISCALE" /> : </label>
		</div>
		<div class="element"><s:property value="%{currentMandante.codiceFiscale}"/></div>
	</div>
	
	<div class="fieldset-row">
		<div class="label">
			<label for="partitaIVA"><wp:i18n key="LABEL_PARTITA_IVA" /> : </label>
		</div>
		<div class="element"><s:property value="%{currentMandante.partitaIVA}"/></div>
	</div>

	<div class="fieldset-row">
		<div class="label">
			<label><wp:i18n key="LABEL_NOMINATIVO" /> : </label>
		</div>
		<div class="element">
			<div class="contents-group">
				<label for="cognome"><wp:i18n key="LABEL_COGNOME" /> : 
					<span class="required-field">*</span>
				</label>
				<s:set var="classBloccoCampo" value="" />
				<s:textfield name="cognome" id="cognome" value="%{#key.cognome}" size="20" maxlength="80" />
				<label for="nome"><wp:i18n key="LABEL_NOME" /> : 
					<span class="required-field">*</span>
				</label>
				<s:textfield name="nome" id="nome" value="%{#key.nome}" size="20" maxlength="80" />
			</div> 
		</div>
	</div>

	<div class="fieldset-row">
		<div class="label">
			<label><wp:i18n key="LABEL_DATI_NASCITA" /> : </label>
		</div>
		<div class="element">
			<div class="contents-group">
				<label for="dataNascita"><wp:i18n key="LABEL_DATI_NASCITA_NATO_IL" /> (<wp:i18n key="LABEL_FORMATO_DATA" />) : 
					<span class="required-field">*</span>
				</label>
				<s:textfield name="dataNascita" id="dataNascita" value="%{#key.dataNascita}" size="10" maxlength="10" />
				<label for="comuneNascita"> <wp:i18n key="LABEL_DATI_NASCITA_NATO_A" /> : 
					<span class="required-field">*</span>
				</label>
				<s:textfield name="comuneNascita" id="comuneNascita" value="%{#key.comuneNascita}" size="20" maxlength="100" />
			</div>
			<div class="contents-group">
				<label for="provinciaNascita"> <wp:i18n key="LABEL_PROVINCIA" /> :</label>
				<s:select list="maps['province']" name="provinciaNascita"
					id="provinciaNascita" value="%{#key.provinciaNascita}" headerKey=""
					headerValue="%{#attr.headerValueProvincia}" >
				</s:select>
			</div>
		</div>
	</div>
	
	<div class="fieldset-row">
		<div class="label">
			<label for="sesso"><wp:i18n key="LABEL_SESSO" /> : 
				<span class="required-field">*</span>
			</label>
		</div>
		<div class="element">
			<wp:i18n key="OPT_CHOOSE_SESSO" var="headerValueSesso" />
			<s:select list="maps['sesso']" name="sesso" id="sesso"
				value="%{#key.sesso}" headerKey="" headerValue="%{#attr.headerValueSesso}"
				>
			</s:select>
		</div>
	</div>

	<div class="fieldset-row">
		<div class="label">
			<label for="codiceFiscale"><wp:i18n key="LABEL_CODICE_FISCALE" /> : 
				<span class="required-field">*</span>
			</label>
		</div>
		<div class="element">
			<s:set var="classBloccoCampo" value="" />
			<s:if test="%{readonlyCodiceFiscale}">
				<s:set var="classBloccoCampo" value="%{'no-editable'}" />
			</s:if>
			<s:textfield name="codiceFiscale" id="codiceFiscale" value="%{#key.codiceFiscale}" size="20" maxlength="16" 
				readonly="%{readonlyCodiceFiscale}"
				cssClass="%{#classBloccoCampo}" />
		</div>
	</div>

	<div class="fieldset-row">
		<div class="label">
			<label for="indirizzo"><wp:i18n key="LABEL_RESIDENZA" /> : </label>
		</div>
		<div class="element">
			<div class="contents-group">
				<label for="indirizzo"><wp:i18n key="LABEL_INDIRIZZO" /> : 
					<span class="required-field">*</span>
				</label>
				<s:textfield name="indirizzo" id="indirizzo" value="%{#key.indirizzo}" size="40" maxlength="60" />
				<label for="numCivico"><wp:i18n key="LABEL_NUM_CIVICO" /> : 
					<span class="required-field">*</span>
				</label>
				<s:textfield name="numCivico" id="numCivico" value="%{#key.numCivico}" size="4" maxlength="10" />
			</div>
			<div class="contents-group">
				<label for="cap"><wp:i18n key="LABEL_CAP" /> : 
					<span class="required-field">*</span>
				</label>
				<s:textfield name="cap" id="cap" value="%{#key.cap}" size="5" maxlength="5" />
				<label for="comune"><wp:i18n key="LABEL_COMUNE" /> : 
					<span class="required-field">*</span>
				</label>
				<s:textfield name="comune" id="comune" value="%{#key.comune}" size="30" maxlength="100" />
			</div>
			<div class="contents-group">
				<label for="provincia"><wp:i18n key="LABEL_PROVINCIA" /> :</label>
				<s:select list="maps['province']" name="provincia" id="provincia"
					value="%{#key.provincia}" headerKey=""
					headerValue="%{#attr.headerValueProvincia}" >
				</s:select>
				<div class="note"><wp:i18n key="LABEL_NOTA_PROVINCIA_SOLO_PER_ITALIA" /></div>
			</div>
			<div class="contents-group">
				<label for="nazione"><wp:i18n key="LABEL_NAZIONE" /> : 
					<span class="required-field">*</span>
				</label>
				<wp:i18n key="OPT_CHOOSE_NAZIONE" var="headerValueNazione" />
				<s:select list="maps['nazioni']" name="nazione" id="nazione" value="{#key.nazione}"
							headerKey="" headerValue="%{#attr.headerValueNazione}" >
				</s:select>
			</div>
		</div>
	</div>
	
	<s:if test="%{!mandanteLiberoProfessionista}">
		<div class="fieldset-row last-row">
			<div class="label">
				<label><wp:i18n key="LABEL_INCARICO" /> : </label>
			</div>
			<div class="element">
				<div class="contents-group">
					<label for="soggettoQualifica"><wp:i18n key="LABEL_QUALIFICA" /> : 
						<span class="required-field">*</span>
					</label>
					<wp:i18n key="OPT_CHOOSE_QUALIFICA" var="headerValueQualifica" />
					<wp:i18n key="OPT_GROUP_ALTRE_CARICHE" var="labelAltreCariche" />
					<s:select list="maps['tipiSoggetto']" name="soggettoQualifica" id="soggettoQualifica" value="%{#key.soggettoQualifica}"
								headerKey="" headerValue="%{#attr.headerValueQualifica}" >
						<s:optgroup label="%{#attr.labelAltreCariche}" list="maps['tipiAltraCarica']"></s:optgroup>						
					</s:select>

				</div>
			</div>
		</div>
	</s:if>
	
	<input type="hidden" name="obbQualifica" value="${!mandanteLiberoProfessionista}" />
	<div class="azioni">
		<input type="submit" id="firmatarioMandante" value="<wp:i18n key="BUTTON_REFRESH" />"
			title="<wp:i18n key="TITLE_AGGIORNA_FIRMATARIO_MANDANTE" />"
			class="button" />
	</div>

</fieldset>