package nl.thecheerfuldev.rssh;

import nl.thecheerfuldev.rssh.service.SshProfileRepository;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;

@Command(
        name = "ls",
        description = "List all profiles.",
        mixinStandardHelpOptions = true)
public class Ls implements Callable<Integer> {

    @Override
    public Integer call() {
        System.out.println("Profiles:");

        for (String profile : SshProfileRepository.getAllProfileNames()) {
            if (ProfileUtil.isProfileRunning(profile)) {
                try {
                    String urlString =
                            Files.readAllLines(Path.of(ConfigItems.RSSH_HOME_STRING, profile + ConfigItems.RSSH_POSTFIX)).get(0);
                    System.out.println("  " + profile + " " + urlString);
                } catch (IOException e) {
                    return CommandLine.ExitCode.SOFTWARE;
                }
            } else {
                System.out.println("  " + profile);
            }
        }
        return CommandLine.ExitCode.OK;
    }

}
