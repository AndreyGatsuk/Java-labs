import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

// Класс для преобразования объектов Message в JSON и обратно
public class MessageMapper {

    // Объект ObjectMapper для работы с JSON
    private static ObjectMapper jsonMapper = new ObjectMapper();

    // Метод для преобразования объекта Message в JSON
    public static String messageToJson(Message msg) {
        try {
            return jsonMapper.writeValueAsString(msg);
        } catch (JsonProcessingException e) {
            // Обработка исключения в случае ошибки преобразования
            throw new IllegalStateException(e);
        }
    }

    // Метод для преобразования JSON в объект Message
    public static Message jsonToMessage(String json) {
        try {
            return jsonMapper.readValue(json, Message.class);
        } catch (IOException e) {
            // Обработка исключения в случае ошибки десериализации
            throw new IllegalStateException(e);
        }
    }
}