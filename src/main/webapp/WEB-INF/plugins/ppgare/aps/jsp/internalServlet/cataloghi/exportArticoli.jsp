<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>

<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/send_blockUI.jsp" />
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
	
			$('#listaArticoli').dataTable({
				scrollY: "25em",
				scrollCollapse: true,
				autoWidth: false,
				columns: [
					{ width: '20%' },
					{ width: '40%' },
					{ width: '20%' },
					<s:if test="%{massimaQuantita > 1}">
						{ width: '10%' },
					</s:if>
					{ width: '10%'	}
				]
			});
	
		});
	</script>
</c:if>
<script>
$(document).ready(function() {
	$("#backForm").insertBefore($("#createExcel").find(":submit"));
	$("#backForm").css("margin-right","0.25em");
});
</script>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2><wp:i18n key="TITLE_PAGE_CATALOGHI_EXPORT_ARTICOLI" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_WIZ_EXPORT_ARTICOLI_CONFIRM"/>
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
		
	<s:if test="%{articoli.size() > 0}">
		
		<s:set var="canDoExport" value="false"></s:set>
		<s:set var="locks" value="false"></s:set>
		<s:url id="urlExcelExport" namespace="/do/FrontEnd/Cataloghi" action="createExcelExportArticoli"></s:url>
	
		<form action="${urlExcelExport}" method="post" id="exportForm">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			<p class="information">
				<wp:i18n key="LABEL_CATALOGHI_PUOI_OFFRIRE_UN_MAX" /> <s:property value="massimaQuantita"/> <wp:i18n key="LABEL_CATALOGHI_PRODOTTI_PER_ARTICOLO" />.
				<s:if test="%{massimaQuantita > 20}">
					<wp:i18n key="LABEL_CATALOGHI_IMPORT_MAX_20_PRODOTTI" /> 
				</s:if>
			</p>

			<s:if test="%{massimaQuantita > 0}">
				<s:if test="%{massimaQuantita == 1}">
					<input type="hidden" name="quantitaProdotti" value='1'/>
				</s:if>
				<s:else>
					<p>
						<wp:i18n key="LABEL_CATALOGHI_MAX_PRODOTTI_OFFERTI_PER_ARTICOLO" />:
						<s:textfield name="quantitaProdotti" maxlength="2" size="2" value="%{quantitaProdotti}" cssClass="money-content"/>
					</p>
				</s:else>
			</s:if>

			<s:set var="imgLocked"><wp:resourceURL/>static/img/locked.svg</s:set>
			
			<s:iterator var="articolo" value="articoli" status="statArticolo">
				<s:if test="%{!disponibili[#statArticolo.index]}">
					 <s:set var="locks" value="%{true}"/> 
				</s:if>
			</s:iterator>
					
			<p class="instructions">
				<s:if test="%{#locks}">
					<c:choose>
						<c:when test="${skin == 'highcontrast' || skin == 'text'}">
							&#42;
						</c:when>
						<c:otherwise>
							<img class="resize-svg-16" src="${imgLocked}" title='<wp:i18n key="TITLE_ARTICOLO_NON_DISPONIBILE" />' alt='<wp:i18n key="TITLE_ARTICOLO_NON_DISPONIBILE" />' />
						</c:otherwise>
					</c:choose>
					<wp:i18n key="LABEL_CATALOGHI_NON_PUOI_INSERIRE_ALTRI_PRODOTTI" />
				</s:if>
			</p>

			<div class="table-container">
				<table id="listaArticoli" summary="Lista quantita' articoli da esportare" class="info-table">
					<thead>
						<tr>
							<th scope="col"><wp:i18n key="LABEL_CODICE_ARTICOLO" /></th>
							<th scope="col"><wp:i18n key="LABEL_DESCRIZIONE_ARTICOLO" /></th>
							<th scope="col"><wp:i18n key="LABEL_COLORE_ARTICOLO" /></th>
							<s:if test="%{massimaQuantita > 1}">
								<th scope="col"><wp:i18n key="LABEL_PRODOTTI_OFFERTI" /></th>
							</s:if>
							<th scope="col"><wp:i18n key="LABEL_STATO_OFFERTA" /></th>
						</tr>
					</thead>
					<tbody>
						<s:iterator var="articolo" value="articoli" status="statArticolo">
							<tr>
								<td><s:property value="%{#articolo.codice}" /></td>
								<td><s:property value="%{#articolo.descrizione}" /></td>
								<td><s:property value="%{#articolo.colore}" /></td>
								<s:if test="%{massimaQuantita > 1}">
									<td class="money-content">
										<s:property value='%{quantita[#statArticolo.index]}'/>
									</td>
								</s:if>
								<td class="azioni">
									<input type="hidden" name="articoliSelezionati" id="articoliSelezionato<s:property value="%{#articolo.id}"/>" 
											value='<s:property value="%{#articolo.id}"/>' />
									<s:if test="%{disponibili[#statArticolo.index]}">
										<input type="hidden" name="articoliDaEsportare" id="articoliDaEsportare<s:property value="%{#articolo.id}"/>" 
												value='<s:property value="%{#articolo.id}"/>' />
										<s:if test="%{!#canDoExport}">
											<s:set var="canDoExport" value="true"></s:set>
										</s:if>
									</s:if>
									<s:else>
										<c:choose>
											<c:when test="${skin == 'highcontrast' || skin == 'text'}">
												<wp:i18n key="LABEL_OFFERTA_COMPLETA_PER_ARTICOLO" />
											</c:when>
											<c:otherwise>
												<img class="resize-svg-16" src="${imgLocked}" title='<wp:i18n key="LABEL_OFFERTA_COMPLETA_PER_ARTICOLO" />' alt='<wp:i18n key="LABEL_OFFERTA_COMPLETA_PER_ARTICOLO" />' />
											</c:otherwise>
										</c:choose>
									</s:else>
								</td>
							</tr>
						</s:iterator>
					</tbody>
				</table>
			</div>
			<br/>
			<s:if test="%{#canDoExport}">
				<div class="azioni" id="createExcel">
					<input type="hidden" name="catalogo" value='<s:property value="catalogo"/>'/>
					<input type="hidden" name="urlPage" value="${currentPageUrl}" />
					<input type="hidden" name="currentFrame" value="${param.currentFrame}" />
					<input type="hidden" name="massimaQuantita" value="<s:property value="massimaQuantita"/>"/>
					<wp:i18n key="BUTTON_SCARICA_MODELLO" var="buttonScaricaModello"/>
					<wp:i18n key="TITLE_SCARICA_MODELLO" var="titleScaricaModello"/>
					<s:submit method="createExcel" value="%{#attr.buttonScaricaModello}" title="%{#attr.titleScaricaModello}" cssClass="button"/>
				</div>
			</s:if>
		</s:if>
		<s:else>
			<wp:i18n key="LABEL_NESSUN_ARTICOLO_SELEZIONATO"/>
		</s:else>
	</form>
	
	<div class="azioni">
		<form id="backForm" action="<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/backExportArticles.action" />" method="post" class="azione">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			<div>
				<input type="hidden" name="ext" value="${param.ext}" />
				<input type="hidden" name="catalogo" value='<s:property value="catalogo"/>'/>
				<s:iterator var="articolo" value="articoli" status="statArticolo">
					<input type="hidden" name="articoliSelezionati" id="articoliSelezionato_<s:property value="%{#articolo.id}"/>" 
							value='<s:property value="%{#articolo.id}"/>' />
				</s:iterator>
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_previous.jsp" />
			</div>
		</form>
	</div>
	<div class="back-link">
		<a href="<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/openGestioneProdotti.action" />&amp;ext=${param.ext}&amp;${tokenHrefParams}">
			<wp:i18n key="LINK_BACK_TO_GESTIONE_PRODOTTI" />
		</a>
	</div>
</div>