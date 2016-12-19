package pl.edu.pw.eiti.wsd.printerweb.printer;

import java.util.Set;

import jade.core.AID;
import pl.edu.pw.eiti.wsd.printerweb.printer.document.Document;
import pl.edu.pw.eiti.wsd.printerweb.printer.driver.PrinterDriver.PrinterInfo;
import pl.edu.pw.eiti.wsd.printerweb.printer.rank.PrinterRankCalculator;

public class PrinterSelector {

    private PrinterRankCalculator calculator;

    public PrinterSelector(PrinterRankCalculator calculator) {
        this.calculator = calculator;
    }

    public AID selectOffer(Document document, Set<PrinterOffer> offers) {
        AID currentSelection = null;
        int currentRank = Integer.MAX_VALUE;
        for (PrinterOffer offer : offers) {
            int rank = calculator.calculateRank(document, offer.getPrinterInfo());
            if (rank < currentRank) {
                currentRank = rank;
                currentSelection = offer.getAID();
            }
        }

        return currentSelection;
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
