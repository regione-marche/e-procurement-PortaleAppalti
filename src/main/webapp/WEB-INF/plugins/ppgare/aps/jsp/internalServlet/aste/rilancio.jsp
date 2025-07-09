<%@ taglib prefix="wp" uri="aps-core.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/jquery_treeview.jsp" />
 
<s:set var="gara" value="%{#session['dettGara']}"/>
<c:set var="simboloOfferta"><s:if test="%{dettaglioAsta.tipoOfferta == 1}">%</s:if><s:else>&euro;</s:else></c:set>

<s:set var="vociDettaglio" value="%{#session['dettAstaPrezziUnitari']}"/>
<s:set var="rilancioPrezziUnitari" value="%{#vociDettaglio != null}" />
<s:if test="%{dettaglioAsta.tipoOfferta == 1}">
	<!-- OFFERTA CON RIBASSO -->
	<s:set var="importoEditabile" value="true" />
	<s:set var="confermaVisibile" value="true" />
	<s:set var="totPrezziUnitariVisibile" value="%{#rilancioPrezziUnitari}" />
</s:if>
<s:else>
	<!-- OFFERTA CON IMPORTO -->
	<s:set var="importoEditabile" value="%{! #rilancioPrezziUnitari}" />
	<s:set var="confermaVisibile" value="%{! #rilancioPrezziUnitari}" />
	<s:set var="totPrezziUnitariVisibile" value="false" />
</s:else>

<%--
dettaglioAsta.tipoOfferta: <s:property value="%{dettaglioAsta.tipoOfferta}" /> <br/>
simboloOfferta: ${simboloOfferta} <br/>
rilancioPrezziUnitari: <s:property value="%{#rilancioPrezziUnitari}" /> <br/>
importoEditabile: <s:property value="%{#importoEditabile}"/> <br/>
confermaVisibile: <s:property value="%{#confermaVisibile}"/> <br/>
totPrezziUnitariVisibile: <s:property value="%{#totPrezziUnitariVisibile}"/> <br/>
<c:if test="${sessionScope.currentUser != 'guest'}">
</c:if>
--%>


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp">
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>
			
