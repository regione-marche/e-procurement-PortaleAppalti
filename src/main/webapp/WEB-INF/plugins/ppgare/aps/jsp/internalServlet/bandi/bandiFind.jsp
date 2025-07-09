<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>

<es:checkCustomization var="visFiltroAltriSoggetti" objectId="GARE" attribute="FILTROCUC" feature="VIS" />

<s:set var="searchForm" value="%{#session.formSearchBandi}" />

<es:getAppParam name="denominazioneStazioneAppaltanteUnica" var="stazAppUnica" scope = "page"/> 	

<c:set var="cigVisibile"  value="${param.cigVisibile}"/>


<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/send_blockUI.jsp" />

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-list">

	<h2><wp:i18n key="TITLE_PAGE_RICERCA_BANDI" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_RICERCA_BANDI"/>
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />

	<form action="<wp:action path="/ExtStr2/do/FrontEnd/Bandi/search.action" />" method="post" >
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
		
		<fieldset>
			<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_SEARCH_CRITERIA" /></legend>
			
			<div class="fieldset-row first-row special">
				<div class="label">
					<label><wp:i18n key="LABEL_SEARCH_FOR" /> : </label>
				</div>
				<div class="element">
					<span class="important"><wp:i18n key="LABEL_BANDI" /></span>
					&nbsp;
					<a href="<wp:action path="/ExtStr2/do/FrontEnd/Avvisi/openSearch.action" />" title="<wp:i18n key="LABEL_GO_TO_SEARCH_AVVISI" />"><wp:i18n key="LABEL_AVVISI" /></a>
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
									cssStyle="width: 100%;" >
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
											 size="50" />
				</div>
			</div>
	
			<c:if test="${cigVisibile}">
				<div class="fieldset-row">
					<div class="label">
						<label for="model.cig"><wp:i18n key="LABEL_CIG" /> : </label>
					</div>
					<div class="element">
						<s:textfield name="model.cig" id="model.cig" cssClass="text" value="%{#searchForm.cig}" 
												 size="50" maxlength="10" />
					</div>
				</div>
			</c:if>

			<div class="fieldset-row">
				<div class="label">
					<label for="model.tipoAppalto"><wp:i18n key="LABEL_TIPO_APPALTO" /> : </label>
				</div>
				<div class="element">
					<wp:i18n key="OPT_CHOOSE_TIPO_APPALTO" var="headerValueTipoAppalto" />
					<s:select name="model.tipoAppalto" id="model.tipoAppalto" list="maps['tipiAppalto']" value="%{#searchForm.tipoAppalto}"
										headerKey="" headerValue="%{#attr.headerValueTipoAppalto}" >
					</s:select>
				</div>
			</div>
	
			<div class="fieldset-row">
				<div class="label">
					<label><wp:i18n key="LABEL_DATA_PUBBLICAZIONE_BANDO" /> : </label>
				</div>
				<div class="element">
					<wp:i18n key="LABEL_DATA_PUBBLICAZIONE_BANDO" var="headerValueDataPubblicazione"/>
					<wp:i18n key="LABEL_DA_DATA" var="headerValueDa"/>
					<wp:i18n key="LABEL_A_DATA" var="headerValueA"/>
					<label><wp:i18n key="LABEL_DA_DATA" />: </label>
					<s:textfield name="model.dataPubblicazioneDa" id="model.dataPubblicazioneDa" cssClass="text" 
											 value="%{#searchForm.dataPubblicazioneDa}"
											 title="%{#attr.headerValueDataPubblicazione} %{#attr.headerValueDa}"  
											 maxlength="10" size="10" />
					<label><wp:i18n key="LABEL_A_DATA" />: </label>
					<s:textfield name="model.dataPubblicazioneA" id="model.dataPubblicazioneA" cssClass="text" 
											 value="%{#searchForm.dataPubblicazioneA}" 
											 title="%{#attr.headerValueDataPubblicazione} %{#attr.headerValueA}"
											 maxlength="10" size="10" />
					(<wp:i18n key="LABEL_FORMATO_DATA" />)
				</div>
			</div>
	
			<div class="fieldset-row">
				<div class="label">
					<label><wp:i18n key="LABEL_DATA_SCADENZA_BANDO" /> : </label>
				</div>
				<div class="element">
					<wp:i18n key="LABEL_DATA_SCADENZA_BANDO" var="headerValueDataScadenza"/>
					<label><wp:i18n key="LABEL_DA_DATA" />: </label>
					<s:textfield name="model.dataScadenzaDa" id="model.dataScadenzaDa" cssClass="text" 
											 value="%{#searchForm.dataScadenzaDa}" 
											 title="%{#attr.headerValueDataScadenza} %{#attr.headerValueDa}"  
											 maxlength="10" size="10" />
					<label><wp:i18n key="LABEL_A_DATA" />: </label>
					<s:textfield name="model.dataScadenzaA" id="model.dataScadenzaA" cssClass="text" 
											 value="%{#searchForm.dataScadenzaA}" 
											 title="%{#attr.headerValueDataScadenza} %{#attr.headerValueA}"  
											 maxlength="10" size="10" />
					 (<wp:i18n key="LABEL_FORMATO_DATA" />)
				</div>
			</div>

			<div class="fieldset-row">
				<div class="label">
					<label for="model.codice"><wp:i18n key="LABEL_RIFERIMENTO_PROCEDURA" /> : </label>
				</div>
				<div class="element">
					<s:textfield name="model.codice" id="model.codice" cssClass="text"
								value="%{#searchForm.codice}"
								size="20" maxlength="20"/>
				</div>
			</div>
	
			<%--
			<div class="fieldset-row">
				<div class="label">
					<label for="model.proceduraTelematica">Procedura telematica : </label>
				</div>
				<div class="element">
					<s:select name="model.proceduraTelematica" id="model.proceduraTelematica" list="maps['sino']" 
										value="%{#searchForm.proceduraTelematica}" headerKey="" headerValue="" >
					</s:select>
				</div>
			</div>
			--%>
				
			<div class="fieldset-row">
				<div class="label">
					<label for="model.stato"><wp:i18n key="LABEL_STATO_GARA" /> : </label>
				</div>
				<div class="element">
					<wp:i18n key="OPT_CHOOSE_STATO_GARA" var="headerValueStatoGara" />
					<s:select name="model.stato" id="model.stato" list="maps['statiGara']" value="%{#searchForm.stato}"
										headerKey="" headerValue="%{#attr.headerValueStatoGara}" >
					</s:select>
				</div>
			</div>

			<c:if test="${visFiltroAltriSoggetti}">
				<div class="fieldset-row">
					<div class="label">
						<label for="model.altriSoggetti"><wp:i18n key="LABEL_CUC_AGISCE_PER_CONTO" var="headerValueCUG"/> : </label>
					</div>
					<wp:i18n key="OPT_CHOOSE_CUC_AGISCE_PER_CONTO" var="headerValueCUC"/>
					<div class="element">
						<s:select name="model.altriSoggetti" id="model.altriSoggetti" list="maps['tipoAltriSoggetti']" 
								value="%{#searchForm.altriSoggetti}" headerKey="" headerValue="%{#attr.headerValueCUC}" >
						</s:select>
					</div>
				</div>
			</c:if>
			
			<div class="fieldset-row">
				<div class="label">
					<label for="model.sommaUrgenza"><wp:i18n key="LABEL_SOMMA_URGENZA" /> : </label>
				</div>
				<div class="element">
					<s:select name="model.sommaUrgenza" id="model.sommaUrgenza" list="maps['sino']" 
										value="%{#searchForm.sommaUrgenza}" headerKey="" headerValue="" >
					</s:select>
				</div>
			</div>
			
			<div class="fieldset-row  last-row">
            	<div class="label">
                	<label for="model.orderCriteria"><wp:i18n key="LABEL_ORDER_CRITERIA" /> : </label>
 				</div>
                <div class="element">
                	<s:select name="model.orderCriteria" id="model.orderCriteria" list="maps['orderCriteria']"
                    					value="%{#searchForm.orderCriteria}"
                                        headerKey="" headerValue="" >
					</s:select>
           		</div>
            </div>

			<div class="azioni">
				<wp:i18n key="BUTTON_SEARCH" var="valueSearchButton" />
				<s:submit value="%{#attr.valueSearchButton}" cssClass="button block-ui"/>
			</div>
		</fieldset>
		
		<c:if test="${listaBandi ne null}">
			<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/bandi/inc/listaBandi.jsp" >
				<jsp:param name="cigVisibile" value="${cigVisibile}" />	
			</jsp:include>
		</c:if>
	</form>
		
</div>