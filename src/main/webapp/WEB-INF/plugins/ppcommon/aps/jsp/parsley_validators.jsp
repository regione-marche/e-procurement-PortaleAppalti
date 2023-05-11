<%-- 
To include Parsley custom validators to a page add the following instructions
	
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/parsley_validators.jsp" />

	<script type="text/javascript">
  		$('#some_form_id').parsley();  
	</script>
	
--%>
<%@ taglib prefix="wp"  uri="aps-core.tld" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>

<%-- validazione delle lingue disponibili --%>
<c:set var="currentLangCode">
	<c:choose>
	<c:when test="${currentLang.code == 'en'}">${currentLang.code}</c:when>
	<c:when test="${currentLang.code == 'it'}">${currentLang.code}</c:when>
	<c:otherwise>it</c:otherwise>
	</c:choose>
</c:set>

<script type="text/javascript" src="<wp:resourceURL/>static/js/parsley.min.js"></script> 
<script type="text/javascript" src="<wp:resourceURL/>static/js/i18n/${currentLangCode}.js"></script>
<script type="text/javascript" src="<wp:resourceURL/>static/js/i18n/${currentLangCode}.extra.js"></script>

<%--
currentLang=${currentLang}<br/>
currentLang.code=${currentLang.code}<br/>
currentLang.descr=${currentLang.descr}<br/>
--%>
 

<%--
<script type="text/javascript">
	var currentLangCode = '${currentLang.code}';

	function countDecimals(value) {
		var ds = 1.1 + "";		// get decimal separator position
		d = value + "";
		var i = d.indexOf( ds.substring(1, 2) ); 
		return (i >= 0 ? d.substr(i+1).length : 0); 
	} 	

	
	// custom validator for percent format ##0.##### %
	//
	window.Parsley.addValidator('ispercent', {
	  validateNumber: function(value, maxdecimals) {
		var v = value * 1.0;
		var n = countDecimals(v);
	    return (n <= maxdecimals) && (0.0 <= v && v <= 100.0);
	  },
	  requirementType: 'number',
	  messages: {
		it: 'Il formato del valore deve essere una percentuale tra 0 e 100.',
	    en: 'This value should be a percent from 0 to 100.',
	  }
	});
	

	// custom validator for number format #,###,###,##0.#####  
	//
  	window.Parsley.addValidator('isnumber', {
	  validateNumber: function(value, maxdecimals) {
		return countDecimals(value) <= maxdecimals;
	  },
	  requirementType: 'number',
	  messages: {
	    it: 'Il formato del valore deve essere un numero.',
	    en: 'This value should be a number.',
	  }
	});
  	
	
	// custom validator for date format DD/MM/YYYY  
	// 
	window.Parsley.addValidator('isdate', {
    	validate: function(value, id) {
        	var isValid = moment(value, "DD/MM/YYYY", true).isValid();
        	return isValid;
    	},
    	messages: {
    		it: 'il formato del valore deve essere una data gg/mm/aaaa.',
        	en: 'Please provide date in format dd/mm/yyyy'
    	}
	}); 

	window.Parsley.setLocale(currentLangCode);
</script>
 --%>