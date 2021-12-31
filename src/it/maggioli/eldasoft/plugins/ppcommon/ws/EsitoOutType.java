/**
 * EsitoOutType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.maggioli.eldasoft.plugins.ppcommon.ws;

public class EsitoOutType  implements java.io.Serializable {
    private boolean esitoOk;

    private java.lang.String codiceErrore;

    public EsitoOutType() {
    }

    public EsitoOutType(
           boolean esitoOk,
           java.lang.String codiceErrore) {
           this.esitoOk = esitoOk;
           this.codiceErrore = codiceErrore;
    }


    /**
     * Gets the esitoOk value for this EsitoOutType.
     * 
     * @return esitoOk
     */
    public boolean isEsitoOk() {
        return esitoOk;
    }


    /**
     * Sets the esitoOk value for this EsitoOutType.
     * 
     * @param esitoOk
     */
    public void setEsitoOk(boolean esitoOk) {
        this.esitoOk = esitoOk;
    }


    /**
     * Gets the codiceErrore value for this EsitoOutType.
     * 
     * @return codiceErrore
     */
    public java.lang.String getCodiceErrore() {
        return codiceErrore;
    }


    /**
     * Sets the codiceErrore value for this EsitoOutType.
     * 
     * @param codiceErrore
     */
    public void setCodiceErrore(java.lang.String codiceErrore) {
        this.codiceErrore = codiceErrore;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof EsitoOutType)) return false;
        EsitoOutType other = (EsitoOutType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.esitoOk == other.isEsitoOk() &&
            ((this.codiceErrore==null && other.getCodiceErrore()==null) || 
             (this.codiceErrore!=null &&
              this.codiceErrore.equals(other.getCodiceErrore())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        _hashCode += (isEsitoOk() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        if (getCodiceErrore() != null) {
            _hashCode += getCodiceErrore().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(EsitoOutType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.eldasoft.it/PortaleAlice/", "EsitoOutType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("esitoOk");
        elemField.setXmlName(new javax.xml.namespace.QName("", "esitoOk"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("codiceErrore");
        elemField.setXmlName(new javax.xml.namespace.QName("", "codiceErrore"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
