import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;

import java.util.Random;

public class Main {

    // Генератор случайных чисел
    static Random rand = new Random();

    public static void main(String[] args) throws InterruptedException {
        // Создаем Observable с 10 элементами
        Observable<Object> obs = Observable.create(subscriber -> {
            // Генерация и отправка чисел от 0 до 9 в поток данных
            for (int i = 0; i < 10; i++) {
                subscriber.onNext(i);
            }
        });

        // Пропускаем последние 5 элементов и подписываемся на оставшиеся элементы
        Subscription s = obs.skipLast(5).subscribe(System.out::println);
    }
}
