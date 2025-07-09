<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>

<s:set var="sessionId">${param.sessionIdObj}</s:set>

<s:set var="helper" value="%{#session[#sessionId].bustaPartecipazione.helper}" />
<s:set var="isGara" value="1" />
<c:choose>
	<c:when test="${param.namespace == 'IscrAlbo'}">
		<s:set var="helper" value="%{#session[#sessionId]}" />
		<s:set var="isGara" value="0" />
	</c:when>
</c:choose>

<%-- Tipologia di evento (1=partecipazione gara, 2=invio offerta, 3=aggiornamento dati art.48) --%>
<s:set var="domandaPartecipazione" value="%{#helper.tipoEvento == 1}" />
<s:set var="invioOfferta" value="%{#helper.tipoEvento == 2}" />

<s:set var="RTIEditabile" value="%{#helper.editRTI}" />
<s:if test="%{#helper.plicoUnicoOfferteDistinte}">
	<s:set var="RTIEditabile" value="0" />
</s:if>


<!DOCTYPE s:set PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<script>
<!--//--><![CDATA[//><!--
	$(document).ready(function() {

		isRtiChecked();

		// gestisci il click sul si no nella
		$("input[name=rti]").on("click", function() {
			isRtiChecked();
		});
	});

	function isRtiChecked() {

		var editRTI = <s:if test="%{#RTIEditabile && !readOnly}">true</s:if><s:else>false</s:else>;
		var rti = false;
		if(editRTI) {
			rti = ($("input[name=rti]:checked").val() == 1);
		} else {
			rti = <s:if test="%{rti == 1}">true</s:if><s:else>false</s:else>;
		}
		var step = $("#step_componenti");
		var consorzio = <s:if test="%{#helper.impresa.consorzio && !concProgNegoziata}">true</s:if><s:else>false</s:else>;
		var garaRistretta = <s:if test="%{#helper.iterGara == 2 || #helper.iterGara == 4}">true</s:if><s:else>false</s:else>;
		var domandaPartecipazione = <s:if test="%{#helper.tipoEvento == 1}">true</s:if><s:else>false</s:else>;
		var consorziateEsecutriciPresenti = <s:if test="%{#helper.consorziateEsecutriciPresenti == true}">true</s:if><s:else>false</s:else>;
		
		var esecutrici = (consorzio && !garaRistretta) ||
						 (consorzio && garaRistretta && !domandaPartecipazione);

//		var divTipoRaggruppamento = $("#divTipoRaggr");
		var divDen = $("#divDen");
		var editDenRti = <s:if test="%{#helper.DenominazioneRTIReadonly}">false</s:if><s:else>true</s:else>;

		if (rti) {
			step.find("div").text('<wp:i18n key="LABEL_COMPONENTI_RAGGRUPPAMENTO" />');
			step.text('<wp:i18n key="LABEL_COMPONENTI_RAGGRUPPAMENTO" />');
			step.show();
//			divTipoRaggruppamento.show();
			divDen.show();
			$("#denominazioneRequired").show();
			$("#denominazioneRTI").prop("aria-required='true'");
			$("#denominazioneRTI").prop("readonly='" + editDenRti + "'");
//			divDen.find(".label").children().first().append('<span class="required-field">*</span>');
		} else if (esecutrici) {
			step.find("div").text('<wp:i18n key="LABEL_CONSORZIATE_ESECUTRICI" />');
			step.text('<wp:i18n key="LABEL_CONSORZIATE_ESECUTRICI" />');
			step.show();
//			divTipoRaggruppamento.hide();
//			$("#tipoRaggruppamento").val("");
			divDen.hide();
			$("#denominazioneRequired").hide();
			$("#denominazioneRTI").val("");
			$("#denominazioneRTI").prop("aria-required='false'");
//			divDen.find(".label").find('span').remove(".required-field");
		} else {
			// singola
			step.hide();
//			divTipoRaggruppamento.hide();
//			$("#tipoRaggruppamento").val("");
			divDen.hide();
			$("#denominazioneRequired").hide();
			$("#denominazioneRTI").val("");
			$("#denominazioneRTI").prop("aria-required='false'");
//			divDen.find(".label").find('span').remove(".required-field");
		}
	}
