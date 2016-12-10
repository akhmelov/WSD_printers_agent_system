package pl.edu.pw.eiti.wsd.printerweb;

import java.util.Collection;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;

public interface AgentRole {
    
    abstract Collection<Behaviour> getBehaviours(Agent userAgent);
}
