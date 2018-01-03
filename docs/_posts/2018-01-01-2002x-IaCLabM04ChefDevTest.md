
---
layout: page
title: Chef Server Config and Dev Test Labs
category: IaC
order: 0
---

  
<h1></strong><span style="color: #0000CD;"> DevOps200.2x: Infrastructure as Code </span></strong></h1>

<h2><span style="color: #0000CD;"> Module 4 Labs: Deploying and Managing Environments in Azure  </span></h2>
 
 
Module 4: Azure IaaS and PaaS, Environment Configuration and Deployment, and Optimization 
 
  
<h3><span style="color: #0000CD;">Deploy a Chef server to Azure (Portal)</span></h3> 
This lab depends on the following components: 

- [Putty.exe](http://www.chiark.greenend.org.uk/~sgtatham/putty/latest.html)
- [Google Chrome](http://www.chiark.greenend.org.uk/~sgtatham/putty/latest.html)
 
 
1.	From the lab VM, start Internet Explorer and navigate to the Azure portal. When prompted, sign in with the Microsoft Account that is the Service Administrator of your Azure subscription. 
2.	Deploy a new Chef Server 2012, BYOL image from the Azure Marketplace by using the Azure Resource Manager deployment model with the following settings (leave all other settings at their defaults): 

- Host name: **chefsrv** 
- VM disk type: **HDD** 
- User name: **student** 
- Password: **Pa55w.rd1234** 
- Resource group: **DevOpsLab4aRG** 
- Location: **an Azure region close to the lab location** 
- VM size: **D2_V2** 
3.	Once the new Azure VM has been provisioned and is running, from the Azure portal, assign a unique public DNS name label to its public IP address.  Take a note of the resulting fully qualified DNS name.  
4.	From the lab VM, start putty.exe and connect from it to the Chef Server Azure VM. In the **Host name (or IP address)** text box, specify the fully qualified DNS name assigned to the 
Chef Server Azure VM. When prompted, in the **PuTTY Security Alert** dialog box, click **Yes**  
5.	Log on by using the credentials you specified when creating the Azure VM (**student** and **Pa55w.rd1234**)  
6.	Ensure that the Chef Server has completed its initial configuration by streaming the content of the **/var/log/cloud-init-output.log** file. To view changes in real time, run: 

```
tailf /var/log/cloud-init-output.log 
```
7.	Wait until the initial configuration completes. The final entries in the log should resemble the following:

```
Cloud-init v. 0.7.5 finished at Mon, 06 Mar 2017 23:00:55 +0000. Datasource DataSourceAzureNet [seed=/dev/sr0].  Up 983.02 seconds 
```

8.	To stop the **tailf** command, press **Ctrl+C**. Next, set the **FQDN** environment variable to the fully qualified DNS name of the Chef Server Azure VM by running the following: 
```
FQDN=’fully_qualified_domain_name’
```
 
where **’fully_qualified_domain_name’** represents the fully qualified DNS name of the Chef Server Azure VM. 
9.	Next, run the following commands to configure the Chef Server VM to be accessible via that FQDN: 
```
sudo rm /var/opt/opscode/nginx/etc/nginx.d/analytics.conf echo "api_fqdn '$FQDN'" | sudo tee -a /etc/chef-marketplace/marketplace.rb echo "analytics_fqdn '$FQDN'" | sudo tee -a /etc/opscode-analytics/opscodeanalytics.rb

sudo chef-marketplace-ctl hostname $FQDN sudo opscode-analytics-ctl reconfigure 

sudo chef-manage-ctl reconfigure 
```

10.	Once completed, from the lab VM, start Google Chrome and browse to 
**https:// 
’fully_qualified_domain_name’** where **’fully_qualified_domain_name’** represents the fully qualified DNS name of the Chef Server Azure VM. Click **ADVANCED** and then, click the link **Proceed to ’fully_qualified_domain_name’ (unsafe)** 
11.	When prompted, type the DNS name label (short name) you assigned to the public IP address of the Azure VM and click Submit 
12.	Click the link labeled **Click here to get started !**. 
 
*If you receive an error about Sign Up being disabled due to timeout, switch back to the PuTTY session and run the command:* 
```
Sudo chef-manage-ctl reconfigure
```

13.	On the **Welcome to Chef** page, fill out the form to create a Chef user account by providing the Full Name (your first and last name), Company name (cheflabs), Email (the email 
address of your Microsoft account), Username (student), and Password (Pa55w.rd1234) and 
selecting the **I agree to the Terms of Service and the Master License and Services Agreement** checkbox and click **Get Started**. 
14.	Next, click **Create New Organization**. 
15.	In the **Create Organization** dialog box, type in the Full Name (Chef Labs) and Short Name (cheflabs) of the new organization and click **Create Organization**.  
16.	Click the **Download Starter Kit** link and when prompted, click **Proceed**.  
17.	Click the downloaded file in the lower left corner of the Chrome window and click **Show in folder**. 
18.	Extract the content of the **chef-starter.zip** file into the **chef-starter** subfolder under the **Downloads** folder. 
19.	From the lab VM, start Command Prompt as Administrator, change the current directory to the **downloads\chef-starter\chef-repo\.chef** subfolder and run:
```
knife ssl fetch 
```

*This will place the certificates from the Azure VM in your trusted certificate store on the Azure VM.*
20.	Change the current directory to **chef-repo** and use **knife** to download the **learn_chef_iis** cookbook from the default repository by running:  
```
knife cookbook site download learn_chef_iis 
```
21.	Extract the content of the downloaded file by running:  
```
tar -zxvf learn_chef_iis-0.2.1.tar.gz -C ..\cookbooks 
```
This will automatically create a new subfolder named **leaern_chef.iis** under **chefstarter\chef-repo\cookbooks** 

22.	Upload the cookbook to the Chef Server Azure VM by running the following command from the chef-repo directory:  
```
knife cookbook upload learn_chef_iis 
```
23.	To verify that the upload has been successful, switch to the Google Chrome window and click the **Policy** tab in the Chef Server interface. You should see there an cookbook entry named learn_chef_iis. 
 
*At this point, you could deploy an Azure or an on-premises VM, configure it as a Chef client and deploy to it the newly uploaded cookbook. In our course, you will step through this process in the next exercise of this module. In the exercise, you will deploy an Azure VM by using an Azure DevTest lab.*
 
 
 
 
  
<h3><span style="color: #0000CD;">Use Chef to configure Azure DevTest Lab VMs (Portal)</span></h3>  
1.	From the Azure portal, click **New**. On the **New** blade, click **Developer tool** and then, on the **Developer tools** blade, click **DevTest Labs**.  
2.	From the Create a DevTest Lab blade, create a DevTest lab with the following settings: 

- Lab name: **DevTestLab1** 
- Subscription: **your Azure subscription** 
- Location: **an Azure region close to the lab location** 
- Auto-shutdown: **Disabled**

3.	In the Azure portal, navigate to DevTestLab1 and, on the DevTestLab1 blade, click **Configuration and policies**. 
4.	In the **POLICY SETTINGS** section, click **Allowed virtual machine sizes**. 
5.	Enable the Allowed virtual machine sizes setting and limit the size choice to **Standard D2_V2** only. 
6.	In the **POLICY SETTINGS** section, click **Virtual machines per user**. 
7.	Limit the number of virtual machines per user to 1. 
8.	In the **POLICY SETTINGS** section, click **Virtual machines per lab**. 
9.	Limit the number of virtual machines per lab to 5. 
10.	In the **SCHEDULES** section, click **Auto-shutdown**. 
11.	Set the auto-shutdown to take place at 7:00PM your local time. 
12.	In the **SCHEDULES** section, click **Auto-start**. 
13.	Set the auto-start to take place at 7:00AM your local time on weekdays. 
14.	In the **EXTERNAL RESOURCES** section, click **Virtual networks**. 
15.	Note that the lab includes an auto-generated virtual network. You can modify its configuration, if needed. You can also customize the default behavior, which automatically assigns to VMs a shared public IP address. 
16.	In the **EXTERNAL RESOURCES** section, click **Repositories**. 
17.	Note that the lab is pre-configured with a **Public Repo** referencing https://github.com/Azure/azure-devtestlab.git , which includes artifacts and Azure Resource Manager templates you can use for VM deployment. You have the option of creating your own repositories (you can start by cloning the Public Repo). 
18.	In the **VIRTUAL MACHINE BASES** section, click **Marketplace images**. 
19.	Note that you have the option of modifying the default configuration, which allows al Azure Marketplace images as virtual machine bases and instead limiting selection to specific images only.  
20.	In the **VIRTUAL MACHINE BASES** section, click **Custom images (VHDs)**. 
21.	Note that you have the option of uploading custom images for lab VM deployment. 
22.	Scroll back to the **DevTestLab1** blade and click **My secrets**
23.	Create a secret named **WS2016Password** with the value **Pa55w.rd1234**
24.	On the **DevTestLab1** blade, click **Formulas (reusable bases)**. 
25.	Click **Add** and, on the **Choose a base** blade, click **Windows Server 2016 Datacenter**. 
26.	On the **Create formula (reusable bases)** blade, specify the following: 
    - Formula name: **WS2016DatacenterLabVM**
    - Description: **Windows Server 2016 Datacenter Lab VM** 
    - User name: **student** 
    - Password: **WS2016Password** 
    - Disk and size: accept the default (this is enforced via the **Allowed virtual machine sizes** setting you chose earlier) 
    - Virtual machine size: accept the default (this is enforced via the **Allowed virtual machine sizes** setting you chose earlier) 
    - Artifacts: **Chef Client** 
    - Chef Node Name: **labVM** 
    - Chef Server URL: **https://’fully_qualified_domain_name’/organizations/cheflabs**  where **’fully_qualified_domain_name’** represents the fully qualified DNS name of the Chef Server Azure VM 
    - validation Client Name: **cheflabs-validator** 
    - validation Key: **browse to the Downloads\chef-starter\chef-
    repo\.chef\cheflabs-validator.pem)** 
    - Run List: **recipe[learn_chef_iis]**
    - Client Configuration File: **leave blank** 
    - SSL Verification Mode: **none**
    - Chef environment tag: **_default**
    - Encrypted databag secret: **leave blank**
    - Chef Server SSL Certificate: **leave blank** 
    - Advanced settings: **leave at their defaults** 
27.	In the **MY LAB** section, click **My virtual machines** and then click **Add**. 
28.	On the **Choose a base** blade, click **WS2016DataCenterLabVM** formula. 
29.	On the **Virtual machine** name, in the **Virtual machine name** textbox, type **labVM0**, accept all remaining settings with their default values and click **Artifacts.** 
30.	On the **Add artifacts** blade, click the link **1 artifact(s) selected**. 
31.	On the **Selected artifacts** blade, click **Chef Client.** 
32.	On the **Add artifact** blade, set the **Chef Node Name** value to **labVM0**, verify that the validation key is set to **Downloads\chef-starter\chef-repo\.chef\cheflabs-validator.pem**, and click **OK**.  
33.	On the **Selected artifacts** blade, click **OK**. 
34.	On the **Add artifacts** blade, click **OK**. 
35.	On the **Virtual machine** blade, click **Create**. 
36.	Wait for the lab VM to be provisioned. Switch to the Chrome window and click the **Nodes** tab to verify that a newly provisioned VM is configured as a Chef client. 
  
