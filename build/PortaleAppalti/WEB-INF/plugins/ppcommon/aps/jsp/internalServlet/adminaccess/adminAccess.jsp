<%@ taglib prefix="wp" uri="aps-core.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<script>
$(document).ready(function(){
	
	// Aggiungo all'url dell'action SPID la motivazione della login
	$('.spid-idp-button-link').click(function( event, a, b) {
		let target = event.target;
		while (target && target.href === undefined) {
			target = target.parentElement;
		}			
		let href = target.href + "&motivazione="+$("#motivazione-spid").val();
		window.location.href = href;
		return false;
	});
	
	// Disabilito i bottoni di submit (sono riabilitati solo se tutti gli input text di sezione sono valorizzati)
	handleMtokenButton();
	handlePortokenButton();
	handleSpidButton();	

	// Listener che aggancia la funzione di controllo abilitazione/ disabilitazione button di submit mtoken
	$('#motivazione-mtoken').on('input',handleMtokenButton);

	// Listener che aggancia la funzione di controllo abilitazione/ disabilitazione button di submit portoken
	$('#mail-portoken').on('input',handlePortokenButton);
	$('#pwd-portoken').on('input',handlePortokenButton);
	$('#motivazione-portoken').on('input',handlePortokenButton);

	// Listener che aggancia la funzione di controllo abilitazione/ disabilitazione button di submit SPID			
	$('#motivazione-spid').on('input',handleSpidButton);

	// Di default le sezioni Portoken, SPID sono nascoste (comandata dal radioButton)
	//$('#admin_mtoken_section').hide();
	$('#admin_spid_section').hide();
	$('#admin_portoken_section').hide();

	// RadioButton che comanda la visualizzazione in mutua esclusione, click su mtoken radio			
	$('#sceltaProvider_mtoken').click(function( event) {
		$('#admin_mtoken_section').show();
		$('#admin_portoken_section').hide();
		$('#admin_spid_section').hide();
	});
	// RadioButton che comanda la visualizzazione in mutua esclusione, click su portoken radio
	$('#sceltaProvider_portoken').click(function( event) {
		$('#admin_mtoken_section').hide();
		$('#admin_portoken_section').show();
		$('#admin_spid_section').hide();
	});
	// RadioButton che comanda la visualizzazione in mutua esclusione, click su spid radio			
	$('#sceltaProvider_spid').click(function( event) {
		$('#admin_mtoken_section').hide();
		$('#admin_portoken_section').hide();
		$('#admin_spid_section').show();
	});
	
});

function handleMtokenButton(){
	let fileValue = $("#certificato").val();
	let certValue = $("#certificatoText").val();
	let motivazioneValue = $("#motivazione-mtoken").val();
	if(((fileValue == undefined || fileValue == null || fileValue =="")&&
	    (certValue == undefined || certValue == null || certValue =="")) ||
	   (motivazioneValue == undefined || motivazioneValue == null || motivazioneValue =="")){
		
		disableButton($("#button-send-mtoken"));
	} else {
		enableButton($("#button-send-mtoken"));
	}
}

function handlePortokenButton(){
	let mailValue = $("#mail-portoken").val();
	let pwdValue = $("#pwd-portoken").val();
	let motivazioneValue = $("#motivazione-portoken").val();
	if((mailValue == undefined || mailValue == null || mailValue =="")||
	   (pwdValue == undefined || pwdValue == null || pwdValue =="")||
	   (motivazioneValue == undefined || motivazioneValue == null || motivazioneValue =="")){
			
		disableButton($("#button-send-portoken"));
	} else {
		enableButton($("#button-send-portoken"));
	}
}

function handleSpidButton(){
	let motivazioneValue = $("#motivazione-spid").val()
	if(motivazioneValue == undefined || motivazioneValue == null || motivazioneValue ==""){
		disableButton($('#admin-spid-button'));
	} else {
		enableButton($("#admin-spid-button"));
	}
}

function disableButton(button){
	button.children().prop('disabled',true);
	button.css({ opacity: 0.6 });
}

function enableButton(button){
	button.children().prop('disabled',false);
	button.css({ opacity: 1 });
}


