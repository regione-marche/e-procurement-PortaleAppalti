<%@ taglib prefix="wp"   uri="aps-core.tld" %>
<%@ taglib prefix="c"    uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wpsa" uri="apsadmin-core.tld" %>
<%@ taglib prefix="s"    uri="/struts-tags" %>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-list">

    <s:if test="%{isAttivo == null || isAttivo}">
        <c:set var="titleKey" value="TITLE_PAGE_LISTA_CATALOGHI" />
        <c:set var="balloonKey" value="BALLOON_LISTA_CATALOGHI" />
    </s:if>
    <s:else>
        <c:set var="titleKey" value="TITLE_PAGE_LISTA_CATALOGHI_ARCHIVIATI" />
        <c:set var="balloonKey" value="BALLOON_LISTA_CATALOGHI_ARCHIVIATI" />
    </s:else>

	<h2><wp:i18n key="${titleKey}" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="${balloonKey}"/>
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />

	<form action="<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/listAllCataloghi.action" />" method="post">
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
		
		<c:if test="${! empty dataUltimoAggiornamento}">
			<div class="align-right important last-update-list">
				<wp:i18n key="LABEL_LAST_UPDATE" />	<s:date name="dataUltimoAggiornamento" format="dd/MM/yyyy" />
			</div>
		</c:if>

		<wpsa:subset source="listaCataloghi" count="10" objectName="groupLista" advanced="true" offset="5">
			
			<s:set name="group" value="#groupLista" />

			<s:include value="/WEB-INF/plugins/ppcommon/aps/jsp/pager_info.jsp" />

			<s:iterator var="bando">
				<div class="list-item ">
				
					<wp:i18n key="LABEL_MERCATO_ELETTRONICO" var="entita"/>
					<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/bandi/inc/itemBandoIscrizione.jsp">
						<jsp:param name="entita" value="${entita}"/>
					</jsp:include>
					
					<div class="list-action">
						<c:choose>
							<c:when test="${skin == 'highcontrast' || skin == 'text'}">
								<a href='<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/viewIscrizione.action" />&amp;codice=<s:property value="codice"/>&amp;${tokenHrefParams}'
								   title='<wp:i18n key="LINK_VIEW_DETAIL" />'>
									<wp:i18n key="LINK_VIEW_DETAIL" />
								</a>
							</c:when>
							<c:otherwise>
								<a href='<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/viewIscrizione.action" />&amp;codice=<s:property value="codice"/>&amp;${tokenHrefParams}'
								   title='<wp:i18n key="LINK_VIEW_DETAIL" />' class="bkg detail-very-big">
									 <wp:i18n key="LINK_VIEW_DETAIL" />
								</a>
							</c:otherwise>
						</c:choose>
					</div>
				</div>
			</s:iterator>
			
			<s:include value="/WEB-INF/plugins/ppcommon/aps/jsp/pager_navigation.jsp" />
			
		</wpsa:subset>
	</form>
</div>