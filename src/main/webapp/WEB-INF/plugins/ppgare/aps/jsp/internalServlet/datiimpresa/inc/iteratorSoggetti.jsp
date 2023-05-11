<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>

<s:iterator value="#elencoSoggetti" status="status">
	<tr>
		<td>
			<s:iterator value="maps['tipiSoggetto']">
				<s:if test="%{key == soggettoQualifica}"><s:property value="%{value}"/></s:if>
			</s:iterator>
			<s:iterator value="maps['tipiAltraCarica']">
				<s:if test="%{key == soggettoQualifica}"><s:property value="%{value}"/></s:if>
			</s:iterator>
			<s:iterator value="maps['tipiCollaborazione']">
				<s:if test="%{key == soggettoQualifica}"><s:property value="%{value}"/></s:if>
			</s:iterator>
		</td>
		<td><s:property value="%{cognome}"/> <s:property value="%{nome}"/></td>
		<td><s:property value="%{codiceFiscale}"/></td>
		<td class="date-content"><s:property value="%{dataInizioIncarico}"/></td>
		<td class="date-content"><s:property value="%{dataFineIncarico}"/></td>
		<td class="azioni">
			<ul>
				<c:choose>
					<c:when test="${skin == 'highcontrast' || skin == 'text'}">
						<li>
							<a href='<wp:action path="/ExtStr2/do/FrontEnd/${param.namespace}/modifySoggettoImpresa.action"/>&amp;tipoSoggetto=${tipoSoggetto}&amp;id=${status.index}&amp;ext=${param.ext}&amp;${tokenHrefParams}'
								 title="<wp:i18n key="TITLE_AZIONE_MODIFICA_SOGGETTO" />">
								<wp:i18n key="LABEL_AZIONE_MODIFICA_SOGGETTO" />
							</a>
						</li>
						<li>
							<a href='<wp:action path="/ExtStr2/do/FrontEnd/${param.namespace}/copySoggettoImpresa.action"/>&amp;tipoSoggetto=${tipoSoggetto}&amp;id=${status.index}&amp;ext=${param.ext}&amp;${tokenHrefParams}'
								 title="<wp:i18n key="TITLE_AZIONE_COPIA_SOGGETTO" />">
								<wp:i18n key="LABEL_AZIONE_COPIA_SOGGETTO" />
							</a>
						</li>
						<s:if test="%{not esistente}">
							<li>
								<a href='<wp:action path="/ExtStr2/do/FrontEnd/${param.namespace}/confirmDeleteSoggettoImpresa.action"/>&amp;tipoSoggettoDelete=${tipoSoggetto}&amp;idDelete=${status.index}&amp;id=${status.index}&amp;ext=${param.ext}&amp;${tokenHrefParams}'
									 title="<wp:i18n key="TITLE_AZIONE_ELIMINA_SOGGETTO" />">
									<wp:i18n key="LABEL_AZIONE_ELIMINA_SOGGETTO" />
								</a>
							</li>
						</s:if>
					</c:when>
					<c:otherwise>
						<li>
							<a class="bkg modify" href='<wp:action path="/ExtStr2/do/FrontEnd/${param.namespace}/modifySoggettoImpresa.action"/>&amp;tipoSoggetto=${tipoSoggetto}&amp;id=${status.index}&amp;ext=${param.ext}&amp;${tokenHrefParams}'
								 title="<wp:i18n key="TITLE_AZIONE_MODIFICA_SOGGETTO" />">
							</a>
						</li>
						<li>
							<a class="bkg copy" href='<wp:action path="/ExtStr2/do/FrontEnd/${param.namespace}/copySoggettoImpresa.action"/>&amp;tipoSoggetto=${tipoSoggetto}&amp;id=${status.index}&amp;ext=${param.ext}&amp;${tokenHrefParams}'
								 title="<wp:i18n key="TITLE_AZIONE_COPIA_SOGGETTO" />">
							</a>
						</li>
						<s:if test="%{not esistente}">
							<li>
								<a class="bkg delete" href='<wp:action path="/ExtStr2/do/FrontEnd/${param.namespace}/confirmDeleteSoggettoImpresa.action"/>&amp;tipoSoggettoDelete=${tipoSoggetto}&amp;idDelete=${status.index}&amp;ext=${param.ext}&amp;${tokenHrefParams}'
									 title="<wp:i18n key="TITLE_AZIONE_ELIMINA_SOGGETTO" />">
								</a>
							</li>
						</s:if>
					</c:otherwise>
				</c:choose>
			</ul>
		</td>
	</tr>
</s:iterator>