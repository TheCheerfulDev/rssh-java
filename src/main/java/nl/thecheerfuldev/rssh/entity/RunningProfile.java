package nl.thecheerfuldev.rssh.entity;

public record RunningProfile(String profile, String remotePort, String url, String sshCommand, String host, String localPort) {

}
