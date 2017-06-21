---
layout: page
title:  Continuous Integration with Jenkins
category: AzureStack
order: 7
---
In this lab, we have an application called Parts Unlimited MRP. We want to set up Jenkins to be able continuously integrate code into the master branch of code. This means that whenever code is committed and pushed to the master branch, we want to ensure that it integrates into our code correctly to get fast feedback. To do so, we are going to be creating a pipeline that will allow us to compile and run unit tests on our code when it is pushed to GitHub.

### *Important Consideration for Azure Stack
Combining Jenkins and GitHub allows for the automated building of an application as soon as developers check in code to their GitHub repository, through a capability known as webhooks. For instance, you can configure Jenkins and GitHub in such a way that when GitHub detects new code from your developers, it automatically sends information to Jenkins, which triggers the build process to start, using the freshly checked-in code. This is more efficient than having Jenkins poll GitHub on an interval for instance, or via manual triggering.

Due to the way that Azure Stack is configured from a networking perspective, utilizing webhooks from GitHub (resides on the public internet) and your Jenkins VM (inside an Azure Stack virtual network, which itself is likely inside your corporate network) is difficult. If you recall earlier, your IP address for your Jenkins Master VM is likely 192.168.102.X i.e. a private IP. This will not resolve on the public internet, and thus, GitHub would not be able to reach this address directly.

Workarounds include the use of a GitHub proxy, that has communication between the Jenkins VM and the public internet, or alternatively, configuring multiple NAT rules, using public IPs etc. to allow GitHub to reach the Jenkins Master VM. A more realistic alternative would be the deployment of GitHub Enterprise on-premises, which would provide GitHub functionlity, but within your corporate environment.

These workarounds are currently out of scope, but may be addressed in the future.  For this lab, you will trigger builds manually in Jenkins.

### Pre-Requisites: ##

