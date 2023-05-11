<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>

<%--
12/07/2019: non viene piu' utilizzata
<es:checkCustomization var="obblAClassifica" objectId="ISCRALBO-CATEG" attribute="ACLASSIFICA" feature="MAN" />
 --%>
<es:checkCustomization var="obblNotaNonLavori" objectId="ISCRALBO-CATEG" attribute="NOTA" feature="MAN" />

<%-- escape di "catErrata" --%>
<c:set var="categoriaErrata" value=""/>
<s:iterator value="catErrata" var="cat" status="stat">
	<c:set var="value"><s:property value='%{#cat}' /></c:set>
	<c:set var="categoriaErrata">${categoriaErrata}<s:if test="%{#stat.index > 0}">|</s:if>${fn:replace(value, '.', '\\\\.')}</c:set> 
</s:iterator>


<script type="text/javascript" src='<wp:resourceURL/>static/js/jquery.treeview.js'></script>
<wp:headInfo type="CSS" info="jquery/treeview/jquery.treeview.css" />
<script type="text/javascript">	
<!--//--><![CDATA[//><!--
$(document).ready(function(){
	
	$.fn.reverse = [].reverse;
	
	// gestisci l'espansione/contrazione degli alberi in base al click
	$('[id^="title_"]').on("click", function(){
	    if ($(this).attr('class') == 'expand') {
		    $('ul[id^="tree_'+this.id+'"]').fadeIn('slow');
	    	$(this).attr('class', 'collapse');
	    	$(this).attr('title', '<wp:i18n key="LABEL_LISTA_CATEGORIE_COMPRIMI" />');
	    }
	    else {
		    $('ul[id^="tree_'+this.id+'"]').fadeOut('slow');
	    	$(this).attr('class', 'expand');
	    	$(this).attr('title', '<wp:i18n key="LABEL_LISTA_CATEGORIE_ESPANDI" />');
	    }
	});
	
	// gestisci l'espansione/contrazione delle note
	$('[id^="funznota"]').on("click", function(){
		var cat = $(this).attr('id').substring(8);
	    if ($(this).hasClass('expand')) {
		    $('textarea[id="nota'+cat+'"]').fadeIn('slow');
	    	$(this).removeClass('expand');
	    	$(this).addClass('collapse');
	    	<c:if test="${skin == 'highcontrast' || skin == 'text'}">$(this).text('(<wp:i18n key="LABEL_NOTA_CATEGORIA_COMPRIMI" />)');</c:if>
	    	$(this).attr('title', '<wp:i18n key="LABEL_NOTA_CATEGORIA_COMPRIMI" />');
	    }
	    else {
		    $('textarea[id="nota'+cat+'"]').fadeOut('slow');
	    	$(this).removeClass('collapse');
	    	$(this).addClass('expand');
	    	<c:if test="${skin == 'highcontrast' || skin == 'text'}">$(this).text('(<wp:i18n key="LABEL_NOTA_CATEGORIA_ESPANDI" />)');</c:if>
	    	$(this).attr('title', '<wp:i18n key="LABEL_NOTA_CATEGORIA_ESPANDI" />');
	    }
	});
	
	// tutti gli alberi compressi
	$(".filetree").treeview({collapsed: true});
	
	// tutte le textarea delle note vanno nascoste
	$('textarea[id^="nota"]').hide();
	
	// inserisci i contatori occorrenze sui nodi
	$("span.folder").each(function( index ) {
		var count = $(this).parent().find("span.item").length;
		var selected = $(this).parent().find('input:checkbox:checked').length;
		if (selected > 0) {
			$(this).addClass('folder-selected');
		} else {
			$(this).removeClass('folder-selected');
		}
		$(this).append(" <em>[" + selected + " selezionati su " + count + "]</em>");
	});
	
	// evidenzia lo sfondo di tutte le righe di categorie selezionate
	$("div.item-container").each(function( index ) {
		var selected = $(this).children().find('input:checkbox:checked').length;
		if (selected > 0) {
			$(this).addClass('item-selected');
		}
	});
	
	// simulo il click su ogni elemento per espandere l'albero fino all'elemento selezionato
	$(':checkbox').filter(":checked").parents('li').reverse().each(function( index ) {
		var firstInner = $(this).children(":first");
		if (firstInner.is('div')) {
			firstInner.click(); 
		}
	});		
	
	// per le foglie con errori si espande il corrispondente albero
	<c:forTokens items="${categoriaErrata}" delims="|" var="cat">
	$(':checkbox[value=${cat}]').parents('li').reverse().each(function( index ) {
		<%-- simulo il click su ogni elemento per espandere l'albero fino all'elemento errato --%>
		var firstInner = $(this).children(":first");
		if (firstInner.is('div') && firstInner.hasClass('expandable-hitarea')) { firstInner.click(); }
	});
	$(':checkbox[value=${cat}]').each(function( index ) {	
		<%-- per ogni nodo con errori apro anche la nota così correggo gli eventuali errori --%>		
		$('#funznota${cat}').click();
	});		
	</c:forTokens>
	
	// ad ogni click su un check aggiorna i contatori su tutti i nodi superiori e cambia lo sfondo dell'elemento
	$(':checkbox').on("click", function(){
		var cat = $(this).attr('id').substring(14);
		if (!$(this).is(':checked')) {
			// nel caso di eliminazione spunta si eliminano le classifiche selezionate
			$('#classeDa'+cat).val('');
			$('#classeA'+cat).val('');
			$(this).parent().parent().removeClass('item-selected');
			// inoltre si elimina la nota e si chiude la nota se espanza
			$('#nota'+cat).val('');
			if ($("#funznota"+cat).hasClass('collapse')) {
				$("#funznota"+cat).click();
			}
		} else {
			$(this).parent().parent().addClass('item-selected');
			<c:if test="${obblNotaNonLavori}">
			// si predispone l'apertura della nota se obbligatoria
			if (!$(this).hasClass('cat-lavori') && $("#funznota"+cat).hasClass('expand')) {
				$("#funznota"+cat).click();
			}
			</c:if>
			
		}
		// per ogni nodo sopra la foglia si riaggiornano i contatori
		$(this).parents('li').last().find("span.folder").each(function( index ) {
			var count = $(this).parent().find("span.item").length;
			var selected = $(this).parent().find('input:checked').length;
			if (selected > 0) {$(this).addClass('folder-selected');} else {$(this).removeClass('folder-selected');}
			$(this).children('em').html("[" + selected + " selezionati su " + count + "]");
		});
	});
});
//--><!]]></script>


