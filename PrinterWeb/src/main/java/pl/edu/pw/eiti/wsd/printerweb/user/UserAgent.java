package pl.edu.pw.eiti.wsd.printerweb.user;

import java.util.Collection;

import pl.edu.pw.eiti.wsd.printerweb.AgentRole;
import pl.edu.pw.eiti.wsd.printerweb.RoleBasedAgent;

/**
 * Agent which is responsible for interactions with {@link pl.edu.pw.eiti.wsd.printerweb.printer.PrinterAgent PrinterAgent}s on behalf of the user.
 */
public class UserAgent extends RoleBasedAgent {

    private static final long serialVersionUID = -3135506373065999424L;

    /**
     * @param roles
     *      Collection of roles which should be applied to this agent. Not null.
     */
    public UserAgent(Collection<? extends AgentRole> roles) {
        super(roles);
    }
}
