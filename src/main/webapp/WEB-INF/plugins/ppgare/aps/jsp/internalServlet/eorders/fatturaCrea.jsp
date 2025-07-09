<%@ taglib prefix="wp" uri="aps-core.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<c:set var="backLink" value="ordiniTutti" />


<%-- ******************************************************************************** --%>
<script type="text/javascript">
<!--//--><![CDATA[//><!--
// apertura della pagina...
$(document).ready(function() {
	
});	
function showDate(elNoun){
	console.log(elNoun);
	if(elNoun.startsWith('#')){
		$(elNoun).value=now();		
	} else {
		$('#'elNoun).value=now();
	}
}
//--><!]]>
</script>
<%-- ******************************************************************************** --%>


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp">
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2>
		<wp:i18n key="TITLE_PAGE_EORDERS_FATTURA_CREA" />&nbsp;<s:property value="orderCode"/>
	</h2>
	<c:catch var="exceptionOrders">
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/field_errors.jsp" />
		<jsp:include page="stepsFatture.jsp" />

		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
			<jsp:param name="keyMsg" value="BALLOON_EORDERS_FATTURA_CREA" />
		</jsp:include>


		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/skip_form_buttons.jsp" />

		<div class="detail-section first-detail-section">
			<h3 class="detail-section-title">
				<span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span>
				<wp:i18n key="LABEL_FATTURA_DATI_GENERALI" />
			</h3>
		</div>

		<form
			action="<wp:action path="/ExtStr2/do/FrontEnd/EOrders/saveHeader.action" />"
			method="post">
			<fieldset>
				<legend>
					<span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span>
					<wp:i18n key="LABEL_FATTURA_TESTATA" />
				</legend>
				<%-- <div class="fieldset-row first-row">
					<div class="label">
						<label for="codiceFattura"><wp:i18n
								key="LABEL_FATT_CODICEFATT" /></label>:
					</div>
					<div class="element">
						<input type="text" name="model.codiceFattura" class="text"
							id="model.codiceFattura" value="" placeholder="000001" />
					</div>
				</div> --%>
				<div class="fieldset-row first-row">
					<div class="label">
						<label><wp:i18n key="LABEL_FATT_DG_NUMERO" /></label>:
					</div>
					<div class="element">
						<s:textfield name="model.numero" id="model.numero" class="text" value="%{model.numero}" placeholder="K00001" />
					</div>
				</div>
				<c:if test="${ambitoTerritoriale ne 1 }">
					<div class="fieldset-row">
						<div class="label">
							<label><wp:i18n key="LABEL_FATTURA_STAB_ORG" /> : </label>
						</div>
						<div class="element">
							<c:set var="objectInd" value="${indirizzi}" />
							<c:catch var="nestedErrorIndS">
								<select name="model.stabileOrganizzazione"
									id="model.stabileOrganizzazione">
									<option>&nbsp;</option>
									<c:forEach var="elementInd" items="${objectInd}">
										<option value="${elementInd}"
											<c:out value="${(elementInd eq model.stabileOrganizzazione) ? 'selected':''}" />><c:out
												value="${elementInd.indirizzo} ${elementInd.numCivico} ${elementInd.comune} ${elementInd.provincia} ${elementInd.nazione}" /></option>
									</c:forEach>
								</select>
							</c:catch>
							<c:if test="${nestedErrorIndS!=null}">
					There is an exception: ${nestedErrorIndS.message}<br>
					The exception is : ${nestedErrorIndS}<br>
								<br>
							</c:if>
						</div>

					</div>

					<div class="fieldset-row">
						<div class="label">
							<label><wp:i18n key="LABEL_FATTURA_RAPP_FISC" /> : </label>
						</div>
						<div class="element">
							<div class="contents-group">
								<label for="idFiscaleIvaidpaese"><wp:i18n
										key="LABEL_FATTURA_IDFISCIVA_IDPAESE" /> : <span
									class="required-field">*</span></label>
								<s:textfield name="idFiscaleIvaidpaese" id="idFiscaleIvaidpaese"
									value="" size="2" maxlength="2" aria-required="true" />
							</div>
							<div class="contents-group">
								<label for="idFiscaleIvaidcodice"><wp:i18n
										key="LABEL_FATTURA_IDFISCIVA_IDCODICE" /> : <span
									class="required-field">*</span></label>
								<s:textfield name="idFiscaleIvaidcodice"
									id="idFiscaleIvaidcodice" value="" size="28" maxlength="28"
									aria-required="true" />
							</div>
							<div class="contents-group">
								<label for="idFiscaleCodFiscale"><wp:i18n
										key="LABEL_CODICE_FISCALE" /> : </label>
								<s:textfield name="idFiscaleCodFiscale" id="idFiscaleCodFiscale"
									value="" size="28" maxlength="28" aria-required="true" />
							</div>
							<div class="contents-group">
								<label for="idFiscaleDenom"><wp:i18n
										key="LABEL_FATTURA_IDFISCIVA_DENOM" /> : </label>
								<s:textfield name="idFiscaleDenom" id="idFiscaleDenom" value=""
									size="80" maxlength="80" aria-required="true" />
							</div>
							<div class="contents-group">
								<label for="idFiscaleNome"><wp:i18n
										key="LABEL_FATTURA_IDFISCIVA_NOME" /> : </label>
								<s:textfield name="idFiscaleNome" id="idFiscaleNome" value=""
									size="60" maxlength="60" aria-required="true" />
							</div>
							<div class="contents-group">
								<label for="idFiscaleCogn"><wp:i18n
										key="LABEL_FATTURA_IDFISCIVA_COGN" /> : </label>
								<s:textfield name="idFiscaleCogn" id="idFiscaleCogn" value=""
									size="60" maxlength="60" aria-required="true" />
							</div>
							<div class="contents-group">
								<label for="idFiscaleTit"><wp:i18n
										key="LABEL_FATTURA_IDFISCIVA_TIT" /> : </label>
								<s:textfield name="idFiscaleTit" id="idFiscaleTit" value=""
									size="10" maxlength="10" aria-required="true" />
							</div>
							<div class="contents-group">
								<label for="idFiscaleEori"><wp:i18n
										key="LABEL_FATTURA_IDFISCIVA_EORI" /> : </label>
								<s:textfield name="idFiscaleEori" id="idFiscaleEori" value=""
									size="17" maxlength="17" aria-required="true" />
							</div>
						</div>
					</div>
				</c:if>
				<div class="fieldset-row">
					<div class="label">
						<label><wp:i18n key="LABEL_FATTURA_STATLIQ" /> : </label>
					</div>
					<div class="element">
						<s:select list="model.statiLiquidazione" listKey="value"
							listValue="value" name="model.statoLiquidazione"
							id="model.statoLiquidazione" required="required"
							value="model.statoLiquidazione.value" headerKey="" headerValue=""></s:select>
						<%-- <select name="model.statoLiquidazione">
					<option value="LS" <c:if test="${model.statoLiquidazione eq 'LS'}">selected</c:if>>[LS]: In liquidazione</option>
					<option value="LN" <c:if test="${model.statoLiquidazione eq 'LN'}">selected</c:if>>[LN]: Non in liquidazione</option>
				</select> --%>
					</div>
				</div>
			</fieldset>

			<fieldset>
				<legend>
					<span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span>
					<wp:i18n key="LABEL_FATTURA_CORPO" />
				</legend>
				<div class="fieldset-row">
					<div class="label">
						<label><wp:i18n key="LABEL_FATT_DG_TIPDOC" /></label>:
					</div>
					<div class="element">
						<s:select list="model.tipiDocumento" listKey="value"
							listValue="value" name="model.tipoDocumento"
							id="model.tipoDocumento" required="required"
							value="model.tipoDocumento.value" headerKey="" headerValue=""></s:select>
					</div>
				</div>
				<div class="fieldset-row">
					<div class="label">
						<label><wp:i18n key="LABEL_FATT_DG_DATA" /></label>:
					</div>
					<div class="element">
						<s:textfield name="model.data" id="model.data" value="%{model.data}"
							format="dd/MM/yyyy" />
					</div>
				</div>
				<%-- <div class="fieldset-row">
					<div class="label">
						<label><wp:i18n key="LABEL_FATT_DG_NUMERO" /></label>:
					</div>
					<div class="element">
						<s:textfield name="model.numero" id="model.numero" type="number" />
					</div>
				</div> --%>
				<div class="fieldset-row">
					<div class="label">
						<label><wp:i18n key="LABEL_FATT_DG_DATIRITENUTA" /></label>:
					</div>
					<div class="element">
						<div class="contents-group">
							<label for="model.tipiRitenuta"><wp:i18n
									key="LABEL_FATT_DG_DR_TR" /> : </label>
							<s:select list="model.tipiRitenuta" listKey="value"
								listValue="value" name="model.tipoRitenuta"
								id="model.tipoRitenuta" value="model.tipoRitenuta.value" headerKey="" headerValue=""></s:select>
						</div>
						<div class="contents-group">
							<label for="model.aliquotaRitenuta"><wp:i18n
									key="LABEL_FATT_DG_DR_AL" /> : </label>
							<s:textfield name="model.aliquotaRitenuta"
								id="model.aliquotaRitenuta" type="number" maxlength="3" size="3" />
							%
						</div>
						<div class="contents-group">
							<label for="model.causalePagamento"><wp:i18n
									key="LABEL_FATT_DG_DR_CP" /> : </label>
							<s:select list="model.causaliPagamento" listKey="value"
								listValue="value" name="model.causalePagamento"
								id="model.causalePagamento" value="model.causalePagamento.value" headerKey="" headerValue=""></s:select>
						</div>
					</div>
				</div>
				<div class="fieldset-row">
					<div class="label">
						<label><wp:i18n key="LABEL_FATT_DATT_BOL" /></label>:
					</div>
					<div class="element">
						<wp:i18n key="LABEL_FATT_DATT_BOL_FIXTEXT" />
						Bollo assolto ai sensi del decreto MEF 17 giugno 2014 (art. 6)
						<!-- <input type="hidden" name="model.bolloVirtuale" value="SI" /> -->
						<%-- <s:hidden name="model.bolloVirtuale" value="model.bolloVirtuale"
							id="model.bolloVirtuale"></s:hidden> --%>

					</div>
				</div>
				<c:if test="${not empty model.tcapre}">
					<div class="fieldset-row">
						<div class="label">
							<label><wp:i18n key="LABEL_FATT_DATCASSPREV" /></label>:
						</div>
						<div class="element">
							<div class="contents-group">
								<label for="datCassTipoCassa"><wp:i18n
										key="LABEL_FATTURA_DATCASSPREV_TIPO" /> : </label>
								<%-- <s:textfield name="model.datCassTipoCassa" id="model.datCassTipoCassa" value="model.datCassTipoCassa"
									size="17" maxlength="17" /> --%>
								<s:select list="model.datCassTipoCassaEnum" listKey="value"
										name="model.datCassTipoCassa"
										id="model.datCassTipoCassa"
										value="model.datCassTipoCassa" headerValue="" headerKey=""></s:select>
							</div>
							<div class="contents-group">
								<label for="datRitTipRit"><wp:i18n
										key="LABEL_FATTURA_DATCASSPREV_ALRIT" /> : </label>
								<s:textfield name="model.datCassAliquota" id="model.datCassAliquota" value="%{model.datCassAliquota}"
									size="17" maxlength="17" />
								
							</div>
							<div class="contents-group">
								<label for="datRitAlRit"><wp:i18n
										key="LABEL_FATTURA_DATCASSPREV_IMPCASSA" /> : </label>
								<s:textfield name="model.datCassImponibile" id="model.datCassImponibile" value="%{model.datCassImponibile}"
									size="17" maxlength="17" />
							</div>
							<div class="contents-group">
								<label for="datRitCausPag"><wp:i18n
										key="LABEL_FATTURA_DATCASSPREV_ALIVA" /> : </label>
								<s:textfield name="model.datCassAliquotaIva" id="model.datCassAliquotaIva"
									value="%{model.datCassAliquotaIva}" size="17" maxlength="17" aria-required="true" />
							</div>
							<div class="contents-group">
								<label for="datRitCausPag"><wp:i18n
										key="LABEL_FATTURA_DATCASSPREV_RITENUTA" /> : </label>
								<%-- <s:textfield name="model.datCassRitenuta" id="model.datCassRitenuta"
									value="model.datCassRitenuta" size="17" maxlength="17" aria-required="true" /> --%>
									<s:select list="model.datCassRitenutaEnum" listKey="value"
										name="model.datCassRitenuta"
										id="model.datCassRitenuta"
										value="model.datCassRitenuta" headerValue="" headerKey=""></s:select>
							</div>
							<div class="contents-group">
								<label for="datRitCausPag"><wp:i18n
										key="LABEL_FATTURA_DATCASSPREV_NATURA" /> : </label>
								<%-- <s:textfield name="model.datCassNatura" id="model.datCassNatura"
									value="model.datCassNatura" size="17" maxlength="17" aria-required="true" /> --%>
								<s:select list="model.datCassNaturaEnum" listKey="value"
										name="model.datCassNatura"
										id="model.datCassNatura"
										value="model.datCassNatura" headerValue="" headerKey="" ></s:select>
							</div>
						</div>
					</div>
				</c:if>
				<div class="fieldset-row">
					<div class="label">
						<label><wp:i18n key="LABEL_FATT_DATISAL" /></label>:
					</div>
					<div class="element">
						<input name="model.riferimentoFase" id="model.riferimentoFase"
							type="number" value='<c:out value="${model.riferimentoFase}" />'
							size="17" maxlength="17" aria-required="false" placeholder="1" />
					</div>
				</div>
				<div class="fieldset-row">
					<div class="label">
						<label><wp:i18n key="LABEL_FATT_DDT" /></label>:
					</div>
					<div class="element">
						<div class="contents-group">
							<label for="model.ddtnum"><wp:i18n key="LABEL_FATT_DDT_N" />
								: </label>
							<s:textfield name="model.ddtnum" id="model.ddtnum"
								value="%{model.ddtnum}" size="20" maxlength="20"
								aria-required="false" />
						</div>
						<div class="contents-group">
							<label for="model.ddtdata"><wp:i18n
									key="LABEL_FATTURA_DDT_DATA" /> : </label>
							<s:textfield name="model.ddtdata" id="model.ddtdata"
								value="%{model.ddtdata}" size="17" maxlength="17"
								aria-required="false"  />
						</div>
						<div class="contents-group">
							<label for="model.ddtrifnum"><wp:i18n
									key="LABEL_FATTURA_DDT_RIF" /> : </label>
							<s:textfield name="model.ddtrifnum" id="model.ddtrifnum"
								value="%{model.ddtrifnum}" size="17" maxlength="17"
								aria-required="false" />
						</div>
					</div>
				</div>
				<div class="fieldset-row">
					<div class="label">
						<label><wp:i18n key="LABEL_FATT_DATIPAGAMENTO" /></label>:
					</div>
					<div class="element">
						<c:if test="${not empty model.datiPagamento }">
							<c:forEach var="datoPagamento" items="${model.datiPagamento}">
								<div class="contents-group">
									<label for="model.condPag"><wp:i18n
											key="LABEL_FATT_PAG_COND" /></label>: <select name="model.condPag">
										<option value="TP01"
											<c:if test="${datoPagamento.condizioniPagamento.value eq 'TP01'}">selected</c:if>>[TP01]:
											pagamento a rate</option>
										<option value="TP02"
											<c:if test="${datoPagamento.condizioniPagamento.value eq 'TP02'}">selected</c:if>>[TP02]:
											pagamento completo</option>
										<option value="TP03"
											<c:if test="${datoPagamento.condizioniPagamento.value eq 'TP03'}">selected</c:if>>[TP03]:
											anticipo</option>
									</select>
								</div>
								<div class="contents-group">
									<label for="model.modalitaPagamentoEnum"><wp:i18n
											key="LABEL_FATT_PAG_MOD" /></label>:
									<s:select list="model.modalitaPagamentoEnum" listKey="value"
										listValue="value" name="model.modalitaPagamento"
										id="model.modalitaPagamento"
										value="datoPagamento.dettaglioPagamento.modalitaPagamento" headerKey="" headerValue=""></s:select>
								</div>
								<div class="contents-group">
									<label for="model.dataScadenzaPagamento"><wp:i18n
											key="LABEL_FATT_PAG_DATSCAD" /></label>:
									<s:textfield name="model.dataScadenzaPagamento"
										id="model.dataScadenzaPagamento"
										value="%{model.dataScadenzaPagamento}" size="17"
										maxlength="17" aria-required="false" />
								</div>
								<div class="contents-group">
									<label for="model.istitutoFinanziario"><wp:i18n
											key="LABEL_FATT_PAG_ISTFIN" /></label>:
									<s:textfield name="model.istitutoFinanziario"
										id="model.istitutoFinanziario"
										value="%{model.istitutoFinanziario}" size="80" maxlength="80"
										aria-required="false" />
								</div>
								<div class="contents-group">
									<label for="model.iban"><wp:i18n
											key="LABEL_FATT_PAG_IBAN" /></label>:
									<s:textfield name="model.iban" id="model.iban"
										value="%{model.iban}" size="34" maxlength="34"
										aria-required="false" />
								</div>
								<div class="contents-group">
									<label for="model.abi"><wp:i18n
											key="LABEL_FATT_PAG_ABI" /></label>:
									<s:textfield name="model.abi" id="model.abi"
										value="%{model.abi}" size="5" maxlength="5"
										aria-required="false" />
								</div>
								<div class="contents-group">
									<label for="model.cab"><wp:i18n
											key="LABEL_FATT_PAG_CAB" /></label>:
									<s:textfield name="model.cab" id="model.cab"
										value="%{model.cab}" size="5" maxlength="5"
										aria-required="false" />
								</div>
								<div class="contents-group">
									<label for="model.bic"><wp:i18n
											key="LABEL_FATT_PAG_BIC" /></label>:
									<s:textfield name="model.bic" id="model.bic"
										value="%{model.bic}" size="11" maxlength="11"
										aria-required="false" />
								</div>
							</c:forEach>
						</c:if>
					</div>
				</div>
			</fieldset>
			<input type="hidden" name="id" value="${id}" />
			<input type="hidden" name="idFatt" value="${idFatt}" />
			<input type="hidden" name="orderCode" value="${orderCode}" />
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			<%-- <s:submit label="SALVA" value="SALVA" title="SALVA" cssClass="button" method="saveHeader"></s:submit> --%>
			<div class="azioni">
				<wp:i18n key="LABEL_EORDERS_FATT_REGEN" var="valueFATT_REGENButton" />
				<s:submit value="%{#attr.valueFATT_REGENButton}" title="%{#attr.valueFATT_REGENButton}" cssClass="button" method="rigenera"></s:submit>
			</div>
			<div class="azioni">
				<input type="hidden" name="backLink" value="ordiniConfermati" />
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_cancel.jsp" />
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_next.jsp" />
			</div>
		</form>
	</c:catch>
	<c:if test="${exceptionOrders!=null}">
	There is an exception: ${exceptionOrders.message}<br>
	The exception is : ${exceptionOrders}<br>
		<br>
	</c:if>

	<%-- <div class="detail-section">
		<form action="<wp:action path="/ExtStr2/do/FrontEnd/EOrders/elaboraLinee.action" />" method="post" >
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			<c:out value="id: ${id}"></c:out>
			<br><c:out value="idFatt: ${idFatt}"></c:out>
			<br><c:out value="orderCode: ${orderCode}"></c:out>
			<input type="hidden" name="id" value="${id}" />
			<input type="hidden" name="idFatt" value="${idFatt}" />
			<input type="hidden" name="orderCode" value="${orderCode}" />
			<div class="detail-row">
				<wp:i18n key="LABEL_EORDERS_FATT_CREALINEE" var="valueFATT_CREALINEEButton" />
				<s:submit value="%{#attr.valueFATT_CREALINEEButton}" title="%{#attr.valueFATT_CREALINEEButton}" cssClass="button" method="elaboraLinee"></s:submit>
			</div>
		</form>		
	</div>
	<div class="detail-section">
		<form action="<wp:action path="/ExtStr2/do/FrontEnd/EOrders/fatturaCrea.action" />" method="post" >
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			<c:out value="id: ${id}"></c:out>
			<br><c:out value="idFatt: ${idFatt}"></c:out>
			<br><c:out value="orderCode: ${orderCode}"></c:out>
			<input type="hidden" name="id" value="${id}" />
			<input type="hidden" name="idFatt" value="${idFatt}" />
			<input type="hidden" name="orderCode" value="${orderCode}" />
			<div class="detail-row">
				<wp:i18n key="LABEL_EORDERS_FATT_CREA" var="valueFATT_CREAButton" />
				<s:submit value="%{#attr.valueFATT_CREAButton}" title="%{#attr.valueFATT_CREAButton}" cssClass="button" method="crea"></s:submit>
			</div>
		</form>		
		<form action="<wp:action path="/ExtStr2/do/FrontEnd/EOrders/fatturaCrea.action" />" method="post" >
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			<c:out value="id: ${id}"></c:out>
			<br><c:out value="idFatt: ${idFatt}"></c:out>
			<br><c:out value="orderCode: ${orderCode}"></c:out>
			<input type="hidden" name="id" value="${id}" />
			<input type="hidden" name="idFatt" value="${idFatt}" />
			<input type="hidden" name="orderCode" value="${orderCode}" />
			<div class="detail-row">
				<wp:i18n key="LABEL_EORDERS_FATT_REGEN" var="valueFATT_REGENButton" />
				<s:submit value="%{#attr.valueFATT_REGENButton}" title="%{#attr.valueFATT_REGENButton}" cssClass="button" method="rigenera"></s:submit>
			</div>
		</form>		
		<div class="back-link">
			<a href="<wp:action path="/ExtStr2/do/FrontEnd/EOrders/${backLink}.action"/>&amp;last=1">
				<wp:i18n key="LINK_BACK_TO_LIST" />
			</a>
		</div>
	</div> --%>
</div>