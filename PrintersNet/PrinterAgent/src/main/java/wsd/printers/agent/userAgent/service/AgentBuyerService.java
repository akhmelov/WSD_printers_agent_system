package wsd.printers.agent.userAgent.service;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wsd.printers.agent.userAgent.model.DocumentModel;

import javax.annotation.PostConstruct;

/**
 * Created by akhmelov on 12/10/16.
 */
@Service
public class AgentBuyerService {

    private Logger logger = Logger.getLogger(AgentBuyerService.class);

    @Autowired
    private AgentPrinter agentPrinter;
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
        while (true){
            try {
                DocumentModel documentModel = agentPrinter.blockingQueueTake(); //to sie zawieszamy i czekamy poki cos do nas przyjdzie
                logger.debug("Algorithm document: " + documentModel.toString());

                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                msg.addReceiver(new AID("seller-agent-main-1", AID.ISLOCALNAME));
                msg.setLanguage("English");
                msg.setOntology("Weather-forecast-ontology");
                msg.setContent("Today it’s raining");
                agent.send(msg);
            } catch (InterruptedException e) {
                logger.error(e);
            }
        }
    }


}
