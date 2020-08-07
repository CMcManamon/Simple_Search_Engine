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

enum QueryState {NUMBER_OF_ANIMALS, ANIMAL_ENTRY, NUMBER_OF_QUERIES, DATA_TO_FIND}

class SearchEngine {
    QueryState queryState = QueryState.NUMBER_OF_ANIMALS;
    ArrayList<String> animals = new ArrayList<>();
    boolean acceptingInput = true;
    int inputCount = 0;

    public SearchEngine() {
        System.out.println("Enter the number of animals:");
    }

    public void processInput(String input) {
        switch (queryState) {
            case NUMBER_OF_ANIMALS:
                inputCount = Integer.parseInt(input); // ToDo: exception check
                queryState = QueryState.ANIMAL_ENTRY;
                System.out.println("Enter all animals:");
                break;
            case ANIMAL_ENTRY:
                if (inputCount > 0) {
                    animals.add(input);
                    inputCount--;
                }
                if (inputCount <= 0) {
                    inputCount = 0;
                    queryState = QueryState.NUMBER_OF_QUERIES;
                    System.out.println("\nEnter the number of search queries:");
                }
                break;
            case NUMBER_OF_QUERIES:
                inputCount = Integer.parseInt(input); // ToDo: exception check
                queryState = QueryState.DATA_TO_FIND;
                System.out.println("\nEnter data to search for animals:");
                break;
            case DATA_TO_FIND:
                if (inputCount > 0) {
                    searchForAnimal(input);
                    inputCount--;
                }
                if (inputCount <= 0) {
                    acceptingInput = false;
                    inputCount = 0;
                    queryState = QueryState.NUMBER_OF_ANIMALS;
                } else {
                    System.out.println("\nEnter data to search for animals:");
                }
                break;
            default:
                break;
        }
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
