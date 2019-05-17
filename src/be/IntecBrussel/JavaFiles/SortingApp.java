package be.IntecBrussel.JavaFiles;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//TODO: Mooi gebruik van commentaar, dit maakt je code zeer leesbaar.

//TODO: Ik heb een .gitignore gemaakt met daarin de nodige excludes voor user/project specific files.
// Deze zal de getrackte files niet hiden. Maar dan hebben jullie een template voor het volgende project.

//TODO: Probeer meer aandacht te besteden aan de benaming van je variabelen.

public class SortingApp {

    //TODO: IOExeption wordt nooit gesmeten, dus throws IOException mag weg
    public static void main(String[] args) throws IOException {
        Path unsort = Paths.get("C:/data/unsorted");
        Path sort = Paths.get("C:/data/sorted");

        createMaps(unsort, sort);
        moveFiles(unsort, sort);
        makeAReadMe(unsort, sort);

    }

    //methode to creates the directory's

    public static void createMaps(Path unsorted, Path sorted) {
        try (Stream<Path> subPaths = Files.walk(unsorted, 3)) {

            // the following code will make the directory's

            //A List of files in String format for easy manipulation.

            //TODO: Probeer de Java naming convention te volgen, zeer mooi gebruik van lambdas en Streams.
            List<String> SubPathList = subPaths.filter(Files::isRegularFile)
                                                .map(Objects::toString)
                                                .collect(Collectors.toList());

            //this hashSet will contain all the extension that are present in the above list

            HashSet<String> type = new HashSet<>();

            //the following for loop will extract the extension from the files and add them to the above HashSet

            for (String list : SubPathList) {
                int i = list.lastIndexOf('.');  //TODO: Mooi gebruik SonarLint
                if (i >= 0) {
                    type.add(list.substring(i + 1));
                }
            }

            //this for loop will then check if the directory already exist and then make them if the dont.
            for (String direct : type) {
                File dir = new File(String.valueOf(sorted.resolve(direct)));
                if (!dir.exists()) {
                    dir.mkdir();  //TODO: Probeer iets nuttig te doen met de return van mkdir methode.

                }
            }

        } catch (IOException | NullPointerException e) {
            System.out.println("something went wrong");
            System.out.println(e.getLocalizedMessage());
        }
    }

    //methode to moves the Files towards the directory's

    //TODO: Probeer de methode in kleinere stukken te verdelen,
    // deze heeft een te hoge complexiteit wat onderhoud ervan moeilijker zal maken.
    public static void moveFiles(Path unsorted, Path sorted) {

        try(Stream<Path> subPathsUnsort = Files.walk(unsorted, 3);
            //TODO: subPathsSort is redundant dus deze mag verwijderd worden.
            Stream<Path> subPathsSort = Files.walk(sorted)){
            //the following code will move the files towards the corresponding directory

            //these arrays will contain the files that will move

            //TODO: Door je collect(Collectors.toList()) methode is je explicit cast hier overbodig.
            List<Path> SubPathListUnsorted = (List<Path>) subPathsUnsort.filter(Files::isRegularFile)
                                                                        .collect(Collectors.toList());

            ArrayList<File> files= new ArrayList<>();

            for (Path path: SubPathListUnsorted){
                //TODO: Had ook gekunnen: path.getFileName().toFile();
                File fileFromPath = new File(String.valueOf(path.getFileName()));
                files.add(fileFromPath);
            }

            //and this array will contain the directory's where the file's will move to

            File sort = sorted.toFile();
            File[] sorty = sort.listFiles();

            //the following for loops will iterate over the previous two arrays

           for (File filesToMove : files) {
                for (File dir : sorty) {

                    //checks if the file extension is the same as the directory's name and if so copys the file and places it in the corresponding directory

                    if (filesToMove.getName().contains(dir.getName().toLowerCase())) {

                        //TODO: Het wordt sterk aangeraden om de klasse Path te gebruiken bij nieuwe code.
                        // Indien nodig kan je nog steeds de omzetting doen om gebruik te maken van de File methoden.

                        //file1 will hold the old file location and file2 will hold the location of where the file will go
                        //TODO: Het wordt afgeraden om hard delimiters te gebruiken vanwege platform onafhankelijkheid
                        File file1 = unsorted.resolve(filesToMove.toPath()).toFile();
                        File file2 = new File(dir.getAbsolutePath() + "\\" + file1.getName());

                        // will check is the file alreadsy exist and if doesnt it will create it

                        if (!file2.exists()) {
                            Files.copy(file1.toPath(), file2.toPath());
                            if (file2.renameTo(new File(dir + "\\" + file2.getName()))) {
                                System.out.println("filemade");
                            }
                        } else {
                            System.out.println("file already exist");
                        }

                    }
                }
            }

        } catch (IOException | NullPointerException e) {
            //TODO: java.nio.file.NoSuchFileException: C:\data\unsorted\MagPi01.pdf
            // Dit komt door de mappen die niet vernoemd worden in je File path.
            System.out.println("something went wrong");
            System.out.println(e.getMessage());
        }
    }

    //and a methode to make a summary of all the files found and if there readable or writable

    public static void makeAReadMe(Path unsorted, Path sorted){

        //the file that will hold the summary
        //TODO: sorted.resolve("Summary.txt").toFile(); was ook mogelijk.
        File read = new File(String.valueOf(sorted.resolve("Summary.txt")));

        //the following code will hold the FileWriter and PrintWriter

        try(FileWriter writer = new FileWriter(read,true);
            PrintWriter print = new PrintWriter(writer)){

            File unsort = unsorted.toFile();
            File[] unsorty = unsort.listFiles();

            //summary header
            print.println("name | readable | writeable |");

            //will check if the file is not null and wil score the according file if its readable and/or writeAble

            if(unsorty != null) {
                for (File files : unsorty){ //TODO: files als variabele naam zorgt voor verwarring in je code.

                    //adds to the summaary.txt

                    if (files.canRead()){
                        //TODO: Dit was ook een mogelijkheid
                        String attr = files.canWrite() ? " | * | * | " : " | * | / | ";
                        print.println(files.getName() + attr);
                    }else if (!files.canRead()) {
                        if (files.canWrite()) {
                            print.println(files.getName() + " | / | * | ");
                        } else{
                            print.println(files.getName() + " | / | / | ");
                        }
                    }
                }
            }

        }catch(IOException e){
            System.out.println("something went wrong");
            System.out.println(e.getMessage());
        }
    }

    //TODO: Goede effort, probeer echter goed op benamingen en onnodige complexiteit te letten.


    // i didnt get all the files including the hidden files but got the basic concept of the assignment and will work on my IO in the future.
}
