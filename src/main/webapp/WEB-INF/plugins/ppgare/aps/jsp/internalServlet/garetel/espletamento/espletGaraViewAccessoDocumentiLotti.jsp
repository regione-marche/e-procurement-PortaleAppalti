<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2><wp:i18n key="TITLE_PAGE_GARETEL_ACCESSO_DOCUMENTI" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_GARA_TEL_ACCESSO_DOCUMENTI_LOTTI"/>
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
					</tr>
				</thead>
				<tbody>
					<s:iterator var="item" value="lotti" status="stat">
						<tr>
							<td class="azioni">
								<a href="<wp:action path="/ExtStr2/do/FrontEnd/GareTel/espletGaraViewAccessoDocumenti.action"/>&amp;codice=${param.codice}&amp;codiceLotto=<s:property value="%{#item.lotto}"/>"
								   title='<wp:i18n key="LABEL_VISUALIZZA_ACCESSO_DOCUMENTI" />' >
									<s:property value="#item.codiceInterno" /> 
								</a>
							</td>
							<td>
								<s:property value="#item.oggetto" />
							</td>
						</tr>
					</s:iterator>
				</tbody>
			</table>
		</div>
	</div>
</div>

<div class="back-link">
	<a href="<wp:action path="/ExtStr2/do/FrontEnd/Bandi/view.action" />&amp;codice=${codice}&amp;ext=${param.ext}">
		<wp:i18n key="LINK_BACK_TO_PROCEDURE" />
	</a>
</div>