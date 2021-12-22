# Open Liberty Masterclass

This document contains the hands-on lab modules for the Open Liberty Masterclass.  It is intended to be used in conjunction with taught materials, however, feel free to give it a try, even if you're not in a Masterclass.

## Table of Contents

- [Open Liberty Masterclass](#open-liberty-masterclass)
  - [Table of Contents](#table-of-contents)
  - [Before you begin](#before-you-begin)
    - [Install Pre-requisites](#install-pre-requisites)
    - [Prime Maven and Docker Caches](#prime-maven-and-docker-caches)
  - [The Application](#the-application)
  - [Module 1: Build](#module-1-build)
  - [Module 2: Dev Mode](#module-2-dev-mode)
  - [Module 3: Application APIs](#module-3-application-apis)
  - [Module 4: Server Configuration](#module-4-server-configuration)
  - [Module 5: Externalizing Configuration](#module-5-externalizing-configuration)
  - [Module 6: Integration Testing](#module-6-integration-testing)
  - [Module 7: Docker](#module-7-docker)
    - [Overriding Dev Server Configuration](#overriding-dev-server-configuration)
  - [Module 8: Testing in Containers](#module-8-testing-in-containers)
  - [Module 9: Support Licensing](#module-9-support-licensing)
  - [Conclusion](#conclusion)

## Before you begin

### Install Pre-requisites

* A Java 8/11 JDK (e.g. https://adoptopenjdk.net/?variant=openjdk8&jvmVariant=openj9)
* Apache Maven (https://maven.apache.org/)
* A git client (https://git-scm.com/downloads)
* An editor with Java support (e.g. Eclipse, VS Code, IntelliJ)
* Docker 
  * **Windows:** Set up Docker for Windows as described at https://docs.docker.com/docker-for-windows/.
  *  **Mac:** Set up Docker for Mac as described at https://docs.docker.com/docker-for-mac/.

### Prime Maven and Docker Caches

If you will be taking the Masterclass at a location with limited network bandwidth, it is recommended you do the following beforehand in order to populate your local .m2 repo and Docker cache.

```
git clone https://github.com/OpenLiberty/open-liberty-masterclass.git
cd open-liberty-masterclass/finish/coffee-shop
mvn package liberty:create liberty:install-feature
docker build -t masterclass:coffee-shop .
cd ../barista
mvn package liberty:create liberty:install-feature
docker build -t masterclass:barista .
cd ..
```
## The Application

The application consists of two Microservices; `coffee-shop` and `barista`.  The `coffee-shop` service allows you to place an order and the `barista` service services the making of the coffee.

```
                ^|
                || orderCoffee()
                || 
                || 
            ┌───|v────────┐   startCoffeeBrew()   ┌─────────────┐
            │ coffee-shop │---------------------->│   barista   │
            └─────────────┘<----------------------└─────────────┘
```
The completed code for the Masterclass is provided in the `open-liberty-masterclass/finish` directory.  To work through the Masterclass you will develop in the `open-liberty-masterclass/start` directory.


## Module 1: Build

Liberty has support for building and deploying applications using Maven and Gradle.  The source and documentation for these plugins can be found here:
* https://github.com/OpenLiberty/ci.maven
* https://github.com/OpenLiberty/ci.gradle

The Masterclass will make use of the `liberty-maven-plugin`.

Take a look at the Maven build file for the coffee-shop project: `open-liberty-masterclass/start/barista/pom.xml`

Go to the barista project:

```
cd start/barista
```

Build and run the barista service:

```
mvn liberty:run
```

Visit: http://localhost:9081/openapi/ui

This page is an OpenAPI UI that lets you try out the barista service.  

Click on `POST` and then `Try it out`

Under `Example Value` specify:

```JSON
{
  "type": "ESPRESSO"
}
```

Click on `Execute`

Scroll down and you should see the server response code of `200`.  This says that the barista request to make an `ESPRESSO` was successfully `Created`. If you go back to the terminal you will also see the message `starting to brew: EXPRESSO`. Now leave this terminal with the Open Liberty server running and open up a new terminal to continue this masterclass from the same directory.


## Module 2: Dev Mode

The Open Liberty Maven plug-in includes a dev goal that listens for any changes in the project, including application source code or configuration. The Open Liberty server automatically reloads the configuration without restarting. This goal -- dev mode -- allows for quicker turnarounds and an improved developer experience by providing hot deploy, hot testing and hot debug capabilities.

We are going to make changes to the coffee-shop project.

Navigate to the coffee-shop project and start the server up in dev mode and make some changes to the configuration. This will need to install new features while the server is still running:

```
cd ../coffee-shop
mvn liberty:dev
```

Take a look at the Maven build file for the coffee-shop project: `open-liberty-masterclass/start/coffee-shop/pom.xml`

The Open Liberty Maven plugin must be version 3.x or above to use dev mode. 

```XML
    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-war-plugin</artifactId>
        <version>3.3.1</version>
    </plugin>
    <plugin>
        <groupId>io.openliberty.tools</groupId>
        <artifactId>liberty-maven-plugin</artifactId>
        <version>3.3.4</version>
    </plugin>
    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-failsafe-plugin</artifactId>
        <version>2.22.2</version>
    </plugin>   
    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-surefire-plugin</artifactId>
        <version>2.22.2</version>
    </plugin>
```
 
In the same `coffee-shop/pom.xml` locate the `<dependencies/>` section. All the features we are using in this Masterclass are part of Jakarta EE and MicroProfile. By having the two dependencies below means that at build time these are available for Maven to use and then it will install any of the features you requests in your server.xml but we will get to that shortly.

``` XML
    <dependencies>
      <!--Open Liberty features -->
        <dependency>
            <groupId>jakarta.platform</groupId>
            <artifactId>jakarta.jakartaee-web-api</artifactId>
            <version>8.0.0</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>org.eclipse.microprofile</groupId>
            <artifactId>microprofile</artifactId>
            <version>4.0.1</version>
            <type>pom</type>
            <scope>provided</scope>
        </dependency> 
        ...
    </dependencies>
```

Let's add the dependency on the `MicroProfile OpenAPI` feature so we can try the `coffee-shop` service out.

We have already loaded the MicroProfile 4.0 feature in the pom that will include the latest version of MicroProfile OpenAPI so we just need to configure the Open Liberty server.

Open the file `open-liberty-masterclass/start/coffee-shop/src/main/liberty/config/server.xml`

This file is the configuration for the `coffee-shop` server.

Near the top of the file, you'll see the following `<featureManager/>` entry:

```XML
    <featureManager>
        <feature>jaxrs-2.1</feature>
        <feature>ejbLite-3.2</feature>
        <feature>cdi-2.0</feature>
        <feature>beanValidation-2.0</feature>
        <feature>mpHealth-3.0</feature>
        <feature>mpConfig-2.0</feature>
        <feature>mpRestClient-2.0</feature>
        <feature>jsonp-1.1</feature>
    </featureManager>
```
This entry lists all the features to be loaded by the server.  Add the following entry inside the `<featureManager/>` element:

```XML
        <feature>mpOpenAPI-2.0</feature>
```

If you now go back to your terminal you should notice Open Liberty installing the new features without shutting down. You can also re-run tests by simply pressing enter in the Terminal. 

Lets go have a look at the new application you installed due to installing the Open API feature:

Visit: http://localhost:9080/openapi/ui

As with the barista service, this is an Open API UI page that lets to try out the service API for the coffee-shop service.

For a full list of all the features available, see https://openliberty.io/docs/ref/feature/.

## Module 3: Application APIs

Open Liberty has support for many standard APIs out of the box, including Java EE 7 & 8, Jakarta EE 8 and the latest MicroProfile APIs.

As you have seen in the previous section, the API dependencies that you need to use MicroProfile or Jakarta EE APIs have been added as dependencies to the POM file. You are all set to use these APIs, as you need as you write your code.

Then, we need to enable the corresponding features in Liberty's server configuration for Liberty to load and use what you have chosen for your application. With Liberty's modular and composable architecture, only the features specified in the server configuration will be loaded giving you a lightweight and performant runtime.

We're now going to add Metrics to the `coffee-shop`.  Edit the `open-liberty-masterclass/start/coffee-shop/src/main/liberty/config/server.xml` file and add the following dependency in the featureManager section like we did above:

```XML
        <feature>mpMetrics-3.0</feature>
```

You should see that the server has been automatically updates, the following features are installed, and include mpMetrics-3.0:

```
[INFO] [AUDIT   ] CWWKF0012I: The server installed the following features: [beanValidation-2.0, cdi-2.0, distributedMap-1.0, ejbLite-3.2, el-3.0, jaxrs-2.1, jaxrsClient-2.1, jndi-1.0, json-1.0, jsonp-1.1, mpConfig-1.3, mpHealth-2.2, mpMetrics-2.0, mpOpenAPI-1.1, mpRestClient-1.3, servlet-4.0, ssl-1.0].
```
Now we have the API available, we can update the application to include a metric which will count the number of times a coffee order is requested. In the file `open-liberty-masterclass/start/coffee-shop/src/main/java/com/sebastian_daschner/coffee_shop/boundary/OrdersResource.java`, add the following `@Counted` annotation to the `orderCoffee` method:

```java
@Counted(name="order", displayName="Order count", description="Number of times orders requested.")
```
It should look like:

```Java
    @POST
    @Counted(name="order", displayName="Order count", description="Number of times orders requested.")
    public Response orderCoffee(@Valid @NotNull CoffeeOrder order) {
        ...
    }
```

You'll also need to add the following package import:
```Java
import org.eclipse.microprofile.metrics.annotation.Counted;
```


## Module 4: Server Configuration

From your previous addition of the MicroProfile Metrics feature in the server.xml you should now see a message for a new metrics endpoint in the terminal that looks like:

```
[INFO] [AUDIT   ] CWWKT0016I: Web application available (default_host): http://localhost:9080/metrics/

```

Open the metrics endpoint in your browser http://localhost:9080/metrics/.  You should see a message like this:

```
Error 403: Resource must be accessed with a secure connection try again using an HTTPS connection.
```
or a **Username** and **Password** will be required

It's one thing to configure the server to load a feature, but many Liberty features require additional configuration.  The complete set of Liberty features and their configuration can be found here: https://openliberty.io/docs/ref/config/.

The error message suggests we need to add a `keyStore` and one route to solve this would be to add a `keyStore` and user registry (e.g. a `basicRegistry` for test purposes).  However, if we take a look at the configuration for [mpMetrics](https://openliberty.io/docs/ref/config/#mpMetrics.html) we can see that it has an option to turn the metrics endpoint authentication off.

Add the following below the `</featureManager>` in the `open-liberty-masterclass/start/coffee-shop/src/main/liberty/config/server.xml`

```XML
    <mpMetrics authentication="false" />
```

Now restart your server and visit the metrics endpoint:

http://localhost:9080/metrics/

You should see a number of metrics automatically generated by the JVM:

```
TYPE base:classloader_total_loaded_class_count counter
# HELP base:classloader_total_loaded_class_count Displays the total number of classes that have been loaded since the Java virtual machine has started execution.
base:classloader_total_loaded_class_count 10616
...
```
This doesn't contain the metrics you added because the service hasn't been called and so no application metrics have been recorded. Use the OpenAPI UI (http://localhost:9080/openapi/ui/) to send a few requests to the service.

As with the `barista` service, you'll need to specify the following payload for the `POST` request:

```JSON
{
  "type": "ESPRESSO"
}
```

Reload the metrics page and at the bottom of the metrics results you should see:

```
...
# TYPE application:com_sebastian_daschner_coffee_shop_boundary_orders_resource_order counter
# HELP application:com_sebastian_daschner_coffee_shop_boundary_orders_resource_order Number of times orders requested.
application:com_sebastian_daschner_coffee_shop_boundary_orders_resource_order 3
```
Now go to the terminal and type `q` followed by `Enter` to shut down the server.


## Module 5: Externalizing Configuration

If you're familiar with the concept of 12-factor applications (see http://12factor.net) you'll know that factor III states that an application's configuration should be stored in the environment. Configuration here, is referring to variables which vary between development, staging and production. In doing so, you can build the deployment artefact once and deploy it in different environments unchanged.

Liberty lets your application pick up configuration from a number of sources, such as environment variables, bootstrap.properties and Kubernetes configuration.

Stop the **barista** service by pressing **CTRL+C** in the command-line session where you ran it at the module 1.

We now need to change the server configuration to externalize the ports.  

Open the **open-liberty-masterclass/start/barista/src/main/liberty/config/server.xml** file, change these lines:

```XML
   <httpEndpoint id="defaultHttpEndpoint" host="*" 
        httpPort="9081" 
        httpsPort="9444"/>
```
to 

```XML
    <variable name="default.http.port" defaultValue="9081"/>
    <variable name="default.https.port" defaultValue="9444"/>

    <httpEndpoint id="defaultHttpEndpoint" host="*" 
        httpPort="${default.http.port}" 
        httpsPort="${default.https.port}"/>
```

Start the **barista** service by running the following curl commands:
```
export DEFAULT_HTTP_PORT=9082
mvn liberty:dev
````

If you take a look at the **barista** server output, you should find out that the **barista** service is running on the port `9082` now:
```
[INFO] [AUDIT   ] CWWKT0016I: Web application available (default_host): http://192.000.0.00:9082/openapi/
[INFO] [AUDIT   ] CWWKT0016I: Web application available (default_host): http://192.000.0.00:9082/health/
[INFO] [AUDIT   ] CWWKT0016I: Web application available (default_host): http://192.000.0.00:9082/openapi/ui/
[INFO] [AUDIT   ] CWWKT0016I: Web application available (default_host): http://192.000.0.00:9082/barista/
```

Next we'll use the `default_barista_base_url` in the code to avoid hard-coding the location of the **barista** service for the **coffee-shop** service.

Edit the file `open-liberty-masterclass/start/coffee-shop/src/main/java/com/sebastian_daschner/coffee_shop/control/HealthResource.java`

Change:

```Java
    String baristaBaseURL = "http://localhost:9081";
```

To:

```Java
    @Inject
    @ConfigProperty(name="default_barista_base_url")
    String baristaBaseURL;
```

You'll also need to add the following imports:

```Java
import javax.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
```

This is using the MicroProfile Config specification to inject the configuration value. Configuration can come from a number of sources.

Open the `coffee-shop/src/main/webapp/META-INF/microprofile-config.properties` MicroProfile configuration file. Add the following value:
```
default_barista_base_url=http://localhost:9081
```

We also need to make the same changes to the CoffeeShopReadinessCheck of the **coffee-shop** service. 

Edit the file: **open-liberty-masterclass/start/coffee-shop/src/main/java/com/sebastian_daschner/coffee_shop/health/CoffeeShopReadinessCheck.java**

Change:

```Java
    String baristaBaseURL = "http://localhost:9081";
```

To:

```Java
  @Inject
  @ConfigProperty(name="default_barista_base_url")
  String baristaBaseURL;
```

Add the following imports:

```Java
import javax.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
```

For more information on MicroProfile Config see https://openliberty.io/guides/microprofile-config.html.

Visit the following URL to check the health of your service:

http://localhost:9080/health/ready

You'll find out from the **coffee-shop** service is not ready because the **barista** is not running on the port `9081`:
```
{"checks":[{"data":{},"name":"CoffeeShopReadinessCheck Readiness Check","status":"DOWN"}],"status":"DOWN"}
```

Update the `coffee-shop/src/main/webapp/META-INF/microprofile-config.properties` MicroProfile configuration file. Change the port to 9082 as the following:
```
default_barista_base_url=http://localhost:9082
```

Visit the following url again:

http://localhost:9080/health/ready


You'll find out from the **coffee-shop** service is ready now:
```
{"checks":[{"data":{},"name":"CoffeeShopReadinessCheck Readiness Check","status":"UP"}],"status":"UP"}
```

You can set the `default_barista_base_url` value through the `DEFAULT_BARISTA_BASE_URL` environment variable but you'll need to restart the **coffee-shop** service.


## Module 6: Integration Testing

Tests are essential for developing maintainable code. Developing your application using bean-based component models like CDI makes your code easily unit-testable. Integration Tests are a little more challenging. In this section you'll add a `barista` service integration test using the `maven-failsafe-plugin`. During the build, the Liberty server will be started along with the `barista` application deployed, the test will be run and then the server will be stopped.

Because we're going to be testing a REST `POST` request, we need JAX-RS client support and also support for serializing `json` into the request. We also need `junit` for writing the test. 


Add these dependencies to the `open-liberty-masterclass/start/barista/pom.xml`:

```XML
        <!-- Test dependencies -->  
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <version>5.7.1</version>
            <scope>test</scope>
        </dependency>     
        <dependency>
            <groupId>org.apache.cxf</groupId>
            <artifactId>cxf-rt-rs-mp-client</artifactId>
            <version>3.4.3</version>
            <scope>test</scope>
        </dependency>      
        <dependency>
            <groupId>com.fasterxml.jackson.jaxrs</groupId>
            <artifactId>jackson-jaxrs-json-provider</artifactId>
            <version>2.12.3</version>
            <scope>test</scope>
        </dependency>  
```

Note the `<scope/>` of the dependencies is set to `test` because we only want the dependencies to be used during testing.

Add the following `<configuration>...</configuration>` to the `maven-failsafe-plugin` plugin:
```XML
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-failsafe-plugin</artifactId>
                <version>2.22.2</version>
                <configuration>
                    <systemPropertyVariables>
                        <liberty.test.port>9082</liberty.test.port>
                    </systemPropertyVariables>
                </configuration>
            </plugin> 
```

Note, this configuration makes the port of the server available to the test as a system property called `liberty.test.port`.

Finally, add the test code.  Create a file called, `open-liberty-masterclass/start/barista/src/test/java/com/sebastian_daschner/barista/it/BaristaIT.java` and add the following:

```Java
package com.sebastian_daschner.barista.it;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

import com.sebastian_daschner.barista.boundary.BrewsResource;
import com.sebastian_daschner.barista.entity.CoffeeBrew;
import com.sebastian_daschner.barista.entity.CoffeeType;

public class BaristaIT {
    private static String URL;

    @BeforeAll
    public static void init() {
        String port = System.getProperty("liberty.test.port");
        URL = "http://localhost:" + port + "/barista/resources/brews";
    }
    @Test
    public void testService() throws Exception {

        Client client = null;
        WebTarget target = null;
        try {
            client = ClientBuilder.newClient().register(JacksonJsonProvider.class);
            target = client.target(URL);

        } catch (Exception e) {
            client.close();
            throw e;
        }

        CoffeeBrew brew = new CoffeeBrew();
        brew.setType(CoffeeType.POUR_OVER);

        Response response = target.request(MediaType.APPLICATION_JSON).post(Entity.json(brew));

        try {
            if (response == null) {
                assertNotNull(response, "GreetingService response must not be NULL");
            } else {
                assertEquals( 200, response.getStatus(), "Response must be 200 OK");
            }

        } finally {
            response.close();
        }
    }
}

```

This test sends a `json` request to the `barista` service and checks for a `200 OK` response. 

Run the tests by pressing `Enter` on your running server terminal.

In the output of the build, you should see:

```
-------------------------------------------------------
 T E S T S
-------------------------------------------------------
Running com.sebastian_daschner.barista.it.BaristaIT
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 1.365 sec - in com.sebastian_daschner.barista.it.BaristaIT

Results :

Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
```

Once the test has finished, shut down both microservices by typing `q` in both terminals, then press the Enter key and restart the terminal to pick up any environmental changes you have previously set.

## Module 7: Docker

We're now going to dockerize the two services and show how we can override the defaults to re-wire the two services.  We're going to use a Docker user-defined network (see https://docs.docker.com/network/network-tutorial-standalone/#use-user-defined-bridge-networks) because by using Docker user-defined networks we are able to connect the two containers to the same network and have them communicate using only the others IP address or name.  For real-world production deployments you would use a Kubernetes environment, such as Red Hat OpenShift or IBM Cloud Kubernetes Service.

Take a look at the `open-liberty-masterclass/start/coffee-shop/Dockerfile`:

```Dockerfile
FROM openliberty/open-liberty:full-java8-openj9-ubi

COPY src/main/liberty/config /config/
ADD target/barista.war /config/dropins

RUN configure.sh
```

The `FROM` statement is building this image using the Open Liberty kernel image (see https://hub.docker.com/_/open-liberty/ for the available images).

The `COPY` statement is copying over the server.xml file we mentioned earlier to the Docker image.

The `ADD` statement is copying our application into the Docker image.

The `RUN` command runs a script that is already located on the image that will add the requested XML snippets, grow the image to be fit-for-purpose and apply interim fixes.

Let's build the docker image.  In the `open-liberty-masterclass/start/coffee-shop` directory run: 

```
mvn package
docker build -t masterclass:coffee-shop .
```

In the `open-liberty-masterclass/start/barista` directory, run (note the period (`.`) at the end of the line is important):

```
mvn package
docker build -t masterclass:barista .
```

Next, create the user-defined bridge network:

```
docker network create --driver bridge masterclass-net
```

You can now run the two Docker containers and get them to join the same bridge network.  Providing names to the containers makes those names available for DNS resolution within the bridge network so there's no need to use IP addresses.

Run the `barista` container:

```
docker run -d --network=masterclass-net --name=barista masterclass:barista
```

Note, we don't need to map the `barista` service ports outside the container because the bridge network gives access to the other containers on the same network.

Next, we're going to run the `coffee-shop` container.  For it to work,The approach we're going to take is to use a Docker volume we'll need to provide new values for ports and the location of the barista service.  Run the `coffee-shop` container

```
docker run -d -p 9080:9080 -p 9445:9443 --network=masterclass-net --name=coffee-shop \
  -e default_barista_base_url='http://barista:9081' \
  -e default_http_port=9080 \
  -e default_https_port=9443 masterclass:coffee-shop
```

You can take a look at the bridge network using:

```
docker network inspect masterclass-net
```

You'll see something like:

```JSON
[
    {
        "Name": "masterclass-net",
        ...
        "IPAM": {
            "Driver": "default",
            "Options": {},
            "Config": [
                {
                    "Subnet": "172.19.0.0/16",
                    "Gateway": "172.19.0.1"
                }
            ]
        },
        ...
        "Containers": {
            "0fc740d52f2ed8dfdb04127fe3e49366dcbeb7924fee6b0cbf6f891c0909b0e8": {
                "Name": "coffee-shop",
                "EndpointID": "157d697fb4bff2722d654c68e3a5e5fe7554a91e860213d22362cd7cc074fc8f",
                "MacAddress": "02:42:ac:13:00:02",
                "IPv4Address": "172.19.0.2/16",
                "IPv6Address": ""
            },
            "2b78ebf13596147042c8f2f5bd3171ca1c6f77241f419472010ddc2f28fd7a0c": {
                "Name": "barista",
                "EndpointID": "c93163547eb7e3c2c84dd0f72beb77127cfc319b6d9d7f6d9d99e17b85ff6d30",
                "MacAddress": "02:42:ac:13:00:03",
                "IPv4Address": "172.19.0.3/16",
                "IPv6Address": ""
            }
        },
        "Options": {},
        "Labels": {}
    }
]
```

You should now be able to load the `coffee-shop` service's Open API page and call the service.  Give it a try.

http://localhost:9080/openapi/ui

Or, you can run the following curl commands to try out the services running in containers:

```
curl http://localhost:9080/health
curl -X POST "http://localhost:9080/coffee-shop/resources/orders" \
     -H  "accept: */*" -H  "Content-Type: application/json" \
     -d "{\"status\":\"FINISHED\",\"type\":\"ESPRESSO\"}"
curl http://localhost:9080/coffee-shop/resources/orders
```

Now, let's stop and remove the **coffee-shop**  container for the following section:

```
docker stop coffee-shop
docker rm coffee-shop
```

### Overriding Dev Server Configuration

The above works fine, but still has a metrics endpoint with authentication turned off.  We'll now show how `configDropins/overrides` can be used to override existing, or add new, server configuration.  For example, this can be used to add server configuration in a production environment. The approach we're going to take is to use a Docker volume for simplicity. Docker Volumes are the preferred mechanism for persisting data generated by and used by Docker containers. While bind mounts are dependent on the directory structure and OS of the host machine, volumes are completely managed by Docker. .In a real-world scenario you would use Kubernetes ConfigMaps and secrets to include the production server configuration, security configuration and environment variables. 

In fact, unlike what we have done here, the best practice is to build an image that does not contain any environment specific configuration (such as the unsecured endpoint in our example) and then add those things through external configuration in the development, staging and production environments.  The goal is to ensure deployment of the image without configuration doesn't not cause undesirable results such as security vulnerabilities or talking to the wrong data sources.

Take a look at the file `open-liberty-masterclass/start/coffee-shop/configDropins/overrides/metrics-prod.xml`:

```XML
<?xml version="1.0" encoding="UTF-8"?>
<server description="Coffee Shop Server">

    <featureManager>
        <feature>mpMetrics-3.0</feature>
    </featureManager>
    
    <mpMetrics authentication="true" />

     <!-- 
     Note, this configuration is for demo purposes
     only and MUST NOT BE USED IN PRODUCTION AS IT 
     IS INSECURE. -->  
    <variable name="admin.password" value="change_it" />
    
    <quickStartSecurity userName="admin" userPassword="${admin.password}"/>
     
</server>
```

You'll see that this turns metrics authentication on and sets up some simple security required for securing/accessing the metrics endpoint.  Note, this configuration really is **NOT FOR PRODUCTION**, it's simply aiming to show how to override, or provide new, server configuration.

If you're on a unix-based OS, in the `open-liberty-masterclass/start/coffee-shop` directory, run the `coffee-shop` container:

```
docker run -d -p 9080:9080 -p 9445:9443 --network=masterclass-net --name=coffee-shop \
  -e default_barista_base_url='http://barista:9081' \
  -e default_http_port=9080 \
  -e default_https_port=9443 \
  -v $(pwd)/configDropins/overrides:/opt/ol/wlp/usr/servers/defaultServer/configDropins/overrides masterclass:coffee-shop
```

The above relies on `pwd` to fill in the docker volume source path.  If you're on Windows, replace `$(pwd)` with the absolute path to the `open-liberty-masterclass/start/coffee-shop` directory in the above command.

You should see the following message as the server is starting if you look at the logs:

```
docker logs coffee-shop
```

```
[AUDIT ] CWWKG0093A: Processing configuration drop-ins resource: /opt/ol/wlp/usr/servers/defaultServer/configDropins/overrides/metrics-prod.xml
```

This shows that we have turned metrics authentication back on.

Access the metrics endpoint at: https://localhost:9445/metrics

You will see that the browser complains about the certificate.  This is a self-signed certificate generated by Liberty for test purposes.  Accept the exception (note,  Firefox may not allow you to do this in which case you'll need to use a different browser).  You'll be presented with a login prompt.  Sign in with userid `admin` and password `change_it` (the values in the `metrics-prod.xml`).

Or, you can run the following curl command to retrieve the metrics:
```
curl -k --user admin:change_it https://localhost:9445/metrics
```

Now, let's stop and remove the **barista** and **coffee-shop** containers and the network:

```
docker stop barista coffee-shop
docker rm barista coffee-shop
docker network rm masterclass-net
```

## Module 8: Testing in Containers

We saw in an earlier module, how to perform Integration Tests against the application running in the server.  We then showed how to package the application and server and run them inside a Docker container.  Assuming we're going to deploy our application in production inside Containers it would be a good idea to actually perform tests against that configuration.  The more we can make our development and test environments the same as production, the less likely we are to encounter issues in production. [MicroShed Testing](microshed.org) is a project that enables us to do just that.

Firstly let's start by deleting the tests we created earlier. We would not normally have integration tests done with MicroShed testing and the way we previously looked at. This can be achieved but it is not best practice. The reason for deleting the old tests is because without extra configuration maven will try to run those tests against MicroShed but as these tests run in a container the configuration for connecting to our application will be different.

Delete the file `open-liberty-masterclass/start/barista/src/test/java/com/sebastian_daschner/barista/it/BaristaIT.java`

Now let's create a new Integration Test that will perform the same test, but inside a running container.  In the Barista project, add the following dependencies to the `open-liberty-masterclass/start/barista/pom.xml` file in the `<dependencies>` element:

```XML
          <!-- For MicroShed Testing -->      
        <dependency>
            <groupId>org.microshed</groupId>
            <artifactId>microshed-testing-liberty</artifactId>
            <version>0.9.1</version>
        <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-log4j12</artifactId>
            <version>1.7.30</version>
            <scope>test</scope>
        </dependency>
```

Create a new Integration Test called `BaristaContainerIT.java` in the directory `start/barista/src/test/java/com/sebastian_daschner/barista/it` and add the following code:

```Java
package com.sebastian_daschner.barista.it;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Duration;

import javax.ws.rs.core.Response;

import org.junit.jupiter.api.Test;
import org.microshed.testing.jaxrs.RESTClient;
import org.microshed.testing.jupiter.MicroShedTest;
import org.microshed.testing.testcontainers.ApplicationContainer;
import org.testcontainers.junit.jupiter.Container;

import com.sebastian_daschner.barista.boundary.BrewsResource;
import com.sebastian_daschner.barista.entity.CoffeeBrew;
import com.sebastian_daschner.barista.entity.CoffeeType;

@MicroShedTest
public class BaristaContainerIT {

    @Container
    public static ApplicationContainer app = new ApplicationContainer()
                    .withAppContextRoot("/barista")
                    .withExposedPorts(9081)
                    .withReadinessPath("/health/ready");
    
    @RESTClient
    public static BrewsResource brews;

    @Test
    public void testService() throws Exception {
        CoffeeBrew brew = new CoffeeBrew();
        brew.setType(CoffeeType.POUR_OVER);
        Response response = brews.startCoffeeBrew(brew);

        try {
            if (response == null) {
            	assertNotNull(response, "GreetingService response must not be NULL");
            } else {
            	assertEquals( 200, response.getStatus(), "Response must be 200 OK");
            }
        } finally {
            response.close();
        }
    }
}


```

You'll see that the class is marked as a MicroShed test with the `@MicroShedTest` annotation.

The test also contains the following Container configuration:

```Java
    @Container
    public static MicroProfileApplication app = new MicroProfileApplication()
                    .withAppContextRoot("/barista")
                    .withExposedPorts(9081)
                    .withReadinessPath("/health");
```


You'll see that the unit test is like any other.

We need to configure `log4j` in order to see the detailed progress of the MicroShed test.  In the directory `start/barista/src/test/resources/` create the file `log4j.properties` and add the following configuration to it:

```properties
log4j.rootLogger=INFO, stdout

log4j.appender=org.apache.log4j.ConsoleAppender
log4j.appender.layout=org.apache.log4j.PatternLayout

log4j.appender.stdout=org.apache.log4j.ConsoleAppender
log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
log4j.appender.stdout.layout.ConversionPattern=%r %p %c %x - %m%n

log4j.logger.org.microshed=DEBUG
```

Start the server in Dev Mode and run the tests by pressing `Enter` after the server has started:

```
mvn liberty:dev
```

You should see the following output:

```
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.sebastian_daschner.barista.it.BaristaContainerIT
0 INFO org.microshed.testing.jupiter.MicroShedTestExtension  - Using ApplicationEnvironment class: org.microshed.testing.testcontainers.config.HollowTestcontainersConfiguration
70 INFO org.testcontainers.dockerclient.DockerClientProviderStrategy  - Loaded org.testcontainers.dockerclient.UnixSocketClientProviderStrategy from ~/.testcontainers.properties, will try it first
710 INFO org.testcontainers.dockerclient.UnixSocketClientProviderStrategy  - Accessing docker with local Unix socket
710 INFO org.testcontainers.dockerclient.DockerClientProviderStrategy  - Found Docker environment with local Unix socket (unix:///var/run/docker.sock)
868 INFO org.testcontainers.DockerClientFactory  - Docker host IP address is localhost
914 INFO org.testcontainers.DockerClientFactory  - Connected to docker: 
  Server Version: 19.03.1
  API Version: 1.40
  Operating System: Docker Desktop
  Total Memory: 1998 MB
1638 INFO org.testcontainers.utility.RegistryAuthLocator  - Credential helper/store (docker-credential-desktop) does not have credentials for quay.io
2627 INFO org.testcontainers.DockerClientFactory  - Ryuk started - will monitor and terminate Testcontainers containers on JVM exit
        ℹ︎ Checking the system...
        ✔ Docker version should be at least 1.6.0
        ✔ Docker environment should have more than 2GB free disk space
2827 INFO org.microshed.testing.testcontainers.MicroProfileApplication  - Discovered ServerAdapter: class org.testcontainers.containers.liberty.LibertyAdapter
2828 INFO org.microshed.testing.testcontainers.MicroProfileApplication  - Using ServerAdapter: org.testcontainers.containers.liberty.LibertyAdapter
2834 DEBUG org.microshed.testing.testcontainers.config.TestcontainersConfiguration  - No networks explicitly defined. Using shared network for all containers in class com.sebastian_daschner.barista.it.BaristaContainerIT
2842 INFO org.microshed.testing.testcontainers.config.HollowTestcontainersConfiguration  - exposing port: 9081 for container alpine:3.5
2843 INFO org.microshed.testing.testcontainers.config.HollowTestcontainersConfiguration  - exposing port: 9444 for container alpine:3.5
2844 INFO org.microshed.testing.testcontainers.config.TestcontainersConfiguration  - Starting containers in parallel for class com.sebastian_daschner.barista.it.BaristaContainerIT
2845 INFO org.microshed.testing.testcontainers.config.TestcontainersConfiguration  -   java.util.concurrent.CompletableFuture@465232e9[Completed normally]
2848 INFO org.microshed.testing.testcontainers.config.TestcontainersConfiguration  - All containers started in 3ms
2868 DEBUG org.microshed.testing.jaxrs.RestClientBuilder  - no classes implementing Application found in pkg: com.sebastian_daschner.barista.boundary
2868 DEBUG org.microshed.testing.jaxrs.RestClientBuilder  - checking in pkg: com.sebastian_daschner.barista
2873 DEBUG org.microshed.testing.jaxrs.RestClientBuilder  - Using ApplicationPath of 'resources'
2874 INFO org.microshed.testing.jaxrs.RestClientBuilder  - Building rest client for class com.sebastian_daschner.barista.boundary.BrewsResource with base path: http://localhost:9081/barista/resources and providers: [class org.microshed.testing.jaxrs.JsonBProvider]
3273 DEBUG org.microshed.testing.jupiter.MicroShedTestExtension  - Injecting rest client for public static com.sebastian_daschner.barista.boundary.BrewsResource com.sebastian_daschner.barista.it.BaristaContainerIT.brews
3419 INFO org.microshed.testing.jaxrs.JsonBProvider  - Sending data to server: {"type":"POUR_OVER"}
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 3.93 s - in com.sebastian_daschner.barista.it.BaristaContainerIT
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] 
```

## Module 9: Support Licensing

Open Liberty is Open Source under the Eclipse Public License v1, as a result there is no fee to use in production.  Community support is available via StackOverflow, Gitter, or the mail list, and bugs can be raised in [GitHub](https://github.com/openliberty/open-liberty). Commercial support from IBM is available for Open Liberty, you can find out more on the [IBM Marketplace](https://www.ibm.com/uk-en/marketplace/elite-support-for-open-liberty). The WebSphere Liberty product is built on Open Liberty, there is no migration required to use WebSphere Liberty, you simply point to WebSphere Liberty in your build.  Users of WebSphere Liberty get support for the packaged Open Liberty function.

WebSphere Liberty is also available in [Maven Central](https://search.maven.org/search?q=g:com.ibm.websphere.appserver.runtime).

You can use WebSphere Liberty for development even if you haven't purchased it, but if you have production entitlement you can easily change to use it, as follows:

In the `open-liberty-masterclass/start/barista/pom.xml` and `open-liberty-masterclass/start/coffee-shop/pom.xml`, add the `<configuration>...</configuration>` as the following:

```XML
            <plugin>
                <groupId>io.openliberty.tools</groupId>
                <artifactId>liberty-maven-plugin</artifactId>
                <version>3.3.4</version>
                <configuration>
                  <runtimeArtifact>
                      <groupId>com.ibm.websphere.appserver.runtime</groupId>
                      <artifactId>wlp-kernel</artifactId>
                      <version>[21.0.0.4,)</version>
                      <type>zip</type>
                  </runtimeArtifact>
                </configuration>
            </plugin>
```

Rebuild and re-start the **barista** service:

```
export DEFAULT_HTTP_PORT=9082
mvn clean
mvn liberty:dev
```

and the **coffee-shop** service:
```
export DEFAULT_HTTP_PORT=9080
mvn clean
mvn liberty:dev
```

The **barista** service should be started at the port `9082` and the **coffee-shop** service at the port `9080`. 
Then, try the service out using the Open API Web page and you should see the behavior is identical.  Not surprising since the code is identical, from the same build, just built into WebSphere Liberty.


## Conclusion
Thanks for trying the Open Liberty Masterclass. If you're interested in finding out more, please visit the [Open Liberty website](http://openliberty.io), and for more hands-on experience, why not try the [Open Liberty Guides](http://openliberty.io/guides).

