# Module 6: Integration Testing

Tests are essential for developing maintainable code. Developing your application using bean-based component models like CDI makes your code easily unit-testable. Integration Tests are a little more challenging. In this section you'll add a **barista** service integration test using the `maven-failsafe-plugin`. During the build, the Liberty server will be started along with the **barista** application deployed, the test will be run and then the server will be stopped.

Because we're going to be testing a **REST POST** request, we need JAX-RS client support and also support for serializing **json** into the request.  We also need **junit** for writing the test.  Add these dependencies to the **open-liberty-masterclass/start/barista/pom.xml**:

>[File->Open]open-liberty-masterclass/start/barista/pom.xml

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
{: codeblock}

Note: the **`<scope/>`** of the dependencies is set to **test** because we only want the dependencies to be used during testing.

Add the following `<configuration>...</configuration>` to the `maven-failsafe-plugin` plugin:
```
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
{: codeblock}

Note: this configuration makes the port of the server available to the test as a system property called `liberty.test.port`.

Finally, add the test code.  Create a file called **BaristaIT.java**.
Open a new Terminal

> Terminal -> New Terminal

```
touch open-liberty-masterclass/start/barista/src/test/java/com/sebastian_daschner/barista/it/BaristaIT.java
```
{: codeblock}

Open the **BaristaIT.java** and add the following:

[File->Open]open-liberty-masterclass/start/barista/src/test/java/com/sebastian_daschner/barista/it/BaristaIT.java

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
{: codeblock}

This test sends a `json` request to the **barista** service and checks for a `200 OK` response. 

You'll see the following information from the **barista** service output:
```
[INFO] Tests compilation was successful.
```

Because you started Open Liberty in dev mode, press the **enter/return** key to run the tests.

If the tests pass, you see a similar output to the following example:
```
-------------------------------------------------------
 T E S T S
-------------------------------------------------------
Running com.sebastian_daschner.barista.it.BaristaIT
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 1.365 sec - in com.sebastian_daschner.barista.it.BaristaIT

Results :

Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
```

When you are done checking out the services, exit dev mode by pressing **CTRL+C** in the command-line sessions
where you ran the **barista** and **coffee-shop** services, or by typing **q** and then pressing the **enter/return** key.

# Next Steps

Congratulations on completing the exercise. Don't stop now. Move on to the next module in the master class by simply closing this tab and clicking on the next module in the Open Liberty Masterclass landing page.
