<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>


<s:set name="visualizzaAmmissione" value="false"/>
<s:iterator var="item" value="lotti" status="stat">
	<s:if test="%{#item.faseGara >= 6}" >
		<s:set name="visualizzaAmmissione" value="true"/>
	</s:if>
</s:iterator>


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2><wp:i18n key="LABEL_VALUTAZIONE_TECNICA" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_GARA_TEL_VALUTAZIONE_TECNICA_LOTTI"/>
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_messages.jsp" />

	<div class="detail-row">
		<div class="table-container">
			<table id="tableLotti" summary="Tabella lotti" class="info-table">
				<thead>
					<tr>
						<th scope="col"><wp:i18n key="LABEL_CODICE_LOTTO" /></th>
						<th scope="col"><wp:i18n key="LABEL_OGGETTO" /></th>
						<s:if test="%{visualizzaAmmissione}" >
							<th scope="col"><wp:i18n key="LABEL_AMMISSIONE" /></th>
						</s:if>
					</tr>
				</thead>
				<tbody>
					<s:iterator var="item" value="lotti" status="stat">
						<tr>
							<td class="azioni">
								<a href="<wp:action path="/ExtStr2/do/FrontEnd/GareTel/espletGaraViewValTec.action"/>&amp;codice=${param.codice}&amp;codiceLotto=<s:property value="%{#item.lotto}"/>"
									title='<wp:i18n key="LABEL_VISUALIZZA_VALUTAZIONE_TECNICA" />' >
									<s:property value="#item.codiceInterno" /> 
								</a>
							</td>
							<td>
								<s:property value="#item.oggetto" />
							</td>
							<s:if test="%{visualizzaAmmissione}" >
								<td>
									<s:if test="%{#item.faseGara >= 6}" >
										<s:property value="#item.ammissione" />
									</s:if>
								</td>
							</s:if>
						</tr>
					</s:iterator>
				</tbody>
			</table>
		</div>
	</div>
</div>

<div class="back-link">
	<a href="<wp:action path="/ExtStr2/do/FrontEnd/GareTel/espletGaraFasi.action" />&amp;codice=${param.codice}&amp;ext=${param.ext}">
		<wp:i18n key="LINK_BACK" />
	</a>
</div>



