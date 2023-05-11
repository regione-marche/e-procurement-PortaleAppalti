<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<wp:i18n key="BUTTON_ATTACH_FILE" var="valueAddDocUltButton" />
<wp:i18n key="TITLE_ATTACH_FILE" var="titleAddDocUltButton" />
<s:submit value="%{#attr.valueAddDocUltButton}" title="%{#attr.titleAddDocUltButton}" cssClass="button" method="addUltDoc" />