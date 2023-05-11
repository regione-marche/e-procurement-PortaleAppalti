<%@ taglib prefix="wp" uri="aps-core.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
	<script>
		function OpenSideMenu(hamburger)
		{
			var all = document.getElementById("ext-container");
			var menu = document.getElementsByClassName("responsive-static-menu")[0];
			var hb = hamburger;
			if(hamburger.checked)
			{
				all.className += " move-content-right";
				menu.className += " open";
			}
			else{
				all.className = all.className.replace( /(?:^|\s)move-content-right(?!\S)/g , '' );	
				menu.className = menu.className.replace( /(?:^|\s)open(?!\S)/g , '' );	
			}
		}

		</script>
<div id="menu-wrapper">
	<div id="menu-top"></div>
	<div id="menu">
		<div class="top-menu-container">
			<div class="hamburger-menu">
				<input type="checkbox" class="hamburger-checkbox" onclick="OpenSideMenu(this)" title="menu" />
				<div class="hamburger-line smooth-transition first"><div class="line-color"></div></div>
				<div class="hamburger-line smooth-transition second"><div class="line-color"></div></div>
				<div class="hamburger-line smooth-transition third"><div class="line-color"></div></div>
			</div>

			<div class="responsive-right-menu">
				<div id="date-sub-menu" class="sub-menu">
					<h2 class="noscreen information"><wp:i18n key="SECTION_DATE_TIME" />:</h2>
					<wp:show frame="0"/>
					<p class="noscreen">[ <a href="#pagestart"><wp:i18n key="BACK_TO_THE_TOP" /></a> ]</p><!-- end #date-sub-menu -->
				</div>
				<div id="search-sub-menu" class="sub-menu">
					<h2 class="noscreen information"><wp:i18n key="SECTION_CMS_SEARCH" />:</h2>
					<p class="noscreen">[ <a id="searchform" href="#menu1"><wp:i18n key="SKIP_TO_MENU" /></a> ]</p>
					<wp:show frame="1"/>
					<p class="noscreen">[ <a href="#pagestart"><wp:i18n key="BACK_TO_THE_TOP" /></a> ]</p><!-- end #search-sub-menu -->
				</div>
			</div>
		</div>	
		<div id="style-sub-menu" class="sub-menu">
			<h2 class="noscreen information"><wp:i18n key="SECTION_ACCESSIBILITY_FUNCTIONS" />:</h2>
			<ul>
				<li class="submenu">
					<a class="font-normal" title="<wp:i18n key="FONTS_TITLE_NORMAL" />" href="<wp:url page="${currentViewCode}"><wp:urlPar name="font">normal</wp:urlPar></wp:url>&amp;${tokenHrefParams}">A</a>
				</li>
				
				<li class="submenu">
					<a class="font-big" title="<wp:i18n key="FONTS_TITLE_BIG" />" href="<wp:url page="${currentViewCode}"><wp:urlPar name="font">big</wp:urlPar></wp:url>&amp;${tokenHrefParams}">A</a>
				</li>
				
				<li class="submenu">
					<a class="font-very-big" title="<wp:i18n key="FONTS_TITLE_VERY_BIG" />" href="<wp:url page="${currentViewCode}"><wp:urlPar name="font">verybig</wp:urlPar></wp:url>&amp;${tokenHrefParams}">A</a>
				</li>
				<li class="responsive-sub-menu-divider"><br/></li>
				<li class="submenu">
					<a title="<wp:i18n key="TITLE_GRAPHICS_MODE_NORMAL" />" href="<wp:url page="${currentViewCode}"><wp:urlPar name="skin">normal</wp:urlPar></wp:url>&amp;${tokenHrefParams}"><wp:i18n key="LINK_GRAPHICS_MODE_NORMAL" /></a>
				</li>
				
				<li class="submenu">
					<a title="<wp:i18n key="TITLE_GRAPHICS_MODE_TEXT" />" href="<wp:url page="${currentViewCode}"><wp:urlPar name="skin">text</wp:urlPar></wp:url>&amp;${tokenHrefParams}"><wp:i18n key="LINK_GRAPHICS_MODE_TEXT" /></a>
				</li>
				
				<li class="submenu">
					<a title="<wp:i18n key="TITLE_GRAPHICS_MODE_HIGH_CONTRAST" />" href="<wp:url page="${currentViewCode}"><wp:urlPar name="skin">highcontrast</wp:urlPar></wp:url>&amp;${tokenHrefParams}"><wp:i18n key="LINK_GRAPHICS_MODE_HIGH_CONTRAST" /></a>
				</li>
				<c:if test="${sessionScope.currentUser == 'guest' || sessionScope.currentUser.credentialsNotExpired}">

					<li class="responsive-sub-menu-divider"><br/></li>
					<li class="submenu">
						<a title="<wp:i18n key="TITLE_GO_TO_SITEMAP" />" href="<wp:url page="ppcommon_site_map"/>"><wp:i18n key="TITLE_SITEMAP" /></a>
					</li>				
				</c:if>
			</ul>
			<p class="noscreen">[ <a href="#pagestart"><wp:i18n key="BACK_TO_THE_TOP" /></a> ]</p><!-- end #style-sub-menu -->
		</div><!-- end #menu -->

	</div>
	<div id="menu-sub"></div>
</div>