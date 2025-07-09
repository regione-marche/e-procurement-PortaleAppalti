<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>	


<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/send_blockUI.jsp" />

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<es:getAppParam name="denominazioneStazioneAppaltanteUnica" var="stazAppUnica" scope = "page"/> 	

		
<div class="portgare-view">

	<h2><wp:i18n key="TITLE_PAGE_LISTA_CONTRATTI_LFS" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_LISTA_CONTRATTI_LFS"/>
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
    <s:set var="searchForm" value="%{#session.formSearchContrattiLFS}" />
	<form action="<wp:action path="/ExtStr2/do/FrontEnd/ContrattiLFS/findContratti.action" />" method="post">
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
		<fieldset>
			<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_SEARCH_CRITERIA" /></legend>

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
							<s:property value="%{stazioneAppaltante}"/>
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
					<label for="model.oggetto"><wp:i18n key="LABEL_OGGETTO" />: </label>
				</div>
				<div class="element">
					<s:textfield name="model.oggetto" id="model.oggetto" cssClass="text" value="%{#searchForm.oggetto}" 
											 maxlength="50" size="30" />
				</div>
			</div>
			<div class="fieldset-row">
				<div class="label">
					<label for="model.codice"><wp:i18n key="LABEL_CODICE_CONTRATTO" />: </label>
				</div>
				<div class="element">
					<s:textfield name="model.codice" id="model.codice" cssClass="text" value="%{#searchForm.codice}" 
											 maxlength="50" size="30" />
				</div>
			</div>	
			<div class="fieldset-row">
				<div class="label">
					<label for="model.cig"><wp:i18n key="LABEL_CIG" />: </label>
				</div>
				<div class="element">
					<s:textfield name="model.cig" id="model.cig" cssClass="text" value="%{#searchForm.cig}" 
											 maxlength="50" size="30" />
				</div>
			</div>
			<div class="fieldset-row">
				<div class="label">
					<label for="model.gara"><wp:i18n key="LABEL_RIFERIMENTO_PROCEDURA" />: </label>
				</div>
				<div class="element">
					<s:textfield name="model.gara" id="model.gara" cssClass="text" value="%{#searchForm.gara}" 
											 maxlength="50" size="30" />
				</div>
			</div>	
			
			<div class="azioni">
				<wp:i18n key="BUTTON_SEARCH" var="valueSearchButton" />
				<s:submit value="%{#attr.valueSearchButton}" cssClass="button block-ui"/>
			</div>
		</fieldset>
		<div class="list-summary">
			<wp:i18n key="SEARCH_RESULTS_INTRO" /> <s:property value="%{model.iTotalRecords}"/> <wp:i18n key="SEARCH_RESULTS_OUTRO" />.
		</div>
		<s:if test="%{contratti.length > 0}">
			<s:iterator id="contratto" value="contratti">
				<div class = "list-item">
					<div class = "list-item-row">
						<label><wp:i18n key="LABEL_CODICE_CONTRATTO" />: </label>
						<s:property value="codice" />/<s:property value="nappal" />
					</div>
					<div class = "list-item-row">
						<label><wp:i18n key="LABEL_OGGETTO" />: </label>
						<s:property value="oggetto" />
					</div>
					<div class = "list-item-row">
						<label><wp:i18n key="LABEL_CIG" />: </label>
						<s:property value="cig" />
					</div>
					<div class = "list-item-row">
					
						<label><wp:i18n key="LABEL_IMPORTO_CONTRATTO" />:</label>
						<s:if test="%{#contratto.importo != null}">
							<s:text name="format.money"><s:param value="importo"/></s:text> &euro;
						</s:if>
					</div>
					<div class = "list-item-row">
						<label><wp:i18n key="LABEL_RIFERIMENTO_PROCEDURA" />: </label>
						<s:property value="ngara" />
					</div>
					<div class="list-action">
						<a href='<wp:action path="/ExtStr2/do/FrontEnd/ContrattiLFS/dettaglio.action" />&amp;nappal=<s:property value="nappal"/>&amp;codice=<s:property value="codice"/>' 
							   title='<wp:i18n key="LINK_VIEW_DETAIL" />' class="bkg detail-very-big">
							<wp:i18n key="LINK_VIEW_DETAIL" />
						</a>
					
					</div>
				</div>	
			</s:iterator>
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/pagination.jsp"></jsp:include>
		</s:if>
	

	</form>
</div>