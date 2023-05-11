<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<wp:i18n key="BUTTON_WIZARD_BACK_TO_EDIT" var="valueBackToEditButton" />
<s:submit value="%{#attr.valueBackToEditButton}" cssClass="button" />