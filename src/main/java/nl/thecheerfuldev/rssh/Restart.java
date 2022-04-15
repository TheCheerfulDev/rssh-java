package nl.thecheerfuldev.rssh;

import nl.thecheerfuldev.rssh.entity.RunningProfile;
import nl.thecheerfuldev.rssh.entity.SshProfile;
import nl.thecheerfuldev.rssh.service.ProfileService;
import nl.thecheerfuldev.rssh.service.SshProfileRepository;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.IOException;
import java.util.concurrent.Callable;

@Command(name = "restart",
        description = "Restart the provided profile.",
        mixinStandardHelpOptions = true)
public class Restart implements Callable<Integer> {

    @Parameters(index = "0", arity = "0..1", description = "The name of the profile you wish to start.")
    String profile;

    @Override
    public Integer call() throws IOException {

        if (!SshProfileRepository.exists(profile)) {
            System.out.print("Profile [" + profile + "]" + " doesn't exist. ");
            System.out.println("[" + String.join(", ", SshProfileRepository.getAllProfileNames()) + "]");
            return CommandLine.ExitCode.USAGE;
        }

        if (!ProfileService.isProfileRunning(profile)) {
            System.out.println("Profile [" + profile + "]" + " isn't running.");
            return CommandLine.ExitCode.USAGE;
        }

        RunningProfile runningProfile = ProfileService.getRunningProfile(profile);
        Start start = new Start();
        start.profile = runningProfile.profile();
        start.host = runningProfile.host();
        start.localPort = runningProfile.localPort();

        return start.startProfile(new SshProfile(runningProfile.profile(), runningProfile.remotePort(), runningProfile.url(), runningProfile.sshCommand()));
    }

}
