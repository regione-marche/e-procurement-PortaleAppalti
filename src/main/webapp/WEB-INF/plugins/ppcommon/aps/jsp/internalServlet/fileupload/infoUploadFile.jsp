<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>


<script>
<!--//--><![CDATA[//><!--
$(document).ready(function () {	

	var link = $('[id="linkEstensioni"]');
	var note = $('#noteEstensioni');
	
	viewNote();
	link.click(function() {
		viewNote();
	});

	function viewNote() {
		if (note.hasClass('visible')) {
			note.hide();
			note.removeClass('visible');
			link.text("<wp:i18n key='LABEL_VISUALIZZA_ESTENSIONI_FILE'/>");	
		} else {
			note.show();
			note.addClass('visible');
			link.text("<wp:i18n key='LABEL_NASCONDI_ESTENSIONI_FILE'/>");
		}
	}
	
});	
//--><!]]>
</script>


<s:set var="dimensioneAttualeFileCaricati">${param.dimensioneAttualeFileCaricati}</s:set>
<s:set var="kbCaricati" value="%{dimensioneAttualeFileCaricati}"></s:set>
<s:set var="kbDisponibili" value="%{limiteTotaleUpload - dimensioneAttualeFileCaricati}"></s:set>

<wp:i18n key="LABEL_MAX_FILE_SIZE" /> <strong><s:property value="%{limiteUploadFile}" /></strong> KB.<br/>
<c:if test="${dimensioneAttualeFileCaricati != null && ! empty param.dimensioneAttualeFileCaricati}">
	<wp:i18n key="LABEL_MAX_REQUEST_SIZE_1" /> <strong><s:property value="%{#kbCaricati}" /></strong> KB,
	<wp:i18n key="LABEL_MAX_REQUEST_SIZE_2" /> <strong><s:property value="%{#kbDisponibili}" /></strong> KB.
	<br/>
</c:if>
<a href="javascript:;" onclick="parentNode.submit();" id="linkEstensioni" ><wp:i18n key='LABEL_VISUALIZZA_ESTENSIONI_FILE'/></a>
<div id="noteEstensioni" class="note visible">
	<ul>
	<s:iterator var="info" value="%{infoEstensioniAmmesse}">
		<li>
			<s:property value="%{#info}" />
		</li>
	</s:iterator>
	</ul>
</div>

