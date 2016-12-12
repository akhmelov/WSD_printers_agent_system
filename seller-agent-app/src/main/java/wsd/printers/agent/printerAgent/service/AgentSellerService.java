package wsd.printers.agent.printerAgent.service;

import jade.core.Agent;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Created by akhmelov on 12/12/16.
 */
@Service
public class AgentSellerService {

    private Logger logger = Logger.getLogger(AgentSellerService.class);

    @Autowired
    private Agent agent;

    private Thread algorithmThread;

    @PostConstruct
    public void init(){
        this.algorithmThread = new Thread(this::run);
        this.algorithmThread.setDaemon(true);
        this.algorithmThread.start();
    }

    private void run(){

    }
}
