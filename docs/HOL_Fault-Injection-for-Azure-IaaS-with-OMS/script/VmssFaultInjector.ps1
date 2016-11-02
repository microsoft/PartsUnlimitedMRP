 param (
    [Parameter(Mandatory=$true)][string]$ResourceGroupName,
    [Parameter(Mandatory=$true)][int]$DurationOfLoadtestInMinutes
 )

# Get general info about VMSS.
$VmssInfo = Get-AzureRmVmss -ResourceGroupName $ResourceGroupName
$VmssName = $VmssInfo.Name

# Get generak info about VMs in VMSS.
$AllVMs = Get-AzureRmVmssVM -ResourceGroupName $ResourceGroupName -VMScaleSetName $VmssName

# Find VMs which are currently running.
[System.Collections.ArrayList]$InstanceIdsOfRunningVMs = @()
foreach ($InstanceId in $AllVMs.InstanceId) 
{
    $VmInstanceView = Get-AzureRmVmssVM -ResourceGroupName $ResourceGroupName -VMScaleSetName $VmssName -InstanceView $InstanceId
    if($VmInstanceView.Statuses[1].DisplayStatus -eq "VM Running"){
        $InstanceIdsOfRunningVMs += $InstanceId
    }
}

# Exit if there are 0 or 1 VM running in VMSS.
if($InstanceIdsOfRunningVMs.Count -eq 0)
{
    Write-Error "No running VMs were found in this VMSS"
    return
}
elseif($InstanceIdsOfRunningVMs.Count -eq 1)
{
    Write-Error "Found only one running VM in this VMSS, please add at least one more."
    return
}

# Calculate how often to kill a new VM.
$DurationBetweenKillingVmsInSeconds = $DurationOfLoadtestInMinutes * 60 / $InstanceIdsOfRunningVMs.Count
$BackgroundJobs = @()

# Script that kills the VMs in VMSS.
$KillScript={
    Param($ProfilePath,$ResourceGroupName,$VmssName,$IntanceIdToRemove )
    Select-AzureRmProfile -Path $ProfilePath
    Stop-AzureRmVmss -ResourceGroupName $ResourceGroupName -VMScaleSetName $VmssName -InstanceId $IntanceIdToRemove 
}

# Save Azure session, so that background threads can use it to kill the VMs in VMSS.
$ProfilePath = ".\profile.json"
Save-AzureRmProfile -Path $ProfilePath -Force

# Sleep for a calculated preriod of time and then kill a new randomly selected VM.
while($InstanceIdsOfRunningVMs.Count -ne 1)
{
    Write-Output "Waiting for $DurationBetweenKillingVmsInSeconds seconds"
    Start-Sleep -s $DurationBetweenKillingVmsInSeconds
    $IndexOfIntanceIdToRemove = Get-Random -Minimum 0 -Maximum $InstanceIdsOfRunningVMs.Count
    $IntanceIdToRemove = $InstanceIdsOfRunningVMs[$IndexOfIntanceIdToRemove]
    $InstanceIdsOfRunningVMs.RemoveAt($IndexOfIntanceIdToRemove)
    Write-Output "Deallocating $($AllVMs.Name[$AllVMs.InstanceId.IndexOf($IntanceIdToRemove.ToString())]) VM"
    # Kill VM on a background thread, so that it won't effect overall total run time of this script.
    $BackgroundJobs += Start-Job -scriptblock $KillScript -ArgumentList @($ProfilePath, $ResourceGroupName, $VmssName, $IntanceIdToRemove)
}

Wait-Job -Job $BackgroundJobs
