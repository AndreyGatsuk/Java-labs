import java.util.concurrent.TimeUnit;

public class FirstMethod {
    public static void main(String[] args) throws InterruptedException {
        long usedBytes = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        int[] array;
        array = new int[10000];
        for (int i = 0; i < array.length; i++) {
            array[i] = i;
        }
        long start = System.currentTimeMillis();
        int max = array[0];
        for (int i = 0; i < array.length; i++) {
            if (i > max)
                max = i;
            TimeUnit.MILLISECONDS.sleep(1);
        }
        long finish = System.currentTimeMillis();
        long time = finish - start;
        System.out.println("Задействовано " + usedBytes + " байт\n" + "Время выполнения: " + time + " милисекунды\n"
                + "Максимальный элемент массива: " + max);
    }
}
