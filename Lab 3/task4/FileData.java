import java.util.UUID;

// Класс, представляющий данные файла
public class FileData {
    private String type;
    private int size;
    private UUID uuid;

    // Конструктор для создания объекта FileData с заданным типом и размером
    public FileData(String type, int size) {
        this.type = type;
        this.size = size;
        this.uuid = UUID.randomUUID();
    }

    // Геттеры и сеттеры для доступа к полям класса

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public UUID getUuid() {
        return uuid;
    }

    // Переопределение метода toString для удобного вывода объекта в строковом
    // представлении
    @Override
    public String toString() {
        return "FileData{" +
                "type='" + type + '\'' +
                ", size=" + size +
                ", uuid=" + uuid +
                '}';
    }
}
