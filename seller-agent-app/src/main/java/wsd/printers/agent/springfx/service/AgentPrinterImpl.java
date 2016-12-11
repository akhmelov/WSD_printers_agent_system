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

    @Autowired
    private PrinterService printerService;

    private BlockingQueue<DocumentModel> documentModelBlockingQueue = new LinkedBlockingQueue<>();

    @Override
    public AgentConfModel getThisAgentParameters() {
        return printerService.getAgentConfModel();
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
        return printerService.estimatedQueueDuration();
    }

    @Override
    public BlockingQueue<Map.Entry<String, StatusOfDocumentEnum>> printHere(DocumentModel documentModel) {
        return printerService.addToQueue(documentModel);
    }

    @Override
    public StatusOfDocumentEnum checkStatus(String idDocument) {
        return printerService.checkStatus(idDocument);
    }

    @Override
    public void informUser(String infoText) {
        documentManagePresentation.addEventInfo(infoText);
    }

    @Override
    public boolean isDocumentSupported(DocumentModel documentModel) {
        return printerService.isDocumentSupported(documentModel);
    }

    @Override
    public Duration estimateDocumentDuration(DocumentModel documentModel) {
        throw new UnsupportedOperationException();
    }
}
