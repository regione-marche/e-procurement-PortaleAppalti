<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<c:set var="hrefLogin"><wp:info key="systemParam" paramName="applicationBaseURL"/>do/adminfunlogin.action</c:set>
<c:set var="hrefInfo"><wp:info key="systemParam" paramName="applicationBaseURL"/>do/adminfuninfo.action</c:set>

<div class="portgare-view">
	<h2>
	Accesso esclusivo per utenti interni
	</h2>

	<p>
	Se non si è abilitati, uscire da questa pagina.
	</p>
	
	<form action="${hrefLogin}" method="post">
		<fieldset>
			<div class="fieldset-row first-row">
				<div class="label">
					<label for="username"><wp:i18n key="USERNAME" />: </label>
				</div>
				<div class="element">
					<s:textfield name="username" id="username" cssClass="text" />
				</div>
			</div>
			<div class="fieldset-row">
				<div class="label">
					<label for="password"><wp:i18n key="PASSWORD" />: </label>
				</div>
				<div class="element">
					<s:textfield name="password" id="password" cssClass="text" />
				</div>
			</div>
			<div class="azioni">
				<s:submit value="Entra" cssClass="button"/>
			</div>
			
			<div class="back-link">
				<a href="${hrefInfo}" >
					Istruzioni riservate agli amministratori
				</a>
			</div>
		</fieldset>
	</form>	
</div>