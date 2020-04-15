package com.sebastian_daschner.coffee_shop.control;

import javax.enterprise.context.Dependent;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import com.sebastian_daschner.coffee_shop.entity.CoffeeBrew;

@Dependent
@RegisterRestClient
@Path("/resources/brews")
public interface BaristaClient {

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response startCoffeeBrew(CoffeeBrew brew);
}
