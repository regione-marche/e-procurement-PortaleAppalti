<%@ taglib prefix="wp"   uri="aps-core.tld" %>
<%@ taglib prefix="wpsa" uri="apsadmin-core.tld" %>
<%@ taglib prefix="c"    uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"    uri="/struts-tags" %>
<%@ taglib prefix="es"   uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>
<%@ taglib prefix="fn"   uri="http://java.sun.com/jsp/jstl/functions" %>

<%-- <es:checkCustomization var="visTabIndicizzazione" objectId="TABINF-INDICIZZAZIONE" attribute="TAB" feature="VIS" /> --%>
<es:getAppParam name = "denominazioneStazioneAppaltanteUnica" var = "stazAppUnica" scope = "page"/>


<c:if test="${! empty dataUltimoAggiornamento}">
	<div class="align-right important last-update-list">
		<wp:i18n key="LABEL_LAST_UPDATE" />	<s:date name="dataUltimoAggiornamento" format="dd/MM/yyyy" />
	</div>
</c:if>


<s:if test="%{listaAvvisi.dati.size > 0}">
	<div class="list-summary">
		<wp:i18n key="SEARCH_RESULTS_INTRO" />
		<s:property value="model.iTotalDisplayRecords" />
		<wp:i18n key="SEARCH_RESULTS_OUTRO" />.
	</div>
		
	<s:iterator var="avviso" value="listaAvvisi.dati" status="status">
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
				<label><wp:i18n key="LABEL_TIPO_AVVISO_GENERALE" /> : </label>
				<s:iterator value="maps['tipiAvvisoGenerali']">
					<s:if test="%{key == tipoAvvisoGenerale}"><s:property value="%{value}"/></s:if>
				</s:iterator>
			</div>

			<div class="list-item-row">
				<label><wp:i18n key="LABEL_TITOLO" /> : </label>
				<s:property value="oggetto" />
			</div>

			<div class="list-item-row">
				<label><wp:i18n key="LABEL_DATA_PUBBLICAZIONE_AVVISO" /> : </label>
				<s:date name="dataPubblicazione" format="dd/MM/yyyy" />
			</div>

			<div class="list-item-row">
				<label><wp:i18n key="LABEL_DATA_SCADENZA_AVVISO" /> : </label>
				<s:date name="dataScadenza" format="dd/MM/yyyy" />
				<s:if test="%{#avviso.oraScadenza != null}">
					<wp:i18n key="LABEL_ENTRO_LE_ORE" /> <s:property value="#avviso.oraScadenza" />
				</s:if>
			</div>
			
			<div class="list-item-row">
				<label><wp:i18n key="LABEL_RIFERIMENTO_PROCEDURA" /> : </label>
				<s:property value="codice" />
			</div>

			<div class="list-item-row">
				<label><wp:i18n key="LABEL_STATO_GARA" /> : </label>
				<s:iterator value="maps['statiAvviso']">
					<s:if test="%{key == stato}"><s:property value="%{value}"/></s:if>
				</s:iterator>
			</div>

			<div class="list-action">
				<c:choose>
					<c:when test="${skin == 'highcontrast' || skin == 'text'}">
						<a href='<wp:action path="/ExtStr2/do/FrontEnd/Avvisi/viewAvvisoComunicazioneAtto.action" />&amp;codice=<s:property value="codice"/>' 
						   title='<wp:i18n key="LINK_VIEW_DETAIL" />'>
									<wp:i18n key="LINK_VIEW_DETAIL" />
						</a>
					</c:when>
					<c:otherwise>
						<a href='<wp:action path="/ExtStr2/do/FrontEnd/Avvisi/viewAvvisoComunicazioneAtto.action" />&amp;codice=<s:property value="codice"/>' 
						   title='<wp:i18n key="LINK_VIEW_DETAIL" />' class="bkg detail-very-big">
							<wp:i18n key="LINK_VIEW_DETAIL" />
						</a>
					</c:otherwise>
				</c:choose>

			</div>
		</div>
	</s:iterator>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/pagination.jsp"></jsp:include>
</s:if>
<s:else>
	<%-- Accessibility Fix Criterion 3.2.2: insert an invisible "submit" button as workaraound --%>
	<input disabled="disabled" type="submit" style="display:none;"/>
	<div class="list-summary">
		<wp:i18n key="SEARCH_RESULTS_INTRO" />
		<s:property value="model.iTotalDisplayRecords" />
		<wp:i18n key="SEARCH_RESULTS_OUTRO" />.
	</div>
</s:else>