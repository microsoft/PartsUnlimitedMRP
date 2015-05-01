$RGName = "dcarolinuxcse"
New-AzureResourceGroup -Name $RGName -Location 'West US'

#### Before deploying Add you subscription ID #####
New-AzureResourceGroupDeployment -Name dcarodeploy02 -ResourceGroupName $RGName -TemplateFile '.\DeployMRP-CSE.json' -TemplateParameterFile '.\DeployMRP-CSE - parameters.json'-Verbose
Get-AzureResourceGroup -Name $RGName
