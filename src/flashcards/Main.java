package flashcards;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final Map<String, String> data = new LinkedHashMap<>();

    public static void main(String[] args) {
        fillData();
        learnTerms();
    }

    private static void fillData() {
        System.out.print("Input the number of cards:\n");
        int numberOfCards = Integer.parseInt(scanner.nextLine());
        for (int i = 0; i < numberOfCards; i++) {
            boolean repeat;
            String term;
            String definition;

            System.out.printf("Card #%d:\n", i + 1);
            do {
                term = scanner.nextLine();

                if (data.containsKey(term)) {
                    System.out.printf("The term \"%s\" already exists. Try again:%n", term);
                    repeat = true;
                } else {
                    repeat = false;
                }
            } while (repeat);

            System.out.printf("The definition for card #%d:\n", i + 1);
            do {
                definition = scanner.nextLine();

                if (data.containsValue(definition)) {
                    System.out.printf("The definition \"%s\" already exists. Try again:%n", definition);
                    repeat = true;
                } else {
                    repeat = false;
                }
            } while (repeat);

            data.put(term, definition);
        }
    }

    private static void learnTerms() {
        for (var entry : data.entrySet()) {
            System.out.printf("Print the definition of \"%s\":\n", entry.getKey());
            String userAnswer = scanner.nextLine();
            String rightAnswer = entry.getValue();

            if (rightAnswer.equals(userAnswer)) {
                System.out.println("Correct!");
            } else if (data.containsValue(userAnswer)) {
                String rightTerm = "";
                for (var el : data.entrySet()) {
                    if (userAnswer.equals(el.getValue())) {
                        rightTerm = el.getKey();
                        break;
                    }
                }
                System.out.printf("Wrong. The right answer is \"%s\", but your definition is correct for \"%s\".%n", rightAnswer, rightTerm);
            } else {
                System.out.printf("Wrong. The right answer is \"%s\".%n", rightAnswer);
            }
        }
    }
}
