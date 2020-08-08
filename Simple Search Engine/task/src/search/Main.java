package search;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        SearchEngine searchEngine = new SearchEngine();

        while (searchEngine.acceptingInput) {
            if(scanner.hasNext()) {
                searchEngine.processInput(scanner.nextLine());
            }
        }
    }
}

enum QueryState {NUMBER_OF_ANIMALS, ANIMAL_ENTRY, MENU_OPTIONS, DATA_TO_FIND}

class SearchEngine {
    QueryState queryState = QueryState.NUMBER_OF_ANIMALS;
    ArrayList<String> animals = new ArrayList<>();
    boolean acceptingInput = true;
    int inputCount = 0;

    public SearchEngine() {
        promptNumAnimals();
    }

    public void processInput(String input) {
        switch (queryState) {
            case NUMBER_OF_ANIMALS:
                inputCount = stringToNumAnimals(input);
                promptEnterAnimals();
                break;
            case ANIMAL_ENTRY:
                enterAnimal(input);
                break;
            case MENU_OPTIONS:
                processMenuOption(input);
                break;
            case DATA_TO_FIND:
                processSearchQuery(input);
                break;
            default:
                break;
        }
    }

    private void processSearchQuery(String input) {
            searchForAnimal(input);
            promptMenu();
    }

    private void enterAnimal(String input) {
        if (inputCount > 0) {
            animals.add(input);
            inputCount--;
        }
        if (inputCount <= 0) {
            inputCount = 0;
            promptMenu();
        }
    }

    private void promptEnterAnimals() {
        queryState = QueryState.ANIMAL_ENTRY;
        System.out.println("Enter all animals:");
    }

    private void processMenuOption(String input) {
        switch (input) {
            case "1":
                promptQuery();
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

    private int stringToNumAnimals(String input) {
        return Integer.parseInt(input); // ToDo: exception check
    }

    private void searchForAnimal(String input) {
        ArrayList<String> foundAnimals = new ArrayList<String>();
        for (String animal : animals) {
            if (animal.toLowerCase().contains(input.toLowerCase())) {
                foundAnimals.add(animal);
            }
        }
        if (foundAnimals.size() > 0) {
            System.out.println("\nFound animal:");
            for (String animal : foundAnimals) {
                System.out.println(animal);
            }
        } else {
            System.out.println("No matching animal found.");
        }
    }
}
