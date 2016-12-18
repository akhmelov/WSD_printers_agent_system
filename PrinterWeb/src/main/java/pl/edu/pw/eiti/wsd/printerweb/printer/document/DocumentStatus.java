package pl.edu.pw.eiti.wsd.printerweb.printer.document;

public enum DocumentStatus {
    LOADED("Rozpoczęty"), WAITS_IN_MANAGER_QUEUE("Oczekuje na drukarkę"), WAITS_IN_PRINTER_QUEUE("W kolejce"), PRINTING(
            "Drukowanie"), PRINTED("Gotowy"), FAILED("Błąd");

    private String displayName;

    private DocumentStatus(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
