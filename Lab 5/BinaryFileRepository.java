import org.springframework.data.jpa.repository.JpaRepository;
import ru.mirea.practice5.models.BinaryFile;

public interface BinaryFileRepository extends JpaRepository<BinaryFile, Integer> {

    // Метод для поиска файла по имени
    BinaryFile findByFileName(String fileName);

    // Метод для проверки существования файла по имени
    Boolean existsByFileName(String fileName);
}
