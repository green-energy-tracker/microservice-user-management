package com.green.energy.tracker.user_management.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.*;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper beanModelMapper(){
        return new ModelMapper();
    }

}
