package wsd.printers.agent.springfx.service;

import org.apache.log4j.Logger;
import org.codehaus.groovy.runtime.metaclass.ConcurrentReaderHashMap;
import org.springframework.stereotype.Service;
import wsd.printers.agent.springfx.enums.PaperFormatEnum;
import wsd.printers.agent.springfx.enums.PrinterTypeEnum;
import wsd.printers.agent.springfx.enums.StatusOfDocumentEnum;
import wsd.printers.agent.springfx.model.AgentConfModel;
import wsd.printers.agent.springfx.model.DocumentModel;
import wsd.printers.agent.springfx.model.PageConfModel;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by akhmelov on 11/1/16.
 */
@Service
public class PrinterService {

    private Logger logger = Logger.getLogger(PrinterService.class);

    private SecureRandom random = new SecureRandom();

    private AgentConfModel agentConfModel;
    private BlockingQueue<Map.Entry<String, DocumentModel>> documentBlockingQueue = new LinkedBlockingQueue<>();
    private Map<String, BlockingQueue<Map.Entry<String, StatusOfDocumentEnum>>> statusOfDocumentEnumMap = new HashMap();
    private Thread printerThread;
    private boolean isPaperPresent = true;
    private boolean isInkPresent = true;

    @PostConstruct
    public void init(){
        this.printerThread = new Thread(() -> {
            while (true){
                Map.Entry<String, DocumentModel> document = null;
                BlockingQueue<Map.Entry<String, StatusOfDocumentEnum>> blockingQueue = null;
                try {
                    document = documentBlockingQueue.take();
                    DocumentModel documentModel = document.getValue();

                    blockingQueue = statusOfDocumentEnumMap.get(document.getKey());
                    blockingQueue.add(new AbstractMap.SimpleEntry(document.getKey(), StatusOfDocumentEnum.PRINTING));
                    Optional<HashMap> first = agentConfModel.getPageConfModelList()
                            .stream().filter(
                                    p -> PaperFormatEnum.valueOf((String) p.get("format")).equals(documentModel.getPaperFormatEnum())
                            ).findFirst();

                    if(first.isPresent()){
                        int pages = documentModel.countPages();
                        for(; pages > 0; pages--) {
                            while (!isPaperPresent || !isInkPresent){
                                Thread.sleep(1000);
                            }
                            Thread.sleep(Integer.valueOf((String) first.get().get("durationOnePageSeconds")) * 1000);
                        }
                        // TODO: 11/1/16 documentModel to pdf somewhere
                        blockingQueue.add(new AbstractMap.SimpleEntry(document.getKey(), StatusOfDocumentEnum.PRINTED));
                        logger.debug("Printed document id: '" + document.getKey());
                    }
                } catch (InterruptedException e) {
                    logger.error(e);
                    blockingQueue.add(new AbstractMap.SimpleEntry(document.getKey(), StatusOfDocumentEnum.FAILD));
                }
            }
        });
        this.printerThread.setDaemon(true);
        this.printerThread.start();
    }

    public void loadConfig(AgentConfModel agentConfModel){
        this.agentConfModel = agentConfModel;
    }

    public BlockingQueue<Map.Entry<String, StatusOfDocumentEnum>> addToQueue(DocumentModel document){
        BlockingQueue<Map.Entry<String, StatusOfDocumentEnum>> blockingQueue = new LinkedBlockingQueue<>();
        String key;
        do {
            key = new BigInteger(130, random).toString(32);;
        } while (statusOfDocumentEnumMap.containsKey(key));
        statusOfDocumentEnumMap.put(key, blockingQueue);
        blockingQueue.add(new AbstractMap.SimpleEntry(key, StatusOfDocumentEnum.WAIT_IN_PRINTER_QUEUE));
        documentBlockingQueue.add(new AbstractMap.SimpleEntry(key, document));
        return blockingQueue;
    }

    public int getSizeOfQueue(){
        return documentBlockingQueue.size();
    }

    public Duration estimatedQueueDuration(){
        Duration duration = Duration.ZERO;
        for(Map.Entry<String, DocumentModel> queue: documentBlockingQueue){
            DocumentModel document = queue.getValue();
            Optional<HashMap> first = agentConfModel.getPageConfModelList()
                    .stream().filter(p -> PaperFormatEnum.valueOf((String) p.get("format")).equals(document.getPaperFormatEnum())).findFirst();

            if(first.isPresent())
                duration.plusSeconds(document.countPages() * Integer.valueOf((String) first.get().get("durationOnePageSeconds")));
            else
                logger.error("Unsupported format in queue: " + document.getPaperFormatEnum());
        }
        return duration;
    }

    public boolean isDocumentSupported(DocumentModel documentModel){
        if(!agentConfModel.getTypeOfPrinter().equals(documentModel.getPrinterTypeEnum())
                && !documentModel.getPrinterTypeEnum().equals(PrinterTypeEnum.None)){
            return false;
        }
        for (HashMap pageConfModel: agentConfModel.getPageConfModelList()){
            if(PaperFormatEnum.valueOf((String) pageConfModel.get("format")).equals(documentModel.getPaperFormatEnum())){
                return true;
            }
        }
        return false;
    }

    public AgentConfModel getAgentConfModel() {
        return agentConfModel;
    }

    public void setAgentConfModel(AgentConfModel agentConfModel) {
        this.agentConfModel = agentConfModel;
    }

    public StatusOfDocumentEnum checkStatus(String id){
        Optional<Map.Entry<String, StatusOfDocumentEnum>> first
                = statusOfDocumentEnumMap.get(id).stream().filter(x -> x.getKey().equals(id)).findFirst();
        if(first.isPresent())
            return first.get().getValue();
        return null;
    }

    public void pausePrinting() throws InterruptedException {
        printerThread.wait();
    }

    public void resumePrinting(){
        printerThread.notify();
    }

    public boolean isPaperPresent() {
        return isPaperPresent;
    }

    public void setPaperPresent(boolean paperPresent) {
        isPaperPresent = paperPresent;
    }

    public boolean isInkPresent() {
        return isInkPresent;
    }

    public void setInkPresent(boolean inkPresent) {
        isInkPresent = inkPresent;
    }
}
