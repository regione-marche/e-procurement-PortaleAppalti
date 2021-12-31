<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="/struts-tags" %>


<s:if test="%{tipologia == 2}">
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

	<h2><wp:i18n key='TITLE_PAGE_ISCRALBO_RICHIESTA_ISCRIZIONE'/> <c:out value="${titolo}"/></h2>

	<p><wp:i18n key='LABEL_RICHIESTA_ISCRIZIONE_ISCRALBO'/></p>
	<p><wp:i18n key='LABEL_RICHIESTA_INVIATA_IL'/> <s:date name="dataPresentazione" format="dd/MM/yyyy HH:mm:ss" />
	   <s:if test="dataProtocollo != null"> <wp:i18n key='LABEL_RICHIESTA_PROTOCOLLATA_IL'/> <s:property value="dataProtocollo"/></s:if>
	   <s:if test="%{presentiDatiProtocollazione}"><wp:i18n key='LABEL_CON_ANNO'/> <s:property value="annoProtocollo"/> <wp:i18n key='LABEL_E_NUMERO'/> <s:property value="numeroProtocollo"/></s:if>
	   .
	</p>
	
	<s:if test="%{msgErrore != null}">
		<p><wp:i18n key="LABEL_MESSAGE_WARNING" />: <s:property value="%{msgErrore}"/></p>
	</s:if>

	<div class="back-link">
		<a href="<s:if test="%{tipologia == 2}"><wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/viewIscrizione.action" /></s:if>
				<s:else><wp:action path="/ExtStr2/do/FrontEnd/Bandi/viewIscrizione.action" /></s:else>&amp;codice=<s:property value="codice" />&amp;ext=${param.ext}&amp;${tokenHrefParams}">
			<wp:i18n key='LINK_BACK_TO'/> <c:out value="${dettaglio}" />
		</a>
	</div>
</div>