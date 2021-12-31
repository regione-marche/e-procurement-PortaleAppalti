<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">
	
	<h2><wp:i18n key='TITLE_PAGE_GARETEL_RICREA_BUSTA'/></h2>
	
	<p><wp:i18n key='LABEL_GARETEL_RICREA_BUSTA'/></p>
	
	<div class="azioni">
		<wp:i18n key="LABEL_YES" var="valueYesButton" />
			
		<form action="<wp:action path="/ExtStr2/do/FrontEnd/GareTel/ricreaBustaEco.action" />" method="post" class="azione">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />

			<div>
				<s:submit value="%{#attr.valueYesButton}" cssClass="button" />
				<input type="hidden" name="codice" value="<s:property value="codice" />"/>
				<input type="hidden" name="codiceGara" value="<s:property value="codiceGara" />"/>
				<input type="hidden" name="operazione" value="<s:property value="operazione" />"/>
				<input type="hidden" name="progressivoOfferta" value="<s:property value="progressivoOfferta" />"/>
			</div>
		</form>
	</div>
</div>