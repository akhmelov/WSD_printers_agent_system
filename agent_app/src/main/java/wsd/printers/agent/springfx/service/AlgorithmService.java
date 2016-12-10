package wsd.printers.agent.springfx.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wsd.printers.agent.springfx.model.DocumentModel;

import javax.annotation.PostConstruct;

/**
 * Created by akhmelov on 12/10/16.
 */
@Service
public class AlgorithmService {

    private Logger logger = Logger.getLogger(AlgorithmService.class);

    @Autowired
    private AgentPrinter agentPrinter;

    private Thread algorithmThread;

    @PostConstruct
    public void init(){
        this.algorithmThread = new Thread(this::run);
        this.algorithmThread.setDaemon(true);
        this.algorithmThread.start();
    }

    private void run(){
        while (true){
            try {
                DocumentModel documentModel = agentPrinter.blockingQueueTake(); //to sie zawieszamy i czekamy poki cos do nas przyjdzie
                // TODO: 12/10/16 logikka algorytmu
            } catch (InterruptedException e) {
                logger.error(e);
            }
        }
    }
}
