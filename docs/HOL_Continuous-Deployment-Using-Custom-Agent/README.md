# HOL - Parts Unlimited MRP App Continuous Deployment with Visual Studio Team Services - Local Agent #

In this lab, you will learn how to deploy the Parts Unlimited MRP App in an automated fashion onto a local agent on a Linux VM. After this lab, you will have a working, automated build in Visual Studio Team Services that will build, test, and deploy the Parts Unlimited MRP app to a Virtual Machine in Azure.

>**Note:** If you would like to trigger continuous deployments using the hosted VSTS agent instead of a local agent, see [this lab](https://github.com/Microsoft/PartsUnlimitedMRP/tree/master/docs/HOL_Continuous-Deployment).  


###Pre-requisites###

- Completion of the Continuous Integration with Parts Unlimited MRP HOL
- An active Azure Subscription
- An active Visual Studio Team Services Account


### Tasks Overview ###

In this lab, you will work with one machine which will serve as both the deployment agent and the MRP server.

**Provision the lab:** Provision a VSTS agent and MRP machine (Ubuntu VM) in Azure using an ARM template.

**Configure the release definition:** Configure a release definition in VSTS that picks up build artifacts and triggers whenever new artifacts are produced. 

**Trigger a build for continuous deployment:** Trigger a build for continuous deployment by making changes to code and automatically deploying the MRP app to the local agent.

### Task 1: Provision the Lab ###

1. Create the MRP agent pool in Visual Studio Team Services if you do not have one already. Go to the homepage of your VSTS account or the PartsUnlimitedMRP team project and clicking on the gear icon in the upper-right corner of the homepage.

	![](<media/vsts_gear_icon.png>)

2. Then, click on the **Agent Pool** tab and click on **New pool...** to create a pool called "MRP." Keep the checkbox to "Auto-provision Queues in all Projects" checked.

    ![](<media/create_agent_pool.png>) 

3. Instead of manually creating the VM in Azure, we are going to use an Azure Resource Management (ARM) template. Simply click the **Deploy to Azure** button below and follow the wizard to deploy the machine. You will need to log in to the Azure Portal.
                                                                    
    <a href="https://portal.azure.com/#create/Microsoft.Template/uri/https%3A%2F%2Fraw.githubusercontent.com%2FMicrosoft%2FPartsUnlimitedMRP%2Fmaster%2Fdocs%2FHOL_Continuous-Deployment-Using-Custom-Agent%2Fenv%2FContinuousDeploymentCustomAgentPartsUnlimitedMRP.json" target="_blank">
        <img src="http://azuredeploy.net/deploybutton.png"/>
    </a>
    <a href="http://armviz.io/#/?load=https%3A%2F%2Fraw.githubusercontent.com%2FMicrosoft%2FPartsUnlimitedMRP%2Fmaster%2Fdocs%2FHOL_Continuous-Deployment-Using-Custom-Agent%2Fenv%2FContinuousDeploymentCustomAgentPartsUnlimitedMRP.json" target="_blank">
        <img src="http://armviz.io/visualizebutton.png"/>
    </a>

    The VMs will be deployed to a Resource Group along with a virtual network (VNET) and some other required resources. You can 
    delete the resource group in order to remove all the created resources at any time.

4. You will need to select a subscription and region to deploy the Resource Group to and supply an admin username, password, and unique name for the machine. The machine will be a Standard A2.

    ![](<media/set_arm_parameters.png>)

    Make sure you make a note of the region as well as the username and password for the machine. Allow about 10 minutes for deployment and then another 10 minutes for the VSTS agent and MRP dependency configuration. 

5. You will also need to specify the VSTS account to use (the DNS name before *visualstudio.com*) and a personal access token. If you don't have a personal access token, follow [this link](https://www.visualstudio.com/en-us/docs/setup-admin/team-services/use-personal-access-tokens-to-authenticate) to create one.

6. When the deployment completes, you should see the following resources in the Azure Portal:

    ![](<media/post_deployment_rg.png>)

    Click on the "partsmrp" Public IP Address. Then make a note of the DNS name:

    ![](<media/public_ip_dns.png>)

    >**Note:** The lab requires several ports to be open, such as SSH ports and the Parts Unlimited MRP app port on the partsmrp machine. 
	The ARM template opens these ports on the machine for you.

### Task 2: Create release definition ###

1. At the homepage of the PartsUnlimitedMRP team project in Visual Studio Team Services, click on the **Release** tab in the upper-left corner of the page. Then, click the **+** button on the left and choose **Create new release definition**.

    ![](<media/create_release_definition.png>)

2. In the **Create new release definition** dialog, choose an empty template then the OK button. 

    ![](<media/create_empty_definition.png>)

3. Keep the artifacts as **Build**, select the CI build definition that you used in the previous lab (such as "PartsUnlimited.CI"), check the checkbox to enable the **Continuous Deployment trigger**, and choose "MRP" as the  **agent queue**.

    ![](<media/choose_source_queue_new_dialog.png>) 

4. Click on the **Environment** keyword and rename the environment to be "Dev." Click on the pencil icon on the top of the definition and rename it to be PartsUnlimitedMRP.CD. 

     ![](<media/change_environment_name.png>)

5. Click on the **Add tasks** button and add a shell script task (under the Utility category). 

	 ![](<media/add_shell_script.png>)

6. Point to the **Deploy-MRP-App.sh** build artifact as the script path in the task. Then save the release definition. 

	 ![](<media/add_script_path.png>)
 
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

-   [Parts Unlimited MRP Continuous Deployment - Hosted](https://github.com/Microsoft/PartsUnlimitedMRP/tree/master/docs/HOL_Continuous-Deployment)

-   [Deploying Parts Unlimited MRP with Chef to Azure]

-   [Parts Unlimited MRP Application Performance Monitoring](https://github.com/Microsoft/PartsUnlimitedMRP/tree/master/docs/HOL_Application-Performance-Monitoring)

-	[Parts Unlimited MRP Auto-Scaling and Load Testing](https://github.com/Microsoft/PartsUnlimitedMRP/tree/master/docs/HOL_Autoscaling-Load-Testing)