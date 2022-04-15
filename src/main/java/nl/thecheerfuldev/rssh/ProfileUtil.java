package nl.thecheerfuldev.rssh;

import java.nio.file.Files;
import java.nio.file.Path;

public final class ProfileUtil {

    private ProfileUtil() {
    }

    public static boolean isProfileRunning(String profile) {
        return Files.exists(Path.of(ConfigItems.RSSH_HOME_STRING, profile));
    }

    public static boolean isValidPort(String localPort) {
        int port;
        try {
            port = Integer.parseInt(localPort);
        } catch (NumberFormatException e) {
            return false;
        }
        return port >= 1 && port <= 65535;
    }
}
