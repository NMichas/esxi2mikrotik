package com.github.nmichas.esxi2mikrotik;


import com.github.nmichas.esxi2mikrotik.commander.ESXiCommander;
import com.github.nmichas.esxi2mikrotik.commander.MikrotikCommander;
import com.github.nmichas.esxi2mikrotik.commander.SSHCommander;
import com.github.nmichas.esxi2mikrotik.dto.CredentialsDTO;
import com.github.nmichas.esxi2mikrotik.dto.TargetVMDTO;
import com.jcraft.jsch.JSchException;
import me.legrange.mikrotik.MikrotikApiException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.text.MessageFormat;
import java.time.Instant;

public class App {

  private static final Logger LOG = Logger.getLogger(App.class);

  /**
   * CLI options
   */
  private static Options cliOptions;

  private CredentialsDTO esxiCredentials;
  private CredentialsDTO mikrotikCredentials;
  private TargetVMDTO targetVMDTO;

  static {
    /** Setup CLI options */
    cliOptions = new Options();
    cliOptions.addOption(
        Option.builder("eh").longOpt("esxiHostname").required().hasArg().numberOfArgs(1)
            .desc("ESXi/vCenter hostname").build());
    cliOptions.addOption(
        Option.builder("eu").longOpt("esxiUsername").required().hasArg().numberOfArgs(1)
            .desc("ESXi/vCenter username").build());
    cliOptions.addOption(
        Option.builder("ep").longOpt("esxiPassword").required().hasArg().numberOfArgs(1)
            .desc("ESXi/vCenter password").build());

    cliOptions.addOption(
        Option.builder("mh").longOpt("mikrotikHostname").required().hasArg()
            .numberOfArgs(1)
            .desc("Mikrotik hostname").build());
    cliOptions.addOption(
        Option.builder("mu").longOpt("mikrotikUsername").required().hasArg()
            .numberOfArgs(1)
            .desc("Mikrotik username").build());
    cliOptions.addOption(
        Option.builder("mp").longOpt("mikrotikPassword").required().hasArg()
            .numberOfArgs(1)
            .desc("Mikrotik password").build());

    cliOptions.addOption(
        Option.builder("vn").longOpt("vmName").required().hasArg()
            .numberOfArgs(1)
            .desc("The name of the VM in ESXi/vCenter").build());
    cliOptions.addOption(
        Option.builder("vh").longOpt("vmHostname").required().hasArg()
            .numberOfArgs(1)
            .desc("The hostname to be assigned to the VM").build());
    cliOptions.addOption(
        Option.builder("vi").longOpt("vmIP").required().hasArg()
            .numberOfArgs(1)
            .desc("The new IP for the VM").build());
    cliOptions.addOption(
        Option.builder("vu").longOpt("vmUsername").required().hasArg()
            .numberOfArgs(1)
            .desc("The username to connect to the VM via SSH").build());
    cliOptions.addOption(
        Option.builder("vp").longOpt("vmPassword").required().hasArg()
            .numberOfArgs(1)
            .desc("The password to connect to the VM via SSH").build());

  }

  public App(CredentialsDTO esxiCredentials, CredentialsDTO mikrotikCredentials,
      TargetVMDTO targetVMDTO) {
    this.esxiCredentials = esxiCredentials;
    this.mikrotikCredentials = mikrotikCredentials;
    this.targetVMDTO = targetVMDTO;
  }

  private static void cliHelp() {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp("help", cliOptions);
  }

  public void process()
      throws IOException, MikrotikApiException, JSchException, InterruptedException {
    /** Create commander instances of underlying resources */
    ESXiCommander esxiCommander = new ESXiCommander(esxiCredentials);
    MikrotikCommander mikrotikCommander = new MikrotikCommander(mikrotikCredentials);
    SSHCommander sshCommander = new SSHCommander(targetVMDTO);

    /** Find the MAC address of the VM */
    LOG.info("Finding the MAC address of the VM.");
    String macAddress = esxiCommander.getMACAddress(targetVMDTO.getName());

    /** Find the current IP address of the VM */
    LOG.info("Finding the current IP address of the VM.");
    String currentIPAddress = esxiCommander.getIpAddress(targetVMDTO.getName());

    /** Create a DHCP entry */
    LOG.info(MessageFormat.format("Creating a DHCP entry with IP {0} for MAC address {1}.",
        new Object[]{targetVMDTO.getNewIp(), macAddress}));
    mikrotikCommander.createDHCPEntry(macAddress, targetVMDTO.getNewIp(), targetVMDTO.getName());

    /** Create a DNS entry */
    LOG.info(MessageFormat.format("Creating a DNS entry with IP {0} for hostname {1}.",
        new Object[]{targetVMDTO.getNewIp(), targetVMDTO.getHostname()}));
    mikrotikCommander.createDNSEntry(targetVMDTO.getNewIp(), targetVMDTO.getHostname(), targetVMDTO.getName());

    /** Update the linux VM */
    LOG.info("Updating /etc/hostname");
    sshCommander.updateHostname(currentIPAddress);
    LOG.info("Updating /etc/hosts");
    sshCommander.updateHosts(currentIPAddress);

    /** Reboot the VM to pickup new IP address from DHCP */
    LOG.info(MessageFormat.format("Rebooting {0}.", targetVMDTO.getHostname()));
    sshCommander.reboot(currentIPAddress);

    /** Wait for the VM to be back alive with its new ip address */
    LOG.info(MessageFormat.format("Waiting for the VM to be back online with is new IP adddress {0}.",
        targetVMDTO.getNewIp()));
    InetAddress inetAddress = InetAddress.getByName(targetVMDTO.getNewIp());
    long startTimer = Instant.now().toEpochMilli();
    while (Instant.now().toEpochMilli() - startTimer < 60*1000) {
      try {
        if (inetAddress.isReachable(1000)) {
          break;
        }
      } catch (Exception e) {

      }
      Thread.sleep(1000);
    }
  }

  public static void main(String[] args) throws Exception {
    /** Show help if no arguments passed */
    if (args.length == 0) {
      cliHelp();
      System.exit(0);
    }

    /** Parse cli and create App instance */
    CommandLine cmd = new DefaultParser().parse(cliOptions, args);

    App app = new App(
        new CredentialsDTO(cmd.getOptionValue("eh"), cmd.getOptionValue("eu"),
            cmd.getOptionValue("ep")),
        new CredentialsDTO(cmd.getOptionValue("mh"), cmd.getOptionValue("mu"),
            cmd.getOptionValue("mp")),
        new TargetVMDTO(cmd.getOptionValue("vh"), cmd.getOptionValue("vu"),
            cmd.getOptionValue("vp"), cmd.getOptionValue("vn"), cmd.getOptionValue("vi")));

    /** Start processing */
    app.process();

    LOG.info("ESXi2Mikrotik terminated.");
  }
}
