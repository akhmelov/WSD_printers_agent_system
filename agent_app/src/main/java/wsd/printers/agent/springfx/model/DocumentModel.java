package wsd.printers.agent.springfx.model;

import org.apache.pdfbox.pdmodel.PDDocument;
import wsd.printers.agent.springfx.enums.PaperFormatEnum;
import wsd.printers.agent.springfx.enums.PrinterTypeEnum;

import java.io.File;
import java.util.List;

/**
 * Created by akhmelov on 11/1/16.
 */
public class DocumentModel {
    private PDDocument file;
    private PrinterTypeEnum printerTypeEnum;
    private PaperFormatEnum paperFormatEnum;

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
}
