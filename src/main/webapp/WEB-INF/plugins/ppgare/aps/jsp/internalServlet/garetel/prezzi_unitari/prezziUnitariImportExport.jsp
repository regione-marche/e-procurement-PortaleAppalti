<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2><wp:i18n key="TITLE_PAGE_MODIFICA_PREZZI_UNITARI" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/field_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_MODIFICA_PREZZI_UNITARI"/>
	</jsp:include>

	<s:url id="urlExcelVariazionePrezziUnitariDownload" namespace="/do/FrontEnd/GareTel" action="createVariazionePrezziUnitariExcel">
		<s:param name="urlPage">${currentPageUrl}</s:param>
		<s:param name="currentFrame">${param.currentFrame}</s:param>
	</s:url>

	<h3><wp:i18n key="LABEL_SELEZIONA_UNA_OPZIONE" />:</h3>
	<ul>
		<li>
			<p>
				<a class="list-element bkg go important" title='<wp:i18n key="LABEL_SCARICA_XLS_CON_TUOI_PREZZI_UNITARI" />'
					 href='${urlExcelVariazionePrezziUnitariDownload}&amp;${tokenHrefParams}'>
					<wp:i18n key="LABEL_SCARICA_XLS_CON_TUOI_PREZZI_UNITARI" />
				</a>
				<br/>
				<span>
					<wp:i18n key="LABEL_SCARICA_XLS_CON_TUOI_PREZZI_UNITARI_INFO" />
				</span>
			</p>
		</li>
		<li>
			<p>
				<a class="list-element bkg go important" title='<wp:i18n key="LABEL_IMPORTA_NUOVI_PREZZI_UNITARI_E_VALIDA" />'
					 href='<wp:action path="/ExtStr2/do/FrontEnd/GareTel/openPageImportPrezziUnitari.action"/>&amp;${tokenHrefParams}'>
					<wp:i18n key="LABEL_IMPORTA_NUOVI_PREZZI_UNITARI_E_VALIDA" />
				</a>
				<br/>
				<span>
					<wp:i18n key="LABEL_AGGIORNA_PREZZI_UNITARI_INSERITI_IN_XLS" />
				</span>
			</p>
		</li>
	</ul>
	<div class="back-link">
	    <form action="<wp:action path="/ExtStr2/do/FrontEnd/GareTel/processPageOffTelOfferta.action" />&amp;${tokenHrefParams}" method="post">
            <input type="hidden" name="codice" value="${codice}" />
            <jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_previous.jsp" />
		</form>
	</div>
</div>