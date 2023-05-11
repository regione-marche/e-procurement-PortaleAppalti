<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>

<div class="categorie-list">

<s:set var="idOperatore" value="ditta"/>
<%-- 
param.tipoClassifica: ${param.tipoClassifica}<br/>
ditta: <s:property value="ditta" /><br/>
categorieOperatoriIscritti[ditta]: <s:property value="categorieOperatoriIscritti[ditta]" /><br/>
--%>

<s:if test="%{categorieOperatoriIscritti[ditta].size() > 0}">

	<c:set var="tipoClassifica" value="${param.tipoClassifica}"/>
	<s:set var="lastTipoAppalto" value="%{categorieOperatoriIscritti[ditta][0].tipoAppalto}"/>
  	<s:set var="lastTitolo" value="%{titoliCategorie[categorieOperatoriIscritti[ditta][0].codice]}"/>
  	<s:set var="lastLivello" value="%{categorieOperatoriIscritti[ditta][0].livello}"/>
	
	<s:iterator var="categoria" value="categorieOperatoriIscritti[ditta]" status="status">
		
 		<s:set var="nuovoAlbero" value="%{false}"/>

		<s:set var="titolo" value="%{titoliCategorie[#categoria.codice]}"/>		
			
<%--			
titolo: <s:property value="#titolo"/> <br/>
tipoAppalto: <s:property value="#categoria.tipoAppalto"/> <br/> 		
codice: <s:property value="#categoria.codice"/> <br/>
descrizione: <s:property value="#categoria.descrizione"/> <br/>
livello: <s:property value="#categoria.livello"/> <br/>
foglia: <s:property value="#categoria.foglia"/> <br/>
classificaMinima: <s:property value="#categoria.classificaMinima"/> <br/>
classificaMassima: <s:property value="#categoria.classificaMassima"/> <br/>
lastTipoAppalto: <s:property value="#lastTipoAppalto" /><br/>
lastTitolo: <s:property value="#lastTitolo" /><br/>
lastLivello: <s:property value="#lastLivello" /><br/>
$lastLivello: ${lastLivello}<br/>
$livello: ${livello}<br/>
--%>
			
		<s:if test="%{#status.index==0}">
			<%-- primo albero --%>
			<s:iterator value="maps['tipiAppaltoIscrAlbo']">
				<s:if test="%{key == #lastTipoAppalto}">
					<s:set var="labelTipoAppalto" value="%{value}"/>
				</s:if>
			</s:iterator>							
			<div class="tree-root">
				<a class="expand" href="javascript:" id='title_<s:property value="%{#idOperatore}" />_<s:property value="%{#status.index}"/>' title="<wp:i18n key="LABEL_LISTA_CATEGORIE_ESPANDI" />">
					<span>
						<s:property value="%{#labelTipoAppalto}"/>
						<s:if test="%{#lastTitolo != null}"> - <s:property value="%{#lastTitolo}"/></s:if>
					</span>
				</a>
			</div>
			<ul class="filetree" id='tree_title_<s:property value="%{#idOperatore}" />_<s:property value="%{#status.index}"/>' style='display: none;'>	
			<s:set var="nuovoAlbero" value="%{true}"/>
		</s:if>
		<s:elseif test="%{(#lastTipoAppalto != #categoria.tipoAppalto) || (#lastTitolo != #titolo)}">
			<%-- nuovo albero --%>
			<s:iterator value="maps['tipiAppaltoIscrAlbo']">
				<s:if test="%{key == #categoria.tipoAppalto}">
					<s:set var="labelTipoAppalto" value="%{value}"/>
				</s:if>
			</s:iterator>				
			<c:forEach begin="1" end="${lastLivello}">
				</li></ul>
			</c:forEach>
			<div class="tree-root">
				<a class="expand" href="javascript:" id='title_<s:property value="%{#idOperatore}" />_<s:property value="%{#status.index}"/>' title="<wp:i18n key="LABEL_LISTA_CATEGORIE_ESPANDI" />">
					<span>
						<s:property value="%{#labelTipoAppalto}"/> <s:if test="%{#titolo != null}"> - <s:property value="#titolo"/></s:if>
					</span>
				</a>
			</div>
			<ul class="filetree" id='tree_title_<s:property value="%{#idOperatore}" />_<s:property value="%{#status.index}"/>' style='display: none;'>
			<s:set var="nuovoAlbero" value="%{true}"/>
		</s:elseif>
		<%-- fine nuovo albero --%>
			
			
		<%-- stampa del nodo/foglia 
		     (aprendo un nuovo livello o chiudendo i precedenti, se necessario) --%>
		<s:set var="classifiche" value=""/>
		<s:if test="%{(#categoria.classificaMinima != null) || (#categoria.classificaMassima != null)}">
			<s:set var="classifiche">,</s:set>
			
			<s:set var="discrClassifica" value=""/>
			<s:if test="%{#categoria.tipoAppalto == 1}">
				<s:set var="discrClassifica" value='%{"classifLavoriIscrAlbo"}'></s:set>
			</s:if>
			<s:elseif test="%{#categoria.tipoAppalto == 2}">
				<s:set var="discrClassifica" value='%{"classifForniture"}'></s:set>
			</s:elseif>
			<s:elseif test="%{#categoria.tipoAppalto == 3}">
				<s:set var="discrClassifica" value='%{"classifServizi"}'></s:set>
			</s:elseif>
			<s:elseif test="%{#categoria.tipoAppalto == 4}">
				<s:set var="discrClassifica" value='%{"classifLavoriSottoSoglia"}'></s:set>
			</s:elseif>
			<s:elseif test="%{#categoria.tipoAppalto == 5}">
				<s:set var="discrClassifica" value='%{"classifServiziProf"}'></s:set>
			</s:elseif>
				
			<s:if test="%{#discrClassifica.length() > 0}">									 						
				<c:if test="${tipoClassifica == 1}">						
					<s:if test="%{#categoria.classificaMinima != null}">
						<s:iterator value="maps[#discrClassifica]">
							<s:if test="%{key == #categoria.classificaMinima}">
								<s:set var="classifiche"><s:property value="%{#classifiche}"/>
									<wp:i18n key="LABEL_DA_CLASSIFICA" /> <s:property value="%{value}" />
								</s:set>								
							</s:if>
						</s:iterator>
					</s:if>
				</c:if>
					
				<c:if test="${tipoClassifica != 3}">					
					<s:if test="%{#categoria.classificaMassima != null}">				
						<s:iterator value="maps[#discrClassifica]">					
							<s:if test="%{key == #categoria.classificaMassima}">
								<s:set var="classifiche"><s:property value="%{#classifiche}"/>
									<c:choose>
										<c:when test="${tipoClassifica == 1}">
											<wp:i18n key="LABEL_A_CLASSIFICA" />
										</c:when>    
										<c:otherwise>
										    <wp:i18n key="LABEL_CLASSIFICA" />
										</c:otherwise>
									</c:choose> <s:property value="%{value}" />
								</s:set>
							</s:if>
						</s:iterator>
					</s:if>
				</c:if>
			</s:if>
		</s:if>			
  		
		<c:choose>
			<c:when test="${livello gt lastLivello}">
				<ul>
					<li>
			</c:when>
			<c:when test="${livello lt lastLivello}">
				<s:if test="%{!#nuovoAlbero}">
					<c:forEach begin="${livello}" end="${lastLivello-1}">
						</li></ul>
					</c:forEach>
					</li>
				</s:if>
				<li>
			</c:when>
			<c:otherwise>
				<s:if test="%{!#nuovoAlbero}"></li></s:if>
				<li>
			</c:otherwise>
		</c:choose>
		
		<span class='<s:if test="%{#categoria.foglia}">file </s:if><s:else>folder </s:else>'>
			<s:property value="#categoria.codice" /> - <s:property value="#categoria.descrizione" />
			<s:if test="%{#categoria.foglia}"><s:property value="%{#classifiche}"/></s:if>
		</span>
		<%-- fine nodo/foglia --%>
		
		<%-- chiusura dei sottolivelli dell'ultimo albero --%>		
		<s:if test="%{#status.last}">		
			<c:forEach begin="1" end="${livello}">
				</li></ul>
			</c:forEach>
		</s:if>

		<s:set var="lastTipoAppalto" value="%{#categoria.tipoAppalto}"/>
		<s:set var="lastTitolo" value="%{#titolo}"/>
		<s:set var="lastLivello" value="%{#categoria.livello}"/>
	</s:iterator>
</s:if>
<s:else>
	<wp:i18n key="LABEL_NESSUNA_CATEGORIA_PRESTAZIONE" />
</s:else>
	
</div>


 