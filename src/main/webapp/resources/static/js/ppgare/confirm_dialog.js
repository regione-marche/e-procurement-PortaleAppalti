
// Attualmente non utilizzato, ma, mantenuto in caso servisse in futuro
$(document).ready(function() {	
    if ($(".confirm-dialog").length == 0)
        $("body").append('<div class="confirm-dialog source-popup" style="display: none"></div>')
})

function nothing() { }

function showDialog(
    title = "Confirm",
    description = "Do you want to proceed?",
    on_confirm, 
    on_denied = nothing, 
    on_open = nothing
) {
    var larghezzaSchermo = $(window).width();
    if (larghezzaSchermo > 960)
        var larghezzaDialog = 960;
    else
        var larghezzaDialog = larghezzaSchermo;

    $(".confirm-dialog")
        .attr("title", title)
        .append("<p>" + description + "</p>")
        .dialog({
            dialogClass: "no-close",
            autoOpen: true,
            closeOnEscape: true,    //Chiude il dialog se si preme ESC
            width: larghezzaDialog - 100,
            /*height: altezzaDialog,*/
            show: { //Animazione quando si visualizza il dialog
                effect: "blind",
                duration: 500
            },
            hide: { //Animazione quando si chiude il dialog
                effect: "blind",
                duration: 500
            },
            modal: true,    //Blocca tutto c'ho che è sotto il dialog (a meno che qualcuno non abbia giocato con il z-index)
            resizable: false,
            focusCleanup: true,
            draggable: false,
            cache: false
            // , zIndex: 1000
            , open: on_open
            , buttons: {
                // Conferma
                SI: function() {
                    on_confirm()
                    closeDialog($(this))
                }
                //Chiudi
                , NO: function() {
                    on_denied()
                    closeDialog($(this))
                }
            }
        }).position({
            my: 'center'    //Posiziona il dialog al centro dello schermo
        })
}

function closeDialog(dialog) {
    dialog.dialog("close")
    dialog.empty()
}