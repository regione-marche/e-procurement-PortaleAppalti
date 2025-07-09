<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">
	
	<h2><wp:i18n key="TITLE_PAGE_GARETEL_RINUNCIA_OFFERTA" /></h2>

	<p>
	<c:choose>
		<c:when test="${riepilogo}">
			<p>
				<wp:i18n key='LABEL_GARETEL_ANNULLA_RINUNCIA_1'/> <s:date name="dataPubblicazione" format="dd/MM/yyyy" /> 
				<wp:i18n key='LABEL_EFFETTUATE_CON_SUCCESSO_IL'/> <s:date name="dataInvio" format="dd/MM/yyyy HH:mm:ss" />.
			</p>
			<p>
				<wp:i18n key='LABEL_GARETEL_ANNULLA_RINUNCIA_2'/>
			</p>
		</c:when>
		<c:otherwise>
			<s:if test="%{codiceSistema == 'LAPISOPERA'}"> 
	   			${LABEL_RICHIESTA_CON_ID}
	   		</s:if>
	   		<s:else>
		   		<wp:i18n key="LABEL_RICHIESTA_INVIATA_IL" /> <s:date name="dataInvio" format="dd/MM/yyyy HH:mm:ss" />
				<s:if test="dataProtocollo != null"><wp:i18n key="LABEL_RICHIESTA_PROTOCOLLATA_IL" /> <s:property value="dataProtocollo"/></s:if>
		  		<s:if test="%{presentiDatiProtocollazione}"><wp:i18n key="LABEL_CON_ANNO" /> <s:property value="annoProtocollo"/> <wp:i18n key="LABEL_E_NUMERO" /> <s:property value="numeroProtocollo"/></s:if>
		  	</s:else>
		    .
		</c:otherwise>
	</c:choose>
	</p>
	
	<s:if test="%{msgErrore != null}">
		<p><wp:i18n key="LABEL_MESSAGE_WARNING" />: <s:property value="%{msgErrore}"/></p>
	</s:if>

	<div class="back-link">
		<a href="<wp:action path="/ExtStr2/do/FrontEnd/Bandi/view.action" />&amp;ext=${param.ext}&amp;codice=${codice}">
			<wp:i18n key="LINK_BACK_TO_PROCEDURE" />
		</a>
	</div>
</div>