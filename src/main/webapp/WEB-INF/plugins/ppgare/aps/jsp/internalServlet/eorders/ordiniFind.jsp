<%@ taglib prefix="wp"   uri="aps-core.tld" %>
<%@ taglib prefix="wpsa" uri="apsadmin-core.tld" %>
<%@ taglib prefix="c"    uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"    uri="/struts-tags" %>
<%@ taglib prefix="es"   uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>

<%-- 
public static final String SESSION_ID_SEARCH_NSO = "formSearchNSO";
private static final String FROM_PAGE_OWNER		 = "searchordini";
--%>
<es:getAppParam name = "denominazioneStazioneAppaltanteUnica" var = "stazAppUnica" scope = "page"/> 	
	
<c:set var="listOrdiniDaValutare" value="false"/>
<c:set var="listOrdiniConfermati" value="false"/>
<c:set var="listOrdiniTutti" value="false"/>
<c:choose>
	<c:when test="${sessionScope.fromPage eq 'listordinidavalutare'}">
		<c:set var="codiceTitolo" value="TITLE_PAGE_EORDERS_DA_VALUTARE"/>
		<c:set var="codiceBalloon" value="BALLOON_EORDERS_RICERCA_DA_VALUTARE"/>
		<s:set var="searchForm" value="%{#session.formListNSODaValutare}" />
		<c:set var="formAction" value="ordiniDaValutare"/>
		<c:set var="listOrdiniDaValutare" value="true"/>	
	</c:when>
	<c:when test="${sessionScope.fromPage eq 'listordiniconfermati'}">
		<c:set var="codiceTitolo" value="TITLE_PAGE_EORDERS_CONFERMATI"/>
		<c:set var="codiceBalloon" value="BALLOON_EORDERS_RICERCA_CONFERMATI"/>
		<s:set var="searchForm" value="%{#session.formListNSOConfermati}" />
		<c:set var="formAction" value="ordiniConfermati"/>
		<c:set var="listOrdiniConfermati" value="true"/>	
	</c:when>
	<c:when test="${sessionScope.fromPage eq 'listordinitutti'}">
		<c:set var="codiceTitolo" value="TITLE_PAGE_EORDERS_TUTTI"/>
		<c:set var="codiceBalloon" value="BALLOON_EORDERS_RICERCA_TUTTI"/>
		<s:set var="searchForm" value="%{#session.formListNSOAll}" />
		<c:set var="formAction" value="ordiniTutti"/>
		<c:set var="listOrdiniTutti" value="true"/>	
	</c:when>
</c:choose>

	
<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/send_blockUI.jsp" />

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-list">

	<h2><wp:i18n key="${codiceTitolo}" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="${codiceBalloon}"/>
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
	
	<form action="<wp:action path="/ExtStr2/do/FrontEnd/EOrders/${formAction}.action" />" method="post" >
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
		
		<%-- FILTRI DI RICERCA --%>
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
					<label for="model.cig"><wp:i18n key="LABEL_CIG" /> : </label>
				</div>
				<div class="element">
					<s:textfield name="model.cig" id="model.cig" cssClass="text" value="%{#searchForm.cig}" 
											 size="50" maxlength="10" />
				</div>
			</div>
			
			<div class="fieldset-row">
				<div class="label">
					<label for="model.gara"><wp:i18n key="LABEL_RIFERIMENTO_PROCEDURA" /> : </label>
				</div>
				<div class="element">
					<s:textfield name="model.gara" id="model.gara" cssClass="text" value="%{#searchForm.gara}" 
											 size="50" maxlength="10" />
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
	
			<c:if test="${listOrdiniTutti}">
				<div class="fieldset-row">
					<div class="label">
						<label for="model.stato"><wp:i18n key="LABEL_STATO_ORDINE" /> : </label>
					</div>
					<div class="element">
						<wp:i18n key="OPT_CHOOSE_STATO_ORDINE" var="headerValueStato" />
						<s:select name="model.stato" id="model.stato" list="maps['statiOrdineNso']" 
								value="%{#searchForm.stato}" 
								headerKey="" headerValue="%{#attr.headerValueStato}" 
								cssStyle="width: 100%;" >							
						</s:select>		
					</div>
				</div>			
			</c:if>	
			
			<div class="azioni">
				<wp:i18n key="BUTTON_SEARCH" var="valueSearchButton" />
				<s:submit value="%{#attr.valueSearchButton}" cssClass="button block-ui"/>
			</div>
		</fieldset>


		<%-- LISTA DEI RISULTATI --%>
		<s:if test="%{listaOrdini.dati.size > 0}">
			<div class="list-summary">
				<wp:i18n key="SEARCH_RESULTS_INTRO" />
				<s:property value="model.iTotalDisplayRecords" />
				<wp:i18n key="SEARCH_RESULTS_OUTRO" />.
			</div>
				
			<s:iterator var="ordine" value="listaOrdini.dati" status="status">

				<div class="list-item">
					<div class="list-item-row">
						<label><wp:i18n key="LABEL_STAZIONE_APPALTANTE" /> : </label>
						<c:choose>
							<c:when test="${! empty stazAppUnica }">
								<s:set var="stazAppUnicaToStruts">${stazAppUnica}</s:set>
								<s:property value="stazAppUnicaToStruts" />
							</c:when>
							<c:otherwise>
								<s:iterator value="maps['stazioniAppaltanti']">
									<s:if test="%{key == contractingAuthority}">
										<s:property value="%{value}"/>
									</s:if>
								</s:iterator>
							</c:otherwise>
						</c:choose>
					</div>
					
					<c:if test="${! empty cig}">
						<div class="list-item-row">
							<label><wp:i18n key="LABEL_CIG" /> : </label>
							<s:property value="cig" />
						</div>
					</c:if>
					
					<div class="list-item-row">
						<label><wp:i18n key="LABEL_RIFERIMENTO_PROCEDURA" /> :</label>
						<s:property value="tender" />
					</div>
					
					<div class="list-item-row">
						<!-- LABEL_IMPORTO -->
						<label><wp:i18n key="LABEL_IMPORTO_BANDO" /> : </label>
						<s:if test="%{totalPriceWithVat != null}">
							<s:text name="format.money"><s:param value="totalPriceWithVat"/></s:text> &euro;
						</s:if>
					</div>
					
					<div class="list-item-row">
						<label><wp:i18n key="LABEL_DATA_EMISSIONE" /> : </label>
						<s:date name="???" format="dd/MM/yyyy" />
					</div>
					
					<div class="list-item-row">
						<label><wp:i18n key="LABEL_DATA_SCADENZA_BANDO" /> : </label>
						<s:date name="expiryDate" format="dd/MM/yyyy" />
					</div>
					
					<div class="list-action">
						<c:choose>
							<c:when test="${skin == 'highcontrast' || skin == 'text'}">
								<a href='<wp:action path="/ExtStr2/do/FrontEnd/EOrders/view.action" />&amp;id=<s:property value="id"/>' title="<wp:i18n key="LINK_VIEW_DETAIL" />">
									<wp:i18n key="LINK_VIEW_DETAIL" />
								</a>
							</c:when>
							<c:otherwise>
								<a href='<wp:action path="/ExtStr2/do/FrontEnd/EOrders/view.action" />&amp;id=<s:property value="id"/>' class="bkg detail-very-big" title="<wp:i18n key="LINK_VIEW_DETAIL" />">
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
		
	</form>
</div>