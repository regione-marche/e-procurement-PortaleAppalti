<%	
if ("logout".equals(request.getParameter("action"))) {
	session.invalidate();
	response.sendRedirect(request.getParameter("return"));
}	else {
	response.sendRedirect("error.htm");
}
%>
