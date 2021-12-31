<%@ taglib prefix="wp" uri="aps-core.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<%-- WE976 --%>
<script type="text/javascript">
//<![CDATA[

<c:set var="cfg" value=""/>
<c:forEach items="${maps['checkPILiberoProf']}" var="currentItem">
	<c:set var="cfg" value="${currentItem.value}" />
</c:forEach>

var optPartitaIVALiberoProfessionista = "<c:out value="${cfg}"/>";

<c:set var="codici" value="#"/> 
<c:forEach items="${maps['tipiImpresaLiberoProf']}" var="currentItem">
	<c:set var="codici" value="${codici}${currentItem.key}#" />
</c:forEach>

var codiciLiberoProfessionista = "<c:out value="${codici}"/>"; 

<c:set var="cfg" value=""/>
<c:forEach items="${maps['checkPIImprSociale']}" var="currentItem">
	<c:set var="cfg" value="${currentItem.value}" />
</c:forEach>

var optPartitaIVAImpresaSociale = "<c:out value="${cfg}"/>";

<c:set var="codici" value="#"/> 
<c:forEach items="${maps['tipiImpresaSociale']}" var="currentItem">
	<c:set var="codici" value="${codici}${currentItem.key}#" />
</c:forEach>

var codiciImpresaSociale = "<c:out value="${codici}"/>"; 

	$(document).ready(function() {

		viewChoice();
		$('[id="tipoImpresa"]').change(function() {
			viewChoice();
		});
	});

	function viewChoice() {
 		var codiceCombo = "#"+$('[id="tipoImpresa"] option:selected').val()+"#";
		if(codiciLiberoProfessionista.indexOf(codiceCombo) >= 0 && optPartitaIVALiberoProfessionista=="1" ){
			$('#obbPI').hide();
		} else if(codiciImpresaSociale.indexOf(codiceCombo) >= 0 && optPartitaIVAImpresaSociale=="1" ){
			$('#obbPI').hide();
		}else{
			$('#obbPI').show();
		} 
	}
//]]>
</script>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp">
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2><wp:i18n key="TITLE_PAGE_REGISTRA_OE" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/field_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_messages.jsp" />

	<jsp:include page="stepsRegistrazione.jsp" />

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg"
			value="BALLOON_REG_IMPRESA_DATI_PRINC_IMPRESA" />
	</jsp:include>

	<p class="noscreen">
		[ <a href="#"><wp:i18n key="SKIP_TO_FORM_BUTTONS" /></a> ]
	</p>

	<form
		action="<wp:action path="/ExtStr2/do/FrontEnd/RegistrImpr/processPageDatiPrincImpresa.action" />"
		method="post">
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
		
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/mandatory_fields_message.jsp" />

		<jsp:include
			page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/datiimpresa/datiPrincipaliImpresaSection.jsp">
			<jsp:param name="noEdit" value="false" />
		</jsp:include>

		<p class="noscreen">
			[ <a id="buttons" href="#"><wp:i18n key="SKIP_TO_MAIN_CONTENT" /></a> ]
		</p>

		<div class="azioni">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_next.jsp" />
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_cancel.jsp" />		
		</div>
	</form>
</div>