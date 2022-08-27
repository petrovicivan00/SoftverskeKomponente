import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

//1T7S9wzAjvfTMDKG9UQFaeceYvPeEKr2W
public class GDSkladiste extends Skladiste {

    /**
     * Application name.
     */
    private static final String APPLICATION_NAME = "GoogleDriveSK";
    /**
     * Global instance of the JSON factory.
     */
    private static final JacksonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    /**
     * Global instance of the scopes required by this quickstart.
     * <p>
     * If modifying these scopes, delete your previously saved credentials at
     * ~/.credentials/calendar-java-quickstart
     */
    private static final List<String> SCOPES = Arrays.asList(DriveScopes.DRIVE);
    /**
     * Global instance of the {@link FileDataStoreFactory}.
     */
    private static FileDataStoreFactory DATA_STORE_FACTORY;
    /**
     * Global instance of the HTTP transport.
     */
    private static HttpTransport HTTP_TRANSPORT;

    static {
        RepositoryManager.registerExporter(new GDSkladiste());
    }

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    ObjectMapper objectMapper = new ObjectMapper();
    User connectedUser = null;
    List<User> allUsers = new ArrayList<>();
    RootFolder rf = null;
    String cc = null;
    String uc = null;

    public GDSkladiste() {

    }

    /**
     * Creates an authorized Credential object.
     *
     * @return an authorized Credential object.
     * @throws IOException
     */
    public static Credential authorize() throws IOException {
        // Load client secrets.
        InputStream in = GDSkladiste.class.getResourceAsStream("/client_secret.json");
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
                clientSecrets, SCOPES).setAccessType("offline").build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
        return credential;
    }

    /**
     * Build and return an authorized Calendar client service.
     *
     * @return an authorized Calendar client service
     * @throws IOException
     */
    public static Drive getDriveService() throws IOException {
        Credential credential = authorize();
        return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    @Override
//radi
    void initRoot(String ime, String path, String username, String password) {

        File file = new File();
        file.setName(ime);
        file.setMimeType("application/vnd.google-apps.folder");
        file.setParents(Collections.singletonList(path));

        if (checkItemExists(file.getName(), path)) {
            System.out.println("Root already exists.");
            return;
        }
        try {
            file = GDSkladiste.getDriveService().files().create(file)
                    .setFields("id,parents")
                    .execute();
            System.out.println("New Root ID: " + file.getId());
        } catch (IOException e) {
            e.printStackTrace();
        }

        rf = new RootFolder(ime, file.getId());
        rf.setMaxFiles(Integer.MAX_VALUE);
        rf.setMaxVelicina(Long.MAX_VALUE);
        rf.setNumberOfFiles(0);
        rf.setNepodrzaneEkstenzije(Collections.singletonList(""));
        makeConfig(rf, file.getId());
        connectedUser = new User(username, password, "admin", file.getId());
        makeUser(username, password, file.getId(), "admin");
    }

    @Override
//radi
    void initRootWithRestrictions(String ime, String path, String maxSize, String maxFile, String extension, String username, String password) {

        File file = new File();
        file.setName(ime);
        file.setMimeType("application/vnd.google-apps.folder");
        file.setParents(Collections.singletonList(path));

        if (checkItemExists(file.getName(), path)) {
            System.out.println("Root already exists.");
            return;
        }

        try {
            file = GDSkladiste.getDriveService().files().create(file)
                    .setFields("id,parents")
                    .execute().setQuotaBytesUsed(Long.parseLong(maxSize));

            System.out.println("New Root ID: " + file.getId());
        } catch (IOException e) {
            e.printStackTrace();
        }

        rf = new RootFolder(ime, file.getId());
        rf.setMaxFiles(Integer.parseInt(maxFile));
        rf.setNumberOfFiles(0);
        rf.setMaxVelicina(Long.parseLong(maxSize));
        rf.setNepodrzaneEkstenzije(List.of(extension.split(",")));
        makeConfig(rf, file.getId());
        connectedUser = new User(username, password, "admin", file.getId());
        makeUser(username, password, file.getId(), "admin");
    }

    @Override
//radi
    void list(String s) {
        listFolders(s);
        listFiles(s);
    }


    @Override
//radi
    void listFiles(String s) {
        if (checkConnection()) {
            String pageToken = null;
            do {
                FileList result = null;
                try {
                    result = GDSkladiste.getDriveService().files().list()
                            .setQ("'" + s + "' in parents")
                            .setQ("mimeType != 'application/vnd.google-apps.folder'")
                            .setSpaces("drive")
                            .setFields("nextPageToken, files(id, name)")
                            .setPageToken(pageToken)
                            .execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (result.isEmpty()) {
                    System.out.println("No files found.");
                    return;
                }
                for (File file : result.getFiles()) {
                    System.out.printf("Found file: %s (%s)\n", file.getName(), file.getId());
                }
                pageToken = result.getNextPageToken();
            } while (pageToken != null);
        }
    }

    @Override
//radi
    void listFolders(String s) {
        if (checkConnection()) {
            String pageToken = null;
            do {
                FileList result = null;
                try {
                    result = GDSkladiste.getDriveService().files().list()
                            .setQ("'" + s + "' in parents")
                            .setQ("mimeType = 'application/vnd.google-apps.folder'")
                            .setSpaces("drive")
                            .setFields("nextPageToken, files(id, name)")
                            .setPageToken(pageToken)
                            .execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (result.isEmpty()) {
                    System.out.println("No folders found.");
                    return;
                }
                for (File file : result.getFiles()) {
                    System.out.printf("Found folder: %s (%s)\n", file.getName(), file.getId());
                }
                pageToken = result.getNextPageToken();
            } while (pageToken != null);
        }
    }

    @Override
//radi
    void sort(String s, String s1, String s2) {
        if (checkConnection()) {
            String type;
            String sort;

            if (s1.equalsIgnoreCase("name"))
                type = "name";
            else if (s1.equalsIgnoreCase("date modified"))
                type = "modifiedByMeTime";
            else if (s1.equalsIgnoreCase("date created"))
                type = "createdTime";
            else {
                System.out.println("Unknown sort type");
                return;
            }


            if (s2.equalsIgnoreCase("ascending"))
                sort = "asc";
            else if (s2.equalsIgnoreCase("descending"))
                sort = "desc";
            else {
                System.out.println("Unknown sort type");
                return;
            }

            String pageToken = null;
            do {
                FileList result = null;
                try {
                    result = GDSkladiste.getDriveService().files().list()
                            .setQ("'" + s + "' in parents")
                            .setOrderBy(type + " " + sort)
                            .setSpaces("drive")
                            .setFields("nextPageToken, files(id, name)")
                            .setPageToken(pageToken)
                            .execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                for (File file : result.getFiles()) {
                    System.out.printf("Found file: %s (%s)\n", file.getName(), file.getId());
                }
                pageToken = result.getNextPageToken();
            } while (pageToken != null);
        }
    }

    @Override
//radi
    void search(String s, String s1) {
        if (checkConnection()) {
            String pageToken = null;
            do {
                FileList result = null;
                try {
                    result = GDSkladiste.getDriveService().files().list()
                            .setQ("name = '" + s + "'")
                            .setQ("'" + s1 + "' in parents")
                            .setSpaces("drive")
                            .setFields("nextPageToken, files(id, name)")
                            .setPageToken(pageToken)
                            .execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                for (File file : result.getFiles()) {
                    if (file.getName().equals(s)) {
                        System.out.printf("Found %s with ID(%s)\n", file.getName(), file.getId());
                    }
                }
                pageToken = result.getNextPageToken();
            } while (pageToken != null);

            System.out.println("File not found.");
        }
    }

    @Override
//radi
    void makeDirectory(String ime, String path) {
        if (checkConnection()) {
            File file = new File();
            file.setName(ime);
            file.setMimeType("application/vnd.google-apps.folder");
            file.setParents(Collections.singletonList(path));

            if (checkItemExists(file.getName(), path)) {
                System.out.println("Folder already exists.");
                return;
            }

            try {
                file = GDSkladiste.getDriveService().files().create(file)
                        .setFields("id,parents")
                        .execute();
                System.out.println("Folder ID: " + file.getId());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
//radi
    void makeListOfDirectories(String ime, String path, Integer integer) {
        if (checkConnection()) {
            for (int i = 1; i <= integer; i++)
                makeDirectory(ime + i, path);
        }
    }

    @Override
//radi
    void moveDirectory(String s, String s1) {
        if (checkConnection()) {

            if (checkItemExists(s, s1)) {
                System.out.println("Folder already exists.");
                return;
            }

            // Retrieve the existing parents to remove
            File file = null;
            try {
                file = GDSkladiste.getDriveService().files().get(s)
                        .setFields("parents")
                        .execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            StringBuilder previousParents = new StringBuilder();
            for (String parent : file.getParents()) {
                previousParents.append(parent);
                previousParents.append(',');
            }
            // Move the file to the new folder
            try {
                file = GDSkladiste.getDriveService().files().update(s, null)
                        .setAddParents(s1)
                        .setRemoveParents(previousParents.toString())
                        .setFields("id, parents")
                        .execute();
                System.out.println("Move successfully made.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
//radi
    void deleteDirectory(String s, String s1) {
        if (checkConnection()) {
            try {
                GDSkladiste.getDriveService().files().delete(s).execute();
                System.out.println("Folder deleted.");
            } catch (IOException e) {
                System.out.println("Folder doesn't exist.");
            }
        }
    }

    @Override
//radi
    void makeFile(String ime, String path) {
        if (checkConnection()) {
            File file = new File();
            file.setName(ime);
            file.setParents(Collections.singletonList(path));

            if (checkItemExists(file.getName(), path)) {
                System.out.println("File already exists.");
                return;
            }

            if (rf.getNumberOfFiles() + 1 > rf.maxFiles) {
                System.out.println("File limit.");
                return;
            }

            for (String ex : rf.getNepodrzaneEkstenzije())
                if (ime.contains("." + ex) || rf.getNepodrzaneEkstenzije().size()==0) {
                    System.out.println("You cannot make file with this extension.");
                    return;
                }

            if (file.size() >= rf.getMaxVelicina()) {//nedovrseno
                System.out.println("Not enough free space.");
                return;
            }

            try {
                file = GDSkladiste.getDriveService().files().create(file)
                        .setFields("id, parents")
                        .execute();
                System.out.println("File " + ime + " successfully created on " + file.getParents());
                rf.NumberOfFiles++;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
//radi
    void makeListOfFiles(String ime, String path, Integer integer) {
        if (checkConnection()) {
            for (int i = 1; i <= integer; i++) {
                String[] split = ime.split("\\.");
                makeFile(split[0] + i + "." + split[1], path);
            }
        }
    }

    @Override
//radi
    void moveFile(String s, String s1) {
        if (checkConnection()) {
            if (checkItemExists(s, s1)) {
                System.out.println("File already exists in this folder.");
                return;
            }

            // Retrieve the existing parents to remove
            File file = null;
            try {
                file = GDSkladiste.getDriveService().files().get(s)
                        .setFields("parents")
                        .execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            StringBuilder previousParents = new StringBuilder();
            for (String parent : file.getParents()) {
                previousParents.append(parent);
                previousParents.append(',');
            }
            // Move the file to the new folder
            try {
                file = GDSkladiste.getDriveService().files().update(s, null)
                        .setAddParents(s1)
                        .setRemoveParents(previousParents.toString())
                        .setFields("id, parents")
                        .execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
//radi
    void deleteFile(String s, String s1) {
        if (checkConnection()) {
            try {
                GDSkladiste.getDriveService().files().delete(s).execute();
                rf.NumberOfFiles--;
                System.out.println("File deleted.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
//radi
    void downloadFile(String s) {

        if (connectedUser == null || connectedUser.isReader()) {
            System.out.println("You do not have privilege");
            return;
        }

        try {
            OutputStream outputStream = new ByteArrayOutputStream();
            GDSkladiste.getDriveService().files().get(s).executeMediaAndDownloadTo(outputStream);
            java.io.File f = new java.io.File(findNameByID(s));
            FileWriter fileWriter = new FileWriter(f.getPath());
            fileWriter.write(String.valueOf(outputStream));
            fileWriter.close();
            outputStream.close();
            System.out.println("File " + findNameByID(s) + " downloaded.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
//radi
    boolean connect(String user, String pass, String path) {

        String pageToken = null;
        do {
            FileList result = null;
            try {
                result = GDSkladiste.getDriveService().files().list()
                        .setQ("'" + path + "' in parents")
                        .setSpaces("drive")
                        .setFields("nextPageToken, files(id, name)")
                        .setPageToken(pageToken)
                        .execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

            for (File file : result.getFiles()) {
                if (file.getName().equals("users.json") || file.getName().equals("config.json"))

                    try {
                        OutputStream outputStream = new ByteArrayOutputStream();
                        GDSkladiste.getDriveService().files().get(file.getId()).executeMediaAndDownloadTo(outputStream);
                        String name = findNameByID(file.getId());
                        if(name.equals("users.json"))
                            uc = file.getId();
                        if(name.equals("config.json"))
                            cc = file.getId();
                        java.io.File f = new java.io.File(name);
                        FileWriter fileWriter = new FileWriter(f.getPath());
                        fileWriter.write(String.valueOf(outputStream));
                        fileWriter.close();
                        outputStream.close();
                        System.out.println("File " + f.getName() + " downloaded.");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
            pageToken = result.getNextPageToken();
        } while (pageToken != null);

        List<User> users = new ArrayList<>();

        try {
            rf = objectMapper.readValue(Paths.get("config.json").toFile(), RootFolder.class);
            users = Arrays.asList(objectMapper.readValue(Paths.get("users.json").toFile(), User[].class));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(user) &&
                    u.getPassword().equalsIgnoreCase(pass) &&
                    u.getPath().equalsIgnoreCase(path)) {
                connectedUser = u;
                System.out.println("User is connected");
                return true;
            }
        }
        System.out.println("User doesn't exist.");
        disconnect();
        return false;
    }

    @Override
//radi
    void makeUser(String username, String password, String path, String privilege) {

        if (connectedUser != null) {
            if (!connectedUser.isAdmin()) {
                System.out.println("You do not have privilege to make a user.");
                return;
            }
        } else {
            System.out.println("Error,you can't make user unless you login on your account with 'connect'");
            return;
        }

        if (checkItemExists("users.json",path))
            deleteFile(findID("users.json", path), path);

        User u = new User(username, password, privilege, path);
        connectedUser = u;

        java.io.File useri = new java.io.File("users.json");
        try {
            useri.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<User> users = new ArrayList<>();
        if (!(useri.length() == 0)) {
            try {
                users = Arrays.asList(objectMapper.readValue(Paths.get("users.json").toFile(), User[].class));

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        allUsers.clear();
        allUsers.addAll(users);
        allUsers.add(u);

        try {
            objectMapper.writeValue(Paths.get("users.json").toFile(), allUsers);
            uploadJSONFile("users.json", path);
            useri.delete();
            System.out.println("User " + username + " created.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
//radi
    void disconnect() {
        User us = connectedUser;

        if (connectedUser != null) {

            connectedUser = null;
            java.io.File u = new java.io.File("users.json");
            u.delete();
            uc = null;
            java.io.File c = new java.io.File("config.json");
            deleteFile(cc, us.getPath());
            cc=null;
            makeConfig(rf, us.getPath());
            c.delete();
            System.out.println("User is disconnected.");
        } else System.out.println("Error, there is no user connected.");
    }

    boolean checkItemExists(String s, String s1) {

        String pageToken = null;
        do {
            FileList result = null;
            try {
                result = GDSkladiste.getDriveService().files().list()
                        .setQ("name = '" + s + "'")
                        .setQ("'" + s1 + "' in parents")
                        .setSpaces("drive")
                        .setFields("nextPageToken, files(id, name)")
                        .setPageToken(pageToken)
                        .execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            for (File file : result.getFiles()) {
                if (file.getName().equalsIgnoreCase(s))
                    return true;
            }
            pageToken = result.getNextPageToken();
        } while (pageToken != null);

        return false;
    }

    void makeConfig(RootFolder rf, String path) {

        java.io.File config = new java.io.File("config.json");

        try {
            config.createNewFile();
            objectMapper.writeValue(Paths.get("config.json").toFile(), rf);
            uploadJSONFile("config.json", path);
            config.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void uploadJSONFile(String ime, String path) {

        File file = new File();
        file.setName(ime);
        java.io.File filePath = new java.io.File(ime);
        FileContent mediaContent = new FileContent("application/json", filePath);
        file.setParents(Collections.singletonList(path));
        try {
            file = GDSkladiste.getDriveService().files().create(file, mediaContent)
                    .setFields("id,parents")
                    .execute();
            System.out.println("JSON file: " + file.getId());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    String findNameByID(String s) {

        String pageToken = null;
        do {
            FileList result = null;
            try {
                result = GDSkladiste.getDriveService().files().list()
                        .setSpaces("drive")
                        .setFields("nextPageToken, files(id, name)")
                        .setPageToken(pageToken)
                        .execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            for (File file : result.getFiles()) {
                if (file.getId().equals(s)) {
                    return file.getName();
                }
            }
            pageToken = result.getNextPageToken();
        } while (pageToken != null);

        System.out.println("File not found.");
        return null;
    }

    String findID(String s,String s1) {

        String pageToken = null;
        do {
            FileList result = null;
            try {
                result = GDSkladiste.getDriveService().files().list()
                        .setQ("name = '" + s + "'")
                        .setQ("'" + s1 + "' in parents")
                        .setSpaces("drive")
                        .setFields("nextPageToken, files(id, name)")
                        .setPageToken(pageToken)
                        .execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            for (File file : result.getFiles()) {
                if (file.getName().equals(s)) {
                    return file.getId();
                }
            }
            pageToken = result.getNextPageToken();
        } while (pageToken != null);

        System.out.println("File not found.");
        return null;
    }

    boolean checkConnection() {

        if (connectedUser == null) {
            System.out.println("User is not connected");
            return false;
        }
        if (connectedUser.isReader() || connectedUser.isDownloader()) {
            System.out.println("You do not have privilege");
            return false;
        }
        return true;
    }
}