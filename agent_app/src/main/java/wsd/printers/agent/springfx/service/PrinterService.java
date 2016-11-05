package wsd.printers.agent.springfx.service;

import org.apache.log4j.Logger;
import org.codehaus.groovy.runtime.metaclass.ConcurrentReaderHashMap;
import org.springframework.stereotype.Service;
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

    @PostConstruct
    public void init(){
        Thread thread = new Thread(() -> {
            Map.Entry<String, DocumentModel> document = null;
            BlockingQueue<Map.Entry<String, StatusOfDocumentEnum>> blockingQueue = null;
            try {
                document = documentBlockingQueue.take();
                DocumentModel documentModel = document.getValue();

                blockingQueue = statusOfDocumentEnumMap.get(document.getKey());
                blockingQueue.add(new AbstractMap.SimpleEntry(document.getKey(), StatusOfDocumentEnum.PRINTING));
                Optional<PageConfModel> first = agentConfModel.getPageConfModelList()
                        .stream().filter(p -> p.getFormat().equals(documentModel.getPaperFormatEnum())).findFirst();

                if(first.isPresent()){
                    int pages = documentModel.countPages();
                    for(; pages > 0; pages--) {
                        Thread.sleep(first.get().getDurationOnePageSeconds() * 1000);
                    }
                    // TODO: 11/1/16 documentModel to pdf somewhere
                    blockingQueue.add(new AbstractMap.SimpleEntry(document.getKey(), StatusOfDocumentEnum.PRINTED));
                }
            } catch (InterruptedException e) {
                logger.error(e);
                blockingQueue.add(new AbstractMap.SimpleEntry(document.getKey(), StatusOfDocumentEnum.FAILD));
            }
        });
        thread.start();
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
        documentBlockingQueue.add(new AbstractMap.SimpleEntry(key, document));
        blockingQueue.add(new AbstractMap.SimpleEntry(key, StatusOfDocumentEnum.WAIT_IN_PRINTER_QUEUE));
        statusOfDocumentEnumMap.put(key, blockingQueue);
        return blockingQueue;
    }

    public int getSizeOfQueue(){
        return documentBlockingQueue.size();
    }

    public Duration estimatedQueueDuration(){
        Duration duration = Duration.ZERO;
        for(Map.Entry<String, DocumentModel> queue: documentBlockingQueue){
            DocumentModel document = queue.getValue();
            Optional<PageConfModel> first = agentConfModel.getPageConfModelList()
                    .stream().filter(p -> p.getFormat().equals(document.getPaperFormatEnum())).findFirst();

            if(first.isPresent())
                duration.plusSeconds(document.countPages() * first.get().getDurationOnePageSeconds());
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
        for (PageConfModel pageConfModel: agentConfModel.getPageConfModelList()){
            if(pageConfModel.getFormat().equals(documentModel.getPaperFormatEnum())){
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
}
