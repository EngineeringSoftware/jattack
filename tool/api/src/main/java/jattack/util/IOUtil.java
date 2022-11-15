package jattack.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Utility class for IO.
 */
public class IOUtil {

    public static void writeToFile(String dir, String file, String code) {
        writeToFile(Paths.get(dir, file), code.getBytes(), false);
    }

    public static void writeToFile(String dir, String file, String code, boolean append) {
        writeToFile(Paths.get(dir, file), code.getBytes(), append);
    }

    public static void writeToFile(Path path, byte[] bytes, boolean append) {
        try {
            if (append) {
                Files.write(path, bytes, StandardOpenOption.APPEND);
            } else {
                Files.write(path, bytes);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void createDir(String outputDir) {
        Path outputDirPath = Paths.get(outputDir);
        if (Files.notExists(outputDirPath)) {
            try {
                Files.createDirectories(outputDirPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void saveBytecodeToFile(byte[] classfileBuffer, String name)
            throws IOException {
        Files.write(Paths.get(name + ".class"), classfileBuffer);
    }
}
