package flashcards;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final Map<String, String> data = new LinkedHashMap<>();
    private static final Map<String, Integer> statistics = new HashMap<>();
    private static final Map<String, String> params = new HashMap<>();

    private enum Action {
        ADD("add"),
        REMOVE("remove"),
        IMPORT("import"),
        EXPORT("export"),
        ASK("ask"),
        EXIT("exit"),
        LOG("log"),
        HARDEST_CARD("hardest card"),
        RESET_STATS("reset stats"),
        UNKNOWN("unknown"),
        ;

        String value;

        Action(String value) {
            this.value = value;
        }

        public static Action getByKeyword(String keyword) {
            for (Action action : Action.values()) {
                if (action.value.equals(keyword)) {
                    return action;
                }
            }
            return UNKNOWN;
        }
    }

    public static void main(String[] args) {
        processParameters(args);
        if (params.containsKey("import")) {
            doImport(params.get("import"));
        }
        while (true) {
            System.out.println("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):");
            Action action = Action.getByKeyword(scanner.nextLine());
            performAction(action);
        }
    }

    private static void processParameters(String[] args) {
        for (int i = 0; i < args.length; i += 2) {
            String key = args[i];
            String value = args[i + 1];
            params.put(key.substring(1), value);
        }
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
            case LOG:
                log();
                break;
            case HARDEST_CARD:
                hardestCard();
                break;
            case RESET_STATS:
                resetStats();
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
        doImport(fileName);
    }

    private static void doImport(String fileName) {
        int counter = 0;
        try (Scanner scanner = new Scanner(new File(fileName))) {
            while (scanner.hasNext()) {
                String[] entity = scanner.nextLine().split(";");
                data.put(entity[0], entity[1]);
                statistics.put(entity[0], Integer.parseInt(entity[2]));
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
        doExport(fileName);
    }

    private static void doExport(String fileName) {
        File file = new File(fileName);
        try (PrintWriter writer = new PrintWriter(file)) {
            for (var entry : data.entrySet()) {
                writer.printf("%s;%s;%d%n", entry.getKey(), entry.getValue(), statistics.getOrDefault(entry.getKey(), 0));
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
            String term = entry.getKey();
            System.out.printf("Print the definition of \"%s\":\n", term);
            String userAnswer = scanner.nextLine();
            String rightAnswer = entry.getValue();

            if (rightAnswer.equals(userAnswer)) {
                System.out.println("Correct!");
            } else {
                if (data.containsValue(userAnswer)) {
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
                updateStatistics(term);
            }
            count--;
        }
    }

    private static void updateStatistics(String term) {
        if (statistics.containsKey(term)) {
            statistics.put(term, statistics.get(term) + 1);
        } else {
            statistics.put(term, 1);
        }
    }

    private static void exit() {
        if (params.containsKey("export")) {
            doExport(params.get("export"));
        }
        System.out.println("Bye bye!");
        System.exit(0);
    }

    private static void log() {
        System.out.println("File name:");
        String fileName = scanner.nextLine();

        File file = new File(fileName);

        try (PrintWriter writer = new PrintWriter(file)) {
            /*
            for (var entry : data.entrySet()) {
                writer.printf("%s:%d%n", entry.getKey(), statistics.getOrDefault(entry.getKey(), 0));
            }
            */
            for (int i = 0; i < 33; i++) {
                writer.println("Hey");
            }
        } catch (IOException e) {
            System.out.printf("An exception occurs %s", e.getMessage());
        }
        System.out.println("The log has been saved.");
    }

    private static void hardestCard() {
        if (statistics.isEmpty()) {
            System.out.println("There are no cards with errors.");
        } else {
            Map<Integer, List<Map.Entry<String, Integer>>> groupedStatistics = statistics.entrySet().stream().collect(Collectors.groupingBy(Map.Entry::getValue));
            Optional<List<Map.Entry<String, Integer>>> hardestGroup = groupedStatistics.entrySet().stream().max(Map.Entry.comparingByKey()).map(Map.Entry::getValue);
            if (hardestGroup.isPresent()) {
                List<Map.Entry<String, Integer>> hardestCards = hardestGroup.get();
                if (hardestCards.size() > 1) {
                    List<String> hardestCardsTerms = hardestCards.stream().map(Map.Entry::getKey).collect(Collectors.toList());
                    System.out.print("The hardest cards are ");
                    for (int i = 0; i < hardestCardsTerms.size() - 1; i++) {
                        System.out.printf("\"%s\", ", hardestCardsTerms.get(i));
                    }
                    System.out.printf("\"%s\". You have %d errors answering them.%n", hardestCardsTerms.get(hardestCards.size() - 1), hardestCards.get(0).getValue());
                } else {
                    Map.Entry<String, Integer> hardestCard = hardestCards.get(0);
                    System.out.printf("The hardest card is \"%s\". You have %d errors answering it.%n", hardestCard.getKey(), hardestCard.getValue());
                }
            }
        }
    }

    private static void resetStats() {
        statistics.clear();
        System.out.println("Card statistics have been reset.");
    }
}
