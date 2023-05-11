<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>

<es:checkCustomization var="withAdvancedUI" objectId="UI-ADVANCED" attribute="DATATABLE" feature="ACT" />

<script src='<wp:resourceURL/>static/js/jquery.dataTables.min.js'></script>
<c:if test="${withAdvancedUI}" >
	<script>
		$(document).ready(function() {
	
			$.extend($.fn.dataTable.defaults, {
				"paging": false,
			    "ordering": false,
			    "info": false,
			    "searching" : false
			});
			
			$('#tableArticoli').dataTable({
				scrollX: true,
				scrollY: "25em",
				scrollCollapse: true
			});
		});
	</script>
</c:if>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-list">

	<h2><wp:i18n key="TITLE_PAGE_LISTA_ARTICOLI" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_LISTA_ARTICOLI"/>
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
	
	<s:url id="urlDownloadExcel" namespace="/do/FrontEnd/Cataloghi" action="downloadArticoliFiltrati">
		<s:param name="model.codice" value="%{#session.formSearchArticoli.codice}"/>
		<s:param name="model.descrizione" value="%{#session.formSearchArticoli.descrizione}"/>
		<s:param name="model.tipo" value="%{#session.formSearchArticoli.tipo}"/>
		<s:param name="model.colore" value="%{#session.formSearchArticoli.colore}"/>
		<s:param name="model.mieiArticoli" value="%{#session.formSearchArticoli.mieiArticoli}"/>
		<s:param name="codiceCatalogo" value="%{codiceCatalogo}"/>
	</s:url>

	<form action="<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/searchArticoli.action" />" method="post">
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
		
		<fieldset>
			<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_SEARCH_CRITERIA" /></legend>

			<div class="fieldset-row first-row">
				<div class="label">
					<label for="model.codice"><wp:i18n key="LABEL_CODICE" /> : </label>
				</div>
				<div class="element">
					<s:textfield name="model.codice" id="model.codice" cssClass="text" value="%{#session.formSearchArticoli.codice}" 
									maxlength="30" size="30" />
				</div>
			</div>
				
			<div class="fieldset-row">
				<div class="label">
					<label for="model.descrizione"><wp:i18n key="LABEL_LISTA_ARTICOLI_DESCRIZIONE" /> : </label>
				</div>
				<div class="element">
					<s:textfield name="model.descrizione" id="model.descrizione" cssClass="text" value="%{#session.formSearchArticoli.descrizione}" 
									maxlength="50" size="50" />
				</div>
			</div>
				
			<div class="fieldset-row">
				<div class="label">
					<label for="model.tipo"><wp:i18n key="LABEL_LISTA_ARTICOLI_TIPOLOGIA" /> : </label>
				</div>
				<div class="element">
					<wp:i18n key="OPT_CHOOSE_TIPOLOGIA_ARTICOLO" var="valueTipoArticolo" />
					<s:select name="model.tipo" id="model.tipo" list="maps['tipiArticolo']" value="%{#session.formSearchArticoli.tipo}" 
								headerKey="" headerValue="%{#attr.valueTipoArticolo}" >
					</s:select>
				</div>
			</div>
				
			<div class='fieldset-row <s:if test="%{#session.currentUser.username == 'guest' || !impresaAbilitataAlCatalogo}">last-row</s:if>'>
				<div class="label">
					<label for="model.colore"><wp:i18n key="LABEL_LISTA_ARTICOLI_COLORE" /> : </label>
				</div>
				<div class="element">
					<s:textfield name="model.colore" id="model.colore" cssClass="text" value="%{#session.formSearchArticoli.colore}" 
									maxlength="50" size="50" />
				</div>
			</div>
			
			<s:if test="%{#session.currentUser.username != 'guest' && impresaAbilitataAlCatalogo}">
				<div class="fieldset-row last-row">
					<div class="label">
						<label for="model.mieiArticoli"><wp:i18n key="LABEL_LISTA_ARTICOLI_MIEI_ARTICOLI" /> : </label>
					</div>
					<div class="element">
						<s:checkbox name="model.mieiArticoli" id="model.mieiArticoli" value="%{model.mieiArticoli}"></s:checkbox>
					</div>
				</div>
			</s:if>

			<div class="azioni">
				<input type="hidden" name="codiceCatalogo" value="${codiceCatalogo}" />
				<input type="hidden" name="model.codiceCategoria" value='<s:property value="%{model.codiceCategoria}"/>' />
				<wp:i18n key="BUTTON_SEARCH" var="valueSearchButton" />
				<s:submit value="%{#attr.valueSearchButton}" cssClass="button"></s:submit>
			</div>
		</fieldset>

		<s:if test="%{listaArticoli.size() > 0}">
			<div class="list-summary">
				<wp:i18n key="SEARCH_RESULTS_INTRO" /> <s:property value="model.iTotalDisplayRecords"/> <wp:i18n key="SEARCH_RESULTS_OUTRO" /> <wp:i18n key="SEARCH_RESULTS_COUNT" /> <s:property value="model.iTotalRecords"/>.
			</div>
			<div class="table-container">
				<table id="tableArticoli" summary="Tabella articoli del catalogo" class="info-table">
					<thead>
						<tr>
							<th scope="col" style="min-width: 10em;"><wp:i18n key="LABEL_CODICE" /></th>
							<th scope="col" style="min-width: 25em;"><wp:i18n key="LABEL_LISTA_ARTICOLI_DESCRIZIONE" /></th>
							<th scope="col" style="min-width: 5em;"><wp:i18n key="LABEL_LISTA_ARTICOLI_TIPOLOGIA" /></th>
							<th scope="col" style="min-width: 10em;"><wp:i18n key="LABEL_LISTA_ARTICOLI_COLORE" /></th>
						</tr>
					</thead>
					<tbody>			
						<s:iterator var="riga" value="listaArticoli">
							<tr>
								<td class="detail-link-content">
									<a href="<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/viewArticolo.action" />&amp;codiceCatalogo=${codiceCatalogo}&amp;ext=${param.ext}&amp;articoloId=${id}&amp;${tokenHrefParams}">
										<s:property value="codice"/>
									</a>
								</td>
								<td><s:property value="descrizione" /></td>
								<td><s:property value="tipo" /></td>
								<td><s:property value="colore" /></td>
							</tr>
						</s:iterator>
					</tbody>
				</table>
			</div>
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/pagination.jsp"></jsp:include>
			<s:if test="%{#session.currentUser.username != 'guest' && impresaAbilitataAlCatalogo}">
				<p>
					<wp:i18n key="TITLE_SCARICA_ARTICOLI" var="titleScaricaArticoli"/>
					<c:choose>
						<c:when test="${skin == 'highcontrast' || skin == 'text'}">
							<s:a href="%{#urlDownloadExcel}&amp;%{#attr.tokenHrefParams}" title="%{#attr.titleScaricaArticoli}" cssClass="important">
								<wp:i18n key="LINK_SCARICA_ARTICOLI" />
							</s:a>
						</c:when>
						<c:otherwise>
							<s:a href="%{#urlDownloadExcel}&amp;%{#attr.tokenHrefParams}" title="%{#attr.titleScaricaArticoli}" cssClass="bkg xls">
								<wp:i18n key="LINK_SCARICA_ARTICOLI" />
							</s:a>
						</c:otherwise>
					</c:choose>
				</p>
			</s:if>
		</s:if>
		<s:else>
			<div class="list-summary">
				<wp:i18n key="SEARCH_NOTHING_FOUND" />.
			</div>
		</s:else>
	</form>
		
	<div class="back-link">
		<s:if test='%{#session.formSearchArticoli.codiceCategoria != null && !#session.formSearchArticoli.codiceCategoria.equals("")}'>
			<a href="<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/viewCategorieIscrizione.action" />&amp;codice=${codiceCatalogo}&amp;mercatoElettronico=true&amp;ext=${param.ext}&amp;${tokenHrefParams}">
				<wp:i18n key="LINK_BACK_TO_CATEGORIE" />
			</a>
		</s:if>
		<s:else>
			<a href="<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/viewIscrizione.action" />&amp;codice=${codiceCatalogo}&amp;ext=${param.ext}&amp;${tokenHrefParams}">
				<wp:i18n key="LINK_BACK_TO_ISCRIZIONE" />
			</a>
		</s:else>
	</div>	
</div>