<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld"%>
<%@ taglib prefix="wp" uri="aps-core.tld"%>

<s:if test="model.totalPages > 1">
	<s:set id="offset" name="offset" value="5" />
	<s:if test="%{1 == model.currentPage }">
		<s:set id="goFirst" name="goFirst">
			<wp:resourceURL />administration/img/icons/transparent.png</s:set>
	</s:if>
	<s:else>
		<s:set id="goFirst" name="goFirst">
			<wp:resourceURL />administration/img/icons/go-first.png</s:set>
	</s:else>
	
	<s:if test="%{model.currentPage < #offset}">
		<s:set id="jumpBackward" name="jumpBackward">
			<wp:resourceURL />administration/img/icons/transparent.png</s:set>
	</s:if>
	<s:else>
		<s:set id="jumpBackward" name="jumpBackward">
			<wp:resourceURL />administration/img/icons/go-jump-backward.png</s:set>
	</s:else>
	
	<s:if test="%{1 == model.currentPage}">
		<s:set id="goPrevious" name="goPrevious">
			<wp:resourceURL />administration/img/icons/transparent.png</s:set>
	</s:if>
	<s:else>
		<s:set id="goPrevious" name="goPrevious">
			<wp:resourceURL />administration/img/icons/previous.png</s:set>
	</s:else>
	<s:if test="%{model.totalPages == model.currentPage}">
		<s:set id="goNext" name="goNext">
			<wp:resourceURL />administration/img/icons/transparent.png</s:set>
	</s:if>
	<s:else>
		<s:set id="goNext" name="goNext">
			<wp:resourceURL />administration/img/icons/next.png</s:set>
	</s:else>
	<div>
		<s:if test="%{model.currentPage+#offset > model.totalPages}">
			<s:set id="jumpForward" name="jumpForward">
				<wp:resourceURL />administration/img/icons/transparent.png</s:set>
		</s:if>
		<s:else>
			<s:set id="jumpForward" name="jumpForward">
				<wp:resourceURL />administration/img/icons/go-jump-forward.png</s:set>
		</s:else>

		<s:if test="%{model.totalPages == model.currentPage}">
			<s:set id="goLast" name="goLast">
				<wp:resourceURL />administration/img/icons/transparent.png</s:set>
		</s:if>
		<s:else>
			<s:set id="goLast" name="goLast">
				<wp:resourceURL />administration/img/icons/go-last.png</s:set>
		</s:else>
		
		
		
		<s:if test="model.hasPrevious">
			<input type="submit" style="background: url(${goFirst}) no-repeat; width:22px; height:22px; text-indent: -999em; border:0" name="model.currentPage" value="${1}" />
		</s:if>
		<s:else>
			<c:if test="${skin != 'highcontrast' && skin != 'text'}">
				<input hidden="true" disabled="disabled" type="submit" />
			</c:if>
		</s:else>

		<s:if test="model.hasPrevious">
				<s:set id="pageJump" name="pageJump"
					value="%{model.currentPage- #offset}" />
			 <input type="submit" style="background: url(${jumpBackward}) no-repeat; width:22px; height:22px; text-indent: -999em; border:0" name="model.currentPage" value="${pageJump}" />
		</s:if>

		<s:if test="model.hasPrevious">
 			<input type="submit" style="background: url(${goPrevious}) no-repeat; width:22px; height:22px; text-indent: -999em; border:0" name="model.currentPage" value="${model.currentPage - 1}" />
		</s:if>
		<s:else>
			<c:if test="${skin != 'highcontrast' && skin != 'text'}">
				<input hidden="true" disabled="disabled" type="submit" />
			</c:if>
		</s:else>

		<!-- numeri di pagina direttamente accessibili -->
		<s:iterator value="model.pageList" status="status" var="page">
			<input type="submit" id='page_button_<s:property value="%{#page}"/>'
				class="paddingLateral05" value='<s:property value="%{#page}"/>'
				name="model.currentPage"
				title="Pagina <s:property value="%{#page}"/>"
				<s:if test="%{#page == model.currentPage}">disabled="disabled"</s:if> />
		</s:iterator>

		<s:if test="model.hasNext">
			<input type="submit" style="background: url(${goNext}) no-repeat; width:22px; height:22px; text-indent: -999em; border:0" name="model.currentPage" value="${model.currentPage + 1}" />
		</s:if>
		<s:else>
			<c:if test="${skin != 'highcontrast' && skin != 'text'}">
				<input hidden="true" disabled="disabled" type="submit" />
			</c:if>
		</s:else>

		<s:if test="model.hasNext">
				<s:set id="pageJump" name="pageJump"
					value="%{model.currentPage + #offset}" />
			<input type="submit" style="background: url(${jumpForward}) no-repeat; width:22px; height:22px; text-indent: -999em; border:0" name="model.currentPage" value="${pageJump}" />
		</s:if>
		<s:else>
			<c:if test="${skin != 'highcontrast' && skin != 'text'}">
				<input hidden="true" disabled="disabled" type="submit" />
			</c:if>
		</s:else>

		<s:if test="model.hasNext">
				<input type="submit" style="background: url(${goLast}) no-repeat; width:22px; height:22px; text-indent: -999em; border:0" name="model.currentPage" value="${model.totalPages}" />
		</s:if>
		<s:else>
			<c:if test="${skin != 'highcontrast' && skin != 'text'}">
				<input hidden="true" disabled="disabled" type="submit" />
			</c:if>
		</s:else>
	</div>
</s:if>