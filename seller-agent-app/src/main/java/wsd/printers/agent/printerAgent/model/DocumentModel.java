package wsd.printers.agent.printerAgent.model;

import org.apache.pdfbox.pdmodel.PDDocument;
import wsd.printers.agent.printerAgent.enums.PaperFormatEnum;
import wsd.printers.agent.printerAgent.enums.PrinterTypeEnum;

/**
 * Created by akhmelov on 11/1/16.
 */
public class DocumentModel {
    protected PDDocument file;
    protected PrinterTypeEnum printerTypeEnum;
    protected PaperFormatEnum paperFormatEnum;

    public DocumentModel() {
    }

    public DocumentModel(PDDocument file, PrinterTypeEnum printerTypeEnum, PaperFormatEnum paperFormatEnum) {
        this.file = file;
        this.printerTypeEnum = printerTypeEnum;
        this.paperFormatEnum = paperFormatEnum;
    }

    public int countPages(){
        return file.getNumberOfPages();
    }

    public PDDocument getFile() {
        return file;
    }

    public void setFile(PDDocument file) {
        this.file = file;
    }

    public PrinterTypeEnum getPrinterTypeEnum() {
        return printerTypeEnum;
    }

    public void setPrinterTypeEnum(PrinterTypeEnum printerTypeEnum) {
        this.printerTypeEnum = printerTypeEnum;
    }

    public PaperFormatEnum getPaperFormatEnum() {
        return paperFormatEnum;
    }

    public void setPaperFormatEnum(PaperFormatEnum paperFormatEnum) {
        this.paperFormatEnum = paperFormatEnum;
    }

    @Override
    public String toString() {
        return "DocumentModel{" +
                "file=" + file +
                ", printerTypeEnum=" + printerTypeEnum +
                ", paperFormatEnum=" + paperFormatEnum +
                '}';
    }
}
