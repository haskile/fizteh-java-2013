package ru.fizteh.fivt.students.chernigovsky.proxy;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.StringWriter;
import java.io.Writer;

public class Test {
    public  static void main(String[] args) {
        try {
            StringWriter writer = new StringWriter();
            XMLOutputFactory outputFactory = XMLOutputFactory.newFactory();
            XMLStreamWriter xmlStreamWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(writer);
            xmlStreamWriter.writeStartElement("start");
            xmlStreamWriter.writeAttribute("atr", Long.toString(0));
            xmlStreamWriter.writeEndElement();
            System.out.print(writer.toString());
        } catch (XMLStreamException ex) {
            // Do nothing
        }
    }
}
