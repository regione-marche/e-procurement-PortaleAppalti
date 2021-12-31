<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>
<wp:contentNegotiation mimeType="application/xhtml+xml" charset="utf-8"/>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="it">
<head>
	<title>jAPS 2.0 - Login</title>
	<link rel="stylesheet" type="text/css" href="<wp:resourceURL />administration/css/administration.css" />
	<link rel="stylesheet" type="text/css" href="<wp:resourceURL />administration/css/layout.css" />
	<!--[if lte IE 6]>
		<link rel="stylesheet" type="text/css" href="<wp:resourceURL />administration/css/layout_ie6.css" />
	<![endif]-->

	<script type="text/javascript" src="<wp:resourceURL />administration/js/mootools-1.2-core.js"></script>
	<script type="text/javascript" src="<wp:resourceURL />administration/js/mootools-1.2-more.js"></script>
	<script type="text/javascript" src="<wp:resourceURL />administration/js/login.js" ></script>	
	
</head>
<body>

<h1>jAPS 2.0 - <s:text name="title.login.intro" />&#32;<acronym title="Content Management System">C.M.S.</acronym></h1>
<s:form action="doLogin" >

	<s:if test="hasActionErrors()">
	<div id="actionErrorsBox" class="message message_error">
	<h2><s:text name="message.title.ActionErrors" /></h2>
	<ul>
		<s:iterator value="actionErrors">
		<li><s:property /></li>
		</s:iterator>
	</ul>
	</div>
	</s:if>

	<s:if test="hasFieldErrors()">
	<div id="fieldErrorsBox" class="message message_error">
	<h2><s:text name="message.title.FieldErrors" /></h2>	
	<ul>
		<s:iterator value="fieldErrors">
			<s:iterator value="value">
	          <li><s:property escape="false"/></li>
			</s:iterator>
		</s:iterator>
	</ul>
	</div>
	</s:if>

<s:if test="#session.currentUser != null && #session.currentUser.username != 'guest'">
<div class="whiteBox">
<p>
	<em><s:text name="note.userbar.welcome"/></em>,
	<strong> <s:property value="#session.currentUser" /></strong>!
</p>

<%--
<s:if test="!#session.currentUser.accountNotExpired && #session.currentUser.username != 'admin'">
<p>
	<s:text name="note.login.expiredAccount" />.
</p>
</s:if>
--%>

<s:if test="!#session.currentUser.credentialsNotExpired">
<p>
	<s:text name="note.login.expiredPassword.intro" />&#32;<a href="<s:url action="editPassword" />" ><s:text name="note.login.expiredPassword.outro" /></a>.
</p>
</s:if>

<s:else>
<wp:ifauthorized permission="enterBackend" var="checkEnterBackend" />

<c:choose>
	<c:when test="${checkEnterBackend}">
	<p>
		<s:text name="note.login.yetLogged" />,<br />
		<a href="<s:url action="main" />" ><s:text name="note.goToMain" /></a> | <a href="<s:url action="logout" namespace="/do" />" ><s:text name="menu.exit"/></a>
	</p>
	</c:when>
	<c:otherwise>
	<p>
		<s:text name="note.login.notAllowed" />, <a href="<s:url action="logout" namespace="/do" />" ><s:text name="menu.exit"/></a>
	</p>

	</c:otherwise>
</c:choose>

</s:else>

</div>
</s:if>

<s:else>
<div class="login">
<fieldset id="fieldset_space"><legend><s:text name="login.authenticationInfo" /></legend>
	<p><label for="username"><s:text name="label.username" />:</label><br />
	<wpsf:textfield name="username" id="username" cssClass="text" /></p>
	<p><label for="password"><s:text name="label.password" />:</label><br />
	<wpsf:password name="password" id="password" cssClass="text" /></p>
	<p><label for="request_locale"><s:text name="label.languages" />:</label><br />
	<%-- TODO: deve diventare un foreach sulle lingue configurate in amministrazione?  --%>
	<select id="request_locale" name="request_locale" class="text">
		<option value="en" selected="selected">English</option>
		<option value="it">Italiano</option>
	</select></p>
</fieldset>
<p><s:submit cssClass="button" value="%{getText('label.ok')}" /></p>
</div>
</s:else>
</s:form>
</body>
</html>