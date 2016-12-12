package wsd.printers.agent.springfx;

import jade.core.Agent;

/**
 * Created by akhmelov on 12/11/16.
 */
public class BuyerAgent extends Agent {

    private static final jade.util.Logger loggerJade = jade.util.Logger.getMyLogger(BuyerAgent.class.getName());

    private static BuyerAgent instance;

    @Override
    protected void setup(){
        if(instance != null)
            throw new IllegalStateException("Agent already exist!!!!!");
        instance = this;
        loggerJade.info("Buyer-agent " + getAID().getName() + " ran.");
    }

    @Override
    protected void takeDown(){

    }

    public static BuyerAgent getInstance(){
        return instance;
    }
}
