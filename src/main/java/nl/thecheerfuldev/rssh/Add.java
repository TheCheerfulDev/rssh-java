package nl.thecheerfuldev.rssh;

import nl.thecheerfuldev.rssh.entity.SshProfile;
import nl.thecheerfuldev.rssh.service.SshProfileRepository;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.IOException;
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

    @Option(names = {"--force"}, description = "Forces overriding of existing profile.", arity = "0")
    boolean force;

    @Override
    public Integer call() {

        if (SshProfileRepository.exists(profile) && !force) {
            System.out.println("Profile [" + profile + "] already exists. Use --force to override existing profile.");
            return CommandLine.ExitCode.USAGE;
        }

        if (ProfileUtil.isProfileRunning(profile)) {
            System.out.println("Profile [" + profile + "] is running. Overriding will not stop this profile.");
        }

        if (!ProfileUtil.isValidPort(remotePort)) {
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

}
