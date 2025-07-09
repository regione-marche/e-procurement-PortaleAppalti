<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/jquery_ui.jsp" />

<script src='<wp:resourceURL/>static/js/jquery.dataTables.min.js'></script>
<script src='<wp:resourceURL/>static/js/jquery.dataTables.select.min.js'></script>
<link type="text/css" rel="stylesheet" href="<wp:cssURL/>jquery/dataTables/jquery.dataTables.css" />
<link type="text/css" rel="stylesheet" href="<wp:cssURL/>jquery/dataTables/select.dataTables.min.css" />

<%-- ******************************************************************************** --%>
<s:url id="rifProcURL" namespace="/do/FrontEnd/PagoPA" action="getRiferimentoProcedura" />

<script type="text/javascript">
<!--//--><![CDATA[//><!--
	
	
// apertura della pagina...
$(document).ready(function() {
	console.log("PagoPA","Pagina caricata correttamente.");
	var tableRifProc = $('#tableRifProc').DataTable( {
	  //  ajax: '/do/FrontEnd/PagoPA/getRiferimentoProcedura.action',
	    ajax: '${rifProcURL}',
	    deferRender: true,
	    select: {
	        style: 'single',
	        info: false
	    },
	    columns: [
	        { data: 'codice' },
	        { data: 'cig' },
	        { data: 'oggetto' }
	    ]
	    ,language: {
           // "decimal":        "",
            "emptyTable":     "No data available in table",
            "info":           "<wp:i18n key='SEARCH_RESULTS_FROM'/> _START_ <wp:i18n key='SEARCH_RESULTS_TO'/> _END_ <wp:i18n key='SEARCH_RESULTS_COUNT'/> _TOTAL_ <wp:i18n key='SEARCH_RESULTS_OUTRO'/>",
            "infoEmpty":      "<wp:i18n key='SEARCH_RESULTS_FROM'/> 0 <wp:i18n key='SEARCH_RESULTS_TO'/> 0 <wp:i18n key='SEARCH_RESULTS_COUNT'/> 0 <wp:i18n key='SEARCH_RESULTS_OUTRO'/>",
            "infoFiltered":   "(<wp:i18n key='SEARCH_RESULTS_COUNT'/> _MAX_ <wp:i18n key='SEARCH_RESULTS_OUTRO'/>)",
            "infoPostFix":    "",
            "thousands":      ",",
            "lengthMenu":     '<s:property value="%{getText('label.rowsPerPage')}" />: _MENU_ ',
            "loadingRecords": "Loading...",
            "processing":     "Processing...",
            "search":         "<wp:i18n key='SEARCH'/>:",
            "zeroRecords":    "<wp:i18n key='SEARCH_NOTHING_FOUND'/>",
            "paginate": {
                "first":      "First",
                "last":       "Last",
                "next":       '<s:property value="%{getText('label.next.full')}" />',
                "previous":   '<s:property value="%{getText('label.prev.full')}" />'
            },
            "aria": {
                "sortAscending":  ": activate to sort column ascending",
                "sortDescending": ": activate to sort column descending"
            }
	        
	    }
	} );

	tableRifProc.on( 'select', function ( e, dt, type, indexes ) {
		//console.log('select type:' + type);
	    if ( type === 'row' ) {
			$("#modelcodiceGara").val(dt.data().codice);
			console.log('$("#modelcodiceGara").val(): '+$("#modelcodiceGara").val());
	    }
	} );
	
	 $( "#dialogRifProc" ).dialog({
	    	autoOpen: false,
	    	closeOnEscape: false,
	    	width: "80%",
	    	height: "auto",
	    	show: {
	    		effect: "blind",
	    		duration: 500
	        },
	        hide: {
	        	effect: "blind",
	        	duration: 200
	        },
	        modal: true,
	        resizable: false,
			focusCleanup: true,
			cache: false,
	        buttons: {
		        	"<wp:i18n key='BUTTON_CONFIRM'/>": function() {
		        	$( this ).dialog( "close" );
	 		    },
	 		    "<wp:i18n key='BUTTON_WIZARD_CANCEL'/>" : function() {
	 		    	$( this ).dialog( "close" );
			    }
	       }
	    });	
});

function loadRiferimenti(){
	console.log('loadRiferimenti');
	$( "#dialogRifProc" ).dialog( "open" );
}

