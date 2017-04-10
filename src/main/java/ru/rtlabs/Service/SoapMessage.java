package ru.rtlabs.Service;

import org.w3c.dom.Document;

public class SoapMessage {

    private Document document;
    private boolean fault;

    public SoapMessage(Document document, boolean fault) {
        this.document = document;
        this.fault = fault;
    }

    public Document getDocument() {
        return document;
    }

    public boolean isFault() {
        return fault;
    }
}