package nl.thecheerfuldev.rssh;

import nl.thecheerfuldev.rssh.entity.RunningProfile;
import nl.thecheerfuldev.rssh.service.ProfileService;
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
            if (ProfileService.isProfileRunning(profile)) {
                isAnyProfileRunning = true;
                try {
                    RunningProfile runningProfile = ProfileService.getRunningProfile(profile);
                    System.out.println("  " + profile + " " + runningProfile.activeString());
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
