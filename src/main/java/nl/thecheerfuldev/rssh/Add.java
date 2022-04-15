package nl.thecheerfuldev.rssh;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;

@Command(
        name = "add",
        description = "Add a new profile with the provided details.",
        mixinStandardHelpOptions = true)
public class Add implements Callable<Integer> {

    @Parameters(index = "0", arity = "1", description = "Profile name.")
    String profile;
    @Parameters(index = "1", arity = "1", description = "Remote port (on the target server).")
    String remotePort;
    @Parameters(index = "2", arity = "1", description = "Full url that this profile is reachable on.")
    String url;
    @Parameters(index = "3", arity = "1", description = "Ssh command (or config) to connect to ssh server.")
    String sshCommand;

    @Option(names = {"--force"}, description = "Forces overriding of existing profile.", arity = "0..1")
    boolean force;

    @Override
    public Integer call() {

        if (SshProfileRepository.existsByName(profile) && !force) {
            System.out.println("Profile [" + profile + "] already exists. Use --force to override existing profile.");
            return CommandLine.ExitCode.USAGE;
        }

        if (isProfileRunning(profile)) {
            System.out.println("Profile [" + profile + "] is running. Overriding will not stop this profile.");
        }

        if (!isValidPort(remotePort)) {
            System.out.println("Please provide a valid [remotePort]: 1-65535");
            return CommandLine.ExitCode.USAGE;
        }

        SshProfileRepository.add(new SshProfile(profile, remotePort, url, sshCommand));
        try {
            SshProfileRepository.writeToDisk();
            System.out.println("Profile [" + profile + "] has been added.");
        } catch (IOException e) {
            System.out.println("Something went wrong while writing to the profiles.");
            return CommandLine.ExitCode.SOFTWARE;
        }
        return CommandLine.ExitCode.OK;
    }

    private boolean isValidPort(String remotePort) {
        int port;
        try {
            port = Integer.parseInt(remotePort);
        } catch (NumberFormatException e) {
            return false;
        }

        return port >= 1 && port <= 65535;
    }

    private boolean isProfileRunning(String profile) {
        return Files.exists(Path.of(ConfigItems.RSSH_HOME_STRING, profile));
    }

}
