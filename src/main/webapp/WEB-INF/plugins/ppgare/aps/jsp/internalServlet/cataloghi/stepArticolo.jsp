<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>

<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/jquery_treeview.jsp" />
<script type="text/javascript" src='<wp:resourceURL/>static/js/jquery.highlight.js'></script>

<script type="text/javascript">
<!--//--><![CDATA[//><!--
	$(document).ready(function() {
		
		$(".search").show();
		
		$(".divclearable").append('<a class="bkg reset" id="clearlink" href="javascript:" title="Clicca per eliminare i termini di ricerca"></a>');

		$.fn.reverse = [].reverse;

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

		// tutti gli alberi compressi
		$(".filetree").treeview({collapsed: true});

		//Funzione di sbiancamento della textbox di ricerca
		$("#clearlink").on("click", function() {
			$("#textsearch").val("");
			$('.filetree li').filter('.collapsable').reverse().each(function( index ) {
				var firstInner = $(this).children(":first");
				if (firstInner.is('div')) {
					firstInner.click();
				}
			});
			$('.filetree').unhighlight();
			$('.filetree ul li').unhighlight();
			$("span").filter(".found").each(function( index ) {
				$(this).removeClass('found');
			});
			$("#messaggioricerca").html("");
		});

		/*
		 * Ricerca nell'albero categorie/aricoli/colori.
		 */
		function searchCategorie() {
			$('.filetree li').filter('.collapsable').reverse().each(function( index ) {
				var firstInner = $(this).children(":first");
				if (firstInner.is('div')) {
					firstInner.click();
				}
			});
			$('.filetree').unhighlight();
			$('.filetree ul li').unhighlight();
			$("span").filter(".found").each(function( index ) {
				$(this).removeClass('found');
			});
			var visualizzaRisultato = false;
			$(".filetree").find("span").each(function(index) {
				var word, words = [];
				var searchFor = $("#textsearch").val();
				searchFor = searchFor.toLowerCase().replace(/^\s+/g,'').replace(/\s+$/g,'');
				if (searchFor.indexOf(' ') >= 0) {
					words = searchFor.split(' ');
				}
				else {
					words = [searchFor];
				}
				for (var i = 0; i < words.length; i++) {
					word = words[i];
					if (word != "" && word != " " && word.length > 1) {
						visualizzaRisultato = true;
						if (($(this).text() || "").toLowerCase().indexOf(word) >= 0) {
							$(this).parents('li').filter('.expandable').each(function( index ) {
								var firstInner = $(this).children(":first");
								if (firstInner.is('div')) {
									firstInner.click();
								}
							});
							$(this).highlight(word);
							if (!$(this).hasClass("found")) {
								$(this).addClass('found');
							}
						}
					}
				}
				searchMessaggio(visualizzaRisultato);
			});
		}
		
		//espando l'albero fino a raggiungere l'articolo selezionato
		$("input[name=articoloSelezionato]").filter(":checked").parents('li').reverse().each(function( index ) {
			var firstInner = $(this).children(":first");
			if (firstInner.is('div')) {
				firstInner.click();
			}
		});
		
		//espando l'albero fino a raggiungere la categoria precedentemente utilizzata
		$("span").filter(".last-selected").parents('li').reverse().each(function( index ) {
			var firstInner = $(this).children(":first");
			if (firstInner.is('div')) {
				firstInner.click(); 
			}
		});
		
		//Associo alla textbox la funzione di ricerca
		$("#textsearch").keyup(function() {
			searchCategorie();
		});
		
		/*
     * Conteggio elementi trovati
     */
    function searchMessaggio(show) {
			if (!show) {
				$("#messaggioricerca").html("");
				return;
			}
    	var numero = $("span.found").length;
			if (numero) {
				if (numero == 0) {
					$("#messaggioricerca").html("Nessun elemento trovato.");
				} else if (numero == 1) {
					$("#messaggioricerca").html("Trovato 1 elemento.");
				} else {
					$("#messaggioricerca").html("Trovati " +  numero + " elementi.");
				}
			} else {
				$("#messaggioricerca").html("Nessun elemento trovato.");
			}
    }
	});
