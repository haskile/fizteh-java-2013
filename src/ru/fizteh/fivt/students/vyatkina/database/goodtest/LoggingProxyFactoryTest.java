package ru.fizteh.fivt.students.vyatkina.database.goodtest;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import ru.fizteh.fivt.proxy.LoggingProxyFactory;
import ru.fizteh.fivt.students.vyatkina.database.logging.LoggingProxyFactoryImp;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.util.StreamReaderDelegate;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

public class LoggingProxyFactoryTest {
    private LoggingProxyFactory factory = new LoggingProxyFactoryImp();
    private XmasWizard xmasWizard;
    private StringWriter writer;
    private Class <XmasWizard> interfaceClass = XmasWizard.class;
    private XMLStreamReader reader;


    public interface XmasWizard {
      void deliverAllPresents (Collection<Child> children,Collection <Object> presents);
      void deliverPresent (Child child, Object present);
      boolean beHappy ();
    }

    private class DedMoroz implements XmasWizard {
        HashSet <Child> happyChilden = new HashSet();
        @Override
        public void deliverAllPresents(Collection <Child> children, Collection <Object> presents) {
           if (children == null) {
               return;
           }
           if (presents == null || children.size() < presents.size()) {
               throw new PresentForChildNotFoundExeption();
           }
            Iterator it = presents.iterator();
            for (Child child: children) {
                deliverPresent(child, it.next());
            }
        }

        @Override
        public void deliverPresent(Child child, Object present) {
           happyChilden.add(child);
           child.getPresent(present);
        }

        @Override
        public boolean beHappy() {
            if (happyChilden.isEmpty()) {
                System.out.println("I'm useless");
                return false;
            } else {
            System.out.println("I'm so glad I can please children");
                return true;
            }
        }
    }

    @Before
    public void init () throws Exception {
        this.xmasWizard = new DedMoroz();
        this.writer = new StringWriter();
        this.reader = new StreamReaderDelegate();
    }

    @After
    public void inFile () throws IOException {
        System.out.println("In file:");
        System.out.println(writer.toString());
        writer.close();
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

   @Test
    public void createProxyWithNullArgumentsShouldFail () {
        thrown.expect(IllegalArgumentException.class);
        factory.wrap(null,null,null);
    }

   @Test
    public void beHappyShouldReturnFalse () {
        XmasWizard proxy = (XmasWizard) factory.wrap(writer,xmasWizard,interfaceClass);
        Assert.assertEquals("He should not be happy", false, proxy.beHappy());
    }

    @Test
    public void deliverPresentShouldWriteGoodThings () {
       Child Anny = new Child("Anny");
        XmasWizard proxy = (XmasWizard) factory.wrap(writer,xmasWizard,interfaceClass);
        proxy.deliverPresent(Anny,null);
        Assert.assertEquals("He should be happy", true, proxy.beHappy());
    }

    @Test
    public void deliverAllPresentsShouldWriteInteresting () {
        ArrayList <Child> children = new ArrayList();
        children.add(new Child ("Mary"));
        children.add(new Child ("David"));
        ArrayList <Object> presents = new ArrayList<>();
        presents.add(new Present("Butterfly"));
        presents.add(new Present("Tardis"));
        XmasWizard proxy = (XmasWizard) factory.wrap(writer,xmasWizard,interfaceClass);
        proxy.deliverAllPresents(children,presents);
    }

    @Test
    public void deliverStrangePresentsShouldBeInteresting () {
        ArrayList <Child> children = new ArrayList();
        children.add(new Child ("Tommy"));
        children.add(new Child ("Alice"));
        ArrayList <Object> presents = new ArrayList<>();
        presents.add(children);
        presents.add(children);
        XmasWizard proxy = (XmasWizard) factory.wrap(writer,xmasWizard,interfaceClass);
        proxy.deliverAllPresents(children,presents);
    }

    private class Child {
        String name;
        public Child (String name) {
            this.name = name;
        }
        void getPresent (Object present) {
            String reaction = present == null? "There is nothing there..." : "That a lovely " + present + "!";
            System.out.println(name + ": "+ reaction);
        };

        @Override
        public String toString () {
            return "Child " + name;
        }
    }

    private class Present {
        private final Object inside;
        public Present () {
            inside = null;
        }
        public Present (Object inside) {
            this.inside = inside;
        }
        @Override
        public String toString () {
            return inside == null? "nothing" : inside.toString();
        }
    }

    class PresentForChildNotFoundExeption extends RuntimeException {
        public PresentForChildNotFoundExeption() {
            super();
        }
    }

}
