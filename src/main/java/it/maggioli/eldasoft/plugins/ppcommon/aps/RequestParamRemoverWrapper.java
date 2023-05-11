package it.maggioli.eldasoft.plugins.ppcommon.aps;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * Rimuove i parametri specificati.
 *
 * Aggiungere il nome del parametro da escludere alla lista: notValidParams.
 */
public class RequestParamRemoverWrapper extends HttpServletRequestWrapper {

    private List<String> notValidParams = new ArrayList<>(10);

    /**
     * Constructs a request object wrapping the given request.
     *
     * @param request
     * @throws IllegalArgumentException if the request is null
     */
    public RequestParamRemoverWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public String getHeader(String name) {
        return notValidParams.contains(name) ? "" : super.getHeader(name);
    }

    @Override
    public Enumeration getHeaders(String name) {
        List<String> newVals = new ArrayList<>();
        Enumeration values = super.getHeaders(name);
        while (values.hasMoreElements()) {
            String value = (String) values.nextElement();
            newVals.add(
                    notValidParams.contains(name)
                    ? ""
                    : value
            );
        }

        return Collections.enumeration(newVals);
    }

    @Override
    public String[] getParameterValues(String parameter) {
        return notValidParams.contains(parameter)
               ? new String[0]
               : super.getParameterValues(parameter);
    }

    @Override
    public String getParameter(String parameter) {
        return notValidParams.contains(parameter) ? "" : super.getParameter(parameter);
    }

    public void addNotValidParam(String paramName) {
        notValidParams.add(paramName);
    }

}
