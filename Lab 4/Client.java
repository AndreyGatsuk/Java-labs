import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.WebsocketClientTransport;
import io.rsocket.util.DefaultPayload;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Client {

    // Настройка логгера
    private static Logger log = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    static {
        log.setLevel(Level.INFO);
    }

    public static void main(String[] args) {
        final Client client = new Client();

        // Установка соединения с сервером через RSocket
        final RSocket rSocket = client.connect();

        // Настройка простого графического интерфейса Swing для взаимодействия
        final JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 300);
        frame.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        final JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(300, 150));
        panel.setBackground(Color.lightGray);
        panel.setLayout(new GridLayout(2, 2, 10, 10));

        // Создание кнопок для различных типов взаимодействия с RSocket
        final JButton fireAndForgetBtn = new JButton("Fire and Forget");
        final JButton requestResponseBtn = new JButton("Request-Response");
        final JButton requestStreamBtn = new JButton("Request-Stream");
        final JButton requestChannelBtn = new JButton("Request-Channel");

        // ActionListener для обработки кликов по кнопкам
        final ActionListener AListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Обработка различных кликов по кнопкам
                switch (e.getActionCommand()) {
                    case "Fire and Forget":
                        client.fireAndForget(rSocket);
                        break;
                    case "Request-Response":
                        client.requestResponse(rSocket);
                        break;
                    case "Request-Stream":
                        client.requestStream(rSocket);
                        break;
                    case "Request-Channel":
                        client.requestChannel(rSocket);
                        break;
                }
            }
        };

        // Добавление ActionListener к кнопкам
        fireAndForgetBtn.addActionListener(AListener);
        requestResponseBtn.addActionListener(AListener);
        requestStreamBtn.addActionListener(AListener);
        requestChannelBtn.addActionListener(AListener);

        // Добавление кнопок в панель
        panel.add(fireAndForgetBtn);
        panel.add(requestResponseBtn);
        panel.add(requestStreamBtn);
        panel.add(requestChannelBtn);

        // Добавление панели в окно и отображение его
        frame.add(panel);
        frame.setVisible(true);
    }

    // Метод для установления соединения с сервером через RSocket
    private RSocket connect() {
        return RSocketFactory.connect()
                .transport(WebsocketClientTransport.create(8801))
                .start()
                .block();
    }

    // Метод для демонстрации Fire-and-Forget взаимодействия
    private void fireAndForget(RSocket rSocket) {
        log.info("отправка Fire-and-Forget с клиента");
        Flux.just(new Message("Fire-and-Forget JAVA клиент!"))
                .map(msg -> MessageMapper.messageToJson(msg))
                .map(json -> DefaultPayload.create(json))
                .flatMap(message -> rSocket.fireAndForget(message))
                .blockLast();
    }

    // Метод для демонстрации Request-Response взаимодействия
    private void requestResponse(RSocket rSocket) {
        log.info("отправка Request-Response с клиента");
        Flux.just(new Message("Request-Response от JAVA клиента!"))
                .map(msg -> MessageMapper.messageToJson(msg))
                .map(json -> DefaultPayload.create(json))
                .flatMap(message -> rSocket.requestResponse(message))
                .map(payload -> payload.getDataUtf8())
                .doOnNext(payloadString -> {
                    log.info("получен ответ от JAVA клиента");
                    log.info(payloadString);
                })
                .blockLast();
    }

    // Метод для демонстрации Request-Stream взаимодействия
    private void requestStream(RSocket rSocket) {
        log.info("отправка Request-Stream с клиента");
        Flux.just(new Message("Request-Stream от JAVA клиента!"))
                .map(msg -> MessageMapper.messageToJson(msg))
                .map(json -> DefaultPayload.create(json))
                .flatMap(message -> rSocket.requestStream(message))
                .map(payload -> payload.getDataUtf8())
                .doOnNext(payloadString -> log.info(payloadString))
                .blockLast();
    }

    // Метод для демонстрации Request-Channel взаимодействия
    private void requestChannel(RSocket rSocket) {
        log.info("отправка Request-Channel с клиента");
        final Flux<Payload> requestPayload = Flux.range(0, 5)
                .map(count -> new Message("Request-Channel от JAVA клиента! #" + count))
                .map(msg -> {
                    log.info("отправка сообщения: {}", msg.message);
                    return MessageMapper.messageToJson(msg);
                })
                .map(json -> DefaultPayload.create(json));

        rSocket
                .requestChannel(requestPayload)
                .map(payload -> payload.getDataUtf8())
                .doOnNext(payloadString -> log.info(payloadString))
                .blockLast();
    }
}
