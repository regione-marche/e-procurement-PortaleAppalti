<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
 
<s:if test="%{#elencoCategorie.length > 0}">
	<div class="list">
		<h4 class="detail-section-title"><span class="noscreen"><wp:i18n key="LABEL_SUBSECTION" /> </span><wp:i18n key="LABEL_CATEGORIE_PRESTAZIONI" /></h4>
	
		<s:iterator var="categoria" value="#elencoCategorie" status="stat">

			<div class="detail-row">
				<div class="detail-subrow">
					<label><wp:i18n key="LABEL_CATEGORIA_PRESTAZIONE" /> : </label>
					<s:property value="codice" /> - <s:property value="descrizione" />
				</div>
				
				<c:if test="${! empty classe}">
					<div class="detail-subrow">
						<label><wp:i18n key="LABEL_CLASSIFICA_CAPITALIZE" /> : </label>
						<s:property value="classe" />
					</div>
				</c:if>
				
				<s:if test="prevalente">
					<div class="detail-subrow">
						<label><wp:i18n key="LABEL_CATEGORIA_PREVALENTE" />? : </label>
						<s:if test="prevalente"><wp:i18n key="LABEL_YES" /></s:if><s:else><wp:i18n key="LABEL_NO" /></s:else>
					</div>
				</s:if>
			</div>
			
		</s:iterator>
	</div>
</s:if>
<s:else>
	<wp:i18n key="LABEL_NESSUNA_CATEGORIA_PRESTAZIONE" />
</s:else>