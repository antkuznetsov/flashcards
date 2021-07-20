package flashcards;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final Map<String, String> data = new LinkedHashMap<>();

    private enum Action {
        ADD,
        REMOVE,
        IMPORT,
        EXPORT,
        ASK,
        EXIT,
        UNKNOWN,
    }

    public static void main(String[] args) {
        while (true) {
            System.out.println("Input the action (add, remove, import, export, ask, exit):");
            Action action = parseAction(scanner.nextLine());
            performAction(action);
        }
    }

    private static Action parseAction(String action) {
        Action currentAction;
        try {
            currentAction = Action.valueOf(action.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            currentAction = Action.UNKNOWN;
        }
        return currentAction;
    }

    private static void performAction(Action action) {
        switch (action) {
            case ADD:
                add();
                break;
            case REMOVE:
                remove();
                break;
            case IMPORT:
                importData();
                break;
            case EXPORT:
                exportData();
                break;
            case ASK:
                ask();
                break;
            case EXIT:
                exit();
                break;
            default:
                break;
        }
    }

    private static void add() {
        System.out.println("The card:");
        String term = scanner.nextLine();
        if (data.containsKey(term)) {
            System.out.printf("The card \"%s\" already exists.%n", term);
            return;
        }

        System.out.println("The definition of the card:");
        String definition = scanner.nextLine();
        if (data.containsValue(definition)) {
            System.out.printf("The definition \"%s\" already exists. Try again:%n", definition);
            return;
        }

        data.put(term, definition);

        System.out.printf("The pair (\"%s\":\"%s\") has been added.%n", term, definition);
    }

    private static void remove() {
        System.out.println("Which card?");
        String term = scanner.nextLine();
        if (data.containsKey(term)) {
            data.remove(term);
            System.out.println("The card has been removed.");
        } else {
            System.out.printf("Can't remove \"%s\": there is no such card.%n", term);
        }
    }

    private static void importData() {
        System.out.println("File name:");
        String fileName = scanner.nextLine();
        int counter = 0;
        try (Scanner scanner = new Scanner(new File(fileName))) {
            while (scanner.hasNext()) {
                String[] entity = scanner.nextLine().split(":");
                data.put(entity[0], entity[1]);
                counter++;
            }
            System.out.printf("%d cards have been loaded.%n", counter);
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
        }
    }

    private static void exportData() {
        System.out.println("File name:");
        String fileName = scanner.nextLine();
        File file = new File(fileName);

        try (PrintWriter writer = new PrintWriter(file)) {
            for (var entry : data.entrySet()) {
                writer.printf("%s:%s%n", entry.getKey(), entry.getValue());
            }
        } catch (IOException e) {
            System.out.printf("An exception occurs %s", e.getMessage());
        }
        System.out.printf("%d cards have been saved.%n", data.size());
    }

    private static void ask() {
        System.out.println("How many times to ask?");
        int count;
        try {
            count = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            count = 0;
        }
        for (var entry : data.entrySet()) {
            if (count == 0) {
                break;
            }
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
            count--;
        }
    }

    private static void exit() {
        System.out.println("Bye bye!");
        System.exit(0);
    }
}
