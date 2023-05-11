<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"   uri="/struts-tags" %>

<%-- 
  Inizializza le variabili per la gestione della pagina
  
   	 - helper				{ WizardRinnovo... | WizardIscrizione... }
	 - sessionIdObj  		{ 'dettIscrAlbo' | 'dettRinnAlbo' }
	 - aggiornamentoDatiDoc { true | false }
	 - rinnovo				{ true | false }
	 - iscrizione			{ true | false }

  Esempio di chiamata 
  
  	<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/iscralbo/inc/initDomanda.jsp"/>
		 	
--%>

<%-- Prepara il tipo di pagina in base al tipo di domanda 
	- AGGIORNAMENTO DI DATI O DOCUMENTI
	- DOMANDA DI RINNOVO
	- DOMANDA D'ISCRIZIONE 
--%>
<%-- recupera l'helper in sessione => {'dettIscrAlbo' | 'dettRinnAlbo' } --%>
<s:set name="helper" value="%{null}"/>
<c:set var="sessionIdObj" value="" scope="request"/>
<s:if test="%{#session.dettIscrAlbo != null}">
	<c:set var="sessionIdObj" scope="request" value="dettIscrAlbo"/>
	<s:set name="helper" value="%{#session.dettIscrAlbo}"/>
</s:if>
<s:elseif test="%{#session.dettRinnAlbo != null}">
	<c:set var="sessionIdObj" scope="request" value="dettRinnAlbo"/>
	<s:set name="helper" value="%{#session.dettRinnAlbo}"/>
</s:elseif>

<s:set name="aggiornamentoDatiDoc" value="%{#helper.aggiornamentoIscrizione}"/>
<s:set name="rinnovo" value="%{!#aggiornamentoDatiDoc && #helper.rinnovoIscrizione}"/>
<s:set name="iscrizione" value="%{!#aggiornamentoDatiDoc && !#rinnovo}"/>

<%--
DEBUG INFO <br>
sessionIdObj: ${sessionIdObj}<br>
helper=<s:property value="%{#helper}"/><br>
rinnovoIscrizione=<s:property value="%{#helper.rinnovoIscrizione}"/><br>
aggiornamentoIscrizione=<s:property value="%{#helper.aggiornamentoIscrizione}"/><br>
iscrizione=<s:property value="%{#iscrizione}"/><br>
rinnovo=<s:property value="%{#rinnovo}"/><br>
aggiornamentoDatiDoc=<s:property value="%{#aggiornamentoDatiDoc}"/><br>
<br>
--%>
