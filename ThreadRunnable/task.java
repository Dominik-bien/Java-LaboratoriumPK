import java.util.concurrent.atomic.AtomicBoolean;

public class Task implements Runnable {
    private final int id;
    private String result = null;
    private final AtomicBoolean cancelled = new AtomicBoolean(false);
    private final AtomicBoolean finished = new AtomicBoolean(false);
    private Thread thread;

    public Task(int id) {
        this.id = id;
    }

    public void start() {
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        try {
            //symulacja zadania
            for (int i = 0; i < 5; i++) {
                if (cancelled.get()) {
                    System.out.println("Task " + id + " has been cancelled.");
                    return;
                }
                System.out.println("Task " + id + " is working... step " + (i+1));
                Thread.sleep(1000); //1 sekunda pracy
            }
            result = "Result of Task " + id;
            finished.set(true);
            System.out.println("Task " + id + " completed.");
        } catch (InterruptedException e) {
            System.out.println("Task " + id + " was interrupted.");
        }
    }

    public boolean isFinished() {
        return finished.get();
    }

    public boolean isCancelled() {
        return cancelled.get();
    }

    public void cancel() {
        cancelled.set(true);
        if (thread != null) {
            thread.interrupt();
        }
    }

    public String getResult() {
        if (isFinished()) {
            return result;
        } else {
            return "Task " + id + " not finished yet.";
        }
    }

    public int getId() {
        return id;
    }
}