//--><!]]>
</script>

<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/skip_form_buttons.jsp" />

<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/mandatory_fields_message.jsp" />

<fieldset>
	<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key='LABEL_FORMA_DI_PARTECIPAZIONE'/></legend>

	<div id="divRti" class="fieldset-row first-row">
		<div class="label">
			<label><wp:i18n key='LABEL_PARTECIPA_COME_MANDATARIA_RAGGRUPPAMENTO'/> <span class="required-field">*</span></label>
		</div>
		<div class="element-orizontal">
			<s:if test="%{#RTIEditabile && !readOnly}">
				<input type="radio" name="rti" id="rti_yes" value="1" <s:if test="%{rti.intValue() == 1}">checked="checked"</s:if> /><label for="rti_yes"><wp:i18n key='LABEL_YES'/></label>
				&nbsp;
				<input type="radio" name="rti" id="rti_no" value="0" <s:if test="%{rti.intValue() == 0}">checked="checked"</s:if> /><label for="rti_no"><wp:i18n key='LABEL_NO'/></label>
			</s:if>
			<s:else>
				<s:if test="%{rti}"><wp:i18n key='LABEL_YES'/></s:if><s:else><wp:i18n key='LABEL_NO'/></s:else>
				<s:hidden name="rti" value="%{rti}" />
			</s:else>
		
			<%--
			<s:if test="plicoImpresaSingolaPresente">
				<div class="note">
					<s:if test="%{#domandaPartecipazione}">
						<wp:i18n key="LABEL_DOMANDA_IMPRESA_SINGOLA_PRESENTE" />
					</s:if>
					<s:else>
						<wp:i18n key="LABEL_OFFERTA_IMPRESA_SINGOLA_PRESENTE" />
					</s:else>
				</div>
			</s:if>
			 --%>
		</div>
	</div>
	
<%--
	<div id="divTipoRaggr" class="fieldset-row">
		<div class="label">
			<label><wp:i18n key='LABEL_TIPO_RAGGRUPPAMENTO'/> : <span class="required-field">*</span></label>
		</div>
		<div class="element">
			<span>
				<s:select list="maps['tipiRaggruppamento']" name="tipoRaggruppamento" id="tipoRaggruppamento" value="%{#helper.tipoRaggruppamento}" 
							headerKey="" headerValue="" aria-required="true">
				</s:select>
			</span>
		</div>
	</div>
--%>

	<div id="divDen" class="fieldset-row">
		<div class="label">
			<label for="denominazioneRTI" ><wp:i18n key='LABEL_DENOMINAZIONE_RAGGRUPPAMENTO_TEMPORANEO'/> : <span id="denominazioneRequired" class="required-field">*</span></label>
		</div>
		<div class="element">
			<c:set var="requiredRti"><s:property value="%{rti == 1}"/></c:set>
			<s:textfield name="denominazioneRTI" id="denominazioneRTI" value="%{denominazioneRTI}" 
						 size="60" maxlength="2000" readonly="%{#helper.denominazioneRTIReadonly || readOnly}" cssClass="%{#classBlocco}"
						 aria-required="${requiredRti}"/>
		</div>
	</div>

	<s:if test="%{#invioOfferta && #isGara}">
		<div class="fieldset-row last-row">
			<div class="label">
				<label for="codiceCNEL" ><wp:i18n key='LABEL_CODICE_CNEL'/> : <span class="required-field">*</span></label>
			</div>
			<div class="element">
				<s:textfield name="codiceCNEL" id="codiceCNEL" value="%{codiceCNEL}" size="4" maxlength="4" />
				<div class="note">
					<wp:i18n key='LABEL_NOTE_CNEL_OFFERTA'/>
				</div>
			</div>
		</div>
	</s:if>

</fieldset>