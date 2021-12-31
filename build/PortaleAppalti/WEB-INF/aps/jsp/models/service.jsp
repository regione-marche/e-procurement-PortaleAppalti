<%@ taglib prefix="wp" uri="aps-core.tld"%>

<wp:contentNegotiation mimeType="application/xhtml+xml" charset="utf-8"/>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang='<wp:info key="currentLang" />'>
	<head>
		<title><wp:currentPage param="title" /></title>
	</head>
	<body>
		<h1>Pagina di errore</h1>
		<a href='<wp:url page="homepage" />'>Home</a><br/>
		<div><wp:show frame="0" /></div>
	</body>
</html>
