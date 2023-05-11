<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<s:set var="helper" value="%{#session['dettOffertaAsta']}"/>
<s:set var="offerta" value="%{#helper.offertaEconomica}"/>
<s:set var="datiImpresa" value="%{#helper.impresa}"/>
<s:set var="documenti" value="%{#helper.documenti}"/>

<s:set var="idFirmatarioSelezionato" value="%{#helper.offertaEconomica.idFirmatarioSelezionatoInLista}"/>
<s:set var="rilanciPresenti" value="%{#helper.rilanci != null && #helper.rilanci.size > 0}"/>

<%--
offerta.componentiRTI.size: <s:property value="%{#offerta.componentiRTI.size}"/><br/>
--%>


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>
 
<div class="portgare-view">

	<h2><wp:i18n key='TITLE_PAGE_ASTA_CONFERMA_OFFERTA_FINALE'/></h2>
	
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/field_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />

	<jsp:include page="stepsConfermaOfferta.jsp" />

	<s:if test="%{listaImpreseVisible}">
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
			<jsp:param name="keyMsg" value="BALLOON_WIZ_CONFERMA_ASTA_SCARICA_OFFERTA_RTI"/>
		</jsp:include>
	</s:if>
	<s:else>
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
			<jsp:param name="keyMsg" value="BALLOON_WIZ_CONFERMA_ASTA_SCARICA_OFFERTA"/>
		</jsp:include>
	</s:else>
 	
	<s:set var="imgCheck"><wp:resourceURL/>static/img/check.svg</s:set>
	
	<!-- lista delle imprese del raggruppamento RTI -->
	<s:if test="%{listaImpreseVisible}">	
		<fieldset>
			<legend><wp:i18n key='LABEL_PARTECIPANTI_RTI'/></legend>
			<table>
				<tr>
					<th style="min-width: 1em;">&nbsp;</th>
					<th><wp:i18n key="LABEL_RAGIONE_SOCIALE" /></th>
					<th><wp:i18n key='LABEL_FIRMATARIO'/></th>
					<th><wp:i18n key="ACTIONS" /></th>
				</tr>
				<s:iterator var="componente" value="%{#offerta.componentiRTI}" status="status">
				
					<s:set name="key" value="%{#offerta.componentiRTI.getFirmatario(#componente)}"/>
					
					<tr>
						<td class="azioni">
							<s:if test="%{#key.nominativo!=null}">
								<img class="resize-svg-16" class="resize-svg-16" src="${imgCheck}" title='<wp:i18n key="TITLE_FIRMATARIO_INSERITO_GENERAZIONE_PDF_PRONTA" />'/>
							</s:if>
						</td>
						<td>
							<s:property value="#componente.ragioneSociale"/>
						</td>
						<td>
							<s:if test="%{#key.nominativo!=null}">
								<s:property value="%{#key.nominativo}"/>
							</s:if>
						</td>
						<td class="azioni">
							<!-- il I elemento è sempre la mandataria, 
							     i successivi sono le mandanti        -->
							<s:if test="%{#status.index==0}">
								<s:if test="%{modificaFirmatarioVisible}">
									<c:set var="hrefParams">&amp;id=${status.index}</c:set>
									<a href='<wp:action path="/ExtStr2/do/FrontEnd/Aste/editFirmatarioMandataria.action"/>&amp;${tokenHrefParams}' class="bkg modify" title='<wp:i18n key="BUTTON_MODIFICA" />'>
									</a>
								</s:if>
							</s:if>
							<s:else>
								<a href='<wp:action path="/ExtStr2/do/FrontEnd/Aste/editFirmatarioMandante.action"/>&amp;id=${status.index}&amp;${tokenHrefParams}' class="bkg modify" title='<wp:i18n key="BUTTON_MODIFICA" />'>
								</a>
							</s:else>
						</td>
					</tr>
 					
				</s:iterator>
			</table>
		</fieldset>	
 	</s:if> 
 	<s:else> 
		<fieldset>
			<legend><wp:i18n key="LABEL_SCARICA_OFFERTA_FINALE" /></legend>
		
			<s:url id="urlPdfOffertaDownload" namespace="/do/FrontEnd/Aste" action="createOffertaAstaPdf">
				<s:param name="urlPage">${currentPageUrl}</s:param>
				<s:param name="currentFrame">${param.currentFrame}</s:param>
			</s:url>
			
			<form action="${urlPdfOffertaDownload}"  method="post">
				
				<p><wp:i18n key="LABEL_LISTA_SOGGETTI_DIRITTO_FIRMA" /> :
				<%-- <s:if test="%{#offerta.componentiRTI.size > 1}" >. Selezionare il firmatario </s:if> :--%>
				</p>
					
				<ul class="list">					
					<s:if test="%{#offerta.componentiRTI.size > 1}" >
						<%-- *** Elenco dei firmatari scelti per un RTI *** --%>
						<table>
							<th style="min-width: 1em;"></th>
							<th><wp:i18n key="LABEL_RAGIONE_SOCIALE" /></th>
							<th><wp:i18n key='LABEL_FIRMATARIO'/></th>
							<th><wp:i18n key="ACTIONS" /></th>
							<s:iterator var="componente" value="%{#offerta.componentiRTI}" status="status">
							
								<s:set name="key" value="%{#offerta.componentiRTI.getFirmatario(#componente)}"/>
								
								<tr>
									<td class="azioni">
										<s:if test="%{#key.nominativo!=null}">
											<img src="${imgCheck}" title='<wp:i18n key="TITLE_FIRMATARIO_INSERITO_GENERAZIONE_PDF_PRONTA" />'/>
										</s:if>
									</td>
									<td>
										<s:property value="#componente.ragioneSociale"/>
									</td>
									<td>
										<s:if test="%{#key.nominativo!=null}">
											<s:property value="%{#key.nominativo}"/>
										</s:if>
									</td>
									<td class="azioni">
										<%-- il I elemento è sempre la mandataria, i successivi sono le mandanti --%>
										<s:if test="%{#status.index==0}">
											<s:if test="%{modificaFirmatarioVisible}">
												<c:set var="hrefParams">&amp;id=${status.index}</c:set>
												<a href='<wp:action path="/ExtStr2/do/FrontEnd/Aste/editFirmatarioMandataria.action"/>&amp;${tokenHrefParams}' class="bkg modify" title='<wp:i18n key="BUTTON_MODIFICA" />'>
												</a>
											</s:if>
										</s:if>
										<s:else>
											<a href='<wp:action path="/ExtStr2/do/FrontEnd/Aste/editFirmatarioMandante.action"/>&amp;id=${status.index}&amp;${tokenHrefParams}' class="bkg modify" title='<wp:i18n key="BUTTON_MODIFICA" />'>
											</a>
										</s:else>
									</td>
								</tr>
			 					
							</s:iterator>
						</table>
					</s:if>
					<s:else>
						<%-- *** Non è un RTI, mostra l'elenco dei firmatari della ditta *** --%>
						<s:iterator var="firmatario" value="%{#offerta.listaFirmatariMandataria}" status="status">
							<li
								class='<s:if test="%{#status.first}">first</s:if> <s:if test="%{#status.last}">last</s:if>'>
								<input type="radio" name="firmatarioSelezionato"
									   id='firmatarioSelezionato<s:property value="%{#status.index}"/>'
									   value='<s:if test="%{#firmatario.lista != null}"><s:property value="#firmatario.lista" />-<s:property value="#firmatario.index" /></s:if>'
									   <s:if test="%{(#status.count==1 && #offerta.listaFirmatariMandataria.size==1) || (#status.index==#offerta.idFirmatarioSelezionatoInLista) }"> checked="checked"</s:if> 
									   /> 
								<s:if test="%{tipoQualificaCodifica != null}">
									<s:property value="%{#firmatario.nominativo + ' ( ' + tipoQualificaCodifica.get(#status.index) + ' )'}" />
								</s:if>
								<s:else>
									<s:property value="%{#firmatario.nominativo}" />
								</s:else>
							</li>
						</s:iterator>
					</s:else>
				</ul>
				
				<div class="azioni">
					<wp:i18n key="BUTTON_WIZARD_GENERA_PDF_OFFERTA_ECONOMICA" var="valueGenPdfButton" />
					<wp:i18n key="TITLE_GARETEL_GENERA_DF_OFFERTA_ECONOMICA" var="titleGenPdfButton" />
					<input type="submit" id="createPdf" value="${attr.valueGenPdfButton}" title="${attr.titleGenPdfButton}" class="button" />
				</div>
			 </form>
		</fieldset>
	</s:else>
	
	<s:if test="%{ListaFirmatariMandatariaVisible}">
		<fieldset>
			<legend><wp:i18n key='LABEL_FIRMATARIO'/> <s:property value="%{#datiImpresa.datiPrincipaliImpresa.ragioneSociale}"/></legend>
	
			<form action="<wp:action path="/ExtStr2/do/FrontEnd/Aste/saveFirmatarioMandataria.action"/>"  method="post">
					<p><wp:i18n key='LABEL_LISTA_SOGGETTI_DIRITTO_FIRMA'/> : </p>
					<ul class="list">
						<s:iterator var="firmatario" value="%{#offerta.listaFirmatariMandataria}" status="status">
							<li
								class='<s:if test="%{#status.first}">first</s:if> <s:if test="%{#status.last}">last</s:if>'>
								<input type="radio" name="firmatarioSelezionato"
									   id='firmatarioSelezionato<s:property value="%{#status.index}"/>'
									   value='<s:if test="%{#firmatario.lista != null}"><s:property value="#firmatario.lista" />-<s:property value="#firmatario.index" /></s:if>'
									   <s:if test="%{(#status.count==1 && #offerta.listaFirmatariMandataria.size==1) || (#status.index==#offerta.idFirmatarioSelezionatoInLista) }"> checked="checked"</s:if>				
								 	   /> 
								 <s:property value="%{#firmatario.nominativo + ' ( ' + tipoQualificaCodifica.get(#status.index) + ' )'}" />
							</li>
						</s:iterator>
					</ul>
		
					<div class="azioni">
						<input type="submit" id="firmatarioMandataria" value='<wp:i18n key="BUTTON_REFRESH"/>'
							title='<wp:i18n key="TITLE_GARETEL_AGGIORNA_FIRMATARIO_MANDATARIA_RTI"/>' class="button" />
					</div>
			 </form>
			  
		</fieldset>
	</s:if>
	
	<s:elseif test="%{inputFirmatarioMandanteVisible}">
		<form action="<wp:action path="/ExtStr2/do/FrontEnd/Aste/saveFirmatarioMandante.action"/>&amp;id=<s:property value="%{id}"/>"
				method="post">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			
			<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/garetel/inc/gestioneFirmatario.jsp">
				<jsp:param name="sessionIdObj" value="dettAnagrImpresa" />
			</jsp:include>
		</form>	
	</s:elseif>
 		
	<form action="<wp:action path="/ExtStr2/do/FrontEnd/Aste/processPageFirmatari.action" />" method="post">
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
		
		<div class="azioni">
			<input type="hidden" name="page" value="firmatari"/>
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_previous.jsp" />
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_next.jsp" />
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_cancel.jsp" />
		</div>
	</form> 
	
</div>

