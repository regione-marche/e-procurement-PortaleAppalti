<%@ taglib prefix="wp"   uri="aps-core.tld" %>
<%@ taglib prefix="wpsa" uri="apsadmin-core.tld" %>
<%@ taglib prefix="c"    uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"    uri="/struts-tags" %>
<%@ taglib prefix="es"   uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>

<s:set var="searchForm" value="%{#session.formSearchContratti}" />

<es:getAppParam name = "denominazioneStazioneAppaltanteUnica" var = "stazAppUnica" scope = "page"/> 	

<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/send_blockUI.jsp" />

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-list">

	<h2><wp:i18n key="TITLE_PAGE_CONTRATTI_ORDINI" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_RICERCA_CONTRATTI"/>
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />

	<form action="<wp:action path="/ExtStr2/do/FrontEnd/Contratti/searchContratti.action" />" method="post" >
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
		
		<fieldset>
			<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_SEARCH_CRITERIA" /></legend>

			<div class="fieldset-row first-row">
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
					<s:textfield name="model.oggetto" id="model.oggetto" value="%{#searchForm.oggetto}" 
											 cssClass="text" size="50" />
				</div>
			</div>

			<div class="fieldset-row last-row">
				<div class="label">
					<label for="model.cig"><wp:i18n key="LABEL_CIG" /> : </label>
				</div>
				<div class="element">
					<s:textfield name="model.cig" id="model.cig" cssClass="text" value="%{#searchForm.cig}" 
											 size="50" maxlength="10" />
				</div>
			</div>
			
			<div class="azioni">
				<wp:i18n key="BUTTON_SEARCH" var="valueSearchButton" />
				<s:submit value="%{#attr.valueSearchButton}" cssClass="button block-ui"/>
			</div>
		</fieldset>

		<c:if test="${listaContratti ne null}">

			<c:if test="${! empty dataUltimoAggiornamento}">
				<div class="align-right important last-update-list">
					<wp:i18n key="LABEL_LAST_UPDATE" />	<s:date name="dataUltimoAggiornamento" format="dd/MM/yyyy" />
				</div>
			</c:if>

			<wpsa:subset source="listaContratti" count="10" objectName="groupContratti" advanced="true" offset="5">

				<s:set name="group" value="#groupContratti" />
				<s:include value="/WEB-INF/plugins/ppcommon/aps/jsp/pager_info.jsp" />

				<s:iterator var="contratto">

					<div class="list-item">
						<div class="list-item-row">
							<label><wp:i18n key="LABEL_STAZIONE_APPALTANTE" /> : </label>
							<c:choose>
								<c:when test="${! empty stazAppUnica }">
									<s:set var="stazAppUnicaToStruts">${stazAppUnica}</s:set>
									<s:property value="stazAppUnicaToStruts" />
								</c:when>
								<c:otherwise>
									<s:property value="stazioneAppaltante" />
								</c:otherwise>
							</c:choose>
						</div>

						<div class="list-item-row">
							<label><wp:i18n key="LABEL_TITOLO" /> : </label>
							<s:property value="oggetto" />
						</div>

						<div class="list-item-row">
							<label><wp:i18n key="LABEL_CIG" /> : </label>
							<s:property value="cig" />
						</div>

						<div class="list-item-row">
							<label><wp:i18n key="LABEL_IMPORTO" /> : </label>
							<s:if test="%{#contratto.importo != null}">
								<s:text name="format.money"><s:param value="importo"/></s:text> &euro;
							</s:if>
						</div>

						<div class="list-item-row">
							<label><wp:i18n key="LABEL_RIFERIMENTO_PROCEDURA" /> : </label>
							<s:property value="codice" />
						</div>
			
						<div class="list-action">
							<c:choose>
								<c:when test="${skin == 'highcontrast' || skin == 'text'}">
									<a href='<wp:action path="/ExtStr2/do/FrontEnd/Contratti/view.action" />&amp;codice=<s:property value="codice"/>' title="<wp:i18n key="LINK_VIEW_DETAIL" />">
										<wp:i18n key="LINK_VIEW_DETAIL" />
									</a>
								</c:when>
								<c:otherwise>
									<a href='<wp:action path="/ExtStr2/do/FrontEnd/Contratti/view.action" />&amp;codice=<s:property value="codice"/>' class="bkg detail-very-big" title="<wp:i18n key="LINK_VIEW_DETAIL" />">
										<wp:i18n key="LINK_VIEW_DETAIL" />
									</a>
								</c:otherwise>
							</c:choose>
						</div>
					</div>
				</s:iterator>

				<s:include value="/WEB-INF/plugins/ppcommon/aps/jsp/pager_navigation.jsp" />
			</wpsa:subset>
		</c:if>
	</form>
</div>