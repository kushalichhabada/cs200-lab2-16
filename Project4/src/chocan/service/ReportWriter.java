package chocan.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Saves report text to a file in the reports folder (the requirements say
 * reports are "sent" to members, providers and the bank; here that means
 * writing a file for each one).
 *
 * @author [TEAM MEMBER NAME]
 */
public class ReportWriter {

    private final Path directory;

    public ReportWriter(Path directory) {
        this.directory = directory;
    }

    /**
     * Writes {@code content} to {@code <directory>/<baseName>.txt}.
     *
     * @param baseName file name without extension; unsafe characters are replaced by '_'
     * @return the path of the written file
     */
    public Path write(String baseName, String content) throws IOException {
        Files.createDirectories(directory);
        String safeName = baseName.replaceAll("[^A-Za-z0-9._-]", "_");
        Path file = directory.resolve(safeName + ".txt");
        Files.write(file, content.getBytes(StandardCharsets.UTF_8));
        return file;
    }
}
