<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/internalServlet/fileupload/uploadFile.jsp" />
<%-- ******************************************************************************** --%>
<script type="text/javascript">
<!--//--><![CDATA[//><!--
	
	
// apertura della pagina...
$(document).ready(function() {
	$('inviaFattura').disable();
});	
//--><!]]>
</script>
<%-- ******************************************************************************** --%>
<style>
<!--
.button:disabled {
    background-color: #dddddd;
    border: 1px solid #06465D!important;
    margin-right: 0;
	color:#000000;
}
-->
</style>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp">
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2><wp:i18n key="TITLE_PAGE_EORDERS_UPLOAD_FATTURE" /> <s:property value="orderCode"/> </h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_EORDERS_UPLOAD_FATTURE" />
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
<%-- 	<form action="<wp:action path="/ExtStr2/do/FrontEnd/EOrders/upload.action" />" method="post" > --%>
		<div class="detail-section first-detail-section">
			<h3 class="detail-section-title">
				<span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_FATT_UPLOAD" />
			</h3>
				
		</div>
		<div class="detail-section">
				<form action="<wp:action path="/ExtStr2/do/FrontEnd/EOrders/upload.action" />"
							          		  method="post" enctype="multipart/form-data" id="docFatturaForm">
<%-- 					<span class="noscreen"></span><input type="file" name="docUlteriore" size="20" accept=".p7m,.tsd,.pdf,.xml" />&nbsp; --%>
					<span class="noscreen"></span><input type="hidden" name="docFatturaDesc" id="docFatturaDesc" /><br>
					<span class="noscreen"></span><input type="file" name="docFattura" id="docFattura" size="20" accept=".p7m,.xml" /><br>
					
					<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
					<input type="hidden" name="id" value="${id}" />
					<input type="hidden" name="orderCode" value="${orderCode}" />
					<wp:i18n key="BUTTON_ATTACH_FILE_FIRMATO" var="valueAttachButton" />
					<wp:i18n key="TITLE_ATTACH_FILE_FIRMATO" var="titleAttachButton" />
					<s:submit value="%{#attr.valueAttachButton}" title="%{#attr.titleAttachButton}" cssClass="button block-ui" />
					
					
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/internalServlet/fileupload/fileupload.jsp" >
					<jsp:param value="docFatturaDesc" name="inputDescr"/>
					<jsp:param value="docFattura" name="inputFile"/>
					<jsp:param value="<wp:action path='/ExtStr2/do/FrontEnd/EOrders/uploadFile.action' />" name="actionUrl"/>
				</jsp:include>
			</form>
		</div>
		<c:if test="${not empty warningInvioFattura}">
			<div class="detail-section">
				<wp:i18n key="LABEL_FATT_FILENAMEUPLOADED" var="valueAttachButton" /> : <s:property value="docFatturaFileName"/>
			</div>
		</c:if>

		
	<c:if test="${not empty warningInvioFattura}">
		<div class="detail-section">
			<b><wp:i18n key="${warningInvioFattura}" /></b>
		</div>
	</c:if>
	<form action="<wp:action path="/ExtStr2/do/FrontEnd/EOrders/upload.action" />" method="post" >
		<div class="azioni">
			<input type="hidden" name="id" value="${id}" />
			<input type="hidden" name="orderCode" value="${orderCode}" />
			<input type="hidden" name="progInvio" value="${progInvio}" />
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_cancel.jsp" />
			<wp:i18n key="BUTTON_FATT_INVIO" var="valueSendInvoiceButton" />
			<s:submit id="inviaFattura" value="%{#attr.valueSendInvoiceButton}" title="%{#attr.valueSendInvoiceButton}" cssClass="button" method="inviaFattura" disabled="sendDisabled"></s:submit>
		</div>
	</form>

</div>