//--><!]]>
</script>


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2><wp:i18n key="TITLE_PAGE_STEP_INSERISCI_PRODOTTO_CATALOGO" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/field_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />

	<jsp:include page="stepsProdotto.jsp" />

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_WIZ_PRODOTTO_ARTICOLO"/>
	</jsp:include>

	<p class="noscreen">[ <a href="#" ><wp:i18n key="LABEL_SALTA_FORM_ARTICOLO_VAI_A_BOTTONI" /></a> ]</p>
	
	<div class="search" style="display: none">
		<span id="labelricerca"><wp:i18n key="LABEL_FILTRA_CATEGORIE_ARTICOLI_COLORE_PER" /></span>
		<div class="divclearable"><input type="text" name="filtro" id="textsearch"  aria-label="<wp:i18n key='LABEL_FILTRA_CATEGORIE_ARTICOLI_COLORE_PER'/>" /></div>
		<span id="messaggioricerca"></span>
	</div>

	<form action="<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/processPageSelezioneArticolo.action" />" method="post">
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
		
		<fieldset>
			<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_CATEGORIE_ARTICOLI_DEL_CATALOGO" /></legend>

			<s:if test="%{categorie.size() > 0}">

				<s:set var="lastTipoAppalto" value="%{categorie[0].tipoAppalto}"/>
				<s:set var="lastTitolo" value="%{categorie[0].titolo}"/>
				<s:set var="lastLivello" value="%{categorie[0].livello}"/>

				<s:iterator var="categoria" value="categorie" status="status">
					
					<s:set var="nuovoAlbero" value="%{false}"/>
					
					<%-- primo albero --%>
					<s:if test="%{#status.index==0}">
						<s:iterator value="maps['tipiAppaltoIscrAlbo']">
							<s:if test="%{key == #lastTipoAppalto}">
								<s:set var="labelTipoAppalto" value="%{value}"/>
							</s:if>
						</s:iterator>
						<div class="tree-root">
							<a class="collapse" href="#" id='title_<s:property value="%{#status.index}"/>' title="<wp:i18n key="LABEL_LISTA_CATEGORIE_COMPRIMI" />">
								<span>
									<s:property value="%{#labelTipoAppalto}"/>
									<s:if test="%{#lastTitolo != null}"> - <s:property value="%{#lastTitolo}"/></s:if>
								</span>
							</a>
						</div>
						<ul class="filetree" id="tree_title_<s:property value="%{#status.index}"/>">	
						<s:set var="nuovoAlbero" value="%{true}"/>
					</s:if><%-- fine primo albero --%>
					
					<%-- nuovo albero --%>
					<s:elseif test="%{(#lastTipoAppalto != #categoria.tipoAppalto) || (#lastTitolo != #categoria.titolo)}">						
						<s:iterator value="maps['tipiAppaltoIscrAlbo']">
							<s:if test="%{key == tipoAppalto}">
								<s:set var="labelTipoAppalto" value="%{value}"/>
							</s:if>
						</s:iterator>
						<c:forEach begin="1" end="${lastLivello}">
								</li></ul>
						</c:forEach>
						<div class="tree-root">
							<a class="collapse" href="#" id="title_<s:property value="%{#status.index}"/>" title="<wp:i18n key="LABEL_LISTA_CATEGORIE_COMPRIMI" />">
								<span>
									<s:property value="%{#labelTipoAppalto}"/>
									<s:if test="%{titolo != null}"> - <s:property value="%{titolo}"/></s:if>
								</span>
							</a>
						</div>
						<ul class="filetree" id="tree_title_<s:property value="%{#status.index}"/>">
						<s:set var="nuovoAlbero" value="%{true}"/>
					</s:elseif><%-- fine nuovo albero --%>

					<%-- stampa del nodo/foglia (aprendo un nuovo livello o chiudendo i precedenti, se necessario) --%>
					<c:choose>
						<c:when test="${livello gt lastLivello}">
							<ul>
								<li id="_<s:property value="codice" />">
						</c:when>
						<c:when test="${livello lt lastLivello}">
							<s:if test="%{!#nuovoAlbero}">
								<c:forEach begin="${livello}" end="${lastLivello-1}">
										</li></ul>
								</c:forEach>
								</li>
							</s:if>
								<li id="_<s:property value="codice" />">
						</c:when>
						<c:otherwise>
							<s:if test="%{!#nuovoAlbero}">
								</li>
							</s:if>
							<li id="_<s:property value="codice" />">
						</c:otherwise>
					</c:choose>
								
					<span class="folder <s:if test='%{ultimaCategoria == codice}'>last-selected</s:if>"><s:property value="codice" /> - <s:property value="descrizione" /></span>
					
					<s:if test="%{#categoria.articoli.length > 0}">
						<ul>
					</s:if>
						<s:iterator var="articolo" value="#categoria.articoli" status="statusArticolo">
							<li class="articolo">
								<div class="item-container">
									<c:set var="lblArticolo"><s:property value="#articolo.codice" /> <s:property value="#articolo.descrizione" /></c:set>
									<input type="radio" name="articoloSelezionato" id="articoloSelezionato<s:property value='%{#articolo.id}'/>" value="<s:property value='%{#articolo.id}' />"
											 <s:if test="%{articoloSelezionato != null && articoloSelezionato == #articolo.id}"> checked="true"</s:if>
											 aria-label="${lblArticolo}" />
									<span>
										<s:property value="#articolo.codice" />&nbsp;-&nbsp;<s:property value="#articolo.descrizione" />
										<s:if test="%{#articolo.colore != null && #articolo.colore != ''}">&nbsp;-&nbsp;<s:property value="#articolo.colore" /></s:if>
									</span>
								</div>
							</li>
						</s:iterator>
					<s:if test="%{#categoria.articoli.length > 0}">
						</ul>
					</s:if>

					<%-- serve sempre, per controllare le categorie gestite --%>
					<s:hidden name="codiceCategoria" value="%{codice}" id="codiceCategoria%{codice}"/>

					<%-- chiusura dei sottolivelli dell'ultimo albero --%>
					<s:if test="%{#status.last}">
						<c:forEach begin="1" end="${livello}"></li></ul></c:forEach>
					</s:if>

					<s:set var="lastTipoAppalto" value="%{#categoria.tipoAppalto}"/>
					<s:set var="lastTitolo" value="%{#categoria.titolo}"/>
					<s:set var="lastLivello" value="%{#categoria.livello}"/>
				</s:iterator>
			</s:if>
			<s:else>
				<wp:i18n key="LABEL_NESSUNA_CATEGORIA_PRESTAZIONE" />
			</s:else>
		</fieldset>
			
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/skip_main_content_from_buttons.jsp" />

		<div class="azioni">
			<input type="hidden" name="page" value="articolo"/>
			<input type="hidden" name="ext" value="${param.ext}" />
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_next.jsp" />
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_cancel.jsp" />
		</div>
	</form>
</div>