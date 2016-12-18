package pl.edu.pw.eiti.wsd.printerweb.printer;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.FSMBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.AchieveREResponder;
import jade.proto.ContractNetInitiator;
import jade.proto.ContractNetResponder;
import pl.edu.pw.eiti.wsd.printerweb.printer.driver.PrinterDriver.PrinterEvent;
import pl.edu.pw.eiti.wsd.printerweb.printer.driver.PrinterDriver.PrinterInfo;
import pl.edu.pw.eiti.wsd.printerweb.printer.driver.PrinterDriver.PrinterListener;
import pl.edu.pw.eiti.wsd.printerweb.user.UserAgent;
import pl.edu.pw.eiti.wsd.printerweb.user.gui.UserController;
import pl.edu.pw.eiti.wsd.printerweb.printer.PrinterSelector.PrinterOffer;
import pl.edu.pw.eiti.wsd.printerweb.printer.document.Document;
import pl.edu.pw.eiti.wsd.printerweb.printer.document.DocumentStatus;
import pl.edu.pw.eiti.wsd.printerweb.printer.driver.PrinterDriver;
import pl.edu.pw.eiti.wsd.printerweb.printer.driver.PrinterDriverImpl;

/**
 * Agent which is responsible for interaction with {@link pl.edu.pw.eiti.wsd.printerweb.user.UserAgent UserAgent}. Provides access to printers.
 *
 */
public class PrinterAgent extends Agent {

    private static final long serialVersionUID = 6504683624380808507L;

    @Override
    protected void setup() {
        addBehaviour(createManagerRequestServer());
        addBehaviour(new PrintRequestServerBehaviour(this));
//        addBehaviour(createContractNetServer());
    }

//    private ContractNetResponder createContractNetServer() {
//        MessageTemplate mt = ContractNetResponder.createMessageTemplate(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
//        mt = MessageTemplate.and(mt, MessageTemplate.not(MessageTemplate.MatchContent("PrintManagerRequest")));
//
//        return new ContractNetResponder(this, mt) {
//
//            private static final long serialVersionUID = -5089076528343302500L;
//
//            private final NegotiatorRole negotiator = new NegotiatorRoleImpl();
//
//            private final ExecutorRole executor = new ExecutorRoleImpl(PrinterAgent.this,
//                    new PrinterDriverImpl(new PrinterInfoImpl(myAgent.getName())));
//
//            @Override
//            protected ACLMessage handleCfp(ACLMessage cfp) throws RefuseException, FailureException, NotUnderstoodException {
//                System.out.println("PrinterAgent: Handle ask for proposal");
//                return negotiator.handleProposal(cfp);
//            }
//
//            @Override
//            protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept)
//                    throws FailureException {
//                System.out.println("PrinterAgent: Handle accept proposal");
//                return executor.handleAcceptProposal(accept);
//            }
//        };
//    }

    private static final class PrintRequestServerBehaviour extends AchieveREResponder {

        private static final long serialVersionUID = 2095424768575958579L;

        private final PrinterSelector selector = new PrinterSelector();

        private final PrintersMap map = new PrintersMap();

        private final LocationProvider locationProvider = new LocationProvider();

        public PrintRequestServerBehaviour(Agent a) {
            super(a, AchieveREResponder.createMessageTemplate(FIPANames.InteractionProtocol.FIPA_REQUEST));
        }

        @Override
        protected ACLMessage handleRequest(ACLMessage request) throws NotUnderstoodException, RefuseException {
            System.out.println("PrinterAgent: Handle print request");
            try {
                DocumentWrapper documentWrapper = new DocumentWrapper((Document) request.getContentObject());
                documentWrapper.setConversationId(request.getConversationId());
                documentWrapper.setConversationPartner(request.getSender());

                myAgent.addBehaviour(new PrintDocumentBehaviour(myAgent, map, locationProvider, selector, documentWrapper));

                ACLMessage reply = request.createReply();
                reply.setPerformative(ACLMessage.AGREE);
                return reply;
            } catch (UnreadableException e) {
                throw new NotUnderstoodException(e.getMessage());
            }
        }

