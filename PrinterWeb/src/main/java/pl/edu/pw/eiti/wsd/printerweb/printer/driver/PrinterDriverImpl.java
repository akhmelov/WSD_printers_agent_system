package pl.edu.pw.eiti.wsd.printerweb.printer.driver;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import jade.domain.FIPAAgentManagement.FailureException;
import pl.edu.pw.eiti.wsd.printerweb.printer.document.Document;
import pl.edu.pw.eiti.wsd.printerweb.printer.driver.PrinterDriver.PrinterEvent.Type;

public class PrinterDriverImpl implements PrinterDriver, Runnable {

    private final BlockingQueue<DocumentTask> queue = new LinkedBlockingQueue<>(50);

    private final AtomicInteger idGenerator = new AtomicInteger(1);

    private final PrinterInfo printerInfo;

    private PrinterListener listener;

    private ExecutorService executor;

    private Future<?> workingFuture;

    public PrinterDriverImpl(PrinterInfo printerInfo) {
        this.printerInfo = printerInfo;
        // PrinterController view = new PrinterController(this);
        // view.show();
        this.executor = Executors.newSingleThreadExecutor();
        startExecution();
    }

    @Override
    public String addToQueue(Document document) throws FailureException {
        DocumentTask doc = new DocumentTask(Integer.toString(idGenerator.getAndIncrement()), document);
        if (queue.offer(doc)) {
            return doc.getId();
        }

        throw new FailureException("Could not add to printers queue!");
    }

    @Override
    public PrinterInfo getInfo() {
        return printerInfo;
    }

    @Override
    public void addListener(PrinterListener listener) {
        this.listener = listener;
    }

    @Override
    public void setNoInk(boolean b) {
        if (b) {
            stopExecution();
            listener.listen(new PrinterEventImpl(PrinterEvent.Type.NO_INK, ""));
        } else {
            setReady();
        }
    }

    @Override
    public void setNoPaper(boolean b) {
        if (b) {
            listener.listen(new PrinterEventImpl(PrinterEvent.Type.NO_PAPER, ""));
        } else {
            setReady();
        }
    }

    @Override
    public void setCrashed(boolean b) {
        if (b) {
            listener.listen(new PrinterEventImpl(PrinterEvent.Type.CRASHED, ""));
        } else {
            setReady();
        }
    }

    private void setReady() {
        listener.listen(new PrinterEventImpl(PrinterEvent.Type.READY, ""));
    }

    private void stopExecution() {
        if (!workingFuture.cancel(false)) {
            executor.shutdownNow();
            throw new RuntimeException("Cannot stop printer!");
        }
    }

    private void startExecution() {
        workingFuture = executor.submit(this);
    }

    @Override
    public String toString() {
        return printerInfo.getName();
    }

    private static final class PrinterEventImpl implements PrinterEvent {

        private Type type;

        private String value;

        public PrinterEventImpl(Type type, String value) {
            this.type = type;
            this.value = value;
        }

        @Override
        public Type getType() {
            return type;
        }

        @Override
        public String getValue() {
            return value;
        }
    }

    private static class DocumentTask implements Runnable {

        private final String id;

        private final Document doc;

        public DocumentTask(String id, Document doc) {
            this.id = id;
            this.doc = doc;
        }

        public String getId() {
            return id;
        }

        public Document getDoc() {
            return doc;
        }

        @Override
        public void run() {

        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                DocumentTask documentTask = queue.take();
                listener.listen(new PrinterEventImpl(Type.PRINTING, documentTask.getId()));
                int pages = documentTask.getDoc().getNumberOfPages();
                for (int i = 0; i < pages; i++) {
                    System.out.println("Page: " + i);
                    Thread.sleep(getInfo().getPrinterBlackEfficiency());
                }
                listener.listen(new PrinterEventImpl(Type.PRINTED, documentTask.getId()));
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
