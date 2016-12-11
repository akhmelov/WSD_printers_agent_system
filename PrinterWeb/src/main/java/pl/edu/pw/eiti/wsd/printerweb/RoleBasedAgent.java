package pl.edu.pw.eiti.wsd.printerweb;

import java.util.Collection;

import org.springframework.util.Assert;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;

/**
 * Abstract implementation of Agent which may play multiple roles.
 */
public abstract class RoleBasedAgent extends Agent {

    private static final long serialVersionUID = -5096850918249835455L;

    private Collection<? extends AgentRole> roles;

    /**
     * @param roles
     *      Collection of roles which should be applied to this agent. Not null.
     */
    public RoleBasedAgent(Collection<? extends AgentRole> roles) {
        super();

        Assert.notNull(roles);
        this.roles = roles;
    }

    @Override
    protected void setup() {
        if (roles.isEmpty()) {
            doDelete();
            return;
        }

        for (AgentRole agentRole : roles) {
            applyRole(agentRole);
        }
    }

    private void applyRole(final AgentRole agentRole) {
        Collection<Behaviour> behaviours = agentRole.getBehaviours(this);
        if (behaviours != null) {
            for (Behaviour behavior : behaviours) {
                addBehaviour(behavior);
            }
        }
    }
    
    public void errorOccured() {
        
    }
    
    public void readyToWork() {
        
    }
}
