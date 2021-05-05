# Module 3: Application APIs

Open Liberty has support for many standard APIs out of the box, including Java EE 7 & 8, Jakarta EE 8 and the latest MicroProfile APIs.

As you have seen in the previous section, the API dependencies that you need to use MicroProfile or Jakarta EE APIs have been added as dependencies to the POM file. You are all set to use these APIs as you need as you write your code.

Then, we need to enable the corresponding features in Liberty's server configuration for Liberty to load and use what you have chosen for your application. With Liberty's modular and composable architecture, only the features specified in the server configuration will be loaded giving you a lightweight and performant runtime.

We're now going to add Metrics to the **coffee-shop.**  Edit **Coffee-Shop server.xml** file and add the following dependency in the featureManager section like we did above:

> [File->Open] **open-liberty-masterclass/start/coffee-shop/src/main/liberty/config/server.xml**

```XML
        <feature>mpMetrics-3.0</feature>
```
{: codeblock}

You should see that the server has been automatically updates, the following features are installed, and include mpMetrics-3.0:

```
[INFO] Installing features: [mpconfig-2.0, mpopenapi-2.0, mpmetrics-3.0, cdi-2.0, mprestclient-2.0, jsonp-1.1, beanvalidation-2.0, ejblite-3.2, mphealth-3.0, jaxrs-2.1]
...
[INFO] [AUDIT   ] CWWKF0012I: The server installed the following features: [distributedMap-1.0, monitor-1.0, mpMetrics-3.0, ssl-1.0].
```
Now we have the API available, we can update the application to include a metric which will count the number of times a coffee order is requested. 

Open the **OrderResource.java** and add the following **@Counted** annotation to the **orderCoffee** method:
> [File->Open] **open-liberty-masterclass/start/coffee-shop/src/main/java/com/sebastian_daschner/coffee_shop/boundary/OrdersResource.java**

```java
@Counted(name="order", displayName="Order count", description="Number of times orders requested.")
```
{: codeblock}

 After you add the **@Counted** annotation, the mothod should look like:

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
{: codeblock}

# Next Steps

Congratulations on completing the exercise. Don't stop now. Move on to the next module in the master class by simply closing this tab and clicking on the next module in the Open Liberty Masterclass landing page.
