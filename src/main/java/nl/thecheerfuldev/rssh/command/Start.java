package nl.thecheerfuldev.rssh.command;

import nl.thecheerfuldev.rssh.config.ConfigItems;
import nl.thecheerfuldev.rssh.entity.SshProfile;
import nl.thecheerfuldev.rssh.service.ProfileService;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.IOException;
import java.util.concurrent.Callable;

@Command(name = "start",
        description = "Start the provided profile.",
        mixinStandardHelpOptions = true)
public class Start implements Callable<Integer> {

    public Start() {
    }

    public Start(String profile, String localPort, String host) {
        this.profile = profile;
        this.localPort = localPort;
        this.host = host;
    }

    @Parameters(index = "0", arity = "0..1", description = "The name of the profile you wish to start.")
    String profile;

    @Parameters(index = "1", arity = "0..1", description = "Number of the port you want to make available.")
    String localPort = "8080";

    @Option(names = {"--host"}, arity = "0..1", description = "Override the default (localhost) host.")
    String host = "localhost";

    @Override
    public Integer call() {

        if (host == null || host.isBlank()) {
            host = "localhost";
        }

        if (ProfileService.isInvalidPort(localPort)) {
            System.out.println("Please provide a valid [localPort]: 1-65535");
            return CommandLine.ExitCode.USAGE;
        }

        if (!ProfileService.exists(profile)) {
            System.out.print("Profile [" + profile + "]" + " doesn't exist. ");
            System.out.println("[" + String.join(", ", ProfileService.getAllProfileNames()) + "]");
            return CommandLine.ExitCode.USAGE;
        }
        return startProfile(ProfileService.getSshProfile(profile));
    }

    public Integer startProfile(SshProfile sshProfile) {

        if (ProfileService.isProfileRunning(this.profile)) {
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

            ProfileService.deleteForProfile(sshProfile.profile());
            if (i != 0) {
                ProfileService.saveRunningProfile(sshProfile, this.host, this.localPort);
                System.out.print("Something went wrong while starting the ssh tunnel: ");
                System.out.println(new String(start.getErrorStream().readAllBytes()));
            }

            return CommandLine.ExitCode.SOFTWARE;
        } catch (IOException | InterruptedException e) {
            System.out.println("Something went wrong while starting the ssh tunnel.");
            return CommandLine.ExitCode.SOFTWARE;
        }
    }

}
