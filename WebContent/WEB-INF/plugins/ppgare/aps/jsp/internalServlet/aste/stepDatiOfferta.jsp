<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>

<s:set var="helper" value="%{#session['dettOffertaAsta']}"/>
<s:set var="asta" value="%{#session['dettOffertaAsta'].asta}"/>
<s:set var="offertaEconomica" value="%{#session['dettOffertaAsta'].offertaEconomica}"/>
<s:set var="gara" value="#offertaEconomica.gara"/>

<s:set name="cig" value="%{#gara.cig}"/>
<s:set name="cup" value="%{#gara.cup}"/>
<s:set name="importoNonSoggettoRibasso" value="%{#gara.importoNonSoggettoRibasso != null ? #gara.importoNonSoggettoRibasso : 0}"/>
<s:set name="importoSicurezza" value="%{#gara.importoSicurezza != null ? #gara.importoSicurezza : 0}"/>
<s:set name="importoEsclusoRibasso" value="%{#importoNonSoggettoRibasso + #importoSicurezza}"/>
<s:set name="oneriProgettazione" value="%{#gara.importoOneriProgettazione}" />
<s:set name="oneriSoggettiARibasso" value="%{#gara.oneriSoggettiRibasso}" />

<!-- Condizioni di visibilita dei campi  -->
<s:set name="importoOneriProgettazioneVisible" value="%{#offertaEconomica.importoOneriProgettazioneVisible}"/>
<s:set name="importoOffertoVisible" value="%{#offertaEconomica.importoOffertoVisible}"/>
<s:set name="importoOffertoEditable" value="%{#offertaEconomica.importoOffertoEditable}"/>
<s:set name="importoOffertoMandatory" value="%{#offertaEconomica.importoOffertoMandatory}"/>
<s:set name="percentualeRibassoEditable" value="%{#offertaEconomica.percentualeRibassoEditable}"/>
<s:set name="percentualeRibassoMandatory" value="%{#offertaEconomica.percentualeRibassoMandatory}"/>
<s:set name="percentualeAumentoVisible" value="%{#offertaEconomica.percentualeAumentoVisible}"/>
<s:set name="percentualeAumentoEditable" value="%{#offertaEconomica.percentualeAumentoEditable}"/>
<s:set name="importoOffertoPerPermutaVisible" value="%{#offertaEconomica.importoOffertoPerPermutaVisible}"/>
<s:set name="importoOffertoCanoneAssistenzaVisible" value="%{#offertaEconomica.importoOffertoPerCanoneAssistenzaVisible}"/>

<!-- Note per importo offerto -->
<s:set name="comprensivoNonSoggettiARibasso" value="%{#offertaEconomica.comprensivoNonSoggettiARibasso}"/>
<s:set name="comprensivoOneriSicurezza" value="%{#offertaEconomica.comprensivoOneriSicurezza}"/>
<s:set name="nettoOneriSicurezza" value="%{#offertaEconomica.nettoOneriSicurezza}"/>
<s:set name="comprensivoOneriProgettazione" value="%{#offertaEconomica.comprensivoOneriProgettazione}"/>
<s:set name="nonSuperioreAImportoBaseAsta" value="%{#offertaEconomica.nonSuperioreAImportoBaseAsta}"/>
<s:set name="valoreMassimoImportoOfferto" value="%{#offertaEconomica.valoreMassimoImportoOfferto}"/> 

<!-- Importo della gara / Importo del lotto -->
<s:set name="importoGara" value="%{#gara.importo}"/>
<s:iterator var="lotto" value="%{#helper.lotti}" status="stat">
	<s:if test="%{#lotto.codiceLotto == #asta.codiceLotto}">
		<s:set name="importoGara" value="%{#lotto.importo}"/>
	</s:if>
