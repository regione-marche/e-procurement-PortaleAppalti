<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<wp:i18n key="BUTTON_WIZARD_SAVE_DOCUMENTI" var="valueSaveDocsButton" />
<wp:i18n key="TITLE_WIZARD_SAVE_DOCUMENTI" var="titleSaveDocsButton" />
<s:submit value="%{#attr.valueSaveDocsButton}" title="%{#attr.titleSaveDocsButton}" cssClass="button block-ui" method="save" ></s:submit>
