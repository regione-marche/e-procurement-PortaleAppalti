<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags"%>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>
<%@ taglib  prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

 <jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp">
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>  

<c:set var="profilo" value="${sessionScope.currentUser.profile}" scope="page"/>

<script type="text/javascript" src="<wp:resourceURL/>static/js/masonry.pkgd.js"></script> 
<div class="portgare-view">

	
	<h2><wp:i18n key="TITLE_PAGE_AREA_PERSONALE_SSO" /></h2>
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_AREA_PERSONALE_SSO" />
	</jsp:include> 
	
	<c:choose>
		<c:when test="${sessionScope.accountSSO != null}">
	
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_messages.jsp" />
	
			<fieldset class="floating-box agid-box">
				<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_AREA_PERSONALE_SSO_OP_ECO" /></legend>
					<ul class="options-list">
						 <s:if test="%{impreseAssociate.size > 0}">
							<s:iterator
								value="%{impreseAssociate.iterator()}"
								var="impresaAssociata" status="stat">
									<s:if test="%{!#impresaAssociata.disabled}">
										<s:url id="loginAsImpresa" namespace="/do/SSO" action="loginAs"/>
										<li>
											<s:if test="%{#session.currentUser == null || #session.currentUser.username.equals('guest')}">
												<s:a href="%{#loginAsImpresa}?id=%{#impresaAssociata.username}" title="Accedi per conto dell'operatore">
													<s:property value="%{ragioniSocialiImprese.get(#stat.index)}"/>
												</s:a>
											</s:if>
											<s:else>
												<s:property value="%{ragioniSocialiImprese.get(#stat.index)}"/>
											</s:else>
											<s:if test="%{#session.currentUser.username == #impresaAssociata.username}">
												<strong>&nbsp;(<wp:i18n key="LABEL_AREA_PERSONALE_SSO_OP_ATTIVO" />)</strong>
											</s:if>
										</li>					
									</s:if>
							</s:iterator>
						</s:if>
						<s:else> 
							<li><wp:i18n key="LABEL_AREA_PERSONALE_SSO_OP_NESSUN_OP_ECO" /></li>
						</s:else> 
						<li>
							<s:if test="%{impreseInAttesa > 0}">
								<wp:i18n key="LABEL_AREA_PERSONALE_SSO_OP_ECO_ATTESA" />: <strong><s:property value="%{impreseInAttesa}"/></strong>
							</s:if>
						</li>
					</ul>
			</fieldset>
			
			<s:if test="%{!soggettoBloccato}">
                <s:if test='%{#session.accountSSO.tipologiaLogin != "MAGGIOLI_AUTH_SSO_BUSINESS" || (impreseAssociate.size == 0 && impreseInAttesa == 0 && #session.accountSSO.tipologiaLogin == "MAGGIOLI_AUTH_SSO_BUSINESS")}'>
					<fieldset class="floating-box agid-box">
						<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_AREA_PERSONALE_SSO_OPERAZIONI" /></legend>
							<ul class="options-list">
								<li><a href="<s:property value="%{urlRegistraNuovoSoggetto}"/>"><wp:i18n key="LABEL_AREA_PERSONALE_SSO_REGISTRA" /></a></li>
							</ul>
					</fieldset>
				</s:if>	
			</s:if>
			</c:when>
		<c:otherwise>
			<p>
				<wp:i18n key="LABEL_AREA_PERSONALE_SSO_PLEASE_LOGIN" />
			</p>
		</c:otherwise>
	</c:choose>
</div>