package pl.edu.pw.eiti.wsd.printerweb.printer;

import jade.core.Agent;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetResponder;
import pl.edu.pw.eiti.wsd.printerweb.printer.driver.PrinterDriverImpl;

/**
 * Agent which is responsible for interaction with {@link pl.edu.pw.eiti.wsd.printerweb.user.UserAgent UserAgent}. Provides access to printers.
 *
 */
public class PrinterAgent extends Agent {

    private static final long serialVersionUID = 6504683624380808507L;

    @Override
    protected void setup() {
        MessageTemplate mt = ContractNetResponder.createMessageTemplate(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
        addBehaviour(new ContractNetResponder(this, mt) {

            private static final long serialVersionUID = -5089076528343302500L;
            
            private final NegotiatorRole negotiator = new NegotiatorRoleImpl();
            
            private final ExecutorRole executor = new ExecutorRoleImpl(new PrinterDriverImpl());

            @Override
            protected ACLMessage handleCfp(ACLMessage cfp) throws RefuseException, FailureException, NotUnderstoodException {
                return negotiator.handleProposal(cfp);
            }

            @Override
            protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept)
                    throws FailureException {
                return executor.handleAcceptProposal(cfp);
            }
        });
    }

    @Override
    protected void takeDown() {
        // TODO Auto-generated method stub
        super.takeDown();
    }
}
