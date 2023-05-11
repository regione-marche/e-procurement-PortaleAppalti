<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<wp:i18n key="BUTTON_WIZARD_PREVIOUS" var="valuePreviousButton" />
<wp:i18n key="TITLE_WIZARD_PREVIOUS" var="titlePreviousButton" />
<s:submit value="%{#attr.valuePreviousButton}" title="%{#attr.titlePreviousButton}" cssClass="button" method="back"></s:submit>
