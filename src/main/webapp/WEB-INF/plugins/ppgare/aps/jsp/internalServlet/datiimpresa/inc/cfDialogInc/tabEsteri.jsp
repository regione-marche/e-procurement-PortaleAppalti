<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<div id="Estero" class="tabcontent">
    <table class="cfDialogTable">
        <div class="fieldset-row first-row">
            <div class="label">
                <label for="dialogCognomeE"><wp:i18n key="LABEL_COGNOME" /> : <span class="required-field">*</span></label>
            </div>
            <div class="element">
                <s:textfield name="dialogCognome" id="dialogCognomeE" value="%{cognome}" 
                    size="40" maxlength="80" aria-required="true"/>
            </div>
        </div>
        <div class="fieldset-row">
            <div class="label">
                <label for="dialogNomeE"><wp:i18n key="LABEL_NOME" /> : <span class="required-field">*</span></label>
            </div>
            <div class="element">
                <s:textfield name="dialogNome" id="dialogNomeE" value="%{nome}" 
                    size="40" maxlength="80" aria-required="true"/>
            </div>
        </div>
        <div class="fieldset-row">
            <div class="label">
                <label for="dialogSessoE"><wp:i18n key="LABEL_SESSO" /> : <span class="required-field">*</span></label>
            </div>
            <div class="element">
                <wp:i18n key="OPT_CHOOSE_SESSO" var="headerValueSesso" />
                <s:select list="maps['sesso']" name="dialogSesso" id="dialogSessoE" value="%{sesso}"
                                headerKey="" headerValue="%{#attr.headerValueSesso}" readonly="%{readonlySesso}"
                                aria-required="true" >
                </s:select>
            </div>
        </div>
        <div class="fieldset-row">
            <div class="label">
                <label for="dialogDataNascitaE"><wp:i18n key="LABEL_DATI_NASCITA_NATO_IL" /> (<wp:i18n key="LABEL_FORMATO_DATA" />) : <span class="required-field">*</span></label>
            </div>
            <div class="element">
                <s:textfield name="dialogDataNascita" id="dialogDataNascitaE"  value="%{dataNascita}" 
                                    size="40" maxlength="10" readonly="%{readonlyDataNascita}"
                                    aria-required="true" />
            </div>
        </div>
        <div class="fieldset-row">
            <div class="label">
                <label for="dialogNazioneE"> <wp:i18n key="LABEL_NAZIONE" /> : </label>
            </div>
            <div class="element">
                <select name="dialogNazione" id="dialogNazioneE" >
                    <option value=""><wp:i18n key="OPT_CHOOSE_NAZIONE" /></option>
                </select>
            </div>
        </div>
    </table>
</div>