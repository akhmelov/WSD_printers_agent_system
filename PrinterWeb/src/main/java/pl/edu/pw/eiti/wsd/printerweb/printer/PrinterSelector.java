package pl.edu.pw.eiti.wsd.printerweb.printer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import jade.core.AID;
import pl.edu.pw.eiti.wsd.printerweb.printer.document.Document;
import pl.edu.pw.eiti.wsd.printerweb.printer.driver.PrinterDriver.PrinterInfo;
import pl.edu.pw.eiti.wsd.printerweb.printer.driver.PrinterDriver.PrinterInfo.PrinterType;

public class PrinterSelector {
    
    private LocationProvider locationProvider;

    public PrinterSelector(LocationProvider locationProvider) {
        this.locationProvider = locationProvider;
    }

    private final Comparator<PrinterOffer> byResolutionCopmarator = new Comparator<PrinterSelector.PrinterOffer>() {

        @Override
        public int compare(PrinterOffer o1, PrinterOffer o2) {
            return Integer.compare(o1.getPrinterInfo().getResolution(), o2.getPrinterInfo().getResolution()) * (-1);
        }
    };

    private final Comparator<PrinterOffer> byQueueLengthComparator = new Comparator<PrinterSelector.PrinterOffer>() {

        @Override
        public int compare(PrinterOffer o1, PrinterOffer o2) {
            return Integer.compare(o1.getPrinterInfo().getCurrentQueueLength(), o2.getPrinterInfo().getCurrentQueueLength());
        }
    };
    private final Comparator<PrinterOffer> byPrintingTimeComparator = new Comparator<PrinterSelector.PrinterOffer>() {

        @Override
        public int compare(PrinterOffer o1, PrinterOffer o2) {
            return Integer.compare(o1.getPrintingTime(), o2.getPrintingTime());
        }
    };
    
    private final Comparator<PrinterOffer> byDistanceComparator = new Comparator<PrinterSelector.PrinterOffer>() {

        @Override
        public int compare(PrinterOffer o1, PrinterOffer o2) {
            return Integer.compare(o1.getDistance(), o2.getDistance());
        }
    };

    public AID selectOffer(Document document, List<PrinterOffer> offers) {
        AID currentSelection = null;

        Map<AID, Integer> offerRank = new HashMap<>();
        List<PrinterOffer> applicableOffers = filter(document, offers);

        Collections.sort(applicableOffers, byResolutionCopmarator);
        applyPoints(offerRank, applicableOffers);

        Collections.sort(applicableOffers, byQueueLengthComparator);
        applyPoints(offerRank, applicableOffers);

        for (PrinterOffer printerOffer : applicableOffers) {
            printerOffer.setPrintingTime(calculatePrintingTime(document, printerOffer.getPrinterInfo()));
            printerOffer.setDistance(locationProvider.calculateDistance(document.getSourceLocation(), printerOffer.getPrinterInfo().getLocation()));
        }
        Collections.sort(applicableOffers, byPrintingTimeComparator);
        applyPoints(offerRank, applicableOffers);
        
        Collections.sort(applicableOffers, byDistanceComparator);
        applyPoints(offerRank, applicableOffers);
        
        return currentSelection;
    }

    private int calculatePrintingTime(Document document, PrinterInfo printerInfo) {
        int pagePrintingTime = document.getPrinterType() == PrinterType.BLACK ? printerInfo.getPrinterBlackEfficiency()
                : printerInfo.getPrinterColorEfficiency();
        int numberOfPages = document.getNumberOfPages();

        int jobTime = numberOfPages * pagePrintingTime;
        if (numberOfPages < printerInfo.getPaperContainerActualCapacity()) {
            jobTime += ((numberOfPages - printerInfo.getPaperContainerActualCapacity()) / printerInfo.getPaperContainerCapacity())
                    * printerInfo.getRefillTime();
        }

        return jobTime;
    }

    private void applyPoints(Map<AID, Integer> offerRank, List<PrinterOffer> applicableOffers) {
        int points = 5;
        for (PrinterOffer printerOffer : applicableOffers) {
            offerRank.put(printerOffer.getAID(), offerRank.get(printerOffer.getAID()) + points);
            points -= 1;
        }
    }

    private List<PrinterOffer> filter(Document document, List<PrinterOffer> offers) {
        List<PrinterOffer> applicableOffers = new ArrayList<>();
        for (PrinterOffer offer : offers) {
            if (isApplicable(document, offer.getPrinterInfo())) {
                applicableOffers.add(offer);
            }
        }

        return applicableOffers;
    }

    private int calculateRank(Document document, PrinterInfo printerInfo, Set<PrinterOffer> offers) {

        return Integer.MAX_VALUE;
    }

    private int calculate(Document document, PrinterInfo printerInfo) {
        return document.getNumberOfPages() * printerInfo.getPrinterColorEfficiency();
    }

    private boolean isApplicable(Document document, PrinterInfo printerInfo) {
        boolean applicable = true;

        applicable = applicable && printerInfo.getSupportedPaperFormats().contains(document.getPaperFormat());
        applicable = document.isDoubleSided() ? printerInfo.isDoubleSidedSupported() : true;
        applicable = applicable && document.getMinResolution() < printerInfo.getResolution();

        if (applicable) {
            if (document.getPrinterType() == PrinterType.BLACK) {
                applicable = printerInfo.getPrinterType() == PrinterType.BLACK
                        || printerInfo.getPrinterType() == PrinterType.COLOR;
            } else {
                applicable = applicable && printerInfo.getPrinterType() == PrinterType.COLOR;
            }
        }

        return applicable;
    }

    public static class PrinterOffer {

        private AID id;

        private PrinterInfo info;
        
        private int printingTime;
        
        private int distance;

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

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((id == null) ? 0 : id.hashCode());
            result = prime * result + ((info == null) ? 0 : info.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            PrinterOffer other = (PrinterOffer) obj;
            if (id == null) {
                if (other.id != null)
                    return false;
            } else if (!id.equals(other.id))
                return false;
            if (info == null) {
                if (other.info != null)
                    return false;
            } else if (!info.equals(other.info))
                return false;
            return true;
        }

        private int getPrintingTime() {
            return printingTime;
        }

        
        private void setPrintingTime(int printingTime) {
            this.printingTime = printingTime;
        }
        
        private int getDistance() {
            return distance;
        }
        
        private void setDistance(int distance) {
            this.distance = distance;
        }
    }
}
