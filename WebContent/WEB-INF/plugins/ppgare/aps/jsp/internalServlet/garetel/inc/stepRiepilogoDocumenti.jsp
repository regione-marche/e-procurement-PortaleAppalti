<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<%--
i parametri "busta", "docObbligatoriMancanti" vanno passati negli attributi 
della request, come segue:  
 
	<c:set var="busta" scope="request" value="..."/>
	<c:set var="docObbligatoriMancanti" scope="request" value="..."/>	
 	<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/garetel/inc/iteratorStepRiepilogoDocumenti.jsp">
 		... 		
 	<jsp:include> 		 
--%>

<s:set var="imgCheck"><wp:resourceURL />static/img/check.svg</s:set>

<s:set var="busta" value="#request.busta"/>
<s:set var="docObbligatoriMancanti" value="#request.docObbligatoriMancanti"/>

<div class="fieldset-row first-row">
	<div class="label">
		<label><wp:i18n key="LABEL_DOCUMENTI_INSERITI" /> <s:if test="%{#busta.documentiInseriti.size() > 0}">(<s:property value="%{#busta.documentiInseriti.size()}"/>)</s:if> : </label>
	</div>
	<div class="element">
		<s:if test="%{#busta.documentiInseriti.size() > 0}">
			<c:set var="busta" scope="request" value="${busta}"/>
			<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/garetel/inc/iteratorDocumentiInseriti.jsp"/>
		</s:if>
		<s:else>
			<wp:i18n key="LABEL_NO_DOCUMENT" />.
		</s:else>
	</div>
</div>

<div class="fieldset-row last-row">
	<c:choose>
		<c:when test="${!param.offertaTelematica}">	
			<c:set var="href"><wp:action path="/ExtStr2/do/FrontEnd/GareTel/openPageDocumenti.action" />&amp;tipoBusta=${param.tipoBusta}&amp;codice=${param.codice}&amp;operazione=${param.operazione}&amp;${tokenHrefParams}</c:set>
		</c:when>
		<c:otherwise>
			<c:set var="href"><wp:action path="/ExtStr2/do/FrontEnd/GareTel/initOffTel.action" />&amp;codice=${param.codice}&amp;${tokenHrefParams}</c:set>
		</c:otherwise>
	</c:choose>
	
	<div class="label">
		<label><wp:i18n key="LABEL_DOCUMENTI_OBBLIGATORI_MANCANTI" /> <s:if test="%{#docObbligatoriMancanti.size() > 0}">
			(<s:property value="%{#docObbligatoriMancanti.size()}"/>)</s:if> : <a title='<wp:i18n key="TITLE_MODIFICA_BUSTA" />' class="bkg modify" href='${href}'></a>
		</label>
	</div>
	<div class="element">
		<s:if test="%{#docObbligatoriMancanti.size() > 0}">
			<ul class="list">
				<s:iterator value="#docObbligatoriMancanti" var="documento" status="stat">
					<li><s:property value="%{#documento}"/></li>
				</s:iterator>
			</ul>
		</s:if>
		<s:else>
			<img class="resize-svg-16" src="${imgCheck}" title="<wp:i18n key='TITLE_BUSTA_PRONTA_PER_INVIO' />" alt="<wp:i18n key='TITLE_BUSTA_PRONTA_PER_INVIO' />"/>
		</s:else>
	</div>
</div>