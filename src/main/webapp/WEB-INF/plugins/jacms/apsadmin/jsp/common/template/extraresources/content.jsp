<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="wpsa" uri="apsadmin-core.tld" %>
<s:include value="/WEB-INF/apsadmin/jsp/common/template/defaultExtraResources.jsp" />

<!-- for Date Attribute -->
<script type="text/javascript" src="<wp:resourceURL />administration/js/calendar_wiz.js"></script>
<link rel="stylesheet" type="text/css" media="screen" href="<wp:resourceURL />administration/css/calendar.css" />
<!--[if gte IE 7]>
	<link rel="stylesheet" type="text/css" media="screen" href="<wp:resourceURL />administration/css/calendar_ie.css" />
<![endif]-->

<s:if test="htmlEditorCode == 'fckeditor'">
	<!-- per attributo Hypertext -->
	<script type="text/javascript" src="<wp:resourceURL />administration/js/fckeditor/fckeditor.js"></script>
</s:if>
	
<script type="text/javascript">
<!--//--><![CDATA[//><!--

//for content tabs
window.addEvent('domready', function(){
	 var tabSet = new Taboo({
			tabs: "tab",
			tabTogglers: "tab-toggle",
			activeTabClass: "tab-current"
		});
});

window.addEvent('domready', function(){
	$$('.js_noscreen').each(function(delendum) {
		delendum.removeClass("js_noscreen");
		delendum.addClass("noscreen");
	});
});

//for content categories
window.addEvent('domready', function(){
	var catTree  = new Wood({
		menuToggler: "subTreeToggler",
		rootId: "categoryTree",
		openClass: "node_open",
		closedClass: "node_closed",
		showTools: "true",
		expandAllLabel: "<s:text name="label.expandAll" />",
		collapseAllLabel: "<s:text name="label.collapseAll" />",
		type: "tree",
		startIndex: "<s:property value="selectedNode" />",
		toolTextIntro: "<s:text name="label.introExpandAll" />",
		toolexpandAllLabelTitle: "<s:text name="label.expandAllTitle" />",
		toolcollapseLabelTitle: "<s:text name="label.collapseAllTitle" />"	
	});

});

//per attributo Date
<s:iterator value="content.attributeList" id="attribute">
<%-- INIZIALIZZAZIONE TRACCIATORE --%>

<wpsa:tracerFactory var="attributeTracer" lang="%{#lang.code}" />

<s:if test="#attribute.type == 'Date'">
window.addEvent('domready', function() { myCal_<s:property value="%{#attributeTracer.getFormFieldName(#attribute)}" /> = new Calendar({ <s:property value="%{#attributeTracer.getFormFieldName(#attribute)}" />: 'd/m/Y' }, { 
		navigation: 1, 
		months: ['<s:text name="calendar.month.gen" />','<s:text name="calendar.month.feb" />','<s:text name="calendar.month.mar" />','<s:text name="calendar.month.apr" />','<s:text name="calendar.month.may" />','<s:text name="calendar.month.jun" />','<s:text name="calendar.month.jul" />','<s:text name="calendar.month.aug" />','<s:text name="calendar.month.sep" />','<s:text name="calendar.month.oct" />','<s:text name="calendar.month.nov" />','<s:text name="calendar.month.dec" />'],
		days: ['<s:text name="calendar.week.sun" />','<s:text name="calendar.week.mon" />','<s:text name="calendar.week.tue" />','<s:text name="calendar.week.wen" />','<s:text name="calendar.week.thu" />','<s:text name="calendar.week.fri" />','<s:text name="calendar.week.sat" />'],
		calendarTitle: "<s:text name="calendar.button.title" />",
		//prevTitle: '',  
		//nextTitle: '',
		prevText: "<s:text name="calendar.label.prevText" />",	//Mese precedente 
		nextText: "<s:text name="calendar.label.nextText" />",	//Mese successivo
		introText: "<s:text name="calendar.label.introText" />"	//Benvenuto nel calendario			
	});});
</s:if>

