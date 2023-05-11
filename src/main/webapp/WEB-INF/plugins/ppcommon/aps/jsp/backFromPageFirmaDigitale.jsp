<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>

<%-- 
  Parametri per il back-link della firma digitale "fromPageFirmaDigitale" 
  indica il link della pagina a cui ritornare dalla pagina delle informazioni 
  di firma digitale. 
  Viene utilizzata dalla action 'InfoFirmaDigitaleAction'
--%>

<%--
KO requestURI=${pageContext.request.requestURI}<br/> 
KO requestURL=${pageContext.request.requestURL}<br/>
KO servletPath=${pageContext.request.servletPath}<br/>

OK javax.servlet.forward.request_uri=${requestScope['javax.servlet.forward.request_uri']}<br/>
OK queryString=${pageContext.request.queryString}<br/>
--%>
 
<%-- <c:remove var="fromPageFirmaDigitale"/> --%>
<c:set var="fromPageFirmaDigitale" scope="session">
${requestScope['javax.servlet.forward.request_uri']}?${pageContext.request.queryString}
</c:set>
