package pl.edu.pw.eiti.wsd.printerweb.printer;

import jade.lang.acl.ACLMessage;
import pl.edu.pw.eiti.wsd.printerweb.AgentRole;


public interface NegotiatorRole extends AgentRole {

    ACLMessage handleProposal(ACLMessage cfp);

}
