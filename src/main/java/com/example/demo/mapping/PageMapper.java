package com.example.demo.mapping;

import com.example.demo.dto.PageDto;
import com.example.demo.model.Page;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PageMapper {
    PageDto toDto(Page page);

    @Mapping(target = "id", ignore = true)
    Page toEntity(PageDto dto);
}
