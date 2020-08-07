package search;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String[] words = scanner.nextLine().split("\\s");
        String target = scanner.next();

        // if text contains target, print index
        for (int i = 0; i < words.length; i++) {
            if (words[i].equals(target)) {
                System.out.println(i + 1);
                return;
            }
        }
        // else print "Not Found"
        System.out.println("Not Found");
    }
}