<s:elseif test="#attribute.type == 'Monolist'">
<s:set name="masterAttributeTracer" value="#attributeTracer" />
<s:set name="masterAttribute" value="#attribute" />
<s:iterator value="#attribute.attributes" id="attribute" status="elementStatus">
<s:set name="attributeTracer" value="#masterAttributeTracer.getMonoListElementTracer(#elementStatus.index)"></s:set>
<s:set name="elementIndex" value="#elementStatus.index" />

	<s:if test="#attribute.type == 'Composite'">
	<s:set name="masterCompositeAttributeTracer" value="#attributeTracer" />
	<s:set name="masterCompositeAttribute" value="#attribute" />
	<s:iterator value="#attribute.attributes" id="attribute">
		<s:set name="attributeTracer" value="#masterCompositeAttributeTracer.getCompositeTracer(#masterCompositeAttribute)"></s:set>
		<s:set name="parentAttribute" value="#masterCompositeAttribute"></s:set>

		<s:if test="#attribute.type == 'Date'">
		window.addEvent('domready', function() { myCal_<s:property value="%{#attributeTracer.getFormFieldName(#attribute)}" /> = new Calendar({ <s:property value="%{#attributeTracer.getFormFieldName(#attribute)}" />: 'd/m/Y' }, { 
				navigation: 1, 
				months: ['<s:text name="calendar.month.gen" />','<s:text name="calendar.month.feb" />','<s:text name="calendar.month.mar" />','<s:text name="calendar.month.apr" />','<s:text name="calendar.month.may" />','<s:text name="calendar.month.jun" />','<s:text name="calendar.month.jul" />','<s:text name="calendar.month.aug" />','<s:text name="calendar.month.sep" />','<s:text name="calendar.month.oct" />','<s:text name="calendar.month.nov" />','<s:text name="calendar.month.dec" />'],
				days: ['<s:text name="calendar.week.mon" />','<s:text name="calendar.week.tue" />','<s:text name="calendar.week.wen" />','<s:text name="calendar.week.thu" />','<s:text name="calendar.week.fri" />','<s:text name="calendar.week.sat" />','<s:text name="calendar.week.sun" />'],
				calendarTitle: '<s:text name="calendar.button.title" />',
				//prevTitle: '',  
				//nextTitle: '',
				prevText: "<s:text name="calendar.label.prevText" />",	//Mese precedente 
				nextText: "<s:text name="calendar.label.nextText" />",	//Mese successivo
				introText: "<s:text name="calendar.label.introText" />"	//Benvenuto nel calendario
			});});
		</s:if>

	</s:iterator>
	<s:set name="attributeTracer" value="#masterCompositeAttributeTracer" />
	<s:set name="attribute" value="#masterCompositeAttribute" />
	<s:set name="parentAttribute" value=""></s:set>
	</s:if>

	<s:elseif test="#attribute.type == 'Date'">
window.addEvent('domready', function() { myCal_<s:property value="%{#attributeTracer.getFormFieldName(#attribute)}" /> = new Calendar({ <s:property value="%{#attributeTracer.getFormFieldName(#attribute)}" />: 'd/m/Y' }, { 
		navigation: 1, 
		months: ['<s:text name="calendar.month.gen" />','<s:text name="calendar.month.feb" />','<s:text name="calendar.month.mar" />','<s:text name="calendar.month.apr" />','<s:text name="calendar.month.may" />','<s:text name="calendar.month.jun" />','<s:text name="calendar.month.jul" />','<s:text name="calendar.month.aug" />','<s:text name="calendar.month.sep" />','<s:text name="calendar.month.oct" />','<s:text name="calendar.month.nov" />','<s:text name="calendar.month.dec" />'],
		days: ['<s:text name="calendar.week.mon" />','<s:text name="calendar.week.tue" />','<s:text name="calendar.week.wen" />','<s:text name="calendar.week.thu" />','<s:text name="calendar.week.fri" />','<s:text name="calendar.week.sat" />','<s:text name="calendar.week.sun" />'],
		calendarTitle: '<s:text name="calendar.button.title" />',
		//prevTitle: '',  
		//nextTitle: '',
		prevText: "<s:text name="calendar.label.prevText" />",	//Mese precedente 
		nextText: "<s:text name="calendar.label.nextText" />",	//Mese successivo
		introText: "<s:text name="calendar.label.introText" />"	//Benvenuto nel calendario
	});});
	</s:elseif>
	
</s:iterator>
<s:set name="attributeTracer" value="#masterAttributeTracer" />
<s:set name="attribute" value="#masterAttribute" />
</s:elseif>

</s:iterator>
//fine attributo Date

