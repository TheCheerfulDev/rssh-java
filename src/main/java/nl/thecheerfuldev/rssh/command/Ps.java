package nl.thecheerfuldev.rssh.command;

import nl.thecheerfuldev.rssh.entity.RunningProfile;
import nl.thecheerfuldev.rssh.service.ProfileService;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

@Command(
        name = "ps",
        header = "List all running profiles.",
        description = "Lists all running profiles with the corresponding details. There will be no output if there are no running profiles.",
        footerHeading = " ",
        footer = "")
public class Ps implements Callable<Integer> {

    @Option(names = {"--help"}, arity = "0", description = "Show this help message and exit.", usageHelp = true)
    boolean help;

    @Override
    public Integer call() {

        List<String> runningProfiles = new ArrayList<>();

        for (String profile : ProfileService.getAllProfileNames()) {
            if (ProfileService.isProfileRunning(profile)) {
                try {
                    RunningProfile runningProfile = ProfileService.getRunningProfile(profile);
                    runningProfiles.add("  " + profile + " " + runningProfile.activeString());
                } catch (IOException e) {
                    return CommandLine.ExitCode.SOFTWARE;
                }
            }
        }

        if (!runningProfiles.isEmpty()) {
            System.out.println("Running profiles:");
            runningProfiles.forEach(System.out::println);
        }

        return CommandLine.ExitCode.OK;
    }
}
