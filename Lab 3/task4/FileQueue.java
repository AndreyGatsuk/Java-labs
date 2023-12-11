import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

// Очередь файлов
public class FileQueue implements Observer<FileData>, ObservableOnSubscribe<FileData> {
    private final int LENGTH_QUEUE;
    private final ArrayList<FileData> fileDataArrayList;

    // Конструктор для создания очереди с заданной вместимостью
    public FileQueue(int LENGTH_QUEUE) {
        this.LENGTH_QUEUE = LENGTH_QUEUE;
        this.fileDataArrayList = new ArrayList<>();
    }

    // Реализация метода subscribe интерфейса ObservableOnSubscribe
    @Override
    public void subscribe(@NonNull ObservableEmitter<FileData> emitter) throws Throwable {
        // Создаем таймер для периодической проверки наличия файлов в очереди
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            public void run() {
                // Если очередь не пуста, отправляем первый файл в поток данных
                if (!fileDataArrayList.isEmpty()) {
                    emitter.onNext(fileDataArrayList.remove(0));
                }
            }
        };

        // Устанавливаем таймер на выполнение с периодом 10 мс
        timer.schedule(task, 0, 10);
    }

    // Реализация методов интерфейса Observer
    @Override
    public void onSubscribe(@NonNull Disposable d) {
        System.out.println("onSubscribe " + this.getClass());
    }

    @Override
    public void onNext(@NonNull FileData fileData) {
        // Если в очереди есть место, добавляем файл
        if (fileDataArrayList.size() < LENGTH_QUEUE) {
            fileDataArrayList.add(fileData);
        }
    }

    @Override
    public void onError(@NonNull Throwable e) {
        e.printStackTrace();
    }

    @Override
    public void onComplete() {
        System.out.println("onComplete " + this.getClass());
    }
}
