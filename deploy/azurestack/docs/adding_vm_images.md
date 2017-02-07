# Adding VM Images to Azure Stack#

Azure Stack enables administrators to make VM images, such as their organization’s custom VHD, available to their tenants. Images can be referenced by Azure Resource Manager templates or added to the Azure Marketplace UI with the creation of a Marketplace item. As we saw previously, a Windows Server 2012 R2 image is included by default in the Azure Stack Technical Preview.

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

