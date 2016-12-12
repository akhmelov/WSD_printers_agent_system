package pl.edu.pw.eiti.wsd.printerweb.printer.driver;

import jade.domain.FIPAAgentManagement.FailureException;
import pl.edu.pw.eiti.wsd.printerweb.printer.document.Document;

public interface PrinterDriver {

    abstract String addToQueue(Document document) throws FailureException;

    abstract PrinterInfo getInfo();

    abstract void addListener(PrinterListener listener);

    public interface PrinterListener {

        abstract void listen(PrinterEvent event);
    }

    public interface PrinterEvent {

        abstract Type getType();

        abstract String getValue();

        public static enum Type {
            PRINTED, NO_PAPER, NO_INK, CRASHED, READY;
        }
    }

    public interface PrinterInfo {
        public enum PrinterType {
            None, Laser, Matrix, Inkjet, Lazer
        }

        String getName();
    }

    abstract void setNoInk(boolean b);

    abstract void setNoPaper(boolean b);

    abstract void setCrashed(boolean b);
}
