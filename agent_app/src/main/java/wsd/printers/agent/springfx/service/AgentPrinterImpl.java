package wsd.printers.agent.springfx.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wsd.printers.agent.springfx.enums.StatusOfDocumentEnum;
import wsd.printers.agent.springfx.exception.UnsupportedParametersPresentException;
import wsd.printers.agent.springfx.gui.DocumentManagePresentation;
import wsd.printers.agent.springfx.model.AgentConfModel;
import wsd.printers.agent.springfx.model.DocumentModel;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by akhmelov on 11/5/16.
 */
@Service
public class AgentPrinterImpl implements AgentPrinter {

    @Autowired
    private DocumentManagePresentation documentManagePresentation;

    private BlockingQueue<DocumentModel> documentModelBlockingQueue = new LinkedBlockingQueue<>();

    @Override
    public AgentConfModel getThisAgentParameters() {
        return null;
    }

    @Override
    public void addDocumentToQueue(DocumentModel documentModel) throws UnsupportedParametersPresentException {
        documentManagePresentation.addEventInfo("Dokument jest w trakcie przetwarzania i ma "
                + documentModel.countPages() + " stron(y)");
        documentModelBlockingQueue.add(documentModel);
    }

    @Override
    public DocumentModel blockingQueueTake() {
        return null;
    }

    @Override
    public Duration estimatedQueueDuration() {
        return null;
    }

    @Override
    public BlockingQueue<Map.Entry<String, StatusOfDocumentEnum>> printHere(DocumentModel documentModel) {
        return null;
    }

    @Override
    public StatusOfDocumentEnum checkStatus(String idDocument) {
        return null;
    }

    @Override
    public void informUser(String infoText) {

    }

    @Override
    public boolean isDocumentSupported(DocumentModel documentModel) {
        return false;
    }
}
