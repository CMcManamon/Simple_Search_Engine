package search;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        SearchEngine searchEngine = new SearchEngine(args);

        while (searchEngine.acceptingInput) {
            if(scanner.hasNext()) {
                searchEngine.processInput(scanner.nextLine());
            }
        }
    }
}

enum QueryState {NUMBER_OF_ANIMALS, ANIMAL_ENTRY, MENU_OPTIONS, SELECT_STRATEGY, DATA_TO_FIND}

class SearchEngine {
    QueryState queryState = QueryState.NUMBER_OF_ANIMALS;
    ArrayList<String> animals = new ArrayList<>();
    Map<String, ArrayList<Integer>> animalMap = new HashMap<>();
    SearchMethod searchMethod;
    boolean acceptingInput = true;
    int inputCount = 0;

    public SearchEngine(String[] args) {
        if (args.length > 1 && args[0].equals("--data")) {
            importDataFromFile(args[1]);
            promptMenu();
        } else {
            System.out.println("Invalid command line arguments. Enter data manually.");
            promptNumAnimals();
        }
    }

    private void importDataFromFile(String arg) {
        try (Scanner scanner = new Scanner(new File(arg))) {
            while (scanner.hasNext()) {
                animals.add(scanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            System.out.println("Data file not found: " + arg);
        }

        generateDataMap();
    }

    private void generateDataMap() {
        for (int i = 0; i < animals.size(); i++) {
            String[] list = animals.get(i).split("\\s");
            for (int j = 0; j < list.length; j++) {
                String wordKey = list[j].toLowerCase();
                if (!animalMap.containsKey(wordKey)) {
                    animalMap.put(wordKey, new ArrayList<>());
                }
                animalMap.get(wordKey).add(i);
            }
        }
    }

    public void processInput(String input) {
        switch (queryState) {
            case NUMBER_OF_ANIMALS:
                setNumAnimals(input);
                break;
            case ANIMAL_ENTRY:
                enterAnimal(input);
                break;
            case MENU_OPTIONS:
                processMenuOption(input);
                break;
            case SELECT_STRATEGY:
                setStrategy(input);
                break;
            case DATA_TO_FIND:
                processSearchQuery(input);
                break;
            default:
                break;
        }
    }

    private void setNumAnimals(String input) {
        inputCount = stringToNumAnimals(input);
        if (inputCount <= 0) {
            System.out.println("Must input at least 1 animal");
            promptNumAnimals();
        } else {
            promptEnterAnimals();
        }
    }

    private void setStrategy(String input) {
        switch (input.toUpperCase()) {
            case "ALL":
                searchMethod = new searchAll();
                break;
            case "ANY":
                searchMethod = new searchAny();
                break;
            case "NONE":
                searchMethod = new searchNone();
                break;
            default:
                System.out.println("Invalid matching strategy: " + input);
                promptSelectStrategy();
                return;
        }
        promptQuery();
    }

    private void processSearchQuery(String input) {
        // break the query into terms
        List<String> terms = new ArrayList(Arrays.asList(input.toLowerCase().split("\\s")));

        // apply search method to get indexes that match the search pattern
        Set<Integer> results = searchMethod.find(terms, animalMap);

        // print results
        if (results.isEmpty()) {
            System.out.println("No matches found");
        } else {
            for (Integer i : results) {
                System.out.println(animals.get(i));
            }
        }

        // return to menu
        promptMenu();
    }

    private void enterAnimal(String input) {
        if (inputCount > 0) {
            animals.add(input);
            inputCount--;
        }
        if (inputCount <= 0) {
            inputCount = 0;
            generateDataMap();
            promptMenu();
        }
    }

    private void processMenuOption(String input) {
        switch (input) {
            case "1":
                promptSelectStrategy();
                break;
            case "2":
                printAll();
                break;
            case "0":
                System.out.println("\nBye!");
                System.exit(0);
                break;
            default:
                System.out.println("\nIncorrect option! Try again.");
                promptMenu();
                break;
        }
    }

    private void printAll() {
        System.out.println("\n=== List of animals ===");
        for (String animal : animals) {
            System.out.println(animal);
        }
        promptMenu();
    }

    /* PROMPTS */
    private void promptMenu() {
        queryState = QueryState.MENU_OPTIONS;
        System.out.println("\n=== Menu ===" +
                "\n1. Find an animal" +
                "\n2. Print all animals" +
                "\n0. Exit");
    }

    private void promptQuery() {
        queryState = QueryState.DATA_TO_FIND;
        System.out.println("\nEnter an animal or description to search " +
                "all suitable animals");
    }

    private void promptNumAnimals() {
        queryState = QueryState.NUMBER_OF_ANIMALS;
        System.out.println("Enter the number of animals:");
    }

    private void promptSelectStrategy() {
        queryState = QueryState.SELECT_STRATEGY;
        System.out.println("\nSelect a matching strategy: ALL, ANY, NONE");
    }

    private void promptEnterAnimals() {
        queryState = QueryState.ANIMAL_ENTRY;
        System.out.println("Enter all animals:");
    }

    private Integer stringToNumAnimals(String input) {
        try {
            return Integer.parseInt(input);
        } catch(NumberFormatException e) {
            return 0;
        }
    }

} // end SearchEngine class


// Search Methods
interface SearchMethod {
    Set<Integer> find(Collection<String> targets, Map<String, ArrayList<Integer>> database);
}

class searchAny implements SearchMethod {
    @Override
    public Set<Integer> find(Collection<String> targets, Map<String, ArrayList<Integer>> database) {
        Set<Integer> results = new TreeSet<>();
        for (String target : targets) {
            if (database.containsKey(target)) {
                for (Integer n : database.get(target)) {
                    results.add(n);
                }
            }
        }

        return results;
    }
}

class searchAll implements SearchMethod {
    @Override
    public Set<Integer> find(Collection<String> targets, Map<String, ArrayList<Integer>> database) {
        Set<Integer> results = new TreeSet<>();
        ArrayList<ArrayList<Integer>> matches = new ArrayList<>();
        for (String target : targets) {
            if (database.containsKey(target)) {
                matches.add(database.get(target));
            }
        }

        if (!matches.isEmpty()) {
            results.addAll(matches.get(0)); // add the first set of matches to the set
            for (ArrayList match : matches) {
                results.retainAll(match); // keep all shared matches
            }
        }

        return results;
    }
}

class searchNone implements SearchMethod {
    @Override public Set<Integer> find(Collection<String> targets, Map<String, ArrayList<Integer>> database) {
        Set<Integer> results = new TreeSet<>();
        for (ArrayList<Integer> c : database.values()) { // fill the set with all possible values
            results.addAll(c);
        }

        for (String target : targets) {
            if (database.containsKey(target)) {
                results.removeAll(database.get(target));
            }
        }

        return results;
    }
}


