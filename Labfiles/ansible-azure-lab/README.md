# Table of Contents

[Objectives and initial setup](#objectives)

[Introduction to Ansible](#intro)

[Lab 1: Create Control VM using Azure CLI](#lab1)

[Lab 2: Create Service Principal](#lab2)

[Lab 3: Install Ansible in the provisioning VM](#lab3)

[Lab 4: Ansible dynamic inventory for Azure](#lab4)

[Lab 5: Creating a VM using an Ansible Playbook](#lab5)

[Lab 6: Running an Ansible playbook on the new VM](#lab6)

[Lab 7: Running Ansible playbooks periodically for Azure configuration management](#lab7)

[Lab 8: Using Ansible with ARM templates for Azure configuration management](#lab8)

[Lab 9: Deleting a VM using Ansible - Optional](#lab9)

[End the lab](#end)

[Conclusion](#conclusion)

[References](#ref)


# Objectives and initial setup <a name="objectives"></a>

This document contains a lab guide that helps to deploy a basic environment in Azure that allows to test some of the functionality of the integration between Azure and Ansible.

Before starting with this account, make sure to fulfill all the requisites:

- A valid Azure subscription account. If you don&#39;t have one, you can create your [free azure account](https://azure.microsoft.com/en-us/free/) (https://azure.microsoft.com/en-us/free/) today.
- If you are using Windows 10, you can [install Bash shell on Ubuntu on Windows](http://www.windowscentral.com/how-install-bash-shell-command-line-windows-10) ( [http://www.windowscentral.com/how-install-bash-shell-command-line-windows-10](http://www.windowscentral.com/how-install-bash-shell-command-line-windows-10)).
- Install Azure CLI, please see here for instructions: [https://docs.microsoft.com/en-us/cli/azure/install-azure-cli](https://docs.microsoft.com/en-us/cli/azure/install-azure-cli) 

This lab will cover:

- Introduction to Ansible installation and first steps
- Example of playbooks to interact with Azure in order to create and delete VMs
- Example of playbooks to interact with Azure Linux VMs in order to modify them installing additional software packages or downloading files from external repositories
- Using Ansible&#39;s dynamic inventory information so that VM names to be controlled by Ansible do not need to be statically defined, but are dynamically retrieved from Azure

Along this lab some variables will be used, that might (and probably should) look different in your environment. This is the variables you need to decide on before starting with the lab. Notice that the VM names are prefixed by a (not so) random number, since these names will be used to create DNS entries as well, and DNS names need to be unique.

| **Description** | **Value used in this lab guide** |
| --- | --- |
| Azure resource group | ansiblelab |
| Name for provisioning VM | 19761013myvm |
| Username for provisioning VM | lab-user |
| Password for provisioning VM | Microsoft123! |
| Name for created VM | your-vm-name |
| Azure region | westeurope |


# Introduction to Ansible <a name="intro"></a>

Ansible is a software that falls into the category of **Configuration Management Tools**. These tools are mainly used in order to describe in a declarative language the configuration that should possess a certain machine (or a group of them) in so called playbooks, and then make sure that those machines are configured accordingly.

Playbooks are structured using YAML (Yet Another Markup Language) and support the use of variables, as we will see along the labs.

As opposed to other Configuration Management Tools like Puppet or Chef, Ansible is **agent-less**, which means that it does not require the installation of any software in the managed machines. Ansible uses **SSH** to manage Linux machines, and **remote Powershell** to manage Windows systems.

In order to interact with machines other than Linux servers (for example, with the Azure portal in order to create VMs), Ansible supports extensions called **modules**. Ansible is completely written in Python, and these modules are equally Python libraries. In order to support Azure, Ansible needs the Azure Python SDK.

Additionally, Ansible requires that the managed hosts are documented in a **host inventory**. Alternatively, Ansible supports **dynamic inventories** for some systems, including Azure, so that the host inventory is dynamically generated at runtime.

![Architecture Image](https://github.com/erjosito/ansible-azure-lab/blob/master/ansible_arch.png "Ansible Architecture Example")

**Figure**: Ansible architecture example to configure web servers and databases


# Lab 1: Create Control VM using Azure CLI <a name="lab1"></a>


**Step 1.** Log into your system. If you are using the Learn On Demand lab environment, the user for the Centos VM is lab-user, with the password Microsoft123!

**Step 2.** If you don’t have a valid Azure subscription, but have received a voucher code for Azure, go to https://www.microsoftazurepass.com/Home/HowTo for instructions about how to redeem it.  

**Step 3.** Open a terminal window. In Windows, for example by hitting the Windows key in your keyboard, typing &#39;cmd&#39; (without the quotes) and hitting the Enter key. You might want to maximize the command Window so that it fills your desktop.

**Step 4.** Update the existing Azure CLI to the latest version. Note that this will only work in certain Linux distributions. If you are using the VM provided by Learn On Demand Systems, you can (and you should) issue the following command to update the Azure CLI. For other distros or for Windows please refer to the Azure CLI 2.0 documentation.
```
az component update
```

**Step 5.** Login to Azure in your terminal window.

<pre lang="...">
<b>az login</b>
To sign in, use a web browser to open the page https://aka.ms/devicelogin and enter the code XXXXXXXXX to authenticate.
</pre>

The &#39;az login&#39; command will provide you a code, that you need to introduce (over copy and paste) in the web page http://aka.ms/devicelogin. Open an Internet browser (Firefox is preinstalled int the VM provided by Learn on Demand Systems), go to this URL, and after introducing the code, you will need to authenticate with credentials that are associated to a valid Azure subscription. After a successful login, you can enter the following two commands back in the terminal window in order to create a new resource group, and to set the default resource group accordingly.

**Step 6.** Create a resource group, define it as the default group for further commands, create a Vnet and a subnet, and a Linux machine in that subnet with a public IP address. Here the commands you need for these tasks:

```
az group create --name ansiblelab --location westeurope
```

```
az configure --defaults group=ansiblelab
```
**Note:** the previous command set the default resource group to &#39;ansiblelab&#39;, so that in the next commands the resource group does not need to be explicitly identified with the option -g.

```
az network vnet create -n ansibleVnet --address-prefixes 192.168.0.0/16 --subnet-name ansibleSubnet --subnet-prefix 192.168.1.0/24
```

```
az network public-ip create --name masterPip
```

<pre lang="...">
<b>az vm create -n ansibleMaster --image OpenLogic:CentOS:7.3:latest --vnet-name ansibleVnet --subnet ansibleSubnet --public-ip-address masterPip --authentication-type password --admin-username lab-user --admin-password Microsoft123!</b>
{
  "fqdns": "",
  "id": "/subscriptions/3e78e84b-6750-44b9-9d57-d9bba935237a/resourceGroups/ansiblelab/providers/Microsoft.Compute/virtualMachines/ansibleMaster",
  "location": "westeurope",
  "macAddress": "00-0D-3A-24-E2-C0",
  "powerState": "VM running",
  "privateIpAddress": "192.168.1.4",
  "publicIpAddress": "1.2.3.4",
  "resourceGroup": "ansiblelab"
}
</pre>

**Note:** while this command is running (might take between 10 and 15 minutes), you might wait until it finishes, or in the meantime you can temporarily jump to Lab 2 (Create Service Principal) in a new terminal window. When you are finished with Lab 2 you can come back to this point to finish Lab 1.

**Step 7.** The previous command might take 10-15 minutes to run. After you get again the command prompt, connect over SSH to the new VM, using the public IP address displayed in the output of the previous command, and username and password provided in the previous command (lab-user / Microsoft123!). Please **replace 1.2.3.4** with the actual public IP address retrieved out of the last command in Step 2

<pre lang="...">
<b>ssh lab-user@1.2.3.4</b>
The authenticity of host '1.2.3.4 (1.2.3.4)' can't be established.
ECDSA key fingerprint is 09:7f:7e:fc:34:d9:9f:ff:a6:5c:de:50:5a:5a:4f:14.
Are you sure you want to continue connecting (yes/no)? yes
Warning: Permanently added '1.2.3.4' (ECDSA) to the list of known hosts.
Password:
[lab-user@ansibleMaster ~]$
</pre>

**Step 8.** Install Azure CLI 2.0 in the provisioning machine &#39;ansibleMaster&#39;:

```
sudo yum update -y
```

```
sudo yum install -y gcc libffi-devel python-devel openssl-devel
```

```
curl -L https://aka.ms/InstallAzureCli | bash
```

**Note:** you can just press Enter to accept the default answer when the installation program asks you a question. In the last question, make sure to tell the script to update the PATH variable (answer 'Y').


## What we have learnt

You can use the Azure CLI from any platform (including Linux) to manage Azure. In this section you used basic Azure CLI commands to create virtual networks and a CentOS virtual machine.



# Lab 2: Create Service Principal <a name="lab2"></a>

This step is required so that Ansible can log in to Azure with non-interactive authentication. We will define a service principal and an application ID, and we will give permissions to the service principal to operate on the resource group that we created in Lab 1.

As best practice, you should give the minimum permissions required to your service principals. 

See for more information: [https://docs.microsoft.com/en-us/azure/azure-resource-manager/resource-group-authenticate-service-principal-cli](https://docs.microsoft.com/en-us/azure/azure-resource-manager/resource-group-authenticate-service-principal-cli))


**Step 1.** Create Active Directory application for Ansible:

<pre lang="...">
<b>az ad app create --password ThisIsTheAppPassword --display-name ansibleApp --homepage ansible.mydomain.com --identifier-uris ansible.mydomain.com</b>
{
  "appId": "11111111-1111-1111-1111-111111111111",
  "appPermissions": null,
  "availableToOtherTenants": false,
  "displayName": "ansibleApp",
  "homepage": "ansible.mydomain.com",
  "identifierUris": [
    "ansible.mydomain.com"
  ],
  "objectId": "55555555-5555-5555-5555-555555555555",
  "objectType": "Application",
  "replyUrls": []
}
</pre>

**Step 2.** Create Service Principal associated to that application:

<pre lang="...">
<b>az ad sp create --id 11111111-1111-1111-1111-111111111111</b>
{
  "appId": "11111111-1111-1111-1111-111111111111",
  "displayName": "ansibleApp",
  "objectId": "44444444-4444-4444-4444-444444444444",
  "objectType": "ServicePrincipal",
  "servicePrincipalNames": [
    "11111111-1111-1111-1111-111111111111",
    "ansible.mydomain.com"
  ]
}
</pre>

**Step 3.** Find out your subscription and tenant IDs:

<pre lang="...">
<b>az account show</b>
{
  "environmentName": "AzureCloud",
  "id": "22222222-2222-2222-2222-222222222222",
  "isDefault": true,
  "name": "Your Subscription Name",
  "state": "Enabled",
  "tenantId": "33333333-3333-3333-3333-333333333333",
  "user": {
    "name": "your.name@microsoft.com",
    "type": "user"
  }
}
</pre>

**Step 4.**	Assign the Contributor role to the principal for our resource group (remember we have specified the default resource group in Lab 1, so we do not need to specify it again), using the object ID for the service principal:

<pre lang="...">
<b>az role assignment create --assignee 44444444-4444-4444-4444-444444444444 --role contributor</b>
{
  "id": "/subscriptions/22222222-2222-2222-2222-222222222222/resourceGroups/ansiblelab/providers/Microsoft.Authorization/roleAssignments/66666666-6666-6666-6666-666666666666",
  "name": "66666666-6666-6666-6666-666666666666",
  "properties": {
    "principalId": "44444444-4444-4444-4444-444444444444",
    "roleDefinitionId": "/subscriptions/22222222-2222-2222-2222-222222222222/providers/Microsoft.Authorization/roleDefinitions/77777777-7777-7777-7777-777777777777",
    "scope": "/subscriptions/22222222-2222-2222-2222-222222222222/resourceGroups/ansiblelab"
  },
  "resourceGroup": "ansiblelab",
  "type": "Microsoft.Authorization/roleAssignments"
}
</pre>

Note the following values of your output, since we will use them later. In this guide they are marked in different colors for easier identification:

1. Subscription ID: **22222222-2222-2222-2222-222222222222**
2. Tenant ID: **33333333-3333-3333-3333-333333333333**
3. Application ID (also known as Client ID): **11111111-1111-1111-1111-111111111111**
4. Password: **ThisIsTheAppPassword**

## What we have learnt

Username and password is not a good authentication method for automation solutions, since it is interactive. The solution for non-interactive authentication in Azure is called "Service Principal" where an application can authenticate with a pre-defined password, and it gets specific permissions within a certain scope.

As alternative to password authentication for the application, digital certificates can be used, but that is out of the scope of this lab.


# Lab 3: Install Ansible in the provisioning VM <a name="lab3"></a>

At this point we have our master VM running in Azure, and we have configured a service principal for automation. This section will install Ansible and the Azure Python SDK on the master VM that was created in the previous steps.

**Step 1.** Install required software packages
```
sudo yum install -y python-devel openssl-devel git gcc epel-release
```
```
sudo yum install -y ansible python-pip jq
```
```
sudo pip install --upgrade pip
```


**Step 2.** Install Azure Python SDK for Ansible. Additionally, we will install the package DNS Python so that we can do DNS checks in Ansible playbooks (to make sure that DNS names are not taken). Some Python versions require as well the packaging module to be installed separatedly 

```
sudo pip install ansible[azure]
```

```
sudo pip install dnspython packaging
```


**Step 3.** We will clone some Github repositories, such as the ansible source code (which includes the dynamic inventory files such as `azure\_rm.py`), and the repository for this lab.

```
git clone git://github.com/ansible/ansible.git --recursive
```
```
git clone git://github.com/erjosito/ansible-azure-lab
```

**Step 4.** Lastly, you need to create a new file in the directory `~/.azure` (create it if it does not exist), using the credentials generated in the previous sections. The filename is `~/.azure/credentials`.

```
mkdir ~/.azure
```

```
touch ~/.azure/credentials
```

```
cat <<EOF > ~/.azure/credentials
[default]
subscription_id=22222222-2222-2222-2222-222222222222
client_id=11111111-1111-1111-1111-111111111111
secret=ThisIsTheAppPassword
tenant=33333333-3333-3333-3333-333333333333
EOF
```

**Note:** don’t forget to replace the numbers with the actual information you retrieved when you created the service principal


**Step 5.** And lastly, we will create a pair of private/public keys, and install the public key in the local machine, to test the correct operation of Ansible.

<pre lang="...">
<b>ssh-keygen -t rsa</b>
Generating public/private rsa key pair.
Enter file in which to save the key (/home/lab-user/.ssh/id_rsa):
Created directory '/home/lab-user/.ssh'.
Enter passphrase (empty for no passphrase):
Enter same passphrase again:
Your identification has been saved in /home/lab-user/.ssh/id_rsa.
Your public key has been saved in /home/lab-user/.ssh/id_rsa.pub.
The key fingerprint is:
81:86:f7:9c:6b:34:3a:5a:b2:d9:49:c4:8b:36:19:3b lab-user@ansibleMaster
The key's randomart image is:
+--[ RSA 2048]----+
|                 |
|     . .         |
|    . + .        |
|     + o o       |
|    . o S        |
|     * + o       |
|    E * o        |
|   . @ +         |
|    + o          |
+-----------------+
</pre>

```
chmod 755 ~/.ssh
```

```
touch ~/.ssh/authorized_keys; chmod 644 ~/.ssh/authorized_keys
```

```
ssh-copy-id lab-user@127.0.0.1
```

You can verify that when trying to ssh to the local machine, no password will be requested:

<pre lang="...">
[lab-user@ansibleMaster ~]$ <b>ssh 127.0.0.1</b>
Last login: Tue Jun  6 20:39:03 2017 from mymachine.mydomain.com
[lab-user@ansibleMaster ~]$
</pre>

## What we have learnt

Ansible can be installed on an Azure VM exactly the same as in other Linux systems



# Lab 4: Ansible dynamic inventory for Azure <a name="lab4"></a>

Ansible allows to execute operations in machines that can be defined in a static inventory in the machine where Ansible runs. But what if you would like to run Ansible in all the machines in a resource group, but you don&#39;t know whether it is one or one hundred? This is where dynamic inventories come into place, they discover the machines that fulfill certain requirements (such as existing in Azure, or belonging to a certain resource group), and makes Ansible execute operations on them.

**Step 1.** In this first step we will test that the dynamic inventory script is running, executing it with the parameter &#39;--list&#39;. This should show JSON text containing information about all the VMs in your subscription.

<pre lang="...">
<b>python ./ansible/contrib/inventory/azure_rm.py --list | jq</b>
{
  "azure": [
    "ansibleMaster"
  ],
  "westeurope": [
    "ansibleMaster"
  ],
  "ansibleMasterNSG": [
    "ansibleMaster"
  ],
  "ansiblelab": [
    "ansibleMaster"
  ],
  "_meta": {
    "hostvars": {
      "ansibleMaster": {
        "powerstate": "running",
        "resource_group": "ansiblelab",
        "tags": {},
        "image": {
          "sku": "7.3",
          "publisher": "OpenLogic",
          "version": "latest",
          "offer": "CentOS"
        },
        "public_ip_alloc_method": "Dynamic",
        "os_disk": {
          "operating_system_type": "Linux",
          "name": "osdisk_vD2UtEJhpV"
        },
        "provisioning_state": "Succeeded",
        "public_ip": "52.174.19.210",
        "public_ip_name": "masterPip",
        "private_ip": "192.168.1.4",
        "computer_name": "ansibleMaster",
        ...
      }
    }
  }
}
</pre>

**Note:** &#39;jq&#39; is a command-line JSON interpreter, that you can use here to make the JSON output readable. Try to use the previous command without the ` | jq` part and see the effect.



**Step 2.** Now we can test Ansible functionality. But we will not change anything on the target machines, just test reachability with the Ansible function `ping`.

<pre lang="...">
<b>ansible -i ./ansible/contrib/inventory/azure_rm.py all -m ping</b>
The authenticity of host '1.2.3.4 (1.2.3.4)' can't be established.
ECDSA key fingerprint is 09:7f:7e:fc:34:d9:9f:ff:a6:5c:de:50:5a:5a:4f:14.
Are you sure you want to continue connecting (yes/no)? yes
ansibleMaster | SUCCESS => {
    "changed": false,
    "ping": "pong"
}

<b>ansible -i ./ansible/contrib/inventory/azure_rm.py all -m ping</b>
ansibleMaster | SUCCESS => {
    "changed": false,
    "ping": "pong"
}
</pre>

**Note:** The first time you run the command you will have to acknowledge the host's authenticity, after that it should run automatically

**Step 3.** Step 3.	If you already had VMs in your Azure subscription, they probably didn't pop up in the previous steps in this lab. The reason is because when we created the service principal, the scope was set to the resource group.

Still, you could further refine the inventory script in order to return only the VMs in a certain resource group or a location. To that purpose, we will modify the .ini file that controls some aspects of `azure\_rm.py`. This .ini file is to be located in the same directory as the Python script: `~/ansible/contrib/inventory/azure\_rm.ini`. You need to find the line that specifies which resource groups are to be inspected, uncomment it and change it to something like this:


```
resource_groups=ansiblelab
```

**Note:** edit the file ~/ansible/contrib/inventory/azure_rm.ini with a text editor such as vi. Note that you can filter not only per resource group, but per location, and more importantly, per tag.


**Step 4.** You can actually do much more with ansible, such as running any command on all the VMs returned by the dynamic inventory script, in this case `/bin/uname -a`

```
ansible -i ~/ansible/contrib/inventory/azure_rm.py all -m shell -a "/bin/uname -a"
```

**Note:** the command `uname -a` returns some information about the machine where it is executed, such as the Kernel version, the date and time and the CPU architecture

## What we have learnt

Dynamic Inventory is an Ansible feature that allows for a certain operation to be executed on a list of VMs which is not defined statically, but is evaluated at execution time. For example, on all VMs in Azure in a certain resource group or a certain location.



# Lab 5: Creating a VM using an Ansible Playbook <a name="lab5"></a>

Now that we have Ansible up and running, we can deploy our first playbook in order to create a VM. This playbook will not be executed using the dynamic inventory function, but on the localhost. This will trigger the necessary calls to Azure so that all required objects are created. We will be using the playbook example that was cloned from the Github repository for this lab in previous sections, which you should have stored in `~/ansible-azure-lab/new_vm_web.yml`.

**Step 1.** You need to change the public SSH key that you will find inside of `~/ansible-azure-lab/new\_vm\_web.yml` with your own key, which you can find using this command:

```
cat ~/.ssh/id_rsa.pub
```

**Note:** edit the file `~/ansible-azure-lab/new_vm_web.yml` with a text editor such as vi from the command line. You can find the SSH string towards the end. It is important that the username matches the user name in your ansibleMaster VM (&#39;lab-user&#39;, if you followed the lab guide)

**Step 2.** You can use the following commands to double check the vnet and subnet that were used to create the master VM. that information (note that the outputs have been truncated so that they fit to the width of this document):

<pre lang="...">
<b>az network vnet list -o table</b>
Location    Name         ProvisioningState    ResourceGroup    ResourceGuid
----------  -----------  -------------------  ---------------  -------------
westeurope  ansibleVnet  Succeeded            ansiblelab       ...
</pre>


<pre lang="...">
<b>az network vnet subnet list --vnet-name ansibleVnet -o table</b>
AddressPrefix    Name           ProvisioningState    ResourceGroup
---------------  -------------  -------------------  ---------------
192.168.1.0/24   ansibleSubnet  Succeeded            ansiblelab
</pre>


**Step 3.** Step 3.	Now we have all the information we need, and we can run all playbook with all required variables. Note that variables can be defined inside of playbooks, or can be entered at runtime along the ansible-playbook command with the `--extra-vars` option. As VM name please use **only lower case letters and numbers** (no hyphens, underscore signs or upper case letters), and a unique name, for example, using your birthday as suffix), since the creation of the DNS for the public IP requires that the VM name is unique (region-wide).

<pre lang="...">
<b>ansible-playbook ~/ansible-azure-lab/new_vm_web.yml --extra-vars "vmname=your-vm-name resgrp=ansiblelab vnet=ansibleVnet subnet=ansibleSubnet"</b>
[WARNING]: provided hosts list is empty, only localhost is available

PLAY [CREATE VM PLAYBOOK] *********************************************************

TASK [debug] **********************************************************************
ok: [localhost] => {
    "changed": false,                                                                                                    "msg": "Public DNS name your-vm-name.westeurope.cloudapp.azure.com resolved to IP NXDOMAIN. "
}

TASK [Create storage account] *****************************************************
changed: [localhost]                                                                                                     

TASK [Create security group that allows SSH and HTTP] ***********************************************************************************
changed: [localhost]                                                                                                     

TASK [Create public IP address] ***************************************************
changed: [localhost]                                                                                                     

TASK [Create NIC] *****************************************************************
changed: [localhost]                                                                                                     

TASK [Create VM] ******************************************************************
changed: [localhost]

PLAY RECAP ************************************************************************
localhost                  : <b>ok=6</b>    changed=5    unreachable=0    <b>failed=0</b>
</pre>

**Note:** some errors you might get at this step, if you enter a "wrong" VM name (see the appendix for more details):
- `fatal: [localhost]: FAILED! => {"changed": false, "failed": true, "msg": "The storage account named storageaccountname is already taken. - Reason.already_exists"}`
Resolution: use another name for your VM, that one seems to be already taken
- `fatal: [localhost]: FAILED! => {"changed": false, "failed": true, "msg": "Error creating or updating your-vm-name - Azure Error: InvalidDomainNameLabel\nMessage`: The domain name label for your VM is invalid. It must conform to the following regular expression: ^[a-z][a-z0-9-]{1,61}[a-z0-9]$."}
Resolution: use another name for your VM following the naming syntax. The problem could be that VM names should not start with a number or an upper case letter, but with a lower case letter 


**Step 4.** While the playbook is running, have a look in another console inside of the file `~/ansible-azure-lab/new_vm_web.yml` , and try to identify the different parts it is made out of. 

**Step 5.** Step 5.	You can run the dynamic inventory, to verify that the new VM is now detected by Ansible:

<pre lang="...">
<b>python ./ansible/contrib/inventory/azure_rm.py --list | jq</b>
{
  "westeurope": [
    "ansibleMaster",
    "your-vm-name"
  ],
  "your-vm-name": [
    "your-vm-name"
  ],
  "_meta": {
    "hostvars": {
      "ansibleMaster": {
        "powerstate": "running",
        "resource_group": "ansiblelab",
        ...
        "ansible_host": "52.174.19.210",
        "name": "ansibleMaster",
      },
      "your-vm-name": {
        "powerstate": "running",
        "resource_group": "ansiblelab",
        ...
        "ansible_host": "52.174.198.220",
        "name": "your-vm-name",
        "fqdn": "your-vm-name.westeurope.cloudapp.azure.com",
      }
    }
  },
  "ansibleMasterNSG": [
    "ansibleMaster"
  ],
  "azure": [
    "ansibleMaster",
    "your-vm-name"
  ],
  "ansiblelab": [
    "ansibleMaster",
    "your-vm-name"
  ]
}
</pre>

**Step 6.** Using the dynamic inventory, run the ping test again, to verify that the dynamic inventory file can see the new machine. The first time you run the test you will have to verify the SSH host key, but successive attempts should run without any interaction being required:

<pre lang="...">
<b>ansible -i ~/ansible/contrib/inventory/azure_rm.py all -m ping</b>
The authenticity of host '52.174.47.97 (52.174.47.97)' can't be established.
ECDSA key fingerprint is 48:89:dc:6d:49:77:2d:85:50:6b:73:90:70:c6:05:5c.
Are you sure you want to continue connecting (yes/no)? ansibleMaster | SUCCESS => {
    "changed": false,
    "ping": "pong"
}
yes
your-vm-name | <b>SUCCESS</b> => {
    "changed": false,
    "ping": "pong"
}
</pre>


<pre lang="...">
<b>ansible -i ~/ansible/contrib/inventory/azure_rm.py all -m ping</b>
ansibleMaster | SUCCESS => {
    "changed": false,
    "ping": "pong"
}
your-vm-name | SUCCESS => {
    "changed": false,
    "ping": "pong"
}
</pre>

**Note:** the first time you connect to the new VM you need to manually accept the SSH fingerprint, further attempts will work without manual intervention


## What we have learnt

Ansible playbooks can be used not only to interact with Linux VMs running on Azure, but with Azure itself. In this section we used an Ansible playbook (which is executed against the local host) to create a new Linux VM in Azure.

As with any Ansible deployment, getting password-less SSH authentication right is a critical step. For that purpose, the creation of the VM in Azure needs to make sure that the right users with the right SSH public keys are deployed. 



# Lab 6: Running an Ansible playbook on the new VM <a name="lab6"></a>

In this section we will run another Ansible playbook, this time to configure the newly created machine. As example, we will run a very simple playbook that installs a software package (httpd) and downloads an HTML page from a Github repository. If everything works, after running the playbook you will have a fully functional Web server.

You will probably be thinking that if the purpose of the exercise is creating a Web server, there are other quicker ways in Azure to do that, for example, using Web Apps. Please consider that we are using this as example, you could be running an Ansible playbook to do anything that Ansible supports, and that is a lot.

**Step 1.** We will be using the example playbook that was downloaded from Github `~/ansible-azure-lab/httpd.yml`. Additionally, we will be using the variable `vmname` in order to modify the `hosts` parameter of the playbook, that defines on which host (out of the ones returned by the dynamic inventory script) the playbook will be run. First, we verify that there is no Web server running on the machine. Please replace &#39;your-vm-name&#39; with the real name of your VM (with your birthday as suffix, if you followed the recommendation in lab 5 step 3):

```
curl http://your-vm-name.westeurope.cloudapp.azure.com
curl: (7) Failed connect to your-vm-name.westeurope.cloudapp.azure.com:80; Connection refused
```
**Note:** if you provisioned your VM to a different region, the URL will be different too.

And now we will install the HTTP server with our Ansible playbook:

<pre lang="...">
<b>ansible-playbook -i ~/ansible/contrib/inventory/azure_rm.py ~/ansible-azure-lab/httpd.yml --extra-vars  "vmname=your-vm-name"</b>
		
PLAY [Install Apache Web Server] ***********************************************
		
TASK [Ensure apache is at the latest version] **********************************
changed: [your-vm-name]
		
TASK [Change permissions of /var/www/html] *************************************
changed: [your-vm-name]
		
TASK [Download index.html] *****************************************************
changed: [your-vm-name]
		
TASK [Ensure apache is running (and enable it at boot)] ************************
changed: [your-vm-name]
		
PLAY RECAP *********************************************************************
your-vm-name                : <b>ok=4</b>    changed=4    unreachable=0    <b>failed=0</b>
</pre>

**Step 2.** Now you can test that there is a Web page on our VM using your Internet browser and trying to access the location http://your-vm-name.westeurope.cloudapp.azure.com, or using curl from the master VM:

```
curl http://your-vm-name.westeurope.cloudapp.azure.com
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <title>Hello World</title>
    </head>
    <body>
        <h1>Hello World</h1>
        <p>
            <br>This is a test page
            <br>This is a test page
            <br>This is a test page
        </p>
    </body>
</html>
```

**Step 3.** Now we could run the previous command periodically, to make sure that the configuration is what we want it to be, or more
precisely, what is defined in the VM's playbook. In order to do this we will use the cron functionality of Linux, that allows running
commands at certain schedules. In this lab we will run the command every minute, in a production environment you would probably want a
lower frequency. In order to add an additional entry to your cron scheduled jobs, you need to enter the following command: 

```
crontab -e
```

The default text editor will open, and you need to add a new line at the end of the file (do not forget to replace "your-vm-name" with
the actual name of your virtual machine):

```
* * * * * ansible-playbook -i ~/ansible/contrib/inventory/azure_rm.py ~/ansible-azure-lab/httpd.yml --extra-vars  "vmname=your-vm-name"
```

You can check the times that the command was executed with this line (note that you need root privilege, since all cron jobs for all users are logged in that file):

```
sudo tail /var/log/cron
```

**Step 3.** Now let us verify that our setup works. You can connect to your VM over SSH (you can use the VM name from your Ansible master, since Azure DNS service will resolve it to the right IP address), and change something. In this lab we will delete our Web page.


<pre lang="...">
[lab-user@ansibleMaster ~]$ <b>ssh jose@your-vm-name</b> 
</pre>

<pre lang="...">
[lab-user@your-vm-name ~]$ <b>rm /var/www/html/index.html</b>
[lab-user@your-vm-name ~]$ <b>exit</b>
</pre>

<pre lang="...">
[lab-user@ansibleMaster ~]$ <b>curl http://your-vm-name.westeurope.cloudapp.azure.com</b> 
</pre>

**Step 4.** If you did the previous steps quick enough, the first time you run the curl command you will see that a different page is coming back. If you reissue the previous curl command again after some seconds, cron will have run the playbook again and fixed any issue (such as the absence of index.html), so that you should see our custom page coming back now. 


## What we have learnt

Once the VM is created in Azure, Ansible can be used to configure it via standard Ansible playbooks. Using dynamic inventories is not necessary to have a static list of the existing VMs in Azure.

By periodically running ansible playbooks you can correct changes that deviate VMs from their desired configuration (as defined in the playbooks). We deleted a file, but the change might have been something else such as stopping httpd or even uninstalling it. Note that as we configured the playbook, changing the contents of the index.html file will not suffice for ansible to overwrite it.

# Lab 7: Running Ansible playbooks periodically for configuration management in Azure<a name="lab7"></a>

Similarly to what we learnt in the previous lab, we can run Ansible playbooks periodically not only against VMs, but against Azure itself. The same way that a playbook can correct configuration deviations in a VM, they can equally correct configuration deviation in Azure. See more information about Azure modules in Ansible here: http://docs.ansible.com/ansible/latest/list_of_cloud_modules.html#azure.

**Step 1.** We will use the resources created by the playbook that configured our VM. Verify the objects that the playbook creates from the Ansible master VM. As you can see, it creates a NIC, a Network Security Group (NSG) and a public IP address (obviously other than the VM itself). Other resources are supported by Ansible, such as vnets, resource groups or storage blobs.

```
more ~/ansible-azure-lab/new_vm_web.yml
```

**Step 2.** Connect to Azure and change some of the attributes defined in the playbook. In this example we will remove the Web entry of the NSG, so no web access to the VM will be possible any more. From an Azure CLI prompt where you have logged into Azure, run these commands to delete the WEB entry from the NSG associated to your VM. Do not forget to replace "your-vm-name" with the actual name of your virtual machine:

<pre lang="...">
<b>az network nsg list -g ansiblelab -o table</b>
Location    Name              ProvisioningState    ResourceGroup    ResourceGuid
----------  ----------------  -------------------  ---------------  ------------------------------------
westeurope  ansibleMasterNSG  Succeeded            ansiblelab       d4ab6ce3-a20c-4008-b79a-b72100adca80
westeurope  <b>your-vm-name</b>      Succeeded            ansiblelab       fc655824-2583-4585-84f1-42e8dfab4575
</pre>

<pre lang="...">
<b>az network nsg rule list -g ansiblelab --nsg-name your-vm-name -o table</b>
Access    DestAddressPrefix  DestPortRange  Direction  Name  Priority  Protocol  Provisioning  SrcAddressPrefix  SrcPortRange
--------  -----------------  -------------  ---------  ----  --------  --------  ------------  ----------------  ------------
Allow     *                             22  Inbound    SSH        101  Tcp       Succeeded     *                 *
Allow     *                             80  Inbound    WEB        102  Tcp       Succeeded     *                 *
</pre>

**Note:** the columns in the previous output have been modified for readability purposes

<pre lang="...">
<b>az network nsg rule delete --nsg-name myvm131076 -n WEB</b>
</pre>

<pre lang="...">
<b>az network nsg rule list -g ansiblelab --nsg-name your-vm-name -o table</b>
Access    DestAddressPrefix  DestPortRange  Direction  Name  Priority  Protocol  Provisioning  SrcAddressPrefix  SrcPortRange
--------  -----------------  -------------  ---------  ----  --------  --------  ------------  ----------------  ------------
Allow     *                             22  Inbound    SSH        101  Tcp       Succeeded     *                 *
</pre>

**Note:** the columns in the previous output have been modified for readability purposes

**Step 3.** Verify that Web access to the VM is no longer possible

<pre lang="...">
[lab-user@ansibleMaster ~]$ <b>curl http://myvm131076.westeurope.cloudapp.azure.com</b> 
</pre>

**Step 3.** Similarly to the previous lab, we will create a cron job that will execute our Ansible playbook every minute adding the entry `* * * * * ansible-playbook ~/ansible-azure-lab/new_vm_web.yml --extra-vars "vmname=your-vm-name resgrp=ansiblelab vnet=ansibleVnet subnet=ansibleSubnet"`. Remember that in a production environment you probably do not want to run playbooks that frequently. Do not forget to replace "your-vm-name" with your actual VM's name.

```
crontab -e
```

The default text editor will open, and you need to add a new line at the end of the file (do not forget to replace "your-vm-name" with
the actual name of your virtual machine):

```
* * * * * ansible-playbook ~/ansible-azure-lab/new_vm_web.yml --extra-vars "vmname=your-vm-name resgrp=ansiblelab vnet=ansibleVnet subnet=ansibleSubnet"
```

**Step 4.**  You can check the times that the command was executed with this line (note that you need root privilege, since all cron jobs for all users are logged in that file). After that the playbook has run at least once, verify that the NSG is back to its desired state.

```
sudo tail /var/log/cron
```

<pre lang="...">
<b>az network nsg rule list -g ansiblelab --nsg-name your-vm-name -o table</b>
Access    DestAddressPrefix  DestPortRange  Direction  Name  Priority  Protocol  Provisioning  SrcAddressPrefix  SrcPortRange
--------  -----------------  -------------  ---------  ----  --------  --------  ------------  ----------------  ------------
Allow     *                             22  Inbound    SSH        101  Tcp       Succeeded     *                 *
Allow     *                             80  Inbound    WEB        102  Tcp       Succeeded     *                 *
</pre>

**Note:** the columns in the previous output have been modified for readability purposes

## What we have learnt

Ansible can be used not only to verify that VMs are configured according to a desired state, but that Azure itself (such as the Network Security Groups in this lab) are configured according to a desired state. This way you can be sure that your infrastructure is deployed exactly as it should.


# Lab 8: Using Ansible with ARM templates for Azure configuration management <a name="lab8"></a>

As we saw in the previous lab, Azure modules for Ansible can be used to fix configuration deviations of existing resources supported by Ansible. However, Azure modules for Ansible support a limited amount of resources. For example, there is no Ansible module to create an availability group or a network load balancer at the time of this writing. In order to overcome this limitation, you can use Ansible to deploy a playbook that will refer to an ARM template, and you can offload logic from the playbook to the ARM template.

As additional benefit, the Azure admin does not need to learn the playbook syntax, but can work with the well known constructs of Azure templates.

**Step 1.** We will deploy a second VM, this time with an ARM template. For simplicity reasons we will use a predefined VM name with no public IP address. However, we will create a slightly more complex setup, with an additional vnet, subnet, availability group and load balancer.

<pre lang="...">
<b>ansible-playbook ~/ansible-azure-lab/new_ARM_deployment.yml --extra-vars "resgrp=ansiblelab location=westeurope"</b>
 [WARNING]: provided hosts list is empty, only localhost is available

PLAY [CREATE ARM Deployment PLAYBOOK] *************************************

TASK [Deploy ARM template] ************************************************
changed: [localhost]

PLAY RECAP ****************************************************************
localhost                  : ok=1    changed=1    unreachable=0    failed=0
</pre>

**Step 2.** It is important to realize that applying an ARM template to Azure is an idempotent. That is to say, deploying an ARM template once has the same effect that deploying that very same template one hundred times. In other words, you can safely redeploy ARM templates to the same resource group without the concern that duplicate resources will be created. As such, you can schedule the ARM template to be deployed periodically with the Linux cron facility as we have seen in previous labs. However, for the purpose of our lab we will re-run the template manually, since we have already seen twice what the mechanism with cron looks like (and due to the fact that this playbook takes eventually longer to run, so having it running every minute is quite a bad idea). Re-run the template issuing the same command as in step 1:

<pre lang="...">
<b>ansible-playbook ~/ansible-azure-lab/new_ARM_deployment.yml --extra-vars "resgrp=ansiblelab location=westeurope"</b>
 [WARNING]: provided hosts list is empty, only localhost is available

PLAY [CREATE ARM Deployment PLAYBOOK] *************************************

TASK [Deploy ARM template] ************************************************
changed: [localhost]

PLAY RECAP ****************************************************************
localhost                  : ok=1    changed=1    unreachable=0    failed=0
</pre>

Note how the template took a shorter time to be deployed (no new resources were configured).

**Step 3.** Now we will introduce a change in one of the objects deployed by the template, for example in the Load Balancer probe. These changes are very difficult to detect, but could completely break your application. Therefore, it is extremely useful having a mechanism to automatically fix these deviations from the desired state. In this case, we will change the TCP port that the healthcheck probe of the load balancer uses to verify the state of the servers:

<pre lang="...">
<b>az network lb probe update --lb-name mySlb -n myProbe --set port=80</b>
{
  "etag": "W/\"226cf561-77a4-41f3-8d31-0448c449d002\"",
  "id": "/subscriptions/e7da9914-9b05-4891-893c-546cb7b0422e/resourceGroups/ansiblelab/providers/Microsoft.Network/loadBalancers/mySlb/probes/myProbe",
  "intervalInSeconds": 15,
  "loadBalancingRules": [
    {
      "id": "/subscriptions/e7da9914-9b05-4891-893c-546cb7b0422e/resourceGroups/ansiblelab/providers/Microsoft.Network/loadBalancers/mySlb/loadBalancingRules/SSHrule",
      "resourceGroup": "ansiblelab"
    }
  ],
  "name": "myProbe",
  "numberOfProbes": 2,
  <b>"port": 80</b>,
  "protocol": "Tcp",
  "provisioningState": "Succeeded",
  "requestPath": null,
  "resourceGroup": "ansiblelab"
}
</pre>

**Step 4.** Now re-run the template issuing the same command as in step 1 one more time (this time it will take a bit longer to run than in Step 2, since the load balancer needs to be reprovisioned), and verify after the run that the probe is now back to port 22:

<pre lang="...">
<b>ansible-playbook ~/ansible-azure-lab/new_ARM_deployment.yml --extra-vars "resgrp=ansiblelab location=westeurope"</b>
 [WARNING]: provided hosts list is empty, only localhost is available

PLAY [CREATE ARM Deployment PLAYBOOK] *************************************

TASK [Deploy ARM template] ************************************************
changed: [localhost]

PLAY RECAP ****************************************************************
localhost                  : ok=1    changed=1    unreachable=0    failed=0
</pre>

<pre lang="...">
<b>az network lb probe show --lb-name mySlb -n myProbe</b>
{
  "etag": "W/\"59a4d2d2-d939-4285-b533-d6c362ab81fe\"",
  "id": "/subscriptions/e7da9914-9b05-4891-893c-546cb7b0422e/resourceGroups/ansiblelab/providers/Microsoft.Network/loadBalancers/mySlb/probes/myProbe",
  "intervalInSeconds": 15,
  "loadBalancingRules": [
    {
      "id": "/subscriptions/e7da9914-9b05-4891-893c-546cb7b0422e/resourceGroups/ansiblelab/providers/Microsoft.Network/loadBalancers/mySlb/loadBalancingRules/SSHrule",
      "resourceGroup": "ansiblelab"
    }
  ],
  "name": "myProbe",
  "numberOfProbes": 2,
  <b>"port": 22,</b>
  "protocol": "Tcp",
  "provisioningState": "Succeeded",
  "requestPath": null,
  "resourceGroup": "ansiblelab"
}
</pre>


## What we have learnt

Lab 7 demonstrated how to use Ansible playbooks to define desired state configuration for certain Azure objects (those supported by the Ansible modules for Azure), and in this lab we have seen how to use Ansible functionality together with ARM templates in order to define desired state via templates for anything in Azure that can be defined with an ARM template.

Idempotency is a critical property of ARM templates that enables this use case.

A valid question is why should you bother with Ansible, since you could just schedule Azure CLI deployments with cron without having to rely in Ansible. While you could certainly do that, there are certain scenarios where you want to use the same mechanism for describing and deploying desired state configuration for all your resources (including the state of the guest OS or deployments in other clouds), and Ansible offers a way to do that. In other words, it is a question of standardization.

In any case, ARM templates offer a way to enhance Ansible support for new Azure services, and allows administrators to describe Azure desired state via templates that will be deployed using Ansible.  

# Lab 9: Deleting a VM using Ansible - Optional <a name="lab9"></a>

Finally, similarly to the process to create a VM we can use Ansible to delete it, making sure that associated objects such storage account, NICs and Network Security Groups are deleted as well. For that we will use the playbook in this lab&#39;s repository delete\_vm.yml:

**Step 1.** We need to disable the automatic execution of Ansible playbooks, otherwise you will not see the effect of removing a VM, since the next Ansible pass would recreate it. Edit the crontab file with the following commnad, and comment all lines with a hash symbol at the beginning of each line.

```
crontab -e
```

Your crontab file should look like this:

<pre lang="...">
[lab-user@ansibleMaster ~]$ <b>crontab -l</b>
#* * * * * /usr/bin/ansible-playbook -i ~/ansible/contrib/inventory/azure_rm.py ~/ansible-azure-lab/httpd.yml --extra-vars  "vmname=myvm131076"
#* * * * * /usr/bin/ansible-playbook ~/ansible-azure-lab/new_vm_web.yml --extra-vars "vmname=myvm131076 resgrp=ansiblelab vnet=ansibleVnet subnet=ansibleSubnet"
</pre>


**Step 2.** You can now remove the VM created at the beginning of this lab using the provided playbook. Do not forget to replace "your-vm-name" with the actual name of your Virtual Machine:

<pre lang="...">
<b>ansible-playbook ~/ansible-azure-lab/delete_vm.yml --extra-vars "vmname=your-vm-name resgrp=ansiblelab"</b>
[WARNING]: provided hosts list is empty, only localhost is available

PLAY [Remove Virtual Machine and associated objects] ***************************
		
TASK [Remove VM and all resources] *********************************************
ok: [localhost]
		
TASK [Remove storage account] **************************************************
ok: [localhost]
		
PLAY RECAP *********************************************************************
localhost                  : <b>ok=2</b>    changed=0    unreachable=0    <b>failed=0</b>
</pre>

**Step 3.** Verify that the VM does not exist any more using Ansible&#39;s dynamic inventory functionality:

```
ansible -i ~/ansible/contrib/inventory/azure_rm.py all -m ping
```

## What we have learnt

Ansible playbooks can be used for the full lifecycle of a VM. Not only creation and management, but deletion as well.


# Conclusion <a name="conclusion"></a>

In this lab we have seen how to use Ansible for two purposes:

On one side, Ansible can be used in order to create VMs, in a similar manner than Azure quickstart templates. If you already know Ansible and prefer using Ansible playbooks instead of native Azure JSON templates, you can certainly do so.

On the other side, and probably more importantly, you can use Ansible in order to manage the configuration of all your virtual machines in Azure. Whether you have one VM or one thousand, Ansible will discover all of them (with its dynamic inventory functionality) and apply any playbooks that you have defined, making server management at scale much easier.

Lastly, by automating periodic execution of Ansible playbooks you can make sure that the configuration of Azure resources (including guest OS configuration) matches the desired state defined in Ansible playbooks, and optionally in Azure ARM templates. And all that without installing any agent, due to the agentless nature of Ansible.

All in all, the ultimate purpose of this lab is proving to Ansible admins that you can use the same tools in Azure as in your on-premises systems.

# End the lab <a name="end"></a>

To end the lab, simply delete the resource group that you created in the first place (**ansiblelab** in our example) from the Azure portal or from the Azure CLI:

```
az group delete ansiblelab
```

Optionally, you can delete the service principal and the application that we created at the beginning of this lab:

```
azure ad sp delete -o 44444444-4444-4444-444444444444
```
**Note:** do not forget to replace the GUID above with the real object ID of the Service Principal in your environement

In order to delete the Active Directory application, run this command:

```
az ad app delete --id 11111111-1111-1111-1111111111
```
**Note:** do not forget to replace the GUID above with the real Application ID in your environement


# Conclusion <a name="conclusion"></a>

In this lab we have seen how to use Ansible for two purposes:

On one side, Ansible can be used in order to create VMs, in a similar manner than Azure quickstart templates. If you already know Ansible and prefer using Ansible playbooks instead of native Azure JSON templates, you can certainly do so.

On the other side, and probably more importantly, you can use Ansible in order to manage the configuration of all your virtual machines in Azure. Whether you have one VM or one thousand, Ansible will discover all of them (with its dynamic inventory functionality) and apply any playbooks that you have defined, making server management at scale much easier.
All in all, the purpose of this lab is showing to Ansible admins that they can use the same tools in Azure as in their on-premises systems. 


# References <a name="ref"></a>

Useful links:

- Ansible web page: [https://www.ansible.com](https://www.ansible.com)
- Azure portal: [https://portal.azure.com](https://portal.azure.com)
- Using CLI to create a Service Principal: [https://docs.microsoft.com/en-us/azure/azure-resource-manager/resource-group-authenticate-service-principal-cli](https://docs.microsoft.com/en-us/azure/azure-resource-manager/resource-group-authenticate-service-principal-cli)
- Ansible documentation - Getting started with Azure: [https://docs.ansible.com/ansible/guide\_azure.html](https://docs.ansible.com/ansible/guide_azure.html)
- Azure CLI installation on Linux and Mac: [https://azure.microsoft.com/en-us/downloads/cli-tools-install/](https://azure.microsoft.com/en-us/downloads/cli-tools-install/)