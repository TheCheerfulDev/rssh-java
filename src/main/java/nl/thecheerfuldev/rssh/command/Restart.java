package nl.thecheerfuldev.rssh.command;

import nl.thecheerfuldev.rssh.entity.RunningProfile;
import nl.thecheerfuldev.rssh.entity.SshProfile;
import nl.thecheerfuldev.rssh.service.ProfileService;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

@Command(name = "restart",
        description = "Restart the provided profile.",
        mixinStandardHelpOptions = true)
public class Restart implements Callable<Integer> {

    @Parameters(index = "0", arity = "0..1", description = "The name of the profile you wish to start.")
    String profile;

    @Override
    public Integer call() throws IOException {

        if (profile == null || profile.isBlank()) {
            System.out.println("profile == null");
            List<String> runningProfiles = ProfileService.getRunningProfiles();
            System.out.println(runningProfiles);
            for (String runningProfile : runningProfiles) {
                restartProfile(runningProfile);

            }
            return CommandLine.ExitCode.OK;
        }

        if (!ProfileService.exists(profile)) {
            System.out.print("Profile [" + profile + "]" + " doesn't exist. ");
            System.out.println("[" + String.join(", ", ProfileService.getAllProfileNames()) + "]");
            return CommandLine.ExitCode.USAGE;
        }

        if (!ProfileService.isProfileRunning(profile)) {
            System.out.println("Profile [" + profile + "]" + " isn't running.");
            return CommandLine.ExitCode.USAGE;
        }

        try {
            return restartProfile(profile);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e.getCause());
        }
    }

    private Integer restartProfile(String profile) throws IOException {
        System.out.println("Restart.restartProfile() -> " + profile);
        RunningProfile runningProfile = ProfileService.getRunningProfile(profile);
        Start start = new Start(runningProfile.profile(), runningProfile.localPort(), runningProfile.host());
        return start.startProfile(new SshProfile(runningProfile.profile(), runningProfile.remotePort(), runningProfile.url(), runningProfile.sshCommand()));
    }

}
