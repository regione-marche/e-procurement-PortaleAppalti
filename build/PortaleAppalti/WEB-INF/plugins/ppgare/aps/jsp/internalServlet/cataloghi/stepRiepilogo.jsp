<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<s:set var="helperProdotto" value="%{#session.dettProdotto}" />
<s:set var="helperArticolo" value="%{#session.dettProdotto.articolo.dettaglioArticolo}" />
<%--
helperProdotto=<s:property value="%{#helperProdotto}"/><br/>
helperArticolo=<s:property value="%{#helperArticolo}"/><br/>
--%>

<s:if test="%{!#session.dettProdotto.aggiornamento}">
	<c:set var="codiceBalloon" value="BALLOON_WIZ_PRODOTTO_INS_RIEPILOGO"/>
</s:if>
<s:else>
	<c:set var="codiceBalloon" value="BALLOON_WIZ_PRODOTTO_AGG_RIEPILOGO"/>
</s:else>


<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/send_blockUI.jsp" />

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<s:if test="%{!#session.dettProdotto.aggiornamento}">
		<h2><wp:i18n key="TITLE_PAGE_STEP_INSERISCI_PRODOTTO_CATALOGO" /></h2>
	</s:if>
	<s:else>
		<h2>
			<s:if test="%{!#session.dettProdotto.inCarrello}">
				<wp:i18n key="TITLE_PAGE_STEP_AGGIORNA_PRODOTTO_CATALOGO" />
			</s:if>
			<s:else>
				<wp:i18n key="TITLE_PAGE_STEP_AGGIORNA_PRODOTTO_CARRELLO" />
			</s:else>
		</h2>
	</s:else>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/field_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />

	<jsp:include page="stepsProdotto.jsp" />

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="${codiceBalloon}"/>
	</jsp:include>
	
	<s:if test="%{verificaStazioneApp}">
		<p class="instructions">
			<wp:i18n key="LABEL_PRODOTTO_SOGGETTO_A_VERIFICA_SA" />
		</p>
	</s:if>

	<form action="<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/endWizard.action" />" method="post">
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
		
		<fieldset>
			<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_RIEPILOGO" /></legend>

			<div class="fieldset-row first-row">
				<div class="label">
					<label><wp:i18n key="LABEL_CATEGORIA" /></label>
				</div>
				<div class="element">
					<s:property value="%{#helperArticolo.codiceCategoria}" />
					&nbsp;-&nbsp;
					<s:property value="%{#helperArticolo.descrizioneCategoria}" />
				</div>
			</div>

			<div class="fieldset-row">
				<div class="label">
					<label><wp:i18n key="LABEL_CODICE_ARTICOLO" /></label>
				</div>
				<div class="element">
					<s:property value="%{#helperArticolo.codice}" />
					&nbsp;-&nbsp;
					<s:property value="%{#helperArticolo.descrizione}" />
				</div>
			</div>

			<div class="fieldset-row">
				<div class="label">
					<label><wp:i18n key="LABEL_TIPO_ARTICOLO" /></label>
				</div>
				<div class="element">
					<s:iterator value="maps['tipiArticolo']">
						<s:if test="%{key == #helperArticolo.tipo}">
							<s:property value="%{value}" />
						</s:if>
					</s:iterator>
				</div>
			</div>

			<div class="fieldset-row">
				<div class="label">
					<label><wp:i18n key="LABEL_DESCRIZIONE_ARTICOLO" /></label>
				</div>
				<div class="element">
					<s:property value="%{#helperArticolo.descrizioneTecnica}" />
				</div>
			</div>
				
			<div class="fieldset-row">
				<div class="label">
					<label><wp:i18n key="LABEL_CODICE_PRODOTTO_FORNITORE" /></label>
				</div>
				<div class="element">
					<s:property value="%{#helperProdotto.dettaglioProdotto.codiceProdottoFornitore}" />
				</div>
			</div>

			<s:if test="%{#helperArticolo.tipo != TIPO_PRODOTTO_SERVIZIO}">
				<div class="fieldset-row">
					<div class="label">
						<label><wp:i18n key="LABEL_MARCA_PRODOTTO_PRODUTTORE" /></label>
					</div>
					<div class="element">
						<s:property value="%{#helperProdotto.dettaglioProdotto.marcaProdottoProduttore}" />
					</div>
				</div>
					
				<div class="fieldset-row">
					<div class="label">
						<label><wp:i18n key="LABEL_CODICE_PRODOTTO_PRODUTTORE" /></label>
					</div>
					<div class="element">
						<s:property value="%{#helperProdotto.dettaglioProdotto.codiceProdottoProduttore}" />
					</div>
				</div>
			</s:if>
				
			<div class="fieldset-row">
				<div class="label">
					<label><wp:i18n key="LABEL_NOME_COMMERCIALE_PRODOTTO" /></label>
				</div>
				<div class="element">
					<s:property value="%{#helperProdotto.dettaglioProdotto.nomeCommerciale}" />
				</div>
			</div>

			<div class="fieldset-row">
				<div class="label">
					<label><wp:i18n key="LABEL_DESCRIZIONE_AGGIUNTIVA" /></label>
				</div>
				<div class="element">
					<s:property value="%{#helperProdotto.dettaglioProdotto.descrizioneAggiuntiva}" />
				</div>
			</div>

			<div class="fieldset-row">
				<div class="label">
					<label><wp:i18n key="LABEL_IMMAGINE" /></label>
				</div>
				<div class="element">
					<s:if test="%{hasImmagine}">
						<wp:i18n key="LABEL_YES" />
					</s:if>
					<s:else>
						<wp:i18n key="LABEL_NO" />
					</s:else>
				</div>
			</div>

			<div class="fieldset-row">
				<div class="label">
					<label><wp:i18n key="LABEL_NUMERO_CERTIFICAZIONI_ALLEGATE" /></label>
				</div>
				<div class="element">
					<s:property value="%{numeroCertificazioniRichieste}" />
				</div>
			</div>

			<div class="fieldset-row">
				<div class="label">
					<label><wp:i18n key="LABEL_NUMERO_SCHEDE_TECNICHE_ALLEGATE" /></label>
				</div>
				<div class="element">
					<s:property value="%{numeroSchedeTecniche}" />
				</div>
			</div>

			<div class="fieldset-row">
				<div class="label">
					<label><wp:i18n key="LABEL_PREZZO" /></label>
				</div>
				<div class="element">
					<s:text name="format.money"><s:param value="%{#helperProdotto.dettaglioProdotto.prezzoUnitarioPerAcquisto}"/></s:text>&nbsp;&euro;
				</div>
			</div>

			<div class="fieldset-row last-row">
				<div class="label">
					<label><wp:i18n key="LABEL_DATA_SCADEZA_OFFERTA" /></label>
				</div>
				<div class="element">
					<s:date name="%{#helperProdotto.dettaglioProdotto.dataScadenzaOfferta}" format="dd/MM/yyyy"/>
				</div>
			</div>
		</fieldset>

		<div class="azioni">
			<input type="hidden" name="ext" value="${param.ext}" />
			<s:hidden name="catalogo" value="%{#session.carrelloProdotti.currentCatalogo}"/>
			
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_previous.jsp" />
			
			<s:if test="%{!#helperProdotto.aggiornamento}">
				<s:if test="%{!comunicazioneExists}">
					<wp:i18n key="BUTTON_ADD" var="valueAddButton" />
					<wp:i18n key="TITLE_AGGIUNGI_PRODOTTO" var="titleAddButton" />
					<s:submit value="%{#attr.valueAddButton}" title="%{#attr.titleAddButton}" cssClass="button block-ui" method="addSave"></s:submit>
				</s:if>
				<wp:i18n key="BUTTON_AGGIUNGI_PRODOTTO_BOZZA" var="valueAddBozzaButton" />
				<s:submit value="%{#attr.valueAddBozzaButton}" title="%{#attr.valueAddBozzaButton}" cssClass="button block-ui" method="addSaveBozza"></s:submit>
			</s:if>
			<s:else>
				<s:if test="%{!comunicazioneExists}">
					<wp:i18n key="BUTTON_SAVE" var="valueSaveButton" />
					<wp:i18n key="TITLE_SAVE_PRODOTTO" var="titleSaveButton" />
					<s:submit value="%{#attr.valueSaveButton}" title="%{#attr.titleSaveButton}" cssClass="button block-ui" method="addSave"></s:submit>
				</s:if>
				<s:if test="%{BOZZA.equals(#helperProdotto.statoProdotto)}">
					<wp:i18n key="BUTTON_SAVE_PRODOTTO_BOZZA" var="valueSaveBozzaButton" />
					<wp:i18n key="TITLE_SAVE_PRODOTTO_BOZZA" var="titleSaveBozzaButton" />
					<s:submit value="%{#attr.valueSaveBozzaButton}" title="%{#attr.titleSaveBozzaButton}" cssClass="button block-ui" method="addSaveBozza"></s:submit>
				</s:if>
			</s:else>
					
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_cancel.jsp" />
		</div>
	</form>
</div>