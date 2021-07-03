package flashcards;

import java.util.Scanner;

public class Main {
    final static Scanner scanner = new Scanner(System.in);
    public static void main(String[] args) {
        System.out.print("Input the number of cards:\n");
        int numberOfCards  = Integer.parseInt(scanner.nextLine());

        String[] terms = new String[numberOfCards];
        String[] definitions = new String[numberOfCards];

        for (int i = 0; i < numberOfCards; i++) {
            System.out.printf("The card #%d:\n", i + 1);
            String term = scanner.nextLine();
            System.out.printf("The definition of the card #%d:\n", i + 1);
            String definition  = scanner.nextLine();
            terms[i] = term.trim();
            definitions[i] = definition.trim();
        }

        for (int i = 0; i < numberOfCards; i++) {
            System.out.printf("Print the definition of \"%s\":\n", terms[i]);
            String userAnswer  = scanner.next();
            String rightAnswer = definitions[i];
            System.out.println(rightAnswer.equals(userAnswer) ? "Correct!" : String.format("Wrong. The right answer is \"%s\".", rightAnswer));
        }
    }
}
