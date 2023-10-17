import java.util.concurrent.TimeUnit;

public class FirstMethod {

    public static void main(String[] args) throws InterruptedException {
        // Измеряем используемую память до создания массива
        long usedBytes = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        // Создаем массив из 10000 элементов и заполняем его значениями от 0 до 9999
        int[] array;
        array = new int[10000];
        for (int i = 0; i < array.length; i++) {
            array[i] = i;
        }

        // Засекаем время начала выполнения поиска максимального элемента в массиве
        long start = System.currentTimeMillis();

        // Ищем максимальный элемент в массиве
        int max = array[0];
        for (int i = 0; i < array.length; i++) {
            if (array[i] > max) {
                max = array[i];
            }
            // Задержка в 1 мс для имитации операции и замера времени выполнения
            TimeUnit.MILLISECONDS.sleep(1);
        }

        // Засекаем время окончания выполнения и вычисляем общее время выполнения
        long finish = System.currentTimeMillis();
        long time = finish - start;

        // Выводим информацию о памяти, времени выполнения и найденном максимальном элементе
        System.out.println("Задействовано " + usedBytes + " байт\n"
                + "Время выполнения: " + time + " миллисекунд\n"
                + "Максимальный элемент массива: " + max);
    }
}
