package nl.thecheerfuldev.rssh;

import nl.thecheerfuldev.rssh.service.SshProfileRepository;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.IOException;
import java.util.concurrent.Callable;

@Command(
        name = "rm",
        description = "Remove the provided profile.",
        mixinStandardHelpOptions = true)
public class Rm implements Callable<Integer> {

    @Parameters(index = "0", arity = "1", description = "Profile that you wish to delete.")
    String profile;

    @Option(names = {"--force"}, description = "Forces removal of profile, even when running, stopping it in the process.", arity = "0")
    boolean force;

    @Override
    public Integer call() {

        if (!SshProfileRepository.exists(profile)) {
            System.out.println("Profile [" + profile + "] doesn't exist.");
            return CommandLine.ExitCode.USAGE;
        }

        if (ProfileUtil.isProfileRunning(profile) && !force) {
            System.out.print("Profile [" + profile + "] is still running. ");
            System.out.println(" Use --force to remove running profile.");
            System.out.println("This will stop the running profile in the process.");
            return CommandLine.ExitCode.USAGE;
        }

        new Stop().stopProfile(profile);

        SshProfileRepository.remove(profile);
        try {
            SshProfileRepository.writeToDisk();
            System.out.println("Profile [" + profile + "] has been removed.");
        } catch (IOException e) {
            System.out.println("Something went wrong while deleting the provided profile.");
            return CommandLine.ExitCode.SOFTWARE;
        }

        return CommandLine.ExitCode.OK;
    }

}
