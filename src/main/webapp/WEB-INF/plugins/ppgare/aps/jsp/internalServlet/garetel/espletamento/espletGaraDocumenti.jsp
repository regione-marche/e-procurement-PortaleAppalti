<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<%--
<%@ taglib prefix="wpsa" uri="apsadmin-core.tld" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>
 --%>


<%-- 
I parametri gestiti da questa jsp sono:	 
	- codice	    codice gara/codice lotto
	- ext
	- documenti 	elenco dei documenti
	- riservati		true|false
--%>

<c:set var="action" value="downloadDocumentoPubblico" />
<c:if test="${param.riservati}">
	<c:set var="action" value="downloadDocumentoRiservato" />
</c:if>
<c:set var="namespace" value="/ExtStr2/do/FrontEnd/DocDig/" />
<c:set var="urlBase" value="/ExtStr2/do/FrontEnd/DocDig/${action}.action" />


<ul class="list">
	<s:iterator var="doc" value="#documenti" status="stat">
		<s:set name="isDOC" value='%{#doc.filename.toUpperCase().endsWith(".DOC")}' />
		<s:set name="isP7M" value='%{#doc.filename.toUpperCase().endsWith(".P7M")}' />
		<s:set name="isPDF" value='%{#doc.filename.toUpperCase().endsWith(".PDF")}' />
		<s:set name="isTSD" value='%{#doc.filename.toUpperCase().endsWith(".TSD")}' />
		<s:set name="isXML" value='%{#doc.filename.toUpperCase().endsWith(".XML")}' />
		<s:set name="isRTF" value='%{#doc.filename.toUpperCase().endsWith(".XML")}' />
		<s:set name="isXLS" value='%{#doc.filename.toUpperCase().endsWith(".XML")' />
		<s:set name="isJPG" value='%{#doc.filename.toUpperCase().endsWith(".JPG") || #doc.filename.toUpperCase().endsWith(".JPEG")}' />
		<s:set name="isGIF" value='%{#doc.filename.toUpperCase().endsWith(".GIF")}' />
		<s:set name="isBMP" value='%{#doc.filename.toUpperCase().endsWith(".BMP")}' />
		<s:set name="isPNG" value='%{#doc.filename.toUpperCase().endsWith(".PNG")}' />
	
		<%-- classe per il tipo di download --%>
		<s:set name="cssClass" value=""/>
		<s:if test='isDOC'>
			<s:set name="cssClass" value="%{'doc'}"/> 
		</s:if>
		<s:elseif test='isP7M'>	
			<s:set name="cssClass" value="%{'p7m'}"/>
		</s:elseif>
		<s:elseif test='idPDF'>
			<s:set name="cssClass" value="%{'pdf'}"/>
		</s:elseif>
		<s:elseif test='isTSD'>
			<s:set name="cssClass" value="%{'p7m'}"/>
		</s:elseif>
		<s:elseif test='isXML'>
			<s:set name="cssClass" value="%{'rtf'}"/>
		</s:elseif>
		<s:elseif test='isRTF'>
			<s:set name="cssClass" value="%{'rtf'}"/>
		</s:elseif>
		<s:elseif test='isXLS'>
			<s:set name="cssClass" value="%{'xls'}"/>
		</s:elseif>
		<s:elseif test='isJPG || isGIF || isBMP || isPNG'>
			<s:set name="cssClass" value="%{'img'}"/>
		</s:elseif>
		<s:else>
			<s:set name="cssClass" value="%{'download'}"/>
		</s:else>
		
		<c:set var="firmato" value="${isP7M || isTSD}"/>

		<li class='<s:if test="%{#stat.index == 0}">first</s:if> <s:if test="%{#stat.index == #documenti.size-1}">last</s:if>'>
			<c:choose>
				<c:when test="${firmato}">
					<wp:i18n key="LABEL_INFO_FIRMA_DIGITALE" var="labelInfoFirmaDigitale"/>
					<c:set var="urlFileDownload">
						<wp:action path='${urlBase}'/>&amp;id=<s:property value="#doc.idDocumento"/>&amp;idprg=<s:property value="#doc.idProgramma"/>
					</c:set>
					<form action="${urlFileDownload}" method="post">
	   					<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
	   					<a href="javascript:;" onclick="parentNode.submit();" title='<s:property value="%{#attr.labelInfoFirmaDigitale}"/>' class="bkg <s:property value='cssClass'/>">
	   						<s:property value="descrizione" />
	   					</a>
						<input type="hidden" name="codice" value="${param.codice}" />
						<c:if test="${not empty param.ext}" >
						    <input type="hidden" name="ext" value="${param.ext}" />
						</c:if>
					</form>
				</c:when>
				<c:otherwise>
					<s:if test="%{#doc.idDocumento != null}">
						<wp:i18n key="LABEL_DOWNLOAD_FILE" var="labelDownloadFile"/>
						<s:url id="url" namespace="/do/FrontEnd/DocDig" action="downloadDocumentoPubblico" />
						<c:if test="${param.riservati}">
							<s:url id="url" namespace="/do/FrontEnd/DocDig" action="downloadDocumentoRiservato" />
						</c:if>
						<c:set var="urlFileDownload">
							${url}?id=<s:property value="#doc.idDocumento"/>&amp;idprg=<s:property value="#doc.idProgramma"/>&amp;codice=${param.codice}
						</c:set>
						<a href="${urlFileDownload}" title='<s:property value="%{#attr.labelDownloadFile}"/>' class="bkg <s:property value='cssClass'/>" >
							<s:property value="#doc.descrizione" />
						</a>
					</s:if>
					<%-- "url" NON ESISTE NELL'OGGETTO DEL DOCUMENTO!!!
					<s:else>
						<c:set var="urlDiretta" value="${url}"/>
						<c:if test="${!(fn:startsWith(url,'http://') || fn:startsWith(url,'https://')
										|| fn:startsWith(url,'ftp://') || fn:startsWith(url,'ftps://'))}">
							<c:set var="urlDiretta" value="http://${url}"/>
						</c:if>
						<a href='<c:out value="${urlDiretta}" escapeXml="false" />' title='<s:property value="%{#attr.labelDownloadFile}"/>' class="bkg <s:property value='cssClass'/>">
							<s:property value="#doc.descrizione" />
						</a>
					</s:else>
					 --%>
				</c:otherwise>
			</c:choose>
		</li>
	</s:iterator>
</ul>
