<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wp" uri="aps-core.tld"%>

<s:if test="%{#elencoComunicazioniAmministrazione.length > 0}">
	<ul class="list-comunicazioni">
		<s:iterator value="#elencoComunicazioniAmministrazione" var="comunicazione" status="statCom">
			<s:set var="numeroComunicazioniAmministrazione" value="%{#numeroComunicazioniAmministrazione + 1}" />
			<li class='<s:if test="%{#statCom.first}">first</s:if> <s:if test="%{#statCom.last}">last</s:if>'>
				<div class="title"><wp:i18n key="LABEL_PUBLISHED_ON" /> <s:date name="dataInvio" format="dd/MM/yyyy" /></div>
				<div class="description"><s:property value="oggetto" escape="false" /><c:if test="${! empty testo}"> - <s:property value="testo" escape="false" /></c:if></div>
				<s:set var="elencoDocumentiAllegati" value="%{#comunicazione.documento}"/>
				<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/bandi/inc/iteratorDocAllegati.jsp">
					<jsp:param name="path" value="downloadDocumentoPubblico"/>
				</jsp:include>
			</li>
		</s:iterator>
	</ul>
</s:if>