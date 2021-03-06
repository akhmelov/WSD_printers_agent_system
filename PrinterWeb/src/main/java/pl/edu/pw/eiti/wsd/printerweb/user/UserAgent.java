package pl.edu.pw.eiti.wsd.printerweb.user;

import java.io.IOException;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.FSMBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREInitiator;
import jade.proto.ContractNetInitiator;
import jade.util.Logger;
import pl.edu.pw.eiti.wsd.printerweb.printer.LocationProvider;
import pl.edu.pw.eiti.wsd.printerweb.printer.LocationProvider.Location;
import pl.edu.pw.eiti.wsd.printerweb.printer.PrintersMap;
import pl.edu.pw.eiti.wsd.printerweb.printer.document.Document;
import pl.edu.pw.eiti.wsd.printerweb.printer.document.DocumentStatus;
import pl.edu.pw.eiti.wsd.printerweb.user.gui.UserController;

/**
 * Agent which is responsible for interactions with {@link pl.edu.pw.eiti.wsd.printerweb.printer.PrinterAgent PrinterAgent}s on behalf of the user.
 */
public class UserAgent extends Agent {

    private static final long serialVersionUID = -3135506373065999424L;

    private final AtomicInteger idGenerator = new AtomicInteger(1);

    private final UserController gui = new UserController(this);

    private final PrintersMap map = new PrintersMap();

    private final LocationProvider locationProvider = new LocationProvider();

    private AID myPrinterManager = null;

    @Override
    protected void setup() {
        gui.show();
    }

    @Override
    protected void takeDown() {
        super.takeDown();

        gui.dispose();
    }

    /**
     * Prints provided document.
     * 
     * @param document
     *      Document which should be printed. Not null.
     * @return
     *      ID of a printing task assigned to provided document. It is further used to identify task statuses. Never null.
     */
    public String printDocument(Document document) {
        if (document == null) {
            throw new RuntimeException("Document cannot be null!");
        }

        String docId = Integer.toString(idGenerator.getAndIncrement());
        addBehaviour(new SchedulePrintingBehaviour(this, gui, map, docId, document));

        return docId;
    }

    /**
     * @return
     *      Current location of this agent. Never null.
     */
    public Location getCurrentLocation() {
        return locationProvider.getCurrentLocation();
    }

    /**
     * @return
     *      Agent ID which is a Printer Manager for this User agent. May be null.
     */
    AID getPrinterManager() {
        return myPrinterManager;
    }

    /**
     * @param myPrinterManager
     *      New Printer Manager of this User Agent. May be null. 
     */
    void setPrinterManager(AID myPrinterManager) {
        this.myPrinterManager = myPrinterManager;
    }

    private static class SchedulePrintingBehaviour extends FSMBehaviour {

        private static final long serialVersionUID = -4012165633409728255L;

