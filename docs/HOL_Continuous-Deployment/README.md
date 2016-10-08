# Please visit [http://aka.ms/pumrplabs](http://aka.ms/pumrplabs)

We are now updating only the documentation here : [http://aka.ms/pumrplabs](http://aka.ms/pumrplabs)
====================================================================================

# HOL - Parts Unlimited MRP App Continuous Deployment with Visual Studio Team Services - Hosted #

In this lab, you will learn how to deploy the Parts Unlimited MRP App in an automated fashion onto a Linux VM using a hosted agent. After this lab, you will have a working, automated build in Visual Studio Online that will build, test, and deploy the Parts Unlimited MRP app to a Virtual Machine in Azure.

>**Note:** If you would like to trigger continuous deployments using a local agent installed on the VM instead of a hosted agent, see [this lab](https://github.com/Microsoft/PartsUnlimitedMRP/tree/master/docs/HOL_Continuous-Deployment-Using-Custom-Agent).  

### Video ###

You may watch a [demo in Channel 9](https://channel9.msdn.com/Series/Parts-Unlimited-MRP-Labs/Parts-Unlimited-MRP-App-Continuous-Deployment-with-Visual-Studio-Team-Services-Hosted-Agent) that walks through many of the steps in the document.

### Pre-requisites ###

- Completion of the Continuous Integration with Parts Unlimited MRP HOL
- An active Azure Subscription
- An active Visual Studio Team Services Account


### Tasks Overview ###

In this lab, you will work with one machine which will serve as both the deployment agent and the MRP server.

**Provision the lab:** Provision an MRP machine (Ubuntu VM) in Azure using an ARM template.

**Configure the release definition:** Configure a release definition in VSTS that picks up build artifacts and triggers whenever new artifacts are produced. 

**Trigger a build for continuous deployment:** Trigger a build for continuous deployment by making changes to code and automatically deploying the MRP app to the local agent.

### Task 1: Provision the Lab ###

1. Instead of manually creating the VM in Azure, we are going to use an Azure Resource Management (ARM) template. Simply click the **Deploy to Azure** button below and follow the wizard to deploy the machine. You will need to log in to the Azure Portal.
                                                                    
    <a href="https://portal.azure.com/#create/Microsoft.Template/uri/https%3A%2F%2Fraw.githubusercontent.com%2FMicrosoft%2FPartsUnlimitedMRP%2Fmaster%2Fdocs%2FHOL_Continuous-Deployment%2Fenv%2FContinuousDeploymentPartsUnlimitedMRP.json" target="_blank">
        <img src="http://azuredeploy.net/deploybutton.png"/>
    </a>
    <a href="http://armviz.io/#/?load=https%3A%2F%2Fraw.githubusercontent.com%2FMicrosoft%2FPartsUnlimitedMRP%2Fmaster%2Fdocs%2FHOL_Continuous-Deployment%2Fenv%2FContinuousDeploymentPartsUnlimitedMRP.json" target="_blank">
        <img src="http://armviz.io/visualizebutton.png"/>
    </a>

    The VMs will be deployed to a Resource Group along with a virtual network (VNET) and some other required resources. You can 
    delete the resource group in order to remove all the created resources at any time.

2. You will need to select a subscription and region to deploy the Resource Group to and supply an admin username, password, and unique name for the machine. The machine will be a Standard D1_V2.

    ![](<media/set_arm_parameters.png>)

    Make sure you make a note of the region as well as the username and password for the machine. Allow about 10 minutes for deployment and then another 10 minutes for the VSTS agent and MRP dependency configuration. 

3. When the deployment completes, you should see the following resources in the Azure Portal:

    ![](<media/post_deployment_rg.png>)

    Click on the Public IP Address for your deployment. Then make a note of the DNS name:

    ![](<media/public_ip_dns.png>)

    >**Note:** The lab requires several ports to be open, such as SSH ports and the Parts Unlimited MRP app port on the partsmrp machine. 
	The ARM template opens these ports on the machine for you.

### Task 2: Create release definition ###

1. At the homepage of the PartsUnlimitedMRP team project in Visual Studio Team Services, click on the **Release** tab in the upper-left corner of the page. Then, click the **New definition** button on the home page.

    ![](<media/new_release.png>)

2. In the **Create new release definition** dialog, choose an empty template then the OK button. 

    ![](<media/create_empty_definition.png>)

3. Keep the artifacts as **Build**, select the CI build definition that you used in the previous lab (such as "PartsUnlimited.CI"), check the checkbox to enable the **Continuous Deployment trigger**, and choose "Hosted" as the  **agent queue**.

    ![](<media/choose_source_queue_new_dialog.png>) 

4. Click on the **Environment** keyword and rename the environment to be **"Dev"**. Click on the pencil icon on the top of the definition and rename it to be PartsUnlimitedMRPCD. 

     ![](<media/change_environment_name.png>)

5. Click on the **Add tasks** button and add a PowerShell script task (under the Utility category). 

	 ![](<media/add_powershell_script.png>)

6. Point to the **SSH-MRP-Artifacts.ps1** build artifact as the script path in the task.

	 ![](<media/add_script_path.png>)

7. In the environment box, click on the ellipses ("...") and select **Configure variables...** option. 

     ![](<media/configure_variables.png>)

8. Create three variables: `sshUser`, `sshPassword`, and `sshTarget`. Fill in the values of the virtual machine that you created previously. `sshTarget` should be the public DNS name of the virtual machine, such as "mylinuxvm.westus.cloudapp.azure.com."

     ![](<media/fill_in_variable_values.png>)

9. In the PowerShell script task, add in the arguments with a hyphen and the variable name, followed by $(*variablename*). The arguments should look like `-sshUser $(sshUser) -sshPassword $(sshPassword) -sshTarget $(sshTarget)`. Click on the **pencil icon** above the task to rename it. 

     ![](<media/fill_in_arguments.png>)

11. Click on **Advanced** to extand the panel and untick the option : **Fail on Standard Error** to avoid some garbage warning.

    ![](<media/ssh_errors.png>)    

12. Click on the **Triggers** tab and set the artifact source by selecting the Build definition that you are created previously.

    ![](<media/vsts_CD.png>)
 
13. Save the release definition. 

### Task 3: Continuous Deployment ###

Now that our release definition is set up, let's test using Continuous Integration and Continuous Deployment. 

1. Navigate to the code tab and find the index.html page in src/Clients/Web/index.html. Make a change by clicking on the **Edit** button, then commit the change. 

	 ![](<media/commit_edited_code.png>)

2. Go to the **Build** tab and note the running build that was queued by the Continuous Integration trigger. 

	 ![](<media/completed_build.png>)

3. Return to the **Release** tab and note the running deployment that was queued by the Continuous Deployment trigger. 

	 ![](<media/completed_deployment.png>)

4. Verify your code change by navigating to the VM's public IP DNS name, such as `http://mylinuxvm.westus.cloudapp.azure.com:9080/mrp`.

Next steps
----------

In this lab, you learned how to create deployments automatically after committing changes to code and build automatically. Try these labs out for next steps:

-   [Parts Unlimited MRP Continuous Deployment - Agent](https://github.com/Microsoft/PartsUnlimitedMRP/tree/master/docs/HOL_Continuous-Deployment-Using-Custom-Agent)

-   [Deploying Parts Unlimited MRP with Chef to Azure](https://github.com/Microsoft/PartsUnlimitedMRP/tree/master/docs/HOL_Deploying-Using-Chef)

-   [Parts Unlimited MRP Application Performance Monitoring](https://github.com/Microsoft/PartsUnlimitedMRP/tree/master/docs/HOL_Application-Performance-Monitoring)

-	[Parts Unlimited MRP Auto-Scaling and Load Testing](https://github.com/Microsoft/PartsUnlimitedMRP/tree/master/docs/HOL_Autoscaling-Load-Testing)

# Continuous Feedbacks

#### Issues / Questions about this HOL ??

[If you are encountering some issues or questions during this Hands on Labs, please open an issue by clicking here](https://github.com/Microsoft/PartsUnlimitedMRP/issues)

Thanks
