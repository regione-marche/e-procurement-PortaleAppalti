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
				<s:property value="termOfUseParam.termOfUse" escape="false"/>
				<br>
	
				<h2><wp:i18n key="LABEL_TERM_OF_USE_ACRONIMI_TITLE" /></h2>
				<s:property value="termOfUseParam.acronimi" escape="false"/>
				<br>

				<h2><wp:i18n key="LABEL_TERM_OF_USE_PLATFORM_DESCRIPTION_TITLE" /></h2>
				<wp:i18n key="LABEL_TERM_OF_USE_PLATFORM_DESCRIPTION" />
				<br>

				<h2><wp:i18n key="LABEL_TERM_OF_USE_PLATFORM_REGISTRATION_TITLE" /></h2>
				<wp:i18n key="LABEL_TERM_OF_USE_PLATFORM_REGISTRATION" />

				<h2><wp:i18n key="LABEL_TERM_OF_USE_ACCOUNT_TITLE" /></h2>
				<wp:i18n key="LABEL_TERM_OF_USE_ACCOUNT" />

				<h2><wp:i18n key="LABEL_TERM_OF_USE_SUBSCRIPTION_TITLE" /></h2>
				<wp:i18n key="LABEL_TERM_OF_USE_SUBSCRIPTION" />

				<h2><wp:i18n key="LABEL_TERM_OF_USE_ELECTION_TITLE" /></h2>
				<wp:i18n key="LABEL_TERM_OF_USE_ELECTION" />
					
				<h2><wp:i18n key="LABEL_TERM_OF_USE_INTELLECTUAL_TITLE" /></h2>
				<wp:i18n key="LABEL_TERM_OF_USE_INTELLECTUAL" />

				<h2><wp:i18n key="LABEL_TERM_OF_USE_RESPONSABILITY_TITLE" /></h2>
				<wp:i18n key="LABEL_TERM_OF_USE_RESPONSABILITY" />
				<br>

				<h2><wp:i18n key="LABEL_TERM_OF_USE_RIGHTS_TITLE" /></h2>
				<wp:i18n key="LABEL_TERM_OF_USE_RIGHTS" />

				<h2><wp:i18n key="LABEL_TERM_OF_USE_PERSONAL_TITLE" /></h2>
				<wp:i18n key="LABEL_TERM_OF_USE_PERSONAL" />

				<h2><wp:i18n key="LABEL_TERM_OF_USE_ACCESS_TITLE" /></h2>
				<wp:i18n key="LABEL_TERM_OF_USE_ACCESS" />

				<h2><wp:i18n key="LABEL_TERM_OF_USE_LAW_TITLE" /></h2>
				<s:property value="termOfUseParam.law" escape="false"/>
				
				<h2><wp:i18n key="LABEL_TERM_OF_USE_CHANGES_TITLE" /></h2>
				<wp:i18n key="LABEL_TERM_OF_USE_CHANGES" />

			</div>
		</div>
	</c:otherwise>
</c:choose>