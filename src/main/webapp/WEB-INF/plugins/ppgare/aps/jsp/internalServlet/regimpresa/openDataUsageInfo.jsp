<%@ taglib prefix="wp" uri="aps-core.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<div class = "responsive content">
	<div class = "container_cms">
	
		<h2><wp:i18n key="LABEL_DATA_USAGES_TITLE" /></h2>
		<s:property value="dataUsageParam.dataUsages" escape="false"/>
		<h3><wp:i18n key="LABEL_DATA_USAGES_TREATMENT_TITLE" /></h3>
		<p><s:property value="dataUsageParam.treatment" escape="false"/></p>
		<c:if test="${! empty dataUsageParam.mailDPO}">
			<h3><wp:i18n key="LABEL_DATA_USAGES_DPO_TITLE" /></h3>
			<p><s:property value="dataUsageParam.dpoEmail" escape="false"/></p>
		</c:if>
		<c:if test="${! empty dataUsageParam.nomeGestore}">
			<h3><wp:i18n key="LABEL_DATA_USAGES_HANDLER_TITLE" /></h3>
			<p><s:property value="dataUsageParam.handler" escape="false"/></p>
		</c:if>
	
		<h2><wp:i18n key="LABEL_DATA_USAGES_TYPE_OF_DATA_TITLE" /></h2>
		<h3><wp:i18n key="LABEL_DATA_USAGES_NAVIGATION_TITLE" /></h3>
		<p><s:property value="dataUsageParam.navigation" escape="false"/></p>
		 
		<h3><wp:i18n key="LABEL_DATA_USAGES_VOLUNTARILY_TITLE" /></h3>
		<p><wp:i18n key="LABEL_DATA_USAGES_VOLUNTARILY" /></p>
		<h3><wp:i18n key="LABEL_DATA_USAGES_REGISTRATION_DATA_TITLE" /></h3>
		<p><wp:i18n key="LABEL_DATA_USAGES_REGISTRATION_DATA" /></p>
		<h3><wp:i18n key="LABEL_DATA_USAGES_CONTACT_ADDRESSES_TITLE" /></h3>
		<p><wp:i18n key="LABEL_DATA_USAGES_CONTACT_ADDRESSES" /></p>
		<h3><wp:i18n key="LABEL_DATA_USAGES_OPERATORS_LIST_TITLE" /></h3>
		<p><wp:i18n key="LABEL_DATA_USAGES_OPERATORS_LIST" /></p>
		<h3><wp:i18n key="LABEL_DATA_USAGES_PLATFORM_SERVICES_TITLE" /></h3>
		<p><wp:i18n key="LABEL_DATA_USAGES_PLATFORM_SERVICES" /></p> 
		<h2><wp:i18n key="LABEL_DATA_USAGES_COOKIE_TITLE" /></h2>
		<p><wp:i18n key="LABEL_DATA_USAGES_COOKIE" /></p>

		<h2><wp:i18n key="LABEL_DATA_USAGES_PURPOSE_TITLE" /></h2>
		<wp:i18n key="LABEL_DATA_USAGES_PURPOSE" />

		<h2><wp:i18n key="LABEL_DATA_USAGES_TREATMENT_MODALITY_TITLE" /></h2>
		<wp:i18n key="LABEL_DATA_USAGES_TREATMENT_MODALITY" />

		<h2><wp:i18n key="LABEL_DATA_USAGES_PRESERVATION_TITLE" /></h2>
		<s:property value="dataUsageParam.preservation" escape="false"/>

		<h2><wp:i18n key="LABEL_DATA_USAGES_RIGHTS_TITLE" /></h2>
		<p><s:property value="dataUsageParam.rights" escape="false"/></p>
		<h2><wp:i18n key="LABEL_DATA_USAGES_UPDATES_TITLE" /></h2>
		<p><s:property value="dataUsageParam.updates" escape="false"/></p>
	</div>
</div>
