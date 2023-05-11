<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>

<es:checkCustomization var="abilitaCookies" objectId="BOTFILTER" attribute="ABILITACOOKIES" feature="ACT" />


<c:if test="${abilitaCookies && (cookie['USERACCESSCOOKIE'] == null || cookie['USERACCESSCOOKIE'] == '')}">

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
	</style>


	<%-- dialog con il messaggio "In questo sito si utilizzano solo cookie ..." --%>
	<div id="modalCookieDialog" class="modalCookies" style="display: block; ">
	  <div class="modal-content">    
	    <p>
	        <wp:i18n key='LABEL_INFO_SITE_COOKIES'/>
	    </p>
	      
	    <button id="allowCookies"><wp:i18n key='BUTTON_ACCETTA_COOKIES'/></button>
	  </div>
	</div>
	

	<script src="<wp:resourceURL/>static/js/jquery-ui-1.12.1.min.js"></script>
	<c:set var="allowUrl"><wp:info key="systemParam" paramName="applicationBaseURL"/>do/allowCookies.action</c:set>

	<script>
	<!--//--><![CDATA[//><!--
		var dialog = document.getElementById("modalCookieDialog");	
		var btnAllow = document.querySelector('#allowCookies');
		var allowUrl = '${allowUrl}';
		
		btnAllow.addEventListener('click', function(event) {
		  if (event.isTrusted) {
			  // thrusted user
			  $.ajax({			  	
				    url : allowUrl,
				    type : "POST",
				    success : function(data) {
				    	dialog.style.display = "none";
				    }
				});
		  }
		});
	//--><!]]>
	</script>

</c:if>
