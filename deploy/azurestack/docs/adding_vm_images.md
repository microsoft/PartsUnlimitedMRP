# Adding VM Images to Azure Stack#

Azure Stack enables administrators to make VM images, such as their organization’s custom VHD, available to their tenants. Images can be referenced by Azure Resource Manager templates or added to the Azure Marketplace UI with the creation of a Marketplace item. As we saw previously, a Windows Server 2012 R2 image is included by default in the Azure Stack Technical Preview.

An image can be added to your Azure Stack Platform Image Repository (PIR) in 2 ways - via the portal, or programmatically. We'll focus on the programmatical approach, specifically with PowerShell, as it's faster, and more repeatable.  Plus, you can copy and paste my code ;-)

## Downloading an Image

Firstly, you should be **logged into you Azure Stack environment, and specifically, within the MAS-CON01 machine**.

For the purpose of our testing, we're going to focus on Linux images. Microsoft has worked with a number of Linux vendors to provide a set of Azure Stack-compatible images for use in your Azure Stack environments:

   * [Bitnami](https://bitnami.com/azure-stack)
   * [CentOS](http://olstacks.cloudapp.net/latest/)
   * [CoreOS](https://stable.release.core-os.net/amd64-usr/current/coreos_production_azure_image.vhd.bz2)
   * [SuSE](https://download.suse.com/Download?buildid=VCFi7y7MsFQ~)
   * [Ubuntu 14.04 LTS](https://partner-images.canonical.com/azure/azure_stack/) / [Ubuntu 16.04 LTS](http://cloud-images.ubuntu.com/releases/xenial/release/ubuntu-16.04-server-cloudimg-amd64-disk1.vhd.zip)

We'll be focusing on **Ubuntu 14.04 LTS**, so that's the one you should be downloading too. When you click on the Ubuntu 14.04 LTS link above, you'll be taken to a page with a list of builds for that particular release. Choose the one at the bottom of the list, which typically corresponds to the newest of the builds. If you accept the default name, and save location, you'll end up with a 300+MB zip file, located in the downloads folder.

Once downloaded, extract the zip file, to end up with a single 30GB VHD, with the name **trusty-server-cloudimg-amd64-disk1.vhd**.

1. Rename the VHD file to Ubuntu1404LTS.vhd
2. Move the file to a new folder called C:\Images
3. Optional - to free up space, delete the original ZIP file you just downloaded.

## Connecting to Azure Stack via PowerShell

Before we can begin adding an image to the Azure Stack PIR, we'll need to be able to connect to the Azure Stack via PowerShell, and there are a couple of steps we need to perform to do that.

### Install Azure Stack PowerShell cmdlets & dependencies
1. Azure Stack uses the same AzureRM cmdlets that you'd use if you were connecting to Azure. These are installed from the PowerShell Gallery. To begin, open an elevated (as administrator) PowerShell Console on MAS-CON01 and run the following command to return a list of PowerShell repositories available:

    ``` PowerShell
    Get-PSRepository
    ```
2. Run the following command to install the AzureRM module:
    ``` PowerShell
    Install-Module -Name AzureRM -RequiredVersion 1.2.6 -Scope CurrentUser -Force -AllowClobber
    ```
> *-Scope CurrentUser* is optional. If you want more than the current user to have access to the modules, use an elevated command prompt and leave off the *Scope* parameter. -Force and -AllowClobber will ensure that any existing AzureRM modules are replaced and updated.
   
3. To confirm the installation of AzureRM modules, execute the following commands:
    ``` PowerShell
    Get-Command -Module AzureRM.AzureStackAdmin
    ```

Once verified, we need to ensure we have the relevant tools in place to connect to Azure Stack via PowerShell. These could also be used for Azure, but may lack some of the latest Azure features. The tools are all [hosted on GitHub](https://github.com/Azure/AzureStack-Tools). To grab them, run the following in an elevated PowerShell console on MAS-CON01:

``` PowerShell
cd\
Invoke-Webrequest https://github.com/Azure/AzureStack-Tools/archive/master.zip -OutFile master.zip
Expand-Archive master.zip -DestinationPath . -Force
cd AzureStack-Tools-master\connect
Import-Module .\AzureStack.Connect.psm1
```
### Connect to Azure Stack
With the Azure Stack connection module now imported, you can use the following commands to connect to your Azure Stack. Note, AzureRM cmdlets can be targeted at multiple Azure clouds such as Azure China, Government, and Azure Stack.
To target your Azure Stack instance, an AzureRM environment needs to be registered as follows.

```powershell
Add-AzureStackAzureRmEnvironment -AadTenant "<mydirectory>.onmicrosoft.com"
```
The AadTenant parameter above specifies the directory that was used when deploying Azure Stack.  After registering, AzureRM environment cmdlets can be easily targeted at your Azure Stack instance. For example:

```powershell
Add-AzureRmAccount -EnvironmentName AzureStack
```
You will be prompted for the account login including two factor authentication if it is enabled in your organization. You can also log in with a service principal using appropriate parameters of the Add-AzureRmAccount cmdlet. Keep this PowerShell console window open, for use later.

**Note, if you need info on connecting to your Azure Stack via VPN, see the [Azure Stack documentation](https://github.com/Azure/AzureStack-Tools/tree/master/Connect).**

Once logged in, you're ready to start adding images to your Azure Stack.

## Add VM Image to Platform Image Repository with PowerShell
The first thing we'll need to do to upload an image with PowerShell, is import the Compute module, from our tools folder.

```powerShell
cd ..
cd ComputeAdmin
Import-Module .\AzureStack.ComputeAdmin.psm1
```
We're then ready to push our Ubuntu image that we downloaded earlier, into our Azure Stack PIR. Now, the code below is accurate assuming you used the same naming conventions I did, earlier. If not, adjust as necessary.

```powerShell
Add-VMImage -publisher "Canonical" -offer "UbuntuServer" -sku "1404-LTS" -version "1.0.0" -osType Linux -osDiskLocalPath 'C:\Images\Ubuntu1404LTS.vhd' -tenantID <myaadtenant>.onmicrosoft.com -CreateGalleryItem $false
```
The command above does the following:

* Authenticates to the Azure Stack environment
* Uploads the local VHD to a newly created temporary storage account
* Adds the VM image to the VM image repository
* Removes the temporary storage account

You'll notice at the end of the command, there was a parameter called **-CreateGalleryItem**, and the value for this, I declared as false. By default, when you run this command without using this parameter, a default marketplace item will be created for this particular image. If you recall, earlier when we deployed our Windows Server 2012 R2 VM, this was created from a marketplace image, which had an icon, desciption and more. For this Ubuntu image, we'll skip the marketplace creation for now, and will create a more professional, complete one later. Once you've executed the command, leave the PowerShell window open for later.

If you're interested in understanding a bit more about the other parameters used with the command above, [check out the docs](https://docs.microsoft.com/en-us/azure/azure-stack/azure-stack-add-vm-image).

## Explore the Platform Image Repository
With the upload complete, it's important to confirm that the image now exist in the PIR. To do so, follow these steps:

1. Open the Azure Stack Portal and log in as the Service Administrator.
2. On the dashboard, click on the **Region Management** tile.
3. In the Region Management blade, click on **local** and then in the Resource Providers box, click on **Compute**.
4. In the Compute blade, under Content, click on **VM Images**.
5. In the VM Images blade, you should see 2 images listed. The original Windows Server image, and the newly added Ubuntu image.

    ![Image Added](/deploy/azurestack/docs/media/VM%20Images%20Complete.PNG)

6. Close all open blades and return to the dashboard.

## *Important - Add Diagnostic Extension to enable Linux Monitoring
The extension required to monitor Linux VMs, didn't ship in the box with the Azure Stack Technical Preview 2, so we'll quickly add it to avoid any issues later. On the **Azure Stack POC Host machine**, execute the following steps, from an **administrative PowerShell console**.

```powershell
New-Item C:\ClusterStorage\Volume1\Shares\SU1_Infrastructure_1\CRP\GuestArtifactRepository\IaaSDiagnosticsLinux -Type directory
Invoke-WebRequest https://raw.githubusercontent.com/bgelens/BlogItems/master/Microsoft.OSTCExtensions.LinuxDiagnostic_2.3.9009.zip -OutFile C:\ClusterStorage\Volume1\Shares\SU1_Infrastructure_1\CRP\GuestArtifactRepository\IaaSDiagnosticsLinux\Microsoft.OSTCExtensions.LinuxDiagnostic_2.3.9009.zip
Invoke-WebRequest https://raw.githubusercontent.com/Microsoft/PartsUnlimitedMRP/deploy/azurestack/customscripts/linux_extension/manifest.json -OutFile C:\ClusterStorage\Volume1\Shares\SU1_Infrastructure_1\CRP\GuestArtifactRepository\IaaSDiagnosticsLinux\manifest.json
```
With those commands executed, hop back over to the MAS-CON01 machine, and in your administrative PowerShell console, run the following:

```powershell
Get-AzureRmVMExtensionImage -PublisherName Microsoft.OSTCExtensions -Location local -Type LinuxDiagnostic
```
You should receive an output, and not an exception. If so, you're ready to move on.

# Next Step
You're now ready to start the deployment of the Parts Unlimited MRP environment, or explore any one of the DevOps scenarios provided within this documetation. These include CI/CD with Jenkins, Configuration Management with Chef, and Configuration Management with Puppet, with more to come in the future. Check out the [Getting Started with Parts Unlimited](/deploy/azurestack/docs/get_started_with_MRP) page to go forward.

If you're interested in understanding how to create more professional marketplace items, so that your users can deploy the base Ubuntu image, along with any other future images, direct from the Azure Stack marketplace, then read on...

## *Optional - Create a Marketplace item for your Ubuntu Base Image
If you'd like something like this, in your Azure Stack gallery:

   ![Azure Stack Gallery](/deploy/azurestack/docs/media/gallerymedium.PNG)

then [follow the steps over on this page](/deploy/azurestack/docs/add_marketplace_item.md).
