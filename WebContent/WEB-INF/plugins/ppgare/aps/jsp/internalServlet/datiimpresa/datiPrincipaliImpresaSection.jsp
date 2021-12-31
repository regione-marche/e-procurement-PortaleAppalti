<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>

<s:set var="bloccoEdit">${param.noEdit}</s:set>
<c:if test="${param.noEdit}">
	<s:set var="classBlocco" value="%{'no-editable'}" />
</c:if>

<es:checkCustomization var="obblFaxRecapito" objectId="IMPRESA-DATIPRINC-RECAPITI" attribute="FAX" feature="MAN" />
<es:checkCustomization var="visEmailRecapito" objectId="IMPRESA-DATIPRINC-RECAPITI" attribute="MAIL" feature="VIS" />
<es:checkCustomization var="obblEmailRecapito" objectId="IMPRESA-DATIPRINC-RECAPITI" attribute="MAIL" feature="MAN" />
<es:checkCustomization var="obblEmailPECRecapito" objectId="IMPRESA-DATIPRINC-RECAPITI" attribute="PEC" feature="MAN" />
<es:checkCustomization var="obblPECEstero" objectId="IMPRESA-DATIPRINC-RECAPITI" attribute="PECESTERO" feature="MAN" />

<es:checkCustomization var="obblCellulareRecapito" objectId="IMPRESA-DATIPRINC-RECAPITI" attribute="CELL" feature="MAN" />

<fieldset>
	<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_DATI_PRINCIPALI_OE" /></legend>
	
	<div class="fieldset-row first-row">
		<div class="label">
			<label for="ragioneSociale"><wp:i18n key="LABEL_RAGIONE_SOCIALE" /> : <span class="required-field">*</span></label>
		</div>
		<div class="element">
			<s:textfield name="ragioneSociale" id="ragioneSociale" value="%{ragioneSociale}" size="60" maxlength="2000" 
									 readonly="%{#bloccoEdit}" cssClass="%{#classBlocco}" aria-required="true"/>
		</div>
	</div>

	<div class="fieldset-row">
		<div class="label">
			<c:set var="lblForTipoImpresa"><s:if test="%{readonlyTipoImpresa}">descTipoImpresa</s:if><s:else>tipoImpresa</s:else></c:set>			
			<label for="${lblForTipoImpresa}"><wp:i18n key="LABEL_TIPO_IMPRESA" /> : <span class="required-field">*</span></label>
		</div>
		<div class="element">
			<s:if test="%{readonlyTipoImpresa}">
				<c:set var="lblDescTipoImp"><wp:i18n key="LABEL_TIPO_IMPRESA" /></c:set> 
				<s:iterator value="maps['tipiImpresaIscrAlbo']">
					<s:if test="%{key == tipoImpresa}">
						<s:textfield name="descTipoImpresa" id="descTipoImpresa" value="%{value}" size="60" 
									readonly="true" cssClass="%{#classBlocco}" aria-required="true"
									aria-label="${lblDescTipoImp}" />
					</s:if>
				</s:iterator>
				<s:hidden name="tipoImpresa" value="%{tipoImpresa}"/>
			</s:if>
			<s:else>
				<wp:i18n key="OPT_CHOOSE_TIPO_IMPRESA" var="headerValueTipoImpresa" />
				<s:select list="maps['tipiImpresaIscrAlbo']" name="tipoImpresa" id="tipoImpresa" value="{tipoImpresa}" 
									headerKey="" headerValue="%{#attr.headerValueTipoImpresa}" aria-required="true">
				</s:select>
			</s:else>
		</div>
	</div>

	<div class="fieldset-row">
		<div class="label">
			<c:set var="lblForNaturaGiuridica"><s:if test="%{readonlyTipoImpresa}">descNaturaGiuridica</s:if><s:else>naturaGiuridica</s:else></c:set>							
			<label for="${lblForNaturaGiuridica}"><wp:i18n key="LABEL_NATURA_GIURIDICA" /> : <span class="required-field">*</span></label>
		</div>
		<div class="element">
			<s:if test="%{readonlyNaturaGiuridica}">
				<c:set var="lblNatGiu"><wp:i18n key="LABEL_NATURA_GIURIDICA" /></c:set> 
				<s:iterator value="maps['tipiNaturaGiuridica']">
					<s:if test="%{key == naturaGiuridica}">
						<s:textfield name="descNaturaGiuridica" id="descNaturaGiuridica" value="%{value}" size="60" 
									readonly="true" cssClass="%{#classBlocco}" aria-required="true"
									aria-label="${lblNatGiu}" />
					</s:if>
				</s:iterator>
				<s:hidden name="naturaGiuridica" value="%{naturaGiuridica}"/>
			</s:if>
			<s:else>
				<wp:i18n key="OPT_CHOOSE_NATURA_GIURIDICA" var="headerValueNaturaGiuridica" />
				<s:select list="maps['tipiNaturaGiuridica']" name="naturaGiuridica" id="naturaGiuridica" value="%{naturaGiuridica}"
									headerKey="" headerValue="%{#attr.headerValueNaturaGiuridica}" aria-required="true">
				</s:select>
			</s:else>
		</div>
	</div>

	<div class="fieldset-row">
		<div class="label">
			<label for="ambitoTerritoriale"><wp:i18n key="LABEL_AMBITO_TERRITORIALE" /> : </label>
		</div>
		<div class="element">
			<s:if test="%{#bloccoEdit}">
				<c:set var="lblAmbitoTerr"><wp:i18n key="LABEL_AMBITO_TERRITORIALE" /></c:set>
				<s:iterator value="maps['ambitoTerritoriale']">
					<s:if test="%{key == ambitoTerritoriale}">
						<s:textfield name="descAmbitoTerr" id="descAmbitoTerr" value="%{value}" size="60" readonly="true" cssClass="%{#classBlocco}"
									aria-label="${lblAmbitoTerr}" />
					</s:if>
				</s:iterator>
				<s:hidden name="ambitoTerritoriale" value="%{ambitoTerritoriale}"/>
			</s:if>
			<s:else>
				<s:select list="maps['ambitoTerritoriale']" name="ambitoTerritoriale" id="ambitoTerritoriale" value="{ambitoTerritoriale}" 
									headerKey="" >
				</s:select>
			</s:else>
		</div>
	</div>
	
	<div class="fieldset-row">
		<div class="label">
			<label for="codiceFiscale"><span class="lblCodiceFiscale"><wp:i18n key="LABEL_CODICE_FISCALE" /></span> : <span class="required-field">*</span></label>
		</div>
		<div class="element">
			<s:textfield name="codiceFiscale" id="codiceFiscale" value="%{codiceFiscale}" maxlength="16" size="60" 
									 readonly="%{#bloccoEdit}" cssClass="%{#classBlocco}" aria-required="true"/>
		</div>
	</div>

	<div class="fieldset-row partitaIVA">
		<div class="label">
			<label for="partitaIVA"><wp:i18n key="LABEL_PARTITA_IVA" /> : <span id="obbPI" class="required-field">*</span></label>
		</div>
		<div class="element">
			<s:textfield name="partitaIVA" id="partitaIVA" value="%{partitaIVA}" maxlength="16" size="60" 
									 readonly="%{#bloccoEdit}"  cssClass="%{#classBlocco}" aria-required="true"/>
		</div>
	</div>
	


