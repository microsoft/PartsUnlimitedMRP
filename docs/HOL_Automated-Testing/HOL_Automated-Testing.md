# HOL - Parts Unlimited MRP App Automated Testing with Visual Studio Online Build #

In this lab you will learn how to create new JUnit tests to the Parts Unlimited MRP App using the Eclipse IDE, and then get them to run in an automated build in Visual Studio Online. After finishing this lab, you will have added unit tests to the Parts Unlimited MRP App and have those tests running during build time.

###Pre-requisites###

- Active Visual Studio Online Account
- Eclipse ([http://www.eclipse.org/](http://www.eclipse.org/ "http://www.eclipse.org/"))
- HOL Continuous Integration with Parts Unlimited MRP


###Tasks Overview###

**Set up development environment:** This step will get your local environment set up to work on the Parts Unlimited MRP App. 

**Add JUnit tests to Parts Unlimited MRP App:** In this step, you will add automated unit tests to the Parts Unlimited MRP App and run them locally.

**Run the automated tests in an automated build:** You will then run the JUnit tests as part of the continuous integration build that was set up in the HOL Continuous Integration with PArts Unlimited MRP

###1. Set up development environment###

First, we need to set up Eclipse in order to work on the Parts Unlimited MRP App on our local machine. The Parts Unlimited MRP App uses Gradle ([https://gradle.org/](https://gradle.org/ "https://gradle.org/")), so we need to set up the code as Gradle projects in Eclipse.

**1.** Open up **Eclipse**.

**2.** Navigate to **[https://marketplace.eclipse.org/content/gradle-integration-eclipse-0#](https://marketplace.eclipse.org/content/gradle-integration-eclipse-0# "https://marketplace.eclipse.org/content/gradle-integration-eclipse-0#")** and drag the **Install** button onto your running **Eclipse** workspace.

![](media/drag_install_button.png)

**Note:** If the drag and drop functionality does not work for you, you can also install it via the `Help -> Eclipse Marketplace` dialog by searching for `gradle`.

**3.** This should trigger the Eclipse Marketplace install dialog to appear. Click **Confirm** to move to the next page.

![](media/confirm_gradle_install.png)

**4.** **Accept** the license agreement, and then click **Finish**.

![](media/accept_license.png)

This will install the Gradle plugin. **Restart Eclipse** once it has finished installing.

**5.** From the menu of Eclipse, select **Window -> Perspective -> Open Perspective -> Other...** to select the perspective that we want. 

![](media/open_other_perspective.png)

**6.** Select **Git** and click **OK**.

![](media/git_perspective.png)

This will open up the Git perspective in Eclipse.

**7.** Click the **Clone a Git repository** from the Git Repositories Window

![](media/clone_repo.png)

**8.** In the Clone Git Repository, **paste the URL for your Git repo** into the URI text field, **enter your alternate credentials** in the Authentication section, and click **Next**.

![](media/repo_uri.png)

**9.** Select any branch that you want, and click **Next**.

![](media/select_branches.png)

**10.** On the next page, leave the defaults and click **Finish**.

![](media/repo_clone_finish.png)

This will clone the repository to your local machine. You should be able to see the repository in the Git Repositories window.

![](media/local_repo.png)

**11.** Open the **Java perspective** by clicking the shortcut in the top right corner of Eclipse.

![](media/java_perspective.png)

**12.** In the top left, click the **New** drop down and select **Java project**.

![](media/new_java_project.png)

**13.** In the New Java Project dialog name the project **IntegrationService**, uncheck the **Use default location**, choose the **IntegrationService** folder located in `src/Backend/` of the PartsUnlimitedMRP repository, and click **Finish**.

![](media/new_integration_service.png)

**14.** In the Package Explorer window, **right-click on IntegrationService** and then select **Configure -> Convert to Gradle Project**.

![](media/integration_to_gradle.png)

Note: at this point, you should see the errors disappear from the project.

**15.** Follow **Step 12** to create a **new Java Project**.

**16.** In the New Java Project dialog name the project **OrderService**, uncheck the **Use default location**, choose the **OrderService** folder located in `src/Backend/` of the PartsUnlimitedMRP repository, and click **Finish**.

![](media/new_order_service.png)

**17.** In the Package Explorer window, **right-click on OrderService** and then select **Configure -> Convert to Gradle Project**.

**18.** Follow **Step 12** again to create a **new Java Project**.

**19.** In the New Java Project dialog name the project **Clients**, uncheck the **Use default location**, choose the **Clients** folder located in `src/` of the PartsUnlimitedMRP repository, and click **Finish**.

![](media/new_clients.png)

**20.** In the Package Explorer window, **right-click on Clients** and then select **Configure -> Convert to Gradle Project**.

You should now have 3 projects in Eclipse that are all Gradle projects (signified by the `G` in the glyph of the projects in the Package Explorer window).

![](media/gradle_projects.png)

**21.** In Eclipse's menu select **Window -> Show View -> Other...**

![](media/other_view.png)

**22.** Type `gradle` in the filter box, and click **OK**.

You can now select one of the three projects that were created, and be able to view all of the Gradle tasks in each on via the Gradle Tasks window.

![](media/order_gradle_tasks.png)

The development environment is now set up, and you should be ready to make write some automated tests. Go ahead and look over the different tasks - you may even want to try out the `build` task.

###2. Add JUnit tests to Parts Unlimited MRP App###

This task will focus on creating a unit test to test a part of the Parts Unlimited App.

**1.** There are already a bunch of JUnit tests that exist within the various different projects of the Parts Unlimited MRP App. **Open up the file** in the **OrderService** Eclipse project located here:

     src/test/java/smpl.ordering.controllers/CatalogControllerTest.java

![](media/open_catalog_tests.png)

**2.** Lets go ahead and run these tests. In the menu select **Run -> Run As -> JUnit Test**.

![](media/run_default_tests.png)

This should open up the JUnit window and give you a summary of the test run.

![](media/default_test_run.png)

**3.** Paste the following code on **line 31** of the `CatalogControllerTest.java` file :

	@Test
	public void testAddCatalogItems() throws Exception
	{
    	ResponseEntity<List<CatalogItem>> oldItems = controller.getCatalogItems();
    	int sizeBeforeAdds = 0;
    	if (oldItems.getBody() != null)
    	{
        	sizeBeforeAdds = oldItems.getBody().size();
    	}
    	
    	ResponseEntity<CatalogItem> response = 
    			controller.addCatalogItem(new CatalogItem("ACC-0123", "Storage", 15.75, 5, 2));
    	assertNotNull(response);
    	assertEquals(HttpStatus.CREATED, response.getStatusCode());
    	
    	response = 
    			controller.addCatalogItem(new CatalogItem("ACC-0124", "Storage", 11.25, 7, 6));
    	assertNotNull(response);
    	assertEquals(HttpStatus.CREATED, response.getStatusCode());
    	
    	ResponseEntity<List<CatalogItem>> newItems = controller.getCatalogItems();
    	int sizeAfterAdds = newItems.getBody().size();
    	
    	assertEquals(sizeBeforeAdds + 2, sizeAfterAdds);
	}

The final result should look something like this:

![](media/new_test_method.png)

**4.** Go ahead and re-run the JUnit tests from the menu **Run -> Run As -> JUnit Test**.

![](media/new_test_result.png)

You should now be able to see that the test in the JUnit Window.

###Run the automated tests in an automated build###

Now that we have the automated tests written, and successfully running, it's important we push the new tests to the shared repository. This will allow other team members to benefit from the automated test and give the automated build more verification that our code is working as expected.

**1.** In eclipse, **switch to the Git perspective** in the top right of the screen.

![](media/switch_to_git_perspective.png)

**2.** In the Git Repositories window, select the `PartsUnlimitedMRP` git repository. 

![](media/select_pum_repo.png)

**3.** Select the **Git Staging** window. **Highlight** all of the Unstaged Changes and **drag and drop them into Stage Changes**.

![](media/stage_changes.png)

Note: Three `.gitignore` flies have changed. Eclipse automatically added the `bin/` folder here, so any binaries that are built locally will not be tracked. You can choose not to stage these.

**4.** Add the following as a commit message, and then click **Commit and Push**.

    Add new test to CatalogControllerTest.java

![](media/commit_and_push.png)

The Push Results window should pop up after you have successfully pushed:

![](media/push_results.png)

**5.** Navigate to the **BUILD** hub in your Visual Studio Online Team Project, and then click the **Queued** tab. You should see that your continuous integration build was triggered, and is running.

![](media/build_triggered.png)

**6.** Once the build is done, **open up the build summary page** by double-clicking on it. 

**7.** On the build summary page, click the **refresh glyph** next to `Test results`, and then **click the second link down** - this should be the results of the `CatalogControllerTest` class.

![](media/build_test_summary.png)

**8.** Click on the **Test results tab** and you should now see that your new automated test is a part of the continuous integration build.

![](media/build_test_results.png)


Next steps
----------

In this lab, you set up your development environment to work with Gradle, you added a new JUnit test to the Parts Unlimited MRP App, and you were able to see that reflected in the automated build process (automatically!). Try out these labs for next steps:

 - HOL Parts Unlimited MRP Continuous Delivery with Visual Studio Online Build
 - HOL Parts Unlimited MRP Application Performance Monitoring
 - HOL PArts Unlimited MRP User Telemetry