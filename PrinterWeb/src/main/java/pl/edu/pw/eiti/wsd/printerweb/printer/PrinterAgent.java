package pl.edu.pw.eiti.wsd.printerweb.printer;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

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
import pl.edu.pw.eiti.wsd.printerweb.printer.PrinterSelector.PrinterOffer;
import pl.edu.pw.eiti.wsd.printerweb.printer.document.Document;
import pl.edu.pw.eiti.wsd.printerweb.printer.document.DocumentStatus;
import pl.edu.pw.eiti.wsd.printerweb.printer.document.PaperFormat;
import pl.edu.pw.eiti.wsd.printerweb.printer.driver.PrinterDriver;
import pl.edu.pw.eiti.wsd.printerweb.printer.driver.PrinterDriver.PrinterEvent;
import pl.edu.pw.eiti.wsd.printerweb.printer.driver.PrinterDriver.PrinterInfo;
import pl.edu.pw.eiti.wsd.printerweb.printer.driver.PrinterDriver.PrinterListener;
import pl.edu.pw.eiti.wsd.printerweb.printer.driver.PrinterDriverImpl;
import pl.edu.pw.eiti.wsd.printerweb.printer.rank.PrinterRankCalculator;

/**
 * Agent which is responsible for interaction with {@link pl.edu.pw.eiti.wsd.printerweb.user.PrintAgent UserAgent}. Provides access to printers.
 *
 */
public class PrinterAgent extends Agent implements PrinterListener {

    private static final long serialVersionUID = 6504683624380808507L;

    private final Map<String, ACLMessage> scheduledTasks = new HashMap<>();

    private PrinterDriver printerDriver;

    private boolean crashed;

    @Override
    protected void setup() {
        printerDriver = new PrinterDriverImpl(new PrinterInfoImpl(getName()));
        printerDriver.addListener(this);
        addBehaviour(createManagerRequestServer());
        addBehaviour(new PrintRequestFromUserServerBehaviour(this));
        addBehaviour(new PrintRequestFromManagerServerBehaviour(this, printerDriver));
    }

    private static final class PrintRequestFromManagerServerBehaviour extends ContractNetResponder {

        private static final long serialVersionUID = -6876826051285116260L;

        private final PrinterAgent printerAgent;

        private final PrinterDriver printerDriver;

        public PrintRequestFromManagerServerBehaviour(PrinterAgent printerAgent, PrinterDriver printerDriver) {
            super(printerAgent, createMessageTemplate());
            this.printerAgent = printerAgent;
            this.printerDriver = printerDriver;
        }

        private static MessageTemplate createMessageTemplate() {
            MessageTemplate mt = ContractNetResponder.createMessageTemplate(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
            return MessageTemplate.and(mt, MessageTemplate.not(MessageTemplate.MatchContent("PrintManagerRequest")));
        }

        @Override
        protected ACLMessage handleCfp(ACLMessage cfp) throws RefuseException, FailureException, NotUnderstoodException {
            ACLMessage reply = cfp.createReply();

            if (printerAgent.isCrashed()) {
                throw new RefuseException("Drukarka niesprawna!");
            }

            try {
                Thread.sleep((System.currentTimeMillis() % 5) * 500);
                reply.setPerformative(ACLMessage.PROPOSE);
                reply.setContentObject(printerDriver.getInfo());
                return reply;
            } catch (IOException | InterruptedException e) {
                reply.setPerformative(ACLMessage.FAILURE);
                return reply;
            }
        }

        @Override
        protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) throws FailureException {
            try {
                Thread.sleep((System.currentTimeMillis() % 5) * 500);
                Document document = (Document) accept.getContentObject();
                String id = printerDriver.addToQueue(document);
                ACLMessage response;
                if (id != null) {
                    printerAgent.scheduledTasks.put(id, accept);
                    response = printerAgent.createConfirmation(id, accept);
                } else {
                    response = printerAgent.createRefusal(accept);
                }

                return response;
            } catch (UnreadableException | FailureException | InterruptedException e) {
                throw new FailureException(e.getMessage());
            }
        }
    }

