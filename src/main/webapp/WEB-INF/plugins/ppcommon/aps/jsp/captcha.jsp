<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<%@page import="java.util.*" %>

<%-- STYLE --%>

<style id="frc-style">
	.frc-captcha *{
		margin:0;
		padding:0;
		border:0;
		text-align:initial;
		border-radius:px;
		filter:none!important;
		transition:none!important;
		font-family: Roboto;
		font-weight:400;
		font-size:14px;
		line-height:1.2;
		text-decoration:none;
		background-color:initial;
		color:#222
	}
	.frc-captcha{
		position:relative;
		min-width:250px;
		max-width:312px;
		border:1px solid #f4f4f4;
		padding-bottom:12px;
		background-color:#fff
	}
	.frc-captcha b{font-weight:700}
	.frc-container{display:flex;align-items:center;min-height:52px}
	.frc-icon{fill:#222;stroke:#222;flex-shrink:0;margin:8px 8px 0}
	.frc-icon.frc-warning{fill:#c00}
	.frc-success .frc-icon{animation:1s ease-in both frc-fade-in}
	.frc-content{white-space:nowrap;display:flex;flex-direction:column;margin:4px 6px 0 0;overflow-x:auto;flex-grow:1}
	.frc-banner{position:absolute;bottom:0;right:6px;line-height:1}
	.frc-banner *{font-size:10px;opacity:.8;text-decoration:none}
	.frc-progress{-webkit-appearance:none;-moz-appearance:none;appearance:none;margin:3px 0;height:4px;border:none;background-color:#eee;color:#222;width:100%;transition:.5s linear}
	.frc-progress::-webkit-progress-bar{background:#eee}
	.frc-progress::-webkit-progress-value{background:#222}
	.frc-progress::-moz-progress-bar{background:#222}
	.frc-button{cursor:pointer;padding:2px 6px;background-color:#f1f1f1;border:1px solid transparent;text-align:center;font-weight:600;text-transform:none}
	.frc-button:focus{border:1px solid #333}
	.frc-button:hover{background-color:#ddd}
	.frc-captcha-solution{display:none}
	.frc-err-url{text-decoration:underline;font-size:.9em}
	.dark .frc-captcha{color:#fff;background-color:#222;border-color:#333}
	.dark .frc-captcha *{color:#fff}
	.dark .frc-captcha button{background-color:#444}
	.dark .frc-icon{fill:#fff;stroke:#fff}
	.dark .frc-progress{background-color:#444}
	.dark .frc-progress::-webkit-progress-bar{background:#444}
	.dark .frc-progress::-webkit-progress-value{background:#ddd}
	.dark .frc-progress::-moz-progress-bar{background:#ddd}@keyframes frc-fade-in{from{opacity:0}to{opacity:1}}
</style>


<c:set var="baseUrl"><wp:info key="systemParam" paramName="applicationBaseURL"/></c:set>	
<%--
RIMOSSO PERCHE' E' GIA' PRESENTE LA VERSIONE \WEB-INF\lib\jquery-3.7.1.min.js 
<script src="${baseUrl}resources/static/js/captcha/jquery-3.6.0.slim.js" integrity="sha256-HwWONEZrpuoh951cQD1ov2HUK5zA5DwJ1DNUXaM6FsY=" crossorigin="anonymous"></script>
 --%>	
<script nomodule src="${baseUrl}resources/static/js/captcha/widget.min.js" async defer></script>
<script type="module" src="${baseUrl}resources/static/js/captcha/widget.module.min.js" async defer></script>

<%
Random rand = new Random();
int n = rand.nextInt(9) + 1;
%>
<c:set var="n"><%=n % 3%></c:set>
 
<table width="100%">
	<tr style="background-color:rgba(0, 0, 0, 0);">
		<c:if test="${n == 2}">
			<td width="33%"/>
			<td width="33%"/>
		</c:if>
		<c:if test="${n == 1}">
			<td width="33%"/>
		</c:if>
		<td width="34%">
			<div style="display: flex; justify-content: center;" width="100%">
				<%-- **************************************************************************************************** --%>
				<%-- friendly captcha
					 data-sitekey="FCMV995O03V7RIMQ" e' quello utilizzato da Maggioli, 
				     viceversa ne va regigrato uno nuovo su https://docs.friendlycaptcha.com/#/installation?id=_1-generating-a-sitekey 
				--%>
				<div class="frc-captcha" 
					 data-sitekey="FCMV995O03V7RIMQ" 
					 data-lang="<wp:info key='currentLang'/>" 
					 data-start="none">
				</div>
			</div>
		</td>
		<c:if test="${n == 1}">
			<td width="33%"/>
		</c:if>
		<c:if test="${n == 0}">
			<td width="33%"/>
			<td width="33%"/>
		</c:if>
	</tr>
</table>


<c:set var="formId" value="${param.submitForm}"/>
<c:set var="autoSubmit" value="${param.autoSubmitForm}"/>

<script type="text/javascript">
<!--//--><![CDATA[//><!--
	
	const captchaStatus = ['.UNSTARTED', '.FETCHING', '.UNFINISHED', '.EXPIRED', '.HEADLESS_ERROR', '.ERROR'];
	const submitForm = '#${formId}';
	const autoSubmit = '${autoSubmit}';

	// gestione delle variazioni dell'observer su ".frc-captcha"
	//
	function onDomMutation(element) {
		element.forEach(element => {
	      	for (var i = 0; i < element.addedNodes.length; i++) {
//console.log('name="' + element.addedNodes[i].name + '"'
//			+ ', id="' + element.addedNodes[i].id + '"'
//			//+ ', textContent="' + element.addedNodes[i].textContent + '"'
//			+ ' added');
			    if(element.addedNodes[i].name == 'frc-captcha-solution') {
					let solution = $('.frc-captcha-solution').val();
					let controlliOk = true;
					for(const item of captchaStatus) {
						if(solution == item) {
							controlliOk = false;
							break;
						}
					}
					if(controlliOk) {
						if(autoSubmit == "1") {
							$(submitForm).submit();
						}
			    	}
				}
			}
	    });
	}

	// aggiungi un obsever per le variazioni dell'elemento ".frc-captcha"
	// 
	$(document).ready(function() {
		var observer = new MutationObserver(onDomMutation);
		observer.observe(document.querySelector(".frc-captcha"), { childList: true });
	});
	
//--><!]]>
</script>
