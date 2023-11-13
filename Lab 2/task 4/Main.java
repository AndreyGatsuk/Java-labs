import static java.nio.file.StandardWatchEventKinds.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    // Compute a 16-bit checksum for all the remaining bytes
    // in the given byte buffer
    private static int sum(ByteBuffer bb) {
        int sum = 0;
        while (bb.hasRemaining()) {
            if ((sum & 1) != 0)
                sum = (sum >> 1) + 0x8000;
            else
                sum >>= 1;
            sum += bb.get() & 0xff;
            sum &= 0xffff;
        }
        return sum;
    }

    // Compute and print a checksum for the given file
    public static void sum(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        FileChannel fc = fis.getChannel();

        // Get the file's size and then map it into memory
        int sz = (int) fc.size();
        MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, sz);

        // Compute and print the checksum
        int sum = sum(bb);
        int kb = (sz + 1023) / 1024;
        String s = Integer.toString(sum);
        System.out.println(file.getName() + ": FileSize " + kb + " KB, CheckSum=" + s);

        // Close the channel and the stream
        fc.close();
        fis.close();
    }

    public static void main(String[] args) {
        // Set up the path for monitoring
        var path = "tmp";
        String currentDir = System.getProperty("user.dir");
        new File(currentDir + File.separator + path).mkdir();
        Path tmpDir = Paths.get(currentDir, path);
        System.out.println("Working directory is - " + tmpDir);

        try {
            // Set up a watcher for the specified directory
            WatchService watcher = FileSystems.getDefault().newWatchService();
            // creating reserve folder
            var reservePath = ".reserve";
            Reserve(tmpDir.getFileName().toString(), reservePath);
            tmpDir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);

            System.out.println("Watch Service registered for dir: " + tmpDir.getFileName());

            while (true) {
                WatchKey key;
                try {
                    // Wait for key events
                    key = watcher.take();
                } catch (InterruptedException ex) {
                    return;
                }

                // Process the events
                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();

                    @SuppressWarnings("unchecked")
                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path fileName = ev.context();

                    System.out.println(kind.name() + ": " + fileName);

                    // Handle different kinds of events
                    if (kind == OVERFLOW) {
                        continue;
                    } else if (kind == ENTRY_CREATE) {
                        try {
                            // Sleep for a second to ensure the file is completely created
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        CreateBak(Paths.get(path, fileName.toString()),
                                Paths.get(reservePath, fileName.toString()));
                    } else if (kind == ENTRY_DELETE) {
                        try {
                            // Compute checksum when a file is deleted
                            sum(Paths.get(reservePath, fileName.toString() + ".bak").toFile());
                        } catch (IOException e) {
                            System.err.println(fileName.toString() + ": " + e);
                        }
                        DeleteBakFile(Paths.get(reservePath, fileName.toString() + ".bak"));
                    } else if (kind == ENTRY_MODIFY) {
                        // Perform diff and create backup when a file is modified
                        Diff(Paths.get(path, fileName.toString()),
                                Paths.get(reservePath, fileName.toString() + ".bak"));
                        CreateBak(Paths.get(path, fileName.toString()),
                                Paths.get(reservePath, fileName.toString()));
                    }
                }

                // Reset the key to continue receiving events
                boolean valid = key.reset();
                if (!valid) {
                    break;
                }
            }

        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

    // Compare the two files and print added or deleted lines
    private static void Diff(Path path, Path reservePath) {
        try {
            Stream<String> stream = Files.lines(path);
            List<String> newList = stream.collect(Collectors.toList());
            stream = Files.lines(reservePath);
            List<String> oldList = stream.collect(Collectors.toList());

            System.out.println("=============");
            System.out.printf("Old list: ");
            System.out.println(oldList);

            ArrayList<String> removedList = new ArrayList<String>();
            for (String itemToRemove : oldList) {
                if (!newList.contains(itemToRemove)) {
                    removedList.add(itemToRemove);
                }
            }

            ArrayList<String> addedList = new ArrayList<String>();
            for (String itemToAdd : newList) {
                if (!oldList.contains(itemToAdd)) {
                    addedList.add(itemToAdd);
                }
            }

            System.out.printf("New list: ");
            System.out.println(newList);

            System.out.println("=============");
            System.out.printf("Removed: ");
            System.out.println(removedList);

            System.out.printf("Added: ");
            System.out.println(addedList);

            stream.close();
        } catch (Throwable cause) {
            System.out.println("Exception");
        }
    }

    private static void Reserve(String path, String reservePath) {
        // Set up the reserve folder and copy existing files to it
        CreateDirectory(reservePath);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                // Delete the reserve folder on program shutdown
                DeleteDirectory(reservePath);
            }
        });
        CopyFolder(path, reservePath);
    }

    private static void CopyFolder(String path, String reservePath) {
        // Recursively copy files from the source folder to the reserve folder
        var files = new File(path).listFiles();
        for (var file : files) {
            if (file.isDirectory()) {
                CopyFolder(file.getAbsolutePath(), reservePath);
            } else {
                var reserveFile = new File(reservePath + File.separator + file.getName() + ".bak");
                try {
                    reserveFile.createNewFile();
                    var fis = new FileInputStream(file);
                    var fos = new FileOutputStream(reserveFile);
                    var buffer = new byte[1024];
                    int length;
                    while ((length = fis.read(buffer)) > 0) {
                        fos.write(buffer, 0, length);
                    }
                    fis.close();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void CreateBak(Path path, Path reservePath) {
        // Create a backup file using Java NIO
        try {
            java.nio.file.Files.copy(path, reservePath.resolveSibling(
                    reservePath.getFileName() + ".bak"), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void DeleteBakFile(Path path) {
        // Delete a backup file
        try {
            java.nio.file.Files.delete(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void CreateDirectory(String name) {
        // Create a directory if it does not exist
        File file = new File(name);
        if (!file.exists()) {
            if (file.mkdir()) {
                System.out.println("Created reserve folder for monitoring.");
            } else {
                System.out.println("Failed to create reserve folder for monitoring.");
            }
        }
    }

    private static void DeleteDirectory(String name) {
        // Delete a directory and its contents
        CleanDirectory(name);
        File file = new File(name);
        if (file.exists()) {
            if (file.delete()) {
                System.out.println("Stop monitoring...");
            } else {
                System.out.println("Failed to delete reserve folder for monitoring.");
            }
        }
    }

    private static void CleanDirectory(String name) {
        // Delete all files in a directory
        File file = new File(name);
        if (file.exists()) {
            File[] files = file.listFiles();
            for (File f : files) {
                if (f.isFile()) {
                    f.delete();
                }
            }
        }
    }
}
