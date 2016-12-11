package pl.edu.pw.eiti.wsd.printerweb.printer;

import java.util.Arrays;

import pl.edu.pw.eiti.wsd.printerweb.RoleBasedAgent;
import pl.edu.pw.eiti.wsd.printerweb.printer.driver.PrinterDriverImpl;

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
    public PrinterAgent() {
        super(Arrays.asList(new ExecutorRoleImpl(new PrinterDriverImpl())));
    }
}
