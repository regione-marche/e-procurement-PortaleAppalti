<%@ taglib prefix="wp" uri="aps-core.tld"%>

<footer>
	<hr class="noscreen"/>
	<div id="footer-wrapper">
		<p class="noscreen">[ <a id="footerarea" class="back-to-main-area" href="#mainarea"><wp:i18n key="SKIP_TO_MAIN_CONTENT"/></a> ]</p>
		<jsp:include page="prefooter.jsp"></jsp:include>
		<div id="footer">
			<div id="footer-top"></div>
			<div id="footer-main">
				<wp:i18n key="COPYRIGHT"/>
			</div>
			<div id="footer-sub"></div><%-- end #footer --%>
		</div><%-- end #footer-wrapper --%>
		<p class="noscreen">[ <a class="back-to-top" href="#pagestart"><wp:i18n key="BACK_TO_THE_TOP"/></a> ]</p><%-- end #footer-main --%>
	</div>
</footer>