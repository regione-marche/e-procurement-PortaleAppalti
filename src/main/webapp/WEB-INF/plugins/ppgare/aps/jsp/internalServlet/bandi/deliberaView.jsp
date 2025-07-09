<%@ taglib prefix="wp" uri="aps-core.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<es:getAppParam name="denominazioneStazioneAppaltanteUnica" var="stazAppUnica" scope = "page"/>


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp">
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">
	<h2><wp:i18n key="TITLE_PAGE_DETTAGLIO_DELIBERA" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_DETTAGLIO_DELIBERA" />
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />

	<s:if test="%{dettaglioGara.datiGeneraliGara.dataUltimoAggiornamento != null}">
		<div class="align-right important last-update-detail">
			<wp:i18n key="LABEL_LAST_UPDATE" />	<s:date name="dettaglioGara.datiGeneraliGara.dataUltimoAggiornamento" format="dd/MM/yyyy" />
		</div>
	</s:if>

	<div class="detail-section first-detail-section">
		<h3 class="detail-section-title">
			<span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_STAZIONE_APPALTANTE" />
		</h3>

		<div class="detail-row">
			<label><wp:i18n key="LABEL_DENOMINAZIONE" /> : </label>
			<c:choose>
				<c:when test="${! empty stazAppUnica }">
					<s:set var="stazAppUnicaToStruts">${stazAppUnica}</s:set>
					<s:property value="stazAppUnicaToStruts" />
				</c:when>
				<c:otherwise>
					<s:property value="delibera.stazioneAppaltante" />
				</c:otherwise>
			</c:choose>
		</div>

		<div class="detail-row">
			<label><wp:i18n key="LABEL_TITOLO" /> : </label>
			<s:property value="delibera.oggetto" />
		</div>

		<c:if test="${! empty cig}">
			<div class="detail-row">
				<label><wp:i18n key="LABEL_CIG" /> : </label>
				<s:property value="delibera.cig" />
			</div>
		</c:if>
		
		<div class="detail-row">
			<label><wp:i18n key="LABEL_RIFERIMENTO_PROCEDURA" /> : </label>
			<s:property value="delibera.codice" />
		</div>
	</div>

	<!-- DOCUMENTAZIONE DELIBERA -->
	<s:if test="%{delibera.documenti != null && delibera.documenti.length > 0}">
