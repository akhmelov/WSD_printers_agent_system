package pl.edu.pw.eiti.wsd.printerweb.printer;

import java.io.IOException;
import java.util.Vector;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetInitiator;
import pl.edu.pw.eiti.wsd.printerweb.printer.document.Document;

public class PrinterManagerRoleImpl implements PrinterManagerRole {

    private Agent printerAgent;

    public PrinterManagerRoleImpl(Agent printerAgent) {
        this.printerAgent = printerAgent;
    }

    @Override
    public ACLMessage handlePrintRequest(ACLMessage request) throws UnreadableException {
        AID aid = new AID("negotiator", false);

        ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
        cfp.addReceiver(aid);
        cfp.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);

        printerAgent.addBehaviour(new PrintRequestInitiator(printerAgent, (Document) request.getContentObject(), cfp));

        ACLMessage reply = request.createReply();
        reply.setPerformative(ACLMessage.INFORM);

        return reply;
    }

    private static final class PrintRequestInitiator extends ContractNetInitiator {

        private static final long serialVersionUID = 3564655341122972899L;

        private final Document document;

        private PrintRequestInitiator(Agent a, Document document, ACLMessage cfp) {
            super(a, cfp);
            this.document = document;
        }

        @Override
        protected Vector prepareCfps(ACLMessage cfp) {
            System.out.println("PrinterManager: prepare CFPs");
            return super.prepareCfps(cfp);
        }

        @Override
        protected void handlePropose(ACLMessage propose, Vector acceptances) {
            try {
                ACLMessage reply = propose.createReply();
                reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                reply.setContentObject(document);

                System.out.println("PrinterManager: proposal received, accept");
                acceptances.add(reply);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected void handleRefuse(ACLMessage refuse) {
            System.out.println("PrinterManager: refused");
        }

        @Override
        protected void handleInform(ACLMessage inform) {
            System.out.println("PrinterManager: inform " + inform.getContent());
        }
    }
}
