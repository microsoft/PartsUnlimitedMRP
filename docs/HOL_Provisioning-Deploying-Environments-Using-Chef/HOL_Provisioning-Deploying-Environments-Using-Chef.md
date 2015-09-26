
In this hands-on lab you will explore some of the new features and capabilities of Deploying MRP App via Chef Server in Azure. This hands-on lab is designed to point out new features, discuss and describe them, and enable you to understand and explain these features to customers as part of the DevOps Lifecycle. 

**Prerequisites**

- An SSH client such as PuTTY

- PartsUnlimitedMRP deployed to an Azure Linux Virtual Machine (see [https://raw.githubusercontent.com/Microsoft/PartsUnlimitedMRP/master/docs/Build-MRP-App-Linux.md](https://raw.githubusercontent.com/Microsoft/PartsUnlimitedMRP/master/docs/Build-MRP-App-Linux.md))

**Tasks**

1. Provisioning a Chef Server
2. Configuring the Chef Server
3. Configuring the Chef Workstation
4. Create a Cookbook
5. Create a Role
6. Configure Chef-Provisioning Azure
7. Provision and Deploy the Application
8. Remediating Configuration Changes

###Task 1: Provisioning a Chef Server
In this exercise, you will provision a new Chef Server in Azure from a template image.

**Step 1.** Navigate to [http://portal.azure.com](http://portal.azure.com) and log in. 

**Step 2.** To create a new Chef server, click on the **New** tile, followed by **Marketplace**. In the Marketplace panel, choose **Everything** and click on the **Chef Server 12** tile. If the Chef tile doesn't appear, search for it in the search text field. 

![](<media/task1-step2.png>)

**Step 3.** After clicking on the Chef Server tile, choose the Classic deployment model. 

**Step 4.** When prompted for the host name, enter a globally unique name (which will also be used as your cloud service name). Enter *labuser* for the username and *labPassw0rd* (or other secure password) for the password. Choose A3 Standard (4 cores, 7 GB memory) for the VM size. Lastly, click **Create** and **buy** to start provisioning your Chef Server.

![](<media/task1-step4.png>) 

###Task 2: Configuring the Chef Server
In this exercise, you will connect to the Chef Server via SSH and configure it.

**Step 1.** Start PuTTY.exe or an SSH client.

**Step 2.** Enter the Cloud Service name created in task 1, step 4 (e.g. *myhostname.cloudapp.net*). Then click **Open**.

**Step 3.** When prompted for a username, enter the username that you chose in exercise 1 (default is *labuser*)and press **Enter**.

**Step 4.** When prompted for a password, enter the password you chose during the VM creation in exercise 1 (default is *labPassw0rd*) and press **Enter**.

**Step 5.** Wait for the command prompt to appear.

**Step 6.** From the command prompt run the **chef-setup** utility to create a user account in the Chef Server. 

    sudo chef-setup -u labuser -p labPassw0rd

**Step 7.** 2.	It will ask you if you agree to the license terms. If you agree, type: `Yes`. This will create a new user with the account name of *labuser* and the password of *labPassw0rd*.

**Step 8.** From the command prompt, run the **chef-server-ctl** command to create an organization named *fabrikam* and add your *labuser* account as a member. 
This will also generate a new SSH key for the organization, which you will capture to a file for later use.

    sudo chef-server-ctl org-create fabrikam fabrikam -a labuser > fabrikam-validator.pem
 	
If you decide you want to name your organization something other than *fabrikam*, you’ll need to make sure the name matches regular expression `/^[a-z0-9\-_]+$/`.

**Step 9.** Open a web browser and navigate to the Chef Server URL (the same as the cloud service name) such as [http://yourname.cloudapp.net](http://yourname.cloudapp.net). 

**Step 10.** Enter *labuser* as the user name. Enter *labPassw0rd* as the password. Click **Sign In**. Confirm that you are successfully logged in and taken to the Chef Manage console.

###Task 3: Configuring the Chef Workstation
In this exercise, you will configure your Chef Server as a Chef Workstation.

**Step 1.** Use the apt-get command to install the Git client tools. 

    sudo apt-get -y install git

**Step 2.** Use the wget command to download the ChefDK package.

    wget https://opscode-omnibus-packages.s3.amazonaws.com/ubuntu/12.04/x86_64/chefdk_0.4.0-1_amd64.deb

The ChefDK can be installed on any workstation across a variety of operating systems and configured to work with a Chef server. For this lab, we are using the Chef Server as our workstation to make things easier and faster for a lab.

**Step 3.** Use the dpkg command to install the ChefDK. This may take a few minutes to install.

    sudo dpkg --install ./chefdk_0.4.0-1_amd64.deb

**Step 4.** Remove the downloaded ChefDK installer.

    sudo rm ./chefdk_0.4.0-1_amd64.deb

**Step 5.** Once finished, verify the ChefDK installation. You should see that all of the components succeeded.

    chef verify

**Step 6.** Set your shell environment to reference the ChefDK

    eval "$(chef shell-init bash)" && echo $PATH

**Step 7.** The Chef Repo is a Git repository used to store your Chef artifacts, such as cookbooks and recipes. This repo can be automatically created for you by using the **chef generate repo** command:

    chef generate repo chef-repo

This creates a chef repo in the chef-repo directory.

**Step 8.** Configure your global git variables with your actual name and email address
    
    git config --global user.name “YourName”
    git config --global user.email “you@yourdomain.com”

**Step 9.** Initialize Git and add the initial files. 

    cd chef-repo
    git init
    git add .
    git commit –m 'initial commit'
    cd ..

**Step 10.** Create a directory called .chef and copy the key files to both the .chef directory and the /etc/chef directory.

    sudo cp *.pem /etc/chef/ && mkdir ~/.chef && cp *.pem ~/.chef

 Chef uses RSA keys to encrypt all communication between the Chef workstation and the Chef server. When we configured our Chef server, we created a file called labuser.pem, which contains the key for the labuser account. When we created our fabrikam organization, we saved the organization key into a file called yourname-validator.pem in our home directory. Now we must move these key files to the correct location for later use.

**Step 11.** Run the knife configure command:
    
    knife configure

 Knife is a command-line tool that provides an interface between your local chef repo and the Chef server.

**Step 12.** Knife will ask you “Where should I put the config file?” with a default value in square brackets *[/home/labuser/.chef/knife.rb]*. Press **Enter** to continue with the default value.

**Step 13.** Knife will ask you “Please enter the chef server URL” with a default value in square brackets. This value is incorrect. Enter (with *yourcloudservicename* replaced by your cloud service name): 

    https://yourcloudservicename.cloudapp.net:443/organizations/fabrikam

**Step 14.** Knife will ask you “Please enter an existing username or clientname for the API:” with a default value in square brackets *[labuser]*. Press **Enter** to continue with the default value.

**Step 15.** Knife will ask you “Please enter the validation clientname::” with a default value in square brackets *[chef-validator]*. This value is incorrect. Enter: 

    fabrikam-validator

**Step 16.** Knife will ask you “Please enter the location of the validation key:” with a default value in square brackets *[/etc/chef-server/chef-validator.pem]*. This value is incorrect. Enter: 

    /etc/chef/fabrikam-validator.pem

**Step 17.** Knife will ask you “Please enter the path to a chef repository (or leave blank):” Enter: 

    /home/labuser/chef-repo

**Step 18.** Run the knife ssl fetch command:
    
    sudo knife ssl fetch

 Our Chef server has an SSL certificate that is not trusted. As a result, we have to manually trust the SSL certificate in order to have our workstation communicate with the Chef server. This can also be addressed by importing a valid SSL certificate for Chef to use.

**Step 19.** View the current chef-repo contents.

    ls chef-repo

**Step 20.** Synchronize the Chef repo.

    knife download /

**Step 21.** Run the **ls** command from Step 1 again, and observe that additional files and folders have been created in the chef-repo directory. 

###Task 4: Create a Cookbook
In this exercise, you will create a cookbook to automate the installation of the MRP application and upload it to the Chef server.

**Step 1.** Use the knife tool to generate a cookbook template. 

    knife cookbook create mrpapp

 A cookbook is a set of tasks for configuring an application or feature. It defines a scenario and everything required to support that scenario. Within a cookbook, there are a series of recipes that define a set of actions to perform. Cookbooks and recipes are written in the Ruby language.

This creates an “mrpapp” directory in the chef-repo/cookbooks/ directory that contains all of the boilerplate code that defines a cookbook and a default recipe.

**Step 2.** Edit the metadata.rb file in our cookbook directory.
   
    nano chef-repo/cookbooks/mrpapp/metadata.rb
 
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

**Step 5.** Save the file with `Ctrl-o` then **Enter**. Exit nano with `Ctrl-x`.

**Step 6.** Install the apt cookbook. 

    knife cookbook site install apt

 We need to install two dependencies for our recipe: the apt cookbook, and the chef-client cookbook. This can be accomplished using the knife cookbook site command, which will download the cookbooks from the official Chef cookbook repository, [https://supermarket.chef.io/cookbooks](https://supermarket.chef.io/cookbooks).

**Step 7.** Install the chef-client cookbook.

    knife cookbook site install chef-client

**Step 8.** We will first open up a full copy of the recipe on the host machine where you are connected to the Chef Server, found at [https://raw.githubusercontent.com/Microsoft/PartsUnlimitedMRP/master/deploy/Chef/cookbooks/mrpapp-idempotent/recipes/default.rb](https://raw.githubusercontent.com/Microsoft/PartsUnlimitedMRP/master/deploy/Chef/cookbooks/mrpapp-idempotent/recipes/default.rb).

**Step 9.** Copy all of the contents of this page to your host machine clipboard with `Ctrl-a` and `Ctrl-c`. 

**Step 10.** Go back to your Chef Server SSH session (i.e. Putty) and then edit recipe.rb with nano. 

    nano chef-repo/cookbooks/mrpapp/recipes/default.rb

**Step 11.** The file should look like this to start: 

    ↪	#
    ↪	# Cookbook Name:: mrpapp
    ↪	# Recipe:: default
    ↪	Cd site insta#
    ↪	# Copyright 2015, YOUR_COMPANY_NAME
    ↪	#
    ↪	# All rights reserved - Do Not Redistribute
    ↪	#
    
**Step 12.** Paste the contents of the recipe into the mrpapp recipe file such as right-clicking inside the Putty session.

**Step 13.** In all of the instances where `https://chefdemowus.blob.core.windows.net/mrpbuild/` exist in the *default.rb* file, replace them with `https://github.com/Microsoft/PartsUnlimitedMRP/tree/master/builds/`. For *MongoRecords.js*, change the source directory to be `https://github.com/Microsoft/PartsUnlimitedMRP/tree/master/deploy/MongoRecords.js`. 

**Step 14.** Save the file with `Ctrl-o` then **Enter**.

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
