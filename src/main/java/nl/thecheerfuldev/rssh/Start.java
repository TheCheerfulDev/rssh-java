package nl.thecheerfuldev.rssh;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.Callable;

@Command(name = "start",
        description = "Start the given profile.",
        mixinStandardHelpOptions = true)
public class Start implements Callable<Integer> {

    @Parameters(index = "0", arity = "1")
    String profile;

    @Parameters(index = "1", arity = "0..1")
    String localPort = "8080";

    @Option(names = {"-h", "--host"}, description = "Override the default (localhost) host.", arity = "0..1")
    String host = "localhost";

    @Override
    public Integer call() {

        if (host == null || host.isBlank()) {
            host = "localhost";
        }

        if (!isValidPort(localPort)) {
            System.out.println("Please provide a valid [localPort]: 1-65535");
            return CommandLine.ExitCode.USAGE;
        }

        if (!SshProfileRepository.existsByName(profile)) {
            System.out.print("Profile [" + profile + "]" + " doesn't exist. ");
            System.out.println("[" + String.join(", ", SshProfileRepository.getAllProfileNames()) + "]");
            return CommandLine.ExitCode.USAGE;
        }
        return startProfile(SshProfileRepository.findByName(profile));
    }

    private Integer startProfile(SshProfile sshProfile) {

        if (isProfileRunning(this.profile)) {
            System.out.println("Profile [" + this.profile + "] is already running.");
            new Stop().stopProfile(this.profile);
        }

        System.out.println("Starting profile [" + this.profile + "].");

        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("ssh", "-f", "-N", "-M", "-S", ConfigItems.RSSH_HOME_STRING + "/" + sshProfile.getProfile(), "-R",
                sshProfile.getRemotePort() + ":" + this.host + ":" + this.localPort, sshProfile.getSshCommand());

        Process start;
        try {
            start = processBuilder.start();
            int i = start.waitFor();
            System.out.println("http://" + this.host + ":" + this.localPort + " can now be reached at " + sshProfile.getUrl());

            Files.deleteIfExists(Path.of(ConfigItems.RSSH_HOME_STRING, sshProfile.getProfile() + ConfigItems.RSSH_POSTFIX));
            Path urlFile = Files.createFile(Path.of(ConfigItems.RSSH_HOME_STRING, sshProfile.getProfile() + ConfigItems.RSSH_POSTFIX));
            Files.writeString(urlFile, "http://" + this.host + ":" + this.localPort + " -> " + sshProfile.getUrl(),
                    StandardCharsets.UTF_8, StandardOpenOption.APPEND);

            return i;
        } catch (IOException | InterruptedException e) {
            System.err.println("Something went wrong while starting the ssh tunnel.");
            return CommandLine.ExitCode.SOFTWARE;
        }
    }

    private boolean isProfileRunning(String profile) {
        return Files.exists(Path.of(ConfigItems.RSSH_HOME_STRING, profile));
    }

    private boolean isValidPort(String localPort) {
        int port;
        try {
            port = Integer.parseInt(localPort);
        } catch (NumberFormatException e) {
            return false;
        }

        return port >= 1 && port <= 65535;
    }
}