</script>
<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp">
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>
<div class="portgare-list">
	<h2>
		<wp:i18n key="TITLE_PAGE_ADMIN_ACCESS" />
	</h2>
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_ADMIN_ACCESS" />
	</jsp:include>

	<div class="element">
		<input type="radio" name="sceltaProvider" id="sceltaProvider_mtoken" value="0" checked="checked" />
		<label for="sceltaProvider_mtoken"><wp:i18n key="MTOKEN_AUTHENTICATION" /></label>
		<input type="radio" name="sceltaProvider" id="sceltaProvider_portoken" value="1" />
		<label for="sceltaProvider_portoken"><wp:i18n key="PORTOKEN_AUTHENTICATION" /></label>
		<input type="radio" name="sceltaProvider" id="sceltaProvider_spid" value="2" />
		<label for="sceltaProvider_spid"><wp:i18n key="SPID_AUTHENTICATION" /></label>		
	</div>

	<div id="admin_mtoken_section">
		<form action="<wp:action path="/ExtStr2/do/FrontEnd/AdminAccess/loginMtoken.action" />" 
				method="post" enctype="multipart/form-data" accept-charset="UTF-8">
			<fieldset>
				<legend>
					<span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span>
					<wp:i18n key="MTOKEN_AUTHENTICATION" />
				</legend>
				
				<div class="fieldset-row">
					<div class="label">
						<label for="certificato"><wp:i18n key="LABEL_ALLEGA_CERTIFICATO" /> : </label>
					</div>
					<div class="element">
						<input type="file" name="certificato" id="certificato" tabindex="0" />
					</div>
				</div>
				<div class="fieldset-row">
					<div class="label">
						<label for="certificatoText"><wp:i18n key="LABEL_CERTIFICATO" /> : </label>
					</div>
					<div class="element">
						<s:textarea name="certificatoText" id="certificatoText" rows="24" cols="80" value="" tabindex="1"></s:textarea>
					</div>
				</div>
				<div class="fieldset-row">
					<div class="label">
						<label for="motivazione"><wp:i18n key="LABEL_MOTIVATION" />
						</label> : <span class="required-field">*</span>
					</div>
					<div class="element">
						<s:textfield id="motivazione-mtoken" name="motivazione" maxlength="50" tabindex="2" cssClass="text" />
					</div>
				</div>
				
				<div class="azioni" id="button-send-mtoken">
					<wp:i18n key="BUTTON_SEND" var="valueSendButton" />
					<s:submit cssClass="button block-ui" method="loginMtoken" value="%{#attr.valueSendButton}" />
				</div>
			</fieldset>
		</form>
	</div>
	<div id="admin_portoken_section">
		<form action="<wp:action path="/ExtStr2/do/FrontEnd/AdminAccess/loginPortoken.action" />" method="post">
			<fieldset>
				<legend>
					<span class="noscreen"><wp:i18n key="LABEL_SECTION" />
					</span>
					<wp:i18n key="PORTOKEN_AUTHENTICATION" />
				</legend>
				<div class="fieldset-row">
					<div class="label">
						<label for="domainMail"><wp:i18n key="LABEL_DOMAIN_EMAIL" />
						</label> : <span class="required-field">*</span>
					</div>
					<div class="element">
						<s:textfield id="mail-portoken" name="domainMail" maxlength="50" value=""
							tabindex="1" cssClass="text" /> @maggioli.it
					</div>
				</div>
				<div class="fieldset-row">
					<div class="label">
						<label for="domainPwd"><wp:i18n key="LABEL_DOMAIN_PWD" />
						</label> : <span class="required-field">*</span>
					</div>
					<div class="element">
						<input type="password" id="pwd-portoken" name="domainPwd" maxlength="50" tabindex="2" class="text"/>
					</div>
				</div>
				<div class="fieldset-row">
					<div class="label">
						<label for="motivazione"><wp:i18n key="LABEL_MOTIVATION" />
						</label> : <span class="required-field">*</span>
					</div>
					<div class="element">
						<s:textfield id="motivazione-portoken" name="motivazione" maxlength="50" tabindex="3" cssClass="text" />
					</div>
				</div>
				<div class="azioni" id="button-send-portoken">
					<wp:i18n key="BUTTON_SEND" var="valueSendButton" />
					<s:submit cssClass="button block-ui" method="loginPortoken" value="%{#attr.valueSendButton}" />
				</div>
			</fieldset>
		</form>
	</div>
	<div id="admin_spid_section">
		<fieldset>
			<legend>
				<span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span>
				<wp:i18n key="SPID_AUTHENTICATION" />
			</legend>
			<div class="fieldset-row">
				<div class="label">
					<label for="motivazione"><wp:i18n key="LABEL_MOTIVATION" />
					</label> : <span class="required-field">*</span>
				</div>
				<div class="element">
					<s:textfield id="motivazione-spid" name="motivazione" maxlength="50" cssClass="text" />
				</div>
			</div>
			<div class="fieldset-row" id="admin-spid-button">
				<c:set var="urlSpid">
					<wp:info key="systemParam" paramName="applicationBaseURL" />do/SSO/SpidAdminLogin.action</c:set>
				<jsp:include page="/WEB-INF/aps/jsp/models/inc/spid-button.jsp">
					<jsp:param name="url" value="${urlSpid}" />
				</jsp:include>
			</div>
		</fieldset>
	</div>

</div>

