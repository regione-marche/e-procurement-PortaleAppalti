<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="s"    uri="/struts-tags" %>
<%@ taglib prefix="c"    uri="http://java.sun.com/jsp/jstl/core" %>

<!--
    In caso di piu filtri avanzati su stessa pagina, ma, showlet diversa si ha problemi.
    Per ovviare al problema, ho creato una variabile con scope la richiesta, che quindi
    è condivisa da tutte le showlet e che incrementa ad ogni utilizza. Questa variabile
    serve per creare un'identificativo progressivo.

    La variabile struts: "ADVANCED_DIV_ID"; Contiene l'id da assegnare al div che contiene
    i filtri. (non e' una variabile con scope request).
    Oltre ad avere l'id generato, il div con il filtro, deve avere assegnata la classe
    "advanced".

    Gli id sono stati quasi completamente rimossi per non avere id duplicati.

    L'attributo: "withAdvanced"; E' un'attributo custom che contiene l'id del div che
    deve mostrare/nascondere (ovvero l'id generata da questa stessa pagina e poi condiviso
    tramite variabile struts con il padre)
-->
<!-- Caso: E' la prima volta che viene richiamata la jsp (nella richiesta attuale) -->
<s:if test="%{#request.numberOfAdvDiv == null}">
    <!-- Scope request per avere il progressivo anche sulle varie showlet -->
    <s:set var="numberOfAdvDiv" value="1" scope="request" />
</s:if>
<s:else>
    <!-- Aumento il progressivo -->
    <s:set var="numberOfAdvDiv" value="%{#request.numberOfAdvDiv + 1}" scope="request" />
</s:else>

<!-- Id da assegnare al div contenente i filtri -->
<s:set var="ADVANCED_DIV_ID" value='%{"advanced_" + #request.numberOfAdvDiv}' />

<!-- Caso: Primo volta che la jsp viene richiamata -->
<s:if test="%{#request.numberOfAdvDiv == 1}">
    <style>
        .filter-divider {
            display: flex;
            flex-direction: row;
            text-align: center;
            margin: auto;
            cursor: pointer;
        }
        .filter-divider:after {
            content: "";
            border-top: 1px solid currentColor;
            margin: auto;
            flex: 1 1;
            width: 100%;
            margin-left: 1em;
        }
        .advanced {
            display: none;
        }
        .margin-auto {
            margin: auto;
        }
    </style>
</s:if>

<div class="filter-divider-color">
    <wp:i18n key="LABEL_RICERCA_AVANZATA" var="lblRicercaAvanzata" />
    <wp:i18n key='EXPAND_ALL' var="lblExpandAll" />
    <wp:i18n key='COLLAPSE_ALL' var="lblCollapseAll" />
    <div class="filter-divider" withAdvanced="<s:property value='%{#ADVANCED_DIV_ID}' />">
        <c:choose>
            <c:when test="${skin == 'highcontrast' || skin == 'text'}">
                <p class="margin-auto">
                    [&nbsp;
                </p>
                <a href="javascript:void(0);" style="margin: auto;" class="openAndClose" >
                    <s:property value='%{#attr.lblExpandAll}'/>
                </a>
                <p class="margin-auto">&nbsp;]</p>
                <p id="labelAdvSearch_<s:property value='%{#request.numberOfAdvDiv}' />" class="margin-auto">
                    &nbsp;<s:property value='%{#attr.lblRicercaAvanzata}'/>
                </p>
            </c:when>
            <c:otherwise>
                <img class="openAndClose" src="<wp:imgURL/>/right-very-big.png" alt="<s:property value='%{#attr.lblRicercaAvanzata}'/>" style="height:auto;"/>
                <p id="labelAdvSearch_<s:property value='%{#request.numberOfAdvDiv}' />" class="margin-auto important">
                    <s:property value='%{#attr.lblRicercaAvanzata}'/>
                </p>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<s:if test="%{#request.numberOfAdvDiv == 1}">

    <script>

    //Se solo i field con questi id sono valorizzati, non verra' aperto automaticamente la sezione al primo avvio
    var escludedField = [
        'model.iDisplayLength'
    ]

    //Testo dei tooltip, current=attualmente impostato, next=impostato al prossimo click
    var expand_tooltip = "<s:property value='%{#attr.lblExpandAll}'/> [ <s:property value='%{#attr.lblRicercaAvanzata}'/> ]"
    var collapse_tooltip = "<s:property value='%{#attr.lblCollapseAll}'/> [ <s:property value='%{#attr.lblRicercaAvanzata}'/> ]"

    $(window).on("load", function() {
        // Tooltip iniziale
        $(".filter-divider").attr("title", expand_tooltip)

        // Inizio a iterare tutti i contenitori di filtri ovvero tutti i div con classe advanced
        $(".advanced").each(function() {
            var found = false
            // Controllo se qualche campo e' gia' stato valorizzato tra i filtri avanzati (dropbox)
            $(this).find("select").each(function() {
                if (hasValue($(this)) && escludedField.indexOf($(this).attr("id")) == -1) {
                    found = true
                    return false
                }
            })
            if (!found) {
                // Controllo se qualche campo e' gia' stato valorizzato tra i filtri avanzati (textfield)
                $(this).find("input").each(function () {
                    if (hasValue($(this)) && escludedField.indexOf($(this).attr("id")) == -1) {
                        found = true
                        return false
                    }
                })
            }
            // Se trovo un filtro (found = true), mi recupero il divisore con attributo = all'id del div contenente i filtri
            if (found)
                changeAdvancedFiltersStates($(".filter-divider[withAdvanced=" + $(this).attr("id") + "]"))
        })

        var fieldsetLabelColor = $(".fieldset-row .label label").css("color")
        $(".filter-divider-color").css("color", fieldsetLabelColor)
        $(".filter-divider-color").css("border-color", fieldsetLabelColor)

        $(".filter-divider").click(function() {
            changeAdvancedFiltersStates($(this))
        });
    });

    function hasValue(element) {
        return element.val() != undefined && element.val() != ''
    }

    function changeAdvancedFiltersStates(advancedFilterLink) {
        // Recupero l'id del div contenente i filtri dall'attributo del divisore.
        var divIdToHideAndShow = advancedFilterLink.attr("withAdvanced")
        if ($("#" + divIdToHideAndShow).is(":visible"))    //La modalità testo cambia semplicemente label
            collapse(advancedFilterLink, divIdToHideAndShow)
        else
            expand(advancedFilterLink, divIdToHideAndShow)
        changeTooltip(advancedFilterLink)
    }

    // Collapse e expand dipendono se in modalita' testo o meno
    <c:choose>
        <c:when test="${skin == 'highcontrast' || skin == 'text'}">
            function collapse(advancedFilterLink, divIdToHideAndShow) {
                advancedFilterLink.find(".openAndClose").text("<s:property value='%{#attr.lblExpandAll}'/>")   //Rotazione standard dell'icona (verso destra)
                $("#" + divIdToHideAndShow).hide(500)    //Nascondo le opzioni avanzate
            }
            function expand(advancedFilterLink, divIdToHideAndShow) {
                advancedFilterLink.find(".openAndClose").text("<s:property value='%{#attr.lblCollapseAll}'/>")
                $("#" + divIdToHideAndShow).show(500)    //Mostro le opzioni avanzate
            }
        </c:when>
        <c:otherwise>
            function collapse(advancedFilterLink, divIdToHideAndShow) {
                advancedFilterLink.find(".openAndClose").animateRotate(90, 0, 500)   //Rotazione standard dell'icona (verso destra)
                $("#" + divIdToHideAndShow).hide(500)    //Nascondo le opzioni avanzate
            }
            function expand(advancedFilterLink, divIdToHideAndShow) {
                advancedFilterLink.find(".openAndClose").animateRotate(0, 90, 500)   //Ruota l'icona verso il basso
                $("#" + divIdToHideAndShow).show(500)    //Mostro le opzioni avanzate
            }
        </c:otherwise>
    </c:choose>

    //Cambio il tooltip
    function changeTooltip(advancedFilterLink) {
        if (advancedFilterLink.attr("title") === expand_tooltip)
            advancedFilterLink.attr("title", collapse_tooltip)
        else
            advancedFilterLink.attr("title", expand_tooltip)
    }

    //Anima la rotazione dell'icona
    $.fn.animateRotate = function(startAngle, destAngle, duration, easing, complete) {
        var args = $.speed(duration, easing, complete);
        var step = args.step;
        return this.each(function(i, e) {
            args.complete = $.proxy(args.complete, e);
            args.step = function(now) {
                $.style(e, 'transform', 'rotate(' + now + 'deg)');
                if (step) return step.apply(e, arguments);
            };
            $({deg: startAngle}).animate({deg: destAngle}, args);
        });
    };

    </script>
</s:if>