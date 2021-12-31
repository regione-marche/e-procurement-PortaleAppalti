<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="wp" uri="aps-core.tld"%>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld"%>

<h1>
	<s:text name="ppcommon.title.customSection" />
</h1>
<h2>
	<s:text name="ppcommon.searchEvents.subtitle" />
</h2>

<p>
	<s:text name="ppcommon.searchEvents.text.1" />
	<br />
	<s:text name="ppcommon.searchEvents.text.2" />
</p>
<br />
<s:form action="searchEventsByCriteria">

	<s:if test="hasActionErrors()">
		<div class="message message_error">
			<h3>
				<s:text name="message.title.ActionErrors" />
			</h3>
			<ul>
				<s:iterator value="actionErrors">
					<li><s:property escape="false" /></li>
				</s:iterator>
			</ul>
		</div>
	</s:if>

	<%-- MASCHERA DI RICERCA --%>

	<fieldset>
		<legend>
			<s:text name="ppcommon.searchEvents.label.search.for" />
		</legend>
		<p>
			<label><s:text name="ppcommon.searchEvents.label.search.date" />:</label><br />
			<s:text name="ppcommon.searchEvents.label.search.dateFrom" />
			<s:textfield name="model.dateFrom" id="model.dateFrom"
				value="%{model.dateFrom}" maxLength="10" size="10" cssClass="text" />
			<s:text name="ppcommon.searchEvents.label.search.dateTo" />
			<s:textfield name="model.dateTo" id="model.dateTo"
				value="%{model.dateTo}" maxLength="10" size="10" cssClass="text" />
			(
			<s:text name="ppcommon.searchEvents.label.search.dateFormat" />
			)
		</p>
		<p>
			<label><s:text
					name="ppcommon.searchEvents.label.search.username" />:</label><br />
			<s:textfield name="model.user" id="model.user" value="%{model.user}"
				cssClass="text" />
		</p>
		<p>
			<label><s:text
					name="ppcommon.searchEvents.label.search.destination" />:</label><br />
			<s:textfield name="model.destination" id="model.destination"
				value="%{model.destination}" cssClass="text" />
		</p>
		<p>
			<label><s:text name="ppcommon.searchEvents.label.search.type" />:</label><br />
			<select name="model.type" id="model.type" class="text">
				<option selected="selected" value=""></option>
				
				<optgroup label="<s:text name="ppcommon.searchEvents.label.search.group.users" />"></optgroup>
				<option <s:if test="model.type=='CHANGEPASSWORD'">selected="selected"</s:if>
					value="CHANGEPASSWORD"><s:text
						name="ppcommon.searchEvents.label.search.type.CHANGEPASSWORD" /></option>	
				<option <s:if test="model.type=='CREATEACCOUNT'">selected="selected"</s:if>
					value="CREATEACCOUNT"><s:text
						name="ppcommon.searchEvents.label.search.type.CREATEACCOUNT" /></option>
				<option <s:if test="model.type=='CREATEACCOUNTSSO'">selected="selected"</s:if>
					value="CREATEACCOUNTSSO"><s:text
						name="ppcommon.searchEvents.label.search.type.CREATEACCOUNTSSO" /></option>				
				<option <s:if test="model.type=='GENTOKEN'">selected="selected"</s:if>
					value="GENTOKEN"><s:text
						name="ppcommon.searchEvents.label.search.type.GENTOKEN" /></option>
				<option <s:if test="model.type=='GENTOKENADMIN'">selected="selected"</s:if>
					value="GENTOKENADMIN"><s:text
						name="ppcommon.searchEvents.label.search.type.GENTOKENADMIN" /></option>
				<option <s:if test="model.type=='IPBLOCK'">selected="selected"</s:if>
					value="IPBLOCK"><s:text
						name="ppcommon.searchEvents.label.search.type.IPBLOCK" /></option>
				<option <s:if test="model.type=='BOTBLOCK'">selected="selected"</s:if>
					value="BOTBLOCK"><s:text
						name="ppcommon.searchEvents.label.search.type.BOTBLOCK" /></option>
				<option <s:if test="model.type=='LOGIN'">selected="selected"</s:if>
					value="LOGIN"><s:text
						name="ppcommon.searchEvents.label.search.type.LOGIN" /></option>
				<option <s:if test="model.type=='LOGINSSO'">selected="selected"</s:if>
					value="LOGINSSO"><s:text
						name="ppcommon.searchEvents.label.search.type.LOGINSSO" /></option>
				<option <s:if test="model.type=='LOGINAS'">selected="selected"</s:if>
					value="LOGINAS"><s:text
						name="ppcommon.searchEvents.label.search.type.LOGINAS" /></option>
				<option <s:if test="model.type=='LOGOUT'">selected="selected"</s:if>
					value="LOGOUT"><s:text
						name="ppcommon.searchEvents.label.search.type.LOGOUT" /></option>
				<option <s:if test="model.type=='LOGOUTSSO'">selected="selected"</s:if>
					value="LOGOUTSSO"><s:text
						name="ppcommon.searchEvents.label.search.type.LOGOUTSSO" /></option>
				<option <s:if test="model.type=='PASSWORDRECOVERY'">selected="selected"</s:if>
					value="PASSWORDRECOVERY"><s:text
						name="ppcommon.searchEvents.label.search.type.PASSWORDRECOVERY" /></option>		
				<option <s:if test="model.type=='PROCTOKEN'">selected="selected"</s:if>
					value="PROCTOKEN"><s:text
						name="ppcommon.searchEvents.label.search.type.PROCTOKEN" /></option>
				<option <s:if test="model.type=='USERBLOCK'">selected="selected"</s:if>
					value="USERBLOCK"><s:text
						name="ppcommon.searchEvents.label.search.type.USERBLOCK" /></option>		
				
				<optgroup label="<s:text name="ppcommon.searchEvents.label.search.group.communications" />"></optgroup>
				<option
					<s:if test="model.type=='ABORTCOM'">selected="selected"</s:if>
					value="ABORTCOM"><s:text
						name="ppcommon.searchEvents.label.search.type.ABORTCOM" /></option>
				<option
					<s:if test="model.type=='COMPLMANPROTCOM'">selected="selected"</s:if>
					value="COMPLMANPROTCOM"><s:text
						name="ppcommon.searchEvents.label.search.type.COMPLMANPROTCOM" /></option>
				<option <s:if test="model.type=='SAVECOM'">selected="selected"</s:if>
					value="SAVECOM"><s:text
						name="ppcommon.searchEvents.label.search.type.SAVECOM" /></option>
				<option <s:if test="model.type=='INVCOM'">selected="selected"</s:if>
					value="INVCOM"><s:text
						name="ppcommon.searchEvents.label.search.type.INVCOM" /></option>
				<option
					<s:if test="model.type=='INVCOMDAPROT'">selected="selected"</s:if>
					value="INVCOMDAPROT"><s:text
						name="ppcommon.searchEvents.label.search.type.INVCOMDAPROT" /></option>
				<option <s:if test="model.type=='PROT'">selected="selected"</s:if>
					value="PROT"><s:text
						name="ppcommon.searchEvents.label.search.type.PROT" /></option>
				<option
					<s:if test="model.type=='PROTCOM'">selected="selected"</s:if>
					value="PROTCOM"><s:text
						name="ppcommon.searchEvents.label.search.type.PROTCOM" /></option>
				<option
					<s:if test="model.type=='STATCOM'">selected="selected"</s:if>
					value="STATCOM"><s:text
						name="ppcommon.searchEvents.label.search.type.STATCOM" /></option>

				<optgroup label="<s:text name="ppcommon.searchEvents.label.search.group.other" />"></optgroup>
				<option <s:if test="model.type=='DOWNLOADRESERVED'">selected="selected"</s:if>
					value="DOWNLOADRESERVED"><s:text
						name="ppcommon.searchEvents.label.search.type.DOWNLOADRESERVED" /></option>				
				<option <s:if test="model.type=='UPLOADFILE'">selected="selected"</s:if>
					value="UPLOADFILE"><s:text
						name="ppcommon.searchEvents.label.search.type.UPLOADFILE" /></option>				
				<option <s:if test="model.type=='DELETEFILE'">selected="selected"</s:if>
					value="DELETEFILE"><s:text
						name="ppcommon.searchEvents.label.search.type.DELETEFILE" /></option>				
				<option <s:if test="model.type=='MAIL'">selected="selected"</s:if>
					value="MAIL"><s:text
						name="ppcommon.searchEvents.label.search.type.MAIL" /></option>
				<option
					<s:if test="model.type=='ACCESSOFUNZ'">selected="selected"</s:if>
					value="ACCESSOFUNZ"><s:text
						name="ppcommon.searchEvents.label.search.type.ACCESSOFUNZ" /></option>
				<option <s:if test="model.type=='RILANCIO'">selected="selected"</s:if>
					value="RILANCIO"><s:text
						name="ppcommon.searchEvents.label.search.type.RILANCIO" /></option>
				<option <s:if test="model.type=='HELPDESK'">selected="selected"</s:if>
					value="HELPDESK"><s:text
						name="ppcommon.searchEvents.label.search.type.HELPDESK" /></option>
				<option
					<s:if test="model.type=='CHECKINVIO'">selected="selected"</s:if>
					value="CHECKINVIO"><s:text
						name="ppcommon.searchEvents.label.search.type.CHECKINVIO" /></option>					
				<option
					<s:if test="model.type=='CONSULTEVENTS'">selected="selected"</s:if>
					value="CONSULTEVENTS"><s:text
						name="ppcommon.searchEvents.label.search.type.CONSULTEVENTS" /></option>
				<option
					<s:if test="model.type=='DELEGATEUSERCHANGE'">selected="selected"</s:if>
					value="DELEGATEUSERCHANGE"><s:text
						name="ppcommon.searchEvents.label.search.type.DELEGATEUSERCHANGE" /></option>
				<option
					<s:if test="model.type=='ACCESSOFASIGARA'">selected="selected"</s:if>
					value="ACCESSOFASIGARA"><s:text
						name="ppcommon.searchEvents.label.search.type.ACCESSOFASIGARA" /></option>
			</select>
		</p>
		<p>
			<label><s:text
					name="ppcommon.searchEvents.label.search.level" />:</label><br /> <select
				name="model.level" id="model.level" class="text">
				<option <s:if test="%{model.level==''}">selected="selected"</s:if>
					value=""></option>
				<option <s:if test="%{model.level==1}">selected="selected"</s:if>
					value="1"><s:text
						name="ppcommon.searchEvents.label.search.level.info" /></option>
				<option <s:if test="%{model.level==2}">selected="selected"</s:if>
					value="2"><s:text
						name="ppcommon.searchEvents.label.search.level.warning" /></option>
				<option <s:if test="%{model.level==3}">selected="selected"</s:if>
					value="3"><s:text
						name="ppcommon.searchEvents.label.search.level.error" /></option>
			</select>
		</p>

		<p>
			<label><s:text
					name="ppcommon.searchEvents.label.search.message" />:</label><br />
			<s:textfield name="model.message" id="model.message" value="%{model.message}"
				cssClass="text" />
		</p>
		
		<p>
			<label><s:text
					name="ppcommon.searchEvents.label.search.rowsperpage" />:</label><br />
			<select name="rowsPerPage" id="rowsPerPage" class="text">
				<option <s:if test="%{rowsPerPage==10}">selected="selected"</s:if> value="10">10</option>
				<option <s:if test="%{rowsPerPage==20}">selected="selected"</s:if> value="20">20</option>
				<option <s:if test="%{rowsPerPage==50}">selected="selected"</s:if> value="50">50</option>
				<option <s:if test="%{rowsPerPage==100}">selected="selected"</s:if> value="100">100</option>
			</select>
		</p>
	</fieldset>
	<p>
		<wpsf:submit value="%{getText('label.search')}" cssClass="button" />
	</p>


	<%-- VISUALIZZAZIONE LISTA RISULTATI --%>
	<s:if test="%{showResult}">
		<div class="pager">
			<jsp:include
				page="/WEB-INF/plugins/ppcommon/apsadmin/jsp/common/inc/pagerInfo.jsp"></jsp:include>
			<jsp:include
				page="/WEB-INF/plugins/ppcommon/apsadmin/jsp/common/inc/pagination.jsp"></jsp:include>
		</div>
		<s:if test="%{risultati.size() > 0}">
			<table class="generic">
