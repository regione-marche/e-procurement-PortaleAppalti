// ParsleyConfig definition if not already set
window.ParsleyConfig = window.ParsleyConfig || {};
window.ParsleyConfig.i18n = window.ParsleyConfig.i18n || {};

// Define then the messages
window.ParsleyConfig.i18n.it = jQuery.extend(window.ParsleyConfig.i18n.it || {}, {
  defaultMessage: "Valore non valido.",
  type: {
    email:        "Indicare un indirizzo email valido.",
    url:          "Indicare un URL valido.",
    number:       "Indicare un numero decimale valido, utilizzando il \".\" come separatore.",
    integer:      "Indicare un numero intero senza decimali.",
    digits:       "Il valore deve essere di tipo numerico.",
    alphanum:     "Il valore deve essere di tipo alfanumerico."
  },
  notblank:       "Il valore non deve essere vuoto.",
  required:       "Valore richiesto.",
  pattern:        "Il formato del valore non \u00e8 corretto.",
  min:            "Il valore deve essere maggiore di %s.",
  max:            "Il valore deve essere minore di %s.",
  range:          "Il valore deve essere compreso tra %s e %s.",
  minlength:      "Valore troppo corto. La lunghezza minima \u00e8 di %s caratteri.",
  maxlength:      "Valore troppo lungo. La lunghezza massima \u00e8 di %s caratteri.",
  length:         "La lunghezza di questo valore deve essere compresa fra %s e %s caratteri.",
  mincheck:       "Devi scegliere almeno %s opzioni.",
  maxcheck:       "Devi scegliere al pi\u00f9 %s opzioni.",
  check:          "Devi scegliere tra %s e %s opzioni.",
  equalto:        "Questo valore deve essere identico."
});

// If file is loaded after Parsley main file, auto-load locale
if ('undefined' !== typeof window.ParsleyValidator)
  window.ParsleyValidator.addCatalog('it', window.ParsleyConfig.i18n.it, true);
