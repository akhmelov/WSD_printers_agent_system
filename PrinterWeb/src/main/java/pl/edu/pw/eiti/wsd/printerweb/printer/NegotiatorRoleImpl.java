package pl.edu.pw.eiti.wsd.printerweb.printer;

import jade.lang.acl.ACLMessage;

public class NegotiatorRoleImpl implements NegotiatorRole {

    @Override
    public ACLMessage handleProposal(ACLMessage cfp) {
        System.out.println("Negotiator: create propose");
        ACLMessage reply = cfp.createReply();
        reply.setPerformative(ACLMessage.PROPOSE);
        return reply;
    }

}
