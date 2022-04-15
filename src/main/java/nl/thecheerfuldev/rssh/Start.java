package nl.thecheerfuldev.rssh;

import nl.thecheerfuldev.rssh.entity.SshProfile;
import nl.thecheerfuldev.rssh.service.SshProfileRepository;
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

    @Parameters(index = "0", arity = "0..1", description = "The name of the profile you wish to start.")
    String profile;

    @Parameters(index = "1", arity = "0..1", description = "Number of the port you want to make available.")
    String localPort = "8080";

    @Option(names = {"-h", "--host"}, arity = "0..1", description = "Override the default (localhost) host.")
    String host = "localhost";

    @Override
    public Integer call() {

        if (host == null || host.isBlank()) {
            host = "localhost";
        }

        if (!ProfileUtil.isValidPort(localPort)) {
            System.out.println("Please provide a valid [localPort]: 1-65535");
            return CommandLine.ExitCode.USAGE;
        }

        if (!SshProfileRepository.exists(profile)) {
            System.out.print("Profile [" + profile + "]" + " doesn't exist. ");
            System.out.println("[" + String.join(", ", SshProfileRepository.getAllProfileNames()) + "]");
            return CommandLine.ExitCode.USAGE;
        }
        return startProfile(SshProfileRepository.get(profile));
    }

    private Integer startProfile(SshProfile sshProfile) {

        if (ProfileUtil.isProfileRunning(this.profile)) {
            System.out.print("Profile [" + this.profile + "] is already running. ");
            new Stop().stopProfile(this.profile);
        }

        System.out.println("Starting profile [" + this.profile + "].");

        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("ssh", "-f", "-N", "-M", "-S", ConfigItems.RSSH_HOME_STRING + "/" + sshProfile.profile(), "-R",
                sshProfile.remotePort() + ":" + this.host + ":" + this.localPort, sshProfile.sshCommand());

        Process start;
        try {
            start = processBuilder.start();
            int i = start.waitFor();
            System.out.println("http://" + this.host + ":" + this.localPort + " can now be reached at " + sshProfile.url());

            Files.deleteIfExists(Path.of(ConfigItems.RSSH_HOME_STRING, sshProfile.profile() + ConfigItems.RSSH_POSTFIX));
            Path urlFile = Files.createFile(Path.of(ConfigItems.RSSH_HOME_STRING, sshProfile.profile() + ConfigItems.RSSH_POSTFIX));
            Files.writeString(urlFile, "http://" + this.host + ":" + this.localPort + " -> " + sshProfile.url(),
                    StandardCharsets.UTF_8, StandardOpenOption.APPEND);

            return i;
        } catch (IOException | InterruptedException e) {
            System.err.println("Something went wrong while starting the ssh tunnel.");
            return CommandLine.ExitCode.SOFTWARE;
        }
    }

}
