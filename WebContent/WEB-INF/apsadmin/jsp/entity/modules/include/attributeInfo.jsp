<%@ taglib prefix="s" uri="/struts-tags" %>

<s:if test="#attribute.required || #attribute.searcheable || #attribute.indexingType != 'NONE' || (#attribute.textAttribute && (#attribute.minLength != -1 || #attribute.maxLength != -1))">
	<span class="monospace"> [
	<s:if test="#attribute.required"><abbr title="<s:text name="Entity.attribute.flag.mandatory.full"/>"><s:text name="Entity.attribute.flag.mandatory.short"/></abbr> </s:if>
	<s:if test="#attribute.searcheable"><abbr title="<s:text name="Entity.attribute.flag.searcheable.full"/>"><s:text name="Entity.attribute.flag.searcheable.short"/></abbr> </s:if>
	<s:if test="#attribute.indexingType != 'NONE'"><abbr title="<s:text name="Entity.attribute.flag.indexed.full"/>"><s:text name="Entity.attribute.flag.indexed.short"/></abbr> </s:if>
	<s:if test="#attribute.textAttribute">
		<s:if test="#attribute.minLength != -1">&#32;<abbr title="<s:text name="Entity.attribute.flag.minLength.full" />" ><s:text name="Entity.attribute.flag.minLength.short" /></abbr>:<s:property value="#attribute.minLength" /> </s:if>
		<s:if test="#attribute.maxLength != -1">&#32;<abbr title="<s:text name="Entity.attribute.flag.maxLength.full" />" ><s:text name="Entity.attribute.flag.maxLength.short" /></abbr>:<s:property value="#attribute.maxLength" /> </s:if>
	</s:if>
	]</span></s:if>
