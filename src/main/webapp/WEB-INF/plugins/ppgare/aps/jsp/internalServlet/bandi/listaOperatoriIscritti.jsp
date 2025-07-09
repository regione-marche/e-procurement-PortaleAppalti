<%@ taglib prefix="wp"   uri="aps-core.tld" %>
<%@ taglib prefix="c"    uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wpsa" uri="apsadmin-core.tld" %>
<%@ taglib prefix="s"    uri="/struts-tags" %>
<%@ taglib prefix="es"   uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>

<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/jquery_treeview.jsp" />

<script type="text/javascript">
<!--//--><![CDATA[//><!--
$(document).ready(function() {
	
	$("#btnHide").on("click", function(){
	    $('[id^="title_"]').each(function() {
			$('ul[id^="tree_' + this.id + '"]').fadeOut('slow');
			$(this).attr('class', 'expand');
			$(this).attr('title', '<wp:i18n key="LABEL_LISTA_CATEGORIE_ESPANDI" />');
	    });
	});

	$("#btnShow").on("click", function(){
	    $('[id^="title_"]').each(function() {
			$('ul[id^="tree_' + this.id + '"]').fadeIn('slow');
			$(this).attr('class', 'collapse');
			$(this).attr('title', '<wp:i18n key="LABEL_LISTA_CATEGORIE_COMPRIMI" />');
	    });
	});

	// gestisci l'espansione/contrazione degli alberi in base al click		
	$('[id^="title_"]').on("click", function() {
		if ($(this).attr('class') == 'expand') {
			$('ul[id^="tree_' + this.id + '"]').fadeIn('slow');
			$(this).attr('class', 'collapse');
			$(this).attr('title', '<wp:i18n key="LABEL_LISTA_CATEGORIE_COMPRIMI" />');
		}
		else {
			$('ul[id^="tree_' + this.id + '"]').fadeOut('slow');
			$(this).attr('class', 'expand');
			$(this).attr('title', '<wp:i18n key="LABEL_LISTA_CATEGORIE_ESPANDI" />');
		}
	});

	// tutti gli alberi espansi (ma non visualizzati, e qui dipende dal display: none attribuito in creazione pagina)
	var collapseTree = false;
	var $treeview =  $(".filetree").treeview({collapsed: collapseTree});

	
//	var mercatoElettronico = $("#mercatoElettronico").val();
//	if (mercatoElettronico) {
//		$("span", $treeview).unbind("click.treeview");
//	}
//
//	// inserisci i contatori occorrenze sui nodi
//	$("span.folder").each(function(index) {
//		var count = $(this).parent().find("span.file").length;
//		$(this).append(" <em>[" + count + "]</em>");
//	});
	
});	
//--><!]]></script>


<!-- prepare il path per i pulsanti "Chiudi tutto" "Espandi tutto" -->
<c:set var="imgPath"><wp:resourceURL/>static/css/jquery/treeview/images/</c:set> 


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<es:checkCustomization var="visAlboIscritti" objectId="ALBO-ISCRITTI" attribute="CATEGORIE" feature="VIS" />
	
<div class="portgare-list">

	<h2><wp:i18n key="TITLE_PAGE_DETTAGLIO_OE_ABILITATI" /></h2>
	
	<c:set var="backLink" value=""/>
	<c:choose>
		<c:when test="${param.entita == 'Mercato elettronico'}">
			<c:set var="codiceBalloon">BALLOON_OPERATORI_ABILITATI_E_CAT_BANDO_ISCRIZIONE</c:set>
			<c:set var="backLink" value="Cataloghi"/>
		</c:when>		
		<c:otherwise>
			<c:set var="codiceBalloon">BALLOON_OPERATORI_ABILITATI_BANDO_ISCRIZIONE</c:set>
			<c:set var="backLink" value="Bandi"/>
		</c:otherwise>
	</c:choose>
	 
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="${codiceBalloon}"/>
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />

 	<form action="<wp:action path="/ExtStr2/do/FrontEnd/Bandi/viewOperatoriIscritti.action" />" method="post"> 
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
		
		<wpsa:subset source="operatoriIscritti" count="10" objectName="groupOperatori" advanced="true" offset="5">
	
			<s:set name="group" value="#groupOperatori" />
			<s:include value="/WEB-INF/plugins/ppcommon/aps/jsp/pager_info.jsp" />
			
			<s:if test="%{operatoriIscritti.size() > 0}" >
				<c:if test="${visAlboIscritti}" >
					<div class="align-right">
						<a title="<wp:i18n key="COLLAPSE_ALL" />" href="#" id="btnHide"><img src="${imgPath}minus.gif" alt="<wp:i18n key="COLLAPSE_ALL" />" /> <wp:i18n key="COLLAPSE_ALL" />
						</a>&nbsp;&nbsp;
						<a title="<wp:i18n key="EXPAND_ALL" />" href="#" id="btnShow"><img src="${imgPath}plus.gif" alt="<wp:i18n key="EXPAND_ALL" />"/> <wp:i18n key="EXPAND_ALL" />
						</a>
					</div>
				</c:if>
			</s:if>
			
			<s:iterator var="operatore">
				<div class="list-item">
					<div class="list-item-row">
						<label><wp:i18n key="LABEL_RAGIONE_SOCIALE" /> : </label>
						<s:property value="denominazione" />
					</div>
					
					<div class="list-item-row">
						<label><wp:i18n key="LABEL_CODICE_FISCALE" /> : </label>
						<s:property value="codiceFiscale" />
					</div>
				
					<div class="list-item-row">
						<label><wp:i18n key="LABEL_PARTITA_IVA" /> : </label>
						<s:property value="partitaIva" />
					</div>
					
					<div class="list-item-row">
						<label><wp:i18n key="LABEL_SEDE" /> : </label>
						<s:property value="localita" /> <c:if test="${! empty provincia}">(<s:property value="provincia" />)</c:if>
					</div>
		
					<c:if test="${visAlboIscritti}" >
						<div class="list-item-row">
							<label><wp:i18n key="LABEL_CATEGORIE" /> : </label>
							
							<!-- Visualizzazione albero categorie -->
							<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/bandi/inc/categorieOperatoreIscritto.jsp" >
								<jsp:param name="tipoClassifica" value="${tipoClassifica}"/>
							</jsp:include>
						</div>
					</c:if>
				</div>
			</s:iterator>

			<input type="hidden" name="codice" value="${param.codice}" /> 
			
			<s:include value="/WEB-INF/plugins/ppcommon/aps/jsp/pager_navigation.jsp" />
		</wpsa:subset>
 	</form>

	<div class="back-link">
		<a href="<wp:action path="/ExtStr2/do/FrontEnd/${backLink}/viewIscrizione.action" />&amp;codice=${param.codice}&amp;ext=${param.ext}">
			<wp:i18n key="LINK_BACK_TO_ISCRIZIONE" />
		</a>
	</div>
</div>

