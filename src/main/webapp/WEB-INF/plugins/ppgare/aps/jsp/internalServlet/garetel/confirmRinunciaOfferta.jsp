<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<%--
<s:set name="isFromListaOfferte" value="%{fromListaOfferte == 1}" />
 --%>
 
		<c:set var="href" value="/ExtStr2/do/FrontEnd/GareTel/openPageRinunciaOfferta.action"/>
		<c:set var="hrefConfirm" value="/ExtStr2/do/FrontEnd/GareTel/annullaRinunciaOfferta.action"/>
		<c:set var="info">
			<p><wp:i18n key='LABEL_GARETEL_ANNULLA_RINUNCIA_INVIO_1'/></p>
			<p><wp:i18n key='LABEL_GARETEL_ANNULLA_RINUNCIA_INVIO_2'/></p>
			<p><wp:i18n key='LABEL_GARETEL_ANNULLA_RINUNCIA_INVIO_3'/></p>	
		</c:set>


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2><wp:i18n key='TITLE_PAGE_GARETEL_RINUNCIA_OFFERTA'/></h2>
	
	${info}

	<div class="azioni">
		<wp:i18n key="LABEL_YES" var="valueYesButton" />
		<wp:i18n key="LABEL_NO" var="valueNoButton" />
		
		<form action='<wp:action path="${hrefConfirm}" />' method="post" class="azione">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			<div>
				<s:if test="!sendBlocked">
					<s:submit value="%{#attr.valueYesButton}" cssClass="button" />
				</s:if>
				<input type="hidden" name="codice" value="${codice}"/>
			</div>
		</form>
		
		<form action='<wp:action path="${href}" />' method="post" class="azione">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			<div>
				<s:submit value="%{#attr.valueNoButton}" cssClass="button" />
				<input type="hidden" name="codice" value="${codice}"/>
			</div>
		</form>
	</div>		
</div>