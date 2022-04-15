package nl.thecheerfuldev.rssh.entity;

public record RunningProfile(String profile, String remotePort, String url, String sshCommand, String host,
                             String localPort) {

    public RunningProfile(SshProfile sshProfile, String host, String localPort) {
        this(sshProfile.profile(), sshProfile.remotePort(), sshProfile.url(), sshProfile.sshCommand(), host, localPort);
    }

    @Override
    public String toString() {
        return profile + ";" + remotePort + ";" + url + ";" +
                sshCommand + ";" + host + ";" + localPort;
    }

    public String activeString() {
        return "http://" + host + ":" + localPort + " -> " + url;
    }
}
