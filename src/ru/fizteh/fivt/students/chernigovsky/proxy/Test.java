package ru.fizteh.fivt.students.chernigovsky.proxy;

import ru.fizteh.fivt.students.chernigovsky.storeable.ExtendedStoreableTable;
import ru.fizteh.fivt.students.chernigovsky.storeable.StoreableTable;
import ru.fizteh.fivt.students.chernigovsky.storeable.StoreableTableProvider;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class Test {
    public  static void main(String[] args) {
        try {
            /*StringWriter writer = new StringWriter();
            XMLOutputFactory outputFactory = XMLOutputFactory.newFactory();
            XMLStreamWriter xmlStreamWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(writer);
            xmlStreamWriter.writeStartElement("start");
            xmlStreamWriter.writeAttribute("atr", Long.toString(0));
            xmlStreamWriter.writeEndElement();
            System.out.print(writer.toString());*/
            StoreableTableProvider tableProvider = new StoreableTableProvider(new File("C:/main/database/"), false);
            List<Class<?>> classes = new ArrayList<Class<?>>();
            classes.add(Integer.class);
            ExtendedStoreableTable table = tableProvider.createTable("table", classes);
            List<Object> value = new ArrayList<>();
            value.add(new Integer(5));
            table.put("key", tableProvider.createFor(table, value));
            table.commit();
            table.close();
            System.out.print(tableProvider.getTable("table").get("key"));
        } catch (IOException ex) {
            // Do nothing
        }
    }
}
