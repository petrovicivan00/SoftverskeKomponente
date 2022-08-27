import com.fasterxml.jackson.annotation.JsonIgnore;

public class User {

    private String username;
    private String password;
    private String privilege;
    private String path;

    public User(String username, String password, String privilege, String path) {
        this.username = username;
        this.password = password;
        this.privilege = privilege;
        this.path = path;
    }

    public User() {

    }

    @JsonIgnore
    public boolean isReader() {
        return privilege.equalsIgnoreCase("reader");
    }

    @JsonIgnore
    public boolean isAdmin() {
        return privilege.equalsIgnoreCase("admin");
    }

    @JsonIgnore
    public boolean isWriter() {
        return privilege.equalsIgnoreCase("writer");
    }

    @JsonIgnore
    public boolean isDownloader() {
        return privilege.equalsIgnoreCase("downloader");
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPrivilege() {
        return privilege;
    }

    public void setPrivilege(String privilege) {
        this.privilege = privilege;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
