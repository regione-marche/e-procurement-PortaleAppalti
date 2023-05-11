<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2><wp:i18n key='TITLE_PAGE_CATALOGHI_ANNULLA_INSERIMENTO'/></h2>

	<p><wp:i18n key='LABEL_CATALOGHI_ANNULLA_INSERIMENTO_1'/> <s:property value="%{#session.dettProdotto.codiceCatalogo}"/>.</p>
	
	<p>
		<s:if test="%{PAGINA_DETTAGLIO_ARTICOLO.equals(#session.dettProdotto.wizardSourcePage)}">
			<wp:i18n key='LABEL_CATALOGHI_ANNULLA_INSERIMENTO_2'/>
		</s:if>
		<s:else>
			<wp:i18n key='LABEL_CATALOGHI_ANNULLA_INSERIMENTO_3'/>
		</s:else>
	</p>
	
	<div class="azioni">
		<form action="<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/cancelWizardProdotto.action" />" method="post" class="azione">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			<div>
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_confirm_cancel.jsp" />
				<s:hidden name="catalogo" value="%{#session.dettProdotto.codiceCatalogo}" />
				<s:hidden name="codiceCatalogo" value="%{#session.dettProdotto.codiceCatalogo}" />
				<input type="hidden" name="ext" value="${param.ext}" />
			</div>
		</form>
		<c:choose>
			<c:when test="${sessionScope.page eq 'articolo'}">
				<c:set var="href" value="/ExtStr2/do/FrontEnd/Cataloghi/openPageSelezioneArticolo.action" />
			</c:when>
			<c:when test="${sessionScope.page eq 'prodotto'}">
				<c:set var="href" value="/ExtStr2/do/FrontEnd/Cataloghi/openPageDefinizioneProdotto.action" />
			</c:when>
			<c:when test="${sessionScope.page eq 'doc'}">
				<c:set var="href" value="/ExtStr2/do/FrontEnd/Cataloghi/openPageDocumenti.action" />
			</c:when>
			<c:when test="${sessionScope.page eq 'fine'}">
				<c:set var="href" value="/ExtStr2/do/FrontEnd/Cataloghi/openPageRiepilogoProdotto.action" />
			</c:when>
		</c:choose>
		<form action="<wp:action path="${href}" />" method="post" class="azione">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			<div>
				<input type="hidden" name="ext" value="${param.ext}" />
				<wp:i18n key="LINK_BACK_TO_INSERT" var="valueBackToButton" />
				<s:submit value="%{#attr.valueBackToButton}" cssClass="button" />
			</div>
		</form>
	</div>
</div>