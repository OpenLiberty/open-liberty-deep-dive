package com.sebastian_daschner.barista.health;

import javax.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

@Readiness
@ApplicationScoped
public class BaristaReadinessCheck implements HealthCheck {

  private static final String readinessCheck = BaristaReadinessCheck.class.getSimpleName() 
            + " Readiness Check";
	
  public boolean isHealthy() {
      // Nothing to check in this implementation
      return true;
  }

  @Override
  public HealthCheckResponse call() {
    if (!isHealthy()) {
      return HealthCheckResponse.down(readinessCheck);
    }
    return HealthCheckResponse.up(readinessCheck);
  }

}
