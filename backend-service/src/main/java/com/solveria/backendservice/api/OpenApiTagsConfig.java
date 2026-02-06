package com.solveria.backendservice.api;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(title = "Backend Service API", version = "v1"),
        tags = {
                @Tag(name = "Customers", description = "Customer management"),
                @Tag(name = "Bookings", description = "Booking management"),
                @Tag(name = "Activities", description = "Activity scheduling"),
                @Tag(name = "Reviews", description = "Review management"),
                @Tag(name = "Notifications", description = "Notification management"),
                @Tag(name = "Invoices", description = "Invoice management"),
                @Tag(name = "Suppliers", description = "Supplier management")
        }
)
public class OpenApiTagsConfig {
}
