<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2>
		<wp:i18n key='TITLE_PAGE_MODULISTICA_CAMBIATA'/>
	</h2>
	
	<p>
		<wp:i18n key='LABEL_MODULISTICA_ELENCO_CAMBIATA_INFO'/>
		<ul>
			<li>
				<form action="<wp:action path='/ExtStr2/do/FrontEnd/IscrAlbo/openQCSummaryToView.action'/>" method="post">
					<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
					<input type="hidden" name="codice" value="${codice}" />
					<input type="hidden" name="ext" value="${param.ext}" />
					
					<a href="javascript:;" onclick="parentNode.submit();" >
						<wp:i18n key='LABEL_MODULISTICA_CAMBIATA_SCARICA_DOCUMENTI'/>
					</a>
				</form>
			</li>
			<li>
				<%--
				se ho già inviato l'offerta
					/ExtStr2/do/FrontEnd/GareTel/confirmRettificaOfferta.action&currentFrame=7
				altrimenti 
					elimino i doc della singola busta 
					/ExtStr2/do/FrontEnd/GareTel/confirmRettificaBusta.action&currentFrame=7
		 		--%>
				<form action="<wp:action path='/ExtStr2/do/FrontEnd/IscrAlbo/eliminaDocumentiElenco.action'/>" method="post">
					<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
					<input type="hidden" name="codice" value="${codice}" />
					<input type="hidden" name="ext" value="${param.ext}" />
					
					<a href="javascript:;" onclick="parentNode.submit();" >
						<wp:i18n key='LABEL_MODULISTICA_CAMBIATA_ELIMINA_DOCUMENTI'/>
					</a>
				</form>
			</li>
		</ul>
		<wp:i18n key='LABEL_MODULISTICA_CAMBIATA_INFO3'/>
	</p>
</div>