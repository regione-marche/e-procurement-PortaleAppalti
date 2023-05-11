<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>

<s:if test="%{#session.dettIscrAlbo != null}">
	<s:set name="sessionIdObj" value="'dettIscrAlbo'"/>
	<c:set var="nextAction" value="/ExtStr2/do/FrontEnd/IscrAlbo/processPageIscrAlboImpresa.action"/>
	<c:set var="sessionIdObj" value="dettIscrAlbo"/>
	<c:set var="fromFlusso" value="iscrizione"/>
</s:if>
<s:else>
	<s:set name="sessionIdObj" value="'dettRinnAlbo'"/>
	<c:set var="nextAction" value="/ExtStr2/do/FrontEnd/IscrAlbo/processPageRinnovoImpresa.action"/>
	<c:set var="sessionIdObj" value="dettRinnAlbo"/>
	<c:set var="fromFlusso" value="rinnovo"/>
</s:else>

<s:set name="helper" value="%{#session[#sessionIdObj]}"/>

<s:if test="%{#helper.aggiornamentoIscrizione}">
	<c:set var="codiceBalloon" value="BALLOON_AGG_ISCR_ALBO_IMPRESA"/>
</s:if>
<s:elseif test="%{#helper.rinnovoIscrizione}">
	<c:set var="codiceBalloon" value="BALLOON_AGG_ISCR_ALBO_IMPRESA"/>
</s:elseif>
<s:else>
	<c:set var="codiceBalloon" value="BALLOON_ISCR_ALBO_IMPRESA"/>
</s:else>

<s:if test="%{#helper.tipologia == 2}">
	<c:set var="titolo"><wp:i18n key='TITLE_PAGE_ISCRALBO_MERCATO_ELETTRONICO'/></c:set>
</s:if>
<s:else>
	<c:set var="titolo"><wp:i18n key='TITLE_PAGE_ISCRALBO_ELENCO_OE'/></c:set>
</s:else>

<s:url id="urlPdf" namespace="/do/FrontEnd/DatiImpr" action="createPdfFromIscrAlbo" />


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<s:if test="%{#helper.rinnovoIscrizione}">
		<h2><wp:i18n key='TITLE_PAGE_ISCRALBO_RICHIESTA_RINNOVO_ISCRIZIONE'/> <c:out value="${titolo}"/></h2>
	</s:if>
	<s:elseif test="%{!#helper.aggiornamentoIscrizione}">
		<h2><wp:i18n key='TITLE_PAGE_ISCRALBO_RICHIESTA_ISCRIZIONE'/> <c:out value="${titolo}"/></h2>
	</s:elseif>
	<s:else>
		<h2><wp:i18n key='TITLE_PAGE_ISCRALBO_AGGIORNAMENTO_ISCRIZIONE'/> <c:out value="${titolo}"/></h2>
	</s:else>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/field_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_messages.jsp" />

	<jsp:include page="stepsIscrizione.jsp">
		<jsp:param name="sessionIdObj" value="${sessionIdObj}" />
	</jsp:include>
	

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="${codiceBalloon}"/>
	</jsp:include>
	

	<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/datiimpresa/datiRiepilogoSection.jsp">
		<jsp:param name="sessionIdObj" value="${sessionIdObj}" />
	</jsp:include>

	
	<div class="azioni">
		<form action="<wp:url page="ppgare_impr_aggdati"/>" method="post" class="azione">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			
			<div>
				<input type="hidden" name="fromUrl" value="<wp:url />" />
				<input type="hidden" name="fromModule" value="IscrAlbo" />
				<s:if test="%{#helper.rinnovoIscrizione}">
					<input type="hidden" name="fromAction" value="openPageRinnovoImpresa" />
				</s:if>
				<s:else>
					<input type="hidden" name="fromAction" value="openPageIscrAlboImpresa" />
				</s:else>
				<input type="hidden" name="fromFrame" value="${param.currentFrame}" />
				<input type="hidden" name="ext" value="${param.ext}" />
				<input type="hidden" name="fromFlusso" value="${fromFlusso}"/>
				<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/datiimpresa/inc/button_edit_datiimpresa.jsp" />
			</div>
		</form>

		<form action="<wp:action path="${nextAction}" />" method="post" class="azione">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			
			<div>
				<input type="hidden" name="ext" value="${param.ext}" />
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_next.jsp" />
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_cancel.jsp" />
			</div>
		</form>
	</div>
</div>