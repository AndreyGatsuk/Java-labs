import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;

public class Main {
    // Method to create a file with a specified filename, pathname, and size in
    // bytes
    public static File createFile(final String filename, final String pathname, final long sizeInBytes)
            throws IOException {
        File file = new File(pathname + File.separator + filename);
        file.createNewFile();

        // Create a RandomAccessFile and set its length to the specified size
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        raf.setLength(sizeInBytes);
        raf.close();

        // Return the created file
        return file;
    }

    // Method to copy a file using FileInputStream and FileOutputStream
    private static void copyFileUsingStream(File source, File dest) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            // Create InputStream and OutputStream for the source and destination files
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);

            // Define a buffer to read and write data
            byte[] buffer = new byte[65536];
            int length;

            // Read from the source file and write to the destination file in chunks
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
            // Close the InputStream and OutputStream
            is.close();
            os.close();
        }
    }

    // Method to copy a file using FileChannel
    private static void copyFileUsingChannel(File source, File dest) throws IOException {
        FileChannel sourceChannel = null;
        FileChannel destChannel = null;
        try {
            // Create FileChannels for the source and destination files
            sourceChannel = new FileInputStream(source).getChannel();
            destChannel = new FileOutputStream(dest).getChannel();

            // Transfer data from the source to the destination using FileChannel
            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
        } finally {
            // Close the FileChannels
            sourceChannel.close();
            destChannel.close();
        }
    }

    // Method to copy a file using Apache Commons IO
    private static void copyFileUsingApacheCommonsIO(File source, File dest) throws IOException {
        // Use Apache Commons IO to copy the file
        FileUtils.copyFile(source, dest);
    }

    // Method to copy a file using Java 7 Files class
    private static void copyFileUsingJava7Files(File source, File dest) throws IOException {
        // Use Files.copy method to copy the file
        Files.copy(source.toPath(), dest.toPath());
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        // Get the current working directory
        String currentDir = System.getProperty("user.dir");

        // Create a new directory named "tmp" in the current working directory
        new File(currentDir + "/tmp").mkdir();

        // Create a Path object representing the "tmp" directory
        Path tmpDir = Paths.get(currentDir, "tmp");

        // Print the working directory
        System.out.println("Working directory is - " + tmpDir);

        // Remove previous files from the "tmp" directory
        File directory = new File(tmpDir.toString());
        FileUtils.cleanDirectory(directory);

        // Create a file named "FILE" with a specified size in the "tmp" directory
        final int FILE_SIZE = 100;
        File myFile = createFile("FILE", tmpDir.toString(), FILE_SIZE * 1024 * 1024);

        // Copy the file using FileInputStream and FileOutputStream
        File dest = new File(tmpDir + "/FILE_copy1");
        long start = System.nanoTime();
        copyFileUsingStream(myFile, dest);
        System.out.println("[IOStreams] Time - " + (System.nanoTime() - start));

        // Copy the file using FileChannel
        dest = new File(tmpDir + "/FILE_copy2");
        start = System.nanoTime();
        copyFileUsingChannel(myFile, dest);
        System.out.println("[FileChannel] Time - " + (System.nanoTime() - start));

        // Copy the file using Apache Commons IO
        dest = new File(tmpDir + "/FILE_copy3");
        start = System.nanoTime();
        copyFileUsingApacheCommonsIO(myFile, dest);
        System.out.println("[Apache Commons IO] Time - " + (System.nanoTime() - start));

        // Copy the file using Files class
        dest = new File(tmpDir + "/FILE_copy4");
        start = System.nanoTime();
        copyFileUsingJava7Files(myFile, dest);
        System.out.println("[Files class] Time - " + (System.nanoTime() - start));
    }
}
