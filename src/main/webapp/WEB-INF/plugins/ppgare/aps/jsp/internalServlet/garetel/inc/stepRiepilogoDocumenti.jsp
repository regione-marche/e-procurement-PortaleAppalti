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

<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/jquery_ui.jsp" />

<s:set var="imgCheck"><wp:resourceURL />static/img/check.svg</s:set>

<s:set var="busta" value="#request.busta"/>
<s:set var="docObbligatoriMancanti" value="#request.docObbligatoriMancanti"/>

<s:set var="documentiInseritiSize" value="#busta.documentiInseriti.size()" />
<s:set var="docObbligatoriMancantiSize" value="#docObbligatoriMancanti.size()" />
<s:set var="questionarioPresente" value="#busta.questionarioPresente" />
<c:if test="${busta.questionarioPresente}" >
	<s:set var="documentiInseritiSize" value="#documentiInseritiSize - 1" />
</c:if>

<c:set var="stato" value=""/>
<c:if test="${param.stato != null}">
	<c:set var="stato" value="${param.stato.trim()}"/>
</c:if>

<s:if test="%{#questionarioPresente}">
	<div class="fieldset-row first-row">
		<div class="label">
			<label><wp:i18n key="LABEL_QUESTIONARIO_COMPLETATO" /> : </label>
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
				(<s:property value="%{#docObbligatoriMancantiSize}"/>)</s:if> :
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

<c:if test='${"1".equals(stato) || "".equals(stato)}'>
	<form action="<wp:action path='/ExtStr2/do/FrontEnd/GareTel/redirectSummaryEnv.action' />" method="post">
		<div class="azioni">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			
			<input type="submit" value="Compila" title='<wp:i18n key="TITLE_MODIFICA_BUSTA" />' class="button" name="method:doModify" />
			<c:if test='${"1".equals(stato)}'>
				<input type="submit" value="<wp:i18n key='LABEL_ELIMINA' />" title="<wp:i18n key='LABEL_ELIMINA' />" class="button" name="method:doReset" />
			</c:if>
			
			<input type="hidden" name="tipoBusta" value="${param.tipoBusta}" />
			<input type="hidden" name="codice" value="${param.codice}" />
			<input type="hidden" name="operazione" value="${param.operazione}" />
			<input type="hidden" name="offertaTelematica" value="${param.offertaTelematica}" />
			<input type="hidden" name="backActionPath" value="/ExtStr2/do/FrontEnd/GareTel/openRiepilogo.action" />
		</div>
	</form>
</c:if>

<script>

// $("input[name='method:resetEnvelope']").click(function(event) {
//     event.preventDefault()
// 	var button = $(this)
//     showDialog(
// 		"Conferma", 
// 		"Sei sicuro di voler cancellare la busta? Non sarà possibile tornare indietro.",
// 		function() {
// 			button
// 				.parent("form")
// 				.append('<input type="hidden" name="method:resetEnvelope" value="Elimina" />')
// 				.submit()
// 		}
// 	)
// })

</script>