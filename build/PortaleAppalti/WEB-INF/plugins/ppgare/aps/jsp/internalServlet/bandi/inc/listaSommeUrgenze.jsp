<%@ taglib prefix="wp"   uri="aps-core.tld" %>
<%@ taglib prefix="wpsa" uri="apsadmin-core.tld" %>
<%@ taglib prefix="c"    uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"    uri="/struts-tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%-- prepara il BACK LINK della pagina di visualizzazione della firma digitale --%>
<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/backFromPageFirmaDigitale.jsp"/>


<s:if test="%{listaSommeUrgenze.dati.size > 0}">
	<div class="list-summary">
		<wp:i18n key="SEARCH_RESULTS_INTRO" />
		<s:property value="model.iTotalDisplayRecords" />
		<wp:i18n key="SEARCH_RESULTS_OUTRO" />.
	</div>
		
	<s:iterator var="riga" value="listaSommeUrgenze.dati" status="status">
		<div class="list-item">
			<div class="list-item-row">
				<label><wp:i18n key="LABEL_TITOLO" /> : </label>
				<s:property value="oggetto" />
			</div>

			<div class="list-item-row">
				<label><wp:i18n key="LABEL_DATA_PUBBLICAZIONE_BANDO" /> : </label>
				<s:date name="dataPubblicazione" format="dd/MM/yyyy" />
			</div>

			<div class="list-item-row">
				<label><wp:i18n key="LABEL_CODICE" /> : </label>
				<s:property value="codice" />
			</div>
			
			<c:if test="${! empty cig}">
				<div class="list-item-row">
					<label><wp:i18n key="LABEL_CIG" /> : </label>
					<s:property value="cig" />
				</div>
			</c:if>
			
			<div class="list-item-row">
				<label><wp:i18n key="LABEL_STATO" /> : </label>
				<s:property value="stato" />
			</div>	

			<div class="list-item-row">
				<label><wp:i18n key="LABEL_TIPO_PROCEDURA" /> : </label>
				<s:property value="tipoProcedura" />
			</div>
			
			<div class="list-item-row">
				<label><wp:i18n key="LABEL_IMPORTO" /> : </label>
				<s:property value="importo" />
			</div>
		
		
			<div class="list-action">
			
				<c:set var="url" value="" />
				<c:choose>
					<c:when test="${dataEsito != null}">
						<c:set var="url"><wp:action path="/ExtStr2/do/FrontEnd/Esiti/view.action" />&amp;codice=<s:property value="codice"/>&amp;${tokenHrefParams}</c:set>
					</c:when>
					<c:when test="${dataPubblicazione != null}">
						<c:set var="url"><wp:action path="/ExtStr2/do/FrontEnd/Bandi/view.action" />&amp;codice=<s:property value="codice"/>&amp;${tokenHrefParams}</c:set>
					</c:when>
				</c:choose>
		
				<c:if test="${! empty url}">
					<c:choose>
						<c:when test="${skin == 'highcontrast' || skin == 'text'}">
							<a href='${url}' title='<wp:i18n key="LINK_VIEW_DETAIL" />'>
								<wp:i18n key="LINK_VIEW_DETAIL" />
							</a>
						</c:when>
						<c:otherwise>
							<a href='${url}' title='<wp:i18n key="LINK_VIEW_DETAIL" />' class="bkg detail-very-big">
								<wp:i18n key="LINK_VIEW_DETAIL" />
							</a>
						</c:otherwise>
					</c:choose>
				</c:if>
			</div>
		</div>
	</s:iterator>

	<form action='<wp:action path="/ExtStr2/do/FrontEnd/Bandi/listSommeUrgenze.action" />' method="post">
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
		<input type="hidden" name="last" value="1" />
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/pagination.jsp"></jsp:include>
	</form>
</s:if>
<s:else>
	<div class="list-summary">
		<wp:i18n key="SEARCH_RESULTS_INTRO" /> 0 <wp:i18n key="SEARCH_RESULTS_OUTRO" />.
	</div>
</s:else>
