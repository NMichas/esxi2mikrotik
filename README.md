![vCenter](http://images.locanto.net/vmware-online-training/gallery_2521531850.jpg)
![mikrotik](http://www.observium.org/vendor_images/mikrotik.png)

# ESXi2Mikrotik

This is a handy little utility created to speedup the workflow on
my home ESXi/vCenter lab. If you have a similarly operated lab it may
save you some time while testing multiple VMs.

## Prerequisites
* Tested on vCenter 6.5.0
* Tested on Mikrotik 6.39.2
* VM image: Ubuntu 16.04.2 server 64bit

## Usual test-workflow
1. Create a VM in ESXi/vCenter (usually, cloning a template with a
generic image)
2. Find the VM's MAC address on ESXi/vCenter console.
3. Create a DHCP entry in Mikrotik for the new VM
4. Create a DNS entry in Mikrotik for the new VM
5. Modify VM's /etc/hostname
6. Modify VM's /etc/hosts
7. Reboot the VM
8. Check the VM is accessible on the new IP address

This script automates all steps but #1. #1 could be easily automated
too, however providing VM-level configuration options would be far
outside the scope of this script.

## Usage

### Running

Usage instructions:

    java -jar target/esxi2mikrotik-1.0.0-SNAPSHOT.jar

Example:
```
java -jar esxi2mikrotik-1.0.0-SNAPSHOT.jar \
    -eh vcenter.domain \
    -eu admin@vcenter.domain \
    -ep secret \
    -mh mikrotik.domain \
    -mu admin \
    -mp secret2 \
    -vn MyTestVM \
    -vh my-test-vm.domain \
    -vi 1.2.3.4 \
    -vu superuser \
    -vp secret3
```

### Parameters
```
usage: help
 -eh,--esxiHostname <arg>       ESXi/vCenter hostname
 -ep,--esxiPassword <arg>       ESXi/vCenter password
 -eu,--esxiUsername <arg>       ESXi/vCenter username
 -mh,--mikrotikHostname <arg>   Mikrotik hostname
 -mp,--mikrotikPassword <arg>   Mikrotik password
 -mu,--mikrotikUsername <arg>   Mikrotik username
 -vh,--vmHostname <arg>         The hostname to be assigned to the VM
 -vi,--vmIP <arg>               The new IP for the VM
 -vn,--vmName <arg>             The name of the VM in ESXi/vCenter
 -vp,--vmPassword <arg>         The password to connect to the VM via SSH
 -vu,--vmUsername <arg>         The username to connect to the VM via SSH
```



## Development
### Building
To build this script you need Java 1.8.x, Maven 3.2.x, an Internet
connection and running the following command:

    mvn clean install

### Testing while building
```
mvn install exec:java \-Dexec.args="\
    -eh vcenter.domain \
    -eu admin@vcenter.domain \
    -ep secret \
    -mh mikrotik.domain \
    -mu admin \
    -mp secret2 \
    -vn MyTestVM \
    -vh my-test-vm.domain \
    -vi 1.2.3.4 \
    -vu superuser \
    -vp secret3"
```
