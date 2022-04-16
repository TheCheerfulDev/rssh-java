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
        header = "Start the provided profile.",
        description = "Starts a remote ssh tunnel with the presets in the provided profile. When no port is provided, it will default to port 8080.",
        footerHeading = " ",
        footer = "")
public class Start implements Callable<Integer> {

    public Start() {
    }

    public Start(String profile, String localPort, String host) {
        this.profile = profile;
        this.localPort = localPort;
        this.host = host;
    }

    @Parameters(index = "0", arity = "1", description = "The name of the profile you wish to start.")
    String profile;
    @Parameters(index = "1", arity = "0..1", description = "Port you want to make available. Defaults to port 8080 if none is provided.")
    String localPort = "8080";
    @Option(names = {"--host"}, arity = "0..1", description = "Override the default (localhost) host.")
    String host = "localhost";

    @Option(names = {"--help"}, arity = "0", description = "Show this help message and exit.", usageHelp = true)
    boolean help;

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
            handleStop(this.profile);
        }
        System.out.println("Starting profile [" + this.profile + "].");
        return handleStart(sshProfile);
    }

    private void handleStop(String profile) {
        new Stop().handleStop(profile);
    }

    public int handleStart(SshProfile sshProfile) {
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
                System.out.print("Something went wrong while starting the ssh tunnel: ");
                System.out.println(new String(start.getErrorStream().readAllBytes()));
                return i;
            }

            ProfileService.saveRunningProfile(sshProfile, this.host, this.localPort);
            return i;
        } catch (IOException | InterruptedException e) {
            System.out.println("Something went wrong while starting the ssh tunnel.");
            return CommandLine.ExitCode.SOFTWARE;
        }
    }

}
