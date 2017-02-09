## Adding a Marketplace Item to Azure Stack
Once your Ubuntu base image is in the platform image repository within your Azure Stack, you can deploy ARM templates that reference those images without any further prep work...however having choices in your marketplace is pretty cool, and it’s not that difficult. Especially seeing as I’ve packaged marketplace items for you to save you a job :-)

If you've stumbled upon this page and you're not quite sure what a Marketplace item is, it's one of these:

  ![Azure Stack Gallery](/deploy/azurestack/docs/media/gallerymedium.PNG)

As you can see, I've created entries for the Ubuntu base image, along with images specific to Jenkins, and the Parts Unlimited MRP application. Creating a marketplace item requires a number of steps, which involve the following:

1. Creation of a **manifest.json** file, which is a bit like a metadata document
2. Creation of an **ARM Template** which actually configures the deployment from your image
3. Creation of a **UI Definition** file, which determines the blades used during the portal deployment

Once you have those 3 items defined, you're at a point where you can used the [Azure Gallery Packager tool](http://www.aka.ms/azurestackmarketplaceitem) to package all of those respective files into a .azpkg file, that is then uploaded into your Azure Stack.

### Download an Example Package for the Base Ubuntu Image
As mentioned earlier, it's much easier if you start from an esiting set of resources, and customize from there, so to help you understand the relationship between the core package files discussed above, you can download a set of files I've provided, already packaged as a .azpkg file. **Download it onto your MAS-CON01 machine**.

- [Download Base Image Package Files](/deploy/azurestack/instances/ubuntu_server_1404_base/Canonical.UbuntuServer.1.0.0.azpkg?raw=true)

1. Once downloaded, navigate to the folder containing your newly downloaded image, right click the file, and **rename to .zip**.
2. Unzip the files

What you'll see inside are a number of files and folders.

  ![Unzipped Package](/deploy/azurestack/docs/media/UnzippedPackage.PNG)
  
