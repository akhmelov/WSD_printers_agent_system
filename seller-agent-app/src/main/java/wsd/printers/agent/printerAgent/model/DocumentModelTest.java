package wsd.printers.agent.printerAgent.model;

import org.apache.pdfbox.pdmodel.PDDocument;
import wsd.printers.agent.printerAgent.enums.PaperFormatEnum;
import wsd.printers.agent.printerAgent.enums.PrinterTypeEnum;

/**
 * Created by akhmelov on 11/7/16.
 */
public class DocumentModelTest extends DocumentModel {
    protected int pagesNumber;

    @Override
    public int countPages() {
        return pagesNumber;
    }

    @Override
    public void setPrinterTypeEnum(PrinterTypeEnum printerTypeEnum) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setPaperFormatEnum(PaperFormatEnum paperFormatEnum) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PDDocument getFile() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setFile(PDDocument file) {
        throw new UnsupportedOperationException();
    }

    public DocumentModelTest(int pagesNumber, PrinterTypeEnum printerTypeEnum, PaperFormatEnum paperFormatEnum) {
        this.pagesNumber = pagesNumber;
        this.printerTypeEnum = printerTypeEnum;
        this.paperFormatEnum = paperFormatEnum;
    }

    public DocumentModelTest() {
    }
}
