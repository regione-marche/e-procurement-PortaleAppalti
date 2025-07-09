<%@ taglib prefix="wp" uri="aps-core.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div id="menu-wrapper">
	<div id="menu-top"></div>
	<div id="menu">
		<div id="date-sub-menu" class="sub-menu">
			<h1 class="noscreen information"><wp:i18n key="SECTION_DATE_TIME" />:</h1>
			<wp:show frame="0"/>
			<p class="noscreen">[ <a href="#pagestart"><wp:i18n key="BACK_TO_THE_TOP" /></a> ]</p><!-- end #date-sub-menu -->
		</div>
		<div id="search-sub-menu" class="sub-menu">
			<h2 class="noscreen information"><wp:i18n key="SECTION_CMS_SEARCH" />:</h2>
			<p class="noscreen">[ <a id="searchform" href="#menu1"><wp:i18n key="SKIP_TO_MENU" /></a> ]</p>
			<wp:show frame="1"/>
			<p class="noscreen">[ <a href="#pagestart"><wp:i18n key="BACK_TO_THE_TOP" /></a> ]</p><!-- end #search-sub-menu -->
		</div>
		<div id="style-sub-menu" class="sub-menu">
			<h2 class="noscreen information"><wp:i18n key="SECTION_ACCESSIBILITY_FUNCTIONS" />:</h2>
			<ul>
				<li class="submenu">
					<a class="font-normal" title="<wp:i18n key="FONTS_TITLE_NORMAL" />" href="<wp:url page="${currentViewCode}"><wp:urlPar name="font">normal</wp:urlPar></wp:url>">A</a>
				</li>
				<li>-</li>
				<li class="submenu">
					<a class="font-big" title="<wp:i18n key="FONTS_TITLE_BIG" />" href="<wp:url page="${currentViewCode}"><wp:urlPar name="font">big</wp:urlPar></wp:url>">A</a>
				</li>
				<li>-</li>
				<li class="submenu">
					<a class="font-very-big" title="<wp:i18n key="FONTS_TITLE_VERY_BIG" />" href="<wp:url page="${currentViewCode}"><wp:urlPar name="font">verybig</wp:urlPar></wp:url>">A</a>
				</li>
				<li>|</li>
				<li class="submenu">
					<a title="<wp:i18n key="TITLE_GRAPHICS_MODE_NORMAL" />" href="<wp:url page="${currentViewCode}"><wp:urlPar name="skin">normal</wp:urlPar></wp:url>"><wp:i18n key="LINK_GRAPHICS_MODE_NORMAL" /></a>
				</li>
				<li>-</li>
				<li class="submenu">
					<a title="<wp:i18n key="TITLE_GRAPHICS_MODE_TEXT" />" href="<wp:url page="${currentViewCode}"><wp:urlPar name="skin">text</wp:urlPar></wp:url>"><wp:i18n key="LINK_GRAPHICS_MODE_TEXT" /></a>
				</li>
				<li>-</li>
				<li class="submenu">
					<a title="<wp:i18n key="TITLE_GRAPHICS_MODE_HIGH_CONTRAST" />" href="<wp:url page="${currentViewCode}"><wp:urlPar name="skin">highcontrast</wp:urlPar></wp:url>"><wp:i18n key="LINK_GRAPHICS_MODE_HIGH_CONTRAST" /></a>
				</li>
				
			</ul>
			<p class="noscreen">[ <a href="#pagestart"><wp:i18n key="BACK_TO_THE_TOP" /></a> ]</p><!-- end #style-sub-menu -->
		</div><!-- end #menu -->
	</div>
	<div id="menu-sub"></div>
</div>