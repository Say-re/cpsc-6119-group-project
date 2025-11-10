package patterns.singleton;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class DataSource {
    private static final DataSource INSTANCE = new DataSource();
    private final Path dataDir;

    private DataSource() { this.dataDir = Paths.get("src/main/java/data"); }

    public static DataSource getInstance() { return INSTANCE; }

    public Path resolve(String filename) { return dataDir.resolve(filename); }
}
