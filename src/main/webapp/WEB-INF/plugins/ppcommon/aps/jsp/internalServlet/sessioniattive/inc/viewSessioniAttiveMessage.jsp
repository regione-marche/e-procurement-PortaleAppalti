<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${not empty sessionScope.msgAdmin}">
	<div style="background-color: red; color: white; padding: 1em">
		${sessionScope.msgAdmin}
	</div>
</c:if>

				