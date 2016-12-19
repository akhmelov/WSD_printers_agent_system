package pl.edu.pw.eiti.wsd.printerweb.printer.driver;

import java.io.Serializable;

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
            PRINTED, PRINTING, NO_PAPER, NO_INK, CRASHED, READY;
        }
    }

    public interface PrinterInfo extends Serializable {

        public enum PrinterType {
            COLOR("Kolorowy"), BLACK("Czarno-biały");

            private final String displayString;

            PrinterType(String displayString) {
                this.displayString = displayString;
            }

            @Override
            public String toString() {
                return displayString;
            }
        }

        PrinterType getPrinterType();

        int getPrinterEfficiency();

        String getName();
    }

    abstract void setNoInk(boolean b);

    abstract void setNoPaper(boolean b);

    abstract void setCrashed(boolean b);
}
