<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<wp:i18n key="LABEL_YES" var="valueYesButton" />
<wp:i18n key="LABEL_NO" var="valueNoButton" />
<wp:i18n key="TITLE_CONFIRM_DEL_YES" var="titleYesButton" />
<wp:i18n key="TITLE_CONFIRM_DEL_NO" var="titleNoButton" />
<s:submit value="%{#attr.valueYesButton}" title="%{#attr.titleYesButton}"  cssClass="button block-ui" method="delete"></s:submit>
<s:submit value="%{#attr.valueNoButton}" title="%{#attr.titleNoButton}"  cssClass="button block-ui" method="add"></s:submit>
