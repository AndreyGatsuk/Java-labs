import io.reactivex.rxjava3.core.Observable;

public class Main {
    // Генератор случайных чисел
    static Random rand = new Random();

    public static void main(String[] args) throws InterruptedException {
        int num = 0;

        // Генерация 1000 случайных чисел
        for (int i = 0; i < 1000; i++) {
            // Генерация случайного числа от 0 до 1000
            num = rand.nextInt(1000);

            // Создание Observable из единственного числа и фильтрация значений больше 500
            Observable.from(num).filter(num2 -> num2 > 500).subscribe(System.out::println);
        }
    }
}
