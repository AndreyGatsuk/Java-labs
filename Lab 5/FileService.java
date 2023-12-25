import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import ru.mirea.practice5.models.BinaryFile;
import ru.mirea.practice5.repositories.BinaryFileRepository;

@Service
public class FileService {

    // Внедрение зависимости BinaryFileRepository
    @Autowired
    BinaryFileRepository repository;

    // Метод для загрузки файла в базу данных
    public void uploadFile(String name, byte[] data) {
        // Проверка, существует ли файл с таким именем
        if (!repository.existsByFileName(name)) {
            // Создание объекта BinaryFile и сохранение его в базу данных
            BinaryFile file = new BinaryFile(name, data);
            repository.save(file);
        }
    }

    // Метод для скачивания файла из базы данных
    public byte[] downloadFile(String name) {
        // Поиск файла по имени и получение данных
        BinaryFile binaryFile = repository.findByFileName(name);
        return binaryFile.getFileData();
    }

    // Метод для определения MIME-типа файла
    public String getMediaType(String name) {
        // Извлечение расширения файла из имени
        String getFormat = name.split("\\.")[1];
        String type = null;

        // Определение MIME-типа в зависимости от расширения файла
        switch (getFormat) {
            case "pdf":
                type = MediaType.APPLICATION_PDF_VALUE;
                break;
            case "txt":
                type = MediaType.TEXT_PLAIN_VALUE;
                break;
            case "jpg":
            case "jpeg":
                type = MediaType.IMAGE_JPEG_VALUE;
                break;
            case "png":
                type = MediaType.IMAGE_PNG_VALUE;
                break;
        }
        return type;
    }
}
