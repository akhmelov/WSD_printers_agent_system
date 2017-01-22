package pl.edu.pw.eiti.wsd.printerweb.printer.driver;

import java.io.Serializable;
import java.util.Set;

import jade.domain.FIPAAgentManagement.FailureException;
import pl.edu.pw.eiti.wsd.printerweb.printer.LocationProvider.Location;
import pl.edu.pw.eiti.wsd.printerweb.printer.document.Document;
import pl.edu.pw.eiti.wsd.printerweb.printer.document.PaperFormat;
import pl.edu.pw.eiti.wsd.printerweb.printer.gui.GuiInfo;

public interface PrinterDriver {

    abstract String addToQueue(Document document) throws FailureException;

    abstract PrinterInfo getInfo();

    abstract void addListener(PrinterListener listener);

    public interface PrinterListener {

        abstract void listen(PrinterEvent event);
    }

    void setGuiInfoStatusListener(GuiInfo guiInfoStatus);

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

        int getPrinterColorEfficiency();

        int getPrinterBlackEfficiency();
        
        int getResolution();
        
        Set<PaperFormat> getSupportedPaperFormats();
        
        boolean isDoubleSidedSupported();
        
        int getPaperContainerCapacity();
        
        int getPaperContainerActualCapacity();
        
        boolean isColorSupported();
        
        int getCurrentQueueLength();

        String getName();

        int getRefillTime();

        Location getLocation();
    }

    abstract void setNoInk(boolean b);

    abstract void setNoPaper(boolean b);

    abstract void setCrashed(boolean b);
}
