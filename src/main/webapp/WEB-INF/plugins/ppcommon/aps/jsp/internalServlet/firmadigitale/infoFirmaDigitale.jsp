<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="x"  uri="http://java.sun.com/jsp/jstl/xml" %>

<script type="text/javascript" src='<wp:resourceURL/>static/js/jquery.treeview.js'></script>

<script type="text/javascript">	
<!--//--><![CDATA[//><!--
$(document).ready(function() {
		
	$("#firmetree").treeview({
		collapsed: true,
		animated: "slow",
		control: "#firmetreecontrol"
	});

	$("#marchetemporalitree").treeview({
		collapsed: true,
		animated: "slow",
		control: "#marchetemporalitreecontrol"
	});

	// espandi solo i nodi "Livello NNN"
	$('[id^="livello_"]').each(function() {
		var firstInner = $(this).children(":first");
		if (firstInner.is('div')) {
			firstInner.click();
		}
	});

});	
//--><!]]></script>

<wp:headInfo type="CSS" info="jquery/treeview/jquery.treeview.css" />

<!-- prepare il path per i pulsanti "Chiudi tutto" "Espandi tutto" -->
<c:set var="imgPath"><wp:resourceURL/>static/css/jquery/treeview/images/</c:set> 


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">
	
	<h2><wp:i18n key="TITLE_PAGE_FIRMA_DIGITALE" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_INFO_FIRMA_DIGITALE" />
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/warning_javascript.jsp" />

	<div class="detail-section first-detail-section">
		<h3 class="detail-section-title">
			<span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_FIRMA_DIGITALE_DOWNLOAD_DOCUMENTI" />
		</h3>
		<div class="detail-row">
			<ul class='list'>
				<s:iterator var="item" value="downloadAllegati" status="stat">
				
					<!-- imposta la classe del tipo documento -->
					<s:if test='#item.toUpperCase().endsWith(".DOC")'>
						<s:set name="cssClass" value="%{'doc'}"/>
					</s:if>
					<s:elseif test='#item.toUpperCase().endsWith(".P7M")'>
						<s:set name="cssClass" value="%{'p7m'}"/>
					</s:elseif>
					<s:elseif test='#item.toUpperCase().endsWith(".PDF")'>
						<s:set name="cssClass" value="%{'pdf'}"/>
					</s:elseif>
					<s:elseif test='#item.toUpperCase().endsWith(".TSD")'>
						<s:set name="cssClass" value="%{'p7m'}"/>
					</s:elseif>
					<s:elseif test='#item.toUpperCase().endsWith(".XML")'>
						<s:set name="cssClass" value="%{'rtf'}"/>
					</s:elseif>
					<s:elseif test='#item.toUpperCase().endsWith(".RTF")'>
						<s:set name="cssClass" value="%{'rtf'}"/>
					</s:elseif>
					<s:elseif test='#item.toUpperCase().endsWith(".XLS")'>
						<s:set name="cssClass" value="%{'xls'}"/>
					</s:elseif>
					<s:elseif test='#item.toUpperCase().endsWith(".JPG") 
									|| #item.toUpperCase().endsWith(".JPEG") 
									|| #item.toUpperCase().endsWith(".GIF") 
									|| #item.toUpperCase().endsWith(".BMP") 
									|| #item.toUpperCase().endsWith(".PNG")'>
						<s:set name="cssClass" value="%{'img'}"/>
					</s:elseif>
					<s:else>
						<s:set name="cssClass" value="%{'download'}"/>
					</s:else>
				
					<%-- imposta il link di download --%>
					<c:choose>
						<c:when test="${riservato == DOWNLOAD_INVITO}">
							<s:url id="urldoc" namespace="/do/FrontEnd/FirmaDigitale" action="downloadDocumentoInvito" />
						</c:when>
						<c:when test="${riservato == DOWNLOAD_FILE}">
							<s:url id="urldoc" namespace="/do/FrontEnd/FirmaDigitale" action="downloadDocumentoFile" />											
						</c:when>
						<c:when test="${riservato == DOWNLOAD_RISERVATO}">
							<s:url id="urldoc" namespace="/do/FrontEnd/FirmaDigitale" action="downloadDocumentoRiservato" />
						</c:when>
						<c:otherwise>
							<s:url id="urldoc" namespace="/do/FrontEnd/FirmaDigitale" action="downloadDocumentoPubblico" />
						</c:otherwise>
					</c:choose>
					
					<li <s:if test="%{#stat.index == 0}">class='first'</s:if> >  <!-- class='last'> -->
						<c:set var="url">${urldoc}?id=${id}&amp;idprg=${idprg}&amp;pos=<s:property value='%{#stat.index}'/></c:set>
						<c:if test="${riservato == DOWNLOAD_FILE}">
							<c:set var="url">${url}&amp;isfile=${isfile}</c:set>
						</c:if>
						
						<a href="${url}"
							<s:if test='%{#stat.index == 0}'>title="<wp:i18n key="LABEL_FIRMA_DIGITALE_DOWNLOAD_DOCUMENTO" />"</s:if>
							<s:else>title="<wp:i18n key="LABEL_FIRMA_DIGITALE_DOWNLOAD_CONTENUTO" />"</s:else> 
							class='bkg <s:property value="cssClass"/>' >
							<s:property value="#item"/>
						</a>
					</li>
				</s:iterator>
			</ul>
		</div>
	</div>


	<c:if test="${! empty verificaFirmaDigitaleXML}">
		<c:choose>
			<c:when test="${state eq 'NO-DATA-FOUND'}">
				<strong><wp:i18n key="LABEL_FIRMA_DIGITALE_DOCUMENTO_NON_TROVATO" /></strong>
			</c:when>
			<c:when test="${state eq 'DATE-PARSE-EXCEPTION'}">
				<strong><wp:i18n key="LABEL_FIRMA_DIGITALE_FORMATO_DATA" /></strong>
			</c:when>
			<c:when test="${state eq 'ERROR'}">
				<strong>${message}</strong>
			</c:when>
			<c:otherwise>
				
				<!-- FIRMA DIGITALE -->			
				<x:parse var="xverificaFirmaDigitale" xml="${verificaFirmaDigitaleXML}" />
				<x:if select='count($xverificaFirmaDigitale) > 0'>
				
					<c:if test="${!empty ckdateformat}">
						<div class="detail-section">
							<h3 class="detail-section-title">
								<span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_FIRMA_DIGITALE_ATTENDIBILITA_CERTIFICATI" />
							</h3>
					
							<div class="detail-row">
								<label><wp:i18n key="LABEL_FIRMA_DIGITALE_LISTA_UNTRUSTED" /> ${ckdateformat} : </label>
								<x:choose>
									<x:when select="count($xverificaFirmaDigitale//Certificate[CheckAtDate = 'false']) > 0">
										<x:forEach var="Certificate" select="$xverificaFirmaDigitale//Certificate" varStatus="certificatestatus">
											<x:if select="$Certificate/CheckAtDate = 'false'">
												<span>
													#<x:out select="$Certificate/@Id"/>
													<x:if select="$Certificate/SubjectDN/SURNAME != ''">
														<x:out select="$Certificate/SubjectDN/SURNAME"/>
													</x:if>
													<x:if select="$Certificate/SubjectDN/GIVENNAME != ''">
														<x:out select="$Certificate/SubjectDN/GIVENNAME"/>
													</x:if>
												</span>
												<br>					
											</x:if>
										</x:forEach>
									</x:when>
									<x:otherwise>
										<wp:i18n key="LABEL_FIRMA_DIGITALE_TUTTI_TRUSTED" />
									</x:otherwise>
								</x:choose>
							</div>				
						</div>				
					</c:if>
									
	 				<div class="tree-content">		
						<h3 class="detail-section-title">
							<span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_FIRMA_DIGITALE_LISTA_FIRME" />
						</h3>
						
						<span id="firmetreecontrol" style="float: right;">
							<a title="<wp:i18n key="COLLAPSE_ALL" />" href="#"><img src="${imgPath}minus.gif" alt="<wp:i18n key="COLLAPSE_ALL" />" /> <wp:i18n key="COLLAPSE_ALL" /></a>&nbsp;&nbsp;
							<a title="<wp:i18n key="EXPAND_ALL" />" href="#"><img src="${imgPath}plus.gif" alt="<wp:i18n key="EXPAND_ALL" />" /> <wp:i18n key="EXPAND_ALL" /></a>
						</span>				
						
						<ul class="maintree" id="firmetree" >
							<span>
								<c:set var="livelloOld" value="0"/>
								<c:set var="livelli" value="0"/>
							</span>
							
							<x:forEach var="Certificate" select="$xverificaFirmaDigitale//Certificate" varStatus="certificatestatus">
								
								<x:set var="livelloNew" select="string($Certificate/Level)"/>
								<c:if test="${livelloOld != livelloNew}">
									<li id="livello_${livelli}">
									<c:if test="${not fn:contains(livelloNew, '.')}">
										<span>
											<b><wp:i18n key="LABEL_FIRMA_DIGITALE_LIVELLO" /> <x:out select = "$Certificate/Level" /></b>
										</span>
									</c:if>
									<x:set var="livelloOld" select="string($Certificate/Level)"/>
									<c:set var="livelli" value="${livelli + 1}"/>									
									<ul>
								</c:if>
								
								<!-- <li class="closed">  -->
								<div class="detail-row">																														   
									<li class='<s:if test="#certificatestatus.first">first</s:if> <s:if test="#certificatestatus.last">last</s:if>' >
										<x:if select="$Certificate/SubjectDN/SURNAME != ''">
											<x:out select="$Certificate/SubjectDN/SURNAME"/>
										</x:if>
										<x:if select="$Certificate/SubjectDN/GIVENNAME != ''">
											<x:out select="$Certificate/SubjectDN/GIVENNAME"/>
										</x:if>									
										<%--
										<x:if select="$Certificate/SubjectDN/SERIALNUMBER != ''">
											(<x:out select="substring($Certificate/SubjectDN/SERIALNUMBER,4)"/>)
										</x:if>
										 --%>
										<x:if select="$Certificate/Valid = 'false'">
											[<wp:i18n key="LABEL_FIRMA_DIGITALE_CERTIFICATO_NON_INTEGRO" />]
										</x:if>
										<x:if select="$Certificate/CheckAtDate = 'false'">
											[<wp:i18n key="LABEL_FIRMA_DIGITALE_CERTIFICATO_UNTRUSTED" />]
										</x:if>
										<x:if select="$Certificate/NonRepudiation = 'false'">
											[<wp:i18n key="LABEL_FIRMA_DIGITALE_CERTIFICATO_NON_VALIDO" />]
										</x:if>
										
										<ul class='infoFirma'>
											<!-- DATI DEL FIRMATARIO -->
											<li class=''>																	
												<i><wp:i18n key="LABEL_FIRMA_DIGITALE_DATI_FIRMATARIO" /></i>
												
												<c:set var="titolo"/>
												<x:if select="$Certificate/SubjectDN/T != ''">
													<c:set var="titolo"><x:out select="$Certificate/SubjectDN/T"/></c:set>
												</x:if>
												<c:set var="cognome"/>
												<x:if select="$Certificate/SubjectDN/SURNAME != ''">
													<c:set var="cognome"><x:out select="$Certificate/SubjectDN/SURNAME"/></c:set>
												</x:if>
												<c:set var="nome"/>
												<x:if select="$Certificate/SubjectDN/GIVENNAME != ''">
													<c:set var="nome"><x:out select="$Certificate/SubjectDN/GIVENNAME"/></c:set>
												</x:if>									
												<c:set var="codice"/>
												<x:if select="$Certificate/SubjectDN/DN != ''">
													<c:set var="codice"><x:out select="$Certificate/SubjectDN/DN"/></c:set>
												</x:if>									
												<c:set var="cf"/>
												<x:if select="$Certificate/SubjectDN/SERIALNUMBER != ''">
													<c:set var="cf"><x:out select="substring($Certificate/SubjectDN/SERIALNUMBER,4)"/></c:set>
												</x:if>
												<c:set var="o"/>
												<x:if select="$Certificate/SubjectDN/O != ''">
													<c:set var="o"><x:out select="$Certificate/SubjectDN/O"/></c:set>
												</x:if>
												<c:set var="ou"/>
												<x:if select="$Certificate/SubjectDN/OU != ''">
													<c:set var="ou"><x:out select="$Certificate/SubjectDN/OU"/></c:set>
												</x:if>
												<c:set var="nazione"/>
												<x:if select="$Certificate/SubjectDN/C != ''">
													<c:set var="nazione"><x:out select="$Certificate/SubjectDN/C"/></c:set>
												</x:if>
												
												<ul>
													<c:if test='${titolo != ""}'>
														<li class='<c:if test='${cognome == "" && nome == "" && codice == "" && cf == "" && o == "" && ou == "" && nazione == ""}'>last</c:if>'>
															<label><wp:i18n key="LABEL_TITOLO" /> :</label> ${titolo}
														</li>
													</c:if>
																				
													<c:if test='${cognome != ""}'>	
														<li class='<c:if test='${nome == "" && codice == "" && cf == "" && o == "" && ou == "" && nazione == ""}'>last</c:if>'>
															<label><wp:i18n key="LABEL_COGNOME" /> :</label> ${cognome}
														</li>
													</c:if>
													
													<c:if test='${nome != ""}'>
														<li class='<c:if test='${codice == "" && cf == "" && o == "" && ou == "" && nazione == ""}'>last</c:if>'>
															<label><wp:i18n key="LABEL_NOME" /> :</label> ${nome}
														</li>
													</c:if>
				
													<c:if test='${codice != ""}'>
														<li class='<c:if test='${cf == "" && o == "" && ou == "" && nazione == ""}'>last</c:if>'>										
															<label><wp:i18n key="LABEL_CODICE_IDENTIFICATIVO" /> :</label> ${codice}
														</li>
													</c:if>
																	
													<%--
													<c:if test='${cf != ""}'>
														<li class='<c:if test='${o == "" && ou == "" && nazione == ""}'>last</c:if>'>																	
															<label><wp:i18n key="LABEL_CODICE_FISCALE" /> :</label> ${cf}
														</li>
													</c:if>
													--%>
													
													<c:if test='${o != ""}'>
														<li class='<c:if test='${ou == "" && nazione == ""}'>last</c:if>'>
															<label><wp:i18n key="LABEL_ORGANIZZAZIONE" /> :</label> ${o}
														</li>
													</c:if>
															
													<c:if test='${ou != ""}'>
														<li class='<c:if test='${nazione == ""}'>last</c:if>'>
															<label><wp:i18n key="LABEL_UNITA_ORGANIZZATIVA" /> :</label> ${ou}
														</li>								
													</c:if>
															
													<c:if test='${nazione != ""}'>
														<li class='last'>
															<label><wp:i18n key="LABEL_NAZIONE" /> :</label> ${nazione}
														</li>
													</c:if>
												</ul>
											</li>
																													
											<!-- DATI ENTE CERTIFICATORE -->	
											<li class=''>
												<i><wp:i18n key="LABEL_FIRMA_DIGITALE_DATI_ENTE_CERTIFICATORE" /></i>
																					
												<c:set var="denominazione"><x:out select="$Certificate/IssuerDN/CN"/></c:set>
												<c:set var="o"><x:out select="$Certificate/IssuerDN/O"/></c:set>
												<c:set var="ou"><x:out select="$Certificate/IssuerDN/OU"/></c:set>
												<c:set var="nazione"><x:out select="$Certificate/IssuerDN/C"/></c:set>
												
												<ul>
													<c:if test='${denominazione != ""}'>		
														<li class='<c:if test='${o == "" && ou == "" && nazione == ""}'>last</c:if>'>
															<label><wp:i18n key="LABEL_DENOMINAZIONE" /> :</label> ${denominazione}											
														</li>
													</c:if>
															
													<c:if test='${o != ""}'>					
														<li class='<c:if test='${ou == "" && nazione == ""}'>last</c:if>'>								
															<label><wp:i18n key="LABEL_ORGANIZZAZIONE" /> :</label> ${o}
														</li>
													</c:if>
				 											
													<c:if test='${ou != ""}'>	
														<li class='<c:if test='${nazione == ""}'>last</c:if>'>
															<label><wp:i18n key="LABEL_UNITA_ORGANIZZATIVA" /> :</label> ${ou}
														</li>
													</c:if>
													
													<c:if test='${nazione != ""}'>
														<li class='last'>
															<label><wp:i18n key="LABEL_NAZIONE" /> :</label> ${nazione}
														</li>
													</c:if>
												</ul>																											
											</li>	
																						
											<!-- DATI DEL CERTIFICATO -->
											<li class='last'>
												<i><wp:i18n key="LABEL_FIRMA_DIGITALE_DATI_CERTIFICATO" /></i>
												
												<ul>
													<x:if select="$Certificate/SerialNumber != ''">
														<li>
															<label><wp:i18n key="LABEL_FIRMA_DIGITALE_NUMERO_SERIALE" /> :</label>
															<x:out select="$Certificate/SerialNumber"/>
														</li>
													</x:if>
															
													<x:if select="$Certificate/NotBefore != ''">
														<li>
															<label><wp:i18n key="LABEL_FIRMA_DIGITALE_CERTIFICATO_VALIDO_DAL" /> :</label>
															<x:out select="$Certificate/NotBefore"/>
														</li>
													</x:if>
															
													<x:if select="$Certificate/NotAfter != ''">
														<li>
															<label><wp:i18n key="LABEL_FIRMA_DIGITALE_DATA_SCADENZA" /> :</label>
															<x:out select="$Certificate/NotAfter"/>
														</li>
													</x:if>
													
													<li>
														<label><wp:i18n key="LABEL_FIRMA_DIGITALE_DATA_FIRMA" /> :</label>
														<x:out select="$Certificate/SigningDate"/>
													</li>
													
													<x:if select="$Certificate/DigestAlgOID != ''">																								
														<li>
															<label><wp:i18n key="LABEL_FIRMA_DIGITALE_ALGORITMO" /> :</label>
															<x:out select="$Certificate/DigestAlgOID"/><x:if select="$Certificate/DigestAlgOIDDescription != ''"> (<x:out select="$Certificate/DigestAlgOIDDescription"/>)</x:if>											
														</li>
													</x:if>
													
													<li>
														<label><wp:i18n key="LABEL_FIRMA_DIGITALE_DOMANDA_VALIDO" /> </label>
														<x:choose>
															<x:when select="$Certificate/Valid = 'true'">
																<wp:i18n key="YES" />
															</x:when>
															<x:otherwise>
																<wp:i18n key="NO" />
															</x:otherwise>										
														</x:choose>									
													</li>
															
													<c:if test="${!empty ckdateformat}">
														<li>
															<label><wp:i18n key="LABEL_FIRMA_DIGITALE_DOMANDA_ATTENDIBILE" />? </label>
															<x:choose>
																<x:when select="$Certificate/CheckAtDate = 'true'">
																	<wp:i18n key="YES" />
																</x:when>
																<x:otherwise>
																	<wp:i18n key="NO" />							
																</x:otherwise>										
															</x:choose>
														</li>
													</c:if>
																						
													<li class='last'>
														<label><wp:i18n key="LABEL_FIRMA_DIGITALE_DOMANDA_LEGALE" /> </label>
														<x:choose>
															<x:when select="$Certificate/NonRepudiation = 'true'">
																<wp:i18n key="YES" />
															</x:when>
															<x:otherwise>
																<wp:i18n key="NO" />, <wp:i18n key="LABEL_FIRMA_DIGITALE_NO_RIPUDIO" />
															</x:otherwise>										
														</x:choose>							
													</li>
												</ul>																				
											</li>											
										</ul>													
									</li>									
								</div>		
							</x:forEach>
							<c:forEach begin="1" end="${livelli}" varStatus="loop">
								</ul>
								</li>
							</c:forEach>
						</ul>			
					</div>									
				</x:if>		
				
			</c:otherwise>
		</c:choose>
 	</c:if>
 
				<!-- MARCHE TEMPORALI -->				
				<c:if test="${! empty verificaMarcheTemporaliXML}">
					<x:parse var="xverificaMarcheTemporali" xml="${verificaMarcheTemporaliXML}" />					
					<x:if select='count($xverificaMarcheTemporali) > 0'>
					
						<div class="tree-content">		
							<h3 class="detail-section-title">
								<span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_FIRMA_DIGITALE_MARCHE_TEMPORALI" />
							</h3>

							<span id="marchetemporalitreecontrol" style="float: right;">
								<a title="<wp:i18n key="COLLAPSE_ALL" />" href="#"><img src="${imgPath}minus.gif" /> <wp:i18n key="COLLAPSE_ALL" /></a>&nbsp;&nbsp;
								<a title="<wp:i18n key="EXPAND_ALL" />" href="#"><img src="${imgPath}plus.gif" /> <wp:i18n key="EXPAND_ALL" /></a>
							</span>				
						
							<ul class="maintree" id="marchetemporalitree" >							
								<x:forEach var="TimeStamp" select="$xverificaMarcheTemporali//TimeStamp" varStatus="timeStampstatus">
									<li>
										<i><wp:i18n key="LABEL_FIRMA_DIGITALE_MARCA_TEMPORALE" /> # <x:out select="$TimeStamp/@Id"/></i>
										<ul>
											<li>
												<label><wp:i18n key="LABEL_FIRMA_DIGITALE_DATA_MARCA" /> : </label>
												<x:out select="$TimeStamp/GenTime"/>
											</li>
											<li>
												<label><wp:i18n key="LABEL_FIRMA_DIGITALE_NUMERO_SERIALE" /> : </label>
												<x:out select="$TimeStamp/SerialNumber"/>
											</li>
											<li>
												<label><wp:i18n key="LABEL_FIRMA_DIGITALE_TSA" /> : </label>
												<x:out select="$TimeStamp/Tsa"/>
											</li>
											<li>
												<label><wp:i18n key="LABEL_FIRMA_DIGITALE_TSA_POLICY_ID" /> : </label>
												<x:out select="$TimeStamp/TsaPolicyId"/>
											</li>
										</ul>
									</li>
								</x:forEach>
							</ul>
						</div>						
					</x:if>
				</c:if>	
  		
	<div class="back-link">
		<form action="${from}" method="post">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			
			<s:iterator value="hiddenParams" status="stat">
				<s:if test='value != ""'>
					<input type="hidden" name='<s:property value="key"/>' value='<s:property value="value"/>'/>
				</s:if>
			</s:iterator>
			
			<a href="javascript:;" onclick="parentNode.submit();">
				<wp:i18n key="LINK_BACK_TO_PAGE" />
			</a>
		</form>
	</div>
</div>
