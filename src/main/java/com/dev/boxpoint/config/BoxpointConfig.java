package com.dev.boxpoint.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BoxpointConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
