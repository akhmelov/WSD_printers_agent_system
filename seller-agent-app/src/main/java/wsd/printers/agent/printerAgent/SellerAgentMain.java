package wsd.printers.agent.printerAgent;

import jade.core.Agent;

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
