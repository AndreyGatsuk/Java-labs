import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class FutureThreadMethod {
    public static void main(String[] args) throws InterruptedException {
        // Создаем и инициализируем массив из 10000 элементов
        int[] array = new int[10000];
        for (int i = 0; i < array.length; i++) {
            array[i] = i;
        }

        // Вызываем метод для поиска максимального элемента в нескольких потоках
        findMaxInSeveralThreads(array, 8);
    }

    // Метод для поиска максимального элемента в нескольких потоках
    public static Integer findMaxInSeveralThreads(int[] arr, int numOfThreads)
            throws InterruptedException {
        // Создаем список потоков и счетчик ожидания для синхронизации потоков
        List<MyThread> threads = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(numOfThreads);
        long start = System.currentTimeMillis();

        // Разбиваем массив на части и создаем и запускаем потоки
        for (int i = 0; i < numOfThreads; i++) {
            MyThread thread = new MyThread(
                    Arrays.copyOfRange(arr, i * 1000 / numOfThreads, (i + 1) * 1000 / numOfThreads), latch);
            threads.add(thread);
            thread.start();
        }

        // Ожидаем завершения всех потоков
        latch.await();

        // Находим максимальный результат среди всех потоков
        int result = threads.stream().max(Comparator.comparing(MyThread::getMax)).get().max;
        long end = System.currentTimeMillis();
        long usedBytes = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        // Выводим информацию о памяти, времени выполнения и найденном максимальном элементе массива
        System.out.println("Задействовано " + usedBytes + " байт\n" + "Время выполнения: " + (end - start)
                + " миллисекунды\n" + "Максимальный элемент массива: " + result);
        return result;
    }
}

// Класс MyThread наследует Thread и представляет собой поток для поиска максимального элемента в массиве
class MyThread extends Thread {
    private int[] arr;
    private int max;
    private CountDownLatch latch;

    public MyThread(int[] arr, CountDownLatch latch) {
        this.arr = arr;
        this.max = arr[0];
        this.latch = latch;
    }

    public int getMax() {
        return max;
    }

    @Override
    public void run() {
        // Поиск максимального элемента в подмассиве
        for (int num : arr) {
            try {
                Thread.sleep(1); // Имитация операции и замера времени выполнения
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (num > max) {
                max = num;
            }
        }
        latch.countDown(); // Уменьшаем счетчик ожидания
    }
}
