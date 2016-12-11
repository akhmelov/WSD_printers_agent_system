package wsd.printers.agent.springfx.sellerAgent;

import javax.swing.text.Position;
import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by akhmelov on 12/11/16.
 */
public class HeartbeatInfoDiscoveryServer {
    private String name;
    private Point point;
    private URL discoveryServerUrl;

    public HeartbeatInfoDiscoveryServer(int x, int y, String discoveryHost, Integer discoveryPort, String name) throws MalformedURLException {
        this.point = new Point(x, y);
        this.name = name;

        if(discoveryPort != null)
            this.discoveryServerUrl = new URL("http://" + discoveryHost + ":" + discoveryPort);
        else
            this.discoveryServerUrl = new URL("http://" + discoveryHost);
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public URL getDiscoveryServerUrl() {
        return discoveryServerUrl;
    }

    public void setDiscoveryServerUrl(URL discoveryServerUrl) {
        this.discoveryServerUrl = discoveryServerUrl;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
