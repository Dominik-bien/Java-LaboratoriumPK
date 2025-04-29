import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = new TaskManager();
        Scanner scanner = new Scanner(System.in);
        String command;

        System.out.println("Task Manager Started. Commands: create, cancel <id>, status <id>, result <id>, list, exit");

        while (true) {
            System.out.print("> ");
            command = scanner.nextLine();
            String[] parts = command.split(" ");
            switch (parts[0]) {
                case "create":
                    int id = manager.createTask();
                    System.out.println("Created Task with ID: " + id);
                    break;
                case "cancel":
                    if (parts.length > 1) {
                        manager.cancelTask(Integer.parseInt(parts[1]));
                    } else {
                        System.out.println("Usage: cancel <id>");
                    }
                    break;
                case "status":
                    if (parts.length > 1) {
                        manager.showStatus(Integer.parseInt(parts[1]));
                    } else {
                        System.out.println("Usage: status <id>");
                    }
                    break;
                case "result":
                    if (parts.length > 1) {
                        manager.showResult(Integer.parseInt(parts[1]));
                    } else {
                        System.out.println("Usage: result <id>");
                    }
                    break;
                case "list":
                    manager.listTasks();
                    break;
                case "exit":
                    System.out.println("Exiting Task Manager...");
                    return;
                default:
                    System.out.println("Unknown command.");
            }
        }
    }
}
