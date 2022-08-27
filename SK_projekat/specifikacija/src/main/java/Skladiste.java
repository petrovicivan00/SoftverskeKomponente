public abstract class Skladiste {

    protected String fileName;

    public Skladiste() {

    }

    /**
     * Inicijalizuje root skladiste
     *
     * @param ime      ime skladista
     * @param path     putanja skladista
     * @param username username admina
     * @param password password admina
     */
    abstract void initRoot(String ime, String path, String username, String password);

    /**
     * Inicijalizuje root skladiste
     *
     * @param ime        ime skladista
     * @param path       putanja skladista
     * @param username   username admina
     * @param password   password admina
     * @param maxSize    maksimalna velicina skladista
     * @param extensions nedozvoljene ekstenzije
     */
    abstract void initRootWithRestrictions(String ime, String path, String maxSize, String maxFiles, String extensions,
                                           String username, String password);

    /**
     * Pretrazuje fajl na zadatoj putanji
     *
     * @param ime  ime fajla
     * @param path putanja
     */
    abstract void search(String ime, String path);

    /**
     * Ispisuje foldere i fajlove za zadatu putanju
     *
     * @param path putanja
     */
    abstract void list(String path);

    /**
     * Ispisuje fajlove za zadatu putanju
     *
     * @param path putanja
     */
    abstract void listFiles(String path);

    /**
     * Ispisuje foldere za zadatu putanju
     *
     * @param path putanja
     */
    abstract void listFolders(String path);

    /**
     * Sortira zadati ispit po zadatom kriterijumu
     *
     * @param path putanja foldera koji zelimo da sortiramo
     * @param funk odabir po cemu ce metoda sortirati (ime,datum kreiranja, datum izmene)
     * @param tip  odabir rastuceg ili opadajuceg sortiranja
     */
    abstract void sort(String path, String funk, String tip);

    /**
     * Pravi prazan folder na zadatoj putanji
     *
     * @param ime  ime foldera koji zelimo da kreiramo
     * @param path putanja gde zelimo kreirati folder
     */
    abstract void makeDirectory(String ime, String path);

    /**
     * Pravi listy praznih foldera na zadatoj putanji
     *
     * @param ime  ime foldera koji zelimo da kreiramo
     * @param path putanja gde zelimo kreirati foldere
     */
    abstract void makeListOfDirectories(String ime, String path, Integer number);

    /**
     * Pomera folder na zadatu putanju
     *
     * @param ime   ime foldera koji zelimo da pomerimo
     * @param path1 putanja gde zelimo pomeriti folder
     */
    abstract void moveDirectory(String ime, String path1);

    /**
     * Brise folder na zadatoj putanji
     *
     * @param ime  ime foldera koji zelimo da obrisemo
     * @param path putanja gde se nalazi folder koji zelimo da obrisemo
     */
    abstract void deleteDirectory(String ime, String path);

    /**
     * Pravi prazan fajl na zadatoj putanji
     *
     * @param ime  ime fajla koji zelimo da kreiramo
     * @param path putanja gde zelimo kreirati folder
     */
    abstract void makeFile(String ime, String path);

    /**
     * Pravi listy fajlova na zadatoj putanji
     *
     * @param ime  ime fajlova koji zelimo da kreiramo
     * @param path putanja gde zelimo kreirati fajlove
     */
    abstract void makeListOfFiles(String ime, String path, Integer number);

    /**
     * Pomera fajl na zadatu putanju
     *
     * @param ime  ime fajl koji zelimo da pomerimo
     * @param path putanja gde zelimo pomeriti fajl
     */
    abstract void moveFile(String ime, String path);

    /**
     * Brise fajl na zadatoj putanji
     *
     * @param ime  ime fajla koji zelimo da obrisemo
     * @param path putanja gde se nalazi fajl koji zelimo da obrisemo
     */
    abstract void deleteFile(String ime, String path);

    /**
     * Skida fajl sa zadate putanje
     *
     * @param ID putanja
     */
    abstract void downloadFile(String ID);

    /**
     * Povezuje korisnika na skladiste
     *
     * @param username username korisnika koji zeli da se poveze
     * @param password password korisnika koji zeli da se poveze
     * @param path     putanja skladista na koje korisnik zeli da se poveze
     */
    abstract boolean connect(String username, String password, String path);

    /**
     * Pravljenje novog korisnika
     *
     * @param username  username korisnika kojeg zelimo da napravimo
     * @param password  password korisnika kojeg zelimo da napravimo
     * @param path      putanja skladista za koje se korisnik pravi
     * @param privilege privilegija koju ce korisnik imati
     */
    abstract void makeUser(String username, String password, String path, String privilege);

    /**
     * Diskonektuje trenutno povezanog korisnika sa skladista
     */
    abstract void disconnect();

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}