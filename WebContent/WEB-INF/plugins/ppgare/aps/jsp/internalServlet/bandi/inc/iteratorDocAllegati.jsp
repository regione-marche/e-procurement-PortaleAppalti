<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<s:set var="path">${param.path}</s:set>

<s:url id="urlFileDownload" namespace="/do/FrontEnd/DocDig" action="%{path}" />

<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/bandi/inc/innerIteratorDocAllegati.jsp">							
	<jsp:param name="urlDownload" value="${urlFileDownload}"/>																				
	<jsp:param name="dataPubblicazione" value="${param.dataPubblicazione}"/>
</jsp:include>