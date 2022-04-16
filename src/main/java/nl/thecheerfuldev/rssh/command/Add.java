package nl.thecheerfuldev.rssh.command;

import nl.thecheerfuldev.rssh.entity.SshProfile;
import nl.thecheerfuldev.rssh.service.ProfileService;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

@Command(
        name = "add",
        description = "Adds new profile with the provided details. If the profile that you wish to add already exists, it won't be overridden, unless forced with --force."
                + "%n%n"
                + "The following words are reserved, and can't be used to name profiles: [start, stop, restart, ls, ps, add, rm].",
        header = "Add a new profile with the provided details.",
        footerHeading = " ",
        footer = "")
public class Add implements Callable<Integer> {

    private final List<String> reservedWords = List.of("start", "stop", "restart", "ls", "ps", "add", "rm");

    @Parameters(index = "0", arity = "1", description = "Profile name.")
    String profile;
    @Parameters(index = "1", arity = "1", description = "Remote port (on the target server).")
    String remotePort;
    @Parameters(index = "2", arity = "1", description = "Full url that this profile is reachable on.")
    String url;
    @Parameters(index = "3", arity = "1", description = "Ssh command (or config) to connect to ssh server.")
    String sshCommand;
    @Option(names = {"--force"}, description = "Forces overriding of existing profile.", arity = "0")
    boolean force;
    @Option(names = {"--help"}, arity = "0", description = "Show this help message and exit.", usageHelp = true)
    boolean help;

    @Override
    public Integer call() {
        if (reservedWords.contains(profile)) {
            System.out.println("[" + profile + "] can't be used, as it's a reserved word. Reserved words: [" + String.join(", ", reservedWords) + "]");
            return CommandLine.ExitCode.USAGE;
        }

        if (ProfileService.exists(profile) && !force) {
            System.out.println("Profile [" + profile + "] already exists. Use --force to override existing profile.");
            return CommandLine.ExitCode.USAGE;
        }

        if (ProfileService.isProfileRunning(profile)) {
            System.out.println("Profile [" + profile + "] is running. Overriding will not stop this profile.");
        }

        if (ProfileService.isInvalidPort(remotePort)) {
            System.out.println("Please provide a valid [remotePort]: 1-65535");
            return CommandLine.ExitCode.USAGE;
        }

        try {
            ProfileService.add(new SshProfile(profile, remotePort, url, sshCommand));
            System.out.println("Profile [" + profile + "] has been added.");
        } catch (IOException e) {
            System.out.println("Something went wrong while writing to the profiles.");
            return CommandLine.ExitCode.SOFTWARE;
        }
        return CommandLine.ExitCode.OK;
    }

}
