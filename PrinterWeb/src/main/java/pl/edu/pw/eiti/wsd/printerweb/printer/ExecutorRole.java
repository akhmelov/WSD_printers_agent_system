package pl.edu.pw.eiti.wsd.printerweb.printer;

import jade.domain.FIPAAgentManagement.FailureException;
import jade.lang.acl.ACLMessage;
import pl.edu.pw.eiti.wsd.printerweb.AgentRole;

public interface ExecutorRole extends AgentRole {

    ACLMessage handleAcceptProposal(ACLMessage cfp) throws FailureException;

}
