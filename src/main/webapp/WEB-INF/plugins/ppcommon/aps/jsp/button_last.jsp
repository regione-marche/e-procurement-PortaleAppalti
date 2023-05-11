<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<wp:i18n key="BUTTON_WIZARD_NEXT" var="valueNextButton" />
<wp:i18n key="TITLE_WIZARD_NEXT" var="titleNextButton" />
<s:submit value="%{#attr.valueNextButton}" title="%{#attr.titleNextButton}" cssClass="button" method="last"></s:submit>
