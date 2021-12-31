<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<s:url id="urlPdf" namespace="/do/FrontEnd/DatiImpr" action="createPdf">
	<s:param name="urlPage">${currentPageUrl}</s:param>
	<s:param name="currentFrame">${param.currentFrame}</s:param>
</s:url>
<wp:i18n key="LABEL_DATI_OE_STAMPA_ANAGRAFICA" var="labelStampaAnagrafica" />

<div class="portgare-view">

<h2><wp:i18n key="TITLE_PAGE_DATI_OE" /></h2>

<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/field_errors.jsp" />
<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />

<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_DETT_IMPRESA"/>
</jsp:include>

<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/datiimpresa/datiRiepilogoSection.jsp">
	<jsp:param name="sessionIdObj" value="dettAnagrImpresa" />
</jsp:include>

	<div id="linkCreatePdf">
		<c:choose>
			<c:when test="${skin == 'highcontrast' || skin == 'text'}">
				<s:a href="%{#urlPdf}&amp;%{#attr.tokenHrefParams}" title="%{#labelStampaAnagrafica}"><wp:i18n key="BUTTON_DATI_OE_STAMPA_ANAGRAFICA" /></s:a>
			</c:when>
			<c:otherwise>
				<a href="${urlPdf}&amp;${tokenHrefParams}" title="${labelStampaAnagrafica}" class="bkg download"></a> 
			</c:otherwise>
		</c:choose>
		${labelStampaAnagrafica}.
	</div>

	<div class="azioni">
		<div class="azione">
			<input type="button" id="createPdf" value="<wp:i18n key="BUTTON_DATI_OE_STAMPA_ANAGRAFICA" />" 
						 title="${labelStampaAnagrafica}" 
						 onclick="document.location.href='${urlPdf}'" class="button" style="display: none;"/>
		</div>
		<form action="<wp:url page="ppgare_impr_aggdati"/>" method="post" class="azione">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			<div>
				<input type="hidden" name="fromUrl" value="<wp:url />" />
				<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/datiimpresa/inc/button_edit_datiimpresa.jsp" />
			</div>
		</form>
		<form action="<wp:url page="ppgare_impr_vardati"/>" method="post" class="azione">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			<div>
				<input type="hidden" name="fromUrl" value="<wp:url />" />
				<wp:i18n key="BUTTON_DATI_OE_RICHIEDI_VARIAZIONE" var="valueRichVariazioneButton" />
				<wp:i18n key="TITLE_DATI_OE_RICHIEDI_VARIAZIONE" var="titleRichVariazioneButton" />
				<s:submit value="%{#attr.valueRichVariazioneButton}" title="%{#attr.titleRichVariazioneButton}" cssClass="button"></s:submit>
			</div>
		</form>
	</div>

	<script type="text/javascript">
		<!--//--><![CDATA[//><!--
		document.getElementById("createPdf").style.display = "inline";
		document.getElementById("linkCreatePdf").style.display = "none";
		//--><!]]>
	</script>
</div>