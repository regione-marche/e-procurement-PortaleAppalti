<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="/struts-tags" %>


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">
	
	<h2>
		<wp:i18n key='TITLE_PAGE_RETTIFICA_CONFERMA_RICHIESTA'/>
	</h2>
	
	<p>
		<s:if test="%{tipoBusta == 2}">
			<wp:i18n key='LABEL_PAGE_RETTIFICA_CONFERMA_RICHIESTA_TEC'/>
		</s:if>
		<s:if test="%{tipoBusta == 3}">
			<wp:i18n key='LABEL_PAGE_RETTIFICA_CONFERMA_RICHIESTA_ECO'/>
		</s:if>
	</p>
	
	<div class="azioni">
		<wp:i18n key="LABEL_YES" var="valueYesButton" />
		<wp:i18n key="LABEL_NO" var="valueNoButton" />
		<c:set var="hrefConferma" value="/ExtStr2/do/FrontEnd/Comunicazioni/sendRichiestaRettifica.action"/>
		<c:set var="hrefAnnulla" value="/ExtStr2/do/FrontEnd/Comunicazioni/cancelRichiestaRettifica.action"/>
			
		<form action="<wp:action path="${hrefConferma}" />" method="post" class="azione">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			
			<div>
				<s:if test="!sendBlocked">
					<s:submit value="%{#attr.valueYesButton}" cssClass="button" />
				</s:if>
				<input type="hidden" name="codice" value="<s:property value="codice" />"/>
				<input type="hidden" name="codice2" value="<s:property value="codice2" />"/>
				<input type="hidden" name="progressivoOfferta" value="<s:property value="progressivoOfferta" />"/>
				<input type="hidden" name="tipoBusta" value="<s:property value="tipoBusta" />"/>
				<input type="hidden" name="operazione" value="<s:property value="operazione" />"/>
				<input type="hidden" name="ext" value="${param.ext}" />
			</div>
		</form>
		
		<form action="<wp:action path="${hrefAnnulla}" />" method="post" class="azione">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			
			<div>
				<s:submit value="%{#attr.valueNoButton}" cssClass="button" />
				<input type="hidden" name="codice" value="<s:property value="codice" />"/>
				<input type="hidden" name="codice2" value="<s:property value="codice2" />"/>
				<input type="hidden" name="tipoBusta" value="<s:property value="tipoBusta" />"/>
				<input type="hidden" name="operazione" value="<s:property value="operazione" />"/>
				<input type="hidden" name="ext" value="${param.ext}" />
			</div>
		</form>
	</div>
</div>