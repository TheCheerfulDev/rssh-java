package nl.thecheerfuldev.rssh.command;

import nl.thecheerfuldev.rssh.service.ProfileService;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.IOException;
import java.util.concurrent.Callable;

@Command(
        name = "rm",
        header = "Remove the provided profile.",
        description = "Removes the provided profile. If the profile that you wish to remove is still running, it won't remove the profile, unless forced with --force.",
        footerHeading = " ",
        footer = "",
        mixinStandardHelpOptions = true,
        usageHelpAutoWidth = true)
public class Rm implements Callable<Integer> {

    @Parameters(index = "0", arity = "1", description = "Profile that you wish to remove.")
    String profile;
    @Option(names = {"--force"}, description = "Forces removal of profile, even when running, stopping it in the process.", arity = "0")
    boolean force;
    @Option(names = {"--help"}, arity = "0", description = "Show this help message and exit.", usageHelp = true)
    boolean help;

    @Override
    public Integer call() {
        if (!ProfileService.exists(profile)) {
            System.out.println("Profile [" + profile + "] doesn't exist.");
            return CommandLine.ExitCode.USAGE;
        }

        if (ProfileService.isProfileRunning(profile) && !force) {
            System.out.print("Profile [" + profile + "] is still running. ");
            System.out.println(" Use --force to remove running profile.");
            System.out.println("This will stop the running profile in the process.");
            return CommandLine.ExitCode.USAGE;
        }

        new Stop().stopProfile(profile);

        try {
            ProfileService.remove(profile);
            System.out.println("Profile [" + profile + "] has been removed.");
        } catch (IOException e) {
            System.out.println("Something went wrong while deleting the provided profile.");
            return CommandLine.ExitCode.SOFTWARE;
        }

        return CommandLine.ExitCode.OK;
    }

}
