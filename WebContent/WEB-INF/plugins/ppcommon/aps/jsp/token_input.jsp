<%-- TokenInterceptor Struts 2.0 --%>	
<%-- hidden input for <form> tag --%>
<%--
token1=${attr[strutsTokenName]}<br/>
token2=${requestScope[strutsTokenName]}<br/>
 --%>
<input type="hidden" name="${strutsTokenName}" value="${requestScope[strutsTokenName]}" />
