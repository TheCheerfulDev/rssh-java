package nl.thecheerfuldev.rssh;

public class SshProfile {

    private String profile;
    private String remotePort;
    private String url;
    private String sshCommand;

    public SshProfile(String profile, String remotePort, String url, String sshCommand) {
        this.profile = profile;
        this.remotePort = remotePort;
        this.url = url;
        this.sshCommand = sshCommand;
    }

    public String getProfile() {
        return profile;
    }

    public String getRemotePort() {
        return remotePort;
    }

    public String getUrl() {
        return url;
    }

    public String getSshCommand() {
        return sshCommand;
    }

    @Override
    public String toString() {
        return "SshProfile{" +
                "profile='" + profile + '\'' +
                ", remotePort='" + remotePort + '\'' +
                ", url='" + url + '\'' +
                ", sshCommand='" + sshCommand + '\'' +
                '}';
    }
}
