import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class ForkJoinMethod {

    public static void main(String[] args) throws Exception {
        long usedBytes = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        int[] array = getInitArray(10000);
        ValueMaxCounter counter = new ValueMaxCounter(array);
        System.out.println(new Date());
        long start = System.currentTimeMillis();
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        System.out.println(new Date());
        System.out.println("Максимальный элемент массива: " + forkJoinPool.invoke(counter));
        long finish = System.currentTimeMillis();
        long time = finish - start;
        System.out.println(new Date());
        System.out.println("Задействовано " + usedBytes + " байт\n" + "Время выполнения: " + time + " милисеккунды\n");
    }

    public static int[] getInitArray(int capacity) {
        int[] array = new int[capacity];
        for (int i = 0; i < capacity; i++) {
            array[i] = i;
        }
        return array;
    }
}

class ValueMaxCounter extends RecursiveTask<Integer> {
    private int[] array;

    public ValueMaxCounter(int[] array) {
        this.array = array;
    }

    @Override
    protected Integer compute() {
        if (array.length <= 2) {
            try {
                System.out.printf("Task %s execute in thread %s%n", this, Thread.currentThread().getName());
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return Arrays.stream(array).max().getAsInt();
        }
        ValueMaxCounter firstHalfArrayValueMaxCounter = new ValueMaxCounter(
                Arrays.copyOfRange(array, 0, array.length / 2));
        ValueMaxCounter secondHalfArrayValueMaxCounter = new ValueMaxCounter(
                Arrays.copyOfRange(array, array.length / 2, array.length));
        firstHalfArrayValueMaxCounter.fork();
        secondHalfArrayValueMaxCounter.fork();
        firstHalfArrayValueMaxCounter.join();
        return Arrays.stream(array).max().getAsInt();
    }
}
