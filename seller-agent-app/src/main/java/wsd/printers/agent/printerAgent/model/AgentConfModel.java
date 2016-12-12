package wsd.printers.agent.printerAgent.model;

import wsd.printers.agent.printerAgent.enums.PrinterTypeEnum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by akhmelov on 11/1/16.
 */
public class AgentConfModel {
    private String discoveryServerUrl;
    private String name;
    private Boolean color;
    private PrinterTypeEnum typeOfPrinter;
    private List<HashMap> pageConfModelList = new ArrayList<>();

    public String getDiscoveryServerUrl() {
        return discoveryServerUrl;
    }

    public void setDiscoveryServerUrl(String discoveryServerUrl) {
        this.discoveryServerUrl = discoveryServerUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<HashMap> getPageConfModelList() {
        return pageConfModelList;
    }

    public void setPageConfModelList(List<HashMap> pageConfModelList) {
        this.pageConfModelList = pageConfModelList;
    }

    public Boolean getColor() {
        return color;
    }

    public void setColor(Boolean color) {
        this.color = color;
    }

    public PrinterTypeEnum getTypeOfPrinter() {
        return typeOfPrinter;
    }

    public void setTypeOfPrinter(PrinterTypeEnum typeOfPrinter) {
        this.typeOfPrinter = typeOfPrinter;
    }

    @Override
    public String toString() {
        return "AgentConfModel{" +
                "discoveryServerUrl='" + discoveryServerUrl + '\'' +
                ", name='" + name + '\'' +
                ", color=" + color +
                ", typeOfPrinter='" + typeOfPrinter + '\'' +
                ", pageConfModelList=" + pageConfModelList +
                '}';
    }
}