<s:if test="%{#session.dettIscrAlbo.tipologia == 2}">
	<c:set var="titolo"><wp:i18n key='TITLE_PAGE_ISCRALBO_MERCATO_ELETTRONICO'/></c:set>
	<c:set var="titoloFieldset"><wp:i18n key='LABEL_ISCRALBO_CATEGORIE_CATALOGO_ELETTRONICO'/></c:set>
	<c:set var="codiceBalloon" value="BALLOON_ISCR_CATALOGO_CATEGORIE"/>
</s:if>
<s:else>
	<c:set var="titolo"><wp:i18n key='TITLE_PAGE_ISCRALBO_ELENCO_OE'/></c:set>
	<c:set var="titoloFieldset"><wp:i18n key='LABEL_ISCRALBO_CATEGORIE_ELENCO_OE'/></c:set>
	<c:set var="codiceBalloon" value="BALLOON_ISCR_ALBO_CATEGORIE"/>
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

<%-- 
	<form action="<wp:action path="/ExtStr2/do/FrontEnd/IscrAlbo/processPageCategorie.action" />" method="post"> 
--%>
	<form action="<wp:action path="/ExtStr2/do/FrontEnd/IscrAlbo/processPageIscrAlboCategorie.action" />" method="post">
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
		
		<s:if test="%{#session.dettIscrAlbo.richiestaCoordinatoreSicurezza}">
			<s:set var="requisitiCoordinatoreSicurezzaValue" value=""/>
			<s:if test="%{#session.dettIscrAlbo.requisitiCoordinatoreSicurezza != null}">
				<s:set var="requisitiCoordinatoreSicurezzaValue" value="%{#session.dettIscrAlbo.requisitiCoordinatoreSicurezza ? 1 : 0}"/>
			</s:if>
		
			<fieldset>
				<label for="requisitiCoordinatoreSicurezza"><wp:i18n key='LABEL_POSSESSO_REQUISITI_COORDINATORE_SICUREZZA'/>? : <span class="required-field">*</span></label>
				<s:select list="maps['sino']" name="requisitiCoordinatoreSicurezza" id="requisitiCoordinatoreSicurezza"
							headerKey="" headerValue=""
							value="%{#requisitiCoordinatoreSicurezzaValue}" 
							aria-required="true" >
				</s:select>
			</fieldset>
		</s:if>
		
		<s:if test="%{#session.dettIscrAlbo.richiestaAscesaTorre}">
			<s:set var="requisitiAscesaTorreValue" value=""/>
			<s:if test="%{#session.dettIscrAlbo.requisitiAscesaTorre != null}">
				<s:set var="requisitiAscesaTorreValue" value="%{#session.dettIscrAlbo.requisitiAscesaTorre ? 1 : 0}"/>
			</s:if>
		
			<fieldset>
				<label for="requisitiAscesaTorre"><wp:i18n key='LABEL_REQUISITI_ASCESA_TORRE'/>? : <span class="required-field">*</span></label>
				<s:select list="maps['sino']" name="requisitiAscesaTorre" id="requisitiAscesaTorre"
							headerKey="" headerValue=""
							value="%{#requisitiAscesaTorreValue}" 
							aria-required="true" >
				</s:select>
			</fieldset>
		</s:if>
		
		<fieldset>
			<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><c:out value="${titoloFieldset}"/></legend>

			<div class="search">
				<wp:i18n key="LABEL_FILTRA_CATEGORIE_PER"/> <input type="text" name="filtroCategorieNew" id="filtroCategorieNew" value='<s:property value="filtroCategorie" />' title="<wp:i18n key="LABEL_FILTRA_CATEGORIE_PER"/>" />
				<wp:i18n key="BUTTON_WIZARD_FILTER" var="valueFilterButton" />
				<s:submit value="%{#attr.valueFilterButton}" cssClass="button" method="filter" />
				<s:hidden name="filtroCategorie" />
			</div>

			<s:if test="%{categorieBando.size() > 0}">

				<s:set var="lastTipoAppalto" value="%{categorieBando[0].tipoAppalto}"/>
				<s:set var="lastTitolo" value="%{categorieBando[0].titolo}"/>
				<s:set var="lastLivello" value="%{categorieBando[0].livello}"/>

				<s:iterator var="categoria" value="categorieBando" status="status">
					
					<s:set var="nuovoAlbero" value="%{false}"/>

					<%-- primo albero --%>
					<s:if test="%{#status.index==0}">
						<s:iterator value="maps['tipiAppaltoIscrAlbo']">
							<s:if test="%{key == #lastTipoAppalto}">
								<s:set var="labelTipoAppalto" value="%{value}"/>
							</s:if>
						</s:iterator>
						<div class="tree-root">
							<a class="collapse" href="#" id="title_<s:property value="%{#status.index}"/>" title="<wp:i18n key="LABEL_LISTA_CATEGORIE_COMPRIMI" />">
								<span class="folder-selected">
									<s:property value="%{#labelTipoAppalto}"/>
									<s:if test="%{#lastTitolo != null}"> - <s:property value="%{#lastTitolo}"/></s:if>
								</span>
							</a>
						</div>
						<ul class="filetree" id="tree_title_<s:property value="%{#status.index}"/>">
						<s:set var="nuovoAlbero" value="%{true}"/>
					</s:if>
					<%-- fine primo albero --%>

					<%-- nuovo albero --%>
					<s:elseif test="%{(#lastTipoAppalto != #categoria.tipoAppalto) || (#lastTitolo != #categoria.titolo)}">
						<s:iterator value="maps['tipiAppaltoIscrAlbo']">
							<s:if test="%{key == tipoAppalto}">	    <!-- #categoria.tipoAppalto -->
								<s:set var="labelTipoAppalto" value="%{value}"/>
							</s:if>
						</s:iterator>
						<c:forEach begin="1" end="${lastLivello}">
							</li></ul>
						</c:forEach>
						<div class="tree-root">
							<a class="collapse" href="#" id="title_<s:property value="%{#status.index}"/>" title="<wp:i18n key="LABEL_LISTA_CATEGORIE_COMPRIMI" />">
								<span class="folder-selected">
									<s:property value="%{#labelTipoAppalto}"/>
									<s:if test="%{titolo != null}"> - <s:property value="%{titolo}"/></s:if>
								</span>
							</a>
						</div>
						<ul class="filetree" id="tree_title_<s:property value="%{#status.index}"/>">
						<s:set var="nuovoAlbero" value="%{true}"/>
					</s:elseif>
					<%-- fine nuovo albero --%>

					<%--stampa del nodo/foglia (aprendo un nuovo livello o chiudendo i precedenti, se necessario), con eventuale checkbox di selezione --%>
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
				
					<s:if test="foglia">
						<div class="item-container">
							<div class="item-check">
								<s:if test="%{#session.dettIscrAlbo.categorie.categorieBloccate.contains(codice)}">
									<s:checkbox name="descCatSelezionata" fieldValue="%{codice}" id="descCatSelezionata%{codice}" 
															value="%{checkCategoria[#status.index]}" disabled="true" title="%{codice} - %{descrizione}"> </s:checkbox>
									<s:hidden name="catSelezionata" value="%{codice}" id="catSelezionata%{codice}"></s:hidden>
								</s:if>
								<s:else>
									<s:checkbox name="catSelezionata" fieldValue="%{codice}" id="catSelezionata%{codice}" 
															value="%{checkCategoria[#status.index]}" cssClass="%{'Lavori'.equals(#labelTipoAppalto) ? 'cat-lavori' : ''}"
															title="%{codice} - %{descrizione}"></s:checkbox>
								</s:else>
								<span class="item"><s:property value="codice" /> - <s:property value="descrizione" /></span>
								<span id="funznota${codice}" class="bkg vertical-middle expand" title="<wp:i18n key="LABEL_NOTA_CATEGORIA_ESPANDI" />" ><c:if test="${skin == 'highcontrast' || skin == 'text'}">(Visualizza la nota)</c:if></span>
							</div>
					</s:if>
					<s:else>
						<span class="folder"><s:property value="codice" /> - <s:property value="descrizione" /></span>
					</s:else>

					<s:set var="separatore" value="%{false}"/>
					
					<%-- se si tratta di foglia, si inseriscono anche le select per le classifiche --%>
					<s:if test="foglia">
						<%-- <s:if test="%{#session.dettIscrAlbo.tipologia != 2 && #session.dettIscrAlbo.tipoClassifica != 3}"> --%>
						<s:if test="%{(#session.dettIscrAlbo.tipoClassifica == 1 || #session.dettIscrAlbo.tipoClassifica == 2)}">
							<div class="item-classifica">
								<%-- si visualizza il dalla classifica --%>
								<s:if test="%{#session.dettIscrAlbo.tipoClassifica == 1}">
									
									<s:set var="discrClassifica" value=''></s:set>
									<s:if test="%{tipoAppalto == 1}">
										<s:set var="discrClassifica" value='%{"classifLavoriIscrAlbo"}'></s:set>
									</s:if>
									<s:elseif test="%{tipoAppalto == 2 && maps['classifForniture'].size() > 0}">
										<s:set var="discrClassifica" value='%{"classifForniture"}'></s:set>
									</s:elseif>
									<s:elseif test="%{tipoAppalto == 3 && maps['classifServizi'].size() > 0}">
										<s:set var="discrClassifica" value='%{"classifServizi"}'></s:set>
									</s:elseif>
									<s:elseif test="%{tipoAppalto == 4 && maps['classifLavoriSottoSoglia'].size() > 0}">
										<s:set var="discrClassifica" value='%{"classifLavoriSottoSoglia"}'></s:set>
									</s:elseif>
									<s:elseif test="%{tipoAppalto == 5 && maps['classifServiziProf'].size() > 0}">
										<s:set var="discrClassifica" value='%{"classifServiziProf"}'></s:set>
									</s:elseif>
									
									<s:if test="%{#discrClassifica.length()>0}">
										<s:set var="separatore" value="%{true}"/>
										<span><wp:i18n key="LABEL_DA_CLASSIFICA" /></span>
										<s:if test="%{#session.dettIscrAlbo.categorie.categorieBloccate.contains(codice)}">
											<s:iterator value="maps[#discrClassifica]">
												<s:if test="%{key == classeDa[#status.index]}"><s:textfield name="descClasseDa" id="descClasseDa" value="%{value}" size="5" readonly="true" cssClass="no-editable" /></s:if>
											</s:iterator>
											<s:hidden name="classeDa" value="%{classeDa[#status.index]}" id="classeDa%{codice}"></s:hidden>
										</s:if>
										<s:else>
											<c:set var="lblClasseDa"><s:property value="%{codice}"/> <s:property value="%{descrizione}"/></c:set>
											<s:select list="maps[#discrClassifica]" id="classeDa%{codice}" name="classeDa" headerKey="" headerValue="" 
																listKey="key" listValue="value" value="%{classeDa[#status.index]}" 
																aria-label="${lblClasseDa}" >
											</s:select>
										</s:else>
									</s:if>
									<s:else>
										<input type="hidden" name="classeDa" value=""/>
									</s:else>
								</s:if>

								<%-- si inserisce comunque il campo hidden nella colonna esistente (Da classifica) per la validazione W3C e per la logica --%>
								<s:if test="%{#session.dettIscrAlbo.tipoClassifica == 2}">
									<s:hidden name="classeDa" value="" id="classeDa%{codice}" ></s:hidden>
								</s:if>

								<%-- si visualizza [fino alla] classifica --%>
								<s:set var="discrClassifica" value=''></s:set>
								<s:if test="%{tipoAppalto == 1}">
									<s:set var="discrClassifica" value='%{"classifLavoriIscrAlbo"}'></s:set>
								</s:if>
								<s:elseif test="%{tipoAppalto == 2 && maps['classifForniture'].size() > 0}">
									<s:set var="discrClassifica" value='%{"classifForniture"}'></s:set>
								</s:elseif>
								<s:elseif test="%{tipoAppalto == 3 && maps['classifServizi'].size() > 0}">
									<s:set var="discrClassifica" value='%{"classifServizi"}'></s:set>
								</s:elseif>
								<s:elseif test="%{tipoAppalto == 4 && maps['classifLavoriSottoSoglia'].size() > 0}">
									<s:set var="discrClassifica" value='%{"classifLavoriSottoSoglia"}'></s:set>
								</s:elseif>
								<s:elseif test="%{tipoAppalto == 5 && maps['classifServiziProf'].size() > 0}">
									<s:set var="discrClassifica" value='%{"classifServiziProf"}'></s:set>
								</s:elseif>

								<s:if test="%{#discrClassifica.length()>0}">
									<span>
										<s:if test="%{#session.dettIscrAlbo.tipoClassifica == 1}">
											<wp:i18n key="LABEL_A_CLASSIFICA" />
										</s:if>
										<s:else>
											<wp:i18n key="LABEL_CLASSIFICA" />
										</s:else>
									</span>
									<s:if test="%{#session.dettIscrAlbo.categorie.categorieBloccate.contains(codice)}">
										<s:iterator value="maps[#discrClassifica]">
											<s:if test="%{key == classeA[#status.index]}">
												<s:textfield name="descClasseA" id="descClasseA" value="%{value}" size="5" readonly="true" cssClass="no-editable" />
											</s:if>
										</s:iterator>
										<s:hidden name="classeA" value="%{classeA[#status.index]}" id="classeA%{codice}"></s:hidden>
										<!-- <input type="hidden" name="obblAClassifica" value="false"/>  -->
										<input type="hidden" name="obblNota" value="false"/>
									</s:if>
									<s:else>
										<c:set var="lblClasseA"><s:property value="%{codice}"/> <s:property value="%{descrizione}"/></c:set>
										<s:select list="maps[#discrClassifica]" id="classeA%{codice}" name="classeA" headerKey="" headerValue="" 
															listKey="key" listValue="value" value="%{classeA[#status.index]}" 
															aria-label="${lblClasseA}" >
										</s:select>
										<!-- <input type="hidden" name="obblAClassifica" value="${obblAClassifica}"/>  -->
										<input type="hidden" name="obblNota" value="${obblNotaNonLavori && !('Lavori' eq labelTipoAppalto)}"/>
									</s:else>
								</s:if>
								<s:else>
									<input type="hidden" name="classeA" value=""/>
									<!-- <input type="hidden" name="obblAClassifica" value="false"/> -->
									<input type="hidden" name="obblNota" value="${obblNotaNonLavori && !('Lavori' eq labelTipoAppalto)}"/>
								</s:else>
							</div>
						</s:if>
						<%-- si inseriscono i campi hidden nel caso di nodo interno in modo da garantirne la presenza nella struttura anche se non utilizzati --%>
						<s:else>
							<input type="hidden" name="classeDa" value=""/>
							<input type="hidden" name="classeA" value=""/>
							<!-- <input type="hidden" name="obblAClassifica" value="false"/> -->
							<input type="hidden" name="obblNota" value="${obblNotaNonLavori && !('Lavori' eq labelTipoAppalto)}"/>
						</s:else>
						<div class="clear item-nota">
							<wp:i18n key="LABEL_ULTERIORI_INFO" var="titleUlterioriInformazioni" />
							<s:textarea name="nota" value="%{nota[#status.index]}" id="nota%{codice}" rows="3" cols="68" title="%{#attr.titleUlterioriInformazioni}"></s:textarea>
						</div>
						</div>
						<%-- fine nodo/foglia --%>
					</s:if>
					<%-- si inseriscono i campi hidden nel caso di nodo interno in modo da garantirne la presenza nella struttura anche se non utilizzati --%>
					<s:else>
						<input type="hidden" name="classeDa" value=""/>
						<input type="hidden" name="classeA" value=""/>
						<input type="hidden" name="nota" value=""/>
						<!-- <input type="hidden" name="obblAClassifica" value="false"/>  -->
						<input type="hidden" name="obblNota" value="false"/>
					</s:else>

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
			<input type="hidden" name="page" value="cate"/>
			<input type="hidden" name="ext" value="${param.ext}" />
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_previous.jsp" />
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_next.jsp" />
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_cancel.jsp" />
		</div>
	</form>
</div>