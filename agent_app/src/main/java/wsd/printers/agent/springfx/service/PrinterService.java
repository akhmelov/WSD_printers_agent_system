package wsd.printers.agent.springfx.service;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import wsd.printers.agent.springfx.model.AgentConfModel;
import wsd.printers.agent.springfx.model.DocumentModel;
import wsd.printers.agent.springfx.model.PageConfModel;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by akhmelov on 11/1/16.
 */
@Service
public class PrinterService {

    private Logger logger = Logger.getLogger(PrinterService.class);

    private AgentConfModel agentConfModel;
    private BlockingQueue<DocumentModel> documentBlockingQueue = new LinkedBlockingQueue<>();

    @PostConstruct
    public void init(){
        Thread thread = new Thread(() -> {
            try {
                DocumentModel documentModel = documentBlockingQueue.take();
                Optional<PageConfModel> first = agentConfModel.getPageConfModelList()
                        .stream().filter(p -> p.getFormat().equals(documentModel.getFormat())).findFirst();
                if(first.isPresent()){
                    int pages = documentModel.countPages();
                    for(; pages > 0; pages--) {
                        Thread.sleep(first.get().getDurationOnePageSeconds() * 1000);
                    }
                    // TODO: 11/1/16 documentModel to pdf somewhere
                }
            } catch (InterruptedException e) {
                logger.error(e);
            }
        });
        thread.start();
    }

    public void loadConfig(AgentConfModel agentConfModel){
        this.agentConfModel = agentConfModel;
    }

    public void addToQueue(DocumentModel document){
        documentBlockingQueue.add(document);
    }

    public int getSizeOfQueue(){
        return documentBlockingQueue.size();
    }

    public Duration estimatedQueueDuration(){
        Duration duration = Duration.ZERO;
        for(DocumentModel document: documentBlockingQueue){
            Optional<PageConfModel> first = agentConfModel.getPageConfModelList()
                    .stream().filter(p -> p.getFormat().equals(document.getFormat())).findFirst();

            if(first.isPresent())
                duration.plusSeconds(document.countPages() * first.get().getDurationOnePageSeconds());
            else
                logger.error("Unsupported format in queue: " + document.getFormat());
        }
        return duration;
    }
}
