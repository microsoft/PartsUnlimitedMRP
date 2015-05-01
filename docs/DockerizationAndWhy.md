# Docker-ization and Why #

We'll take a brief look at the thought process and mental model utilized in this example.  And specifically how we identified how to make it run within Docker containers. While this example is one way to *Docker-ize* an application, as with many solutions, there are probably numerous others.

The decision process generally is driven from the immediate Use Case or Scenarios to be leveraged by running the Solution within Docker.

For this example, the immediate need is to provide a Developer ready deployment, quick and repeatable, with minimal changes.

## Why Docker ##

A good place to start your journey with Docker is to review the [What is Docker](https://www.docker.com/whatisdocker/) introduction.

In general, the way we're looking at Docker is to facilitate:
1. Infrastructure as Code (IaC)
2. Instantiate a solution from bare metal:
	* fast
	* consistent
	* known and desired state
	* transparent and no magic


## Steps for Docker-izing an existing Solution ##

From a high level we'll assume that you've chosen Docker as a method to meet the needs as we've identified above.

The effort in going from a deployment that utilizes custom scripts or hand configuration of each Tier of the deployment to a full Docker container approach varies based upon what is available for the existing solution.

This example we are able to take advantage of several existing shell scripts (bash scripts) that were utilize in both the Middle tier and the MongoDB tier.

In other solutions if nothing exists already or its a totally hand woven approach to deployment, the effort to move to Docker could be a valuable effort -- it at least provides a more declarative identification of what exists or is needed and minimum should give your the ability to resurrect the solution for Development, Testing, or even Disaster Recovery needs.

### High Level Steps ###

1. Review existing application deployment and architecture
2. Identify primary *tiers* and platform components at each tier
  * For this sample, review the section and image below [Identification of Tiers](#identification-of-tiers)
3. Determine state-fullness of each tier
  * Long Term Persistence or transient state-fullness? (a DB vs. User Session)
5. Network needs
  * Identify inbound and outbound TCP/IP port requirements
  * Identify cross tier network requirements

### Next Steps ###
Once we have identified the major tiers, state-fullness, and networking requirements, we are basically ready to begin building the [Dockerfile](https://docs.docker.com/reference/builder/ "Docker File Documentation") (container definitions) and start looking at the deployment as "Infrastructure as Code!". 

Those steps are provided in the demonstration script.

### Identification of Tiers ###

The following diagram illustrates the deployment of the Order Service application.

![order service app deployment](./images/orderserviceprestate.png)

#### Important Deployment Points ####

* Tomcat hosted content is Static Content - no server side logic
 1. Mostly HTML, CSS, and Image content
 2. JavaScript content provides client side rendering
 3. JavaScript also initializes "configuration" values - such as what Port to use for accessing 
* Browser requires non-standard Port access to the REST Tier
* MongoDB requires "seed" data
	1. For development and testing, we'll provide a simple script to load seed data each deployment
	2. While in production, we'll need to ensure that a MongoDB startup and recover from a node being added or recovered in a cluster.

** NOTE ** We will not be covering the Production Deployment and MongoDB clustering approaches. For more information, you can refer to:
* [Docker and MongoDB Sharded Cluster - How Docker helps to create a complex MongoDB setup](https://sebastianvoss.com/docker-mongodb-sharded-cluster.html)
* [Deploy a MongoDB Cluster in 9 steps Using Docker](https://medium.com/@gargar454/deploy-a-mongodb-cluster-in-steps-9-using-docker-49205e231319) 

### Statefull vs. Non-Statefull ###
While MongoDB and most persistence stores are state-full, and require long-term durable storage, we also need to examine what other tiers may need from short term persistence needs - if any.

For example, some application tiers may require "Session" management - that could be through a cache engine or even local files.  For this example no other tiers require this; however, if needed we would have to accommodate this in some manner as when each Docker container is started or restarted it would require (possibly) access to state which must have some durability outside the life-cycle of the running container. If not noted already, it's critical to understand that Docker fully manages from creation and destruction resources required during the life of the container when that resource is wholly contained.

#### MongoDB ####
For MongoDB, Docker provides the ability to map volumes into the container. This is described here: [Managing Data in Containers](https://docs.docker.com/userguide/dockervolumes/)

The Dockerfile for MongoDB in this example creates the directory /data/db if it doesn't exist - and the script that starts the container passes in the Docker host path via the '-v' switch - this mapping then ensures that any writes are persisted across container restarts.

#### Static Web and Spring Framework Tiers ####
Both of these tiers are stateless. Therefore it is not necessary to provide host volume mapping to the running containers.

### Networking Considerations ###
For each tier, we have identified what ports require exposing either to external callers or between other containers running on the Docker host.

In this example, we see:
  * Browser requires Inbound communication to Static Content Tier
  * Browser requires Inbound communication to Spring Framework Tier
  * NO communication is required from the "Static Content" tier to any other tier
  * Spring Framework Tier requires Inbound communication to MongoDB Tier

#### MongoDB ####
MongoDB by default listens on port 27017 for HTTP based communications from clients. In the Dockerfile we provide the following (which is really just a *hint*). It is still required to pass the port mapping via the 'docker run' command and -p switch.

~~~
# Expose port #27017 from the container to the host
EXPOSE 27017
~~~

#### App Tier to MongoDB ####
For this example we identified that the App Tier (Spring Framework based App) needs to make direct calls to MongoDB.

By default Docker does not allow communication between containers. All intra-container communication must be declared.  See [Linking Containers Together](https://docs.docker.com/userguide/dockerlinks/) for more information.

The linking is demonstrated in the following 'docker run' commands via the '--link' switch.

~~~
docker run -d -p 27017:27017 --name mongodb -v /data/db:/data/db scicoria/mongoseed:0.1
docker run -d -p 8080:8080 --link mongodb:mongodb scicoria/orderservice:0.1
~~~

Note that in the 'docker run' command for Order Service (2nd line) we specify the link using the container name and an alias.  That alias is then presented to the Order Service container as the hostname within the virtual networking support inside of Docker.  There are far more implications of networking that should be reviewed for more complex scenarios [Linking Containers Together](https://docs.docker.com/userguide/dockerlinks/).





