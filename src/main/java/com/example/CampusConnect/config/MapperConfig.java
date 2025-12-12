package com.example.CampusConnect.config;

import com.example.CampusConnect.dto.EventDTO;
import com.example.CampusConnect.dto.UpdateEventDTO;
import com.example.CampusConnect.model.Event;
import com.example.CampusConnect.model.EventMode;
import com.example.CampusConnect.model.EventType;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        // String → EventType
        modelMapper.addConverter(ctx ->
                EventType.valueOf(ctx.getSource()), String.class, EventType.class);

        // String → EventMode
        modelMapper.addConverter(ctx ->
                EventMode.valueOf(ctx.getSource()), String.class, EventMode.class);

        // EVENT → EVENTDTO
        modelMapper.addMappings(new PropertyMap<Event, EventDTO>() {
            @Override
            protected void configure() {

            }
        });

        // EVENTDTO → EVENT (for update)
        modelMapper.addMappings(new PropertyMap<UpdateEventDTO, Event>() {
            @Override
            protected void configure() {
                skip().setId(null);
                skip().setCreatedBy(null);
                skip().setRegistrations(null);
            }
        });

        return modelMapper;
    }

}
