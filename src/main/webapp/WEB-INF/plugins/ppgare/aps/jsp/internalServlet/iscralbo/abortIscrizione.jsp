<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<s:if test="%{#session.dettIscrAlbo != null}">
	<s:set name="sessionIdObj" value="'dettIscrAlbo'"/>
	<c:set var="nextAction" value="/ExtStr2/do/FrontEnd/IscrAlbo/processPageIscrAlboGeneraPdf.action"/>
	<c:set var="sessionIdObj" value="dettIscrAlbo"/>
</s:if>
<s:else>
	<s:set name="sessionIdObj" value="'dettRinnAlbo'"/>
	<c:set var="nextAction" value="/ExtStr2/do/FrontEnd/IscrAlbo/processPageRinnovoGeneraPdf.action"/>
	<c:set var="sessionIdObj" value="dettRinnAlbo"/>
</s:else>

<s:if test="%{#session[#sessionIdObj].tipologia == 2}">
	<c:set var="titolo"><wp:i18n key='TITLE_PAGE_ISCRALBO_MERCATO_ELETTRONICO'/></c:set>
	<c:set var="dettaglio"><wp:i18n key="LABEL_BANDO"/></c:set>
	<c:set var="lista"><wp:i18n key="LABEL_AL_MERCATO_ELETTRONICO"/></c:set>
</s:if>
<s:else>
	<c:set var="titolo"><wp:i18n key='TITLE_PAGE_ISCRALBO_ELENCO_OE'/></c:set>
	<c:set var="dettaglio"><wp:i18n key="LABEL_BANDO"/></c:set>
	<c:set var="lista"><wp:i18n key="LABEL_ALL_ELENCO"/></c:set>
