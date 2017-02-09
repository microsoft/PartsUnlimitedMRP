## Adding a Marketplace Item to Azure Stack
Once your Ubuntu base image is in the platform image repository within your Azure Stack, you can deploy ARM templates that reference those images without any further prep work...however having choices in your marketplace is pretty cool, and it’s not that difficult. Especially seeing as I’ve packaged marketplace items for you to save you a job :-)

If you've stumbled upon this page and you're not quite sure what a Marketplace item is, it's one of these:

  ![Azure Stack Gallery](/deploy/azurestack/docs/media/gallerymedium.PNG)

As you can see, I've created entries for the Ubuntu base image, along with images specific to Jenkins, and the Parts Unlimited MRP application. Creating a marketplace item requires a number of steps, which involve the following:

1. Creation of a **manifest.json** file, which is a bit like a metadata document
2. Creation of a **resources.resjson** file, which contains useful description information
2. Creation of an **ARM Template** which actually configures the deployment from your image
3. Creation of a **UI Definition** file, which determines the blades used during the portal deployment

Once you have those 3 items defined, you're at a point where you can used the [Azure Gallery Packager tool](http://www.aka.ms/azurestackmarketplaceitem) to package all of those respective files into a .azpkg file, that is then uploaded into your Azure Stack.

### Download an Example Package for the Base Ubuntu Image
As mentioned earlier, it's much easier if you start from an esiting set of resources, and customize from there, so to help you understand the relationship between the core package files discussed above, you can download a set of files I've provided, already packaged as a .azpkg file. **Download it onto your MAS-CON01 machine**.

- [Download Base Image Package Files](/deploy/azurestack/instances/ubuntu_server_1404_base/Canonical.UbuntuServer.1.0.0.azpkg?raw=true)

1. Once downloaded, navigate to the folder containing your newly downloaded image, and **create a copy of the file**.
2. Right click the new copy, and **rename to .zip**
2. Unzip the files

What you'll see inside are a number of files and folders.

  ![Unzipped Package](/deploy/azurestack/docs/media/UnzippedPackage.PNG)
  
Some of these can be ignored, as they are created during the packaging process, such as the **_rels** folder, a **GUID folder** and a **Content_Types** XML document. Ignore all of these, and focus on the key files we'll talk about now.

At the root of this folder are 2 files. The **manifest.json** file provides key information about the marketplace item, such as the name, publisher, summary, description and much more. By no means is my manifest file a complete manifest file – more can be done within the manifest file, but for our needs, what we have here is plenty. You’ll see it also makes links to artifacts such as the actual ARM template (in this case, **DeploymentTemplates\\UbuntuServer1404.json**), and **icons** (all stored within the Icons folder), categories, which determines where in the Azure Stack Portal UI, this marketplace item appears, and a **UIDefinition.json** reference, which, at a high level, determines the blade wizard experience that a user would have when selecting this marketplace item. Within the strings folder, there’s also a **resources.resjson** file that links closely with the manifest. You use this file to provide more detailed information about the marketplace item, such as a detailed description, long and short summaries etc. Take a look and see where the links between the two files are.

Feel free to explore the files within the folder structure, and if you wish to customize. **Be careful though – changing certain parameters and variables may result in your ARM template not utilizing the UbuntuServer image we uploaded earlier**, so be careful.

A good way to look through all the files quickly, is to download and install the excellent [Visual Studio Code](https://code.visualstudio.com/Download). Once downloaded and installed, open the containing folder (be sure to keep the existing naming convention of the folder) and you’ll see all the files within the folder, and can quickly navigate between them.

If you want a quick way to download and install Visual Studio Code on your MAS-CON01 machine, here's a useful PowerShell script, to be run within an administrator PowerShell console:

```powershell
Invoke-Webrequest https://go.microsoft.com/fwlink/?LinkID=623230 -OutFile C:\VSCode.exe
Start-Process -FilePath C:\VSCode.exe -ArgumentList '/VerySilent /mergetasks="addcontextmenufiles,addcontextmenufolders,associatewithfiles,addtopath,!runcode"'
Start-Sleep -S 240
Restart-Computer -Force -Confirm:$false
```


Ben Gelens has a [great write up on his blog](https://azurestack.eu/2016/10/adding-and-using-os-gallery-items-to-azure-stack-tp2/) on adding marketplace items, and in addition, there is some very useful information on marketplace items on the [official Azure Stack documentation site](https://docs.microsoft.com/en-us/azure/azure-stack/azure-stack-create-and-publish-marketplace-item), especially the explanation of the key manifest.json components that are very useful to understand.

The key question is, how do you take those files and folders, and get them into Azure Stack?

### Uploading a Package to Azure Stack

Normally, once you've finished creating your respective files within your package folder, you would download and install the [Azure Gallery Packager Tool](http://www.aka.ms/azurestackmarketplaceitem) and follow the [guidance here](https://docs.microsoft.com/en-us/azure/azure-stack/azure-stack-create-and-publish-marketplace-item).

To save you time however, we'll just use the package we orginally downloaded earlier.

1. Navigate to your **Canonical.UbuntuServer.1.0.0.azpkg** file, you downloaded earlier
2. Move it to a newly created folder **C:\MyMarketPlaceItems**.

    It’s important to note that if you are going to use the package I have provided, you need to have used the following info when you uploaded your Ubuntu VHD image to the platform image repository earlier. Any differences, and the package I’m providing will not reference your uploaded image. If you used an exact copy of my PowerShell upload script, you're all set.
    
    - Publisher "Canonical"
    - Offer "UbuntuServer"
    - SKU "1404-LTS"

Now that we have the package ready to upload, we need *somewhere* in Azure Stack to upload it to. For that, we'll create a **Storage Account** that will be used to hold this package, and any others we upload in the future.

1. On MAS-CON01, connect to your Azure Stack via PowerShell. If you're not still connected from the earlier steps, refer to the [Connect to Azure Stack](/deploy/azurestack/docs/connect_to_azurestack.md) docs from earlier.
