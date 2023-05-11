<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2><wp:i18n key="LABEL_ESPLETAMENTO_GARA" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_GARA_TEL_FASI"/>
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_messages.jsp" />

	<div class="menu-gestione">
		<ul>
			<%-- Apertura doc. amministrativa --%>
			<li class='bkg administration <s:if test="%{!abilitaAperturaDocAmm}">disabled</s:if>'>
				<a class="menu-item-link go"
					title='<s:if test="%{abilitaAperturaDocAmm}"><wp:i18n key='LABEL_APERTURA_DOC_AMMINISTRATIVA'/></s:if>
						   <s:else>(<wp:i18n key='LABEL_FUNZIONE_DISABILITATA'/>) <wp:i18n key='LABEL_APERTURA_DOC_AMMINISTRATIVA'/></s:else>'
					href='<s:if test="%{abilitaAperturaDocAmm}">
						  	<wp:action path="/ExtStr2/do/FrontEnd/GareTel/espletGaraViewDocAmm.action" />&amp;codice=${codice}&amp;${tokenHrefParams}
						  </s:if><s:else>#</s:else>'>
					<span class="menu-item-link-label"><wp:i18n key='LABEL_APERTURA_DOC_AMMINISTRATIVA'/></span>
				</a>
			</li>
	
			<%-- Valutazione tecnica --%>
			<s:if test="%{visibileValTec}">
				<!-- OEPV oppure gara con offerta tecnica -->
				<li class='bkg technology <s:if test="%{!abilitaValTec}">disabled</s:if>'>
					<s:if test="%{lotti}">
						<c:set var="href" value="/ExtStr2/do/FrontEnd/GareTel/espletGaraViewValTecLotti.action" />
					</s:if>
					<s:else>
						<c:set var="href" value="/ExtStr2/do/FrontEnd/GareTel/espletGaraViewValTec.action" />
					</s:else>
					<a class="menu-item-link go" 
						title='<s:if test="%{abilitaValTec}"><wp:i18n key='LABEL_VALUTAZIONE_TECNICA'/></s:if>
							   <s:else>(<wp:i18n key='LABEL_FUNZIONE_DISABILITATA'/>) <wp:i18n key='LABEL_VALUTAZIONE_TECNICA'/></s:else>' 
						href='<s:if test="%{abilitaValTec}">
								<wp:action path="${href}" />&amp;codice=${codice}&amp;${tokenHrefParams}
							  </s:if><s:else>#</s:else>'>
						<span class="menu-item-link-label"><wp:i18n key='LABEL_VALUTAZIONE_TECNICA'/></span>
					</a>
				</li>
			</s:if>
			
			<%-- Offerte economiche --%>
			<s:if test="%{visibileOffEco}">
				<li class='bkg economy <s:if test="%{!abilitaOffEco}">disabled</s:if>'>
					<s:if test="%{lotti}">
						<c:set var="href" value="/ExtStr2/do/FrontEnd/GareTel/espletGaraViewOffEcoLotti.action" />
					</s:if>
					<s:else>
						<c:set var="href" value="/ExtStr2/do/FrontEnd/GareTel/espletGaraViewOffEco.action" />
					</s:else>
					<a class="menu-item-link go"
						title='<s:if test="%{abilitaOffEco}"><wp:i18n key='LABEL_OFFERTE_ECONOMICHE'/></s:if>
							   <s:else>(<wp:i18n key='LABEL_FUNZIONE_DISABILITATA'/>) <wp:i18n key='LABEL_OFFERTE_ECONOMICHE'/></s:else>'
						href='<s:if test="%{abilitaOffEco}">
								<wp:action path="${href}" />&amp;codice=${codice}&amp;${tokenHrefParams}
							  </s:if><s:else>#</s:else>'>
						<span class="menu-item-link-label"><wp:i18n key='LABEL_OFFERTE_ECONOMICHE'/></span>
					</a>
				</li>
			</s:if>
			
			<%-- Graduatoria --%>
			<s:if test="%{visibileGraduatoria}">
				<li class='bkg summary <s:if test="%{!abilitaGraduatoria}">disabled</s:if>'>
					<s:if test="%{lotti}">
						<c:set var="href" value="/ExtStr2/do/FrontEnd/GareTel/espletGaraViewGraduatoriaLotti.action" />
					</s:if>
					<s:else>
						<c:set var="href" value="/ExtStr2/do/FrontEnd/GareTel/espletGaraViewGraduatoria.action" />
					</s:else>
					<a class="menu-item-link go" 
						title='<s:if test="%{abilitaGraduatoria}"><wp:i18n key='LABEL_GRADUATORIA'/></s:if>
							   <s:else>(<wp:i18n key='LABEL_FUNZIONE_DISABILITATA'/>) <wp:i18n key='LABEL_GRADUATORIA'/></s:else>' 
						href='<s:if test="%{abilitaGraduatoria}">
								<wp:action path="${href}" />&amp;codice=${codice}&amp;${tokenHrefParams}
							  </s:if><s:else>#</s:else>'>
						<span class="menu-item-link-label"><wp:i18n key='LABEL_GRADUATORIA'/></span>
					</a>
				</li>
			</s:if>
			
		</ul>
	</div>
	
</div>

<div class="back-link">
	<a href="<wp:action path="/ExtStr2/do/FrontEnd/Bandi/view.action" />&amp;codice=${codice}&amp;ext=${param.ext}&amp;${tokenHrefParams}">
		<wp:i18n key="LINK_BACK_TO_PROCEDURE" />
	</a>
</div>

