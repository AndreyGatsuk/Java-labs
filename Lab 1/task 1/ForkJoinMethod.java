import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class ForkJoinMethod {

    public static void main(String[] args) throws Exception {
        // Измеряем используемую память до создания массива
        long usedBytes = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        // Создаем и инициализируем массив из 10000 элементов
        int[] array = getInitArray(10000);

        // Создаем объект для подсчета максимального значения в массиве с использованием ForkJoin
        ValueMaxCounter counter = new ValueMaxCounter(array);

        // Засекаем время начала выполнения
        System.out.println(new Date());
        long start = System.currentTimeMillis();

        // Создаем пул ForkJoin и вызываем метод compute() для подсчета максимального значения
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        System.out.println(new Date());
        System.out.println("Максимальный элемент массива: " + forkJoinPool.invoke(counter));

        // Засекаем время окончания выполнения и вычисляем общее время выполнения
        long finish = System.currentTimeMillis();
        long time = finish - start;

        // Выводим информацию о памяти, времени выполнения и найденном максимальном элементе массива
        System.out.println(new Date());
        System.out.println("Задействовано " + usedBytes + " байт\n" + "Время выполнения: " + time + " миллисекунды\n");
    }

    // Метод для инициализации и заполнения массива
    public static int[] getInitArray(int capacity) {
        int[] array = new int[capacity];
        for (int i = 0; i < capacity; i++) {
            array[i] = i;
        }
        return array;
    }
}

// Класс ValueMaxCounter наследует RecursiveTask и представляет собой задачу подсчета максимального значения в массиве
class ValueMaxCounter extends RecursiveTask<Integer> {
    private int[] array;

    public ValueMaxCounter(int[] array) {
        this.array = array;
    }

    @Override
    protected Integer compute() {
        // Если длина массива меньше или равна 2, находим максимальный элемент
        if (array.length <= 2) {
            try {
                System.out.printf("Task %s execute in thread %s%n", this, Thread.currentThread().getName());
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return Arrays.stream(array).max().getAsInt();
        }

        // Разбиваем массив на две половины
        ValueMaxCounter firstHalfArrayValueMaxCounter = new ValueMaxCounter(Arrays.copyOfRange(array, 0, array.length / 2));
        ValueMaxCounter secondHalfArrayValueMaxCounter = new ValueMaxCounter(Arrays.copyOfRange(array, array.length / 2, array.length));

        // Рекурсивно вызываем compute() для каждой половины
        firstHalfArrayValueMaxCounter.fork();
        secondHalfArrayValueMaxCounter.fork();

        // Ожидаем результаты от подзадач и находим максимальный элемент в исходном массиве
        firstHalfArrayValueMaxCounter.join();
        return Arrays.stream(array).max().getAsInt();
    }
}
