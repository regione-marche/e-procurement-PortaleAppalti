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
	   <s:if test="%{codiceSistema == 'LAPISOPERA'}"> 
	   		${LABEL_RICHIESTA_CON_ID}
	   </s:if>
	   <s:else>
		    <wp:i18n key='LABEL_RINNOVO_ISCRIZIONE_ISCRALBO_1'/> <s:date name="dataInvio" format="dd/MM/yyyy HH:mm:ss" />
		    <s:if test="dataProtocollo != null"> <wp:i18n key='LABEL_RICHIESTA_PROTOCOLLATA_IL'/> <s:property value="dataProtocollo"/></s:if>
	   		<s:if test="%{presentiDatiProtocollazione}"> <wp:i18n key='LABEL_CON_ANNO'/> <s:property value="annoProtocollo"/> <wp:i18n key='LABEL_E_NUMERO'/> <s:property value="numeroProtocollo"/></s:if>
	   </s:else>
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
 	 	<c:set var="urlBackto">
			<s:if test="%{tipoElenco == TIPOLOGIA_ELENCO_STANDARD}">
				<wp:action path="/ExtStr2/do/FrontEnd/Bandi/viewIscrizione.action" />
			</s:if>
			<s:else>
				<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/viewIscrizione.action" />
			</s:else>
		</c:set>
		<form action='${urlBackto}' method="post">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			<input type="hidden" name="codice" value="${codice}" />
			<input type="hidden" name="ext" value="${param.ext}" />
			
			<a href="javascript:;" onclick="parentNode.submit();" >
				<s:if test="%{tipoElenco == TIPOLOGIA_ELENCO_STANDARD}">
						<wp:i18n key='LINK_BACK_TO_ELENCO'/>
				</s:if>
				<s:else>
						<wp:i18n key='LINK_BACK_TO_CATALOGO'/>
				</s:else>
			</a>
		</form>
	</div>  
</div>