<%-- 13/10/2017 questo elemento viene disabilitato
	<div class="fieldset-row">
		<div class="label">
			<label for="microPiccolaMediaImpresa">Micro, piccola o media impresa? : </label>
		</div>
		<div class="element">
			<s:select list="maps['sino']" name="microPiccolaMediaImpresa" id="microPiccolaMediaImpresa"  
								headerKey="" headerValue="" value="%{microPiccolaMediaImpresa}" ></s:select>
		</div>
	</div>
--%>

	<div class="fieldset-row">
		<div class="label">
			<label><wp:i18n key="LABEL_SEDE_LEGALE" /> : </label>
		</div>
		<div class="element">
			<div class="contents-group">
				<label for="indirizzoSedeLegale"><wp:i18n key="LABEL_INDIRIZZO" /> : <span class="required-field">*</span></label>
				<s:textfield name="indirizzoSedeLegale" id="indirizzoSedeLegale" value="%{indirizzoSedeLegale}" 
										 size="40" maxlength="60" aria-required="true"/>
				<label for="numCivicoSedeLegale"><wp:i18n key="LABEL_NUM_CIVICO" /> : <span class="required-field">*</span></label>
				<s:textfield name="numCivicoSedeLegale" id="numCivicoSedeLegale" value="%{numCivicoSedeLegale}" 
										 size="4" maxlength="10" aria-required="true"/>
			</div>
			<div class="contents-group">
				<label for="capSedeLegale" class="capSedeLegale"><wp:i18n key="LABEL_CAP" /> : <span id="obbCapSedeLegale" class="required-field">*</span></label>
				<s:textfield name="capSedeLegale" id="capSedeLegale" value="%{capSedeLegale}" cssClass="capSedeLegale" 
										 size="5" maxlength="5" aria-required="true"/>
				<label for="comuneSedeLegale"><wp:i18n key="LABEL_COMUNE" /> : <span class="required-field">*</span></label>
				<s:textfield name="comuneSedeLegale" id="comuneSedeLegale" value="%{comuneSedeLegale}" 
										 size="30" maxlength="100" aria-required="true"/>
			</div>
			<div class="contents-group provinciaSedeLegale">
				<label for="provinciaSedeLegale"><wp:i18n key="LABEL_PROVINCIA" /> : <span id="obbCapSedeLegale" class="required-field">*</span> </label>
				<wp:i18n key="OPT_CHOOSE_PROVINCIA" var="headerValueProvincia" />
				<s:select list="maps['province']" name="provinciaSedeLegale" id="provinciaSedeLegale" value="%{provinciaSedeLegale}"
									headerKey="" headerValue="%{#attr.headerValueProvincia}" aria-required="true" >
				</s:select>
				<div class="note"><wp:i18n key="LABEL_NOTA_PROVINCIA_SOLO_PER_ITALIA" /></div>
			</div>
			<div class="contents-group nazioneSedeLegale">
				<label for="nazioneSedeLegale"><wp:i18n key="LABEL_NAZIONE" /> : <span class="required-field">*</span></label>
				<wp:i18n key="OPT_CHOOSE_NAZIONE" var="headerValueNazione" />
				<s:select list="maps['nazioni']" name="nazioneSedeLegale" id="nazioneSedeLegale" value="{nazioneSedeLegale}"
									headerKey="" headerValue="%{#attr.headerValueNazione}" aria-required="true"></s:select>
			</div>
		</div>
	</div>

	<div class="fieldset-row">
		<div class="label">
			<label for="sitoWeb"><wp:i18n key="LABEL_SITO_WEB" /> : </label>
		</div>
		<div class="element">
				<s:textfield name="sitoWeb" id="sitoWeb" value="%{sitoWeb}" size="60" maxlength="60" />
		</div>
	</div>

	<div class="fieldset-row last-row">
		<div class="label">
			<label><wp:i18n key="LABEL_RECAPITI" /> : </label>
		</div>
		<div class="element">
			<div class="contents-group">
				<label for="telefonoRecapito"><wp:i18n key="LABEL_TELEFONO" /> : <span class="required-field">*</span></label>
				<s:textfield name="telefonoRecapito" id="telefonoRecapito" value="%{telefonoRecapito}" 
										 size="20" maxlength="50" aria-required="true" />
				<label for="faxRecapito"><wp:i18n key="LABEL_FAX" /> : <c:if test="${obblFaxRecapito}"><span class="required-field">*</span></c:if></label>			
				<%-- <s:textfield name="faxRecapito" id="faxRecapito" value="%{faxRecapito}" size="20" maxlength="20" /> --%>
				<input type="text" name="faxRecapito" id="faxRecapito" value="${faxRecapito}" size="20" maxlength="20"
										<c:if test="${obblFaxRecapito}"> aria-required="true" </c:if> />
				<input type="hidden" name="obblFaxRecapito" value="${obblFaxRecapito}"/>
			</div>
			<div class="contents-group">
				<label for="cellulareRecapito"><wp:i18n key="LABEL_CELLULARE" /> : <c:if test="${obblCellulareRecapito}"><span class="required-field">*</span></c:if></label>
				<input type="text" name="cellulareRecapito" id="cellulareRecapito" value="${cellulareRecapito}" size="20" maxlength="15"
										<c:if test="${obblCellulareRecapito}"> aria-required="true" </c:if> />
				<input type="hidden" name="obblCellulareRecapito" value="${obblCellulareRecapito}"/>
			</div>
			<div class="contents-group">
				<input type="hidden" name="visEmailRecapito" value="${visEmailRecapito}"/>
				<c:choose>
					<c:when test="${visEmailRecapito}">
						<label for="emailRecapito"><wp:i18n key="LABEL_EMAIL" /> : <c:if test="${obblEmailRecapito}"><span class="required-field">*</span></c:if></label>
						<input type="text" name="emailRecapito" id="emailRecapito" value="${emailRecapito}" 
												 size="26" maxlength="60" <c:if test="${obblEmailRecapito}"> aria-required="true" </c:if> />
						<br/>											
						<label for="emailRecapitoConferma"><wp:i18n key="LABEL_EMAIL_CONFIRM" /> : <c:if test="${obblEmailRecapito}"><span class="required-field">*</span></c:if></label>
						<input type="text" name="emailRecapitoConferma" id="emailRecapitoConferma" value="${emailRecapitoConferma}" 
												 size="26" maxlength="60" <c:if test="${obblEmailRecapito}"> aria-required="true" </c:if> />
					</c:when>
					<c:otherwise>
						<input type="hidden"  name="emailRecapito" id="emailRecapito" value=""/>
						<input type="hidden"  name="emailRecapitoConferma" id="emailRecapitoConferma" value=""/>
					</c:otherwise>
				</c:choose>
			</div>				
			<div class="contents-group">				
				<label for="emailPECRecapito"><wp:i18n key="LABEL_PEC" /> : <c:if test="${obblEmailPECRecapito}"><span id="obbPec" class="required-field">*</span></c:if></label>
				<input type="text" name="emailPECRecapito" id="emailPECRecapito" value="${emailPECRecapito}" 
										 size="26" maxlength="60" <c:if test="${obblEmailPECRecapito}"> aria-required="true" </c:if> />
				<br/>
				<label for="emailPECRecapitoConferma"><wp:i18n key="LABEL_PEC_CONFIRM" /> : <c:if test="${obblEmailPECRecapito}"><span id="obbPec" class="required-field">*</span></c:if></label>
				<input type="text" name="emailPECRecapitoConferma" id="emailPECRecapitoConferma" value="${emailPECRecapitoConferma}" 
										 size="26" maxlength="60" <c:if test="${obblEmailPECRecapito}"> aria-required="true" </c:if> />
				<input type="hidden" name="obblEmailRecapito" value="${obblEmailRecapito}"/>
				<input type="hidden" name="obblEmailPECRecapito" value="${obblEmailPECRecapito}"/>
				<input type="hidden" name="obblPECEstero" value="${obblPECEstero}"/>
			</div>
			<div class="note">
				<wp:i18n key="DATI_IMPRESA_INFO_PEC" />
			</div>
			<div class="note">
				<wp:i18n key="DATI_IMPRESA_INFO_PEC2" />
			</div>
		</div>
	</div>
