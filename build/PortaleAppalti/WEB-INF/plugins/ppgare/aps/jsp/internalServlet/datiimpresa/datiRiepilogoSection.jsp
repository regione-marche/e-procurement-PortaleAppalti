<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<s:set var="sessionId">${param.sessionIdObj}</s:set>

<fieldset>
	<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_DATI_PRINCIPALI_OE" /></legend>
	
	<div class="fieldset-row first-row">
		<div class="label">
			<label><wp:i18n key="LABEL_RAGIONE_SOCIALE" /> : </label>
		</div>
		<div class="element">
			<span>
				<s:property value="%{#session[#sessionId].datiPrincipaliImpresa.ragioneSociale}"/>
			</span>
		</div>
	</div>

	<div class="fieldset-row">
		<div class="label">
			<label><wp:i18n key="LABEL_TIPO_IMPRESA" /> : </label>
		</div>
		<div class="element">
			<span>
				<s:if test="%{#session[#sessionId].datiPrincipaliImpresa.tipoImpresa.length() > 0}">
					<s:iterator value="maps['tipiImpresaIscrAlbo']">
						<s:if test="%{key == #session[#sessionId].datiPrincipaliImpresa.tipoImpresa}">
							<s:property value="%{value}"/><br/>
						</s:if>
					</s:iterator>
				</s:if>
				<s:else><wp:i18n key="LABEL_NOT_DEFINED" /></s:else>
			</span>
		</div>
	</div>

	<div class="fieldset-row">
		<div class="label">
			<label><wp:i18n key="LABEL_NATURA_GIURIDICA" /> : </label>
		</div>
		<div class="element">
			<span>
				<s:if test="%{#session[#sessionId].datiPrincipaliImpresa.naturaGiuridica.length() > 0}">
					<s:iterator value="maps['tipiNaturaGiuridica']">
						<s:if test="%{key == #session[#sessionId].datiPrincipaliImpresa.naturaGiuridica}">
							<s:property value="%{value}"/><br/>
						</s:if>
					</s:iterator>
				</s:if>
				<s:else><wp:i18n key="LABEL_NOT_DEFINED" /></s:else>
			</span>
		</div>
	</div>

	<div class="fieldset-row">
		<div class="label">
			<label><wp:i18n key="LABEL_CODICE_FISCALE" /> : </label>
		</div>
		<div class="element">
			<span>
				<s:property value="%{#session[#sessionId].datiPrincipaliImpresa.codiceFiscale}"/>
			</span>
		</div>
	</div>

	<div class="fieldset-row">
		<div class="label">
			<label><wp:i18n key="LABEL_PARTITA_IVA" /> : </label>
		</div>
		<div class="element">
			<span>
				<s:property value="%{#session[#sessionId].datiPrincipaliImpresa.partitaIVA}"/>
			</span>
		</div>
	</div>
	
	<div class="fieldset-row">
		<div class="label">
			<label><wp:i18n key="LABEL_SEDE_LEGALE" /> : </label>
		</div>
		<div class="element">
			<span>
				<s:property value="%{#session[#sessionId].datiPrincipaliImpresa.indirizzoSedeLegale}"/>
				<s:property value="%{#session[#sessionId].datiPrincipaliImpresa.numCivicoSedeLegale}"/>,
				<s:property value="%{#session[#sessionId].datiPrincipaliImpresa.capSedeLegale}"/>
				<s:property value="%{#session[#sessionId].datiPrincipaliImpresa.comuneSedeLegale}"/>
				<c:if test="${! empty sessionScope[param.sessionIdObj].datiPrincipaliImpresa.provinciaSedeLegale}">
					(<s:property value="%{#session[#sessionId].datiPrincipaliImpresa.provinciaSedeLegale}"/>)
				</c:if>
				- <s:property value="%{#session[#sessionId].datiPrincipaliImpresa.nazioneSedeLegale}"/>
			</span>
		</div>
	</div>

	<div class="fieldset-row">
		<div class="label">
			<label><wp:i18n key="LABEL_EMAIL" />: </label>
		</div>
		<div class="element">
			<span>
				<c:if test="${! empty sessionScope[param.sessionIdObj].datiPrincipaliImpresa.emailRecapito}">
					<label><wp:i18n key="LABEL_EMAIL" /> : </label>
					<s:property value="%{#session[#sessionId].datiPrincipaliImpresa.emailRecapito}"/>
				</c:if>
				<c:if test="${! empty sessionScope[param.sessionIdObj].datiPrincipaliImpresa.emailPECRecapito}">
					<label><wp:i18n key="LABEL_PEC" /> : </label>
					<s:property value="%{#session[#sessionId].datiPrincipaliImpresa.emailPECRecapito}"/>
				</c:if>
			</span>
		</div>
	</div>

	<div class="fieldset-row">
		<div class="label">
			<label><wp:i18n key="LABEL_LEGALI_RAPPRESENTANTI" /> : </label>
		</div>

		<div class="element">
			<s:if test="%{#session[#sessionId].legaliRappresentantiImpresa.size() > 0}">
				<ul class="list">
					<s:iterator value="%{#session[#sessionId].legaliRappresentantiImpresa.iterator()}" var="legaleRappresentante" status="stat">
						<li class='<s:if test="%{#stat.first}">first</s:if> <s:if test="%{#stat.last}">last</s:if>'>
							<s:property value="%{#legaleRappresentante.cognome}"/> <s:property value="%{#legaleRappresentante.nome}"/> 
							<c:if test="${! empty legaleRappresentante.dataInizioIncarico}">
							<wp:i18n key="LABEL_DA_DATA" /> <s:property value="%{#legaleRappresentante.dataInizioIncarico}"/>
							</c:if> 
							<c:if test="${! empty legaleRappresentante.dataFineIncarico}">
							<wp:i18n key="LABEL_A_DATA" /> <s:property value="%{#legaleRappresentante.dataFineIncarico}"/>
							</c:if>
						</li>
					</s:iterator>
				</ul>
			</s:if>
			<s:else><wp:i18n key="LABEL_NOT_DEFINED" /></s:else>
		</div>
	</div>

	<div class="fieldset-row last-row">
		<div class="label">
			<label><wp:i18n key="LABEL_DIRETTORI_TECNICI" /> : </label>
		</div>
		<div class="element">
				<s:if test="%{#session[#sessionId].direttoriTecniciImpresa.size() > 0}">
				<ul class="list">
					<s:iterator value="%{#session[#sessionId].direttoriTecniciImpresa.iterator()}" var="direttoreTecnico" status="stat">
						<li class='<s:if test="%{#stat.first}">first</s:if> <s:if test="%{#stat.last}">last</s:if>'>
							<s:property value="%{#direttoreTecnico.cognome}"/> <s:property value="%{#direttoreTecnico.nome}"/> 
							<c:if test="${! empty direttoreTecnico.dataInizioIncarico}">
							<wp:i18n key="LABEL_DA_DATA" /> <s:property value="%{#direttoreTecnico.dataInizioIncarico}"/>
							</c:if> 
							<c:if test="${! empty direttoreTecnico.dataFineIncarico}">
							<wp:i18n key="LABEL_A_DATA" /> <s:property value="%{#direttoreTecnico.dataFineIncarico}"/>
							</c:if>
						</li>
					</s:iterator>
				</ul>
			</s:if>
			<s:else><wp:i18n key="LABEL_NOT_DEFINED" /></s:else>
		</div>
	</div>
</fieldset>