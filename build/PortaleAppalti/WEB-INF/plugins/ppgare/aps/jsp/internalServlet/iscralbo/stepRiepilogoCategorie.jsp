<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>

<script type="text/javascript" src='<wp:resourceURL/>static/js/jquery.treeview.js'></script>
<script type="text/javascript">
<!--//--><![CDATA[//><!--
$(document).ready(function(){
	// gestisci l'espansione/contrazione degli alberi in base al click
	$('[id^="title_"]').on("click", function(){
	    if ($(this).attr('class') == 'expand') {
		    $('ul[id^="tree_'+this.id+'"]').fadeIn('slow');
	    	$(this).attr('class', 'collapse');
	    	$(this).attr('title', '<wp:i18n key="LABEL_LISTA_CATEGORIE_COMPRIMI"/>');
	    }
	    else {
		    $('ul[id^="tree_'+this.id+'"]').fadeOut('slow');
	    	$(this).attr('class', 'expand');
	    	$(this).attr('title', '<wp:i18n key="LABEL_LISTA_CATEGORIE_ESPANDI"/>');
	    }
	});
	// tutti gli alberi compressi
	$(".filetree").treeview({collapsed: false});
	// inserisci i contatori occorrenze sui nodi
	$("span.folder").each(function( index ) {
		var count = $(this).parent().find("span.file").length;
		$(this).append(" <em>[" + count + "]</em>");
	});
});
//--><!]]></script>

<wp:headInfo type="CSS" info="jquery/treeview/jquery.treeview.css" />


<s:if test="%{#session.dettIscrAlbo.tipologia == 2}">
	<c:set var="titolo"><wp:i18n key='TITLE_PAGE_ISCRALBO_MERCATO_ELETTRONICO'/></c:set>
	<c:set var="codiceBalloon" value="BALLOON_ISCR_CATALOGO_RIEP_CATEGORIE"/>
</s:if>
<s:else>
	<c:set var="titolo"><wp:i18n key='TITLE_PAGE_ISCRALBO_ELENCO_OE'/></c:set>
	<c:set var="codiceBalloon" value="BALLOON_ISCR_ALBO_RIEP_CATEGORIE"/>
</s:else>


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<s:if test="%{!#session.dettIscrAlbo.aggiornamentoIscrizione}">
		<h2><wp:i18n key='TITLE_PAGE_ISCRALBO_RICHIESTA_ISCRIZIONE'/> <c:out value="${titolo}"/></h2>
	</s:if>
	<s:else>
		<h2><wp:i18n key='TITLE_PAGE_ISCRALBO_AGGIORNAMENTO_ISCRIZIONE'/> <c:out value="${titolo}"/></h2>
	</s:else>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/field_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_messages.jsp" />

	<jsp:include page="stepsIscrizione.jsp">
		<jsp:param name="sessionIdObj" value="dettIscrAlbo" />
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="${codiceBalloon}"/>
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/skip_form_buttons.jsp" />

	<form action="<wp:action path="/ExtStr2/do/FrontEnd/IscrAlbo/processPageIscrAlboRiepilogoCategorie.action" />" method="post">
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
		
		<fieldset>
			<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key='LABEL_ISCRALBO_CATEGORIE_SELEZIONATE_OE' /></legend>
			
		 	<%-- Visualizzazione albero categorie --%>
		 	<c:set var="titolo"><wp:i18n key='TITLE_PAGE_ISCRALBO_MERCATO_ELETTRONICO'/></c:set>
			<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/bandi/inc/categorieIscrizioneOperatore.jsp" >			
				<jsp:param name="tipoclassifica" value="${session.dettIscrAlbo.tipoClassifica}"/>
			</jsp:include>
		</fieldset>

		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/skip_main_content_from_buttons.jsp" />
		
		<div class="azioni">
			<input type="hidden" name="page" value="riepcate"/>
			<input type="hidden" name="ext" value="${param.ext}" />
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_previous.jsp" />
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_next.jsp" />
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_cancel.jsp" />
		</div>
	</form>
</div>