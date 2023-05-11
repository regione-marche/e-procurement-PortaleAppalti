<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="wp" uri="aps-core.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp">
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>


<c:set var="genere" value='${sessionScope.comunicazioniGenereProcedura}'/>
<c:set var="pathProcedura" value=""/>

<c:if test="${genere == 10}">
	<c:set var="pathProcedura" value="/ExtStr2/do/FrontEnd/Bandi/viewIscrizione.action"/>
</c:if>
<c:if test="${genere == 20}">
	<c:set var="pathProcedura" value="/ExtStr2/do/FrontEnd/Cataloghi/viewIscrizione.action"/>
</c:if>


<div class="portgare-list">

	<h2><wp:i18n key="TITLE_COMUNICAZIONI_LISTA_INVII" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_LISTA_INVII" />
	</jsp:include>
		
	<s:if test="%{elencoInvii.size>0}">
		<table class="info-table">
			<thead>
				<tr>
					<th scope="col"><wp:i18n key="LABEL_COMUNICAZIONI_TIPO_INVIO" /></th>
					<th scope="col"><wp:i18n key="LABEL_COMUNICAZIONI_DATA_ACQUISIZIONE" /></th>
				</tr>
			</thead>
			<tbody>
				<s:iterator var="riga" value="elencoInvii" status="status">
					<tr>
						<td>
							<a href='<wp:action path="/ExtStr2/do/FrontEnd/Comunicazioni/dettaglioRichiesta.action"/>&amp;id=<s:property value="%{#riga.id}" />&amp;codiceElenco=<s:property value="%{codiceElenco}" />&amp;${tokenHrefParams}'
								title="Visualizza dettaglio invio"	>
								<%-- <s:property value="%{#riga.tipo}" /> - --%>
								<s:iterator value="maps['tipiComunicazione']" var="m">
									<s:if test="%{#m.key == #riga.tipo}" >
										<s:property value="%{#m.value}" />
									</s:if>
								</s:iterator>
							</a>
						</td>
						<td>
							<s:if test="%{#riga.stato != 5}" >
								<s:date name="#riga.dataAggStato.time" format="dd/MM/yyyy HH:mm:ss" />
							</s:if>
						</td>
					</tr>
				</s:iterator>
			</tbody>
		</table>
	</s:if>
	<s:else>
		<wp:i18n key="LABEL_NO_COMUNICAZIONI" />.
	</s:else>
	
	<div class="back-link">
		<a href='<wp:action path="${pathProcedura}" />&amp;codice=<s:property value="%{codiceElenco}"/>&amp;${tokenHrefParams}'>
			<wp:i18n key="LINK_BACK_TO_PROCEDURE" />
		</a>
	</div>
</div>