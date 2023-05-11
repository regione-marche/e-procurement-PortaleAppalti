<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags"%>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp">
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2><wp:i18n key="TITLE_PAGE_SELEZIONE_SA" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_SELEZIONE_STAZIONE_APPALTANTE" />
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_messages.jsp" />
			
	<form action="<wp:action path="/ExtStr2/do/FrontEnd/SAFilter/confirm.action"/>" method="post" id="formStazioneAppaltante">
				
		<fieldset>
			<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_STAZIONE_APPALTANTE" /></legend>
			 
			<c:if test="${! empty stazioneAppaltante}">
				<div class="fieldset-row first-row">
					<div class="label">
						<label for="model.utente"><wp:i18n key="LABEL_SELEZIONE_SA_STAZIONE_ATTIVA" />: </label>
					</div>
					<div class="element">
						<s:if test="%{stazioneAppaltante != null && stazioneAppaltante != ''}">
							<ul>
								<li>
									<s:property value="%{descStazioneAppaltante}"/>
								</li>
								<li>
									<wp:i18n key="LABEL_SELEZIONE_SA_CODFISC_PIVA" /> <s:property value="%{codiceFiscaleStazioneAppaltante}"/>
								</li>
							</ul>
						</s:if>								
					</div>
				</div>	
			</c:if>			
	 
 			<div class="table-container">
				<table id="tableStazioniAppaltanti" class="info-table">
					<caption style="display:none;"><wp:i18n key="LABEL_SELEZIONE_SA_TABELLA_SUMMARY" /></caption>
					<thead>
						<tr>
							<th scope="col"></th>
							<th scope="col"><wp:i18n key="LABEL_SELEZIONE_SA_CODFISC_PIVA" /></th>
							<th scope="col"><wp:i18n key="LABEL_DENOMINAZIONE" /></th>
						</tr>
					</thead>
					<tbody>
						<tr>									
							<td>																				
								<input type="radio" name="stazioneAppaltante" id="stazioneAppaltante_0" 
									value=""
									<c:if test="${empty stazioneAppaltante}">checked="checked"</c:if> />
							</td>								
							<td></td>
							<td><label for="stazioneAppaltante_0"><wp:i18n key="LABEL_SELEZIONE_SA_ANNULLA_FILTRO" /></label></td>
						</tr>
						
						<s:iterator var="riga" value="listaStazioniAppaltanti" status="status">
							<s:set var="codice" value="#riga.key" />
							<s:set var="codiceFiscale" value="#riga.value[0]" />
							<s:set var="denominazione" value="#riga.value[1]" />
							
							<tr>									
								<td>																				
									<input type="radio" name="stazioneAppaltante" id="stazioneAppaltante_${status.index+1}" 
										value="<s:property value='#codice'/>" 
										title="<s:property value='#denominazione'/>"  
										<s:if test="%{#riga.key.equals(stazioneAppaltante)}">checked="checked"</s:if> />
								</td>								
								<td><s:property value="#codiceFiscale"/></td>
								<td><s:property value="#denominazione"/></td>
							</tr>
						</s:iterator>
					</tbody>
				</table>
			</div> 	
	
	 		<div class="azioni">
	 			<wp:i18n key="BUTTON_CONFIRM" var="valueSubmitButton"/>
	 			<wp:i18n key="LABEL_SELEZIONE_SA_TITLE_CONFIRM" var="titleSubmitButton"/>
				<s:submit value="%{#attr.valueSubmitButton}" title="%{#attr.titleSubmitButton}" cssClass="button" method="confirm"></s:submit>
			</div>		
		</fieldset>
		
	</form>
</div>
