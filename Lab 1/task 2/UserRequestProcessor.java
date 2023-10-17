import java.util.Scanner;
import java.util.concurrent.*;

public class UserRequestProcessor {
    public static void main(String[] args) throws Exception {
        // Создаем объект класса Calculator для вычисления квадрата числа
        Calculator squareCalculator = new Calculator();

        // Создаем объект Scanner для ввода чисел пользователем
        Scanner in = new Scanner(System.in);

        // Запрашиваем число у пользователя
        System.out.print("Вычислить квадрат для числа ");
        int x = in.nextInt();

        // Вычисляем квадрат числа в отдельном потоке и получаем объект Future для получения результата
        Future<Integer> future = squareCalculator.calculate(x);

        // Запрашиваем еще одно число у пользователя
        int x1 = in.nextInt();

        // Вычисляем квадрат второго числа в отдельном потоке и получаем объект Future для получения результата
        Future<Integer> future1 = squareCalculator.calculate(x1);

        // Ожидаем завершения вычислений и выводим результаты
        System.out.println("Квадрат числа: " + future.get());
        System.out.println("Квадрат числа: " + future1.get());
    }
}

// Класс Calculator представляет собой вычислитель, который вычисляет квадрат числа в отдельном потоке
class Calculator {
    // Создаем пул потоков с фиксированным числом потоков (в данном случае, 2)
    private ExecutorService executor = Executors.newFixedThreadPool(2);

    // Метод calculate принимает число, вычисляет его квадрат асинхронно и возвращает Future с результатом
    public Future<Integer> calculate(Integer input) {
        return executor.submit(() -> {
            // Генерируем случайную задержку от 1 до 5 секунд (в миллисекундах)
            int time = (int) ((Math.random() * 4000) + 1000);
            System.out.println("Время задержки: " + time + " миллисекунд");

            // Имитируем обработку запроса с задержкой
            Thread.sleep(time);

            // Вычисляем квадрат числа
            return input * input;
        });
    }
}
