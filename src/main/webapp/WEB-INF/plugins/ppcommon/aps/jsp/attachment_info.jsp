<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>

<c:choose>

	<c:when test="${skin == 'highcontrast' || skin == 'text'}">
		<c:if test="${param.formatoFile == DOCUMENTO_FORMATO_FIRMATO || param.formatoFile == 1}">
			<span title='<wp:i18n key="TITLE_RICHIESTA_FIRMA_DIGITALE"/>'>[<wp:i18n key="LABEL_RICHIESTA_FIRMA_DIGITALE"/>]</span>
		</c:if>
		<c:if test="${param.formatoFile == DOCUMENTO_FORMATO_PDF || param.formatoFile == 2}">
			<span title='<wp:i18n key="TITLE_RICHIESTO_DOCUMENTO_PDF"/>'>[<wp:i18n key="LABEL_RICHIESTO_DOCUMENTO_PDF"/>]</span>
		</c:if>
		<c:if test="${param.formatoFile == DOCUMENTO_FORMATO_EXCEL || param.formatoFile == 4}">
			<span title='<wp:i18n key="TITLE_RICHIESTO_DOCUMENTO_EXCEL"/>'>[<wp:i18n key="LABEL_RICHIESTO_DOCUMENTO_EXCEL"/>]</span>
		</c:if>
		${param.nome} <c:if test="${param.obbligatorio}"><span class="required-field">*</span></c:if>
	</c:when>
	
	<c:otherwise>
		<c:if test="${param.formatoFile != null}">
			<s:set var="imgPdf"><wp:resourceURL/>static/img/pdf.svg</s:set>
			<s:set var="imgDocumentoFirmato"><wp:resourceURL/>static/img/smartcard.svg</s:set>
			<s:set var="imgExcel"><wp:resourceURL/>static/img/xls.svg</s:set>
			<s:set var="imgDocNonFirmato"><wp:resourceURL/>static/img/not-signed-alert.svg</s:set>
		
			<c:if test="${param.formatoFile == DOCUMENTO_FORMATO_FIRMATO || param.formatoFile == 1}">
				[<img src="${imgDocumentoFirmato}" class="resize-svg-16" title='<wp:i18n key="TITLE_RICHIESTA_FIRMA_DIGITALE"/>' alt='<wp:i18n key="LABEL_RICHIESTA_FIRMA_DIGITALE"/>' />]
			</c:if>
			<c:if test="${param.formatoFile == DOCUMENTO_FORMATO_PDF || param.formatoFile == 2}">
				[<img src="${imgPdf}" class="resize-svg-16" title='<wp:i18n key="TITLE_RICHIESTO_DOCUMENTO_PDF"/>' alt='<wp:i18n key="LABEL_RICHIESTO_DOCUMENTO_PDF"/>' />]
			</c:if>
			<c:if test="${param.formatoFile == DOCUMENTO_FORMATO_EXCEL || param.formatoFile == 4}">
				[<img src="${imgExcel}" class="resize-svg-16" title='<wp:i18n key="TITLE_RICHIESTO_DOCUMENTO_EXCEL"/>' alt='<wp:i18n key="LABEL_RICHIESTO_DOCUMENTO_EXCEL"/>' />]
			</c:if>
			${param.nome} <c:if test="${param.obbligatorio}"><span class="required-field">*</span></c:if>
		</c:if>
	</c:otherwise>
	
</c:choose>

