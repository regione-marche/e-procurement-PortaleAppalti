<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="wp" uri="aps-core.tld"%>

<%-- NB: questi commenti non vengono visualizzati nel sorgente di pagina!!!
I parametri gestiti da questa jsp sono:
	- path  	   {downloadDocumentoPubblico, downloadDocumentoRiservato, downloadAllegatoRichiesta, downloadDocumentoInvito}
	- urlDownload  contiene la action di download
	- idprg		   {PG, PA, ...}
	- id           id del documento in docdig
	- url	   	   link ad un documento	
	- codice	   codice gara	 
	- ext
	- dataPubblicazione se valorizzata serve per comprendere se visualizzare la stampa "Pubblicato il GG/MM/AAAA" per un documento
--%>

<s:set var="urlParamIdPrg">&idprg=<s:property value="%{#idprg}"/></s:set>
<s:set var="urlBase">${param.urlDownload}</s:set>
<c:set var="anonimo" value="false"/>
<c:set var="dataPubblicazioneProceduraStringa" scope="request">${param.dataPubblicazione}</c:set>

<c:choose>
	<c:when test="${param.path eq 'downloadAllegatoRichiesta'}" >
		<c:set var="namespace" value="/ExtStr2/do/FrontEnd/Comunicazioni/" /> 
	</c:when>
	<c:when test="${param.path eq 'downloadDocumentoInvito'}" >
		<c:set var="namespace" value="/ExtStr2/do/FrontEnd/DocDig/" />
		<s:url id="urlBase" namespace="/do/FrontEnd/DocDig" action="%{path}" />
		<c:set var="anonimo" value="true"/>
	</c:when>
	<c:otherwise>
		<c:set var="namespace" value="/ExtStr2/do/FrontEnd/DocDig/" />
	</c:otherwise>
</c:choose>

<s:set var="downloadAction">${namespace}${param.path}.action</s:set>
<c:set var="urlDownloadBase"><s:property value="%{urlBase}"/></c:set>

<%-- prepara il BACK LINK della pagina di visualizzazione della firma digitale --%>
<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/backFromPageFirmaDigitale.jsp"/>

