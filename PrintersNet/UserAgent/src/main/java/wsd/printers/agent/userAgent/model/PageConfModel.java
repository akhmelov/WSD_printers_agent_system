package wsd.printers.agent.userAgent.model;

import wsd.printers.agent.common.enums.PaperFormatEnum;

/**
 * Created by akhmelov on 11/1/16.
 */
public class PageConfModel {
    private PaperFormatEnum format;
    private long durationOnePageSeconds;


    public PaperFormatEnum getFormat() {
        return format;
    }

    public void setFormat(PaperFormatEnum format) {
        this.format = format;
    }

    public long getDurationOnePageSeconds() {
        return durationOnePageSeconds;
    }

    public void setDurationOnePageSeconds(long durationOnePageSeconds) {
        this.durationOnePageSeconds = durationOnePageSeconds;
    }
}
