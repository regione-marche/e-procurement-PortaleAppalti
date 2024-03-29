<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="wpsa" uri="apsadmin-core.tld" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>

<s:if test="#attribute.symbolicLink != null">
	
	<s:if test="#attribute.symbolicLink.destType == 2 || #attribute.symbolicLink.destType == 4">
		<s:set name="linkedPage" value="%{getPage(#attribute.symbolicLink.pageDest)}"></s:set>
	</s:if>
	<s:if test="#attribute.symbolicLink.destType == 3 || #attribute.symbolicLink.destType == 4">
		<s:set name="linkedContent" value="%{getContentVo(#attribute.symbolicLink.contentDest)}"></s:set>
	</s:if>
	<s:set name="validLink" value="true"></s:set>
	<s:if test="(#attribute.symbolicLink.destType == 2 || #attribute.symbolicLink.destType == 4) && #linkedPage == null">
	<s:text name="LinkAttribute.fieldError.linkToPage.voidPage" />
	<s:set name="validLink" value="false"></s:set>
	</s:if>
	<s:if test="(#attribute.symbolicLink.destType == 3 || #attribute.symbolicLink.destType == 4) && (#linkedContent == null || !#linkedContent.onLine)">
	<s:text name="LinkAttribute.fieldError.linkToContent" />
	<s:set name="validLink" value="false"></s:set>
	</s:if>
	
	<s:if test="#validLink">
	<%-- LINK VALORIZZATO CORRETTAMENTE --%>
	
		<s:if test="#attribute.symbolicLink.destType == 1">
			<wpsa:set name="iconImagePath" id="iconImagePath"><wp:resourceURL/>administration/img/icons/link-url.png</wpsa:set>
			<s:set name="linkDestination" value="%{getText('note.URLLinkTo') + ': ' + #attribute.symbolicLink.urlDest}" />
		</s:if>
		
		<s:if test="#attribute.symbolicLink.destType == 2">
			<wpsa:set name="iconImagePath" id="iconImagePath"><wp:resourceURL/>administration/img/icons/link-page.png</wpsa:set>
			<s:set name="linkDestination" value="%{getText('note.pageLinkTo') + ': ' + #linkedPage.titles[currentLang.code]}" />
		</s:if>
		
		<s:if test="#attribute.symbolicLink.destType == 3">
			<wpsa:set name="iconImagePath" id="iconImagePath"><wp:resourceURL/>administration/img/icons/link-content.png</wpsa:set>
			<s:set name="linkDestination" value="%{getText('note.contentLinkTo') + ': ' + #attribute.symbolicLink.contentDest + ' - ' + #linkedContent.descr}" />
		</s:if>
		
		<s:if test="#attribute.symbolicLink.destType == 4">
			<wpsa:set name="iconImagePath" id="iconImagePath"><wp:resourceURL/>administration/img/icons/link-contentOnPage.png</wpsa:set>
			<s:set name="linkDestination" value="%{getText('note.contentLinkTo') + ': ' + #attribute.symbolicLink.contentDest + ' - ' + #linkedContent.descr + ', ' + getText('note.contentOnPageLinkTo') + ': ' + #linkedPage.titles[currentLang.code]}" />
		</s:if>
		
		<img src="<s:property value="iconImagePath" />" alt="<s:property value="linkDestination" />" title="<s:property value="linkDestination" />" />
		<%-- CAMPO DI TESTO --%>
		<s:include value="/WEB-INF/apsadmin/jsp/entity/view/textAttribute.jsp" />&#32;[<s:property value="linkDestination" />]
	</s:if>
</s:if>