package com.sebastian_daschner.barista.boundary;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.sebastian_daschner.barista.entity.CoffeeBrew;
import com.sebastian_daschner.barista.entity.CoffeeType;

@Path("/brews")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BrewsResource {

    @Inject
    CoffeeBrews coffeeBrews;

    @POST
    public Response startCoffeeBrew(CoffeeBrew brew) {
        CoffeeType coffeeType = brew.getType();

        if (coffeeType == null)
            throw new BadRequestException();

        coffeeBrews.startBrew(coffeeType);

        return Response.ok().build();
    }

}