</s:else>


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">
	<s:if test="%{!#session[#sessionIdObj].rinnovoIscrizione}">
		<h2><wp:i18n key="TITLE_PAGE_ISCRALBO_ANNULLA_ISCRIZIONE"/> <c:out value="${titolo}"/></h2>
		<p><wp:i18n key="LABEL_CANCEL_ISCRIZIONE_ISCRALBO_1"/> <c:out value="${lista}"/>.</p>
		<p><wp:i18n key="LABEL_CANCEL_ISCRIZIONE_ISCRALBO_2"/> <c:out value="${dettaglio}"/> 
		   <wp:i18n key="LABEL_CANCEL_ISCRIZIONE_ISCRALBO_3"/> <c:out value="${lista}"/>.
		</p>
	</s:if>
	<s:else>
		<h2><wp:i18n key="TITLE_PAGE_ISCRALBO_ANNULLA_RINNOVO"/> <c:out value="${titolo}"/></h2>
		<p><wp:i18n key="LABEL_CANCEL_RINNOVO_ISCRALBO_1"/> <c:out value="${lista}"/>.</p>
		<p><wp:i18n key="LABEL_CANCEL_RINNOVO_ISCRALBO_2"/> <c:out value="${dettaglio}"/>
		   <wp:i18n key="LABEL_CANCEL_RINNOVO_ISCRALBO_3"/> <c:out value="${lista}"/>.
		</p>
	</s:else>
	
	<div class="azioni">
		<s:if test="%{#session[#sessionIdObj].rinnovoIscrizione}">
			<form action="<wp:action path="/ExtStr2/do/FrontEnd/IscrAlbo/cancelRinnovo.action" />" method="post" class="azione">
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
				
				<div>
					<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_confirm_cancel.jsp" />
					<input type="hidden" name="codice" value="<s:property value="codice" />"/>
					<input type="hidden" name="ext" value="${param.ext}" />
				</div>
			</form>
		</s:if>
		<s:else>
			<form action="<wp:action path="/ExtStr2/do/FrontEnd/IscrAlbo/cancelIscrizione.action" />" method="post" class="azione">
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
				
				<div>
					<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_confirm_cancel.jsp" />
					<input type="hidden" name="codice" value="<s:property value="codice" />"/>
					<input type="hidden" name="ext" value="${param.ext}" />
				</div>
			</form>
		</s:else>
		
		<c:choose>
			<c:when test="${sessionScope.page eq 'impresa'}">
				<c:choose>	
					<c:when test = "${sessionIdObj eq 'dettIscrAlbo'}">
						<c:set var="href" value="/ExtStr2/do/FrontEnd/IscrAlbo/openPageIscrAlboImpresa.action" />
					</c:when>
					<c:when test = "${sessionIdObj eq 'dettRinnAlbo'}">
						<c:set var="href" value="/ExtStr2/do/FrontEnd/IscrAlbo/openPageRinnovoImpresa.action" />
					</c:when>
				</c:choose>
			</c:when>
			<c:when test="${sessionScope.page eq 'sa'}">
				<c:set var="href" value="/ExtStr2/do/FrontEnd/IscrAlbo/openPageSA.action" />
			</c:when>
			<c:when test="${sessionScope.page eq 'RTI'}">
				<c:set var="href" value="/ExtStr2/do/FrontEnd/IscrAlbo/openPageIscrAlboRTI.action" />
			</c:when>
			<c:when test="${sessionScope.page eq 'Componenti'}">
				<c:set var="href" value="/ExtStr2/do/FrontEnd/IscrAlbo/openPageIscrAlboComponenti.action" />
			</c:when>
			<c:when test="${sessionScope.page eq 'categorie'}">
				<c:set var="href" value="/ExtStr2/do/FrontEnd/IscrAlbo/openPageIscrAlboCategorie.action" />
			</c:when>
			<c:when test="${sessionScope.page eq 'riepilogoCategorie'}">
				<c:set var="href" value="/ExtStr2/do/FrontEnd/IscrAlbo/openPageIscrAlboRiepilogoCategorie.action" />
			</c:when>
			<c:when test="${sessionScope.page eq 'generaPdfRichiesta'}">
				<c:choose>	
					<c:when test = "${sessionIdObj eq 'dettIscrAlbo'}">
						<c:set var="href" value="/ExtStr2/do/FrontEnd/IscrAlbo/openPageIscrAlboGeneraPdfRichiesta.action" />
					</c:when>
					<c:when test = "${sessionIdObj eq 'dettRinnAlbo'}">
						<c:set var="href" value="/ExtStr2/do/FrontEnd/IscrAlbo/openPageRinnovoGeneraPdfRichiesta.action" />
					</c:when>
				</c:choose>
			</c:when>
			<%-- 
			<c:when test="${sessionScope.page eq 'documenti'}">
				<c:set var="href" value="/ExtStr2/do/FrontEnd/IscrAlbo/openPageIscrAlboDocumenti.action" />
			</c:when>
			<c:when test="${sessionScope.page eq 'documentiRinnovo'}">
				<c:set var="href" value="/ExtStr2/do/FrontEnd/IscrAlbo/openPageRinnovoDocumenti.action" />
			</c:when> 
			--%>
			<c:when test="${sessionScope.page eq 'documenti'}">
				<s:if test="%{#session[#sessionIdObj].rinnovoIscrizione}">
					<c:set var="href" value="/ExtStr2/do/FrontEnd/IscrAlbo/openPageRinnovoDocumenti.action" />
				</s:if>
				<s:else>
					<c:set var="href" value="/ExtStr2/do/FrontEnd/IscrAlbo/openPageIscrAlboDocumenti.action" />
				</s:else>
			</c:when>
			<%-- 	
			<c:when test="${sessionScope.page eq 'riepilogo'}">
				<c:set var="href" value="/ExtStr2/do/FrontEnd/IscrAlbo/openPageIscrAlboRiepilogo.action" />
			</c:when> 
			--%>
			<c:when test="${sessionScope.page eq 'riepilogo'}">
			
				<c:choose>	
					<c:when test = "${sessionIdObj eq 'dettIscrAlbo'}">
						<c:set var="href" value="/ExtStr2/do/FrontEnd/IscrAlbo/openPageIscrAlboRiepilogo.action" />
					</c:when>
					<c:when test = "${sessionIdObj eq 'dettRinnAlbo'}">
						<c:set var="href" value="/ExtStr2/do/FrontEnd/IscrAlbo/openPageRinnovoRiepilogo.action" />
					</c:when>
				</c:choose>
				<%-- 
				<s:if test="%{#session[#sessionIdObj].rinnovoIscrizione}">
					<c:set var="href" value="/ExtStr2/do/FrontEnd/IscrAlbo/openPageRinnovoRiepilogo.action" />
				</s:if>
				<s:else>
					<c:set var="href" value="/ExtStr2/do/FrontEnd/IscrAlbo/openPageIscrAlboRiepilogo.action" />
				</s:else> 
				--%>
			</c:when>
			<c:when test="${sessionScope.page eq 'openQC'}">
					<c:set var="href" value="/ExtStr2/do/FrontEnd/IscrAlbo/openQC.action" />
			</c:when>
			<c:when test="${sessionScope.page eq 'openQCSummary' and fromQFormChanged}">
					<c:set var="href" value="/ExtStr2/do/FrontEnd/IscrAlbo/openQCSummaryToView.action" />
			</c:when>
			<c:when test="${sessionScope.page eq 'openQCSummary' and not fromQFormChanged}">
					<c:set var="href" value="/ExtStr2/do/FrontEnd/IscrAlbo/openQCSummary.action" />
			</c:when>
		</c:choose>
		<form action="<wp:action path="${href}" />" method="post" class="azione">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			
			<div>
				<input type="hidden" name="ext" value="${param.ext}" />
				<s:if test="%{!#session[#sessionIdObj].rinnovoIscrizione}">
					<wp:i18n key="BUTTON_WIZARD_BACK_TO_ISCRIZIONE_ISCRALBO" var="valueBackTo" />
					<s:submit value="%{#attr.valueBackTo}" cssClass="button" />
				</s:if>
				<s:else>
					<wp:i18n key="BUTTON_WIZARD_BACK_TO_RINNOVO_ISCRALBO" var="valueBackTo" />
					<s:submit value="%{#attr.valueBackTo}" cssClass="button" />
				</s:else>
			</div>
		</form>
	</div>
</div>