<!-- 			<table class="generic" style="display: block; overflow: auto;"> -->
				<tr>
					<th><s:text name="ppcommon.searchEvents.label.result.id" /></th>
					<th width="140px"><s:text name="ppcommon.searchEvents.label.result.date" /></th>
					<th><s:text name="ppcommon.searchEvents.label.search.ipAddress" /></th>
					<th><s:text name="ppcommon.searchEvents.label.search.username" /></th>
					<th><s:text name="ppcommon.searchEvents.label.search.sessionId" /></th>
					<th><s:text name="ppcommon.searchEvents.label.search.destination" /></th>
					<th><s:text name="ppcommon.searchEvents.label.search.type" /></th>
					<th><s:text name="ppcommon.searchEvents.label.search.level" /></th>
					<th><s:text name="ppcommon.searchEvents.label.result.message" /></th>
				</tr>
				<s:iterator var="event" value="%{risultati}">
					<tr>
						<td>
							<s:if test="%{#event.level.level==3}">
							
								<a href="<s:url action="openEventDetail?eventId=%{#event.id}"/>"><s:property
										value="%{#event.id}" /></a>
							</s:if> <s:else>
								<s:property value="%{#event.id}" />
							</s:else>
						</td>
						<td><s:date name="%{#event.eventDate}" format="dd/MM/yyyy HH:mm:ss" /></td>
						<td><s:property value="%{#event.ipAddress}" /></td>
						<td><s:property value="%{#event.username}" /></td>
						<td><s:property value="%{#event.sessionId}" /></td>
						<td><s:property value="%{#event.destination}" /></td>
						<td><s:property value="%{#event.eventType}" /></td>
						<td><s:property value="%{#event.level}" /></td>
						<td><s:property value="%{#event.message}" /></td>
					</tr>
				</s:iterator>
			</table>

		</s:if>

		<div class="pager">
			<jsp:include
				page="/WEB-INF/plugins/ppcommon/apsadmin/jsp/common/inc/pagination.jsp"></jsp:include>
		</div>

	</s:if>
</s:form>