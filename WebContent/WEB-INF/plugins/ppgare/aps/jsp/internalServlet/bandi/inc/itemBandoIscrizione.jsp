<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<div class="list-item-row">
	<label>${param.entita} <wp:i18n key="LABEL_PER" /> : </label>
	<s:property value="tipoElenco" />
</div>

<s:if test="maps['stazioniAppaltanti'].size() > 1">
	<div class="list-item-row">
		<label><wp:i18n key="LABEL_STAZIONE_APPALTANTE" /> : </label>
		<s:property value="stazioneAppaltante" />
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