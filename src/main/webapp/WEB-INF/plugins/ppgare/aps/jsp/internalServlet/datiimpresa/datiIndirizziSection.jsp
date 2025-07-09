<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>

<s:set var="sessionId">${param.sessionIdObj}</s:set>
<s:set var="helper" value="%{#session[#sessionId]}"/>

<es:checkCustomization var="needPrefisso" objectId="IMPRESA-RECAPITITEL" attribute="FORMATOSIMAP" feature="ACT" />

<s:if test="%{delete}">
	<s:set var="ind"  value="%{#helper.indirizziImpresa.get(idDelete)}"/>
	<p class="question">
		<wp:i18n key="LABEL_QUESTION_CONFIRM_DEL_INDIRIZZO" /> <s:property value="%{#ind.indirizzo}"/>
		<s:property value="%{#ind.numCivico}"/> <s:property value="%{#ind.comune}"/>?
	</p>
	<div class="azioni">
		<s:hidden name="idDelete" value="%{idDelete}"/>
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_confirm_delete.jsp" />
	</div>
</s:if>
<s:else>
	<s:if test="%{#helper.indirizziImpresa.size() > 0}">

		<p class="noscreen">[ <a href="#" ><wp:i18n key="LABEL_SKIP_INDIRIZZI" /></a> ]</p>

		<fieldset>
			<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_ELENCO_INDIRIZZI" /></legend>

			<table class="wizard-table" summary="<wp:i18n key="ELENCO_INDIRIZZI_TABELLA_SUMMARY" />">
				<tr>
					<th scope="col"><wp:i18n key="LABEL_TIPO_INDIRIZZO" /></th>
					<th scope="col"><wp:i18n key="LABEL_INDIRIZZO" /></th>
					<th scope="col"><wp:i18n key="LABEL_COMUNE" /></th>
					<th scope="col"><wp:i18n key="LABEL_PROVINCIA" /></th>
					<th scope="col"><wp:i18n key="ACTIONS" /></th>
				</tr>
				<s:iterator value="#helper.indirizziImpresa" status="status">
					<tr>
						<td>
							<s:iterator value="maps['tipiIndirizzo']">
								<s:if test="%{key == tipoIndirizzo}"><s:property value="%{value}"/></s:if>
							</s:iterator>
						</td>
						<td><s:property value="%{indirizzo}"/> <s:property value="%{numCivico}"/></td>
						<td><s:property value="%{comune}"/></td>
						<td>
							<s:iterator value="maps['province']">
								<s:if test="%{key == provincia}"><s:property value="%{value}"/></s:if>
							</s:iterator>
						</td>
						<td class="azioni">
							<ul>
								<c:choose>
									<c:when test="${skin == 'highcontrast' || skin == 'text'}">
										<li>
											<a href='<wp:action path="/ExtStr2/do/FrontEnd/${param.namespace}/modifyIndirizzoImpresa.action"/>&amp;id=${status.index}&amp;ext=${param.ext}' 
												 title="<wp:i18n key="TITLE_AZIONE_MODIFICA_INDIRIZZO" />">
												<wp:i18n key="LABEL_AZIONE_MODIFICA_INDIRIZZO" />
											</a>
										</li>
										<li>
											<a href='<wp:action path="/ExtStr2/do/FrontEnd/${param.namespace}/confirmDeleteIndirizzoImpresa.action"/>&amp;idDelete=${status.index}&amp;ext=${param.ext}' 
												 title="<wp:i18n key="TITLE_AZIONE_ELIMINA_INDIRIZZO" />">
												<wp:i18n key="LABEL_AZIONE_ELIMINA_INDIRIZZO" />
											</a>
										</li>
									</c:when>
									<c:otherwise>
										<li>
											<a href='<wp:action path="/ExtStr2/do/FrontEnd/${param.namespace}/modifyIndirizzoImpresa.action"/>&amp;id=${status.index}&amp;ext=${param.ext}' 
												 title="<wp:i18n key="TITLE_AZIONE_MODIFICA_INDIRIZZO" />" class="bkg modify">
											</a>
										</li>
										<li>
											<a href='<wp:action path="/ExtStr2/do/FrontEnd/${param.namespace}/confirmDeleteIndirizzoImpresa.action"/>&amp;idDelete=${status.index}&amp;ext=${param.ext}' 
												 title="<wp:i18n key="TITLE_AZIONE_ELIMINA_INDIRIZZO" />" class="bkg delete">
											</a>
										</li>
									</c:otherwise>
								</c:choose>
							</ul>
						</td>
					</tr>
				</s:iterator>
			</table>
		</fieldset>
	</s:if>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/skip_form_buttons.jsp" />

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/mandatory_fields_message.jsp" />

	<fieldset>
		<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span>
			<c:choose>
				<c:when test="${empty id}">
					<wp:i18n key="LABEL_NUOVO_INDIRIZZO" />
				</c:when>
				<c:otherwise>
					<wp:i18n key="LABEL_MODIFICA_INDIRIZZO" />
				</c:otherwise>
			</c:choose>
		</legend>
		
		<div class="fieldset-row first-row">
			<div class="label">
				<label for="tipoIndirizzo"><wp:i18n key="LABEL_TIPO_INDIRIZZO" /> : <span class="required-field">*</span></label>
			</div>
			<div class="element">
				<wp:i18n key="OPT_CHOOSE_TIPO_INDIRIZZO" var="headerValueTipoIndirizzo" />
				<s:select list="maps['tipiIndirizzo']" name="tipoIndirizzo" id="tipoIndirizzo" value="{tipoIndirizzo}"
									headerKey="" headerValue="%{#attr.headerValueTipoIndirizzo}" aria-required="true" >
				</s:select>
			</div>
		</div>

		<div class="fieldset-row">
			<div class="label">
				<label><wp:i18n key="LABEL_INDIRIZZO" /> : </label>
			</div>
			<div class="element">
				<div class="contents-group">
					<label for="indirizzo"><wp:i18n key="LABEL_INDIRIZZO" /> : <span class="required-field">*</span></label>
					<s:textfield name="indirizzo" id="indirizzo" value="%{indirizzo}" 
											 size="40" maxlength="100" aria-required="true" />
					<label for="numCivico"><wp:i18n key="LABEL_NUM_CIVICO" /> : <span class="required-field">*</span></label>
					<s:textfield name="numCivico" id="numCivico" value="%{numCivico}" 
											 size="4" maxlength="10" aria-required="true" />
				</div>
				<div class="contents-group">
					<label for="nazione"><wp:i18n key="LABEL_NAZIONE" /> : <span class="required-field">*</span></label>
					<wp:i18n key="OPT_CHOOSE_NAZIONE" var="headerValueNazione" />
					<s:select list="maps['nazioni']" name="nazione" id="nazione" value="{nazione}"
										headerKey="" headerValue="%{#attr.headerValueNazione}" aria-required="true" >
					</s:select>
				</div>
				<div class="contents-group">
					<label for="cap" class="cap"><wp:i18n key="LABEL_CAP" /> : <span id="obbCap" class="required-field">*</span></label>
					<s:textfield name="cap" id="cap" value="%{cap}" cssClass="cap" 
											 size="5" maxlength="5" aria-required="true" />
					<label for="comune"><wp:i18n key="LABEL_COMUNE" /> : <span class="required-field">*</span></label>
					<s:textfield name="comune" id="comune" value="%{comune}" 
											 size="30" maxlength="100" aria-required="true" />
				</div>
				<div class="contents-group">
					<label for="provincia" class="provincia"><wp:i18n key="LABEL_PROVINCIA" /> : <span id="obbProv" class="required-field">*</span></label>
					<wp:i18n key="OPT_CHOOSE_PROVINCIA" var="headerValueProvincia" /> 
					<s:select list="maps['province']" name="provincia" id="provincia" value="%{provincia}" cssClass="provincia"
										headerKey="" headerValue="%{#attr.headerValueProvincia}" aria-required="true" >
					</s:select>
				</div>
				<div class="note">
					<wp:i18n key="LABEL_NOTA_PROVINCIA_SOLO_PER_ITALIA" />
				</div>
			</div>
		</div>

		<div class="fieldset-row last-row">
			<div class="label">
				<label><wp:i18n key="LABEL_RECAPITI" /> : </label>
			</div>
			<div class="element">
				<div class="contents-group">
					<label for="telefono"><wp:i18n key="LABEL_TELEFONO" /> :</label>
					<s:textfield name="telefono" id="telefono" value="%{telefono}" 
											 size="20" maxlength="50" />
					<label for="fax"><wp:i18n key="LABEL_FAX" /> :</label>
					<s:textfield name="fax" id="fax" value="%{fax}" 
											 size="20" maxlength="20" />
				</div>
				<div class="note">
					<c:choose>
						<c:when test="${needPrefisso}"><wp:i18n key="DATI_IMPRESA_INFO_TELEFONO" /></c:when>
						<c:otherwise><wp:i18n key="DATI_IMPRESA_INFO_TELEFONO_NO_RECAP" /></c:otherwise>
					</c:choose>
				</div>
			</div>
		</div>

		<div class="azioni">
			<c:choose>
				<c:when test="${empty id}">
					<wp:i18n key="BUTTON_ADD" var="valueAddButton" />
					<s:submit value="%{#attr.valueAddButton}" title="Aggiungi l'indirizzo" cssClass="button" method="insert"></s:submit>
				</c:when>
				<c:otherwise>
					<wp:i18n key="BUTTON_REFRESH" var="valueRefreshButton" />
					<s:submit value="%{#attr.valueRefreshButton}" title="Salva le modifiche all'indirizzo esistente" cssClass="button" method="save"></s:submit>
					<wp:i18n key="BUTTON_NEW" var="valueNewButton" />
					<s:submit value="%{#attr.valueNewButton}" title="Avvia la compilazione di un nuovo indirizzo" cssClass="button" method="add"></s:submit>
				</c:otherwise>
			</c:choose>
		</div>
	</fieldset>
</s:else>


<%-- gestione CAP per nazione estera --%>
<script type="text/javascript">
//<![CDATA[
	$(document).ready(function() {

		nazioneChange();
	
		$('[id="nazione"]').change(function() {
			nazioneChange();
		});
	});

	function nazioneChange() {
 		var newval = $('[id="nazione"] option:selected').val();
 		var oldval = $('[id="nazione"]').data("value");
 		$('[id="nazione"]').data("value", newval); 
 		if(newval.toUpperCase() == "ITALIA") {
			$('#obbCap').show();
			$("#obbProv").show();
			$('.cap').show();
			$('.provincia').show();
		} else {
			$('#obbCap').hide();
            $("#obbProv").hide();
			$('#provincia').val("");
			$('.cap').hide();
			$('.provincia').hide();
		}
		if(newval != oldval && oldval != null) {
			$('[id="cap"]').val("");
		}
	}
//]]>
</script>