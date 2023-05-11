<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>	


<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/send_blockUI.jsp" />

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>
		
		
<div class="portgare-view">

	<h2><wp:i18n key="TITLE_PAGE_LISTA_DOCUMENTI_SMAT" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_LISTA_DOCUMENTI_SMAT"/>
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
	
	<s:if test="%{documenti.size > 0}">
		<s:iterator id="doc" value="documenti">
			<div class = "list-item">
				<div class = "list-item-row">
					<label><wp:i18n key="LABEL_COD_STA" />: </label>
					<s:property value="codice_sta" />
				</div>
				<div class = "list-item-row">
					<label><wp:i18n key="LABEL_DESC_STA" />: </label>
					<s:property value="descrizione_sta" />
				</div>
				<div class="list-action">
					<s:url action="detail" id="urldoc" namespace="/do/FrontEnd/DocumentiSmat">
						<s:param name="codice">${codice_sta}</s:param>
					</s:url>				
					<a href="${urldoc}"
						   title='<wp:i18n key="LINK_VIEW_DOCUMENT_SMAT" />' class="bkg detail-very-big">
						<wp:i18n key="LINK_VIEW_DOCUMENT_SMAT" />
					</a>
				
				</div>
			</div>	
		</s:iterator>
	</s:if>

</div>