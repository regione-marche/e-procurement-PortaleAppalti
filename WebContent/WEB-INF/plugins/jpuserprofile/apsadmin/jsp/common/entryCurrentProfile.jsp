<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="wpsa" uri="apsadmin-core.tld" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>

<h1><s:text name="jpuserprofile.title.changeUserProfile" /></h1>
<s:form>

	<s:if test="hasFieldErrors()">
		<div class="message message_error">
		<h3><s:text name="message.title.FieldErrors" /></h3>
		<ul>
			<s:iterator value="fieldErrors">
				<s:iterator value="value">
					<li><s:property escape="false" /></li>
				</s:iterator>
			</s:iterator>
		</ul>
		</div>
	</s:if>

	<s:set name="lang" value="defaultLang"></s:set>

	<p>
		<s:set name="checkedValue" value="%{userProfile.publicProfile != null && userProfile.publicProfile == true}" />
		<label for="jpuserprofile_isPublic"><s:text name="jpuserprofile.title.myPublicPorfile" /></label> 
		<wpsf:checkbox id="jpuserprofile_isPublic" name="publicProfile" value="#checkedValue" />
	</p>

	<%-- START CICLO ATTRIBUTI --%>
	<s:iterator value="userProfile.attributeList" id="attribute">
		
		<%-- INIZIALIZZAZIONE TRACCIATORE --%>
		<wpsa:tracerFactory var="attributeTracer" lang="%{#lang.code}" />
		
		<s:if test="#attribute.type == 'List' || #attribute.type == 'Monolist'">
		<p class="important">
			<span class="monospace">(<s:text name="label.list" />)&#32;</span><s:property value="#attribute.name" /><s:include value="/WEB-INF/apsadmin/jsp/entity/modules/include/attributeInfo.jsp" />:
		</p>
		</s:if>
		<s:elseif test="#attribute.type == 'Boolean' || #attribute.type == 'ThreeState'">
		<p class="important">
			<s:property value="#attribute.name" /><s:include value="/WEB-INF/apsadmin/jsp/entity/modules/include/attributeInfo.jsp" />:
		</p>
		</s:elseif>
		<s:elseif test="#attribute.type == 'CheckBox'">
		<%-- Leave this completely blank --%>
		</s:elseif>
		<s:else>
		<p>
			<label for="<s:property value="%{#attributeTracer.getFormFieldName(#attribute)}" />"><s:property value="#attribute.name" /><s:include value="/WEB-INF/apsadmin/jsp/entity/modules/include/attributeInfo.jsp" />:</label><br />
		</s:else>
		
		<s:if test="#attribute.type == 'Monotext'">
		<%-- ############# ATTRIBUTO TESTO MONOLINGUA ############# --%>
		<s:include value="/WEB-INF/apsadmin/jsp/entity/modules/monotextAttribute.jsp" />
		</p>
		</s:if>
		
		<s:elseif test="#attribute.type == 'Text'">
		<%-- ############# ATTRIBUTO TESTO SEMPLICE MULTILINGUA ############# --%>
		<s:include value="/WEB-INF/apsadmin/jsp/entity/modules/textAttribute.jsp" />
		</p>
		</s:elseif>
		
		<s:elseif test="#attribute.type == 'Longtext'">
		<%-- ############# ATTRIBUTO TESTOLUNGO ############# --%>
		<s:include value="/WEB-INF/apsadmin/jsp/entity/modules/longtextAttribute.jsp" />
		</p>
		</s:elseif>
		
		<s:elseif test="#attribute.type == 'Hypertext'">
		<%-- ############# ATTRIBUTO TESTOLUNGO ############# --%>
		<s:include value="/WEB-INF/apsadmin/jsp/entity/modules/hypertextAttribute.jsp" />
		</p>
		</s:elseif>
		
		<s:elseif test="#attribute.type == 'Boolean'">
		<%-- ############# ATTRIBUTO Boolean ############# --%>
		<s:include value="/WEB-INF/apsadmin/jsp/entity/modules/booleanAttribute.jsp" />
		</s:elseif>
		
		<s:elseif test="#attribute.type == 'ThreeState'">
		<%-- ############# ATTRIBUTO ThreeState ############# --%>
		<s:include value="/WEB-INF/apsadmin/jsp/entity/modules/threeStateAttribute.jsp" />
		</s:elseif>
		
		<s:elseif test="#attribute.type == 'Number'">
		<%-- ############# ATTRIBUTO Number ############# --%>
		<s:include value="/WEB-INF/apsadmin/jsp/entity/modules/numberAttribute.jsp" />
		</p>
		</s:elseif>
		
		<s:elseif test="#attribute.type == 'Date'">
		<%-- ############# ATTRIBUTO Date ############# --%>
		<s:include value="/WEB-INF/apsadmin/jsp/entity/modules/dateAttribute.jsp" />
		</p>
		</s:elseif>
		
		<s:elseif test="#attribute.type == 'Enumerator'">
		<%-- ############# ATTRIBUTO TESTO Enumerator ############# --%>
		<s:include value="/WEB-INF/apsadmin/jsp/entity/modules/enumeratorAttribute.jsp" />
		</p>
		</s:elseif>
		
		<s:elseif test="#attribute.type == 'Monolist'">
		<%-- ############# ATTRIBUTO Monolist ############# --%>
		<s:include value="/WEB-INF/apsadmin/jsp/entity/modules/monolistAttribute.jsp" />
		</s:elseif>
		
		<s:elseif test="#attribute.type == 'List'">
		<%-- ############# ATTRIBUTO List ############# --%>
		<s:include value="/WEB-INF/apsadmin/jsp/entity/modules/listAttribute.jsp" />
		</s:elseif>
		
		<s:elseif test="#attribute.type == 'Composite'">
		<%-- ############# ATTRIBUTO Composite ############# --%>
		<s:include value="/WEB-INF/apsadmin/jsp/entity/modules/compositeAttribute.jsp" />
		</p>
		</s:elseif>
		
		<s:elseif test="#attribute.type == 'CheckBox'">
		<%-- ############# ATTRIBUTO CheckBox ############# --%>
		<p><s:include value="/WEB-INF/apsadmin/jsp/entity/modules/checkBoxAttribute.jsp" /></p>
		</s:elseif>
		
	</s:iterator>
	<%-- END CICLO ATTRIBUTI --%>
	
	<p><wpsf:submit value="%{getText('label.save')}" cssClass="button" action="save" /></p>
</s:form>