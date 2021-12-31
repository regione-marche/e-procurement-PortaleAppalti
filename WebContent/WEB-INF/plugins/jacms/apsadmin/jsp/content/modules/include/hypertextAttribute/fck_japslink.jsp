<%@ taglib uri="aps-core.tld" prefix="wp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib uri="apsadmin-core.tld" prefix="wpsa"%>
<%@ taglib uri="apsadmin-form.tld" prefix="wpsf"%>
<wp:contentNegotiation mimeType="application/xhtml+xml" charset="utf-8" />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="it">
<head>
<title>jAPSLink</title>
	<link rel="stylesheet" type="text/css" href="<wp:resourceURL />administration/js/fckeditor/editor/plugins/japslink/fck_japslink.css" media="screen"  />
	<link rel="stylesheet" type="text/css" href="<wp:resourceURL />administration/css/administration.css" media="screen"  />
	<link rel="stylesheet" type="text/css" href="<wp:resourceURL />administration/css/menu.css" media="screen"  />
	<link rel="stylesheet" type="text/css" href="<wp:resourceURL />administration/css/layout.css" media="screen"  />
	<link rel="stylesheet" type="text/css" href="<wp:resourceURL />administration/css/aural.css" media="aural" />

	<!--[if lte IE 6]>
		<link rel="stylesheet" type="text/css" href="<wp:resourceURL />administration/css/administration_ie6.css" media="screen"  />
		<link rel="stylesheet" type="text/css" href="<wp:resourceURL />administration/css/menu_ie6.css" media="screen"  />
		<link rel="stylesheet" type="text/css" href="<wp:resourceURL />administration/css/layout_ie6.css" media="screen"  />		
	<![endif]-->

	<!--[if IE 7]>
		<link rel="stylesheet" type="text/css" href="<wp:resourceURL />administration/css/menu_ie7.css" media="screen"  />
	<![endif]-->

	<script type="text/javascript" src="<wp:resourceURL />administration/js/fckeditor/editor/dialog/common/fck_dialog_common.js"></script>
	<script type="text/javascript" src="<wp:resourceURL />administration/js/mootools-1.2-core.js"></script>
	<script type="text/javascript" src="<wp:resourceURL />administration/js/mootools-1.2-more.js"></script>
	<script type="text/javascript" src="<wp:resourceURL />administration/js/moo-japs/moo-jAPS-0.2.js"></script>

<script type="text/javascript">
<!--//--><![CDATA[//><!--

//per tab
window.addEvent('domready', function(){
	 var tabSet = new Taboo({
			tabs: "tab",
			tabTogglers: "tab-toggle",
			activeTabClass: "tab-current"
		});
});

//per pageTree
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

window.addEvent('domready', function(){
	if ( !eSelected )
		return ;

	if ( eSelected.tagName == 'SPAN' && eSelected._fckjapslink )
		txtName.value = eSelected._fckjapslink ;
	else
		eSelected == null ;
});

var FCK = window.opener.FCK;
var eSelected = FCK.Selection.GetSelectedElement();

//Eventi per bottoni
window.addEvent('domready', function(){

	//$('linkForm').getElements('input[name^=button]').removeEvents();

	//External URL
	$('form_externalURL').addEvent('submit', function(){
		if ( $('txtName').value.length == 0 ) {
			alert( '<s:text name="note.typeValidURL" />' ) ;
			return false ;
		}
		oLink = FCK.CreateLink( "#!U;" + $('txtName').value + "!#" );
		if (oLink) {
			window.close();
		}
	});	

	//Page Link
	$('form_pageLink').addEvent('submit', function(){
	    pageCode = null;
    	pageCodes = $('pageTree').getElements('input[name=selectedNode]').getProperty("value");
    	pageCodes.each(function(item, index){
	    	if ( $(item).getProperty("checked") ) {
		    	pageCode = $(item).getProperty("value");
	    	} 
    	});

		if ( null == pageCode ) {
			alert( '<s:text name="note.choosePageToLink" />');
			return false ;
		}
		oLink = FCK.CreateLink( "#!P;" + pageCode + "!#" );
		if (oLink) {
			window.close();
		}
	});	

	//Content Link
	$('button_contentLink').addEvents({
	    'click': function(){
	    	contentCode = null;
	    	contentCodes = $('contentListTable').getElements('input[name=contentId]').getProperty("value");
	    	contentCodes.each(function(item, index){
		    	if ( $('contentId_' + item).getProperty("checked") ) {
			    	contentCode = $('contentId_' + item).getProperty("value");
		    	} 
	    	});

			if ( null == contentCode ) {
				alert( '<s:text name="note.chooseContentToLink" />');
				return false ;
			}

			oLink = FCK.CreateLink( "#!C;" + contentCode + "!#" );
			if (oLink) {
				window.close();
			}
	    },
	    //FIXME: se ne va per la tangente col keypress del URL esterno per via del tabindex. Fixare :(
	    'keypress': function(){
	    	contentCodes = $('contentListTable').getElements('input[name=contentId]').getProperty("value");
	    	contentCodes.each(function(item, index){
		    	if ( $('contentId_' + item).getProperty("checked") ) {
			    	contentCode = $('contentId_' + item).getProperty("value");
		    	} 
	    	});

			if ( contentCode.length == 0 ) {
				alert( '<s:text name="note.chooseContentToLink" />');
				return false ;
			}

			oLink = FCK.CreateLink( "#!C;" + contentCode + "!#" );
			if (oLink) {
				window.close();
			}
	    }
	});	

});