    private static final class PrintRequestFromUserServerBehaviour extends AchieveREResponder {

        private static final long serialVersionUID = 2095424768575958579L;

        private final PrinterAgent printerAgent;

        private final PrinterSelector selector = new PrinterSelector(new PrinterRankCalculator());

        private final PrintersMap map = new PrintersMap();

        private final LocationProvider locationProvider = new LocationProvider();

        public PrintRequestFromUserServerBehaviour(PrinterAgent printerAgent) {
            super(printerAgent, AchieveREResponder.createMessageTemplate(FIPANames.InteractionProtocol.FIPA_REQUEST));
            this.printerAgent = printerAgent;
        }

        @Override
        protected ACLMessage handleRequest(ACLMessage request) throws NotUnderstoodException, RefuseException {
            try {
                DocumentWrapper documentWrapper = new DocumentWrapper((Document) request.getContentObject());
                documentWrapper.setRequestorConversationId(request.getConversationId());
                documentWrapper.setRequestorId(request.getSender());

                myAgent.addBehaviour(new PrintDocumentBehaviour(printerAgent, map, locationProvider, selector, documentWrapper));

                ACLMessage reply = request.createReply();
                reply.setPerformative(ACLMessage.AGREE);
                return reply;
            } catch (UnreadableException e) {
                throw new NotUnderstoodException(e.getMessage());
            }
        }

