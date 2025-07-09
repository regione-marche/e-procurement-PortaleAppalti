<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<s:if test="model.totalPages > 1">

	<s:set var="labelGoToFirst" value ="%{getText('label.goToFirst')}" />
	<s:set var="labelPrevFull" value ="%{getText('label.prev.full')}" />
	<s:set var="labelPage" value ="%{getText('label.page')}" />
	<s:set var="labelNextFull" value ="%{getText('label.next.full')}" />
	<s:set var="labelGoToLast" value ="%{getText('label.goToLast')}" />
		
	<div id="pagination">

		<div id="pagination-info">
			<wp:i18n key="SEARCH_RESULTS_FROM"/> <s:property value="model.startIndexNumber + 1" /> <wp:i18n key="SEARCH_RESULTS_TO"/> <s:property value="model.lastIndexNumber" />
		</div>

		<div id="pagination-navi">
			<s:if test="model.hasPrevious">
				<input type="submit" class="nav-button nav-button-left-end" value="1" name="model.currentPage" title="<s:property value='%{#attr.labelGoToFirst}'/>" />
			</s:if>
			<s:else>
				<c:if test="${skin != 'highcontrast' && skin != 'text'}">
					<input disabled="disabled" type="submit" class="nav-button nav-button-left-end-gray"/>
				</c:if>
			</s:else>

			<s:if test="model.hasPrevious">
				<input type="submit" class="nav-button nav-button-left"  value="<s:property value="model.currentPage - 1"/>" name="model.currentPage" title="<s:property value='%{#attr.labelPrevFull}'/>" />
			</s:if>
			<s:else>
				<c:if test="${skin != 'highcontrast' && skin != 'text'}">
					<input disabled="disabled" type="submit" class="nav-button nav-button-left-gray"/>
				</c:if>
			</s:else>

			<!-- numeri di pagina direttamente accessibili -->
			<s:iterator value="model.pageList" status="status" var="page">
				<input type="submit" 
							 id='page_button_<s:property value="%{#page}"/>' 
							 class='button nav-page-button <s:if test="%{!#status.last}">nav-page-button-first</s:if> <s:if test="%{model.currentPage == #page}">selected</s:if>' 
							 value='<s:property value="%{#page}"/>' 
							 name="model.currentPage"
							 title="<s:property value='%{#attr.labelPage}'/> <s:property value="%{#page}"/>" />
			</s:iterator>

			<s:if test="model.hasNext">
				<input type="submit" class="nav-button nav-button-right" value="<s:property value="model.currentPage + 1"/>" name="model.currentPage" title="<s:property value='%{#attr.labelNextFull}'/>" />
			</s:if>
			<s:else>
				<c:if test="${skin != 'highcontrast' && skin != 'text'}">
					<input disabled="disabled" type="submit" class="nav-button nav-button-right-gray"/>
				</c:if>
			</s:else>

			<s:if test="model.hasNext">
				<input type="submit" class="nav-button nav-button-right-end" value="<s:property value="model.totalPages"/>" name="model.currentPage" title="<s:property value='%{#attr.labelGoToLast}'/>" />
			</s:if>
			<s:else>
				<c:if test="${skin != 'highcontrast' && skin != 'text'}">
					<input disabled="disabled" type="submit" class="nav-button nav-button-right-end-gray"/>
				</c:if>
			</s:else>
		</div>
		<div style="clear: both;"></div>
	</div>
</s:if>
<s:else>
	<%-- Accessibility Fix Criterion 3.2.2: insert an invisible "submit" button as workaraound --%>
	<input disabled="disabled" type="submit" style="display:none;"/>
</s:else>
