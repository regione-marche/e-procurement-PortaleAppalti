/*
 * Created on 04/apr/2019
 *
 * Copyright (c) Maggioli S.p.A. - Divisione Informatica
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.maggioli.eldasoft.plugins.ppcommon.aps;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Imposta a stringa vuota gli header con valore non valido.
 *
 * NB: Vengono controllati solo un numero limitato di header.
 */
public class XSSRequestWrapper extends HttpServletRequestWrapper {

    private static final Logger log = LoggerFactory.getLogger(XSSRequestWrapper.class);
    private static final Pattern VALID_PARAMETER_NAME = Pattern.compile("(?i)[a-z_\\d-.:?;=]+");

    /**
     * Reflected CrossSite Scripting filter
     */
    public XSSRequestWrapper(HttpServletRequest servletRequest) {
      super(servletRequest);
    }

    @Override
    public Enumeration getParameterNames() {
        List<String> list = new ArrayList<>();
        Enumeration e = super.getParameterNames();
        while (e.hasMoreElements()) {
            String current = (String) e.nextElement();
            if (VALID_PARAMETER_NAME.matcher(current).matches()) {
                list.add(current);
            }else {
                log.warn("Rimosso Parameter: {} contenente caratteri non ammessi", current.trim());
            }
        }
        return Collections.enumeration(list);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> myObject = new HashMap<>();
        super.getParameterMap().forEach((key, value) -> {
            String keyString = (String) key;
            if (VALID_PARAMETER_NAME.matcher(keyString).matches()) {
                myObject.put(keyString, (String[]) value);
            }else {
                log.warn("Rimosso Parameter: {} contenente caratteri non ammessi", keyString.trim());
            }
        });
        return myObject;
    }

    @Override
    public String getHeader(String name) {
      String value = super.getHeader(name);
      return XSSValidation.hasToBeChecked(name, value) && XSSValidation.isNotValid(name, value)
             ? logAndEmpty(name, value)
             : value;
    }

    @Override
    public Enumeration getHeaders(String name) {
      List<String> newVals = new ArrayList<>();
      Enumeration values = super.getHeaders(name);
      while (values.hasMoreElements()) {
          String v = (String) values.nextElement();
          newVals.add(
                    XSSValidation.hasToBeChecked(name, v) && XSSValidation.isNotValid(name, v)
                      ? logAndEmpty(name, v)
                      : v
          );
      }

      return Collections.enumeration(newVals);
    }
  
    @Override
    public String[] getParameterValues(String parameter) {
      String[] values = super.getParameterValues(parameter);
      return XSSValidation.hasToBeChecked(parameter, values) && XSSValidation.isNotValid(parameter, values)
                    ? logAndEmpty(parameter, values)
                    : values;
    }

    @Override
    public String getParameter(String parameter) {
      String value = super.getParameter(parameter);
      return XSSValidation.hasToBeChecked(parameter, value) && XSSValidation.isNotValid(parameter, value)
              ? logAndEmpty(parameter, value)
              : value;
    }

    private String[] logAndEmpty(String key, String[] value) {
        log.error("The values: {}; for the field {} are not valid", StringUtils.join(value, ", "), key);
        return new String[0];
    }
    private String logAndEmpty(String key, String value) {
      log.error("The value {} for the field {} is not valid", value, key);
      return "";
    }

}