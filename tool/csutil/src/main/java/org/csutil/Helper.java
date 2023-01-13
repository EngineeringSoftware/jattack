package org.csutil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class Helper {
    public static String outFilePath = "checksum.txt";
    public static int nItrs= Integer.MAX_VALUE;

    public static void parseArgs(String[] args) {
        for (String arg : args) {
            if (!arg.startsWith("--")) {
                throw new RuntimeException("Argument does NOT start with --: " + arg + "!");
            }
            String opt = arg.substring(2); // remove "--"
            if (opt.isEmpty()) {
                throw new RuntimeException("Nothing following --: " + arg + "!");
            }
            int pos = opt.indexOf('=');
            String key = "";
            String value = "";
            if (pos == -1) {
                // no "="
                key = opt;
            } else {
                if (pos == opt.length() - 1) {
                    throw new RuntimeException("Nothing following key: " + arg + "!");
                }
                key = opt.substring(0, pos);
                value = opt.substring(pos + 1);
            }
            switch (key) {
            case "outFilePath": {
                outFilePath = value;
                break;
            }
            case "nItrs": {
                nItrs = Integer.parseInt(value);
                break;
            }
            default:
                throw new RuntimeException("Unrecognized option: " + key);
            }
        }
    }

    public static void write(long checksumValue) {
        try {
            Files.write(Path.of(outFilePath),
                        (checksumValue + "\n").getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

