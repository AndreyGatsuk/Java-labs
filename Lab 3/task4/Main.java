import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.observables.ConnectableObservable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class Main {
    private static final int PERIOD_MULTIPLIER = 7;

    public static void main(String[] args) {
        // Создаем генератор файлов с заданными характеристиками
        GeneratorFile generatorFile = new GeneratorFile(
                new String[] { "XML", "JSON", "XLS" },
                10, 100,
                100, 1000);

        // Создаем очередь с вместимостью 5 файлов
        FileQueue fileQueue = new FileQueue(5);

        // Создаем обработчики файлов для каждого типа
        FileDataHandler handlerXML = new FileDataHandler(PERIOD_MULTIPLIER, "XML");
        FileDataHandler handlerJSON = new FileDataHandler(PERIOD_MULTIPLIER, "JSON");
        FileDataHandler handlerXLS = new FileDataHandler(PERIOD_MULTIPLIER, "XLS");

        // Подписываем генератор на очередь
        Observable.create(generatorFile).subscribe(fileQueue);

        // Создаем ConnectableObservable для возможности множественных подписок
        ConnectableObservable<FileData> observableFileQueue = Observable.create(fileQueue).publish();

        // Подписываем обработчики на ConnectableObservable
        observableFileQueue.subscribe(handlerXML);
        observableFileQueue.subscribe(handlerJSON);
        observableFileQueue.subscribe(handlerXLS);

        // Соединяем ConnectableObservable для начала испускания элементов
        observableFileQueue.connect();
    }
}
