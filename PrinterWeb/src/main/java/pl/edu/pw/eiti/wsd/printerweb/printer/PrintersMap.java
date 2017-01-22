package pl.edu.pw.eiti.wsd.printerweb.printer;

import java.util.HashSet;
import java.util.Set;

import jade.core.AID;
import pl.edu.pw.eiti.wsd.printerweb.printer.LocationProvider.Location;

public class PrintersMap {

    public Set<AID> getPrintersNearby(Location location) {
        Set<AID> printers = new HashSet<>();
        for (int i = 0; i < 2; ++i) {
            printers.add(new AID("negotiator" + i, AID.ISLOCALNAME));
        }

        return printers;
    }
}
