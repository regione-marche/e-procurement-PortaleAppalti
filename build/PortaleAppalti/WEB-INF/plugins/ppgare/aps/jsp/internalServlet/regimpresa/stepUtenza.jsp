<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<es:checkCustomization var="visConsensoPrivacy" objectId="REG-IMPRESA" attribute="PRIVACY" feature="VIS" />
<es:checkCustomization var="bloccaLoginUgualeCodiceFiscale" objectId="REG-IMPRESA" attribute="LOGINCF" feature="ACT" />
<es:checkCustomization var="registrazioneManuale" objectId="REG-IMPRESA" attribute="VERIFICAMANUALE" feature="ACT" />

<s:set var="codiceFiscale" value="%{#session.dettRegistrImpresa.datiPrincipaliImpresa.codiceFiscale}" />
<s:set var="ragioneSociale" value="%{#session.dettRegistrImpresa.datiPrincipaliImpresa.ragioneSociale}" />
<c:set var="ssoAttivo" value="${ssoProtocollo == 1 || ssoProtocollo == 2}"/>

<c:set var="bloccaLogin" value="${0}"/>
<%--
<c:if test="${ssoAttivo && registrazioneManuale}" >
	<c:set var="bloccaLogin" value="${1}"/>
</c:if>
 --%>

<c:set var="titoloFieldsetUtenza" value="LABEL_REGISTRA_OE_UTENZA"/>
<c:set var="codiceBalloon" value="BALLOON_REG_IMPRESA_UTENZA"/>
<c:if test="${bloccaLoginUgualeCodiceFiscale}">
	<c:set var="titoloFieldsetUtenza" value="LABEL_REGISTRA_OE_UTENZA_LOGINCF"/>
	<c:set var="codiceBalloon" value="BALLOON_REG_IMPRESA_UTENZA_LOGINCF"/>
</c:if>

<s:set var="helper" value="%{#session.dettRegistrImpresa}" />


<%-- 
helper: <s:property value="%{#helper}" /><br/>
liberoProfessionista: <s:property value="%{#helper.liberoProfessionista}" /><br/>
dittaIndividuale: <s:property value="%{#helper.dittaIndividuale}" /><br/>
accountSSO: <s:property value="%{#session.accountSSO}" /><br/>
ssoPresente = ${ssoPresente}<br/>
registrazioneManuale = ${registrazioneManuale}<br/>
ssoProtocollo=${ssoProtocollo}<br/>
ssoAttivo=${ssoAttivo}<br/>
--%>


