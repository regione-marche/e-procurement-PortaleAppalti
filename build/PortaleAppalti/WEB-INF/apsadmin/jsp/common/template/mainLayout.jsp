<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>
<wp:contentNegotiation mimeType="application/xhtml+xml" charset="utf-8"/>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="it">
<head>
	<title>jAPS 2.0 - <s:set name="documentTitle"><tiles:getAsString name="title"/></s:set><s:property value="%{getText(#documentTitle)}" escape="false" /></title>
	<link rel="stylesheet" type="text/css" href="<wp:resourceURL />administration/css/administration.css" media="screen" />
	<link rel="stylesheet" type="text/css" href="<wp:resourceURL />administration/css/menu.css" media="screen" />
	<link rel="stylesheet" type="text/css" href="<wp:resourceURL />administration/css/layout.css" media="screen" />
	<link rel="stylesheet" type="text/css" href="<wp:resourceURL />administration/css/aural.css" media="aural" />
	<!--[if lte IE 6]>
		<link rel="stylesheet" type="text/css" href="<wp:resourceURL />administration/css/administration_ie6.css" media="screen" />
		<link rel="stylesheet" type="text/css" href="<wp:resourceURL />administration/css/menu_ie6.css" media="screen" />
		<link rel="stylesheet" type="text/css" href="<wp:resourceURL />administration/css/layout_ie6.css" media="screen" />
	<![endif]-->

	<!--[if IE 7]>
		<link rel="stylesheet" type="text/css" href="<wp:resourceURL />administration/css/administration_ie7.css" media="screen" />
		<link rel="stylesheet" type="text/css" href="<wp:resourceURL />administration/css/menu_ie7.css" media="screen" />
	<![endif]-->
	

	<!-- QUA' FUORI LE COSE COMUNI. DENTRO L'EXTRA LE DIFFERENZE -->
	<tiles:insertAttribute name="extraResources"/>
	
</head>
<body>
<div id="header">
<tiles:insertAttribute name="header"/>
</div>
<div id="corpo">
	<div id="colonna1">
	<tiles:insertAttribute name="menu"/>
	</div>
	<div id="colonna2">
	<p class="noscreen"><a href="#fagiano_start" id="fagiano_mainContent"><s:text name="note.backToStart" /></a></p>	
	<tiles:insertAttribute name="body"/>
	<p class="noscreen"><a href="#fagiano_mainContent"><s:text name="note.backToMainContent" /></a></p>	
	<p class="noscreen"><a href="#fagiano_start"><s:text name="note.backToStart" /></a></p>
	</div>
</div>
<div id="footer">
<tiles:insertAttribute name="footer"/>
</div>
</body>
</html>