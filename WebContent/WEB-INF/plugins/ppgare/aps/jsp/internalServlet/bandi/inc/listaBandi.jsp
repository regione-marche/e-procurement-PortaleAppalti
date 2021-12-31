<%@ taglib prefix="wp"   uri="aps-core.tld" %>
<%@ taglib prefix="wpsa" uri="apsadmin-core.tld" %>
<%@ taglib prefix="c"    uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"    uri="/struts-tags" %>
<%@ taglib prefix="es"   uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>
<%@ taglib prefix="fn"   uri="http://java.sun.com/jsp/jstl/functions" %>

<es:checkCustomization var="visTabIndicizzazione" objectId="TABINF-INDICIZZAZIONE" attribute="TAB" feature="VIS" />

<c:set var="cigVisibile"  value="${param.cigVisibile}"/>

<c:if test="${! empty dataUltimoAggiornamento}">
	<div class="align-right important last-update-list">
		<wp:i18n key="LABEL_LAST_UPDATE" />	<s:date name="dataUltimoAggiornamento" format="dd/MM/yyyy" />
	</div>
</c:if>


<s:if test="%{listaBandi.dati.size > 0}">
	<div class="list-summary">
		<wp:i18n key="SEARCH_RESULTS_INTRO" />
		<s:property value="model.iTotalDisplayRecords" />
		<wp:i18n key="SEARCH_RESULTS_OUTRO" />.
	</div>
		
	<s:iterator var="bando" value="listaBandi.dati" status="status">
		<div class="list-item">
			<div class="list-item-row">
				<label><wp:i18n key="LABEL_STAZIONE_APPALTANTE" /> : </label>
				<s:property value="stazioneAppaltante" />
			</div>

			<div class="list-item-row">
				<label><wp:i18n key="LABEL_TITOLO" /> : </label>
				<s:property value="oggetto" />
			</div>
			
			<div class="list-item-row">
				<label><wp:i18n key="LABEL_TIPO_APPALTO" /> : </label>
				<s:property value="tipoAppalto" />
			</div>

			<c:if test="${esitoVisibile}">
				<c:if test="${! empty cig}">
					<div class="list-item-row">
						<label><wp:i18n key="LABEL_CIG" /> : </label>
						<s:property value="cig" />
					</div>
				</c:if>
			</c:if>
			
			<div class="list-item-row">
				<label><wp:i18n key="LABEL_IMPORTO_BANDO" /> : </label>
				<s:if test="%{#bando.importo != null}">
					<s:text name="format.money"><s:param value="importo"/></s:text> &euro;
				</s:if>
			</div>
			
			<div class="list-item-row">
				<label><wp:i18n key="LABEL_DATA_PUBBLICAZIONE_BANDO" /> : </label>
				<s:date name="dataPubblicazione" format="dd/MM/yyyy" />
			</div>
			
			<div class="list-item-row">
				<label><wp:i18n key="LABEL_DATA_SCADENZA_BANDO" /> : </label>
				<s:date name="#bando.dataTermine" format="dd/MM/yyyy" />
				<s:if test="%{#bando.oraTermine != null}">
					<wp:i18n key="LABEL_ENTRO_LE_ORE" /> <s:property value="#bando.oraTermine" />
				</s:if>
			</div>
			
			<%--
			<div class="list-item-row">
				<label>Procedura telematica :</label>
				<s:if test="%{#bando.proceduraTelematica}">
					SI
					<s:if test="%{#bando.astaElettronica}">
						, con ricorso all'asta elettronica
					</s:if>						
				</s:if>
				<s:else>NO</s:else>				
			</div>
			 --%>
			
			<div class="list-item-row">
				<label><wp:i18n key="LABEL_RIFERIMENTO_PROCEDURA" /> :</label>
				<s:property value="#bando.codice" />
			</div>
			
			<div class="list-item-row">
				<label><wp:i18n key="LABEL_STATO_GARA" /> : </label>
				<s:if test="%{#bando.stato != null}">
					<s:property value="stato" /> <s:if test="%{#bando.esito != null}"> - <s:property value="#bando.esito" /></s:if>
				</s:if>
			</div>
			
			<div class="list-action">
				<c:choose>
					<c:when test="${skin == 'highcontrast' || skin == 'text'}">
						<a href='<wp:action path="/ExtStr2/do/FrontEnd/Bandi/view.action" />&amp;codice=<s:property value="codice"/>&amp;${tokenHrefParams}' 
						   title='<wp:i18n key="LINK_VIEW_DETAIL" />'>
							<wp:i18n key="LINK_VIEW_DETAIL" />
						</a>
					</c:when>
					<c:otherwise>
						<a href='<wp:action path="/ExtStr2/do/FrontEnd/Bandi/view.action" />&amp;codice=<s:property value="codice"/>&amp;${tokenHrefParams}' 
						   title='<wp:i18n key="LINK_VIEW_DETAIL" />' class="bkg detail-very-big">
							<wp:i18n key="LINK_VIEW_DETAIL" />
						</a>
					</c:otherwise>
				</c:choose>

				<c:if test="${visTabIndicizzazione && 
				              (bando.iterGara != 3 && bando.iterGara != 5 && bando.iterGara != 6) && 
							  (sessionScope.fromPage eq 'listAllInCorso' || sessionScope.fromPage eq 'listAllScaduti' || sessionScope.fromPage eq 'search')}">
					<c:choose>
						<c:when test="${skin == 'highcontrast' || skin == 'text'}">
							<a href='<wp:url page="ppgare_tab_inf_dpcm" />?codice=<s:property value="codice"/>&amp;tipo=Bando&amp;${tokenHrefParams}' 
							   title="<wp:i18n key="LINK_VIEW_INDEXING_INFORMATION_TABLE" />">
								<wp:i18n key="LINK_VIEW_INDEXING_INFORMATION_TABLE" />
							</a>
						</c:when>
						<c:otherwise>
							<a href='<wp:url page="ppgare_tab_inf_dpcm" />?codice=<s:property value="codice"/>&amp;tipo=Bando&amp;${tokenHrefParams}' 
							   title="<wp:i18n key="LINK_VIEW_INDEXING_INFORMATION_TABLE" />" class="bkg table">
								<wp:i18n key="LINK_VIEW_INDEXING_INFORMATION_TABLE" />
							</a>
						</c:otherwise>
					</c:choose>
				</c:if>
			</div>
		</div>
			
	</s:iterator>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/pagination.jsp"></jsp:include>
</s:if>
<s:else>
	<div class="list-summary">
		<wp:i18n key="SEARCH_RESULTS_INTRO" />
		<s:property value="model.iTotalDisplayRecords" />
		<wp:i18n key="SEARCH_RESULTS_OUTRO" />.
	</div>
</s:else>