<div class="portgare-view">

	<h2><wp:i18n key="TITLE_PAGE_REGISTRA_OE" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/field_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />

	<jsp:include page="stepsRegistrazione.jsp" />

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="${codiceBalloon}"/>
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/skip_form_buttons.jsp" />

	<form action="<wp:action path="/ExtStr2/do/FrontEnd/RegistrImpr/processPageUtenza.action" />" method="post">
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
		
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/mandatory_fields_message.jsp" />
		
		<fieldset>
			<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="${titoloFieldsetUtenza}" /></legend>
	
			<c:choose>
				<c:when test="${bloccaLoginUgualeCodiceFiscale || (bloccaLogin == 1)}">
					<div class="fieldset-row">
						<div class="label">
							<label for="usernamereg"><wp:i18n key="USERNAME" /> : </label>
						</div>
						<div class="element">
							<div class="contents-group">
								<s:property value="%{#codiceFiscale}" /> 
								<s:hidden name="username" id="usernamereg" value="%{#codiceFiscale}"/>
								<s:hidden name="usernameConfirm" id="usernameConfirm" value="%{#codiceFiscale}"/>
								<%--
								<div class="note">
									Attenzione: questo portale assegna <strong>in automatico</strong> il nome utente uguale al codice fiscale impresa.
								</div>
								 --%>
								<%--
								<c:if test="${ssoPresente}">
									<div class="note">
										<wp:i18n key="LABEL_REGISTRA_OE_USERNAME_NOTA_SSO" />
									</div>
								</c:if>
								--%>
							</div>
						</div>
					</div>
				</c:when>
				<c:otherwise>
					<div class="fieldset-row">
						<div class="label">
							<label for="usernamereg"><wp:i18n key="USERNAME" /> : <span class="required-field">*</span></label>
						</div>
						<div class="element">
							<div class="contents-group">
								<s:textfield name="username" id="usernamereg" maxlength="20" size="20" aria-required="true" />
								<div class="note">
									<wp:i18n key="LABEL_REGISTRA_OE_USERNAME_NOTA_1" /><br/>
									<strong><wp:i18n key="LABEL_REGISTRA_OE_USERNAME_NOTA_2" /></strong>
								</div>
								<c:if test="${ssoPresente}">
									<div class="note">
										<wp:i18n key="LABEL_REGISTRA_OE_USERNAME_NOTA_SSO" />
									</div>
								</c:if>
							</div>
						</div>
					</div>

					<div class="fieldset-row">
						<div class="label">
							<label for="usernameConfirm"><wp:i18n key="LABEL_USERNAME_CONFIRM" /> : <span class="required-field">*</span></label>
						</div>
						<div class="element">
							<div class="contents-group">
								<s:textfield name="usernameConfirm" id="usernameConfirm" maxlength="20" size="20" aria-required="true" />
							</div>
							<div class="note"><wp:i18n key="LABEL_REGISTRA_OE_USERNAME_CONFIRM_NOTA" /></div>
						</div>
					</div>
				</c:otherwise>
			</c:choose>
	
		</fieldset>
		
		<!-- CONSENSO UTILIZZO PIATTAFORMA -->
		<fieldset>
			<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_REGISTRA_OE_REGOLE_UTILIZZO_PIATTAFORMA" /></legend>
		
			<p><wp:i18n key="LABEL_REGISTRA_OE_IL_SOTTOSCRITTO" />
			<s:if test="%{#session.accountSSO != null}">
				<%-- SINGLE SIGN ON --%>
				${sessionScope.accountSSO.nome} ${sessionScope.accountSSO.cognome} (${sessionScope.accountSSO.login}),
				
				<s:hidden name="soggettoRichiedente" id="soggettoRichiedente" 
					value="%{#session.accountSSO.nome} %{#session.accountSSO.cognome} (%{#session.accountSSO.login})" />
					
			</s:if>
			<s:elseif test="%{#helper.liberoProfessionista}">	<%-- <s:if test="%{#helper.liberoProfessionista || #helper.dittaIndividuale}">  --%>				
				<%-- LIBERO PROFESSIONISTA --%>
					<s:property value="%{#session.dettRegistrImpresa.datiPrincipaliImpresa.ragioneSociale}"/> (<s:property value="%{#helper.datiPrincipaliImpresa.codiceFiscale}"/>),
					<s:hidden name="soggettoRichiedente" id="soggettoRichiedente" 
						value="%{#session.dettRegistrImpresa.datiPrincipaliImpresa.ragioneSociale} (%{#helper.datiPrincipaliImpresa.codiceFiscale})"/>
			</s:elseif>
			<s:else>
				<%-- DITTA --%>
				<select id="soggettoRichiedente" name="soggettoRichiedente" title="<wp:i18n key="LABEL_REGISTRA_OE_IL_SOTTOSCRITTO" />">
				
 					<option value=''><wp:i18n key="OPT_CHOOSE_REGISTRA_OE_SOGGETTO_RICHIEDENTE" /></option>
				
					<%-- LEGALI RAPPRESENTANTI --%>
					<s:iterator var="item" value="%{#helper.legaliRappresentantiImpresa}" status="status">
						<s:set var="cfpiva">
							<s:if test="%{#item.codiceFiscale != null}"> 
								<s:property value="#item.codiceFiscale"/>
							</s:if>
							<s:else>
								<s:property value="#item.partitaIva"/>
							</s:else>
						</s:set>
						
						<c:set var="value"><s:property value="#item.nome"/> <s:property value="#item.cognome"/> (<s:property value="%{cfpiva}"/>)</c:set>
						<option value="${value}" <c:if test='${soggettoRichiedente == value}'>selected="selected"</c:if> >						
							<s:property value="#item.nome"/> <s:property value="#item.cognome"/> (<s:property value="%{cfpiva}"/>)   
						</option>
					</s:iterator>
					
					<%-- DIRETTORI TECNICI --%>
					<s:iterator var="item" value="%{#helper.direttoriTecniciImpresa}" status="status">
						<s:set var="cfpiva">
							<s:if test="%{#item.codiceFiscale != null}"> 
								<s:property value="#item.codiceFiscale"/>
							</s:if>
							<s:else>
								<s:property value="#item.partitaIva"/>
							</s:else>
						</s:set>
							
						<c:set var="value"><s:property value="#item.nome"/> <s:property value="#item.cognome"/> (<s:property value="%{cfpiva}"/>)</c:set>
						<option value="${value}" <c:if test='${soggettoRichiedente == value}'>selected="selected"</c:if> >
							<s:property value="#item.nome"/> <s:property value="#item.cognome"/> (<s:property value="%{cfpiva}"/>)   
						</option>
					</s:iterator>
					
					<%-- ALTRE CARICHE --%>
					<s:iterator var="item" value="%{#helper.altreCaricheImpresa}" status="status">
						<s:set var="cfpiva">
							<s:if test="%{#item.codiceFiscale != null}"> 
								<s:property value="#item.codiceFiscale"/>
							</s:if>
							<s:else>
								<s:property value="#item.partitaIva"/>
							</s:else>
						</s:set>
							
						<c:set var="value"><s:property value="#item.nome"/> <s:property value="#item.cognome"/> (<s:property value="%{cfpiva}"/>)</c:set>
						<option value="${value}" <c:if test='${soggettoRichiedente == value}'>selected="selected"</c:if> >
							<s:property value="#item.nome"/> <s:property value="#item.cognome"/> (<s:property value="%{cfpiva}"/>)   
						</option>
					</s:iterator>
					
					<%-- COLLABORATORI --%>
					<s:iterator var="item" value="%{#helper.collaboratoriImpresa}" status="status">
						<s:set var="cfpiva">
							<s:if test="%{#item.codiceFiscale != null}"> 
								<s:property value="#item.codiceFiscale"/>
							</s:if>
							<s:else>
								<s:property value="#item.partitaIva"/>
							</s:else>
						</s:set>

						<c:set var="value"><s:property value="#item.nome"/> <s:property value="#item.cognome"/> (<s:property value="%{cfpiva}"/>)</c:set>
						<option value="${value}" <c:if test='${soggettoRichiedente == value}'>selected="selected"</c:if> >
							<s:property value="#item.nome"/> <s:property value="#item.cognome"/> (<s:property value="%{cfpiva}"/>)   
						</option>
					</s:iterator>
				</select>,
				
			</s:else>
		   <wp:i18n key="LABEL_REGISTRA_OE_SOGGETTO_RICHIEDENTE_DICHIARA_1" />
		   <s:property value="ragioneSociale"/> (<wp:i18n key="LABEL_CODICE_FISCALE_ABBR" /> <s:property value="codiceFiscale"/>) 
		   <wp:i18n key="LABEL_REGISTRA_OE_SOGGETTO_RICHIEDENTE_DICHIARA_2" />
		   <a href="<wp:resourceURL/>cms/documents/Regole_utilizzo_piattaforma_telematica.pdf" >[<wp:i18n key="HERE" />]</a>
		   </p>
		   
		   	<div class="fieldset-row first-row last-row">
				<div class="label">
					<label><wp:i18n key="LABEL_UTILIZZO_PIATTAFORMA" /> : <span class="required-field">*</span></label>
				</div>
				<div class="element">
					<input type="radio" name="utilizzoPiattaforma" id="utilizzoPiattaforma_yes" value="1" <s:if test="%{utilizzoPiattaforma.intValue() == 1}">checked="checked"</s:if> /><label for="utilizzoPiattaforma_yes"><wp:i18n key="LABEL_REGISTRA_OE_ACCETTO" /></label>
					<input type="radio" name="utilizzoPiattaforma" id="utilizzoPiattaforma_no" value="0" <s:if test="%{utilizzoPiattaforma.intValue() == 0}">checked="checked"</s:if> /><label for="utilizzoPiattaforma_no"><wp:i18n key="LABEL_REGISTRA_OE_NON_ACCETTO" /></label>
				</div>
			</div>
		</fieldset>

		<!-- CONSENSO PRIVACY -->
		<c:choose>
			<c:when test="${visConsensoPrivacy}">
				<fieldset>
					<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_PRIVACY" /></legend>
					
					<p><wp:i18n key="LABEL_REGISTRA_OE_PRIVACY_NOTA" /></p>
					
					<wp:i18n key="LABEL_REGISTRA_OE_PRIVACY_EXTRA" />
				
					<div class="fieldset-row first-row last-row">
						<div class="label">
							<label><wp:i18n key="LABEL_PRIVACY" /> : <span class="required-field">*</span></label>
						</div>
						<div class="element">
							<input type="radio" name="privacy" id="privacy_yes" value="1" <s:if test="%{privacy.intValue() == 1}">checked="checked"</s:if> /><label for="privacy_yes"><wp:i18n key="LABEL_REGISTRA_OE_ACCETTO" /></label>
							<input type="radio" name="privacy" id="privacy_no" value="0" <s:if test="%{privacy.intValue() == 0}">checked="checked"</s:if> /><label for="privacy_no"><wp:i18n key="LABEL_REGISTRA_OE_NON_ACCETTO" /></label>
						</div>
					</div>
				</fieldset>
			</c:when>
			<c:otherwise>
				<input type="hidden" name="privacy" value="1"/>
			</c:otherwise>
		</c:choose>

		<input type="hidden" name="visConsensoPrivacy" value="${visConsensoPrivacy}"/>

		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/skip_main_content_from_buttons.jsp" />

		<div class="azioni">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_previous.jsp" />
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_next.jsp" />
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_cancel.jsp" />
		</div>
	</form>
</div>