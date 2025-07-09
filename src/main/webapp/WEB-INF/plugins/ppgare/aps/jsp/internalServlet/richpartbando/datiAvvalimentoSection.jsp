<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>

<s:set var="buste" value="%{#session.dettaglioOffertaGara}"/>
<s:set var="partecipazione" value="%{#buste.bustaPartecipazione.helper}"/>
<s:set var="bustaPrequalifica" value="%{#buste.bustaPrequalifica.helper}"/>
<s:set var="bustaAmministrativa" value="%{#buste.bustaAmministrativa.helper}"/>
<s:set var="bustaTecnica" value="%{#buste.bustaTecnica.helper}"/>
<s:set var="bustaEconomica" value="%{#buste.bustaEconomica.helper}"/>

<s:set var="invioOfferta" value="%{#partecipazione.tipoEvento == 2}" />
<s:set var="garaRistretta" value="%{#dettGara.datiGeneraliGara.iterGara == 2 || #dettGara.datiGeneraliGara.iterGara == 4}" />
<s:set var="dettGara" value="%{#buste.dettaglioGara}"/>
<s:set var="impreseAusiliarie" value="%{#partecipazione.impreseAusiliarie}"/>


<s:hidden id="readOnly" name="readOnly" value="%{readOnly}" />

<%--
<s:hidden id="avvalimento" name="avvalimento" value="%{#partecipazione.avvalimento ? 1 : 0}" />
#partecipazione.impreseAusiliarie=<s:property value="%{#partecipazione.impreseAusiliarie}"/><br/>
#impreseAusiliarie=<s:property value="%{#impreseAusiliarie}"/><br/>
delete=<s:property value="%{delete}"/><br/>
readOnly=<s:property value="%{readOnly}"/><br/>
avvalimento=<s:property value="%{avvalimento}"/><br/>
--%> 

<script type="text/javascript">
<!--//--><![CDATA[//><!--
	var optPartitaIVALiberoProfessionista;
	var codiciLiberoProfessionista;
	var optPartitaIVAImpresaSociale;
	var codiciImpresaSociale;
	var id = "${id}";
		
	<c:set var="cfg" value=""/>
	<c:forEach items="${maps['checkPILiberoProf']}" var="currentItem">
		<c:set var="cfg" value="${currentItem.value}" />
	</c:forEach>
	var optPartitaIVALiberoProfessionista = "<c:out value="${cfg}"/>";

	<c:set var="codici" value="#"/> 
	<c:forEach items="${maps['tipiImpresaLiberoProf']}" var="currentItem">
		<c:set var="codici" value="${codici}${currentItem.key}#" />
	</c:forEach>
	var codiciLiberoProfessionista = "<c:out value="${codici}"/>"; 

	<c:set var="cfg" value=""/>
	<c:forEach items="${maps['checkPIImprSociale']}" var="currentItem">
		<c:set var="cfg" value="${currentItem.value}" />
	</c:forEach>
	var optPartitaIVAImpresaSociale = "<c:out value="${cfg}"/>";

	<c:set var="codici" value="#"/> 
	<c:forEach items="${maps['tipiImpresaSociale']}" var="currentItem">
		<c:set var="codici" value="${codici}${currentItem.key}#" />
	</c:forEach>
	var codiciImpresaSociale = "<c:out value="${codici}"/>"; 
	
	$(document).ready(function() {

		// gestisci la selezione dell'avvalimento
		if(id == null || id.length === 0) { 
			isAvvalimentoChecked();
			$("input[name=avvalimento]").on("click", function() {
				isAvvalimentoChecked();
			});
		}

		// gestisci la selezione ambito territoriale italia/estero
		viewIdFiscaleEstero(false);
		$('#ambitoTerritoriale').on('change', function() {
			viewIdFiscaleEstero(true);
		});
		

		// gestisci la selezione della tipologia 'Libero professionista'
		ivaRequired();
		$('#tipoImpresa').on('change', function() {
			ivaRequired();
		});

	});

	function isAvvalimentoChecked() {
		var avvalimento = ($("input[name=avvalimento]:checked").val() == 1);
		var divImpreseAusiliarie = $("#divImpreseAusiliarie");
		if (avvalimento) {
			divImpreseAusiliarie.show();
		} else {
			divImpreseAusiliarie.hide();
		}
	}
	
	function ivaRequired() {
		var labelIVA = $('#labelIVA');
		labelIVA.find('span').remove(".required-field");
		var codiceCombo = "#"+$('[id="tipoImpresa"] option:selected').val()+"#";
		if(codiciLiberoProfessionista.indexOf(codiceCombo) >= 0 && optPartitaIVALiberoProfessionista=="1" ){
			labelIVA.find('span').remove(".required-field");
		} else if(codiciImpresaSociale.indexOf(codiceCombo) >= 0 && optPartitaIVAImpresaSociale=="1" ){
			labelIVA.find('span').remove(".required-field");
		} else {
			labelIVA.append('<span class="required-field">*</span>');
		}
	}

	function viewIdFiscaleEstero(changeValori) {
		var ambitoTerritoriale = "#"+$('[id="ambitoTerritoriale"] option:selected').val()+"#";
		if(ambitoTerritoriale == "#1#") {
			$('[id="nazione"] option[value="Italia"]').attr("disabled", false);
			if (changeValori) {
				$('[id="nazione"]').val("Italia");
				$('[id="idFiscaleEstero"]').val("");
			}
			$('.clsAmbitoTerIT').show();
			$('.clsAmbitoTerEE').hide();
		} else if(ambitoTerritoriale == "#2#"){
			//$('[id="nazione"]').val("");
			$('[id="nazione"] option[value="Italia"]').attr("disabled", true);
			if (changeValori) {
				$('[id="nazione"]').val("");
				$('[id="codiceFiscale"]').val("");
				$('[id="partitaIVA"]').val("");
			}
			$('.clsAmbitoTerIT').hide();
			$('.clsAmbitoTerEE').show();
		} else {
			$('[id="nazione"]').val("");
			$('[id="nazione"] option[value="Italia"]').attr("disabled", false);
			$('[id="codiceFiscale"]').val("");
			$('[id="partitaIVA"]').val("");
			$('[id="idFiscaleEstero"]').val("");
			$('.clsAmbitoTerIT').hide();
			$('.clsAmbitoTerEE').hide();
		}
	}

	//--><!]]>
