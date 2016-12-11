package pl.edu.pw.eiti.wsd.printerweb.user;

import java.util.Collections;

import pl.edu.pw.eiti.wsd.printerweb.RoleBasedAgent;

/**
 * Agent which is responsible for interactions with {@link pl.edu.pw.eiti.wsd.printerweb.printer.PrinterAgent PrinterAgent}s on behalf of the user.
 */
public class UserAgent extends RoleBasedAgent {

    private static final long serialVersionUID = -3135506373065999424L;

    /**
     *
     */
    public UserAgent() {
        super(Collections.singleton(new UserRoleImpl()));
    }
}
