<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<script type="text/javascript">
<!--//--><![CDATA[//><!--
	$(document).ready(function() {
		
		//ricavo informazioni sul cliente dell'utente
		var nVer = navigator.appVersion;
		var nAgt = navigator.userAgent;
		var browserName = navigator.appName;
		var fullVersion = '' + parseFloat(navigator.appVersion);
		var majorVersion = parseInt(navigator.appVersion, 10);
		var nameOffset, verOffset, ix;

		if ((verOffset = nAgt.indexOf("Opera")) != -1) {
			// In Opera, the true version is after "Opera" or after "Version"
			browserName = "Opera";
			fullVersion = nAgt.substring(verOffset + 6);
			if ((verOffset = nAgt.indexOf("Version")) != -1) {
				fullVersion = nAgt.substring(verOffset + 8);
			}
		} else if ((verOffset = nAgt.indexOf("MSIE")) != -1) {
			// In MSIE, the true version is after "MSIE" in userAgent
			browserName = "Microsoft Internet Explorer";
			fullVersion = nAgt.substring(verOffset + 5);
		} else if ((verOffset = nAgt.indexOf("Chrome")) != -1) {
			// In Chrome, the true version is after "Chrome"
			browserName = "Chrome";
			fullVersion = nAgt.substring(verOffset + 7);
		} else if ((verOffset = nAgt.indexOf("Safari")) != -1) {
			// In Safari, the true version is after "Safari" or after "Version"
			browserName = "Safari";
			fullVersion = nAgt.substring(verOffset + 7);
			if ((verOffset = nAgt.indexOf("Version")) != -1) {
				fullVersion = nAgt.substring(verOffset + 8);
			}
		} else if ((verOffset = nAgt.indexOf("Firefox")) != -1) {
			// In Firefox, the true version is after "Firefox"
			browserName = "Firefox";
			fullVersion = nAgt.substring(verOffset + 8);
		} else if ((nameOffset = nAgt.lastIndexOf(' ') + 1) <
						(verOffset = nAgt.lastIndexOf('/'))) {
			// In most other browsers, "name/version" is at the end of userAgent
			browserName = nAgt.substring(nameOffset, verOffset);
			fullVersion = nAgt.substring(verOffset + 1);
			if (browserName.toLowerCase() == browserName.toUpperCase()) {
				browserName = navigator.appName;
			}
		}
		// trim the fullVersion string at semicolon/space if present
		if ((ix = fullVersion.indexOf(";")) != -1) {
			fullVersion = fullVersion.substring(0, ix);
		}
		if ((ix = fullVersion.indexOf(" ")) != -1) {
			fullVersion = fullVersion.substring(0, ix);
		}

		majorVersion = parseInt('' + fullVersion, 10);
		if (isNaN(majorVersion)) {
			fullVersion = '' + parseFloat(navigator.appVersion);
			majorVersion = parseInt(navigator.appVersion, 10);
		}

		var result = 'Browser name  = ' + browserName + '\r\n'
						+ 'Full version  = ' + fullVersion + '\r\n'
						+ 'Major version = ' + majorVersion + '\r\n'
						+ 'navigator.appName = ' + navigator.appName + '\r\n'
						+ 'navigator.userAgent = ' + navigator.userAgent;

		$("#infoSystem").val(result);

		$("#request_help_form").show();
	});
//--><!]]>
</script>

