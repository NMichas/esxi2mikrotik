package com.github.nmichas.esxi2mikrotik.commander;

import com.github.nmichas.esxi2mikrotik.dto.CredentialsDTO;
import me.legrange.mikrotik.ApiConnection;
import me.legrange.mikrotik.MikrotikApiException;
import org.apache.log4j.Logger;

import java.text.MessageFormat;

public class MikrotikCommander {

  private static final Logger LOG = Logger.getLogger(MikrotikCommander.class);
  private CredentialsDTO credentials;

  public MikrotikCommander(CredentialsDTO credentials) {
    LOG.debug(MessageFormat
        .format("Initialising MikrotikCommander with credentials: {0}", credentials.toString()));
    this.credentials = credentials;
  }

  public ApiConnection getConnection() throws MikrotikApiException {
    return ApiConnection.connect(credentials.getHostname());
  }

  public void createDHCPEntry(String macAddress, String ipAddress, String comment) throws MikrotikApiException {
    ApiConnection mikrotik = getConnection();
    mikrotik.login(credentials.getUsername(), credentials.getPassword());

    String cmd = "/ip/dhcp-server/lease/add address=" + ipAddress +
        " mac-address=" + macAddress +
        " comment=" + comment;
    LOG.debug(MessageFormat.format("Executing Mikrotik command: {0}.", cmd));
    mikrotik.execute(cmd.toString());

    mikrotik.close();
  }

  public void createDNSEntry(String ipAddress, String hostname, String comment) throws MikrotikApiException {
    ApiConnection mikrotik = getConnection();
    mikrotik.login(credentials.getUsername(), credentials.getPassword());

    String cmd = "/ip/dns/static/add address=" + ipAddress +
        " name=" + hostname +
        " comment=" + comment;
    LOG.info(MessageFormat.format("Executing Mikrotik command: {0}.", cmd));
    mikrotik.execute(cmd.toString());

    mikrotik.close();
  }
}
