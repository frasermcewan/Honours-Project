package com.example.mcewans_lager.honoursproject;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;



/**
 * Created by mcewans_lager on 17/02/16.
 */
public class XMLHandler extends DefaultHandler {


    XMLCollection xcol = new XMLCollection();

public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
    if (localName.equals("name")) {
        String Location = attributes.getValue("n");

    }
}

}
