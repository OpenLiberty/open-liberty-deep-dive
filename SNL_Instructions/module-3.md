# Module 3: Application APIs

Open Liberty has support for many standard APIs out of the box, including all the latest Java EE 8/11 APIs and the latest MicroProfile APIs.  To lead in the delivery of new APIs, a new version of Liberty is released every 4 weeks and aims to provide MicroProfile implementations soon after they are finalized.

As we've seen, to use a new feature, we need to add them to the build.  There is no need to add a dependency on the APIs for the feature because each feature depends on the APIs.  That means during build, the API dependencies are automatically added from maven central.

For example, take a look at: https://search.maven.org/artifact/io.openliberty.features/mpMetrics-2.0/20.0.0.4/esa

You'll see in the XML on the left that this feature depends on:

```XML
    <dependency>
      <groupId>io.openliberty.features</groupId>
      <artifactId>com.ibm.websphere.appserver.org.eclipse.microprofile.metrics-2.0</artifactId>
      <version>19.0.0.8</version>
      <type>esa</type>
    </dependency>
```
Which depends on the Metrics API from Eclipse MicroProfile:

```XML
    <dependency>
      <groupId>org.eclipse.microprofile.metrics</groupId>
      <artifactId>microprofile-metrics-api</artifactId>
      <version>2.3.0</version>
    </dependency>
```

And so during build, this API will be added for you.

We're now going to add Metrics to the `coffee-shop`.  Edit the `open-liberty-masterclass/start/coffee-shop/src/main/liberty/config/server.xml` file and add the following dependency in the featureManager section like we did above:

```XML
        <feature>mpMetrics-2.3</feature>
```
{: codeblock}

You should see that the server has been automatically updates, the following features are installed, and include mpMetrics-2.3:

```
[INFO] [AUDIT   ] CWWKF0012I: The server installed the following features: [beanValidation-2.0, cdi-2.0, distributedMap-1.0, ejbLite-3.2, el-3.0, jaxrs-2.1, jaxrsClient-2.1, jndi-1.0, json-1.0, jsonp-1.1, mpConfig-1.3, mpHealth-2.2, mpMetrics-2.0, mpOpenAPI-1.1, mpRestClient-1.3, servlet-4.0, ssl-1.0].
```
Now we have the API available, we can update the application to include a metric which will count the number of times a coffee order is requested. In the file `open-liberty-masterclass/start/coffee-shop/src/main/java/com/sebastian_daschner/coffee_shop/boundary/OrdersResource.java`, add the following `@Counted` annotation to the `orderCoffee` method:

```Java
    @POST
    @Counted(name="order", displayName="Order count", description="Number of times orders requested.")
    public Response orderCoffee(@Valid @NotNull CoffeeOrder order) {
        ...
    }
```
{: codeblock}

You'll also need to add the following package import:
```Java
import org.eclipse.microprofile.metrics.annotation.Counted;
```
{: codeblock}

# Next Steps

Congratulations on completing your next excercise. Don't stop now. Move on to the next module in the master class by simply closing this tab and clicking on the next module in the Open Liberty Masterclass landing page.