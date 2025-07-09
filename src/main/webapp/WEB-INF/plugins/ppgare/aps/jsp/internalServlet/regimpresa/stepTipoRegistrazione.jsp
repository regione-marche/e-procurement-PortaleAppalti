<%@ taglib prefix="wp" uri="aps-core.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>

<es:checkCustomization var="visImportMichelangelo" objectId="REG-IMPRESA" attribute="IMPORT-MICHELANGELO" feature="VIS" />


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp">
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2><wp:i18n key="TITLE_PAGE_REGISTRA_OE" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/field_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_REG_IMPRESA_DATI_PRINC_IMPRESA" />
	</jsp:include>
	
	<form action="<wp:action path="/ExtStr2/do/FrontEnd/RegistrImpr/openPageImportImpresa.action" />" 
		  method="post">
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
	
		<%-- IMPORT DA MICHELANGELO (SACE) --%>
		<c:if test="${visImportMichelangelo}">
			<br/>
			<br/>
	        <p>
	            <h3><wp:i18n key="LABEL_REGISTRA_OE_IMPORT_MICHELANGELO" /></h3>
	            <br/>
				<wp:i18n key="BALLOON_REG_IMPRESA_FROM_MICHELANGELO" />
	        </p>
        
			<div class="azioni">
	            <wp:i18n key="BUTTON_REGISTRA_OE_RICERCA_ANAGRAFICA" var="valueRicercaAnagraficaloButton" />
	            <wp:i18n key="TITLE_REGISTRA_OE_RICERCA_ANAGRAFICA" var="titleRicercaAnagraficaloButton" />
				<s:submit value="%{#attr.valueRicercaAnagraficaloButton}"
					title="%{#attr.titleRicercaAnagraficaloButton}"
					cssClass="button" method="openPageFromMichelangelo"></s:submit>
	        </div>
		</c:if>		
	
		<%-- COMPILAZIONE MANUALE --%>
		<p>
			<h3><wp:i18n key="LABEL_REGISTRA_OE_PROSEGUI_MANUALE" /></h3>
			<br/>
			<wp:i18n key="LABEL_REGISTRA_OE_ISTRUZIONI_MANUALE" />
		</p>
		
		<div class="azioni">
			<wp:i18n key="BUTTON_REGISTRA_OE_COMPILA_MANUALE" var="valueCompilaManualmenteButton" />
			<s:submit value="%{#attr.valueCompilaManualmenteButton}" 
				title="%{#attr.valueCompilaManualmenteButton}" 
				cssClass="button" method="openPageOnline"></s:submit>
		</div>		
				
		<%-- IMPORT DA M-XML --%>
		<br/>
		<br/>
		<p>
			<h3><wp:i18n key="LABEL_REGISTRA_OE_IMPORT_MXML" /></h3>
			<br/>
			<wp:i18n key="BALLOON_REG_IMPRESA_FROM_PORTALE" />
		</p>
		
		<div class="azioni">
			<wp:i18n key="BUTTON_REGISTRA_OE_IMPORT_MXML" var="valueImportMXMLButton" />
			<wp:i18n key="TITLE_REGISTRA_OE_IMPORT_MXML" var="titleImportMXMLButton" />
			<s:submit value="%{#attr.valueImportMXMLButton}"
				title="%{#attr.titleImportMXMLButton}" 
				cssClass="button" method="openPageFromPortale"></s:submit>
		</div>

        <%-- IMPORT DA DGUE --%>
        <br/>
		<br/>
		<p>
            <h3><wp:i18n key="LABEL_REGISTRA_OE_IMPORT_DGUE" /></h3>
            <br/>
			<wp:i18n key="BALLOON_REG_IMPRESA_FROM_DGUE" />
        </p>

        <div class="azioni">
            <wp:i18n key="BUTTON_REGISTRA_OE_IMPORT_DGUE" var="valueImportDGUEButton" />
            <wp:i18n key="TITLE_REGISTRA_OE_IMPORT_DGUE" var="titleImportDGUEButton" />
			<s:submit value="%{#attr.valueImportDGUEButton}"
				title="%{#attr.titleImportDGUEButton}"
				cssClass="button" method="openPageFromDGUE"></s:submit>
        </div>
        
	</form>
</div>