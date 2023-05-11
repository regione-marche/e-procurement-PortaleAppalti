<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags"%>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>

<es:checkCustomization var="visInviaComunicazioni" objectId="COMUNICAZIONI" attribute="INVIARISPONDI" feature="VIS" />
<es:checkCustomization var="visVendorRating" objectId="AREARISERVATA" attribute="VENDORRATING" feature="VIS" />
<es:checkCustomization var="visAlbi" objectId="AREAPERSONALE" attribute="ALBI" feature="VIS" />

<c:set var="profilo" value="${sessionScope.currentUser.profile}" scope="page"/>

<script src="<wp:resourceURL/>static/js/masonry.pkgd.js"></script>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp">
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2><wp:i18n key="TITLE_PAGE_AREA_PERSONALE" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_AREA_PERSONALE" />
	</jsp:include>

	<c:choose>
		<c:when test="${sessionScope.currentUser != 'guest'}">

			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_messages.jsp" />

			<div id="box-container">

			<fieldset class="floating-box agid-box">
				<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_AREA_PERSONALE_PROFILO" /></legend>

				<ul class="options-list">
					<c:choose>
						<c:when test="${sessionScope.currentUser.credentialsNotExpired || sessionScope.accountSSO != null}">
							<c:if test="${! empty profilo}">
								<li>
									<a href="<wp:url page="ppgare_impr_visdati" />?${tokenHrefParams}" title="<wp:i18n key="TITLE_AREA_PERSONALE_DATI_OP" />"><wp:i18n key="LINK_AREA_PERSONALE_DATI_OP" /></a>
								</li>
							</c:if>
							<s:if test="%{#session.accountSSO == null}">
								<li>
									<a href="<wp:url page="ppcommon_cambia_password" />?${tokenHrefParams}" title="<wp:i18n key="TITLE_AREA_PERSONALE_CAMBIA_PSW" />"><wp:i18n key="LINK_AREA_PERSONALE_CAMBIA_PSW" /></a>
								</li>
							</s:if>
							<c:if test="${showAbilitaAccessoCon}">
								<li>
									<a href="<wp:url page="ppcommon_abilita_accesso_sso" />?${tokenHrefParams}" title="<wp:i18n key="TITLE_AREA_PERSONALE_ABILITA_ACCESSO_CON" />"><wp:i18n key="LINK_AREA_PERSONALE_ABILITA_ACCESSO_CON" /></a>
								</li>
							</c:if>
							<c:if test="${showEliminaLinkAccountSSo}">
								<li>
									<a href="<wp:url page="ppcommon_abilita_accesso_sso" />?${tokenHrefParams}" title="<wp:i18n key="TITLE_AREA_PERSONALE_ABILITA_ACCESSO_CON" />"><wp:i18n key="LINK_AREA_PERSONALE_DISABILITA_ACCESSO_CON" /></a>
								</li>
							</c:if>

							<s:if test="%{#session.accountSSO != null}">
								<s:if test="%{#session.accountSSO.tipologiaLogin != 'MAGGIOLI_AUTH_SSO_BUSINESS'}">
									<li>
										<a href="<wp:url page="ppcommon_area_soggetto_sso" />?${tokenHrefParams}" title="<wp:i18n key="TITLE_AREA_PERSONALE_REGISTRA_OP" />"><wp:i18n key="LINK_AREA_PERSONALE_REGISTRA_OP" /></a>
									</li>
								</s:if>
							</s:if>
							<c:if test="${!showMonitoraggio}">
							<li>
								<s:url id="url" namespace="/do/FrontEnd/DatiImpr" action="exportDatiImpresa" />
								<a href='<s:property value="%{url}"/>'
									title="<wp:i18n key="TITLE_AREA_PERSONALE_EXPORT_MXML" />"><wp:i18n key="LINK_AREA_PERSONALE_EXPORT_MXML" />
								</a>
							</li>
							</c:if>
                           <c:if test="${visVendorRating && showVendorRating}">
								<li>
									<a href='<wp:action path="/ExtStr2/do/FrontEnd/AreaPers/openPageVendorRating.action" />&amp;${tokenHrefParams}' title='Vendor Rating'> Vendor Rating</a>
								</li>
                            </c:if>
						</c:when>
					</c:choose>
				</ul>
			</fieldset>
			
			<!-- MONITORAGGIO -->
			<c:if test="${showMonitoraggio}">
				<fieldset class="floating-box agid-box">
					<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_AREA_PERSONALE_MONITORAGGIO" /></legend>
					
					<ul class="options-list">
						<c:choose>
							<c:when test="${sessionScope.currentUser.credentialsNotExpired || sessionScope.accountSSO != null}">
								<li>
									<a href="<wp:url page="ppcommon_registra_op_manuale" />" title="<wp:i18n key="LINK_AREA_PERSONALE_REG_MANUALE_OP" />"><wp:i18n key="LINK_AREA_PERSONALE_REG_MANUALE_OP" /></a>
								</li>
								<li>
									<a href="<wp:url page="ppcommon_admin_op_ko" />" title="<wp:i18n key="LINK_AREA_PERSONALE_OP_KO" />"><wp:i18n key="LINK_AREA_PERSONALE_OP_KO" /></a>
								</li>
								<li>
									<a href="<wp:url page="ppcommon_admin_invii_ko" />" title="<wp:i18n key="LINK_AREA_PERSONALE_FLUSSI_KO" />"><wp:i18n key="LINK_AREA_PERSONALE_FLUSSI_KO" /></a>
								</li>
								<c:if test="${showSbloccaAccount}">
								<li>
									<a href="<wp:url page="ppcommon_admin_sblocca_account" />" title="<wp:i18n key="LINK_AREA_PERSONALE_SBLOCCA_ACCOUNT" />"><wp:i18n key="LINK_AREA_PERSONALE_SBLOCCA_ACCOUNT" /></a>
								</li>
								</c:if>
								<li>
									<a href="<wp:url page="ppcommon_admin_delegateuser" />" title="<wp:i18n key="LINK_AREA_PERSONALE_DELEGATEUSER" />"><wp:i18n key="LINK_AREA_PERSONALE_DELEGATEUSER" /></a>
								</li>
								<li>
									<a href="<wp:url page="ppcommon_admin_searchoe" />" title="<wp:i18n key="LINK_AREA_PERSONALE_SEARCHOE" />"><wp:i18n key="LINK_AREA_PERSONALE_SEARCHOE" /></a>
								</li>
							</c:when>
						</c:choose>
					</ul>
				</fieldset>
			</c:if>
			
			<c:if test="${! empty profilo && (sessionScope.currentUser.credentialsNotExpired || sessionScope.accountSSO != null)}">
				
				<fieldset class="floating-box agid-box">
					<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_AREA_PERSONALE_SERVIZI" /></legend>
	
					<ul class="options-list">
					
						<li> <wp:i18n key="LABEL_AREA_PERSONALE_COMUNICAZIONI" />
							<ul class="options-list">
								<li>
									<a href='<wp:action path="/ExtStr2/do/FrontEnd/Comunicazioni/openPageComunicazioniRicevute.action" />&amp;${tokenHrefParams}' title='<wp:i18n key="TITLE_AREA_PERSONALE_COM_RICEVUTE" />'>	
										<s:property value="%{numComunicazioniRicevute}" /> <wp:i18n key="LINK_AREA_PERSONALE_COM_RICEVUTE" />
										<s:if test="%{numComunicazioniRicevuteDaLeggere > 0}">
											<strong>(<s:property value="%{numComunicazioniRicevuteDaLeggere}" /> <wp:i18n key="LINK_AREA_PERSONALE_COM_RICEVUTE_NON_LETTE" />)</strong>
										</s:if>
									</a>
								</li>
								<li>
									<a href='<wp:action path="/ExtStr2/do/FrontEnd/Comunicazioni/openPageComunicazioniArchiviate.action" />&amp;${tokenHrefParams}' title='<wp:i18n key="TITLE_AREA_PERSONALE_COM_ARCHIVIATE" />'>
										<s:property value="%{numComunicazioniArchiviate}" /> <wp:i18n key="LINK_AREA_PERSONALE_COM_ARCHIVIATE" />
										<s:if test="%{numComunicazioniArchiviateDaLeggere > 0}">
											<strong>&nbsp;(<s:property value="%{numComunicazioniArchiviateDaLeggere}" /> <wp:i18n key="LINK_AREA_PERSONALE_COM_RICEVUTE_NON_LETTE" />)</strong>
										</s:if>
									</a>
								</li>
								<s:if test="%{numSoccorsiIstruttori > 0}">
									<li>
										<a href='<wp:action path="/ExtStr2/do/FrontEnd/Comunicazioni/openPageSoccorsiIstruttori.action" />&amp;${tokenHrefParams}' title='<wp:i18n key="TITLE_AREA_PERSONALE_SOCCORSI_ISTRUTTORI" />'>
											<s:property value="%{numSoccorsiIstruttori}" /> <wp:i18n key="LINK_AREA_PERSONALE_SOCCORSI_ISTRUTTORI" />
											<s:if test="%{numSoccorsiIstruttoriDaLeggere > 0}">
												<strong>&nbsp;(<s:property value="%{numSoccorsiIstruttoriDaLeggere}" /> <wp:i18n key="LINK_AREA_PERSONALE_COM_RICEVUTE_NON_LETTE" />)</strong>
											</s:if>
										</a>
									</li>
								</s:if>
								<c:if test="${visInviaComunicazioni}">
									<li>
										<a href='<wp:action path="/ExtStr2/do/FrontEnd/Comunicazioni/openPageComunicazioniInviate.action" />&amp;${tokenHrefParams}' title='<wp:i18n key="TITLE_AREA_PERSONALE_COM_INVIATE" />'>
										<s:property value="%{numComunicazioniInviate}" /> <wp:i18n key="LINK_AREA_PERSONALE_COM_INVIATE" />
										</a>
									</li>
								</c:if>
							</ul>
						</li>
						<c:if test="${showRichiestaAssistenza}">
							<li>
								<a href="<wp:url page="ppgare_doc_assistenza_tecnica" />?${tokenHrefParams}" title="<wp:i18n key="TITLE_AREA_PERSONALE_RICHIESTA_HELPDESK" />"><wp:i18n key="TITLE_PAGE_ASSISTENZA_TECNICA" /></a>
							</li>
						</c:if>
					</ul>
				</fieldset>
			
				<c:if test="${showBandi || showRichOfferta || showComprovaRequisiti || showProcAggiudicazione || showAsteInCorso}">
					<fieldset class="floating-box agid-box">
						<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_AREA_PERSONALE_PROCEDURE_INTERESSE" /></legend>
						
						<ul class="options-list">
							<c:if test="${showBandi}">
								<li>
									 <a href="<wp:url page="ppgare_bandi_lista" />?${tokenHrefParams}" title="<wp:i18n key="TITLE_AREA_PERSONALE_BANDI_GARA" />"><wp:i18n key="LINK_AREA_PERSONALE_BANDI_GARA" /></a>
								</li>
							</c:if>
							<c:if test="${showRichOfferta}">
								<li>
									<a href="<wp:url page="ppgare_vai_a_rich_offerta" />?${tokenHrefParams}" title="<wp:i18n key="TITLE_AREA_PERSONALE_RICHIESTE_OFFERTA" />"><wp:i18n key="LINK_AREA_PERSONALE_RICHIESTE_OFFERTA" /></a>
								</li>
							</c:if>
							<c:if test="${showComprovaRequisiti}">
								<li>
									<a href="<wp:url page="ppgare_vai_a_rich_documenti" />?${tokenHrefParams}" title="<wp:i18n key="TITLE_AREA_PERSONALE_COMPROVA_REQUISITI" />"><wp:i18n key="LINK_AREA_PERSONALE_COMPROVA_REQUISITI" /></a>
								</li>
							</c:if>
							<c:if test="${showProcAggiudicazione}">
								<li>
									<a href="<wp:url page="ppgare_vai_a_proc_aggiudicaz" />?${tokenHrefParams}" title="<wp:i18n key="TITLE_AREA_PERSONALE_PROC_AGGIUDIC_O_CONCLUSE" />"><wp:i18n key="LINK_AREA_PERSONALE_PROC_AGGIUDIC_O_CONCLUSE" /></a>
								</li>
							</c:if>
							<c:if test="${showAsteInCorso}">
								<li>
									<a href="<wp:url page="ppgare_vai_a_aste_corso" />?${tokenHrefParams}" title="<wp:i18n key="TITLE_AREA_PERSONALE_ASTE" />"><wp:i18n key="LINK_AREA_PERSONALE_ASTE" /></a>
								</li>
							</c:if>
						</ul>
					</fieldset>
				</c:if>
				
				<%-- gare privatistiche acquisto --%>
				<c:if test="${showBandiAcqPriv}">
					<fieldset class="floating-box agid-box">
					<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_AREA_PERSONALE_PROCEDURE_ACQUISTO" /></legend>

						<ul class="options-list">
							<li>
								 <a href="<wp:url page="ppgare_acq_reg_priv_in_corso" />?${tokenHrefParams}" title="<wp:i18n key="TITLE_AREA_PERSONALE_BANDI_GARA" />"><wp:i18n key="LINK_AREA_PERSONALE_BANDI_GARA" /></a>
							</li>
							<li>
								<a href="<wp:url page="ppgare_acq_reg_priv_rich_off" />?${tokenHrefParams}" title="<wp:i18n key="TITLE_AREA_PERSONALE_RICHIESTE_OFFERTA_ACQ" />"><wp:i18n key="LINK_AREA_PERSONALE_RICHIESTE_OFFERTA_ACQ" /></a>
							</li>
							<li>
								<a href="<wp:url page="ppgare_acq_reg_priv_proc_agg" />?${tokenHrefParams}" title="<wp:i18n key="TITLE_AREA_PERSONALE_PROC_AGGIUDIC_ACQ" />"><wp:i18n key="LINK_AREA_PERSONALE_PROC_AGGIUDIC_ACQ" /></a>
							</li>
						</ul>
					</fieldset>
				</c:if>
				
				<c:if test="${showContratti}">
					<fieldset class="floating-box agid-box">
					<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_AREA_PERSONALE_CONTRATTI" /></legend>
						<ul class="options-list">
							<li>
								<a href="<wp:url page="ppcommon_contracts" />?${tokenHrefParams}" title="<wp:i18n key="LINK_AREA_PERSONALE_CONTRATTI" />"><wp:i18n key="LINK_AREA_PERSONALE_CONTRATTI" /></a>
							</li>
						</ul>
					</fieldset>
				</c:if>
				
				<c:if test="${showContrattiLFS}">
					<fieldset class="floating-box agid-box">
					<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_AREA_PERSONALE_CONTRATTI_LFS" /></legend>
						<ul class="options-list">
							<li>
								<a href="<wp:url page="ppgare_contratti_lfs_lista" />?${tokenHrefParams}" title="<wp:i18n key="LINK_AREA_PERSONALE_CONTRATTI_LFS" />"><wp:i18n key="LINK_AREA_PERSONALE_CONTRATTI_LFS" /></a>
							</li>
						</ul>
					</fieldset>
				</c:if>
				
				
				<%-- gare privatistiche vendita --%>
				<c:if test="${showBandiVenPriv}">
					<fieldset class="floating-box agid-box">
					<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_AREA_PERSONALE_PROCEDURE_VENDITA" /></legend>
					
						<ul class="options-list">
							<li>
								 <a href="<wp:url page="ppgare_vend_reg_priv_in_corso" />?${tokenHrefParams}" title="<wp:i18n key="TITLE_AREA_PERSONALE_BANDI_GARA" />"><wp:i18n key="LINK_AREA_PERSONALE_BANDI_GARA" /></a>
							</li>
							<li>
								<a href="<wp:url page="ppgare_vend_reg_priv_rich_off" />?${tokenHrefParams}" title="<wp:i18n key="TITLE_AREA_PERSONALE_RICHIESTE_OFFERTA_VEN" />"><wp:i18n key="LINK_AREA_PERSONALE_RICHIESTE_OFFERTA_VEN" /></a>
							</li>
							<li>
								<a href="<wp:url page="ppgare_vend_reg_priv_proc_agg" />?${tokenHrefParams}" title="<wp:i18n key="TITLE_AREA_PERSONALE_PROC_AGGIUDIC_VEN" />"><wp:i18n key="LINK_AREA_PERSONALE_PROC_AGGIUDIC_VEN" /></a>
							</li>
						</ul>
					</fieldset>
				</c:if>
				
				<c:if test="${showElenchi && visAlbi}">
					<fieldset class="floating-box agid-box">
						<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_ELENCHI_OPERATORI_ECONOMICI" /></legend>						
							<ul class="options-list">
							
								<s:if test="%{showIscrivitiElenco}">
									<s:if test='%{singoloCodiceElenco != null}' >
										<wp:url page="ppgare_oper_ec_bandi_avvisi" var="hrefIscriviti">
											<wp:urlPar name="actionPath">/ExtStr2/do/FrontEnd/Bandi/viewIscrizione.action</wp:urlPar>
											<wp:urlPar name="currentFrame">7</wp:urlPar>
											<wp:urlPar name="codice"><s:property value="%{singoloCodiceElenco}"/></wp:urlPar>
										</wp:url>
										<c:set var='hrefIscriviti'>${hrefIscriviti}&amp;${tokenHrefParams}</c:set>
										<li>
											<wp:i18n key="LABEL_NON_SEI_ANCORA_ISCRITTO" />	<a href="${hrefIscriviti}"><wp:i18n key="LABEL_ISCRIVITI" /></a>
										</li>
									</s:if>
									<s:else>
										<c:set var='hrefIscriviti'><wp:url page='ppgare_oper_ec_bandi_avvisi'/>?${tokenHrefParams}</c:set>
										<li>
											<wp:i18n key="LABEL_NON_SEI_ANCORA_ISCRITTO" />	<a href="${hrefIscriviti}"><wp:i18n key="LABEL_ISCRIVITI" /></a>
										</li>
									</s:else>
								</s:if>
								<s:elseif test="%{elenchi != null && elenchi.size > 0}">
									<s:if test="%{showIscrizioneElenco}" >
										<li> 
											<a href="<wp:url page="ppgare_oper_ec_bandi_avvisi" />?${tokenHrefParams}" title="<wp:i18n key="TITLE_AREA_ICRIZIONE_A_ELENCO" />"><wp:i18n key="LINK_AREA_ICRIZIONE_A_ELENCO" /></a>
										</li>
									</s:if>
									<s:else>
										<li> 
											<a href="<wp:url page="ppgare_oper_ec_bandi_avvisi" />?${tokenHrefParams}" title="<wp:i18n key="TITLE_AREA_PERSONALE_ELENCHI" />"><wp:i18n key="LINK_AREA_PERSONALE_ELENCHI" /></a>
										</li>
									</s:else>

									<li>
										<wp:i18n key="LABEL_AREA_PERSONALE_ELENCHI" />
									</li>
									<li>
										<ul class="options-list">
											<s:iterator value="%{elenchi}">
												<li>
													<wp:url page="ppgare_oper_ec_bandi_avvisi" var="collegamentoDettaglio">
														<wp:urlPar name="actionPath">/ExtStr2/do/FrontEnd/Bandi/viewIscrizione.action</wp:urlPar>
														<wp:urlPar name="currentFrame">7</wp:urlPar>
														<wp:urlPar name="codice"><s:property value="%{codice}"/></wp:urlPar>
													</wp:url>
													<a href="${collegamentoDettaglio}&amp;${tokenHrefParams}"><s:property value="%{oggetto}"/></a>
												</li>
											</s:iterator>
										</ul>
									</li>
								</s:elseif>
							</ul>
					</fieldset>
				</c:if>

				<c:if test="${showCataloghi}">
					<fieldset class="floating-box agid-box">
						<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_MERCATO_ELETTRONICO" /></legend>
							<ul class="options-list">
								<li> 
									<a href="<wp:url page="ppgare_cataloghi_bandi_avvisi" />?${tokenHrefParams}" title="<wp:i18n key="TITLE_AREA_PERSONALE_MERCATI_ELETTRONICI" />"><wp:i18n key="LINK_AREA_PERSONALE_MERCATI_ELETTRONICI" /></a>
								</li>
								<li>
									<wp:i18n key="LABEL_AREA_PERSONALE_MERCATI_ELETTRONICI" />
								</li>
								<li>
									<ul class="options-list">
										<s:iterator value="%{cataloghi}">
											<li>
												<wp:url page="ppgare_cataloghi_bandi_avvisi" var="collegamentoDettaglio">
													<wp:urlPar name="actionPath">/ExtStr2/do/FrontEnd/Cataloghi/viewIscrizione.action</wp:urlPar>
													<wp:urlPar name="currentFrame">7</wp:urlPar>
													<wp:urlPar name="codice"><s:property value="%{codice}"/></wp:urlPar>
												</wp:url>
												<a href="${collegamentoDettaglio}&amp;${tokenHrefParams}"><s:property value="%{oggetto}"/></a>
											</li>
										</s:iterator>
									</ul>
								</li>
							</ul>
					</fieldset>
					<fieldset class="floating-box agid-box">
						<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_AREA_PERSONALE_ORDINI" /></legend>
						<ul class="options-list">
							<li>
								<a href="<wp:url page="ppgare_contratti_ordini_lista" />?${tokenHrefParams}" title="<wp:i18n key="TITLE_AREA_PERSONALE_ORDINI" />"><wp:i18n key="LINK_AREA_PERSONALE_ORDINI" /></a>
							</li>
						</ul>
					</fieldset>
				</c:if>
				
				<c:if test="${showEOrders}">
					<fieldset class="floating-box agid-box">
						<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_AREA_PERSONALE_EORDERS" /></legend>
						<ul class="options-list">
							<li>
								<a href="<wp:url page="ppgare_eorders_davalutare_list" />?${tokenHrefParams}" title="<wp:i18n key="TITLE_AREA_PERSONALE_EORDERS_DA_VALUTARE" />"><wp:i18n key="TITLE_AREA_PERSONALE_EORDERS_DA_VALUTARE" /> (${numEOrdersDaValutare})</a>
							</li>
							<li>
								<a href="<wp:url page="ppgare_eorders_confermati_list" />?${tokenHrefParams}" title="<wp:i18n key="TITLE_AREA_PERSONALE_EORDERS_CONFERMATI" />"><wp:i18n key="TITLE_AREA_PERSONALE_EORDERS_CONFERMATI" /> (${numEOrdersConfermati})</a>
							</li>
							<li>
								<a href="<wp:url page="ppgare_eorders_tutti_list" />?${tokenHrefParams}" title="<wp:i18n key="TITLE_AREA_PERSONALE_EORDERS_TUTTI" />"><wp:i18n key="TITLE_AREA_PERSONALE_EORDERS_TUTTI" /></a>
							</li>
						</ul>
					</fieldset>
				</c:if>
				<c:if test="${showPagoPA}">
					<fieldset class="floating-box agid-box">
						<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_AREA_PERSONALE_PAGOPA" /></legend>
						<ul class="options-list">
							<li>
								<a href="<wp:url page="ppgare_pagopa_nuovo" />?${tokenHrefParams}" title="<wp:i18n key="TITLE_AREA_PERSONALE_PAGOPA_NUOVO" />"><wp:i18n key="TITLE_AREA_PERSONALE_PAGOPA_NUOVO" /></a>
							</li>
							<li>
								<a href="<wp:url page="ppgare_pagopa_effettuati" />?${tokenHrefParams}" title="<wp:i18n key="TITLE_AREA_PERSONALE_PAGOPA_FATTI" />"><wp:i18n key="TITLE_AREA_PERSONALE_PAGOPA_FATTI" /> (${numPagoPAFatti})</a>
							</li>
							<li>
								<a href="<wp:url page="ppgare_pagopa_daeffettuare" />?${tokenHrefParams}" title="<wp:i18n key="TITLE_AREA_PERSONALE_PAGOPA_DAFARE" />"><wp:i18n key="TITLE_AREA_PERSONALE_PAGOPA_DAFARE" /> (${numPagoPADaFare})</a>
							</li>
						</ul>
					</fieldset>
				</c:if>
			</c:if>
			
			</div>
		</c:when>
		<c:otherwise>
			<p>
				<wp:i18n key="LABEL_AREA_PERSONALE_PLEASE_LOGIN" />
			</p>
		</c:otherwise>
	</c:choose>
</div>

<script>
$('#box-container').masonry({
	itemSelector: '.floating-box'
});
</script>