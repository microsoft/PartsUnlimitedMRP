## Deploy the PartsUnlimited MRP App to an Azure Ubuntu Server
Click on the button below to deploy an Ubuntu server in Azure that will install and configure
the PartsUnlimited MRP application.

The server is deployed according to the settings in the ARM template. A custom script
extension then runs the install shell script to install and configure the app.

[![Deploy to Azure](http://azuredeploy.net/deploybutton.png)](https://azuredeploy.net/)

### Access the MRP application
Once the deployment has succeeded, you can navigate to
```
http://_dnsName_.cloudapp.net:9080/mrp
```
where `dnsName` is the name of the public IP you configured when deploying.