package nl.thecheerfuldev.rssh;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;

@Command(
        name = "rm",
        description = "Remove the provided profile.",
        mixinStandardHelpOptions = true)
public class Rm implements Callable<Integer> {

    @Parameters(index = "0", arity = "1", description = "Profile that you wish to delete.")
    String profile;

    @Override
    public Integer call() {

        if (!SshProfileRepository.existsByName(profile)) {
            System.out.println("Profile [" + profile + "] doesn't exist.");
            return CommandLine.ExitCode.USAGE;
        }

        if (isProfileRunning(profile)) {
            System.out.println("Profile [" + profile + "] is still running.");
            return CommandLine.ExitCode.USAGE;
        }

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

    private boolean isProfileRunning(String profile) {
        return Files.exists(Path.of(ConfigItems.RSSH_HOME_STRING, profile));
    }
}
