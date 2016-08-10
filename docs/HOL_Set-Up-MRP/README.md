HOL 0 - Set up Parts Unlimited MRP
====================================================================================

In this lab, we will set up the PartsUnlimited MRP VSTS team project as a pre-requisite for the Continuous Integration lab. You will need an active Visual Studio Team Services (VSTS) account already, or you will need to sign up for a free Visual Studio Team Services account by following [this link.](https://www.visualstudio.com/en-us/docs/setup-admin/team-services/sign-up-for-visual-studio-team-services) Ensure that you are also a Project Collection Administrator for the Visual Studio Team Services account or follow [this link.](https://www.visualstudio.com/en-us/docs/setup-admin/add-administrator-tfs) 

###Pre-requisites:###

-   An active Visual Studio Team Services (VSTS) account

-   Project Collection Administrator rights to the Visual Studio Team Services account

### Tasks Overview: ###

**Set up your Visual Studio Team Services account:** This step helps you download the source code, and then push it to your own Visual Studio Team Services account.

### 1: Set up your Visual Studio Team Services account

We want to push the application code to your Visual Studio Team Services account in
order to use Team Build for the Continuous Integration HOL.

**1.** First, navigate to your Visual Studio Team Services account by typing into a browser: 

    https://{VSTS instance name}.visualstudio.com

**2.** Once at your Visual Studio Team Services account, create a new PartsUnlimitedMRP team project by clicking on the **New** button under **Recent projects & teams**. Type in the project name as *PartsUnlimitedMRP* and select *Git* as the version control, then click on **Create project**.

![](<media/create_team_project.png>)

**3.** After the wizard creates your new team project, navigate to the PartsUnlimitedMRP team project and click on the **Code** tab on the upper-left. 

![](<media/navigate_to_code.png>)

**4.** The PartsUnlimitedMRP Git repository will be empty, so copy the Clone URL of the VSTS repository to your clipboard and paste it into a text document for use later. 

![](<media/copy_vsts_repo_url.png>)

**5.** Now that we have the URL for the VSTS repo, we need to authenticate access to  a Visual Studio Team Services personal access token. Follow the steps in this [link](https://www.visualstudio.com/en-us/docs/setup-admin/team-services/use-personal-access-tokens-to-authenticate) 
 to create a personal access token (PAT).

**NOTE:** These Personal Access Token (PAT) will be used when interacting with the git repository from the
command line. Make sure you copy the token now. Visual Studio Team Services does not store it and you won't be able to see it again. 


**6.** Clone the **PartsUnlimitedMRP** git repository located in GitHub onto your local machine:

    git clone https://github.com/Microsoft/PartsUnlimitedMRP.git

![](<media/clone_mrp.png>)

**NOTE:** If you are running Linux on your local workstation, install git:

    sudo apt-get install git
	
**NOTE:** If you are running Windows, you can install the git client from here:

	http://git-scm.com/download

**3.** Add the Clone URL of your Visual Studio Team Services repository as a new remote called **vsts** and push to it
your Visual Studio Team Services account. While pushing, use any username without the "@" symbol and the PAT that you copied as the password.

	cd PartsUnlimitedMRP/

	git remote add vsts <url_to_repository>

	git push -u vsts --all
	
![](<media/push_to_vsts.png>)

**NOTE:** We added the Visual Studio Team Services repository as a remote named **vsts**, so we need to
push to that remote in the future for our changes to appear in our Visual Studio Team Services
repository.

**4.** Your Visual Studio Team Services account should now have a copy of the PartsUnlimitedMRP
application:

![](<media/mrp_in_vsts.png>)
 

Next steps
----------

In this lab, you learned how to set up the PartsUnlimitedMRP team project in Visual Studio Team Services. Try these labs out for next steps:

-   [Parts Unlimited MRP Continuous Integration](https://github.com/Microsoft/PartsUnlimitedMRP/tree/master/docs/HOL_Continuous-Integration)

-   [Parts Unlimited MRP Continuous Deployment](https://github.com/Microsoft/PartsUnlimitedMRP/tree/master/docs/HOL_Continuous-Deployment)

-   [Parts Unlimited MRP Application Performance Monitoring](https://github.com/Microsoft/PartsUnlimitedMRP/tree/master/docs/HOL_Application-Performance-Monitoring)

-	[Parts Unlimited MRP Auto-Scaling and Load Testing](https://github.com/Microsoft/PartsUnlimitedMRP/tree/master/docs/HOL_Autoscaling-Load-Testing)
