<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>

	<%-- recupera 'tipoclassifica' da 
		- parametri della query string (es: link in bandoIscrizione.jsp)
		- attributi della request (es: stepRiepilogoCategorie.jsp)		
	 --%>		
	<c:set var="tipoClassifica" value="${param.tipoclassifica}" />

<%-- DEBUG
JSTL param.tipoclassifica=<c:out value="${param.tipoclassifica}" /> <br/>
ONGL parameters.tipoclassifica=<s:property value="#parameters.tipoclassifica" /> <br/>
<br/>
return tipoClassifica=<c:out value="${tipoClassifica}" /> <br/>	
--%>


<c:set var="toShow" value='processed' />
<c:set var="isRiepilogo"><s:property value="%{notProcessedResult == null}" /></c:set>

<%-- Se == null allora sono nel riepilogo --%> 
<c:if test="${!isRiepilogo}">
    <div class="search" id="categorySearch" style="display:none;">
        <label for="selezione"><wp:i18n key='LABEL_CATEGORY_FILTER_FOR_STATUS'/></label>
        <select id="selezione">
            <option value="${toShow}"><wp:i18n key='TITLE_CATEGORY_PROCESSED'/></option>
            <option value="not_processed"><wp:i18n key='TITLE_CATEGORY_NOT_PROCESSED'/></option>
        </select>
    </div>
</c:if>

<s:set var="categories" value="%{processedResult}" />
<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/bandi/inc/categorieIscrizioneOperatoreList.jsp" >
    <jsp:param name="containerID" value='processed' />
    <jsp:param name="tipoClassifica" value='${tipoClassifica}' />
    <jsp:param name="title_label" value='TITLE_CATEGORY_PROCESSED' />
    <jsp:param name="empty_case_label" value="LABEL_CATEGORY_FOUND_WITH_FILTER" />
    <jsp:param name="isRiepilogo" value="${isRiepilogo}" />
</jsp:include>

<%-- Se == null allora sono nel riepilogo --%> 
<c:if test="${!isRiepilogo}">
    <s:set var="categories" value="%{notProcessedResult}" />
    <jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/bandi/inc/categorieIscrizioneOperatoreList.jsp" >
        <jsp:param name="containerID" value='not_processed' />
        <jsp:param name="tipoClassifica" value='${tipoClassifica}' />
        <jsp:param name="title_label" value='TITLE_CATEGORY_NOT_PROCESSED' />
        <jsp:param name="empty_case_label" value="LABEL_CATEGORY_NOT_FOUND_WITH_FILTER" />
        <jsp:param name="isRiepilogo" value="${isRiepilogo}" />
    </jsp:include>

    <script>
        $(document).ready(function() {
            $("#categorySearch").show()
            $("h3[data-name='categories_title']").hide()
            $("div[data-name='categories_view']").hide()
            $("#${toShow}").show(500)
        });
        $("#selezione").change(function() {
            $("div[data-name='categories_view']").each(function() {
                if ($(this).css("display") != "none")
                    $(this).hide(500)
            });
            $("#" + $(this).val()).show(500)
        });
    </script>
</c:if>