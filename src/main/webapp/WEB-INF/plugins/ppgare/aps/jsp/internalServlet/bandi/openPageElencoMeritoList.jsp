<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>

<es:checkCustomization var="withAdvancedUI" objectId="UI-ADVANCED" attribute="DATATABLE" feature="ACT" />

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
    <jsp:param name="skin" value="${param.skin}" />
    <jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-list">

    <h2><wp:i18n key="TITLE_PAGE_ELENCO_OPERATORI_ISCRITTI" /></h2>
    <jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
        <jsp:param name="keyMsg" value="BALLOON_ELENCO_OPERATORI_ISCRITTI"/>
    </jsp:include>

    <jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />

    <form class="form-ricerca" action="<wp:action path="/ExtStr2/do/FrontEnd/Bandi/openPageImpreseElenco.action" />" method="post" >
        <jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
        <input type="hidden" name="currentFrame" value="7" />
        <s:hidden name="isRer" id="isRer" value="%{isRer}" />
        <!-- Elenco operatori abilitati elenco -->

        <fieldset>
            <legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_SEARCH_CRITERIA" /></legend>

            <!-- Ragione sociale -->
            <div class="fieldset-row first-row">
                <div class="label">
                    <label for="model.businessName"><wp:i18n key="LABEL_RAGIONE_SOCIALE" /> : </label>
                </div>
                <div class="element">
                    <s:textfield name="model.businessName" id="model.businessName" value="%{model.businessName}" size="60" maxlength="2000" />
                </div>
            </div>

            <!-- CODICE FISCALE -->
            <div class="fieldset-row">
                <div class="label">
                    <label for="model.fiscalCode"><wp:i18n key="LABEL_CODICE_FISCALE" /> : </label>
                </div>
                <div class="element">
                    <s:textfield name="model.fiscalCode" id="model.fiscalCode" value="%{model.fiscalCode}" maxlength="16" size="60" />
                </div>
            </div>

            <!-- COMUNE -->
            <div class="fieldset-row">
                <div class="label">
                    <label for="model.city"><wp:i18n key="LABEL_COMUNE" /> : </label>
                </div>
                <div class="element">
                    <s:textfield name="model.city" id="model.city" value="%{model.city}" size="30" maxlength="100" />
                </div>
            </div>

            <!-- Provincia -->
            <div class="fieldset-row">
                <div class="label">
                    <label for="model.province" class="provincia"><wp:i18n key="LABEL_PROVINCIA" /> : </label>
                </div>
                <div class="element">
                    <wp:i18n key="OPT_CHOOSE_PROVINCIA" var="headerValueProvincia" />
                    <s:select list="maps['province']" name="model.province" id="model.province" value="%{model.province}"
                              headerKey="" headerValue="%{#attr.headerValueProvincia}" aria-required="true" />
                </div>
            </div>

            <!-- Rating -->
            <s:if test="%{isRer}">
                <div class="fieldset-row">
                    <div class="label">
                        <label for="model.rating"><wp:i18n key="LABEL_OPERATORI_ISCRITTI_RATING" />: </label>
                    </div>
                    <div class="element">

                        <s:select list="maps['listaRating']" name="model.rating" id="model.rating" value="%{model.rating}"
                                  headerKey="" headerValue=""
                                  aria-required="true">
                        </s:select>
                    </div>
                </div>
            </s:if>

            <!-- Codice Elenco -->
            <s:if test="%{!isRer}">
                <div class="fieldset-row">
                    <div class="label">
                        <label for="model.codElenco"><wp:i18n key="LABEL_ELENCO" /> : </label>
                    </div>
                    <div class="element">
                        <s:textfield name="model.codElenco" id="model.codElenco" value="%{model.codElenco}" size="30" maxlength="100" />
                    </div>
                </div>
            </s:if>

            <!-- Stato -->
            <div class="fieldset-row">
                <div class="label">
                    <label for="model.stato"><wp:i18n key="LABEL_STATO" />: </label>
                </div>
                <div class="element">
                    <wp:i18n key="OPT_CHOOSE_STATO_GARA" var="headerValueStato" />
                    <s:select list="maps['statiElenco']" name="model.stato" id="model.stato" value="%{model.stato}"
                              headerKey="" headerValue="%{#attr.headerValueStato}"
                              aria-required="true">
                    </s:select>
                </div>
            </div>

            <!-- Categoria -->
            <div class="fieldset-row">
                <div class="label">
                        <label for="model.category"><wp:i18n key="LABEL_CATEGORIA_CODE_DESCR" /> : </label>
                </div>
                <div class="element">
                    <s:textfield name="model.category" id="model.category" value="%{model.category}" size="60" maxlength="60" />
                </div>
            </div>

            <!-- Classificazione -->
            <div class="fieldset-row">
                <div class="label">
                    <label for="model.classification" style="text-transform: capitalize"><wp:i18n key="LABEL_CLASSIFICA" /> : </label>
                </div>
                <div class="element">
                    <s:textfield name="model.classification" id="model.classification" value="%{model.classification}" size="60" maxlength="60" />
                </div>
            </div>

            <div class="fieldset-row last-row">
                <div class="label">
                    <label for="model.iDisplayLength"><s:property value="%{getText('label.rowsPerPage')}" /> : </label>
                </div>
                <div class="element">
                    <select name="model.iDisplayLength" id="model.iDisplayLength" class="text">
                        <option <s:if test="%{model.iDisplayLength==10}">selected="selected"</s:if> value="10">10</option>
                        <option <s:if test="%{model.iDisplayLength==20}">selected="selected"</s:if> value="20">20</option>
                        <option <s:if test="%{model.iDisplayLength==50}">selected="selected"</s:if> value="50">50</option>
                        <option <s:if test="%{model.iDisplayLength==100}">selected="selected"</s:if> value="100">100</option>
                    </select>
                </div>
            </div>

            <div class="azioni">
                <wp:i18n key="BUTTON_SEARCH" var="valueSearchButton" />
                <s:submit value="%{#attr.valueSearchButton}" cssClass="button block-ui"/>
            </div>

        </fieldset>

    <s:if test="%{listaElenchi.dati.size() > 0}">

        <div class="list-summary">
            <wp:i18n key="SEARCH_RESULTS_INTRO" />
            <s:property value="%{model.iTotalDisplayRecords}" />
            <wp:i18n key="SEARCH_RESULTS_OUTRO" />.
        </div>

        <s:iterator var="riga" value="listaElenchi.dati">
            <div class="list-item">
                <div class="list-item-row">
                    <label><wp:i18n key="LABEL_RAGIONE_SOCIALE" /> : </label>
                    <s:property value="businessName" />
                </div>
                <div class="list-item-row">
                    <label><wp:i18n key="LABEL_CODICE_FISCALE" /> : </label>
                    <s:property value="fiscalCode" />
                </div>
                <div class="list-item-row">
                    <label><wp:i18n key="LABEL_COMUNE" /> : </label>
                    <s:property value="city" />
                </div>
                <div class="list-item-row">
                    <label><wp:i18n key="LABEL_PROVINCIA" /> : </label>
                    <s:property value="province" />
                </div>
                <s:if test="%{isRer}">
                    <div class="list-item-row">
                        <label><wp:i18n key="LABEL_OPERATORI_ISCRITTI_RATING" /> : </label>
                        <s:property value="rating" />
                    </div>
                </s:if>
                <s:if test="%{!isRer}">
                    <div class="list-item-row">
                        <label><wp:i18n key="LABEL_ELENCO" /> : </label>
                        <s:property value="codElenco" />
                    </div>
                </s:if>
                <div class="list-item-row">
                    <label><wp:i18n key="LABEL_STATO" /> : </label>
                    <s:if test="stato">
                        <s:property value='%{maps["statiElenco"].get("1")}' />
                    </s:if>
                    <s:else>
                        <s:property value='%{maps["statiElenco"].get("0")}' />
                    </s:else>
                </div>

                <div class="list-item-row">
                    <ul>
                    <s:iterator var="categoria" value="categories">
                        <li>
                            <label><wp:i18n key="LABEL_CATEGORIA" /> : </label><s:property value="category" />
                            <s:if test="%{classifica != null && !classifica.isEmpty()}">
                            <br/>
                            <label style="text-transform: capitalize"><wp:i18n key="LABEL_CLASSIFICA" /> : </label><s:property value="classifica" />
                            </s:if>
                        </li>
                    </s:iterator>
                    </ul>
                </div>
            </div>
        </s:iterator>

        <s:url id="urlExport" namespace="/do/FrontEnd/Bandi" action="exportElenchiOperatoriAbilitati">
            <s:param name="last" value="1" />
        </s:url>

        <p>
            <a href='<s:property value="%{#urlExport}" />' class="important"><wp:i18n key="LINK_EXPORT_CSV" /></a>
        </p>

        <jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/pagination.jsp" />

    </s:if>
    <s:else>
    	<%-- Accessibility Fix Criterion 3.2.2: insert an invisible "submit" button as workaraound --%>
		<input disabled="disabled" type="submit" style="display:none;"/>
        <div class="list-summary">
            <wp:i18n key="SEARCH_RESULTS_INTRO" />
            0
            <wp:i18n key="SEARCH_RESULTS_OUTRO" />.
        </div>
    </s:else>

</form>


</div>