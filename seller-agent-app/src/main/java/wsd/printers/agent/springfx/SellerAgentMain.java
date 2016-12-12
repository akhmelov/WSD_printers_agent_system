package wsd.printers.agent.springfx;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import wsd.printers.agent.springfx.service.AgentPrinter;

/**
 * Created by akhmelov on 12/11/16.
 */
public class SellerAgentMain extends Agent {

    private static final jade.util.Logger loggerJade = jade.util.Logger.getMyLogger(SellerAgentMain.class.getName());

    private static SellerAgentMain instance;

    @Override
    public void setup(){
        if(instance != null)
            throw new IllegalStateException("Agent already exist!!!!!");
        instance = this;
        loggerJade.info("Seller-agent " + getAID().getName() + " ran.");
    }

    @Override
    protected void takeDown(){

    }

    public static SellerAgentMain getInstance(){
        return instance;
    }
}
