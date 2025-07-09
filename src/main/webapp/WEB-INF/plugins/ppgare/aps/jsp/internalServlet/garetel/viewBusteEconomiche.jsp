<%@ taglib prefix="wp" uri="aps-core.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<c:set var="keyMsg" value="BALLOON_LOTTI_BUSTE_ECONOMICHE"/>

<!-- OBSOLETO <s:set var="partecipazione" value="%{#session.dettPartecipGara}"/> -->
<!-- OBSOLETO <s:set var="riepilogoBuste" value="%{#session.riepilogoBuste}" /> -->
<s:set var="partecipazione" value="%{#session.dettaglioOffertaGara.bustaPartecipazione.helper}"/>
<s:set var="riepilogoBuste" value="%{#session.dettaglioOffertaGara.bustaRiepilogo.helper}" />
<s:set var="codiceTitolo" value="%{#partecipazione.idBando}"/>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp">
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>
	
<div class="portgare-view">
	<s:set var="imgCheck"> <wp:resourceURL />static/img/check.svg</s:set>

	<h2><wp:i18n key="LABEL_BUSTE_ECONOMICHE" /> : <wp:i18n key="TITLE_PAGE_LISTA_LOTTI" /> [${codiceTitolo}]</h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="${keyMsg}"/>
	</jsp:include>

	<fieldset>
		<legend><wp:i18n key="LABEL_LISTA_LOTTI" /></legend>
		<table>
			<tr>
				<th><wp:i18n key="LABEL_GARETEL_OGGETTO" /></th>
				<th scope="col" style="width: 10em;"><wp:i18n key="LABEL_PRONTO_PER_INVIO" /></th>
			</tr>
			<s:iterator value="%{#partecipazione.lotti}" var="lotto" status="stat">
				<s:if test="%{#session.visualizzaLotto.get(#lotto)}">
					<tr>
						<td>
							<s:if test="%{#session.aperturaLotto.get(#lotto)}">
								<s:if test="%{#session.wizardOfferta.get(#lotto)}">
									<c:set var="href" value="/ExtStr2/do/FrontEnd/GareTel/initOffTel.action"/>
									<form action='<wp:action path="${href}"/>' method="post">
							    		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
						 				<input type="hidden" name="tipoBusta" value="3" />
						 				<input type="hidden" name="operazione=" value="${operazione}" />
						 				<input type="hidden" name="codice" value='<s:property value="%{#lotto}"/>' />
						 				<input type="hidden" name="codiceGara" value="${codiceGara}" />
						 				<input type="hidden" name="progressivoOfferta" value="${progressivoOfferta}" />
						 				
							 			<a href="javascript:;" onclick="parentNode.submit();" >
											<wp:i18n key="LABEL_LOTTO" /> <s:property value="%{#riepilogoBuste.listaCodiciInterniLotti.get(#lotto)}" /> 
											&nbsp;-&nbsp;<s:property value="%{#riepilogoBuste.busteEconomicheLotti.get(#lotto).oggetto}"/>
										</a>
									</form>
								</s:if>
								<s:else>
									<c:set var="href" value="/ExtStr2/do/FrontEnd/GareTel/openPageDocumenti.action"/>
									<form action='<wp:action path="${href}"/>' method="post">
							    		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
						 				<input type="hidden" name="tipoBusta" value="3" />
						 				<input type="hidden" name="operazione=" value="${operazione}" />
						 				<input type="hidden" name="codice" value='<s:property value="%{#lotto}"/>' />
						 				<input type="hidden" name="codiceGara" value="${codiceGara}" />
						 				<input type="hidden" name="progressivoOfferta" value="${progressivoOfferta}" />
						 				
							 			<a href="javascript:;" onclick="parentNode.submit();" >
											<wp:i18n key="LABEL_LOTTO" /> <s:property value="%{#riepilogoBuste.listaCodiciInterniLotti.get(#lotto)}" />
											&nbsp;-&nbsp;<s:property value="%{#riepilogoBuste.busteEconomicheLotti.get(#lotto).oggetto}"/>
										</a>
									</form>
								</s:else>
							</s:if>
							<s:else>
								<!-- lotto DA PROCESSARE -->
								(<wp:i18n key="LABEL_BUSTA_CHIUSA_DA_TRASMETTERE" />) <wp:i18n key="LABEL_LOTTO" /> <s:property value="%{#riepilogoBuste.listaCodiciInterniLotti.get(#lotto)}" />&nbsp;-&nbsp;<s:property value="%{#riepilogoBuste.busteEconomicheLotti.get(#lotto).oggetto}"/> 
							</s:else>
						</td>
						<td class="azioni">
							<s:if test="%{#session.documentiLottoPronti.get(#lotto)}"> 
								<img class="resize-svg-16" src="${imgCheck}" title='<wp:i18n key="TITLE_DOCUMENTI_PRONTI" />' />
							</s:if>
						</td>
					</tr>
				</s:if>
 			</s:iterator>
		</table>
	</fieldset>
	
	
	<div class="back-link">
		<a href="<wp:action path="/ExtStr2/do/FrontEnd/GareTel/openGestioneBusteDistinte.action" />&amp;codiceGara=${codiceGara}&amp;operazione=${operazione}&amp;progressivoOfferta=${progressivoOfferta}&amp;ext=${param.ext}">
			<wp:i18n key="BUTTON_WIZARD_BACK_TO_MENU" />
		</a>
	</div>
</div>

