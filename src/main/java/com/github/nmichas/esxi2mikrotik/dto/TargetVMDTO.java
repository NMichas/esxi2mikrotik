package com.github.nmichas.esxi2mikrotik.dto;

public class TargetVMDTO extends CredentialsDTO {

  /**
   * The name of the VM in ESXi/vCenter
   */
  private String name;

  /**
   * The new IP for this VM.
   */
  private String newIp;

  public TargetVMDTO(String hostname, String username, String password, String name, String newIp) {
    super(hostname, username, password);
    this.name = name;
    this.newIp = newIp;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getNewIp() {
    return newIp;
  }

  public void setNewIp(String newIp) {
    this.newIp = newIp;
  }

}
