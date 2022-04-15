package nl.thecheerfuldev.rssh;

import nl.thecheerfuldev.rssh.entity.RunningProfile;
import nl.thecheerfuldev.rssh.service.ProfileService;
import nl.thecheerfuldev.rssh.service.SshProfileRepository;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.io.IOException;
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
            if (ProfileService.isProfileRunning(profile)) {
                try {
                    RunningProfile runningProfile = ProfileService.getRunningProfile(profile);
                    System.out.println("  " + profile + " " + runningProfile.activeString());
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
