<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">
	
	<s:if test="%{sendOnlyBozze}">
		<h2><wp:i18n key="TITLE_PAGE_CATALOGHI_SALVA_BOZZE" /></h2>
		
		<p>
			<wp:i18n key="LABEL_SALVATAGGIO_BOZZE_PER_MODIFICHE_PRODOTTI" /> <s:property value="%{catalogo}"/> <wp:i18n key="LABEL_EFFETTUATA_CON_SUCCESSO" />.
		</p>
	</s:if>
	<s:else>
		<h2><wp:i18n key="TITLE_PAGE_CATALOGHI_INVIO_MODIFICHE_PRODOTTI" /></h2>
		
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
		
		<p>
			<wp:i18n key="LABEL_RICHIESTA_MODIFICHE_PRODODOTTI" /> <s:property value="%{catalogo}"/> <wp:i18n key="LABEL_EFFETTUATA_CON_SUCCESSO" />.
		</p>
		<p>
			<wp:i18n key="LABEL_RICHIESTA_INVIATA_IL" /> <s:date name="dataPresentazione" format="dd/MM/yyyy HH:mm:ss" />
			<s:if test="dataProtocollo != null"> <wp:i18n key="LABEL_RICHIESTA_PROTOCOLLATA_IL" /> <s:property value="dataProtocollo"/></s:if>
			<s:if test="%{presentiDatiProtocollazione}"> <wp:i18n key="LABEL_CON_ANNO" /> <s:property value="annoProtocollo"/> <wp:i18n key="LABEL_E_NUMERO" /> <s:property value="numeroProtocollo"/>
			</s:if>
			.
		</p>

		<s:if test="%{msgErrore != null}">
			<p><wp:i18n key="LABEL_MESSAGE_WARNING" />: <s:property value="%{msgErrore}"/></p>
		</s:if>
	</s:else>
	
	<div class="back-link">
		<a href='<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/openGestioneProdotti.action" />&amp;catalogo=<s:property value="%{catalogo}" />&amp;ext=${param.ext}&amp;${tokenHrefParams}'>
			<%-- Torna alla gestione prodotti (prodotti in carrello) --%>
			<wp:i18n key="LINK_BACK_TO_GESTIONE_PRODOTTI" />
		</a>
	</div>
</div>