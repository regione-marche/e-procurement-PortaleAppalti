<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-list">

	<h2><wp:i18n key="TITLE_PAGE_ASSISTENZA_TECNICA" /></h2>
 
	<s:if test="%{erroreInvio != null}">
		<p><wp:i18n key="LABEL_MESSAGE_WARNING" />: <s:property value="erroreInvio"/></p>
	</s:if>
	<s:elseif test="%{ticketId != null}">
		<p><wp:i18n key="LABEL_DEAR_USER" />, </p>
		<p><wp:i18n key="LABEL_ASSISTENZA_TECNICA_ESITO_INVIO_OK" /> <wp:i18n key="LABEL_ASSISTENZA_TECNICA_TICKET_ID" /> ${ticketId}.</p> 
		<p><wp:i18n key="LABEL_ASSISTENZA_TECNICA_PRESA_IN_CARICO" />.</p>
		<p><wp:i18n key="LABEL_ASSISTENZA_TECNICA_FUTURE_SEGNALAZIONI" />.</p>
	</s:elseif>
	<s:else>
		<p><wp:i18n key="LABEL_DEAR_USER" />,</p>
		<p><wp:i18n key="LABEL_ASSISTENZA_TECNICA_ESITO_INVIO_OK" />.</p>
		<p><wp:i18n key="LABEL_ASSISTENZA_TECNICA_PRESA_IN_CARICO" />.</p>			
	</s:else>
	<p></p>
	<p><wp:i18n key="LABEL_ASSISTENZA_TECNICA_SALUTI" />,<br/><wp:i18n key="LABEL_ASSISTENZA_TECNICA_DESTINATARIO" /></p>
 	
</div>