//--><!]]>
</script>
<%-- ******************************************************************************** --%>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp">
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">
<c:catch var="exceptionPagoPa">
	<h2><wp:i18n key="TITLE_PAGE_PAGOPA_NUOVO" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_PAGOPA_NUOVO" />
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
		
	<%-- <div class="detail-section first-detail-section">
		<h3 class="detail-section-title">
			<span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_STAZIONE_APPALTANTE" />
		</h3>
		
		<div class="detail-row">
			<label><wp:i18n key="LABEL_DENOMINAZIONE" /> : </label>
			<s:iterator value="maps['stazioniAppaltanti']">
				<s:if test="%{key == dettaglioOrdine.contractingAuthority}">
					<s:property value="%{value}" />
				</s:if>
			</s:iterator>
		</div>
	</div> --%>
	<form action="<wp:action path="/ExtStr2/do/FrontEnd/PagoPA/nuovoPagamento.action" />" method="post">
		<fieldset>
		<div class="fieldset-row first-row">
			<div class="label">
				<label for="model.codiceGara"><wp:i18n key="LABEL_PAGOPA_GARA_CODICE" /><span class="required-field">*</span>: </label>
			</div>
			<div class="element">
				<s:textfield name="model.codiceGara" value="%{model.codiceGara}" id="modelcodiceGara" class="text" />
				<script>document.write('<a href="javascript:loadRiferimenti();"><img class="right" src="<wp:imgURL />/summary.svg" alt="<s:text name="column.select" /> <wp:i18n key="LABEL_PAGOPA_GARA_CODICE" />" title="<s:text name="column.select" /> <wp:i18n key="LABEL_PAGOPA_GARA_CODICE" />" /></a>')</script>
			</div>
		</div>
		<div class="fieldset-row">
			<wp:i18n key="LABEL_PAGOPA_CAUSALE" var="headerValueCausalePagmento" />
			<div class="label">
				<label for="model.causale"><s:property value="%{#attr.headerValueCausalePagmento}"/><span class="required-field">*</span>: </label>
			</div>
			<div class="element">
<%-- 				<s:select list="%{tipiCausalePagamento}" id="model.causale" name="model.causale" value="%{model.causale}" headerKey="-1" headerValue="%{#attr.headerValueCausalePagmento}" > --%>
				<s:select list="%{tipiCausalePagamento}" id="model.causale" name="model.causale" value="%{model.causale}" headerKey="-1" headerValue="" >
				</s:select>
			</div>
		</div>
		<div class="fieldset-row">
			<div class="label">
				<label for="model.importo"><wp:i18n key="LABEL_PAGOPA_IMPORTO" /><span class="required-field">*</span>: </label>
			</div>
			<div class="element">
				<s:textfield name="model.importo" value="%{model.importo}" id="model.importo" class="text" />
			</div>
		</div>
		<%-- <s:hidden name="model.idDebito" value="%{model.idDebito}" id="model.idDebito" class="text" />
		<s:hidden name="model.idRata" value="%{model.idRata}" id="model.idRata" class="text" /> --%>
		
		</fieldset>
		<div class="azioni">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			<wp:i18n key="LABEL_PAGOPA_CREA" var="labelCreaButton" />
			<s:submit value="%{#attr.labelCreaButton}" title="%{#attr.labelCreaButton}" cssClass="button" method="crea"></s:submit>
		</div>
	</form>

	<div class="back-link">
		<a href="<wp:url page="ppcommon_area_personale" />">
			<wp:i18n key="LINK_BACK_TO_AREAPERSONALE" />
		</a>
	</div>
	<div id="dialogRifProc" style="display: none;">
		<table id="tableRifProc" class="dataTable hover" style="width: 100%;height: 100%;">
		    <thead>
		        <tr>
		            <th><wp:i18n key="LABEL_PAGOPA_GARA_CODICE" /></th>
		            <th><wp:i18n key="LABEL_CIG" /></th>
		            <th><wp:i18n key="LABEL_TITOLO" /></th>
		        </tr>
		    </thead>
		    <tfoot>
		        <tr>
		            <th><wp:i18n key="LABEL_PAGOPA_GARA_CODICE" /></th>
		            <th><wp:i18n key="LABEL_CIG" /></th>
		            <th><wp:i18n key="LABEL_TITOLO" /></th>
		        </tr>
		    </tfoot>
		</table>
	</div>
</c:catch>
<c:if test="${exceptionPagoPa!=null}">
There is an exception: ${exceptionPagoPa.message}<br>
The exception is : ${exceptionPagoPa}<br>
	<br>
</c:if>
</div>