<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>


<div class="fieldset-row first-row">
	<div class="label">
		<label><wp:i18n key="LABEL_BUSTA_PREQUALIFICA" /> : </label>
	</div>
	<div class="element">
		<s:if test="%{!bustaPrequalificaCifrata}">
			<s:if test="%{idBustaPreq != null}">
				<wp:i18n key="TITLE_DOWNLOAD_DOCUMENTI_BUSTA_PREQUALIFICA" var="titleDownloadDocumentiPreq" />
				<c:choose>
					<c:when test="${skin == 'highcontrast' || skin == 'text'}">
						<s:a href="%{#urlDownloadBusta}?idBusta=%{idBustaPreq}&amp;tipoBusta=%{BUSTA_PRE_QUALIFICA}&amp;codice=%{codice}" 
								title="%{#attr.titleDownloadDocumentiPreq}">
							<wp:i18n key="LABEL_DOWNLOAD_DOCUMENTI" />
						</s:a>
					</c:when>
					<c:otherwise>
						<s:a href="%{#urlDownloadBusta}?idBusta=%{idBustaPreq}&amp;tipoBusta=%{BUSTA_PRE_QUALIFICA}&amp;codice=%{codice}" 
								 title="%{#attr.titleDownloadDocumentiPreq}" cssClass="bkg zip">
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
			<s:if test="%{bustaRiepilogativa.bustaPrequalifica.documentiInseriti.size() > 0}">
				<c:set var="busta" scope="request" value="${bustaRiepilogativa.bustaPrequalifica}"/>
				<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/garetel/inc/iteratorDocumentiInseriti.jsp"/>
			</s:if>
			<s:else>
				<wp:i18n key="LABEL_NO_DOCUMENTS_ATTACHED" />
			</s:else>
		</s:else>
	</div>
</div>
