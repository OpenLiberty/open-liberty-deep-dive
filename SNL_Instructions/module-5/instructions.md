# Module 5: Externalizing Configuration

If you're familiar with the concept of 12-factor applications (see http://12factor.net) you'll know that factor III states that an application's configuration should be stored in the environment. Configuration here, is referring to variables which vary between development, staging and production. In doing so, you can build the deployment artefact once and deploy it in different environments unchanged.

Liberty lets your application pick up configuration from a number of sources, such as environment variables, bootstrap.properties and Kubernetes configuration.

Stop the `barista` service by pressing **CTRL+C** in the command-line session where you ran it at the module 1.

We now need to change the server configuration to externalize the ports.  

Open the **open-liberty-masterclass/start/coffee-shop/src/main/liberty/config/server.xml** file, change this line:

```XML
    <httpEndpoint host="*" httpPort="9080" httpsPort="9443" id="defaultHttpEndpoint"/>
```
to 

```XML
    <variable name="default.http.port" defaultValue="9081"/>
    <variable name="default.https.port" defaultValue="9444"/>

    <httpEndpoint id="defaultHttpEndpoint" host="*" 
        httpPort="${default.http.port}" 
        httpsPort="${default.https.port}"/>
```
{: codeblock}

Restart the `barista` service by running the following curl commands:
```
export DEFAULT_HTTP_PORT=9082
mvn liberty:run
````
{: codeblock}

You will know from the output that the  `barista` service is running on the port `9082`:
```
[INFO] [AUDIT   ] CWWKT0016I: Web application available (default_host): http://gilberts-mbp.lan:9082/health/
[INFO] [AUDIT   ] CWWKT0016I: Web application available (default_host): http://gilberts-mbp.lan:9082/openapi/
[INFO] [AUDIT   ] CWWKT0016I: Web application available (default_host): http://gilberts-mbp.lan:9082/openapi/ui/
[INFO] [AUDIT   ] CWWKT0016I: Web application available (default_host): http://gilberts-mbp.lan:9082/barista/
```

Next we'll use the `default_barista_base_url` in the code to avoid hard-coding the location of the `barista` service.

Edit the file `open-liberty-masterclass/start/coffee-shop/src/main/java/com/sebastian_daschner/coffee_shop/control/Barista.java`

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
{: codeblock}

You'll also need to add the following imports:

```Java
import javax.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
```
{: codeblock}

This is using the MicroProfile Config specification to inject the configuration value.  Configuration can come from a number of sources, including **bootstrap.properties.**

We also need to make the same changes to the HealthResource of the **coffee-shop** service. 

Edit the file: **open-liberty-masterclass/start/coffee-shop/src/main/java/com/sebastian_daschner/coffee_shop/boundary/HealthResource.java**

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
{: codeblock}

Add the following imports:

```Java
import javax.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
```
{: codeblock}

For more information on MicroProfile Config see https://openliberty.io/guides/microprofile-config.html.

Rebuild the code, start the **coffee-shop** and **barista** servers and try out the endpoint through the Open API UI.  You can also try out the health endpoint at `http://localhost:9080/health`. Select **Launch Application** tab, the application is running on **port 9080**. To access the **/health** endpoint add **/health** to the end of the URL.

# Next Steps

Congratulations on completing next first exercise. Don't stop now. Move on to the next module in the master class by simply closing this tab and clicking on the next module in the Open Liberty Masterclass landing page.
