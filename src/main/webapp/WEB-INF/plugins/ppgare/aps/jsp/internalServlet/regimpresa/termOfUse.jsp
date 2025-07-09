<%@ taglib prefix="wp" uri="aps-core.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<c:choose>
	<c:when test="${! empty resourceURL }">
		<c:redirect url="${resourceURL}" />
	</c:when>
	<c:otherwise>
		<div class = "responsive content">
			<div class = "container_cms">
			
				<h2><wp:i18n key="LABEL_TERM_OF_USE_TERMS_TITLE" /></h2>
				<p><s:property value="termOfUseParam.termOfUse" escape="false"/></p>
				<br>
	
				<h3><wp:i18n key="LABEL_TERM_OF_USE_ACRONIMI_TITLE" /></h3>
				<p><s:property value="termOfUseParam.acronimi" escape="false"/></p>
				<br>

				<h3><wp:i18n key="LABEL_TERM_OF_USE_PLATFORM_DESCRIPTION_TITLE" /></h3>
				<p><wp:i18n key="LABEL_TERM_OF_USE_PLATFORM_DESCRIPTION" /></p>
				<br>

				<h3><wp:i18n key="LABEL_TERM_OF_USE_PLATFORM_REGISTRATION_TITLE" /></h3>
				<p><wp:i18n key="LABEL_TERM_OF_USE_PLATFORM_REGISTRATION" /></p>

				<h3><wp:i18n key="LABEL_TERM_OF_USE_ACCOUNT_TITLE" /></h3>
				<p><wp:i18n key="LABEL_TERM_OF_USE_ACCOUNT" /></p>

				<h3><wp:i18n key="LABEL_TERM_OF_USE_SUBSCRIPTION_TITLE" /></h3>
				<p><wp:i18n key="LABEL_TERM_OF_USE_SUBSCRIPTION" /></p>

				<h3><wp:i18n key="LABEL_TERM_OF_USE_ELECTION_TITLE" /></h3>
				<p><wp:i18n key="LABEL_TERM_OF_USE_ELECTION" /></p>
					
				<h3><wp:i18n key="LABEL_TERM_OF_USE_INTELLECTUAL_TITLE" /></h3>
				<p><wp:i18n key="LABEL_TERM_OF_USE_INTELLECTUAL" /></p>

				<h3><wp:i18n key="LABEL_TERM_OF_USE_RESPONSABILITY_TITLE" /></h3>
				<p><wp:i18n key="LABEL_TERM_OF_USE_RESPONSABILITY" /></p>
				<br>

				<h3><wp:i18n key="LABEL_TERM_OF_USE_RIGHTS_TITLE" /></h3>
				<p><wp:i18n key="LABEL_TERM_OF_USE_RIGHTS" /></p>

				<h3><wp:i18n key="LABEL_TERM_OF_USE_PERSONAL_TITLE" /></h3>
				<p><wp:i18n key="LABEL_TERM_OF_USE_PERSONAL" /></p>

				<h3><wp:i18n key="LABEL_TERM_OF_USE_ACCESS_TITLE" /></h3>
				<p><wp:i18n key="LABEL_TERM_OF_USE_ACCESS" /></p>

				<h3><wp:i18n key="LABEL_TERM_OF_USE_LAW_TITLE" /></h3>
				<p><s:property value="termOfUseParam.law" escape="false"/></p>
				
				<h3><wp:i18n key="LABEL_TERM_OF_USE_CHANGES_TITLE" /></h3>
				<p><wp:i18n key="LABEL_TERM_OF_USE_CHANGES" /></p>

			</div>
		</div>
	</c:otherwise>
</c:choose>