        @Override
        protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) throws FailureException {
            response.setPerformative(ACLMessage.INFORM);
            return response;
        }
    }

    private static final class PrintDocumentBehaviour extends FSMBehaviour {

        private static final long serialVersionUID = 4248496606864447021L;

        public PrintDocumentBehaviour(PrinterAgent myAgent, PrintersMap map, LocationProvider locationProvider,
                PrinterSelector selector, DocumentWrapper documentWrapper) {
            super(myAgent);

            registerDefaultTransition(State.PRINT_ORDER, State.INFORM_FAILED);
            registerTransition(State.PRINT_ORDER, State.WAITS_IN_PRINTER, Event.SUCCEED);

            registerDefaultTransition(State.WAITS_IN_PRINTER, State.INFORM_FAILED);
            registerTransition(State.WAITS_IN_PRINTER, State.WAIT_FOR_STATUS, Event.SUCCEED);

            registerDefaultTransition(State.WAIT_FOR_STATUS, State.INFORM_FAILED);
            registerTransition(State.WAIT_FOR_STATUS, State.INFORM_SUCCEED, Event.SUCCEED);

            registerFirstState(new PrintOrderBehaviour(myAgent, map, locationProvider, selector, documentWrapper),
                    State.PRINT_ORDER);
            registerState(new InformBehaviour(myAgent, documentWrapper, DocumentStatus.WAITS_IN_PRINTER_QUEUE),
                    State.WAITS_IN_PRINTER);
            registerState(new WaitForDocStatusChangesBehaviour(myAgent, documentWrapper), State.WAIT_FOR_STATUS);
            registerLastState(new InformFailedBehaviour(myAgent, documentWrapper, DocumentStatus.FAILED), State.INFORM_FAILED);
            registerLastState(new InformBehaviour(myAgent, documentWrapper, DocumentStatus.PRINTED), State.INFORM_SUCCEED);
        }

        private static final class State {

            private static final String PRINT_ORDER = "Print-order";

            private static final String WAITS_IN_PRINTER = "Waits-in-printer";

            private static final String WAIT_FOR_STATUS = "Wait-for-status";

            private static final String INFORM_SUCCEED = "Inform-success";

            private static final String INFORM_FAILED = "Inform-failed";
        }

        private static final class Event {

            private static final int FAILED = 0;

            private static final int SUCCEED = 1;

            public static final int WAITS_IN_PRINTER = 2;
        }

        private static final class PrintOrderBehaviour extends ContractNetInitiator {

            private static final long serialVersionUID = 3564655341122972899L;

            private final PrintersMap map;

            private final LocationProvider locationProvider;

            private final PrinterSelector selector;

            private final DocumentWrapper documentWrapper;

            private int exitStatus = Event.FAILED;

            public PrintOrderBehaviour(Agent myAgent, PrintersMap map, LocationProvider locationProvider,
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

                documentWrapper.setStatusInfo("Brak dostępnych drukarek w pobliżu!");
                exitStatus = Event.FAILED;
                return null;
            }

            @Override
            protected void handleAllResponses(Vector responses, Vector acceptances) {
                if (responses.isEmpty()) {
                    documentWrapper.setStatusInfo("Żadna drukarka nie odpowiada!");
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
                        ACLMessage reply = response.createReply();
                        reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
                        acceptances.add(reply);
                    }
                }

                try {
                    AID aid = selector.selectOffer(documentWrapper.getDocument(), offers);
                    if (aid != null) {
                        ACLMessage reply = msgBySender.remove(aid).createReply();
                        reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                        reply.setContentObject(documentWrapper.getDocument());
                        acceptances.add(reply);
                    } else {
                        documentWrapper.setStatusInfo("Nie można wybrac oferty!");
                        exitStatus = Event.FAILED;
                    }
                } catch (IOException e) {
                    documentWrapper.setStatusInfo("Bład podczas przesylania pliku do drukarki!");
                    exitStatus = Event.FAILED;
                }

                for (ACLMessage offer : msgBySender.values()) {
                    ACLMessage reply = offer.createReply();
                    reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                    acceptances.add(reply);
                }
            }

            @Override
            protected void handleRefuse(ACLMessage refuse) {
                documentWrapper.setStatusInfo("Drukarka nie odpowiada!");
                exitStatus = Event.FAILED;
            }

            @Override
            protected void handleInform(ACLMessage inform) {
                documentWrapper.setExecutorConversationId(inform.getConversationId());
                documentWrapper.setExecutorId(inform.getSender());
                documentWrapper.setStatusInfo("Dodano do kolejki drukarki: " + inform.getSender().getName());
                exitStatus = Event.SUCCEED;
            }

            @Override
            public int onEnd() {
                return exitStatus;
            }
        }

        private static final class WaitForDocStatusChangesBehaviour extends Behaviour {

            private static final long serialVersionUID = 4673059172599989088L;

            private final PrinterAgent printerAgent;

            private final DocumentWrapper documentWrapper;

            private int exitStatus = Event.WAITS_IN_PRINTER;

            public WaitForDocStatusChangesBehaviour(PrinterAgent printerAgent, DocumentWrapper documentWrapper) {
                super(printerAgent);
                this.printerAgent = printerAgent;
                this.documentWrapper = documentWrapper;
            }

            @Override
            public void action() {
                MessageTemplate matchConversation = MessageTemplate
                        .MatchConversationId(documentWrapper.getExecutorConversationId());
                MessageTemplate mt = MessageTemplate.and(matchConversation,
                        MessageTemplate.MatchSender(documentWrapper.getExecutorId()));

                ACLMessage msg = printerAgent.receive(mt);
                if (msg != null) {
                    if (msg.getPerformative() == ACLMessage.INFORM) {
                        DocumentStatus status = DocumentStatus.valueOf(msg.getContent());
                        switch (status) {
                            case FAILED:
                                exitStatus = Event.FAILED;
                                documentWrapper.setStatusInfo("Bład podczas drukowania!");
                                break;
                            case PRINTED:
                                documentWrapper.setStatusInfo("Gotowy do odbioru!");
                                exitStatus = Event.SUCCEED;
                                break;
                            case PRINTING:
                                ACLMessage reqMsg = new ACLMessage(ACLMessage.INFORM);
                                reqMsg.setConversationId(documentWrapper.getRequestorConversationId());
                                reqMsg.addReceiver(documentWrapper.getRequestorId());
                                reqMsg.setContent(status.name() + ";W trakcie drukowania...");
                                myAgent.send(reqMsg);
                                break;
                            default:
                                break;
                        }
                    } else {
                        exitStatus = Event.FAILED;
                        return;
                    }
                } else {
                    block();
                }
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

            private static final long serialVersionUID = -5271369978257311237L;

            private final DocumentWrapper documentWrapper;

            private final DocumentStatus status;

            public InformFailedBehaviour(Agent a, DocumentWrapper documentWrapper, DocumentStatus status) {
                super(a);
                this.documentWrapper = documentWrapper;
                this.status = status;
            }

            @Override
            public void action() {
                ACLMessage msg = new ACLMessage(ACLMessage.FAILURE);
                msg.setConversationId(documentWrapper.getRequestorConversationId());
                msg.addReceiver(documentWrapper.getRequestorId());
                msg.setContent(status.name() + ";" + documentWrapper.getStatusInfo());
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
                msg.setConversationId(documentWrapper.getRequestorConversationId());
                msg.addReceiver(documentWrapper.getRequestorId());
                msg.setContent(status.name() + ";" + documentWrapper.getStatusInfo());
                myAgent.send(msg);
            }

            @Override
            public int onEnd() {
                return Event.SUCCEED;
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
                try {
                    Thread.sleep((System.currentTimeMillis() % 5) * 500);
                    ACLMessage reply = cfp.createReply();
                    reply.setPerformative(ACLMessage.PROPOSE);
                    return reply;
                } catch (InterruptedException e) {
                    throw new FailureException(e.getMessage());
                }
            }

            @Override
            protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept)
                    throws FailureException {
                try {
                    Thread.sleep((System.currentTimeMillis() % 5) * 500);
                    ACLMessage reply = accept.createReply();
                    reply.setPerformative(ACLMessage.INFORM);
                    return reply;
                } catch (InterruptedException e) {
                    throw new FailureException(e.getMessage());
                }
            }
        };
    }

    public void errorOccured() {
        this.setCrashed(true);
    }

    public void readyToWork() {
        this.setCrashed(false);
    }

    // TODO load from config
    private static class PrinterInfoImpl implements PrinterInfo {

        private final String name;

        public PrinterInfoImpl(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public PrinterType getPrinterType() {
            return PrinterType.COLOR;
        }

        @Override
        public int getPrinterColorEfficiency() {
            return (int) (System.currentTimeMillis() % 200) * 8;
        }

        @Override
        public int getPrinterBlackEfficiency() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public int getResolution() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public Set<PaperFormat> getSupportedPaperFormats() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public boolean isDoubleSidedSupported() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public int paperContainerCapacity() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public int paperContainerActualCapacity() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public boolean isColorSupported() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public int currentQueueLength() {
            // TODO Auto-generated method stub
            return 0;
        }
    }

    private static final class DocumentWrapper {

        private Document document;

        private String requestorConversationId;

        private AID requestorId;

        private String executorConversationId;

        private AID executorId;

        private String statusInfo;

        public DocumentWrapper(Document document) {
            this.document = document;
        }

        public Document getDocument() {
            return document;
        }

        public AID getRequestorId() {
            return requestorId;
        }

        public void setRequestorId(AID conversationPartner) {
            this.requestorId = conversationPartner;
        }

        public void setRequestorConversationId(String conversationId) {
            this.requestorConversationId = conversationId;
        }

        public String getRequestorConversationId() {
            return requestorConversationId;
        }

        public String getExecutorConversationId() {
            return executorConversationId;
        }

        public void setExecutorConversationId(String executorConversationId) {
            this.executorConversationId = executorConversationId;
        }

        public AID getExecutorId() {
            return executorId;
        }

        public void setExecutorId(AID selectedPrinter) {
            this.executorId = selectedPrinter;
        }

        public String getStatusInfo() {
            return statusInfo;
        }

        public void setStatusInfo(String statusInfo) {
            this.statusInfo = statusInfo;
        }
    }

    @Override
    public void listen(PrinterEvent event) {
        switch (event.getType()) {
            case CRASHED:
            case NO_INK:
            case NO_PAPER:
                addBehaviour(new OneShotBehaviour(this) {

                    private static final long serialVersionUID = -1075560137030711767L;

                    @Override
                    public void action() {
                        for (Entry<String, ACLMessage> entry : scheduledTasks.entrySet()) {
                            myAgent.addBehaviour(new OneShotBehaviour() {

                                private static final long serialVersionUID = -435171932434060891L;

                                @Override
                                public void action() {
                                    ACLMessage failure = createFailure(entry.getKey(), entry.getValue());
                                    myAgent.send(failure);
                                }
                            });
                        }

                        errorOccured();
                    }
                });

                break;
            case PRINTING:
                addBehaviour(new OneShotBehaviour() {

                    private static final long serialVersionUID = -435171932434060891L;

                    @Override
                    public void action() {
                        ACLMessage request = scheduledTasks.get(event.getValue());
                        ACLMessage info = request.createReply();
                        info.setPerformative(ACLMessage.INFORM);
                        info.setContent(DocumentStatus.PRINTING.name());
                        myAgent.send(info);
                    }
                });
                break;
            case PRINTED:
                addBehaviour(new OneShotBehaviour() {

                    private static final long serialVersionUID = -435171932434060891L;

                    @Override
                    public void action() {
                        ACLMessage request = scheduledTasks.remove(event.getValue());
                        ACLMessage info = createDocumentPrintedInfo(event.getValue(), request);
                        myAgent.send(info);
                    }
                });
                break;
            case READY:
                addBehaviour(new OneShotBehaviour() {

                    private static final long serialVersionUID = -435171932434060891L;

                    @Override
                    public void action() {
                        readyToWork();
                    }
                });
                break;
            default:
                break;
        }
    }

    private ACLMessage createRefusal(ACLMessage msg) {
        ACLMessage refusal = msg.createReply();
        refusal.setPerformative(ACLMessage.REFUSE);
        return refusal;
    }

    private ACLMessage createConfirmation(String id, ACLMessage msg) {
        ACLMessage confirmation = msg.createReply();
        confirmation.setSender(getAID());
        confirmation.setPerformative(ACLMessage.INFORM);
        confirmation.setContent(DocumentStatus.WAITS_IN_PRINTER_QUEUE.name());
        return confirmation;
    }

    private ACLMessage createFailure(String value, ACLMessage request) {
        ACLMessage failure = request.createReply();
        failure.setPerformative(ACLMessage.FAILURE);
        failure.setContent(DocumentStatus.FAILED.name());
        return failure;
    }

    private ACLMessage createDocumentPrintedInfo(String value, ACLMessage request) {
        ACLMessage info = request.createReply();
        info.setPerformative(ACLMessage.INFORM);
        info.setContent(DocumentStatus.PRINTED.name());
        return info;
    }

    public boolean isCrashed() {
        return crashed;
    }

    public void setCrashed(boolean crashed) {
        this.crashed = crashed;
    }
}

// TODO convert to behaviors
// public class ExecutorRoleImpl implements ExecutorRole, PrinterListener {
//
// private final PrinterDriver printerDriver;
//
// private final Map<String, ACLMessage> scheduledTasks = new ConcurrentHashMap<>();
//
// private final PrinterAgent agent;
//
// /**
// * @param printerDriver
// * Printer driver which is used to communicate with the printer which is controlled by this instance. Not null.
// */
// public ExecutorRoleImpl(final PrinterAgent agent, final PrinterDriver printerDriver) {
// this.agent = agent;
// this.printerDriver = printerDriver;
// printerDriver.addListener(this);
// }
//
// @Override
// public ACLMessage handleAcceptProposal(ACLMessage msg) {
// System.out.println("Executor: proposal accepted, print");
// try {
// Document document = (Document) msg.getContentObject();
// String id = printerDriver.addToQueue(document);
// ACLMessage response;
// if (id != null) {
// scheduledTasks.put(id, msg);
// response = createConfirmation(id, msg);
// } else {
// response = createRefusal(msg);
// }
//
// return response;
// } catch (UnreadableException | FailureException e) {
// throw new RuntimeException(e); // TODO respond not understood
// }
// }
//

//

// }