//per attributo Hypertext
<s:if test="htmlEditorCode != 'none'">
	
	<s:iterator value="langs" id="lang">
		<s:iterator value="content.attributeList" id="attribute">
		<%-- INIZIALIZZAZIONE TRACCIATORE --%>
		<wpsa:tracerFactory var="attributeTracer" lang="%{#lang.code}" />
		
		<s:if test="#attribute.type == 'Hypertext'">
			<s:if test="htmlEditorCode == 'fckeditor'">				
				window.addEvent('domready', function() {
					var ofckeditor = new FCKeditor( "<s:property value="%{#attributeTracer.getFormFieldName(#attribute)}" />" ); 
					ofckeditor.Config["AppBaseUrl"] = "<wp:info key="systemParam" paramName="applicationBaseURL" />";
					ofckeditor.BasePath = "<wp:resourceURL />administration/js/fckeditor/";
					ofckeditor.ToolbarSet = "jAPS";
					ofckeditor.Config["CustomConfigurationsPath"] = "<wp:resourceURL />administration/js/fckeditor/jAPSConfig.js";
					ofckeditor.Height = 250;
					ofckeditor.ReplaceTextarea();
				});
			</s:if>
			<s:elseif test="htmlEditorCode == 'hoofed'">
				window.addEvent('domready', function() {
					var ohoofed = new HoofEd({
						basePath: '<wp:resourceURL />administration/js/moo-japs/hoofed',
						lang: '<s:property value="currentLang.code" />',
						textareaID: '<s:property value="%{#attributeTracer.getFormFieldName(#attribute)}" />',
						buttons: [ 'bold', 'italic', 'list', 'nlist', 'link', 'paragraph' ],
						toolPosition: "after",
						toolElement: "span" 
					});
				}); 
			</s:elseif>
		</s:if>

		<s:elseif test="#attribute.type == 'Monolist'">
			<s:set name="masterAttributeTracer" value="#attributeTracer" />
			<s:set name="masterAttribute" value="#attribute" />
			<s:iterator value="#attribute.attributes" id="attribute" status="elementStatus">
				<s:set name="attributeTracer" value="#masterAttributeTracer.getMonoListElementTracer(#elementStatus.index)"></s:set>
				<s:set name="elementIndex" value="#elementStatus.index" />
				
				
				<s:if test="#attribute.type == 'Composite'">
					<s:set name="masterCompositeAttributeTracer" value="#attributeTracer" />
					<s:set name="masterCompositeAttribute" value="#attribute" />
					<s:iterator value="#attribute.attributes" id="attribute">
						<s:set name="attributeTracer" value="#masterCompositeAttributeTracer.getCompositeTracer(#masterCompositeAttribute)"></s:set>
						<s:set name="parentAttribute" value="#masterCompositeAttribute"></s:set>
						<s:if test="#attribute.type == 'Hypertext'">
							<s:if test="htmlEditorCode == 'fckeditor'">	
								window.addEvent('domready', function() {
									var ofckeditor = new FCKeditor( "<s:property value="%{#attributeTracer.getFormFieldName(#attribute)}" />" ); 
									ofckeditor.Config["AppBaseUrl"] = "<wp:info key="systemParam" paramName="applicationBaseURL" />";
									ofckeditor.BasePath = "<wp:resourceURL />administration/js/fckeditor/";
									ofckeditor.ToolbarSet = "jAPS";
									ofckeditor.Config["CustomConfigurationsPath"] = "<wp:resourceURL />administration/js/fckeditor/jAPSConfig.js";
									ofckeditor.Height = 250;
									ofckeditor.ReplaceTextarea();
								});
							</s:if>
							<s:elseif test="htmlEditorCode == 'hoofed'">	
								window.addEvent('domready', function() {
									var ohoofed = new HoofEd({
										basePath: '<wp:resourceURL />administration/js/moo-japs/hoofed',
										lang: '<s:property value="currentLang.code" />', 
										textareaID: '<s:property value="%{#attributeTracer.getFormFieldName(#attribute)}" />',
										buttons: [ 'bold', 'italic', 'list', 'nlist', 'link', 'paragraph' ],
										toolPosition: "after",
										toolElement: "span"
									}); 
								});
							</s:elseif>
						</s:if>
					</s:iterator>
					<s:set name="attributeTracer" value="#masterCompositeAttributeTracer" />
					<s:set name="attribute" value="#masterCompositeAttribute" />
					<s:set name="parentAttribute" value=""></s:set>
				</s:if>
				
				
				<s:elseif test="#attribute.type == 'Hypertext'">
					<s:if test="htmlEditorCode == 'fckeditor'">	
						window.addEvent('domready', function() {

							var ofckeditor = new FCKeditor( "<s:property value="%{#attributeTracer.getFormFieldName(#attribute)}" />" ); 
							ofckeditor.Config["AppBaseUrl"] = "<wp:info key="systemParam" paramName="applicationBaseURL" />";
							ofckeditor.BasePath = "<wp:resourceURL />administration/js/fckeditor/";
							ofckeditor.ToolbarSet = "jAPS";
							ofckeditor.Config["CustomConfigurationsPath"] = "<wp:resourceURL />administration/js/fckeditor/jAPSConfig.js";
							ofckeditor.Height = 250;
							ofckeditor.ReplaceTextarea();
						});
					</s:if>
					<s:elseif test="htmlEditorCode == 'hoofed'">	
						window.addEvent('domready', function() {
							var ohoofed = new HoofEd({
								basePath: '<wp:resourceURL />administration/js/moo-japs/hoofed',
								lang: '<s:property value="currentLang.code" />', 
								textareaID: '<s:property value="%{#attributeTracer.getFormFieldName(#attribute)}" />',
								buttons: [ 'bold', 'italic', 'list', 'nlist', 'link', 'paragraph' ],
								toolPosition: "after",
								toolElement: "span"
							}); 
						});
					</s:elseif>
				</s:elseif>
			</s:iterator>
			<s:set name="attributeTracer" value="#masterAttributeTracer" />
			<s:set name="attribute" value="#masterAttribute" />
		</s:elseif>
		</s:iterator>
	</s:iterator>

</s:if>
//fine attributo Hypertext

//--><!]]></script>