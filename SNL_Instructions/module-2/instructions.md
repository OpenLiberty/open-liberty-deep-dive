# Module 2: Dev Mode

The Open Liberty Maven plug-in includes a dev goal that listens for any changes in the project, including application source code or configuration. The Open Liberty server automatically reloads the configuration without restarting. This goal -- dev mode -- allows for quicker turnarounds and an improved developer experience by providing hot deploy, hot testing and hot debug capabilities.

Open a new terminal and navigate to the **coffee-shop** service.

> [Terminal -> Split Terminal]

```
cd open-liberty-masterclass/start/coffee-shop/
```
{: codeblock}

We are going to make changes to the coffee-shop project.

Navigate to the coffee-shop project and start the server up in dev mode and make some changes to the configuration. 
Dev mode can help to install features automatically if adding new features to the server configuration file server.xml when the server is still running:
```
mvn liberty:dev
```
{: codeblock}

Take a look at the Maven build file for the coffee-shop project: **open-liberty-masterclass/start/coffee-shop/pom.xml**

The Open Liberty Maven plugin must be version 3.x or above to use dev mode. We define the latest versions of the plugins at the pom.xml:

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
 

In the same **coffee-shop/pom.xml** locate the **`<dependencies/>`** section.  All the features we are using in this Masterclass are part of Jakarta EE and MicroProfile. By having the two dependencies below means that at build time these are available for Maven to use and then it will install any of the features you request in your **server.xml,** but we will get to that shortly.

``` XML
    <dependencies>
        <!--Open Liberty provided features -->
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

Let's add the dependency for the **MicroProfile OpenAPI** feature so we can try the **coffee-shop** service out.

We have already loaded the MicroProfile 4.0 feature in the **pom.xml** that will include the latest version of MicroProfile OpenAPI so we just need to configure the Open Liberty server.

Open the **server.xml**

> [File -> Open]open-liberty-masterclass/start/coffee-shop/src/main/liberty/config/server.xml


This file is the configuration for the **coffee-shop** server.

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
{: codeblock}

If you now go back to your terminal you should notice Open Liberty installing the new features without shutting down. You can also re-run tests by simply pressing enter in the Terminal. 

```
[INFO] [AUDIT   ] CWWKF0012I: The server installed the following features: [mpOpenAPI-2.0].
```

For a full list of all the features available, see https://openliberty.io/docs/ref/feature/.

# Next Steps

Congratulations on completing your next exercise. Don't stop now. Move on to the next module in the master class by simply closing this tab and clicking on the next module in the Open Liberty Masterclass landing page.
