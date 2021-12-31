<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<wp:i18n key="BUTTON_EDIT" var="valueEditButton" />
<wp:i18n key="TITLE_EDIT_DATI_IMPRESA" var="titleEditDatiImpresaButton" />
<s:submit value="%{#attr.valueEditButton}" title="%{#attr.titleEditDatiImpresaButton}" cssClass="button" ></s:submit>
