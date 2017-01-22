package pl.edu.pw.eiti.wsd.printerweb.printer.document;

import java.io.File;
import java.io.Serializable;
import java.time.LocalDate;

import pl.edu.pw.eiti.wsd.printerweb.printer.LocationProvider.Location;
import pl.edu.pw.eiti.wsd.printerweb.printer.driver.PrinterDriver.PrinterInfo.PrinterType;

public interface Document extends Serializable {

    abstract PaperFormat getPaperFormat();

    abstract int getNumberOfPages();

    abstract File getFile();

    abstract PrinterType getPrinterType();

    abstract int getNumberOfCopies();

    abstract LocalDate getPreferredDate();

    abstract boolean isDoubleSided();

    abstract int getMinResolution();

    abstract Location getSourceLocation();
}
