<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="wp" uri="aps-core.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp">
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<s:if test="%{genere == 10}">
	<c:set var="pathProcedura" value="/ExtStr2/do/FrontEnd/Bandi/viewIscrizione.action"/>
</s:if>
<s:elseif test="%{genere == 20}">
	<c:set var="pathProcedura" value="/ExtStr2/do/FrontEnd/Cataloghi/viewIscrizione.action"/>
</s:elseif>
<s:elseif test="%{genere == 4}">
	<c:set var="pathProcedura" value="/ExtStr2/do/FrontEnd/Contratti/view.action"/>
</s:elseif>
<s:elseif test="%{genere == 11}">
	<c:set var="pathProcedura" value="/ExtStr2/do/FrontEnd/Avvisi/view.action"/>
</s:elseif>
<s:else>
	<c:set var="pathProcedura" value="/ExtStr2/do/FrontEnd/Bandi/view.action"/>
</s:else>


<div class="portgare-list">

	<h2><wp:i18n key="TITLE_COMUNICAZIONI_INVIATE" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_COMUNICAZIONI_INVIATE" />
	</jsp:include>
		
	<s:if test="%{comunicazioni.dati.size>0}">
		<div class="list-summary">
			<wp:i18n key="SEARCH_RESULTS_INTRO" />
			<s:property value="model.iTotalDisplayRecords" />
			<wp:i18n key="SEARCH_RESULTS_OUTRO" />.
		</div>
		<form action="<wp:action path="/ExtStr2/do/FrontEnd/Comunicazioni/openPageComunicazioniInviate.action"/>"
			method="post">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />

			<table class="info-table">
				<thead>
					<tr>
						<th scope="col"><wp:i18n key="LABEL_COMUNICAZIONI_MITTENTE" /></th>
						<th scope="col"><wp:i18n key="LABEL_COMUNICAZIONI_OGGETTO" /></th>
						<th scope="col"><wp:i18n key="LABEL_COMUNICAZIONI_INVIATO_IL" /></th>
					</tr>
				</thead>
				<tbody>
					<s:iterator var="riga" value="comunicazioni.dati" status="status">
					
						<c:set var="href"> 
							&amp;idComunicazione=<s:property value="%{idComunicazione}"/>
							<c:if test="${! empty param.ext}">&amp;ext=${param.ext}</c:if>
							&amp;${tokenHrefParams}
						</c:set>
						
						<tr>
							<td>
								<s:property value="%{mittente}"/>
							</td>
							<td>
								<a href='<wp:action path="/ExtStr2/do/FrontEnd/Comunicazioni/openPageDettaglioComunicazioneInviata.action" />${href}'>
									<s:property value="%{oggetto}" />
								</a>
							</td>
							<td>
								<s:date name="dataInvio.time" format="dd/MM/yyyy HH:mm:ss" />
							</td>
						</tr>
					</s:iterator>
				</tbody>
			</table>
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/pagination.jsp"></jsp:include>
			<s:hidden name="comunicazioniCodiceProcedura" value="%{comunicazioniCodiceProcedura}"/>
			<s:hidden name="genere" value="%{genere}"/> 
			<s:hidden name="namespace" value="%{namespace}"/>
			<s:hidden name="actionName" value="%{actionName}"/>
		</form>
	</s:if>
	<s:else>
		<wp:i18n key="LABEL_NO_COMUNICAZIONI" />.
	</s:else>
	<div class="back-link">
		<s:if test="%{genere != null}">
			<c:set var="href"> 
				&amp;codice=<s:property value="%{comunicazioniCodiceProcedura}"/>
				<c:if test="${! empty param.ext}">&amp;ext=${param.ext}</c:if>
				&amp;${tokenHrefParams}
			</c:set>
			<a href='<wp:action path="${pathProcedura}" />${href}'>
				<wp:i18n key="LINK_BACK_TO_PROCEDURE" />
			</a>
		</s:if>
		<s:else>
		 	<a href="ppcommon_area_personale.wp">
		 		<wp:i18n key="LINK_BACK_TO_AREAPERSONALE" />
		 	</a>
		</s:else>
	</div>
</div>