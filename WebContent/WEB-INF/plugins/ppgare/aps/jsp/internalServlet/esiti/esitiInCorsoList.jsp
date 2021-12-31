<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>

<es:checkCustomization var="visRSS" objectId="TABINF-INDICIZZAZIONE" attribute="RSS" feature="VIS" />
<es:checkCustomization var="visFiltroAltriSoggetti" objectId="GARE" attribute="FILTROCUC" feature="VIS" />

<c:choose>
	<c:when test="${sessionScope.fromPage eq 'listAllInCorso'}">
		<c:set var="codiceTitolo" value="TITLE_PAGE_LISTA_ESITI"/>
		<c:set var="codiceBalloon" value="BALLOON_LISTA_ESITI_IN_CORSO"/>
		<c:set var="rss" value="bandi.xml" />
		<s:set var="searchForm" value="%{#session.formListAllInCorsoEsiti}" />
	</c:when>
	<c:when test="${sessionScope.fromPage eq 'listAllAffidamenti'}">
		<c:set var="codiceTitolo" value="TITLE_PAGE_LISTA_ESITI_AFFIDAMENTI"/>
		<c:set var="codiceBalloon" value="BALLOON_LISTA_ESITI_AFFIDAMENTI"/>
		<s:set var="searchForm" value="%{#session.formListAllAffidamentiEsiti}" />
	</c:when>
</c:choose>


<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/send_blockUI.jsp" />

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-list">

	<h2><wp:i18n key="${codiceTitolo}"/></h2>

	<c:if test="${visRSS && (sessionScope.fromPage eq 'listAllInCorso')}">
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_rss.jsp" >
			<jsp:param name="rss" value="${rss}" />
		</jsp:include>
	</c:if>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="${codiceBalloon}"/>
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />

	<form action="<wp:action path="/ExtStr2/do/FrontEnd/Esiti/${sessionScope.fromPage}.action" />" method="post">
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
		
		<!-- FILTRI DI RICERCA -->
		<fieldset>
			<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_SEARCH_CRITERIA" /></legend>

			<div class="fieldset-row first-row">
				<div class="label">
					<label for="model.stazioneAppaltante"><wp:i18n key="LABEL_STAZIONE_APPALTANTE" /> : </label>
				</div>
				<div class="element">
					<c:choose>
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
					<s:textfield name="model.oggetto" id="model.oggetto" cssClass="text" 
								value="%{#searchForm.oggetto}" size="50" />
				</div>
			</div>

			<div class="fieldset-row">
				<div class="label">
					<label for="model.cig"><wp:i18n key="LABEL_CIG" /> : </label>
				</div>
				<div class="element">
					<s:textfield name="model.cig" id="model.cig" cssClass="text" 
								value="%{#searchForm.cig}" size="50" maxlength="10" />
				</div>
			</div>

			<div class="fieldset-row">
				<div class="label">
					<label for="model.tipoAppalto"><wp:i18n key="LABEL_TIPO_APPALTO" /> : </label>
				</div>
				<div class="element">
					<wp:i18n key="OPT_CHOOSE_TIPO_APPALTO" var="headerValueTipoAppalto" />
					<s:select name="model.tipoAppalto" id="model.tipoAppalto" list="maps['tipiAppalto']" 
							value="%{#searchForm.tipoAppalto}"
							headerKey="" headerValue="%{#attr.headerValueTipoAppalto}" >
					</s:select>
				</div>
			</div>

			<div class="fieldset-row">
				<div class="label">
					<label><wp:i18n key="LABEL_DATA_PUBBLICAZIONE_ESITO" /> : </label>
				</div>
				<div class="element">
					<label for="model.dataPubblicazioneDa"><wp:i18n key="LABEL_DA_DATA" /> : </label>
					<s:textfield name="model.dataPubblicazioneDa" id="model.dataPubblicazioneDa" cssClass="text" 
								value="%{#searchForm.dataPubblicazioneDa}" maxlength="10" size="10" />
					<label for="model.dataPubblicazioneA"><wp:i18n key="LABEL_A_DATA" /> : </label>
					<s:textfield name="model.dataPubblicazioneA" id="model.dataPubblicazioneA" cssClass="text" 
								value="%{#searchForm.dataPubblicazioneA}" maxlength="10" size="10" />
					 (<wp:i18n key="LABEL_FORMATO_DATA" />)
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
			
			<c:if test="${visFiltroAltriSoggetti}">
				<div class="fieldset-row">
					<div class="label">
						<label for="model.altriSoggetti"><wp:i18n key="LABEL_CUC_AGISCE_PER_CONTO" /> : </label>
					</div>
					<div class="element">
						<s:select name="model.altriSoggetti" id="model.altriSoggetti" list="maps['tipoAltriSoggetti']" 
								value="%{#searchForm.altriSoggetti}" headerKey="" headerValue="" >
						</s:select>
					</div>
				</div>
			</c:if>
			
			<div class="fieldset-row ">
				<div class="label">
					<label for="model.sommaUrgenza"><wp:i18n key="LABEL_SOMMA_URGENZA" /> : </label>
				</div>
				<div class="element">
					<s:select name="model.sommaUrgenza" id="model.sommaUrgenza" list="maps['sino']" 
										value="%{#searchForm.sommaUrgenza}" headerKey="" headerValue="" >
					</s:select>
				</div>
			</div>

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
			
			<div class="azioni">
				<wp:i18n key="BUTTON_SEARCH" var="valueSearchButton" />
				<s:submit value="%{#attr.valueSearchButton}" cssClass="button block-ui" />
			</div>
		</fieldset>
	
		<!-- LISTA -->
		<c:if test="${listaEsiti ne null}">
			<jsp:include page="listaEsiti.jsp" />
		</c:if>
		
	</form>
</div>