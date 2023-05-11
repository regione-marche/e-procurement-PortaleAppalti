<%@ taglib prefix="s" uri="/struts-tags" %>

<s:if test="%{#elencoRequisitiRichiesti.length > 0}">
	<ul class="list">
		<s:iterator value="#elencoRequisitiRichiesti" status="stat">
			<s:set var="numeroRequisitiRichiesti" value="%{#numeroRequisitiRichiesti + 1}" />
			<li class='<s:if test="%{#stat.first}">first</s:if> <s:if test="%{#stat.last}">last</s:if>'>
				<span class="bkg check"><s:property value="nome" /></span>
			</li>
		</s:iterator>
	</ul>
</s:if>