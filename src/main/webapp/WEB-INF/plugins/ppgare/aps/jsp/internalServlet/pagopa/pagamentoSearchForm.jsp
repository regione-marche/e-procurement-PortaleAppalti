<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="/struts-tags" %>


		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
		<%-- FILTRI DI RICERCA --%>
	<fieldset>
		<div class="fieldset-row  first-row">
			<div class="label">
				<label for="model.codiceGara"><wp:i18n key="LABEL_PAGOPA_GARA_CODICE" /> : </label>
			</div>
			<div class="element">
				<s:textfield name="model.codiceGara" id="model.codiceGara" cssClass="text" 
							value="%{model.codiceGara}" 
							size="50" />
			</div>
		</div>
		<%-- <div class="fieldset-row">
			<div class="label">
				<label for="model.iuv"><wp:i18n key="LABEL_IUV" /> : </label>
			</div>
			<div class="element">
				<s:textfield name="model.iuv" id="model.iuv" cssClass="text" 
							value="%{model.iuv}" 
							size="50" />
			</div>
		</div>
		<div class="fieldset-row">
			<div class="label">
				<label for="model.idDebito"><wp:i18n key="LABEL_IDDEBITO" /> : </label>
			</div>
			<div class="element">
				<s:textfield name="model.idDebito" id="model.idDebito" cssClass="text" 
							value="%{model.idDebito}" 
							size="50" />
			</div>
		</div> --%>
		<%-- <div class="fieldset-row">
				<wp:i18n key="LABEL_DATA_SCADENZA" var="headerValueDataPubblicazione"/>
				<wp:i18n key="LABEL_DA_DATA" var="headerValueDa"/>
				<wp:i18n key="LABEL_A_DATA" var="headerValueA"/>
			<div class="label">
				<label><s:property value="%{#attr.headerValueDataPubblicazione}" /> : </label>
			</div>
			<div class="element">
				<label><s:property value="%{#attr.headerValueDa}" />: </label>
				<s:textfield name="model.dataScadenzaDa" id="model.dataScadenzaDa" cssClass="text" 
							value="%{model.dataScadenzaDa}" title="%{#attr.headerValueDataPubblicazione} %{#attr.headerValueDa}"  
							maxlength="10" size="10" />
				<label><s:property value="%{#attr.headerValueA}" />: </label>
				<s:textfield name="model.dataScadenzaA" id="model.dataScadenzaA" cssClass="text" 
							value="%{model.dataScadenzaA}" title="%{#attr.headerValueDataPubblicazione} %{#attr.headerValueA}" 
							maxlength="10" size="10" />
				(<wp:i18n key="LABEL_FORMATO_DATA" />)
			</div>
		</div> --%>
		<div class="fieldset-row">
			<div class="label">
				<label for="model.causale"><wp:i18n key="LABEL_PAGOPA_CAUSALE" /> : </label>
			</div>
			<div class="element">
				<s:select list="%{tipiCausalePagamento}" id="model.causale" name="model.causale" value="%{model.causale}" headerKey="-1" headerValue="%{#attr.headerValueStatoGara}" >
				</s:select>
			</div>
		</div>
		<s:hidden id="model.stato" name="model.stato" value="%{model.stato}" />
		<div class="fieldset-row last-row">
			<div class="label">
				<label for="model.iDisplayLength"><s:property value="%{getText('label.rowsPerPage')}" /> : </label>
			</div>
			<div class="element">
				<select name="model.iDisplayLength" id="model.iDisplayLength" class="text">
					<option <s:if test="%{model.iDisplayLength==10}">selected="selected"</s:if> value="10">10</option>
					<option <s:if test="%{model.iDisplayLength==20}">selected="selected"</s:if> value="20">20</option>
					<option <s:if test="%{model.iDisplayLength==50}">selected="selected"</s:if> value="50">50</option>
					<option <s:if test="%{model.iDisplayLength==100}">selected="selected"</s:if> value="100">100</option>
				</select>
			</div>
		</div>
	</fieldset>
	<div class="azioni">
		<wp:i18n key="BUTTON_SEARCH" var="valueSearchButton" />
		<s:submit value="%{#attr.valueSearchButton}" cssClass="button block-ui"/>
	</div>
