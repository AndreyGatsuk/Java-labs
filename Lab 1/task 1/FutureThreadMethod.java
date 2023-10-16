import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class FutureThreadMethod {
    public static void main(String[] args) throws InterruptedException {
        int[] array;
        array = new int[10000];
        for (int i = 0; i < array.length; i++) {
            array[i] = i;
        }
        findMaxInSeveralThreads(array, 8);
    }

    public static Integer findMaxInSeveralThreads(int[] arr, int numOfThreads)
            throws InterruptedException {
        List<MyThread> threads = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(numOfThreads);
        long start = System.currentTimeMillis();
        for (int i = 0; i < numOfThreads; i++) {
            MyThread thread = new MyThread(
                    Arrays.copyOfRange(arr, i * 1000 / numOfThreads, (i + 1) * 1000 / numOfThreads), latch);
            threads.add(thread);
            thread.start();
        }
        latch.await();
        int result = threads.stream().max(Comparator.comparing(MyThread::getMax)).get().max;
        long end = System.currentTimeMillis();
        long usedBytes = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        System.out.println("Задействовано " + usedBytes + " байт\n" + "Время выполнения: " + (end - start)
                + " милисекунды\n" + "Максимальный элемент массива: " + result);
        return result;
    }
}

class MyThread extends Thread {
    public int[] arr;
    public int max;
    CountDownLatch latch;

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
        for (int num : arr) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (num > max) {
                max = num;
            }
        }
        latch.countDown();
    }
}
