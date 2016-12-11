package pl.edu.pw.eiti.wsd.printerweb.printer;

import java.util.Collection;

import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import pl.edu.pw.eiti.wsd.printerweb.RoleBasedAgent;

public class NegotiatorRoleImpl implements NegotiatorRole {

    @Override
    public Collection<Behaviour> getBehaviours(RoleBasedAgent agent) {
        // TODO Auto-generated method stub
        return null;
    }

    private static class NegotiatorBehaviour extends CyclicBehaviour {

        @Override
        public void action() {
            // TODO Auto-generated method stub

        }
    }
}
