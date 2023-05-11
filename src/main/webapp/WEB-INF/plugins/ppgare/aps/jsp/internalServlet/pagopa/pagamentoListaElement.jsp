<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="/struts-tags" %>



	<div class="list-summary">
		<wp:i18n key="SEARCH_RESULTS_INTRO" />
		<s:property value="model.iTotalDisplayRecords" />
		<wp:i18n key="SEARCH_RESULTS_OUTRO" />.
	</div>
	<s:iterator var="pag" value="listaPagamenti.dati" status="status">
		<div class="list-item">
			<%-- <div class="list-item-row">
				<label><wp:i18n key="LABEL_STAZIONE_APPALTANTE" /> : </label>
				<s:property value="id" />
			</div> --%>
			<div class="list-item-row">
				<label><wp:i18n key="LABEL_PAGOPA_GARA_CODICE" /> : </label>
				<s:property value="codicegara" />
			</div>
			<div class="list-item-row">
				<label><wp:i18n key="LABEL_PAGOPA_CAUSALE" /> : </label>
				<s:property value="%{tipiCausalePagamento[causaletip]}" />
			</div>
			<div class="list-item-row">
				<label><wp:i18n key="LABEL_PAGOPA_IMPORTO" /> : </label>
				<s:property value="importo" />
			</div>
			<div class="list-item-row">
				<label><wp:i18n key="LABEL_PAGOPA_STATO" /> : </label>
				<s:property value="%{tipiStatoPagamento[statotip]}" />
			</div>
			<%-- <div class="list-item-row">
				<label><wp:i18n key="LABEL_PAGOPA_DATASCAD" /> : </label>
				<s:property value="datascadenza" />
				<s:date name="datascadenza" format="dd/MM/yyyy HH:mm:ss" />
			</div> --%>
			<!-- TODO href to element -->
			<div align="right">
			<a href='<wp:action path="/ExtStr2/do/FrontEnd/PagoPA/dettaglioPagamento.action"/>&amp;model.id=<s:property value="id"/>&amp;${tokenHrefParams}' 
				   title="<wp:i18n key="LINK_VIEW_PAGOPA_DETT" />" class="bkg summary">
					<wp:i18n key="LINK_VIEW_PAGOPA_DETT" />
				</a>
			</div>
		</div>
	</s:iterator>
	
