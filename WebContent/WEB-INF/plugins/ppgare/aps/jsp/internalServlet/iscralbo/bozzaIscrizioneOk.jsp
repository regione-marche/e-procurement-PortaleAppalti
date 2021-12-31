<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<s:if test="%{#session.dettIscrAlbo.tipologia == 2}">
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

	<p><wp:i18n key='LABEL_BOZZA_ISCRIZIONE_ISCRALBO_1'/> <c:out value="${lista}"/> <wp:i18n key='LABEL_BOZZA_ISCRIZIONE_ISCRALBO_2'/></p>
	<p><wp:i18n key='LABEL_BOZZA_ISCRIZIONE_ISCRALBO_3'/> <c:out value="${dettaglio}"/>.</p>

	<div class="back-link">
		<a href="<s:if test="%{#session.dettIscrAlbo.tipologia == 2}"><wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/viewIscrizione.action" /></s:if>
		         <s:else><wp:action path="/ExtStr2/do/FrontEnd/Bandi/viewIscrizione.action" /></s:else>&amp;codice=<s:property value="codice" />&amp;ext=${param.ext}&amp;${tokenHrefParams}">
			<wp:i18n key='LINK_BACK_TO'/> <c:out value="${dettaglio}" />
		</a>
	</div>
</div>