</s:iterator>


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2><wp:i18n key='TITLE_PAGE_ASTA_CONFERMA_OFFERTA_FINALE'/></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/field_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />

	<jsp:include page="stepsConfermaOfferta.jsp" />

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_WIZ_CONFERMA_ASTA_OFFERTA"/>
	</jsp:include>
	
	<form action="<wp:action path="/ExtStr2/do/FrontEnd/Aste/processPageDatiOfferta.action" />" method="post">
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
		
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/mandatory_fields_message.jsp" />
				
		<fieldset>
			<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_GARETEL_DATI_DELLA_GARA" /></legend>
			<div class="fieldset-row first-row">
				<div class="label">
					<label><wp:i18n key="LABEL_TITOLO" /> :</label>
				</div>
				<div class="element">
					<s:property value="%{#gara.oggetto}" /> 
					<c:if test="${! empty gara.cig}">- <wp:i18n key="LABEL_CIG" /> : <s:property value="%{#gara.cig}" /></c:if>
					<c:if test="${! empty gara.cup}">- <wp:i18n key="LABEL_CUP" /> : <s:property value="%{#gara.cup}" /></c:if>
				</div>
			</div>
			
			<div class="fieldset-row <s:if test="%{!((#importoNonSoggettoRibasso > 0)||(#importoSicurezza > 0)||#importoOneriProgettazioneVisible)}">last-row</s:if>">
				<div class="label">
					<label><wp:i18n key="LABEL_IMPORTO_BASE_GARA" /> : </label>
				</div>
				<div class="element">
					<s:if test="%{#importoGara != null}">
						<s:text name="format.money"><s:param value="%{#importoGara}"/></s:text> &euro;
					</s:if>
				</div>
			</div>

			<s:if test="%{#importoNonSoggettoRibasso > 0}">
			<div class="fieldset-row <s:if test="%{!((#importoSicurezza > 0)||#importoOneriProgettazioneVisible)}">last-row</s:if>">
				<div class="label">
					<label><wp:i18n key="LABEL_DI_CUI_NON_SOGGETTO_RIBASSO" /> : </label>
				</div>
				<div class="element">
						<s:text name="format.money"><s:param value="%{#importoNonSoggettoRibasso}"/></s:text> &euro;
				</div>
			</div>
			</s:if>
	
			<s:if test="%{#importoSicurezza > 0}">
				<div class="fieldset-row <s:if test="%{!(#importoOneriProgettazioneVisible)}">last-row</s:if>">
					<div class="label">
						<label><wp:i18n key="LABEL_DI_CUI_SICUREZZA" /> : </label>
					</div>
					<div class="element">
						<s:text name="format.money"><s:param value="%{#importoSicurezza}"/></s:text> &euro;
					</div>
				</div>
			</s:if>
			
			<s:if test="%{#importoOneriProgettazioneVisible}">
				<div class="fieldset-row last-row">
					<div class="label">
						<label><wp:i18n key="LABEL_DI_CUI_ONERI_PROGETTAZIONE" /> : </label>
					</div>
					<div class="element">
						<s:text name="format.money"><s:param value="%{#oneriProgettazione}"/></s:text> &euro; 
						<s:if test="%{!#oneriSoggettiARibasso}">
						&nbsp;(<wp:i18n key="LABEL_NON_SOGGETTI_RIBASSO" />)
						</s:if>
					</div>
				</div>
			</s:if>
			
		</fieldset>
		
		<fieldset>
			<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_OFFERTA_ASTA" /></legend>

			<div class="fieldset-row first-row">										
				<div class="label">
					<label><wp:i18n key="LABEL_DATA_RILANCIO" /> : </label>
				</div>
				<div class="element">
					<s:date name="#asta.dataUltimoRilancio" format="dd/MM/yyyy HH:mm:ss" />
				</div>				
			</div>
						
			<div class="fieldset-row last-row">										
				<s:if test="%{#asta.tipoOfferta == 1}">
					<div class="label">
						<label><wp:i18n key="LABEL_RIBASSO_PERCENTUALE_FINALE" /> : </label>
					</div>
					<div class="element">
						<s:text name="format.money5dec"><s:param value="%{#asta.ribassoUltimoRilancio}"/></s:text> %
					</div>				
				</s:if>
				<s:else>
					<div class="label">
						<label><wp:i18n key="LABEL_IMPORTO_OFFERTO_FINALE" /> : </label>
					</div>
					<div class="element">
						<s:text name="format.money5dec"><s:param value="%{#asta.importoUltimoRilancio}"/></s:text> &euro;
					</div>				
				</s:else>
<%--					
					<c:set var="vociDettaglio" scope="request" value="${offertaEconomica.vociDettaglio}"/>
					<c:choose>
						<c:when test="${vociDettaglio == null}">
							<s:if test="%{#asta.tipoOfferta == 1}">
								<s:text name="format.money5dec"><s:param value="%{#asta.ribassoUltimoRilancio}"/></s:text> %
							</s:if>
							<s:else>
								<s:text name="format.money5dec"><s:param value="%{#asta.importoUltimoRilancio}"/></s:text> &euro;
							</s:else>
						</c:when>
						<c:otherwise>
							<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/aste/inc/iteratorPrezziUnitari.jsp" >
								<jsp:param name="vociDettaglioSummary" value="Tabella riepilogo lavorazioni e forniture di dettaglio per l'offerta prezzi unitari" />											
								<jsp:param name="vociDettaglioColonneVisibili" value="voce,descrizione,unitaMisura,quantita,astePrezzoUnitario,asteImportoUnitario" />
								<jsp:param name="vociDettaglioTitoliColonne" value="Voce,Descrizione,udm,Qta,Prezzo unitario,Importo" />
								<jsp:param name="vociDettaglioTipiColonne" value="7,7,7,5,2,2" />
								<jsp:param name="vociDettaglioColonneEditabili" value="-" />
								<jsp:param name="vociDettaglioColQta" value="quantita" />
								<jsp:param name="vociDettaglioColPrz" value="astePrezzoUnitario" />
								<jsp:param name="vociDettaglioColImp" value="asteImportoUnitario" />
								<jsp:param name="vociDettaglioEditabile" value="false" />																					
							</jsp:include>
						</c:otherwise>
					</c:choose>
					 --%>
			</div>
		</fieldset>
		
		<div class="azioni">	
			<input type="hidden" name="page" value="datiofferta"/>
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_previous.jsp" />
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_next.jsp" />
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_cancel.jsp" />
		</div>
	</form>
	
</div>
