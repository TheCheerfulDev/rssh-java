package nl.thecheerfuldev.rssh;

import nl.thecheerfuldev.rssh.service.SshProfileRepository;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;

@Command(
        name = "ps",
        description = "List all running profiles.",
        mixinStandardHelpOptions = true)
public class Ps implements Callable<Integer> {

    @Override
    public Integer call() {
        System.out.println("Running profiles:");

        boolean isAnyProfileRunning = false;

        for (String profile : SshProfileRepository.getAllProfileNames()) {
            if (ProfileUtil.isProfileRunning(profile)) {
                isAnyProfileRunning = true;
                try {
                    String urlString =
                            Files.readAllLines(Path.of(ConfigItems.RSSH_HOME_STRING, profile + ConfigItems.RSSH_POSTFIX)).get(0);
                    System.out.println("  " + profile + " " + urlString);
                } catch (IOException e) {
                    return CommandLine.ExitCode.SOFTWARE;
                }
            }
        }

        if (!isAnyProfileRunning) {
            System.out.println("  None.");
        }

        return CommandLine.ExitCode.OK;
    }

}
