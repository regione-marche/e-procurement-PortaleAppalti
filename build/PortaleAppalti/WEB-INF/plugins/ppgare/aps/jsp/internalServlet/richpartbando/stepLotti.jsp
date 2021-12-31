<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<s:if test="%{#session.dettPartecipGara.tipoEvento == 1}">
	<c:set var="titolo"><wp:i18n key='TITLE_PAGE_RICHPART_PRESENTA_PARTECIPAZIONE'/></c:set>
	<c:set var="codiceBalloon" value="BALLOON_PART_GARA_LOTTI"/>
</s:if>
<s:elseif test="%{#session.dettPartecipGara.tipoEvento == 2}">
	<c:set var="titolo"><wp:i18n key='TITLE_PAGE_RICHPART_PRESENTA_OFFERTA'/></c:set>
	<c:set var="codiceBalloon" value="BALLOON_INVIO_OFFERTA_LOTTI"/>
</s:elseif>
<s:else>
	<c:set var="titolo"><wp:i18n key='TITLE_PAGE_RICHPART_PRESENTA_DOCUMENTAZIONE'/></c:set>
	<c:set var="codiceBalloon" value="BALLOON_INVIO_DOC_ART48_LOTTI"/>
</s:else>	


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2>${titolo}</h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/field_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_messages.jsp" />

	<jsp:include page="stepsPartecipazione.jsp" />

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="${codiceBalloon}"/>
	</jsp:include>

	<form action="<wp:action path="/ExtStr2/do/FrontEnd/RichPartBando/processPageLotti.action" />" method="post">
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
		
		<fieldset>
			<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_LOTTI" /></legend>

			<div id="div-select-all" class="fieldset-row first-row last-row noscreen">
				<div class="checkbox-selection">
					<input type="checkbox" id="select-all" />
				</div>
				<div class="element">
					<label for="select-all"><wp:i18n key="LABEL_SELEZIONA_TUTTI_LOTTI" /></label>
				</div>
			</div>

			<s:iterator value="lotti" var="lotto" status="status">
				<div class="fieldset-row <s:if test="#status.first">first-row</s:if><s:if test="#status.last">last-row</s:if>">					
					<div class="checkbox-selection">
						<s:checkbox cssClass="checkLotto" name="lottoSelezionato" fieldValue="%{codiceLotto}" id="lottoSelezionato%{codiceLotto}" 
									value="%{checkLotto[#status.index]}" ></s:checkbox>
					</div>
					<div class="element">
						<label for="lottoSelezionato<s:property value='%{codiceLotto}'/>">
							<wp:i18n key="LABEL_LOTTO" />&nbsp;<s:property value="%{codiceInterno}"/>&nbsp;-&nbsp;<s:property value="oggetto" />
						</label>
					</div>
				</div>
			</s:iterator>
		</fieldset>

		<div class="azioni">
			<input type="hidden" name="page" value="lotti"/>
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_previous.jsp" />
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_next.jsp" />
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_cancel.jsp" />
		</div>
	</form>
</div>

<script type="text/javascript">
$(document).ready(function(){
	$("#div-select-all").removeClass("noscreen");
	
    $("#select-all").on("change", function(){
      $(".checkLotto").prop('checked', $(this).prop("checked"));
      });
});
</script>
