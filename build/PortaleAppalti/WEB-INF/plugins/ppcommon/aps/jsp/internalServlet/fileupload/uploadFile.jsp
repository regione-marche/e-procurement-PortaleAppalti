<%-- 
  QUESTA JSP SOSTITUISCE ED INTEGRA IL VECCHIO static/js/ppgare/uploadFile.js
  REPLICA IL CODICE CONTENUTO NELLO SCRIPT ED AGGIUNGE UNA PARAMETRIZZAZIONE
  PER LA VERIFICA DEI CARATTERI VALIDI IN UN FILE IN FASE DI UPLOAD !!!
--%>
<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<c:set var="validFilenameChars"><%= it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.FileUploadUtilities.getValidFilenameChars() %></c:set>
<c:set var="invalidFileMsg"><s:property value='%{getText("Errors.invalidFileName")}' /></c:set>

<script type="text/javascript">
<!--//--><![CDATA[//><!--
                  
	var validFilenameChars = '${validFilenameChars}';
	var invalidFilenameMsg = '${invalidFileMsg}';
	
//--><!]]></script>

<script type="text/javascript" src='<wp:resourceURL/>static/js/ppgare/uploadFile.js'></script>
 