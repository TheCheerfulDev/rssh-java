package nl.thecheerfuldev.rssh;

import nl.thecheerfuldev.rssh.command.Add;
import nl.thecheerfuldev.rssh.command.Ls;
import nl.thecheerfuldev.rssh.command.Ps;
import nl.thecheerfuldev.rssh.command.Restart;
import nl.thecheerfuldev.rssh.command.Rm;
import nl.thecheerfuldev.rssh.command.Start;
import nl.thecheerfuldev.rssh.command.Stop;
import nl.thecheerfuldev.rssh.config.ConfigItems;
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
        version = "rssh - Remote SSH Tunneling 1.0 © 2022 by Mark Hendriks <thecheerfuldev>",
        sortOptions = false,
        parameterListHeading = "%nParameters:%n",
        optionListHeading = "%nOptions:%n",
        commandListHeading = "%nCommands:%n",
        headerHeading = "Usage:%n%n",
        synopsisHeading = "%n",
        descriptionHeading = "%nDescription:%n%n",
        header = "Start the provided profile.",
        footerHeading = "%n",
        footer = "See 'rssh <COMMAND> --help' for information on a specific command.%n",
        description = "Starts a remote ssh tunnel with the presets in the provided profile. When no port is provided, 8080 will be used.",
        scope = CommandLine.ScopeType.INHERIT,
        subcommands = {Start.class, Stop.class, Restart.class, Ls.class, Ps.class, Add.class, Rm.class})
public class Rssh implements Callable<Integer> {

    @Spec
    CommandSpec spec;

    @Parameters(index = "0", arity = "0..1", description = "The name of the profile you wish to start.")
    String profile;

    @Parameters(index = "1", arity = "0..1", description = "Number of the port you want to make available.")
    String localPort = "8080";

    @Option(names = {"--host"}, arity = "0..1", description = "Override the default (localhost) host.")
    String host = "localhost";

    @Option(names = {"--help"}, arity = "0", description = "Show this help message and exit.", usageHelp = true)
    boolean help;

    @Option(names = {"--version"}, arity = "0", description = "Show version information and exit.", versionHelp = true)
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
            return new Start(this.profile, this.localPort, this.host).call();
        }

        spec.commandLine().usage(System.out);
        return CommandLine.ExitCode.OK;
    }

}
