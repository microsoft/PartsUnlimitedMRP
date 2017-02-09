## Adding a Marketplace Item to Azure Stack

Once your Ubuntu base image is in the platform image repository within your Azure Stack, you can deploy ARM templates that reference those images without any further prep work...however having choices in your marketplace is pretty cool, and it’s not that difficult. Especially seeing as I’ve packaged marketplace items for you to save you a job :-)

If you've stumbled upon this page and you're not quite sure what a Marketplace item is, it's one of these:

  ![Azure Stack Gallery](/deploy/azurestack/docs/media/gallerymedium.PNG)

As you can see, I've created entries for the Ubuntu base image, along with images specific to Jenkins, and the Parts Unlimited MRP application. Creating a marketplace item requires a number of steps, which involve the following:

1. Creation of a **manifest.json** file, which is a bit like a metadata document
2. Creation of an **ARM Template** which actually configures the deployment from your image
3. Creation of a **UI Definition** file, which determines the blades used during the portal deployment

