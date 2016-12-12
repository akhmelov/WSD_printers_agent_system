package pl.edu.pw.eiti.wsd.printerweb.printer;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SenderBehaviour;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import pl.edu.pw.eiti.wsd.printerweb.printer.document.Document;
import pl.edu.pw.eiti.wsd.printerweb.printer.driver.PrinterDriver;
import pl.edu.pw.eiti.wsd.printerweb.printer.driver.PrinterDriver.PrinterEvent;
import pl.edu.pw.eiti.wsd.printerweb.printer.driver.PrinterDriver.PrinterListener;

public class ExecutorRoleImpl implements ExecutorRole, PrinterListener {

    private final PrinterDriver printerDriver;

    private final Map<String, ACLMessage> scheduledTasks = new ConcurrentHashMap<>();

    private final PrinterAgent agent;

    /**
     * @param printerDriver
     *      Printer driver which is used to communicate with the printer which is controlled by this instance. Not null.
     */
    public ExecutorRoleImpl(final PrinterAgent agent, final PrinterDriver printerDriver) {
        this.agent = agent;
        this.printerDriver = printerDriver;
        printerDriver.addListener(this);
    }

    @Override
    public ACLMessage handleAcceptProposal(ACLMessage msg) {
        System.out.println("Executor: proposal accepted, print");
        try {
            Document document = (Document) msg.getContentObject();
            String id = printerDriver.addToQueue(document);
            ACLMessage response;
            if (id != null) {
                scheduledTasks.put(id, msg);
                response = createConfirmation(id, msg);
            } else {
                response = createRefusal(msg);
            }

            return response;
        } catch (UnreadableException | FailureException e) {
            throw new RuntimeException(e); // TODO respond not understood
        }
    }

    private ACLMessage createRefusal(ACLMessage msg) {
        ACLMessage refusal = msg.createReply();
        refusal.setPerformative(ACLMessage.REFUSE);
        return refusal;
    }

    private ACLMessage createConfirmation(String id, ACLMessage msg) {
        ACLMessage confirmation = msg.createReply();
        confirmation.setSender(agent.getAID());
        confirmation.setPerformative(ACLMessage.INFORM);
        confirmation.setContent(id);
        return confirmation;
    }

    private ACLMessage createFailure(String value, ACLMessage request) {
        ACLMessage failure = request.createReply();
        failure.setPerformative(ACLMessage.FAILURE);
        failure.setContent(value);
        return failure;
    }

    private ACLMessage createDocumentPrintedInfo(String value, ACLMessage request) {
        ACLMessage info = request.createReply();
        info.setPerformative(ACLMessage.INFORM);
        info.setContent(value);
        return info;
    }

    @Override
    public void listen(PrinterEvent event) {
        switch (event.getType()) {
            case CRASHED:
            case NO_INK:
            case NO_PAPER:
                for (Entry<String, ACLMessage> entry : scheduledTasks.entrySet()) {
                    agent.addBehaviour(new OneShotBehaviour() {
                        
                        private static final long serialVersionUID = -435171932434060891L;

                        @Override
                        public void action() {
                            ACLMessage failure = createFailure(entry.getKey(), entry.getValue());
                            myAgent.send(failure);
                        }
                    });
                }
                agent.errorOccured();
                break;
            case PRINTED:
                agent.addBehaviour(new OneShotBehaviour() {
                    
                    private static final long serialVersionUID = -435171932434060891L;

                    @Override
                    public void action() {
                        ACLMessage request = scheduledTasks.get(event.getValue());
                        ACLMessage info = createDocumentPrintedInfo(event.getValue(), request);
                        myAgent.send(info);
                    }
                });
                break;
            case READY:
                agent.readyToWork();
                break;
            default:
                break;
        }
    }
}
