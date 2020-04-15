package com.sebastian_daschner.coffee_shop.control;

import com.sebastian_daschner.coffee_shop.entity.CoffeeBrew;

import java.net.MalformedURLException;
import java.net.URL;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Response;

import javax.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.eclipse.microprofile.rest.client.RestClientDefinitionException;

@ApplicationScoped
public class Barista {

    URL url;
    
    @Inject
    @ConfigProperty(name="default_barista_base_url")
    String baristaBaseURL;

    @PostConstruct
    private void initClient() {
        try {
            url = new URL(baristaBaseURL + "/barista");
            System.out.println(url);
        } catch (NumberFormatException | MalformedURLException e) {
            e.printStackTrace();
        }

    }

    public void startCoffeeBrew(CoffeeBrew brew) {
        // TODO: remove thread when this is fixed - https://github.com/OpenLiberty/open-liberty/issues/6273
        new Thread(() -> {
            try {
                BaristaClient baristaClient = RestClientBuilder.newBuilder()
                    .baseUrl(url)
                    .build(BaristaClient.class);
                Response response = baristaClient.startCoffeeBrew(brew);
                System.out.println("BaristaClient response: " + response.getStatus());
            } catch (IllegalStateException | RestClientDefinitionException e) {
                e.printStackTrace();
            }
        }).start();
    }

}
