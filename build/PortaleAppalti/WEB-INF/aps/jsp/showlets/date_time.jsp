<%@ taglib prefix="wp"  uri="aps-core.tld" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="es"  uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>

<es:ntpDate var="now"/>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="date_time" />
</jsp:include>

<c:if test="${empty now.error}">
	<script type="text/javascript">
		
	<!--//--><![CDATA[//><!--
	var date_ntp = new Date(${now.year}, ${now.month}-1, ${now.day}, ${now.hours}, ${now.minutes}, ${now.seconds});

	function displayTime() {
		
		var disp;
		disp = (date_ntp.getDate() < 10 ? '0' : '') + date_ntp.getDate();
		disp += '/' + ((date_ntp.getMonth()+1) < 10 ? '0' : '') + (date_ntp.getMonth()+1);
		disp += '/' + date_ntp.getFullYear();
		disp += ' ' + (date_ntp.getHours() < 10 ? '0' : '') + date_ntp.getHours();
		disp += ':' + (date_ntp.getMinutes() < 10 ? '0' : '') + date_ntp.getMinutes();
		return disp ;
	}

	function refreshDateTime() {
		
		date_ntp.setSeconds(date_ntp.getSeconds()+1);
		show = displayTime();
		if (document.getElementById){
			document.getElementById('date-time').innerHTML = show;
		}  else if (document.all){
			document.all['date-time'].innerHTML = show;
		}  else if (document.layers) {
			document.layers['date-time'].innerHTML = show;
		}
		setTimeout('refreshDateTime()',1000);
	}

	window.onload = refreshDateTime;
	//--><!]]>
	
	</script>
</c:if>

<div id="date-time">
	<c:choose>
		<c:when test="${empty now.error}">
			<wp:currentPage param="code" var="currentViewCode"/>
			<form action="<wp:url page="${currentViewCode}" />" >
					<div>
						<fmt:formatDate value="${now.date}" pattern="dd/MM/yyyy HH:mm" />
						<input type="submit" value="<wp:i18n key="BUTTON_REFRESH_TIME" />" class="bkg time" title="<wp:i18n key="TITLE_REFRESH_TIME" />"/>
					</div>
			</form>
		</c:when>
		<c:otherwise>
			${now.error}
		</c:otherwise>
	</c:choose>
</div>