- Completion of the lab [Set up Parts Unlimited MRP with Jenkins](azurestack-36-jenkins-setup.html)
- Have a GitHub account [https://github.com](https://github.com).

### Tasks Overview:
During the following tasks you will fork the Parts Unlimited MRP github repository and create a Jenkins pipeline for the Continuous Integration of the Parts Unlimited MRP application. You will learn how to configure Jenkins so that whenever a change is checked in on the code repository, a build will be triggered and several tests will be performed.

#### Configure your GitHub repository

**1.** Nagivate to [https://github.com/Microsoft/PartsUnlimitedMRP/](https://github.com/Microsoft/PartsUnlimitedMRP/)

**2.** Sign in with your github account

**3.** Click on Fork.

**NOTE**: if you have multiple accounts, select the account you want to fork to.

![Github fork](<../assets/jenkins/github_fork.png>)

#### Create a new pipeline
In this task, we will create a new pipeline that will build the artifacts of the application.  Before we do this however, there are a couple of settings to tweak on the Jenkins master, to ensure smooth operation of the lab.

**1.** Firstly navigate to:

```
http://ip_address_of_your_jenkinsmaster:8080/configureSecurity/
```

**2.** Check the box "Allow anonymous read access"

**3.** Untick the box "Prevent Cross Site Request Forgery exploits" and click **Save** 

![CSRF_disabled](<../assets/jenkins/csrf_disabled.png>)

This will Disable the CSRF protection on the Jenkins master but is an easy way to enable CI with Github.

Click **Save**

**4.** Create an empty pipeline: from your Jenkins master, click on **New Item**.

**NOTE:** You can also go directly to: http://IP_address_of_your_jenkinsmaster/view/All/newJob

![New Jenkins item](<../assets/jenkins/jenkins_newitem.png>)

* Type the following name for the pipeline: **PartsUnlimitedMRP**
* Select **Pipeline** 
* Click **OK**

![New Jenkins Pipeline](<../assets/jenkins/jenkins_newpipeline.png>)

The pipeline type in Jenkins will allow us to describe all the build steps with Groovy code. This code can be easily ported on any Jenkins system and could also be embedded in a Jenkinsfile in the source code.

**5.** Create the Pipeline

Click on the tab name **Pipeline** and ensure that the definition is **Pipeline script**

The following code will clone the source code from PartsUnlimited, define the environment variables for the JDK and print the version of Java that we are using. Copy the script below in the **Script** box. 

``` Groovy

    node{
        stage ("Checkout") {
        git 'https://github.com/Microsoft/PartsUnlimitedMRP.git'
        }

        env.JAVA_HOME = "${tool 'JDK 8'}"
        env.PATH = "${env.JAVA_HOME}/bin:${env.PATH}"
        sh 'java -version'
    }

```

Replace the git url with the url of your own github repository. 

![Pipeline script](<../assets/jenkins/pipeline_script1.png>)

The **stage** syntax in the code above defines a boundary of code that will be executed together. In a pipeline you can have as many stages as you want, they can run sequentially or in parallel, depending on the constraints that you have to build the application.

Click **Save** and then **Build Now** 

![Pipeline script](<../assets/jenkins/pipeline_build1.png>)

After few seconds you should have a successful build with following result:

![Build results](<../assets/jenkins/build_result1.png>)

**6.** Building PartsUnlimitedMRP

Now that we have a basic pipeline, let's add the code that will define the build of the Parts Unlimited MRP application.
The application is composed of three components: 
- The _Order Service_
- The _Integration Service_
- The _Client_ application 

We will create a stage for each of those components.

Click on **Configure** on the left section.

Copy the following code in the pipeline script: 

``` Groovy

    node{
    stage ("Checkout") {
        git 'https://github.com/Microsoft/PartsUnlimitedMRP.git'
        }

    env.JAVA_HOME = "${tool 'JDK 8'}"
    env.PATH = "${env.JAVA_HOME}/bin:${env.PATH}"
    sh 'java -version'  
          
    stage ("Integration Service") {
            dir('src/Backend/IntegrationService') {
            sh 'chmod +x gradlew'
            sh './gradlew build'
            archiveArtifacts artifacts: '**/integration-service*.jar', excludes: null
        }
    }    
    stage ("Order Service") {
            dir('src/Backend/OrderService') {
            sh 'chmod +x gradlew'
            sh './gradlew build'
            archiveArtifacts artifacts: '**/ordering-service*.jar', excludes: null
        }
    }    
    stage ("Clients") {
        dir('src/Clients') {
            sh 'chmod +x gradlew'
            sh './gradlew build'
            archiveArtifacts artifacts: '**/mrp.war', excludes: null
        }
    }
    }  

```

Replace the git url with the url of your own github repository. 

Click **Save** and then **Build Now**

**NOTE:** You may have to refresh the page once the build has completed to see the artifacts that have been produced.

![Build Pipeline for PartsUnlimitedMRP](<../assets/jenkins/build_pipeline2.png>)


#### Adding test coverage
The Parts Unlimited MRP Application performs tests for the OrderService component. In this task, we will add some information about the results of those tests and display the trend of the results of those tests.

**1.** Cick on **Configure** to edit your pipeline script.

**2.** After line 21, insert the following code
            ```
            junit '**/TEST-*.xml'
            ```

The new pipeline code is:

``` Groovy

    node{
    stage ("Checkout") {
    git 'https://github.com/Microsoft/PartsUnlimitedMRP.git'
    }
    env.JAVA_HOME = "${tool 'JDK 8'}"
    env.PATH = "${env.JAVA_HOME}/bin:${env.PATH}"
    sh 'java -version'    
    stage ("Integration Service") {
            dir('src/Backend/IntegrationService') {
            sh 'chmod +x gradlew'
            sh './gradlew build'
            archiveArtifacts artifacts: '**/integration-service*.jar', excludes: null
        }
    }    
    stage ("Order Service") {
            dir('src/Backend/OrderService') {
            sh 'chmod +x gradlew'
            sh './gradlew build'
            archiveArtifacts artifacts: '**/ordering-service*.jar', excludes: null
            junit '**/TEST-*.xml'
        }
    }    
    stage ("Clients") {
        dir('src/Clients') {
            sh 'chmod +x gradlew'
            sh './gradlew build'
            archiveArtifacts artifacts: '**/mrp.war', excludes: null
        }
    }
    }

```

**3.** Click on **Save** then **Build Now**, and once complete, click **Build Now** again.

The test results as displayed below will be displayed AFTER running two builds and refreshing the page.

![Pipeline with test results](<../assets/jenkins/pipeline_withtest.png>)

## Next steps

In this lab, you learned how to create a Continuous Integration build that runs when new commits are pushed to the master branch. This allows you to get feedback as to whether your changes made breaking syntax changes, or if they broke one or more automated tests, or if your changes are OK. Try this lab out for next steps:

- [Parts Unlimited MRP Continous Deployment with Jenkins](azurestack-38-jenkins-cd.html)

### Continuous Feedback

##### Issues / Questions about this Hands-On-Lab?

[If you are encountering issues or have questions during this Hands-on-Lab, please open an issue by clicking here](https://github.com/Microsoft/PartsUnlimitedMRP/issues)
