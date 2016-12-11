package wsd.printers.agent.springfx.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wsd.printers.agent.springfx.enums.PaperFormatEnum;
import wsd.printers.agent.springfx.enums.PrinterTypeEnum;
import wsd.printers.agent.springfx.exception.UnsupportedParametersPresentException;
import wsd.printers.agent.springfx.model.DocumentModel;

import java.io.File;
import java.io.IOException;

/**
 * Created by akhmelov on 11/1/16.
 */
@Service
public class DocumentManageService {

    @Autowired
    private AgentPrinter agentPrinter;

    public int sayAgentAboutDocument(File file, PrinterTypeEnum printerTypeEnum, PaperFormatEnum paperFormatEnum) throws IOException, UnsupportedParametersPresentException {
        PDDocument doc = PDDocument.load(file);
        DocumentModel documentModel = new DocumentModel(doc, printerTypeEnum, paperFormatEnum);
        agentPrinter.addDocumentToQueue(documentModel);
        return documentModel.countPages();
    }

}
