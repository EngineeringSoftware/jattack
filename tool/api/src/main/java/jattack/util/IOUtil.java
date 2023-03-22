package jattack.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utility class for IO.
 */
public class IOUtil {

    /**
     * Write the given code to the given file under the given
     * directory and sub-directories, always overwriting the file.
     */
    public static void writeToFile(
            String dir, String file, String code, String... subDirs) {
        writeToFile(dir, file, code, false, subDirs);
    }

    /**
     * Write the given code to the given file under the given
     * directory and sub-directories.
     */
    public static void writeToFile(
            String dir, String file, String code, boolean append, String... subDirs) {
        List<String> dirs = new ArrayList<>();
        dirs.addAll(Arrays.asList(subDirs));
        dirs.add(file);
        try {
            writeToFile(Paths.get(dir, dirs.toArray(String[]::new)),
                        code.getBytes(StandardCharsets.UTF_8),
                        append);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeToFile(Path path, byte[] bytes, boolean append)
            throws IOException{
        Path dir = path.getParent();
        if (Files.notExists(dir)) {
            Files.createDirectories(dir);
        }
        if (append) {
            Files.write(path, bytes, StandardOpenOption.APPEND);
        } else {
            Files.write(path, bytes);
        }
    }

    public static void saveBytecodeToFile(byte[] classfileBuffer, String name)
            throws IOException {
        Files.write(Paths.get(name + ".class"), classfileBuffer);
    }
}
