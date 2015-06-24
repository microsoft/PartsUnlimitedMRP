Switch-AzureMode -Name AzureResourceManager
#You can deploy a new vm and application with only this PowerShell script and files referenced in the github repo. Visual Studio not needed.
#The Visual Studio deployment project is present only if you want to have a better Template/JSON editing experience.
#### Deployment of MRP ######
$RGName = "PartsUnlimitedMRP"
New-AzureResourceGroup -Name $RGName -Location 'West US'
New-AzureResourceGroupDeployment -Name mrpdeploy -ResourceGroupName $RGName -TemplateFile '.\VisualStudioDeploymentProject\VisualStudioDeploymentProject\Templates\DeployMRP-CSE.json' -TemplateParameterFile '.\VisualStudioDeploymentProject\VisualStudioDeploymentProject\Templates\DeployMRP-CSE.param.json' -Verbose
Get-AzureResourceGroup -Name $RGName
Get-AzurePublicIpAddress -ResourceGroupName $RGName