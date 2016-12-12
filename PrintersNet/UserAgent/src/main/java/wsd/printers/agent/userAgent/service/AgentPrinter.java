package wsd.printers.agent.userAgent.service;

import wsd.printers.agent.common.enums.StatusOfDocumentEnum;
import wsd.printers.agent.userAgent.exception.UnsupportedParametersPresentException;
import wsd.printers.agent.userAgent.model.AgentConfModel;
import wsd.printers.agent.userAgent.model.DocumentModel;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * Created by akhmelov on 11/5/16.
 */
public interface AgentPrinter {
    /**
     * Mowi o parametrach drukarki, agenta ...
     * @return
     */
    AgentConfModel getThisAgentParameters();
    /**
     * Wrzuca dokument zaladowany przez uzutkownika (do agenta) do kolejki blokujacej
     * @param documentModel
     */
    void addDocumentToQueue(DocumentModel documentModel) throws UnsupportedParametersPresentException;

    /**
     * Kiedy uzytkownik laduje dokument do agenta, zeby ten wydrukowal, to dokument jest ladowany do
     * kolejki blokujacej. <br/>Idea jest taka, ze watek agenta, ktory dogaduje sie z innymi agentami jest zawieszony
     * na kolejce blokujacej i czeka poki tam cos trafi, po czym pobiera z kolejki dokument i dogaduje sie z innymi
     * agentami
     * i decyduje gdzie go wydrukowac. Szacowany czas oczekiwania kiedy bedzie mogl wydrukowac, ze swojej drukarki
     * pobiera z {@link #estimatedQueueDuration()}. Jesli decyduje to wydrukowac na swojej drukarce
     * to wybiera metode {@link #printHere(DocumentModel)}. Zeby sprawdzic status dokumentu, ktory zoztal wrzucony do
     * druku wywoluje na tym komputerze {@link #checkStatus(String)}.
     * Zeby poinformowac o czyms usera to wywoluje {@link #informUser(String)}.
     * F-cja {@link #isDocumentSupported(DocumentModel)} sprawdza czy drukarka na tym agencie jest w stanie wydrukowac
     *
     *
     * @return zwraca dokument, ktory zostal zaladowany
     */
    DocumentModel blockingQueueTake() throws InterruptedException;

    /**
     * Zwraca szacowany czas oczekiwania w kolejcy na drukarce podwladnej temu agentowi
     * @return
     */
    Duration estimatedQueueDuration();

    /**
     * Oszacowanie czas trwania drukowania dokumentu na tej drukarce
     * @param documentModel
     * @return
     */
    Duration estimateDocumentDuration(DocumentModel documentModel);

    /**
     * Wrzucanie do kolejki do druku na tym komputerze
     * @param documentModel
     * @return Kolejka blokujaca do, ktorej beda trafialy eventy, co sie dzieje z tym dokumentem
     * np. czy zostal wydrukowany, czy jest nie powodzenie ...
     * Zawartosc Map.Entry<id dokumentu w kolejce (potrzebne do sprawdzenia statusu {@link #checkStatus(String)},
     * StatusOfDocumentEnum - czyli obecny event>
     */
    BlockingQueue<Map.Entry<String, StatusOfDocumentEnum>> printHere(DocumentModel documentModel)
            throws UnsupportedParametersPresentException;

    /**
     * Sprawdza status dokumentu, ktory zostal wrzucony do kolejki na tym komputerze
     * @param idDocument id dokumentu (zwrocone przez {@link #printHere(DocumentModel)}
     * @return
     */
    StatusOfDocumentEnum checkStatus(String idDocument);

    /**
     * Wypisuje komunikat dla usera, ktory jest na tym komputerze (np. Dokument bedzie drukowany na tym agencie)
     * @param infoText komunikat
     */
    void informUser(String infoText);

    /**
     * Sprawdza czy dana drukarka jest w stanie wydrukowac ten dokument
     * @param documentModel dokument do sprawdzenia
     * @return true - jesli ta drukarka jest w stanie go wydrukowac, false - jesli nie
     */
    boolean isDocumentSupported(DocumentModel documentModel);
}
