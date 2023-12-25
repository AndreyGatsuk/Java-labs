import java.io.IOException;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.mirea.practice5.services.FileService;

@RestController
public class FileController {

    // Внедрение зависимости FileService
    @Autowired
    FileService service;

    // Обработчик POST-запроса для загрузки файла в базу данных
    @PostMapping("/upload")
    public ResponseEntity uploadToDB(@RequestParam("file") MultipartFile file) throws IOException {
        // Получение чистого имени файла
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        // Вызов сервиса для загрузки файла в базу данных
        service.uploadFile(StringUtils.cleanPath(file.getOriginalFilename()), file.getBytes());

        // Построение URI для скачивания файла
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/download/")
                .path(fileName)
                .toUriString();

        // Возвращение URI в качестве ответа
        return ResponseEntity.ok(fileDownloadUri);
    }

    // Обработчик GET-запроса для скачивания файла из базы данных
    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity downloadFromDB(@PathVariable String fileName) {
        // Получение MIME-типа файла
        String mediaType = service.getMediaType(fileName);

        // Формирование ответа с файлом
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(mediaType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(service.downloadFile(fileName));
    }
}
