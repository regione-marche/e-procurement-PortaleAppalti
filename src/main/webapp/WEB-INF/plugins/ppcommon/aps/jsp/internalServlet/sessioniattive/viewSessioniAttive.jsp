<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>

<html>

	<head>
		<title><wp:i18n key="MAIN_TITLE" /></title>
		
		<meta http-equiv="X-UA-Compatible" content="IE=Edge" /> <%-- per forzare ultima versione di Internet Explorer --%>

		<link rel="shortcut icon" href="<wp:imgURL/>favicon.ico" />
	</head>

	<body>

	<div class="portgare-view">
		
		<h2><s:text name="viewSessioniAttive.title" /></h2>
	
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_messages.jsp" />
	
		<br/>
	
		<s:url id="url" namespace="/do" action="processSessioniAttive"/>
		<c:set var="href">${url}</c:set>
		
	 	<form action="${href}" method="post">
 	
			<jsp:useBean id="dateValue" class="java.util.Date" />
			
			<div class="table-container">
				<table id="tableSessioniUtenti" summary="<s:text name="viewSessioniAttive.list.summary" />" 
					style="margin: 0px; 
					       width: 100%; 
					       border-collapse: collapse;
					       border-left: 1px solid #A0AABA; 
					       border-right: 1px solid #A0AABA;
					       border-top: 1px solid #A0AABA;
					       border-bottom: 1px solid #A0AABA;" >
					<thead>
						<tr>
							<th><strong><s:text name="viewSessioniAttive.column.select" /></strong></th>
							<th><strong><s:text name="viewSessioniAttive.column.ip" /></strong></th>
							<th><strong><wp:i18n key="USERNAME" /></strong></th>
							<th><strong><s:text name="viewSessioniAttive.column.firstAccess" /></strong></th>
							<th><strong><s:text name="viewSessioniAttive.column.lastAccess" /></strong></th>
							<%-- <th><strong><s:text name="viewSessioniAttive.column.sessionId" /></strong></th> --%>
						</tr>
					</thead>
					<tbody align="center" 
						style="border-top: 1px solid #A0AABA;
						       border-bottom: 1px solid #A0AABA;">
						
						<c:forEach items="${idSessioniUtentiConnessi}" var="id">
							<c:set var="dati" value="${datiSessioniUtentiConnessi[id]}" />
							
							<c:set var="rowColor" value="#A0A0A0" />
							<c:if test="${sessioniUtenti[id].lastAccessedTime > 0}">
								<c:set var="rowColor" value="#000000" />
							</c:if>
							
							<tr style="border-bottom: 1px solid #A0AABA; color: ${rowColor}" >
								<td>
									<input type="checkbox" name="keys" value="${id}" />
								</td>
								<td>
									${dati.ip}
								</td>
								<td>
									${dati.login}
								</td>
								<td>
									${dati.loginTime}
								</td>
								<td>
									<c:choose>
										<c:when test="${sessioniUtenti[id].lastAccessedTime <= 0}">
											<%-- <s:text name="viewSessioniAttive.sessionId.invalidated" />  --%>
											...
										</c:when>
										<c:otherwise>
											<c:catch var="exceptionSessioneInvalidata"> 
											<jsp:setProperty name="dateValue" property="time" value="${sessioniUtenti[id].lastAccessedTime}" />
											</c:catch>
											<c:choose>
												<c:when test="${! empty exceptionSessioneInvalidata}"><s:text name="viewSessioniAttive.sessionId.invalidated" /></c:when>
												<c:otherwise><fmt:formatDate value="${dateValue}" pattern="dd/MM/yyyy HH:mm:ss" /></c:otherwise>
											</c:choose>
										</c:otherwise>
									</c:choose>
								</td>
								<%--
								<td>
									${id}
								</td>
								 --%>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
				
			<br/>
		
			<div>
				<s:text name="viewSessioniAttive.question" /> <br>
				<input type="radio" name="metodo" value="refresh" checked="checked"><s:text name="viewSessioniAttive.action.refresh" /></input>&nbsp;&nbsp;&nbsp;
				<input type="radio" name="metodo" value="sendMessage"><s:text name="viewSessioniAttive.action.sendMessage" /></input>&nbsp;&nbsp;&nbsp;
				<input type="radio" name="metodo" value="invalidate"><s:text name="viewSessioniAttive.action.invalidate" /></input><br/>
				
				<s:text name="viewSessioniAttive.input.message" />: <textarea name="message" cols="40" rows="5"></textarea><br/>
				<s:text name="viewSessioniAttive.input.pin" />: <input type="password" name="pin" /><br/>
				<s:text name="viewSessioniAttive.button.submit" var="valueSubmit" />
				<s:submit value="%{#attr.valueSubmit}" cssClass="button" method="execute"></s:submit>
			</div>
			
		</form>	
	</div>
	</body>
</html>