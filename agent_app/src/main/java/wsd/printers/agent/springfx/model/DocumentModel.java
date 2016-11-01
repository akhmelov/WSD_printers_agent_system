package wsd.printers.agent.springfx.model;

import java.io.File;
import java.util.List;

/**
 * Created by akhmelov on 11/1/16.
 */
public class DocumentModel {
    private File file;
    protected String format;

    public int countPages(){
        // TODO: 11/1/16
        return 1;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
