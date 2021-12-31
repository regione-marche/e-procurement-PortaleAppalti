<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>


<div class="fieldset-row first-row">
	<div class="label">
		<label><wp:i18n key="LABEL_BUSTA_AMMINISTRATIVA" /> : </label>
	</div>
	<div class="element">
		<s:if test="%{!bustaAmministrativaCifrata}">
			<s:if test="%{idBustaAmm != null}">
				<wp:i18n key="TITLE_DOWNLOAD_DOCUMENTI_BUSTA_AMMINISTRATIVA" var="titleDownloadDocumentiAmm" />
				<c:choose>
					<c:when test="${skin == 'highcontrast' || skin == 'text'}">
						<s:a href="%{#urlDownloadBusta}?idBusta=%{idBustaAmm}&amp;tipoBusta=%{BUSTA_AMMINISTRATIVA}&amp;codice=%{codice}" 
								title="%{#attr.titleDownloadDocumentiAmm}">
							 <wp:i18n key="LABEL_DOWNLOAD_DOCUMENTI" />
						</s:a>
					</c:when>
					<c:otherwise>
						<s:a href="%{#urlDownloadBusta}?idBusta=%{idBustaAmm}&amp;tipoBusta=%{BUSTA_AMMINISTRATIVA}&amp;codice=%{codice}" 
						 		title="%{#attr.titleDownloadDocumentiAmm}" cssClass="bkg zip">
						 	<wp:i18n key="LABEL_DOWNLOAD_DOCUMENTI" />
						</s:a>
					</c:otherwise>
				</c:choose>
			</s:if>
			<s:else>
				<wp:i18n key="LABEL_NO_DOCUMENTS_ATTACHED" />
			</s:else>
		</s:if>
		<s:else>
			<!-- INFO STATICHE -->
			<s:if test="%{bustaRiepilogativa.bustaAmministrativa.documentiInseriti.size() > 0}">
				<c:set var="busta" scope="request" value="${bustaRiepilogativa.bustaAmministrativa}"/>
				<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/garetel/inc/iteratorDocumentiInseriti.jsp"/>
			</s:if>
			<s:else>
				<wp:i18n key="LABEL_NO_DOCUMENTS_ATTACHED" />
			</s:else>
		</s:else>
	</div>
</div>

<s:if test="%{offertaTecnica}">
	<div class="fieldset-row">
		<div class="label">
			<label><wp:i18n key="LABEL_BUSTA_TECNICA" /> : </label>
		</div>
		<div class="element">
			<s:if test="%{!bustaTecnicaCifrata}">
				<s:if test="%{idBustaTec != null}">
					<wp:i18n key="TITLE_DOWNLOAD_DOCUMENTI_BUSTA_TECNICA" var="titleDownloadDocumentiTec" />
					<c:choose>
						<c:when test="${skin == 'highcontrast' || skin == 'text'}">
							<s:a href="%{#urlDownloadBusta}?idBusta=%{idBustaTec}&amp;tipoBusta=%{BUSTA_TECNICA}&amp;codice=%{codice}" 
									title="%{#attr.titleDownloadDocumentiTec}">
								<wp:i18n key="LABEL_DOWNLOAD_DOCUMENTI" />
							</s:a>
						</c:when>
						<c:otherwise>
							<s:a href="%{#urlDownloadBusta}?idBusta=%{idBustaTec}&amp;tipoBusta=%{BUSTA_TECNICA}&amp;codice=%{codice}" 
									 title="%{#attr.titleDownloadDocumentiTec}" cssClass="bkg zip">
								<wp:i18n key="LABEL_DOWNLOAD_DOCUMENTI" />
							</s:a>
						</c:otherwise>
					</c:choose>
				</s:if>
				<s:else>
					<wp:i18n key="LABEL_NO_DOCUMENTS_ATTACHED" />
				</s:else>
			</s:if>
			<s:else>
				<s:if test="%{bustaRiepilogativa.bustaTecnica.documentiInseriti.size() > 0}">
					<c:set var="busta" scope="request" value="${bustaRiepilogativa.bustaTecnica}"/>
					<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/garetel/inc/iteratorDocumentiInseriti.jsp"/>				
				</s:if>
				<s:else>
					<wp:i18n key="LABEL_NO_DOCUMENTS_ATTACHED" />
				</s:else>
			</s:else>
		</div>
	</div>
</s:if>

<!-- OEPV costoFisso=1 ==> non c'e' offerta economica -->
<s:set var="costoFisso" value="0"/>
<s:if test="%{dettGara.datiGeneraliGara.costoFisso != null && dettGara.datiGeneraliGara.costoFisso == 1}" >
	<s:set var="costoFisso" value="%{dettGara.datiGeneraliGara.costoFisso}"/>
</s:if>

<s:if test="%{#costoFisso != 1}" >
	<div class="fieldset-row last-row">
		<div class="label">
			<label><wp:i18n key="LABEL_BUSTA_ECONOMICA" /> : </label>
		</div>
		<div class="element">
			<s:if test="%{!bustaEconomicaCifrata}">
				<s:if test="%{idBustaEco != null}">
					<wp:i18n key="TITLE_DOWNLOAD_DOCUMENTI_BUSTA_ECONOMICA" var="titleDownloadDocumentiEco" />
					<c:choose>
						<c:when test="${skin == 'highcontrast' || skin == 'text'}">
							<s:a href="%{#urlDownloadBusta}?idBusta=%{idBustaEco}&amp;tipoBusta=%{BUSTA_ECONOMICA}&amp;codice=%{codice}" 
									title="%{#attr.titleDownloadDocumentiEco}">
								<wp:i18n key="LABEL_DOWNLOAD_DOCUMENTI" />
							</s:a>
						</c:when>
						<c:otherwise>
							<s:a href="%{#urlDownloadBusta}?idBusta=%{idBustaEco}&amp;tipoBusta=%{BUSTA_ECONOMICA}&amp;codice=%{codice}" 
									 title="%{#attr.titleDownloadDocumentiEco}" cssClass="bkg zip">
								<wp:i18n key="LABEL_DOWNLOAD_DOCUMENTI" />
							</s:a>
						</c:otherwise>
					</c:choose>
				</s:if>
				<s:else>
					<wp:i18n key="LABEL_NO_DOCUMENTS_ATTACHED" />
				</s:else>
			</s:if>
			<s:else>
				<s:if test="%{bustaRiepilogativa.bustaEconomica.documentiInseriti.size() > 0}">
					<c:set var="busta" scope="request" value="${bustaRiepilogativa.bustaEconomica}"/>
					<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/garetel/inc/iteratorDocumentiInseriti.jsp"/>			
				</s:if>
				<s:else>
					<wp:i18n key="LABEL_NO_DOCUMENTS_ATTACHED" />
				</s:else>
			</s:else>
		</div>
	</div>
</s:if>
 