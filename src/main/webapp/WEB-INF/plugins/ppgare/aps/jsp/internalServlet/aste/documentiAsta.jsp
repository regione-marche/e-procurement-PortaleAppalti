<%@ taglib prefix="wp" uri="aps-core.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<script type="text/javascript" src='<wp:resourceURL/>static/js/jquery.treeview.js'></script>
 
<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp">
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<c:if test="${sessionScope.currentUser != 'guest'}">
</c:if>

<!-- <td class="money-content"> -->
<%-- <s:text name="format.money"><s:param value="#item.importo" /></s:text> &euro;	--%>

<div class="portgare-view">

	<h2><wp:i18n key="LABEL_ASTA_ELETTRONICA" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_DOCUMENTI_FASI_ASTA" />
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />

	<s:if test="not lottiDistinti" >
		<!-- ASTA LOTTO UNICO -->
		<div class="detail-section first-detail-section">
			<h3 class="detail-section-title">
				<span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_DOCUMENTAZIONE_INVITO" />
			</h3>
			
			<div class="detail-row">
				<s:set var="elencoDocumentiAllegati" value="%{allegatiAsta[codice]}" />
				<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/bandi/inc/iteratorDocAllegati.jsp">					
					<jsp:param name="path" value="downloadDocumentoRiservato"/>
				</jsp:include>
			</div>
			
			<div class="detail-section ">
				<h3 class="detail-section-title">
					<span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_FASI_SVOLGIMENTO" />
				</h3>
				
				<div class="detail-row">
					<s:set var="elencoFasi" value="%{fasiAsta[codice]}" />
					<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/aste/inc/iteratorFasi.jsp" />
				</div>
			</div>
		</div>	
	</s:if>
	<s:else>
		<!-- ASTA A LOTTI DISTINTI -->
		<s:iterator var="item" value="lotti" status="stat">
			<s:set var="lotto" value="#item.key" />
			<s:set var="asta" value="#item.value" />						
			<s:if test="%{#lotto.codiceInterno != null && #lotto.codiceInterno != ''}" >								
				<div class="detail-section">
					<h3 class="detail-section-title">
						<span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span> <wp:i18n key="LABEL_LOTTO" /> <s:property value="%{#lotto.codiceInterno}" />
					</h3>
					
					<div class="detail-row">
						<span class="important"><wp:i18n key="LABEL_DOCUMENTAZIONE_INVITO" /> </span>
						
						<div class="detail-subrow">
							<s:set var="elencoDocumentiAllegati" value="%{allegatiAsta[#lotto.codiceLotto]}" />
							<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/bandi/inc/iteratorDocAllegati.jsp">
								<jsp:param name="path" value="downloadDocumentoRiservato"/>
							</jsp:include>
						</div>
					</div>
					
					<div class="detail-row">
						<span class="important"><wp:i18n key="LABEL_FASI_SVOLGIMENTO" /> </span>
						
						<div class="detail-subrow">
							<s:set var="elencoFasi" value="%{fasiAsta[#lotto.codiceLotto]}" />
							<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/aste/inc/iteratorFasi.jsp" />
						</div>
					</div>
				</div>
			</s:if>
		</s:iterator>
	</s:else>
	
	<div class="back-link">
		<a href="<wp:action path="/ExtStr2/do/FrontEnd/Bandi/view.action"/>&amp;codice=${codice}&amp;ext=&amp;${tokenHrefParams}">
			<wp:i18n key="LINK_BACK_TO_PROCEDURE" />
		</a>
	</div>
</div>
