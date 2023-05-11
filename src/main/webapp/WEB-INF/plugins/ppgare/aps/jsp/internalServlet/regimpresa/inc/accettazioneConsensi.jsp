<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>

<es:checkCustomization var="visConsensoPrivacy" objectId="REG-IMPRESA" attribute="PRIVACY" feature="VIS" />
<es:checkCustomization var="bloccaLoginUgualeCodiceFiscale" objectId="REG-IMPRESA" attribute="LOGINCF" feature="ACT" />
<es:checkCustomization var="registrazioneManuale" objectId="REG-IMPRESA" attribute="VERIFICAMANUALE" feature="ACT" />
<es:checkCustomization var="consensoExtra" objectId="REG-IMPRESA" attribute="CONSENSO-EXTRA" feature="VIS" />
<es:getAppParam var="pubblicaCondizioniUsoStd" name="privacy.pubblicaCondizioniUsoStd" /> 	

<c:set var="showDefaultTermOfUse" value="${pubblicaCondizioniUsoStd == 1}" />

<%--
session.accountSSO=<s:property value="%{#session.accountSSO}"/><br/>
session.accountSSO.nome=<s:property value="%{#session.accountSSO.nome}"/><br/>
session.accountSSO.cognome=<s:property value="%{#session.accountSSO.cognome}"/><br/>
session.accountSSO.nome.length()=<s:property value="%{#session.accountSSO.nome.length()}"/><br/>
session.accountSSO.cognome.length()=<s:property value="%{#session.accountSSO.cognome.length()}"/><br/>
helper.liberoProfessionista=<s:property value="%{#helper.liberoProfessionista}"/><br/>
helper.datiPrincipaliImpresa.ragioneSociale=<s:property value="%{#helper.datiPrincipaliImpresa.ragioneSociale}"/><br/>
--%>	


