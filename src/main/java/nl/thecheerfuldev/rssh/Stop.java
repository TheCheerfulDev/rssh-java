package nl.thecheerfuldev.rssh;

import nl.thecheerfuldev.rssh.service.SshProfileRepository;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;

@Command(
        name = "stop",
        description = "Stop the given profile.",
        mixinStandardHelpOptions = true)
public class Stop implements Callable<Integer> {

    @Parameters(index = "0", arity = "0..1")
    String profile;

    @Override
    public Integer call() {
        if (profile == null || profile.isBlank()) {
            SshProfileRepository.getAllProfileNames().forEach(this::stopProfile);
            return CommandLine.ExitCode.OK;
        }

        return stopProfile(this.profile);
    }

    public Integer stopProfile(final String profile) {

        if (!SshProfileRepository.exists(profile)) {
            System.out.println("Profile [" + profile + "] doesn't exist.");
            return CommandLine.ExitCode.USAGE;
        }

        if (!ProfileUtil.isProfileRunning(profile)) {
            return CommandLine.ExitCode.OK;
        }

        System.out.println("Stopping profile [" + profile + "].");

        ProcessBuilder processBuilder = new ProcessBuilder("ssh", "-S", ConfigItems.RSSH_HOME_STRING + "/" + profile, "-O",
                "exit", "mhnas", "2>/dev/null");

        Process start;
        try {
            start = processBuilder.start();
        } catch (IOException e) {
            return CommandLine.ExitCode.SOFTWARE;
        }
        try {
            int i = start.waitFor();
            Files.deleteIfExists(Path.of(ConfigItems.RSSH_HOME_STRING, profile + ConfigItems.RSSH_POSTFIX));
            return i;
        } catch (InterruptedException | IOException e) {
            return CommandLine.ExitCode.SOFTWARE;
        }
    }

}
