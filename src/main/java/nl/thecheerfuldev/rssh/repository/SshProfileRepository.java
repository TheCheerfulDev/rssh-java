package nl.thecheerfuldev.rssh.repository;

import nl.thecheerfuldev.rssh.config.ConfigItems;
import nl.thecheerfuldev.rssh.entity.SshProfile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public final class SshProfileRepository {
    private static final Map<String, SshProfile> DATABASE = new HashMap<>();


    public SshProfileRepository() {
        init();
    }

    private void init() {
        if (DATABASE.isEmpty()) {
            loadFromDisk();
        }
    }

    public void add(SshProfile profile) {
        DATABASE.put(profile.profile(), profile);
    }

    public void remove(String profile) {
        DATABASE.remove(profile);
    }

    public List<String> getAllProfileNames() {
        return DATABASE.keySet()
                .stream()
                .sorted()
                .toList();
    }

    private void loadFromDisk() {
        if (Files.notExists(ConfigItems.RSSH_PROFILES_PATH)) {
            return;
        }

        try (Stream<String> stringStream = Files.lines(ConfigItems.RSSH_PROFILES_PATH, StandardCharsets.UTF_8)) {
            stringStream
                    .filter(string -> !string.isBlank())
                    .filter(string -> !string.startsWith("#"))
                    .forEach(string -> {
                        String[] split = string.split(";");
                        DATABASE.put(split[0], new SshProfile(split[0], split[1], split[2], split[3]));
                    });

        } catch (IOException exception) {
            System.out.println("Something went wrong while reading the profiles");
        }
    }

    public void writeToDisk() throws IOException {
        Path path = ConfigItems.RSSH_PROFILES_PATH;

        Files.deleteIfExists(path);

        try {
            Files.createFile(path);
            DATABASE.values().stream()
                    .sorted(Comparator.comparing(SshProfile::profile))
                    .forEach(sshProfile -> {
                        try {
                            Files.writeString(path, sshProfile.profile() + ";" + sshProfile.remotePort() + ";" + sshProfile.url() + ";" + sshProfile.sshCommand() + System.lineSeparator(),
                                    StandardCharsets.UTF_8, StandardOpenOption.APPEND);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            System.out.println("Something went wring while saving the git-cd database.");
        }
    }

    public SshProfile get(String name) {
        return DATABASE.get(name);
    }

    public boolean exists(String name) {
        return DATABASE.containsKey(name);
    }

}
