import javax.xml.bind.JAXB;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


public class ChainTracker {
    public static final String RESET = "\u001B[0m";
    public static final String GREEN = "\u001B[32m";
    public static final String GRAY = "\u001b[38;5;244m";
    public static final String RED = "\u001B[31m";
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
            int fileCount = Objects.requireNonNull(new File(dataFilePath).listFiles()).length;
            if (fileCount > 0) {
                System.out.println("Available files:");
                try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(dataFiles)) {
                    List<String> fileNames = new ArrayList<>();
                    for (Path p : directoryStream) {
                        String fileName = p.getFileName().toString().replace(".xml", "");
                        fileNames.add(fileName);
                        System.out.printf("%s%n", fileName);
                    }
                    System.out.println("New\nExit");
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
                            validSelection = true;
                            System.out.println("Please enter a file name to be created: ");
                            Scanner fileInput = new Scanner(System.in);
                            currentFile = fileInput.next();
                        }  else {
                            System.out.println("Invalid selection\nPlease make a valid selection");
                        }
                    }
                }
            } else {
                System.out.println("No files to load\nPlease enter a file name to be created: ");
                Scanner input = new Scanner(System.in);
                currentFile = input.next();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        // Main functionality
        String menuChoice = "";
        do {
            System.out.print("""
                    \nEnter the number of the function you wish to perform:
                    1) Add Links
                    2) View Links
                    3) Edit Link
                    4) Remove Link
                    5) View Chains
                    6) View Locations
                    7) Find Pokemon
                    0) Exit
                    Selection:\s""");
            Scanner input = new Scanner(System.in);
            menuChoice = input.next();
            if (menuChoice.equals("1")) {
                AddLinks(pokemonChain);
            } else if (menuChoice.equals("2")) {
                ViewLinks(pokemonChain);
            } else if (menuChoice.equals("3")) {
                EditLink(pokemonChain);
            } else if (menuChoice.equals("4")) {
                RemoveLink(pokemonChain);
            } else if (menuChoice.equals("5")) {
                ViewChains(pokemonChain);
            } else if (menuChoice.equals("6")) {
                ViewLocations(pokemonChain);
            } else if (menuChoice.equals("7")) {
                FindChains(pokemonChain);
            }
        } while (!menuChoice.equals("0"));
        if (!pokemonChain.getChain().isEmpty()) {
            Path saveFile = Paths.get(dataFilePath+"/"+currentFile+".xml");
            SaveChain(pokemonChain, saveFile);
        }
    }

    public static void AddLinks (Chain currentChain) {
        boolean active = true;
        boolean first = true;
        System.out.printf("%nHit enter with no entry to exit.%n");
        System.out.print("Starting Pokemon: ");
        Scanner input = new Scanner(System.in);
        String keyPokemon = input.nextLine();
        String startingPokemon = keyPokemon;
        if (!keyPokemon.isEmpty() && !CheckExisting(currentChain, keyPokemon)) {
            do {
                keyPokemon = keyPokemon.substring(0, 1).toUpperCase() + keyPokemon.substring(1);
                System.out.print("Next Pokemon: ");
                String valuePokemon = input.nextLine();
                if (valuePokemon.isEmpty()) {
                    active = false;
                } else {
                    valuePokemon = valuePokemon.substring(0, 1).toUpperCase() + valuePokemon.substring(1);
                    String pokemonLocation = "N/A";
                    if (first) {
                        System.out.print("Chain starting location: ");
                        pokemonLocation = input.nextLine();
                        pokemonLocation = pokemonLocation.substring(0, 1).toUpperCase() + pokemonLocation.substring(1);
                        first = false;
                    }
                    ChainLink toAdd = new ChainLink();
                    toAdd.setKey(keyPokemon);
                    toAdd.setValue(valuePokemon);
                    toAdd.addLocation(pokemonLocation);
                    currentChain.getChain().add(toAdd);
                    keyPokemon = valuePokemon;
                    if (CheckExisting(currentChain, valuePokemon)) {
                        WriteChain(currentChain, startingPokemon);
                        active = false;
                    }
                }
            } while (active);
        } else if (CheckExisting(currentChain, keyPokemon)) {
            WriteChain(currentChain, keyPokemon);
        }
    }

    private static void WriteChain(Chain currentChain, String keyPokemon) {
        String chainOutput = CreateChainOutput(currentChain, new StringBuilder(), keyPokemon, 0).toString();
        String lastLink = chainOutput.substring(chainOutput.lastIndexOf(" ") + 1);
        String loopInfo = "";
        if (lastLink.equals("???")) {
            chainOutput = colorizeChain(chainOutput, GREEN);
        } else {
            loopInfo = " that ends in a loop";
            chainOutput = colorizeChain(chainOutput, RED);
        }
        System.out.printf("You've entered an existing chain%s:%n%s%n", loopInfo, chainOutput);
    }

    public static void ViewLinks (Chain currentChain) {
        System.out.printf("%nKnown Links%n");
        int counter = 0;
        for (ChainLink link: currentChain.getChain()) {
            counter++;
            String chainLink = String.format("%s -> %s", link.getKey(), link.getValue());
            if (counter % 5 == 0) {
                System.out.printf("%-28s%n", chainLink);
            } else {
                System.out.printf("%-28s | ", chainLink);
            }
        }
        System.out.println();
    }

    public static void RemoveLink (Chain currentChain) {
        System.out.printf("%nEnter the name of the starting pokemon of the link to be removed: ");
        Scanner input = new Scanner(System.in);
        String keyPokemon = input.next();
        currentChain.getChain().removeIf(key -> (key.getKey().equals(keyPokemon)));
        System.out.printf("Removed any links (if any) with %s as the starting link.%n", keyPokemon);
    }

    public static void SaveChain (Chain currentChain, Path filePath) {
        try (BufferedWriter output = Files.newBufferedWriter(filePath)) {
            JAXB.marshal(currentChain, output);
            System.out.printf("Chain information saved to %s", filePath.getFileName());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void EditLink (Chain currentChain) {
        System.out.printf("%nEnter the name of the starting pokemon of the link to be edited: ");
        Scanner input = new Scanner(System.in);
        String keyPokemon = input.nextLine();
        ChainLink workingLink = currentChain.getLinkByKey(keyPokemon);
        if (!workingLink.getKey().equals("No Link")) {
            System.out.printf("""
                    %nEnter the number of the function you wish to perform:
                    1) Change Start (%s)
                    2) Change End (%s)
                    3) Add Location (%s)
                    4) Remove Location
                    Selection:\s""", workingLink.getKey(), workingLink.getValue(), workingLink.getLocations());
            String option = input.nextLine();
            if (option.equals("1")) {
                System.out.print("Desired Start Pokemon: ");
                String change = input.nextLine();
                if (!change.isEmpty()) {
                    workingLink.setKey(change);
                    System.out.println("Change updated.");
                }
            } else if (option.equals("2")) {
                System.out.print("Desired End Pokemon: ");
                String change = input.nextLine();
                if (!change.isEmpty()) {
                    workingLink.setValue(change);
                    System.out.println("Change completed.");
                }
            } else if (option.equals("3")) {
                System.out.print("Desired Location: ");
                String change = input.nextLine();
                if (!change.isEmpty()) {
                    if (workingLink.getLocations().contains("N/A")) {
                        workingLink.removeLocation("N/A");
                    }
                    workingLink.addLocation(change);
                    System.out.println("Change completed.");
                }
            } else if (option.equals("4")) {
                System.out.print("Location to remove: ");
                String change = input.nextLine();
                if (!change.isEmpty() && workingLink.getLocations().contains(change)) {
                    workingLink.removeLocation(change);
                    System.out.println("Change completed.");
                } else {
                    System.out.println("Could not remove that location, returning to main menu");
                }
            } else {
                System.out.println("Invalid selection, returning to main menu.");
            }
        } else {
            System.out.println("Link not found, returning to main menu.");
        }
    }

    public static boolean CheckExisting(Chain currentChain, String key) {
        String value = currentChain.getValueByKey(key);
        return !value.equals("No Value");
    }

    public static StringBuilder CreateChainOutput (Chain currentChain, StringBuilder currentOutput, String key, int iteration) {
        if(iteration == 0) {
            currentOutput.append(key);
        }
        String next = currentChain.getValueByKey(key);
        boolean inOutput = currentOutput.toString().matches(String.format(".*(%s).*", next));
        if (currentChain.getKeys().contains(key) && !inOutput) {
            CreateChainOutput(currentChain, currentOutput.append(" -> ").append(currentChain.getValueByKey(key)), currentChain.getValueByKey(key), iteration + 1);
        } else if (inOutput) {
            currentOutput.append(" -> ").append(currentChain.getValueByKey(key));
        } else {
            currentOutput.append(" -> ").append("???");
        }
        return currentOutput;
    }

    public static void ViewChains (Chain currentChain) {
        int iterator = 0;
        System.out.printf("%nAll currently known chains:%n");
        for (ChainLink link: currentChain.getChain()) {
            if (!link.getLocations().contains("N/A")) {
                iterator++;
                String chainOutput = CreateChainOutput(currentChain, new StringBuilder(), link.getKey(), 0).toString();
                String lastLink = chainOutput.substring(chainOutput.lastIndexOf(" ") + 1);
                if (lastLink.equals("???")) {
                    chainOutput = colorizeChain(chainOutput, GREEN);
                } else {
                    chainOutput = colorizeChain(chainOutput, RED);
                }
                System.out.printf("%d) %s%n", iterator, chainOutput);
            }
        }
    }

    public static void ViewLocations (Chain currentChain) {
        System.out.printf("%nAll locations with chain starters:%n");
        for (String location: currentChain.getLocations()) {
            List<String> pokemonAtLocation = new ArrayList<>();
            for (ChainLink link: currentChain.getChain()) {
                if (link.getLocations().contains(location)) {
                    pokemonAtLocation.add(link.getKey());
                }
            }
            System.out.printf("%s: %s%n", colorText(location, GREEN, RESET), String.join(", ", pokemonAtLocation));
        }
    }

    public static void FindChains (Chain currentChain) {
        System.out.printf("%nWhich pokemon are you looking to find? ");
        Scanner input = new Scanner(System.in);
        String desiredPokemon = input.nextLine();
        if (currentChain.getValues().contains(desiredPokemon)) {
            System.out.printf("%nAll potential chains leading to %s:%n", desiredPokemon);
            for (String route : CreateRoutes(currentChain, colorText(desiredPokemon, GREEN, RESET), desiredPokemon, new ArrayList<>())) {
                System.out.println(route);
            }
        } else {
            System.out.println("Pokemon not found in data, returning to main menu.");
        }
    }

    public static List<String> CreateRoutes (Chain currentChain, String currentRoute, String currentPokemon, List<String> accumulatedRoutes) {
        if (currentChain.getValues().contains(currentPokemon) && !currentRoute.matches(String.format(".*(%s).*(%s).*", currentPokemon, currentPokemon))) {
            for (String key: currentChain.getKeysByValue(currentPokemon)) {
                CreateRoutes(currentChain, key + (currentChain.getLocationsByKey(key).contains("N/A") ? "" : " " + colorText(currentChain.getLocationsByKey(key).toString(), GRAY, RESET)) + " -> " + currentRoute, key, accumulatedRoutes);
            }
        } else {
            accumulatedRoutes.add(currentRoute);
        }
        return accumulatedRoutes;
    }

    public static String colorText (String text, String colorToBe, String colorAfter) {
        return colorToBe + text + colorAfter;
    }

    private static String colorizeChain(String chain, String endColor) {
        String startingLink = chain.substring(0, chain.indexOf(" "));
        String endingLink = chain.substring(chain.lastIndexOf(" ") + 1);
        String output;
        if (startingLink.equals(endingLink)) {
            output = chain.replaceAll(endingLink, colorText(endingLink, endColor, GRAY));
        } else {
            output = chain.replaceAll(endingLink, colorText(endingLink, endColor, GRAY)).replace(startingLink, colorText(startingLink, GREEN, RESET));
        }
        return output + RESET;
    }
}
