import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MySemaphore extends Semaphore {

    private int permits; // количество текущих разрешений
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition permitsAvailable = lock.newCondition();

    public MySemaphore(int initialPermits) {
        super(initialPermits); // для совместимости, можно не использовать
        this.permits = initialPermits;
    }

    @Override
    public void acquire() throws InterruptedException {
        lock.lock();
        try {
            while (permits == 0) {
                // Ждём, пока не появится разрешение
                permitsAvailable.await();
            }
            // Захватываем одно разрешение
            permits--;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void release() {
        lock.lock();
        try {
            permits++; // Освобождаем одно разрешение
            permitsAvailable.signal(); // Уведомляем один ожидающий поток
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int availablePermits() {
        lock.lock();
        try {
            return permits;
        } finally {
            lock.unlock();
        }
    }
}
