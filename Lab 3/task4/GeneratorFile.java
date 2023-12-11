import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;

import java.util.Timer;
import java.util.TimerTask;

// Генератор файлов
public class GeneratorFile implements ObservableOnSubscribe<FileData> {
    private final String[] arrTypes;
    private final int MIN_SIZE;
    private final int MAX_SIZE;
    private final int MIN_PERIOD;
    private final int MAX_PERIOD;

    // Конструктор для создания генератора файлов с заданными характеристиками
    public GeneratorFile(String[] arrTypes, int MIN_SIZE, int MAX_SIZE, int MIN_PERIOD, int MAX_PERIOD) {
        this.arrTypes = arrTypes;
        this.MIN_SIZE = MIN_SIZE;
        this.MAX_SIZE = MAX_SIZE;
        this.MIN_PERIOD = MIN_PERIOD;
        this.MAX_PERIOD = MAX_PERIOD;
    }

    // Реализация метода subscribe интерфейса ObservableOnSubscribe
    @Override
    public void subscribe(@NonNull ObservableEmitter<FileData> emitter) {
        // Генерируем файлы и отправляем их в поток данных
        generateSchedule(emitter);
    }

    // Генерация файлов с заданными характеристиками и отправка в поток
    private void generateSchedule(@NonNull ObservableEmitter<FileData> emitter) {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            public void run() {
                // Генерируем новый файл и отправляем в поток
                emitter.onNext(new FileData(generateType(), generateSize()));
                // Рекурсивно вызываем метод для генерации следующего файла
                generateSchedule(emitter);
            }
        };

        // Устанавливаем таймер на выполнение с периодом, генерируемым в заданных
        // пределах
        timer.schedule(task, generatePeriod());
    }

    // Генерация случайного типа файла
    private String generateType() {
        int index = (int) Math.floor(Math.random() * this.arrTypes.length);

        return this.arrTypes[index];
    }

    // Генерация случайного размера файла в заданных пределах
    private int generateSize() {
        return generateNumberInRange(MIN_SIZE, MAX_SIZE);
    }

    // Генерация случайного периода генерации файла в заданных пределах
    private int generatePeriod() {
        return generateNumberInRange(MIN_PERIOD, MAX_PERIOD);
    }

    // Генерация случайного числа в заданном диапазоне
    private int generateNumberInRange(int min, int max) {
        return min + (int) Math.round(Math.random() * (max - min));
    }
}