<%-- SOGGETTO RICHIEDENTE --%>
<p>
	<wp:i18n key="LABEL_REGISTRA_OE_IL_SOTTOSCRITTO" />
	<s:if test="%{#session.accountSSO != null && 
				  (#session.accountSSO.nome != null && #session.accountSSO.nome.length() > 0) && 
				  (#session.accountSSO.cognome != null && #session.accountSSO.cognome.length() > 0)}">
		<%-- SINGLE SIGN ON --%>
		${sessionScope.accountSSO.nome} ${sessionScope.accountSSO.cognome} (${sessionScope.accountSSO.login}),				
		
		<s:hidden name="soggettoRichiedente" id="soggettoRichiedente" 
			value="%{#session.accountSSO.nome} %{#session.accountSSO.cognome} (%{#session.accountSSO.login})" />

	</s:if>
	<s:elseif test="%{#helper.liberoProfessionista}">				
		<%-- LIBERO PROFESSIONISTA --%>		
		<%-- <s:property value="%{#helper.datiPrincipaliImpresa.ragioneSociale}"/> (<s:property value="%{#helper.datiPrincipaliImpresa.codiceFiscale}"/>), --%>
		<s:property value="%{#helper.altriDatiAnagraficiImpresa.cognome}"/> <s:property value="%{#helper.altriDatiAnagraficiImpresa.nome}"/> (<s:property value="%{#helper.datiPrincipaliImpresa.codiceFiscale}"/>),
		<s:hidden name="soggettoRichiedente" id="soggettoRichiedente" 
				value="%{#helper.altriDatiAnagraficiImpresa.cognome} %{#helper.altriDatiAnagraficiImpresa.nome} (%{#helper.datiPrincipaliImpresa.codiceFiscale})"/>
	</s:elseif>
	<s:else>
		<%-- DITTA --%>
		<select id="soggettoRichiedente" name="soggettoRichiedente" title="<wp:i18n key="LABEL_REGISTRA_OE_IL_SOTTOSCRITTO" />">
		
			<option value=''><wp:i18n key="OPT_CHOOSE_REGISTRA_OE_SOGGETTO_RICHIEDENTE" /></option>
		
			<%-- LEGALI RAPPRESENTANTI --%>
			<s:iterator var="item" value="%{#helper.legaliRappresentantiImpresa}" status="status">
				<s:set var="cfpiva">
					<s:if test="%{#item.codiceFiscale != null}"> 
						<s:property value="#item.codiceFiscale"/>
					</s:if>
					<s:else>
						<s:property value="#item.partitaIva"/>
					</s:else>
				</s:set>
				
				<c:set var="value"><s:property value="#item.nome"/> <s:property value="#item.cognome"/> (<s:property value="%{cfpiva}"/>)</c:set>
				<option value="${value}" <c:if test='${soggettoRichiedente == value}'>selected="selected"</c:if> >						
					<s:property value="#item.nome"/> <s:property value="#item.cognome"/> (<s:property value="%{cfpiva}"/>)   
				</option>
			</s:iterator>
			
			<%-- DIRETTORI TECNICI --%>
			<s:iterator var="item" value="%{#helper.direttoriTecniciImpresa}" status="status">
				<s:set var="cfpiva">
					<s:if test="%{#item.codiceFiscale != null}"> 
						<s:property value="#item.codiceFiscale"/>
					</s:if>
					<s:else>
						<s:property value="#item.partitaIva"/>
					</s:else>
				</s:set>
				
				<c:set var="value"><s:property value="#item.nome"/> <s:property value="#item.cognome"/> (<s:property value="%{cfpiva}"/>)</c:set>
				<option value="${value}" <c:if test='${soggettoRichiedente == value}'>selected="selected"</c:if> >
					<s:property value="#item.nome"/> <s:property value="#item.cognome"/> (<s:property value="%{cfpiva}"/>)   
				</option>
			</s:iterator>
			
			<%-- ALTRE CARICHE --%>
			<s:iterator var="item" value="%{#helper.altreCaricheImpresa}" status="status">
				<s:set var="cfpiva">
					<s:if test="%{#item.codiceFiscale != null}"> 
						<s:property value="#item.codiceFiscale"/>
					</s:if>
					<s:else>
						<s:property value="#item.partitaIva"/>
					</s:else>
				</s:set>
				
				<c:set var="value"><s:property value="#item.nome"/> <s:property value="#item.cognome"/> (<s:property value="%{cfpiva}"/>)</c:set>
				<option value="${value}" <c:if test='${soggettoRichiedente == value}'>selected="selected"</c:if> >
					<s:property value="#item.nome"/> <s:property value="#item.cognome"/> (<s:property value="%{cfpiva}"/>)   
				</option>
			</s:iterator>
			
			<%-- COLLABORATORI --%>
			<s:iterator var="item" value="%{#helper.collaboratoriImpresa}" status="status">
				<s:set var="cfpiva">
					<s:if test="%{#item.codiceFiscale != null}"> 
						<s:property value="#item.codiceFiscale"/>
					</s:if>
					<s:else>
						<s:property value="#item.partitaIva"/>
					</s:else>
				</s:set>

				<c:set var="value"><s:property value="#item.nome"/> <s:property value="#item.cognome"/> (<s:property value="%{cfpiva}"/>)</c:set>
				<option value="${value}" <c:if test='${soggettoRichiedente == value}'>selected="selected"</c:if> >
					<s:property value="#item.nome"/> <s:property value="#item.cognome"/> (<s:property value="%{cfpiva}"/>)   
				</option>
			</s:iterator>
		</select>,
		
	</s:else>
</p>

<%-- CONSENSO UTILIZZO PIATTAFORMA --%>
<fieldset>
	<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_REGISTRA_OE_REGOLE_UTILIZZO_PIATTAFORMA" /></legend>

   	<wp:i18n key="LABEL_REGISTRA_OE_SOGGETTO_RICHIEDENTE_DICHIARA_1" />
   	<s:property value="ragioneSociale"/> (<wp:i18n key="LABEL_CODICE_FISCALE_ABBR" /> <s:property value="codiceFiscale"/>) 
   	<wp:i18n key="LABEL_REGISTRA_OE_SOGGETTO_RICHIEDENTE_DICHIARA_2" />
   	<c:choose>
		<c:when test="${showDefaultTermOfUse}">	
			<a id="linkUtilizzoPiattaforma" href="<wp:action path="/ExtStr2/do/FrontEnd/RegistrImpr/openTermOfUse.action" />" target="_blank">[<wp:i18n key="HERE" />]</a>
		</c:when>			
		<c:otherwise>
	   		<a id="linkUtilizzoPiattaforma" href="<wp:resourceURL/>cms/documents/Regole_utilizzo_piattaforma_telematica.pdf" target="_blank">[<wp:i18n key="HERE" />]</a>
		</c:otherwise>
	</c:choose>
   
   	<div class="fieldset-row first-row last-row">
		<div class="label">
			<label><wp:i18n key="LABEL_UTILIZZO_PIATTAFORMA" /> <span class="required-field">*</span></label>
		</div>
		<div class="element">
			<input type="radio" name="utilizzoPiattaforma" id="utilizzoPiattaforma_yes" value="1" <s:if test="%{utilizzoPiattaforma.intValue() == 1}">checked="checked"</s:if> /><label for="utilizzoPiattaforma_yes"><wp:i18n key="LABEL_REGISTRA_OE_ACCETTO" /></label>
			<input type="radio" name="utilizzoPiattaforma" id="utilizzoPiattaforma_no" value="0" <s:if test="%{utilizzoPiattaforma.intValue() == 0}">checked="checked"</s:if> /><label for="utilizzoPiattaforma_no"><wp:i18n key="LABEL_REGISTRA_OE_NON_ACCETTO" /></label>
		</div>
	</div>
</fieldset>


<c:if test="${consensoExtra}" >
	<fieldset>
		<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_CONSENSO_EXTRA_TITLE" /></legend>
			
		<p><wp:i18n key="LABEL_CONSENSO_EXTRA_NOTA" /></p>
		
		<div class="fieldset-row first-row last-row">
			<div class="label">
				<label><wp:i18n key="LABEL_CONSENSO_EXTRA" /> : <span class="required-field">*</span></label>
			</div>
			<div class="element">
				<input type="radio" name="consensoExtra" id="consensoExtra_yes" value="1" <s:if test="%{consensoExtra.intValue() == 1}">checked="checked"</s:if> /><label for="consensoExtra_yes"><wp:i18n key="LABEL_REGISTRA_OE_ACCETTO" /></label>
				<input type="radio" name="consensoExtra" id="consensoExtra_no" value="0" <s:if test="%{consensoExtra.intValue() == 0}">checked="checked"</s:if> /><label for="consensoExtra_no"><wp:i18n key="LABEL_REGISTRA_OE_NON_ACCETTO" /></label>
			</div>
		</div>
	</fieldset>
</c:if>

<!-- CONSENSO PRIVACY -->
<c:choose>
	<c:when test="${visConsensoPrivacy}">
		<fieldset>
			<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_PRIVACY" /></legend>
			
			<p>
				<wp:i18n key="LABEL_REGISTRA_OE_PRIVACY_NOTA" />
			   	<c:if test="${showDefaultTermOfUse}">
					<wp:i18n key="LABEL_REGISTRA_OE_PRIVACY_LINK" />
					<a id="linkPrivacy" href="<wp:action path="/ExtStr2/do/FrontEnd/RegistrImpr/openDataUsageInfo.action" />" target="_blank">[<wp:i18n key="HERE" />]</a>
				</c:if>
			</p>
			<wp:i18n key="LABEL_REGISTRA_OE_PRIVACY_EXTRA" />
			<div class="fieldset-row first-row last-row">
				<div class="label">
					<label><wp:i18n key="LABEL_PRIVACY" /> <span class="required-field">*</span></label>
				</div>
				<div class="element">
					<c:choose>
						<c:when test="${!showDefaultTermOfUse}">
							<input type="radio" name="privacy" id="privacy_yes" value="1" <s:if test="%{privacy.intValue() == 1}">checked="checked"</s:if> /><label for="privacy_yes"><wp:i18n key="LABEL_REGISTRA_OE_ACCETTO" /></label>
							<input type="radio" name="privacy" id="privacy_no" value="0" <s:if test="%{privacy.intValue() == 0}">checked="checked"</s:if> /><label for="privacy_no"><wp:i18n key="LABEL_REGISTRA_OE_NON_ACCETTO" /></label>
						</c:when>
						<c:otherwise>
							<s:checkbox name="privacy" fieldValue="1" id="privacy_checkbox" />
                            <label for="privacy_checkbox"><wp:i18n key="LABEL_PRESA_VISIONE" /></label>

						</c:otherwise>
					</c:choose>
				</div>
			</div>
		</fieldset>
	</c:when>
	<c:otherwise>
		<input type="hidden" name="privacy" value="1"/>
	</c:otherwise>
</c:choose>

<input type="hidden" name="visConsensoPrivacy" value="${visConsensoPrivacy}"/>

<script type="text/javascript"> 
$(document).ready(function() { 
	var utilizzoPiattaformaChecked = $("input[name='utilizzoPiattaforma']:checked").val(); 
	if (utilizzoPiattaformaChecked == undefined) { 
		$('#utilizzoPiattaforma_yes').attr('disabled', 'disabled'); 
		$('#utilizzoPiattaforma_no').attr('disabled', 'disabled'); 
	}
	<c:if test="${showDefaultTermOfUse}">
	var privacyChecked = $("input[name='privacy_checkbox']:checked").val(); 
	if (privacyChecked == undefined) { 
		$('#privacy_checkbox').attr('disabled', 'true'); 
	}
	</c:if>
});

$('#linkUtilizzoPiattaforma').click(function(){ 
	$('#utilizzoPiattaforma_yes').removeAttr('disabled'); 
	$('#utilizzoPiattaforma_no').removeAttr('disabled'); 
});
<c:if test="${showDefaultTermOfUse}">
$('#linkPrivacy').click(function(){ 
	$('#privacy_checkbox').removeAttr('disabled'); 
});
</c:if>
</script>
