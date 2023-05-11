<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="wp" uri="aps-core.tld"%>
<%@ taglib prefix="wpsa" uri="apsadmin-core.tld"%>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<h1>
	<s:text name="ppcommon.title.customSection" />
</h1>
<h2>
	<s:text name="ppcommon.searchEvents.eventDetail.subtitle" />
</h2>


<dl class="table-display" style="width: 100%">
	<dt style="width: 15%"><s:text name="ppcommon.searchEvents.label.result.id"/></dt>
	<dd style="width: 80%"><s:property value="%{selectedEvent.id}" /></dd>

	<dt style="width: 15%"><s:text name="ppcommon.searchEvents.label.result.date"/></dt>
	<dd style="width: 80%"><s:date name="%{selectedEvent.eventDate}" format="dd/MM/yyyy HH:mm:ss" /></dd>

	<dt style="width: 15%"><s:text name="ppcommon.searchEvents.label.search.ipAddress"/></dt>
	<dd style="width: 80%"><s:property value="%{selectedEvent.ipAddress}" />&nbsp;</dd>

	<dt style="width: 15%"><s:text name="ppcommon.searchEvents.label.search.username"/></dt>
	<dd style="width: 80%"><s:property value="%{selectedEvent.username}" />&nbsp;</dd>

	<dt style="width: 15%"><s:text name="ppcommon.searchEvents.label.search.sessionId"/></dt>
	<dd style="width: 80%"><s:property value="%{selectedEvent.sessionId}" />&nbsp;</dd>

	<dt style="width: 15%"><s:text name="ppcommon.searchEvents.label.search.destination"/></dt>
	<dd style="width: 80%"><s:property value="%{selectedEvent.destination}" />&nbsp;</dd>

	<dt style="width: 15%"><s:text name="ppcommon.searchEvents.label.search.type"/></dt>
	<dd style="width: 80%">
		<s:set var="descEvento" value="%{'ppcommon.searchEvents.label.search.type.' + selectedEvent.eventType}" />
		<s:text name="%{#descEvento}" />
	</dd>

	<dt style="width: 15%"><s:text name="ppcommon.searchEvents.label.search.level"/></dt>
	<dd style="width: 80%"><s:property value="%{selectedEvent.level}" /></dd>

	<dt style="width: 15%"><s:text name="ppcommon.searchEvents.label.result.message"/></dt>
	<dd style="width: 80%"><s:property value="%{selectedEvent.message}" /></dd>

	<dt style="width: 15%"><s:text name="ppcommon.searchEvents.label.result.detailMessage"/></dt>
	<dd style="width: 80%"><s:property value="%{selectedEvent.htmlDetailMessage}" escape="false" />&nbsp;</dd>
</dl>

<%-- <a href="<s:url action="backFromDetail"/>">Torna ai risultati</a> --%>
