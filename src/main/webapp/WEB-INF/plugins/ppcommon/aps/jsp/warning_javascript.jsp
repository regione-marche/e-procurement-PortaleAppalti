<%@ taglib prefix="wp" uri="aps-core.tld" %>
<script type="text/javascript">
<!--//--><![CDATA[//><!--
	$(document).ready(function() {
		// se js abilitato rimuovo l'avviso che per essere usabile la pagina
		// serve js abilitato
		$('#noJs').remove();
	});
//--><!]]></script>
	
<div class="balloon errors" id="noJs">
	<div class="balloon-content balloon-info">
		<wp:i18n key="BALLOON_JAVASCRIPT_REQUIRED"/>
	</div>
</div>
