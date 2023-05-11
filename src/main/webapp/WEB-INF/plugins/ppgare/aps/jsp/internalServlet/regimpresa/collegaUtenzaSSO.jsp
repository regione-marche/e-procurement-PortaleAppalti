<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags"%>


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp">
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>  

<div class="portgare-view">
	
	<h2><wp:i18n key="TITLE_PAGE_AREA_PERSONALE_COLLEGA_UTENZA_SSO" /></h2>
	
	<s:if test="%{!collegaUtenza}" >
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_AREA_PERSONALE_COLLEGA_UTENZA_SSO" />
	</jsp:include> 
	</s:if>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_messages.jsp" />

	<s:if test="%{collegaUtenza}" >
		<p>
			<wp:i18n key="LABEL_COLLEGA_UTENZA_SUCCESS" />
		</p>
	</s:if>
	<s:else>
		<fieldset>
			<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_DATI_PRINCIPALI_OE" /></legend>
			
			<div class="fieldset-row first-row">
				<div class="label">
					<label>TOKEN : </label>
				</div>
				<div class="element">
					<span>
						${token}
					</span>
				</div>
			</div>
			
			<div class="fieldset-row">
				<div class="label">
					<label><wp:i18n key="USERNAME" /> : </label>
				</div>
				<div class="element">
					<span>
						${username}	
					</span>
				</div>
			</div>
		
			<div class="fieldset-row">
				<div class="label">
					<label><wp:i18n key="LABEL_RAGIONE_SOCIALE" /> : </label>
				</div>
				<div class="element">
					<span>
						${ragioneSociale}					
					</span>
				</div>
			</div>
			
			<div class="fieldset-row">
				<div class="label">
					<label><wp:i18n key="LABEL_CODICE_FISCALE" /> : </label>
				</div>
				<div class="element">
					<span>
						${codiceFiscale}	
					</span>
				</div>
			</div>
			
			<div class="fieldset-row">
				<div class="label">
					<label><wp:i18n key="LABEL_PARTITA_IVA" /> : </label>
				</div>
				<div class="element">
					<span>
						${partitaIVA}	
					</span>
				</div>
			</div>
			
			<div class="fieldset-row">
				<div class="label">
					<label><wp:i18n key="LABEL_EMAIL" /> : </label>
				</div>
				<div class="element">
					<span>
						${email}	
					</span>
				</div>
			</div>
			
			<div class="fieldset-row">
				<div class="label">
					<label><wp:i18n key="LABEL_PEC" /> : </label>
				</div>
				<div class="element">
					<span>
						${pec}	
					</span>
				</div>
			</div>
		</fieldset>
		
		<form action="<wp:action path='/ExtStr2/do/FrontEnd/RegistrImpr/collegaUtenzaSSO.action' />" method="post" >		
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />		
			<div class="azioni">
				<input type="hidden" id="token" name="token" value="${token}" />
				<wp:i18n key="BUTTON_ACCESSO_COLLEGA_UTENZA" var="valueButtonCollega" />
				<s:submit value="%{#attr.valueButtonCollega}" title="%{#attr.valueButtonCollega}" cssClass="button block-ui"></s:submit>			
			</div>
		</form>
	</s:else>
</div>