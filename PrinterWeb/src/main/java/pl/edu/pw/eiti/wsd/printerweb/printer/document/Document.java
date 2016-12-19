package pl.edu.pw.eiti.wsd.printerweb.printer.document;

import java.io.File;
import java.io.Serializable;

import pl.edu.pw.eiti.wsd.printerweb.printer.driver.PrinterDriver.PrinterInfo.PrinterType;

public interface Document extends Serializable {

    abstract PaperFormat getPaperFormat();
    
    abstract int getNumberOfPages();

    abstract File getFile();

    abstract PrinterType getPrinterType();
}