<div class="portgare-view">

	<h2><wp:i18n key='TITLE_PAGE_ASTA_RILANCIO'/></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_ASTA_RILANCIO" />
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/field_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />

	<form action="<wp:action path="/ExtStr2/do/FrontEnd/Aste/rilanci.action" />" method="post" >
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
		
		<fieldset>
			<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key='LABEL_RILANCIO'/></legend>
					
			<div class="fieldset-row first-row">
				<div class="label">
					<label><wp:i18n key='LABEL_SCARTO_MINIMO_RILANCIO'/> </label>
				</div>
				<div class="element">
					<s:text name="format.money5dec"><s:param value="dettaglioAsta.scartoRilancioMinimo"/></s:text>&nbsp;${simboloOfferta}
				</div>
			</div>
			
			<s:if test="dettaglioAsta.scartoRilancioMassimo != null">
				<div class="fieldset-row">
					<div class="label">
						<label><wp:i18n key='LABEL_SCARTO_MASSIMO_RILANCIO'/> </label>
					</div>
					<div class="element">
						<s:text name="format.money5dec"><s:param value="dettaglioAsta.scartoRilancioMassimo"/></s:text>&nbsp;${simboloOfferta}
					</div>
				</div>
			</s:if>
			
			<div class="fieldset-row">
				<s:if test="%{dettaglioAsta.tipoOfferta == 1}">
					<div class="label">
						<label><wp:i18n key='LABEL_ULTIMO_RIBASSO_OFFERTO'/> </label>
					</div>
					<div class="element">
						<s:text name="format.money5dec"><s:param value="dettaglioAsta.ribassoUltimoRilancio"/></s:text>&nbsp;${simboloOfferta}
					</div>
				</s:if>
				<s:else>
					<div class="label">
						<label><wp:i18n key='LABEL_ULTIMO_IMPORTO_OFFERTO'/> </label>
					</div>
					<div class="element">
						<s:text name="format.money5dec"><s:param value="dettaglioAsta.importoUltimoRilancio"/></s:text>&nbsp;${simboloOfferta}
					</div>
				</s:else>
			</div>

			<s:if test="%{#totPrezziUnitariVisibile}" >
				<div class="fieldset-row">
					<div class="label">
						<label><wp:i18n key='LABEL_TOTALE_PREZZI_UNITARI'/> <span class="required-field"> </span></label>
					</div>
					<div class="element">
						<s:text name="format.money5dec"><s:param value="%{totalePrezziUnitari}"/></s:text>&nbsp;&euro;
					</div>
				</div>
			</s:if>
			
			<div class="fieldset-row">
				<s:if test="%{dettaglioAsta.tipoOfferta == 1}">
					<div class="label">
						<label for="ribasso"><wp:i18n key='LABEL_NUOVO_RIBASSO_OFFERTO'/> <span class="required-field"> *</span></label>
					</div>
				</s:if>
				<s:else>
					<div class="label">
						<label for="importoOfferto"><wp:i18n key='LABEL_NUOVO_IMPORTO_OFFERTO'/> <span class="required-field"> *</span></label>
					</div>
				</s:else>
				<div class="element">
					<s:if test="%{#importoEditabile}" >
						<s:if test="%{dettaglioAsta.tipoOfferta == 1}">
							<s:textfield name="ribasso" id="ribasso" value="%{ribasso}" size="20" maxlength="20" aria-required="true" /> ${simboloOfferta} (<wp:i18n key="LABEL_INDICARE_MAX" /> ${maxNumeroDecimali} <wp:i18n key="LABEL_DECIMALI" />)
						</s:if> 
						<s:else>
							<s:textfield name="importoOfferto" id="importoOfferto" value="%{importoOfferto}" size="20" maxlength="20" aria-required="true" /> ${simboloOfferta} (<wp:i18n key="LABEL_INDICARE_MAX" /> ${maxNumeroDecimali} <wp:i18n key="LABEL_DECIMALI" />)
						</s:else>
					</s:if>
					<s:else>
						<%-- il campo importo/ribasso non e' editabile --%>
						<s:if test="%{dettaglioAsta.tipoOfferta == 1}">
							<s:text name="format.money5dec"><s:param value="%{valoreRibasso}"/></s:text>&nbsp;${simboloOfferta}
							<input type="hidden" name="ribasso" value="${ribasso}" />
							<input type="hidden" name="confermaRibasso" value="${ribasso}" />
						</s:if> 
						<s:else>
							<s:text name="format.money5dec"><s:param value="%{valoreImporto}"/></s:text>&nbsp;${simboloOfferta}
							<input type="hidden" name="importoOfferto" value="${importoOfferto}" />
							<input type="hidden" name="confermaImporto" value="${importoOfferto}" />
						</s:else>
						<input type="hidden" name="tipoOfferta" value="${tipoOfferta}" />
					</s:else>
				</div>
			</div>
			
			<s:if test="%{#confermaVisibile}" >
				<div class="fieldset-row last-row">
					<s:if test="%{dettaglioAsta.tipoOfferta == 1}">
						<div class="label">
							<label for="confermaRibasso"><wp:i18n key='LABEL_CONFERMA_NUOVO_RIBASSO_OFFERTO'/> <span class="required-field"> *</span></label>
						</div>
					</s:if>
					<s:else>
						<div class="label">
							<label for="confermaImporto"><wp:i18n key='LABEL_CONFERMA_NUOVO_IMPORTO_OFFERTA'/> <span class="required-field"> *</span></label>
						</div>
					</s:else>
					<div class="element">
						<s:if test="%{dettaglioAsta.tipoOfferta == 1}">
							<s:textfield name="confermaRibasso" id="confermaRibasso" value="" size="20" maxlength="20" aria-required="true" /> ${simboloOfferta}	
						</s:if> 
						<s:else>
							<s:textfield name="confermaImporto" id="confermaImporto" value="" size="20" maxlength="20" aria-required="true" /> ${simboloOfferta}
						</s:else>
					</div>
				</div>
			</s:if>	
			
		</fieldset>
		
		<div class="azioni">
			<input type="hidden" name="codice" value="${param.codice}" />
			<input type="hidden" name="codiceLotto" value="${param.codiceLotto}" />
			
			<s:if test="%{#rilancioPrezziUnitari}" >
				<wp:i18n key="BUTTON_WIZARD_PREVIOUS" var="valuePreviousButton" />
				<wp:i18n key="TITLE_WIZARD_PREVIOUS" var="titlePreviousButton" />
				<s:submit value="%{#attr.valuePreviousButton}" title="%{#attr.titlePreviousButton}" cssClass="button" method="back"/>
			</s:if>
			
			<wp:i18n key="BUTTON_RILANCIA" var="valueRilanciaButton" />
			<s:submit value="%{#attr.valueRilanciaButton}" title="%{#attr.valueRilanciaButton}" cssClass="button" method="rilancio"/>
			
			<wp:i18n key="BUTTON_WIZARD_CANCEL" var="valueCancelButton" />
			<wp:i18n key="TITLE_WIZARD_CANCEL" var="titleCancelButton" />
			<s:submit value="%{#attr.valueCancelButton}" title="%{#attr.titleCancelButton}" cssClass="button" method="cancel"/>
		</div>
	</form>
		
</div>
