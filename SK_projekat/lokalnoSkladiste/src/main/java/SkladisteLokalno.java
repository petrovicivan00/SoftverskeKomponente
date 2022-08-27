import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class SkladisteLokalno extends Skladiste {


    static {
        RepositoryManager.registerExporter(new SkladisteLokalno());
    }

    ObjectMapper objectMapper = new ObjectMapper();
    User connectedUser = null;
    List<User> allUsers = new ArrayList<>();
    RootFolder rf = null;

    public SkladisteLokalno() {

    }

    public static long getFileCreationEpoch(File file) {
        try {
            BasicFileAttributes attr = Files.readAttributes(file.toPath(),
                    BasicFileAttributes.class);
            return attr.creationTime()
                    .toInstant().toEpochMilli();
        } catch (IOException e) {
            throw new RuntimeException(file.getAbsolutePath(), e);
        }
    }

    @Override
    void initRoot(String ime, String path, String username, String password) { //

        File root = new File(path + "/" + ime);
        rf = new RootFolder(root.getName(), root.getPath());
        rf.setMaxVelicina(Integer.MAX_VALUE);
        rf.setNepodrzaneEkstenzije(new ArrayList<>());
        rf.setMaxFiles(Integer.MAX_VALUE);
        rf.setNumberOfFiles(0);

        if (!root.exists()) {
            root.mkdirs();
            System.out.println("Root " + root.getName() + " created on " + root.getPath());
            connectedUser = new User(username, password, "admin", path);
            makeUser(username, password, root.getPath(), "admin");

            File config = new File(root.getPath() + "/config.json");

            try {
                config.createNewFile();
                objectMapper.writeValue(Paths.get(root.getPath() + "/" + "config.json").toFile(), rf);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            System.out.println("Root already exists");
        }
    }

    @Override
    void initRootWithRestrictions(String ime, String path, String maxSize, String maxFiles, String extension, String username, String password) { //

        File root = new File(path + "/" + ime);
        rf = new RootFolder(root.getName(), root.getPath());
        rf.setMaxVelicina(Long.parseLong(maxSize));
        rf.setNepodrzaneEkstenzije(List.of(extension.split(",")));
        rf.setMaxFiles(Integer.parseInt(maxFiles));
        rf.setNumberOfFiles(0);

        if (!root.exists()) {
            root.mkdirs();
            System.out.println("Root " + root.getName() + " created on " + root.getPath());
            connectedUser = new User(username, password, "admin", path);
            makeUser(username, password, root.getPath(), "admin");

            File config = new File(root.getPath() + "/config.json");

            try {
                config.createNewFile();
                objectMapper.writeValue(Paths.get(root.getPath() + "/" + "config.json").toFile(), rf);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            System.out.println("Root already exists");
        }
    }

    @Override
    void makeUser(String username, String password, String path, String privilege) { //

        if (connectedUser != null) {
            if (!connectedUser.isAdmin()) {
                System.out.println("You do not have privilege to make a user.");
                return;
            }
        } else {
            System.out.println("Error,you can't make user unless you login on your account with 'connect'");
            return;
        }

        User u = new User(username, password, privilege, path);
        connectedUser = u;
        File useri = new File(rf.getPath() + "/users.json");
        try {
            useri.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<User> users = new ArrayList<>();
        if (!(useri.length() == 0)) {
            try {
                users = Arrays.asList(objectMapper.readValue(Paths.get(rf.getPath() + "/" + "users.json").toFile(), User[].class));

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        allUsers.clear();
        allUsers.addAll(users);
        allUsers.add(u);
        try {
            objectMapper.writeValue(Paths.get(rf.getPath() + "/" + "users.json").toFile(), allUsers);
            System.out.println("User " + username + " created.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    void search(String ime, String path) { // test
        if (checkConnection()) {
            File p = new File(path);
            File[] list = p.listFiles();
            if (list != null) {
                for (File f : list) {
                    if (f.isDirectory())
                        search(ime, f.getPath());
                    if (ime.equalsIgnoreCase(f.getName())) {
                        System.out.println("Item found at " + f.getPath());
                    }
                }
            } else {
                System.out.println("Directory is empty.");
            }
        }
    }

    @Override
    void list(String s) { // test
        if (checkConnection()) {
            listFolders(s);
            listFiles(s);
        }
    }

    @Override
    void listFiles(String s) { // test
        if (checkConnection()) {
            File file = new File(s);
            File[] files = file.listFiles();
            if (files == null) {
                System.out.println("No files found.");
                return;
            }
            for (File f : files) {
                if (f.isFile())
                    System.out.println(f.getName());
            }
        }
    }

    @Override
    void listFolders(String s) { // test
        if (checkConnection()) {
            File file = new File(s);
            File[] files = file.listFiles();
            if (files == null) {
                System.out.println("No folders found.");
                return;
            }
            for (File f : files) {
                if (f.isDirectory())
                    System.out.println(f.getName());
            }
        }
    }

    @Override
    void sort(String path, String funk, String type) { // test
        if (checkConnection()) {
            File folder = new File(path);

            if (!folder.isDirectory())
                return;
            File[] fileList = folder.listFiles();
            if ((type.equalsIgnoreCase("asc")) && funk.equalsIgnoreCase("name")) {
                Arrays.sort(fileList);
            } else if ((type.equalsIgnoreCase("desc") && funk.equalsIgnoreCase("name"))) {
                Arrays.sort(fileList, Collections.reverseOrder());
            } else if ((type.equalsIgnoreCase("asc") && funk.equalsIgnoreCase("date modified"))) {
                Arrays.sort(fileList, new Comparator<File>() {
                    public int compare(File f1, File f2) {
                        return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
                    }
                });
            } else if ((type.equalsIgnoreCase("desc") && funk.equalsIgnoreCase("date modified"))) {
                Arrays.sort(fileList, new Comparator<File>() {
                    public int compare(File f1, File f2) {
                        return Long.valueOf(f2.lastModified()).compareTo(f1.lastModified());
                    }
                });
            } else if ((type.equalsIgnoreCase("asc") && funk.equalsIgnoreCase("date created"))) {
                Arrays.sort(fileList, new Comparator<File>() {
                    public int compare(File f1, File f2) {
                        long l1 = getFileCreationEpoch(f1);
                        long l2 = getFileCreationEpoch(f2);
                        return Long.valueOf(l1).compareTo(l2);
                    }
                });
            } else if ((type.equalsIgnoreCase("desc") && funk.equalsIgnoreCase("date created"))) {
                Arrays.sort(fileList, new Comparator<File>() {
                    public int compare(File f1, File f2) {
                        long l1 = getFileCreationEpoch(f1);
                        long l2 = getFileCreationEpoch(f2);
                        return Long.valueOf(l2).compareTo(l1);
                    }
                });
            } else {
                System.out.println("Unknown sort type");
                return;
            }
            for (File file : fileList) {
                System.out.println(file.getName());
            }
        }
    }

    @Override
    void makeDirectory(String name, String path) { // test
        if (checkConnection()) {
            File dir = new File(path + "/" + name);

            if (rf.getPath().length() + dir.length() <= rf.getMaxVelicina()) {

                if (!dir.exists()) {
                    dir.mkdirs();
                    System.out.println("Directory " + dir.getName() + " successfully created on " + dir.getPath());
                } else {
                    System.out.println("Directory already exists");
                }
            } else {
                System.out.println("Not enough free space.");
            }
        }
    }

    @Override
    void makeListOfDirectories(String name, String path, Integer integer) { //test
        if (checkConnection()) {
            for (int i = 1; i <= integer; i++) {
                File f = new File(path + "/" + name + i);

                if (rf.getPath().length() + f.length() <= rf.getMaxVelicina()) {
                    f.mkdirs();
                    System.out.println("Directory " + f.getName() + " successfully created on " + f.getPath());
                } else {
                    System.out.println("Not enough free space.");
                }
            }
        }
    }

    @Override
    void moveDirectory(String name, String path) {// test
        if (checkConnection()) {
            File from = new File(name);
            File to = new File(path);
            File[] files = from.listFiles();

            if (files == null) {
                File f = new File(path + "/" + from.getName());
                f.mkdirs();
                from.delete();
                System.out.println("Directory successfully moved.");
            } else {
                for (File f : files) {
                    File temp = new File(path + "/" + from.getName());
                    try {
                        Files.move(from.toPath(), temp.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
                System.out.println("Directory successfully moved.");
            }
        }
    }

    @Override
    void deleteDirectory(String s, String s1) { // test
        if (checkConnection()) {
            File dir = new File(s1 + "/" + s);
            File[] files = dir.listFiles();
            if (dir.exists()) {
                for (File f : files) {
                    f.delete();
                }
                dir.delete();
                System.out.println("Directory " + dir.getName() + " successfully deleted.");
            } else {
                System.out.println("Directory doesn't exist.");
            }
        }
    }

    @Override
    void makeFile(String s, String s1) { // test
        if (checkConnection()) {

            File f = new File(s1 + "/" + s);

            for (String ex : rf.getNepodrzaneEkstenzije())
                if (f.getName().contains("." + ex)) {
                    System.out.println("You cannot make file with this extension.");
                    return;
                }
            if (rf.getNumberOfFiles() + 1 > rf.maxFiles) {
                System.out.println("File limit.");
                return;
            }
            if (rf.getPath().length() + f.length() <= rf.getMaxVelicina()) {
                rf.setMaxVelicina(rf.getMaxVelicina() - f.length());
                if (!f.exists()) {
                    try {
                        f.createNewFile();
                        rf.NumberOfFiles++;
                        System.out.println("File " + f.getName() + " successfully created on " + f.getPath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("File already exists");
                }
            } else {
                System.out.println("Not enough free space.");
            }
        }
    }

    @Override
    void makeListOfFiles(String s, String s1, Integer integer) { // ne radi test
        if (checkConnection()) {
            for (int i = 1; i <= integer; i++) {
                String[] split = s.split("\\.");
                makeFile(split[0] + i + "." + split[1], s1);
            }
        }
    }

    @Override
    void moveFile(String s, String s1) {
        if (checkConnection()) {
            File from = new File(s);
            File to = new File(s1);

            try {
                Files.move(from.toPath(), to.toPath(), StandardCopyOption.REPLACE_EXISTING);
                System.out.println("File moved successfully.");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    void deleteFile(String s, String s1) { // radi
        if (checkConnection()) {
            File file = new File(s1 + "/" + s);

            if (file.exists()) {
                file.delete();
                rf.NumberOfFiles--;
                System.out.println("File " + file.getName() + " successfully deleted.");
            } else {
                System.out.println("File doesn't exists");
            }
        }
    }

    @Override
    void downloadFile(String s) {

    }

    @Override
    boolean connect(String user, String pass, String path) {

        List<User> users = new ArrayList<>();
        try {
            users = Arrays.asList(objectMapper.readValue(Paths.get(path + "/" + "users.json").toFile(), User[].class));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        for (User u : users) {
            //System.out.println(u.getUsername() + " " + u.getPassword() + " " + u.getPrivilege() + " " + u.getPath());
            if (u.getUsername().equalsIgnoreCase(user) &&
                    u.getPassword().equalsIgnoreCase(pass) &&
                    u.getPath().equalsIgnoreCase(path)) {
                connectedUser = u;
                System.out.println("User is connected");
                //System.out.println(u.getUsername() + " " + u.getPassword());
                return true;
            }
        }
        System.out.println("User doesn't exist.");
        return false;
    }

    @Override
    void disconnect() {
        try {
            objectMapper.writeValue(Paths.get(rf.getPath() + "/" + "config.json").toFile(), rf);
        } catch (IOException e) {
            e.printStackTrace();
        }
        connectedUser = null;
        System.out.println("User is disconnected");
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
