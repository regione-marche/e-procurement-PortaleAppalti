<%@ taglib prefix="wp"  uri="aps-core.tld" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="s"   uri="/struts-tags" %>

<s:set name="helper" value="%{#session['offertaTecnica']}" />

<s:set name="gara" value="%{#helper.gara}" />
<s:set name="cig" value="%{#gara.cig}"/>
<s:set name="cup" value="%{#gara.cup}"/>

<%-- 
Offerta OEPV se:
	- procedura di gara telematica (GARTEL=1)
	- gara con upload documenti ed inserimento importi (OFFTEL=1)
	- gara/lotto OEPV (CODMODAGG=6) 
	- esistenza di almeno un criterio di valutazione (almeno un'occorrenza 
	  ottenuta da GOEV INNER JOIN G1CRIDEF con FORMATO<>100 e TIPPAR=2)	  
--%>
<s:set name="OEPV" value="%{#session['offertaTecnica'].criteriValutazioneVisibili}"/>


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2><wp:i18n key="TITLE_PAGE_GARETEL_OFFERTA_TECNICA" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/field_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />

	<jsp:include page="stepsOffertaTecnica.jsp" />

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_WIZ_OFFERTA_TECNICA_TEL"/>
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/skip_form_buttons.jsp" />

	<form action="<wp:action path="/ExtStr2/do/FrontEnd/GareTel/processPageOffTecOfferta.action" />" method="post" 
		  id="formOffertaTecnica" name="formOffertaTecnica">
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
		
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/mandatory_fields_message.jsp" />
				
		<fieldset>
			<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_GARETEL_DATI_DELLA_GARA" /></legend>
			<div class="fieldset-row first-row">
				<div class="label">
					<label><wp:i18n key="LABEL_TITOLO" /> :</label>
				</div>
				<div class="element">
					<s:property value="%{#gara.oggetto}" /> 
					<c:if test="${! empty gara.cig}">- <wp:i18n key="LABEL_CIG" /> : <s:property value="%{#gara.cig}" /></c:if>
					<c:if test="${! empty gara.cup}">- <wp:i18n key="LABEL_CUP" /> : <s:property value="%{#gara.cup}" /></c:if>
				</div>
			</div>
			
			<s:if test="{!#session.riepilogoBuste.listaCodiciInterniLotti.isEmpty()}">
				<div class="fieldset-row">
					<div class="label">
						<label><wp:i18n key="LABEL_LOTTO" /> :</label>
					</div>
					<div class="element">
						<s:property value="%{#session.riepilogoBuste.listaCodiciInterniLotti.get(#session['offertaTecnica'].codice)}" /> - <s:property value="%{#session.riepilogoBuste.busteEconomicheLotti.get(#session['offertaTecnica'].codice).oggetto}" />
					</div>
				</div>
			</s:if>
			
		</fieldset>
		
		<fieldset>
			<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="TITLE_PAGE_GARETEL_OFFERTA_TECNICA" /></legend>
			
			<%-- CRITERI DI VALUTAZIONE PER OEPV --%>
			<%-- In caso di OEPV "importo offerto", "Aumento" e "Ribasso"      --%>
			<%-- vanno nascosti e vanno visualizzati i criteri di valutazione  --%>			
			<input type="hidden" name="codice" value="<s:property value="%{#helper.codice}"/>"/>
			<c:set var="criteriValutazione" scope="request" value="${sessionScope['offertaTecnica'].listaCriteriValutazione}"/>			
			<c:set var="criteriValutazioneEditabile" scope="request" value="${sessionScope['offertaTecnica'].criterioValutazioneEditabile}" />
			<c:set var="criteriValutazioneValore" scope="request" value="${criterioValutazione}"/>
			
			<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/garetel/inc/criteriValutazioneOEPV.jsp">
				<jsp:param name="tipoBusta" value="${BUSTA_TECNICA}" />
			</jsp:include>
		</fieldset>
		
		
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/skip_main_content_from_buttons.jsp" />

		<div class="azioni">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_next.jsp" />
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_cancel.jsp" />
		</div>
	</form>
</div>


<script type="text/javascript">
  $('#formOffertaTecnica').parsley();
  window.Parsley.setLocale('it');
</script>
