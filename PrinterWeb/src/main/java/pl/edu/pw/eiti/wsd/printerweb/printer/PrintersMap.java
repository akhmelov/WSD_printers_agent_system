package pl.edu.pw.eiti.wsd.printerweb.printer;

import java.util.HashSet;
import java.util.Set;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.Property;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.util.leap.Iterator;
import pl.edu.pw.eiti.wsd.printerweb.printer.LocationProvider.Location;
import pl.edu.pw.eiti.wsd.printerweb.printer.document.PaperFormat;
import pl.edu.pw.eiti.wsd.printerweb.printer.driver.PrinterDriver;

public class PrintersMap {

    public Set<AID> getPrintersNearby(Location location) {
        Set<AID> printers = new HashSet<>();
        for (int i = 0; i < 2; ++i) {
            printers.add(new AID("negotiator" + i, AID.ISLOCALNAME));
        }
        // Perform the request

        return printers;
    }

    public Set<AID> getPrintersNearby(Location location, Agent myAgent, PaperFormat paperFormat, PrinterDriver.PrinterInfo.PrinterType printerType, int distanceAccept){
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        Set<AID> printers = new HashSet<>();
        sd.setType(PrinterAgent.AGENT_TYPE);
        template.addServices(sd);
        try {

            while (true){
                DFAgentDescription[] result = DFService.search(myAgent, template);
                LookingForPrinterConditionGlob acceptedDistance = new LookingForPrinterConditionGlob();
                acceptedDistance.acceptedDistance = distanceAccept;
                for (int i = 0; i < result.length; ++i) {
                    LookingForPrinterCondition lookingForPrinterCondition = new LookingForPrinterCondition();
                    result[i].getAllServices().forEachRemaining(o -> {
                        ServiceDescription next = (ServiceDescription)o;
                        next.getAllProperties().forEachRemaining(o1 -> {
                            Property nextProperties = (Property)o1;

                            if(nextProperties.getName().equals("supported_paper_format")){
                                if(paperFormat.equals((PaperFormat.valueOf((String)nextProperties.getValue()))))
                                    lookingForPrinterCondition.isFormatSupported = true;
                            } else if(nextProperties.getName().equals("location")) {
                                int i1 = new LocationProvider().calculateDistance(location, LocationProvider.deseralizeLocation((String)nextProperties.getValue()));
                                if (i1 < acceptedDistance.acceptedDistance)
                                    lookingForPrinterCondition.isLocationGood = true;
                            } else if(nextProperties.getName().equals("printer_type")) {
                                PrinterDriver.PrinterInfo.PrinterType printerTypeRemote = PrinterDriver.PrinterInfo.PrinterType.valueOf((String) nextProperties.getValue());

                                if (printerType.equals(PrinterDriver.PrinterInfo.PrinterType.COLOR)
                                        && printerTypeRemote.equals(PrinterDriver.PrinterInfo.PrinterType.COLOR))
                                    lookingForPrinterCondition.isPrinterTypeOk = true;
                                else if(printerType.equals(PrinterDriver.PrinterInfo.PrinterType.BLACK))
                                    lookingForPrinterCondition.isPrinterTypeOk = true;
                            } else if(nextProperties.getName().equals("double_side")) {
                                lookingForPrinterCondition.isDoubleSideOk = true;// TODO: 1/23/17 jakas logika
                            }
                        });
                    });
                    if(lookingForPrinterCondition.isOk()) {
                        DFAgentDescription dfAgentDescription = result[i];
                        printers.add(new AID(dfAgentDescription.getName().getName(), AID.ISLOCALNAME));
                    }
                }
                if(printers.isEmpty())
                    acceptedDistance.acceptedDistance += acceptedDistance.acceptedDistance + 5;
                else
                    break;
            }
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }

        return printers;
    }

    private class LookingForPrinterConditionGlob {
        int acceptedDistance = 5;
    }

    private class LookingForPrinterCondition {
        boolean isFormatSupported = false, isLocationGood = false, isPrinterTypeOk = false, isDoubleSideOk = false;

        boolean isOk(){
            return isFormatSupported && isLocationGood && isPrinterTypeOk && isDoubleSideOk;
        }
    }
}
