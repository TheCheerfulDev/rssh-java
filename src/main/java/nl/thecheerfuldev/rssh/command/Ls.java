package nl.thecheerfuldev.rssh.command;

import nl.thecheerfuldev.rssh.entity.RunningProfile;
import nl.thecheerfuldev.rssh.service.ProfileService;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.util.concurrent.Callable;

@Command(
        name = "ls",
        header = "List all profiles.",
        description = "Lists all profiles. If a profile is running, it will print the details.",
        footerHeading = " ",
        footer = "")
public class Ls implements Callable<Integer> {

    @Option(names = {"--help"}, arity = "0", description = "Show this help message and exit.", usageHelp = true)
    boolean help;

    @Override
    public Integer call() {
        System.out.println("Profiles:");

        for (String profile : ProfileService.getAllProfileNames()) {
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