        public SchedulePrintingBehaviour(UserAgent myUserAgent, UserController gui, PrintersMap map, String docId,
                Document document) {
            super(myUserAgent);

            DocumentWrapper documentWrapper = new DocumentWrapper(docId, document);

            registerDefaultTransition(State.CHECK_PRINTER_MANAGER, State.INFORM_FAILED);
            registerTransition(State.CHECK_PRINTER_MANAGER, State.REQUEST_PRINT_DOCUMENT, Event.PRINTER_MANAGER_SELECTED);
            registerTransition(State.CHECK_PRINTER_MANAGER, State.SELECT_PRINTER_MANAGER, Event.PRINTER_MANAGER_NOT_SELECTED);
            registerDefaultTransition(State.SELECT_PRINTER_MANAGER, State.INFORM_FAILED);
            registerTransition(State.SELECT_PRINTER_MANAGER, State.CHECK_PRINTER_MANAGER, Event.PRINTER_MANAGER_SELECTED);
            registerDefaultTransition(State.REQUEST_PRINT_DOCUMENT, State.INFORM_FAILED);
            registerTransition(State.REQUEST_PRINT_DOCUMENT, State.CHECK_PRINTER_MANAGER,
                    Event.NO_RESPONSE);
            registerTransition(State.REQUEST_PRINT_DOCUMENT, State.WAIT_FOR_DOC_STATUS_CHANGES,
                    Event.PRINTER_MANAGER_ACCEPTED_DOCUMENT);
            registerDefaultTransition(State.WAIT_FOR_DOC_STATUS_CHANGES, State.INFORM_FAILED);
            registerTransition(State.WAIT_FOR_DOC_STATUS_CHANGES, State.INFORM_SUCCEED, Event.SUCCEED);

            registerFirstState(new CheckPrinterManagerBehaviour(myUserAgent, gui), State.CHECK_PRINTER_MANAGER);
            registerState(new RequestDocumentPrintBehaviour(myUserAgent, gui, documentWrapper), State.REQUEST_PRINT_DOCUMENT);
            registerState(new SelectPrinterManagerBehaviour(myUserAgent, map, documentWrapper), State.SELECT_PRINTER_MANAGER);
            registerState(new WaitForDocStatusChangesBehaviour(myUserAgent, gui, documentWrapper),
                    State.WAIT_FOR_DOC_STATUS_CHANGES);
            registerLastState(new InformFailedBehaviour(myUserAgent, gui, documentWrapper), State.INFORM_FAILED);
            registerLastState(new InformSucceedBehaviour(myUserAgent, gui, documentWrapper), State.INFORM_SUCCEED);
        }

        private static final class State {

            private static final String CHECK_PRINTER_MANAGER = "CHECK_PRINTER_MANAGER";

            private static final String SELECT_PRINTER_MANAGER = "SELECT_PRINTER_MANAGER";

            private static final String REQUEST_PRINT_DOCUMENT = "REQUEST_PRINT_DOCUMENT";

            private static final String WAIT_FOR_DOC_STATUS_CHANGES = "WAIT_FOR_DOC_STATUS_CHANGES";

            private static final String INFORM_FAILED = "INFORM_REFUSED";

            private static final String INFORM_SUCCEED = "INFORM_SUCCEED";
        }

        private static final class Event {

            private static final int FAILED = 0;

            private static final int PRINTER_MANAGER_NOT_SELECTED = 1;

            private static final int PRINTER_MANAGER_SELECTED = 2;

            private static final int PRINTER_MANAGER_NOT_FOUND = 3;

            private static final int PRINTER_MANAGER_ACCEPTED_DOCUMENT = 4;

            public static final int SUCCEED = 5;

            public static final int NO_RESPONSE = 6;
        }

        private static class CheckPrinterManagerBehaviour extends OneShotBehaviour {

            private static final long serialVersionUID = 3785542403885865676L;

            private final UserAgent myUserAgent;

            private final UserController gui;

            private int exitStatus = Event.FAILED;

            public CheckPrinterManagerBehaviour(UserAgent myAgent, UserController gui) {
                super(myAgent);
                this.myUserAgent = myAgent;
                this.gui = gui;
            }

            @Override
            public void action() {
                if (isPrinterManagerSelected()) {
                    gui.addStatusInfo("Połączony z " + myUserAgent.getPrinterManager().getName()); // TODO do not hardcode display
                                                                                                   // strings
                    exitStatus = Event.PRINTER_MANAGER_SELECTED;
                } else {
                    gui.addStatusInfo("Rozłączony");
                    exitStatus = Event.PRINTER_MANAGER_NOT_SELECTED;
                }
            }

            private boolean isPrinterManagerSelected() {
                return myUserAgent.getPrinterManager() != null;
            }

            @Override
            public int onEnd() {
                return exitStatus;
            }
        }

        private static final class RequestDocumentPrintBehaviour extends AchieveREInitiator {

            private static final long serialVersionUID = 4937763137251180518L;

            private final Logger log = Logger.getJADELogger(getClass().getName());

            private final UserAgent myUserAgent;

            private final UserController gui;

            private final DocumentWrapper documentWrapper;

            private int exitStatus = Event.FAILED;

