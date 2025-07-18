<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags"%>

<%-- <s:url id="urlPdf" namespace="/do/FrontEnd/Aste" action="createPdfBarcode" /> --%>


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp">
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2><wp:i18n key='TITLE_PAGE_ASTA_CONFERMA_OFFERTA_FINALE'/></h2>

	<p><wp:i18n key='LABEL_CONFERMA_INVIO'/></p>
	<p>
	   <s:if test="%{codiceSistema == 'LAPISOPERA'}"> 
	   		${LABEL_RICHIESTA_CON_ID}
	   </s:if>
	   <s:else>
	   		<wp:i18n key='LABEL_RICHIESTA_INVIATA_IL'/> ${dataInvio} <s:if test="dataProtocollo != null"><wp:i18n key='LABEL_RICHIESTA_PROTOCOLLATA_IL'/> <s:property value="dataProtocollo"/></s:if>
	   		<s:if test="%{presentiDatiProtocollazione}"> <wp:i18n key='LABEL_CON_ANNO'/> <s:property value="annoProtocollo"/> <wp:i18n key='LABEL_E_NUMERO'/> <s:property value="numeroProtocollo"/></s:if>
	   </s:else>
	   .
	</p>

	<s:if test="%{msgErrore != null}">
		<p><wp:i18n key="LABEL_MESSAGE_WARNING" />: <s:property value="%{msgErrore}"/></p>
	</s:if>
	
	<div class="back-link">
		<c:set var="urlBack"><wp:action path="/ExtStr2/do/FrontEnd/Aste/openAsta.action"/></c:set>	
		<form action='${urlBack}' method="post">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			<input type="hidden" name="codice" value='<s:property value="codice"/>' />
			<input type="hidden" name="codiceLotto" value='<s:property value="codiceLotto"/>' />
			
			<a href="javascript:;" onclick="parentNode.submit();" >
				<wp:i18n key="LINK_BACK_TO_PROCEDURE" />
			</a>
		</form>
	</div>
</div>
