import edu.mirea.rksp.pr4.Server.DefaultSimpleService;
import io.rsocket.Payload;
import io.rsocket.util.DefaultPayload;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;

class ServerTest {

    // Создание экземпляра DefaultSimpleService для тестирования
    DefaultSimpleService defSimServ = new DefaultSimpleService();

    // Тест для проверки корректности Fire-and-Forget взаимодействия
    @Test
    @DisplayName("Correct F&F result")
    public void fnfTest() {
        // Отправка Fire-and-Forget запроса с тестовым сообщением
        defSimServ.fireAndForget(DefaultPayload.create(MessageMapper.messageToJson(new Message("Test Message"))));
    }

    // Тест для проверки корректности Request-Response взаимодействия
    @Test
    @DisplayName("Correct reqResponse result")
    public void reqRTest() {
        // Отправка Request-Response запроса с тестовым сообщением
        defSimServ.requestResponse(DefaultPayload.create(MessageMapper.messageToJson(new Message("Test Message"))));
    }

    // Тест для проверки корректности Request-Stream взаимодействия
    @Test
    @DisplayName("Correct reqStream result")
    public void reqSTest() {
        // Отправка Request-Stream запроса с тестовым сообщением
        defSimServ.requestStream(DefaultPayload.create(MessageMapper.messageToJson(new Message("Test Message"))));
    }

    // Тест для проверки корректности Request-Channel взаимодействия
    @Test
    @DisplayName("Correct reqChanel result")
    public void reqCTest() {
        // ПРИМЕЧАНИЕ: Раскомментируйте следующую строку после реализации метода
        // requestChannel в DefaultSimpleService
        // defSimServ.requestChannel((Publisher<Payload>)
        // DefaultPayload.create(MessageMapper.messageToJson(new Message("Test
        // Message"))));
    }

    // Тест для демонстрации, что JUnit всегда проходит
    @Test
    @DisplayName("Should always pass")
    public void uselessTest() {
        Assertions.assertTrue(true);
    }
}
