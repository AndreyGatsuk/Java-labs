import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "binary_file")
@Data
@NoArgsConstructor
public class BinaryFile {

    // Идентификатор файла
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    // Имя файла
    @Column(name = "file_name")
    String fileName;

    // Данные файла в виде массива байт
    @Column(name = "file_data")
    byte[] fileData;

    // Конструктор по умолчанию для JPA
    public BinaryFile() {
    }

    // Конструктор для установки имени и данных файла
    public BinaryFile(String name, byte[] data) {
        this.fileData = data;
        this.fileName = name;
    }
}
