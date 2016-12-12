package pl.edu.pw.eiti.wsd.printerweb.printer;

import jade.core.Agent;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.AchieveREResponder;
import jade.proto.ContractNetResponder;
import pl.edu.pw.eiti.wsd.printerweb.printer.driver.PrinterDriver.PrinterInfo;
import pl.edu.pw.eiti.wsd.printerweb.printer.driver.PrinterDriverImpl;

/**
 * Agent which is responsible for interaction with {@link pl.edu.pw.eiti.wsd.printerweb.user.UserAgent UserAgent}. Provides access to printers.
 *
 */
public class PrinterAgent extends Agent {

    private static final long serialVersionUID = 6504683624380808507L;

    @Override
    protected void setup() {
        addBehaviour(createContractNetServer());
        addBehaviour(createPrintRequestServer());
    }

    private ContractNetResponder createContractNetServer() {
        MessageTemplate mt = ContractNetResponder.createMessageTemplate(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
        return new ContractNetResponder(this, mt) {

            private static final long serialVersionUID = -5089076528343302500L;

            private final NegotiatorRole negotiator = new NegotiatorRoleImpl();

            private final ExecutorRole executor = new ExecutorRoleImpl(PrinterAgent.this, new PrinterDriverImpl(new PrinterInfoImpl()));

            @Override
            protected ACLMessage handleCfp(ACLMessage cfp) throws RefuseException, FailureException, NotUnderstoodException {
                return negotiator.handleProposal(cfp);
            }

            @Override
            protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept)
                    throws FailureException {
                return executor.handleAcceptProposal(accept);
            }
        };
    }

    private AchieveREResponder createPrintRequestServer() {
        MessageTemplate mt = AchieveREResponder.createMessageTemplate(FIPANames.InteractionProtocol.FIPA_REQUEST);

        return new AchieveREResponder(this, mt) {

            private final PrinterManagerRole manager = new PrinterManagerRoleImpl(myAgent);

            private static final long serialVersionUID = 2095424768575958579L;

            @Override
            protected ACLMessage handleRequest(ACLMessage request) throws NotUnderstoodException, RefuseException {
                System.out.println("print request received");
                try {
                    return manager.handlePrintRequest(request);
                } catch (UnreadableException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    public void errorOccured() {
        System.out.println("PrinterAgent: error occured!");
    }

    public void readyToWork() {
        System.out.println("PrinterAgent: ready to work!");
    }
    
    private static class PrinterInfoImpl implements PrinterInfo {

        @Override
        public String getName() {
            return "name";
        }
        
    }
}
