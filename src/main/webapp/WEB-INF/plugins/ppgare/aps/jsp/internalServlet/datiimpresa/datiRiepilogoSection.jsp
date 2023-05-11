<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<s:set var="helper" value="" />
<c:set var="chelper" value="" />

<s:if test="%{#session.dettIscrAlbo != null}">
	<s:set var="helper" value="%{#session.dettIscrAlbo}" />
	<c:set var="chelper" value="${sessionScope['dettIscrAlbo']}"/>
</s:if>
<s:elseif test="%{#session.dettRinnAlbo != null}">
	<s:set var="helper" value="%{#session.dettRinnAlbo}" />
	<c:set var="chelper" value="${sessionScope['dettRinnAlbo']}"/>
</s:elseif>

<s:if test="%{#session.dettAnagrImpresa != null}">
	<s:set var="helper" value="%{#session.dettAnagrImpresa}" />
	<c:set var="chelper" value="${sessionScope['dettAnagrImpresa']}"/>
</s:if>
<s:elseif test="%{#session.dettRegistrImpresa != null}">
	<s:set var="helper" value="%{#session.dettRegistrImpresa}" />
	<c:set var="chelper" value="${sessionScope['dettRegistrImpresa']}"/>
</s:elseif>

<s:if test="%{#session.dettaglioOffertaGara != null}">
	<s:set var="helper" value="%{#session.dettaglioOffertaGara.impresa}" />
	<c:set var="chelper" value="${sessionScope['dettaglioOffertaGara'].impresa}"/>
</s:if>

<s:set var="abilitaVatGroup">${abilitaVatGroup}</s:set>


