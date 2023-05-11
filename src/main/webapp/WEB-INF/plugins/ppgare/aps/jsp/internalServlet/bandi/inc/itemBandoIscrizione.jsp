<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<es:getAppParam name = "denominazioneStazioneAppaltanteUnica" var = "stazAppUnica" scope = "page"/> 	


<div class="list-item-row">
	<label>${param.entita} <wp:i18n key="LABEL_PER" /> : </label>
	<s:property value="tipoElenco" />
</div>

<s:if test="maps['stazioniAppaltanti'].size() > 1">
	<div class="list-item-row">
		<label><wp:i18n key="LABEL_STAZIONE_APPALTANTE" /> : </label>
		<c:choose>
			<c:when test="${! empty stazAppUnica }">
				<s:set var="stazAppUnicaToStruts">${stazAppUnica}</s:set>
				<s:property value="stazAppUnicaToStruts" />
			</c:when>
			<c:otherwise>
				<s:property value="stazioneAppaltante" />
			</c:otherwise>
		</c:choose>
	</div>
</s:if>	

<div class="list-item-row">
	<label><wp:i18n key="LABEL_TITOLO_BANDO_AVVISO" /> : </label>
	<s:property value="oggetto" />
</div>
	
<div class="list-item-row">
	<label><wp:i18n key="LABEL_DATA_PUBBLICAZIONE_BANDO" /> : </label>
	<s:date name="dataPubblicazione" format="dd/MM/yyyy" />
</div>

<div class="list-item-row">
	<label><wp:i18n key="LABEL_RIFERIMENTO_PROCEDURA" /> : </label>
	<s:property value="codice" />
</div>
			
<div class="list-item-row">
	<label><wp:i18n key="LABEL_STATO_GARA" /> : </label>
	<s:property value="stato" />
</div>