<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/send_blockUI.jsp" />

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2><wp:i18n key="TITLE_PAGE_ASSISTENZA_TECNICA" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_ASSISTENZA_TECNICA"/>
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/field_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />

	<form id="request_help_form" 
		class="request_help_form" 
		action='<wp:action path="/ExtStr2/do/FrontEnd/Assistenza/help.action" />'
		method="post" 
		enctype="multipart/form-data">
		
		<fieldset>
			<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_ASSISTENZA_TECNICA_INS_RICHIESTA" /></legend>
			 
		<input type="hidden" name="infoSystem" id="infoSystem" value=""/>
		
		<label class="noscreen">
			<s:textfield name="address" value="" maxlength="50" title="Parametro di sicurezza da non compilare" aria-required="true"/>
			<span class="required-field">*</span>
		</label>
		
		<c:if test="${not empty codiceStazioneAppaltante}">	
			<div class="fieldset-row first-row">
				<div class="label">
					<label><wp:i18n key="LABEL_STAZIONE_APPALTANTE" /></label> : 
				</div>
				<div class="element">
					${descStazioneAppaltante} (${codiceFiscaleStazioneAppaltante}) 
					<%-- ${codiceStazioneAppaltante} --%> 
				</div>
			</div>
		</c:if>
		
		<div class="fieldset-row <c:if test="${codiceStazioneAppaltante}">first-row</c:if>">
			<div class="label">
				<label for="ente"><wp:i18n key="LABEL_RAGIONE_SOCIALE" /></label> : <span class="required-field">*</span>
			</div>
			<div class="element">
				<s:textfield name="ente" maxlength="50" />
			</div>
		</div>

		<div class="fieldset-row">
			<div class="label">
				<label for="referente"><wp:i18n key="LABEL_ASSISTENZA_TECNICA_REFERENTE" /></label> : <span class="required-field">*</span>
			</div>
			<div class="element">
				<s:textfield name="referente" maxlength="50" />
			</div>
		</div>

		<div class="fieldset-row">
			<div class="label">
				<label for="email"><wp:i18n key="LABEL_EMAIL" /></label> : <span class="required-field">*</span>
			</div>
			<div class="element">
				<s:textfield name="email" maxlength="50" />
			</div>
		</div>

		<div class="fieldset-row">
			<div class="label">
				<label for="telefono"><wp:i18n key="LABEL_TELEFONO" /> : </label>
			</div>
			<div class="element">
				<s:textfield name="telefono" maxlength="16" size="20" />
			</div>
		</div>

		<div class="fieldset-row">
			<div class="label">
				<label for="tipoRichiesta"><wp:i18n key="LABEL_ASSISTENZA_TECNICA_TIPOLOGIA" /></label> : <span class="required-field">*</span>
			</div>
			<div class="element">
				<wp:i18n key="OPT_ASSISTENZA_TECNICA_CHOOSE_TIPOLOGIA" var="attr.headerValueTipoRichiesta" />
				<s:select name="tipoRichiesta" list="maps['tipologieAssistenza']" 
									headerKey="" headerValue="%{#attr.headerValueTipoRichiesta}" >
				</s:select>
			</div>
		</div>

		<div class="fieldset-row">
			<div class="label">
				<label for="descrizione"><wp:i18n key="LABEL_ASSISTENZA_TECNICA_DESCRIZIONE" /> : </label>
			</div>
			<div class="element">
				<s:textarea name="descrizione" rows="5" cols="50" />
			</div>
		</div>

		<div class="fieldset-row">
			<div class="label">
				<label for="allegato"><wp:i18n key="LABEL_ASSISTENZA_TECNICA_ALLEGA_FILE" /> : </label>
			</div>
			<div class="element">
				<input type="file" name="allegato" id="allegato" />
				<div class="note">
				<wp:i18n key="LABEL_MAX_FILE_SIZE" /> <strong><s:property value="%{limiteUploadFile}" /></strong> KB.
				</div>
			</div>
		</div>
			
		<div class="fieldset-row last-row">
			<div class="label">
				<label for="captchaConferma"><wp:i18n key="LABEL_ASSISTENZA_TECNICA_CAPTCHA" /></label> : <span class="required-field">*</span>
			</div>
			<div class="element">
				<s:url id="getcaptcha" namespace="/captcha" action="getimage" />
				<img id="captchaImg" src="${getcaptcha}" alt="Captcha Image" border="1" />
    			<input type="text" name="captchaConferma" id="captchaConferma" />
			</div>
		</div>

		<div class="azioni">
			<wp:i18n key="BUTTON_SEND" var="valueSendButton" />
			<s:submit value="%{#attr.valueSendButton}" cssClass="button block-ui" />
		</div>
		
		</fieldset>
	</form>

	<c:set var="visualizzaRiferimenti" value="false"/>
	<c:set var="numeroRiferimenti" value="0"/>
	<c:if test="${! empty telefonoAssistenza}">
		<c:set var="visualizzaRiferimenti" value="true"/>
		<c:set var="numeroRiferimenti" value="${numeroRiferimenti + 1}"/>
	</c:if>
	<c:if test="${! empty mailAssistenza}">
		<c:set var="visualizzaRiferimenti" value="true"/>
		<c:set var="numeroRiferimenti" value="${numeroRiferimenti + 1}"/>
	</c:if>
	<c:if test="${visualizzaRiferimenti}">
		<fieldset>
			<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_ASSISTENZA_TECNICA_RIFERIMENTI" /></legend>
			<wp:i18n key="LABEL_ASSISTENZA_TECNICA_RIFERIMENTI_TESTO" />
			
			<c:if test="${! empty telefonoAssistenza}">
				<div class="fieldset-row first-row <c:if test='${numeroRiferimenti eq 1}'>last-row</c:if>">
					<div class="label">
						<label><wp:i18n key="LABEL_ASSISTENZA_TECNICA_TELEFONO_CALL_CENTER" /> :</label>
					</div>
					<div class="element">
						<s:property value="telefonoAssistenza"/>
					</div>
				</div>
			</c:if>
			<c:if test="${! empty mailAssistenza}">
				<div class="fieldset-row  <c:if test='${numeroRiferimenti eq 1}'>first-row</c:if> last-row">
					<div class="label">
						<label><wp:i18n key="LABEL_EMAIL" />:</label>
					</div>
					<div class="element">
						<a href="mailto:<s:property value='mailAssistenza'/>"><s:property value='mailAssistenza'/></a>
					</div>
				</div>
			</c:if>
		</fieldset>
	</c:if>

	<wp:i18n key="LABEL_ASSISTENZA_TECNICA_INFO_EXTRA" var="infoExtra" />
	<c:if test="${(not fn:startsWith(infoExtra,'<!--')) && (infoExtra ne '&nbsp;')}">
	<fieldset>
			<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_ASSISTENZA_TECNICA_OPERATIVITA" /></legend>
			<c:out value="${infoExtra}" escapeXml="false"></c:out>
	</fieldset>
	</c:if>
	
</div>
