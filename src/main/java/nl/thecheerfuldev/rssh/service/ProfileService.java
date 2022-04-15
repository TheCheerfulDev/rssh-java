package nl.thecheerfuldev.rssh.service;

import nl.thecheerfuldev.rssh.config.ConfigItems;
import nl.thecheerfuldev.rssh.entity.RunningProfile;
import nl.thecheerfuldev.rssh.entity.SshProfile;
import nl.thecheerfuldev.rssh.repository.SshProfileRepository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Stream;

public final class ProfileService {

    private static final SshProfileRepository sshProfileRepository = new SshProfileRepository();

    private ProfileService() {
    }

    public static boolean isProfileRunning(String profile) {
        return Files.exists(Path.of(ConfigItems.RSSH_HOME_STRING, profile));
    }

    public static boolean isInvalidPort(String localPort) {
        int port;
        try {
            port = Integer.parseInt(localPort);
        } catch (NumberFormatException e) {
            return true;
        }
        return port < 1 || port > 65535;
    }

    public static void saveRunningProfile(SshProfile sshProfile, String host, String localPort) throws IOException {
        RunningProfile runningProfile = new RunningProfile(sshProfile, host, localPort);
        Path runningProfileFile = Files.createFile(Path.of(ConfigItems.RSSH_HOME_STRING, sshProfile.profile() + ConfigItems.RSSH_POSTFIX));
        Files.writeString(runningProfileFile, runningProfile.toString(),
                StandardCharsets.UTF_8, StandardOpenOption.APPEND);
    }

    public static RunningProfile getRunningProfile(String profile) throws IOException {
        Path runningProfileFile = Path.of(ConfigItems.RSSH_HOME_STRING, profile + ConfigItems.RSSH_POSTFIX);
        try (Stream<String> stringStream = Files.lines(runningProfileFile, StandardCharsets.UTF_8)) {
            String runningString = stringStream.toList().get(0);
            String[] split = runningString.split(";");
            return new RunningProfile(split[0], split[1], split[2], split[3], split[4], split[5]);
        }
    }

    public static void deleteForProfile(String profile) throws IOException {
        Files.deleteIfExists(Path.of(ConfigItems.RSSH_HOME_STRING, profile + ConfigItems.RSSH_POSTFIX));
    }

    public static void add(SshProfile profile) throws IOException {
        sshProfileRepository.add(profile);
        sshProfileRepository.writeToDisk();
    }

    public static void addAll(List<SshProfile> defaultProfiles) throws IOException {
        defaultProfiles.forEach(sshProfileRepository::add);
        sshProfileRepository.writeToDisk();
    }

    public static void remove(String profile) throws IOException {
        sshProfileRepository.remove(profile);
        sshProfileRepository.writeToDisk();
    }

    public static List<String> getAllProfileNames() {
        return sshProfileRepository.getAllProfileNames();
    }

    public static SshProfile getSshProfile(String name) {
        return sshProfileRepository.get(name);
    }

    public static boolean exists(String name) {
        return sshProfileRepository.exists(name);
    }

    public static List<String> getRunningProfiles() {
        return sshProfileRepository.getAllProfileNames()
                .stream()
                .filter(ProfileService::isProfileRunning)
                .toList();
    }
}
