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

<s:set var="documentiInseritiSize" value="#busta.documentiInseriti.size()" />
<s:set var="docObbligatoriMancantiSize" value="#docObbligatoriMancanti.size()" />
<s:set var="questionarioPresente" value="#busta.questionarioPresente" />
<c:if test="${busta.questionarioPresente}" >
	<s:set var="documentiInseritiSize" value="#documentiInseritiSize - 1" />
</c:if>

<c:choose>
	<c:when test="${!param.offertaTelematica}">	
		<c:set var="href"><wp:action path="/ExtStr2/do/FrontEnd/GareTel/openPageDocumenti.action" />&amp;tipoBusta=${param.tipoBusta}&amp;codice=${param.codice}&amp;operazione=${param.operazione}&amp;${tokenHrefParams}</c:set>
	</c:when>
	<c:otherwise>
		<c:set var="href"><wp:action path="/ExtStr2/do/FrontEnd/GareTel/initOffTel.action" />&amp;codice=${param.codice}&amp;${tokenHrefParams}</c:set>
	</c:otherwise>
</c:choose>


<s:if test="%{#questionarioPresente}">
	<div class="fieldset-row first-row">
		<div class="label">
			<label><wp:i18n key="LABEL_QUESTIONARIO_COMPLETATO" /> : <c:if test='${(param.stato != null && param.stato.trim() == "1") || param.stato == ""}'><a title='<wp:i18n key="TITLE_MODIFICA_BUSTA" />' class="bkg modify" href='${href}'></a></c:if></label>
		</div>
		<div class="element">
			<s:if test="%{#busta.questionarioCompletato}">
				<wp:i18n key="LABEL_YES" />
			</s:if>
			<s:else>
				<wp:i18n key="LABEL_NO" />
			</s:else>
		</div>
	</div>
</s:if>

<div class="fieldset-row <s:if test='%{!questionarioPresente}'>first-row</s:if>">
	<div class="label">
		<label><wp:i18n key="LABEL_DOCUMENTI_INSERITI" /> <s:if test="%{#documentiInseritiSize > 0}">(<s:property value="%{#documentiInseritiSize}"/>)</s:if> : </label>
	</div>
	<div class="element">
		<s:if test="%{#documentiInseritiSize > 0}">
			<c:set var="busta" scope="request" value="${busta}"/>
			<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/garetel/inc/iteratorDocumentiInseriti.jsp"/>
		</s:if>
		<s:else>
			<wp:i18n key="LABEL_NO_DOCUMENT" />.
		</s:else>
	</div>
</div>

<s:if test="%{!#questionarioPresente}">
	<div class="fieldset-row last-row">
		<div class="label">
			<label><wp:i18n key="LABEL_DOCUMENTI_OBBLIGATORI_MANCANTI" /> <s:if test="%{#docObbligatoriMancantiSize > 0}">
				(<s:property value="%{#docObbligatoriMancantiSize}"/>)</s:if> : <c:if test='${(param.stato != null && param.stato.trim() == "1") || param.stato == ""}'><a title='<wp:i18n key="TITLE_MODIFICA_BUSTA" />' class="bkg modify" href='${href}'></a></c:if>
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
</s:if>