package pl.edu.pw.eiti.wsd.printerweb.printer.document;

import java.io.File;
import java.io.Serializable;

public interface Document extends Serializable {

    abstract PaperFormat getPaperFormat();
    
    abstract int getNumberOfPages();

    abstract File getFile();
}