        @Override
        protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) throws FailureException {
            ACLMessage reply = request.createReply();
            reply.setPerformative(ACLMessage.AGREE);
            return reply;
        }
    }

    private static final class PrintDocumentBehaviour extends FSMBehaviour {

        public PrintDocumentBehaviour(Agent myAgent, PrintersMap map, LocationProvider locationProvider, PrinterSelector selector,
                DocumentWrapper documentWrapper) {
            super(myAgent);

            registerState(new PrintOrderBehaviour(myAgent, map, locationProvider, selector, documentWrapper), State.PRINT_ORDER);
        }

        private static final class State {

            private static final String PRINT_ORDER = "Print-order";

            private static final String INFORM_SUCCESS = "Inform-success";

            private static final String INFORM_FAILED = "Inform-failed";
        }

        private static final class Event {

            private static final int FAILED = 0;

            private static final int SUCCEED = 1;
        }

        private static final class PrintOrderBehaviour extends ContractNetInitiator {

            private static final long serialVersionUID = 3564655341122972899L;

            private final PrintersMap map;

            private final LocationProvider locationProvider;

            private final PrinterSelector selector;

            private final DocumentWrapper documentWrapper;
            
            private int exitStatus = Event.FAILED;

            private PrintOrderBehaviour(Agent myAgent, PrintersMap map, LocationProvider locationProvider,
                    PrinterSelector selector, DocumentWrapper documentWrapper) {
                super(myAgent, new ACLMessage(ACLMessage.CFP));
                this.map = map;
                this.locationProvider = locationProvider;
                this.documentWrapper = documentWrapper;
                this.selector = selector;
            }

            @Override
            protected Vector prepareCfps(ACLMessage cfp) {
                Set<AID> printersNearby = map.getPrintersNearby(locationProvider.getCurrentLocation());
                if (!printersNearby.isEmpty()) {
                    printersNearby.forEach(printer -> {
                        if (!printer.equals(myAgent.getAID()))
                            cfp.addReceiver(printer);
                    });

                    cfp.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
                    return super.prepareCfps(cfp);
                }

                exitStatus = Event.FAILED;
                return null;
            }

            @Override
            protected void handleAllResponses(Vector responses, Vector acceptances) {
                if(responses.isEmpty()) {
                    exitStatus = Event.FAILED;
                    return;
                }
                
                Map<AID, ACLMessage> msgBySender = new HashMap<>();
                Set<PrinterOffer> offers = new HashSet<>();
                for (ACLMessage response : (Vector<ACLMessage>) responses) {
                    try {
                        offers.add(new PrinterOffer(response.getSender(), (PrinterInfo) response.getContentObject()));
                        msgBySender.put(response.getSender(), response);
                    } catch (UnreadableException e) {
                        e.printStackTrace(); // TODO respond with not understood
                    }
                }

                try {
                    AID aid = selector.selectOffer(offers);
                    ACLMessage reply = msgBySender.remove(aid).createReply();
                    reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                    reply.setContentObject(documentWrapper.getDocument());
                    System.out.println("PrinterManager: proposal received, accept");
                    acceptances.add(reply);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                for (ACLMessage offer : msgBySender.values()) {
                    ACLMessage reply = offer.createReply();
                    reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                    acceptances.add(reply);
                }
            }

            @Override
            protected void handleRefuse(ACLMessage refuse) {
                exitStatus = Event.FAILED;
            }

            @Override
            protected void handleInform(ACLMessage inform) {
                exitStatus = Event.SUCCEED;
            }

            @Override
            public int onEnd() {
                return exitStatus;
            }
        }
        
        private static final class WaitForDocStatusChangesBehaviour extends Behaviour {

            private static final long serialVersionUID = 4673059172599989088L;

            private final UserAgent userAgent;
            
            private final DocumentWrapper documentWrapper;
            
            private final MessageTemplate mt;

            private int exitStatus = Event.FAILED;

            public WaitForDocStatusChangesBehaviour(UserAgent userAgent, DocumentWrapper documentWrapper) {
                super(userAgent);
                this.userAgent = userAgent;
                this.documentWrapper = documentWrapper;
                
                MessageTemplate matchConversation = MessageTemplate.MatchConversationId(documentWrapper.getConversationId());
                this.mt = MessageTemplate.and(matchConversation, MessageTemplate.MatchSender(documentWrapper.getConversationPartner()));
            }
            
            @Override
            public void action() {
                ACLMessage msg = userAgent.receive(mt);
                if(msg != null) {
                    if(msg.getPerformative() == ACLMessage.INFORM) {
                        DocumentStatus status = DocumentStatus.valueOf(msg.getContent());
                        switch(status) {
                            case FAILED:
                                exitStatus = Event.FAILED;
                                break;
                            case PRINTED:
                                exitStatus = Event.SUCCEED;
                                //$FALL-THROUGH$
                            case LOADED:
                            case PRINTING:
                            case WAITS_IN_MANAGER_QUEUE:
                            case WAITS_IN_PRINTER_QUEUE:
                                break;
                            default:
                        }
                    } else {
                        exitStatus = Event.FAILED;
                        return;
                    }
                }
                
                block();
            }

            @Override
            public boolean done() {
                return exitStatus == Event.FAILED || exitStatus == Event.SUCCEED;
            }

            @Override
            public int onEnd() {
                return exitStatus;
            }
        }

        private static final class InformFailedBehaviour extends OneShotBehaviour {

            private static final long serialVersionUID = -1684688589310085659L;

            private final DocumentWrapper documentWrapper;

            public InformFailedBehaviour(Agent a, DocumentWrapper documentWrapper) {
                super(a);
                this.documentWrapper = documentWrapper;
            }

            @Override
            public void action() {
                ACLMessage msg = new ACLMessage(ACLMessage.FAILURE);
                msg.setConversationId(documentWrapper.getConversationId());
                msg.addReceiver(documentWrapper.getConversationPartner());
                myAgent.send(msg);
            }
        }

        private static final class InformBehaviour extends OneShotBehaviour {

            private static final long serialVersionUID = -5271369978257311237L;

            private final DocumentWrapper documentWrapper;

            private final DocumentStatus status;

            public InformBehaviour(Agent a, DocumentWrapper documentWrapper, DocumentStatus status) {
                super(a);
                this.documentWrapper = documentWrapper;
                this.status = status;
            }

            @Override
            public void action() {
                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                msg.setConversationId(documentWrapper.getConversationId());
                msg.addReceiver(documentWrapper.getConversationPartner());
                msg.setContent(status.name());
                myAgent.send(msg);
            }
        }
    }

    private ContractNetResponder createManagerRequestServer() {
        MessageTemplate mt = AchieveREResponder.createMessageTemplate(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
        mt = MessageTemplate.and(mt, MessageTemplate.MatchContent("PrintManagerRequest"));

        return new ContractNetResponder(this, mt) {

            private static final long serialVersionUID = 2095424768575958579L;

            @Override
            protected ACLMessage handleCfp(ACLMessage cfp) throws RefuseException, FailureException, NotUnderstoodException {
                System.out.println("PrinterAgent: Handle ask for being printer manager");
                ACLMessage reply = cfp.createReply();
                reply.setPerformative(ACLMessage.PROPOSE);
                return reply;
            }

            @Override
            protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept)
                    throws FailureException {
                ACLMessage reply = accept.createReply();
                reply.setPerformative(ACLMessage.INFORM);
                return reply;
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

        private final String name;

        public PrinterInfoImpl(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

    }

    private static final class DocumentWrapper {

        private Document document;

        private String conversationId;

        private AID conversationPartner;

        public DocumentWrapper(Document document) {
            this.document = document;
        }

        public Document getDocument() {
            return document;
        }

        public AID getConversationPartner() {
            return conversationPartner;
        }

        public void setConversationPartner(AID conversationPartner) {
            this.conversationPartner = conversationPartner;
        }

        public void setConversationId(String conversationId) {
            this.conversationId = conversationId;
        }

        public String getConversationId() {
            return conversationId;
        }
    }
}

// TODO convert to behaviors 
//public class ExecutorRoleImpl implements ExecutorRole, PrinterListener {
//
//    private final PrinterDriver printerDriver;
//
//    private final Map<String, ACLMessage> scheduledTasks = new ConcurrentHashMap<>();
//
//    private final PrinterAgent agent;
//
//    /**
//     * @param printerDriver
//     *      Printer driver which is used to communicate with the printer which is controlled by this instance. Not null.
//     */
//    public ExecutorRoleImpl(final PrinterAgent agent, final PrinterDriver printerDriver) {
//        this.agent = agent;
//        this.printerDriver = printerDriver;
//        printerDriver.addListener(this);
//    }
//
//    @Override
//    public ACLMessage handleAcceptProposal(ACLMessage msg) {
//        System.out.println("Executor: proposal accepted, print");
//        try {
//            Document document = (Document) msg.getContentObject();
//            String id = printerDriver.addToQueue(document);
//            ACLMessage response;
//            if (id != null) {
//                scheduledTasks.put(id, msg);
//                response = createConfirmation(id, msg);
//            } else {
//                response = createRefusal(msg);
//            }
//
//            return response;
//        } catch (UnreadableException | FailureException e) {
//            throw new RuntimeException(e); // TODO respond not understood
//        }
//    }
//
//    private ACLMessage createRefusal(ACLMessage msg) {
//        ACLMessage refusal = msg.createReply();
//        refusal.setPerformative(ACLMessage.REFUSE);
//        return refusal;
//    }
//
//    private ACLMessage createConfirmation(String id, ACLMessage msg) {
//        ACLMessage confirmation = msg.createReply();
//        confirmation.setSender(agent.getAID());
//        confirmation.setPerformative(ACLMessage.INFORM);
//        confirmation.setContent(id);
//        return confirmation;
//    }
//
//    private ACLMessage createFailure(String value, ACLMessage request) {
//        ACLMessage failure = request.createReply();
//        failure.setPerformative(ACLMessage.FAILURE);
//        failure.setContent(value);
//        return failure;
//    }
//
//    private ACLMessage createDocumentPrintedInfo(String value, ACLMessage request) {
//        ACLMessage info = request.createReply();
//        info.setPerformative(ACLMessage.INFORM);
//        info.setContent(value);
//        return info;
//    }
//
//    @Override
//    public void listen(PrinterEvent event) {
//        switch (event.getType()) {
//            case CRASHED:
//            case NO_INK:
//            case NO_PAPER:
//                for (Entry<String, ACLMessage> entry : scheduledTasks.entrySet()) {
//                    agent.addBehaviour(new OneShotBehaviour() {
//                        
//                        private static final long serialVersionUID = -435171932434060891L;
//
//                        @Override
//                        public void action() {
//                            ACLMessage failure = createFailure(entry.getKey(), entry.getValue());
//                            myAgent.send(failure);
//                        }
//                    });
//                }
//                agent.errorOccured();
//                break;
//            case PRINTED:
//                agent.addBehaviour(new OneShotBehaviour() {
//                    
//                    private static final long serialVersionUID = -435171932434060891L;
//
//                    @Override
//                    public void action() {
//                        ACLMessage request = scheduledTasks.get(event.getValue());
//                        ACLMessage info = createDocumentPrintedInfo(event.getValue(), request);
//                        myAgent.send(info);
//                    }
//                });
//                break;
//            case READY:
//                agent.readyToWork();
//                break;
//            default:
//                break;
//        }
//    }
//}
