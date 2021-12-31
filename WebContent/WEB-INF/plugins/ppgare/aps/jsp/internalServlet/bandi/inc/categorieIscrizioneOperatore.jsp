<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>

	<s:set var="lastTipoAppalto" value="%{categorieBando[0].tipoAppalto}"/>
	<s:set var="lastTitolo" value="%{categorieBando[0].titolo}"/>
	<s:set var="lastLivello" value="%{categorieBando[0].livello}"/>

	<%-- recupera 'tipoclassifica' da 
		- parametri della query string (es: link in bandoIscrizione.jsp)
		- attributi della request (es: stepRiepilogoCategorie.jsp)		
	 --%>		
	<c:set var="tipoClassifica" value="${param.tipoclassifica}" />

<%-- DEBUG
JSTL param.tipoclassifica=<c:out value="${param.tipoclassifica}" /> <br/>
ONGL parameters.tipoclassifica=<s:property value="#parameters.tipoclassifica" /> <br/>
<br/>
return tipoClassifica=<c:out value="${tipoClassifica}" /> <br/>	
--%>

	<s:iterator var="categoria" value="categorieBando" status="status">
		
		<s:set var="nuovoAlbero" value="%{false}"/>

		<%-- primo albero --%>
		<s:if test="%{#status.index==0}">
			<s:iterator value="maps['tipiAppaltoIscrAlbo']">
				<s:if test="%{key == #lastTipoAppalto}">
					<s:set var="labelTipoAppalto" value="%{value}"/>
				</s:if>
			</s:iterator>	
			<div class="tree-root">
				<a class="collapse" href="#" id="title_<s:property value="%{#status.index}"/>" title="<wp:i18n key="LABEL_LISTA_CATEGORIE_COMPRIMI" />">
					<span>
						<s:property value="%{#labelTipoAppalto}"/>
						<s:if test="%{#lastTitolo != null}"> - <s:property value="%{#lastTitolo}"/></s:if>
					</span>
				</a>
			</div>
			<ul class="filetree" id="tree_title_<s:property value="%{#status.index}"/>">	
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
				<a class="collapse" href="#" id="title_<s:property value="%{#status.index}"/>" title="<wp:i18n key="LABEL_LISTA_CATEGORIE_COMPRIMI" />">
					<span>
						<s:property value="%{#labelTipoAppalto}"/>
						<s:if test="%{titolo != null}"> - <s:property value="%{titolo}"/></s:if>
					</span>
				</a>
			</div>
			<ul class="filetree" id="tree_title_<s:property value="%{#status.index}"/>">
			<s:set var="nuovoAlbero" value="%{true}"/>	
		</s:elseif><%-- fine nuovo albero --%>

		<%--stampa del nodo/foglia (aprendo un nuovo livello o chiudendo i precedenti, se necessario), con eventuale checkbox di selezione --%>
		<c:choose>
			<c:when test="${livello gt lastLivello}">
				<ul><li>
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
				<s:if test="%{!#nuovoAlbero}">
					</li>
				</s:if>
				<li>
			</c:otherwise>
		</c:choose>
		
		<span <s:if test="foglia">class="file"</s:if><s:else>class="folder"</s:else>>
			
			<s:property value="codice" /> - <s:property value="descrizione" />
			
			<%-- se si tratta di foglia, si inseriscono anche le classifiche --%>
			<s:if test="foglia">
				<c:if test="${(!empty classeDa[status.index]) || (!empty classeA[status.index])}">,</c:if>
				
				<%-- si visualizza il dalla classifica --%>
				<c:if test="${tipoClassifica == 1}">

					<s:set var="discrClassifica" value=''></s:set>
					<s:if test="%{tipoAppalto == 1}">
						<s:set var="discrClassifica" value='%{"classifLavoriIscrAlbo"}'></s:set>
					</s:if>
					<s:elseif test="%{tipoAppalto == 2 && maps['classifForniture'].size() > 0}">
						<s:set var="discrClassifica" value='%{"classifForniture"}'></s:set>
					</s:elseif>
					<s:elseif test="%{tipoAppalto == 3 && maps['classifServizi'].size() > 0}">
						<s:set var="discrClassifica" value='%{"classifServizi"}'></s:set>
					</s:elseif>
					<s:elseif test="%{tipoAppalto == 4 && maps['classifLavoriSottoSoglia'].size() > 0}">
						<s:set var="discrClassifica" value='%{"classifLavoriSottoSoglia"}'></s:set>
					</s:elseif>
					<s:elseif test="%{tipoAppalto == 5 && maps['classifServiziProf'].size() > 0}">
						<s:set var="discrClassifica" value='%{"classifServiziProf"}'></s:set>
					</s:elseif>

					<s:if test="%{#discrClassifica.length()>0}">
						<s:iterator value="maps[#discrClassifica]" >
							<s:if test="%{key == classeDa[#status.index]}">
								<wp:i18n key="LABEL_DA_CLASSIFICA" /> <s:property value="%{value}" />
							</s:if>
						</s:iterator>
					</s:if>								
				</c:if>							

				<%-- si visualizza [fino alla] classifica --%>
				<s:set var="discrClassifica" value=''></s:set>
				<s:if test="%{tipoAppalto == 1}">
					<s:set var="discrClassifica" value='%{"classifLavoriIscrAlbo"}'></s:set>
				</s:if>
				<s:elseif test="%{tipoAppalto == 2 && maps['classifForniture'].size() > 0}">
					<s:set var="discrClassifica" value='%{"classifForniture"}'></s:set>
				</s:elseif>
				<s:elseif test="%{tipoAppalto == 3 && maps['classifServizi'].size() > 0}">
					<s:set var="discrClassifica" value='%{"classifServizi"}'></s:set>
				</s:elseif>
				<s:elseif test="%{tipoAppalto == 4 && maps['classifLavoriSottoSoglia'].size() > 0}">
					<s:set var="discrClassifica" value='%{"classifLavoriSottoSoglia"}'></s:set>
				</s:elseif>
				<s:elseif test="%{tipoAppalto == 5 && maps['classifServiziProf'].size() > 0}">
					<s:set var="discrClassifica" value='%{"classifServiziProf"}'></s:set>
				</s:elseif>

				<c:if test="${tipoClassifica != 3}">
					<s:if test="%{#discrClassifica.length()>0}">
						<s:iterator value="maps[#discrClassifica]">
							<s:if test="%{key == classeA[#status.index]}">
								<c:choose>
									<c:when test="${tipoClassifica == 1}">
										<wp:i18n key="LABEL_A_CLASSIFICA" />
									</c:when>    
									<c:otherwise>
									    <wp:i18n key="LABEL_CLASSIFICA" />
									</c:otherwise>
								</c:choose> <s:property value="%{value}" />
							</s:if>
						</s:iterator>
					</s:if>
				</c:if>
				
				<c:if test="${!empty nota[status.index]}"><div><wp:i18n key="LABEL_ULTERIORI_INFO" />: <div class="note"><s:property value="%{nota[#status.index]}" escape="false"/></div></div></c:if>
				
			</s:if>				
		</span>

		<%-- chiusura dei sottolivelli dell'ultimo albero --%>
		<s:if test="%{#status.last}">
			<c:forEach begin="1" end="${livello}"></li></ul></c:forEach>
		</s:if>

		<s:set var="lastTipoAppalto" value="%{#categoria.tipoAppalto}"/>
		<s:set var="lastTitolo" value="%{#categoria.titolo}"/>
		<s:set var="lastLivello" value="%{#categoria.livello}"/>
	</s:iterator>
