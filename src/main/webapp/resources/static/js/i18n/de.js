// ParsleyConfig definition if not already set
window.ParsleyConfig = window.ParsleyConfig || {};
window.ParsleyConfig.i18n = window.ParsleyConfig.i18n || {};

// Define then the messages
window.ParsleyConfig.i18n.de = jQuery.extend(window.ParsleyConfig.i18n.de || {}, {
  defaultMessage: "Valore non valido.",
  type: {
    email:        "Insert a valid email address.",
    url:          "Insert a valid URL.",
    number:       "Insert a valid decimal number, using \".\" as decimal separator.",
    integer:      "Insert a valid integer number without decimals.",
    digits:       "Value must be a number.",
    alphanum:     "Value must be a text."
  },
  notblank:       "Value cannot be empty.",
  required:       "Value is required.",
  pattern:        "Format of the value is not correct.",
  min:            "Value must be greater than %s.",
  max:            "Value must be less than %s.",
  range:          "Value must be between %s and %s.",
  minlength:      "Value is too short. Minimum lenght is of %s chars.",
  maxlength:      "Value is too long. Maximum length is of %s chars.",
  length:         "Length of this value must be between %s and %s chars.",
  mincheck:       "Choose at least %s options.",
  maxcheck:       "Choose at most %s options.",
  check:          "Choose between %s and %s options.",
  equalto:        "This value must be indetical."
});

// If file is loaded after Parsley main file, auto-load locale
if ('undefined' !== typeof window.ParsleyValidator)
  window.ParsleyValidator.addCatalog('de', window.ParsleyConfig.i18n.en, true);
