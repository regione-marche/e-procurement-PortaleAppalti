<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>
<wp:contentNegotiation mimeType="application/xhtml+xml" charset="utf-8"/>
<s:include value="/WEB-INF/apsadmin/jsp/common/template/defaultExtraResources.jsp" />

<script type="text/javascript">
<!--//--><![CDATA[//><!--

window.addEvent('domready', function(){

	var pageTree = new Wood({
		rootId: "pageTree",
		menuToggler: "subTreeToggler",
		openClass: "node_open",
		closedClass: "node_closed",
		showTools: "true",
		expandAllLabel: "<s:text name="label.expandAll"/>",
		collapseAllLabel: "<s:text name="label.collapseAll"/>",
		type: "tree",
		startIndex: "<s:property value="selectedNode" />",
		toolTextIntro: "<s:text name="label.introExpandAll" />",
		toolexpandAllLabelTitle: "<s:text name="label.expandAllTitle" />",
		toolcollapseLabelTitle: "<s:text name="label.collapseAllTitle" />"			
	});

});

//--><!]]></script>