package com.ben.my_portfolio;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfiguration {

        @Configuration
        public class ModelMapperConfig {

            @Bean
            public ModelMapper modelMapper() {
                ModelMapper modelMapper = new ModelMapper();
                modelMapper.getConfiguration()
                        .setMatchingStrategy(MatchingStrategies.STRICT)
                        .setSkipNullEnabled(true)
                        .setFieldMatchingEnabled(true);
                return modelMapper;
            }
        }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return builder -> {
            builder.modules(new JavaTimeModule());
            builder.featuresToDisable(
                    SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        };
    }
}
