<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="wp" uri="aps-core.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp">
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>
<c:set var="skin" value=""/>
<c:if test="${! empty param.skin}">
	<c:set var="skin" value="${param.skin}"/>
</c:if>
<c:if test="${empty skin}">
	<c:set var="skin" value="${cookie.skin.value}"/>
</c:if>


<div class="portgare-list">

	<h2><wp:i18n key="TITLE_COMUNICAZIONI_NEWS" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_NEWS" />
	</jsp:include>

	<div class="list-summary">
		<s:text name="note.searchIntro" />&#32;<s:property value="model.iTotalDisplayRecords" />&#32;<s:text name="note.searchOutro" />.
	</div>

	<s:if test="%{comunicazioni.dati.size > 0}">
		<form action="<wp:action path="/ExtStr2/do/FrontEnd/Comunicazioni/news.action"/>" method="post">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			<table class="info-table">
				<s:iterator var="riga" value="comunicazioni.dati" status="status">
					
					<s:if test="%{genere == 10}" >
						<c:set var ="href"><wp:action path="/ExtStr2/do/FrontEnd/Bandi/viewIscrizione.action" />&amp;codice=<s:property value="codice"/></c:set>
					</s:if>
					<s:elseif test="%{genere == 20}" >
						<c:set var ="href"><wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/viewIscrizione.action" />&amp;codice=<s:property value="codice"/></c:set>
					</s:elseif>
					<s:elseif test="%{genere == 100}" >
						<c:set var ="href"><wp:action path="/ExtStr2/do/FrontEnd/Bandi/viewFromLotto.action" />&amp;codice=<s:property value="codice"/></c:set>
					</s:elseif>
					<%--
 					<s:elseif test="%{genere == 4}" >
 						<c:set var ="href"><wp:action path="/ExtStr2/do/FrontEnd/Contratti/view.action" />&amp;codice=<s:property value="codice"/></c:set>
 					</s:elseif>
					--%>
					<s:elseif test="%{genere == 11}" >
						<c:set var ="href"><wp:action path="/ExtStr2/do/FrontEnd/Avvisi/view.action" />&amp;codice=<s:property value="codice"/></c:set>
					</s:elseif>
					<s:else>
						<c:set var ="href"><wp:action path="/ExtStr2/do/FrontEnd/Bandi/view.action" />&amp;codice=<s:property value="codice"/></c:set>
					</s:else>

					<div class="list-item">
						<div class="list-item-row">
							<label><wp:i18n key="LABEL_COMUNICAZIONI_DATAINVIO" /> : </label>
							<s:date name="dataInserimento.time" format="dd/MM/yyyy" />
						</div>
						<div class="list-item-row">
							<label><wp:i18n key="LABEL_COMUNICAZIONI_OGGETTO" /> : </label>
							<s:property value="oggetto" escape="false"/>
						</div>
						<div class="list-item-row">
							<label><wp:i18n key="LABEL_COMUNICAZIONI_TESTO" /> : </label>
							<s:property value="testo" escape="false"/>
						</div>
						<div class="list-item-row">
							<label><wp:i18n key="LABEL_RIFERIMENTO_PROCEDURA" /> : </label>
							<s:property value="codice"/>&nbsp(<s:property value="tipologia"/>)
						</div>
						<div class="list-item-row">
							<label><wp:i18n key="LABEL_TITOLO_PROCEDURA" /> : </label>
							<s:property value="titolo"/>
						</div>
						<div class="list-action">
							<c:choose>
								<c:when test="${skin == 'highcontrast' || skin == 'text'}">
									<a href='${href}' title='<wp:i18n key="LINK_VIEW_DETAIL" />' >
										<wp:i18n key="LINK_VIEW_DETAIL" />
									</a>
								</c:when>
								<c:otherwise>
									<a href='${href}' title='<wp:i18n key="LINK_VIEW_DETAIL" />' class="bkg detail-very-big">
										<wp:i18n key="LINK_VIEW_DETAIL" />
									</a>
								</c:otherwise>
							</c:choose>
						</div>
					</div>
				</s:iterator>
			</table>
			
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/pagination.jsp"></jsp:include>
		
		</form>
	</s:if>	
	
</div>