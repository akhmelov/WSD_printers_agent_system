package pl.edu.pw.eiti.wsd.printerweb.printer;

import java.util.Collection;

import pl.edu.pw.eiti.wsd.printerweb.AgentRole;
import pl.edu.pw.eiti.wsd.printerweb.RoleBasedAgent;

/**
 * Agent which is responsible for interaction with {@link pl.edu.pw.eiti.wsd.printerweb.user.UserAgent UserAgent}. Provides access to printers.
 *
 */
public class PrinterAgent extends RoleBasedAgent {

    private static final long serialVersionUID = 6504683624380808507L;

    /**
     * @param roles
     *      Collection of roles which should be applied to this agent. Not null.
     */
    public PrinterAgent(Collection<? extends AgentRole> roles) {
        super(roles);
    }

}
