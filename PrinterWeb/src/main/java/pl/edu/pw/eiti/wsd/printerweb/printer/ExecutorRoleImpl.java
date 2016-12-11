package pl.edu.pw.eiti.wsd.printerweb.printer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.util.Assert;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import pl.edu.pw.eiti.wsd.printerweb.Document;
import pl.edu.pw.eiti.wsd.printerweb.printer.driver.PrinterDriver;
import pl.edu.pw.eiti.wsd.printerweb.printer.driver.PrinterDriver.PrinterEvent;
import pl.edu.pw.eiti.wsd.printerweb.printer.driver.PrinterDriver.PrinterListener;

public class ExecutorRoleImpl implements ExecutorRole, PrinterListener {

    private final PrinterDriver printerDriver;

    private final Map<String, AID> scheduledTasks = new ConcurrentHashMap<>();

    /**
     * @param printerDriver
     *      Printer driver which is used to communicate with the printer which is controlled by this instance. Not null.
     */
    public ExecutorRoleImpl(final PrinterDriver printerDriver) {
        Assert.notNull(printerDriver);

        this.printerDriver = printerDriver;
        printerDriver.addListener(this);
    }

    @Override
    public ACLMessage handleAcceptProposal(ACLMessage msg) {
        Document document;
        try {
            document = (Document) msg.getContentObject();
            String id = printerDriver.addToQueue(document);
            if (id != null) {
                scheduledTasks.put(id, msg.getSender());
                return createConfirmation(id, msg);
            }
            return createRefusal(msg);
        } catch (UnreadableException e) {
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
        confirmation.setPerformative(ACLMessage.CONFIRM);
        confirmation.setContent(id);
        return confirmation;
    }

    private ACLMessage createFailure(String value, AID aid) {
        ACLMessage failure = new ACLMessage(ACLMessage.FAILURE);
        failure.addReceiver(aid);
        failure.setContent(value);
        return failure;
    }

    private ACLMessage createDocumentPrintedInfo(String value, AID aid) {
        ACLMessage info = new ACLMessage(ACLMessage.INFORM);
        info.addReceiver(aid);
        info.setContent(value);
        return info;
    }

    @Override
    public void listen(PrinterEvent event) {
        // switch (event.getType()) {
        // case CRASHED:
        // case NO_INK:
        // case NO_PAPER:
        // for (Entry<String, AID> entry : scheduledTasks.entrySet()) {
        // ACLMessage failure = createFailure(entry.getKey(), entry.getValue());
        // agent.addBehaviour(new SenderBehaviour(agent, failure));
        // }
        // agent.errorOccured();
        // break;
        // case PRINTED:
        // AID aid = scheduledTasks.get(event.getValue());
        // ACLMessage info = createDocumentPrintedInfo(event.getValue(), aid);
        // agent.addBehaviour(new SenderBehaviour(agent, info));
        // break;
        // case READY:
        // agent.readyToWork();
        // break;
        // default:
        // break;
        // }
    }
}
