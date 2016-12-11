package wsd.printers.agent.springfx.sellerAgent;

import jade.core.Agent;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Created by akhmelov on 12/10/16.
 */
@Service
public class PrinterSellerAgentService {

    private Thread mainThread;

    @PostConstruct
    private void init(){
        this.mainThread = new Thread(this::run);
        this.mainThread.setDaemon(true);
        this.mainThread.run();
    }

    private void run(){

    }

}
