package ru.rtlabs.Service;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class SoapParser {
    private Document document;
    private String namespace;

    public SoapParser(Document document) {
        this.document = document;
    }

    public SoapMessage parse() throws SoapParserException {
        Node soapBody = getSoapBody();
        Node messageNode = getChildElement(soapBody);
        boolean fault = isFaultNode(messageNode);
        try {
            Document result = getBodyMessage(getDocumentBuilder(), messageNode);
            return new SoapMessage(result, fault);
        } catch (ParserConfigurationException e) {
            throw new SoapParserException(ErrorCode.CREATE_DOCUMENT_BUILDER,
                    "Cannot create DocumentBuilder", e);
        }
    }

    private Node getSoapBody() throws SoapParserException {
        StringBuilder stringBuilder = new StringBuilder();
        Element root = document.getDocumentElement();
        namespace = root.getNamespaceURI();
        String rootPrefix = root.getPrefix();

        if(rootPrefix.isEmpty()) {
            stringBuilder.append("Body");
        }
        else {
            stringBuilder.append(rootPrefix).append(":Body");

        }

        NodeList nodeList = root.getElementsByTagName(stringBuilder.toString());
        if (nodeList == null || nodeList.item(0) == null) {
            throw new SoapParserException(ErrorCode.MISSING_BODY,
                    "No Body tag found in document");
        }

        return nodeList.item(0);
    }

    private Node getChildElement(Node node) throws SoapParserException {
        Node messageNode = node.getFirstChild();
        while(messageNode != null && !(messageNode instanceof Element)) {
            messageNode = messageNode.getNextSibling();
        }

        if (messageNode != null) {
            return messageNode;
        } else {
            throw new SoapParserException(ErrorCode.MISSING_CHILD_NODE,
                    "Missing message tag");
        }
    }

    private static Document getBodyMessage(DocumentBuilder builder, Node soapBody) {
        Document document;

        document = builder.newDocument();
        document.setDocumentURI(soapBody.getNamespaceURI());

        document.adoptNode(soapBody);
        document.appendChild(soapBody);

        return document;
    }

    private static DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder;
    }

    private boolean isFaultNode(Node node) {
        if(node.getNamespaceURI().equals(namespace) &&
                node.getLocalName().equals("Fault")) {
            return true;
        } else {
            return false;
        }
    }
}