<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags"%>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>
<%@ taglib  prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="hrefUnlockSSO" value="/ExtStr2/do/FrontEnd/AreaPers/confirmUnlockDelegatoSSO.action"/>


 <jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp">
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>  
 
<div class="portgare-view">

	<h2><wp:i18n key="TITLE_AREA_PERSONALE_SSO_SESSIONI_SOGGETTI" /></h2>
	
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_AREA_PERSONALE_SSO_SESSIONI_SOGGETTI" />
	</jsp:include> 
	
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/field_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_messages.jsp" />

	<s:if test="%{questionConfirmDelete}">
		<%-- richiesta cancellazione una sessione soggetto impresa --%>
		
		<p class="question">
			<wp:i18n key="LABEL_UNLOCK_SOGGETTO_IMPRESA" /> "<s:property value='%{sessioni.get(idDelete).delegate}'/>" (<s:property value='%{sessioni.get(idDelete).flusso}'/>)?
		</p>
		<div class="azioni">
			<form action="<wp:action path="/ExtStr2/do/FrontEnd/AreaPers/unlockDelegatoSSO.action" />" method="post" class="azione">
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
	
				<input type="hidden" name="ext" value="${param.ext}" />
				<s:hidden name="id" id="id" value="%{idDelete}" />
				<s:hidden name="idUtente" id="idUtente" value="%{sessioni.get(idDelete).delegate}" />

				<wp:i18n key="LABEL_YES" var="valueButtonYes" />
				<wp:i18n key="TITLE_CONFIRM_DEL_YES" var="titleButtonYes" />
				<s:submit value="%{#attr.valueButtonYes}" title="%{#attr.titleButtonYes}" cssClass="button"></s:submit>
			</form>
			<form action="<wp:action path="/ExtStr2/do/FrontEnd/AreaPers/cancelUnlockDelegatoSSO.action" />" method="post" class="azione">
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
	
				<input type="hidden" name="ext" value="${param.ext}" />
				<s:hidden name="id" id="id" value="%{idDelete}" />
				<s:hidden name="idUtente" id="idUtente" value="%{#sessioni.get(idDelete).delegate}" />
				
				<wp:i18n key="LABEL_NO" var="valueButtonNo" />
				<wp:i18n key="TITLE_CONFIRM_DEL_NO" var="titleButtonNo" />
				<s:submit value="%{#attr.valueButtonNo}" title="%{#attr.titleButtonNo}" cssClass="button"></s:submit>
			</form>
		</div>
		
	</s:if>
	<s:else>
		<%-- lista delle sessioni dei soggetti impresa --%>

		<fieldset>
			<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_SSO_SESSIONI_SOGGETTI" /></legend>
	
			<div class="table-container">
				<table class="wizard-table">
					<%-- <caption style="display:none;"><wp:i18n key="LABEL_SSO_SESSIONI_SOGGETTI_SUMMARY" /></caption>--%>
					<tr>
						<th scope="col"><wp:i18n key="LABEL_ID_UTENTE"/></th>
						<th scope="col"><wp:i18n key="LABEL_FUNCTION"/></th>
						<th scope="col"><wp:i18n key="LABEL_LOGINTIME"/></th>
						<th scope="col"><wp:i18n key="LABEL_LOGOUTTIME"/></th>
						<th scope="col"><wp:i18n key="ACTIONS"/></th>
					</tr>
					<s:iterator value="sessioni" var="item" status="stat">
						<tr>
							<td>
								<s:property value="%{#item.delegate}"/>
							</td>
							<td>
								<s:property value="%{#item.flusso}"/>
							</td>
							<td>
								<s:date name="%{#item.loginTime}" format="dd/MM/yyyy HH:mm:ss" />
							</td>
							<td>
								<s:date name="%{#item.logoutTime}" format="dd/MM/yyyy HH:mm:ss" />
							</td>
							<td class="azioni">
								<ul>
									<li>
										<c:set var="hrefUnlock">
											<wp:action path='${hrefUnlockSSO}'/>&amp;id=<s:property value="%{#stat.index}"/>&amp;idUtente=<s:property value="%{#item.delegate}"/>
										</c:set>
										<a href="${hrefUnlock}" title='<wp:i18n key="LABEL_UNLOCK"/>' class='bkg delete'>
										<c:choose>
											<c:when test="${skin == 'highcontrast' || skin == 'text'}">
													<wp:i18n key="LABEL_UNLOCK"/>
											</c:when>
											<c:otherwise>
													
											</c:otherwise>
										</c:choose>
										</a>
									</li>
								</ul>
							</td>
						</tr>
					</s:iterator>
				</table>
			</div>
			
			<form action="<wp:action path="/ExtStr2/do/FrontEnd/AreaPers/sessionsDelegatiSSO.action" />" method="post" >
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
				<div class="azioni">
					<wp:i18n key="BUTTON_REFRESH" var="lblRefresh" />
					<s:submit value="%{#attr.lblRefresh}" title="%{#attr.lblRefresh}" cssClass="button" ></s:submit>
				</div>
			</form>	
		</fieldset>
		
	</s:else>
	
</div>
