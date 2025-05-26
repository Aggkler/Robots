package gui;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CommandReader {
    private final String fileName;

    public CommandReader(String fileName) {
        this.fileName = fileName;
    }

    public List<String> loadCommandsFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            List<String> commands = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                commands.add(line.trim());
            }
            return commands;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeFirstCommandFromFile() {
        try {
            List<String> lines = Files.readAllLines(Paths.get(fileName));
            if (!lines.isEmpty()) {
                lines.remove(0);
                Files.write(Paths.get(fileName), lines);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
