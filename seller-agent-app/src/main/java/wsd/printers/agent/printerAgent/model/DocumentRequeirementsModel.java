package wsd.printers.agent.printerAgent.model;

import wsd.printers.agent.printerAgent.enums.PaperFormatEnum;
import wsd.printers.agent.printerAgent.enums.PrinterTypeEnum;

import java.time.Duration;

/**
 * Created by akhmelov on 11/1/16.
 */
public class DocumentRequeirementsModel {
    private PrinterTypeEnum printerTypeEnum;
    private PaperFormatEnum paperFormatEnum;
    private Duration prefferedTimeEnd;

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

    public Duration getPrefferedTimeEnd() {
        return prefferedTimeEnd;
    }

    public void setPrefferedTimeEnd(Duration prefferedTimeEnd) {
        this.prefferedTimeEnd = prefferedTimeEnd;
    }
}
