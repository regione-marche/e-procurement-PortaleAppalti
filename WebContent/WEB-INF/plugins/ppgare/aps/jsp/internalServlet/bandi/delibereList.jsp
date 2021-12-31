<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="wp" uri="aps-core.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp">
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-list">
	<h2><wp:i18n key="TITLE_BANDI_DELIBERE_A_CONTRARRE" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_DELIBERE_A_CONTRARRE" />
	</jsp:include>
	
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
	
	<%-- FORM DI RICERCA CON FILTRI DI RICERCA --%>
	<form action="<wp:action path="/ExtStr2/do/FrontEnd/Bandi/listDelibere.action" />" method="post"> 	
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
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
									value="%{model.stazioneAppaltante}" 
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
								value="%{model.oggetto}" size="50" />
				</div>
			</div>
		
			<div class="fieldset-row">
				<div class="label">
					<label for="model.cig"><wp:i18n key="LABEL_CIG" /> : </label>
				</div>
				<div class="element">
					<s:textfield name="model.cig" id="model.cig" cssClass="text" 
								value="%{model.cig}" size="50" maxlength="10" />
				</div>
			</div>
		
			<div class="fieldset-row">
				<div class="label">
					<label for="model.tipoAppalto"><wp:i18n key="LABEL_TIPO_APPALTO" /> : </label>
				</div>
				<div class="element">
					<wp:i18n key="OPT_CHOOSE_TIPO_APPALTO" var="headerValueTipoAppalto" />
					<s:select name="model.tipoAppalto" id="model.tipoAppalto" list="maps['tipiAppalto']" 
								value="%{model.tipoAppalto}"
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
											 value="%{model.dataPubblicazioneDa}"
											 title="%{#attr.headerValueDataPubblicazione} %{#attr.headerValueDa}"  
											 maxlength="10" size="10" />
					<label><wp:i18n key="LABEL_A_DATA" />: </label>
					<s:textfield name="model.dataPubblicazioneA" id="model.dataPubblicazioneA" cssClass="text" 
											 value="%{model.dataPubblicazioneA}" 
											 title="%{#attr.headerValueDataPubblicazione} %{#attr.headerValueA}"
											 maxlength="10" size="10" />
					(<wp:i18n key="LABEL_FORMATO_DATA" />)
				</div>
			</div>

			<div class="fieldset-row">
				<div class="label">
					<label for="model.sommaUrgenza"><wp:i18n key="LABEL_SOMMA_URGENZA" /> : </label>
				</div>
				<div class="element">
					<s:select name="model.sommaUrgenza" id="model.sommaUrgenza" list="maps['sino']" 
							value="%{model.sommaUrgenza}" 
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
					
			<div class="azioni">
				<wp:i18n key="BUTTON_SEARCH" var="valueSearchButton" />
				<s:submit value="%{#attr.valueSearchButton}" cssClass="button block-ui"/>
			</div>
		</fieldset>
	</form>	
															
	<%-- LISTA --%>
	<c:if test="${listaDelibere ne null}">		 				
		<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/bandi/inc/listaDelibere.jsp" />		
	</c:if>		
	
</div>