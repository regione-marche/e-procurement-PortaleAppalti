package it.maggioli.eldasoft.plugins.ppcommon.aps;

import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Premessa: Possono coesistere più cookie con lo stesso nome, per uno stesso dominio se aventi differente PATH.
 * <p>
 * Sviluppo:
 * Come standard se non specifico il Path, i cookie vengono creati con il path dell'url (dal primo all'ultimo slash)
 * Per evitare questa gestione che provoca l'utilizzo di cookie diversi in base alla lingua selezionata,
 * si utilizza questa classe che inserisce il path nel costruttore (il path corrisponde al nome del contesto della webapp).
 */
public class PortalCookie extends Cookie {
    /**
     * The COOKIE_DATE_FORMAT variable represents the standard date format used for cookies according to RFC 6265.
     *
     * The format of the date should be "EEE, dd-MMM-yyyy HH:mm:ss z" with the default locale set to US.
     *
     * This variable is an instance of the SimpleDateFormat class and is declared as private and final to ensure immutability and thread safety.
     */
    private static final SimpleDateFormat COOKIE_DATE_FORMAT = new SimpleDateFormat("EEE, dd-MMM-yyyy HH:mm:ss z", Locale.US);
    static {
        COOKIE_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    private boolean isHttpOnly = true;

    public void setHttpOnly(boolean isHttpOnly) {
        this.isHttpOnly = isHttpOnly;
    }

    public PortalCookie(String name, String value, ServletRequest req) {
        super(name, value);
        setPath(((HttpServletRequest) req).getContextPath());
    }

    public boolean isHttpOnly() {
        return isHttpOnly;
    }

    /**
     * Converts the Cookie object to its corresponding header representation.
     *
     * @return the header string representation of the Cookie object
     */
    public String toHeader() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.getName()).append('=').append(this.getValue());

        // Path
        if (getPath() != null)
            builder.append(";Path=").append(getPath());

        // Domain
        if (getDomain() != null)
            builder.append(";Domain=").append(getDomain());

        // Expiration
        if (getMaxAge() > 0)
            builder.append(";Expires=").append(COOKIE_DATE_FORMAT.format(new Date(System.currentTimeMillis() + getMaxAge() * 1000L)));

        // Secure
//        if (isSecure())
//            builder.append(";Secure");
        // Same site
//        builder.append(";SameSite=").append(cookie.getSameSyteType().getStringValue());

        // HttpOnly
        if (isHttpOnly())
            builder.append(";HttpOnly");

        return builder.toString();
    }

}
