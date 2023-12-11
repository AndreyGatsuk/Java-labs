import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;

import java.util.Timer;
import java.util.TimerTask;

public class Sensor implements ObservableOnSubscribe<Integer> {
    private final int minValue;
    private final int maxValue;
    private final Timer timer;

    // Конструктор для установки минимального и максимального значений датчика
    public Sensor(int minValue, int maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        // Создание таймера для эмуляции публикации значений датчика каждую секунду
        timer = new Timer();
    }

    // Метод, реализующий интерфейс ObservableOnSubscribe
    @Override
    public void subscribe(@NonNull ObservableEmitter<Integer> emitter) {
        // Запуск таймера для эмуляции работы датчика
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // Генерация случайного значения и отправка его в Observable
                emitter.onNext((int) Math.round(Math.random() * (maxValue - minValue) + minValue));
            }
        }, 0, 1000);
    }
}
