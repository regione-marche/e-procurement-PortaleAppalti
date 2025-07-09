<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>

<es:checkCustomization var="visFiltroAltriSoggetti" objectId="GARE" attribute="FILTROCUC" feature="VIS" />
<es:getAppParam name = "denominazioneStazioneAppaltanteUnica" var = "stazAppUnica" scope = "page"/> 	

<s:set var="searchForm" value="%{#session.formSearchAvvisi}" />

<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/send_blockUI.jsp" />

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-list">

	<h2><wp:i18n key="TITLE_PAGE_RICERCA_AVVISI" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
			<jsp:param name="keyMsg" value="BALLOON_RICERCA_AVVISI"/>
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />

	<form class="form-ricerca" action="<wp:action path="/ExtStr2/do/FrontEnd/Avvisi/search.action" />" method="post" >
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />

		<fieldset>
			<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_SEARCH_CRITERIA" /></legend>

			<div class="fieldset-row first-row special">
				<div class="label">
					<label><wp:i18n key="LABEL_SEARCH_FOR" /> : </label>
				</div>
				<div class="element">
					<a href="<wp:action path="/ExtStr2/do/FrontEnd/Bandi/openSearch.action" />" title="<wp:i18n key="LABEL_GO_TO_SEARCH_BANDI" />"><wp:i18n key="LABEL_BANDI" /></a>
					&nbsp;
					<span class="important"><wp:i18n key="LABEL_AVVISI" /></span>
					&nbsp;
					<a href="<wp:action path="/ExtStr2/do/FrontEnd/Esiti/openSearch.action" />" title="<wp:i18n key="LABEL_GO_TO_SEARCH_ESITI" />"><wp:i18n key="LABEL_ESITI" /></a>
				</div>
			</div>

			<div class="fieldset-row">
				<div class="label">
					<label for="model.stazioneAppaltante"><wp:i18n key="LABEL_STAZIONE_APPALTANTE" /> : </label>
				</div>
				<div class="element">
					<c:choose>
						<c:when test="${! empty stazAppUnica }">
							<s:set var="stazAppUnicaToStruts">${stazAppUnica}</s:set>
							<s:property value="stazAppUnicaToStruts" />
						</c:when>
						<c:when test="${! empty stazioneAppaltante}">
							<s:property value="%{descStazioneAppaltante}"/>
						</c:when>
						<c:otherwise>
							<wp:i18n key="OPT_CHOOSE_STAZIONE_APPALTANTE" var="headerValueStazioneAppaltante" />
							<s:select name="model.stazioneAppaltante" id="model.stazioneAppaltante" list="maps['stazioniAppaltanti']" 
									value="%{#searchForm.stazioneAppaltante}" 
									headerKey="" headerValue="%{#attr.headerValueStazioneAppaltante}" 
									cssStyle="width: 100%;" autocomplete="off" >
							</s:select>
						</c:otherwise>
					</c:choose>
				</div>
			</div>

			<div class="fieldset-row">
				<div class="label">
					<label for="model.oggetto"><wp:i18n key="LABEL_TITOLO" /> : </label>
				</div>
				<div class="element">
					<s:textfield name="model.oggetto" id="model.oggetto" cssClass="text" value="%{#searchForm.oggetto}" 
											 size="50" autocomplete="off" />
				</div>
			</div>
	
			<div class="fieldset-row">
				<div class="label">
					<label for="model.tipoAvviso"><wp:i18n key="LABEL_TIPO_AVVISO" /> : </label>
				</div>
				<div class="element">
					<wp:i18n key="OPT_CHOOSE_TIPO_AVVISO" var="headerValueTipoAvviso"/>
					<s:select name="model.tipoAvviso" id="model.tipoAvviso" list="maps['tipiAvviso']" value="%{#searchForm.tipoAvviso}"
										headerKey="" headerValue="%{#attr.headerValueTipoAvviso}" autocomplete="off" >
					</s:select>
				</div>
			</div>
	
			<div class="fieldset-row">
				<div class="label">
					<label><wp:i18n key="LABEL_DATA_PUBBLICAZIONE_AVVISO" /> : </label>
				</div>
				<div class="element">
					<wp:i18n key="LABEL_DATA_PUBBLICAZIONE_AVVISO" var="headerValueDataPubblicazione"/>
					<wp:i18n key="LABEL_DA_DATA" var="headerValueDa"/>
					<wp:i18n key="LABEL_A_DATA" var="headerValueA"/>
					<label><wp:i18n key="LABEL_DA_DATA" />: </label>
					<s:textfield name="model.dataPubblicazioneDa" id="model.dataPubblicazioneDa" cssClass="text" 
								value="%{#searchForm.dataPubblicazioneDa}" maxlength="10" size="10" 
								title="%{#attr.headerValueDataPubblicazione} %{#attr.headerValueDa}" autocomplete="off" />
					<label><wp:i18n key="LABEL_A_DATA" />: </label>
					<s:textfield name="model.dataPubblicazioneA" id="model.dataPubblicazioneA" cssClass="text" 
								value="%{#searchForm.dataPubblicazioneA}" maxlength="10" size="10" 
								title="%{#attr.headerValueDataPubblicazione} %{#attr.headerValueA}" autocomplete="off" />
					 (<wp:i18n key="LABEL_FORMATO_DATA" />)
				</div>
			</div>
	
			<div class="fieldset-row">
				<div class="label">
					<label><wp:i18n key="LABEL_DATA_SCADENZA_AVVISO" /> : </label>
				</div>
				<div class="element">
					<wp:i18n key="LABEL_DATA_SCADENZA_AVVISO" var="headerValueDataScadenza"/>
					<label><wp:i18n key="LABEL_DA_DATA" />: </label>
					<s:textfield name="model.dataScadenzaDa" id="model.dataScadenzaDa" cssClass="text" 
								value="%{#searchForm.dataScadenzaDa}" maxlength="10" size="10" 
								title="%{#attr.headerValueDataScadenza} %{#attr.headerValueDa}" autocomplete="off" />
					<label><wp:i18n key="LABEL_A_DATA" />: </label>
					<s:textfield name="model.dataScadenzaA" id="model.dataScadenzaA" cssClass="text" 
								value="%{#searchForm.dataScadenzaA}" maxlength="10" size="10" 
								title="%{#attr.headerValueDataScadenza} %{#attr.headerValueA}" autocomplete="off" />
					 (<wp:i18n key="LABEL_FORMATO_DATA" />)
				</div>
			</div>
			
			
			<c:if test="${visFiltroAltriSoggetti}">
				<div class="fieldset-row last-row">
					<wp:i18n key="OPT_CHOOSE_CUC_AGISCE_PER_CONTO" var="headerValueCUC"/>
					<div class="label">
						<label for="model.altriSoggetti"><wp:i18n key="LABEL_CUC_AGISCE_PER_CONTO" /> : </label>
					</div>
					<div class="element">
						<s:select name="model.altriSoggetti" id="model.altriSoggetti" list="maps['tipoAltriSoggetti']" 
								value="%{#searchForm.altriSoggetti}" headerKey="" headerValue="%{#attr.headerValueCUC}" autocomplete="off" >
						</s:select>
					</div>
				</div>
			</c:if>
	
			<div class="azioni">
				<wp:i18n key="BUTTON_SEARCH" var="valueSearchButton" />
				<s:submit value="%{#attr.valueSearchButton}" cssClass="button block-ui"/>
			</div>
		</fieldset>

		<c:if test="${listaAvvisi ne null}">
			<jsp:include page="listaAvvisi.jsp" />
		</c:if>
	</form>
</div>