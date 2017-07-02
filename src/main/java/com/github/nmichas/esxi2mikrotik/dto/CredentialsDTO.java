package com.github.nmichas.esxi2mikrotik.dto;

public class CredentialsDTO {
  private String hostname;
  private String username;
  private String password;

  public CredentialsDTO(String hostname, String username, String password) {
    this.hostname = hostname;
    this.username = username;
    this.password = password;
  }

  public CredentialsDTO(String hostname, String username) {
    this(hostname, username, null);
  }

  public String getHostname() {
    return hostname;
  }

  public void setHostname(String hostname) {
    this.hostname = hostname;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  @Override
  public String toString() {
    return "CredentialsDTO{" +
        "hostname='" + hostname + '\'' +
        ", username='" + username + '\'' +
        ", password='" + password + '\'' +
        '}';
  }
}
