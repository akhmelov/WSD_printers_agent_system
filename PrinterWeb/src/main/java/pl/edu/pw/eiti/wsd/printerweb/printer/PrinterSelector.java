package pl.edu.pw.eiti.wsd.printerweb.printer;

import java.util.Collections;
import java.util.Set;

import jade.core.AID;
import pl.edu.pw.eiti.wsd.printerweb.printer.driver.PrinterDriver.PrinterInfo;

public class PrinterSelector {

    public AID selectOffer(Set<PrinterOffer> offers) {
        return offers.iterator().next().getAID();
    }

    public static class PrinterOffer {

        private AID id;

        private PrinterInfo info;

        public PrinterOffer(AID id, PrinterInfo info) {
            this.id = id;
            this.info = info;
        }

        public AID getAID() {
            return id;
        }

        public PrinterInfo getPrinterInfo() {
            return info;
        }
    }
}
