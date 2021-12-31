<%@ taglib prefix="wp"  uri="aps-core.tld"%>

				<dd><a href="<wp:url page="homepage"/>?${tokenHrefParams}" accesskey="H"><wp:i18n key="LINK_GO_TO_HOMEPAGE"/></a> [H]</dd>
				<dd><a href="<wp:url page="ppgare_accessibilita"/>?${tokenHrefParams}" accesskey="W"><wp:i18n key="LINK_GO_TO_PAGE_ACCESSIBILITY"/></a> [W]</dd>
				<dd><a href="<wp:url page="ppcommon_site_map"/>?${tokenHrefParams}" accesskey="Y"><wp:i18n key="TITLE_GO_TO_SITEMAP" /></a> [Y]</dd>

				<dd><a href="<wp:url page="${currentViewCode}"><wp:urlPar name="font">normal</wp:urlPar></wp:url>&amp;${tokenHrefParams}" accesskey="N"><wp:i18n key="LINK_SET_FONT_NORMAL" /></a> [N]</dd>
				<dd><a href="<wp:url page="${currentViewCode}"><wp:urlPar name="font">big</wp:urlPar></wp:url>&amp;${tokenHrefParams}" accesskey="B"><wp:i18n key="LINK_SET_FONT_BIG" /></a> [B]</dd>
				<dd><a href="<wp:url page="${currentViewCode}"><wp:urlPar name="font">verybig</wp:urlPar></wp:url>&amp;${tokenHrefParams}" accesskey="V"><wp:i18n key="LINK_SET_FONT_VERY_BIG" /></a> [V]</dd>
				<dd><a href="<wp:url page="${currentViewCode}"><wp:urlPar name="skin">normal</wp:urlPar></wp:url>&amp;${tokenHrefParams}" accesskey="G"><wp:i18n key="LINK_SET_GRAPHICS_MODE_NORMAL" /></a> [G]</dd>
				<dd><a href="<wp:url page="${currentViewCode}"><wp:urlPar name="skin">text</wp:urlPar></wp:url>&amp;${tokenHrefParams}" accesskey="T"><wp:i18n key="LINK_SET_GRAPHICS_MODE_TEXT" /></a> [T]</dd>
				<dd><a href="<wp:url page="${currentViewCode}"><wp:urlPar name="skin">highcontrast</wp:urlPar></wp:url>&amp;${tokenHrefParams}" accesskey="X"><wp:i18n key="LINK_SET_GRAPHICS_MODE_HIGH_CONTRAST" /></a> [X]</dd>
				<dd><a href="#searchform" accesskey="S"><wp:i18n key="SKIP_TO_CONTENTS_SEARCH" /></a> [S]</dd>

				<dd><a href="#menu1" accesskey="1"><wp:i18n key="SKIP_TO_MENU" /></a> [1]</dd>
				<dd><a href="#mainarea" accesskey="2"><wp:i18n key="SKIP_TO_MAIN_CONTENT"/></a> [2]</dd>
