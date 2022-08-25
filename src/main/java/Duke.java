import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Duke {
    private static boolean isAlive;
    private static List<Task> tasks;

    private static int checkTask(String id) {
        try {
            int task = Integer.parseInt(id);
            if (task > tasks.size() || task <= 0) {
                return -1;
            }
            return task;
        } catch (NumberFormatException e) {
            return -2;
        }
    }

    private static String handle(String command, String params) {
        switch (command) {
        case "list":
            String out = "";
            for (int i = 0; i < tasks.size(); ++i) {
                if (i != 0) out += "\n";
                out += (i + 1) + ". " + tasks.get(i);
            }
            if (out.equals("")) {
                out = "No tasks left!";
            }
            return out;
        case "mark":
            int markedTask = checkTask(params);
            if (markedTask < 0) return "Invalid task number!";
            tasks.get(markedTask - 1).isDone = true;
            saveToFile();
            return "OK, this task is done:\n" + tasks.get(markedTask - 1);
        case "unmark":
            int unmarkedTask = checkTask(params);
            if (unmarkedTask < 0) return "Invalid task number!";
            tasks.get(unmarkedTask - 1).isDone = false;
            saveToFile();
            return "OK, this task is undone:\n" + tasks.get(unmarkedTask - 1);
        case "delete":
            int deleteTask = checkTask(params);
            if (deleteTask < 0) return "Invalid task number!";
            tasks.remove(deleteTask - 1);
            saveToFile();
            return "OK, that task has been deleted.";
        case "bye":
            isAlive = false;
            return "Goodbye!";
        case "todo":
            if (params.equals("")) return "Todo description can't be empty.";
            tasks.add(new Todo(params));
            saveToFile();
            return "Added new todo: " + tasks.get(tasks.size() - 1);
        case "deadline":
            if (params.equals("")) return "Deadline description can't be empty.";
            String[] splitDeadline = splitOnFirst(params, " /by ");
            try {
                tasks.add(new Deadline(splitDeadline[0], splitDeadline[1]));
            } catch (DateTimeParseException e) {
                return "Invalid date! (yyyy-mm-dd)";
            }
            saveToFile();
            return "Added new deadline: " + tasks.get(tasks.size() - 1);
        case "event":
            if (params.equals("")) return "Event description can't be empty.";
            String[] splitEvent = splitOnFirst(params, " /at ");
            try {
                tasks.add(new Event(splitEvent[0], splitEvent[1]));
            } catch (DateTimeParseException e) {
                return "Invalid date! (yyyy-mm-dd)";
            }
            saveToFile();
            return "Added new event: " + tasks.get(tasks.size() - 1);
        default:
            return "I don't know what '" + command + "' is!";
        }
    }

    private static void printResponse(String response) {
        System.out.println("    __________________________________________________");
        System.out.println("    " + response.replace("\n", "\n    "));
        System.out.println("    __________________________________________________");
    }

    private static String[] splitOnFirst(String str, String target) {
        int split = str.indexOf(target);
        if (split < 0) {
            return new String[] {str, ""};
        } else {
            String[] out = new String[2];
            out[0] = str.substring(0, split);
            out[1] = str.substring(split + target.length());
            return out;
        }
    }

    private static void saveToFile() {
        try {
            PrintWriter saveFile = new PrintWriter("tasks.txt");
            for (Task task : tasks) {
                saveFile.println(task.getEncoded());
            }
            saveFile.close();
        } catch (IOException e) {
            System.out.println("<couldn't save to file!>");
        }
    }

    public static void main(String[] args) {

        tasks = new ArrayList<>();

        try {
            Scanner saved = new Scanner(new File("tasks.txt"));
            while (saved.hasNextLine()) {
                try {
                    tasks.add(Task.fromEncoded(saved.nextLine()));
                } catch (DateTimeParseException e) {
                    System.out.println("<failed to load a task from file>");
                }
            }
            System.out.println("<loaded " + tasks.size() + " tasks from file>");
            saved.close();
        } catch (FileNotFoundException e) {
            System.out.println("<loaded 0 tasks from file>");
        }

        printResponse("Hello! What can I do for you today?");
        Scanner sc = new Scanner(System.in);

        isAlive = true;
        while (isAlive) {
            String in;
            in = sc.nextLine();
            String[] splits = splitOnFirst(in, " ");
            printResponse(handle(splits[0], splits[1]));
        }
    }
}
