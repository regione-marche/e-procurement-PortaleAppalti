<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/send_blockUI.jsp" />

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2>Conferma rinnovo iscrizione</h2>

	<p>
		Sicuro di voler procedere con la richiesta di rinnovo iscrizione?
	</p>

	<div class="azioni">
		<form action="<wp:action path="/ExtStr2/do/FrontEnd/IscrAlbo/rinnovo.action" />" method="post" class="azione">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			
			<div>
				<s:submit value="Sì" cssClass="button block-ui" />
				<s:hidden name="codice" value="%{#helper.codice}" />
				<s:hidden name="tipoElenco" value="%{tipoElenco}" />
				<input type="hidden" name="ext" value="${param.ext}" />
			</div>
		</form>
		<form action="<wp:action path="/ExtStr2/do/FrontEnd/IscrAlbo/openPageRinnovoDocumentiClear.action" />" method="post" class="azione">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			
			<div>
				<s:submit value="No" cssClass="button" />
				<s:hidden name="codice" value="%{#helper.codice}" />
				<s:hidden name="tipoElenco" value="%{tipoElenco}" />
				<input type="hidden" name="ext" value="${param.ext}" />
			</div>
		</form>
	</div>
</div>