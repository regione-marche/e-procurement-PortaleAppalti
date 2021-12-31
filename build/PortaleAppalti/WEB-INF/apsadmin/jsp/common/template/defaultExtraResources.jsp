<%@ taglib prefix="wp" uri="aps-core.tld" %>

<%@ taglib prefix="wpsa" uri="apsadmin-core.tld" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<script type="text/javascript" src="<wp:resourceURL />administration/js/mootools-1.2-core.js"></script>
<script type="text/javascript" src="<wp:resourceURL />administration/js/mootools-1.2-more.js"></script>
<script type="text/javascript" src="<wp:resourceURL />administration/js/moo-japs/moo-jAPS-0.2.js"></script>
<script type="text/javascript">
<!--//--><![CDATA[//><!--

//per gli accordion
window.addEvent('domready', function(){
	var myAccordion = new Accordion($$('.accordion_toggler'), $$('.accordion_element'), {
	    show: -1,
	    duration: 'short',
	    alwaysHide: true
	});

	var myAnchor = new Element('img', {
	    'src': '<wp:resourceURL/>administration/img/icons/media-eject.png',
	    'class': 'myClass',
	    'style': 'vertical-align: middle',
	    'alt': '<s:text name="label.open" /> ',
	    'title': '<s:text name="label.open" /> '	    
	});

	$$('.accordion_toggler').each(function(cToggler) {
		cToggler.appendText(' ');
		var poba = myAnchor.clone();
		poba.injectBottom(cToggler);
	});
	
});


var menoo;
                  
window.addEvent('domready', function(){
	
	var menoo  = new Wood({
		menuToggler: "subMenuToggler",
		type: "menu",
		enableHistory: true,
		cookieName: '<wp:info key="systemParam" paramName="applicationBaseURL" />'
	});

});
//--><!]]></script>