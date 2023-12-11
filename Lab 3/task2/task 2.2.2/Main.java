import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;

import java.util.Random;

public class Main {
    // Генератор случайных чисел
    static Random rand = new Random();

    public static void main(String[] args) throws InterruptedException {
        // Создаем Observable с 1000 элементами
        Observable<Object> obs = Observable.create(subscriber -> {
            // Генерация и отправка чисел от 0 до 999 в поток данных
            for (int i = 0; i < 1000; i++) {
                subscriber.onNext(i);
            }
        });

        // Подписываем первый потребитель на Observable и выводим элементы на консоль
        Subscription s = obs.subscribe(System.out::println);

        // Подписываем второго потребителя на Observable и выводим элементы на консоль
        Subscription s1 = obs.subscribe(System.out::println);
    }
}
