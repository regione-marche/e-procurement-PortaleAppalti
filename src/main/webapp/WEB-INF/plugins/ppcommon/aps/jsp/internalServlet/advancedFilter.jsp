<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="s"    uri="/struts-tags" %>
<%@ taglib prefix="c"    uri="http://java.sun.com/jsp/jstl/core" %>

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
    #advanced {
        display: none;
    }
    .margin-auto {
        margin: auto;
    }
</style>

<div id="filter-divider-color">
    <wp:i18n key="LABEL_RICERCA_AVANZATA" var="lblRicercaAvanzata" />
    <wp:i18n key='EXPAND_ALL' var="lblExpandAll" />
    <wp:i18n key='COLLAPSE_ALL' var="lblCollapseAll" />
    <div class="filter-divider">
        <c:choose>
            <c:when test="${skin == 'highcontrast' || skin == 'text'}">
                <p class="margin-auto">
                    [&nbsp;
                </p>
                <a href="javascript:void(0);" style="margin: auto;" id="openAndClose" >
                    <s:property value='%{#attr.lblExpandAll}'/>
                </a>
                <p class="margin-auto">&nbsp;]</p>
                <p id="labelAdvSearch" class="margin-auto">
                    &nbsp;<s:property value='%{#attr.lblRicercaAvanzata}'/>
                </p>
            </c:when>
            <c:otherwise>
                <img id="openAndClose" src="<wp:imgURL/>/right-very-big.png" alt="<s:property value='%{#attr.lblRicercaAvanzata}'/>" style="height:auto;"/>
                <p id="labelAdvSearch" class="margin-auto important">
                    <s:property value='%{#attr.lblRicercaAvanzata}'/>
                </p>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<script>

//Se solo i field con questi id sono valorizzati, non verrà aperto automaticamente la sezione al primo avvio
var escludedField = [
    'model.iDisplayLength'
]

//Testo dei tooltip, current=attualmente impostato, next=impostato al prossimo click
var currentTooltip = "<s:property value='%{#attr.lblExpandAll}'/>"
var nextTooltip = "<s:property value='%{#attr.lblCollapseAll}'/>"

$(window).on("load", function() {
    var found = false
    //Tooltip iniziale
    $(".filter-divider").attr("title", currentTooltip + " [ <s:property value='%{#attr.lblRicercaAvanzata}'/> ]")
    //Controllo se qualche campo è già stato valorizzato tra i filtri avanzati (dropbox)
    $("#advanced select").each(function() {
        if (hasValue($(this)) && escludedField.indexOf($(this).attr("id")) == -1) {
            found = true
            return false
        }
    })
    //Controllo se qualche campo è già stato valorizzato tra i filtri avanzati (textfield)
    if (!found) 
        $("#advanced input").each(function() {
            if (hasValue($(this)) && escludedField.indexOf($(this).attr("id")) == -1) {
                found = true
                return false
            }
        })
    if (found)
        changeAdvancedFiltersStates($(".filter-divider"))

    var fieldsetLabelColor = $(".fieldset-row .label label").css("color")
    $("#filter-divider-color").css("color", fieldsetLabelColor)
    $("#filter-divider-color").css("border-color", fieldsetLabelColor)
});

function hasValue(element) {
    return element.val() != undefined && element.val() != ''
} 
    
$(".filter-divider").click(function() {
    changeAdvancedFiltersStates($(this))
});
function changeAdvancedFiltersStates(advancedFilterLink) {
    if ($("#advanced").is(":visible"))    //La modalità testo cambia semplicemente label
        collapse(advancedFilterLink)
    else
        expand(advancedFilterLink)
    changeTooltip()
}

//Collapse e expand dipendono se in modalità testo o meno
<c:choose>
    <c:when test="${skin == 'highcontrast' || skin == 'text'}">
        function collapse(advancedFilterLink) {
            advancedFilterLink.find("#openAndClose").text("<s:property value='%{#attr.lblExpandAll}'/>")   //Rotazione standard dell'icona (verso destra)
            $("#advanced").hide(500)    //Nascondo le opzioni avanzate
        }
        function expand(advancedFilterLink) {
            advancedFilterLink.find("#openAndClose").text("<s:property value='%{#attr.lblCollapseAll}'/>")
            $("#advanced").show(500)    //Mostro le opzioni avanzate
        }
    </c:when>
    <c:otherwise>
        function collapse(advancedFilterLink) {
            advancedFilterLink.find("#openAndClose").animateRotate(90, 0, 500)   //Rotazione standard dell'icona (verso destra)
            $("#advanced").hide(500)    //Nascondo le opzioni avanzate
        }
        function expand(advancedFilterLink) {
            advancedFilterLink.find("#openAndClose").animateRotate(0, 90, 500)   //Ruota l'icona verso il basso
            $("#advanced").show(500)    //Mostro le opzioni avanzate
        }
    </c:otherwise>
</c:choose>

//Cambio il tooltip
function changeTooltip() {
    var temp = currentTooltip
    currentTooltip = nextTooltip
    nextTooltip = temp
    $(".filter-divider").attr("title", currentTooltip + " [ <s:property value='%{#attr.lblRicercaAvanzata}'/> ]")
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