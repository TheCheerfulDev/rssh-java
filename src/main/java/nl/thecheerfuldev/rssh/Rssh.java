package nl.thecheerfuldev.rssh;

import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.io.IOException;
import java.util.concurrent.Callable;

@Command(
        name = "rssh",
        version = "1.0",
        mixinStandardHelpOptions = true,
        subcommands = {Start.class, Stop.class, Ls.class, Ps.class, Add.class, Rm.class})
public class Rssh implements Callable<Integer> {

    public static void main(String[] args) {
        try {
            ConfigItems.initRssh();
        } catch (IOException e) {
            System.out.println("Error while loading RSSH-JAVA...");
            System.exit(2);
        }
        int exitCode = new CommandLine(new Rssh()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws IOException {

        return CommandLine.ExitCode.USAGE;
    }

    private Integer handleStart() {

        return CommandLine.ExitCode.USAGE;
    }

}
