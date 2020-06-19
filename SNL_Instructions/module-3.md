# Module 3: Application APIs

Open Liberty has support for many standard APIs out of the box, including all the latest Java EE 8/11 APIs and the latest MicroProfile APIs. A new version of Liberty is released every four weeks and aims to provide MicroProfile implementations soon after they are finalized while leading the delivery of new APIs.

As we've seen, to use a new feature, we need to add them to the **server.xml.**  When adding a new feature, a dependency does not need to be added because each feature depends on the APIs. That means during a build, the API dependencies are automatically added from maven central.


For an example, take a look at: https://search.maven.org/artifact/io.openliberty.features/mpMetrics-2.0/20.0.0.4/esa

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

We're now going to add Metrics to the **coffee-shop.**  Edit **Coffee-Shop server.xml** file and add the following dependency in the featureManager section like we did above:

> [File->Open] **open-liberty-masterclass/start/coffee-shop/src/main/liberty/config/server.xml**

```XML
        <feature>mpMetrics-2.3</feature>
```
{: codeblock}

You should see that the server has been automatically updates, the following features are installed, and include mpMetrics-2.3:

```
[INFO] [AUDIT   ] CWWKF0012I: The server installed the following features: [beanValidation-2.0, cdi-2.0, distributedMap-1.0, ejbLite-3.2, el-3.0, jaxrs-2.1, jaxrsClient-2.1, jndi-1.0, json-1.0, jsonp-1.1, mpConfig-1.3, mpHealth-2.2, mpMetrics-2.0, mpOpenAPI-1.1, mpRestClient-1.3, servlet-4.0, ssl-1.0].
```
Now we have the API available, we can update the application to include a metric which will count the number of times a coffee order is requested. 

Open the **OrderResource.java** and add the following **@Counted** annotation to the **orderCoffee** method:
> [File->Open] **open-liberty-masterclass/start/coffee-shop/src/main/java/com/sebastian_daschner/coffee_shop/boundary/OrdersResource.java**

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

Congratulations on completing the excercise. Don't stop now. Move on to the next module in the master class by simply closing this tab and clicking on the next module in the Open Liberty Masterclass landing page.
