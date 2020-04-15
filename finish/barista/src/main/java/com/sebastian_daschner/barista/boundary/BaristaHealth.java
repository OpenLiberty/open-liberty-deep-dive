package com.sebastian_daschner.barista.boundary;

import javax.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.health.Health;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;

@Health
@ApplicationScoped
public class BaristaHealth implements HealthCheck {
	
  public boolean isHealthy() {
      // Nothing to check in this implementation
      return true;
  }

  @Override
  public HealthCheckResponse call() {
    if (!isHealthy()) {
      return HealthCheckResponse.named(this.getClass().getSimpleName())
                                .down()
                                .build();
    }
    return HealthCheckResponse.named(this.getClass().getSimpleName())
                              .up().build();
  }

}