<%--	
		<div class="list">
			<h4 class="detail-section-title"><span class="noscreen"><wp:i18n key="LABEL_SUBSECTION" /> </span><wp:i18n key="LABEL_DETTAGLIO_GARA_DOCUMENTAZIONE_GARA" /></h4>
			<div class="table-container">
				<table class="info-table">
					<thead>
						<tr>
							<th scope="col"><wp:i18n key="LABEL_DATA_PUBBLICAZIONE_BANDO" /></th>
							<th scope="col"><wp:i18n key="LABEL_DATA_ATTO" /></th>
							<th scope="col"><wp:i18n key="LABEL_NUMERO_ATTO" /></th>
							<th scope="col"><wp:i18n key="LABEL_DESCRIZIONE_DOCUMENTO" /></th>
						</tr>
					</thead>
					<tbody>
						<s:iterator value="delibera.documenti" status="stat">
							<tr>
								<td>
									<s:date name="dataPubblicazione" format="dd/MM/yyyy" />
								</td>
								<td>
									<s:date name="dataAtto" format="dd/MM/yyyy" />
								</td>
								<td>
									<s:property value="numeroAtto" />
								</td>
								<td>
									<!-- DOWNLOAD PUBBLICO DEL DOCUMENTO (NB: da trasformae in jsp parametrica?) -->
									<s:if test="%{fileDoc != null}" >
										<!-- documento in W_DOCDIG -->
										<s:if test="%{idDoc != null}">
											<s:set var="docFirmato" value ='fileDoc.toUpperCase().endsWith(".P7M") || 
													        				fileDoc.toUpperCase().endsWith(".TSD")' />
											<s:if test='#docFirmato' >
												<!-- DOCUMENTO CON FIRMA DIGITALE -->
					                            <c:set var="urlFileApriFileFirmato"><wp:action path="/ExtStr2/do/FrontEnd/DocDig/downloadDocumentoPubblico.action"/>&amp;id=<s:property value='%{idDoc}'/></c:set>
					                            <jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
					   								<a href="${urlFileApriFileFirmato}"  title="<wp:i18n key="LABEL_INFO_FIRMA_DIGITALE" />" class='bkg download'>
					   		    						<s:property value='%{descrizioneDoc}'/>
					   		    					</a>
					   						</s:if>
					   						<s:else>
					                        <!-- DOCUMENTO SENZA FIRMA -->
					                            <s:url id="urlFileDownload" namespace="/do/FrontEnd/DocDig" action="downloadDocumentoPubblico" />
					                                <a href="${urlFileDownload}?id=<s:property value='%{idDoc}'/>" title="<wp:i18n key="LABEL_DOWNLOAD_FILE" />" class='bkg download'>
					                                    <s:property value='%{descrizioneDoc}'/>
					                                </a>
					                        </s:else>
					                    </s:if>
					                </s:if>
					                <s:else>
										<!-- documento ad un dato URL -->
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
								</td>
							</tr>
						</s:iterator>
					</tbody>
				</table>
			</div>
		</div>
 --%>
 		<div class="detail-section">
			<h3 class="detail-section-title">
				<span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_DETTAGLIO_GARA_DOCUMENTAZIONE_GARA" />
			</h3>
			<div class="detail-row">
				<div class="detail-subrow">	
					<s:iterator value="delibera.documenti" status="stat">
						<span class="important">
							<!-- <label><wp:i18n key="LABEL_DESCRIZIONE_DOCUMENTO" /> : </label> -->
							<!-- DOWNLOAD PUBBLICO DEL DOCUMENTO  -->
							<s:if test="%{fileDoc != null}" >
								<!-- documento in W_DOCDIG -->
								<s:if test="%{idDoc != null}">
									<s:set var="docFirmato" value ='fileDoc.toUpperCase().endsWith(".P7M") || 
											        				fileDoc.toUpperCase().endsWith(".TSD")' />
									<s:if test='#docFirmato' >
										<!-- DOCUMENTO CON FIRMA DIGITALE -->
			                            <c:set var="urlFileApriFileFirmato"><wp:action path="/ExtStr2/do/FrontEnd/DocDig/downloadDocumentoPubblico.action"/>&amp;id=<s:property value='%{idDoc}'/></c:set>
			                            <jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			   								<a href="${urlFileApriFileFirmato}"  title="<wp:i18n key="LABEL_INFO_FIRMA_DIGITALE" />" class='bkg download'>
			   		    						<s:property value='%{descrizioneDoc}'/>
			   		    					</a>
			   						</s:if>
			   						<s:else>
			                        <!-- DOCUMENTO SENZA FIRMA -->
			                            <s:url id="urlFileDownload" namespace="/do/FrontEnd/DocDig" action="downloadDocumentoPubblico" />
			                                <a href="${urlFileDownload}?id=<s:property value='%{idDoc}'/>" title="<wp:i18n key="LABEL_DOWNLOAD_FILE" />" class='bkg download'>
			                                    <s:property value='%{descrizioneDoc}'/>
			                                </a>
			                        </s:else>
			                    </s:if>
			                </s:if>
			                <s:else>
								<!-- documento ad un dato URL -->
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
						</span>
						<ul class="list">
							<li>
								<label><wp:i18n key="LABEL_DATA_PUBBLICAZIONE_BANDO" /> : </label>
								<s:date name="dataPubblicazione" format="dd/MM/yyyy" />
							</li>
							<li>
								<label><wp:i18n key="LABEL_DATA_ATTO" /> : </label>
								<s:date name="dataAtto" format="dd/MM/yyyy" />
							</li>
							<li>
								<label><wp:i18n key="LABEL_NUMERO_ATTO" /> : </label>
								<s:property value="numeroAtto" />
							</li>
						</ul>
					</s:iterator>
				</div>
			</div>
		</div>
	</s:if>

	<div class="azioni">
		<!-- azioni  -->
	</div>
	 
 	<div class="back-link">
		<a href="<wp:action path="/ExtStr2/do/FrontEnd/Bandi/listDelibere.action" />">
			<wp:i18n key="LINK_BACK_TO_LIST" />
		</a>
	</div>
</div>