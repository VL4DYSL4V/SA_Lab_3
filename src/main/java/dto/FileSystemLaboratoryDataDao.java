package dto;

import org.apache.commons.io.FileUtils;
import org.apache.commons.math3.linear.RealVector;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class FileSystemLaboratoryDataDao implements LaboratoryDataDao {

    private static final String DIRECTORY = "Documents";

    private static final String FILE_NAME = "laboratory.txt";

    private Path path;

    public FileSystemLaboratoryDataDao() {
        this.path = getFilePath();
        if (Files.notExists(path)) {
            try {
                Files.createFile(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Path getFilePath() {
        String filePathString = System.getProperty("user.home")
                .concat(File.separator)
                .concat(DIRECTORY)
                .concat(File.separator)
                .concat(FILE_NAME);
        return Paths.get(filePathString);
    }


    @Override
    public void append(int step, RealVector x, RealVector u) {
        try {
            String nextEntry = Files.size(path) == 0 ? getHeader() : buildEntry(step, x, u);
            Files.write(path, nextEntry.getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String buildEntry(int step, RealVector x, RealVector u) {
        String template = "%d\t%.4f\t%.4f\t%.4f\t%.4f%n";
        return String.format(template, step, x.getEntry(0), x.getEntry(1), x.getEntry(2), u.getEntry(0));
    }

    private String getHeader() {
        return "k\t  x1\t  x2\t  x3\t  Uk";
    }

    @Override
    public void clear() {
        try {
            FileUtils.fileWrite(path.toString(), "");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
