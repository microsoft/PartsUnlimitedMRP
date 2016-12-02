[CmdletBinding()]
Param(
    [Parameter(Mandatory=$True)] [string] $sshTarget,
	[Parameter(Mandatory=$True)] [string] $sshUser,
    [Parameter(Mandatory=$True)] [string] $sshPassword,
    [Parameter(Mandatory=$True)] [string] $sshKey
)

$ErrorActionPreference = "Stop"

# Get plink and pscp
$pscpExeUrl="https://the.earth.li/~sgtatham/putty/latest/x86/pscp.exe"
$plinkExeUrl="https://the.earth.li/~sgtatham/putty/latest/x86/plink.exe"

if (-not (Test-Path pscp.exe)) {
    wget $pscpExeUrl -OutFile pscp.exe
}
if (-not (Test-Path plink.exe)) {
    wget $plinkExeUrl -OutFile plink.exe
}

# Run tests
$plinkFile = "plink.txt"
$plinkContent = @'
cd bdd-security/
rm -rf build/reports/
rm -rf build/test-results/
./gradlew -Dcucumber.options="--tags @app_scan --tags ~@skip"
'@
Set-Content -Path $plinkFile -Value $plinkContent
& ./plink.exe $sshUser@$sshTarget -batch -hostkey $sshKey -pw $sshPassword -m $plinkFile

# Copy test results back to agent.
$currentExecutionLocation = (Get-Location).Path
& ./pscp.exe -batch -hostkey $sshKey -pw $sshPassword $sshUser@${sshTarget}:bdd-security/build/reports/junit/all_tests.xml `"$currentExecutionLocation`"
