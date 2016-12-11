package pl.edu.pw.eiti.wsd.printerweb.printer.driver;

import pl.edu.pw.eiti.wsd.printerweb.Document;

public interface PrinterDriver {

    abstract String addToQueue(Document document);

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

    }
}
