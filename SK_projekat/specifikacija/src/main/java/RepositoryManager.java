public class RepositoryManager {

    private static Skladiste skladiste;


    public static void registerExporter(Skladiste dbExp) {
        skladiste = dbExp;
    }

    public static Skladiste getExporter(String fileName) {
        skladiste.setFileName(fileName);
        return skladiste;
    }

}
