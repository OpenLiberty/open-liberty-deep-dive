package com.sebastian_daschner.barista.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.BeforeClass;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;;

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
                assertNotNull("GreetingService response must not be NULL", response);
            } else {
                assertEquals("Response must be 200 OK", 200, response.getStatus());
            }

        } finally {
            response.close();
        }
    }
}