<fieldset>
	<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_DATI_PRINCIPALI_OE" /></legend>
	
	<div class="fieldset-row first-row">
		<div class="label">
			<label><wp:i18n key="LABEL_RAGIONE_SOCIALE" /> : </label>
		</div>
		<div class="element">
			<span>
				<s:property value="%{#helper.datiPrincipaliImpresa.ragioneSociale}"/>
			</span>
		</div>
	</div>

	<div class="fieldset-row">
		<div class="label">
			<label><wp:i18n key="LABEL_TIPO_IMPRESA" /> : </label>
		</div>
		<div class="element">
			<span>
				<s:if test="%{#helper.datiPrincipaliImpresa.tipoImpresa.length() > 0}">
					<s:iterator value="maps['tipiImpresaIscrAlbo']">
						<s:if test="%{key == #helper.datiPrincipaliImpresa.tipoImpresa}">
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
				<s:if test="%{#helper.datiPrincipaliImpresa.naturaGiuridica.length() > 0}">
					<s:iterator value="maps['tipiNaturaGiuridica']">
						<s:if test="%{key == #helper.datiPrincipaliImpresa.naturaGiuridica}">
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
			<label><wp:i18n key="LABEL_AMBITO_TERRITORIALE" /> : </label>
		</div>
		<div class="element">
			<span>
				<s:if test='%{ !"".equals(#helper.datiPrincipaliImpresa.ambitoTerritoriale) }'>
					<s:iterator value="maps['ambitoTerritoriale']">
						<s:if test="%{key == #helper.datiPrincipaliImpresa.ambitoTerritoriale}">
							<s:property value="%{value}"/><br/>
						</s:if>
					</s:iterator>
				</s:if>
				<s:else><wp:i18n key="LABEL_NOT_DEFINED" /></s:else>
			</span>
		</div>
	</div>

	<s:if test='%{"1".equals(#helper.datiPrincipaliImpresa.vatGroup)}'>
		<div class="fieldset-row">
			<div class="label">
				<label for="vatGroup"><span><wp:i18n key="LABEL_VAT_GROUP" /></span> : </label>
			</div>
			<div class="element">
				<s:set var="valueVatGroup" value="%{#helper.datiPrincipaliImpresa.vatGroup}" />
				<c:if test="${empty valueVatGroup}" >
					<s:set var="valueVatGroup" value="0" />
				</c:if>
				<s:iterator value="maps['sino']">
					<s:if test="%{key == #valueVatGroup}">
						<s:property value="%{value}"/><br/>
					</s:if>
				</s:iterator>
			</div>
		</div>	
	</s:if>
		
	<s:if test='%{#helper.datiPrincipaliImpresa.ambitoTerritoriale == "1"}' >
		<%-- operatore italiano --%>
		<div class="fieldset-row">
			<div class="label">
				<label><wp:i18n key="LABEL_CODICE_FISCALE" /> : </label>
			</div>
			<div class="element">
				<span>
					<s:property value="%{#helper.datiPrincipaliImpresa.codiceFiscale}"/>
				</span>
			</div>
		</div>
		
		<div class="fieldset-row">
			<div class="label">
				<label><wp:i18n key="LABEL_PARTITA_IVA" /> : </label>
			</div>
			<div class="element">
				<span>
					<s:property value="%{#helper.datiPrincipaliImpresa.partitaIVA}"/>
				</span>
			</div>
		</div>
	</s:if>	
	<s:elseif test='%{#helper.datiPrincipaliImpresa.ambitoTerritoriale == "2"}' >
		<%-- operatore UE o extra UE --%>
		<div class="fieldset-row">
			<div class="label">
				<label><wp:i18n key="LABEL_NAZIONE" /> : </label>
			</div>
			<div class="element">
				<s:property value="%{#helper.datiPrincipaliImpresa.nazioneSedeLegale}"/>
			</div>
		</div>
		
		<div class="fieldset-row">
			<div class="label">
				<label><wp:i18n key="LABEL_IDENTIFICATIVO_FISCALE_ESTERO" /> : </label>
			</div>
			<div class="element">
				<span>
					<s:property value="%{#helper.datiPrincipaliImpresa.codiceFiscale}"/>
				</span>
			</div>
		</div>	
	</s:elseif>
	
	<div class="fieldset-row">
		<div class="label">
			<label><wp:i18n key="LABEL_SEDE_LEGALE" /> : </label>
		</div>
		<div class="element">
			<span>
				<s:property value="%{#helper.datiPrincipaliImpresa.indirizzoSedeLegale}"/>
				<s:property value="%{#helper.datiPrincipaliImpresa.numCivicoSedeLegale}"/>,
				<s:property value="%{#helper.datiPrincipaliImpresa.capSedeLegale}"/>
				<s:property value="%{#helper.datiPrincipaliImpresa.comuneSedeLegale}"/>
				<c:if test="${! empty chelper.datiPrincipaliImpresa.provinciaSedeLegale}">
					(<s:property value="%{#helper.datiPrincipaliImpresa.provinciaSedeLegale}"/>)
				</c:if>
				- <s:property value="%{#helper.datiPrincipaliImpresa.nazioneSedeLegale}"/>
			</span>
		</div>
	</div>

	<div class="fieldset-row">
		<div class="label">
			<label><wp:i18n key="LABEL_EMAIL" />: </label>
		</div>
		<div class="element">
			<span>
				<c:if test="${! empty chelper.datiPrincipaliImpresa.emailRecapito}">
					<label><wp:i18n key="LABEL_EMAIL" /> : </label>
					<s:property value="%{#helper.datiPrincipaliImpresa.emailRecapito}"/>
				</c:if>
				<c:if test="${! empty chelper.datiPrincipaliImpresa.emailPECRecapito}">
					<label><wp:i18n key="LABEL_PEC" /> : </label>
					<s:property value="%{#helper.datiPrincipaliImpresa.emailPECRecapito}"/>
				</c:if>
			</span>
		</div>
	</div>

	<div class="fieldset-row">
		<div class="label">
			<label><wp:i18n key="LABEL_LEGALI_RAPPRESENTANTI" /> : </label>
		</div>

		<div class="element">
			<s:if test="%{#helper.legaliRappresentantiImpresa.size() > 0}">
				<ul class="list">
					<s:iterator value="%{#helper.legaliRappresentantiImpresa.iterator()}" var="legaleRappresentante" status="stat">
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
				<s:if test="%{#helper.direttoriTecniciImpresa.size() > 0}">
				<ul class="list">
					<s:iterator value="%{#helper.direttoriTecniciImpresa.iterator()}" var="direttoreTecnico" status="stat">
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