import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

import java.util.concurrent.TimeUnit;

public class Main {
    private static int MIN_TEMPERATURE_VALUE = 15;
    private static int MAX_TEMPERATURE_VALUE = 30;
    private static int MIN_CO2_VALUE = 30;
    private static int MAX_CO2_VALUE = 100;
    private static int NORM_TEMPERATURE = 25;
    private static int NORM_CO2 = 70;

    public static void main(String[] args) {
        // Создание объектов датчиков температуры и CO2
        Sensor sensorTemperature = new Sensor(MIN_TEMPERATURE_VALUE, MAX_TEMPERATURE_VALUE);
        Sensor sensorCO2 = new Sensor(MIN_CO2_VALUE, MAX_CO2_VALUE);

        // Создание Observable для каждого датчика
        Observable<Integer> observableCO2 = Observable.create(sensorCO2);
        Observable<Integer> observableTemperature = Observable.create(sensorTemperature);

        // Объединение потоков данных с датчиков
        observableCO2.join(
                observableTemperature,
                // Задержка для корректного соединения значений
                i -> Observable.timer(100, TimeUnit.MILLISECONDS),
                i -> Observable.timer(100, TimeUnit.MILLISECONDS),
                // Создание объекта, содержащего данные от обоих датчиков
                (co2, temperature) -> new DataSensor(temperature, co2)).subscribe(new Observer<>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        System.out.println("onSubscribe");
                    }

                    @Override
                    public void onNext(@NonNull DataSensor dataSensor) {
                        // Получение данных от обоих датчиков
                        int temperature = dataSensor.getTemperature();
                        int co2 = dataSensor.getCo2();

                        String warningMessage = "";

                        // Проверка на превышение нормы по температуре и/или CO2
                        if (temperature > NORM_TEMPERATURE && co2 > NORM_CO2) {
                            warningMessage = "ALARM!!!";
                        } else if (temperature > NORM_TEMPERATURE || co2 > NORM_CO2) {
                            warningMessage = "Warning!";
                        }

                        // Вывод сообщения с данными и предупреждением
                        System.out.println("Temperature: " + temperature + ", CO2: " + co2 + " " + warningMessage);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("onComplete");
                    }
                });
    }
}