</fieldset>

<%-- DISABLE CUT/COPY AND PASTE ON FIELD --%>
<script type="text/javascript">
$(document).ready(function() {
    $('#emailRecapitoConferma').bind("cut copy paste", function(e) {
        e.preventDefault();
        $('#emailRecapitoConferma').bind("contextmenu", function(e) {
            e.preventDefault();
        });
    });
    $('#emailPECRecapitoConferma').bind("cut copy paste", function(e) {
        e.preventDefault();
        $('#emailPECRecapitoConferma').bind("contextmenu", function(e) {
            e.preventDefault();
        });
    });
});
</script>

<%-- gestione CAP per nazione estera --%>
<script type="text/javascript">
//<![CDATA[

var optEmailPECRecapitoObbligatoria = <c:out value='${obblEmailPECRecapito}'/>;
var optPECEsteroObbligatoria = <c:out value='${obblPECEstero}'/>;

	$(document).ready(function() {

		ambitoTerritorialeChange();
		nazioneSedeLegaleChange();
	
		$('[id="ambitoTerritoriale"]').change(function() {
			ambitoTerritorialeChange();
		});
		
		$('[id="nazioneSedeLegale"]').change(function() {
			nazioneSedeLegaleChange();
		});
	});

	function ambitoTerritorialeChange() {
 		var newval = $('[id="ambitoTerritoriale"]').val();
 		if(newval == "1") {
 			// operatore economico italiano
 			$('[id="nazioneSedeLegale"] option[value="Italia"]').prop('selected', true);
 			$('.nazioneSedeLegale').hide();
 			$('.lblCodiceFiscale').text("<wp:i18n key='LABEL_CODICE_FISCALE'/>");
 			$('.partitaIVA').show();
 			$('.capSedeLegale').show();
 			$('.provinciaSedeLegale').show();
 		
		} else {
			// operatore economico UE/extra UE
			var naz = $('[id="nazioneSedeLegale"] option:selected').val();
 			if(naz.toUpperCase() == "ITALIA") {
 				$('[id="nazioneSedeLegale"]').val("");
 			}
			$('.nazioneSedeLegale').show();
			$('.lblCodiceFiscale').text("<wp:i18n key='LABEL_IDENTIFICATIVO_FISCALE_ESTERO'/>");
			//$('#capSedeLegale').val("");
			$('#provinciaSedeLegale').val("");
			$('.partitaIVA').hide();
			$('.capSedeLegale').hide();
			$('.provinciaSedeLegale').hide();
		}
	}

	function nazioneSedeLegaleChange() {
 		var newval = $('[id="nazioneSedeLegale"] option:selected').val();
 		var oldval = $('[id="nazioneSedeLegale"]').data("value");
 		$('[id="nazioneSedeLegale"]').data("value", newval);
 		if(newval.toUpperCase() == "ITALIA") {
			$('#obbCapSedeLegale').show();
		} else {
			$('#obbCapSedeLegale').show();
		} 
		if(newval != oldval && oldval != null) {
			$('[id="capSedeLegale"]').val("");
 		}
		
		// pec nazione estero facoltativa
		$('[id="obbPec"]').hide();
		if(optEmailPECRecapitoObbligatoria) {
			if(newval.toUpperCase() != "ITALIA") {
				// nazione estero
				if(optPECEsteroObbligatoria) {
					$('[id="obbPec"]').show();
				} else {
					$('[id="obbPec"]').hide();
				} 	
			} else {
				// nazione ITALIA
				$('[id="obbPec"]').show();
			}
		}
	}
//]]>
</script>
