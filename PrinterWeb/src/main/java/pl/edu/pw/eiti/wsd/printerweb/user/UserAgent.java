package pl.edu.pw.eiti.wsd.printerweb.user;

import java.io.IOException;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;
import pl.edu.pw.eiti.wsd.printerweb.printer.document.Document;
import pl.edu.pw.eiti.wsd.printerweb.user.gui.UserController;

/**
 * Agent which is responsible for interactions with {@link pl.edu.pw.eiti.wsd.printerweb.printer.PrinterAgent PrinterAgent}s on behalf of the user.
 */
public class UserAgent extends Agent {

    private static final long serialVersionUID = -3135506373065999424L;

    public UserAgent() {
        super();
        UserController gui = new UserController(this);
        gui.show();
    }

    public void schedulePrinting(Document documentImpl) throws IOException {
        ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
        request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
        request.addReceiver(new AID("printManager", AID.ISLOCALNAME));
        request.setContentObject(documentImpl);
        System.out.println("User: print request");

        addBehaviour(new AchieveREInitiator(this, request) {

            private static final long serialVersionUID = 4937763137251180518L;

            @Override
            protected void handleInform(ACLMessage inform) {
                System.out.println("User: print-request-accepted");
            }
        });

    }
}
