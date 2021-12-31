<%@ taglib prefix="s" uri="/struts-tags" %>
<h1><s:text name="title.changePassword" /></h1>
<div class="message message_confirm">
<h2><s:text name="messages.confirm" /></h2>	
<p><s:text name="message.passwordChanged" /></p>
</div>


<br /><br /><br />
<s:if test="!#session.currentUser.credentialsNotExpired">
<a href="<s:url action="logout" />" >E' NECESSARIO EFFETTUARE NUOVAMENTE IL LOGIN</a>
<br /><br />

</s:if>