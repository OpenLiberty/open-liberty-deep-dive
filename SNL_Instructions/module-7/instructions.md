# Module 7: Docker

We're now going to dockerize the two services and show how we can override the defaults to re-wire the two services.  We're going to use a Docker user-defined network (see https://docs.docker.com/network/network-tutorial-standalone/#use-user-defined-bridge-networks) because we'll be running them on the same host and it keeps things simple.  For real-world production deployments you would use a Kubernetes environment, such as IBM Cloud Private or the IBM Cloud Kubernetes Service.

Take a look at the **open-liberty-masterclass/start/coffee-shop/Dockerfile:**

```Dockerfile
FROM openliberty/open-liberty:full-java8-openj9-ubi

COPY src/main/liberty/config /config/
ADD target/coffee-shop.war /config/dropins

RUN configure.sh
```

The **FROM** statement is building this image using the Open Liberty kernel image (see https://hub.docker.com/_/open-liberty/ for the available images).

Let's build the docker image.  In the **open-liberty-masterclass/start/coffee-shop** directory:

```
mvn package
docker build -t masterclass:coffee-shop .
```
{: codeblock}

Navigate to the **barista** directory and build the docker image: 

```
cd ../barista/
mvn package
docker build -t masterclass:barista .
```
{: codeblock}

Next, create the user-defined bridge network:

```
docker network create --driver bridge masterclass-net
```
{: codeblock}

You can now run the two Docker containers and get them to join the same bridge network.  Providing names to the containers makes those names available for DNS resolution within the bridge network so there's no need to use IP addresses.

Run the `barista` container:

```
docker run -d --network=masterclass-net --name=barista masterclass:barista
```
{: codeblock}


Note, we don't need to map the `barista` service ports outside the container because the bridge network gives access to the other containers on the same network.

Next, we're going to run the `coffee-shop` container. The approach we're going to take is to use a Docker volume, therefore we'll need to provide new values for ports and the location of the barista service.  Run the `coffee-shop` container

```
docker run -d -p 9080:9080 -p 9445:9443 --network=masterclass-net --name=coffee-shop -e default_barista_base_url='http://barista:9081' -e default_http_port=9080 -e default_https_port=9443 masterclass:coffee-shop
```
{: codeblock}


You can take a look at the bridge network using:

```
docker network inspect masterclass-net
```
{: codeblock}

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

You can run the following curl commands to try out the services running in containers:
```
curl http://localhost:9080/health
curl -X POST "http://localhost:9080/coffee-shop/resources/orders" \
     -H  "accept: */*" -H  "Content-Type: application/json" \
     -d "{\"status\":\"FINISHED\",\"type\":\"ESPRESSO\"}"
curl http://localhost:9080/coffee-shop/resources/orders

```
{: codeblock}

If you need to remove a container, use:

```
docker container rm <container name>
```
{: codeblock}

You should now be able to load the `coffee-shop` service's Open API page and call the service.  Give it a try.

# Overriding Dev Server Configuration

The above works fine, but still has a metrics endpoint with authentication turned off.  We'll now show how `configDropins/overrides` can be used to override existing, or add new, server configuration.  For example, this can be used to add server configuration in a production environment.  The approach we're going to take is to use a Docker volume for simplicity, but in a real-world scenario you would use Kubernetes ConfigMaps and secrets to include the production server configuration, security configuration and environment variables. 

In fact, unlike what we have done here, the best practice is to build an image that does not contain any environment specific configuration (such as the unsecured endpoint in our example) and then add those things through external configuration in the development, staging and production environments.  The goal is to ensure deployment of the image without configuration doesn't not cause undesirable results such as security vulnerabilities or talking to the wrong data sources.

Take a look at the file **open-liberty-masterclass/start/coffee-shop/configDropins/overrides/metrics-prod.xml**:

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
    <variable name="keystore.password" value="change_it" />
    
    <quickStartSecurity userName="admin" userPassword="${admin.password}"/>
    <keyStore id="defaultKeyStore" password="${keystore.password}"/>    
     
</server>
```
You'll see that this turns metrics authentication on and sets up some simple security required for securing/accessing the metrics endpoint.  Note, this configuration really is **NOT FOR PRODUCTION**, it's simply aiming to show how to override, or provide new, server configuration.

If you're on a unix-based OS, in the `open-liberty-masterclass/start/coffee-shop` directory, run the `coffee-shop` container:

```
docker run -p 9080:9080 -p 9445:9443 --network=masterclass-net --name=coffee-shop -e default_barista_base_url='http://barista:9081' -e default_http_port=9080 -e default_https_port=9443 -v $(pwd)/configDropins/overrides:/opt/ol/wlp/usr/servers/defaultServer/configDropins/overrides  masterclass:coffee-shop
```
{: codeblock}

The above relies on `pwd` to fill in the docker volume source path.  If you're on Windows, replace `$(pwd)` with the absolute path to the `open-liberty-masterclass/start/coffee-shop` directory in the above command.

You should see the following message as the server is starting:

```
[AUDIT   ] CWWKG0102I: Found conflicting settings for mpMetrics configuration.
  Property authentication has conflicting values:
    Value false is set in file:/opt/ol/wlp/usr/servers/defaultServer/server.xml.
    Value true is set in file:/opt/ol/wlp/usr/servers/defaultServer/configDropins/overrides/metrics-prod.xml.
  Property authentication will be set to true.
```
This shows that we have turned metrics authentication back on.

Access the metrics endpoint at: `https://localhost:9445/metrics` via the `launch application` tab above the IDE and enter the port `9445`

You will see that the browser complains about the certificate.  This is a self-signed certificate generated by Liberty for test purposes.  Accept the exception (note,  Firefox may not allow you to do this in which case you'll need to use a different browser).  You'll be presented with a login prompt.  Sign in with userid `admin` and password `change_it` (the values in the `metrics-prod.xml`).

# Next Steps

Congratulations on completing the exercise. Don't stop now. Move on to the next module in the master class by simply closing this tab and clicking on the next module in the Open Liberty Masterclass landing page.
