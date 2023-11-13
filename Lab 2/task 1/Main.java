import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    // Method to create a file with a specified filename, pathname, and size in
    // bytes
    public static File createFile(final String filename, final String pathname, final long sizeInBytes)
            throws IOException {
        // Create a File object with the specified pathname and filename
        File file = new File(pathname + File.separator + filename);

        // Create a new empty file
        file.createNewFile();

        // Set the length of the file to the specified size in bytes
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        raf.setLength(sizeInBytes);
        raf.close();

        // Return the created file
        return file;
    }

    public static void main(String[] args) throws IOException {
        // Get the current working directory
        String currentDir = System.getProperty("user.dir");

        // Create a new directory named "tmp" in the current working directory
        new File(currentDir + "/tmp").mkdir();

        // Create a Path object representing the "tmp" directory
        Path tmpDir = Paths.get(currentDir, "tmp");

        // Print the working directory
        System.out.println("Working directory is - " + tmpDir);

        // Create a file named "file.txt" in the "tmp" directory with size 0 bytes
        File myFile = createFile("file.txt", tmpDir.toString(), 0);

        // String containing multiple lines of text
        String str = "Hello, World!\nAndrey Gatsuk was here\nMIREA 2023";

        // Convert the string to a byte array
        byte[] bs = str.getBytes();

        // Write the byte array to the created file
        Files.write(myFile.toPath(), bs);

        // Print the contents of the created file
        System.out.println("File " + myFile.getName() + " contents:\n" +
                new String(Files.readAllBytes(myFile.toPath())));
    }
}
