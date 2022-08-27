import java.io.InputStreamReader;
import java.util.Scanner;

public class Test {

    public static void main(String[] args) {

        System.out.println("Welcome, type your command");
        Scanner s = new Scanner(new InputStreamReader(System.in));
        String input = "";
        Class local;
        try {
            Class.forName("GDSkladiste");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Skladiste skladiste = RepositoryManager.getExporter("");
        String root = "";
        input = s.nextLine();

        while (!input.equalsIgnoreCase("exit")) {
            if (input.equalsIgnoreCase("initRoot")) {
                System.out.println("Type name");
                String name = s.nextLine();
                System.out.println("Type path");
                String path = s.nextLine();
                root = path;
                System.out.println("Make admin username");
                String username = s.nextLine();
                System.out.println("Make admin password");
                String password = s.nextLine();
                skladiste.initRoot(name, path,username,password);
            } else if (input.equalsIgnoreCase("initRootWithRestrictions")) {
                System.out.println("Type name");
                String name = s.nextLine();
                System.out.println("Type path");
                String path = s.nextLine();
                root = path;
                System.out.println("Set your root size");
                String maxSize = s.nextLine();
                System.out.println("Set your file limit");
                String maxFile = s.nextLine();
                System.out.println("Set your unwanted extensions");
                String extensions = s.nextLine();
                System.out.println("Make admin username");
                String username = s.nextLine();
                System.out.println("Make admin password");
                String password = s.nextLine();
                skladiste.initRootWithRestrictions(name, path, maxSize, maxFile, extensions,username,password);
            } else if (input.equalsIgnoreCase("makeUser")) {
                System.out.println("Type name");
                String name = s.nextLine();
                System.out.println("Type password");
                String password = s.nextLine();
                System.out.println("Type root path");
                String path = s.nextLine();
                if (!path.startsWith(root)) path = root + path;
                System.out.println("Type privilege");
                String privilege = s.nextLine();
                skladiste.makeUser(name, password, path, privilege);
            } else if (input.equalsIgnoreCase("search")) {
                System.out.println("Enter the item to be searched");
                String name = s.nextLine();
                System.out.println("Enter the directory where to search");
                String path = s.nextLine();
                if (!path.startsWith(root)) path = root + path;
                skladiste.search(name, path);
            } else if (input.equalsIgnoreCase("makeDirectory")) {
                System.out.println("Enter a name of directory you want to create");
                String name = s.nextLine();
                System.out.println("Enter a path where you want to put your directory");
                String path = s.nextLine();
                if (!path.startsWith(root)) path = root + path;
                skladiste.makeDirectory(name, path);
            } else if (input.equalsIgnoreCase("moveDirectory")) {
                System.out.println("Enter a path with a name of directory you want to move");
                String name = s.nextLine();
                System.out.println("Enter a path where you want to put your directory");
                String path = s.nextLine();
                if (!path.startsWith(root)) path = root + path;
                skladiste.moveDirectory(name, path);
            } else if (input.equalsIgnoreCase("makeListOfDirectories")) {
                System.out.println("Enter a name of directories you want to create");
                String name = s.nextLine();
                System.out.println("Enter a path where you want to put your directories");
                String path = s.nextLine();
                if (!path.startsWith(root)) path = root + path;
                System.out.println("How many dirs do you want to create?");
                Integer num = Integer.parseInt(s.nextLine());
                skladiste.makeListOfDirectories(name, path, num);
            } else if (input.equalsIgnoreCase("deleteDirectory")) {
                System.out.println("Enter a name of directory you want to delete");
                String name = s.nextLine();
                System.out.println("Enter a path where you want to delete your directory");
                String path = s.nextLine();
                if (!path.startsWith(root)) path = root + path;
                skladiste.deleteDirectory(name, path);
            } else if (input.equalsIgnoreCase("makeFile")) {
                System.out.println("Enter a name of file you want to create");
                String name = s.nextLine();
                System.out.println("Enter a path where you want to put your file");
                String path = s.nextLine();
                if (!path.startsWith(root)) path = root + path;
                skladiste.makeFile(name, path);
            } else if (input.equalsIgnoreCase("makeListOfFiles")) {
                System.out.println("Enter a name of files you want to create");
                String name = s.nextLine();
                System.out.println("Enter a path where you want to put your files");
                String path = s.nextLine();
                System.out.println("How many files do you want to create?");
                Integer num = Integer.parseInt(s.nextLine());
                skladiste.makeListOfFiles(name, path, num);
            } else if (input.equalsIgnoreCase("moveFile")) {
                System.out.println("Enter a path with a name of file you want to move");
                String name = s.nextLine();
                System.out.println("Enter a path where you want to put your file");
                String path = s.nextLine();
                if (!path.startsWith(root)) path = root + path;
                skladiste.moveFile(name, path);
            } else if (input.equalsIgnoreCase("deleteFile")) {
                System.out.println("Enter a name of file you want to delete");
                String name = s.nextLine();
                System.out.println("Enter a path where you want to delete your file");
                String path = s.nextLine();
                if (!path.startsWith(root)) path = root + path;
                skladiste.deleteFile(name, path);
            } else if (input.equalsIgnoreCase("sort")) {
                System.out.println("Enter a path of directory you want to sort");
                String path = s.nextLine();
                if (!path.startsWith(root)) path = root + path;
                System.out.println("How do you want to sort by: name, date modified, date created?");
                String type = s.nextLine();
                System.out.println("How do you want to sort by: ascending or descending?");
                String sort = s.nextLine();
                skladiste.sort(path, type, sort);
            } else if (input.equalsIgnoreCase("list")) {
                System.out.println("Enter a folder you want to see");
                String path = s.nextLine();
                skladiste.list(path);
            } else if (input.equalsIgnoreCase("listFiles")) {
                System.out.println("Enter a folder you want to see");
                String path = s.nextLine();
                skladiste.listFiles(path);
            } else if (input.equalsIgnoreCase("listFolders")) {
                System.out.println("Enter a folder you want to see");
                String path = s.nextLine();
                skladiste.listFolders(path);
            } else if (input.equalsIgnoreCase("connect")) {
                System.out.println("Enter username");
                String user = s.nextLine();
                System.out.println("Enter password");
                String password = s.nextLine();
                System.out.println("Enter root");
                String path = s.nextLine();
                skladiste.connect(user, password, path);
            } else if (input.equalsIgnoreCase("disconnect")) {
                skladiste.disconnect();
            }else if (input.equalsIgnoreCase("downloadFile")) {
                System.out.println("Enter ID of a file you want to download");
                String name = s.nextLine();
                skladiste.downloadFile(name);
            } else
                System.out.println("Unknown command");

            input = s.nextLine();
        }
    }
}
