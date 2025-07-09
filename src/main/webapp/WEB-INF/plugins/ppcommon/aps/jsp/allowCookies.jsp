<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>

<es:checkCustomization var="abilitaCookies" objectId="BOTFILTER" attribute="ABILITACOOKIES" feature="ACT" />

<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/jquery_ui.jsp" />

<%-- verifica se e' gia' presente il cookie oppure se e' appena stato generato --%>
<c:set var="accessCookiePresente" value=""/>
<c:if test="${(cookie['USERACCESSCOOKIE'] != null && cookie['USERACCESSCOOKIE'] != '')}">
	<c:set var="accessCookiePresente" value="${cookie['USERACCESSCOOKIE'].value}"/>
</c:if>
 
<%--
cookie['USERACCESSCOOKIE']=${cookie['USERACCESSCOOKIE']}<br/>
cookie['USERACCESSCOOKIE'].value=${cookie['USERACCESSCOOKIE'].value}<br/>
accessCookiePresente=${accessCookiePresente}<br/>
 --%>

<c:set var="showAllowCookie" scope="request"  value="0"/>
<c:if test="${abilitaCookies && accessCookiePresente == ''}">
	<c:set var="showAllowCookie" scope="request" value="1"/>

	<style>
		.modalCookies {
		  display: none;
		  position: fixed;
		  z-index: 999;			/* dialog on top */
		  padding-top: 100px;
		  left: 0;
		  top: 0;
		  width: 100%;
		  height: 100%;
		  overflow: auto;
		  background-color: rgb(0,0,0);
		  background-color: rgba(0,0,0,0.4);
		}
		
		.modal-content {
		  background-color: #fefefe;
		  margin: auto;
		  padding: 20px;
		  border: 1px solid #888;
		  width: 80%;
		}
		
		table, tbody, tr, th, td{
    		background-color: rgba(0, 0, 0, 0.0) !important;
    		border: 0;
		}
	</style>


	<%-- dialog con il messaggio "In questo sito si utilizzano solo cookie ..." --%>
	<c:set var="baseUrl"><wp:info key="systemParam" paramName="applicationBaseURL"/></c:set>
	<c:set var="allowCaptchaUrl">${baseUrl}do/allowCaptcha.action</c:set>
	
	<div id="modalCookieDialog" class="modalCookies" style="display: block; ">
		<div class="modal-content">
		    <p>
		        <wp:i18n key='LABEL_INFO_SITE_COOKIES'/>
		    </p>
			<br/>
			<form action="${allowCaptchaUrl}" id="formCaptcha" name="formCaptcha" method="post">
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/captcha.jsp">
	    			<jsp:param name="submitForm" value="formCaptcha" /> 
	    			<jsp:param name="autoSubmitForm" value="1" />
	    		</jsp:include>
			</form>
		</div>
	</div>

</c:if>
