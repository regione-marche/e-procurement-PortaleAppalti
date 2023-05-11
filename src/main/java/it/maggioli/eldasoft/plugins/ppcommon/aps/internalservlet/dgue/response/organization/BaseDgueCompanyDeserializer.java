package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.dgue.response.organization;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;

/**
 * Classe con metodi di utilità per estrarre valori dall'xml.
 */
abstract class BaseDgueCompanyDeserializer {

    protected Document doc;
    protected XPath    xpath;
    protected NodeList rootElement;

    protected int notFoundValue = 0;

    public BaseDgueCompanyDeserializer(Document doc, XPath xpath) {
        this.doc = doc;
        this.xpath = xpath;
    }
    public BaseDgueCompanyDeserializer(NodeList rootElement) {
        this.rootElement = rootElement;
    }

    /**
     * Recupero il contenuto (stringa) del nodo specificato
     * @param path
     * @return
     */
    protected String getNodeContent(String path) {
        String toReturn = null;

        try {
            Node node = (Node) xpath.compile(path).evaluate(doc, XPathConstants.NODE);

            if (node == null) notFoundValue++;
            else toReturn = node.getTextContent();
        } catch (Exception e) {
            notFoundValue++;
        }

        return toReturn;
    }

    /**
     * Recupero la lista di nodi con percorso specificato
     * @param path
     * @return
     */
    protected NodeList getNodeList(String path) {
        NodeList toReturn = null;

        try {
            toReturn = (NodeList) xpath.compile(path).evaluate(doc, XPathConstants.NODESET);
            if (toReturn == null || toReturn.getLength() == 0) notFoundValue++;
        } catch (Exception e) {
            notFoundValue++;
        }

        return toReturn;
    }

    /**
     * Cerco il nodo con nome specificato tra i figli della lista di nodi passata come parametro.
     * @param list
     * @param nodeName
     * @return
     */
    protected Node findNode(NodeList list, String nodeName) {
        Node toReturn = null;

        if (list != null && list.getLength() > 0) {
            int index = -1;
            while (++index < list.getLength()) {
                Node node = list.item(index);
                if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals(nodeName)) {
                    toReturn = node;
                    break;
                }
            }
        }

        return toReturn;
    }

    public int getNotFoundValue() {
        return notFoundValue;
    }

}
