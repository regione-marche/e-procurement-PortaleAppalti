<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<wp:i18n key="BUTTON_WIZARD_CANCEL" var="valueCancelButton" />
<wp:i18n key="TITLE_WIZARD_CANCEL" var="titleCancelButton" />
<s:submit value="%{#attr.valueCancelButton}" title="%{#attr.titleCancelButton}" cssClass="button" method="cancel"></s:submit>
