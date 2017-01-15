package pl.edu.pw.eiti.wsd.printerweb.printer.rank;

import pl.edu.pw.eiti.wsd.printerweb.printer.document.Document;
import pl.edu.pw.eiti.wsd.printerweb.printer.driver.PrinterDriver.PrinterInfo;

public class PrinterRankCalculator {

    public int calculateRank(Document document, PrinterInfo printerInfo) {
        return document.getNumberOfPages() * printerInfo.getPrinterEfficiency();
    }
}
