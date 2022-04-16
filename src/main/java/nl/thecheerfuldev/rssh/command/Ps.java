package nl.thecheerfuldev.rssh.command;

import nl.thecheerfuldev.rssh.entity.RunningProfile;
import nl.thecheerfuldev.rssh.service.ProfileService;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.util.concurrent.Callable;

@Command(
        name = "ps",
        header = "List all running profiles.",
        description = "Lists all running profiles with the corresponding details.",
        footerHeading = " ",
        footer = "")
public class Ps implements Callable<Integer> {

    @Option(names = {"--help"}, arity = "0", description = "Show this help message and exit.", usageHelp = true)
    boolean help;

    @Override
    public Integer call() {
        System.out.println("Running profiles:");

        boolean isAnyProfileRunning = false;

        for (String profile : ProfileService.getAllProfileNames()) {
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
