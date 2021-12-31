<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="wpsa" uri="apsadmin-core.tld" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>

<s:set name="currentResource" value="#attribute.resources[#lang.code]"></s:set>
<s:set name="defaultResource" value="#attribute.resource"></s:set>

<span class="noscreen"><s:text name="note.imageContent" /></span>

<s:if test="#lang.default">
<%-- Lingua di DEFAULT --%>
	<s:if test="#currentResource != null">
		<%-- Lingua di default - Risorsa VALORIZZATA --%>
		<%-- IMMAGINE E LINK + TESTO + PULSANTE RIMUOVI --%>
		<%-- IMMAGINE E LINK --%>
		<a href="<s:property value="#defaultResource.getImagePath('0')" />"><img src="<s:property value="#defaultResource.getImagePath('1')"/>" 
			alt="<s:property value="#defaultResource.descr"/>" /></a>
		
		<%-- CAMPO DI TESTO --%>
		<s:include value="/WEB-INF/apsadmin/jsp/entity/modules/textAttribute.jsp" />
		
		<s:if test="!(#attributeTracer.monoListElement) || ((#attributeTracer.monoListElement) && (#attributeTracer.compositeElement))">
			<%-- PULSANTE DI RIMOZIONE RISORSA --%>
			<s:include value="/WEB-INF/plugins/jacms/apsadmin/jsp/content/modules/include/removeResourceSubmit.jsp">
				<s:param name="resourceTypeCode">Image</s:param>
				<s:param name="iconImagePath"><wp:resourceURL/>administration/img/icons/delete.png</s:param>
			</s:include>
		</s:if>
		
	</s:if>
	<s:else>
		<%-- Lingua di default - Risorsa NON VALORIZZATA --%>
		
		<%-- PULSANTE DI RICERCA RISORSA --%>
		<s:include value="/WEB-INF/plugins/jacms/apsadmin/jsp/content/modules/include/chooseResourceSubmit.jsp">
			<s:param name="resourceTypeCode">Image</s:param>
			<s:param name="iconImagePath"><wp:resourceURL/>administration/img/icons/image.png</s:param>
		</s:include>
		
	</s:else>
</s:if>
<s:else>
<%-- Lingua NON di DEFAULT --%>
	<s:if test="#defaultResource == null">
		<%-- Risorsa lingua di DEFAULT NON VALORIZZATA --%>
		<s:text name="note.editContent.doThisInTheDefaultLanguage" />.
	</s:if>
	<s:else>
		<%-- Risorsa lingua di DEFAULT VALORIZZATA --%>
		<s:if test="#currentResource == null">
			<%-- Risorsa lingua corrente NON VALORIZZATA --%>
			<%-- IMMAGINE DI DEFAULT + PULSANTE SCEGLI RISORSA --%> 
			
			<%-- IMMAGINE DI DEFAULT --%> 
			<a href="<s:property value="#defaultResource.getImagePath('0')" />"><img src="<s:property value="#defaultResource.getImagePath('1')"/>" 
				alt="<s:property value="#defaultResource.descr"/>" /></a>
			
			<%-- PULSANTE DI RICERCA RISORSA --%>
			<s:include value="/WEB-INF/plugins/jacms/apsadmin/jsp/content/modules/include/chooseResourceSubmit.jsp">
				<s:param name="resourceTypeCode">Image</s:param>
				<s:param name="iconImagePath"><wp:resourceURL/>administration/img/icons/image.png</s:param>
			</s:include>
			
		</s:if>
		<s:else>
			<%-- IMMAGINE LINGUA CORRENTE CON LINK + PULSANTE RIMUOVI --%>
			
			<%-- IMMAGINE LINGUA CORRENTE CON LINK  --%> 
			<a href="<s:property value="#currentResource.getImagePath('0')" />"><img src="<s:property value="#currentResource.getImagePath('1')"/>" 
				alt="<s:property value="#currentResource.descr"/>" /></a>
			
			<%-- PULSANTE DI RICERCA RISORSA --%>
			<s:include value="/WEB-INF/plugins/jacms/apsadmin/jsp/content/modules/include/chooseResourceSubmit.jsp">
				<s:param name="resourceTypeCode">Image</s:param>
				<s:param name="iconImagePath"><wp:resourceURL/>administration/img/icons/image.png</s:param>
			</s:include>
			
			<%-- PULSANTE DI RIMOZIONE RISORSA --%>
			<s:include value="/WEB-INF/plugins/jacms/apsadmin/jsp/content/modules/include/removeResourceSubmit.jsp">
				<s:param name="resourceTypeCode">Image</s:param>
				<s:param name="iconImagePath"><wp:resourceURL/>administration/img/icons/clear.png</s:param>
			</s:include>
			
		</s:else>
		<%-- CAMPO DI TESTO --%>
		<%-- CAMPO DI TESTO - MODULARIZZARE --%>
		<s:include value="/WEB-INF/apsadmin/jsp/entity/modules/textAttribute.jsp" />
	</s:else>
</s:else>
