<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>	

<s:set var="searchForm" value="%{#session.formSearchStipuleContratto}" />


<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/send_blockUI.jsp" />

<es:getAppParam name="denominazioneStazioneAppaltanteUnica" var="stazAppUnica" scope = "page"/> 	

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>
		
<div class="portgare-view">
	<h2><wp:i18n key="TITLE_PAGE_LISTA_STIPULE" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_LISTA_STIPULE"/>
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
	
	<form action="<wp:action path="/ExtStr2/do/FrontEnd/Contratti/searchStipulaContratti.action" />" method="post">
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
					<label for="model.codstipula"><wp:i18n key="LABEL_COD_STIPULA" />: </label>
				</div>
				<div class="element">
					<s:textfield name="model.codstipula" id="model.codstipula" cssClass="text" value="%{#searchForm.codstipula}" 
											 maxlength="50" size="30" />
				</div>
			</div>	
			<div class="fieldset-row">
				<div class="label">
					<label for="model.oggetto"><wp:i18n key="LABEL_OGGETTO" />: </label>
				</div>
				<div class="element">
					<s:textfield name="model.oggetto" id="model.oggetto" cssClass="text" value="%{#searchForm.oggetto}" 
											 maxlength="50" />
				</div>
			</div>
			<div class="fieldset-row">
				<div class="label">
					<label for="model.stato"><wp:i18n key="LABEL_STATO" />: </label>
				</div>
				<div class="element">
					<wp:i18n key="OPT_CHOOSE_STATO_GARA" var="headerValueStato" />
					<s:select name="model.stato" id="model.stato" list="maps['listaStatiStipule']" 
							value="%{#searchForm.stato}" 
							headerKey="" headerValue="%{#attr.headerValueStato}" 
							cssStyle="width: 100%;" >
					</s:select>
					
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
		<s:if test="%{stipule.size > 0}">
			<s:iterator id="stipula" value="stipule">
				<div class = "list-item">
					<div class = "list-item-row">
						<label><wp:i18n key="LABEL_COD_STIPULA" />: </label>
						<s:property value="codiceStipula" />
					</div>
					<div class = "list-item-row">
						<label><wp:i18n key="LABEL_OGGETTO" />: </label>
						<s:property value="oggetto" />
					</div>
					<div class = "list-item-row">
					
						<label><wp:i18n key="LABEL_IMPORTO_STIPULA" />:</label>
						<s:if test="%{#stipula.importo != null}">
							<s:text name="format.money"><s:param value="importo"/></s:text> &euro;
						</s:if>
					</div>
					<div class = "list-item-row">
						<label><wp:i18n key="LABEL_RIFERIMENTO_PROCEDURA" />: </label>
						<s:property value="codiceGara" />
					</div>
					<div class = "list-item-row">
						<label><wp:i18n key="LABEL_STATO" />: </label>
						<s:property value="descStato" />
					</div>
					<div class="list-action">
						<a href='<wp:action path="/ExtStr2/do/FrontEnd/Contratti/dettaglioStipulaContratti.action" />&amp;id=<s:property value="id"/>&amp;codice=<s:property value="codiceStipula"/>' 
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