import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    public static void main(String[] args) {
        // Создаем объект очереди файлов с вместимостью 5 файлов
        FileQueue queue = new FileQueue(5);
        
        // Типы файлов
        String[] types = new String[] { "XML", "JSON", "XLS" };
        
        // Создаем обработчики файлов для каждого типа
        FileHandler[] fileHandlers = new FileHandler[3];
        Thread[] handlerThreads = new Thread[3];

        for (int i = 0; i < fileHandlers.length; i++) {
            fileHandlers[i] = new FileHandler(queue, types[i], queue.lock, queue.notEmpty);
            handlerThreads[i] = new Thread(fileHandlers[i]);
            handlerThreads[i].start();
        }

        // Создаем генератор файлов и запускаем его в отдельном потоке
        FileGenerator fg = new FileGenerator(queue, queue.lock, queue.notFull, types);
        Thread gt = new Thread(fg);
        gt.start();
    }
}

// Класс, представляющий файл
class File {
    String type;
    Integer size;

    public File(String type, Integer size) {
        this.type = type;
        this.size = size;
    }
}

// Класс, реализующий генерацию файлов
class FileGenerator implements Runnable {
    private final FileQueue queue;
    private final Lock lock;
    private final Condition condition;
    private final String[] types;
    private final Random random = new Random();

    public FileGenerator(FileQueue queue, Lock lock, Condition condition, String[] types) {
        this.queue = queue;
        this.lock = lock;
        this.condition = condition;
        this.types = types;
    }

    @Override
    public void run() {
        while (true) {
            lock.lock();
            try {
                // Пока очередь полна, ожидаем
                while (queue.size() == 5) {
                    condition.await();
                }

                // Генерируем случайный тип и размер файла
                String type = types[random.nextInt(3)];
                Integer size = random.nextInt(91) + 10;
                int time = random.nextInt(901) + 100;

                File file = new File(type, size);

                System.out.println(
                        "[" + Thread.currentThread().getName() + "] Generated File: " + file.size + " " + file.type);

                // Имитация времени обработки файла
                Thread.sleep(time);

                // Добавляем файл в очередь и оповещаем обработчики
                queue.add(file);
                condition.signalAll();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }
}

// Класс, реализующий обработку файлов
class FileHandler implements Runnable {
    private final FileQueue queue;
    private final String type;
    private final Lock lock;
    private final Condition condition;

    public FileHandler(FileQueue queue, String type, Lock lock, Condition condition) {
        this.queue = queue;
        this.type = type;
        this.lock = lock;
        this.condition = condition;
    }

    @Override
    public void run() {
        while (true) {
            lock.lock();
            try {
                // Пока очередь пуста, ожидаем
                while (queue.size() == 0) {
                    condition.await();
                }
                File file = queue.peek();
                
                // Если тип файла соответствует текущему обработчику, обрабатываем файл
                if (file.type.equals(type)) {
                    queue.remove();
                    long time = file.size * 7L; // Время обработки файла
                    Thread.sleep(time);
                    System.out.println("[" + Thread.currentThread().getName() + "] Processed File: " + file.size + " "
                            + file.type + " [time spent: " + time + "ms]");
                }
                condition.signalAll();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }
}

// Класс, представляющий очередь файлов с потокобезопасными методами добавления, удаления и получения размера и первого элемента
class FileQueue {
    public int capacity;
    public Queue<File> queue = new LinkedList<>();
    public ReentrantLock lock = new ReentrantLock();
    public Condition notFull = lock.newCondition();
    public Condition notEmpty = lock.newCondition();

    public FileQueue(int capacity) {
        this.capacity = capacity;
    }

    public void add(File file) throws InterruptedException {
        lock.lock();
        try {
            // Пока очередь полна, ожидаем
            while (queue.size() == capacity) {
                notFull.await();
            }
            queue.add(file);
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    public void remove() throws InterruptedException {
        lock.lock();
        try {
            // Пока очередь пуста, ожидаем
            while (queue.size() == 0) {
                notEmpty.await();
            }
            queue.remove();
            notFull.signal();
        } finally {
            lock.unlock();
        }
    }

    public int size() {
        return queue.size();
    }

    public File peek() {
        return queue.peek();
    }
}
