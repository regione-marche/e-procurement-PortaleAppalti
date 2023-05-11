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
				<a href="<wp:action path='/ExtStr2/do/FrontEnd/IscrAlbo/openQCSummaryToView.action'/>&amp;ext=${param.ext}&amp;codice=${codice}&amp;${tokenHrefParams}">
					<wp:i18n key='LABEL_MODULISTICA_CAMBIATA_SCARICA_DOCUMENTI'/>
				</a>
			</li>
			<li>
				<%--
				se ho già inviato l'offerta
					/ExtStr2/do/FrontEnd/GareTel/confirmRettificaOfferta.action&currentFrame=7
				altrimenti 
					elimino i doc della singola busta 
					/ExtStr2/do/FrontEnd/GareTel/confirmRettificaBusta.action&currentFrame=7
		 		--%>
				<a href="<wp:action path='/ExtStr2/do/FrontEnd/IscrAlbo/eliminaDocumentiElenco.action'/>&amp;ext=${param.ext}&amp;codice=${codice}&amp;${tokenHrefParams}">
					<wp:i18n key='LABEL_MODULISTICA_CAMBIATA_ELIMINA_DOCUMENTI'/>
				</a>
			</li>
		</ul>
		<wp:i18n key='LABEL_MODULISTICA_CAMBIATA_INFO3'/>
	</p>
</div>