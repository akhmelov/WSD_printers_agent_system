package wsd.printers.agent.springfx.model;

import java.time.Duration;

/**
 * Created by akhmelov on 11/1/16.
 */
public class PageConfModel {
    private String format;
    private long durationOnePageSeconds;


    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public long getDurationOnePageSeconds() {
        return durationOnePageSeconds;
    }

    public void setDurationOnePageSeconds(long durationOnePageSeconds) {
        this.durationOnePageSeconds = durationOnePageSeconds;
    }
}
