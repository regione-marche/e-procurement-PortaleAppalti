<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<wp:i18n key="BUTTON_WIZARD_BACK_TO_MENU" var="valueBackToMenuButton" />
<s:submit value="< %{#attr.valueBackToMenuButton}" title="%{#attr.valueBackToMenuButton}" cssClass="button" method="back"></s:submit>
