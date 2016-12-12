package wsd.printers.agent.userAgent.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wsd.printers.agent.userAgent.enums.StatusOfDocumentEnum;
import wsd.printers.agent.userAgent.exception.UnsupportedParametersPresentException;
import wsd.printers.agent.userAgent.gui.DocumentManagePresentation;
import wsd.printers.agent.userAgent.model.AgentConfModel;
import wsd.printers.agent.userAgent.model.DocumentModel;

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
        throw new UnsupportedOperationException();
    }

    @Override
    public void addDocumentToQueue(DocumentModel documentModel) throws UnsupportedParametersPresentException {
        documentManagePresentation.addEventInfo("Dokument jest w trakcie przetwarzania i ma "
                + documentModel.countPages() + " stron(y)");
        documentModelBlockingQueue.add(documentModel);
    }

    @Override
    public DocumentModel blockingQueueTake() throws InterruptedException {
        return documentModelBlockingQueue.take();
    }

    @Override
    public Duration estimatedQueueDuration() {
        throw new UnsupportedOperationException();
    }

    @Override
    public BlockingQueue<Map.Entry<String, StatusOfDocumentEnum>> printHere(DocumentModel documentModel) {
        throw new UnsupportedOperationException();
    }

    @Override
    public StatusOfDocumentEnum checkStatus(String idDocument) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void informUser(String infoText) {
        documentManagePresentation.addEventInfo(infoText);
    }

    @Override
    public boolean isDocumentSupported(DocumentModel documentModel) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Duration estimateDocumentDuration(DocumentModel documentModel) {
        throw new UnsupportedOperationException();
    }
}
