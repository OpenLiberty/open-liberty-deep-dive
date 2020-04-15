package com.sebastian_daschner.barista.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.Test;
import org.microshed.testing.jupiter.MicroShedTest;
import org.microshed.testing.testcontainers.MicroProfileApplication;
import org.testcontainers.junit.jupiter.Container;

import com.sebastian_daschner.barista.boundary.BrewsResource;
import com.sebastian_daschner.barista.entity.CoffeeBrew;
import com.sebastian_daschner.barista.entity.CoffeeType;

@MicroShedTest
public class BaristaContainerIT {

    @Container
    public static MicroProfileApplication app = new MicroProfileApplication()
                    .withAppContextRoot("/barista")
                    .withExposedPorts(9081, 9444)
                    .withReadinessPath("/health");
    
    @Inject
    public static BrewsResource brews;

    @Test
    public void testService() throws Exception {
        CoffeeBrew brew = new CoffeeBrew();
        brew.setType(CoffeeType.POUR_OVER);
        Response response = brews.startCoffeeBrew(brew);

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
