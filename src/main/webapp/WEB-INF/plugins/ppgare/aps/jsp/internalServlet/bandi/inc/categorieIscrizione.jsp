<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>

<input type="hidden" id="mercatoElettronico" value='${param.mercatoElettronico}'/>

<s:if test="%{categorie.length > 0}">

	<s:set var="lastTipoAppalto" value="%{categorie[0].tipoAppalto}"/>
  	<s:set var="lastTitolo" value="%{categorie[0].titolo}"/>
  	<s:set var="lastLivello" value="%{categorie[0].livello}"/>
	
	<s:iterator var="categoria" value="categorie" status="status">
		
		<s:set var="nuovoAlbero" value="%{false}"/>
		
		<%-- primo albero --%>
		<s:if test="%{#status.index==0}">
			<s:iterator value="maps['tipiAppaltoIscrAlbo']">
				<s:if test="%{key == #lastTipoAppalto}">
					<s:set var="labelTipoAppalto" value="%{value}"/>
				</s:if>
			</s:iterator>
			<div class="tree-root">
				<a class="collapse" href="#" id='title_<s:property value="%{#status.index}"/>' 
				   title="<wp:i18n key="LABEL_LISTA_CATEGORIE_COMPRIMI" />">
					<span>
						<s:property value="%{#labelTipoAppalto}"/>
						<s:if test="%{#lastTitolo != null}"> - <s:property value="%{#lastTitolo}"/></s:if>
					</span>
				</a>
			</div>
			<ul class="filetree" id='tree_title_<s:property value="%{#status.index}"/>'>	
			<s:set var="nuovoAlbero" value="%{true}"/>
		</s:if><%-- fine primo albero --%>

		<%-- nuovo albero --%>
		<s:elseif test="%{(#lastTipoAppalto != #categoria.tipoAppalto) || (#lastTitolo != #categoria.titolo)}">
			<s:iterator value="maps['tipiAppaltoIscrAlbo']">
				<s:if test="%{key == tipoAppalto}">
					<s:set var="labelTipoAppalto" value="%{value}"/>
				</s:if>
			</s:iterator>	
			<c:forEach begin="1" end="${lastLivello}">
				</li></ul>
			</c:forEach>
			<div class="tree-root">
				<a class="collapse" href="#" id='title_<s:property value="%{#status.index}"/>' title="<wp:i18n key="LABEL_LISTA_CATEGORIE_COMPRIMI" />">
					<span>
						<s:property value="%{#labelTipoAppalto}"/> <s:if test="%{titolo != null}">- <s:property value="%{titolo}"/></s:if>
					</span>
				</a>
			</div>
			<ul class="filetree" id='tree_title_<s:property value="%{#status.index}"/>'>
			<s:set var="nuovoAlbero" value="%{true}"/>
		</s:elseif><%-- fine nuovo albero --%>
			
		<%--stampa del nodo/foglia (aprendo un nuovo livello o chiudendo i precedenti, se necessario) --%>
		<c:choose>
			<c:when test="${livello gt lastLivello}">
				<ul>
					<li>
						<span class='<s:if test="foglia">file </s:if><s:else>folder </s:else>'>
							<c:if test="${param.mercatoElettronico}">
								<a href='<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/searchArticoli.action" />&amp;codiceCatalogo=${param.codice}&amp;ext=${param.ext}&amp;model.codiceCategoria=<s:property value="codice" />&amp;${tokenHrefParams}' 
								   title="<wp:i18n key="LABEL_VISUALIZZA_LISTA_ARTICOLI_CATEGORIA" />">
							</c:if>
							<s:property value="codice" /> - <s:property value="descrizione" />
							<c:if test="${param.mercatoElettronico}">
								</a>
							</c:if>
						</span>
			</c:when>
			<c:when test="${livello lt lastLivello}">
				<s:if test="%{!#nuovoAlbero}">
					<c:forEach begin="${livello}" end="${lastLivello-1}">
						</li></ul>
					</c:forEach>
					</li>
				</s:if>
				<li>
					<span class='<s:if test="foglia">file </s:if><s:else>folder </s:else>'>
						<c:if test="${param.mercatoElettronico}">
							<a href='<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/searchArticoli.action" />&amp;codiceCatalogo=${param.codice}&amp;ext=${param.ext}&amp;model.codiceCategoria=<s:property value="codice" />${tokenHrefParams}' 
							   title="<wp:i18n key="LABEL_VISUALIZZA_LISTA_ARTICOLI_CATEGORIA" />">
						</c:if>
						<s:property value="codice" /> - <s:property value="descrizione" />
						<c:if test="${param.mercatoElettronico}">
							</a>
						</c:if>
					</span>
			</c:when>
			<c:otherwise>
				<s:if test="%{!#nuovoAlbero}"></li></s:if>
				<li>
					<span class='<s:if test="foglia">file </s:if><s:else>folder </s:else>'>
						<c:if test="${param.mercatoElettronico}">
							<a href='<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/searchArticoli.action" />&amp;codiceCatalogo=${param.codice}&amp;ext=${param.ext}&amp;model.codiceCategoria=<s:property value="codice" />&amp;${tokenHrefParams}' 
							   title="<wp:i18n key="LABEL_VISUALIZZA_LISTA_ARTICOLI_CATEGORIA" />">
						</c:if>
							<s:property value="codice" /> - <s:property value="descrizione" />
						<c:if test="${param.mercatoElettronico}">
							</a>
						</c:if>
					</span>
			</c:otherwise>
		</c:choose><%-- fine nodo/foglia --%>
		
		<%-- chiusura dei sottolivelli dell'ultimo albero --%>
		<s:if test="%{#status.last}">
			<c:forEach begin="1" end="${livello}">
				</li></ul>
			</c:forEach>
		</s:if>
	
		<s:set var="lastTipoAppalto" value="%{#categoria.tipoAppalto}"/>
		<s:set var="lastTitolo" value="%{#categoria.titolo}"/>
		<s:set var="lastLivello" value="%{#categoria.livello}"/>
	</s:iterator>
</s:if>
<s:else>
	<wp:i18n key="LABEL_NESSUNA_CATEGORIA_PRESTAZIONE" />
</s:else>