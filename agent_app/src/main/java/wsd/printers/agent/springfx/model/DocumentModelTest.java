package wsd.printers.agent.springfx.model;

import org.apache.pdfbox.pdmodel.PDDocument;
import wsd.printers.agent.springfx.enums.PaperFormatEnum;
import wsd.printers.agent.springfx.enums.PrinterTypeEnum;

/**
 * Created by akhmelov on 11/7/16.
 */
public class DocumentModelTest  {
    protected int pagesNumber;
    protected PrinterTypeEnum printerTypeEnum;
    protected PaperFormatEnum paperFormatEnum;


    public int countPages() {
        return pagesNumber;
    }


    public void setPrinterTypeEnum(PrinterTypeEnum printerTypeEnum) {
        this.printerTypeEnum = printerTypeEnum;
//        throw new UnsupportedOperationException();
    }


    public PaperFormatEnum getPaperFormatEnum() {
        return getPaperFormatEnum();
    }


    public PrinterTypeEnum getPrinterTypeEnum() {
        return getPrinterTypeEnum();
    }


    public void setPaperFormatEnum(PaperFormatEnum paperFormatEnum) {
        this.paperFormatEnum = paperFormatEnum;
//        throw new UnsupportedOperationException();
    }


//    public PDDocument getFile() {
//        throw new UnsupportedOperationException();
//    }
//
//
//    public void setFile(PDDocument file) {
//        throw new UnsupportedOperationException();
//    }

    public DocumentModelTest(int pagesNumber, PrinterTypeEnum printerTypeEnum, PaperFormatEnum paperFormatEnum) {
        this.pagesNumber = pagesNumber;
        this.printerTypeEnum = printerTypeEnum;
        this.paperFormatEnum = paperFormatEnum;
    }

    public DocumentModelTest() {
    }
}
