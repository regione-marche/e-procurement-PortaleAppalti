<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags"%>


<s:set var="helper" value="%{datiImpresa}" />


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp">
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>  

<div class="portgare-view">
	
	<h2><wp:i18n key="TITLE_PAGE_AREA_PERSONALE_ACCETTA_CONSENSI" /></h2>
	
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_AREA_PERSONALE_ACCETTA_CONSENSI" />
	</jsp:include> 

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/field_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
 	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_messages.jsp" />

<%-- 	 
	<s:if test="%{ !soggettiRichiedentiPresenti }">
		<form action="<wp:url page="ppgare_impr_aggdati"/>" method="post" >
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			<div class="azioni">
				<input type="hidden" name="fromUrl" value="<wp:url />" />
				<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/datiimpresa/inc/button_edit_datiimpresa.jsp" />
			</div>
		</form>
	</s:if>
	<s:else>
		<form action="<wp:action path='/ExtStr2/do/FrontEnd/AreaPers/accettazioneConsensi.action' />" method="post" >
			<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/regimpresa/inc/accettazioneConsensi.jsp" />
				
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			<div class="azioni">
				<input type="hidden" id="username" name="username" value="${username}" />
				
				<wp:i18n key="BUTTON_CONFIRM" var="valueButtonAccetta" />
				<s:submit value="%{#attr.valueButtonAccetta}" title="%{#attr.valueButtonAccetta}" cssClass="button block-ui"></s:submit>
			</div>
		</form>
	</s:else>
 --%>
 
 		<form action="<wp:action path='/ExtStr2/do/FrontEnd/AreaPers/accettazioneConsensi.action' />" method="post" >
			<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/regimpresa/inc/accettazioneConsensi.jsp" />
				
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			<div class="azioni">
				<input type="hidden" id="username" name="username" value="${username}" />
				
				<wp:i18n key="BUTTON_CONFIRM" var="valueButtonAccetta" />
				<s:submit value="%{#attr.valueButtonAccetta}" title="%{#attr.valueButtonAccetta}" cssClass="button block-ui"></s:submit>
			</div>
		</form>
 	
</div>