            public RequestDocumentPrintBehaviour(UserAgent myUserAgent, UserController gui, DocumentWrapper documentWrapper) {
                super(myUserAgent, new ACLMessage(ACLMessage.REQUEST));
                this.myUserAgent = myUserAgent;
                this.gui = gui;
                this.documentWrapper = documentWrapper;
            }

            @SuppressWarnings("rawtypes")
            @Override
            protected Vector prepareRequests(ACLMessage request) {
                try {
                    request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
                    request.setContentObject(documentWrapper.getDocument());
                    request.addReceiver(myUserAgent.getPrinterManager());

                    return super.prepareRequests(request);
                } catch (IOException e) {
                    log.log(Level.SEVERE, "Error while adding document {0} to message content: {1}",
                            new String[] { documentWrapper.getDocument().getFile().getName(), e.getMessage() });
                    exitStatus = Event.FAILED;
                    return null;
                }
            }

            @SuppressWarnings("unchecked") // it is known that Vector contains ACLMessage, just old Java implementation
            @Override
            protected void handleAllResponses(@SuppressWarnings("rawtypes") Vector responses) {
                ACLMessage agreement = null;
                if (!responses.isEmpty()) {
                    for (ACLMessage response : (Vector<ACLMessage>) responses) { // in fact there is one response
                        if (response.getPerformative() == ACLMessage.AGREE) {
                            agreement = response;
                            break;
                        }
                    }
                }

                if (agreement != null) {
                    gui.addDocumentStatusInfo(documentWrapper.getId(), DocumentStatus.WAITS_IN_MANAGER_QUEUE,
                            "Dodano do kolejki managera: " + agreement.getSender().getName());

                    documentWrapper.setConversationPartner(agreement.getSender());
                    documentWrapper.setConversationId(agreement.getConversationId());
                    exitStatus = Event.PRINTER_MANAGER_ACCEPTED_DOCUMENT;
                } else {
                    log.log(Level.SEVERE, "Printer manager did not agreed!");
                    exitStatus = Event.NO_RESPONSE;
                }
            }

            @Override
            public int onEnd() {
                return exitStatus;
            }
        }

        private static final class WaitForDocStatusChangesBehaviour extends Behaviour {

            private static final long serialVersionUID = 4673059172599989088L;

            private final UserAgent userAgent;

            private final UserController gui;

            private final DocumentWrapper documentWrapper;

            private int exitStatus = Event.PRINTER_MANAGER_ACCEPTED_DOCUMENT;

            public WaitForDocStatusChangesBehaviour(UserAgent userAgent, UserController gui, DocumentWrapper documentWrapper) {
                super(userAgent);
                this.userAgent = userAgent;
                this.gui = gui;
                this.documentWrapper = documentWrapper;
            }

