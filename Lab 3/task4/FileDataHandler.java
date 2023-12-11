import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

// Обработчик данных файла
public class FileDataHandler implements Observer<FileData> {
    private final int PERIOD_MULTIPLIER;
    private final String TYPE_FILE;

    // Конструктор для создания обработчика с заданным множителем и типом файла
    public FileDataHandler(int PERIOD_MULTIPLIER, String TYPE_FILE) {
        this.PERIOD_MULTIPLIER = PERIOD_MULTIPLIER;
        this.TYPE_FILE = TYPE_FILE;
    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {
        System.out.println("onSubscribe " + this.getClass());
    }

    @Override
    public void onNext(@NonNull FileData fileData) {
        // Проверяем, соответствует ли тип файла текущему обработчику
        if (!fileData.getType().equals(this.TYPE_FILE)) {
            return;
        }

        // Создаем новый поток для обработки файла с учетом его размера
        Thread thread = new Thread(() -> {
            try {
                long sleepTime = (long) fileData.getSize() * this.PERIOD_MULTIPLIER;
                System.out.println("sleep: " + sleepTime);
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Выводим информацию о файле после обработки
            System.out.println(fileData);
        });

        // Запускаем поток
        thread.start();
    }

@Override
    public void onError(@NonNull Throwable e) {
