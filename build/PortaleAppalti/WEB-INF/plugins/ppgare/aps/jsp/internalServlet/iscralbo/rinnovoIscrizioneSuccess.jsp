<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">
	
	<h2><wp:i18n key='TITLE_PAGE_ISCRALBO_RINNOVO_ISCRIZIONE'/></h2>

	<p>
	   <wp:i18n key='LABEL_RINNOVO_ISCRIZIONE_ISCRALBO_1'/> <s:date name="dataInvio" format="dd/MM/yyyy HH:mm:ss" />
	   <s:if test="dataProtocollo != null"> <wp:i18n key='LABEL_RICHIESTA_PROTOCOLLATA_IL'/> <s:property value="dataProtocollo"/></s:if>
	   <s:if test="%{presentiDatiProtocollazione}"> <wp:i18n key='LABEL_CON_ANNO'/> <s:property value="annoProtocollo"/> <wp:i18n key='LABEL_E_NUMERO'/> <s:property value="numeroProtocollo"/></s:if>
	   .
	</p>
	<p><wp:i18n key='LABEL_RINNOVO_ISCRIZIONE_ISCRALBO_2'/></p>
	
	<s:if test="%{msgErrore != null}">
		<p><wp:i18n key="LABEL_MESSAGE_WARNING" />: <s:property value="%{msgErrore}"/></p>
	</s:if>
	
<%-- 
<div class="back-link">
	******************<s:property value="%{#session.dettRinnAlbo}"/>
	<s:if test="%{#session.dettRinnAlbo.tipologia == TIPOLOGIA_ELENCO_STANDARD}">
		<a href='<wp:action path="/ExtStr2/do/FrontEnd/Bandi/viewIscrizione.action" />&amp;codice=${codice}&amp;ext=${param.ext}'>Torna al dettaglio dell'elenco</a>
	</s:if>
	<s:else>
		<a href='<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/viewIscrizione.action" />&amp;codice=${codice}&amp;ext=${param.ext}'>Torna al dettaglio del catalogo</a>
	</s:else>
</div> 
--%>

 	 <div class="back-link">
		<s:if test="%{tipoElenco == TIPOLOGIA_ELENCO_STANDARD}">
			<a href='<wp:action path="/ExtStr2/do/FrontEnd/Bandi/viewIscrizione.action" />&amp;codice=${codice}&amp;ext=${param.ext}&amp;${tokenHrefParams}'>
				<wp:i18n key='LINK_BACK_TO_ELENCO'/>
			</a>
		</s:if>
		<s:else>
			<a href='<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/viewIscrizione.action" />&amp;codice=${codice}&amp;ext=${param.ext}&amp;${tokenHrefParams}'>
				<wp:i18n key='LINK_BACK_TO_CATALOGO'/>
			</a>
		</s:else>
	</div>  
</div>