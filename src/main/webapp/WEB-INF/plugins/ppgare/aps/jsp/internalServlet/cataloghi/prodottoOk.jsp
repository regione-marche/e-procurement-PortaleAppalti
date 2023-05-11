<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<s:if test="%{bozza}">
		<%-- SALVA BOZZA --%>
		<h2><wp:i18n key="TITLE_PAGE_SALVATAGGIO_BOZZA_CATALOGO" /></h2>
		<p>
			<wp:i18n key="LABEL_CATALOGHI_BOZZA_PRODOTTO_SALVATA" />.
		</p>
	</s:if>
	<s:elseif test="%{aggiornamento}">
		<%-- AGGIORNAMENTO --%>
		<h2><wp:i18n key="TITLE_PAGE_AGGIORNA_PRODOTTO_CATALOGO" /></h2>
		<p>			
			<s:if test="%{!#session.dettProdotto.inCarrello}">
				<wp:i18n key="LABEL_IL_PRODOTTO_DEL_CATALOGO" />
			</s:if>
			<s:else>
				<wp:i18n key="LABEL_IL_PRODOTTO_DEL_CARRELLO" />
			</s:else>
			<wp:i18n key="LABEL_CATALOGHI_PRODOTTO_AGGIORNATO_CON_SUCCESSO" />.
		</p>
	</s:elseif>
	<s:else>
		<%-- INSERIMENTO --%>
		<h2><wp:i18n key="TITLE_PAGE_INSERIMENTO_PRODOTTO_CATALOGO" /></h2>
		<p>
			<wp:i18n key="LABEL_CATALOGHI_PRODOTTO_INSERITO_CON_SUCCESSO" />.
		</p>
	</s:else>
	
	<div class="back-link">
		<a href='<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/openGestioneProdotti.action" />&amp;ext=${param.ext}&amp;${tokenHrefParams}'>
			<wp:i18n key="LINK_BACK_TO_GESTIONE_PRODOTTI" />
		</a>
	</div>
</div>