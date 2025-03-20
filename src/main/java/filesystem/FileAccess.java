package filesystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileAccess {
    public String readFileContent(Path path) throws IOException {
        return Files.readString(path);
    }
}
