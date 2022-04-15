package nl.thecheerfuldev.rssh;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.Callable;

@Command(
        name = "rssh",
        version = "1.0",
        mixinStandardHelpOptions = true,
        sortOptions = false,
        subcommands = {Start.class, Stop.class, Ls.class, Ps.class, Add.class, Rm.class, Restart.class})
public class Rssh implements Callable<Integer> {

    @Spec
    CommandSpec spec;

    @Parameters(index = "0", arity = "0..1", description = "The name of the profile you wish to start.")
    String profile;

    @Parameters(index = "1", arity = "0..1", description = "Number of the port you want to make available.")
    String localPort = "8080";

    @Option(names = {"--host"}, arity = "0..1", description = "Override the default (localhost) host.")
    String host = "localhost";

    @Option(names = {"-h", "--help"}, arity = "0", description = "Show this help message and exit.", order = 98)
    boolean help;

    @Option(names = {"-V", "--version"}, arity = "0", description = "Show version information and exit.", order = 99)
    boolean version;

    @Option(names = {"--reset"}, arity = "1", hidden = true)
    boolean reset;


    public static void main(String[] args) {
        try {
            ConfigItems.initRssh();
        } catch (IOException e) {
            System.out.println("Error while loading rssh...");
            System.exit(2);
        }
        int exitCode = new CommandLine(new Rssh()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws IOException {

        if (reset) {
            Files.deleteIfExists(ConfigItems.RSSH_PROFILES_PATH);
            ConfigItems.initRssh();
            System.out.println("Profiles have been reset to their defaults.");
            return CommandLine.ExitCode.OK;
        }

        if (profile != null && !profile.isBlank()) {
            Start start = new Start();
            start.profile = profile;
            start.localPort = localPort;
            start.host = host;
            return start.call();
        }

        spec.commandLine().usage(System.err);
        return CommandLine.ExitCode.USAGE;
    }

}
