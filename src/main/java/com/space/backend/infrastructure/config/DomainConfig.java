// src/main/java/com/space/backend/infrastructure/config/DomainConfig.java
package com.space.backend.infrastructure.config;

import com.space.backend.domain.booking.BookingDomainService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainConfig {

    @Bean
    public BookingDomainService bookingDomainService() {
        return new BookingDomainService();
    }
}
