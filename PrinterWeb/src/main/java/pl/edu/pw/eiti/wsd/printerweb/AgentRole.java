package pl.edu.pw.eiti.wsd.printerweb;

import java.util.Collection;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;

/**
 * Role which may be applied to an Agent.
 */
public interface AgentRole {

    /**
     * Retrieves all behaviors of this role.
     * 
     * @param agent
     *      Agent to which this role is applied. Not null.
     * @return
     *      All behaviors played by this role. Never null.
     */
    abstract Collection<Behaviour> getBehaviours(final Agent agent);
}
