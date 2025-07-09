<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>


<div class="portgare-view">
    <jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />

    <h2><s:property value="%{retrieveEnvelopeName()}" /> : <wp:i18n key="LABEL_CONFIRM_ELIMINATION" /></h2>
    <p><wp:i18n key="LABEL_CONFIRM_ENVELOPE_RESET" /></p>

    <div class="azioni">
        <form action="<wp:action path='/ExtStr2/do/FrontEnd/GareTel/askResetEnvelope.action' />" method="POST" style="display: inline;" class="azione">
        	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
            <input type="hidden" name="codiceGara" value="<s:property value='%{codiceGara}' />" />
            <input type="hidden" name="codice" value="<s:property value='%{codice}' />" />
            <input type="hidden" name="tipoBusta" value="<s:property value='%{tipoBusta}' />" />
            <input type="hidden" name="operazione" value="<s:property value='%{operazione}' />" />
            <input type="hidden" name="urlRedirect" value="<wp:action path='${backActionPath}' />" />
            <input type="submit" value="<wp:i18n key='LABEL_YES' />" title="<wp:i18n key='LABEL_YES' />" name="method:resetEnvelope" class="button"></input>
        </form>
        <form action="<wp:action path='${backActionPath}' />" method="POST" style="display: inline;" class="azione">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
            <input type="hidden" name="codiceGara" value="<s:property value='%{codiceGara}' />" />
            <input type="hidden" name="codice" value="<s:property value='%{codice}' />" />
            <input type="hidden" name="operazione" value="<s:property value='%{operazione}' />" />
            <input type="hidden" name="tipoBusta" value="<s:property value='%{tipoBusta}' />" />
            <input type="submit" value="<wp:i18n key='LABEL_NO' />" title="<wp:i18n key='LABEL_NO' />" class="button"></input>
        </form>
    </div>

</div>