<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

    <h2><wp:i18n key="TITLE_PAGE_GARETEL_ACCESSO_DOCUMENTI" /></h2>
    
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_GARA_TEL_ACCESSO_DOCUMENTI"/>
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_messages.jsp" />

 
	<%-- DOCUMENTAZIONE DI GARA --%>
	<div class="detail-section">
		<h3 class="detail-section-title">
			<span class="noscreen"><wp:i18n key="LABEL_SECTION" /></span><wp:i18n key="LABEL_ATTI_DOCUMENTI" />
		</h3>

		<div class="fieldset-row">
			<div class="label">
				<label><wp:i18n key="LABEL_DOCUMENTS" /> : </label>
			</div>
			<div class="element">
				<s:set var="documenti" value="documentiGara" />
				<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/garetel/espletamento/espletGaraDocumenti.jsp">
					<jsp:param name="riservati" value="true"/>
				</jsp:include>
			</div>
		</div>
	</div>
	
	<%-- AGGIUDICATARIE --%>
	<s:if test="%{aggiudicatarie != null}">
		<s:iterator var="aggiudicataria" value="aggiudicatarie" status="stat">
			<div class="detail-section">
				<h3 class="detail-section-title">
					<span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_AGGIUDICATARIA" />
				</h3>
	
			 	<div class="fieldset-row">
					<div class="label">
						<label>
							<s:if test="%{!#aggiudicataria.rti}"><wp:i18n key="LABEL_RAGIONE_SOCIALE" />:</s:if>
							<s:else><wp:i18n key="LABEL_DENOMINAZIONE_RTI" /></s:else>
						</label>
					</div>
					<div class="element">
						${aggiudicataria.ragioneSociale}
					</div>
			 	</div>
			 	
			 	<%-- solo se l'operatore partecipa in RTI --%>
			 	<s:if test="%{#aggiudicataria.rti}">
				 	<div class="fieldset-row">
						<div class="label">
							<label><wp:i18n key="LABEL_RAGGRUPPAMENTO_COMPOSTO_DA" />: </label>
						</div>
				
						<div class="element">
							<div class="table-container">
								<table id="tableOperatori" summary="Tabella operatori" class="info-table">
									<thead>
										<tr>
											<th scope="col"><wp:i18n key="LABEL_CODICE_FISCALE" /></th>
											<th scope="col"><wp:i18n key="LABEL_RAGIONE_SOCIALE" /></th>
											<th scope="col"><wp:i18n key="LABEL_MANDATARIA" /></th>
										</tr>
									</thead>
									<tbody>
										<s:iterator var="item" value="#aggiudicataria.componentiRTI" status="stat">
											<tr>
												<td>
													<s:if test="%{#item.partitaIva != null}" >
														<s:property value="#item.partitaIva" />
													</s:if>
													<s:else>
														<s:property value="#item.codiceFiscale" />
													</s:else>
												</td>
												<td>
													<s:property value="#item.ragioneSociale" />
												</td>
												<td>
													<s:if test="%{#item.mandataria == 1}"><wp:i18n key="LABEL_YES" /></s:if><s:else><wp:i18n key="LABEL_NO" /></s:else>
												</td>
											</tr>
										</s:iterator>
									</tbody>
								</table>
							</div>
						</div>
				 	</div>
				</s:if> 
	
				<div class="fieldset-row">
					<div class="label">
						<label><wp:i18n key="LABEL_DOCUMENTI_ALLEGATI" /> : </label>
					</div>
					<div class="element">
						<s:set var="documenti" value="#aggiudicataria.accessoDocumenti" />
						<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/garetel/espletamento/espletGaraDocumenti.jsp">
							<jsp:param name="riservati" value="true"/>
						</jsp:include>
					</div>
				</div>
			</div>
		</s:iterator>
	</s:if>
		
	<%-- II,III,IV,IV CLASSIFICATA --%>
	<s:if test="%{classificate != null && classificate.size > 0}">
		<s:iterator var="classificata" value="classificate" status="stat">
			<div class="detail-section">
				<h3 class="detail-section-title">
					<span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_CONCORRENTE" /> <s:property value="%{stat.index + 2}"/>
				</h3>
	
			 	<div class="fieldset-row">
					<div class="label">
						<label>
							<s:if test="%{!#classificata.rti}"><wp:i18n key="LABEL_RAGIONE_SOCIALE" />:</s:if>
							<s:else><wp:i18n key="LABEL_DENOMINAZIONE_RTI" /></s:else>
						</label>
					</div>
					<div class="element">
						${classificata.ragioneSociale}
					</div>
			 	</div>
			 	
			 	<%-- solo se l'operatore partecipa in RTI --%>
			 	<s:if test="%{#classificata.rti}">
				 	<div class="fieldset-row">
						<div class="label">
							<label><wp:i18n key="LABEL_RAGGRUPPAMENTO_COMPOSTO_DA" />: </label>
						</div>
				
						<div class="element">
							<div class="table-container">
								<table id="tableOperatori" summary="Tabella operatori" class="info-table">
									<thead>
										<tr>
											<th scope="col"><wp:i18n key="LABEL_CODICE_FISCALE" /></th>
											<th scope="col"><wp:i18n key="LABEL_RAGIONE_SOCIALE" /></th>
											<th scope="col"><wp:i18n key="LABEL_MANDATARIA" /></th>
										</tr>
									</thead>
									<tbody>
										<s:iterator var="item" value="#classificata.componentiRTI" status="stat">
											<tr>
												<td>
													<s:if test="%{#item.partitaIva != null}" >
														<s:property value="#item.partitaIva" />
													</s:if>
													<s:else>
														<s:property value="#item.codiceFiscale" />
													</s:else>
												</td>
												<td>
													<s:property value="#item.ragioneSociale" />
												</td>
												<td>
													<s:if test="%{#item.mandataria == 1}"><wp:i18n key="LABEL_YES" /></s:if><s:else><wp:i18n key="LABEL_NO" /></s:else>
												</td>
											</tr>
										</s:iterator>
									</tbody>
								</table>
							</div>
						</div>
				 	</div>
				</s:if> 
	
				<div class="fieldset-row">
					<div class="label">
						<label><wp:i18n key="LABEL_DOCUMENTI_ALLEGATI" /> : </label>
					</div>
					<div class="element">
						<s:set var="documenti" value="#classificata.accessoDocumenti" />
						<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/garetel/espletamento/espletGaraDocumenti.jsp">
							<jsp:param name="riservati" value="true"/>
						</jsp:include>
					</div>
				</div>
			</div>
		</s:iterator>
	</s:if>
	
</div>

<div class="back-link">
	<s:if test="%{lottiDistinti}" >
		<c:set var="href" value="/ExtStr2/do/FrontEnd/GareTel/espletGaraViewAccessoDocumentiLotti.action" />
		<a href="<wp:action path="${href}" />&amp;codice=${param.codice}">
			<wp:i18n key="LINK_BACK" />
		</a>
	</s:if>
	<s:else>
		<c:set var="href" value="/ExtStr2/do/FrontEnd/Bandi/view.action" />
		<a href="<wp:action path="${href}" />&amp;codice=${param.codice}&amp;ext=${param.ext}">
			<wp:i18n key="LINK_BACK" />
		</a>
	</s:else>
</div>
