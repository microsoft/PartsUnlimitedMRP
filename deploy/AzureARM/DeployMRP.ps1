
#### Deployment of MRP ######
$RGName = "<<CONFIGUE>>"
New-AzureResourceGroup -Name $RGName -Location 'West US'
New-AzureResourceGroupDeployment -Name mrpdeploy -ResourceGroupName $RGName -TemplateFile '.\DeployMRP-CSE.json' -TemplateParameterFile '.\DeployMRP-CSE - parameters.json' -Verbose
Get-AzurePublicIpAddress -ResourceGroupName $RGName
Remove-AzureResourceGroup $RGName
