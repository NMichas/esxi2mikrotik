package com.github.nmichas.esxi2mikrotik.commander;

import com.github.nmichas.esxi2mikrotik.dto.CredentialsDTO;
import com.vmware.vim25.GuestNicInfo;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.VirtualMachine;
import org.apache.log4j.Logger;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.text.MessageFormat;

public class ESXiCommander {

  private static final Logger LOG = Logger.getLogger(MikrotikCommander.class);
  private CredentialsDTO credentials;

  public ESXiCommander(CredentialsDTO credentials) {
    LOG.debug(MessageFormat
        .format("Initialising ESXiCommander with credentials: {0}", credentials.toString()));
    this.credentials = credentials;
  }

  private ServiceInstance login() throws MalformedURLException, RemoteException {
    return new ServiceInstance(new URL("https://" + credentials.getHostname() + "/sdk"),
        credentials.getUsername(), credentials.getPassword(), true);
  }

  private void logout(ServiceInstance esxi) {
    esxi.getServerConnection().logout();
  }

  private VirtualMachine getVM(ServiceInstance esxi, String vmName) throws RemoteException {
    return (VirtualMachine) new InventoryNavigator(esxi.getRootFolder())
        .searchManagedEntity("VirtualMachine", vmName);
  }

  public String getMACAddress(String vmName) throws MalformedURLException, RemoteException {
    /** Login to ESXi/vCenter. */
    ServiceInstance esxi = login();

    /** Get the VM */
    final VirtualMachine vm = getVM(esxi, vmName);

    /** Find network info. Currently, only first NIC/IPv4 is scanned/supported. */
    final GuestNicInfo net = vm.getGuest().getNet()[0];

    /** Logout from ESXi/vCenter. */
    logout(esxi);

    return net.getMacAddress();
  }

  public String getIpAddress(String vmName) throws MalformedURLException, RemoteException {
    /** Login to ESXi/vCenter. */
    ServiceInstance esxi = login();

    /** Get the VM */
    final VirtualMachine vm = getVM(esxi, vmName);

    /** Find network info. Currently, only first NIC/IPv4 is scanned/supported. */
    final GuestNicInfo net = vm.getGuest().getNet()[0];

    /** Logout from ESXi/vCenter. */
    logout(esxi);

    return net.getIpAddress()[0];
  }

}
