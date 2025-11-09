package util;

import patterns.singleton.DataSource;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public final class CsvUtil {
    private CsvUtil(){}

    public static List<String[]> read(String filename) throws IOException {
        Path p = DataSource.getInstance().resolve(filename);
        if (!Files.exists(p)) return new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(p, StandardCharsets.UTF_8)) {
            List<String[]> rows = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) rows.add(line.split(",", -1));
            return rows;
        }
    }

    public static void write(String filename, List<String[]> rows) throws IOException {
        Path p = DataSource.getInstance().resolve(filename);
        Files.createDirectories(p.getParent());
        try (BufferedWriter bw = Files.newBufferedWriter(p, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            for (String[] r : rows) {
                bw.write(String.join(",", r));
                bw.newLine();
            }
        }
    }
}