//per tornare al tab di lista contenuti
window.addEvent('domready', function(){
	$('divContentLink').getElements('input[type=submit], input[name^=pagerItem_]').addEvents({
		'click': function(){
			formAction = $(document.body).getElement('form.searchForm').getProperty('action');
			$(document.body).getElement('form.searchForm').setProperty('action', formAction + '#divContentLink');
			//alert($(document.body).getElement('form.tab-container').getProperty('action'));
		},
		'keypress': function(){
			//FIXME :(
		}
	});
});

//per gli accordion
window.addEvent('domready', function(){
	var myAccordion = new Accordion($$('.accordion_toggler'), $$('.accordion_element'), {
	    show: -1,
	    duration: 'short',
	    alwaysHide: true
	});

	var myAnchor = new Element('img', {
	    'src': '<wp:resourceURL/>administration/img/icons/media-eject.png',
	    'class': 'myClass',
	    'style': 'vertical-align: middle',
	    'alt': '<s:text name="label.open" /> ',
	    'title': '<s:text name="label.open" /> '	    
	});

	$$('.accordion_toggler').each(function(cToggler) {
		cToggler.appendText(' ');
		var poba = myAnchor.clone();
		poba.injectBottom(cToggler);
	});
	
});	
	
/*
var variable = null;
var FCK = window.opener.FCK;
function ok() {
if(variable != null) {
FCK.Focus();
var B = FCK.EditorDocument.selection.createRange(); //only works in IE
B.text = variable;
}
window.close();
}
*/

//--><!]]></script>

</head>
<body>
<div id="corpo">
<h1><s:text name="title.contentManagement" /></h1>
<h2><s:text name="title.contentEditing" /></h2>
<h3><s:text name="note.chooseLinkType" /></h3>
<ul class="menu horizontal tab-toggle-bar">
	<li><a href="#divURLLink" class="tab-toggle"><s:text name="note.URLLinkTo" /></a></li>
	<li><a href="#divPageLink" class="tab-toggle"><s:text name="note.pageLinkTo" /></a></li>
	<li><a href="#divContentLink" class="tab-toggle"><s:text name="note.contentLinkTo" /></a></li>
</ul>

<div id="linkForm" class="tab-container">
<!-- URL Link -->
<form id="form_externalURL">
<div id="divURLLink" class="tab">
<h3 class="noscreen"><s:text name="note.URLLinkTo" /></h3>
<p>
	<label for="txtName"><s:text name="note.typeValidURL" />:</label><br />
	<wpsf:textfield id="txtName" name="txtName" maxlength="255" cssClass="text" />
</p>

<p><input id="button_externalURL" name="button_externalURL" type="submit" value="<s:text name="label.confirm" />" class="button" /></p>
</div>
</form>
<!-- //URL Link -->

<!-- Page Link -->
<form id="form_pageLink">
<div id="divPageLink" class="tab">
<p><s:text name="note.choosePageToLink" />.</p>
<ul id="pageTree">
	<s:set name="inputFieldName" value="'selectedNode'" />
	<s:set name="selectedTreeNode" value="selectedNode" />
	<s:set name="liClassName" value="'page'" />
	<s:set name="currentRoot" value="treeRootNode" />
	<s:include value="/WEB-INF/apsadmin/jsp/common/treeBuilder.jsp" />
</ul>
<p><input id="button_pageLink" name="button_pageLink" type="submit" value="<s:text name="label.confirm" />" class="button" /></p>
</div>
</form>
<!-- //Page Link -->

<!-- Content Link -->
<s:form action="search" cssClass="searchForm">
<div id="divContentLink" class="tab">
<p><s:text name="note.chooseContentToLink" />.</p>

