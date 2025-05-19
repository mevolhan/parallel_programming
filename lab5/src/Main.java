import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Main {

    public static final int THREADS = 4; // Кол-во потоков
    public static final int COUNT = 2;   // Кол-во разрешений (потоков одновременно)

    // Наш семафор и стандартный
    public static MySemaphore mySemaphore = new MySemaphore(COUNT);
    public static Semaphore regularSemaphore = new Semaphore(COUNT);

    public static void main(String[] args) {
        System.out.println("-------------------\nRegular semaphore:\n-------------------");
        runTask(regularSemaphore);
        System.out.println("--------------\nMy semaphore:\n--------------");
        runTask(mySemaphore);
    }

    private static void runTask(Semaphore semaphore) {
        ExecutorService es = Executors.newFixedThreadPool(THREADS);

        List<Callable<String>> tasks = new ArrayList<>();

        for (int i = 0; i < THREADS; i++) {
            int threadId = i + 1;
            tasks.add(() -> {
                String threadName = "Thread-" + threadId;
                try {
                    System.out.println(threadName + " trying to acquire...");
                    semaphore.acquire(); // Попытка получить доступ
                    System.out.println(threadName + " acquired semaphore.");

                    // Симуляция работы
                    Thread.sleep(1000);

                    System.out.println(threadName + " releasing semaphore.");
                    semaphore.release(); // Освобождение доступа

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return threadName + " finished.";
            });
        }

        try {
            List<Future<String>> results = es.invokeAll(tasks);
            for (Future<String> result : results) {
                System.out.println(result.get());
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        es.shutdown();
    }
}
