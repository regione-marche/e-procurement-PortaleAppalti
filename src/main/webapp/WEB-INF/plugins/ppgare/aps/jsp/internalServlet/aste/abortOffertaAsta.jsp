<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<s:set var="helper" value="%{#session['dettOffertaAsta']}"/>
<c:set var="codice"><s:property value="%{#helper.asta.codice}"/></c:set>
<c:set var="codiceLotto"><s:property value="%{#helper.asta.codiceLotto}"/></c:set>


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2><wp:i18n key='TITLE_PAGE_ASTA_ANNULLA_OFFERTA'/></h2>

	<p><wp:i18n key='LABEL_ASTE_CANCEL_OFFERTA_1'/></p>	
	<p><wp:i18n key='LABEL_ASTE_CANCEL_OFFERTA_2'/></p>
	
	<div class="azioni">
		<form action="<wp:action path="/ExtStr2/do/FrontEnd/Aste/cancelOfferta.action" />" method="post" class="azione">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			
			<div>
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_confirm_cancel.jsp" />
				<input type="hidden" name="codice" value="${codice}" />
				<input type="hidden" name="codiceLotto" value="${codiceLotto}"/>
			</div>
		</form>
		
		<c:choose>
			<c:when test="${sessionScope.page eq 'datioperatore'}">
			<c:set var="href" value="/ExtStr2/do/FrontEnd/Aste/openPageDatiOperatore.action" />
			</c:when>
			<c:when test="${sessionScope.page eq 'datiofferta'}">
			<c:set var="href" value="/ExtStr2/do/FrontEnd/Aste/openPageDatiOfferta.action" />
			</c:when>
			<c:when test="${sessionScope.page eq 'firmatari'}">
			<c:set var="href" value="/ExtStr2/do/FrontEnd/Aste/openPageFirmatari.action" />
			</c:when>
			<c:when test="${sessionScope.page eq 'uploadpdf'}">
			<c:set var="href" value="/ExtStr2/do/FrontEnd/Aste/openPageUploadPdf.action" />
			</c:when>
			<c:when test="${sessionScope.page eq 'riepilogo'}">
			<c:set var="href" value="/ExtStr2/do/FrontEnd/Aste/openPageRiepilogo.action" />
			</c:when>
		</c:choose>
		
		<form action="<wp:action path="${href}" />" method="post" class="azione">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			
			<div>
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_back_to_edit.jsp" />
				<input type="hidden" name="codice" value="${codice}" />
				<input type="hidden" name="codiceLotto" value="${codiceLotto}"/>
			</div>
		</form>
		
	</div>
</div>