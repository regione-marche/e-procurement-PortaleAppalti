<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/send_blockUI.jsp" />

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">
	
	<h2>
		<wp:i18n key='TITLE_PAGE_ALBO_FORNITORI'/>
	</h2>
	
	<p>
		<wp:i18n key='LABEL_ALBO_FORNITORI_CONFERMA_RICHIESTA'/>
	</p>
	
	<div class="azioni">
		<wp:i18n key="LABEL_YES" var="valueYesButton" />
		<wp:i18n key="LABEL_NO" var="valueNoButton" />
		
		<form action="<wp:action path="/ExtStr2/do/FrontEnd/Bandi/confermaIscrizioneAlboFornitori.action" />" method="post" class="azione">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			
			<div>
				<s:submit value="%{#attr.valueYesButton}" cssClass="button block-ui" />
			</div>
		</form>
		<form action="<wp:action path="/ExtStr2/do/FrontEnd/Bandi/annullaIscrizioneAlboFornitori.action" />" method="post" class="azione">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			
			<div>
				<s:submit value="%{#attr.valueNoButton}" cssClass="button block-ui" />
			</div>
		</form>
	</div>
</div>