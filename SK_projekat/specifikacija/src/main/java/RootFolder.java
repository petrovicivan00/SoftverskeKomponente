import java.util.List;

public class RootFolder {

    protected String ime;
    protected String path;
    protected long maxVelicina = Long.MAX_VALUE;
    protected List<String> nepodrzaneEkstenzije;
    protected Integer maxFiles = Integer.MAX_VALUE;
    protected Integer NumberOfFiles;

    public RootFolder() {
    }

    public RootFolder(String ime, String path) {

        this.ime = ime;
        this.path = path;
    }

    public Integer getMaxFiles() {
        return maxFiles;
    }

    public void setMaxFiles(Integer maxFiles) {
        this.maxFiles = maxFiles;
    }

    public Integer getNumberOfFiles() {
        return NumberOfFiles;
    }

    public void setNumberOfFiles(Integer numberOfFiles) {
        NumberOfFiles = numberOfFiles;
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<String> getNepodrzaneEkstenzije() {
        return nepodrzaneEkstenzije;
    }

    public void setNepodrzaneEkstenzije(List<String> nepodrzaneEkstenzije) {
        this.nepodrzaneEkstenzije = nepodrzaneEkstenzije;
    }

    public long getMaxVelicina() {
        return maxVelicina;
    }

    public void setMaxVelicina(long maxVelicina) {
        this.maxVelicina = maxVelicina;
    }
}
