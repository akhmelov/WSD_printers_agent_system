package pl.edu.pw.eiti.wsd.printerweb.printer;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import pl.edu.pw.eiti.wsd.printerweb.AgentRole;


public interface PrinterManagerRole extends AgentRole {

    ACLMessage handlePrintRequest(ACLMessage request) throws UnreadableException;

}
