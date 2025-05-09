package com.green.energy.tracker.user_management.config;

import com.fasterxml.jackson.dataformat.avro.AvroMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AvroConfig {

    @Bean
    public AvroMapper beanAvroMapper(){
        return new AvroMapper();
    }

}
