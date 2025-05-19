import java.util.*;
import java.util.concurrent.*;

public class Main {

    // Количество потоков, которые будут одновременно читать и писать в Map
    public static final int THREADS = 50;

    // Количество итераций чтения/записи в каждом потоке
    public static final int ITERATIONS = 1000;

    // Константа для перевода наносекунд в секунды
    public static final double NSEC = 1000_000_000.0;

    // Размер диапазона ключей ("key0", "key1", ..., "keyN")
    public static final int MAP_SIZE = 3;

    // Количество повторов измерения для усреднения
    public static final int SAMPLES = 5;

    // Четыре варианта карт для тестирования
    public static Map<String, Integer> hashMap = new HashMap<>(); // не потокобезопасен
    public static Map<String, Integer> hashTable = new Hashtable<>(); // потокобезопасен
    public static Map<String, Integer> syncMap = Collections.synchronizedMap(new HashMap<>()); // потокобезопасен (через обёртку)
    public static Map<String, Integer> cHashMap = new ConcurrentHashMap<>(); // современная потокобезопасная реализация

    public static void main(String[] args) {
        System.out.println("Collections:");

        // Тестируем каждую коллекцию и замеряем время
        double hashMapTime = compute(hashMap) / NSEC;
        double hashTableTime = compute(hashTable) / NSEC;
        double syncMapTime = compute(syncMap) / NSEC;
        double cHashMapTime = compute(cHashMap) / NSEC;

        // Выводим результаты
        System.out.println("Execution times:");
        System.out.println(String.format(
                "\tHashMap: %.3f s,\n\tHashTable: %.3f s,\n\tSyncMap: %.3f s,\n\tConcurrentHashMap: %.3f s.",
                hashMapTime, hashTableTime, syncMapTime, cHashMapTime
        ));
    }

    /**
     Метод, выполняющий многопоточное чтение/запись в переданную карту и измеряющий время выполнения.
     */
    private static long compute(Map<String, Integer> map) {
        System.out.print(String.format("\t%s", map.getClass().getName()));

        long start = 0;
        long stop = 0;

        for (int k = 0; k < SAMPLES; k++) {

            // Очищаем карту перед каждым тестом
            map.clear();

            start = System.nanoTime();

            // Создаём пул потоков фиксированного размера
            ExecutorService executorService = Executors.newFixedThreadPool(THREADS);

            List<Callable<String>> tasks = new ArrayList<>();

            // Создаём задания для потоков
            for (int i = 0; i < THREADS; i++) {
                tasks.add(() -> {
                    Random rand = new Random();
                    for (int j = 0; j < ITERATIONS; j++) {
                        String key = "key" + rand.nextInt(MAP_SIZE);

                        // Получаем текущее значение по ключу
                        Integer value = map.get(key);

                        // Искусственная задержка (усиливает эффект гонки данных)
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }

                        // Обновляем значение: если null, ставим 1; иначе увеличиваем
                        if (value == null) {
                            map.put(key, 1);
                        } else {
                            map.put(key, value + 1);
                        }
                    }
                    return "Thread " + Thread.currentThread().getName() + " done";
                });
            }

            // Запускаем все задачи
            try {
                executorService.invokeAll(tasks);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }

            // Останавливаем пул потоков
            executorService.shutdown();

            stop = System.nanoTime();
        }

        System.out.println("...done.");

        return stop - start;
    }
}
