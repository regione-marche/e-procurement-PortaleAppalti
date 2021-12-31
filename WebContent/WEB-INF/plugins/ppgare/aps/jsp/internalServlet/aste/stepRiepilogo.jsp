<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/send_blockUI.jsp"/>

<s:set var="helper" value="%{#session['dettOffertaAsta']}"/>
<s:set var="asta" value="%{#helper.asta}"/>
<s:set var="offertaEconomica" value="%{#helper.offertaEconomica}"/>
<s:set var="documenti" value="%{#helper.documenti}"/>
<s:set var="gara" value="%{#offertaEconomica.gara}"/>
<s:set var="datiImpresa" value="%{#offertaEconomica.impresa}"/>

<!-- firmatario selezionato -->
<s:set var="firmatarioSelezionato" value=""/>
<s:if test="%{#helper.offertaEconomica.idFirmatarioSelezionatoInLista >= 0}" >
	<s:set var="firmatario" value="%{#helper.offertaEconomica.listaFirmatariMandataria.get(#helper.offertaEconomica.idFirmatarioSelezionatoInLista)}" />
	<s:if test="%{tipoQualificaCodifica != null}">
		<s:set var="firmatarioSelezionato" value="%{#firmatario.nominativo + ' ( ' + tipoQualificaCodifica.get(#helper.offertaEconomica.idFirmatarioSelezionatoInLista) + ' )'}" />
	</s:if>
	<s:else>
		<s:set var="firmatarioSelezionato" value="%{#firmatario.nominativo}" />
	</s:else>
