#Getting Started with DevOps on Azure Stack#

In order to get started exploring DevOps tooling on Azure Stack, you're going to need an Azure Stack, and that requires some hardware. If you're not sure what specific hardware you'll need for the single-node POC configuration, don't fear, it's all [documented here]
(https://docs.microsoft.com/en-us/azure/azure-stack/azure-stack-deploy).

Once you've sourced your hardware, you'll need to [pull down the package](https://azure.microsoft.com/en-us/overview/azure-stack/try/?v=try), which requires you to complete a short registration form, then download the bits. A word of warning though - it's a 20GB file, so set aside a bit of time for a coffee, or 9.

With the package safely downloaded, [follow the documentation](https://docs.microsoft.com/en-us/azure/azure-stack/azure-stack-run-powershell-script) to prepare your deployment machine, extract the necessary bits, and then run the PowerShell deployment script which will essentially deploy Azure Stack on your hardware. A couple of hours later, you'll have yourself an Azure Stack! There are a few examples of deployments scripts within the official documentation that should help you configure your Azure Stack deployment optimally, specifically around the use of Static IP addresses, the use of a Time Server etc. Read the docs carefully.

Make sure you pay particularly close attention to the password expiration configuration, as you don't want your POC machine to expire too soon!

Once your deployment has completed, we can move on to [adding VM images to Azure Stack]() that you can use with the DevOps tools we'll be deploying.
