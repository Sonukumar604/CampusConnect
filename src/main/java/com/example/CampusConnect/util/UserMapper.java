package com.example.CampusConnect.util;

import com.example.CampusConnect.dto.ScholarshipDTO;
import com.example.CampusConnect.model.Scholarship;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;

public class UserMapper {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();

        mapper.typeMap(Scholarship.class, ScholarshipDTO.class).addMappings(m -> {
            m.map(src -> src.getCategory().name(), ScholarshipDTO::setCategory);
            m.map(src -> src.getPublishStatus().name(), ScholarshipDTO::setPublishStatus);

            // Created By
            m.map(src -> src.getCreatedBy().getId(), ScholarshipDTO::setCreatedById);
            m.map(src -> src.getCreatedBy().getName(), ScholarshipDTO::setCreatedByName);

            // Application count
            m.map(src -> src.getApplications().size(), ScholarshipDTO::setApplicationCount);
        });

        return mapper;
    }

}
