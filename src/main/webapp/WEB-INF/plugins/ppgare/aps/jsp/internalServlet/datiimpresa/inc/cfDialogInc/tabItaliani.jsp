<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<div id="Italiano" class="tabcontent">
    <div class="fieldset-row first-row">
        <div class="label">
            <label for="dialogCognome"><wp:i18n key="LABEL_COGNOME" /> : <span class="required-field">*</span></label>
        </div>
        <div class="element">
            <s:textfield name="dialogCognome" id="dialogCognome" value="%{cognome}" 
                size="40" maxlength="80" aria-required="true"/>
        </div>
    </div>
    <div class="fieldset-row">
        <div class="label">
            <label for="dialogNomeI"><wp:i18n key="LABEL_NOME" /> : <span class="required-field">*</span></label>
        </div>
        <div class="element">
            <s:textfield name="dialogNome" id="dialogNomeI" value="%{nome}" 
                size="40" maxlength="80" aria-required="true"/>
        </div>
    </div>
    <div class="fieldset-row">
        <div class="label">
            <label for="dialogSessoI"><wp:i18n key="LABEL_SESSO" /> : <span class="required-field">*</span></label>
        </div>
        <div class="element">
            <wp:i18n key="OPT_CHOOSE_SESSO" var="headerValueSesso" />
            <s:select list="maps['sesso']" name="dialogSesso" id="dialogSessoI" value=""
                            headerKey="" headerValue="%{#attr.headerValueSesso}"
                            aria-required="true">
            </s:select>
        </div>
    </div>
    <div class="fieldset-row">
        <div class="label">
            <label for="dialogDataNascita"><wp:i18n key="LABEL_DATI_NASCITA_NATO_IL" /> (<wp:i18n key="LABEL_FORMATO_DATA" />) : <span class="required-field">*</span></label>
        </div>
        <div class="element">
            <s:textfield name="dialogDataNascita" id="dialogDataNascita"  value="%{dataNascita}" 
                                size="40" maxlength="10"
                                aria-required="true" />
        </div>
    </div>
    <div class="fieldset-row">  
        <div class="label">
            <label for="dialogComuneNascitaI"> <wp:i18n key="LABEL_DATI_NASCITA_NATO_A" /> : <span class="required-field">*</span></label>
        </div>
        <div class="element">
            <s:textfield name="dialogComuneNascita" id="dialogComuneNascitaI" value="%{comuneNascita}" 
                size="40" maxlength="100" readonly="%{readonlyComuneNascita}"
                aria-required="true" />
        </div>
    </div>
    <div class="fieldset-row">
        <div class="label">
            <label for="dialogProvinciaNascitaI"> <wp:i18n key="LABEL_PROVINCIA_NASCITA" /> :</label>
        </div>
        <div class="element">
            <wp:i18n key="OPT_CHOOSE_PROVINCIA" var="headerValueProvincia" />
            <s:select list="maps['province']" name="dialogProvinciaNascita" id="dialogProvinciaNascitaI" value="%{provinciaNascita}"
                                headerKey="" headerValue="%{#attr.headerValueProvincia}" />
        </div>
    </div>
</div>