<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<wp:i18n key="BUTTON_WIZARD_CONFIRM_CANCEL" var="valueConfirmCancelButton" />
<s:submit value="%{#attr.valueConfirmCancelButton}" cssClass="button block-ui" />