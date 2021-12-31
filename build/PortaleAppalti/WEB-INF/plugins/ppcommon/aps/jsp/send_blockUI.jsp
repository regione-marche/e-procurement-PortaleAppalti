<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>

<wp:headInfo type="CSS" info="jquery/blockUI/jquery.blockUI.css" />
<script type="text/javascript" src='<wp:resourceURL/>static/js/jquery.blockUI.js'></script>
<script type="text/javascript">
	$(document).ready(function() { 
		$.blockUI.defaults.css = {};
			$('input.block-ui').on("click", function() {
					$.blockUI({ 
							message: $('#blockUImessage')
					});
			}); 
	});
</script>
<div id="blockUImessage" style="display:none">
<img src="<wp:resourceURL/>static/css/jquery/blockUI/images/wait_animation.gif" alt="<wp:i18n key="LABEL_WAITING"/>"/><wp:i18n key="LABEL_WAITING"/>
</div>