</script>



<s:if test="%{delete}">
	<s:set var="impresaAusiliaria" value="%{#impreseAusiliarie.get(idDelete)}"/>
	<p class="question"><wp:i18n key='LABEL_QUESTION_CONFIRM_IMPRESA_AUSILIARIA'/> <s:property value="%{#impresaAusiliaria.ragioneSociale}"/>?</p>
	<div class="azioni">
		<s:hidden name="idDelete" value="%{idDelete}"/>
		<s:hidden id="avvalimento" name="avvalimento" value="1" />
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_confirm_delete.jsp" />
	</div>
</s:if>
<s:else>
	<fieldset>
		<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_RICORSO_AVVALIMENTO" /></legend>
		<div class="fieldset-row first-row" id="divAvvalimento" >
		<div class="label">
			<label><wp:i18n key='LABEL_RICORSO_AVVALIMENTO'/> <span class="required-field">*</span></label>
		</div>
		<div class="element-orizontal">
			<input type="radio" name="avvalimento" id="avvalimento_yes" value="1" <s:if test="%{avvalimento.intValue() == 1}">checked="checked"</s:if> /><label for="rti_yes"><wp:i18n key='LABEL_YES'/></label>
			&nbsp;
			<input type="radio" name="avvalimento" id="avvalimento_no" value="0" <s:if test="%{avvalimento.intValue() == 0}">checked="checked"</s:if> /><label for="rti_no"><wp:i18n key='LABEL_NO'/></label>		
		</div>
	</div>	
	</fieldset>	

	<p class="noscreen">[ <a href="#" ><wp:i18n key='LABEL_SKIP_IMPRESA_AUSILIARIA'/></a> ]
	</p>
	
 	<div id="divImpreseAusiliarie" style="flex-direction: column; align-items: inherit;">
			
		<%-- verifica se esiste almeno un componente con idFiscaleEstero --%>
		<s:set var="idFiscaliEsteroPresenti" value="0" />
		<s:iterator value="#impreseAusiliarie" status="status">
			<s:if test='%{idFiscaleEstero != null}'>
				<s:set var="idFiscaliEsteroPresenti" value="1" />
			</s:if>
		</s:iterator>
	
		<fieldset>
			<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key='LABEL_ELENCO_IMPRESE_AUSILIARIE'/></legend>
			
			<table class="wizard-table" summary="<wp:i18n key='LABEL_ELENCO_IMPRESE_AUSILIARIE'/>">
				<tr>
					<th scope="col"><wp:i18n key="LABEL_RAGIONE_SOCIALE" /></th>
					<th scope="col"><wp:i18n key="LABEL_CODICE_FISCALE" /></th>
					<th scope="col"><wp:i18n key="LABEL_PARTITA_IVA" /></th>
					<s:if test='%{#idFiscaliEsteroPresenti}'>
						<th scope="col"><wp:i18n key="LABEL_IDENTIFICATIVO_FISCALE_ESTERO" /></th>
					</s:if>
					<th scope="col"><wp:i18n key="LABEL_AVVALIMENTO_PER" /></th>
					<s:if test="%{!readOnly}">
						<th scope="col"><wp:i18n key="ACTIONS" /></th>
					</s:if>
				</tr>
				<s:iterator value="%{#impreseAusiliarie}" status="status">
					<tr>
						<td><s:property value="%{ragioneSociale}"/></td>
						<td><s:property value="%{codiceFiscale}"/></td>
						<td><s:property value="%{partitaIVA}"/></td>
						<s:if test='%{#idFiscaliEsteroPresenti}'>
							<td><s:property value="%{idFiscaleEstero}"/></td>
						</s:if>
						<td>
							<s:iterator value="maps['tipiAvvalimento']">
								<s:if test="%{key == avvalimentoPer}">
									<s:property value="%{value}"/><br/>
								</s:if>
							</s:iterator>
						</td>
						<s:if test="%{!readOnly}">
							<td class="azioni">
								<ul>
									<c:choose>
										<c:when test="${skin == 'highcontrast' || skin == 'text'}">
											<li>
												<a href='<wp:action path="/ExtStr2/do/FrontEnd/RichPartBando/modifyImpresaAusiliaria.action"/>&amp;id=${status.index}&amp;ext=${param.ext}'
													 title="<wp:i18n key='LABEL_MODIFICA_IMPRESA_AUSILIARIA'/>"><wp:i18n key='LABEL_MODIFICA_IMPRESA_AUSILIARIA'/></a>
											</li>
											<li>
												<a href='<wp:action path="/ExtStr2/do/FrontEnd/RichPartBando/confirmDeleteImpresaAusiliaria.action"/>&amp;idDelete=${status.index}&amp;ext=${param.ext}'
													 title="<wp:i18n key='LABEL_ELIMINA_IMPRESA_AUSILIARIA'/>"><wp:i18n key='LABEL_ELIMINA_IMPRESA_AUSILIARIA'/></a>
											</li>
										</c:when>
										<c:otherwise>
											<li>
												<a href='<wp:action path="/ExtStr2/do/FrontEnd/RichPartBando/modifyImpresaAusiliaria.action"/>&amp;id=${status.index}&amp;ext=${param.ext}'
													 title="<wp:i18n key='LABEL_MODIFICA_IMPRESA_AUSILIARIA'/>" class="bkg modify"></a>
											</li>
											<li>
												<a href='<wp:action path="/ExtStr2/do/FrontEnd/RichPartBando/confirmDeleteImpresaAusiliaria.action"/>&amp;idDelete=${status.index}&amp;ext=${param.ext}'
													 title="<wp:i18n key='LABEL_ELIMINA_IMPRESA_AUSILIARIA'/>" class="bkg delete"></a>
											</li>
										</c:otherwise>
									</c:choose>
								</ul>
							</td>
						</s:if>
					</tr>
				</s:iterator>
			</table>
		</fieldset>
	
		<p class="noscreen">[ <a href="#" id="formImpresaAusiliaria"><wp:i18n key="SKIP_TO_FORM_BUTTONS"/></a> ]</p>
	
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/mandatory_fields_message.jsp" />
		
		<fieldset>
			<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span>
				<wp:i18n key='LABEL_IMPRESA_AUSILIARIA' var="labelImpresaAusiliaria"/>
				<c:choose>
					<c:when test="${empty id}">
						<wp:i18n key="LABEL_NUOVA" /> ${fn:toLowerCase(attr.labelImpresaAusiliaria)}
					</c:when>
					<c:otherwise>
						<wp:i18n key="BUTTON_EDIT" /> ${fn:toLowerCase(attr.labelImpresaAusiliaria)}
					</c:otherwise>
				</c:choose>
			</legend>
	
			<div class="fieldset-row first-row">
				<div class="label">
					<label for="ragioneSociale"><wp:i18n key="LABEL_RAGIONE_SOCIALE" /> : <span class="required-field">*</span></label>
				</div>
				<div class="element">
					<s:textfield name="ragioneSociale" id="ragioneSociale" value="%{ragioneSociale}"
								 size="60" maxlength="2000" aria-required="true" />
				</div>
			</div>
	
			<div class="fieldset-row">
				<div class="label">
					<label for="tipoImpresa"><wp:i18n key="LABEL_TIPO_IMPRESA" /> : <span class="required-field">*</span></label>
				</div>
				<div class="element">
					<wp:i18n key="OPT_CHOOSE_TIPO_IMPRESA" var="headerValueTipoImpresa" />
					<s:select name="tipoImpresa" id="tipoImpresa" value="{tipoImpresa}"
								list="maps['tipiImpresaIscrAlbo']"  headerKey="" headerValue="%{#attr.headerValueTipoImpresa}"
								aria-required="true" >
					</s:select>
				</div>
			</div>
	
			<div class="fieldset-row">
				<div class="label">
					<label for="ambitoTerritoriale"><wp:i18n key="LABEL_AMBITO_TERRITORIALE" /> : <span class="required-field">*</span></label>
				</div>
				<div class="element">
					<s:select list="maps['ambitoTerritoriale']" name="ambitoTerritoriale" id="ambitoTerritoriale" value="%{ambitoTerritoriale}"
								headerKey="" aria-required="true" >
					</s:select>
				</div>
			</div>
	
			<%-- ambito territoriale impresa Italia --%>
			<div class="fieldset-row clsAmbitoTerIT">
				<div class="label">
					<label for="codiceFiscale"><wp:i18n key="LABEL_CODICE_FISCALE" /> : <span class="required-field">*</span></label>
				</div>
				<div class="element">
					<s:textfield name="codiceFiscale" id="codiceFiscale" value="%{codiceFiscale}"
									maxlength="16" size="20" aria-required="true" />
				</div>
			</div>
	
			<div class="fieldset-row clsAmbitoTerIT <s:if test='%{!#helper.rti || #isIscrizione}'>last-row</s:if> ">
				<div class="label">
					<label id="labelIVA" for="partitaIVA"><wp:i18n key="LABEL_PARTITA_IVA" /> : <span class="required-field">*</span></label>
				</div>
				<div class="element">
					<s:textfield name="partitaIVA" id="partitaIVA" value="%{partitaIVA}"
									maxlength="16" size="20" aria-required="true" />
				</div>
			</div>
	
			<%-- ambito territoriale impresa Estero --%>
			<div class="fieldset-row clsAmbitoTerEE">
				<div class="label">
					<label for="nazione"><wp:i18n key="LABEL_NAZIONE" /> : <span class="required-field">*</span></label>
				</div>
				<div class="element">
					<wp:i18n key="OPT_CHOOSE_NAZIONE" var="headerValueNazione" />
					<s:select list="maps['nazioni']" name="nazione" id="nazione" value="%{nazione}"
								headerKey="" headerValue="%{#attr.headerValueNazione}"
								aria-required="true" >
					</s:select>
				</div>
			</div>
	
			<div class="fieldset-row clsAmbitoTerEE">
				<div class="label">
					<label><wp:i18n key="LABEL_IDENTIFICATIVO_FISCALE_ESTERO" /> : <span class="required-field">*</span></label>
				</div>
				<div class="element">
					<s:textfield name="idFiscaleEstero" id="idFiscaleEstero" value="%{idFiscaleEstero}"
									maxlength="30" size="20" aria-required="true" />
				</div>
			</div>
	
			<div class="fieldset-row last-row">
				<div class="label">
					<label for="avvalimentoPer"><wp:i18n key="LABEL_AVVALIMENTO_PER" /> : <span class="required-field">*</span></label>
				</div>
				<div class="element">
					<%-- <wp:i18n key="OPT_CHOOSE_NAZIONE" var="headerValueNazione" /> --%>
					<s:select list="maps['tipiAvvalimento']" name="avvalimentoPer" id="avvalimentoPer" value="%{avvalimentoPer}"
								headerKey="" headerValue=""
								aria-required="true" >
					</s:select>
				</div>
			</div>
	
			<div class="azioni">
				<c:choose>
					<c:when test="${empty id}">
						<wp:i18n key="BUTTON_ADD" var="valueAddButton" />
						<wp:i18n key="TITLE_ADD_IMPRESA_AUSILIARIA" var="titleAddButton" />
						<s:submit value="%{#attr.valueAddButton}" title="%{#attr.titleAddButton}" cssClass="button" method="insert"></s:submit>
					</c:when>
					<c:otherwise>
						<wp:i18n key="BUTTON_REFRESH" var="valueRefreshButton" />
						<wp:i18n key="TITLE_REFRESH_IMPRESA_AUSILIARIA" var="titleRefreshButton" />
						<s:submit value="%{#attr.valueRefreshButton}" title="%{#attr.titleRefreshButton}" cssClass="button" method="save"></s:submit>
						<wp:i18n key="BUTTON_NEW" var="valueNewButton" />
						<wp:i18n key="TITLE_NEW_IMPRESA_AUSILIARIA" var="titleAddButton" />
						<s:submit value="%{#attr.valueNewButton}" title="%{#attr.titleNewButton}" cssClass="button" method="add"></s:submit>
					</c:otherwise>
				</c:choose>
			</div>
		</fieldset>
	
	</div>
	
</s:else>
