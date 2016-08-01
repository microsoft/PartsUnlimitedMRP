
In this hands-on lab you will explore some of the new features and capabilities of Deploying MRP App via Chef Server in Azure. This hands-on lab is designed to point out new features, discuss and describe them, and enable you to understand and explain these features to customers as part of the DevOps Lifecycle. 

**Prerequisites**
- PartsUnlimitedMRP deployed to an Azure Linux Virtual Machine (see [https://raw.githubusercontent.com/Microsoft/PartsUnlimitedMRP/master/docs/Build-MRP-App-Linux.md](https://raw.githubusercontent.com/Microsoft/PartsUnlimitedMRP/master/docs/Build-MRP-App-Linux.md))

**Tasks**

1. Provision the Lab (Chef Server and Chef Workstation)
2. Configure Chef Workstation
3. Create a Cookbook
4. Create a Role
5. Configure Chef-Provisioning Azure
6. Provision and Deploy the Application
7. Remediating Configuration Changes

## Task 1: Provision the Lab

1. Provision the Lab machines using an Azure Resource Manager (ARM) Template

    This lab initally calls for the use of two machines. The Chef server must be a Linux machine, but the Chef
    Workstation can run on Linux, Windows, or Mac. For this lab, the Chef Workstation will be on a Windows machine.

    Instead of manually creating the VMs in Azure, we are going to use an Azure Resource Management (ARM) template.
    
1. Click on the "Deploy to Azure" button
    
    Simply click the Deploy to Azure button below and follow the wizard to deploy the two machines. You will need
    to log in to the Azure Portal.
                                                                     
	<a href="https://portal.azure.com/#create/Microsoft.Template/uri/https%3A%2F%2Fraw.githubusercontent.com%2Fnwcadence%2FPartsUnlimitedMRP%2FHOL_Deploying-Using-Chef%2Fdocs%2FHOL_Deploying-Using-Chef%2Fenv%2FChefPartsUnlimitedMRP.json" target="_blank">
		<img src="http://azuredeploy.net/deploybutton.png"/>
	</a>
	<a href="http://armviz.io/#/?load=https%3A%2F%2Fraw.githubusercontent.com%2Fnwcadence%2FPartsUnlimitedMRP%2FHOL_Deploying-Using-Chef%2Fdocs%2FHOL_Deploying-Using-Chef%2Fenv%2FChefPartsUnlimitedMRP.json" target="_blank">
		<img src="http://armviz.io/visualizebutton.png"/>
	</a>

    The VMs will be deployed to a Resource Group along with a virtual network (VNET) and some other required resources. You can 
    delete the resource group in order to remove all the created resources at any time.

1. Specify settings for the deployment
    
    You will need to select a subscription and region to deploy the Resource Group to and to supply an admin username 
    and password and unique name for both machines.

    ![](<media/1.jpg>)

    Make sure you make a note of the region as well as the usernames and passwords for the machines. Allow
    about 10 minutes for deployment and then another 10 minutes for the Chef configuration. 

1. Check the Resource Group in the Azure Portal
    When the deployment completes, you should see the following resources in the Azure Portal:

    ![](<media/2.jpg>)

    Click on the "chefserver" Public IP Address. Then make a note of the DNS name:

    ![](<media/3.jpg>)

    The _dnsaddres_ will be of the form _machinename_._region_.cloudapp.azure.com. Open a browser to https://_dnsaddress_.
    (Make sure you're going to http__s__, not http). You will be prompted about an invalid certificate - it is safe to
    ignore this for the purposes of this lab. If the Puppet configuration has succeeded, you should see the Chef web page:

    ![](<media/4.jpg>)

    >**Note:** The lab requires several ports to be open, such as the Chef Server port, the Chef web page port, and SSH
    ports. The ARM template opens these ports on the machines for you.

1. Log in to the Chef Web Page

    Now go back to the Chef web page in your browser and enter the username and the password you set. 
    When you log in, you should see a page like this:

    ![](<media/6.jpg>)


###Task 3: Configuring the Chef Workstation
In this exercise, you will configure your Chef Workstation.

**Step 1.** Verify the Chef Development Kit install
	Open the Chef Development Kit console (you should have a desktop shortcut for it)
	Run the command:  chef verify

**Step 2.** Download the Chef Starter Kit

    Login to the Chef Web Site
	Go to the Administration tab
	Select the "partsunlimited" organization
	Click Starter Kit on the left hand side
	Click Download Starter Kit

**Step 3.** Extract the Chef Starter Kit

	Extract the Chef starter kit files to a directory like C:\Users\<username>\chef\

**Step 4.** Fix the Chef Server URL
	
	Open the knife.rb file in chef-repo\.chef
	Change the chef_server_url to the external FQDN (i.e. https://<chef-server-dns-name>.<region>.cloudapp.azure.com)
	Save and close the file

**Step 5.** Initialize Git and add the initial files. 
	Change directories to the chef-repo directory (i.e. cd chef-repo)
    git init
    git add .
    git commit –m 'initial commit'
    cd ..

**Step 6.** Run the knife ssl fetch command:
    
    knife ssl fetch

 Our Chef server has an SSL certificate that is not trusted. As a result, we have to manually trust the SSL certificate in order to have our workstation communicate with the Chef server. This can also be addressed by importing a valid SSL certificate for Chef to use.

**Step 7.** View the current chef-repo contents.

    ls chef-repo

**Step 8.** Synchronize the Chef repo.

    knife download /

**Step 9.** Run the **ls** command from Step 1 again, and observe that additional files and folders have been created in the chef-repo directory. 

###Task 4: Create a Cookbook
In this exercise, you will create a cookbook to automate the installation of the MRP application and upload it to the Chef server.

**Step 1.** Use the knife tool to generate a cookbook template. 

    knife cookbook create mrpapp

 A cookbook is a set of tasks for configuring an application or feature. It defines a scenario and everything required to support that scenario. Within a cookbook, there are a series of recipes that define a set of actions to perform. Cookbooks and recipes are written in the Ruby language.

This creates an “mrpapp” directory in the chef-repo/cookbooks/ directory that contains all of the boilerplate code that defines a cookbook and a default recipe.

**Step 2.** Edit the metadata.rb file in our cookbook directory.
   
    Open chef-repo/cookbooks/mrpapp/metadata.rb for edit
 
Cookbooks and recipes can leverage other cookbooks and recipes. Our cookbook will use a pre-existing recipe for managing APT repositories.

**Step 3.** Add the following line at the end of the file:

    depends 'apt'

**Step 4.** The file should look like this:
    
    name 'mrpapp'
    maintainer   'YOUR_COMPANY_NAME'
    maintainer_email 'YOUR_EMAIL'
    license  'All rights reserved'
    description  'Installs/Configures mrpapp'
    long_description IO.read(File.join(File.dirname(__FILE__), 'README.md'))
    version  '0.1.0'
    depends 'apt'

**Step 5.** Save and close the file.

**Step 6.** Install the apt cookbook. 

    knife cookbook site install apt

 We need to install two dependencies for our recipe: the apt cookbook, and the chef-client cookbook. This can be accomplished using the knife cookbook site command, which will download the cookbooks from the official Chef cookbook repository, [https://supermarket.chef.io/cookbooks](https://supermarket.chef.io/cookbooks).

**Step 7.** Install the chef-client cookbook.

    knife cookbook site install chef-client

**Step 8.** Copy the full contents of the recipe from here: [https://raw.githubusercontent.com/Microsoft/PartsUnlimitedMRP/master/deploy/Chef/cookbooks/mrpapp-idempotent/recipes/default.rb](https://raw.githubusercontent.com/Microsoft/PartsUnlimitedMRP/master/deploy/Chef/cookbooks/mrpapp-idempotent/recipes/default.rb).

**Step 9.** Open recipe in text editor. 

    Open chef-repo/cookbooks/mrpapp/recipes/default.rb for edit

**Step 11.** The file should look like this to start: 

    ↪	#
    ↪	# Cookbook Name:: mrpapp
    ↪	# Recipe:: default
    ↪	Cd site insta#
    ↪	# Copyright 2015, YOUR_COMPANY_NAME
    ↪	#
    ↪	# All rights reserved - Do Not Redistribute
    ↪	#
    
**Step 12.** Paste the contents of the recipe into the mrpapp recipe file.

**Step 13.** Save the and close the file.

**Step 15.** *The following explains what the recipe is doing to provision the application.*

The first thing the recipe will do will be to run the 'apt' resource – this will cause our recipe to execute 'apt-get update' prior to running, to make sure the package sources on the machine are up-to-date.

    ↪	# Runs apt-get update
    ↪	include_recipe "apt"

Now we add an apt_repository resource to make sure that the OpenJDK repository is part of our apt repository list and up-to-date.
    
    ↪	# Add the Open JDK apt repo
    ↪	apt_repository 'openJDK' do
    ↪		uri 'ppa:openjdk-r/ppa'
    ↪		distribution 'trusty'
    ↪	end

Next, we will use the apt-package recipe to ensure that the OpenJDK and OpenJRE are installed. 

    ↪	# Install JDK and JRE
    ↪	apt_package 'openjdk-8-jdk' do
    ↪		action :install
    ↪	end
    ↪	
    ↪	apt_package 'openjdk-8-jre' do
    ↪		action :install
    ↪	end

Next, we set the JAVA_HOME and PATH environment variables to reference OpenJDK.

    ↪	# Set Java environment variables
    ↪	ENV['JAVA_HOME'] = "/usr/lib/jvm/java-8-openjdk-amd64"
    ↪	ENV['PATH'] = "#{ENV['PATH']}:/usr/lib/jvm/java-8-openjdk-amd64/bin"

Next, we'll install the MongoDB database engine and Tomcat web server.

    ↪	# Install MongoDB
    ↪	apt_package 'mongodb' do
    ↪		action :install
    ↪	end
    ↪	
    ↪	# Install Tomcat 7
    ↪	apt_package 'tomcat7' do
    ↪		action :install
    ↪	end

At this point, all of our dependencies will be installed, so we can start configuring the applications. First, we need to ensure that our MongoDB database has some baseline data in it. The remote_file resource will download a file to a specified location. It's idempotent – if the file on the server has the same checksum as the local file, it won't take any action! This also uses the "notifies" command – if the resource runs (e.g. there's a new version of the file), it sends a notification to the specified resource, telling it to run.

    ↪	# Load MongoDB data 
    ↪	remote_file 'mongodb_data' do
    ↪		source 'https://github.com/Microsoft/PartsUnlimitedMRP/tree/master/deploy/MongoRecords.js'
    ↪		path './MongoRecords.js'
    ↪		action :create
    ↪		notifies :run, "script[mongodb_import]", :immediately
    ↪	end

Now we use a "script" resource to define what command line script should be executed to load the MongoDB data we downloaded in the previous step. This resource has its "action" set to "nothing" – this means it won't run on its own. The only time this resource will run is when it's notified by the remote_file resource we used in the previous step. So every time a new version of the MongoRecord.js file is uploaded, the recipe will download it and import it. If the MongoRecords.js file doesn't change, nothing is downloaded or imported!

    ↪	script 'mongodb_import' do
    ↪		interpreter "bash"
    ↪		action :nothing
    ↪		code "mongo ordering MongoRecords.js"
    ↪	end

Next, we need to set the port that Tomcat will run our MRP application on. This uses a script resource to invoke a regular expression to update the /etc/tomcat7/server.xml file.
The "not_if" action is a guard statement – if the code in the "not_if" action returns true, the resource won't execute. This lets us make sure the script will only run if it needs to run.
Another thing to note: We are referencing an attribute called #{node['tomcat']['mrp_port']}. We haven't defined this value yet, but we will in the next exercise! With attributes, you can set variables, so the MRP application can run on one port on one server, or a different port on a different server.
If the port changes, you see that it uses "notifies" to invoke a service restart.

    ↪	# Set tomcat port 
    ↪	script 'tomcat_port' do 
    ↪		interpreter "bash"
    ↪		code "sed -i 's/Connector port=\".*\" protocol=\"HTTP\\/1.1\"$/Connector port=\"#{node['tomcat']['mrp_port']}\" protocol=\"HTTP\\/1.1\"/g' /etc/tomcat7/server.xml"
    ↪		not_if "grep 'Connector port=\"#{node['tomcat']['mrp_port']}\" protocol=\"HTTP/1.1\"$' /etc/tomcat7/server.xml"
    ↪		notifies :restart, "service[tomcat7]", :immediately
    ↪	end

Now we can download the MRP application and start running it in Tomcat. If we get a new version, it signals the Tomcat service to restart.

    ↪	# Install the MRP app, restart the Tomcat service if necessary
    ↪	remote_file 'mrp_app' do
    ↪		source 'https://github.com/Microsoft/PartsUnlimitedMRP/tree/master/builds/mrp.war'
    ↪		action :create
    ↪		notifies :restart, "service[tomcat7]", :immediately
    ↪	end

Now we can download the MRP application and start running it in Tomcat. If we get a new version, it signals the Tomcat service to restart.

    ↪	# Install the MRP app, restart the Tomcat service if necessary
    ↪	remote_file 'mrp_app' do
    ↪		source 'https://github.com/Microsoft/PartsUnlimitedMRP/tree/master/builds/mrp.war'
    ↪		path '/var/lib/tomcat7/webapps/mrp.war'
    ↪		action :create
    ↪		notifies :restart, "service[tomcat7]", :immediately
    ↪	end

We can define the Tomcat servce's desired state, which is "running". This will cause the script to check the Tomcat service, and start it if it isn't running. We can also signal this resource to "restart" with "notifies" (see above).

    ↪	# Ensure Tomcat is running
    ↪	service 'tomcat7' do
    ↪		action :start
    ↪	end

Finally, we can make sure the ordering service is running. This uses a combination of remote_file and script resources to check if the ordering service needs to be killed and restarted, or if it's not running at all when it should be. The end result of this is that the ordering service will always be up and running.

    ↪	remote_file 'ordering_service' do
    ↪		source 'https://github.com/Microsoft/PartsUnlimitedMRP/tree/master/builds/ordering-service-0.1.0.jar'
    ↪		path './ordering-service-0.1.0.jar'
    ↪		action :create
    ↪		notifies :run, "script[stop_ordering_service]", :immediately
    ↪	end
    ↪	
    ↪	# Kill the ordering service
    ↪	script 'stop_ordering_service' do
    ↪		interpreter "bash"
    ↪	# Only run when notifed
    ↪		action :nothing
    ↪		code "pkill -f ordering-service"
    ↪		only_if "pgrep -f ordering-service"
    ↪	end
    ↪	
    ↪	# Start the ordering service. 
    ↪	script 'start_ordering_service' do
    ↪		interpreter "bash"
    ↪		code "/usr/lib/jvm/java-8-openjdk-amd64/bin/java -jar ordering-service-0.1.0.jar &"
    ↪		not_if "pgrep -f ordering-service"
    ↪	end

**Step 16.** Now that the recipe is written, we can upload the cookbooks to the Chef server. From the command line, run: 

    knife cookbook upload mrpapp --include-dependencies
    knife cookbook upload chef-client --include-dependencies

Now that we have a recipe created and all of the dependencies installed, we can upload our cookbooks and recipes to the Chef server with the knife upload command.

###Task 5: Create a Role
In this exercise, you will use the Chef Console to create a role to define a baseline set of cookbooks and attributes that can be applied to multiple servers. 

At the start of this task, you should be logged in to the Chef Console in a web browser. 

**Step 1.** Click on the "Policy" tab.

**Step 2.** Click on the "Roles" tab.

**Step 3.** Click the "Create" button. 

**Step 4.** Enter the role name *mrp*.

**Step 5.** Click **Next**.

**Step 6.** Under **Available Recipes**, find the *mrpapp* recipe.

A run list is a series of recipes to apply. We're defining a role that can be applied to as many servers as we want that will run the MRP application.

**Step 7.** Drag the *mrpapp* recipe to the **Current Run List** box.

**Step 8.** Repeat for the **chef-client::service** recipe.

**Step 9.** The run list should be:
    
	1.	mrpapp
	2.	chef-client::service

**Step 10.** Click **Next**.

**Step 11.** In the **Default Attributes** box, paste the text: 

    {
      "tomcat": {
    	"mrp_port": 9080
      }
    }

In the previous exercise, we referenced an attribute called ['tomcat']['mrp_port'] in our recipe. This was referencing a JSON object. Now we can define default value to provide.

**Step 12.** Click **Next**.

**Step 13.** Paste the following JSON in the **Override Attributes** box:

    {
      "chef_client": {
    	"interval": "60",
    	"splay": "1"
      }
    }

The second recipe we added to the run list was chef-client:: service. This recipe ensure that the Chef client will run on a regular basis to ensure that the environment is in sync with what is defined in our recipe. However, the default value for the chef client service is to sync every 30 minutes. We can override that value here and set it to a more frequent interval.

**Step 14.** Click **Create Role**.

###Task 6: Configure Chef-Provisioning Azure
In this exercise, you will configure your Chef Workstation to use the Chef Provisioning Azure tool in order to automatically create and bootstrap a virtual machine running the MRP application.

**Step 1.** Install chef-provisioning-azure: 

    chef gem install chef-provisioning-azure

The next goal is to set our Chef workstation up so that we can run a script that will provision a new Azure virtual machine, register the VM with Chef, assign a role to the VM, and automatically run the recipes assigned to that role. To do that, we'll use a Chef tool called chef-provisioning-azure

**Step 2.** Open a web browser to this link: [https://manage.windowsazure.com/publishsettings/index?client=xplat](https://manage.windowsazure.com/publishsettings/index?client=xplat) and login with your Azure Microsoft ID credentials.

For this task, we'll use the Azure cross-platform command line in order to download an Azure Publish Settings file to our local workstation. We'll need the contents of this in order to set up chef-provisioning-azure. 

**Step 3.** Save the *.publishsettings file to your local hard drive.

**Step 4.** Open the publishsettings file you saved in the previous step and do not close it. 
It should look like this:
    
    ↪	<?xml version="1.0" encoding="utf-8"?>
    ↪	<PublishData>
    ↪	  <PublishProfile
    ↪		SchemaVersion="2.0"
    ↪		PublishMethod="AzureServiceManagementAPI">
    ↪		<Subscription>
    ↪	  		ServiceManagementUrl="https://management.core.windows.net"
    ↪	  		Id="00000000-1111-2222-3333-444444444444"
    ↪	  		Name="Visual Studio Ultimate with MSDN"
    ↪	  		ManagementCertificate="MIIverylongstring=="/>
	↪		</Subscription>
    ↪	  </PublishProfile>
    ↪	</PublishData>

**Step 6.** Make note of the values of the *Id* and *ManagementCertificate* values – you'll need both of them for the next task.

**Step 7.** Make a directory named *.azure*:

    mkdir .azure

**Step 8.** Create a new file called *cert.pfx* in the *.azure* directory: 

    nano ~/.azure/cert.pfx

**Step 9.** On your workstation: Copy just the contents of the certificate characters of the *ManagementCertificate* element from the publish settings file:

![](<media/task6-step9.png>)

**Step 10.** On your workstation: paste the contents into the Putty/SSH window where you have the *cert.pfx* open. Save the file with `Ctrl-o` and **Enter**.

**Step 11.** Exit nano with `Ctrl-x`.

**Step 12.** Create a new file called *config* in the *.azure* directory: 

    nano ~/.azure/config

**Step 13.** Copy these contents into the *config* file with your own subscription ID (which should be the value of the *Id* element from the publishsettings file):

    [default]
    management_certificate = "/home/labuser/.azure/cert.pfx"
    subscription_id = "{subscription id}"

**Step 14.** Save the file with `Ctrl-o` and **Enter** and exit nano with `Ctrl-x`.

###Task 7: Provision and Deploy the Application
In this exercise, you will create a script to automatically provision your Azure server and assign the MRP application role to that server.

**Step 1.** Use nano to create a new file called *provision.rb*.

    nano provision.rb

We will write a Ruby script to provision the environment, install the Chef client, and assign the mrp role.

**Step 2.** Paste the following code into *provision.rb*: 

    require 'chef/provisioning/azure_driver'
    
    with_chef_server "https://{chef server name}.cloudapp.net/organizations/fabrikam",
    :client_name => Chef::Config[:node_name],
    :signing_key_filename => Chef::Config[:client_key]
    with_driver 'azure'
    
    machine_options = {
     :bootstrap_options => {
     :cloud_service_name => '{cloud service name}',
     :storage_account_name => '{storage account name}',
     :vm_size => "Medium",
     :location => 'West US',
     :tcp_endpoints => '9080:9080,8080:8080'
    
     },
     :image_id => 'b39f27a8b8c64d52b05eac6a62ebad85__Ubuntu-14_04_2_LTS-amd64-server-20150309-en-us-30GB',
     :password => "mrpPassw0rd",
     :convergence_options => { ssl_verify_mode: :verify_none }
    
    }
    
    machine '{machine name}' do
     machine_options machine_options
     role 'mrp'
    end

Fill in the following values:

-	 *{chef server name}* – the name of your Chef server
-	*{cloud service name}* – This must be unique. This will be the URL that you navigate to in order to look at the running MRP application. For example, if you name it "mycloudservice", you the MRP application will run under mycloudservice.cloudapp.net. If it does not exist, it will be created.
-	*{storage account name}* – An all lowercase, alphanumeric name for the storage account to use for this VM. If it does not exist, it will be created.
-	*{machine name}* – Any name for your virtual machine. If the VM does not exist, it will be created.
This script represents a desired state for your Azure environment: You are declaring that you want a specific configuration of Cloud Services, storage accounts, TCP endpoints, and that you want a machine to exist that has the role of "mrp".

**Step 3.** Use chef-client to invoke the provisioning script: 

    ↪	chef-client –z provision.rb

**Step 4.** The script will take approximately 15 minutes to run. You will see it do the following things:
-	Create the Azure requirements (cloud service, storage account, VM)
-	Install Chef on the VM
-	Assign the *mrp* Chef role to the VM and execute the *mrpapp* recipe.

Once the deployment is complete, you should be able to navigate to the MRP application website and use it normally.

**Step 5.** Open the URL you chose for your cloud service name in a browser. The URL should be something like [http://mycloudservice.cloudapp.net:9080/mrp](http://mycloudservice.cloudapp.net:9080/mrp). 

![](<media/task7-step5.png>)

**Step 6.** Click around the site and observe that it functions normally.

###Task 8: Remediating Configuration Changes

In this exercise, you will make a change to the configuration of your MRP application server, then observe as Chef automatically corrects the issue.

**Step 1.** Start PuTTY.exe (or other SSH client) and enter the host name of the Cloud Service created in Exercise 7 (which website you also just visited) under **Host Name (or IP address)**. Then click **Open**. 

**Step 2.** When prompted for a user name, enter the user name *ubuntu* and press **Enter**.

**Step 3.** When prompted for a password, enter the *mrpPassw0rd* and press **Enter**.

**Step 4.** Wait for the command prompt to appear.

**Step 5.** On your workstation, open a new In Private / In Cognito browser session.

**Step 6.** In PuTTY on the MRP Server, execute the following command to stop the Tomcat service:

    ↪	sudo service tomcat7 stop

**Step 7.** On your workstation, go to the URL of the MRP website on your Private browser session and observe that it is no longer accessible. 

**Step 8.** Go to the Chef Console in your web browser on your workstation and click on the **Reports** tab. 
This will take you to the dashboard where you can see statistics about your deployments.

**Step 9.** Click **Run History**.

**Step 10.** Observe that the node has a first successful run that executed 24/43 resources, and possibly additional runs that executed 0/37 resources. This is because the chef client installed on the server runs every 60 seconds and checks for environmental discrepancies. You should see a run occur within a minute that shows 1/35 resources executed. 

**Step 11.** Reload the MRP application site, and you should see the site successfully load.

In this hands-on lab you explored some of the new features and capabilities of Deploying MRP App via Chef Server in Azure. This hands-on lab was designed to point out new features, discuss and describe them, and enable you to understand and explain these features to customers as part of the DevOps Lifecycle.