</s:if>

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
		<jsp:param name="keyMsg" value="BALLOON_WIZ_CONFERMA_ASTA_RIEPILOGO"/>
	</jsp:include>

	<fieldset>
		<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_DATI_OPERATORE_ECONOMICO" /></legend>
		
		<div class="fieldset-row first-row">
			<div class="label">
				<label><wp:i18n key="LABEL_OGGETTO" /> : </label>
			</div>
			<div class="element">
				<s:property value="%{#gara.oggetto}" />
				<s:property value="%{#gara.datiGeneraliGara.oggetto}" />
			</div>
		</div>
	
		<div class="fieldset-row">
			<div class="label">
				<label><wp:i18n key="LABEL_OPERATORE_ECONOMICO" /> : </label>
			</div>
			<div class="element">
				<s:property value="%{#datiImpresa.datiPrincipaliImpresa.ragioneSociale}" />
			</div>
		</div>
			
		<div class="fieldset-row">
			<div class="label">
				<label><wp:i18n key="LABEL_LEGALI_RAPPRESENTANTI" /> : </label>
			</div>
			<div class="element">
				<s:if test="#datiImpresa.legaliRappresentantiImpresa.size() > 0">
					<ul class="list">
						<s:iterator value="#datiImpresa.legaliRappresentantiImpresa.iterator()" var="legaleRappresentante" status="stat">
							<li class='<s:if test="%{#stat.first}">first</s:if> <s:if test="%{#stat.last}">last</s:if>'>
								<s:property value="%{#legaleRappresentante.cognome}"/> <s:property value="%{#legaleRappresentante.nome}"/> 
								<c:if test="${! empty legaleRappresentante.dataInizioIncarico}">
									<wp:i18n key="LABEL_DA_DATA" /> <s:property value="%{#legaleRappresentante.dataInizioIncarico}"/>
								</c:if> 
								<c:if test="${! empty legaleRappresentante.dataFineIncarico}">
									<wp:i18n key="LABEL_A_DATA" /> <s:property value="%{#legaleRappresentante.dataFineIncarico}"/>
								</c:if>
							</li>
						</s:iterator>
					</ul>
				</s:if>
				<s:else><p><wp:i18n key="LABEL_NOT_DEFINED" /></p></s:else>
			</div>
		</div>

		<div class="fieldset-row">
			<div class="label">
			<label><wp:i18n key="LABEL_DIRETTORI_TECNICI" /> : </label>
			</div>
			<div class="element">
				<s:if test="#datiImpresa.direttoriTecniciImpresa.size() > 0">
					<ul class="list">
						<s:iterator value="#datiImpresa.direttoriTecniciImpresa.iterator()" var="direttoreTecnico" status="stat">
							<li class='<s:if test="%{#stat.first}">first</s:if> <s:if test="%{#stat.last}">last</s:if>'>
								<s:property value="%{#direttoreTecnico.cognome}"/> <s:property value="%{#direttoreTecnico.nome}"/> 
								<c:if test="${! empty direttoreTecnico.dataInizioIncarico}">
									<wp:i18n key="LABEL_DA_DATA" /> <s:property value="%{#direttoreTecnico.dataInizioIncarico}"/>
								</c:if> 
								<c:if test="${! empty direttoreTecnico.dataFineIncarico}">
									<wp:i18n key="LABEL_A_DATA" /> <s:property value="%{#direttoreTecnico.dataFineIncarico}"/>
								</c:if>
							</li>
						</s:iterator>
					</ul>
				</s:if>
				<s:else><wp:i18n key="LABEL_NOT_DEFINED" /></s:else>
			</div>
		</div>

		<div class="fieldset-row last-row">
			<div class="label">
				<label><wp:i18n key="LABEL_FIRMATARIO" /> : </label>
			</div>
			<div class="element">
				<s:property value="%{#firmatarioSelezionato}" />
			</div>
		</div>

	</fieldset>

	<fieldset>
		<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_OFFERTA_FINALE_ASTA" /></legend>
		
		<div class="fieldset-row first-row">
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
		</div>
				
		<div class="fieldset-row last-row">
			<div class="label">
				<label><wp:i18n key="LABEL_DOCUMENTI_INSERITI" /> <s:if test="%{#documenti.docUlteriori.size() > 0}">(<s:property value="%{#documenti.docUlteriori.size()}"/>)</s:if> : </label>
			</div>
			<div class="element">	
				<s:if test="%{#documenti.docUlteriori.size() > 0}">
					<ul class="list">
						<s:iterator value="#documenti.docUlteriori" var="item" status="stat">
							<s:set var="contentType" value="%{#documenti.docUlterioriContentType[#stat.index]}" />
							<s:set var="descrizione" value="%{#documenti.docUlterioriDesc[#stat.index]}" />
							<s:set var="nomeFile" value="%{#documenti.docUlterioriFileName[#stat.index]}" />
							<s:set var="dimensione" value="%{#documenti.docUlterioriSize[#stat.index]}" />
							
							<li class='<s:if test="%{#stat.first}">first</s:if> <s:if test="%{#stat.last}">last</s:if>'>
								<c:choose>
									<c:when test="${skin == 'highcontrast' || skin == 'text'}">
										 &#8226;&nbsp;<span title="<s:property value="%{#nomeFile}"/> (<s:property value="%{#dimensione}" />&nbsp;KB) SHA1:<s:property value="%{#contentType}"/>"><s:property value="%{#descrizione}"/></span>
									</c:when>
									<c:otherwise>
										 &#8226;&nbsp;<span title="<s:property value="%{#nomeFile}"/> (<s:property value="%{#dimensione}" />&nbsp;KB) SHA1:<s:property value="%{#contentType}"/>"><s:property value="%{#descrizione}"/></span>
									</c:otherwise>
								</c:choose>
								&nbsp;(<s:property value="%{#nomeFile}"/>)
							</li>
						</s:iterator>
					</ul>
				</s:if>
				<s:else>
					<wp:i18n key="LABEL_NO_DOCUMENT" />.
				</s:else>
			</div>
		</div>
		
	</fieldset>


	<form action="<wp:action path="/ExtStr2/do/FrontEnd/Aste/processPageRiepilogo.action" />" method="post">
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
		
		<div class="azioni">
			<input type="hidden" name="page" value="riepilogo"/>

			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_previous.jsp" />
			<s:if test="%{not #helper.datiInviati}">
				<wp:i18n key="LABEL_CONFERMA_INVIO_OFFERTA" var="valueInviaConfermaButton" />
				<wp:i18n key="TITLE_CONFERMA_E_INOLTRA" var="titleInviaConfermaButton" />
				<s:submit value="%{#attr.valueInviaConfermaButton}" title="%{#attr.titleInviaConfermaButton}" cssClass="button block-ui" method="send"></s:submit>
			</s:if>
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_cancel.jsp" />
		</div>
	</form>
</div>
