package it.maggioli.eldasoft.plugins.ppcommon.aps;

import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * Premessa: Possono coesistere più cookie con lo stesso nome, per uno stesso dominio se aventi differente PATH.
 *
 * Sviluppo:
 * Come standard se non specifico il Path, i cookie vengono creati con il path dell'url (dal primo all'ultimo slash)
 * Per evitare questa gestione che provoca l'utilizzo di cookie diversi in base alla lingua selezionata,
 * si utilizza questa classe che inserisce il path nel costruttore (il path corrisponde al nome del contesto della webapp).
 */
public class PortalCookie extends Cookie {
    public PortalCookie(String name, String value, ServletRequest req) {
        super(name, value);
        setPath(((HttpServletRequest) req).getContextPath());
    }

}
