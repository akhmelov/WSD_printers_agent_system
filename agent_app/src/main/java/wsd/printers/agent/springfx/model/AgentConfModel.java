package wsd.printers.agent.springfx.model;

import java.time.Duration;
import java.util.List;

/**
 * Created by akhmelov on 11/1/16.
 */
public class AgentConfModel {
    private String discoveryServerUrl;
    private String name;
    private Boolean color;
    private String typeOfPrinter;
    private List<PageConfModel> pageConfModelList;

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

    public List<PageConfModel> getPageConfModelList() {
        return pageConfModelList;
    }

    public void setPageConfModelList(List<PageConfModel> pageConfModelList) {
        this.pageConfModelList = pageConfModelList;
    }

    public Boolean getColor() {
        return color;
    }

    public void setColor(Boolean color) {
        this.color = color;
    }

    public String getTypeOfPrinter() {
        return typeOfPrinter;
    }

    public void setTypeOfPrinter(String typeOfPrinter) {
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
