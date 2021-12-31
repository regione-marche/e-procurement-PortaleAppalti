<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<%--
il parametro "busta" va passato negli attributi della request, come segue:  
 
	<c:set var="busta" scope="request" value="${oggetto}"/>	
 	<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/garetel/inc/iteratorDocumentiInseriti.jsp"/>
			 
--%>
<s:set var="busta" value="#request.busta"/>

<s:if test="%{#busta.documentiInseriti.size() > 0}">
	<ul class="list">
		<s:iterator value="#busta.documentiInseriti" var="documento" status="stat">
			<li class='<s:if test="%{#stat.first}">first</s:if> <s:if test="%{#stat.last}">last</s:if>'>
				<c:choose>
					<c:when test="${skin == 'highcontrast' || skin == 'text'}">
						<s:if test="%{#documento.required}">
							 &#8226;&nbsp;<span title="<s:property value="%{#documento.nomeFile}"/> (<s:property value="%{#documento.dimensione}" />&nbsp;KB) SHA1: <s:property value="%{#documento.sha1}"/>"><s:property value="%{#documento.descrizione}"/></span>
						</s:if>
						<s:else>
							 &#8226;&nbsp;<span title="<s:property value="%{#documento.nomeFile}"/> (<s:property value="%{#documento.dimensione}" />&nbsp;KB) SHA1:<s:property value="%{#documento.sha1}"/>"><s:property value="%{#documento.descrizione}"/></span>
						</s:else>
					</c:when>
					<c:otherwise>
						<s:if test="%{#documento.required}">
							 &#8226;&nbsp;<span title="<s:property value="%{#documento.nomeFile}"/> (<s:property value="%{#documento.dimensione}" />&nbsp;KB) SHA1:<s:property value="%{#documento.sha1}"/>"><s:property value="%{#documento.descrizione}"/></span>
						</s:if>
						<s:else>
							 &#8226;&nbsp;<span title="<s:property value="%{#documento.nomeFile}"/> (<s:property value="%{#documento.dimensione}" />&nbsp;KB) SHA1:<s:property value="%{#documento.sha1}"/>"><s:property value="%{#documento.descrizione}"/></span>
						</s:else>
					</c:otherwise>
				</c:choose>							
				&nbsp;(<s:property value="%{#documento.nomeFile}"/>)
			</li>
		</s:iterator>
	</ul>
</s:if>