<p><label for="text"><s:text name="label.search.for"/>&#32;<s:text name="label.description"/>:</label><br />
<wpsf:textfield name="text" id="text" cssClass="text" /></p>

<fieldset><legend class="accordion_toggler"><s:text name="title.searchFilters" /></legend>
	<div class="accordion_element">
		
		<p><label for="contentIdToken"><s:text name="label.code"/>:</label><br />
		<wpsf:textfield name="contentIdToken" id="contentIdToken" cssClass="text" /></p>		
		
		<p><label for="contentType"><s:text name="label.type"/>:</label><br />
		<wpsf:select name="contentType" id="contentType" 
			list="contentTypes" listKey="code" listValue="descr" 
			headerKey="" headerValue="%{getText('label.all')}" cssClass="text"></wpsf:select>
		</p>
	</div>
</fieldset>
<p><wpsf:submit value="%{getText('label.search')}" cssClass="button" /></p>

<wpsa:subset source="contents" count="10" objectName="groupContent" advanced="true" offset="5">
<s:set name="group" value="#groupContent" />

<div class="pager">
	<s:include value="/WEB-INF/apsadmin/jsp/common/inc/pagerInfo.jsp" />
	<s:include value="/WEB-INF/apsadmin/jsp/common/inc/pager_formBlock.jsp" />
</div>

<p class="noscreen">
	<wpsf:hidden name="lastGroupBy" />
	<wpsf:hidden name="lastOrder" />
</p>

<s:if test="%{getContents().size() > 0}">
	<table class="generic" id="contentListTable" summary="<s:text name="note.content.fck_japslink.summary" />">
	<caption><s:text name="title.contentList" /></caption>
	<tr>
		<th><a href="
		<s:url action="changeOrder" >
			<s:param name="text">
				<s:property value="#request.text"/>
			</s:param>
			<s:param name="contentIdToken">
				<s:property value="#request.contentIdToken"/>
			</s:param>
			<s:param name="contentType">
				<s:property value="#request.contentType"/>
			</s:param>
			<s:param name="pagerItem">
				<s:property value="#groupContent.currItem"/>
			</s:param>
			<s:param name="lastGroupBy"><s:property value="lastGroupBy"/></s:param>
			<s:param name="lastOrder"><s:property value="lastOrder"/></s:param>
			<s:param name="groupBy">descr</s:param>
		</s:url>#divContentLink"><s:text name="label.description" /></a></th>
		
		<th><a href="
		<s:url action="changeOrder" >
			<s:param name="text">
				<s:property value="#request.text"/>
			</s:param>
			<s:param name="contentIdToken">
				<s:property value="#request.contentIdToken"/>
			</s:param>
			<s:param name="contentType">
				<s:property value="#request.contentType"/>
			</s:param>
			<s:param name="pagerItem">
				<s:property value="#groupContent.currItem"/>
			</s:param>
			<s:param name="lastGroupBy"><s:property value="lastGroupBy"/></s:param>
			<s:param name="lastOrder"><s:property value="lastOrder"/></s:param>
			<s:param name="groupBy">code</s:param>
		</s:url>#divContentLink"><s:text name="label.code" /></a></th>
		
		<th><s:text name="label.creationDate" /></th>
		<th><s:text name="label.lastEdit" /></th>
	</tr>
	<s:iterator id="contentId">
	<s:set name="content" value="%{getContentVo(#contentId)}"></s:set>
	<tr>
	<td><input type="radio" name="contentId" id="contentId_<s:property value="#content.id"/>" value="<s:property value="#content.id"/>" />
	<label for="contentId_<s:property value="#content.id"/>"><s:property value="#content.descr" /></label></td>
	<td><s:property value="#content.id"/></td><%-- AGGIUNTO DA MATTEO--%>
	<td class="centerText"><span class="monospace"><s:date name="#content.create" format="dd/MM/yyyy HH:mm" /></span></td>
	<td class="icon"><span class="monospace"><s:date name="#content.modify" format="dd/MM/yyyy HH:mm" /></span></td>
	</tr>
	</s:iterator>
	</table>
</s:if>		

<div class="pager">
	<s:include value="/WEB-INF/apsadmin/jsp/common/inc/pager_formBlock.jsp" />
</div>

</wpsa:subset>
<p><input id="button_contentLink" name="button_contentLink" type="submit" value="<s:text name="label.confirm" />" class="button" /></p>
</div>
</s:form>
<!-- //Content Link -->
</div>

</div>
</body>
</html>