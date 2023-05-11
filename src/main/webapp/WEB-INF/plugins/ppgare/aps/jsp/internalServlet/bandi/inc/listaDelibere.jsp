<%@ taglib prefix="wp"   uri="aps-core.tld" %>
<%@ taglib prefix="wpsa" uri="apsadmin-core.tld" %>
<%@ taglib prefix="c"    uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"    uri="/struts-tags" %>
<%@ taglib prefix="es"   uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%-- prepara il BACK LINK della pagina di visualizzazione della firma digitale --%>
<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/backFromPageFirmaDigitale.jsp"/>

<es:getAppParam name = "denominazioneStazioneAppaltanteUnica" var = "stazAppUnica" scope = "page"/>
<es:checkCustomization var="visExport" objectId="DELIBERECONTRARRE" attribute="EXPORT" feature="VIS" />

<s:if test="%{listaDelibere.dati.size > 0}">
	<div class="list-summary">
		<wp:i18n key="SEARCH_RESULTS_INTRO" />
		<s:property value="model.iTotalDisplayRecords" />
		<wp:i18n key="SEARCH_RESULTS_OUTRO" />.
	</div>
		
	<s:iterator var="riga" value="listaDelibere.dati" status="status">
		<div class="list-item">
			<div class="list-item-row">
				<label><wp:i18n key="LABEL_STAZIONE_APPALTANTE" /> : </label> 
				<c:choose>
					<c:when test="${! empty stazAppUnica }">
						<s:set var = "stazAppUnicaToStruts">${stazAppUnica}</s:set>
						<s:property value = "stazAppUnicaToStruts" />
					</c:when>
					<c:otherwise>
						<s:property value="stazioneAppaltante" />
					</c:otherwise>
				</c:choose>			
			</div>
				
			<div class="list-item-row">
				<label><wp:i18n key="LABEL_TITOLO" /> : </label>
				<s:property value="oggetto" />
			</div>
			
			<c:if test="${! empty cig}">
				<div class="list-item-row">
					<label><wp:i18n key="LABEL_CIG" /> : </label>
					<s:property value="cig" />
				</div>
			</c:if>
			
			<div class="list-item-row">
				<label><wp:i18n key="LABEL_RIFERIMENTO_PROCEDURA" /> :</label>
				<s:property value="codice" />
			</div>
			
			<div class="list-item-row">
				<label><wp:i18n key="LABEL_DATA_PUBBLICAZIONE_BANDO" /> : </label>
				<s:date name="dataPubblicazione" format="dd/MM/yyyy" />
			</div>
			
			<div class="list-item-row">
				<label><wp:i18n key="LABEL_DATA_ATTO" /> : </label>
				<s:date name="dataAtto" format="dd/MM/yyyy" />
			</div>
			
			<div class="list-item-row">
				<label><wp:i18n key="LABEL_NUMERO_ATTO" /> : </label>
				<s:property value="numeroAtto" />
			</div>
			
			<div class="list-item-row">
				<label><wp:i18n key="LABEL_DESCRIZIONE_DOCUMENTO" /> : </label>
				
				<%-- DOWNLOAD PUBBLICO DEL DOCUMENTO (NB: da trasformae in jsp parametrica?) --%>
				<s:if test="%{fileDoc != null}" >
					<%-- documento in W_DOCDIG --%>
					<s:if test="%{idDoc != null}">
						<s:set var="docFirmato" value ='fileDoc.toUpperCase().endsWith(".P7M") || 
								        				fileDoc.toUpperCase().endsWith(".TSD")' />
						<s:if test='#docFirmato' >
							<%-- DOCUMENTO CON FIRMA DIGITALE --%>
                            <c:set var="urlFileApriFileFirmato"><wp:action path="/ExtStr2/do/FrontEnd/DocDig/downloadDocumentoPubblico.action"/>&amp;id=<s:property value='%{idDoc}'/></c:set>
                            <jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
   								<a href="${urlFileApriFileFirmato}"  title="<wp:i18n key="LABEL_INFO_FIRMA_DIGITALE" />" class='bkg download'>
   		    						<s:property value='%{descrizioneDoc}'/>
   		    					</a>
   						</s:if>
   						<s:else>
                        <%-- DOCUMENTO SENZA FIRMA --%>
                            <s:url id="urlFileDownload" namespace="/do/FrontEnd/DocDig" action="downloadDocumentoPubblico" />
                                <a href="${urlFileDownload}?id=<s:property value='%{idDoc}'/>&amp;${tokenHrefParams}" title="<wp:i18n key="LABEL_DOWNLOAD_FILE" />" class='bkg download'>
                                    <s:property value='%{descrizioneDoc}'/>
                                </a>
                        </s:else>
                    </s:if>
                </s:if>
                <s:else>
					<%-- documento ad un dato URL --%>
					<s:if test="%{urlDoc != null}" >
						<c:set var="urlDiretta"><s:property value="%{urlDoc}"/></c:set>
						<c:if test="${!(fn:startsWith(urlDiretta, 'http://') || fn:startsWith(urlDiretta, 'https://'))}">
							<c:set var="urlDiretta" value="http://${urlDiretta}"/>
						</c:if>
						<a href='${urlDiretta}' title='<wp:i18n key="LABEL_DOWNLOAD_FILE" />' class='bkg download'>
					   		<s:property value="descrizioneDoc" />
						</a>
					</s:if>
				</s:else>
			</div>
			
		</div>
	</s:iterator>

		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/pagination.jsp"></jsp:include>

</s:if>
				<s:url id="urlExport" namespace="/do/FrontEnd/Bandi" action="exportDelibere">
					<s:param name="last" value="1"></s:param>
				</s:url>

            <c:if test="${visExport}">

				<p>
					<a href='<s:property value="%{#urlExport}" />&amp;${tokenHrefParams}' class="important"><wp:i18n key="LINK_EXPORT_CSV" /></a>
				</p>
			</c:if>

<s:else>
	<div class="list-summary">
		<wp:i18n key="SEARCH_RESULTS_INTRO" />
		<s:property value="model.iTotalDisplayRecords" />
		<wp:i18n key="SEARCH_RESULTS_OUTRO" />.
	</div>
</s:else>
