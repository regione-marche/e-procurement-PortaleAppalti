<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<es:checkCustomization var="visFiltroAltriSoggetti" objectId="GARE" attribute="FILTROCUC" feature="VIS" />

<c:set var="searchForm"  value="${param.searchForm}"/>
<c:set var="proceduraTelematicaVisibile"  value="${param.proceduraTelematicaVisibile}"/>
<c:set var="statoVisibile"  value="${param.statoVisibile}"/>
<c:set var="esitoVisibile"  value="${param.esitoVisibile}"/>
<c:set var="cigVisibile"  value="${param.cigVisibile}"/>

<es:getAppParam name="denominazioneStazioneAppaltanteUnica" var="stazAppUnica" scope = "page"/>

<%-- 
searchForm: ${searchForm}<br/>
proceduraTelemanticaVisibile: ${proceduraTelematicaVisibile}<br/>
statoVisibile: ${statoVisibile}<br/>
esitoVisibile: ${esitoVisibile}<br/>
--%>
 
	<form action="<wp:action path="/ExtStr2/do/FrontEnd/Bandi/${sessionScope.fromPage}.action" />" method="post">
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
					<label for="model.oggetto"><wp:i18n key="LABEL_TITOLO" /> : </label>
				</div>
				<div class="element">
					<s:textfield name="model.oggetto" id="model.oggetto" cssClass="text" 
								value="%{#searchForm.oggetto}" 
								size="50" />
				</div>
			</div>
		
			<c:if test="${cigVisibile}">
				<div class="fieldset-row">
					<div class="label">
						<label for="model.cig"><wp:i18n key="LABEL_CIG" /> : </label>
					</div>
					<div class="element">
						<s:textfield name="model.cig" id="model.cig" cssClass="text" 
									value="%{#searchForm.cig}" 
									size="50" maxlength="10" />
					</div>
				</div>
			</c:if>


 		    <jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/internalServlet/advancedFilter.jsp" />
            <div id="advanced">
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
                        <label><wp:i18n key="LABEL_DATA_PUBBLICAZIONE_BANDO" /> : </label>
                    </div>
                    <div class="element">
                        <wp:i18n key="LABEL_DATA_PUBBLICAZIONE_BANDO" var="headerValueDataPubblicazione"/>
                        <wp:i18n key="LABEL_DA_DATA" var="headerValueDa"/>
                        <wp:i18n key="LABEL_A_DATA" var="headerValueA"/>
                        <label><wp:i18n key="LABEL_DA_DATA" />: </label>
                        <s:textfield name="model.dataPubblicazioneDa" id="model.dataPubblicazioneDa" cssClass="text"
                                    value="%{#searchForm.dataPubblicazioneDa}" title="%{#attr.headerValueDataPubblicazione} %{#attr.headerValueDa}"
                                    maxlength="10" size="10" />
                        <label><wp:i18n key="LABEL_A_DATA" />: </label>
                        <s:textfield name="model.dataPubblicazioneA" id="model.dataPubblicazioneA" cssClass="text"
                                    value="%{#searchForm.dataPubblicazioneA}" title="%{#attr.headerValueDataPubblicazione} %{#attr.headerValueA}"
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
                                    value="%{#searchForm.dataScadenzaDa}" title="%{#attr.headerValueDataScadenza} %{#attr.headerValueDa}"
                                    maxlength="10" size="10" />
                        <label><wp:i18n key="LABEL_A_DATA" />: </label>
                        <s:textfield name="model.dataScadenzaA" id="model.dataScadenzaA" cssClass="text"
                                    value="%{#searchForm.dataScadenzaA}" title="%{#attr.headerValueDataScadenza} %{#attr.headerValueA}"
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

                <c:if test="${proceduraTelematicaVisibile}">
                    <%--
                    <div class="fieldset-row">
                        <div class="label">
                            <label for="model.proceduraTelematica">Procedura telematica : </label>
                        </div>
                        <div class="element">
                            <s:select name="model.proceduraTelematica" id="model.proceduraTelematica" list="maps['sino']"
                                    value="%{#searchForm.proceduraTelematica}"
                                    headerKey="" headerValue="" >
                            </s:select>
                        </div>
                    </div>
                     --%>
                </c:if>

                <c:if test="${statoVisibile}">
                    <div class="fieldset-row">
                        <div class="label">
                            <label for="model.stato"><wp:i18n key="LABEL_STATO_GARA" /> : </label>
                        </div>
                        <div class="element">
                            <wp:i18n key="OPT_CHOOSE_STATO_GARA" var="headerValueStatoGara" />
                            <s:select name="model.stato" id="model.stato" list="maps['statiGara']"
                                    value="%{#searchForm.stato}"
                                    headerKey="" headerValue="%{#attr.headerValueStatoGara}" >
                            </s:select>
                        </div>
                    </div>
                </c:if>

                <c:if test="${esitoVisibile}">
                    <div class="fieldset-row" id="esitoGara">
                        <div class="label">
                            <label for="model.esito"><wp:i18n key="LABEL_ESITO" /> : </label>
                        </div>
                        <div class="element">
                            <wp:i18n key="OPT_CHOOSE_ESITO_GARA" var="headerValueEsito"/>
                            <s:select name="model.esito" id="model.esito" list="maps['esitiGara']"
                                            value="%{#searchForm.esito}"
                                            headerKey="" headerValue="%{#attr.headerValueEsito}" >
                                </s:select>
                        </div>
                    </div>
                </c:if>

                <c:if test="${visFiltroAltriSoggetti}">
                    <div class="fieldset-row">
                        <div class="label">
                            <label for="model.altriSoggetti"><wp:i18n key="LABEL_CUC_AGISCE_PER_CONTO" /> : </label>
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
                                value="%{#searchForm.sommaUrgenza}"
                                headerKey="" headerValue="" >
                        </s:select>
                    </div>
                </div>

                <div class="fieldset-row">
                    <div class="label">
                        <label for="model.isGreen"><wp:i18n key="LABEL_IS_GREEN" /> : </label>
                    </div>
                    <div class="element">
                        <s:select name="model.isGreen" id="model.isGreen" list="maps['sino']"
                                value="%{#searchForm.isGreen}"
                                headerKey="" headerValue="" >
                        </s:select>
                    </div>
                </div>

                <div class="fieldset-row">
                    <div class="label">
                        <label for="model.isRecycle"><wp:i18n key="LABEL_IS_RECYCLE" /> : </label>
                    </div>
                    <div class="element">
                        <s:select name="model.isRecycle" id="model.isRecycle" list="maps['sino']"
                                value="%{#searchForm.isRecycle}"
                                headerKey="" headerValue="" >
                        </s:select>
                    </div>
                </div>
                
                <div class="fieldset-row">
                    <div class="label">
                        <label for="model.isPnrr"><wp:i18n key="LABEL_IS_PNRR" /> : </label>
                    </div>
                    <div class="element">
                        <s:select name="model.isPnrr" id="model.isPnrr" list="maps['sino']"
                                value="%{#searchForm.isPnrr}"
                                headerKey="" headerValue="" >
                        </s:select>
                    </div>
                </div>
                
                <div class="fieldset-row">
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
            </div>
			
			<div class="azioni">
				<wp:i18n key="BUTTON_SEARCH" var="valueSearchButton" />
				<s:submit value="%{#attr.valueSearchButton}" cssClass="button block-ui"/>
                <jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_reload_wp.jsp" />
			</div>
		</fieldset>

	</form>	