<s:if test="%{#elencoDocumentiAllegati.length > 0}">
	<ul class="list">
		<s:iterator value="#elencoDocumentiAllegati" status="stat">
			<s:set var="numeroDocumentiAllegati" value="%{#numeroDocumentiAllegati + 1}" />
			<s:set var="isLargeFile" value="%{'LARGEFILE' == idStampa}" />
			
			<s:if test='%{nomefile != null}'>
				<s:if test='nomefile.toUpperCase().endsWith(".DOC")'>
					<s:set name="cssClass" value="%{'doc'}"/>
				</s:if>
				<s:elseif test='nomefile.toUpperCase().endsWith(".P7M")'>
					<s:set name="cssClass" value="%{'p7m'}"/>
				</s:elseif>
				<s:elseif test='nomefile.toUpperCase().endsWith(".PDF")'>
					<s:set name="cssClass" value="%{'pdf'}"/>
				</s:elseif>
				<s:elseif test='nomefile.toUpperCase().endsWith(".TSD")'>
					<s:set name="cssClass" value="%{'p7m'}"/>
				</s:elseif>
				<s:elseif test='nomefile.toUpperCase().endsWith(".XML")'>
					<s:set name="cssClass" value="%{'rtf'}"/>
				</s:elseif>
				<s:elseif test='nomefile.toUpperCase().endsWith(".RTF")'>
					<s:set name="cssClass" value="%{'rtf'}"/>
				</s:elseif>
				<s:elseif test='nomefile.toUpperCase().endsWith(".XLS")'>
					<s:set name="cssClass" value="%{'xls'}"/>
				</s:elseif>
				<s:elseif test='nomefile.toUpperCase().endsWith(".JPG") 
								|| nomefile.toUpperCase().endsWith(".JPEG") 
								|| nomefile.toUpperCase().endsWith(".GIF") 
								|| nomefile.toUpperCase().endsWith(".BMP") 
								|| nomefile.toUpperCase().endsWith(".PNG")'>
					<s:set name="cssClass" value="%{'img'}"/>
				</s:elseif>
				<s:else>
					<s:set name="cssClass" value="%{'download'}"/>
				</s:else>
			</s:if>
			
			<!-- URL DEL DOCUMENTO (FIRMA DIGITALE)-->
			<li class='<s:if test="%{#stat.first}">first</s:if> <s:if test="%{#stat.last}">last</s:if>'>
				<s:if test='%{(url == null) && (nomefile == null)}'>
					<%-- se il documento e' cifrato visualizza solo una descrizione, in quanto non ha senso scaricarlo (documento allegato, quindi no url, e di cui viene sbiancato nomefile per non renderlo scaricabile) --%>
					<s:property value="descrizione" />
				</s:if>
				<s:else>
					<%-- se il documento NON e' cifrato permetti il download --%>
					<c:set var="urlFileDownload">${urlDownloadBase}?codice=${param.codice}</c:set>
					<c:set var="dataPubblicazioneDocumentoStringa" scope="request"><s:date name="dataPubblicazione" format="dd/MM/yyyy" /></c:set>
					<s:set var="docFirmato" value ='nomefile.toUpperCase().endsWith(".P7M")
					            				    || nomefile.toUpperCase().endsWith(".TSD")' />
					<s:if test='%{isLargeFile}' >
						<a href="${urlFileDownload}&amp;id=${id}<s:property value="%{#urlParamIdPrg}"/>" title="<wp:i18n key="LABEL_DOWNLOAD_FILE" />" class='bkg <s:property value="cssClass"/>' target='_black' >
							<s:property value="descrizione" />
						</a><s:if test='%{dataPubblicazione != null && !#request.dataPubblicazioneDocumentoStringa.equals(#request.dataPubblicazioneProceduraStringa)}'> (<wp:i18n key="LABEL_PUBLISHED_ON" /> <c:out value="${dataPubblicazioneDocumentoStringa}"/>)</s:if>
					</s:if>
					<s:elseif test='%{#docFirmato}' >
						<%-- DOCUMENTO CON FIRMA DIGITALE --%>
						<c:set var="urlFileDownload">
							<%-- gestione del download anonimo di un documento riservato --%>
							<s:set var="downloadAction">${namespace}${param.path}.action</s:set>
							<c:if test='${anonimo}' >
								<s:set var="downloadAction">${namespace}viewInvito.action</s:set>
	 						</c:if>
	 						
							<wp:action path="${downloadAction}"/>&amp;id=${id}<s:property value="%{#urlParamIdPrg}"/>
						</c:set>
						<form action='${urlFileDownload}' method="post">
	    					<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
	   						<input type="hidden" name="codice" value="${param.codice}" />
	   						<c:if test="${not empty param.ext}" >
	   						    <input type="hidden" name="ext" value="${param.ext}" />
	   						</c:if>
	   						<a href="javascript:;" onclick="parentNode.submit();" title="<wp:i18n key="LABEL_INFO_FIRMA_DIGITALE" />" class='bkg <s:property value="cssClass"/>'>
	    						<s:property value="descrizione" />
	    					</a><s:if test='%{dataPubblicazione != null && !#request.dataPubblicazioneDocumentoStringa.equals(#request.dataPubblicazioneProceduraStringa)}'> (<wp:i18n key="LABEL_PUBLISHED_ON" /> <c:out value="${dataPubblicazioneDocumentoStringa}"/>)</s:if>
						</form>
					</s:elseif>
					<s:else>
						<%-- DOCUMENTO SENZA FIRMA --%>
						<s:if test="%{id != null}">
							<a href="${urlFileDownload}&amp;id=${id}<s:property value="%{#urlParamIdPrg}"/>" title="<wp:i18n key="LABEL_DOWNLOAD_FILE" />" class='bkg <s:property value="cssClass"/>' >
								<s:property value="descrizione" />
							</a><s:if test='%{dataPubblicazione != null && !#request.dataPubblicazioneDocumentoStringa.equals(#request.dataPubblicazioneProceduraStringa)}'> (<wp:i18n key="LABEL_PUBLISHED_ON" /> <c:out value="${dataPubblicazioneDocumentoStringa}"/>)</s:if>
						</s:if>
						<s:else>
							<%-- DOCUMENTO CON URL --%>
							<c:set var="urlDiretta" value="${url}"/>
							<c:if test="${!(fn:startsWith(url,'http://') || fn:startsWith(url,'https://')
											|| fn:startsWith(url,'ftp://') || fn:startsWith(url,'ftps://'))}">
								<c:set var="urlDiretta" value="http://${url}"/>
							</c:if>
							<a href="<c:out value='${urlDiretta}' escapeXml="false" />" title="<wp:i18n key="LABEL_DOWNLOAD_FILE" />" class='bkg <s:property value="cssClass"/>' target='_blank'>
								<s:property value="descrizione" />
							</a><s:if test='%{dataPubblicazione != null && !#request.dataPubblicazioneDocumentoStringa.equals(#request.dataPubblicazioneProceduraStringa)}'> (<wp:i18n key="LABEL_PUBLISHED_ON" /> <c:out value="${dataPubblicazioneDocumentoStringa}"/>)</s:if>
						</s:else>
					</s:else>
					
					<%-- LINK APERTURA DGUE --%>
					<s:if test="%{idStampa == 'DGUE'}">
	                    <span style="float: right;">
	                        <a href='<wp:action path="/ExtStr2/do/FrontEnd/DGUE/sendTokenToDgue.action"/>&amp;iddocdig=<s:property value="%{id}" />&amp;codice=${param.codice}' target="_blank">
	                        	[<wp:i18n key="LABEL_APRI_CON_DGUE" />]
	                        </a>
	                    <span>
	                </s:if>
				</s:else>
				
			</li>
		</s:iterator>
	</ul>
</s:if>
