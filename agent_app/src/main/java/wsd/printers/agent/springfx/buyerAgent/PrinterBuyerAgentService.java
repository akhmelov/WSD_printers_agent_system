package wsd.printers.agent.springfx.buyerAgent;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Created by akhmelov on 12/10/16.
 */
@Service
public class PrinterBuyerAgentService {

    private Thread mainThread;

    @PostConstruct
    public void init(){
        this.mainThread = new Thread(this::run);
        this.mainThread.setDaemon(true);
        this.mainThread.run();
    }

    private void run(){

    }
}
