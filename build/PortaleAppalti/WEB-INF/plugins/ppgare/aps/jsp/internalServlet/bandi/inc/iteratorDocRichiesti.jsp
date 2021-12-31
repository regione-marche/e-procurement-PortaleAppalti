<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="wp" uri="aps-core.tld"%>

<s:url id="urlFileDownload" namespace="/do/FrontEnd/DocDig" action="downloadDocumentoPubblico" />					
	<ul class="list">
		<s:iterator value="#elencoDocumentiRichiesti" status="stat">
			<s:set var="numeroDocumentiRichiesti" value="%{#numeroDocumentiRichiesti + 1}" />
			<s:set name="cssClass" value="%{'download'}"/>
			<li class='<s:if test="%{#stat.first}">first</s:if> <s:if test="%{#stat.last}">last</s:if>'>
			
				<s:if test="%{idfacsimile != null}">
					<s:set var="docFirmato" 
							value ='nomefilefacsimile.toUpperCase().endsWith(".P7M") || 
							        nomefilefacsimile.toUpperCase().endsWith(".TSD")' />
					
					<s:if test='#docFirmato' >
						<%-- DOCUMENTO CON FIRMA DIGITALE --%>
						<c:set var="urlFileApriFileFirmato"><wp:action path="/ExtStr2/do/FrontEnd/DocDig/downloadDocumentoPubblico.action"/>&amp;id=${idfacsimile}</c:set>
						<form action='${urlFileApriFileFirmato}' method="post">
	    					<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
	    					<a href="javascript:;" onclick="parentNode.submit();" title="<wp:i18n key="LABEL_INFO_FIRMA_DIGITALE" />" class='bkg <s:property value="cssClass"/>'>
	    						<s:property value="nome" />
	    					</a>
	   						<input type="hidden" name="codice" value="${param.codice}" />
	   						<input type="hidden" name="ext" value="${param.ext}" />
						</form>
					</s:if>
					
					<s:else>
						<%-- DOCUMENTO SENZA FIRMA --%>
						<a href="${urlFileDownload}?id=${idfacsimile}&amp;${tokenHrefParams}" title="<wp:i18n key="LABEL_DOWNLOAD_FILE" />" class='bkg <s:property value="cssClass"/>'>
							<s:property value="nome" />
						</a>
					</s:else>
				</s:if>
				<s:else>
					<s:property value="nome" />
				</s:else>
			</li>
		</s:iterator>
	</ul>
