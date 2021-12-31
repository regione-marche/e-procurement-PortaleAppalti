<%@ taglib prefix="wp"  uri="aps-core.tld" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="s"   uri="/struts-tags" %>

<s:set name="comprensivoNonSoggettiARibasso" value="%{#session['offertaEconomica'].comprensivoNonSoggettiARibasso}"/>
<s:set name="comprensivoOneriSicurezza" value="%{#session['offertaEconomica'].comprensivoOneriSicurezza}"/>
<s:set name="nettoOneriSicurezza" value="%{#session['offertaEconomica'].nettoOneriSicurezza}"/>
<s:set name="comprensivoOneriProgettazione" value="%{#session['offertaEconomica'].comprensivoOneriProgettazione}"/>
<s:set name="nonSuperioreAImportoBaseAsta" value="%{#session['offertaEconomica'].nonSuperioreAImportoBaseAsta}"/>
<s:set name="valoreMassimoImportoOfferto" value="%{#session['offertaEconomica'].valoreMassimoImportoOfferto}"/> 

<s:if test="%{#comprensivoNonSoggettiARibasso || #comprensivoOneriSicurezza || #nettoOneriSicurezza || #comprensivoOneriProgettazione || !#nonSuperioreAImportoBaseAsta }">
	<div class="note">
		<wp:i18n key="LABEL_L_IMPORTO_OFFERTO" /> :
		<ul>
			<s:if test="%{#comprensivoNonSoggettiARibasso}"><li><wp:i18n key="LABEL_IMPORTO_CON_SOGGETTI_A_RIBASSO" /></li></s:if>
			<s:if test="%{#comprensivoOneriSicurezza}"><li><wp:i18n key="LABEL_IMPORTO_CON_ONERI_SICUREZZA_NON_SOGGETTI" /></li></s:if>
			<s:if test="%{#nettoOneriSicurezza}"><li><wp:i18n key="LABEL_IMPORTO_NETTO_ONERI_SICUREZZA" /></li></s:if>
			<s:if test="%{#comprensivoOneriProgettazione}"><li><wp:i18n key="LABEL_IMPORTO_CON_ONERI_PROGETTAZIONE" /></li></s:if>
			<s:if test="%{!#nonSuperioreAImportoBaseAsta}"><li><wp:i18n key="LABEL_IMPORTO_NON_SUPERIORE_A" /> <s:text name="format.money5dec"><s:param value="%{#valoreMassimoImportoOfferto}"/></s:text> &euro;</li></s:if>
		</ul>
	</div>
</s:if>	