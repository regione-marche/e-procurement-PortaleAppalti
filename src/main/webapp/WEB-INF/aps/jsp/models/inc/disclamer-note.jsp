<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wp" uri="aps-core.tld"%>

<%--
IE10		Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; WOW64; Trident/6.0)
????		Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.2)
IE11		MOZILLA/5.0 (WINDOWS NT 10.0; WOW64; TRIDENT/7.0; RV:11.0) LIKE GECKO
EDGE		MOZILLA/5.0 (WINDOWS NT 10.0; WIN64; X64) APPLEWEBKIT/537.36 (KHTML, LIKE GECKO) CHROME/64.0.3282.140 SAFARI/537.36 EDGE/18.17763
--%>
<%	
	int IEVer = -1;	
	
	String userAgent = request.getHeader("user-agent");
	if(userAgent != null) {
		userAgent = userAgent.toUpperCase();
		if(userAgent.indexOf("MSIE") > 0) {
			IEVer = 8;
			int i1 = userAgent.indexOf("MSIE");
			int i2 = userAgent.indexOf(";", i1);
			if(i1 > 0 && i2 > 0) {
				String s = userAgent.substring(i1 + 5, i2);
				s = s.substring(0, s.indexOf("."));	
				IEVer = Integer.parseInt(s);
			}
		} else if(userAgent.indexOf("TRIDENT") > 0 && userAgent.indexOf("RV:") > 0) {
			String s = userAgent.substring(userAgent.indexOf("RV:") + 3);
			s = s.substring(0, s.indexOf("."));
			IEVer = Integer.parseInt(s);
		} else if(userAgent.indexOf("EDGE/") > 0) {
			String s = userAgent.substring(userAgent.indexOf("EDGE/") + 5);
			s = s.substring(0, s.indexOf("."));
			IEVer = Integer.parseInt(s);
		} else if(userAgent.indexOf("EDG/") > 0) {
			String s = userAgent.substring(userAgent.indexOf("EDG/") + 4);
			s = s.substring(0, s.indexOf("."));
			IEVer = Integer.parseInt(s);
		}
    }
%>
<c:set var="IEVersion"><%=IEVer%></c:set>

<c:choose>
	<c:when test="${IEVersion > 0 && IEVersion < 11}">
		<div id="iedisclaimer">
			<h1><wp:i18n key="DISCLAIMER" /></h1>
			<p><wp:i18n key="DISCLAIMER_NOTE" /></p>
		</div>
	</c:when>
	<c:otherwise>
		<!--[if lte IE 8]>
			<div id="iedisclaimer">
				<h1><wp:i18n key="DISCLAIMER" /></h1>
				<p><wp:i18n key="DISCLAIMER_NOTE" /></p>
			</div>
		<![endif]-->
	</c:otherwise>
</c:choose>
