import javax.xml.bind.JAXB;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ChainTracker {
    public static void main(String[] args) {
        String dataFilePath = "./src/main/java/ChainFiles";
        Path dataFiles = Paths.get(dataFilePath);
        Chain pokemonChain = new Chain();
        String currentFile = "";

        // Reading File Section
        if (!Files.exists(dataFiles)) {
            new File(dataFilePath).mkdirs();
        }
        try {
            int fileCount = new File(dataFilePath).listFiles().length;
            if (fileCount > 0) {
                System.out.println("Available files:");
                try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(dataFiles)) {
                    List<String> fileNames = new ArrayList<>();
                    for (Path p : directoryStream) {
                        String fileName = p.getFileName().toString().replace(".xml", "");
                        fileNames.add(fileName);
                        System.out.printf("%s%n", fileName);
                    }
                    System.out.print("New\nExit");
                    boolean validSelection = false;
                    while (!validSelection) {
                        System.out.print("File name to load: ");
                        Scanner input = new Scanner(System.in);
                        String selection = input.next();
                        if (fileNames.contains(selection)) {
                            validSelection = true;
                            Path filePath = Paths.get(dataFilePath + "/" + selection + ".xml");
                            try (BufferedReader file = Files.newBufferedReader(filePath)) {
                                pokemonChain = JAXB.unmarshal(file, Chain.class);
                                currentFile = selection;
                                System.out.println("Chains Loaded.");
                            }
                        } else if (selection.equalsIgnoreCase("exit")) {
                            validSelection = true;
                        } else if (selection.equalsIgnoreCase("new")) {
                            System.out.println("Please enter a file name to be created: ");
                            Scanner fileInput = new Scanner(System.in);
                            String desiredName = fileInput.next();
                            currentFile = desiredName;
                        }  else {
                            System.out.println("Invalid selection\nPlease make a valid selection");
                        }
                    }
                }
            } else {
                System.out.println("No files to load\nPlease enter a file name to be created: ");
                Scanner input = new Scanner(System.in);
                String selection = input.next();
                currentFile = selection;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        // Main functionality
        String menuChoice = "";
        do {
            System.out.print("""
                    Enter the number of the function you wish to perform:
                    1) Add Links
                    2) View Links
                    3) Edit Link
                    4) Remove Link
                    0) Exit
                    Selection:%s""");
            Scanner input = new Scanner(System.in);
            menuChoice = input.next();
            if (menuChoice.equals("1")) {
                AddLinks(pokemonChain);
            } else if (menuChoice.equals("2")) {
                ViewLinks(pokemonChain);
            } else if (menuChoice.equals("4")) {
                RemoveLink(pokemonChain);
            }
        } while (!menuChoice.equals("0"));
    }

    public static void AddLinks (Chain currentChain) {
        boolean active = true;
        boolean first = true;
        System.out.println("Hit enter with no entry for any pokemon to exit.");
        do {
            System.out.printf("%nStarting Pokemon: ");
            Scanner input = new Scanner(System.in);
            String keyPokemon = input.next();
            if (keyPokemon.isEmpty()) {
                active = false;
            } else {
                keyPokemon = keyPokemon.substring(0,1).toUpperCase()+keyPokemon.substring(1);
                System.out.printf("%nNext Pokemon: ");
                String valuePokemon = input.next();
                if (valuePokemon.isEmpty()) {
                    active = false;
                } else {
                    valuePokemon = valuePokemon.substring(0,1).toUpperCase()+valuePokemon.substring(1);
                    String pokemonLocation = "";
                    if (first) {
                        System.out.printf("%nChain starting location: ");
                        pokemonLocation = input.next();
                        pokemonLocation = pokemonLocation.substring(0,1).toUpperCase()+pokemonLocation.substring(1);
                        first = false;
                    }
                    currentChain.getChain().add(new ChainLink(keyPokemon, valuePokemon, pokemonLocation));
                }
            }
        } while (active);
    }

    public static void ViewLinks (Chain currentChain) {
        System.out.printf("%nAll Links");
        int counter = 0;
        for (ChainLink link: currentChain.getChain()) {
            counter++;
            if (counter % 5 == 0) {
                System.out.printf("%20s -> %20s%n", link.getKey(), link.getValue());
            } else {
                System.out.printf("%20s -> %20s | ", link.getKey(), link.getValue());
            }
        }
    }

    public static void RemoveLink (Chain currentChain) {
        System.out.printf("%nEnter the name of the starting pokemon of the link to be removed: ");
        Scanner input = new Scanner(System.in);
        String keyPokemon = input.next();
        currentChain.getChain().removeIf(key -> (key.getKey().equals(keyPokemon)));
        System.out.printf("Removed any links (if any) with %s as the starting link.%n", keyPokemon);
    }
}
