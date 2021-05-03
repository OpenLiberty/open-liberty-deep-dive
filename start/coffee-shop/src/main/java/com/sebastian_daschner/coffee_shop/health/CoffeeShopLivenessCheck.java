package com.sebastian_daschner.coffee_shop.health;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

@Liveness
@ApplicationScoped
public class CoffeeShopLivenessCheck implements HealthCheck {

  private static final String livenessCheck = CoffeeShopLivenessCheck.class.getSimpleName() 
            + " Liveness Check";
	
  @Override
  public HealthCheckResponse call() {
    return HealthCheckResponse.up(livenessCheck);
  }

}
