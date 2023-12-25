import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

// Простой класс сообщения с использованием Jackson для маппинга JSON
public class Message {

    // Поле для хранения текста сообщения
    public final String message;

    // Конструктор с аннотацией JsonCreator и JsonProperty для десериализации JSON
    @JsonCreator
    public Message(@JsonProperty("message") String message) {
        this.message = message;
    }
}