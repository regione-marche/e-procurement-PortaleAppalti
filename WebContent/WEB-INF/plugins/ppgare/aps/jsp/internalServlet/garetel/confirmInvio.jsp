<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<s:if test="%{#session.dettPartecipGara.plicoUnicoOfferteDistinte}">
	<c:set var="href" value="/ExtStr2/do/FrontEnd/GareTel/openGestioneBusteDistinte.action"/>
	<c:set var="hrefInvio" value="/ExtStr2/do/FrontEnd/GareTel/invioOfferteDistinte.action"/>
</s:if>
<s:else>
	<c:set var="href" value="/ExtStr2/do/FrontEnd/GareTel/openGestioneBuste.action"/>
	<c:set var="hrefInvio" value="/ExtStr2/do/FrontEnd/GareTel/invio.action"/>
</s:else>


<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/send_blockUI.jsp" />

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">
	<s:if test="%{operazione == inviaOfferta}">
		<c:set var="title"><wp:i18n key='TITLE_PAGE_GARETEL_CONFERMA_INVIO'/></c:set>
		<c:set var="msg"><wp:i18n key='LABEL_GARETEL_INVIO_OFFERTA'/></c:set>
	</s:if>
	<s:elseif test="%{operazione == partecipaGara}">
		<c:set var="title"><wp:i18n key='TITLE_PAGE_GARETEL_CONFERMA_PARTECIPAZIONE'/></c:set>
		<c:set var="msg"><wp:i18n key='LABEL_GARETEL_PARTECIPAZIONE_GARA'/></c:set>
	</s:elseif>
	<s:else>
		<c:set var="title"><wp:i18n key='TITLE_PAGE_GARETEL_CONFERMA_COMPROVA_REQ'/></c:set>
		<c:set var="msg"><wp:i18n key='LABEL_GARETEL_COMPROVA_REQUISITI'/></c:set>
	</s:else>
	
	<h2>
		${title}
	</h2>
	
	<p>
		<wp:i18n key='LABEL_GARETEL_PROCEDERE_CON_RICHIESTA'/> ${msg}?
	</p>

	<div class="azioni">
		<wp:i18n key="LABEL_YES" var="valueYesButton" />
		<wp:i18n key="LABEL_NO" var="valueNoButton" />
	
		<form action="<wp:action path="${hrefInvio}" />" method="post" class="azione">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			
			<div>
				<s:if test="!sendBlocked">
					<s:submit value="%{#attr.valueYesButton}" cssClass="button block-ui" />
				</s:if>
				<input type="hidden" name="codice" value="<s:property value="codice" />"/>
				<input type="hidden" name="operazione" value="<s:property value="operazione" />"/>
				<input type="hidden" name="progressivoOfferta" value="<s:property value='progressivoOfferta' />"/>
				<input type="hidden" name="ext" value="${param.ext}" />
			</div>
		</form>
		
		<form action="<wp:action path="${href}"/>" method="post" class="azione">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			
			<div>
				<s:submit value="%{#attr.valueNoButton}" cssClass="button" />
				<input type="hidden" name="codice" value="<s:property value="codice" />"/>
				<input type="hidden" name="operazione" value="<s:property value="operazione" />"/>
				<input type="hidden" name="progressivoOfferta" value="<s:property value='progressivoOfferta' />"/>
				<input type="hidden" name="ext" value="${param.ext}" />
			</div>
		</form>
	</div>
</div>