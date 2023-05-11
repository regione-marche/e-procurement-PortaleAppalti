<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<h2><wp:i18n key="TITLE_VENDOR_RATING" /></h2>

<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/field_errors.jsp" />
<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />

<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_VENDOR_RATING"/>
</jsp:include>

<fieldset>
	<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_SECTION_VENDOR_RATING" /></legend>

	<div class="fieldset-row first-row">
		<div class="label">
			<label><wp:i18n key="LABEL_VENDOR_RATING" /> : </label>
		</div>
		<div class="element">
			<span>
          <s:if test="vendorRatingCorrente.indiceVR != null">
		  <s:property value="new java.text.DecimalFormat('#.##').format(vendorRatingCorrente.indiceVR)" />
          </s:if>
		  <s:else><wp:i18n key="LABEL_VENDOR_RATING_NON_CALCOLATO" /></s:else>
			</span>
		</div>
	</div>

	<div class="fieldset-row">
		<div class="label">
			<label><wp:i18n key="LABEL_VENDOR_RATING_VALIDITA_DA" /> : </label>
		</div>
		<div class="element">
			<span>
				<s:date name="%{vendorRatingCorrente.dataInizioValidita}" format="dd/MM/yyyy" />
			</span>
		</div>
	</div>

	<div class="fieldset-row">
		<div class="label">
			<label><wp:i18n key="LABEL_VENDOR_RATING_VALIDITA_FINO_A" /> : </label>
		</div>
		<div class="element">
			<span>
				<s:date name="%{vendorRatingCorrente.dataFineValidita}" format="dd/MM/yyyy" />
			</span>
		</div>
	</div>
	<s:if test="%{vendorRatingCorrente.dataInizioSospensione != null}">
	<div class="fieldset-row">
		<div class="label">
			<label><wp:i18n key="LABEL_SOSPENSIONE" /> : </label>
		</div>
		<div class="element">
			<span>
				<wp:i18n key="LABEL_DA_DATA" /> <s:date name="%{vendorRatingCorrente.dataInizioSospensione}" format="dd/MM/yyyy" /> <wp:i18n key="LABEL_A_DATA" /> <s:date name="%{vendorRatingCorrente.dataFineSospensione}" format="dd/MM/yyyy" />
			</span>
		</div>
	</div>
	</s:if>
</fieldset>

<s:if test="%{vendorRatingFuturo.indiceVR != null}" >
<fieldset>
	<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_SECTION_VENDOR_RATING_FUTURO" /></legend>

	<div class="fieldset-row first-row">
		<div class="label">
			<label><wp:i18n key="LABEL_VENDOR_RATING" /> : </label>
		</div>
		<div class="element">
			<span>
				<s:property value="new java.text.DecimalFormat('#.##').format(vendorRatingFuturo.indiceVR)" />
			</span>
		</div>
	</div>

	<div class="fieldset-row">
		<div class="label">
			<label><wp:i18n key="LABEL_VENDOR_RATING_VALIDITA_DA" /> : </label>
		</div>
		<div class="element">
			<span>
				<s:date name="%{vendorRatingFuturo.dataInizioValidita}" format="dd/MM/yyyy" />
			</span>
		</div>
	</div>

	<div class="fieldset-row">
		<div class="label">
			<label><wp:i18n key="LABEL_VENDOR_RATING_VALIDITA_FINO_A" /> : </label>
		</div>
		<div class="element">
			<span><s:date name="%{vendorRatingFuturo.dataFineValidita}" format="dd/MM/yyyy" /></span>
		</div>
	</div>
	<s:if test="%{vendorRatingFuturo.dataInizioSospensione != null}">
		<div class="fieldset-row">
			<div class="label">
				<label><wp:i18n key="LABEL_SOSPENSIONE" /> : </label>
			</div>
			<div class="element">
				<span>
					<wp:i18n key="LABEL_DA_DATA" /> <s:date name="%{vendorRatingFuturo.dataInizioSospensione}" format="dd/MM/yyyy" /> <wp:i18n key="LABEL_A_DATA" /> <s:date name="%{vendorRatingFuturo.dataFineSospensione}" format="dd/MM/yyyy" />
				</span>
			</div>
		</div>
		</s:if>
</fieldset>
</s:if>
<div class="back-link">
	<a href="ppcommon_area_personale.wp" >
		<wp:i18n key="LINK_BACK_TO_AREAPERSONALE" />
	</a>
</div>