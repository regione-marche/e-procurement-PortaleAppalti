<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>

<!--
    Parametri necessari sull'include:
    containerID: L'id del div contenente la mappa;
    tipoClassifica: Intero che indica il tipo di classifica;
    title_label: Nome della label da utilizzare come titolo in caso di javascript disabilitato;
    empty_case_label: Titolo della label da utilizzare come corpo del div in caso non ci siano categorie da visualizzare;
    isRiepilogo: In caso fosse un riepilogo, il titolo non deve essere visualizzato a prescindere;

    Passati tramite variabile struts:
    categories: E' un List<CatalogResultBean>
-->

<s:set var="lastTipoAppalto" value="%{#categories[0].category.tipoAppalto}"/>
<s:set var="lastTitolo" value="%{#categories[0].category.titolo}"/>
<s:set var="lastLivello" value="%{#categories[0].category.livello}"/>

<div id="${param.containerID}" data-name="categories_view">
    <c:if test='${! param.isRiepilogo}'>
        <h3 data-name="categories_title"><wp:i18n key='${param.title_label}'/></h3>
    </c:if>
    <s:if test="%{#categories==null || #categories.size() == 0}" >
        <p class="important" style="text-align: center"><wp:i18n key='${param.empty_case_label}'/></p>
    </s:if>
    <s:else>
        <s:iterator var="categoria" value="%{#categories}" status="status">

            <s:set var="nuovoAlbero" value="%{false}"/>
            <c:set var="level"><s:property value='%{#categoria.category.livello}' /></c:set>

            <%-- primo albero --%>
            <s:if test="%{#status.index==0}">
                <s:iterator value="maps['tipiAppaltoIscrAlbo']">
                    <s:if test="%{key == #lastTipoAppalto}">
                        <s:set var="labelTipoAppalto" value="%{value}"/>
                    </s:if>
                </s:iterator>
                <div class="tree-root">
                    <a class="collapse" href="#" id="title_<s:property value="%{#status.index}"/>_${param.containerID}" title="<wp:i18n key="LABEL_LISTA_CATEGORIE_COMPRIMI" />">
                        <span>
                            <s:property value="%{#labelTipoAppalto}"/>
                            <s:if test="%{#lastTitolo != null}"> - <s:property value="%{#lastTitolo}"/></s:if>
                        </span>
                    </a>
                </div>
                <ul class="filetree" id="tree_title_<s:property value="%{#status.index}"/>_${param.containerID}">
                <s:set var="nuovoAlbero" value="%{true}"/>
            </s:if><%-- fine primo albero --%>

            <%-- nuovo albero --%>
            <s:elseif test="%{(#lastTipoAppalto != #categoria.category.tipoAppalto) || (#lastTitolo != #categoria.category.titolo)}">
                <s:iterator value="maps['tipiAppaltoIscrAlbo']">
                    <s:if test="%{key == #categoria.category.tipoAppalto}">
                        <s:set var="labelTipoAppalto" value="%{value}"/>
                    </s:if>
                </s:iterator>
                <c:set var="currLast"><s:property value="%{#lastLivello}" /></c:set>
                <c:forEach begin="1" end="${currLast}">
                    </li></ul>
                </c:forEach>
                <div class="tree-root">
                    <a class="collapse" href="#" id="title_<s:property value="%{#status.index}"/>_${param.containerID}" title="<wp:i18n key="LABEL_LISTA_CATEGORIE_COMPRIMI" />">
                        <span>
                            <s:property value="%{#labelTipoAppalto}"/>
                            <s:if test="%{category.titolo != null}"> - <s:property value="%{category.titolo}"/></s:if>
                        </span>
                    </a>
                </div>
                <ul class="filetree" id="tree_title_<s:property value="%{#status.index}"/>_${param.containerID}">
                <s:set var="nuovoAlbero" value="%{true}"/>
            </s:elseif><%-- fine nuovo albero --%>

            <%--stampa del nodo/foglia (aprendo un nuovo livello o chiudendo i precedenti, se necessario), con eventuale checkbox di selezione --%>
            <s:if test="%{category.livello > #lastLivello}">
                <ul><li>
            </s:if>
            <s:elseif test="%{category.livello < #lastLivello}">
                <s:if test="%{!#nuovoAlbero}">
                    <c:set var="currLast"><s:property value="%{#lastLivello}" /></c:set>
                    <c:forEach begin="${level}" end="${currLast-1}">
                        </li></ul>
                    </c:forEach>
                    </li>
                </s:if>
                <li>
            </s:elseif>
            <s:else>
                <s:if test="%{!#nuovoAlbero}">
                    </li>
                </s:if>
                <li>
            </s:else>

            <%-- Category icon --%>
            <s:if test="%{#categoria.category.foglia}">
                <s:set var="iconClass" value='%{"file"}' />
            </s:if>
            <s:else>
                <s:set var="iconClass" value='%{"folder"}' />
            </s:else>

            <span class="<s:property value='%{#iconClass}' />">

                <s:property value="%{#categoria.codice}" /> - <s:property value="%{#categoria.category.descrizione}" />

                <%-- se si tratta di foglia, si inseriscono anche le classifiche --%>
                <s:if test="%{#categoria.category.foglia}">
                    <s:if test='%{(classFrom!=null && classFrom!="") || (classTo!=null&&classTo!="")}'>,</s:if>

                    <%-- si visualizza il dalla classifica --%>
                    <c:if test="${param.tipoClassifica == 1}">

                        <s:set var="discrClassifica" value=''></s:set>
                        <s:if test="%{#categoria.category.tipoAppalto == 1}">
                            <s:set var="discrClassifica" value='%{"classifLavoriIscrAlbo"}'></s:set>
                        </s:if>
                        <s:elseif test="%{#categoria.category.tipoAppalto == 2 && maps['classifForniture'].size() > 0}">
                            <s:set var="discrClassifica" value='%{"classifForniture"}'></s:set>
                        </s:elseif>
                        <s:elseif test="%{#categoria.category.tipoAppalto == 3 && maps['classifServizi'].size() > 0}">
                            <s:set var="discrClassifica" value='%{"classifServizi"}'></s:set>
                        </s:elseif>
                        <s:elseif test="%{#categoria.category.tipoAppalto == 4 && maps['classifLavoriSottoSoglia'].size() > 0}">
                            <s:set var="discrClassifica" value='%{"classifLavoriSottoSoglia"}'></s:set>
                        </s:elseif>
                        <s:elseif test="%{#categoria.category.tipoAppalto == 5 && maps['classifServiziProf'].size() > 0}">
                            <s:set var="discrClassifica" value='%{"classifServiziProf"}'></s:set>
                        </s:elseif>

                        <s:if test="%{#discrClassifica.length()>0}">
                            <s:iterator value="maps[#discrClassifica]" >
                                <s:if test="%{key == #categoria.classFrom}">
                                    <wp:i18n key="LABEL_DA_CLASSIFICA" /> <s:property value="%{value}" />
                                </s:if>
                            </s:iterator>
                        </s:if>
                    </c:if>

                    <%-- si visualizza [fino alla] classifica --%>
                    <s:set var="discrClassifica" value=''></s:set>
                    <s:if test="%{#categoria.category.tipoAppalto == 1}">
                        <s:set var="discrClassifica" value='%{"classifLavoriIscrAlbo"}'></s:set>
                    </s:if>
                    <s:elseif test="%{#categoria.category.tipoAppalto == 2 && maps['classifForniture'].size() > 0}">
                        <s:set var="discrClassifica" value='%{"classifForniture"}'></s:set>
                    </s:elseif>
                    <s:elseif test="%{#categoria.category.tipoAppalto == 3 && maps['classifServizi'].size() > 0}">
                        <s:set var="discrClassifica" value='%{"classifServizi"}'></s:set>
                    </s:elseif>
                    <s:elseif test="%{#categoria.category.tipoAppalto == 4 && maps['classifLavoriSottoSoglia'].size() > 0}">
                        <s:set var="discrClassifica" value='%{"classifLavoriSottoSoglia"}'></s:set>
                    </s:elseif>
                    <s:elseif test="%{#categoria.category.tipoAppalto == 5 && maps['classifServiziProf'].size() > 0}">
                        <s:set var="discrClassifica" value='%{"classifServiziProf"}'></s:set>
                    </s:elseif>

                    <c:if test="${param.tipoClassifica != 3}">
                        <s:if test="%{#discrClassifica.length()>0}">
                            <s:iterator value="maps[#discrClassifica]">
                                <s:if test="%{key == #categoria.classTo}">
                                    <c:choose>
                                        <c:when test="${param.tipoClassifica == 1}">
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
                    
                    <s:if test='%{nota!=null && nota!=""}'><div><wp:i18n key="LABEL_ULTERIORI_INFO" />: <div class="note"><s:property value="%{nota}" escape="false"/></div></div></s:if>

                </s:if>
            </span>

            <%-- chiusura dei sottolivelli dell'ultimo albero --%>
            <s:if test="%{#status.last}">
                <c:forEach begin="1" end="${level}"></li></ul></c:forEach>
            </s:if>

            <s:set var="lastTipoAppalto" value="%{#categoria.category.tipoAppalto}"/>
            <s:set var="lastTitolo" value="%{#categoria.category.titolo}"/>
            <s:set var="lastLivello" value="%{#categoria.category.livello}"/>
        </s:iterator>
    </s:else>
</div>