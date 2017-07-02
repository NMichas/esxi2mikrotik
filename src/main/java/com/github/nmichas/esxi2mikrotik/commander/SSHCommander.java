package com.github.nmichas.esxi2mikrotik.commander;

import com.github.nmichas.esxi2mikrotik.dto.TargetVMDTO;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;

public class SSHCommander {
  private static final Logger LOG = Logger.getLogger(SSHCommander.class);
  private TargetVMDTO targetVMDTO;

  private Session login(String ipAddress) throws JSchException {
    return new JSch().getSession(targetVMDTO.getUsername(), ipAddress, 22);
  }

  private void logout(Session ssh) {
    ssh.disconnect();
  }

  public SSHCommander(TargetVMDTO targetVMDTO) {
    LOG.debug(MessageFormat
        .format("Initialising SSHCommander with credentials: {0}", targetVMDTO.toString()));
    this.targetVMDTO = targetVMDTO;
  }

  public void updateHostname(String ipAddress) throws JSchException, IOException {
    Session ssh = login(ipAddress);
    java.util.Properties config = new java.util.Properties();
    config.put("StrictHostKeyChecking", "no");
    ssh.setConfig(config);
    ssh.setPassword(targetVMDTO.getPassword());
    ssh.connect();

    Channel channel = ssh.openChannel("exec");
    ((ChannelExec)channel).setCommand("sudo -S -p '' sed -i 's/.*/" + targetVMDTO.getHostname() + "/g' /etc/hostname");
    InputStream in=channel.getInputStream();
    OutputStream out=channel.getOutputStream();
    ((ChannelExec)channel).setErrStream(System.err);
    channel.connect();
    out.write((targetVMDTO.getPassword() + "\n").getBytes());
    out.flush();

    //TODO refactor channel read-back...
    byte[] tmp=new byte[1024];
    while(true){
      while(in.available()>0){
        int i=in.read(tmp, 0, 1024);
        if(i<0)break;
        System.out.print(new String(tmp, 0, i));
      }
      if(channel.isClosed()){
        break;
      }
      try{Thread.sleep(1000);}catch(Exception ee){}
    }
    channel.disconnect();

    logout(ssh);
  }

  public void updateHosts(String ipAddress) throws JSchException, IOException {
    Session ssh = login(ipAddress);
    java.util.Properties config = new java.util.Properties();
    config.put("StrictHostKeyChecking", "no");
    ssh.setConfig(config);
    ssh.setPassword(targetVMDTO.getPassword());
    ssh.connect();

    Channel channel = ssh.openChannel("exec");
    ((ChannelExec)channel).setCommand("sudo -S -p '' sed -i 's/127.0.1.1.*/127.0.1.1\\t'\"" + targetVMDTO.getHostname() + "\"'/g' /etc/hosts");
    InputStream in=channel.getInputStream();
    OutputStream out=channel.getOutputStream();
    ((ChannelExec)channel).setErrStream(System.err);
    channel.connect();
    out.write((targetVMDTO.getPassword() + "\n").getBytes());
    out.flush();

    //TODO refactor channel read-back...
    byte[] tmp=new byte[1024];
    while(true){
      while(in.available()>0){
        int i=in.read(tmp, 0, 1024);
        if(i<0)break;
        System.out.print(new String(tmp, 0, i));
      }
      if(channel.isClosed()){
        break;
      }
      try{Thread.sleep(1000);}catch(Exception ee){}
    }
    channel.disconnect();

    logout(ssh);
  }

  public void reboot(String ipAddress) throws JSchException, IOException {
    Session ssh = login(ipAddress);
    java.util.Properties config = new java.util.Properties();
    config.put("StrictHostKeyChecking", "no");
    ssh.setConfig(config);
    ssh.setPassword(targetVMDTO.getPassword());
    ssh.connect();

    Channel channel = ssh.openChannel("exec");
    ((ChannelExec)channel).setCommand("sudo -S -p '' reboot");
    InputStream in=channel.getInputStream();
    OutputStream out=channel.getOutputStream();
    ((ChannelExec)channel).setErrStream(System.err);
    channel.connect();
    out.write((targetVMDTO.getPassword() + "\n").getBytes());
    out.flush();

    //TODO refactor channel read-back...
    byte[] tmp=new byte[1024];
    while(true){
      while(in.available()>0){
        int i=in.read(tmp, 0, 1024);
        if(i<0)break;
        System.out.print(new String(tmp, 0, i));
      }
      if(channel.isClosed()){
        break;
      }
      try{Thread.sleep(1000);}catch(Exception ee){}
    }
    channel.disconnect();

    logout(ssh);
  }


}