            @Override
            public void action() {
                MessageTemplate matchConversation = MessageTemplate.MatchConversationId(documentWrapper.getConversationId());
                MessageTemplate mt = MessageTemplate.and(matchConversation,
                        MessageTemplate.MatchSender(documentWrapper.getConversationPartner()));

                ACLMessage msg = userAgent.receive(mt);
                if (msg != null) {
                    String content = msg.getContent();
                    String[] contents = content.split(";");

                    if (msg.getPerformative() == ACLMessage.INFORM) {
                        DocumentStatus status = DocumentStatus.valueOf(contents[0]);
                        switch (status) {
                            case FAILED:
                                documentWrapper.setStatusInfo(contents[1]);
                                exitStatus = Event.FAILED;
                                break;
                            case PRINTED:
                                documentWrapper.setStatusInfo(contents[1]);
                                exitStatus = Event.SUCCEED;
                                break;
                            case LOADED:
                            case PRINTING:
                            case WAITS_IN_MANAGER_QUEUE:
                            case WAITS_IN_PRINTER_QUEUE:
                                gui.addDocumentStatusInfo(documentWrapper.getId(), status, contents[1]);
                                break;
                            default:
                        }
                    } else {
                        documentWrapper.setStatusInfo(contents[1]);
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

            private static final long serialVersionUID = -1684688589310085659L;

            private final UserController gui;

            private final DocumentWrapper documentWrapper;

            public InformFailedBehaviour(Agent a, UserController gui, DocumentWrapper documentWrapper) {
                super(a);
                this.gui = gui;
                this.documentWrapper = documentWrapper;
            }

            @Override
            public void action() {
                gui.addDocumentStatusInfo(documentWrapper.getId(), DocumentStatus.FAILED,
                        "Bład drukowania: " + documentWrapper.getStatusInfo());
            }
        }

        private static final class InformSucceedBehaviour extends OneShotBehaviour {

            private static final long serialVersionUID = -5271369978257311237L;

            private final UserController gui;

            private final DocumentWrapper documentWrapper;

            public InformSucceedBehaviour(Agent a, UserController gui, DocumentWrapper documentWrapper) {
                super(a);
                this.gui = gui;
                this.documentWrapper = documentWrapper;
            }

            @Override
            public void action() {
                gui.addDocumentStatusInfo(documentWrapper.getId(), DocumentStatus.PRINTED, documentWrapper.getStatusInfo());
            }
        }

        private static final class SelectPrinterManagerBehaviour extends ContractNetInitiator {

            private static final long serialVersionUID = -8340963879184409208L;

            private final UserAgent myUserAgent;

            private final PrintersMap map;

            private int exitStatus = Event.PRINTER_MANAGER_NOT_FOUND;

            private final DocumentWrapper documentWrapper;

            public SelectPrinterManagerBehaviour(UserAgent myUserAgent, PrintersMap map, DocumentWrapper documentWrapper) {
                super(myUserAgent, new ACLMessage(ACLMessage.CFP));
                this.myUserAgent = myUserAgent;
                this.map = map;
                this.documentWrapper = documentWrapper;
            }

            @SuppressWarnings("rawtypes")
            @Override
            protected Vector prepareCfps(ACLMessage cfp) {
//                Set<AID> printersNearby = map.getPrintersNearby(myUserAgent.getCurrentLocation());

                Set<AID> printersNearby = map.getPrintersNearby(myUserAgent.getCurrentLocation(), myAgent
                        , documentWrapper.getDocument().getPaperFormat()
                        , documentWrapper.getDocument().getPrinterType(), 10);
                if (!printersNearby.isEmpty()) {
                    printersNearby.forEach(printer -> cfp.addReceiver(printer));
                    cfp.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
                    cfp.setContent("PrintManagerRequest"); // TODO public constant

                    return super.prepareCfps(cfp);
                }

                exitStatus = Event.PRINTER_MANAGER_NOT_FOUND;
                return null;
            }

            @SuppressWarnings({ "unchecked", "rawtypes" })
            @Override
            protected void handleAllResponses(Vector responses, Vector acceptances) {
                boolean accepted = false;
                for (ACLMessage offer : (Vector<ACLMessage>) responses) {
                    if (offer.getPerformative() == ACLMessage.PROPOSE) {
                        if (!accepted) {
                            accepted = true;
                            ACLMessage reply = offer.createReply();
                            reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                            acceptances.add(reply);
                            myUserAgent.setPrinterManager(offer.getSender());
                        } else {
                            ACLMessage reply = offer.createReply();
                            reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                            acceptances.add(reply);
                        }
                    }
                }

                if (accepted) {
                    exitStatus = Event.PRINTER_MANAGER_SELECTED;
                }
            }

            @Override
            public int onEnd() {
                return exitStatus;
            }
        }

        private static final class DocumentWrapper {

            private String id;

            private Document document;

            private String conversationId;

            private AID conversationPartner;

            private String statusInfo;

            public DocumentWrapper(String id, Document document) {
                this.id = id;
                this.document = document;
            }

            public String getId() {
                return id;
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

            public String getStatusInfo() {
                return statusInfo;
            }

            public void setStatusInfo(String info) {
                this.statusInfo = info;
            }
        }
    }
}
