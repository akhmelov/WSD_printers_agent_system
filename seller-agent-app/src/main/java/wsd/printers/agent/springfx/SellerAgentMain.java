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

    @Override
    public void setup(){;
        loggerJade.info("Seller-agent " + getAID().getName() + " ran.");
        addBehaviour(new OverbearingBehaviour());
    }

    public class OverbearingBehaviour extends CyclicBehaviour {
        public void action() {
            ACLMessage msg = myAgent.receive();
            if(msg == null)
                block();
            loggerJade.info("Event cames to as");
        }
    }
}
