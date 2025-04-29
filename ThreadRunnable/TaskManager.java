import java.util.*;

public class TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private int taskIdCounter = 1;

    public int createTask() {
        Task task = new Task(taskIdCounter);
        tasks.put(taskIdCounter, task);
        task.start();
        return taskIdCounter++;
    }

    public void cancelTask(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            task.cancel();
        } else {
            System.out.println("No such task with id: " + id);
        }
    }

    public void showStatus(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            System.out.println("Task " + id + " - Finished: " + task.isFinished() + ", Cancelled: " + task.isCancelled());
        } else {
            System.out.println("No such task with id: " + id);
        }
    }

    public void showResult(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            System.out.println(task.getResult());
        } else {
            System.out.println("No such task with id: " + id);
        }
    }

    public void listTasks() {
        if (tasks.isEmpty()) {
            System.out.println("No tasks created yet.");
        } else {
            for (Task task : tasks.values()) {
                System.out.println("Task ID: " + task.getId() + " | Finished: " + task.isFinished() + " | Cancelled: " + task.isCancelled());
            }
        }
    }
}
