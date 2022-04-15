package nl.thecheerfuldev.rssh;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ConfigItems {

    public static final String RSSH_HOME_STRING = System.getProperty("user.home") + "/.config/rssh-java";
    public static final Path RSSH_HOME_PATH = Path.of(RSSH_HOME_STRING);
    public static final String RSSH_PROFILES_STRING = RSSH_HOME_STRING + "/" + ".profiles";
    public static final Path RSSH_PROFILES_PATH = Path.of(RSSH_PROFILES_STRING);
    public static final String RSSH_POSTFIX = "_rssh-java";

    public static void initRssh() throws IOException {
        if (!Files.exists(ConfigItems.RSSH_HOME_PATH)) {
            Files.createDirectory(ConfigItems.RSSH_HOME_PATH);
        }
        initDefaultProfiles();
    }

    private static void initDefaultProfiles() throws IOException {
        if (Files.exists(ConfigItems.RSSH_PROFILES_PATH)) {
            return;
        }
        List<SshProfile> defaultProfiles = List.of(
                new SshProfile("dev", "20001", "https://dev.markhendriks.nl", "mhnas"),
                new SshProfile("dev2", "20002", "https://dev2.markhendriks.nl", "mhnas"),
                new SshProfile("dev3", "20003", "https://dev3.markhendriks.nl", "mhnas")
        );

        defaultProfiles.forEach(SshProfileRepository::add);
        SshProfileRepository.writeToDisk();
    }

}
